import 'dart:convert';
import 'package:flutter/material.dart';
import 'package:tskswp_client/components/account_status_table.dart';
import 'package:tskswp_client/components/task_row.dart';
import 'package:tskswp_client/services/http_requests/account_requests/account_request_handler.dart';
import 'package:tskswp_client/services/http_requests/account_status_requests/account_status_request_handler.dart';
import 'package:tskswp_client/services/http_requests/task_requests/task_request_handler.dart';
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

  Status status = Status();
  var listOfTaskRow = <TaskRow>[];

  @override
  void initState() {
    super.initState();
    setStatusValues();
    fillTaskRowList();
  }

  Future<void> fillTaskRowList() async {
    List userOpenTasks =
        jsonDecode(await TaskHandler.getAccountTasks(jwt, {'state': 'open'}));

    userOpenTasks.forEach(
      (task) => listOfTaskRow.add(
        TaskRow(
            taskTitle: task['name'],
            onSuccess: onSuccessfulCompletion,
            onFail: onFailCompletion,
            onDelete: onDelete),
      ),
    );
  }

  Future<void> setStatusValues() async {
    var statusValues =
        jsonDecode(await AccountStatusHandler.getAccountStatus(jwt));
    var statusLevel =
        jsonDecode(await AccountHandler.getAccountDetails(jwt))['level'];
    //Uncomment the line below to see the response
    //print(response);
    setState(() {
      status.setNewLevel(statusLevel);
      status.setNewStatusValues(statusValues);
    });
  }

  void onSuccessfulCompletion() {
    print('Successful');
  }

  void onFailCompletion() {
    print('Fail');
  }

  void onDelete() {
    print('Delete');
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar:
          AppBar(backgroundColor: kAppBarColor, title: Center(child: kTitle)),
      body: SafeArea(
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            AccountStatusTable(status),
            Expanded(
              child: SingleChildScrollView(
                child: Column(
                  mainAxisSize: MainAxisSize.min,
                  children: listOfTaskRow,
                ),
              ),
            )
          ],
        ),
      ),
    );
  }
}
