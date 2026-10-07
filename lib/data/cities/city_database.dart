import 'package:panchang_engine/panchang_engine.dart';

class CityDatabase {
  static const List<GeoLocation> popularCities = [
    GeoLocation(
      cityName: 'Palitana',
      latitude: 21.5222,
      longitude: 71.8291,
      timezoneId: 'Asia/Kolkata',
    ),
    GeoLocation(
      cityName: 'Ahmedabad',
      latitude: 23.0225,
      longitude: 72.5714,
      timezoneId: 'Asia/Kolkata',
    ),
    GeoLocation(
      cityName: 'Mumbai',
      latitude: 19.0760,
      longitude: 72.8777,
      timezoneId: 'Asia/Kolkata',
    ),
    GeoLocation(
      cityName: 'Surat',
      latitude: 21.1702,
      longitude: 72.8311,
      timezoneId: 'Asia/Kolkata',
    ),
    GeoLocation(
      cityName: 'Shikharji (Parasnath)',
      latitude: 23.9627,
      longitude: 86.1306,
      timezoneId: 'Asia/Kolkata',
    ),
    GeoLocation(
      cityName: 'Shravanabelagola',
      latitude: 12.8570,
      longitude: 76.4860,
      timezoneId: 'Asia/Kolkata',
    ),
    GeoLocation(
      cityName: 'Delhi / NCR',
      latitude: 28.6139,
      longitude: 77.2090,
      timezoneId: 'Asia/Kolkata',
    ),
    GeoLocation(
      cityName: 'Jaipur',
      latitude: 26.9124,
      longitude: 75.7873,
      timezoneId: 'Asia/Kolkata',
    ),
    GeoLocation(
      cityName: 'Pune',
      latitude: 18.5204,
      longitude: 73.8567,
      timezoneId: 'Asia/Kolkata',
    ),
    GeoLocation(
      cityName: 'London',
      latitude: 51.5074,
      longitude: -0.1278,
      timezoneId: 'Europe/London',
    ),
    GeoLocation(
      cityName: 'New York',
      latitude: 40.7128,
      longitude: -74.0060,
      timezoneId: 'America/New_York',
    ),
    GeoLocation(
      cityName: 'Antwerp',
      latitude: 51.2194,
      longitude: 4.4025,
      timezoneId: 'Europe/Brussels',
    ),
    GeoLocation(
      cityName: 'Dubai',
      latitude: 25.2048,
      longitude: 55.2708,
      timezoneId: 'Asia/Dubai',
    ),
    GeoLocation(
      cityName: 'Singapore',
      latitude: 1.3521,
      longitude: 103.8198,
      timezoneId: 'Asia/Singapore',
    ),
  ];

  static GeoLocation get defaultCity => popularCities[0]; // Palitana
}
