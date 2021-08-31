import 'dart:convert';

import 'package:flutter/material.dart';
import 'package:tskswp_client/components/account_status_table.dart';
import 'package:tskswp_client/components/app_bottom_bar.dart';
import 'package:tskswp_client/components/consumable_slot.dart';
import 'package:tskswp_client/components/error_alert_window.dart';
import 'package:tskswp_client/services/http_requests/consumable_requests/consumable_request_handler.dart';
import 'package:tskswp_client/services/status_of_the_account/Status.dart';

import '../constants.dart';
import 'home_page_screen.dart';
import 'inventory_screen.dart';

class ConsumableShopScreen extends StatefulWidget {
  ConsumableShopScreen({required this.jwt, required this.status});

  final String jwt;

  final Status status;

  @override
  _ConsumableShopScreen createState() =>
      _ConsumableShopScreen(jwt: this.jwt, status: status);
}

class _ConsumableShopScreen extends State<ConsumableShopScreen> {
  _ConsumableShopScreen({required this.jwt, required this.status});

  @override
  void initState() {
    super.initState();
    buildConsumableRow();
  }

  final String jwt;

  final String taskId = 'id';
  final String taskName = 'name';
  final String accountLevel = 'level';

  final List<Widget> listOfConsumables = [];

  Status status;

  void buildConsumableRow() async {
    List consumables =
        jsonDecode(await ConsumableHandler.getListOfConsumables(jwt));

    consumables.forEach((element) {
      listOfConsumables.add(ConsumableSlot(
          onPressed: () async {
            Map response = jsonDecode(
                await ConsumableHandler.buyConsumable(jwt, element['id']));

            if (response.containsKey('error')) {
              ErrorAlertWindow.showErrorWindow(context, response['error']);
              return;
            }
            await status.updateStatusValues();

            setState(() {});
          },
          consumableName: element['name'],
          consumablePriceOrQuantity: '${element['price']} G'));
    });
    setState(() {});
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
                        builder: (context) => HomeScreen(
                              jwt: jwt,
                              status: status,
                            )));
              }),
          IconButton(
              icon: Icon(Icons.backpack),
              onPressed: () {
                Navigator.pushReplacement(
                    context,
                    MaterialPageRoute(
                        builder: (context) => InventoryScreen(
                              jwt: jwt,
                              status: status,
                            )));
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
