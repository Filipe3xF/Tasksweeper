import '../http_request_handler.dart';

class TaskHandler {

  static Future<String> getAccountTasks(String jwt, var query) {
    return HttpHandler.getRequestWithQuery(jwt, '/tasks', query);
  }

  static Future<String> createNewTask(String jwt, var body) {
    return HttpHandler.postRequestWithAuthentication(body, jwt, '/task');
  }

}