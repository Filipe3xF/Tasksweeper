import 'dart:convert';
import 'package:http/http.dart' as http;

const emulatorHost = '10.0.2.2:8080';
const webHost = 'localhost:8080';

class HttpHandler {


  static Future<String> postRequest(Object? body, String path) async {
    return (await http.post(
      Uri.http(emulatorHost, path),
      headers: {'content-type': 'application/json'},
      body: jsonEncode(body),
    )).body;
  }

  static Future<String> getRequestWithQuery(String jwt, String path, var query) async {
    return (await http.get(
      Uri.http(emulatorHost, path, query),
      headers: {'content-type': 'application/json', 'Authorization': 'Bearer $jwt'},
    )).body;
  }

  static Future<String> getRequest(String jwt, String path) async {
    return (await http.get(
      Uri.http(emulatorHost, path),
      headers: {'content-type': 'application/json', 'Authorization': 'Bearer $jwt'},
    )).body;
  }

}
