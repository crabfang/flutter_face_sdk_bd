
import 'dart:async';
import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

class WidgetFaceSdk {
  static const MethodChannel _channel =
      const MethodChannel('FaceSDKPlugin');

  static Future<Object> init(String licenseID, String licenseFileName, { Key key, int qualityLevel, bool isOpenSound, bool remoteAuthorize }) async {
    Map<String, Object> params = {
      "licenseID": licenseID,
      "licenseFileName": licenseFileName,
      "qualityLevel": qualityLevel,
      "isOpenSound": isOpenSound,
      "remoteAuthorize": remoteAuthorize
    };
    return await _channel.invokeMethod("init", params);
  }

  static Future<Object> startVerify() async {
    return await _channel.invokeMethod("startVerify");
  }
}
