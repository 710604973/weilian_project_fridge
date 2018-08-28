package com.mode.fridge.repository;

import com.miot.api.MiotManager;
import com.mode.fridge.bean.prop.RPCResult;
import com.mode.fridge.common.http.ApiClient;
import com.mode.fridge.utils.logUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import rx.Observable;

public class SweepRepository {
    private static final String TAG = SweepRepository.class.getSimpleName();
    /**
     * GetProp
     * 84727362
     */
    public static Observable<RPCResult> getProp(String did) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("method", "get_prop");
            jsonObject.put("did", did);
            jsonObject.put("id", 1);
            JSONArray jsonArray = new JSONArray();
            jsonArray.put("run_state");//模式
            jsonArray.put("battary_life");//电池剩余电量百分比，0 - 0%，100 - 100%
            jsonArray.put("start_time");//扫地开始时间，单位/s
            jsonArray.put("s_time");//本次清扫时间
            jsonArray.put("s_area");//本次清扫面积
//            jsonArray.put("order_time");//扫地机预约时间
            jsonObject.put("params", jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
            logUtil.e(TAG, e.toString());
        }
        return ApiClient.getInstance().getApiService().miOpen("openapp/device/rpc/" + did, jsonObject.toString(), ApiClient.getInstance().getMiClientId(),
                MiotManager.getPeopleManager().getPeople().getAccessToken());
    }


    /**
     * 电源开关设置
     */
    public static Observable<RPCResult> setPower(int param, String did) {
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
     * 电源开关设置
     * 0 - 自动清扫  1 - 拖地   2 - 重点清扫   3 - 回充
     */
    public static Observable<RPCResult> setMode(int param, String did) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("method", "set_mode");
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
