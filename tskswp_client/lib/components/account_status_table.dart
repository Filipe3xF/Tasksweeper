import 'package:flutter/material.dart';

class AccountStatusTable extends StatelessWidget {
  AccountStatusTable(
      {required this.level,
      required this.health,
      required this.maxHealth,
      required this.gold,
      required this.experience,
      required this.maxExperience});

  final int? level;
  final int? health, maxHealth;
  final int? gold;
  final int? experience, maxExperience;

  @override
  Widget build(BuildContext context) {
    return Row(
      mainAxisSize: MainAxisSize.min,
      children: [
        Column(
          children: [
            Container(
              child: Text('HP/MaxHP: $health/$maxHealth'),
            ),
            Container(
              child: Text('Gold: $gold'),
            ),
            Container(
              child: Text('Experience/MaxExperience: $experience/$maxExperience'),
            )
          ],
        )
      ],
    );
  }
}
