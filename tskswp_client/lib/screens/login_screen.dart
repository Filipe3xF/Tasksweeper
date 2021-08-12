import 'dart:convert';

import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:tskswp_client/components/regular_button.dart';
import 'package:tskswp_client/components/standard_text_field.dart';
import 'package:tskswp_client/constants.dart';
import 'package:tskswp_client/screens/home_page_screen.dart';
import 'package:tskswp_client/screens/register_screen.dart';
import 'package:tskswp_client/services/http_requests/account_requests/account_request_handler.dart';
import 'package:tskswp_client/services/status_of_the_account/Status.dart';

class LoginScreen extends StatefulWidget {
  @override
  _LoginScreen createState() => _LoginScreen();
}

class _LoginScreen extends State<LoginScreen> {
  // Used Parameters
  String? _username = '';
  String? _password = '';
  var _error = '';

  // Helping Methods
  Future<void> _loginUser() async {
    if (_username == '' || _password == '') {
      setState(() {
        _error = 'Please add a username and a password.';
      });
      return;
    }

    var responseBody = await AccountHandler.login(_username, _password);

    if (responseBody.contains('error')) {
      setState(() {
        _error = jsonDecode(responseBody)['error'];
      });
      return;
    }

    Navigator.pushReplacement(
      context,
      MaterialPageRoute(
        builder: (context) => HomeScreen(jwt: jsonDecode(responseBody)['jwt'], status: Status(),),
      ),
    );
  }

  void _toRegisterPage() {
    Navigator.pushReplacement(
        context, MaterialPageRoute(builder: (context) => RegisterScreen()));
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

  // Building the screen with the help of the methods
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar:
          AppBar(backgroundColor: kAppBarColor, title: Center(child: kTitle)),
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
                  onTap: _loginUser,
                  buttonTitle: 'Login',
                ),
                Center(child: Text('or')),
                RegularButton(
                  onTap: _toRegisterPage,
                  buttonTitle: 'Create an account',
                  defaultButtonColor: Colors.lightBlueAccent,
                )
              ],
            ),
          ),
        ),
      ),
    );
  }
}
