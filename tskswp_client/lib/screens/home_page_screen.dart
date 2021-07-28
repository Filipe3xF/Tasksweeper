import 'dart:convert';

import 'package:flutter/material.dart';
import 'package:tskswp_client/components/account_status_table.dart';
import 'package:tskswp_client/components/app_bottom_bar.dart';
import 'package:tskswp_client/components/task_row.dart';
import 'package:tskswp_client/screens/task_creation_screen.dart';
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

  final String taskName = 'name';
  final String accountLevel = 'level';

  Status status = Status();
  final listOfTaskRow = <TaskRow>[];
  final Map<String, dynamic> openTaskStateQuery = {'state': 'open'};

  @override
  void initState() {
    super.initState();
    setStatusValues();
    fillTaskRowList();
  }

  Future<void> fillTaskRowList() async {
    List userOpenTasks =
        jsonDecode(await TaskHandler.getAccountTasks(jwt, openTaskStateQuery));
    setState(() {
      userOpenTasks.forEach(
            (task) => listOfTaskRow.add(
          TaskRow(
              taskTitle: task[taskName],
              onSuccess: onSuccessfulCompletion,
              onFail: onFailCompletion,
              onDelete: onDelete),
        ),
      );
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
      bottomNavigationBar: BottomMenu(),
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
            ),
          ],
        ),
      ),
      floatingActionButton: FloatingActionButton(
          onPressed: () {
            Navigator.push(
                    context,
                    MaterialPageRoute(
                        builder: (context) => TaskCreationScreen(jwt: jwt)))
                .then((value) {
              listOfTaskRow.removeRange(0, listOfTaskRow.length);
              fillTaskRowList();
            });
          },
          child: const Icon(Icons.add),
          tooltip: 'Create a new task'),
      floatingActionButtonLocation: FloatingActionButtonLocation.centerDocked,
    );
  }
}
