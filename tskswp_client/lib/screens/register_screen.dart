import 'dart:convert';

import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;
import 'package:tskswp_client/components/regular_button.dart';
import 'package:tskswp_client/components/text_field.dart';
import 'package:tskswp_client/services/http_request_handler.dart';

import '../constants.dart';
import 'home_page_screen.dart';
import 'login_screen.dart';

class RegisterScreen extends StatefulWidget {
  @override
  _RegisterScreen createState() => _RegisterScreen();
}

class _RegisterScreen extends State<RegisterScreen> {
  String? _username = '';
  String? _password = '';
  String? _email = '';
  var _error = '';

  Future<void> _registerUser() async {
    if (_username == '' || _password == '' || _email == '') {
      setState(() {
        _error = 'Please fill all of the fields.';
      });
      return;
    }

    var responseBody = await HttpHandler().postRequest(
        {'username': _username, 'password': _password, 'email': _email},
        '/register');

    if (responseBody.contains('error')) {
      setState(() {
        _error = jsonDecode(responseBody)['error'];
      });
      return;
    }

    Navigator.push(
      context,
      MaterialPageRoute(
        builder: (context) => HomeScreen(
          jwt: jsonDecode(responseBody)['jwt'],
        ),
      ),
    );
  }

  void _toLoginPage() {
    Navigator.pushReplacement(
        context, MaterialPageRoute(builder: (context) => LoginScreen()));
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: kTitle),
      resizeToAvoidBottomInset: true,
      body: SafeArea(
        child: Center(
          child: SingleChildScrollView(
            child: Column(
              mainAxisAlignment: MainAxisAlignment.center,
              mainAxisSize: MainAxisSize.min,
              children: [
                Center(
                  child: Text(
                    _error,
                    style: kTextRedColor,
                  ),
                ),
                Row(
                  children: [
                    Expanded(flex: 2, child: Container()),
                    Expanded(
                      flex: 5,
                      child: Padding(
                        padding: EdgeInsets.all(5.0),
                        child: StandardTextField(
                            onChange: (value) {
                              _email = value;
                            },
                            fieldName: 'Email'),
                      ),
                    ),
                    Expanded(flex: 2, child: Container())
                  ],
                ),
                Row(
                  children: [
                    Expanded(flex: 2, child: Container()),
                    Expanded(
                      flex: 5,
                      child: Padding(
                        padding: EdgeInsets.all(5.0),
                        child: StandardTextField(
                            onChange: (value) {
                              _username = value;
                            },
                            fieldName: 'Username'),
                      ),
                    ),
                    Expanded(flex: 2, child: Container())
                  ],
                ),
                Row(
                  children: [
                    Expanded(flex: 2, child: Container()),
                    Expanded(
                      flex: 5,
                      child: Padding(
                        padding: EdgeInsets.all(5.0),
                        child: StandardTextField(
                          onChange: (value) {
                            _password = value;
                          },
                          fieldName: 'Password',
                          obscureText: true,
                        ),
                      ),
                    ),
                    Expanded(flex: 2, child: Container())
                  ],
                ),
                RegularButton(
                  onTap: _registerUser,
                  buttonTitle: 'Register',
                ),
                Center(child: Text('or')),
                RegularButton(
                  onTap: _toLoginPage,
                  buttonTitle: 'Go back to login',
                  defaultButtonColor: Colors.lightBlueAccent,
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }
}
