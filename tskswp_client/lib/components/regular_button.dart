import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';

class RegularButton extends StatelessWidget {
  RegularButton({required this.onTap, required this.buttonTitle, this.defaultButtonColor = Colors.blueAccent, this.hoverButtonColor = Colors.blueGrey});

  final Function()? onTap;
  final String buttonTitle;
  final Color defaultButtonColor;
  final Color hoverButtonColor;

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: EdgeInsets.all(5.0),
      child: TextButton(
        onPressed: onTap,
        child: Padding(
          padding: EdgeInsets.all(5.0),
          child: Text(
            buttonTitle,
            softWrap: false,
            textScaleFactor: 1.5,
          ),
        ),
        style: ButtonStyle(
          elevation: MaterialStateProperty.all(5.0),
          backgroundColor: _applyColorsForBackground(context),
          foregroundColor: MaterialStateProperty.all(Colors.white),
          shape: MaterialStateProperty.all(
            RoundedRectangleBorder(
              borderRadius: BorderRadius.circular(10.0),
            ),
          ),
        ),
      ),
    );
  }

  MaterialStateProperty<Color>? _applyColorsForBackground(context) {
    return MaterialStateProperty.resolveWith<Color>(
      (Set<MaterialState> states) {
        if (states.contains(MaterialState.hovered) ||
            states.contains(MaterialState.pressed) ||
            states.contains(MaterialState.focused)) return hoverButtonColor;
        return defaultButtonColor;
      },
    );
  }
}
