import 'dart:convert';
import 'package:flutter/services.dart';
import 'package:panchang_engine/panchang_engine.dart';

class JainEventItem {
  final String id;
  final String nameEnglish;
  final String nameGujarati;
  final String nameHindi;
  final String description;
  final bool isFestival;
  final String? importance;

  const JainEventItem({
    required this.id,
    required this.nameEnglish,
    required this.nameGujarati,
    required this.nameHindi,
    required this.description,
    this.isFestival = true,
    this.importance,
  });
}

class FestivalRepository {
  static List<Map<String, dynamic>> _festivals = [];
  static List<Map<String, dynamic>> _kalyanaks = [];
  static bool _isLoaded = false;

  static Future<void> loadData() async {
    if (_isLoaded) return;
    try {
      final fStr = await rootBundle.loadString('assets/data/festivals/jain_festivals.json');
      _festivals = (jsonDecode(fStr) as List).cast<Map<String, dynamic>>();

      final kStr = await rootBundle.loadString('assets/data/kalyanaks/tirthankar_kalyanaks.json');
      _kalyanaks = (jsonDecode(kStr) as List).cast<Map<String, dynamic>>();

      _isLoaded = true;
    } catch (_) {
      // Fallback if running outside asset bundle
    }
  }

  /// Finds any festivals or Tirthankar Kalyanaks matching the given day's Panchang
  static List<JainEventItem> getEventsForDay(DailyPanchang panchang) {
    final events = <JainEventItem>[];
    final tithiNum = panchang.udayaTithi.tithiNumber;
    final isShukla = panchang.udayaTithi.paksha == Paksha.shukla;
    final pakshaStr = isShukla ? 'shukla' : 'krishna';

    for (final f in _festivals) {
      if (f['paksha'] == pakshaStr && f['tithi'] == tithiNum) {
        events.add(JainEventItem(
          id: f['id'] as String,
          nameEnglish: f['nameEnglish'] as String,
          nameGujarati: f['nameGujarati'] as String,
          nameHindi: f['nameHindi'] as String,
          description: f['description'] as String,
          isFestival: true,
          importance: f['importance'] as String?,
        ));
      }
    }

    for (final k in _kalyanaks) {
      final tirthankarName = k['name'] as String;
      final kMap = k['kalyanaks'] as Map<String, dynamic>;
      for (final entry in kMap.entries) {
        final kType = entry.key; // chyavan, janma, diksha, kevalgyan, moksha
        final rule = entry.value as Map<String, dynamic>;
        if (rule['paksha'] == pakshaStr && rule['tithi'] == tithiNum) {
          events.add(JainEventItem(
            id: '${k["number"]}_$kType',
            nameEnglish: '$tirthankarName - ${_kalyanakEnglish(kType)}',
            nameGujarati: '$tirthankarName - ${_kalyanakGujarati(kType)}',
            nameHindi: '$tirthankarName - ${_kalyanakHindi(kType)}',
            description: 'Pancha Kalyanak celebration of Tirthankar #$tirthankarName',
            isFestival: false,
          ));
        }
      }
    }

    return events;
  }

  static String _kalyanakEnglish(String type) {
    switch (type) {
      case 'chyavan': return 'Chyavan (Conception) Kalyanak';
      case 'janma': return 'Janma (Birth) Kalyanak';
      case 'diksha': return 'Diksha (Renunciation) Kalyanak';
      case 'kevalgyan': return 'Kevalgyan (Omniscience) Kalyanak';
      case 'moksha': return 'Moksha (Nirvan) Kalyanak';
      default: return 'Kalyanak';
    }
  }

  static String _kalyanakGujarati(String type) {
    switch (type) {
      case 'chyavan': return 'ચ્યવન કલ્યાણક';
      case 'janma': return 'જન્મ કલ્યાણક';
      case 'diksha': return 'દીક્ષા કલ્યાણક';
      case 'kevalgyan': return 'કેવળજ્ઞાન કલ્યાણક';
      case 'moksha': return 'મોક્ષ કલ્યાણક';
      default: return 'કલ્યાણક';
    }
  }

  static String _kalyanakHindi(String type) {
    switch (type) {
      case 'chyavan': return 'च्यवन कल्याणक';
      case 'janma': return 'जन्म कल्याणक';
      case 'diksha': return 'दीक्षा कल्याणक';
      case 'kevalgyan': return 'केवलज्ञान कल्याणक';
      case 'moksha': return 'मोक्ष कल्याणक';
      default: return 'कल्याणक';
    }
  }
}
