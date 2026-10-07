import 'package:flutter/material.dart';
import 'package:intl/intl.dart';
import 'package:panchang_engine/panchang_engine.dart';

class PachkhanOverviewCard extends StatelessWidget {
  final List<PachkhanItem> pachkhanList;
  final String language;

  const PachkhanOverviewCard({
    super.key,
    required this.pachkhanList,
    required this.language,
  });

  @override
  Widget build(BuildContext context) {
    final title = language == 'gu' ? 'પચ્ચક્ખાણ સમય' : (language == 'hi' ? 'पच्चक्खान समय' : 'Pachkhan Timings');
    final timeFmt = DateFormat('hh:mm a');

    return Card(
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Row(
              children: [
                const Icon(Icons.access_time_filled, size: 20, color: Color(0xFFE65100)),
                const SizedBox(width: 8),
                Text(
                  title,
                  style: const TextStyle(fontSize: 16, fontWeight: FontWeight.bold),
                ),
              ],
            ),
            const SizedBox(height: 12),
            ...pachkhanList.map((item) {
              final name = language == 'gu'
                  ? item.nameGujarati
                  : (language == 'hi' ? item.nameHindi : item.nameEnglish);

              return Padding(
                padding: const EdgeInsets.symmetric(vertical: 4),
                child: Row(
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: [
                    Text(
                      name,
                      style: const TextStyle(fontSize: 14, fontWeight: FontWeight.w500),
                    ),
                    Text(
                      timeFmt.format(item.time),
                      style: const TextStyle(
                        fontSize: 14,
                        fontWeight: FontWeight.bold,
                        color: Color(0xFFE65100),
                      ),
                    ),
                  ],
                ),
              );
            }),
          ],
        ),
      ),
    );
  }
}
