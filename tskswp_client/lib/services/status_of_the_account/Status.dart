import 'dart:collection';

class Status {
  Status.empty();

  Status(var body) {
    for (int i = 0; i < 3; ++i) {
      var stat = body[i];
      parameterValues[stat['statusName']] =
          CurrMax(current: stat['value'], max: stat['maxValue']);
    }
  }

  int getCurrentHealth() {
    return parameterValues['Health']!.current;
  }

  int getMaxHealth() {
    return parameterValues['Health']!.max;
  }

  int getCurrentGold() {
    return parameterValues['Gold']!.current;
  }

  int getCurrentExperience() {
    return parameterValues['Experience']!.current;
  }

  int getMaxExperience() {
    return parameterValues['Experience']!.max;
  }

  final Map<String, CurrMax> parameterValues = {
    'Health': CurrMax(current: 1, max: 1),
    'Gold': CurrMax(current: 0, max: 1),
    'Experience': CurrMax(current: 0, max: 1)
  };
}

class CurrMax {
  CurrMax({required this.current, required this.max});

  final int current;
  final int max;
}
