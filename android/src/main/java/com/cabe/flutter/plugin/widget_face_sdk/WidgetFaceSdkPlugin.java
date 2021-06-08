package com.cabe.flutter.plugin.widget_face_sdk;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.cabe.lib.face.sdk.BDFaceSDK;
import com.cabe.lib.face.sdk.BDFaceSDKInitCallback;

import java.util.Map;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;

/** WidgetFaceSdkPlugin */
public class WidgetFaceSdkPlugin implements FlutterPlugin, MethodCallHandler, ActivityAware {
  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
  private MethodChannel channel;
  private ActivityPluginBinding activityBinding = null;

  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
    channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "FaceSDKPlugin");
    channel.setMethodCallHandler(this);
  }

  @Override
  public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
    if (call.method.equals("init")) {
      String errorInfo = initSDK((Map<String, Object>) call.arguments, result);
      if(TextUtils.isEmpty(errorInfo)) result.success("Android " + android.os.Build.VERSION.RELEASE);
      else result.error("0", errorInfo, null);
    } else if (call.method.equals("startVerify")) {
      startVerify();
      result.success("");
    } else {
      result.notImplemented();
    }
  }

  private String initSDK(Map<String, Object> params, @NonNull final Result resultCallback) {
    if(activityBinding != null) {
      BDFaceSDK.init(activityBinding.getActivity(), params, new BDFaceSDKInitCallback() {
        @Override
        public void onResult(int code, String msg) {
          if(code == 0) {
            resultCallback.success(msg);
          } else {
            resultCallback.error("" + code, msg, null);
          }
        }
      });
    }
    return null;
  }

  private void startVerify() {

  }

  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    channel.setMethodCallHandler(null);
  }

  @Override
  public void onAttachedToActivity(@NonNull ActivityPluginBinding binding) {
    activityBinding = binding;
  }

  @Override
  public void onDetachedFromActivityForConfigChanges() {
  }

  @Override
  public void onReattachedToActivityForConfigChanges(@NonNull ActivityPluginBinding binding) {
  }

  @Override
  public void onDetachedFromActivity() {
    activityBinding = null;
  }
}
