import 'package:panchang_engine/src/models/geo_location.dart';
import 'package:panchang_engine/src/astronomy/astronomy_service.dart';

void main() {
  final palitana = GeoLocation(
    latitude: 21.5222,
    longitude: 71.8291,
    cityName: 'Palitana',
  );
  final date = DateTime(2026, 10, 7);
  final sunTimes = AstronomyService.getSunTimes(date, palitana);
  print('Sunrise: ${sunTimes.sunrise}');
  print('Sunset: ${sunTimes.sunset}');
  print('Noon: ${sunTimes.solarNoon}');
}
