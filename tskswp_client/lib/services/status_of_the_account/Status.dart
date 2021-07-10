import 'dart:collection';

class CurrMax {
  CurrMax({required this.current, required this.max});

  final int current;
  final int max;
}

class Status {

  final Map<String, CurrMax> parameterValues = {
    'Level': CurrMax(current: 1, max: 1),
    'Health': CurrMax(current: 1, max: 1),
    'Gold': CurrMax(current: 0, max: 1),
    'Experience': CurrMax(current: 0, max: 1)
  };

  void setNewLevel(int level){
    parameterValues['Level'] = CurrMax(current: level, max: level);
  }

  void setNewStatusValues (var body) {
    for (int i = 0; i < 3; ++i) {
      var stat = body[i];
      parameterValues[stat['statusName']] =
          CurrMax(current: stat['value'], max: stat['maxValue']);
    }
  }

  int getLevel(){
    return parameterValues['Level']!.current;
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
}
