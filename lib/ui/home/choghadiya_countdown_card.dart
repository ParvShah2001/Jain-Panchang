import 'package:flutter/material.dart';
import 'package:intl/intl.dart';
import 'package:panchang_engine/panchang_engine.dart';

class ChoghadiyaCountdownCard extends StatelessWidget {
  final ChoghadiyaPeriod? activeChoghadiya;
  final String language;

  const ChoghadiyaCountdownCard({
    super.key,
    required this.activeChoghadiya,
    required this.language,
  });

  String _formatDuration(Duration d) {
    final hours = d.inHours;
    final mins = d.inMinutes.remainder(60);
    final secs = d.inSeconds.remainder(60);
    if (hours > 0) {
      return '${hours}h ${mins.toString().padLeft(2, '0')}m ${secs.toString().padLeft(2, '0')}s';
    }
    return '${mins}m ${secs.toString().padLeft(2, '0')}s';
  }

  @override
  Widget build(BuildContext context) {
    if (activeChoghadiya == null) {
      return const SizedBox.shrink();
    }

    final p = activeChoghadiya!;
    final now = DateTime.now();
    final remaining = p.remaining(now);

    final name = language == 'gu'
        ? p.nameGujarati
        : (language == 'hi' ? p.nameHindi : p.nameEnglish);

    final statusText = p.isAuspicious
        ? (language == 'gu' ? 'શુભ મુહૂર્ત' : (language == 'hi' ? 'शुभ मुहूर्त' : 'Auspicious'))
        : (language == 'gu' ? 'અશુભ / ત્યાજ્ય' : (language == 'hi' ? 'अशुभ / त्याज्य' : 'Inauspicious'));

    final badgeColor = p.isAuspicious
        ? const Color(0xFF2E7D32)
        : (p.type == ChoghadiyaType.chal ? const Color(0xFF1565C0) : const Color(0xFFC62828));

    final timeFmt = DateFormat('hh:mm a');

    return Container(
      padding: const EdgeInsets.all(16),
      decoration: BoxDecoration(
        color: badgeColor.withValues(alpha: 0.08),
        borderRadius: BorderRadius.circular(16),
        border: Border.all(color: badgeColor.withValues(alpha: 0.3), width: 1.5),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              Row(
                children: [
                  Icon(
                    p.isDay ? Icons.wb_sunny : Icons.nightlight_round,
                    size: 20,
                    color: badgeColor,
                  ),
                  const SizedBox(width: 8),
                  Text(
                    language == 'gu'
                        ? 'ચાલુ ચોઘડિયું'
                        : (language == 'hi' ? 'वर्तमान चौघड़िया' : 'Current Choghadiya'),
                    style: TextStyle(
                      fontSize: 13,
                      fontWeight: FontWeight.w600,
                      color: badgeColor,
                    ),
                  ),
                ],
              ),
              Container(
                padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 4),
                decoration: BoxDecoration(
                  color: badgeColor,
                  borderRadius: BorderRadius.circular(12),
                ),
                child: Text(
                  statusText,
                  style: const TextStyle(
                    color: Colors.white,
                    fontSize: 11,
                    fontWeight: FontWeight.bold,
                  ),
                ),
              ),
            ],
          ),
          const SizedBox(height: 10),
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            crossAxisAlignment: CrossAxisAlignment.end,
            children: [
              Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(
                    name,
                    style: const TextStyle(
                      fontSize: 26,
                      fontWeight: FontWeight.bold,
                    ),
                  ),
                  Text(
                    '${timeFmt.format(p.startTime)} - ${timeFmt.format(p.endTime)}',
                    style: TextStyle(
                      fontSize: 13,
                      color: Colors.grey.shade600,
                    ),
                  ),
                ],
              ),
              Column(
                crossAxisAlignment: CrossAxisAlignment.end,
                children: [
                  Text(
                    language == 'gu' ? 'બાકી સમય' : (language == 'hi' ? 'शेष समय' : 'Remaining'),
                    style: TextStyle(fontSize: 12, color: Colors.grey.shade600),
                  ),
                  Text(
                    _formatDuration(remaining),
                    style: TextStyle(
                      fontSize: 18,
                      fontWeight: FontWeight.bold,
                      color: badgeColor,
                    ),
                  ),
                ],
              ),
            ],
          ),
        ],
      ),
    );
  }
}
