package com.cabe.flutter.plugin.widget_face_sdk;

import android.content.Intent;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.cabe.lib.face.sdk.BDFaceSDK;
import com.cabe.lib.face.sdk.BDFaceSDKInitCallback;
import com.cabe.lib.face.sdk.ui.FaceDetectExpActivity;
import com.cabe.lib.face.sdk.ui.FaceLivenessExpActivity;

import java.util.Map;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;

/**
 * WidgetFaceSdkPlugin
 */
public class WidgetFaceSdkPlugin implements FlutterPlugin, MethodCallHandler, ActivityAware {
    private static Result verifyResult;
    public static void verifyCancel() {
        if(verifyResult != null) {
            verifyResult.error("1", "取消操作", null);
            verifyResult = null;
        }
    }
    public static void verifySuccess(String base64) {
        if(verifyResult != null) {
            verifyResult.success(base64);
            verifyResult = null;
        }
    }

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
            if (TextUtils.isEmpty(errorInfo))
                result.success("");
            else result.error("0", errorInfo, null);
        } else if (call.method.equals("startVerify")) {
            startVerify(true, result);
        } else {
            result.notImplemented();
        }
    }

    private String initSDK(Map<String, Object> params, @NonNull final Result resultCallback) {
        if (activityBinding != null) {
            BDFaceSDK.init(activityBinding.getActivity(), params, new BDFaceSDKInitCallback() {
                @Override
                public void onResult(int code, String msg) {
                    if (code == 0) {
                        resultCallback.success(msg);
                    } else {
                        resultCallback.error("" + code, msg, null);
                    }
                }
            });
        }
        return null;
    }

    private void startVerify(boolean isActionLive, @NonNull Result result) {
        verifyResult = result;
        if (activityBinding != null) {
            Intent intent;
            if (isActionLive) {
                intent = new Intent(activityBinding.getActivity(), FaceLivenessExpActivity.class);
            } else {
                intent = new Intent(activityBinding.getActivity(), FaceDetectExpActivity.class);
            }
            activityBinding.getActivity().startActivity(intent);
        }
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
