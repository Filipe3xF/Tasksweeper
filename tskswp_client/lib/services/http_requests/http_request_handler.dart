import 'dart:convert';

import 'package:http/http.dart' as http;

const webHost = 'tasksweeper-web-api.herokuapp.com';

class HttpHandler {
  static Future<String> postRequest(Object? body, String path) async {
    return (await http.post(
      Uri.https(webHost, path),
      headers: {'content-type': 'application/json'},
      body: jsonEncode(body),
    ))
        .body;
  }

  static Future<String> getRequestWithQuery(
      String jwt, String path, var query) async {
    return (await http.get(
      Uri.https(webHost, path, query),
      headers: {
        'content-type': 'application/json',
        'Authorization': 'Bearer $jwt'
      },
    ))
        .body;
  }

  static Future<String> getRequest(String jwt, String path) async {
    return (await http.get(
      Uri.https(webHost, path),
      headers: {
        'content-type': 'application/json',
        'Authorization': 'Bearer $jwt'
      },
    ))
        .body;
  }
}
