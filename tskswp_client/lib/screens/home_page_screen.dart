import 'dart:convert';
import 'package:flutter/material.dart';
import 'package:tskswp_client/components/account_status_table.dart';
import 'package:tskswp_client/services/http_requests/account_status_requests/account_status_request_handler.dart';
import 'package:tskswp_client/services/status_of_the_account/Status.dart';

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
  Status status = Status.empty();

  Future<void> setStatusValues() async {
    var response = jsonDecode(await AccountStatusHandler.getAccountStatus(jwt));
    print(response);
    setState(() {
      status = Status(response);
    });
  }

  @override
  void initState() {
    super.initState();
    setStatusValues();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Center(child: kTitle)),
      body: SafeArea(
        child: AccountStatusTable(
          health: status.getCurrentHealth(),
          maxHealth: status.getMaxHealth(),
          gold: status.getCurrentGold(),
          experience: status.getCurrentExperience(),
          maxExperience: status.getMaxExperience(),
        ),
      ),
    );
  }
}
