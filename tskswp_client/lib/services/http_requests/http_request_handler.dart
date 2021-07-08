import 'dart:convert';
import 'package:http/http.dart' as http;

const emulatorHost = '10.0.2.2:8080';
const webHost = 'localhost:8080';

class HttpHandler {


  Future<String> postRequest(Object? body, String path) async {
    return (await http.post(
      Uri.http(emulatorHost, path),
      headers: {'content-type': 'application/json'},
      body: jsonEncode(body),
    )).body;
  }

  Future<String> getRequestWithAuth(String path, String jwt) async {
    return (await http.get(
      Uri.http(emulatorHost, path),
      headers: {'content-type': 'application/json', 'Authorization': 'Bearer $jwt'},
    )).body;
  }

}
