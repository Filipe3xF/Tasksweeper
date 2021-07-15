import 'package:flutter/material.dart';
import 'package:percent_indicator/linear_percent_indicator.dart';
import 'package:tskswp_client/services/status_of_the_account/Status.dart';

class AccountStatusTable extends StatelessWidget {


  AccountStatusTable(Status status){
    setNewStatus(status);
  }

  void setNewStatus(Status status){
    level = status.getLevel();
    health = status.getCurrentHealth();
    maxHealth = status.getMaxHealth();
    gold = status.getCurrentGold();
    experience = status.getCurrentExperience();
    maxExperience = status.getMaxExperience();
  }

  int level = 1;
  int health = 1, maxHealth = 1;
  int gold = 0;
  int experience = 0, maxExperience = 1;

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
          Row(
            mainAxisAlignment: MainAxisAlignment.start,
            children: [
              Padding(padding: EdgeInsets.only(top: 5), child: Text(' Lvl $level' , style: TextStyle(color: Colors.orange))),
            ],
          ),
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
