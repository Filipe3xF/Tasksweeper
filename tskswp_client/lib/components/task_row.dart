import 'dart:convert';
import 'dart:ffi';

import 'package:flutter/material.dart';
import 'package:tskswp_client/services/http_requests/task_requests/task_request_handler.dart';

class TaskRow extends StatelessWidget {
  TaskRow(
      {required this.taskId,
      required this.taskTitle,
      required this.jwt,
      required this.afterRequest,
      });

  final int taskId;
  final String taskTitle;
  final String jwt;
  final Function(String?) afterRequest;


  void _processResult(dynamic response) {
    if(response.contains('error')){
      afterRequest(jsonDecode(response)['error']);
      return;
    }
    afterRequest(null);
  }

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: EdgeInsets.all(5),
      child: Container(
        decoration: BoxDecoration(
            border: Border.all(color: Colors.black),
            color: Color(0xFF294077),
            borderRadius: BorderRadius.all(Radius.circular(20))),
        child: Row(
          children: [
            Container(
              decoration: BoxDecoration(
                  color: Color(0xFF294077),
                  borderRadius: BorderRadius.only(
                      topLeft: Radius.circular(25),
                      bottomLeft: Radius.circular(25))),
              child: IconButton(
                icon: Text(
                  '$taskId',
                  style: TextStyle(color: Colors.white),
                ),
                onPressed: () {},
              ),
            ),
            Padding(
              padding: EdgeInsets.all(5.0),
              child: Text(
                taskTitle,
                style: TextStyle(color: Colors.white),
              ),
            ),
            Expanded(child: Container()),
            Container(
              decoration: BoxDecoration(color: Colors.green),
              child: IconButton(
                icon: const Icon(Icons.done),
                onPressed: () async {
                  var response = await TaskHandler.closeTaskSuccessfully(jwt, taskId);
                  _processResult(response);
                },
              ),
            ),
            Container(
              decoration: BoxDecoration(color: Colors.red),
              child: IconButton(
                icon: const Icon(Icons.close),
                onPressed: () async {
                  var response = await TaskHandler.closeTaskUnsuccessfully(jwt, taskId);
                  _processResult(response);
                },
              ),
            ),
            Container(
              decoration: BoxDecoration(
                  color: Colors.blueGrey,
                  borderRadius: BorderRadius.only(
                      topRight: Radius.circular(20),
                      bottomRight: Radius.circular(20))),
              child: IconButton(
                icon: const Icon(Icons.delete_outline_outlined),
                onPressed: (){afterRequest(null);},
              ),
            )
          ],
        ),
      ),
    );
  }
}
