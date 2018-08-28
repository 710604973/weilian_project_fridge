package com.mode.fridge.repository;


import android.content.Context;

import com.miot.api.MiotManager;
import com.miot.common.config.AppConfiguration;
import com.miot.common.exception.MiotException;
import com.miot.common.people.People;
import com.miot.common.people.PeopleFactory;
import com.mode.fridge.AppConstants;
import com.mode.fridge.bean.QRCodeBase;
import com.mode.fridge.bean.UserInfo;
import com.mode.fridge.common.http.ApiClient;
import com.mode.fridge.preference.LoginPreference;
import com.mode.fridge.utils.ToolUtil;
import com.mode.fridge.utils.logUtil;

import org.json.JSONException;
import org.json.JSONObject;

import rx.Observable;

/**
 * 登录二维码生成相关 API
 * Created by William on 2018/1/27.
 */
public class LoginRepository {
    private static final String TAG = LoginRepository.class.getSimpleName();
    private static LoginRepository mInstance;

    public static LoginRepository getInstance() {
        if (mInstance == null) {
            synchronized (LoginRepository.class) {
                if (mInstance == null) {
                    mInstance = new LoginRepository();
                }
            }
        }
        return mInstance;
    }

    /**
     * 生成二维码
     */
    public Observable<QRCodeBase> createQRCode(Context context) {
        Observable<QRCodeBase> observableNoNet = Observable.create(subscriber -> {
            subscriber.onNext(null);
            subscriber.onCompleted();
        });

        // 生成 ClientId
        String mac = ToolUtil.getMiIdentification().getMac();
        mac = mac == null ? "" : mac.replaceAll(":", "");
        String clientId = mac + System.currentTimeMillis() / 1000;
        LoginPreference.getInstance().saveClientId(clientId);
        logUtil.d(TAG, clientId);

        Observable<QRCodeBase> observable = ApiClient.getInstance().getApiService().createLoginQRCode("1", clientId);
        // 网络不可用
        if (!ToolUtil.isNetworkConnected(context)) return observableNoNet;
        else return observable;
    }

    /**
     * 检查登录状态
     */
    public Observable<QRCodeBase> getLoginStatus(String clientId) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("clientID", clientId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return ApiClient.getInstance().getApiService().checkLoginStatus(ApiClient.getInstance().getRequestBody(jsonObject));
    }

    /**
     * 保存用户登录信息
     *
     * @param result: 登录成功返回 JSON
     */
    public boolean saveUserInfo(Context context, QRCodeBase result) {
        UserInfo userInfo = result.getLoginQRCode().getUserInfo();
        userInfo.setToken(result.getLoginQRCode().getToken());
        userInfo.setUserCode(result.getLoginQRCode().getLoginResult().getUserCode());
        // 小米账号相关信息
        People people = PeopleFactory.createOauthPeople(userInfo.getMiUserInfo().getAccessToken(), userInfo.getMiUserInfo().getMiId(),
                userInfo.getMiUserInfo().getmExpiresIn(), userInfo.getMiUserInfo().getMacKey(), userInfo.getMiUserInfo().getMacAlgorithm());
        try {
            MiotManager.getPeopleManager().savePeople(people);// 保存小米账号信息
        } catch (MiotException e) {
            e.printStackTrace();
            logUtil.e(TAG, "save mi people fail, error:" + e.getMessage());
            return false;
        }
        // 把账号信息保存到文件
        ToolUtil.saveObject(context, AppConstants.USER_INFO_FILE, result);
        // 缓存 userId
        LoginPreference.getInstance().saveUserId(result.getLoginQRCode().getLoginResult().getUserId());
        // 缓存手机系统类型
        LoginPreference.getInstance().savePhoneType(userInfo.getMiUserInfo().getType());
        AppConfiguration appConfiguration = new AppConfiguration();
        if (userInfo.getMiUserInfo().getType().equals("android")) {
            appConfiguration.setAppId(AppConstants.OAUTH_ANDROID_APP_ID);
            appConfiguration.setAppKey(AppConstants.OAUTH_ANDROID_APP_KEY);
        } else {
            appConfiguration.setAppId(AppConstants.OAUTH_IOS_APP_ID);
            appConfiguration.setAppKey(AppConstants.OAUTH_IOS_APP_KEY);
        }
        MiotManager.getInstance().setAppConfig(appConfiguration);// 保存配置
        return true;
    }
}