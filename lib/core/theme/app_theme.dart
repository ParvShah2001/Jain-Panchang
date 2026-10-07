import 'package:flutter/material.dart';

class AppTheme {
  // Saffron / Kesari spiritual accent colors
  static const Color primaryKesari = Color(0xFFE65100); // Deep warm saffron
  static const Color secondaryGold = Color(0xFFD4AF37); // Jain Gold
  static const Color accentMaroon = Color(0xFF880E4F); // Sacred maroon
  static const Color lightBg = Color(0xFFFDFBF7); // Clean warm parchment
  static const Color darkBg = Color(0xFF141210); // Deep soothing night
  static const Color lightCard = Color(0xFFFFFFFF);
  static const Color darkCard = Color(0xFF1E1A17);

  // Auspicious Green & Inauspicious Red for Choghadiya / Muhurats
  static const Color auspiciousGreen = Color(0xFF2E7D32);
  static const Color inauspiciousRed = Color(0xFFC62828);
  static const Color neutralBlue = Color(0xFF1565C0);

  static ThemeData lightTheme = ThemeData(
    useMaterial3: true,
    brightness: Brightness.light,
    colorScheme: ColorScheme.fromSeed(
      seedColor: primaryKesari,
      brightness: Brightness.light,
      surface: lightBg,
      primary: primaryKesari,
      secondary: secondaryGold,
    ),
    scaffoldBackgroundColor: lightBg,
    cardTheme: CardThemeData(
      color: lightCard,
      elevation: 1,
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
    ),
    appBarTheme: const AppBarTheme(
      backgroundColor: lightBg,
      foregroundColor: Color(0xFF212121),
      elevation: 0,
      centerTitle: true,
    ),
  );

  static ThemeData darkTheme = ThemeData(
    useMaterial3: true,
    brightness: Brightness.dark,
    colorScheme: ColorScheme.fromSeed(
      seedColor: primaryKesari,
      brightness: Brightness.dark,
      surface: darkBg,
      primary: primaryKesari,
      secondary: secondaryGold,
    ),
    scaffoldBackgroundColor: darkBg,
    cardTheme: CardThemeData(
      color: darkCard,
      elevation: 1,
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
    ),
    appBarTheme: const AppBarTheme(
      backgroundColor: darkBg,
      foregroundColor: Color(0xFFFAFAFA),
      elevation: 0,
      centerTitle: true,
    ),
  );
}
