import 'dart:convert';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

class NiyamScreen extends StatefulWidget {
  const NiyamScreen({super.key});

  @override
  State<NiyamScreen> createState() => _NiyamScreenState();
}

class _NiyamScreenState extends State<NiyamScreen> {
  List<Map<String, dynamic>> _chaudahNiyams = [];
  final Map<String, bool> _completedNiyams = {};
  bool _isLoading = true;

  @override
  void initState() {
    super.initState();
    _loadNiyams();
  }

  Future<void> _loadNiyams() async {
    try {
      final jsonStr = await rootBundle.loadString('assets/data/niyam/chaudah_niyam.json');
      final list = (jsonDecode(jsonStr) as List).cast<Map<String, dynamic>>();
      setState(() {
        _chaudahNiyams = list;
        _isLoading = false;
      });
    } catch (_) {
      setState(() => _isLoading = false);
    }
  }

  @override
  Widget build(BuildContext context) {
    if (_isLoading) {
      return const Scaffold(body: Center(child: CircularProgressIndicator()));
    }

    final completedCount = _completedNiyams.values.where((v) => v).length;
    final totalCount = _chaudahNiyams.length;

    return Scaffold(
      appBar: AppBar(
        title: const Text('ચૌદહ નિયમ અને નિત્ય નિયમ', style: TextStyle(fontWeight: FontWeight.bold, fontSize: 18)),
      ),
      body: ListView(
        padding: const EdgeInsets.all(16),
        children: [
          // Daily streak and progress card
          Container(
            padding: const EdgeInsets.all(16),
            decoration: BoxDecoration(
              gradient: const LinearGradient(
                colors: [Color(0xFFE65100), Color(0xFFF57C00)],
                begin: Alignment.topLeft,
                end: Alignment.bottomRight,
              ),
              borderRadius: BorderRadius.circular(16),
            ),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                const Row(
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: [
                    Text(
                      'આજનો સંકલ્પ (Daily Niyam)',
                      style: TextStyle(color: Colors.white, fontWeight: FontWeight.bold, fontSize: 16),
                    ),
                    Icon(Icons.local_fire_department, color: Colors.amberAccent, size: 26),
                  ],
                ),
                const SizedBox(height: 8),
                Text(
                  'પૂર્ણ થયેલ: $completedCount / $totalCount નિયમ',
                  style: const TextStyle(color: Colors.white70, fontSize: 13),
                ),
                const SizedBox(height: 8),
                ClipRRect(
                  borderRadius: BorderRadius.circular(8),
                  child: LinearProgressIndicator(
                    value: totalCount > 0 ? (completedCount / totalCount) : 0,
                    backgroundColor: Colors.white24,
                    valueColor: const AlwaysStoppedAnimation<Color>(Colors.white),
                    minHeight: 6,
                  ),
                ),
              ],
            ),
          ),
          const SizedBox(height: 16),

          const Text(
            'ચૌદહ નિયમ (14 Sacred Daily Vows)',
            style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold),
          ),
          const SizedBox(height: 8),

          ..._chaudahNiyams.map((niyam) {
            final id = niyam['id'] as String;
            final isDone = _completedNiyams[id] ?? false;
            final num = niyam['number'] as int;

            return Card(
              margin: const EdgeInsets.symmetric(vertical: 4),
              child: CheckboxListTile(
                value: isDone,
                onChanged: (val) {
                  setState(() {
                    _completedNiyams[id] = val ?? false;
                  });
                },
                activeColor: const Color(0xFF2E7D32),
                title: Text(
                  '$num. ${niyam["nameGujarati"]}',
                  style: TextStyle(
                    fontWeight: FontWeight.bold,
                    decoration: isDone ? TextDecoration.lineThrough : null,
                  ),
                ),
                subtitle: Text(niyam['description'] as String, style: const TextStyle(fontSize: 12)),
              ),
            );
          }),
        ],
      ),
    );
  }
}
