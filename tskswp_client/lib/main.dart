import 'package:flutter/material.dart';
import 'package:tskswp_client/Screens/LoginScreen.dart';

void main() {
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