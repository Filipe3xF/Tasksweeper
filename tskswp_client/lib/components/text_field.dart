import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';

class StandardTextField extends StatelessWidget {

  StandardTextField({required this.onChange, required this.fieldName, this.obscureText = false});

  final Function(String?) onChange;
  final String fieldName;
  final bool obscureText;

  @override
  Widget build(BuildContext context) {
    return Row(
      children: [
        Expanded(
          child: Padding(
            padding: EdgeInsets.all(5.0),
            child: TextField(
              obscureText: obscureText,
              decoration: InputDecoration(
                filled: true,
                fillColor: Colors.white,
                hintText: fieldName,
                hintStyle: TextStyle(color: Colors.grey),
                border: OutlineInputBorder(
                  borderRadius: BorderRadius.all(
                    Radius.circular(10.0),
                  ),
                  borderSide: BorderSide(color: Colors.black, width: 1.0),
                ),
              ),
              onChanged: onChange,
            ),
          ),
        ),
      ],
    );
  }
}
