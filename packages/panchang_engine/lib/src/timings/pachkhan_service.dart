/// Jain Pachkhan Rule Formula definition
/// Allows scholarly customization of offsets or daytime fractions.
class PachkhanRule {
  final String id;
  final String nameEnglish;
  final String nameGujarati;
  final String nameHindi;
  final String description;

  /// Fraction of daytime duration (e.g. 0.25 for 1 Prahara/Porsi, 0.375 for 1.5 Prahara/Sadh Porsi, 0.5 for Purimuddh)
  final double dayFraction;

  /// Fixed minute offset from sunrise (e.g. 48 minutes for Navkarshi)
  final int fixedMinutesFromSunrise;

  /// Relative to sunset if true (e.g. Chauvihar)
  final bool isRelativeToSunset;
  final int offsetMinutesFromSunset;

  const PachkhanRule({
    required this.id,
    required this.nameEnglish,
    required this.nameGujarati,
    required this.nameHindi,
    required this.description,
    this.dayFraction = 0.0,
    this.fixedMinutesFromSunrise = 0,
    this.isRelativeToSunset = false,
    this.offsetMinutesFromSunset = 0,
  });

  Map<String, dynamic> toJson() => {
    'id': id,
    'nameEnglish': nameEnglish,
    'nameGujarati': nameGujarati,
    'nameHindi': nameHindi,
    'description': description,
    'dayFraction': dayFraction,
    'fixedMinutesFromSunrise': fixedMinutesFromSunrise,
    'isRelativeToSunset': isRelativeToSunset,
    'offsetMinutesFromSunset': offsetMinutesFromSunset,
  };

  factory PachkhanRule.fromJson(Map<String, dynamic> json) => PachkhanRule(
    id: json['id'] as String,
    nameEnglish: json['nameEnglish'] as String,
    nameGujarati: json['nameGujarati'] as String,
    nameHindi: json['nameHindi'] as String,
    description: json['description'] as String,
    dayFraction: (json['dayFraction'] as num?)?.toDouble() ?? 0.0,
    fixedMinutesFromSunrise: json['fixedMinutesFromSunrise'] as int? ?? 0,
    isRelativeToSunset: json['isRelativeToSunset'] as bool? ?? false,
    offsetMinutesFromSunset: json['offsetMinutesFromSunset'] as int? ?? 0,
  );
}

/// A computed Pachkhan item with exact target time
class PachkhanItem {
  final PachkhanRule rule;
  final DateTime time;

  const PachkhanItem({
    required this.rule,
    required this.time,
  });

  String get id => rule.id;
  String get nameEnglish => rule.nameEnglish;
  String get nameGujarati => rule.nameGujarati;
  String get nameHindi => rule.nameHindi;
}

/// Service to calculate Jain Pachkhan Timings: Navkarshi, Porsi, Sadh Porsi,
/// Purimuddh, Avaddh, Chauvihar/Sunset, Biyasana, Ekasana.
class PachkhanService {
  /// Default rules for Shwetambar / Digambar tradition
  static const List<PachkhanRule> defaultRules = [
    PachkhanRule(
      id: 'navkarshi',
      nameEnglish: 'Navkarshi',
      nameGujarati: 'નવકારશી',
      nameHindi: 'नवकारशी',
      description: 'Sunrise + 2 Muhurats (48 minutes)',
      fixedMinutesFromSunrise: 48,
    ),
    PachkhanRule(
      id: 'porsi',
      nameEnglish: 'Porsi',
      nameGujarati: 'પોરસી',
      nameHindi: 'पोरसी',
      description: 'Sunrise + 1 Prahara (1/4th of daylight)',
      dayFraction: 0.25,
    ),
    PachkhanRule(
      id: 'sadh_porsi',
      nameEnglish: 'Sadh Porsi',
      nameGujarati: 'સાઢ પોરસી',
      nameHindi: 'साढ़ पोरसी',
      description: 'Sunrise + 1.5 Prahara (3/8th of daylight)',
      dayFraction: 0.375,
    ),
    PachkhanRule(
      id: 'purimuddh',
      nameEnglish: 'Purimuddh',
      nameGujarati: 'પુરિમુડ્ઢ',
      nameHindi: 'पुरिमूढ़',
      description: 'Sunrise + 2 Prahara (Midday / 1/2 of daylight)',
      dayFraction: 0.50,
    ),
    PachkhanRule(
      id: 'avaddh',
      nameEnglish: 'Avaddh',
      nameGujarati: 'અવડ્ઢ',
      nameHindi: 'अवढ्ढ',
      description: 'Sunrise + 3 Prahara (3/4th of daylight)',
      dayFraction: 0.75,
    ),
    PachkhanRule(
      id: 'ekasana_biyasana',
      nameEnglish: 'Ekasana / Biyasana',
      nameGujarati: 'એકાસણા / બિયાસણા',
      nameHindi: 'एकासन / बियासन',
      description: 'Observance guidance completed during daytime before sunset',
      isRelativeToSunset: true,
      offsetMinutesFromSunset: -48,
    ),
    PachkhanRule(
      id: 'chauvihar',
      nameEnglish: 'Chauvihar (Sunset)',
      nameGujarati: 'ચૌવિહાર',
      nameHindi: 'चौविहार',
      description: 'At sunset (food and drink abstention until next sunrise)',
      isRelativeToSunset: true,
      offsetMinutesFromSunset: 0,
    ),
  ];

  /// Calculates Pachkhan times for a given day using configurable rules
  static List<PachkhanItem> calculatePachkhanTimes({
    required DateTime sunrise,
    required DateTime sunset,
    List<PachkhanRule> rules = defaultRules,
  }) {
    final dayMs = sunset.difference(sunrise).inMilliseconds;
    final items = <PachkhanItem>[];

    for (final rule in rules) {
      DateTime time;
      if (rule.isRelativeToSunset) {
        time = sunset.add(Duration(minutes: rule.offsetMinutesFromSunset));
      } else if (rule.dayFraction > 0.0) {
        final addMs = (dayMs * rule.dayFraction).round();
        time = sunrise.add(Duration(milliseconds: addMs));
      } else {
        time = sunrise.add(Duration(minutes: rule.fixedMinutesFromSunrise));
      }

      items.add(PachkhanItem(rule: rule, time: time));
    }

    return items;
  }
}
