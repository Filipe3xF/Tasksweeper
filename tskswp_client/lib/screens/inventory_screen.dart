import 'dart:convert';

import 'package:flutter/material.dart';
import 'package:tskswp_client/components/account_status_table.dart';
import 'package:tskswp_client/components/app_bottom_bar.dart';
import 'package:tskswp_client/components/consumable_slot.dart';
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
  _InventoryScreen createState() => _InventoryScreen(jwt: this.jwt, status: status);
}

class _InventoryScreen extends State<InventoryScreen> {
  _InventoryScreen({required this.jwt,required this.status});

  final String jwt;

  final String taskId = 'id';
  final String taskName = 'name';
  final String accountLevel = 'level';

  final List<ConsumableSlot> listOfAccountConsumables = [];

  Status status = Status();

  @override
  void initState() {
    super.initState();
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
    List accountConsumables =
    jsonDecode(await AccountConsumableHandler.getAllAccountConsumables(jwt));
    print(accountConsumables);

    accountConsumables.forEach((element) async {
      print(element);
      dynamic consumable = jsonDecode(await ConsumableHandler.getConsumableById(jwt, element['consumableId']));
      listOfAccountConsumables.add(ConsumableSlot(
          onPressed: () async {
            Map response = jsonDecode(await AccountConsumableHandler.useAccountConsumable(jwt, consumable['id']));
            setState(() {
              if(response.containsKey('error')){
                errorAlertWindow(response['error']);
                return;
              }
              status.updateStatusValues(jwt);
              listOfAccountConsumables.removeWhere((consumableSlot) => consumableSlot.consumableName == consumable['name']);
            });
          },
          consumableName: consumable['name'],
          consumablePriceOrQuantity: 'X ${element['quantity']}'));
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
