/// Types of Choghadiya
enum ChoghadiyaType {
  amrit, // Good (Shubh)
  shubh, // Good
  labh,  // Good
  chal,  // Neutral / Fair
  kaal,  // Inauspicious
  rog,   // Inauspicious
  udveg, // Inauspicious
}

/// A specific Choghadiya interval with start, end, and auspiciousness
class ChoghadiyaPeriod {
  final ChoghadiyaType type;
  final String nameEnglish;
  final String nameGujarati;
  final String nameHindi;
  final bool isDay;
  final DateTime startTime;
  final DateTime endTime;
  final bool isAuspicious;

  const ChoghadiyaPeriod({
    required this.type,
    required this.nameEnglish,
    required this.nameGujarati,
    required this.nameHindi,
    required this.isDay,
    required this.startTime,
    required this.endTime,
    required this.isAuspicious,
  });

  bool contains(DateTime time) => time.isAfter(startTime) && time.isBefore(endTime);

  Duration remaining(DateTime time) {
    if (time.isAfter(endTime)) return Duration.zero;
    if (time.isBefore(startTime)) return endTime.difference(startTime);
    return endTime.difference(time);
  }
}

/// Choghadiya calculations based on daytime and nighttime 8 equal parts (Prahara divisions)
class ChoghadiyaService {
  // Day sequence starting from Sunday (1) to Saturday (7)
  // Day starting Choghadiya order:
  // Sun: Udveg, Chal, Labh, Amrit, Kaal, Shubh, Rog, Udveg
  // Mon: Amrit, Kaal, Shubh, Rog, Udveg, Chal, Labh, Amrit
  // Tue: Rog, Udveg, Chal, Labh, Amrit, Kaal, Shubh, Rog
  // Wed: Labh, Amrit, Kaal, Shubh, Rog, Udveg, Chal, Labh
  // Thu: Shubh, Rog, Udveg, Chal, Labh, Amrit, Kaal, Shubh
  // Fri: Chal, Labh, Amrit, Kaal, Shubh, Rog, Udveg, Chal
  // Sat: Kaal, Shubh, Rog, Udveg, Chal, Labh, Amrit, Kaal

  static const List<List<ChoghadiyaType>> _daySequences = [
    // 0 = Sunday
    [
      ChoghadiyaType.udveg, ChoghadiyaType.chal, ChoghadiyaType.labh,
      ChoghadiyaType.amrit, ChoghadiyaType.kaal, ChoghadiyaType.shubh,
      ChoghadiyaType.rog, ChoghadiyaType.udveg
    ],
    // 1 = Monday
    [
      ChoghadiyaType.amrit, ChoghadiyaType.kaal, ChoghadiyaType.shubh,
      ChoghadiyaType.rog, ChoghadiyaType.udveg, ChoghadiyaType.chal,
      ChoghadiyaType.labh, ChoghadiyaType.amrit
    ],
    // 2 = Tuesday
    [
      ChoghadiyaType.rog, ChoghadiyaType.udveg, ChoghadiyaType.chal,
      ChoghadiyaType.labh, ChoghadiyaType.amrit, ChoghadiyaType.kaal,
      ChoghadiyaType.shubh, ChoghadiyaType.rog
    ],
    // 3 = Wednesday
    [
      ChoghadiyaType.labh, ChoghadiyaType.amrit, ChoghadiyaType.kaal,
      ChoghadiyaType.shubh, ChoghadiyaType.rog, ChoghadiyaType.udveg,
      ChoghadiyaType.chal, ChoghadiyaType.labh
    ],
    // 4 = Thursday
    [
      ChoghadiyaType.shubh, ChoghadiyaType.rog, ChoghadiyaType.udveg,
      ChoghadiyaType.chal, ChoghadiyaType.labh, ChoghadiyaType.amrit,
      ChoghadiyaType.kaal, ChoghadiyaType.shubh
    ],
    // 5 = Friday
    [
      ChoghadiyaType.chal, ChoghadiyaType.labh, ChoghadiyaType.amrit,
      ChoghadiyaType.kaal, ChoghadiyaType.shubh, ChoghadiyaType.rog,
      ChoghadiyaType.udveg, ChoghadiyaType.chal
    ],
    // 6 = Saturday
    [
      ChoghadiyaType.kaal, ChoghadiyaType.shubh, ChoghadiyaType.rog,
      ChoghadiyaType.udveg, ChoghadiyaType.chal, ChoghadiyaType.labh,
      ChoghadiyaType.amrit, ChoghadiyaType.kaal
    ],
  ];

  // Night sequence starting from Sunday (1) to Saturday (7)
  static const List<List<ChoghadiyaType>> _nightSequences = [
    // 0 = Sunday night
    [
      ChoghadiyaType.shubh, ChoghadiyaType.amrit, ChoghadiyaType.chal,
      ChoghadiyaType.rog, ChoghadiyaType.kaal, ChoghadiyaType.labh,
      ChoghadiyaType.udveg, ChoghadiyaType.shubh
    ],
    // 1 = Monday night
    [
      ChoghadiyaType.chal, ChoghadiyaType.rog, ChoghadiyaType.kaal,
      ChoghadiyaType.labh, ChoghadiyaType.udveg, ChoghadiyaType.shubh,
      ChoghadiyaType.amrit, ChoghadiyaType.chal
    ],
    // 2 = Tuesday night
    [
      ChoghadiyaType.kaal, ChoghadiyaType.labh, ChoghadiyaType.udveg,
      ChoghadiyaType.shubh, ChoghadiyaType.amrit, ChoghadiyaType.chal,
      ChoghadiyaType.rog, ChoghadiyaType.kaal
    ],
    // 3 = Wednesday night
    [
      ChoghadiyaType.udveg, ChoghadiyaType.shubh, ChoghadiyaType.amrit,
      ChoghadiyaType.chal, ChoghadiyaType.rog, ChoghadiyaType.kaal,
      ChoghadiyaType.labh, ChoghadiyaType.udveg
    ],
    // 4 = Thursday night
    [
      ChoghadiyaType.amrit, ChoghadiyaType.chal, ChoghadiyaType.rog,
      ChoghadiyaType.kaal, ChoghadiyaType.labh, ChoghadiyaType.udveg,
      ChoghadiyaType.shubh, ChoghadiyaType.amrit
    ],
    // 5 = Friday night
    [
      ChoghadiyaType.rog, ChoghadiyaType.kaal, ChoghadiyaType.labh,
      ChoghadiyaType.udveg, ChoghadiyaType.shubh, ChoghadiyaType.amrit,
      ChoghadiyaType.chal, ChoghadiyaType.rog
    ],
    // 6 = Saturday night
    [
      ChoghadiyaType.labh, ChoghadiyaType.udveg, ChoghadiyaType.shubh,
      ChoghadiyaType.amrit, ChoghadiyaType.chal, ChoghadiyaType.rog,
      ChoghadiyaType.kaal, ChoghadiyaType.labh
    ],
  ];

  static String getNameEnglish(ChoghadiyaType type) {
    switch (type) {
      case ChoghadiyaType.amrit: return 'Amrit';
      case ChoghadiyaType.shubh: return 'Shubh';
      case ChoghadiyaType.labh: return 'Labh';
      case ChoghadiyaType.chal: return 'Chal';
      case ChoghadiyaType.kaal: return 'Kaal';
      case ChoghadiyaType.rog: return 'Rog';
      case ChoghadiyaType.udveg: return 'Udveg';
    }
  }

  static String getNameGujarati(ChoghadiyaType type) {
    switch (type) {
      case ChoghadiyaType.amrit: return 'અમૃત';
      case ChoghadiyaType.shubh: return 'શુભ';
      case ChoghadiyaType.labh: return 'લાભ';
      case ChoghadiyaType.chal: return 'ચંચળ / ચલ';
      case ChoghadiyaType.kaal: return 'કાળ';
      case ChoghadiyaType.rog: return 'રોગ';
      case ChoghadiyaType.udveg: return 'ઉદ્વેગ';
    }
  }

  static String getNameHindi(ChoghadiyaType type) {
    switch (type) {
      case ChoghadiyaType.amrit: return 'अमृत';
      case ChoghadiyaType.shubh: return 'शुभ';
      case ChoghadiyaType.labh: return 'लाभ';
      case ChoghadiyaType.chal: return 'चल';
      case ChoghadiyaType.kaal: return 'काल';
      case ChoghadiyaType.rog: return 'रोग';
      case ChoghadiyaType.udveg: return 'उद्वेग';
    }
  }

  static bool isAuspicious(ChoghadiyaType type) {
    return type == ChoghadiyaType.amrit ||
        type == ChoghadiyaType.shubh ||
        type == ChoghadiyaType.labh;
  }

  /// Calculates all 16 Choghadiya periods (8 Day + 8 Night)
  static List<ChoghadiyaPeriod> calculateChoghadiyas({
    required DateTime sunrise,
    required DateTime sunset,
    required DateTime nextSunrise,
    required int varaIndex, // 1 = Sunday, ..., 7 = Saturday
  }) {
    final list = <ChoghadiyaPeriod>[];
    final dayIdx = (varaIndex - 1) % 7;

    // Day Choghadiya: daylight divided into 8 equal parts
    final dayPartMs = sunset.difference(sunrise).inMilliseconds / 8.0;
    final dayTypes = _daySequences[dayIdx];

    for (int i = 0; i < 8; i++) {
      final start = sunrise.add(Duration(milliseconds: (i * dayPartMs).round()));
      final end = (i == 7) ? sunset : sunrise.add(Duration(milliseconds: ((i + 1) * dayPartMs).round()));
      final type = dayTypes[i];
      list.add(ChoghadiyaPeriod(
        type: type,
        nameEnglish: getNameEnglish(type),
        nameGujarati: getNameGujarati(type),
        nameHindi: getNameHindi(type),
        isDay: true,
        startTime: start,
        endTime: end,
        isAuspicious: isAuspicious(type),
      ));
    }

    // Night Choghadiya: night duration divided into 8 equal parts
    final nightPartMs = nextSunrise.difference(sunset).inMilliseconds / 8.0;
    final nightTypes = _nightSequences[dayIdx];

    for (int i = 0; i < 8; i++) {
      final start = sunset.add(Duration(milliseconds: (i * nightPartMs).round()));
      final end = (i == 7) ? nextSunrise : sunset.add(Duration(milliseconds: ((i + 1) * nightPartMs).round()));
      final type = nightTypes[i];
      list.add(ChoghadiyaPeriod(
        type: type,
        nameEnglish: getNameEnglish(type),
        nameGujarati: getNameGujarati(type),
        nameHindi: getNameHindi(type),
        isDay: false,
        startTime: start,
        endTime: end,
        isAuspicious: isAuspicious(type),
      ));
    }

    return list;
  }

  /// Finds the currently active Choghadiya
  static ChoghadiyaPeriod? getCurrentChoghadiya(List<ChoghadiyaPeriod> periods, DateTime now) {
    for (final p in periods) {
      if (p.contains(now)) return p;
    }
    return null;
  }
}
