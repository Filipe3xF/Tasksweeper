import 'package:flutter_local_notifications/flutter_local_notifications.dart';
import 'package:timezone/data/latest.dart' as tz;
import 'package:timezone/timezone.dart' as tz;

class NotificationService {
  final FlutterLocalNotificationsPlugin flutterLocalNotificationsPlugin =
      FlutterLocalNotificationsPlugin();

  static final NotificationService _notificationService =
      NotificationService._internal();

  Future<void> init() async {
    final AndroidInitializationSettings initializationSettingsAndroid =
        AndroidInitializationSettings('among_us_player_white_512');

    final IOSInitializationSettings initializationSettingsIOS =
        IOSInitializationSettings(
      requestSoundPermission: false,
      requestBadgePermission: false,
      requestAlertPermission: false,
      //define this later for older IOS versions
      onDidReceiveLocalNotification:
          (int id, String? title, String? body, String? payload) async {
        return 0;
      },
    );

    final InitializationSettings initializationSettings =
        InitializationSettings(
            android: initializationSettingsAndroid,
            iOS: initializationSettingsIOS,
            macOS: null);

    tz.initializeTimeZones();

    await flutterLocalNotificationsPlugin.initialize(initializationSettings);
  }

  void createScheduledAndroidNotification(
      int taskId, String taskName, String dueDate) async {
    const AndroidNotificationDetails androidPlatformChannelSpecifics =
        AndroidNotificationDetails(
            '1', 'TaskSweeper', 'One of your tasks is about to expire!',
            importance: Importance.max, priority: Priority.max);

    const NotificationDetails platformChannelSpecifics =
        NotificationDetails(android: androidPlatformChannelSpecifics);

    tz.TZDateTime scheduledDueDate = tz.TZDateTime.parse(tz.local, dueDate)
        .subtract(const Duration(days: 1));

    tz.TZDateTime currentDueDate = tz.TZDateTime.now(tz.local);

    if (currentDueDate.isAfter(scheduledDueDate)) {
      await flutterLocalNotificationsPlugin.show(
          taskId,
          "Tasksweeper",
          "$taskName with id $taskId finishes in less than 1 day!",
          platformChannelSpecifics,
          );

    } else
      await flutterLocalNotificationsPlugin.zonedSchedule(
          taskId,
          "Tasksweeper",
          "$taskName with id $taskId finishes in 1 day!",
          scheduledDueDate,
          platformChannelSpecifics,
          androidAllowWhileIdle: true,
          uiLocalNotificationDateInterpretation:
              UILocalNotificationDateInterpretation.absoluteTime);
  }

  Future<bool> hasScheduledAndroidNotification(int taskId) async {
    final List<PendingNotificationRequest> pendingNotificationRequests =
        await flutterLocalNotificationsPlugin.pendingNotificationRequests();

    return pendingNotificationRequests
            .where((notification) => notification.id == taskId)
            .length ==
        1;
  }

  void removeNotification(int taskId) async {
    await flutterLocalNotificationsPlugin.cancel(taskId);
  }

  factory NotificationService() {
    return _notificationService;
  }

  NotificationService._internal();
}
