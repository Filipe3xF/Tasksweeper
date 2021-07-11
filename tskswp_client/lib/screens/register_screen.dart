import 'dart:convert';

import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:tskswp_client/components/regular_button.dart';
import 'package:tskswp_client/components/standard_text_field.dart';
import 'package:tskswp_client/services/http_requests/account_requests/account_request_handler.dart';

import '../constants.dart';
import 'home_page_screen.dart';
import 'login_screen.dart';

class RegisterScreen extends StatefulWidget {
  @override
  _RegisterScreen createState() => _RegisterScreen();
}

class _RegisterScreen extends State<RegisterScreen> {


  // Used Parameters
  String? _username = '';
  String? _password = '';
  String? _email = '';
  var _error = '';

  //Helping Methods

  Future<void> _registerUser() async {
    if (_username == '' || _password == '' || _email == '') {
      setState(() {
        _error = 'Please fill all of the fields.';
      });
      return;
    }

    var responseBody = await AccountHandler.register(
        _email, _username, _password);

    if (responseBody.contains('error')) {
      setState(() {
        _error = jsonDecode(responseBody)['error'];
      });
      return;
    }

    Navigator.pushReplacement(
      context,
      MaterialPageRoute(
        builder: (context) =>
            HomeScreen(
              jwt: jsonDecode(responseBody)['jwt'],
            ),
      ),
    );
  }

  Widget _createTextFieldRow(StandardTextField standardTextField) {
    return Row(
      children: [
        Expanded(flex: 2, child: Container()),
        Expanded(flex: 5, child: standardTextField),
        Expanded(flex: 2, child: Container())
      ],
    );
  }

  void _toLoginPage() {
    Navigator.pushReplacement(
        context, MaterialPageRoute(builder: (context) => LoginScreen()));
  }

  // Building the screen with the help of the methods

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(backgroundColor: kAppBarColor,title: Center(child: kTitle)),
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
                _createTextFieldRow(
                    StandardTextField(
                        onChange: (value) {
                          _email = value;
                        },
                        fieldName: kEmail)
                ),
                _createTextFieldRow(
                  StandardTextField(
                      onChange: (value) {
                        _username = value;
                      },
                      fieldName: kUsername),
                ),
                _createTextFieldRow(
                  StandardTextField(
                    onChange: (value) {
                      _password = value;
                    },
                    fieldName: kPassword,
                    obscureText: true,
                  ),
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
