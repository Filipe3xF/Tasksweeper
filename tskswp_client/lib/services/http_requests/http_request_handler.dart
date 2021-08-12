import 'dart:convert';

import 'package:http/http.dart' as http;

const webHost = 'tasksweeper-web-api.herokuapp.com';

class HttpHandler {

  static Future<String> deleteRequest(
      String jwt, String path) async {
    return (await http.delete(Uri.https(webHost, path), headers: {
      'content-type': 'application/json',
      'Authorization': 'Bearer $jwt'
    }))
        .body;
  }

  static Future<String> patchRequest(
      String jwt, String path) async {
    return (await http.patch(Uri.https(webHost, path), headers: {
      'content-type': 'application/json',
      'Authorization': 'Bearer $jwt'
    }))
        .body;
  }

  static Future<String> postRequest(Object? body, String path) async {
    return (await http.post(
      Uri.https(webHost, path),
      headers: {'content-type': 'application/json'},
      body: jsonEncode(body),
    ))
        .body;
  }

  static Future<String> postRequestWithAuthenticationAndNoBody(
      String jwt, String path) async {
    return (await http.post(
      Uri.https(webHost, path),
      headers: {
        'content-type': 'application/json',
        'Authorization': 'Bearer $jwt'
      }
    ))
        .body;
  }

  static Future<String> postRequestWithAuthentication(
      Object? body, String jwt, String path) async {
    return (await http.post(
      Uri.https(webHost, path),
      headers: {
        'content-type': 'application/json',
        'Authorization': 'Bearer $jwt'
      },
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

  static Future<String> getRequestWithAuthentication(
      String jwt, String path) async {
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
