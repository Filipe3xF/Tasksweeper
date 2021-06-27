import 'dart:convert';

import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;
import 'package:tskswp_client/components/regular_button.dart';
import 'package:tskswp_client/components/text_field.dart';

import '../constants.dart';
import 'HomePageScreen.dart';
import 'LoginScreen.dart';

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

    var body = jsonEncode(
        {'username': _username, 'password': _password, 'email': _email});
    var url = Uri.http('10.0.2.2:8080', '/register');
    var response = await http.post(url,
        headers: {'content-type': 'application/json'}, body: body);
    if (response.body.contains('error')) {
      setState(() {
        _error = jsonDecode(response.body)['error'];
      });
      return;
    }

    Navigator.push(
      context,
      MaterialPageRoute(
        builder: (context) => HomeScreen(
          jwt: jsonDecode(response.body)['jwt'],
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
            Padding(
              padding: EdgeInsets.all(5.0),
              child: StandardTextField(
                  onChange: (value) {
                    _email = value;
                  },
                  fieldName: 'Email'),
            ),
            Padding(
              padding: EdgeInsets.all(5.0),
              child: StandardTextField(
                  onChange: (value) {
                    _username = value;
                  },
                  fieldName: 'Username'),
            ),
            Padding(
              padding: EdgeInsets.all(5.0),
              child: StandardTextField(
                onChange: (value) {
                  _password = value;
                },
                fieldName: 'Password',
                obscureText: true,
              )
            ),
            Row(
              mainAxisAlignment: MainAxisAlignment.center,
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
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                Expanded(child: Container()),
                Expanded(
                    child: RegularButton(
                  onTap: _toLoginPage,
                  buttonTitle: 'Go back to login',
                  defaultButtonColor: Colors.lightBlueAccent,
                )),
                Expanded(child: Container())
              ],
            ),
          ],
        ),
      ),
    );
  }
}
