import 'package:test/test.dart';
import 'package:panchang_engine/src/models/geo_location.dart';
import 'package:panchang_engine/src/astronomy/astronomy_service.dart';
import 'package:panchang_engine/src/astronomy/panchang_calculator.dart';
import 'package:panchang_engine/src/astronomy/ayanamsha.dart';

void main() {
  group('AstronomyService tests', () {
    final palitana = GeoLocation(
      latitude: 21.5222,
      longitude: 71.8291,
      cityName: 'Palitana',
    );

    test('Sunrise and Sunset calculation for Palitana', () {
      final date = DateTime(2026, 10, 7);
      final sunTimes = AstronomyService.getSunTimes(date, palitana);

      // Sunrise should be around 06:30 - 06:40 AM IST in early October
      expect(sunTimes.sunrise.hour, inInclusiveRange(6, 7));
      expect(sunTimes.sunset.hour, inInclusiveRange(18, 19));
      expect(sunTimes.dayDuration.inHours, inInclusiveRange(11, 13));
    });

    test('Ayanamsha Lahiri is accurate around 24.1° in 2026', () {
      final jd2026 = AstronomyService.dateTimeToJD(DateTime.utc(2026, 1, 1));
      final ayanamsha = AyanamshaCalculator.calculateAyanamsha(jd2026, AyanamshaType.lahiri);
      expect(ayanamsha, closeTo(24.21, 0.15));
    });

    test('Tithi Calculation returns valid range 1-30', () {
      final date = DateTime(2026, 10, 7, 12, 0);
      final tithi = PanchangCalculator.calculateTithi(date);
      expect(tithi.index, inInclusiveRange(1, 30));
      expect(tithi.tithiNumber, inInclusiveRange(1, 15));
      expect(tithi.endTime.isAfter(tithi.startTime), isTrue);
    });

    test('Nakshatra Calculation returns valid range 1-27', () {
      final date = DateTime(2026, 10, 7, 12, 0);
      final nak = PanchangCalculator.calculateNakshatra(date);
      expect(nak.index, inInclusiveRange(1, 27));
      expect(nak.nameEnglish.isNotEmpty, isTrue);
    });
  });
}
