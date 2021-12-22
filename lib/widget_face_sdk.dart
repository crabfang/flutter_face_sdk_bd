
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
    };
    if(qualityLevel != null) params["qualityLevel"] = qualityLevel;
    if(isOpenSound != null) params["isOpenSound"] = isOpenSound;
    if(remoteAuthorize != null) params["remoteAuthorize"] = remoteAuthorize;
    return await _channel.invokeMethod("init", params);
  }

  static Future<Object> startVerify(bool isAlive) async {
    Map<String, Object> params = {
      "isAlive": isAlive
    };
    return await _channel.invokeMethod("startVerify", params);
  }

  static Future<Object> switchLanguage(String language, { Key key, int tipSizeTop, int tipSizeSecond }) async {
    Map<String, Object> params = {
      "language": language,
      "faceTipSizeTop": tipSizeTop,
      "faceTipSizeSecond": tipSizeSecond
    };
    return await _channel.invokeMethod("switchLanguage", params);
  }
}
