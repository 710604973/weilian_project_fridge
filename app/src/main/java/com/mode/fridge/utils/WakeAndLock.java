package com.mode.fridge.utils;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.os.PowerManager;

import com.mode.fridge.MyApplication;

/**
 * Created by young2 on 2017/3/9.
 */
public class WakeAndLock {
    private static String TAG = WakeAndLock.class.getName();
    private PowerManager powerManager;
    private PowerManager.WakeLock wakeLock;

    public WakeAndLock() {
        powerManager = (PowerManager) MyApplication.getContext().getSystemService(Context.POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK
                | PowerManager.ACQUIRE_CAUSES_WAKEUP, "bright");
    }

    /**
     * 唤醒屏幕
     */
    public void screenOn() {
        wakeLock.acquire();
        logUtil.d(TAG, "screenOn");
    }

    /**
     * 是否熄屏
     */
    public boolean isScreenOn() {
        return powerManager.isScreenOn();
    }

    /**
     * 熄灭屏幕
     */
    public void screenOff() {
        DevicePolicyManager policyManager = (DevicePolicyManager) MyApplication.getContext().getSystemService(Context.DEVICE_POLICY_SERVICE);
        ComponentName adminReceiver = new ComponentName(MyApplication.getContext(), ScreenReceiver.class);
        boolean admin = policyManager.isAdminActive(adminReceiver);
        if (admin) {
            policyManager.lockNow();
        } else {
            ToastUtil.showCenter(MyApplication.getContext(), "没有设备管理权限");
        }
    }
}