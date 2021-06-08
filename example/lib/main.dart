import 'dart:async';

import 'package:flutter/material.dart';
import 'package:widget_face_sdk/widget_face_sdk.dart';

void main() {
  runApp(MyApp());
}

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  @override
  void initState() {
    super.initState();
    initPlatformState();
  }

  // Platform messages are asynchronous, so we initialize in an async method.
  Future<void> initPlatformState() async {
    // If the widget was removed from the tree while the asynchronous platform
    // message was in flight, we want to discard the reply rather than calling
    // setState to update our non-existent appearance.
    if (!mounted) return;

    setState(() {
      //TODO
    });
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Column(
          children: [
            new RaisedButton(
              child: new Text('Init'),
              onPressed: () {
                WidgetFaceSdk.init("Satoshi-Demo-face-android", "idl-license.faceexample-face-android-1").then((value) => {
                  print("init: " + value)
                });
              },
            ),
            new RaisedButton(
              child: new Text('人脸识别'),
              onPressed: () {
                WidgetFaceSdk.startVerify(false).then((value) => {
                  print("verify: " + value)
                });
              },
            ),
            new RaisedButton(
              child: new Text('活体检测'),
              onPressed: () {
                WidgetFaceSdk.startVerify(true).then((value) => {
                  print("verify: " + value)
                });
              },
            )
          ],
        ),
      ),
    );
  }
}
