import 'dart:convert';

import 'package:flutter/material.dart';
import 'package:flutter_login/flutter_login.dart';
import 'package:http/http.dart' as http;
import 'package:tskswp_client/components/regular_button.dart';

import '../constants.dart';
import 'HomePageScreen.dart';
import 'LoginScreen.dart';

class RegisterScreen extends StatefulWidget {
  @override
  _RegisterScreen createState() => _RegisterScreen();
}

class _RegisterScreen extends State<RegisterScreen> {
  var _username;
  var _password;
  var _email;
  var _error;

  Future<void> _registerUser() async {
    var body = jsonEncode({'username': _username, 'password': _password, 'email': _email});
    var url = Uri.http('10.0.2.2:8080', '/register');
    var response = await http.post(url,
        headers: {'content-type': 'application/json'}, body: body);
    if (response.body.contains('error')) _error = response.body;
    Navigator.push(
        context, MaterialPageRoute(builder: (context) => HomeScreen()));
  }

  void _toLoginPage() {
    Navigator.pop(
        context, MaterialPageRoute(builder: (context) => LoginScreen()));
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
                      decoration: kEmailTextFieldInputDecoration,
                      onChanged: (value) {
                        _email = value;
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
                    onTap: _registerUser,
                    buttonTitle: 'Register',
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
                      onTap: _toLoginPage,
                      buttonTitle: 'Login',
                      defaultButtonColor: Colors.lightBlueAccent),
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
