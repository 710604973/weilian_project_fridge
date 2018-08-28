package com.mode.fridge.repository;


import com.miot.api.MiotManager;
import com.mode.fridge.bean.prop.RPCResult;
import com.mode.fridge.common.http.ApiClient;
import com.mode.fridge.utils.logUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import rx.Observable;

/**
 * 烟机相关 Api
 * Created by William on 2018/2/21.
 */
public class RangeHoodRepository {
    private static final String TAG = RangeHoodRepository.class.getSimpleName();

    /**
     * GetProp
     */
    public static Observable<RPCResult> getProp(String did) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("method", "get_prop");
            jsonObject.put("did", did);
            jsonObject.put("id", 1);
            JSONArray jsonArray = new JSONArray();
            jsonArray.put("run_time");
            jsonArray.put("power_state");
            jsonArray.put("wind_state");
            jsonArray.put("light_state");
            jsonArray.put("link_state");
            jsonArray.put("stove1_data");
            jsonArray.put("stove2_data");
            jsonArray.put("pm2_5");
            jsonArray.put("battary_life");
            jsonArray.put("poweroff_delaytime");
            jsonArray.put("aqi_state");
            jsonArray.put("aqi_thd");
            jsonArray.put("aqi_hour");
            jsonArray.put("aqi_min");
            jsonArray.put("aqi_time");
            jsonArray.put("curise_state");
            jsonArray.put("light_sync_state");
            jsonObject.put("params", jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
            logUtil.e(TAG, e.toString());
        }
        return ApiClient.getInstance().getApiService().miOpen("openapp/device/rpc/" + did, jsonObject.toString(), ApiClient.getInstance().getMiClientId(),
                MiotManager.getPeopleManager().getPeople().getAccessToken());
    }

    /**
     * 获取统计数据
     */
    public static Observable<RPCResult> getUserData(String did) {
        long time_end = System.currentTimeMillis() / 1000;
        long time_start = time_end - 365 * 24 * 60 * 60;
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("did", did);
            jsonObject.put("type", "store");
            jsonObject.put("key", "life_record");
            jsonObject.put("time_start", time_start);
            jsonObject.put("time_end", time_end);
        } catch (JSONException e) {
            e.printStackTrace();
            logUtil.e(TAG, e.toString());
        }
        return ApiClient.getInstance().getApiService().miOpen("openapp/user/get_user_device_data/", jsonObject.toString(), ApiClient.getInstance().getMiClientId(),
                MiotManager.getPeopleManager().getPeople().getAccessToken());
    }

    /**
     * 电源开关设置
     */
    public static Observable<RPCResult> setPower(String param, String did) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("method", "set_power");
            jsonObject.put("did", did);
            jsonObject.put("id", 1);
            JSONArray jsonArray = new JSONArray();
            jsonArray.put(param);
            jsonObject.put("params", jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
            logUtil.e(TAG, e.toString());
        }
        return ApiClient.getInstance().getApiService().miOpen("openapp/device/rpc/" + did, jsonObject.toString(), ApiClient.getInstance().getMiClientId(),
                MiotManager.getPeopleManager().getPeople().getAccessToken());
    }

    /**
     * 设置风速
     */
    public static Observable<RPCResult> setWind(String param, String did) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("method", "set_wind");
            jsonObject.put("did", did);
            jsonObject.put("id", 1);
            JSONArray jsonArray = new JSONArray();
            jsonArray.put(param);
            jsonObject.put("params", jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
            logUtil.e(TAG, e.toString());
        }
        return ApiClient.getInstance().getApiService().miOpen("openapp/device/rpc/" + did, jsonObject.toString(), ApiClient.getInstance().getMiClientId(),
                MiotManager.getPeopleManager().getPeople().getAccessToken());
    }

    /**
     * 设置灯光
     */
    public static Observable<RPCResult> setLight(String param, String did) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("method", "set_light");
            jsonObject.put("did", did);
            jsonObject.put("id", 1);
            JSONArray jsonArray = new JSONArray();
            jsonArray.put(param);
            jsonObject.put("params", jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
            logUtil.e(TAG, e.toString());
        }
        return ApiClient.getInstance().getApiService().miOpen("openapp/device/rpc/" + did, jsonObject.toString(), ApiClient.getInstance().getMiClientId(),
                MiotManager.getPeopleManager().getPeople().getAccessToken());
    }
}