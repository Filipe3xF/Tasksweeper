import 'dart:convert';

import 'package:flutter/material.dart';
import 'package:flutter_login/flutter_login.dart';
import 'package:http/http.dart' as http;
import 'package:tskswp_client/components/regular_button.dart';
import 'package:tskswp_client/screens/RegisterScreen.dart';
import 'dart:convert';

import '../constants.dart';
import 'HomePageScreen.dart';

class LoginScreen extends StatefulWidget {
  @override
  _LoginScreen createState() => _LoginScreen();
}

class _LoginScreen extends State<LoginScreen> {
  var _username;
  var _password;
  var _error;

  Future<void> _loginUser() async {
    var body = jsonEncode({'username': _username, 'password': _password});
    var url = Uri.http('10.0.2.2:8080', '/login');
    var response = await http.post(url,
        headers: {'content-type': 'application/json'}, body: body);

    if (response.body.contains('error')) {
      _error = jsonDecode(response.body)['error'];
      return;
    }

    Navigator.push(
      context,
      MaterialPageRoute(
        builder: (context) => HomeScreen(jwt: jsonDecode(response.body)['jwt']),
      ),
    );
  }

  void _toRegisterPage() {
    Navigator.push(
        context, MaterialPageRoute(builder: (context) => RegisterScreen()));
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: SafeArea(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Row(
              children: [
                Expanded(
                  child: Padding(
                    padding: EdgeInsets.all(5.0),
                    child: TextField(
                      decoration: kUsernameTextFieldInputDecoration,
                      onChanged: (value) {
                        _username = value;
                      },
                    ),
                  ),
                ),
              ],
            ),
            Row(
              children: [
                Expanded(
                  child: Padding(
                    padding: EdgeInsets.all(5.0),
                    child: TextField(
                      obscureText: true,
                      decoration: kPasswordTextFieldInputDecoration,
                      onChanged: (value) {
                        _password = value;
                      },
                    ),
                  ),
                ),
              ],
            ),
            Row(
              children: [
                Expanded(child: Container()),
                Expanded(
                  child: RegularButton(
                    onTap: _loginUser,
                    buttonTitle: 'Login',
                  ),
                ),
                Expanded(child: Container())
              ],
            ),
            Center(child: Text('or')),
            Row(
              children: [
                Expanded(child: Container()),
                Expanded(
                  child: RegularButton(
                    onTap: _toRegisterPage,
                    buttonTitle: 'Register',
                    defaultButtonColor: Colors.lightBlueAccent,
                  ),
                ),
                Expanded(child: Container())
              ],
            ),
          ],
        ),
      ),
    );
  }
}
