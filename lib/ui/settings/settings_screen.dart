import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:panchang_engine/panchang_engine.dart';
import '../../providers/panchang_providers.dart';

class SettingsScreen extends ConsumerWidget {
  const SettingsScreen({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final currentSampraday = ref.watch(selectedSampradayProvider);
    final currentAyanamsha = ref.watch(selectedAyanamshaProvider);
    final currentLanguage = ref.watch(selectedLanguageProvider);

    return Scaffold(
      appBar: AppBar(
        title: const Text('સેટિંગ્સ (Settings)', style: TextStyle(fontWeight: FontWeight.bold, fontSize: 18)),
      ),
      body: ListView(
        padding: const EdgeInsets.all(16),
        children: [
          // Sampraday Selector
          Card(
            child: Padding(
              padding: const EdgeInsets.all(16),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  const Text('સંપ્રદાય પસંદગી (Sampraday / Tradition)', style: TextStyle(fontWeight: FontWeight.bold, fontSize: 15)),
                  const SizedBox(height: 4),
                  const Text('તિથિ નિયમો અને પર્યુષણ/સંવત્સરી દિવસ મુજબ', style: TextStyle(fontSize: 12, color: Colors.grey)),
                  const SizedBox(height: 8),
                  DropdownButton<JainSampraday>(
                    value: currentSampraday,
                    isExpanded: true,
                    items: const [
                      DropdownMenuItem(
                        value: JainSampraday.tapagaccha,
                        child: Text('તપા ગચ્છ (Tapagaccha - Shwetambar)'),
                      ),
                      DropdownMenuItem(
                        value: JainSampraday.kharatargaccha,
                        child: Text('ખરતર ગચ્છ (Kharatargaccha)'),
                      ),
                      DropdownMenuItem(
                        value: JainSampraday.sthanakvasi,
                        child: Text('સ્થાનકવાસી (Sthanakvasi)'),
                      ),
                      DropdownMenuItem(
                        value: JainSampraday.terapanth,
                        child: Text('તેરાપંથ (Terapanth)'),
                      ),
                      DropdownMenuItem(
                        value: JainSampraday.digambar,
                        child: Text('દિગંબર (Digambar - Das Lakshan)'),
                      ),
                    ],
                    onChanged: (val) {
                      if (val != null) {
                        ref.read(selectedSampradayProvider.notifier).setSampraday(val);
                      }
                    },
                  ),
                ],
              ),
            ),
          ),
          const SizedBox(height: 12),

          // Ayanamsha Selector
          Card(
            child: Padding(
              padding: const EdgeInsets.all(16),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  const Text('અયનાંશ (Ayanamsha)', style: TextStyle(fontWeight: FontWeight.bold, fontSize: 15)),
                  const SizedBox(height: 4),
                  const Text('નક્ષત્ર અને રાશિ ગણતરી પદ્ધતિ', style: TextStyle(fontSize: 12, color: Colors.grey)),
                  const SizedBox(height: 8),
                  DropdownButton<AyanamshaType>(
                    value: currentAyanamsha,
                    isExpanded: true,
                    items: const [
                      DropdownMenuItem(
                        value: AyanamshaType.lahiri,
                        child: Text('લાહિરી / ચિત્રાપક્ષ (Lahiri - Default)'),
                      ),
                      DropdownMenuItem(
                        value: AyanamshaType.raman,
                        child: Text('રામન (Raman)'),
                      ),
                      DropdownMenuItem(
                        value: AyanamshaType.krishnamurti,
                        child: Text('કૃષ્ણામૂર્તિ (KP)'),
                      ),
                      DropdownMenuItem(
                        value: AyanamshaType.faganBradley,
                        child: Text('ફાગન-બ્રેડલી (Fagan-Bradley)'),
                      ),
                    ],
                    onChanged: (val) {
                      if (val != null) {
                        ref.read(selectedAyanamshaProvider.notifier).setAyanamsha(val);
                      }
                    },
                  ),
                ],
              ),
            ),
          ),
          const SizedBox(height: 12),

          // Language Selector
          Card(
            child: Padding(
              padding: const EdgeInsets.all(16),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  const Text('ભાષા (Language)', style: TextStyle(fontWeight: FontWeight.bold, fontSize: 15)),
                  const SizedBox(height: 8),
                  DropdownButton<String>(
                    value: currentLanguage,
                    isExpanded: true,
                    items: const [
                      DropdownMenuItem(value: 'gu', child: Text('ગુજરાતી (Gujarati)')),
                      DropdownMenuItem(value: 'hi', child: Text('हिन्दी (Hindi)')),
                      DropdownMenuItem(value: 'en', child: Text('English')),
                    ],
                    onChanged: (val) {
                      if (val != null) {
                        ref.read(selectedLanguageProvider.notifier).setLanguage(val);
                      }
                    },
                  ),
                ],
              ),
            ),
          ),
          const SizedBox(height: 16),

          // App info
          const Center(
            child: Text(
              'જૈન પંચાંગ • ૧૦૦% ઓફલાઇન • ખગોળીય ગણતરી\nકોઈ જાહેરાત નહીં • કોઈ લોગિન નહીં',
              textAlign: TextAlign.center,
              style: TextStyle(fontSize: 12, color: Colors.grey),
            ),
          ),
        ],
      ),
    );
  }
}
