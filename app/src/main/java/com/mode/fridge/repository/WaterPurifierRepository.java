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
 * 净水器相关 Api
 * Created by William on 2018/2/3.
 */
public class WaterPurifierRepository {
    private static final String TAG = WaterPurifierRepository.class.getSimpleName();

//    /**
//     * 小米净水器
//     */
//    public static Observable<RPCResult> getProp(String did) {
//        JSONObject jsonObject = new JSONObject();
//        try {
//            jsonObject.put("method", "get_prop");
//            jsonObject.put("did", did);
//            jsonObject.put("id", 1);
//
//            JSONArray jsonArray = new JSONArray();
//            jsonArray.put("tds_out");//出水水质（平均值）
//            jsonArray.put("f1_gain");
//            jsonArray.put("f2_gain");
//            jsonArray.put("f3_gain");
//            jsonArray.put("f4_gain");
//            jsonObject.put("params", jsonArray);
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//            logUtil.e(TAG, e.toString());
//        }
//        return ApiClient.getInstance().getApiService().miOpen("openapp/device/rpc/" + did, jsonObject.toString(), ApiClient.getInstance().getMiClientId(),
//                MiotManager.getPeopleManager().getPeople().getAccessToken());
//    }

    /**
     * 小米净水器 miOpen
     */
    public static Observable<RPCResult> miGetProp(String did) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("method", "get_prop");
            jsonObject.put("did", did);
            jsonObject.put("id", 1);
        } catch (JSONException e) {
            e.printStackTrace();
            logUtil.e(TAG, e.toString());
        }
        return ApiClient.getInstance().getApiService().miOpen("openapp/device/rpc/" + did, jsonObject.toString(), ApiClient.getInstance().getMiClientId(),
                MiotManager.getPeopleManager().getPeople().getAccessToken());
    }

    /**
     * 云米 V 系列 miOpen
     */
    public static Observable<RPCResult> vGetProp(String did) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("method", "get_prop");
            jsonObject.put("did", did);
            jsonObject.put("id", 1);
            JSONArray jsonArray = new JSONArray();
            jsonArray.put("temperature");
            jsonArray.put("uv_state");
            jsonArray.put("press");
            jsonArray.put("elecval_state");
            jsonObject.put("params", jsonArray);
        } catch (JSONException e1) {
            e1.printStackTrace();
            logUtil.e(TAG, e1.toString());
        }
        return ApiClient.getInstance().getApiService().miOpen("openapp/device/rpc/" + did, jsonObject.toString(), ApiClient.getInstance().getMiClientId(),
                MiotManager.getPeopleManager().getPeople().getAccessToken());
    }

    /**
     * 云米 C 系列 GetPrp
     */
    public static Observable<RPCResult> cGetProp(String did) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("method", "get_prop");
            jsonObject.put("did", did);
            jsonObject.put("id", 1);
            JSONArray jsonArray = new JSONArray();
            jsonArray.put("temperature");
            jsonArray.put("uv_state");
            jsonObject.put("params", jsonArray);
        } catch (JSONException e1) {
            e1.printStackTrace();
            logUtil.e(TAG, e1.toString());
        }
        return ApiClient.getInstance().getApiService().miOpen("openapp/device/rpc/" + did, jsonObject.toString(), ApiClient.getInstance().getMiClientId(),
                MiotManager.getPeopleManager().getPeople().getAccessToken());
    }

    /**
     * X3 miOpen
     */
    public static Observable<RPCResult> x3GetProp(String did) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("method", "get_prop");
            jsonObject.put("did", did);
            jsonObject.put("id", 1);
            JSONArray jsonArray = new JSONArray();
            jsonArray.put("setup_tempe");
            jsonArray.put("setup_flow");
            jsonArray.put("custom_tempe1");
            jsonArray.put("custom_flow0");
            jsonArray.put("custom_flow1");
            jsonArray.put("min_set_tempe");
            jsonObject.put("params", jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
            logUtil.e(TAG, e.toString());
        }
        return ApiClient.getInstance().getApiService().miOpen("openapp/device/rpc/" + did, jsonObject.toString(), ApiClient.getInstance().getMiClientId(),
                MiotManager.getPeopleManager().getPeople().getAccessToken());
    }

    /**
     * X5 miOpen
     */
    public static Observable<RPCResult> x5GetProp(String did) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("method", "get_prop");
            jsonObject.put("did", did);
            jsonObject.put("id", 1);
            JSONArray jsonArray = new JSONArray();
            jsonArray.put("temperature");
            jsonArray.put("uv_state");
            jsonArray.put("setup_tempe");
            jsonArray.put("setup_flow");
            jsonArray.put("custom_tempe1");
            jsonArray.put("custom_flow0");
            jsonArray.put("custom_flow1");
            jsonArray.put("custom_flow2");
            jsonObject.put("params", jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
            logUtil.e(TAG, e.toString());
        }
        return ApiClient.getInstance().getApiService().miOpen("openapp/device/rpc/" + did, jsonObject.toString(), ApiClient.getInstance().getMiClientId(),
                MiotManager.getPeopleManager().getPeople().getAccessToken());
    }

    /**
     * 出水温度设定
     */
    public static Observable<RPCResult> setTemp(String did, int temp) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("method", "set_tempe_setup");
            jsonObject.put("did", did);
            jsonObject.put("id", 12);
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

    /**
     * 出水流量设定
     */
    public static Observable<RPCResult> setFlow(String did, int flow, int index) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("method", "set_flow_setup");
            jsonObject.put("did", did);
            jsonObject.put("id", 12);
            JSONArray jsonArray = new JSONArray();
            jsonArray.put(index);
            jsonArray.put(flow);
            jsonObject.put("params", jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
            logUtil.e(TAG, e.toString());
        }
        return ApiClient.getInstance().getApiService().miOpen("openapp/device/rpc/" + did, jsonObject.toString(), ApiClient.getInstance().getMiClientId(),
                MiotManager.getPeopleManager().getPeople().getAccessToken());
    }
}