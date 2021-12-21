package com.cabe.flutter.plugin.widget_face_sdk;

import android.Manifest;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;

import com.cabe.lib.face.sdk.BDFaceSDK;
import com.cabe.lib.face.sdk.BDFaceSDKInitCallback;
import com.cabe.lib.face.sdk.permission.PermissionCallbacks;
import com.cabe.lib.face.sdk.permission.PermissionHelper;
import com.cabe.lib.face.sdk.permission.PermissionTools;
import com.cabe.lib.face.sdk.ui.FaceDetectExpActivity;
import com.cabe.lib.face.sdk.ui.FaceLivenessExpActivity;
import com.cabe.lib.face.sdk.widget.FaceDetectRoundViewPro;

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
    public static String curLanguage = "cn";
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

    public static boolean switchLanguage(Context context) {
        BDFaceSDK.setRes(context, curLanguage);
//        if(curLanguage.equals("en")) BDFaceSDK.setResEn();
//        else BDFaceSDK.setResNormal();
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
//            Resources res = context.getResources();
//            DisplayMetrics dm = res.getDisplayMetrics();
//            Configuration config = res.getConfiguration();
//            Locale locale;
//            if("en".equals(curLanguage)) {
//                locale = Locale.ENGLISH;
//            } else if("zh".equals(curLanguage)) {
//                locale = Locale.SIMPLIFIED_CHINESE;
//            } else {
//                locale = Locale.getDefault();
//            }
//            config.setLocale(locale);
//            res.updateConfiguration(config, dm);
//            return true;
//        }
//        return false;
        return true;
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
            initSDK((Map<String, Object>) call.arguments, result);
        } else if (call.method.equals("switchLanguage")) {
            doLanguage((Map<String, Object>) call.arguments, result);
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
                final String[] permissionList = {
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
                                if(perms.size() == permissionList.length) {
                                    startVerify(actionAlive, result);
                                }
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
                public void onResult(final int code, final String msg) {
                    activityBinding.getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (code == 0) {
                                resultCallback.success(0);
                            } else {
                                resultCallback.error("" + code, msg, null);
                            }
                        }
                    });
                }
            });
        }
        return null;
    }

    private void startVerify(boolean isActionLive, @NonNull Result result) {
        FaceDetectRoundViewPro.setViewStyle();

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

    private void doLanguage(Map<String, Object> params, Result resultCallback) {
        String language = null;
        if(params.containsKey("language")) {
            try {
                language = (String) params.get("language");
            } catch (Exception e) {
                e.fillInStackTrace();
            }
        }
        curLanguage = language;
//        switchLanguage(activityBinding.getActivity().getApplication());
        boolean result = switchLanguage(activityBinding.getActivity());
        if (result) {
            resultCallback.success("success");
        } else resultCallback.error("-1", "系统版本过低", null);
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
