package com.cabe.flutter.plugin.widget_face_sdk;

import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.baidu.idl.face.platform.ui.widget.FaceDetectRoundView;
import com.cabe.lib.face.sdk.BDFaceSDK;
import com.cabe.lib.face.sdk.BDFaceSDKInitCallback;
import com.cabe.lib.face.sdk.permission.PermissionCallbacks;
import com.cabe.lib.face.sdk.permission.PermissionTools;
import com.cabe.lib.face.sdk.ui.FaceDetectExpActivity;
import com.cabe.lib.face.sdk.ui.FaceLivenessExpActivity;
import com.cabe.lib.face.sdk.permission.PermissionHelper;

import java.lang.reflect.Field;
import java.util.List;
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
    public void onMethodCall(@NonNull MethodCall call, @NonNull final Result result) {
        if (call.method.equals("init")) {
            String errorInfo = initSDK((Map<String, Object>) call.arguments, result);
            if (TextUtils.isEmpty(errorInfo))
                result.success("");
            else result.error("0", errorInfo, null);
        } else if (call.method.equals("startVerify")) {
            Map<String, Object> params = (Map<String, Object>) call.arguments;
            Boolean isAlive = true;
            if(params.containsKey("isAlive")) {
                try {
                    isAlive = (Boolean) params.get("isAlive");
                } catch (Exception e) {
                    e.fillInStackTrace();
                }
            }
            final Boolean actionAlive = isAlive;

            try {
                String[] permissionList = {
                        Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA
                };

                PermissionTools permissionTools = new PermissionTools.Builder(activityBinding.getActivity())
                        .setOnPermissionCallbacks(new PermissionCallbacks() {
                            @Override
                            public void onPermissionsGranted(int requestCode, List<String> perms) {
                                PermissionHelper.setPermission(null);
                                startVerify(actionAlive, result);
                            }
                            @Override
                            public void onPermissionsDenied(int requestCode, List<String> perms) {
                                PermissionHelper.setPermission(null);
                                result.error("-1", "permission failed", null);
                            }
                        })
                        .setRequestCode(111)
                        .build();
                permissionTools.requestPermissions(permissionList);
                PermissionHelper.setPermission(permissionTools);
            } catch (Exception e) {
                e.printStackTrace();
                startVerify(actionAlive, result);
            }
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
        try {
            Field fieldBG = FaceDetectRoundView.class.getDeclaredField("COLOR_BG");
            BDFaceSDK.changeStaticFinal(fieldBG, Color.parseColor("#FF101010"));

            Field fieldRound = FaceDetectRoundView.class.getDeclaredField("COLOR_ROUND");
            BDFaceSDK.changeStaticFinal(fieldRound, Color.parseColor("#FFFF9500"));

            Field fieldCircleLine = FaceDetectRoundView.class.getDeclaredField("COLOR_CIRCLE_LINE");
            BDFaceSDK.changeStaticFinal(fieldCircleLine, Color.parseColor("#FF878787"));

            Field fieldCircleLineSelected = FaceDetectRoundView.class.getDeclaredField("COLOR_CIRCLE_SELECT_LINE");
            BDFaceSDK.changeStaticFinal(fieldCircleLineSelected, Color.parseColor("#FFFF9500"));
        } catch (Exception e) {
            e.printStackTrace();
        }

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
