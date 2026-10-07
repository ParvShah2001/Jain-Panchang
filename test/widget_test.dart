import 'package:flutter_test/flutter_test.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:jain_panchang/main.dart';

void main() {
  testWidgets('JainPanchangApp smoke test loads main navigation', (WidgetTester tester) async {
    await tester.pumpWidget(const ProviderScope(child: JainPanchangApp()));
    await tester.pumpAndSettle();

    // Verify app starts and shows today's Panchang screen
    expect(find.text('જૈન પંચાંગ'), findsOneWidget);
    expect(find.text('Palitana'), findsOneWidget);
  });
}
