/// Selectable Ayanamsha algorithms
enum AyanamshaType {
  lahiri, // Chitrapaksha / Indian Government standard
  raman,
  krishnamurti,
  faganBradley,
  tropical, // Sayana (0 ayanamsha)
}

/// Helper methods to calculate Ayanamsha angle in degrees.
class AyanamshaCalculator {
  /// Calculate Ayanamsha in degrees for a given Julian Day.
  /// Standard Lahiri Ayanamsha epoch: J2000.0 (JD 2451545.0) = 23° 51' 11" = 23.85305556°
  /// Precession rate: ~50.29 arcseconds per Julian year (365.25 days)
  static double calculateAyanamsha(double jd, [AyanamshaType type = AyanamshaType.lahiri]) {
    final t = (jd - 2451545.0) / 36525.0; // Julian centuries from J2000.0
    
    switch (type) {
      case AyanamshaType.lahiri:
        // High-precision polynomial for Lahiri/Chitrapaksha ayanamsha:
        // At J2000.0 (2000-01-01 12:00 TT): 23.85305556 degrees (23° 51' 11")
        // Precession in longitude (IAU): 5028.796195 arcsec/century -> 1.39688783 deg/century
        return 23.85305556 + 1.39688783 * t + 0.00030833 * t * t;
      case AyanamshaType.raman:
        // Raman ayanamsha: ~22.40° at J2000.0
        return 22.40 + 1.39688783 * t;
      case AyanamshaType.krishnamurti:
        // KP ayanamsha: ~23.8183° at J2000.0
        return 23.81833333 + 1.39688783 * t;
      case AyanamshaType.faganBradley:
        // Fagan-Bradley ayanamsha: ~24.736° at J2000.0
        return 24.73638889 + 1.39688783 * t;
      case AyanamshaType.tropical:
        return 0.0;
    }
  }
}
