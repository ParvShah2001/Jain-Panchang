import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:panchang_engine/panchang_engine.dart';
import '../data/cities/city_database.dart';

/// Currently selected date in UI
class SelectedDateNotifier extends Notifier<DateTime> {
  @override
  DateTime build() {
    final now = DateTime.now();
    return DateTime(now.year, now.month, now.day);
  }

  void setDate(DateTime date) => state = date;
}

final selectedDateProvider = NotifierProvider<SelectedDateNotifier, DateTime>(
  SelectedDateNotifier.new,
);

/// Currently selected city / location
class SelectedLocationNotifier extends Notifier<GeoLocation> {
  @override
  GeoLocation build() => CityDatabase.defaultCity;

  void setLocation(GeoLocation loc) => state = loc;
}

final selectedLocationProvider = NotifierProvider<SelectedLocationNotifier, GeoLocation>(
  SelectedLocationNotifier.new,
);

/// Currently selected Sampraday
class SelectedSampradayNotifier extends Notifier<JainSampraday> {
  @override
  JainSampraday build() => JainSampraday.tapagaccha;

  void setSampraday(JainSampraday s) => state = s;
}

final selectedSampradayProvider = NotifierProvider<SelectedSampradayNotifier, JainSampraday>(
  SelectedSampradayNotifier.new,
);

/// Currently selected Ayanamsha
class SelectedAyanamshaNotifier extends Notifier<AyanamshaType> {
  @override
  AyanamshaType build() => AyanamshaType.lahiri;

  void setAyanamsha(AyanamshaType a) => state = a;
}

final selectedAyanamshaProvider = NotifierProvider<SelectedAyanamshaNotifier, AyanamshaType>(
  SelectedAyanamshaNotifier.new,
);

/// Selected UI Language ('en', 'gu', 'hi')
class SelectedLanguageNotifier extends Notifier<String> {
  @override
  String build() => 'gu';

  void setLanguage(String lang) => state = lang;
}

final selectedLanguageProvider = NotifierProvider<SelectedLanguageNotifier, String>(
  SelectedLanguageNotifier.new,
);

/// Reactive Panchang computation for the selected date & location
final dailyPanchangProvider = Provider<DailyPanchang>((ref) {
  final date = ref.watch(selectedDateProvider);
  final location = ref.watch(selectedLocationProvider);
  final sampraday = ref.watch(selectedSampradayProvider);
  final ayanamsha = ref.watch(selectedAyanamshaProvider);

  return JainPanchangService.calculatePanchang(
    date,
    location,
    sampraday: sampraday,
    ayanamsha: ayanamsha,
  );
});

/// Choghadiyas for the selected day
final choghadiyasProvider = Provider<List<ChoghadiyaPeriod>>((ref) {
  final panchang = ref.watch(dailyPanchangProvider);
  final location = ref.watch(selectedLocationProvider);
  final date = ref.watch(selectedDateProvider);

  final nextDaySun = AstronomyService.getSunTimes(
    date.add(const Duration(days: 1)),
    location,
  );

  return ChoghadiyaService.calculateChoghadiyas(
    sunrise: panchang.sunTimes.sunrise,
    sunset: panchang.sunTimes.sunset,
    nextSunrise: nextDaySun.sunrise,
    varaIndex: panchang.varaIndex,
  );
});

/// Pachkhan items for the selected day
final pachkhanListProvider = Provider<List<PachkhanItem>>((ref) {
  final panchang = ref.watch(dailyPanchangProvider);
  return PachkhanService.calculatePachkhanTimes(
    sunrise: panchang.sunTimes.sunrise,
    sunset: panchang.sunTimes.sunset,
  );
});

/// Periodic timer ticker for live Choghadiya and countdowns (emits every second)
final clockStreamProvider = StreamProvider.autoDispose<DateTime>((ref) {
  return Stream.periodic(const Duration(seconds: 1), (_) => DateTime.now());
});

/// Currently active Choghadiya based on real-time clock
final activeChoghadiyaProvider = Provider.autoDispose<ChoghadiyaPeriod?>((ref) {
  final choghadiyas = ref.watch(choghadiyasProvider);
  final clockAsync = ref.watch(clockStreamProvider);
  final now = clockAsync.value ?? DateTime.now();
  return ChoghadiyaService.getCurrentChoghadiya(choghadiyas, now);
});
