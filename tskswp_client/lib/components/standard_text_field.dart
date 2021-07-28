import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:tskswp_client/constants.dart';

class StandardTextField extends StatelessWidget {
  StandardTextField({required this.onChange,
    required this.fieldName,
    this.helperText,
    this.onTap,
    this.mainText,
    this.showCursor = true,
    this.enableInteractiveSelection = true,
    this.obscureText = false});

  final Function(String?) onChange;
  final Function()? onTap;
  final String fieldName;
  final String? helperText;
  final String? mainText;
  final bool obscureText;
  final bool showCursor;
  final bool enableInteractiveSelection;

  TextEditingController? setTextEditingController(){
    if(mainText == null)
      return null;
    return TextEditingController(text: mainText);
  }

  @override
  Widget build(BuildContext context) {
    return Row(
      children: [
        Expanded(
          child: Padding(
            padding: EdgeInsets.all(7.0),
            child: TextField(
              enableInteractiveSelection: enableInteractiveSelection,
              showCursor: showCursor,
              controller: setTextEditingController(),
              onTap: onTap,
              obscureText: obscureText,
              decoration: InputDecoration(
                filled: true,
                fillColor: Colors.white,
                hintText: fieldName,
                helperText: helperText,
                hintStyle: TextStyle(color: Colors.grey),
                border: kOutlineInputBorder,
              ),
              onChanged: onChange,
            ),
          ),
        ),
      ],
    );
  }
}
