/// Model for Muhurat interval
class MuhuratPeriod {
  final String name;
  final String nameHindi;
  final String nameGujarati;
  final DateTime startTime;
  final DateTime endTime;
  final bool isAuspicious;

  const MuhuratPeriod({
    required this.name,
    required this.nameHindi,
    required this.nameGujarati,
    required this.startTime,
    required this.endTime,
    required this.isAuspicious,
  });

  bool contains(DateTime time) => time.isAfter(startTime) && time.isBefore(endTime);
}

/// Computes Vedic Muhurats: Abhijit, Rahu Kalam, Yamaganda, Gulika Kalam, Dur Muhurat
class MuhuratService {
  /// Abhijit Muhurat: 8th Muhurat of the day (daylight divided into 15 equal parts).
  /// Abhijit is centered around solar noon.
  static MuhuratPeriod calculateAbhijitMuhurat(DateTime sunrise, DateTime sunset) {
    final dayMs = sunset.difference(sunrise).inMilliseconds;
    final muhuratMs = dayMs / 15.0; // 1 Muhurat = 1/15th of daytime (~48 min)
    final start = sunrise.add(Duration(milliseconds: (7 * muhuratMs).round()));
    final end = sunrise.add(Duration(milliseconds: (8 * muhuratMs).round()));

    return MuhuratPeriod(
      name: 'Abhijit Muhurat',
      nameHindi: 'अभिजित मुहूर्त',
      nameGujarati: 'અભિજિત મુહૂર્ત',
      startTime: start,
      endTime: end,
      isAuspicious: true,
    );
  }

  /// Rahu Kalam: 1/8th part of daytime, day-dependent order:
  /// Mon: 2nd, Tue: 7th, Wed: 5th, Thu: 6th, Fri: 4th, Sat: 3rd, Sun: 8th
  static MuhuratPeriod calculateRahuKalam(DateTime sunrise, DateTime sunset, int varaIndex) {
    const rahuParts = [
      8, // 1 = Sun (8th part)
      2, // 2 = Mon (2nd part)
      7, // 3 = Tue (7th part)
      5, // 4 = Wed (5th part)
      6, // 5 = Thu (6th part)
      4, // 6 = Fri (4th part)
      3, // 7 = Sat (3rd part)
    ];

    final partNumber = rahuParts[(varaIndex - 1) % 7];
    final dayMs = sunset.difference(sunrise).inMilliseconds;
    final partMs = dayMs / 8.0;

    final start = sunrise.add(Duration(milliseconds: ((partNumber - 1) * partMs).round()));
    final end = sunrise.add(Duration(milliseconds: (partNumber * partMs).round()));

    return MuhuratPeriod(
      name: 'Rahu Kalam',
      nameHindi: 'राहु काल',
      nameGujarati: 'રાહુ કાળ',
      startTime: start,
      endTime: end,
      isAuspicious: false,
    );
  }

  /// Yamaganda Kalam:
  /// Sun: 5th, Mon: 4th, Tue: 3rd, Wed: 2nd, Thu: 1st, Fri: 7th, Sat: 6th
  static MuhuratPeriod calculateYamaganda(DateTime sunrise, DateTime sunset, int varaIndex) {
    const yamaParts = [
      5, // Sun
      4, // Mon
      3, // Tue
      2, // Wed
      1, // Thu
      7, // Fri
      6, // Sat
    ];

    final partNumber = yamaParts[(varaIndex - 1) % 7];
    final dayMs = sunset.difference(sunrise).inMilliseconds;
    final partMs = dayMs / 8.0;

    final start = sunrise.add(Duration(milliseconds: ((partNumber - 1) * partMs).round()));
    final end = sunrise.add(Duration(milliseconds: (partNumber * partMs).round()));

    return MuhuratPeriod(
      name: 'Yamaganda Kalam',
      nameHindi: 'यमगंड काल',
      nameGujarati: 'યમગંડ કાળ',
      startTime: start,
      endTime: end,
      isAuspicious: false,
    );
  }

  /// Gulika Kalam:
  /// Sun: 7th, Mon: 6th, Tue: 5th, Wed: 4th, Thu: 3rd, Fri: 2nd, Sat: 1st
  static MuhuratPeriod calculateGulikaKalam(DateTime sunrise, DateTime sunset, int varaIndex) {
    const gulikaParts = [
      7, // Sun
      6, // Mon
      5, // Tue
      4, // Wed
      3, // Thu
      2, // Fri
      1, // Sat
    ];

    final partNumber = gulikaParts[(varaIndex - 1) % 7];
    final dayMs = sunset.difference(sunrise).inMilliseconds;
    final partMs = dayMs / 8.0;

    final start = sunrise.add(Duration(milliseconds: ((partNumber - 1) * partMs).round()));
    final end = sunrise.add(Duration(milliseconds: (partNumber * partMs).round()));

    return MuhuratPeriod(
      name: 'Gulika Kalam',
      nameHindi: 'गुलिक काल',
      nameGujarati: 'ગુલિક કાળ',
      startTime: start,
      endTime: end,
      isAuspicious: true,
    );
  }
}
