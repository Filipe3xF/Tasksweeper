import 'package:flutter/material.dart';

class TaskRow extends StatelessWidget {
  TaskRow(
      {required this.taskTitle,
      required this.onSuccess,
      required this.onFail,
      required this.onDelete});

  final String taskTitle;
  final Function()? onSuccess;
  final Function()? onFail;
  final Function()? onDelete;

  @override
  Widget build(BuildContext context) {
    return Container(
      decoration: BoxDecoration(
          border: Border.all(color: Colors.black), color: Color(0xFF294077)),
      child: Row(
        children: [
          Text(
            taskTitle,
            style: TextStyle(color: Colors.white),
          ),
          Expanded(child: Container()),
          Container(
            decoration: BoxDecoration(color: Colors.green),
            child: IconButton(
              icon: const Icon(Icons.done),
              onPressed: onSuccess,
            ),
          ),
          Container(
            decoration: BoxDecoration(color: Colors.red),
            child: IconButton(
              icon: const Icon(Icons.close),
              onPressed: onFail,
            ),
          ),
          Container(
            decoration: BoxDecoration(color: Colors.blueGrey),
            child: IconButton(
              icon: const Icon(Icons.delete_outline_outlined),
              onPressed: onDelete,
            ),
          )
        ],
      ),
    );
  }
}
