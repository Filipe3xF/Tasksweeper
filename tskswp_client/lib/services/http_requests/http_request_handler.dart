import 'dart:convert';
import 'package:http/http.dart' as http;

const emulatorHost = '10.0.2.2:8080';
const webHost = 'localhost:8080';

class HttpHandler {


  Future<String> postRequest(Object? body, String path) async {
    return (await http.post(
      Uri.http(webHost, path),
      headers: {'content-type': 'application/json'},
      body: jsonEncode(body),
    )).body;
  }

}
