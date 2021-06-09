import 'dart:async';
import 'dart:io';

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
                String licenseID = "Satoshi-Demo-face-android";
                String licenseFile = "idl-license.faceexample-face-android-1";
                if(Platform.isIOS) {
                  licenseID = "Satoshi-Demo-face-ios";
                  licenseFile = "idl-license.face-ios";
                }
                WidgetFaceSdk.init(licenseID, licenseFile).then((value) => {
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
