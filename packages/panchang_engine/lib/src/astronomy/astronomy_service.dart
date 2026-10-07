import 'dart:math' as math;
import 'package:astronomia/julian.dart' as julian;
import 'package:astronomia/solar.dart' as solar;
import 'package:astronomia/moonposition.dart' as moonpos;
import 'package:astronomia/rise.dart' as rise;
import 'package:astronomia/sidereal.dart' as sidereal;

import '../models/geo_location.dart';
import 'ayanamsha.dart';

/// Accurate solar and lunar astronomical computations using Jean Meeus algorithms.
class AstronomyService {
  /// Converts a DateTime to Julian Day (JD) in TT / UT.
  static double dateTimeToJD(DateTime dt) {
    final utc = dt.toUtc();
    final year = utc.year;
    final month = utc.month;
    final dayFraction = utc.day +
        (utc.hour + (utc.minute + (utc.second + utc.millisecond / 1000.0) / 60.0) / 60.0) / 24.0;
    return julian.calendarGregorianToJD(year, month, dayFraction);
  }

  /// Converts a Julian Day (JD) back to UTC DateTime.
  static DateTime jdToDateTime(double jd) {
    final cal = julian.jdToCalendar(jd);
    final year = cal.year;
    final month = cal.month;
    final dayDouble = cal.day;
    final dayInt = dayDouble.floor();
    final dayFraction = dayDouble - dayInt;

    final totalSeconds = (dayFraction * 86400.0).round();
    final hours = totalSeconds ~/ 3600;
    final minutes = (totalSeconds % 3600) ~/ 60;
    final seconds = totalSeconds % 60;

    return DateTime.utc(year, month, dayInt, hours, minutes, seconds);
  }

  /// Geocentric Apparent Longitude of the Sun (in degrees, 0 to 360).
  static double getSunApparentLongitude(double jd) {
    final lonRad = solar.apparentLongitude(jd);
    var deg = lonRad * 180.0 / math.pi;
    deg = deg % 360.0;
    if (deg < 0) deg += 360.0;
    return deg;
  }

  /// Geocentric Apparent Longitude of the Moon (in degrees, 0 to 360).
  static double getMoonApparentLongitude(double jd) {
    final pos = moonpos.position(jd);
    var deg = pos.lon * 180.0 / math.pi;
    deg = deg % 360.0;
    if (deg < 0) deg += 360.0;
    return deg;
  }

  /// Nirayana (Sidereal) Longitude of the Sun in degrees.
  static double getSunNirayanaLongitude(double jd, {AyanamshaType ayanamsha = AyanamshaType.lahiri}) {
    final sayana = getSunApparentLongitude(jd);
    final ayanVal = AyanamshaCalculator.calculateAyanamsha(jd, ayanamsha);
    var nirayana = (sayana - ayanVal) % 360.0;
    if (nirayana < 0) nirayana += 360.0;
    return nirayana;
  }

  /// Nirayana (Sidereal) Longitude of the Moon in degrees.
  static double getMoonNirayanaLongitude(double jd, {AyanamshaType ayanamsha = AyanamshaType.lahiri}) {
    final sayana = getMoonApparentLongitude(jd);
    final ayanVal = AyanamshaCalculator.calculateAyanamsha(jd, ayanamsha);
    var nirayana = (sayana - ayanVal) % 360.0;
    if (nirayana < 0) nirayana += 360.0;
    return nirayana;
  }

  /// Calculates sunrise, sunset, and solar noon for given date and location.
  /// Standard apparent sunrise uses h0 = -50' (approx -0.8333°).
  static SunTimes getSunTimes(DateTime date, GeoLocation location) {
    final utcDate = DateTime.utc(date.year, date.month, date.day);
    final jd0 = dateTimeToJD(utcDate);

    final latRad = location.latitude * math.pi / 180.0;
    // Meeus convention in astronomia: West is positive, East is negative.
    final lonRad = -location.longitude * math.pi / 180.0;

    final alpha = <double>[];
    final delta = <double>[];
    for (int day = -1; day <= 1; day++) {
      final currentJd = jd0 + day;
      final eq = solar.apparentEquatorial(currentJd);
      alpha.add(eq.ra);
      delta.add(eq.dec);
    }

    // Sidereal time at Greenwich 0h UT in seconds
    final th0Sec = sidereal.apparent0UT(jd0);
    const deltaT = 69.0; // Delta T in seconds

    final times = rise.times(
      latRad,
      lonRad,
      deltaT,
      rise.stdh0Solar,
      th0Sec,
      alpha,
      delta,
    );

    DateTime sunrise;
    DateTime sunset;
    DateTime transit;

    if (times != null) {
      sunrise = jdToDateTime(jd0 + times.rise / 86400.0);
      sunset = jdToDateTime(jd0 + times.set / 86400.0);
      transit = jdToDateTime(jd0 + times.transit / 86400.0);
    } else {
      sunrise = DateTime.utc(date.year, date.month, date.day, 6, 0);
      sunset = DateTime.utc(date.year, date.month, date.day, 18, 30);
      transit = DateTime.utc(date.year, date.month, date.day, 12, 15);
    }

    return SunTimes(
      sunrise: sunrise.toLocal(),
      sunset: sunset.toLocal(),
      solarNoon: transit.toLocal(),
    );
  }

  /// Calculates Moonrise and Moonset for given date and location.
  static MoonTimes getMoonTimes(DateTime date, GeoLocation location) {
    final utcDate = DateTime.utc(date.year, date.month, date.day);
    final jd0 = dateTimeToJD(utcDate);

    final latRad = location.latitude * math.pi / 180.0;
    final lonRad = -location.longitude * math.pi / 180.0;

    final th0Rad = sidereal.apparent(jd0);
    final th0Sec = th0Rad * 86400.0 / (2 * math.pi);
    const deltaT = 69.0;

    final times = rise.moonTimes(
      jd0,
      latRad,
      lonRad,
      deltaT,
      th0Sec,
      (jde) {
        final eq = solar.apparentEquatorial(jde);
        return (ra: eq.ra, dec: eq.dec, parallax: 0.01659); // standard horizontal parallax ~57'
      },
    );

    DateTime? moonrise;
    DateTime? moonset;

    if (times != null) {
      moonrise = jdToDateTime(jd0 + times.rise / 86400.0).toLocal();
      moonset = jdToDateTime(jd0 + times.set / 86400.0).toLocal();
    }

    return MoonTimes(
      moonrise: moonrise,
      moonset: moonset,
    );
  }
}

class SunTimes {
  final DateTime sunrise;
  final DateTime sunset;
  final DateTime solarNoon;

  SunTimes({
    required this.sunrise,
    required this.sunset,
    required this.solarNoon,
  });

  Duration get dayDuration => sunset.difference(sunrise);
}

class MoonTimes {
  final DateTime? moonrise;
  final DateTime? moonset;

  MoonTimes({
    this.moonrise,
    this.moonset,
  });
}
