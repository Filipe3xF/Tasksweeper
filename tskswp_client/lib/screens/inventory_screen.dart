import 'dart:convert';

import 'package:flutter/material.dart';
import 'package:tskswp_client/components/account_status_table.dart';
import 'package:tskswp_client/components/app_bottom_bar.dart';
import 'package:tskswp_client/components/consumable_slot.dart';
import 'package:tskswp_client/components/error_alert_window.dart';
import 'package:tskswp_client/services/http_requests/account_consumable_requests/account_consumable_handler.dart';
import 'package:tskswp_client/services/http_requests/account_requests/account_request_handler.dart';
import 'package:tskswp_client/services/http_requests/account_status_requests/account_status_request_handler.dart';
import 'package:tskswp_client/services/http_requests/consumable_requests/consumable_request_handler.dart';
import 'package:tskswp_client/services/status_of_the_account/Status.dart';

import '../constants.dart';
import 'consumable_shop_screen.dart';
import 'home_page_screen.dart';

class InventoryScreen extends StatefulWidget {
  InventoryScreen({required this.jwt, required this.status});

  final String jwt;

  final Status status;

  @override
  _InventoryScreen createState() =>
      _InventoryScreen(jwt: this.jwt, status: status);
}

class _InventoryScreen extends State<InventoryScreen> {
  _InventoryScreen({required this.jwt, required this.status});

  final String jwt;

  final String taskId = 'id';
  final String taskName = 'name';
  final String accountLevel = 'level';

  List<ConsumableSlot> listOfAccountConsumables = [];

  Status status;

  @override
  void initState() {
    super.initState();
    buildConsumableRow();
  }

  Future<void> buildConsumableRow() async {
    List accountConsumables = jsonDecode(
        await AccountConsumableHandler.getAllAccountConsumables(jwt));

    accountConsumables.forEach((element) {
      ConsumableHandler.getConsumableById(jwt, element['consumableId']).then(
          (consumable) =>
              {buildConsumableSlot(element, jsonDecode(consumable))});
    });
  }

  void buildConsumableSlot(dynamic element, dynamic consumable) {
    ConsumableSlot consumableSlot = ConsumableSlot(onPressed: () {});

    consumableSlot.consumableName = consumable['name'];
    consumableSlot.consumablePriceOrQuantityValue = element['quantity'];
    consumableSlot.consumablePriceOrQuantityDisplayMessage =
        'X ${element['quantity']}';

    listOfAccountConsumables.add(consumableSlot);

    consumableSlot.onPressed = () async {
      Map response = jsonDecode(
          await AccountConsumableHandler.useAccountConsumable(
              jwt, consumable['id']));

      if (response.containsKey('error')) {
        ErrorAlertWindow.showErrorWindow(context, response['error']);
        return;
      }

      await status.updateStatusValues();

      ConsumableSlot thisConsumableSlot = listOfAccountConsumables
          .where((consumableSlot) =>
              consumableSlot.consumableName == consumable['name'])
          .single;

      listOfAccountConsumables.removeWhere((consumableSlot) =>
          consumableSlot.consumableName == consumable['name']);

      if (thisConsumableSlot.consumablePriceOrQuantityValue - 1 > 0)
        listOfAccountConsumables.add(
          ConsumableSlot(
            onPressed: thisConsumableSlot.onPressed,
            consumableName: thisConsumableSlot.consumableName,
            consumablePriceOrQuantityDisplayMessage:
                'X ${thisConsumableSlot.consumablePriceOrQuantityValue - 1}',
            consumablePriceOrQuantityValue:
                thisConsumableSlot.consumablePriceOrQuantityValue - 1,
          ),
        );

      setState(() {});
    };

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
              icon: Icon(Icons.shopping_bag),
              onPressed: () {
                Navigator.pushReplacement(
                    context,
                    MaterialPageRoute(
                        builder: (context) => ConsumableShopScreen(
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
                  children: [Row(children: listOfAccountConsumables)],
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }
}
