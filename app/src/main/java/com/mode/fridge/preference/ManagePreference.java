package com.mode.fridge.preference;

import android.content.Context;
import android.content.SharedPreferences;

import com.mode.fridge.MyApplication;

/**
 * 管理中心缓存
 * Created by William on 2018/2/22.
 */
public class ManagePreference {
    private static ManagePreference mInstance;
    private static final String name = "viomiManage";
    private static final String APP_UPDATE = "app_update";// App 更新
    private static final String DEBUG_MODE = "debug_mode";// 测试环境
    private static final String GUIDE_SHOW = "guide_show";// 是否显示指引
    private static String HUMAN_SENSOR = "human_sensor";// 人感开关

    public static ManagePreference getInstance() {
        if (mInstance == null) {
            synchronized (ManagePreference.class) {
                if (mInstance == null) {
                    mInstance = new ManagePreference();
                }
            }
        }
        return mInstance;
    }

    private SharedPreferences getSharedPreferences() {
        return MyApplication.getContext().getSharedPreferences(
                name, Context.MODE_PRIVATE);
    }

    /**
     * 检查 App 更新后缓存标志
     */
    public void saveAppUpdate(boolean isHasNew) {
        SharedPreferences sharedPreferences = getSharedPreferences();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(APP_UPDATE, isHasNew);
        editor.apply();
    }

    /**
     * 获取 App 更新标志
     */
    public boolean getAppUpdate() {
        return getSharedPreferences().getBoolean(APP_UPDATE, false);
    }

    /**
     * 测试环境标志
     */
    public void saveDebug(boolean isDebug) {
        SharedPreferences sharedPreferences = getSharedPreferences();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(DEBUG_MODE, isDebug);
        editor.apply();
    }

    /**
     * 获取测试环境标志
     */
    public boolean getDebug() {
        return getSharedPreferences().getBoolean(DEBUG_MODE, false);
    }

    /**
     * 缓存首次使用冰箱标志
     */
    public void saveGuide(boolean isKnown) {
        SharedPreferences sharedPreferences = getSharedPreferences();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(GUIDE_SHOW, isKnown);
        editor.apply();
    }

    /**
     * 获取首次使用冰箱标志
     */
    public boolean getGuide() {
        return getSharedPreferences().getBoolean(GUIDE_SHOW, true);
    }

    /**
     * 缓存人感开关状态
     */
    public void saveHumanSensorState(boolean enable) {
        SharedPreferences sharedPreferences = getSharedPreferences();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(HUMAN_SENSOR, enable);
        editor.apply();
    }

    /**
     * 获取人感开关状态
     */
    public boolean getHumanSensorState() {
        return getSharedPreferences().getBoolean(HUMAN_SENSOR, true);
    }
}