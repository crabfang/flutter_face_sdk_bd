package com.cabe.lib.face.sdk.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextUtils;
import android.util.AttributeSet;

import com.baidu.idl.face.platform.ui.widget.FaceDetectRoundView;
import com.cabe.lib.face.sdk.BDFaceSDK;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class FaceDetectRoundViewPro extends FaceDetectRoundView {
    public static void setViewStyle() {
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
    }
    public FaceDetectRoundViewPro(Context context, AttributeSet attrs) {
        super(context, attrs);
        getFieldPaint("mTextTopPaint").setColor(Color.parseColor("#FFFF9500"));
        getFieldPaint("mTextSecondPaint").setColor(Color.parseColor("#FF878787"));
    }

    public Paint getTopPaint() {
        return getFieldPaint("mTextTopPaint");
    }

    public Paint getSecondPaint() {
        return getFieldPaint("mTextSecondPaint");
    }

    private Object invokeSuperMethod(String methodName, Object...params) {
        Object result = null;
        try {
            Class<?>[] paramsClass = null;
            if(params != null) {
                paramsClass = new Class<?>[params.length];
                for(int i=0;i<params.length;i++) {
                    paramsClass[i] = params[i].getClass();
                }
            }
            Method method = getClass().getSuperclass().getDeclaredMethod(methodName, paramsClass);
            method.setAccessible(true);
            result = method.invoke(this, params);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    
    private Object getSuperField(String fieldName) {
        Object superField = null;
        try {
            Field field = getClass().getSuperclass().getDeclaredField(fieldName);
            //设置对象的访问权限，保证对private的属性的访问
            field.setAccessible(true);
            superField = field.get(this);
//            superField = fieldClass.getClass();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return superField;
    }

    public String getFieldString(String fieldName) {
        String value = "";
        try {
            value = (String) getSuperField(fieldName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }

    public float getFieldFloat(String fieldName) {
        float value = 0.0f;
        try {
            value = (float) getSuperField(fieldName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }
    
    public boolean getFieldBoolean(String fieldName) {
        boolean value = false;
        try {
            value = (boolean) getSuperField(fieldName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }

    public Paint getFieldPaint(String fieldName) {
        Paint value = null;
        try {
            value = (Paint) getSuperField(fieldName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }

    @Override
    public void onDraw(Canvas canvas) {
//        super.onDraw(canvas);
        canvas.drawPaint(getFieldPaint("mBGPaint"));
        canvas.drawCircle(
                getFieldFloat("mX"),
                getFieldFloat("mY"),
                getFieldFloat("mR"),
                getFieldPaint("mFaceRoundPaint")
        );
        
        // 画文字
        if (!TextUtils.isEmpty(getFieldString("mTipSecondText"))) {
            canvas.drawText(
                    getFieldString("mTipSecondText"), 
                    getFieldFloat("mX"),
                    getFieldFloat("mY") - getFieldFloat("mR") - 40 - 25 - 59, 
                    getFieldPaint("mTextSecondPaint")
            );
        }

        if (!TextUtils.isEmpty(getFieldString("mTipTopText"))) {
            canvas.drawText(
                    getFieldString("mTipTopText"), 
                    getFieldFloat("mX"), 
                    getFieldFloat("mY") - getFieldFloat("mR") - 40 - 25 - 59 - 90, 
                    getFieldPaint("mTextTopPaint")
            );
        }
        
        if (getFieldBoolean("mIsActiveLive")) {
            canvas.translate(getFieldFloat("mX"), getFieldFloat("mY"));
            // 画默认进度
//            drawCircleLine(canvas);
            invokeSuperMethod("drawCircleLine", canvas);
            // 画成功进度
//            drawSuccessCircleLine(canvas);
            invokeSuperMethod("drawSuccessCircleLine", canvas);
        }
    }
}
