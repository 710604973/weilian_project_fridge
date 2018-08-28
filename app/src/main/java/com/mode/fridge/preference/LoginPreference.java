package com.mode.fridge.preference;

import android.content.Context;
import android.content.SharedPreferences;

import com.mode.fridge.MyApplication;

/**
 * 登录相关信息缓存
 * Created by William on 2018/1/29.
 */
public class LoginPreference {
    private static LoginPreference mInstance;
    private static final String name = "viomiLogin";
    private static final String LOGIN_CLIENT_ID = "login_client_id";// 生成登录二维码的 ClientId
    private static final String LOGIN_PHONE_TYPE = "login_phone_type";// 登录手机系统类型
    private static final String LOGIN_BIND_STATUS = "login_bind_status";// 绑定状态
    private static final String LOGIN_USER_ID = "login_user_id";// 用户 id

    public static LoginPreference getInstance() {
        if (mInstance == null) {
            synchronized (LoginPreference.class) {
                if (mInstance == null) {
                    mInstance = new LoginPreference();
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
     * 缓存 ClientId
     */
    public void saveClientId(String clientId) {
        SharedPreferences sharedPreferences = getSharedPreferences();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(LOGIN_CLIENT_ID, clientId);
        editor.apply();
    }

    /**
     * 获取 ClientId
     */
    public String getClientId() {
        return getSharedPreferences().getString(LOGIN_CLIENT_ID, "");
    }

    /**
     * 缓存登录手机系统类型
     */
    public void savePhoneType(String type) {
        SharedPreferences sharedPreferences = getSharedPreferences();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(LOGIN_PHONE_TYPE, type);
        editor.apply();
    }

    /**
     * 获取登录手机系统类型
     */
    public String getPhoneType() {
        return getSharedPreferences().getString(LOGIN_PHONE_TYPE, "");
    }

    /**
     * 缓存绑定状态
     */
    public void saveBindStatus(boolean enable) {
        SharedPreferences sharedPreferences = getSharedPreferences();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(LOGIN_BIND_STATUS, enable);
        editor.apply();
    }

    /**
     * 获取绑定状态
     */
    public boolean getBindStatus() {
        return getSharedPreferences().getBoolean(LOGIN_BIND_STATUS, false);
    }

    /**
     * 缓存用户 Id
     */
    public void saveUserId(int userId) {
        SharedPreferences sharedPreferences = getSharedPreferences();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(LOGIN_USER_ID, userId);
        editor.apply();
    }

    /**
     * 获取用户 id
     */
    public int getUserId() {
        return getSharedPreferences().getInt(LOGIN_USER_ID, 0);
    }
}