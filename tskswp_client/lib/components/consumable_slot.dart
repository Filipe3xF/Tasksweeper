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
        child: Container(
            decoration: BoxDecoration(
                color: Color(0xFF294077),
                borderRadius: BorderRadius.all(Radius.circular(20))),
          child: Column(children: [
            IconButton(
              color: Colors.orange,
              icon: Icon(Icons.emoji_food_beverage_sharp),
              onPressed: onPressed,
            ),
            Padding(padding: EdgeInsets.only(left: 5, right: 5), child: Text(consumableName, style: TextStyle(color: Colors.white),)),
            Padding(padding: EdgeInsets.all(5),child: Text('$consumablePrice G', style: TextStyle(color: Colors.white)))
          ]),
        ));
  }
}
