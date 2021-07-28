

import 'package:flutter/material.dart';

class BottomMenu extends StatelessWidget {

  @override
  Widget build(BuildContext context) {
    return BottomAppBar(
      shape: const CircularNotchedRectangle(),
      color: Colors.blue,
      child: IconTheme(
        data: IconThemeData(color: Theme.of(context).colorScheme.onPrimary),
        child: Row(
          children: <Widget>[
            IconButton(icon: Icon(Icons.menu), onPressed: () {})
          ],
        ),
      )
    );
  }
}
