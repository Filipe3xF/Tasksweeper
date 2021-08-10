import 'package:flutter/material.dart';

class ConsumableSlot extends StatelessWidget {
  ConsumableSlot(
      {required this.onPressed,
      required this.consumableName,
      required this.consumablePrice});

  final String consumableName;
  final int consumablePrice;

  final onPressed;

  @override
  Widget build(BuildContext context) {
    return Padding(
        padding: EdgeInsets.all(5),
        child: Column(children: [
          IconButton(
            icon: Icon(Icons.emoji_food_beverage_sharp),
            onPressed: onPressed,
          ),
          Text(consumableName),
          Text('$consumablePrice G')
        ]));
  }
}
