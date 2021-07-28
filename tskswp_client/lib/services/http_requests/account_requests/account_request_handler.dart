import 'package:tskswp_client/services/http_requests/http_request_handler.dart';

class AccountHandler {

  static Future<String> login(String? username, String? password) async {
    return await HttpHandler.postRequest({'username': username, 'password': password}, '/login');
  }

  static Future<String> register(String? email, String? username, String? password) async {
    return await HttpHandler.postRequest({'email': email, 'username': username, 'password': password}, '/register');
  }

  static Future<String> getAccountDetails(String jwt) async {
    return await HttpHandler.getRequestWithAuthentication(jwt, '/account');
  }
}
