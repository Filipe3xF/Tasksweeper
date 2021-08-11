import 'dart:convert';

import 'package:flutter/material.dart';
import 'package:tskswp_client/components/account_status_table.dart';
import 'package:tskswp_client/components/app_bottom_bar.dart';
import 'package:tskswp_client/components/consumable_slot.dart';
import 'package:tskswp_client/services/http_requests/account_requests/account_request_handler.dart';
import 'package:tskswp_client/services/http_requests/account_status_requests/account_status_request_handler.dart';
import 'package:tskswp_client/services/http_requests/consumable_requests/consumable_request_handler.dart';
import 'package:tskswp_client/services/status_of_the_account/Status.dart';

import '../constants.dart';
import 'home_page_screen.dart';

class ConsumableShopScreen extends StatefulWidget {
  ConsumableShopScreen({required this.jwt, required this.status});

  final String jwt;

  final Status status;

  @override
  _ConsumableShopScreen createState() => _ConsumableShopScreen(jwt: this.jwt, status: status);
}

class _ConsumableShopScreen extends State<ConsumableShopScreen> {
  _ConsumableShopScreen({required this.jwt,required this.status});

  final String jwt;

  final String taskId = 'id';
  final String taskName = 'name';
  final String accountLevel = 'level';

  final List<Widget> listOfConsumables = [];

  Status status;

  @override
  void initState() {
    super.initState();
    setStatusValues();
    buildConsumableRow();
  }

  void errorAlertWindow(String error){
    showDialog<String>(
        context: context,
        builder: (BuildContext context) => AlertDialog(
          title: const Text('Warning!'),
          content: Text('$error'),
          actions: <Widget>[
            TextButton(
              onPressed: () => Navigator.pop(context, 'OK'),
              child: const Text('OK'),
            ),
          ],
        ));
  }

  Future<void> buildConsumableRow() async {
    List consumables =
        jsonDecode(await ConsumableHandler.getListOfConsumables(jwt));

    consumables.forEach((element) {
      listOfConsumables.add(ConsumableSlot(
          onPressed: () async {
            Map response = jsonDecode(await ConsumableHandler.buyConsumable(jwt, element['id']));
            setState(() {
              if(response.containsKey('error')){
                errorAlertWindow(response['error']);
                return;
              }
              setStatusValues();
            });
          },
          consumableName: element['name'],
          consumablePrice: element['price']));
    });

  }

  Future<void> setStatusValues() async {
    var statusValues =
        jsonDecode(await AccountStatusHandler.getAccountStatus(jwt));
    var statusLevel =
        jsonDecode(await AccountHandler.getAccountDetails(jwt))[accountLevel];
    setState(() {
      status.setNewLevel(statusLevel);
      status.setNewStatusValues(statusValues);
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar:
          AppBar(backgroundColor: kAppBarColor, title: Center(child: kTitle)),
      bottomNavigationBar: BottomMenu(
        bottomAppBarOptions: [
          IconButton(
              icon: Icon(Icons.home),
              onPressed: () {
                Navigator.pushReplacement(
                    context,
                    MaterialPageRoute(
                        builder: (context) => HomeScreen(jwt: jwt, status: status,)));
              })
        ],
      ),
      body: SafeArea(
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            AccountStatusTable(status),
            Expanded(
              child: SingleChildScrollView(
                child: Column(
                  mainAxisSize: MainAxisSize.min,
                  children: [Row(children: listOfConsumables)],
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }
}
