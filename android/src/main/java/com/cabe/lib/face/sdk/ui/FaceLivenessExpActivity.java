package com.cabe.lib.face.sdk.ui;

import android.os.Bundle;
import android.view.View;

import com.baidu.idl.face.platform.FaceStatusNewEnum;
import com.baidu.idl.face.platform.model.ImageInfo;
import com.baidu.idl.face.platform.ui.FaceLivenessActivity;
import com.baidu.idl.face.platform.utils.DensityUtils;
import com.cabe.flutter.plugin.widget_face_sdk.R;
import com.cabe.flutter.plugin.widget_face_sdk.WidgetFaceSdkPlugin;
import com.cabe.lib.face.sdk.BDFaceSDK;
import com.cabe.lib.face.sdk.widget.TimeoutDialog;
import com.cabe.lib.face.sdk.widget.FaceDetectRoundViewPro;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FaceLivenessExpActivity extends FaceLivenessActivity implements TimeoutDialog.OnTimeoutDialogClickListener {
    private TimeoutDialog mTimeoutDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WidgetFaceSdkPlugin.switchLanguage(this);
        // 添加至销毁列表
        BDFaceSDK.addDestroyActivity(FaceLivenessExpActivity.this, "FaceLivenessExpActivity");
        ((FaceDetectRoundViewPro)findViewById(R.id.liveness_face_round)).getTopPaint().setTextSize(DensityUtils.dip2px(this, BDFaceSDK.RES_FACE_TIP_SIZE_TOP));
        ((FaceDetectRoundViewPro)findViewById(R.id.liveness_face_round)).getSecondPaint().setTextSize(DensityUtils.dip2px(this, BDFaceSDK.RES_FACE_TIP_SIZE_SECOND));
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mFaceDetectRoundView != null) {
            mFaceDetectRoundView.setTipTopText(getResources().getString(BDFaceSDK.RES_TIPS_DEFAULT));
        }
    }

    @Override
    public void onLivenessCompletion(FaceStatusNewEnum status, String message,
                                     HashMap<String, ImageInfo> base64ImageCropMap,
                                     HashMap<String, ImageInfo> base64ImageSrcMap, int currentLivenessCount) {
        super.onLivenessCompletion(status, message, base64ImageCropMap, base64ImageSrcMap, currentLivenessCount);
        updateResultSpecial(status);
        if (status == FaceStatusNewEnum.OK && mIsCompletion) {
            // 获取最优图片
            getBestImage(base64ImageCropMap, base64ImageSrcMap);
        } else if (status == FaceStatusNewEnum.DetectRemindCodeTimeout) {
            if (mViewBg != null) {
                mViewBg.setVisibility(View.VISIBLE);
            }
            showMessageDialog();
        }
    }
    
    private void updateResultSpecial(FaceStatusNewEnum status) {
        if (status == FaceStatusNewEnum.DetectRemindCodeYawOutofRightRange) {
            mFaceDetectRoundView.setTipTopText(getResources().getString(BDFaceSDK.RES_TIPS_FACE_AHEAD));
        }
        switch (status) {
            case OK:
            case FaceLivenessActionComplete:
            case DetectRemindCodeTooClose:
            case DetectRemindCodeTooFar:
            case DetectRemindCodeBeyondPreviewFrame:
            case DetectRemindCodeNoFaceDetected:
            case FaceLivenessActionTypeLiveEye:
            case FaceLivenessActionTypeLiveMouth:
            case FaceLivenessActionTypeLivePitchUp:
            case FaceLivenessActionTypeLivePitchDown:
            case FaceLivenessActionTypeLiveYawLeft:
            case FaceLivenessActionTypeLiveYawRight:
            case FaceLivenessActionTypeLiveYaw:
            case FaceLivenessActionCodeTimeout:
                break;
            default:
                mFaceDetectRoundView.setTipTopText(getResources().getString(BDFaceSDK.RES_TIPS_FACE_AHEAD));
                break;
        }
    }

    /**
     * 获取最优图片
     * @param imageCropMap 抠图集合
     * @param imageSrcMap  原图集合
     */
    private void getBestImage(HashMap<String, ImageInfo> imageCropMap, HashMap<String, ImageInfo> imageSrcMap) {
        String bmpStr = null;
        // 将抠图集合中的图片按照质量降序排序，最终选取质量最优的一张抠图图片
        if (imageCropMap != null && imageCropMap.size() > 0) {
            List<Map.Entry<String, ImageInfo>> list1 = new ArrayList<>(imageCropMap.entrySet());
            Collections.sort(list1, new Comparator<Map.Entry<String, ImageInfo>>() {

                @Override
                public int compare(Map.Entry<String, ImageInfo> o1,
                                   Map.Entry<String, ImageInfo> o2) {
                    String[] key1 = o1.getKey().split("_");
                    String score1 = key1[2];
                    String[] key2 = o2.getKey().split("_");
                    String score2 = key2[2];
                    // 降序排序
                    return Float.valueOf(score2).compareTo(Float.valueOf(score1));
                }
            });
        }

        // 将原图集合中的图片按照质量降序排序，最终选取质量最优的一张原图图片
        if (imageSrcMap != null && imageSrcMap.size() > 0) {
            List<Map.Entry<String, ImageInfo>> list2 = new ArrayList<>(imageSrcMap.entrySet());
            Collections.sort(list2, new Comparator<Map.Entry<String, ImageInfo>>() {

                @Override
                public int compare(Map.Entry<String, ImageInfo> o1,
                                   Map.Entry<String, ImageInfo> o2) {
                    String[] key1 = o1.getKey().split("_");
                    String score1 = key1[2];
                    String[] key2 = o2.getKey().split("_");
                    String score2 = key2[2];
                    // 降序排序
                    return Float.valueOf(score2).compareTo(Float.valueOf(score1));
                }
            });
            bmpStr = list2.get(0).getValue().getBase64();
        }

        WidgetFaceSdkPlugin.verifySuccess(bmpStr);
        BDFaceSDK.destroyActivity("FaceLivenessExpActivity");

        // 页面跳转
//        IntentUtils.getInstance().setBitmap(bmpStr);
//        Intent intent = new Intent(FaceLivenessExpActivity.this, CollectionSuccessActivity.class);
//        intent.putExtra("destroyType", "FaceLivenessExpActivity");
//        startActivity(intent);
    }

    private void showMessageDialog() {
        mTimeoutDialog = new TimeoutDialog(this);
        mTimeoutDialog.setDialogListener(this);
        mTimeoutDialog.setCanceledOnTouchOutside(false);
        mTimeoutDialog.setCancelable(false);
        mTimeoutDialog.show();
        onPause();
    }

    @Override
    public void finish() {
        super.finish();
    }

    @Override
    public void onRecollect() {
        if (mTimeoutDialog != null) {
            mTimeoutDialog.dismiss();
        }
        if (mViewBg != null) {
            mViewBg.setVisibility(View.GONE);
        }
        onResume();
    }

    @Override
    public void onReturn() {
        WidgetFaceSdkPlugin.verifyCancel();
        if (mTimeoutDialog != null) {
            mTimeoutDialog.dismiss();
        }
        finish();
    }
}
