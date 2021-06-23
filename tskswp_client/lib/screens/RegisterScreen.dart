


import 'dart:convert';

import 'package:flutter/material.dart';
import 'package:flutter_login/flutter_login.dart';
import 'package:http/http.dart' as http;


class LoginScreen extends StatefulWidget {
  @override
  _LoginScreen createState() => _LoginScreen();
}

class _LoginScreen extends State<LoginScreen> {

  Future<String> _loginUser(LoginData data) async {
    var body = jsonEncode({'username': data.name, 'password': data.password});
    var url = Uri.http('10.0.2.2:8080', '/login');
    var response = await http.post(url,
        headers: {'content-type': 'application/json'}, body: body);
    if (response.body.contains('error')) response.body;
    return response.body;
  }

  Future<String?>? _toRegisterPage(String name) async {
    return 'something';
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Column(
        mainAxisSize: MainAxisSize.min,
        children: [
          Expanded(
            child: FlutterLogin(
              title: 'TaskSweeper',
              onLogin: _loginUser,
              onSignup: (value){},
              hideSignUpButton: true,
              hideForgotPasswordButton: false,
              messages: LoginMessages(userHint: 'Username', forgotPasswordButton: 'Remembered that you have an account?'),
              userType: LoginUserType.name,
              onSubmitAnimationCompleted: () {},
              onRecoverPassword: _toRegisterPage,
            ),
          ),
        ],
      ),
    );
  }
