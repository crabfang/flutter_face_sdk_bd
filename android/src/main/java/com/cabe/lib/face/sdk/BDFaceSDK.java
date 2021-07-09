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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BDFaceSDK {
    public static int RES_TIPS_DEFAULT = R.string.detect_face_in;
    public static int RES_TIPS_FACE_AHEAD = R.string.detect_face_with_ahead;
    public static int RES_TIPS_DIALOG_TIMEOUT_TITLE = R.string.dialog_timeout_msg;
    public static int RES_TIPS_DIALOG_TIMEOUT_RETRY = R.string.dialog_timeout_btn_retry;
    public static int RES_TIPS_DIALOG_TIMEOUT_CANCEL = R.string.dialog_timeout_btn_back;
    private static final List<LivenessTypeEnum> livenessList = new ArrayList<>();
    static {
        livenessList.add(LivenessTypeEnum.Eye);
        livenessList.add(LivenessTypeEnum.Mouth);
        livenessList.add(LivenessTypeEnum.HeadRight);
        livenessList.add(LivenessTypeEnum.HeadLeft);
        livenessList.add(LivenessTypeEnum.HeadUp);
        livenessList.add(LivenessTypeEnum.HeadDown);
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
            if(callback != null) callback.onResult(-1, "初始化失败 = json配置文件解析出错");
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
        // 为了android和ios 区分授权，appId=appname_face_android ,其中appname为申请sdk时的应用名
        // 应用上下文
        // 申请License取得的APPID
        // assets目录下License文件名
        FaceSDKManager.getInstance().initialize(context, licenseID, licenseFileName, new IInitCallback() {
                    @Override
                    public void initSuccess() {
                        if(callback != null) callback.onResult(0, "初始化成功");
                    }
                    @Override
                    public void initFailure(final int errCode, final String errMsg) {
                        if(callback != null) callback.onResult(errCode, errMsg);
                    }
                });
    }

    private static boolean setFaceConfig(Context context, int qualityLevel, boolean isOpenSound) {
        FaceConfig config = FaceSDKManager.getInstance().getFaceConfig();
        // 根据质量等级获取相应的质量值（注：第二个参数要与质量等级的set方法参数一致）
        QualityConfigManager manager = QualityConfigManager.getInstance();
        manager.readQualityFile(context.getApplicationContext(), qualityLevel);
        QualityConfig qualityConfig = manager.getConfig();
        if (qualityConfig == null) {
            return false;
        }
        // 设置模糊度阈值
        config.setBlurnessValue(qualityConfig.getBlur());
        // 设置最小光照阈值（范围0-255）
        config.setBrightnessValue(qualityConfig.getMinIllum());
        // 设置最大光照阈值（范围0-255）
        config.setBrightnessMaxValue(qualityConfig.getMaxIllum());
        // 设置左眼遮挡阈值
        config.setOcclusionLeftEyeValue(qualityConfig.getLeftEyeOcclusion());
        // 设置右眼遮挡阈值
        config.setOcclusionRightEyeValue(qualityConfig.getRightEyeOcclusion());
        // 设置鼻子遮挡阈值
        config.setOcclusionNoseValue(qualityConfig.getNoseOcclusion());
        // 设置嘴巴遮挡阈值
        config.setOcclusionMouthValue(qualityConfig.getMouseOcclusion());
        // 设置左脸颊遮挡阈值
        config.setOcclusionLeftContourValue(qualityConfig.getLeftContourOcclusion());
        // 设置右脸颊遮挡阈值
        config.setOcclusionRightContourValue(qualityConfig.getRightContourOcclusion());
        // 设置下巴遮挡阈值
        config.setOcclusionChinValue(qualityConfig.getChinOcclusion());
        // 设置人脸姿态角阈值
        config.setHeadPitchValue(qualityConfig.getPitch());
        config.setHeadYawValue(qualityConfig.getYaw());
        config.setHeadRollValue(qualityConfig.getRoll());
        // 设置可检测的最小人脸阈值
        config.setMinFaceSize(FaceEnvironment.VALUE_MIN_FACE_SIZE);
        // 设置可检测到人脸的阈值
        config.setNotFaceValue(FaceEnvironment.VALUE_NOT_FACE_THRESHOLD);
        // 设置闭眼阈值
        config.setEyeClosedValue(FaceEnvironment.VALUE_CLOSE_EYES);
        // 设置图片缓存数量
        config.setCacheImageNum(FaceEnvironment.VALUE_CACHE_IMAGE_NUM);
        // 设置活体动作，通过设置list，LivenessTypeEunm.Eye, LivenessTypeEunm.Mouth,
        // LivenessTypeEunm.HeadUp, LivenessTypeEunm.HeadDown, LivenessTypeEunm.HeadLeft,
        // LivenessTypeEunm.HeadRight
        config.setLivenessTypeList(livenessList);
        // 设置动作活体是否随机
        config.setLivenessRandom(true);
        // 设置开启提示音
        config.setSound(isOpenSound);
        // 原图缩放系数
        config.setScale(FaceEnvironment.VALUE_SCALE);
        // 抠图宽高的设定，为了保证好的抠图效果，建议高宽比是4：3
        config.setCropHeight(FaceEnvironment.VALUE_CROP_HEIGHT);
        config.setCropWidth(FaceEnvironment.VALUE_CROP_WIDTH);
        // 抠图人脸框与背景比例
        config.setEnlargeRatio(FaceEnvironment.VALUE_CROP_ENLARGERATIO);
        // 加密类型，0：Base64加密，上传时image_sec传false；1：百度加密文件加密，上传时image_sec传true
        config.setSecType(FaceEnvironment.VALUE_SEC_TYPE);
        // 检测超时设置
        config.setTimeDetectModule(FaceEnvironment.TIME_DETECT_MODULE);
        // 检测框远近比率
        config.setFaceFarRatio(FaceEnvironment.VALUE_FAR_RATIO);
        config.setFaceClosedRatio(FaceEnvironment.VALUE_CLOSED_RATIO);
        FaceSDKManager.getInstance().setFaceConfig(config);
        return true;
    }

    public static void changeStaticFinal(Field field, Object newValue) throws Exception {
        field.setAccessible(true); // 如果field为private,则需要使用该方法使其可被访问
        field.set(null, newValue); // 为指定field设置新值
    }

    public static void setRoundPaintColor(Object clazz, String fieldName, int newValue) {
        try {
            Field field = clazz.getClass().getDeclaredField(fieldName);
            //设置对象的访问权限，保证对private的属性的访问
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
     * 添加到销毁队列
     * @param activity 要销毁的activity
     */
    public static void addDestroyActivity(Activity activity, String activityName) {
        destroyMap.put(activityName, activity);
    }

    /**
     * 销毁指定Activity
     */
    public static void destroyActivity(String activityName) {
        Set<String> keySet = destroyMap.keySet();
        for (String key : keySet) {
            destroyMap.get(key).finish();
        }
    }
    
    public static void setResNormal() {
        // Sound Res Id
        FaceEnvironment.setSoundId(FaceStatusNewEnum.DetectRemindCodeNoFaceDetected,
                R.raw.detect_face_in);
        FaceEnvironment.setSoundId(FaceStatusNewEnum.DetectRemindCodeBeyondPreviewFrame,
                R.raw.detect_face_in);
        FaceEnvironment.setSoundId(FaceStatusNewEnum.DetectRemindCodeNoFaceDetected,
                R.raw.detect_face_in);
        FaceEnvironment.setSoundId(FaceStatusNewEnum.FaceLivenessActionTypeLiveEye,
                R.raw.liveness_eye);
        FaceEnvironment.setSoundId(FaceStatusNewEnum.FaceLivenessActionTypeLiveMouth,
                R.raw.liveness_mouth);
        FaceEnvironment.setSoundId(FaceStatusNewEnum.FaceLivenessActionTypeLivePitchUp,
                R.raw.liveness_head_up);
        FaceEnvironment.setSoundId(FaceStatusNewEnum.FaceLivenessActionTypeLivePitchDown,
                R.raw.liveness_head_down);
        FaceEnvironment.setSoundId(FaceStatusNewEnum.FaceLivenessActionTypeLiveYawLeft,
                R.raw.liveness_head_left);
        FaceEnvironment.setSoundId(FaceStatusNewEnum.FaceLivenessActionTypeLiveYawRight,
                R.raw.liveness_head_right);
        FaceEnvironment.setSoundId(FaceStatusNewEnum.FaceLivenessActionComplete,
                R.raw.face_good);
        FaceEnvironment.setSoundId(FaceStatusNewEnum.OK, R.raw.face_good);

        // Tips Res Id
        FaceEnvironment.setTipsId(FaceStatusNewEnum.DetectRemindCodeNoFaceDetected,
                R.string.detect_face_in);
        FaceEnvironment.setTipsId(FaceStatusNewEnum.DetectRemindCodeBeyondPreviewFrame,
                R.string.detect_face_in);
        FaceEnvironment.setTipsId(FaceStatusNewEnum.DetectRemindCodePoorIllumination,
                R.string.detect_low_light);
        FaceEnvironment.setTipsId(FaceStatusNewEnum.DetectRemindCodeImageBlured,
                R.string.detect_keep);
        FaceEnvironment.setTipsId(FaceStatusNewEnum.DetectRemindCodeOcclusionLeftEye,
                R.string.detect_occ_left_eye);
        FaceEnvironment.setTipsId(FaceStatusNewEnum.DetectRemindCodeOcclusionRightEye,
                R.string.detect_occ_right_eye);
        FaceEnvironment.setTipsId(FaceStatusNewEnum.DetectRemindCodeOcclusionNose,
                R.string.detect_occ_nose);
        FaceEnvironment.setTipsId(FaceStatusNewEnum.DetectRemindCodeOcclusionMouth,
                R.string.detect_occ_mouth);
        FaceEnvironment.setTipsId(FaceStatusNewEnum.DetectRemindCodeOcclusionLeftContour,
                R.string.detect_occ_left_check);
        FaceEnvironment.setTipsId(FaceStatusNewEnum.DetectRemindCodeOcclusionRightContour,
                R.string.detect_occ_right_check);
        FaceEnvironment.setTipsId(FaceStatusNewEnum.DetectRemindCodeOcclusionChinContour,
                R.string.detect_occ_chin);
        FaceEnvironment.setTipsId(FaceStatusNewEnum.DetectRemindCodePitchOutofUpRange,
                R.string.detect_head_down);
        FaceEnvironment.setTipsId(FaceStatusNewEnum.DetectRemindCodePitchOutofDownRange,
                R.string.detect_head_up);
        FaceEnvironment.setTipsId(FaceStatusNewEnum.DetectRemindCodeYawOutofLeftRange,
                R.string.detect_head_right);
        FaceEnvironment.setTipsId(FaceStatusNewEnum.DetectRemindCodeYawOutofRightRange,
                R.string.detect_head_left);
        FaceEnvironment.setTipsId(FaceStatusNewEnum.DetectRemindCodeTooFar,
                R.string.detect_zoom_in);
        FaceEnvironment.setTipsId(FaceStatusNewEnum.DetectRemindCodeTooClose,
                R.string.detect_zoom_out);
        FaceEnvironment.setTipsId(FaceStatusNewEnum.DetectRemindCodeLeftEyeClosed,
                R.string.detect_left_eye_close);
        FaceEnvironment.setTipsId(FaceStatusNewEnum.DetectRemindCodeRightEyeClosed,
                R.string.detect_right_eye_close);

        FaceEnvironment.setTipsId(FaceStatusNewEnum.FaceLivenessActionTypeLiveEye,
                R.string.liveness_eye);
        FaceEnvironment.setTipsId(FaceStatusNewEnum.FaceLivenessActionTypeLiveMouth,
                R.string.liveness_mouth);
        FaceEnvironment.setTipsId(FaceStatusNewEnum.FaceLivenessActionTypeLivePitchUp,
                R.string.liveness_head_up);
        FaceEnvironment.setTipsId(FaceStatusNewEnum.FaceLivenessActionTypeLivePitchDown,
                R.string.liveness_head_down);
        FaceEnvironment.setTipsId(FaceStatusNewEnum.FaceLivenessActionTypeLiveYawLeft,
                R.string.liveness_head_left);
        FaceEnvironment.setTipsId(FaceStatusNewEnum.FaceLivenessActionTypeLiveYawRight,
                R.string.liveness_head_right);
        FaceEnvironment.setTipsId(FaceStatusNewEnum.FaceLivenessActionComplete,
                R.string.liveness_good);
        FaceEnvironment.setTipsId(FaceStatusNewEnum.OK, R.string.liveness_good);

        FaceEnvironment.setTipsId(FaceStatusNewEnum.DetectRemindCodeTimeout,
                R.string.detect_timeout);

        RES_TIPS_DEFAULT = R.string.detect_face_in;
        RES_TIPS_FACE_AHEAD = R.string.detect_face_with_ahead;
        RES_TIPS_DIALOG_TIMEOUT_TITLE = R.string.dialog_timeout_msg;
        RES_TIPS_DIALOG_TIMEOUT_RETRY = R.string.dialog_timeout_btn_retry;
        RES_TIPS_DIALOG_TIMEOUT_CANCEL = R.string.dialog_timeout_btn_back;
    }

    public static void setResEn() {
        // Sound Res Id
        FaceEnvironment.setSoundId(FaceStatusNewEnum.DetectRemindCodeNoFaceDetected,
                R.raw.detect_face_in_en);
        FaceEnvironment.setSoundId(FaceStatusNewEnum.DetectRemindCodeBeyondPreviewFrame,
                R.raw.detect_face_in_en);
        FaceEnvironment.setSoundId(FaceStatusNewEnum.DetectRemindCodeNoFaceDetected,
                R.raw.detect_face_in_en);
        FaceEnvironment.setSoundId(FaceStatusNewEnum.FaceLivenessActionTypeLiveEye,
                R.raw.liveness_eye_en);
        FaceEnvironment.setSoundId(FaceStatusNewEnum.FaceLivenessActionTypeLiveMouth,
                R.raw.liveness_mouth_en);
        FaceEnvironment.setSoundId(FaceStatusNewEnum.FaceLivenessActionTypeLivePitchUp,
                R.raw.liveness_head_up_en);
        FaceEnvironment.setSoundId(FaceStatusNewEnum.FaceLivenessActionTypeLivePitchDown,
                R.raw.liveness_head_down_en);
        FaceEnvironment.setSoundId(FaceStatusNewEnum.FaceLivenessActionTypeLiveYawLeft,
                R.raw.liveness_head_left_en);
        FaceEnvironment.setSoundId(FaceStatusNewEnum.FaceLivenessActionTypeLiveYawRight,
                R.raw.liveness_head_right_en);
        FaceEnvironment.setSoundId(FaceStatusNewEnum.FaceLivenessActionComplete,
                R.raw.face_good_en);
        FaceEnvironment.setSoundId(FaceStatusNewEnum.OK, R.raw.face_good_en);

        // Tips Res Id
        FaceEnvironment.setTipsId(FaceStatusNewEnum.DetectRemindCodeNoFaceDetected,
                R.string.detect_face_in_en);
        FaceEnvironment.setTipsId(FaceStatusNewEnum.DetectRemindCodeBeyondPreviewFrame,
                R.string.detect_face_in_en);
        FaceEnvironment.setTipsId(FaceStatusNewEnum.DetectRemindCodePoorIllumination,
                R.string.detect_low_light_en);
        FaceEnvironment.setTipsId(FaceStatusNewEnum.DetectRemindCodeImageBlured,
                R.string.detect_keep_en);
        FaceEnvironment.setTipsId(FaceStatusNewEnum.DetectRemindCodeOcclusionLeftEye,
                R.string.detect_occ_left_eye_en);
        FaceEnvironment.setTipsId(FaceStatusNewEnum.DetectRemindCodeOcclusionRightEye,
                R.string.detect_occ_right_eye_en);
        FaceEnvironment.setTipsId(FaceStatusNewEnum.DetectRemindCodeOcclusionNose,
                R.string.detect_occ_nose_en);
        FaceEnvironment.setTipsId(FaceStatusNewEnum.DetectRemindCodeOcclusionMouth,
                R.string.detect_occ_mouth_en);
        FaceEnvironment.setTipsId(FaceStatusNewEnum.DetectRemindCodeOcclusionLeftContour,
                R.string.detect_occ_left_check_en);
        FaceEnvironment.setTipsId(FaceStatusNewEnum.DetectRemindCodeOcclusionRightContour,
                R.string.detect_occ_right_check_en);
        FaceEnvironment.setTipsId(FaceStatusNewEnum.DetectRemindCodeOcclusionChinContour,
                R.string.detect_occ_chin_en);
        FaceEnvironment.setTipsId(FaceStatusNewEnum.DetectRemindCodePitchOutofUpRange,
                R.string.detect_head_down_en);
        FaceEnvironment.setTipsId(FaceStatusNewEnum.DetectRemindCodePitchOutofDownRange,
                R.string.detect_head_up_en);
        FaceEnvironment.setTipsId(FaceStatusNewEnum.DetectRemindCodeYawOutofLeftRange,
                R.string.detect_head_right_en);
        FaceEnvironment.setTipsId(FaceStatusNewEnum.DetectRemindCodeYawOutofRightRange,
                R.string.detect_head_left_en);
        FaceEnvironment.setTipsId(FaceStatusNewEnum.DetectRemindCodeTooFar,
                R.string.detect_zoom_in_en);
        FaceEnvironment.setTipsId(FaceStatusNewEnum.DetectRemindCodeTooClose,
                R.string.detect_zoom_out_en);
        FaceEnvironment.setTipsId(FaceStatusNewEnum.DetectRemindCodeLeftEyeClosed,
                R.string.detect_left_eye_close_en);
        FaceEnvironment.setTipsId(FaceStatusNewEnum.DetectRemindCodeRightEyeClosed,
                R.string.detect_right_eye_close_en);

        FaceEnvironment.setTipsId(FaceStatusNewEnum.FaceLivenessActionTypeLiveEye,
                R.string.liveness_eye_en);
        FaceEnvironment.setTipsId(FaceStatusNewEnum.FaceLivenessActionTypeLiveMouth,
                R.string.liveness_mouth_en);
        FaceEnvironment.setTipsId(FaceStatusNewEnum.FaceLivenessActionTypeLivePitchUp,
                R.string.liveness_head_up_en);
        FaceEnvironment.setTipsId(FaceStatusNewEnum.FaceLivenessActionTypeLivePitchDown,
                R.string.liveness_head_down_en);
        FaceEnvironment.setTipsId(FaceStatusNewEnum.FaceLivenessActionTypeLiveYawLeft,
                R.string.liveness_head_left_en);
        FaceEnvironment.setTipsId(FaceStatusNewEnum.FaceLivenessActionTypeLiveYawRight,
                R.string.liveness_head_right_en);
        FaceEnvironment.setTipsId(FaceStatusNewEnum.FaceLivenessActionComplete,
                R.string.liveness_good_en);
        FaceEnvironment.setTipsId(FaceStatusNewEnum.OK, R.string.liveness_good_en);

        FaceEnvironment.setTipsId(FaceStatusNewEnum.DetectRemindCodeTimeout,
                R.string.detect_timeout_en);
        
        RES_TIPS_DEFAULT = R.string.detect_face_in_en;
        RES_TIPS_FACE_AHEAD = R.string.detect_face_with_ahead_en;
        RES_TIPS_DIALOG_TIMEOUT_TITLE = R.string.dialog_timeout_msg_en;
        RES_TIPS_DIALOG_TIMEOUT_RETRY = R.string.dialog_timeout_btn_retry_en;
        RES_TIPS_DIALOG_TIMEOUT_CANCEL = R.string.dialog_timeout_btn_back_en;
    }
}