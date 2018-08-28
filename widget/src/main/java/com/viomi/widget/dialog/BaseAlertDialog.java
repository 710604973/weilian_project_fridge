package com.viomi.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.viomi.widget.R;

/**
 * 全局 Dialog
 * Created by William on 2018/1/26.
 */
public class BaseAlertDialog extends Dialog {
    private String message, leftText, rightText;
    private OnLeftClickListener onLeftClickListener;
    private OnRightClickListener onRightClickListener;

    public BaseAlertDialog(@NonNull Context context, String message, String leftText, String rightText) {
        super(context);
        this.message = message;
        this.leftText = leftText;
        this.rightText = rightText;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_base_alert);
        Window window = this.getWindow();
        if (window != null) {
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(lp);
            window.setGravity(Gravity.CENTER);
        }
        setCanceledOnTouchOutside(false);

        TextView tvMessage = (TextView) findViewById(R.id.alert_dialog_text);
        tvMessage.setText(message);
        TextView tvLeft =(TextView) findViewById(R.id.alert_dialog_left);
        tvLeft.setText(leftText);
        TextView tvRight =(TextView) findViewById(R.id.alert_dialog_right);
        tvRight.setText(rightText);

        tvLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onLeftClickListener != null) onLeftClickListener.onClick();
            }
        });

        tvRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onRightClickListener != null) onRightClickListener.onClick();
            }
        });
    }

    public void setOnLeftClickListener(OnLeftClickListener onLeftClickListener) {
        this.onLeftClickListener = onLeftClickListener;
    }

    public void setOnRightClickListener(OnRightClickListener onRightClickListener) {
        this.onRightClickListener = onRightClickListener;
    }

    public interface OnLeftClickListener {
        void onClick();
    }

    public interface OnRightClickListener {
        void onClick();
    }
}