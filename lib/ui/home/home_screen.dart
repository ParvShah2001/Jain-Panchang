import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:intl/intl.dart';
import '../../providers/panchang_providers.dart';
import '../../data/cities/city_database.dart';
import 'choghadiya_countdown_card.dart';
import 'sun_times_card.dart';
import 'panchang_grid_card.dart';
import 'pachkhan_overview_card.dart';

class HomeScreen extends ConsumerWidget {
  const HomeScreen({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final selectedDate = ref.watch(selectedDateProvider);
    final selectedCity = ref.watch(selectedLocationProvider);
    final panchang = ref.watch(dailyPanchangProvider);
    final activeChoghadiya = ref.watch(activeChoghadiyaProvider);
    final pachkhanList = ref.watch(pachkhanListProvider);
    final language = ref.watch(selectedLanguageProvider);

    final dateFmt = DateFormat('EEEE, d MMMM yyyy');

    final samvatLine = language == 'gu'
        ? 'વીર નિર્વાણ સંવત ${panchang.veerNirvanSamvat} • વિક્રમ સંવત ${panchang.vikramSamvat} • ${panchang.jainMonthGujarati}'
        : (language == 'hi'
            ? 'वीर निर्वाण संवत ${panchang.veerNirvanSamvat} • विक्रम संवत ${panchang.vikramSamvat} • ${panchang.jainMonthHindi}'
            : 'VNS ${panchang.veerNirvanSamvat} • VS ${panchang.vikramSamvat} • ${panchang.jainMonthEnglish}');

    return Scaffold(
      appBar: AppBar(
        title: Column(
          children: [
            Text(
              language == 'gu' ? 'જૈન પંચાંગ' : (language == 'hi' ? 'जैन पंचांग' : 'Jain Panchang'),
              style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 18),
            ),
            Text(
              selectedCity.cityName,
              style: TextStyle(fontSize: 12, color: Colors.grey.shade600),
            ),
          ],
        ),
        actions: [
          IconButton(
            icon: const Icon(Icons.location_on_outlined),
            onPressed: () => _showCityPicker(context, ref),
          ),
          IconButton(
            icon: const Icon(Icons.today),
            onPressed: () {
              final now = DateTime.now();
              ref.read(selectedDateProvider.notifier).setDate(DateTime(now.year, now.month, now.day));
            },
          ),
        ],
      ),
      body: ListView(
        padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 12),
        children: [
          // Date Selector Header
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              IconButton(
                icon: const Icon(Icons.chevron_left),
                onPressed: () {
                  ref.read(selectedDateProvider.notifier).setDate(
                      selectedDate.subtract(const Duration(days: 1)));
                },
              ),
              Expanded(
                child: Column(
                  children: [
                    Text(
                      dateFmt.format(selectedDate),
                      style: const TextStyle(fontSize: 15, fontWeight: FontWeight.bold),
                      textAlign: TextAlign.center,
                    ),
                    const SizedBox(height: 2),
                    Text(
                      samvatLine,
                      style: const TextStyle(fontSize: 12, color: Color(0xFFE65100), fontWeight: FontWeight.w600),
                      textAlign: TextAlign.center,
                    ),
                  ],
                ),
              ),
              IconButton(
                icon: const Icon(Icons.chevron_right),
                onPressed: () {
                  ref.read(selectedDateProvider.notifier).setDate(
                      selectedDate.add(const Duration(days: 1)));
                },
              ),
            ],
          ),
          const SizedBox(height: 12),

          // Live Choghadiya Card
          ChoghadiyaCountdownCard(
            activeChoghadiya: activeChoghadiya,
            language: language,
          ),
          const SizedBox(height: 12),

          // Sunrise / Sunset Card
          SunTimesCard(
            sunTimes: panchang.sunTimes,
            moonTimes: panchang.moonTimes,
            language: language,
          ),
          const SizedBox(height: 12),

          // Core Panchang Grid: Tithi, Nakshatra, Yoga, Karana
          PanchangGridCard(
            panchang: panchang,
            language: language,
          ),
          const SizedBox(height: 12),

          // Pachkhan Overview
          PachkhanOverviewCard(
            pachkhanList: pachkhanList,
            language: language,
          ),
          const SizedBox(height: 20),
        ],
      ),
    );
  }

  void _showCityPicker(BuildContext context, WidgetRef ref) {
    showModalBottomSheet(
      context: context,
      shape: const RoundedRectangleBorder(
        borderRadius: BorderRadius.vertical(top: Radius.circular(20)),
      ),
      builder: (ctx) {
        return SafeArea(
          child: Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              const Padding(
                padding: EdgeInsets.all(16),
                child: Text(
                  'Select City / સ્થાન પસંદ કરો',
                  style: TextStyle(fontWeight: FontWeight.bold, fontSize: 16),
                ),
              ),
              Expanded(
                child: ListView.builder(
                  itemCount: CityDatabase.popularCities.length,
                  itemBuilder: (ctx, i) {
                    final city = CityDatabase.popularCities[i];
                    return ListTile(
                      title: Text(city.cityName),
                      subtitle: Text('${city.latitude.toStringAsFixed(2)}° N, ${city.longitude.toStringAsFixed(2)}° E'),
                      onTap: () {
                        ref.read(selectedLocationProvider.notifier).setLocation(city);
                        Navigator.pop(ctx);
                      },
                    );
                  },
                ),
              ),
            ],
          ),
        );
      },
    );
  }
}
