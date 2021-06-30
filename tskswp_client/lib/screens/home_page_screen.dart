


import 'package:flutter/material.dart';

class HomeScreen extends StatefulWidget {

  HomeScreen({required this.jwt});

  final String jwt;

  @override
  _HomeScreen createState() => _HomeScreen(jwt: this.jwt);

}

class _HomeScreen extends State<HomeScreen> {

  _HomeScreen({required this.jwt});

  final String jwt;

  Future<String?>? _toRegisterPage(String name) async {
    return 'something';
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: SafeArea(
        child: Column(
          children: [
            Text(jwt)
          ],
        ),
      ),
    );
  }
}