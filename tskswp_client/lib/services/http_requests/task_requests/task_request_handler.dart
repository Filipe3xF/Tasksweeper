import '../http_request_handler.dart';

class TaskHandler {

  static Future<String> closeTaskUnsuccessfully(String jwt, int taskId) {
    return HttpHandler.patchRequest(jwt, '/task/$taskId/failure');
  }

  static Future<String> closeTaskSuccessfully(String jwt, int taskId) {
    return HttpHandler.patchRequest(jwt, '/task/$taskId/success');
  }

  static Future<String> getAccountTasks(String jwt, var query) {
    return HttpHandler.getRequestWithQuery(jwt, '/tasks', query);
  }

  static Future<String> createNewTask(String jwt, var body) {
    return HttpHandler.postRequestWithAuthentication(body, jwt, '/task');
  }

}