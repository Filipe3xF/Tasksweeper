import '../http_request_handler.dart';

class TaskHandler {

  static Future<String> getAccountTasks(String jwt, var query) {
    return HttpHandler.getRequestWithQuery(jwt, '/tasks', query);
  }

}