package com.mode.fridge.repository;

import com.miot.api.MiotManager;
import com.mode.fridge.bean.prop.RPCResult;
import com.mode.fridge.common.http.ApiClient;
import com.mode.fridge.utils.logUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;

public class FridgeRepository {
    private static final String TAG = FridgeRepository.class.getSimpleName();
    private List<String> mList;// 随机提示文字集合

//    /**
//     * 随机生成冰箱提示文字
//     */
//    public String randomTip() {
//        int mode = SerialManager.getInstance().getDeviceParamsGet().getMode();
//        if (mList == null) mList = new ArrayList<>();
//        mList.clear();
//        if (mode == AppConstants.MODE_SMART) {
//            mList.add(FridgeApplication.getContext().getResources().getString(R.string.fridge_tip_9));
//        } else if (mode == AppConstants.MODE_HOLIDAY) {
//            mList.add(FridgeApplication.getContext().getResources().getString(R.string.fridge_tip_10));
//        } else if (getSmartCool().equals(MiotRepository.getInstance().SWITCH_ON)) {
//            mList.add(FridgeApplication.getContext().getResources().getString(R.string.fridge_tip_4));
//        } else if (getSmartFreeze().equals(MiotRepository.getInstance().SWITCH_ON)) {
//            mList.add(FridgeApplication.getContext().getResources().getString(R.string.fridge_tip_5));
//        }
//        if (isOneKeyCleanRunning()) {
//            mList.add(FridgeApplication.getContext().getResources().getString(R.string.fridge_tip_8));
//        }
//        String report = FridgePreference.getInstance().getWeatherReport();
//        if (!report.equals("")) mList.add(report);
//        mList.add(FridgeApplication.getContext().getResources().getString(R.string.fridge_tip_1));
//        if (FridgePreference.getInstance().getModel().equals(AppConstants.MODEL_X5))
//            mList.add(FridgeApplication.getContext().getResources().getString(R.string.fridge_tip_2));
//        mList.add(FridgeApplication.getContext().getResources().getString(R.string.fridge_tip_3));
//        mList.add(FridgeApplication.getContext().getResources().getString(R.string.fridge_tip_6));
//        mList.add(FridgeApplication.getContext().getResources().getString(R.string.fridge_tip_7));
//
//        int count = mList.size();
//        int index = new Random().nextInt(count - 1);
//        return mList.get(index);
//    }

    /**
     * Fridge
     */
    public static Observable<RPCResult> getProp(String did) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("method", "get_prop");
            jsonObject.put("did", did);
            jsonObject.put("id", 1);
            JSONArray jsonArray = new JSONArray();
            jsonArray.put("RCSet");//冷藏室开关
            jsonArray.put("RCSetTemp");//冷藏室实际温度
            jsonArray.put("CCSet");//变温室开关
            jsonArray.put("CCSetTemp");// 变温室设置温度
            jsonArray.put("FCSetTemp");//冷冻室设定温度
            jsonObject.put("params", jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
            logUtil.e(TAG, e.toString());
        }
        return ApiClient.getInstance().getApiService().miOpen("openapp/device/rpc/" + did, jsonObject.toString(), ApiClient.getInstance().getMiClientId(),
                MiotManager.getPeopleManager().getPeople().getAccessToken());
    }

    /**
     * 获取冰箱初始化数据
     */
    public static Observable<RPCResult> getInitData(String did, ArrayList<String> list) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("method", "get_prop");
            jsonObject.put("did", did);
            jsonObject.put("id", 1);

            JSONArray jsonArray = new JSONArray();
            for (int i = 0; i < list.size(); i++) {
                jsonArray.put(list.get(i));
            }
            jsonObject.put("params", jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
            logUtil.e(TAG, e.toString());
        }
        return ApiClient.getInstance().getApiService().miOpen("openapp/device/rpc/" + did, jsonObject.toString(), ApiClient.getInstance().getMiClientId(),
                MiotManager.getPeopleManager().getPeople().getAccessToken());
    }

    /**
     * 获取冰箱初始化数据
     */
    public static Observable<RPCResult> getInitData(String did) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("method", "get_prop");
            jsonObject.put("did", did);
            jsonObject.put("id", 1);

            JSONArray jsonArray = new JSONArray();
            jsonArray.put("RCSetTemp");//冷藏室实际温度
            jsonArray.put("CCSetTemp");// 变温室设置温度
            jsonArray.put("FCSetTemp");//冷冻室设定温度
            jsonArray.put("FilterLifeBase");//滤芯总寿命，单位小时
            jsonArray.put("FilterLife");//滤芯已用寿命，单位小时
            jsonObject.put("params", jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
            logUtil.e(TAG, e.toString());
        }
        return ApiClient.getInstance().getApiService().miOpen("openapp/device/rpc/" + did, jsonObject.toString(), ApiClient.getInstance().getMiClientId(),
                MiotManager.getPeopleManager().getPeople().getAccessToken());
    }

    /**
     * 获取冰箱初始化数据
     */
    public static Observable<RPCResult> getFilterLife(String did) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("method", "get_prop");
            jsonObject.put("did", did);
            jsonObject.put("id", 1);

            JSONArray jsonArray = new JSONArray();
            jsonArray.put("FilterLifeBase");//滤芯总寿命，单位小时
            jsonArray.put("FilterLife");//滤芯已用寿命，单位小时
            jsonObject.put("params", jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
            logUtil.e(TAG, e.toString());
        }
        return ApiClient.getInstance().getApiService().miOpen("openapp/device/rpc/" + did, jsonObject.toString(), ApiClient.getInstance().getMiClientId(),
                MiotManager.getPeopleManager().getPeople().getAccessToken());
    }

    /**
     * 设置冰箱模式
     */
    public static Observable<RPCResult> setMode(String did, String mode) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("method", "setMode");
            jsonObject.put("did", did);
            jsonObject.put("id", 12);
            JSONArray jsonArray = new JSONArray();
            jsonArray.put(mode);
            jsonObject.put("params", jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
            logUtil.e(TAG, e.toString());
        }
        return ApiClient.getInstance().getApiService().miOpen("openapp/device/rpc/" + did, jsonObject.toString(), ApiClient.getInstance().getMiClientId(),
                MiotManager.getPeopleManager().getPeople().getAccessToken());
    }

    /**
     * 设置冰箱模式
     */
    public static Observable<RPCResult> setRCCMode(String did, int mode) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("method", "setRCCMode");
            jsonObject.put("did", did);
            jsonObject.put("id", 12);
            JSONArray jsonArray = new JSONArray();
            jsonArray.put(mode);
            jsonObject.put("params", jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
            logUtil.e(TAG, e.toString());
        }
        return ApiClient.getInstance().getApiService().miOpen("openapp/device/rpc/" + did, jsonObject.toString(), ApiClient.getInstance().getMiClientId(),
                MiotManager.getPeopleManager().getPeople().getAccessToken());
    }

    /**
     * 设置泠藏室温度
     */
    public static Observable<RPCResult> setRCSetTemp(String did, int temp) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("method", "setRCSetTemp");
            jsonObject.put("did", did);
            jsonObject.put("id", 1);
            JSONArray jsonArray = new JSONArray();
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
     * 设置变温室温度
     */
    public static Observable<RPCResult> setCCSetTemp(String did, int temp) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("method", "setCCSetTemp");
            jsonObject.put("did", did);
            jsonObject.put("id", 1);
            JSONArray jsonArray = new JSONArray();
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
     * 设置泠冻室温度
     */
    public static Observable<RPCResult> setFCSetTemp(String did, int temp) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("method", "setFCSetTemp");
            jsonObject.put("did", did);
            jsonObject.put("id", 1);
            JSONArray jsonArray = new JSONArray();
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
     * 打开/关闭冷藏室，"on","off"
     */
    public static Observable<RPCResult> setRCSet(String did, String status) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("method", "setRCSet");
            jsonObject.put("did", did);
            jsonObject.put("id", 1);
            JSONArray jsonArray = new JSONArray();
            jsonArray.put(status);
            jsonObject.put("params", jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
            logUtil.e(TAG, e.toString());
        }
        return ApiClient.getInstance().getApiService().miOpen("openapp/device/rpc/" + did, jsonObject.toString(), ApiClient.getInstance().getMiClientId(),
                MiotManager.getPeopleManager().getPeople().getAccessToken());
    }

    /**
     * 打开/关闭变温室，"on","off"
     */
    public static Observable<RPCResult> setCCSet(String did, String status) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("method", "setCCSet");
            jsonObject.put("did", did);
            jsonObject.put("id", 1);
            JSONArray jsonArray = new JSONArray();
            jsonArray.put(status);
            jsonObject.put("params", jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
            logUtil.e(TAG, e.toString());
        }
        return ApiClient.getInstance().getApiService().miOpen("openapp/device/rpc/" + did, jsonObject.toString(), ApiClient.getInstance().getMiClientId(),
                MiotManager.getPeopleManager().getPeople().getAccessToken());
    }

    /**
     * 设置/关闭速冷，"on","off"
     */
    public static Observable<RPCResult> setSmartCool(String did, String status) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("method", "setSmartCool");
            jsonObject.put("did", did);
            jsonObject.put("id", 1);
            JSONArray jsonArray = new JSONArray();
            jsonArray.put(status);
            jsonObject.put("params", jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
            logUtil.e(TAG, e.toString());
        }
        return ApiClient.getInstance().getApiService().miOpen("openapp/device/rpc/" + did, jsonObject.toString(), ApiClient.getInstance().getMiClientId(),
                MiotManager.getPeopleManager().getPeople().getAccessToken());
    }

    /**
     * 设置/关闭速冻，"on","off"
     */
    public static Observable<RPCResult> setSmartFreeze(String did, String status) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("method", "setSmartFreeze");
            jsonObject.put("did", did);
            jsonObject.put("id", 1);
            JSONArray jsonArray = new JSONArray();
            jsonArray.put(status);
            jsonObject.put("params", jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
            logUtil.e(TAG, e.toString());
        }
        return ApiClient.getInstance().getApiService().miOpen("openapp/device/rpc/" + did, jsonObject.toString(), ApiClient.getInstance().getMiClientId(),
                MiotManager.getPeopleManager().getPeople().getAccessToken());
    }

    /**
     * 设置/关闭冰饮功能，"on","off"
     */
    public static Observable<RPCResult> setCoolBeverage(String did, String status) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("method", "setCoolBeverage");
            jsonObject.put("did", did);
            jsonObject.put("id", 1);
            JSONArray jsonArray = new JSONArray();
            jsonArray.put(status);
            jsonObject.put("params", jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
            logUtil.e(TAG, e.toString());
        }
        return ApiClient.getInstance().getApiService().miOpen("openapp/device/rpc/" + did, jsonObject.toString(), ApiClient.getInstance().getMiClientId(),
                MiotManager.getPeopleManager().getPeople().getAccessToken());
    }

    /**
     * 开启/关闭一键净化，"on","off"
     */
    public static Observable<RPCResult> setOneKeyClean(String did, String status) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("method", "setOneKeyClean");
            jsonObject.put("did", did);
            jsonObject.put("id", 1);
            JSONArray jsonArray = new JSONArray();
            jsonArray.put(status);
            jsonObject.put("params", jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
            logUtil.e(TAG, e.toString());
        }
        return ApiClient.getInstance().getApiService().miOpen("openapp/device/rpc/" + did, jsonObject.toString(), ApiClient.getInstance().getMiClientId(),
                MiotManager.getPeopleManager().getPeople().getAccessToken());
    }

    /**
     * 设置滤芯寿命，单位小时
     */
    public static Observable<RPCResult> setFilterLife(String did, int hour) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("method", "setFilterLife");
            jsonObject.put("did", did);
            jsonObject.put("id", 1);
            JSONArray jsonArray = new JSONArray();
            jsonArray.put(hour);
            jsonObject.put("params", jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
            logUtil.e(TAG, e.toString());
        }
        return ApiClient.getInstance().getApiService().miOpen("openapp/device/rpc/" + did, jsonObject.toString(), ApiClient.getInstance().getMiClientId(),
                MiotManager.getPeopleManager().getPeople().getAccessToken());
    }

    /**
     * 城市，字符串
     */
    public static Observable<RPCResult> setCity(String did, String city) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("method", "setCity");
            jsonObject.put("did", did);
            jsonObject.put("id", 1);
            JSONArray jsonArray = new JSONArray();
            jsonArray.put(city);
            jsonObject.put("params", jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
            logUtil.e(TAG, e.toString());
        }
        return ApiClient.getInstance().getApiService().miOpen("openapp/device/rpc/" + did, jsonObject.toString(), ApiClient.getInstance().getMiClientId(),
                MiotManager.getPeopleManager().getPeople().getAccessToken());
    }

    /**
     * 变温室供选择场景，最多六个，如：肉类,-17;水果,3;干货,0;蔬菜,5
     */
    public static Observable<RPCResult> setSceneChoose(String did, String sceneName) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("method", "setSceneChoose");
            jsonObject.put("did", did);
            jsonObject.put("id", 1);
            JSONArray jsonArray = new JSONArray();
            jsonArray.put(sceneName);
            jsonObject.put("params", jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
            logUtil.e(TAG, e.toString());
        }
        return ApiClient.getInstance().getApiService().miOpen("openapp/device/rpc/" + did, jsonObject.toString(), ApiClient.getInstance().getMiClientId(),
                MiotManager.getPeopleManager().getPeople().getAccessToken());
    }


    /**
     * 变温室已选择场景
     */
    public static Observable<RPCResult> setSceneName(String did, String sceneName) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("method", "setSceneName");
            jsonObject.put("did", did);
            jsonObject.put("id", 1);
            JSONArray jsonArray = new JSONArray();
            jsonArray.put(sceneName);
            jsonObject.put("params", jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
            logUtil.e(TAG, e.toString());
        }
        return ApiClient.getInstance().getApiService().miOpen("openapp/device/rpc/" + did, jsonObject.toString(), ApiClient.getInstance().getMiClientId(),
                MiotManager.getPeopleManager().getPeople().getAccessToken());
    }

    /**
     * 设置圆屏显示，上半屏和下半屏，具体见属性，如设置冰箱温度[0,0]，注：冰箱温度只能两个同时设置0，其他模式可以随意组合（oled屏才有）
     */
    public static Observable<RPCResult> setRoundScreen(String did, String scene) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("method", "setRoundScreen");
            jsonObject.put("did", did);
            jsonObject.put("id", 1);
            JSONArray jsonArray = new JSONArray();
            jsonArray.put(scene);
            jsonObject.put("params", jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
            logUtil.e(TAG, e.toString());
        }
        return ApiClient.getInstance().getApiService().miOpen("openapp/device/rpc/" + did, jsonObject.toString(), ApiClient.getInstance().getMiClientId(),
                MiotManager.getPeopleManager().getPeople().getAccessToken());
    }


    /**
     * 使能人体感应功能，0:无；1：有
     */
    public static Observable<RPCResult> setPir(String did, int state) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("method", "setPir");
            jsonObject.put("did", did);
            jsonObject.put("id", 1);
            JSONArray jsonArray = new JSONArray();
            jsonArray.put(state);
            jsonObject.put("params", jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
            logUtil.e(TAG, e.toString());
        }
        return ApiClient.getInstance().getApiService().miOpen("openapp/device/rpc/" + did, jsonObject.toString(), ApiClient.getInstance().getMiClientId(),
                MiotManager.getPeopleManager().getPeople().getAccessToken());
    }

    /**
     * 屏幕常亮功能,0:无，1：有
     */
    public static Observable<RPCResult> ScreenOn(String did, int state) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("method", "ScreenOn");
            jsonObject.put("did", did);
            jsonObject.put("id", 1);
            JSONArray jsonArray = new JSONArray();
            jsonArray.put(state);
            jsonObject.put("params", jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
            logUtil.e(TAG, e.toString());
        }
        return ApiClient.getInstance().getApiService().miOpen("openapp/device/rpc/" + did, jsonObject.toString(), ApiClient.getInstance().getMiClientId(),
                MiotManager.getPeopleManager().getPeople().getAccessToken());
    }

}
