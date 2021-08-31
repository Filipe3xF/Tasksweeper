import 'dart:convert';

import 'package:tskswp_client/services/http_requests/account_requests/account_request_handler.dart';
import 'package:tskswp_client/services/http_requests/account_status_requests/account_status_request_handler.dart';

class CurrMax {
  CurrMax({required this.current, required this.max});

  final int current;
  final int max;
}

class Status {

  Status(this.jwt){
    updateStatusValues();
  }

  final String jwt;

  final Map<String, CurrMax> parameterValues = {
    'Level': CurrMax(current: 1, max: 1),
    'Health': CurrMax(current: 1, max: 1),
    'Gold': CurrMax(current: 0, max: 1),
    'Experience': CurrMax(current: 0, max: 1)
  };

  Future<void> updateStatusValues() async {
    var statusValues =
        jsonDecode(await AccountStatusHandler.getAccountStatus(jwt));
    var statusLevel =
        jsonDecode(await AccountHandler.getAccountDetails(jwt))['level'];
    _setNewLevel(statusLevel);
    _setNewStatusValues(statusValues);
  }

  void _setNewLevel(int level) {
    parameterValues['Level'] = CurrMax(current: level, max: level);
  }

  void _setNewStatusValues(var body) {
    for (int i = 0; i < 3; ++i) {
      var stat = body[i];
      parameterValues[stat['statusName']] =
          CurrMax(current: stat['value'], max: stat['maxValue']);
    }
  }

  int getLevel() {
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
