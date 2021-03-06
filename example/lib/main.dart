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
  bool isLanguageEn = false;

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
              child: new Text('switchLanguage'),
              onPressed: () {
                if(isLanguageEn) {
                  WidgetFaceSdk.switchLanguage("zh");
                } else {
                  WidgetFaceSdk.switchLanguage("en", tipSizeTop: 10, tipSizeSecond: 6);
                }
                isLanguageEn = !isLanguageEn;
              },
            ),
            new RaisedButton(
              child: new Text('Init'),
              onPressed: () {
                String licenseID = "Satoshi-Demo-face-android";
                // String licenseFile = "idl-license.faceexample-face-android-1";
                String licenseFile = "idl-license.face-android";
                if(Platform.isIOS) {
                  licenseID = "Satoshi-Demo-face-ios";
                  licenseFile = "idl-license.face-ios";
                }
                WidgetFaceSdk.init(licenseID, licenseFile, qualityLevel: 2).then((value) => {
                  print("init: " + value)
                });
              },
            ),
            new RaisedButton(
              child: new Text('????????????'),
              onPressed: () {
                WidgetFaceSdk.startVerify(false).then((value) => {
                  print("verify: " + value)
                });
              },
            ),
            new RaisedButton(
              child: new Text('????????????'),
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
