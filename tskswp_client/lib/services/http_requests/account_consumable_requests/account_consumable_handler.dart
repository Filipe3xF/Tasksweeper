import '../http_request_handler.dart';

class AccountConsumableHandler {

  static Future<String> getAllAccountConsumables(String jwt) {
    return HttpHandler.getRequestWithAuthentication(jwt, 'account/consumables');
  }

  static Future<String> useAccountConsumable(String jwt, int consumableId) {
    return HttpHandler.postRequestWithAuthenticationAndNoBody(jwt, '/account/consumable/$consumableId');
  }

}