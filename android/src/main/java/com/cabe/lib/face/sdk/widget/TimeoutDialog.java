package com.cabe.lib.face.sdk.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.baidu.idl.face.platform.utils.DensityUtils;
import com.cabe.flutter.plugin.widget_face_sdk.R;
import com.cabe.flutter.plugin.widget_face_sdk.WidgetFaceSdkPlugin;

/**
 * 超时弹窗
 * Created by v_liujialu01 on 2020/4/7.
 */

public class TimeoutDialog extends Dialog implements View.OnClickListener {
    private Context mContext;
    private OnTimeoutDialogClickListener mOnTimeoutDialogClickListener;

    public TimeoutDialog(@NonNull Context context) {
        super(context, R.style.DefaultDialog);
        mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    private void initView() {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.dialog_time_out, null);
        setContentView(view);
        Window dialogWindow = getWindow();
        dialogWindow.setGravity(Gravity.CENTER_HORIZONTAL);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        int widthPx = DensityUtils.getDisplayWidth(getContext());
        int dp = DensityUtils.px2dip(getContext(), widthPx) - 40;
        lp.width = DensityUtils.dip2px(getContext(), dp);
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialogWindow.setAttributes(lp);

        TextView title = (TextView) view.findViewById(R.id.text_title);
        int resTitle = R.string.dialog_timeout_msg;
        if("en".equals(WidgetFaceSdkPlugin.curLanguage)) {
            resTitle = R.string.dialog_timeout_msg_en;
        }
        title.setText(resTitle);

        Button btnDialogRecollect = (Button) view.findViewById(R.id.btn_dialog_recollect);
        int resRecollect = R.string.dialog_timeout_btn_retry;
        if("en".equals(WidgetFaceSdkPlugin.curLanguage)) {
            resRecollect = R.string.dialog_timeout_btn_retry_en;
        }
        btnDialogRecollect.setText(resRecollect);
        btnDialogRecollect.setOnClickListener(this);
        Button btnDialogReturn = (Button) view.findViewById(R.id.btn_dialog_return);
        int resReturn = R.string.dialog_timeout_btn_back;
        if("en".equals(WidgetFaceSdkPlugin.curLanguage)) {
            resReturn = R.string.dialog_timeout_btn_back_en;
        }
        btnDialogReturn.setText(resReturn);
        btnDialogReturn.setOnClickListener(this);
    }

    public void setDialogListener(OnTimeoutDialogClickListener listener) {
        mOnTimeoutDialogClickListener = listener;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_dialog_recollect) {
            if (mOnTimeoutDialogClickListener != null) {
                mOnTimeoutDialogClickListener.onRecollect();
            }
        } else if (id == R.id.btn_dialog_return) {
            if (mOnTimeoutDialogClickListener != null) {
                mOnTimeoutDialogClickListener.onReturn();
            }
        }
    }

    public interface OnTimeoutDialogClickListener {
        void onRecollect();
        void onReturn();
    }
}
