package com.cabe.lib.face.sdk;

import android.app.Activity;
import android.content.Context;

import com.baidu.idl.face.platform.FaceConfig;
import com.baidu.idl.face.platform.FaceEnvironment;
import com.baidu.idl.face.platform.FaceSDKManager;
import com.baidu.idl.face.platform.FaceStatusNewEnum;
import com.baidu.idl.face.platform.LivenessTypeEnum;
import com.baidu.idl.face.platform.listener.IInitCallback;
import com.cabe.flutter.plugin.widget_face_sdk.R;
import com.cabe.lib.face.sdk.manager.QualityConfigManager;
import com.cabe.lib.face.sdk.model.QualityConfig;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class BDFaceSDK {
    public static int RES_TIPS_DEFAULT = R.string.detect_face_in;
    public static int RES_TIPS_FACE_AHEAD = R.string.detect_face_with_ahead;
    public static int RES_TIPS_DIALOG_TIMEOUT_TITLE = R.string.dialog_timeout_msg;
    public static int RES_TIPS_DIALOG_TIMEOUT_RETRY = R.string.dialog_timeout_btn_retry;
    public static int RES_TIPS_DIALOG_TIMEOUT_CANCEL = R.string.dialog_timeout_btn_back;
    public static int RES_FACE_TIP_SIZE_TOP = 22;
    public static int RES_FACE_TIP_SIZE_SECOND = 16;
    static {
        FaceEnvironment.livenessTypeDefaultList.clear();
        FaceEnvironment.livenessTypeDefaultList.add(LivenessTypeEnum.Eye);
        FaceEnvironment.livenessTypeDefaultList.add(LivenessTypeEnum.Mouth);
        FaceEnvironment.livenessTypeDefaultList.add(LivenessTypeEnum.HeadRight);
        FaceEnvironment.livenessTypeDefaultList.add(LivenessTypeEnum.HeadLeft);
        FaceEnvironment.livenessTypeDefaultList.add(LivenessTypeEnum.HeadUp);
        FaceEnvironment.livenessTypeDefaultList.add(LivenessTypeEnum.HeadDown);
    }
    public static void init(final Context context, Map<String, Object> params, final BDFaceSDKInitCallback callback) {
        int qualityLevel = 0;
        if(params.containsKey("qualityLevel")) {
            try {
                qualityLevel = (int) params.get("qualityLevel");
            } catch (Exception e) {
                e.fillInStackTrace();
            }
        }
        boolean isOpenSound = true;
        if(params.containsKey("isOpenSound")) {
            try {
                isOpenSound = (boolean) params.get("isOpenSound");
            } catch (Exception e) {
                e.fillInStackTrace();
            }
        }
        boolean success = setFaceConfig(context, qualityLevel, isOpenSound);
        if (!success) {
            if(callback != null) callback.onResult(-1, "??????????????? = json????????????????????????");
            return;
        }

        String licenseID = null;
        if(params.containsKey("licenseID")) {
            try {
                licenseID = (String) params.get("licenseID");
            } catch (Exception e) {
                e.fillInStackTrace();
            }
        }
        String licenseFileName = null;
        if(params.containsKey("licenseFileName")) {
            try {
                licenseFileName = (String) params.get("licenseFileName");
            } catch (Exception e) {
                e.fillInStackTrace();
            }
        }
        // ??????android???ios ???????????????appId=appname_face_android ,??????appname?????????sdk???????????????
        // ???????????????
        // ??????License?????????APPID
        // assets?????????License?????????
        FaceSDKManager.getInstance().initialize(context, licenseID, licenseFileName, new IInitCallback() {
                    @Override
                    public void initSuccess() {
                        if(callback != null) callback.onResult(0, "???????????????");
                    }
                    @Override
                    public void initFailure(final int errCode, final String errMsg) {
                        if(callback != null) callback.onResult(errCode, errMsg);
                    }
                });
    }

    private static boolean setFaceConfig(Context context, int qualityLevel, boolean isOpenSound) {
        FaceConfig config = FaceSDKManager.getInstance().getFaceConfig();
        // ???????????????????????????????????????????????????????????????????????????????????????set?????????????????????
        QualityConfigManager manager = QualityConfigManager.getInstance();
        manager.readQualityFile(context.getApplicationContext(), qualityLevel);
        QualityConfig qualityConfig = manager.getConfig();
        if (qualityConfig == null) {
            return false;
        }
        // ?????????????????????
        config.setBlurnessValue(qualityConfig.getBlur());
        // ?????????????????????????????????0-255???
        config.setBrightnessValue(qualityConfig.getMinIllum());
        // ?????????????????????????????????0-255???
        config.setBrightnessMaxValue(qualityConfig.getMaxIllum());
        // ????????????????????????
        config.setOcclusionLeftEyeValue(qualityConfig.getLeftEyeOcclusion());
        // ????????????????????????
        config.setOcclusionRightEyeValue(qualityConfig.getRightEyeOcclusion());
        // ????????????????????????
        config.setOcclusionNoseValue(qualityConfig.getNoseOcclusion());
        // ????????????????????????
        config.setOcclusionMouthValue(qualityConfig.getMouseOcclusion());
        // ???????????????????????????
        config.setOcclusionLeftContourValue(qualityConfig.getLeftContourOcclusion());
        // ???????????????????????????
        config.setOcclusionRightContourValue(qualityConfig.getRightContourOcclusion());
        // ????????????????????????
        config.setOcclusionChinValue(qualityConfig.getChinOcclusion());
        // ???????????????????????????
        config.setHeadPitchValue(qualityConfig.getPitch());
        config.setHeadYawValue(qualityConfig.getYaw());
        config.setHeadRollValue(qualityConfig.getRoll());
        // ????????????????????????????????????
        config.setMinFaceSize(FaceEnvironment.VALUE_MIN_FACE_SIZE);
        // ?????????????????????????????????
        config.setNotFaceValue(FaceEnvironment.VALUE_NOT_FACE_THRESHOLD);
        // ??????????????????
        config.setEyeClosedValue(FaceEnvironment.VALUE_CLOSE_EYES);
        // ????????????????????????
        config.setCacheImageNum(FaceEnvironment.VALUE_CACHE_IMAGE_NUM);
        // ?????????????????????????????????list???LivenessTypeEunm.Eye, LivenessTypeEunm.Mouth,
        // LivenessTypeEunm.HeadUp, LivenessTypeEunm.HeadDown, LivenessTypeEunm.HeadLeft,
        // LivenessTypeEunm.HeadRight
        config.setLivenessTypeList(null);
        // ??????????????????????????????
        config.setLivenessRandom(true);
        // ?????????????????????????????????
        config.setLivenessRandomCount(FaceEnvironment.VALUE_LIVENESS_DEFAULT_RANDOM_COUNT);
        // ?????????????????????
        config.setSound(isOpenSound);
        // ??????????????????
        config.setScale(FaceEnvironment.VALUE_SCALE);
        // ???????????????????????????????????????????????????????????????????????????4???3
        config.setCropHeight(FaceEnvironment.VALUE_CROP_HEIGHT);
        config.setCropWidth(FaceEnvironment.VALUE_CROP_WIDTH);
        // ??????????????????????????????
        config.setEnlargeRatio(FaceEnvironment.VALUE_CROP_ENLARGERATIO);
        // ???????????????0???Base64??????????????????image_sec???false???1???????????????????????????????????????image_sec???true
        config.setSecType(FaceEnvironment.VALUE_SEC_TYPE);
        // ??????????????????
        config.setTimeDetectModule(FaceEnvironment.TIME_DETECT_MODULE);
        // ?????????????????????
        config.setFaceFarRatio(FaceEnvironment.VALUE_FAR_RATIO);
        config.setFaceClosedRatio(FaceEnvironment.VALUE_CLOSED_RATIO);
        FaceSDKManager.getInstance().setFaceConfig(config);
        return true;
    }

    public static void changeStaticFinal(Field field, Object newValue) throws Exception {
        field.setAccessible(true); // ??????field???private,??????????????????????????????????????????
        field.set(null, newValue); // ?????????field????????????
    }

    public static void setRoundPaintColor(Object clazz, String fieldName, int newValue) {
        try {
            Field field = clazz.getClass().getDeclaredField(fieldName);
            //???????????????????????????????????????private??????????????????
            field.setAccessible(true);
            Object textPaint = field.get(clazz);
            Class textClass = textPaint.getClass();
            Method method = textClass.getMethod("setColor", int.class);
            method.invoke(textPaint, newValue);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Map<String, Activity> destroyMap = new HashMap<>();
    /**
     * ?????????????????????
     * @param activity ????????????activity
     */
    public static void addDestroyActivity(Activity activity, String activityName) {
        destroyMap.put(activityName, activity);
    }

    /**
     * ????????????Activity
     */
    public static void destroyActivity(String activityName) {
        Set<String> keySet = destroyMap.keySet();
        for (String key : keySet) {
            destroyMap.get(key).finish();
        }
    }

    private static int getResId(Context context, int resId, String language) {
        if(language != null) language = "_" + language.replace("-", "_").toLowerCase(Locale.getDefault());
        else language = "";
        String resName = context.getResources().getResourceName(resId);
        int newId = context.getResources().getIdentifier(resName + language, null, null);
        if(newId == 0) newId = resId;
        return newId;
    }

    public static void setRes(Context context, String language) {
        // Sound Res Id
        FaceEnvironment.setSoundId(FaceStatusNewEnum.DetectRemindCodeNoFaceDetected, getResId(context, R.raw.detect_face_in, language));
        FaceEnvironment.setSoundId(FaceStatusNewEnum.DetectRemindCodeBeyondPreviewFrame, getResId(context, R.raw.detect_face_in, language));
        FaceEnvironment.setSoundId(FaceStatusNewEnum.FaceLivenessActionTypeLiveEye, getResId(context, R.raw.liveness_eye, language));
        FaceEnvironment.setSoundId(FaceStatusNewEnum.FaceLivenessActionTypeLiveMouth, getResId(context, R.raw.liveness_mouth, language));
        FaceEnvironment.setSoundId(FaceStatusNewEnum.FaceLivenessActionTypeLivePitchUp, getResId(context, R.raw.liveness_head_up, language));
        FaceEnvironment.setSoundId(FaceStatusNewEnum.FaceLivenessActionTypeLivePitchDown, getResId(context, R.raw.liveness_head_down, language));
        FaceEnvironment.setSoundId(FaceStatusNewEnum.FaceLivenessActionTypeLiveYawLeft, getResId(context, R.raw.liveness_head_left, language));
        FaceEnvironment.setSoundId(FaceStatusNewEnum.FaceLivenessActionTypeLiveYawRight, getResId(context, R.raw.liveness_head_right, language));
        FaceEnvironment.setSoundId(FaceStatusNewEnum.FaceLivenessActionComplete, getResId(context, R.raw.face_good, language));
        FaceEnvironment.setSoundId(FaceStatusNewEnum.OK, getResId(context, R.raw.face_good, language));

        // Tips Res Id
        FaceEnvironment.setTipsId(FaceStatusNewEnum.DetectRemindCodeNoFaceDetected, getResId(context, R.string.detect_face_in, language));
        FaceEnvironment.setTipsId(FaceStatusNewEnum.DetectRemindCodeBeyondPreviewFrame, getResId(context, R.string.detect_face_in, language));
        FaceEnvironment.setTipsId(FaceStatusNewEnum.DetectRemindCodePoorIllumination, getResId(context, R.string.detect_low_light, language));
        FaceEnvironment.setTipsId(FaceStatusNewEnum.DetectRemindCodeImageBlured, getResId(context, R.string.detect_keep, language));
        FaceEnvironment.setTipsId(FaceStatusNewEnum.DetectRemindCodeOcclusionLeftEye, getResId(context, R.string.detect_occ_left_eye, language));
        FaceEnvironment.setTipsId(FaceStatusNewEnum.DetectRemindCodeOcclusionRightEye, getResId(context, R.string.detect_occ_right_eye, language));
        FaceEnvironment.setTipsId(FaceStatusNewEnum.DetectRemindCodeOcclusionNose, getResId(context, R.string.detect_occ_nose, language));
        FaceEnvironment.setTipsId(FaceStatusNewEnum.DetectRemindCodeOcclusionMouth, getResId(context, R.string.detect_occ_mouth, language));
        FaceEnvironment.setTipsId(FaceStatusNewEnum.DetectRemindCodeOcclusionLeftContour, getResId(context, R.string.detect_occ_left_check, language));
        FaceEnvironment.setTipsId(FaceStatusNewEnum.DetectRemindCodeOcclusionRightContour, getResId(context, R.string.detect_occ_right_check, language));
        FaceEnvironment.setTipsId(FaceStatusNewEnum.DetectRemindCodeOcclusionChinContour, getResId(context, R.string.detect_occ_chin, language));
        FaceEnvironment.setTipsId(FaceStatusNewEnum.DetectRemindCodePitchOutofUpRange, getResId(context, R.string.detect_head_down, language));
        FaceEnvironment.setTipsId(FaceStatusNewEnum.DetectRemindCodePitchOutofDownRange, getResId(context, R.string.detect_head_up, language));
        FaceEnvironment.setTipsId(FaceStatusNewEnum.DetectRemindCodeYawOutofLeftRange, getResId(context, R.string.detect_head_right, language));
        FaceEnvironment.setTipsId(FaceStatusNewEnum.DetectRemindCodeYawOutofRightRange, getResId(context, R.string.detect_head_left, language));
        FaceEnvironment.setTipsId(FaceStatusNewEnum.DetectRemindCodeTooFar, getResId(context, R.string.detect_zoom_in, language));
        FaceEnvironment.setTipsId(FaceStatusNewEnum.DetectRemindCodeTooClose, getResId(context, R.string.detect_zoom_out, language));
        FaceEnvironment.setTipsId(FaceStatusNewEnum.DetectRemindCodeLeftEyeClosed, getResId(context, R.string.detect_left_eye_close, language));
        FaceEnvironment.setTipsId(FaceStatusNewEnum.DetectRemindCodeRightEyeClosed, getResId(context, R.string.detect_right_eye_close, language));
        FaceEnvironment.setTipsId(FaceStatusNewEnum.FaceLivenessActionTypeLiveEye, getResId(context, R.string.liveness_eye, language));
        FaceEnvironment.setTipsId(FaceStatusNewEnum.FaceLivenessActionTypeLiveMouth, getResId(context, R.string.liveness_mouth, language));
        FaceEnvironment.setTipsId(FaceStatusNewEnum.FaceLivenessActionTypeLivePitchUp, getResId(context, R.string.liveness_head_up, language));
        FaceEnvironment.setTipsId(FaceStatusNewEnum.FaceLivenessActionTypeLivePitchDown, getResId(context, R.string.liveness_head_down, language));
        FaceEnvironment.setTipsId(FaceStatusNewEnum.FaceLivenessActionTypeLiveYawLeft, getResId(context, R.string.liveness_head_left, language));
        FaceEnvironment.setTipsId(FaceStatusNewEnum.FaceLivenessActionTypeLiveYawRight, getResId(context, R.string.liveness_head_right, language));
        FaceEnvironment.setTipsId(FaceStatusNewEnum.FaceLivenessActionComplete, getResId(context, R.string.liveness_good, language));
        FaceEnvironment.setTipsId(FaceStatusNewEnum.OK, getResId(context, R.string.liveness_good, language));
        FaceEnvironment.setTipsId(FaceStatusNewEnum.DetectRemindCodeTimeout, getResId(context, R.string.detect_timeout, language));

        RES_TIPS_DEFAULT = getResId(context, R.string.detect_face_in, language);
        RES_TIPS_FACE_AHEAD = getResId(context, R.string.detect_face_with_ahead, language);
        RES_TIPS_DIALOG_TIMEOUT_TITLE = getResId(context, R.string.dialog_timeout_msg, language);
        RES_TIPS_DIALOG_TIMEOUT_RETRY = getResId(context, R.string.dialog_timeout_btn_retry, language);
        RES_TIPS_DIALOG_TIMEOUT_CANCEL = getResId(context, R.string.dialog_timeout_btn_back, language);
    }

//    public static void setResNormal() {
//        // Sound Res Id
//        FaceEnvironment.setSoundId(FaceStatusNewEnum.DetectRemindCodeNoFaceDetected, R.raw.detect_face_in);
//        FaceEnvironment.setSoundId(FaceStatusNewEnum.DetectRemindCodeBeyondPreviewFrame, R.raw.detect_face_in);
//        FaceEnvironment.setSoundId(FaceStatusNewEnum.DetectRemindCodeNoFaceDetected, R.raw.detect_face_in);
//        FaceEnvironment.setSoundId(FaceStatusNewEnum.FaceLivenessActionTypeLiveEye, R.raw.liveness_eye);
//        FaceEnvironment.setSoundId(FaceStatusNewEnum.FaceLivenessActionTypeLiveMouth, R.raw.liveness_mouth);
//        FaceEnvironment.setSoundId(FaceStatusNewEnum.FaceLivenessActionTypeLivePitchUp, R.raw.liveness_head_up);
//        FaceEnvironment.setSoundId(FaceStatusNewEnum.FaceLivenessActionTypeLivePitchDown, R.raw.liveness_head_down);
//        FaceEnvironment.setSoundId(FaceStatusNewEnum.FaceLivenessActionTypeLiveYawLeft, R.raw.liveness_head_left);
//        FaceEnvironment.setSoundId(FaceStatusNewEnum.FaceLivenessActionTypeLiveYawRight, R.raw.liveness_head_right);
//        FaceEnvironment.setSoundId(FaceStatusNewEnum.FaceLivenessActionComplete, R.raw.face_good);
//        FaceEnvironment.setSoundId(FaceStatusNewEnum.OK, R.raw.face_good);
//
//        // Tips Res Id
//        FaceEnvironment.setTipsId(FaceStatusNewEnum.DetectRemindCodeNoFaceDetected, R.string.detect_face_in);
//        FaceEnvironment.setTipsId(FaceStatusNewEnum.DetectRemindCodeBeyondPreviewFrame, R.string.detect_face_in);
//        FaceEnvironment.setTipsId(FaceStatusNewEnum.DetectRemindCodePoorIllumination, R.string.detect_low_light);
//        FaceEnvironment.setTipsId(FaceStatusNewEnum.DetectRemindCodeImageBlured, R.string.detect_keep);
//        FaceEnvironment.setTipsId(FaceStatusNewEnum.DetectRemindCodeOcclusionLeftEye, R.string.detect_occ_left_eye);
//        FaceEnvironment.setTipsId(FaceStatusNewEnum.DetectRemindCodeOcclusionRightEye, R.string.detect_occ_right_eye);
//        FaceEnvironment.setTipsId(FaceStatusNewEnum.DetectRemindCodeOcclusionNose, R.string.detect_occ_nose);
//        FaceEnvironment.setTipsId(FaceStatusNewEnum.DetectRemindCodeOcclusionMouth, R.string.detect_occ_mouth);
//        FaceEnvironment.setTipsId(FaceStatusNewEnum.DetectRemindCodeOcclusionLeftContour, R.string.detect_occ_left_check);
//        FaceEnvironment.setTipsId(FaceStatusNewEnum.DetectRemindCodeOcclusionRightContour, R.string.detect_occ_right_check);
//        FaceEnvironment.setTipsId(FaceStatusNewEnum.DetectRemindCodeOcclusionChinContour, R.string.detect_occ_chin);
//        FaceEnvironment.setTipsId(FaceStatusNewEnum.DetectRemindCodePitchOutofUpRange, R.string.detect_head_down);
//        FaceEnvironment.setTipsId(FaceStatusNewEnum.DetectRemindCodePitchOutofDownRange, R.string.detect_head_up);
//        FaceEnvironment.setTipsId(FaceStatusNewEnum.DetectRemindCodeYawOutofLeftRange, R.string.detect_head_right);
//        FaceEnvironment.setTipsId(FaceStatusNewEnum.DetectRemindCodeYawOutofRightRange, R.string.detect_head_left);
//        FaceEnvironment.setTipsId(FaceStatusNewEnum.DetectRemindCodeTooFar, R.string.detect_zoom_in);
//        FaceEnvironment.setTipsId(FaceStatusNewEnum.DetectRemindCodeTooClose, R.string.detect_zoom_out);
//        FaceEnvironment.setTipsId(FaceStatusNewEnum.DetectRemindCodeLeftEyeClosed, R.string.detect_left_eye_close);
//        FaceEnvironment.setTipsId(FaceStatusNewEnum.DetectRemindCodeRightEyeClosed, R.string.detect_right_eye_close);
//
//        FaceEnvironment.setTipsId(FaceStatusNewEnum.FaceLivenessActionTypeLiveEye, R.string.liveness_eye);
//        FaceEnvironment.setTipsId(FaceStatusNewEnum.FaceLivenessActionTypeLiveMouth, R.string.liveness_mouth);
//        FaceEnvironment.setTipsId(FaceStatusNewEnum.FaceLivenessActionTypeLivePitchUp, R.string.liveness_head_up);
//        FaceEnvironment.setTipsId(FaceStatusNewEnum.FaceLivenessActionTypeLivePitchDown, R.string.liveness_head_down);
//        FaceEnvironment.setTipsId(FaceStatusNewEnum.FaceLivenessActionTypeLiveYawLeft, R.string.liveness_head_left);
//        FaceEnvironment.setTipsId(FaceStatusNewEnum.FaceLivenessActionTypeLiveYawRight, R.string.liveness_head_right);
//        FaceEnvironment.setTipsId(FaceStatusNewEnum.FaceLivenessActionComplete, R.string.liveness_good);
//        FaceEnvironment.setTipsId(FaceStatusNewEnum.OK, R.string.liveness_good);
//
//        FaceEnvironment.setTipsId(FaceStatusNewEnum.DetectRemindCodeTimeout, R.string.detect_timeout);
//
//        RES_TIPS_DEFAULT = R.string.detect_face_in;
//        RES_TIPS_FACE_AHEAD = R.string.detect_face_with_ahead;
//        RES_TIPS_DIALOG_TIMEOUT_TITLE = R.string.dialog_timeout_msg;
//        RES_TIPS_DIALOG_TIMEOUT_RETRY = R.string.dialog_timeout_btn_retry;
//        RES_TIPS_DIALOG_TIMEOUT_CANCEL = R.string.dialog_timeout_btn_back;
//    }
//
//    public static void setResEn() {
//        // Sound Res Id
//        FaceEnvironment.setSoundId(FaceStatusNewEnum.DetectRemindCodeNoFaceDetected, R.raw.detect_face_in_en);
//        FaceEnvironment.setSoundId(FaceStatusNewEnum.DetectRemindCodeBeyondPreviewFrame, R.raw.detect_face_in_en);
//        FaceEnvironment.setSoundId(FaceStatusNewEnum.DetectRemindCodeNoFaceDetected, R.raw.detect_face_in_en);
//        FaceEnvironment.setSoundId(FaceStatusNewEnum.FaceLivenessActionTypeLiveEye, R.raw.liveness_eye_en);
//        FaceEnvironment.setSoundId(FaceStatusNewEnum.FaceLivenessActionTypeLiveMouth, R.raw.liveness_mouth_en);
//        FaceEnvironment.setSoundId(FaceStatusNewEnum.FaceLivenessActionTypeLivePitchUp, R.raw.liveness_head_up_en);
//        FaceEnvironment.setSoundId(FaceStatusNewEnum.FaceLivenessActionTypeLivePitchDown, R.raw.liveness_head_down_en);
//        FaceEnvironment.setSoundId(FaceStatusNewEnum.FaceLivenessActionTypeLiveYawLeft, R.raw.liveness_head_left_en);
//        FaceEnvironment.setSoundId(FaceStatusNewEnum.FaceLivenessActionTypeLiveYawRight, R.raw.liveness_head_right_en);
//        FaceEnvironment.setSoundId(FaceStatusNewEnum.FaceLivenessActionComplete, R.raw.face_good_en);
//        FaceEnvironment.setSoundId(FaceStatusNewEnum.OK, R.raw.face_good_en);
//
//        // Tips Res Id
//        FaceEnvironment.setTipsId(FaceStatusNewEnum.DetectRemindCodeNoFaceDetected, R.string.detect_face_in_en);
//        FaceEnvironment.setTipsId(FaceStatusNewEnum.DetectRemindCodeBeyondPreviewFrame, R.string.detect_face_in_en);
//        FaceEnvironment.setTipsId(FaceStatusNewEnum.DetectRemindCodePoorIllumination, R.string.detect_low_light_en);
//        FaceEnvironment.setTipsId(FaceStatusNewEnum.DetectRemindCodeImageBlured, R.string.detect_keep_en);
//        FaceEnvironment.setTipsId(FaceStatusNewEnum.DetectRemindCodeOcclusionLeftEye, R.string.detect_occ_left_eye_en);
//        FaceEnvironment.setTipsId(FaceStatusNewEnum.DetectRemindCodeOcclusionRightEye, R.string.detect_occ_right_eye_en);
//        FaceEnvironment.setTipsId(FaceStatusNewEnum.DetectRemindCodeOcclusionNose, R.string.detect_occ_nose_en);
//        FaceEnvironment.setTipsId(FaceStatusNewEnum.DetectRemindCodeOcclusionMouth, R.string.detect_occ_mouth_en);
//        FaceEnvironment.setTipsId(FaceStatusNewEnum.DetectRemindCodeOcclusionLeftContour, R.string.detect_occ_left_check_en);
//        FaceEnvironment.setTipsId(FaceStatusNewEnum.DetectRemindCodeOcclusionRightContour, R.string.detect_occ_right_check_en);
//        FaceEnvironment.setTipsId(FaceStatusNewEnum.DetectRemindCodeOcclusionChinContour, R.string.detect_occ_chin_en);
//        FaceEnvironment.setTipsId(FaceStatusNewEnum.DetectRemindCodePitchOutofUpRange, R.string.detect_head_down_en);
//        FaceEnvironment.setTipsId(FaceStatusNewEnum.DetectRemindCodePitchOutofDownRange, R.string.detect_head_up_en);
//        FaceEnvironment.setTipsId(FaceStatusNewEnum.DetectRemindCodeYawOutofLeftRange, R.string.detect_head_right_en);
//        FaceEnvironment.setTipsId(FaceStatusNewEnum.DetectRemindCodeYawOutofRightRange, R.string.detect_head_left_en);
//        FaceEnvironment.setTipsId(FaceStatusNewEnum.DetectRemindCodeTooFar, R.string.detect_zoom_in_en);
//        FaceEnvironment.setTipsId(FaceStatusNewEnum.DetectRemindCodeTooClose, R.string.detect_zoom_out_en);
//        FaceEnvironment.setTipsId(FaceStatusNewEnum.DetectRemindCodeLeftEyeClosed, R.string.detect_left_eye_close_en);
//        FaceEnvironment.setTipsId(FaceStatusNewEnum.DetectRemindCodeRightEyeClosed, R.string.detect_right_eye_close_en);
//
//        FaceEnvironment.setTipsId(FaceStatusNewEnum.FaceLivenessActionTypeLiveEye, R.string.liveness_eye_en);
//        FaceEnvironment.setTipsId(FaceStatusNewEnum.FaceLivenessActionTypeLiveMouth, R.string.liveness_mouth_en);
//        FaceEnvironment.setTipsId(FaceStatusNewEnum.FaceLivenessActionTypeLivePitchUp, R.string.liveness_head_up_en);
//        FaceEnvironment.setTipsId(FaceStatusNewEnum.FaceLivenessActionTypeLivePitchDown, R.string.liveness_head_down_en);
//        FaceEnvironment.setTipsId(FaceStatusNewEnum.FaceLivenessActionTypeLiveYawLeft, R.string.liveness_head_left_en);
//        FaceEnvironment.setTipsId(FaceStatusNewEnum.FaceLivenessActionTypeLiveYawRight, R.string.liveness_head_right_en);
//        FaceEnvironment.setTipsId(FaceStatusNewEnum.FaceLivenessActionComplete, R.string.liveness_good_en);
//        FaceEnvironment.setTipsId(FaceStatusNewEnum.OK, R.string.liveness_good_en);
//
//        FaceEnvironment.setTipsId(FaceStatusNewEnum.DetectRemindCodeTimeout, R.string.detect_timeout_en);
//
//        RES_TIPS_DEFAULT = R.string.detect_face_in_en;
//        RES_TIPS_FACE_AHEAD = R.string.detect_face_with_ahead_en;
//        RES_TIPS_DIALOG_TIMEOUT_TITLE = R.string.dialog_timeout_msg_en;
//        RES_TIPS_DIALOG_TIMEOUT_RETRY = R.string.dialog_timeout_btn_retry_en;
//        RES_TIPS_DIALOG_TIMEOUT_CANCEL = R.string.dialog_timeout_btn_back_en;
//    }
}