import 'package:flutter/material.dart';
import 'package:percent_indicator/linear_percent_indicator.dart';

class AccountStatusTable extends StatelessWidget {
  AccountStatusTable(
      {//required this.level,
      required this.health,
      required this.maxHealth,
      required this.gold,
      required this.experience,
      required this.maxExperience});

  //final int level;
  final int health, maxHealth;
  final int gold;
  final int experience, maxExperience;

  @override
  Widget build(BuildContext context) {
    return Container(
      decoration: BoxDecoration(
          border: Border.all(color: Colors.black),
          color: Colors.deepPurpleAccent
      ),
      child: Column(
        mainAxisSize: MainAxisSize.min,
        children: [
          Row(children: [
            Padding(padding: EdgeInsets.only(top: 5),child: Text(' HP:', style: TextStyle(color: Colors.white))),
            Expanded(child: Container()),
            Padding(
              padding: EdgeInsets.only(top: 5),
              child: LinearPercentIndicator(
                lineHeight: 15.0,
                width: 300,
                percent: (health / maxHealth),
                backgroundColor: Colors.grey,
                progressColor: Colors.red,
                center:
                    Text((health / maxHealth * 100).toStringAsFixed(0) + '%'),
              ),
            ),
          ]),
          Row(
            mainAxisAlignment: MainAxisAlignment.start,
            children: [
              Padding(padding: EdgeInsets.only(top: 5),child: Text(' Experience:' , style: TextStyle(color: Colors.white))),
              Expanded(child: Container()),
              Padding(
                padding: EdgeInsets.only(top: 5),
                child: LinearPercentIndicator(
                  lineHeight: 15.0,
                  width: 300,
                  percent: (experience / maxExperience),
                  backgroundColor: Colors.grey,
                  progressColor: Colors.blue,
                  center: Text(
                      (experience / maxExperience * 100).toStringAsFixed(0) +
                          '%'),
                ),
              )
            ],
          ),
          Row(
            mainAxisAlignment: MainAxisAlignment.start,
            children: [
              Padding(padding: EdgeInsets.symmetric(vertical: 5), child: Text(' Gold:' , style: TextStyle(color: Colors.white))),
              Padding(padding: EdgeInsets.symmetric(vertical: 5), child: Text(' ${gold}G ', style: TextStyle(color: Colors.yellow)))
            ],
          ),
        ],
      ),
    );
  }
}
