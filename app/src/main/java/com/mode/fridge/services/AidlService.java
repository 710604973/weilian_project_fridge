package com.mode.fridge.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.google.gson.JsonIOException;
import com.jd.smart.fridge.control.IControlService;
import com.jd.smart.fridge.control.NotifyCallBack;
import com.mode.fridge.R;
import com.mode.fridge.activity.FridgeActivity;
import com.mode.fridge.bean.DeviceParamsSet;
import com.mode.fridge.bean.SerialInfo;
import com.mode.fridge.bean.aidl.Result;
import com.mode.fridge.bean.aidl.StreamBean;
import com.mode.fridge.broadcast.BroadcastAction;
import com.mode.fridge.common.FridgeStreamId;
import com.mode.fridge.common.rxbus.BusEvent;
import com.mode.fridge.common.rxbus.RxBus;
import com.mode.fridge.device.DeviceManager;
import com.mode.fridge.manager.ControlManager;
import com.mode.fridge.manager.SerialManager;
import com.mode.fridge.utils.ApkUtil;
import com.mode.fridge.utils.ToolUtil;
import com.mode.fridge.utils.log;
import com.mode.fridge.utils.logUtil;
import com.viomi.common.callback.AppCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

public class AidlService extends Service {
    private static final String TAG = AidlService.class.getSimpleName();
    private AppCallback<String> mRCRoomTempCallback, mRCRoomEnableCallback, mRCRoomQuickCoolCallback;//冷藏室设置回调
    private AppCallback<String> mCCRoomTempCallback, mCCRoomEnableCallback, mCCRoomSceneCallback;//变温室设置回调
    private AppCallback<String> mFZRoomTempCallback, mFZRoomQuickColdCallback, mIcedDrinkCallBack, mSmartModeCallback, mHolidayModeCallback;
    //    private RemoteCallbackList<NotifyCallBack> callbackList = new RemoteCallbackList<>();
    private ArrayList<NotifyCallBack> callbackList = new ArrayList<>();
    private Subscription mSubscription;// 统一管理消息订阅
//    private NotifyCallBack notifyLauncherCallBack, notifyVoiceCallBack;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("info", "===================onCreate();");
        Log.i("info", "===================callbackList.size():" + callbackList.size());
        mSubscription = RxBus.getInstance().toObservable()
                .onTerminateDetach()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(busEvent -> {
                    switch (busEvent.getMsgId()) {
                        case BusEvent.MSG_REPORT_FRIDGE: //
                            String json = (String) busEvent.getMsgObject();
                            if (!TextUtils.isEmpty(json)) {
                                sendJson(json, "status");
                            }
                            break;
                    }
                }, throwable -> logUtil.e(TAG, throwable.getMessage()));
    }


    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        Log.i("info", "===================onStart();");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("info", "===================onStartCommand();");
        return super.onStartCommand(intent, flags, startId);
    }

    private void sendJson(String json, String type) {
//        int N = callbackList.beginBroadcast();
//        try {
//            if (type.equals("status")) {
//                if (N - 1 > 0) {
//                    callbackList.getBroadcastItem(N - 1).onNotifyDevice(json, type);
//                }
//            } else if (type.equals("fridge_control")) {
//                callbackList.getBroadcastItem(0).onNotifyDevice(json, type);
//            }
//        } catch (RemoteException e) {
//            e.printStackTrace();
//        }
//        callbackList.finishBroadcast();
        try {
            if (callbackList != null && callbackList.size() != 0) {
                if (type.equals("fridge_control")) {
                    callbackList.get(1).onNotifyDevice(json, type);
                } else if (type.equals("status")) {
                    callbackList.get(0).onNotifyDevice(json, type);
                }
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

//    private void sendExitApp() {
//        if (callbackList != null) {
//            int N = callbackList.getRegisteredCallbackCount();
//            if (N == 1) {
//                Intent intent = new Intent();
//                intent.setAction(BroadcastAction.CLOSE_APP_PROGRESS);
//                sendBroadcast(intent);
//            }
//        }
//    }

    private void sendInitApp() {
//        if (callbackList != null) {
//            int N = callbackList.size();
//            if (N == 1) {
        Intent intent = new Intent();
        intent.setAction(BroadcastAction.INIT_APP_PROGRESS);
        sendBroadcast(intent);
//            }
//        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("info", "===================onDestroy();");
        if (mSubscription != null && !mSubscription.isUnsubscribed()) {
            mSubscription.unsubscribe();
            mSubscription = null;
        }
//        callbackList.kill();
//        callbackList = null;

        callbackList.clear();
        callbackList = null;

        mRCRoomTempCallback = null;
        mRCRoomEnableCallback = null;
        mRCRoomQuickCoolCallback = null;
        mCCRoomTempCallback = null;
        mCCRoomEnableCallback = null;
        mFZRoomTempCallback = null;
        mFZRoomQuickColdCallback = null;
        mIcedDrinkCallBack = null;
        mCCRoomSceneCallback = null;
        mSmartModeCallback = null;
        mHolidayModeCallback = null;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    private IControlService.Stub mBinder = new IControlService.Stub() {
        @Override
        public String onQueryDevice(String type) throws RemoteException {
            return ToolUtil.queryDevice(type);
        }

        @Override
        public String onSetDevice(String cmd, String type) throws RemoteException {
            return setDevice(cmd, type);
        }

//        @Override
//        public String onSetDeviceCall(String cmd, String type, AppCallBack callback) {
//            if (callback != null) {
//                appCallBack = callback;
//            }
//            return setDevice(cmd, type);
//        }

        @Override
        public void registerCallBack(NotifyCallBack cb) throws RemoteException {
            if (cb != null) {
                int size = callbackList.size();
                if (size < 2 && !callbackList.contains(cb)) {
                    callbackList.add(cb);
                    if (callbackList.size() == 1) {
                        sendInitApp();
                    }
                }
            }
        }

        @Override
        public void unregisterCallBack(NotifyCallBack cb) throws RemoteException {
//            if (notifyVoiceCallBack.equals(cb)) {
//                notifyVoiceCallBack = null;
//            } else {
//                notifyLauncherCallBack = null;
//            }
//            if (callbackList.contains(cb)) {
//                callbackList.remove(cb);
//            }
            if (callbackList != null) {
                callbackList.clear();
            }
            sendBroadcast(new Intent(BroadcastAction.CLOSE_APP_PROGRESS));
//            sendExitApp();
        }
    };


    private String setDevice(String cmd, String type) {
        if (cmd != null && type != null) {
            if (type.equals("status") || type.equals("fridge_control")) {
                Result result = JSON.parseObject(cmd, Result.class);
                if (result != null) {
                    ArrayList<StreamBean> control = result.getControl();
                    if (control != null && control.size() != 0) {
                        for (int i = 0; i < control.size(); i++) {
                            StreamBean bean = control.get(i);
                            changeDeviceParamsSet(bean, type);
                        }
                    }
                    RxBus.getInstance().post(BusEvent.MSG_REFRESH_FRIDGE);
                }
            }
            return ToolUtil.queryDevice(type);
        }
        return "";
    }

    private void changeDeviceParamsSet(StreamBean bean, String type) {
        switch (bean.getStream_id()) {
            case FridgeStreamId.SAMRT_MODE:
                doHandSmartMode(bean, type);
                break;
            case FridgeStreamId.HOLIDAY_MODE:
                doHandholidayMode(bean, type);
                break;
            case FridgeStreamId.FRE_TEMP:
//                SerialManager.getInstance().getDataSendInfo().freezing_room_temp_set = Integer.valueOf(bean.getCurrent_value());
                doHandFZRoomTemp(bean, type);
                break;
            case FridgeStreamId.FRI_TEMP:
                doHandRCRoomTemp(bean, type);
//                SerialManager.getInstance().getDataSendInfo().cold_closet_temp_set = Integer.valueOf(bean.getCurrent_value());
                break;
            case FridgeStreamId.VAR_TEMP:
//                SerialManager.getInstance().getDataSendInfo().temp_changeable_room_temp_set = Integer.valueOf(bean.getCurrent_value());
                doHandCCRoomTemp(bean, type);
                break;
            case FridgeStreamId.FRI_POWER:
//                boolean b = Boolean.valueOf(bean.getCurrent_value());
//                SerialManager.getInstance().getDataSendInfo().cold_closet_room_enable = b;
                doHandRCRoomPower(bean, type);
                break;
            case FridgeStreamId.VAR_POWER:
                doHandCCRoomEnablePower(bean, type);
//                SerialManager.getInstance().getDataSendInfo().temp_changeable_room_room_enable = Boolean.valueOf(bean.getCurrent_value());
                break;
            case FridgeStreamId.FASTFRI_MODE:
                doHandRCRoomQuickCoolCheck(bean, type);
//                SerialManager.getInstance().getDataSendInfo().quick_cold = Boolean.valueOf(bean.getCurrent_value());
                break;
            case FridgeStreamId.FASTFRE_MODE:
                doHandFZRoomQuickColdCheck(bean, type);
//                SerialManager.getInstance().getDataSendInfo().quick_freeze = Boolean.valueOf(bean.getCurrent_value());
                break;
        }
    }


    private void ResponeSuccess(String type, String msg) {
        if (type.equals("fridge_control")) {
            sendJson(msg, "fridge_control");
            String json = ToolUtil.queryDevice("fridge_control");
            if (!TextUtils.isEmpty(json)) {
                sendJson(json, "status");
            }
        }
        mRCRoomTempCallback = null;
        mRCRoomEnableCallback = null;
        mRCRoomQuickCoolCallback = null;
        mCCRoomTempCallback = null;
        mCCRoomEnableCallback = null;
        mFZRoomTempCallback = null;
        mFZRoomQuickColdCallback = null;
        mIcedDrinkCallBack = null;
        mCCRoomSceneCallback = null;
        mSmartModeCallback = null;
        mHolidayModeCallback = null;
    }

    private void ResponeFail(String type, int code, String msg) {
        if (type.equals("fridge_control")) {
            sendJson(msg, "fridge_control");
        }
        mRCRoomTempCallback = null;
        mRCRoomEnableCallback = null;
        mRCRoomQuickCoolCallback = null;
        mCCRoomTempCallback = null;
        mCCRoomEnableCallback = null;
        mFZRoomTempCallback = null;
        mFZRoomQuickColdCallback = null;
        mIcedDrinkCallBack = null;
        mCCRoomSceneCallback = null;
        mSmartModeCallback = null;
        mHolidayModeCallback = null;
    }

    private void doHandSmartMode(StreamBean bean, String type) {
        String streamId = bean.getStream_id();
        int value = Integer.valueOf(bean.getCurrent_value());
        if (value == 0)
            return;
        boolean result = ControlManager.getInstance().enableSmartMode(true);
        if (result) {
//            SerialManager.getInstance().getDataSendInfo().mode = SerialInfo.MODE_SMART;
            DeviceParamsSet deviceParamsSetTemp = ControlManager.getInstance().getDataSendInfo();
            DeviceManager.getInstance().sendPropertyFZSetTemp(deviceParamsSetTemp.freezing_room_temp_set);
            DeviceManager.getInstance().sendPropertyRCSetTemp(deviceParamsSetTemp.cold_closet_temp_set);
            DeviceManager.getInstance().sendPropertyCCSetTemp(deviceParamsSetTemp.temp_changeable_room_temp_set);
            DeviceManager.getInstance().sendPropertyRCRoomEnable(true);
            ResponeSuccess(type, "已为您开启智能模式");
        } else {
            String erroMsg = "开启智能模式失败";
            ResponeFail(type, 0, erroMsg);
        }
    }

    private void doHandholidayMode(StreamBean bean, String type) {
        String streamId = bean.getStream_id();
        int value = Integer.valueOf(bean.getCurrent_value());
        if (value == 0)
            return;
        if (mHolidayModeCallback == null) {
            mHolidayModeCallback = new AppCallback<String>() {
                @Override
                public void onSuccess(String data) {
                    log.d(TAG, "onSuccess,deviceParamsSet=" + (ControlManager.getInstance().getDataSendInfo().toString()));
//                    SerialManager.getInstance().getDataSendInfo().mode = SerialInfo.MODE_HOLIDAY;
                    DeviceParamsSet deviceParamsSetTemp = ControlManager.getInstance().getDataSendInfo();
                    DeviceManager.getInstance().sendPropertyFZSetTemp(deviceParamsSetTemp.freezing_room_temp_set);
                    DeviceManager.getInstance().sendPropertyRCSetTemp(deviceParamsSetTemp.cold_closet_temp_set);
                    DeviceManager.getInstance().sendPropertyCCSetTemp(deviceParamsSetTemp.temp_changeable_room_temp_set);
                    DeviceManager.getInstance().sendPropertyRCRoomEnable(true);
                    DeviceManager.getInstance().sendPropertyMode(SerialInfo.MODE_HOLIDAY);
                    ResponeSuccess(type, "已为您开启假日模式");
                }

                @Override
                public void onFail(int errorCode, String msg) {
                    Log.e(TAG, "enableHolidayMode fail,code=" + errorCode + ",msg=" + msg);
                    String erroMsg = "开启假日模式失败";
                    ResponeFail(type, 0, erroMsg);
                }
            };
        }
        boolean result = ControlManager.getInstance().enableHolidayMode(true, mHolidayModeCallback);
    }

    private void doHandFZRoomTemp(StreamBean bean, String type) {
        String streamId = bean.getStream_id();
        int temp = Integer.valueOf(bean.getCurrent_value());
        if (mFZRoomTempCallback == null) {
            mFZRoomTempCallback = new AppCallback<String>() {
                @Override
                public void onSuccess(String data) {
                    log.d(TAG, "onSuccess,deviceParamsSet=" + (ControlManager.getInstance().getDataSendInfo().toString()));
                    DeviceManager.getInstance().sendPropertyFZSetTemp(ControlManager.getInstance().getDataSendInfo().freezing_room_temp_set);
                    DeviceManager.getInstance().sendPropertyMode(SerialInfo.MODE_NULL);
                    String erroMsg = "冷冻室温度设置成功";
                    ResponeSuccess(type, erroMsg);
                }

                @Override
                public void onFail(int errorCode, String msg) {
                    Log.e(TAG, "onTempChange fail,code=" + errorCode + ",msg=" + msg);
                    String erroMsg = "抱歉，冷冻室温度设置失败";
                    ResponeFail(type, errorCode, erroMsg);
                }
            };
        }
        boolean result = ControlManager.getInstance().setRoomTemp(temp, SerialInfo.ROOM_FREEZING_ROOM, mFZRoomTempCallback);
    }

    private void doHandRCRoomTemp(StreamBean bean, String type) {
        String streamId = bean.getStream_id();
        int temp = Integer.valueOf(bean.getCurrent_value());
        if (mRCRoomTempCallback == null) {
            mRCRoomTempCallback = new AppCallback<String>() {
                @Override
                public void onSuccess(String data) {
                    log.d(TAG, "onSuccess,deviceParamsSet=" + (ControlManager.getInstance().getDataSendInfo().toString()));
                    DeviceManager.getInstance().sendPropertyRCSetTemp(ControlManager.getInstance().getDataSendInfo().cold_closet_temp_set);
                    DeviceManager.getInstance().sendPropertyMode(SerialInfo.MODE_NULL);
                    String erroMsg = "冷藏室温度设置成功";
                    ResponeSuccess(type, erroMsg);
                }

                @Override
                public void onFail(int errorCode, String msg) {
                    Log.e(TAG, "onTempChange fail,code=" + errorCode + ",msg=" + msg);
                    String erroMsg = "抱歉，冷藏室温度设置失败";
                    ResponeFail(type, errorCode, erroMsg);
                }
            };
        }
        boolean result = ControlManager.getInstance().setRoomTemp(temp, SerialInfo.ROOM_COLD_COLSET, mRCRoomTempCallback);
    }

    private boolean mflag;

    private void doHandRCRoomPower(StreamBean bean, String type) {
        String streamId = bean.getStream_id();
        boolean enable = getEnable(bean);
        mflag = enable;
        if (mRCRoomEnableCallback == null) {
            mRCRoomEnableCallback = new AppCallback<String>() {
                @Override
                public void onSuccess(String data) {
                    log.d(TAG, "onSuccess,deviceParamsSet=" + (ControlManager.getInstance().getDataSendInfo().toString()));
                    DeviceManager.getInstance().sendPropertyRCSetTemp(ControlManager.getInstance().getDataSendInfo().cold_closet_temp_set);
                    DeviceManager.getInstance().sendPropertyRCRoomEnable(ControlManager.getInstance().getDataSendInfo().cold_closet_room_enable);
                    DeviceManager.getInstance().sendPropertyMode(SerialInfo.MODE_NULL);
                    String msg = "冷藏室关闭成功";
//                    boolean flag = getEnable(bean);
                    if (mflag) {
                        msg = "冷藏室打开成功";
                    }
                    ResponeSuccess(type, msg);
                }

                @Override
                public void onFail(int errorCode, String msg) {
                    Log.e(TAG, "onRoomEnable fail,code=" + errorCode + ",msg=" + msg);
                    String erroMsg = "冷藏室关闭失败";
//                    boolean flag = getEnable(bean);
                    if (mflag) {
                        erroMsg = "冷藏室打开失败";
                    }
                    ResponeFail(type, errorCode, erroMsg);
                }
            };
        }
        boolean result = ControlManager.getInstance().enableRoom(enable, SerialInfo.ROOM_COLD_COLSET, mRCRoomEnableCallback);
    }

    private boolean getEnable(StreamBean bean) {
        String str = bean.getCurrent_value();
        if (str.equals("1"))
            return true;
        return false;
    }

    private void doHandCCRoomTemp(StreamBean bean, String type) {
        String streamId = bean.getStream_id();
        int temp = Integer.valueOf(bean.getCurrent_value());
        if (mCCRoomTempCallback == null) {
            mCCRoomTempCallback = new AppCallback<String>() {
                @Override
                public void onSuccess(String data) {
                    Log.d(TAG, "onSuccess,deviceParamsSet=" + (ControlManager.getInstance().getDataSendInfo().toString()));
                    DeviceManager.getInstance().sendPropertyCCSetTemp(ControlManager.getInstance().getDataSendInfo().temp_changeable_room_temp_set);
                    String erroMsg = "变温室温度设置成功";
                    ResponeSuccess(type, erroMsg);
                }

                @Override
                public void onFail(int errorCode, String msg) {
                    Log.e(TAG, "onTempChange fail,code=" + errorCode + ",msg=" + msg);
                    String erroMsg = "抱歉，变温室温度设置失败";
                    ResponeFail(type, errorCode, erroMsg);
                }
            };
        }
        boolean result = ControlManager.getInstance().setRoomTemp(temp, SerialInfo.ROOM_CHANGEABLE_ROOM, mCCRoomTempCallback);
    }

    private void doHandCCRoomEnablePower(StreamBean bean, String type) {
        String streamId = bean.getStream_id();
        boolean enable = getEnable(bean);

        if (mCCRoomEnableCallback == null) {
            mCCRoomEnableCallback = new AppCallback<String>() {
                @Override
                public void onSuccess(String data) {
                    log.d(TAG, "onSuccess,deviceParamsSet=" + (ControlManager.getInstance().getDataSendInfo().toString()));
                    DeviceManager.getInstance().sendPropertyCCSetTemp(ControlManager.getInstance().getDataSendInfo().temp_changeable_room_temp_set);
                    DeviceManager.getInstance().sendPropertyCCRoomEnable(ControlManager.getInstance().getDataSendInfo().temp_changeable_room_room_enable);
                    String msg = "变温室关闭成功";
                    if (enable) {
                        msg = "变温室打开成功";
                    }
                    ResponeSuccess(type, msg);
                }

                @Override
                public void onFail(int errorCode, String msg) {
                    Log.e(TAG, "onRoomEnable fail,code=" + errorCode + ",msg=" + msg);
                    String erroMsg = "变温室关闭失败";
                    if (enable) {
                        erroMsg = "变温室打开失败";
                    }
                    ResponeFail(type, errorCode, erroMsg);
                }
            };
        }
        boolean result = ControlManager.getInstance().enableRoom(enable, SerialInfo.ROOM_CHANGEABLE_ROOM, mCCRoomEnableCallback);
    }

    private void doHandRCRoomQuickCoolCheck(StreamBean bean, String type) {
        String streamId = bean.getStream_id();
        boolean isChecked = getEnable(bean);
        if (mRCRoomQuickCoolCallback == null) {
            mRCRoomQuickCoolCallback = new AppCallback<String>() {
                @Override
                public void onSuccess(String data) {
                    log.d(TAG, "onSuccess,deviceParamsSet=" + (ControlManager.getInstance().getDataSendInfo().toString()));
                    DeviceParamsSet deviceParamsSetTemp = ControlManager.getInstance().getDataSendInfo();
                    DeviceManager.getInstance().sendPropertyRCSetTemp(deviceParamsSetTemp.cold_closet_temp_set);
                    DeviceManager.getInstance().sendPropertyRCRoomEnable(true);
                    DeviceManager.getInstance().sendPropertyMode(SerialInfo.MODE_NULL);
                    String erroMsg = "关闭速泠模式成功";
                    if (isChecked) {
                        erroMsg = "开启速泠模式成功";
                    }
                    ResponeSuccess(type, erroMsg);
                }

                @Override
                public void onFail(int errorCode, String msg) {
                    Log.e(TAG, "enableQuickCool fail,code=" + errorCode + ",msg=" + msg);
                    String erroMsg = "关闭速泠模式失败";
                    if (isChecked) {
                        erroMsg = "开启速泠模式失败";
                    }
                    ResponeFail(type, errorCode, erroMsg);
                }
            };
        }
        boolean result = ControlManager.getInstance().enableQuickCool(isChecked, mRCRoomQuickCoolCallback);
    }

    private void doHandFZRoomQuickColdCheck(StreamBean bean, String type) {
        String streamId = bean.getStream_id();
        boolean isChecked = getEnable(bean);
        if (mFZRoomQuickColdCallback == null) {
            mFZRoomQuickColdCallback = new AppCallback<String>() {
                @Override
                public void onSuccess(String data) {
                    log.d(TAG, "onSuccess,deviceParamsSet=" + (ControlManager.getInstance().getDataSendInfo().toString()));
                    DeviceManager.getInstance().sendPropertyFZSetTemp(ControlManager.getInstance().getDataSendInfo().freezing_room_temp_set);
                    DeviceManager.getInstance().sendPropertyMode(SerialInfo.MODE_NULL);
                    String erroMsg = "关闭速冻模式成功";
                    if (isChecked) {
                        erroMsg = "开启速冻模式成功";
                    }
                    ResponeSuccess(type, erroMsg);
                }

                @Override
                public void onFail(int errorCode, String msg) {
                    Log.e(TAG, "enableQuickFreeze fail,code=" + errorCode + ",msg=" + msg);
                    String erroMsg = "关闭速冻模式失败";
                    if (isChecked) {
                        erroMsg = "开启速冻模式失败";
                    }
                    ResponeFail(type, errorCode, erroMsg);
                }
            };
        }
        boolean result = ControlManager.getInstance().enableQuickFreeze(isChecked, mFZRoomQuickColdCallback);
    }

}
