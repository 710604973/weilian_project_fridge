package com.mode.fridge.utils;


import android.text.TextUtils;
import android.util.Log;

/**
 * 日志打印管理
 * Created by William on 2017/12/29.
 */
public class logUtil {
    private static final boolean DEBUG = true;// 是否打印日志

    public static void d(String TAG, String msg) {
        if (DEBUG && !TextUtils.isEmpty(msg)) {
            Log.d(TAG, msg);
        }
    }

    public static void e(String TAG, String msg) {
        if (DEBUG && !TextUtils.isEmpty(msg)) {
            Log.e(TAG, msg);
        }
    }

    public static void i(String TAG, String msg) {
        if (DEBUG && !TextUtils.isEmpty(msg)) {
            Log.i(TAG, msg);
        }
    }

    public static void w(String TAG, String msg) {
        if (DEBUG && !TextUtils.isEmpty(msg)) {
            Log.w(TAG, msg);
        }
    }
}