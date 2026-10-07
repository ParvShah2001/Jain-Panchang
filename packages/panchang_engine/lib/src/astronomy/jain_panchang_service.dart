import '../models/geo_location.dart';
import '../models/panchang_constants.dart';
import '../astronomy/astronomy_service.dart';
import '../astronomy/ayanamsha.dart';
import '../astronomy/panchang_calculator.dart';

/// Represents full Panchang details for a calendar day.
class DailyPanchang {
  final DateTime date;
  final GeoLocation location;
  final SunTimes sunTimes;
  final MoonTimes moonTimes;

  // Samvat years
  final int vikramSamvat;
  final int veerNirvanSamvat;
  final String jainMonthGujarati;
  final String jainMonthHindi;
  final String jainMonthEnglish;

  // Day Panchang elements
  final TithiInfo udayaTithi;
  final bool isKshayaTithi;
  final bool isVriddhiTithi;
  final NakshatraInfo nakshatra;
  final YogaInfo yoga;
  final KaranaInfo karana;
  final int varaIndex; // 1 = Sunday, ..., 7 = Saturday
  final String varaEnglish;
  final String varaGujarati;
  final String varaHindi;

  DailyPanchang({
    required this.date,
    required this.location,
    required this.sunTimes,
    required this.moonTimes,
    required this.vikramSamvat,
    required this.veerNirvanSamvat,
    required this.jainMonthGujarati,
    required this.jainMonthHindi,
    required this.jainMonthEnglish,
    required this.udayaTithi,
    required this.isKshayaTithi,
    required this.isVriddhiTithi,
    required this.nakshatra,
    required this.yoga,
    required this.karana,
    required this.varaIndex,
    required this.varaEnglish,
    required this.varaGujarati,
    required this.varaHindi,
  });
}

/// Service to calculate full daily Panchang applying tradition rules
class JainPanchangService {
  /// Computes full Panchang for a specific date and location
  static DailyPanchang calculatePanchang(
    DateTime date,
    GeoLocation location, {
    JainSampraday sampraday = JainSampraday.tapagaccha,
    AyanamshaType ayanamsha = AyanamshaType.lahiri,
  }) {
    final sunTimes = AstronomyService.getSunTimes(date, location);
    final moonTimes = AstronomyService.getMoonTimes(date, location);

    // Prevailing Udaya-Tithi is evaluated at local sunrise time
    final sunrise = sunTimes.sunrise;
    final udayaTithi = PanchangCalculator.calculateTithi(sunrise);

    // Evaluate Kshaya / Vriddhi
    // Check sunrise of previous day and next day
    final prevDaySun = AstronomyService.getSunTimes(date.subtract(const Duration(days: 1)), location);

    final prevUdayaTithi = PanchangCalculator.calculateTithi(prevDaySun.sunrise);

    // Vriddhi: Tithi is active across two consecutive sunrises
    final isVriddhiTithi = udayaTithi.index == prevUdayaTithi.index;

    // Kshaya: A tithi skipped sunrise entirely between yesterday and today
    // e.g. prev was 3, current is 5 (tithi 4 was omitted / kshaya)
    final expectedTithiIndex = (prevUdayaTithi.index % 30) + 1;
    final isKshayaTithi = udayaTithi.index != expectedTithiIndex && !isVriddhiTithi;

    // Nakshatra, Yoga, Karana at sunrise
    final nakshatra = PanchangCalculator.calculateNakshatra(sunrise, ayanamsha: ayanamsha);
    final yoga = PanchangCalculator.calculateYoga(sunrise, ayanamsha: ayanamsha);
    final karana = PanchangCalculator.calculateKarana(sunrise);

    // Vara (Weekday)
    // DateTime weekday: Monday is 1, Sunday is 7
    // In Indian astronomy: Ravivara is 1, Somavara is 2 ... Shanivara is 7
    final varaIdx = (date.weekday % 7) + 1; // 7(Sun)%7 + 1 = 1 (Ravi), 1(Mon)%7 + 1 = 2 (Som)
    final vZero = varaIdx - 1;

    // Samvat calculations:
    // Gujarati Vikram Samvat turns on Kartak Sud 1 (Diwali new year)
    // Usually Vikram Samvat = Gregorian Year + 56 / 57
    // Veer Nirvan Samvat = Gregorian Year + 527 (Kartak Sud 1)
    // Approx determination based on month & solar longitude:
    final jd = AstronomyService.dateTimeToJD(date);
    final sunNirayana = AstronomyService.getSunNirayanaLongitude(jd, ayanamsha: ayanamsha);
    
    // Nirayana Sun in Libra (Tula ~ 180°-210°) / Scorpio (Vrishchika ~ 210°-240°) corresponds to Kartak
    // For general approximation across dates:
    var vs = date.year + 56;
    if (date.month > 10 || (date.month == 10 && date.day >= 25)) {
      vs = date.year + 57;
    }
    final vns = vs + 470; // VNS = VS + 470

    // Approximate Jain month index (0 to 11, starting Kartak)
    // Nirayana solar month index (0 to 11): 0 = Mesha (Chaitra/Vaishakh)
    final solarRashi = (sunNirayana / 30.0).floor(); // 0: Mesha, 6: Tula (Kartak)
    final monthIdx = (solarRashi - 6 + 12) % 12;

    return DailyPanchang(
      date: date,
      location: location,
      sunTimes: sunTimes,
      moonTimes: moonTimes,
      vikramSamvat: vs,
      veerNirvanSamvat: vns,
      jainMonthGujarati: JainMonthNames.gujarati[monthIdx],
      jainMonthHindi: JainMonthNames.hindi[monthIdx],
      jainMonthEnglish: JainMonthNames.english[monthIdx],
      udayaTithi: udayaTithi,
      isKshayaTithi: isKshayaTithi,
      isVriddhiTithi: isVriddhiTithi,
      nakshatra: nakshatra,
      yoga: yoga,
      karana: karana,
      varaIndex: varaIdx,
      varaEnglish: VaraNames.english[vZero],
      varaGujarati: VaraNames.gujarati[vZero],
      varaHindi: VaraNames.hindi[vZero],
    );
  }
}
