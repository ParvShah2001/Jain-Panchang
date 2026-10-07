import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:table_calendar/table_calendar.dart';
import 'package:panchang_engine/panchang_engine.dart';
import '../../providers/panchang_providers.dart';

class CalendarScreen extends ConsumerStatefulWidget {
  const CalendarScreen({super.key});

  @override
  ConsumerState<CalendarScreen> createState() => _CalendarScreenState();
}

class _CalendarScreenState extends ConsumerState<CalendarScreen> {
  CalendarFormat _calendarFormat = CalendarFormat.month;
  late DateTime _focusedDay;

  @override
  void initState() {
    super.initState();
    _focusedDay = ref.read(selectedDateProvider);
  }

  @override
  Widget build(BuildContext context) {
    final selectedDate = ref.watch(selectedDateProvider);
    final location = ref.watch(selectedLocationProvider);
    final sampraday = ref.watch(selectedSampradayProvider);
    final ayanamsha = ref.watch(selectedAyanamshaProvider);
    final language = ref.watch(selectedLanguageProvider);

    // Support 100 years of calendar browsing (1970 to 2070)
    final firstDay = DateTime.utc(1970, 1, 1);
    final lastDay = DateTime.utc(2070, 12, 31);

    return Scaffold(
      appBar: AppBar(
        title: Text(
          language == 'gu' ? 'કૅલેન્ડર (૧૦૦ વર્ષ)' : (language == 'hi' ? 'कैलेंडर (१०० वर्ष)' : 'Calendar (100 Years)'),
          style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 18),
        ),
      ),
      body: Column(
        children: [
          TableCalendar(
            firstDay: firstDay,
            lastDay: lastDay,
            focusedDay: _focusedDay,
            calendarFormat: _calendarFormat,
            selectedDayPredicate: (day) => isSameDay(selectedDate, day),
            onDaySelected: (selectedDay, focusedDay) {
              setState(() {
                _focusedDay = focusedDay;
              });
              ref.read(selectedDateProvider.notifier).setDate(DateTime(
                selectedDay.year,
                selectedDay.month,
                selectedDay.day,
              ));
            },
            onFormatChanged: (format) {
              setState(() {
                _calendarFormat = format;
              });
            },
            onPageChanged: (focusedDay) {
              _focusedDay = focusedDay;
            },
            calendarBuilders: CalendarBuilders(
              defaultBuilder: (context, day, focusedDay) {
                return _buildCalendarCell(day, location, sampraday, ayanamsha, language, isSelected: false);
              },
              selectedBuilder: (context, day, focusedDay) {
                return _buildCalendarCell(day, location, sampraday, ayanamsha, language, isSelected: true);
              },
              todayBuilder: (context, day, focusedDay) {
                return _buildCalendarCell(day, location, sampraday, ayanamsha, language, isToday: true);
              },
            ),
          ),
          const Divider(height: 1),
          Expanded(
            child: _buildDayDetails(context, selectedDate, location, sampraday, ayanamsha, language),
          ),
        ],
      ),
    );
  }

  Widget _buildCalendarCell(
    DateTime day,
    GeoLocation location,
    JainSampraday sampraday,
    AyanamshaType ayanamsha,
    String language, {
    bool isSelected = false,
    bool isToday = false,
  }) {
    // Quick Panchang computation for the cell date
    final panchang = JainPanchangService.calculatePanchang(
      day,
      location,
      sampraday: sampraday,
      ayanamsha: ayanamsha,
    );

    final tithiNum = panchang.udayaTithi.tithiNumber;
    final isShukla = panchang.udayaTithi.paksha == Paksha.shukla;
    final tithiShort = isShukla ? 'સુ $tithiNum' : 'વ $tithiNum';

    final isSpecialTithi = tithiNum == 8 || tithiNum == 14 || tithiNum == 15;

    return Container(
      margin: const EdgeInsets.all(2),
      decoration: BoxDecoration(
        color: isSelected
            ? const Color(0xFFE65100).withValues(alpha: 0.15)
            : (isToday ? Colors.amber.withValues(alpha: 0.1) : null),
        borderRadius: BorderRadius.circular(8),
        border: isSelected
            ? Border.all(color: const Color(0xFFE65100), width: 1.5)
            : (isToday ? Border.all(color: Colors.amber.shade700) : null),
      ),
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          Text(
            '${day.day}',
            style: TextStyle(
              fontWeight: FontWeight.bold,
              fontSize: 14,
              color: isSpecialTithi ? const Color(0xFFC62828) : null,
            ),
          ),
          Text(
            tithiShort,
            style: TextStyle(
              fontSize: 9,
              fontWeight: isSpecialTithi ? FontWeight.bold : FontWeight.normal,
              color: isSpecialTithi ? const Color(0xFFC62828) : Colors.grey.shade700,
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildDayDetails(
    BuildContext context,
    DateTime date,
    GeoLocation location,
    JainSampraday sampraday,
    AyanamshaType ayanamsha,
    String language,
  ) {
    final panchang = JainPanchangService.calculatePanchang(
      date,
      location,
      sampraday: sampraday,
      ayanamsha: ayanamsha,
    );

    return ListView(
      padding: const EdgeInsets.all(16),
      children: [
        Card(
          child: Padding(
            padding: const EdgeInsets.all(16),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  '${panchang.jainMonthGujarati} • ${panchang.udayaTithi.nameGujarati}',
                  style: const TextStyle(fontSize: 18, fontWeight: FontWeight.bold, color: Color(0xFFE65100)),
                ),
                const SizedBox(height: 8),
                Text('નક્ષત્ર: ${panchang.nakshatra.nameGujarati} • યોગ: ${panchang.yoga.nameEnglish} • વાર: ${panchang.varaGujarati}'),
                const SizedBox(height: 8),
                Text('સૂર્યોદય: ${panchang.sunTimes.sunrise.hour.toString().padLeft(2, '0')}:${panchang.sunTimes.sunrise.minute.toString().padLeft(2, '0')} | સૂર્યાસ્ત: ${panchang.sunTimes.sunset.hour.toString().padLeft(2, '0')}:${panchang.sunTimes.sunset.minute.toString().padLeft(2, '0')}'),
              ],
            ),
          ),
        ),
      ],
    );
  }
}
