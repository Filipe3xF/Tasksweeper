import 'dart:convert';

import 'package:flutter/material.dart';
import 'package:tskswp_client/components/regular_button.dart';
import 'package:tskswp_client/components/standard_text_field.dart';
import 'package:select_form_field/select_form_field.dart';
import 'package:intl/intl.dart';
import 'package:tskswp_client/services/http_requests/task_requests/task_request_handler.dart';

import '../constants.dart';

class TaskCreationScreen extends StatefulWidget {
  TaskCreationScreen({required this.jwt});

  final String jwt;

  @override
  _TaskCreationScreen createState() => _TaskCreationScreen(jwt: this.jwt);
}

class _TaskCreationScreen extends State<TaskCreationScreen> {
  _TaskCreationScreen({required this.jwt});

  final String jwt;

  String? difficulty;
  String? name;
  String? description;
  String? repetition;
  DateTime? dueDate;
  TimeOfDay? dueTime;

  final DateFormat dateFormatter = DateFormat('dd-MM-yyyy');
  final DateFormat timeFormatter = DateFormat('HH-mm');

  String error = '';

  final List<Map<String, dynamic>>? difficultySelectionOptions = [
    {'value': 'Easy', 'label': 'Easy'},
    {'value': 'Medium', 'label': 'Medium'},
    {'value': 'Hard', 'label': 'Hard'}
  ];
  final List<Map<String, dynamic>>? repetitionSelectionOptions = [
    {'value': 'Daily', 'label': 'Daily'},
    {'value': 'Weekly', 'label': 'Weekly'},
    {'value': 'Monthly', 'label': 'Monthly'},
    {'value': 'Yearly', 'label': 'Yearly'}
  ];

  void createTask() async {
    Map<String,dynamic> body = {};


    if(name == null || name == '' || difficulty == null){
      setState(() {
        error = 'Both name and difficulty must be filled!';
      });
      return null;
    }

    body['name'] = name;
    body['difficultyName'] = difficulty;

    if (dueDate != null && dueTime != null) {
      List<String> date = dateFormatter.format(dueDate!).split('-');

      DateTime dueTimeDate = DateTime(0,0,0, dueTime!.hour, dueTime!.minute);
      List<String> time = timeFormatter.format(dueTimeDate).split('-');

      body['dueDate'] = {'day': date[0], 'month': date[1], 'year': date[2]};
      body['dueTime'] = {'hour': time[0], 'minute': time[1], 'second': '00'};
    }
    else{
      if(dueDate != null || dueTime != null){
        setState(() {
          error = 'Both DueDate and DueTime forms must be filled in order to establish a valid DueDate!';
        });
        return;
      }
    }

    if(repetition != null){
      body['repetition'] = repetition;
    }

    if(description != null){
      body['description'] = description;
    }

    var response = await TaskHandler.createNewTask(jwt, body);

    if(response.contains('error')){
      setState(() {
        error = jsonDecode(response)['error'];
      });
      return;
    }

    Navigator.pop(context);
  }

  Future<Null> _defineDueDate() async {
    DateTime firstAvailableDueDate = DateTime.now().add(Duration(days: 1));

    final DateTime? picked = await showDatePicker(
        context: context,
        initialDate: dueDate == null ? firstAvailableDueDate : dueDate!,
        firstDate: firstAvailableDueDate,
        lastDate: DateTime(2500));
    if (picked != null && picked != dueDate)
      setState(() {
        dueDate = picked;
      });
  }

  Future<Null> _defineDueTime() async {
    TimeOfDay firstAvailableDueTime = TimeOfDay.now();

    final TimeOfDay? picked = await showTimePicker(
      context: context,
      initialTime: dueTime == null ? firstAvailableDueTime : dueTime!,
      initialEntryMode: TimePickerEntryMode.input,
    );
    if (picked != null && picked != dueTime)
      setState(() {
        dueTime = picked;
      });
  }

  String? _defineDueDateMainText() {
    if (dueDate == null)
      return null;
    else
      return dateFormatter.format(dueDate!);
  }

  String? _defineDueTimeMainText() {
    if (dueTime == null)
      return null;
    else
      return dueTime?.format(context);
  }

  Widget _createRow(Widget widget) {
    return Row(
      children: [
        Expanded(flex: 2, child: Container()),
        Expanded(flex: 5, child: widget),
        Expanded(flex: 2, child: Container())
      ],
    );
  }

  Widget _createPickerTextField(
      Function picker, Function mainTextSetter, String label) {
    return _createRow(
      StandardTextField(
        onChange: (value) {},
        onTap: () {
          //Stops Keyboard from appearing when Tapped on
          FocusScope.of(context).requestFocus(new FocusNode());

          picker();
        },
        fieldName: label,
        showCursor: false,
        enableInteractiveSelection: false,
        mainText: mainTextSetter(),
      ),
    );
  }

  Widget _createSelectForm(EdgeInsets padding, String label,
      List<Map<String, dynamic>>? items, Function(String) onChanged) {
    return Padding(
      padding: padding,
      child: _createRow(SelectFormField(
        type: SelectFormFieldType.dropdown,
        labelText: label,
        items: items,
        onChanged: onChanged,
      )),
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar:
          AppBar(backgroundColor: kAppBarColor, title: Center(child: kTitle)),
      body: SafeArea(
          child: SingleChildScrollView(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Text(error),
            _createSelectForm(EdgeInsets.all(5), 'Select the difficulty *',
                difficultySelectionOptions, (val) => difficulty = val),
            _createSelectForm(
                EdgeInsets.only(top: 5, left: 5, right: 5, bottom: 30),
                'Select The Repetition',
                repetitionSelectionOptions,
                (val) => repetition = val),
            _createRow(
              StandardTextField(
                onChange: (value) {
                  name = value;
                },
                fieldName: 'Name *',
              ),
            ),
            _createRow(
              StandardTextField(
                  onChange: (value) {
                    description = value;
                  },
                  fieldName: 'Description'),
            ),
            _createPickerTextField(
                _defineDueDate, _defineDueDateMainText, 'Due Date'),
            _createPickerTextField(
                _defineDueTime, _defineDueTimeMainText, 'Due Time')
          ],
        ),
      )),
      bottomNavigationBar: RegularButton(
          onTap: createTask,
          buttonTitle: 'Create Task'),
    );
  }
}


