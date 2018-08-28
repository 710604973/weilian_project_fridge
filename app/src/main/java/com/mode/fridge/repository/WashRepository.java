package com.mode.fridge.repository;

import com.miot.api.MiotManager;
import com.mode.fridge.bean.prop.RPCResult;
import com.mode.fridge.common.http.ApiClient;
import com.mode.fridge.utils.logUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import rx.Observable;

public class WashRepository {
    private static final String TAG = WashRepository.class.getSimpleName();

    /**
     * washMachine
     */
    public static Observable<RPCResult> getProp(String did) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("method", "get_prop");
            jsonObject.put("did", did);
            jsonObject.put("id", 1);
            JSONArray jsonArray = new JSONArray();
            jsonArray.put("program");//洗衣机洗涤程序
            jsonArray.put("wash_process");//设备洗涤过程
            jsonArray.put("wash_status");//设备工作状态，工作中才判断
            jsonArray.put("water_temp");//洗涤水温设置
            jsonArray.put("rinse_time");//漂洗次数设置
            jsonArray.put("remain_time");//剩余洗涤时间，分钟
            jsonArray.put("spin_level");//脱水强度（速度）设置
            jsonArray.put("appoint_time");//洗涤完成的时间，单位 小时
            jsonArray.put("be_status");//0：未预约；1：已预约
            jsonObject.put("params", jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
            logUtil.e(TAG, e.toString());
        }
        return ApiClient.getInstance().getApiService().miOpen("openapp/device/rpc/" + did, jsonObject.toString(), ApiClient.getInstance().getMiClientId(),
                MiotManager.getPeopleManager().getPeople().getAccessToken());
    }
}
