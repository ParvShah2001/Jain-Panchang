import '../astronomy/astronomy_service.dart';
import '../astronomy/ayanamsha.dart';
import '../models/panchang_constants.dart';

/// Represents a single Tithi span with start and end times.
class TithiInfo {
  final int index; // 1 to 30 (1-15 Shukla, 16-30 Krishna)
  final int tithiNumber; // 1 to 15
  final Paksha paksha;
  final String nameEnglish;
  final String nameGujarati;
  final String nameHindi;
  final DateTime startTime;
  final DateTime endTime;

  const TithiInfo({
    required this.index,
    required this.tithiNumber,
    required this.paksha,
    required this.nameEnglish,
    required this.nameGujarati,
    required this.nameHindi,
    required this.startTime,
    required this.endTime,
  });

  bool get isPurnima => index == 15;
  bool get isAmavasya => index == 30;

  @override
  String toString() => '$nameEnglish ($paksha) [${startTime.toIso8601String()} - ${endTime.toIso8601String()}]';
}

/// Represents Nakshatra information with start and end times.
class NakshatraInfo {
  final int index; // 1 to 27
  final String nameEnglish;
  final String nameGujarati;
  final String nameHindi;
  final DateTime startTime;
  final DateTime endTime;

  const NakshatraInfo({
    required this.index,
    required this.nameEnglish,
    required this.nameGujarati,
    required this.nameHindi,
    required this.startTime,
    required this.endTime,
  });

  @override
  String toString() => '$nameEnglish [${startTime.toIso8601String()} - ${endTime.toIso8601String()}]';
}

/// Represents Yoga info (1 to 27).
class YogaInfo {
  final int index; // 1 to 27
  final String nameEnglish;
  final DateTime startTime;
  final DateTime endTime;

  const YogaInfo({
    required this.index,
    required this.nameEnglish,
    required this.startTime,
    required this.endTime,
  });
}

/// Represents Karana info (1 to 60 in a lunar month).
class KaranaInfo {
  final int index; // 1 to 11
  final String nameEnglish;
  final DateTime startTime;
  final DateTime endTime;

  const KaranaInfo({
    required this.index,
    required this.nameEnglish,
    required this.startTime,
    required this.endTime,
  });
}

/// Core Astronomical Panchang Calculator
class PanchangCalculator {
  /// Finds the Tithi active at a given moment in time.
  /// Tithi angle = (Moon Longitude - Sun Longitude) mod 360
  /// Each Tithi spans exactly 12 degrees (360 / 30).
  static double getLunarPhaseAngle(double jd) {
    final sunLon = AstronomyService.getSunApparentLongitude(jd);
    final moonLon = AstronomyService.getMoonApparentLongitude(jd);
    var diff = (moonLon - sunLon) % 360.0;
    if (diff < 0) diff += 360.0;
    return diff;
  }

  /// Calculates Tithi active at the given [time], along with transition boundaries.
  static TithiInfo calculateTithi(DateTime time) {
    final jd = AstronomyService.dateTimeToJD(time);
    final angle = getLunarPhaseAngle(jd);
    final tithiIndex = (angle / 12.0).floor() + 1; // 1 to 30

    final paksha = tithiIndex <= 15 ? Paksha.shukla : Paksha.krishna;
    final tithiNum = tithiIndex <= 15 ? tithiIndex : tithiIndex - 15;

    // Search backwards for start time (where angle crossed (tithiIndex - 1) * 12°)
    final targetStartAngle = ((tithiIndex - 1) * 12.0) % 360.0;
    final targetEndAngle = (tithiIndex * 12.0) % 360.0;

    final startJd = _findAngleCrossing(jd - 1.5, jd, targetStartAngle);
    final endJd = _findAngleCrossing(jd, jd + 1.5, targetEndAngle);

    final startTime = AstronomyService.jdToDateTime(startJd).toLocal();
    final endTime = AstronomyService.jdToDateTime(endJd).toLocal();

    return TithiInfo(
      index: tithiIndex,
      tithiNumber: tithiNum,
      paksha: paksha,
      nameEnglish: TithiNames.getName(tithiNum, paksha, lang: 'en'),
      nameGujarati: TithiNames.getName(tithiNum, paksha, lang: 'gu'),
      nameHindi: TithiNames.getName(tithiNum, paksha, lang: 'hi'),
      startTime: startTime,
      endTime: endTime,
    );
  }

  /// Calculates Nakshatra active at the given [time].
  /// Nakshatra spans 13° 20' = 13.333333° of Nirayana Moon longitude.
  static NakshatraInfo calculateNakshatra(
    DateTime time, {
    AyanamshaType ayanamsha = AyanamshaType.lahiri,
  }) {
    final jd = AstronomyService.dateTimeToJD(time);
    final moonLon = AstronomyService.getMoonNirayanaLongitude(jd, ayanamsha: ayanamsha);
    const span = 360.0 / 27.0; // 13.333333333 degrees
    final nakshatraIndex = (moonLon / span).floor() + 1; // 1 to 27

    final targetStart = ((nakshatraIndex - 1) * span) % 360.0;
    final targetEnd = (nakshatraIndex * span) % 360.0;

    final startJd = _findMoonLonCrossing(jd - 1.5, jd, targetStart, ayanamsha);
    final endJd = _findMoonLonCrossing(jd, jd + 1.5, targetEnd, ayanamsha);

    final idxZero = (nakshatraIndex - 1).clamp(0, 26);
    return NakshatraInfo(
      index: nakshatraIndex,
      nameEnglish: NakshatraNames.english[idxZero],
      nameGujarati: NakshatraNames.gujarati[idxZero],
      nameHindi: NakshatraNames.hindi[idxZero],
      startTime: AstronomyService.jdToDateTime(startJd).toLocal(),
      endTime: AstronomyService.jdToDateTime(endJd).toLocal(),
    );
  }

  /// Calculates Yoga active at [time].
  /// Yoga = (Sun Nirayana Lon + Moon Nirayana Lon) mod 360, divided into 27 equal parts of 13° 20'.
  static YogaInfo calculateYoga(
    DateTime time, {
    AyanamshaType ayanamsha = AyanamshaType.lahiri,
  }) {
    final jd = AstronomyService.dateTimeToJD(time);
    final sunLon = AstronomyService.getSunNirayanaLongitude(jd, ayanamsha: ayanamsha);
    final moonLon = AstronomyService.getMoonNirayanaLongitude(jd, ayanamsha: ayanamsha);
    final sum = (sunLon + moonLon) % 360.0;
    const span = 360.0 / 27.0;
    final yogaIndex = (sum / span).floor() + 1; // 1 to 27

    final idxZero = (yogaIndex - 1).clamp(0, 26);
    return YogaInfo(
      index: yogaIndex,
      nameEnglish: YogaNames.english[idxZero],
      startTime: time.subtract(const Duration(hours: 6)),
      endTime: time.add(const Duration(hours: 18)),
    );
  }

  /// Calculates Karana active at [time].
  /// Each Karana is half a Tithi (6 degrees). 60 Karanas in a lunar month.
  static KaranaInfo calculateKarana(DateTime time) {
    final jd = AstronomyService.dateTimeToJD(time);
    final angle = getLunarPhaseAngle(jd);
    final karanaNumber = (angle / 6.0).floor() + 1; // 1 to 60

    int karanaIndex;
    String name;

    if (karanaNumber == 1) {
      karanaIndex = 11; // Kintughna
      name = KaranaNames.english[10];
    } else if (karanaNumber >= 58) {
      if (karanaNumber == 58) {
        karanaIndex = 8; // Shakuni
        name = KaranaNames.english[7];
      } else if (karanaNumber == 59) {
        karanaIndex = 9; // Chatushpada
        name = KaranaNames.english[8];
      } else {
        karanaIndex = 10; // Naga
        name = KaranaNames.english[9];
      }
    } else {
      // 7 repeating karanas (Bava, Balava, Kaulava, Taitila, Gara, Vanija, Vishti)
      karanaIndex = ((karanaNumber - 2) % 7) + 1;
      name = KaranaNames.english[karanaIndex - 1];
    }

    return KaranaInfo(
      index: karanaIndex,
      nameEnglish: name,
      startTime: time.subtract(const Duration(hours: 4)),
      endTime: time.add(const Duration(hours: 8)),
    );
  }

  /// Binary search root-finding for phase angle crossing
  static double _findAngleCrossing(double jdLow, double jdHigh, double targetAngle) {
    double low = jdLow;
    double high = jdHigh;
    for (int i = 0; i < 28; i++) {
      final mid = (low + high) / 2.0;
      final angleMid = getLunarPhaseAngle(mid);
      var diff = (angleMid - targetAngle);
      if (diff > 180) diff -= 360;
      if (diff < -180) diff += 360;

      if (diff < 0) {
        low = mid;
      } else {
        high = mid;
      }
    }
    return (low + high) / 2.0;
  }

  /// Binary search root-finding for Nirayana Moon longitude crossing
  static double _findMoonLonCrossing(
    double jdLow,
    double jdHigh,
    double targetLon,
    AyanamshaType ayanamsha,
  ) {
    double low = jdLow;
    double high = jdHigh;
    for (int i = 0; i < 28; i++) {
      final mid = (low + high) / 2.0;
      final lonMid = AstronomyService.getMoonNirayanaLongitude(mid, ayanamsha: ayanamsha);
      var diff = (lonMid - targetLon);
      if (diff > 180) diff -= 360;
      if (diff < -180) diff += 360;

      if (diff < 0) {
        low = mid;
      } else {
        high = mid;
      }
    }
    return (low + high) / 2.0;
  }
}
