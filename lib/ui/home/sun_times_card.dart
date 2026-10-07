import 'package:flutter/material.dart';
import 'package:intl/intl.dart';
import 'package:panchang_engine/panchang_engine.dart';

class SunTimesCard extends StatelessWidget {
  final SunTimes sunTimes;
  final MoonTimes moonTimes;
  final String language;

  const SunTimesCard({
    super.key,
    required this.sunTimes,
    required this.moonTimes,
    required this.language,
  });

  @override
  Widget build(BuildContext context) {
    final timeFmt = DateFormat('hh:mm a');

    final sunriseText = language == 'gu' ? 'સૂર્યોદય' : (language == 'hi' ? 'सूर्योदय' : 'Sunrise');
    final sunsetText = language == 'gu' ? 'સૂર્યાસ્ત' : (language == 'hi' ? 'सूर्यास्त' : 'Sunset');
    final noonText = language == 'gu' ? 'મધ્યાહ્ન' : (language == 'hi' ? 'मध्याह्न' : 'Noon');
    final dayLengthText = language == 'gu' ? 'દિવસમાન' : (language == 'hi' ? 'दिनमान' : 'Day Length');

    final hours = sunTimes.dayDuration.inHours;
    final mins = sunTimes.dayDuration.inMinutes.remainder(60);

    return Card(
      child: Padding(
        padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 14),
        child: Row(
          mainAxisAlignment: MainAxisAlignment.spaceAround,
          children: [
            _buildItem(
              icon: Icons.wb_sunny_outlined,
              label: sunriseText,
              value: timeFmt.format(sunTimes.sunrise),
              color: Colors.orange.shade800,
            ),
            _buildItem(
              icon: Icons.wb_twilight,
              label: noonText,
              value: timeFmt.format(sunTimes.solarNoon),
              color: Colors.amber.shade800,
            ),
            _buildItem(
              icon: Icons.nights_stay_outlined,
              label: sunsetText,
              value: timeFmt.format(sunTimes.sunset),
              color: Colors.deepOrange.shade700,
            ),
            _buildItem(
              icon: Icons.timelapse,
              label: dayLengthText,
              value: '${hours}h ${mins}m',
              color: Colors.teal.shade700,
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildItem({
    required IconData icon,
    required String label,
    required String value,
    required Color color,
  }) {
    return Column(
      children: [
        Icon(icon, size: 22, color: color),
        const SizedBox(height: 4),
        Text(
          label,
          style: const TextStyle(fontSize: 11, color: Colors.grey),
        ),
        const SizedBox(height: 2),
        Text(
          value,
          style: const TextStyle(fontSize: 13, fontWeight: FontWeight.bold),
        ),
      ],
    );
  }
}
