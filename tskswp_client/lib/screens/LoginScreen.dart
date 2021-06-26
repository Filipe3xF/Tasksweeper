import 'dart:convert';

import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;
import 'package:tskswp_client/components/regular_button.dart';
import 'package:tskswp_client/components/text_field.dart';
import 'package:tskswp_client/constants.dart';
import 'package:tskswp_client/screens/RegisterScreen.dart';

import 'HomePageScreen.dart';

class LoginScreen extends StatefulWidget {
  @override
  _LoginScreen createState() => _LoginScreen();
}

class _LoginScreen extends State<LoginScreen> {
  String? _username = '';
  String? _password = '';
  var _error = '';

  Future<void> _loginUser() async {
    if (_username == '' || _password == '') {
      setState(() {
        _error = 'Please add a username and a password.';
      });
      return;
    }

    var responseBody = (await http.post(
      Uri.http('10.0.2.2:8080', '/login'),
      headers: {'content-type': 'application/json'},
      body: jsonEncode({'username': _username, 'password': _password}),
    ))
        .body;

    if (responseBody.contains('error')) {
      setState(() {
        _error = jsonDecode(responseBody)['error'];
      });
      return;
    }

    Navigator.push(
      context,
      MaterialPageRoute(
        builder: (context) => HomeScreen(jwt: jsonDecode(responseBody)['jwt']),
      ),
    );
  }

  void _toRegisterPage() {
    Navigator.pushReplacement(
        context, MaterialPageRoute(builder: (context) => RegisterScreen()));
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: SafeArea(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Center(
              child: Text(
                _error,
                style: kTextRedColor,
              ),
            ),
            StandardTextField(
                onChange: (value) {
                  _username = value;
                },
                fieldName: 'Username'),
            StandardTextField(
              onChange: (value) {
                _password = value;
              },
              fieldName: 'Password',
              obscureText: true,
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
