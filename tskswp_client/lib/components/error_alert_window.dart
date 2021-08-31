import 'package:flutter/material.dart';

class ErrorAlertWindow {

  static void showErrorWindow(BuildContext context, String error) {
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
}
