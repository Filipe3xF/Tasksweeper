import 'package:flutter/material.dart';

class ConsumableSlot extends StatelessWidget {
  ConsumableSlot(
      {required this.onPressed,
      this.consumableName = 'Loading',
      this.consumablePriceOrQuantityValue = 0,
      this.consumablePriceOrQuantityDisplayMessage = 'Loading'});

  String consumableName;
  int consumablePriceOrQuantityValue;
  String consumablePriceOrQuantityDisplayMessage;

  Function() onPressed;

  bool isLoading() {
    return consumableName == 'Loading' ||
        consumablePriceOrQuantityDisplayMessage == 'Loading';
  }

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
            Padding(
                padding: EdgeInsets.only(left: 5, right: 5),
                child: Text(
                  consumableName,
                  style: TextStyle(color: Colors.white),
                )),
            Padding(
                padding: EdgeInsets.all(5),
                child: Text(consumablePriceOrQuantityDisplayMessage,
                    style: TextStyle(color: Colors.white)))
          ]),
        ));
  }
}
