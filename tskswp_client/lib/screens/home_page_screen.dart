import 'package:flutter/material.dart';
import 'package:tskswp_client/components/account_status_table.dart';

import '../constants.dart';

class HomeScreen extends StatefulWidget {
  HomeScreen({required this.jwt});

  final String jwt;

  @override
  _HomeScreen createState() => _HomeScreen(jwt: this.jwt);
}

class _HomeScreen extends State<HomeScreen> {
  _HomeScreen({required this.jwt});

  final String jwt;

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Center(child: kTitle)),
      body: SafeArea(
        child: AccountStatusTable(level: 0, health: 0, maxHealth: 0, gold: 0, experience: 0, maxExperience: 0)
      ),
    );
  }
}
