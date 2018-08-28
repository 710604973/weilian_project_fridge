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
 * 即热饮水吧相关 Api
 * Created by William on 2018/2/8.
 */
public class HeatKettleRepository {
    private static final String TAG = HeatKettleRepository.class.getSimpleName();

    /**
     * miOpen
     */
    public static Observable<RPCResult> getProp(String did) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("method", "get_prop");
            jsonObject.put("did", did);
            jsonObject.put("id", 1);
            JSONArray jsonArray = new JSONArray();
            jsonArray.put("setup_tempe");
            jsonArray.put("tds");
            jsonArray.put("water_remain_time");
            jsonArray.put("flush_time");
            jsonArray.put("custom_tempe1");
            jsonArray.put("min_set_tempe");
            jsonArray.put("work_mode");
            jsonArray.put("run_status");
            jsonObject.put("params", jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
            logUtil.e(TAG, e.toString());
        }
        return ApiClient.getInstance().getApiService().miOpen("openapp/device/rpc/" + did, jsonObject.toString(), ApiClient.getInstance().getMiClientId(),
                MiotManager.getPeopleManager().getPeople().getAccessToken());
    }

    /**
     * 温水键温度设置
     */
    public static Observable<RPCResult> setTemp(String did, int temp) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("method", "set_tempe_setup");
            jsonObject.put("did", did);
            jsonObject.put("id", 2);
            JSONArray jsonArray = new JSONArray();
            jsonArray.put(1);
            jsonArray.put(temp);
            jsonObject.put("params", jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
            logUtil.e(TAG, e.toString());
        }
        return ApiClient.getInstance().getApiService().miOpen("openapp/device/rpc/" + did, jsonObject.toString(), ApiClient.getInstance().getMiClientId(),
                MiotManager.getPeopleManager().getPeople().getAccessToken());
    }
}