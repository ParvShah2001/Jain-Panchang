import 'package:test/test.dart';
import 'package:panchang_engine/src/models/geo_location.dart';
import 'package:panchang_engine/src/astronomy/astronomy_service.dart';
import 'package:panchang_engine/src/astronomy/jain_panchang_service.dart';
import 'package:panchang_engine/src/timings/choghadiya_service.dart';
import 'package:panchang_engine/src/timings/muhurat_service.dart';
import 'package:panchang_engine/src/timings/pachkhan_service.dart';

void main() {
  group('Phase 1 Core Engine Comprehensive Tests', () {
    final ahmedabad = GeoLocation(
      latitude: 23.0225,
      longitude: 72.5714,
      cityName: 'Ahmedabad',
    );

    final date = DateTime(2026, 10, 7);

    test('DailyPanchang computes Udaya-Tithi, Samvat, and Vara correctly', () {
      final panchang = JainPanchangService.calculatePanchang(date, ahmedabad);

      expect(panchang.vikramSamvat, isIn([2082, 2083]));
      expect(panchang.veerNirvanSamvat, isIn([2552, 2553]));
      expect(panchang.varaEnglish.contains('Wednesday'), isTrue);
      expect(panchang.varaGujarati, 'બુધવાર');
      expect(panchang.varaHindi, 'बुधवार');
      expect(panchang.udayaTithi.nameEnglish.isNotEmpty, isTrue);
      expect(panchang.nakshatra.nameGujarati.isNotEmpty, isTrue);
    });

    test('Choghadiya divides Day and Night into 8 equal parts with accurate count', () {
      final sunTimes = AstronomyService.getSunTimes(date, ahmedabad);
      final nextDaySun = AstronomyService.getSunTimes(date.add(const Duration(days: 1)), ahmedabad);

      final choghadiyas = ChoghadiyaService.calculateChoghadiyas(
        sunrise: sunTimes.sunrise,
        sunset: sunTimes.sunset,
        nextSunrise: nextDaySun.sunrise,
        varaIndex: (date.weekday % 7) + 1,
      );

      expect(choghadiyas.length, 16);
      expect(choghadiyas.where((c) => c.isDay).length, 8);
      expect(choghadiyas.where((c) => !c.isDay).length, 8);

      // Verify Wednesday first day choghadiya is Labh
      expect(choghadiyas.first.nameEnglish, 'Labh');

      // Verify continuity
      for (int i = 0; i < choghadiyas.length - 1; i++) {
        expect(choghadiyas[i].endTime, equals(choghadiyas[i + 1].startTime));
      }
    });

    test('Muhurat service calculates Abhijit and Rahu Kalam within daylight boundaries', () {
      final sunTimes = AstronomyService.getSunTimes(date, ahmedabad);
      final abhijit = MuhuratService.calculateAbhijitMuhurat(sunTimes.sunrise, sunTimes.sunset);
      final rahu = MuhuratService.calculateRahuKalam(sunTimes.sunrise, sunTimes.sunset, 4); // Wed

      expect(abhijit.startTime.isAfter(sunTimes.sunrise), isTrue);
      expect(abhijit.endTime.isBefore(sunTimes.sunset), isTrue);
      expect(rahu.startTime.isAfter(sunTimes.sunrise), isTrue);
      expect(rahu.endTime.isBefore(sunTimes.sunset), isTrue);
    });

    test('Pachkhan formulas calculate Navkarshi, Porsi, Sadh Porsi, and Chauvihar', () {
      final sunTimes = AstronomyService.getSunTimes(date, ahmedabad);
      final pachkhan = PachkhanService.calculatePachkhanTimes(
        sunrise: sunTimes.sunrise,
        sunset: sunTimes.sunset,
      );

      final navkarshi = pachkhan.firstWhere((p) => p.id == 'navkarshi');
      final porsi = pachkhan.firstWhere((p) => p.id == 'porsi');
      final sadhPorsi = pachkhan.firstWhere((p) => p.id == 'sadh_porsi');
      final chauvihar = pachkhan.firstWhere((p) => p.id == 'chauvihar');

      // Navkarshi is exactly sunrise + 48 min
      expect(navkarshi.time.difference(sunTimes.sunrise).inMinutes, 48);
      // Porsi is after Navkarshi
      expect(porsi.time.isAfter(navkarshi.time), isTrue);
      // Sadh Porsi is after Porsi
      expect(sadhPorsi.time.isAfter(porsi.time), isTrue);
      // Chauvihar is at sunset
      expect(chauvihar.time, equals(sunTimes.sunset));
    });
  });
}
