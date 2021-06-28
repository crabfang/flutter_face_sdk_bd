package com.cabe.lib.face.sdk.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewParent;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.graphics.drawable.DrawableCompat;

import com.baidu.idl.face.platform.FaceStatusNewEnum;
import com.baidu.idl.face.platform.model.ImageInfo;
import com.baidu.idl.face.platform.ui.FaceDetectActivity;
import com.cabe.flutter.plugin.widget_face_sdk.WidgetFaceSdkPlugin;
import com.cabe.lib.face.sdk.BDFaceSDK;
import com.cabe.lib.face.sdk.widget.TimeoutDialog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FaceDetectExpActivity extends FaceDetectActivity implements TimeoutDialog.OnTimeoutDialogClickListener {
    private TimeoutDialog mTimeoutDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        WidgetFaceSdkPlugin.switchLanguage(this);
        super.onCreate(savedInstanceState);
        // 添加至销毁列表
        BDFaceSDK.addDestroyActivity(FaceDetectExpActivity.this, "FaceDetectExpActivity");

        ViewParent parent = mFaceDetectRoundView.getParent();
        if(parent instanceof RelativeLayout) {
            final RelativeLayout container = (RelativeLayout) parent;

            for(int i=0;i<container.getChildCount();i++) {
                View child = container.getChildAt(i);
                if(child instanceof TextView) {
                    TextView label = ((TextView) child);
                    String str = label.getText().toString();
                    if(str.contains("百度")) {
                        label.setVisibility(View.GONE);
                    }
                } else if(child instanceof ImageView) {
                    ImageView img = ((ImageView) child);
                    if(img.getId() == -1) {
                        img.setVisibility(View.GONE);
                    }
                }
            }

            DrawableCompat.setTint(mCloseView.getDrawable(), Color.parseColor("#FF878787"));
            DrawableCompat.setTint(mSoundView.getDrawable(), Color.parseColor("#FF878787"));
            BDFaceSDK.setRoundPaintColor(mFaceDetectRoundView, "mTextTopPaint", Color.parseColor("#FFFF9500"));
            BDFaceSDK.setRoundPaintColor(mFaceDetectRoundView, "mTextSecondPaint", Color.parseColor("#FF878787"));
        }
    }

    @Override
    public void onDetectCompletion(FaceStatusNewEnum status, String message,
                                   HashMap<String, ImageInfo> base64ImageCropMap,
                                   HashMap<String, ImageInfo> base64ImageSrcMap) {
        super.onDetectCompletion(status, message, base64ImageCropMap, base64ImageSrcMap);
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
        BDFaceSDK.destroyActivity("FaceDetectExpActivity");

        // 页面跳转
//        IntentUtils.getInstance().setBitmap(bmpStr);
//        Intent intent = new Intent(FaceDetectExpActivity.this,
//                CollectionSuccessActivity.class);
//        intent.putExtra("destroyType", "FaceDetectExpActivity");
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
