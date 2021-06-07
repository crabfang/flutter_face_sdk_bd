
import 'dart:async';

import 'package:flutter/services.dart';

class WidgetFaceSdk {
  static const MethodChannel _channel =
      const MethodChannel('widget_face_sdk');

  static Future<String> get platformVersion async {
    final String version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }
}
