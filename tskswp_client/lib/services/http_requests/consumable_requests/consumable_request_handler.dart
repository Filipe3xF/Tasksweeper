import '../http_request_handler.dart';

class ConsumableHandler {

  static Future<String> getListOfConsumables(String jwt) {
    return HttpHandler.getRequestWithAuthentication(jwt, '/consumables');
  }

  static Future<String> getConsumableById(String jwt, int consumableId) {
    return HttpHandler.getRequestWithAuthentication(jwt, '/consumable/$consumableId');
  }

  static Future<String> buyConsumable(String jwt, int consumableId) {
    return HttpHandler.postRequestWithAuthenticationAndNoBody(jwt, '/consumable/$consumableId/buy');
  }
}