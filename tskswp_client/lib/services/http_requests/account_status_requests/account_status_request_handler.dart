import 'package:tskswp_client/services/http_requests/http_request_handler.dart';

class AccountStatusHandler {

  static Future<String> getAccountStatus(String jwt) {
    return HttpHandler.getRequest(jwt, '/accountStatus');
  }

}
