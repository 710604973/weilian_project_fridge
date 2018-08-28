package com.mode.fridge.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.mode.fridge.R;


/**
 * Created by nanquan on 2018/1/8.
 * 吐司工具类
 */
public class ToastUtil {
    private static Toast toast = null;
    private static Toast mToastCenter = null;
    private static Toast mToastDefined = null;
    private static TextView mTextView;

    @SuppressLint("ShowToast")
    public static void showCenter(Context context, String text) {
        if (mToastCenter == null) {
            mToastCenter = Toast.makeText(context, text, Toast.LENGTH_SHORT);
            mToastCenter.setGravity(Gravity.CENTER, 0, 0);
        } else {
            mToastCenter.setText(text);
        }
        mToastCenter.show();
    }

    @SuppressLint("ShowToast")
    public static void showDefinedCenter(Context context, String text) {
        if (mToastDefined == null) {
            View view = LayoutInflater.from(context).inflate(R.layout.toast_layout, null);
            mTextView = (TextView) view.findViewById(R.id.toast_text);
            mToastDefined = Toast.makeText(context, text, Toast.LENGTH_SHORT);
            mToastDefined.setView(view);
            mTextView.setText(text);
            mToastDefined.setGravity(Gravity.CENTER, 0, 0);
        } else {
            if (mTextView != null) mTextView.setText(text);
        }
        mToastDefined.show();
    }

    @SuppressLint("ShowToast")
    public static void show(Context context, String text) {
        if (toast == null) {
            toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
        } else {
            toast.setText(text);
        }
        toast.show();
    }

    public static void cancel() {
        if (toast != null) toast.cancel();
        if (mToastCenter != null) mToastCenter.cancel();
        if (mToastDefined != null) mToastDefined.cancel();
    }
}