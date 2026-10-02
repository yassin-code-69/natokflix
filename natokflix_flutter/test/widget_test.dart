import 'package:flutter_test/flutter_test.dart';
import 'package:provider/provider.dart';
import 'package:natokflix_flutter/main.dart';
import 'package:natokflix_flutter/providers/natok_provider.dart';

void main() {
  testWidgets('NatokFlix smoke test', (WidgetTester tester) async {
    await tester.pumpWidget(
      MultiProvider(
        providers: [
          ChangeNotifierProvider(create: (_) => NatokProvider()),
        ],
        child: const NatokFlixApp(),
      ),
    );

    // Initial frame shows SplashScreen
    expect(find.byType(NatokFlixApp), findsOneWidget);

    // Advance time past splash timer to settle
    await tester.pump(const Duration(seconds: 3));
    expect(find.byType(NatokFlixApp), findsOneWidget);
  });
}
