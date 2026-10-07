/// Represents a geographical location for astronomical observations.
class GeoLocation {
  final double latitude;
  final double longitude;
  final double altitudeMeters;
  final String cityName;
  final String timezoneId;

  const GeoLocation({
    required this.latitude,
    required this.longitude,
    this.altitudeMeters = 0.0,
    required this.cityName,
    this.timezoneId = 'Asia/Kolkata',
  });

  @override
  String toString() => '$cityName ($latitude, $longitude)';
}
