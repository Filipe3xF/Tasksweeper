import 'package:flutter/material.dart';
import 'package:tskswp_client/screens/login_screen.dart';
import 'package:tskswp_client/services/notifications/notification_service.dart';

Future<void> main() async {
  WidgetsFlutterBinding.ensureInitialized();
  await NotificationService().init();
  runApp(MyApp());
}

class MyApp extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'TaskSweeper',
      debugShowCheckedModeBanner: false,
      home: LoginScreen(),
    );
  }
}