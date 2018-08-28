package com.mode.fridge.utils;

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by William on 2018/3/6.
 */
public class ScreenReceiver extends DeviceAdminReceiver {

    private void showToast(Context context, String msg) {
        ToastUtil.showCenter(context, msg);
    }

    @Override
    public void onEnabled(Context context, Intent intent) {
        ToastUtil.showCenter(context,
                "设备管理器使能");
    }

    @Override
    public void onDisabled(Context context, Intent intent) {
        ToastUtil.showCenter(context,
                "设备管理器没有使能");
    }
}