import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:intl/intl.dart';
import 'package:panchang_engine/panchang_engine.dart';
import '../../providers/panchang_providers.dart';

class TimingsScreen extends ConsumerWidget {
  const TimingsScreen({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final panchang = ref.watch(dailyPanchangProvider);
    final choghadiyas = ref.watch(choghadiyasProvider);
    final pachkhanList = ref.watch(pachkhanListProvider);
    final language = ref.watch(selectedLanguageProvider);

    final sunrise = panchang.sunTimes.sunrise;
    final sunset = panchang.sunTimes.sunset;
    final abhijit = MuhuratService.calculateAbhijitMuhurat(sunrise, sunset);
    final rahu = MuhuratService.calculateRahuKalam(sunrise, sunset, panchang.varaIndex);
    final yama = MuhuratService.calculateYamaganda(sunrise, sunset, panchang.varaIndex);
    final gulika = MuhuratService.calculateGulikaKalam(sunrise, sunset, panchang.varaIndex);

    final timeFmt = DateFormat('hh:mm a');

    return DefaultTabController(
      length: 3,
      child: Scaffold(
        appBar: AppBar(
          title: Text(
            language == 'gu' ? 'ચોઘડિયા અને મુહૂર્ત' : (language == 'hi' ? 'चौघड़िया एवं मुहूर्त' : 'Choghadiya & Muhurats'),
            style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 18),
          ),
          bottom: TabBar(
            tabs: [
              Tab(text: language == 'gu' ? 'ચોઘડિયા' : 'Choghadiya'),
              Tab(text: language == 'gu' ? 'મુહૂર્ત' : 'Muhurat'),
              Tab(text: language == 'gu' ? 'પચ્ચક્ખાણ' : 'Pachkhan'),
            ],
          ),
        ),
        body: TabBarView(
          children: [
            // Tab 1: Day & Night Choghadiya List
            ListView.builder(
              padding: const EdgeInsets.all(16),
              itemCount: choghadiyas.length,
              itemBuilder: (ctx, i) {
                final p = choghadiyas[i];
                final name = language == 'gu' ? p.nameGujarati : (language == 'hi' ? p.nameHindi : p.nameEnglish);
                final color = p.isAuspicious
                    ? const Color(0xFF2E7D32)
                    : (p.type == ChoghadiyaType.chal ? const Color(0xFF1565C0) : const Color(0xFFC62828));

                return Card(
                  margin: const EdgeInsets.symmetric(vertical: 4),
                  child: ListTile(
                    leading: CircleAvatar(
                      backgroundColor: color.withValues(alpha: 0.15),
                      child: Icon(p.isDay ? Icons.wb_sunny : Icons.nightlight_round, color: color, size: 20),
                    ),
                    title: Text(name, style: const TextStyle(fontWeight: FontWeight.bold)),
                    subtitle: Text('${p.isDay ? "દિવસ" : "રાત્રિ"} • ${p.isAuspicious ? "શુભ" : "અશુભ"}'),
                    trailing: Text(
                      '${timeFmt.format(p.startTime)} - ${timeFmt.format(p.endTime)}',
                      style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 12),
                    ),
                  ),
                );
              },
            ),

            // Tab 2: Muhurats (Abhijit, Rahu, Yamaganda, Gulika)
            ListView(
              padding: const EdgeInsets.all(16),
              children: [
                _buildMuhuratCard('અભિજિત મુહૂર્ત (Abhijit)', abhijit, true, timeFmt),
                _buildMuhuratCard('રાહુ કાળ (Rahu Kalam)', rahu, false, timeFmt),
                _buildMuhuratCard('યમગંડ કાળ (Yamaganda)', yama, false, timeFmt),
                _buildMuhuratCard('ગુલિક કાળ (Gulika)', gulika, true, timeFmt),
              ],
            ),

            // Tab 3: Pachkhan Timings
            ListView.builder(
              padding: const EdgeInsets.all(16),
              itemCount: pachkhanList.length,
              itemBuilder: (ctx, i) {
                final item = pachkhanList[i];
                final name = language == 'gu' ? item.nameGujarati : (language == 'hi' ? item.nameHindi : item.nameEnglish);
                return Card(
                  margin: const EdgeInsets.symmetric(vertical: 4),
                  child: ListTile(
                    leading: const CircleAvatar(
                      backgroundColor: Color(0xFFFFF3E0),
                      child: Icon(Icons.timer_outlined, color: Color(0xFFE65100), size: 20),
                    ),
                    title: Text(name, style: const TextStyle(fontWeight: FontWeight.bold)),
                    subtitle: Text(item.rule.description),
                    trailing: Text(
                      timeFmt.format(item.time),
                      style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 14, color: Color(0xFFE65100)),
                    ),
                  ),
                );
              },
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildMuhuratCard(String title, MuhuratPeriod p, bool isGood, DateFormat fmt) {
    final color = isGood ? const Color(0xFF2E7D32) : const Color(0xFFC62828);
    return Card(
      margin: const EdgeInsets.symmetric(vertical: 6),
      child: ListTile(
        leading: CircleAvatar(
          backgroundColor: color.withValues(alpha: 0.15),
          child: Icon(isGood ? Icons.check_circle_outline : Icons.cancel_outlined, color: color),
        ),
        title: Text(title, style: const TextStyle(fontWeight: FontWeight.bold)),
        subtitle: Text(isGood ? 'શુભ મુહૂર્ત' : 'ત્યાજ્ય / અશુભ કાળ'),
        trailing: Text(
          '${fmt.format(p.startTime)} - ${fmt.format(p.endTime)}',
          style: const TextStyle(fontWeight: FontWeight.bold),
        ),
      ),
    );
  }
}
