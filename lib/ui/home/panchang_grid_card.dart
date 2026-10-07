import 'package:flutter/material.dart';
import 'package:intl/intl.dart';
import 'package:panchang_engine/panchang_engine.dart';

class PanchangGridCard extends StatelessWidget {
  final DailyPanchang panchang;
  final String language;

  const PanchangGridCard({
    super.key,
    required this.panchang,
    required this.language,
  });

  @override
  Widget build(BuildContext context) {
    final timeFmt = DateFormat('hh:mm a');

    final tithiTitle = language == 'gu' ? 'તિથિ' : (language == 'hi' ? 'तिथि' : 'Tithi');
    final nakshatraTitle = language == 'gu' ? 'નક્ષત્ર' : (language == 'hi' ? 'नक्षत्र' : 'Nakshatra');
    final yogaTitle = language == 'gu' ? 'યોગ' : (language == 'hi' ? 'योग' : 'Yoga');
    final karanaTitle = language == 'gu' ? 'કરણ' : (language == 'hi' ? 'करण' : 'Karana');

    final tithiVal = language == 'gu'
        ? panchang.udayaTithi.nameGujarati
        : (language == 'hi' ? panchang.udayaTithi.nameHindi : panchang.udayaTithi.nameEnglish);

    final nakVal = language == 'gu'
        ? panchang.nakshatra.nameGujarati
        : (language == 'hi' ? panchang.nakshatra.nameHindi : panchang.nakshatra.nameEnglish);

    return Card(
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Row(
              children: [
                Expanded(
                  child: _buildTile(
                    title: tithiTitle,
                    mainValue: tithiVal,
                    subtitle: 'સુધી: ${timeFmt.format(panchang.udayaTithi.endTime)}',
                    badge: panchang.isVriddhiTithi
                        ? 'વૃદ્ધિ'
                        : (panchang.isKshayaTithi ? 'ક્ષય' : null),
                  ),
                ),
                Container(width: 1, height: 60, color: Colors.grey.withValues(alpha: 0.2)),
                Expanded(
                  child: _buildTile(
                    title: nakshatraTitle,
                    mainValue: nakVal,
                    subtitle: 'સુધી: ${timeFmt.format(panchang.nakshatra.endTime)}',
                  ),
                ),
              ],
            ),
            const Divider(height: 24),
            Row(
              children: [
                Expanded(
                  child: _buildTile(
                    title: yogaTitle,
                    mainValue: panchang.yoga.nameEnglish,
                    subtitle: null,
                  ),
                ),
                Container(width: 1, height: 60, color: Colors.grey.withValues(alpha: 0.2)),
                Expanded(
                  child: _buildTile(
                    title: karanaTitle,
                    mainValue: panchang.karana.nameEnglish,
                    subtitle: null,
                  ),
                ),
              ],
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildTile({
    required String title,
    required String mainValue,
    required String? subtitle,
    String? badge,
  }) {
    return Padding(
      padding: const EdgeInsets.symmetric(horizontal: 12),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            children: [
              Text(
                title,
                style: const TextStyle(fontSize: 12, color: Colors.grey, fontWeight: FontWeight.w500),
              ),
              if (badge != null) ...[
                const SizedBox(width: 6),
                Container(
                  padding: const EdgeInsets.symmetric(horizontal: 6, vertical: 2),
                  decoration: BoxDecoration(
                    color: Colors.red.shade100,
                    borderRadius: BorderRadius.circular(4),
                  ),
                  child: Text(
                    badge,
                    style: TextStyle(color: Colors.red.shade900, fontSize: 10, fontWeight: FontWeight.bold),
                  ),
                ),
              ],
            ],
          ),
          const SizedBox(height: 4),
          Text(
            mainValue,
            style: const TextStyle(fontSize: 16, fontWeight: FontWeight.bold),
            maxLines: 1,
            overflow: TextOverflow.ellipsis,
          ),
          if (subtitle != null) ...[
            const SizedBox(height: 2),
            Text(
              subtitle,
              style: TextStyle(fontSize: 11, color: Colors.grey.shade600),
            ),
          ],
        ],
      ),
    );
  }
}
