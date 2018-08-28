package com.mode.fridge.device;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.DhcpInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.PowerManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.mode.fridge.MyApplication;
import com.mode.fridge.bean.DeviceParamsSet;
import com.mode.fridge.bean.MiIndentify;
import com.mode.fridge.bean.SerialInfo;
import com.mode.fridge.bean.TCRoomScene;
import com.mode.fridge.broadcast.BroadcastAction;
import com.mode.fridge.common.GlobalParams;
import com.mode.fridge.defined.device.ViomiFridge;
import com.mode.fridge.defined.service.FridgeService;
import com.mode.fridge.manager.ControlManager;
import com.mode.fridge.manager.RoomSceneManager;
import com.mode.fridge.manager.SerialManager;
import com.mode.fridge.utils.ApkUtil;
import com.mode.fridge.utils.IpUtils;
import com.mode.fridge.utils.PhoneUtil;
import com.mode.fridge.utils.log;
import com.viomi.common.callback.AppCallback;
import com.xiaomi.miot.host.manager.MiotDeviceConfig;
import com.xiaomi.miot.host.manager.MiotHostManager;
import com.xiaomi.miot.typedef.device.DiscoveryType;
import com.xiaomi.miot.typedef.error.MiotError;
import com.xiaomi.miot.typedef.exception.MiotException;
import com.xiaomi.miot.typedef.listener.CompletedListener;
import com.xiaomi.miot.typedef.listener.OnBindListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by young2 on 2017/1/11.
 */

public class DeviceManager {
    private static final String TAG = DeviceManager.class.getSimpleName();
    private static DeviceManager INSTANCE;
    private ViomiFridge mDevice;
    private final static String TRUN_OFF_STR = "off";
    private final static String TRUN_ON_STR = "on";
    private final static int LIFTER_LIFE = 360 * 24;

    private final static String MODE_NONE = "none";
    private final static String MODE_SMART = "smart";
    private final static String MODE_HOLIDAY = "holiday";
    //    private final static String MODE_QUICK_COLD="quick_cold";
//    private final static String MODE_QUICK_FREEZE="quick_freeze";
    private AppCallback<String> mBindCallback;

    private FridgeService.ActionHandler mActionHandler;
    private FridgeService.PropertyGetter mPropertyGetter;
    private FridgeService.PropertySetter mPropertySetter;

    public static DeviceManager getInstance() {
        synchronized (DeviceManager.class) {
            if (INSTANCE == null) {
                synchronized (DeviceManager.class) {
                    if (INSTANCE == null) {
                        INSTANCE = new DeviceManager();
                    }
                }
            }
        }
        return INSTANCE;
    }

    /***
     * 设备是否已绑定
     * @return
     */
    public boolean isDeviceBind() {
        return GlobalParams.getInstance().isDeviceBindFlag();
    }

    /***
     * 设备绑定状态设置
     * @param flag
     */
    public void setDeviceBindFlag(boolean flag) {
        GlobalParams.getInstance().setDeviceBindFlag(flag);
        if (flag) {//绑定的话，上报属性初始状态
            DeviceParamsSet deviceParamsSet = ControlManager.getInstance().getDataSendInfo();

            sendPropertyMode(deviceParamsSet.mode);
            sendPropertyRCSetTemp(deviceParamsSet.cold_closet_temp_set);
            sendPropertyRCRoomEnable(deviceParamsSet.cold_closet_room_enable);
            sendPropertyFZSetTemp(deviceParamsSet.freezing_room_temp_set);
            if (AppConfig.VIOMI_FRIDGE_V1.equals(DeviceConfig.MODEL) || AppConfig.VIOMI_FRIDGE_V2.equals(DeviceConfig.MODEL)) {
                sendPropertyCCSetTemp(deviceParamsSet.temp_changeable_room_temp_set);
                sendPropertyCCRoomEnable(deviceParamsSet.temp_changeable_room_room_enable);
            }
        }
    }


    /***
     *初始化设备服务
     * @param context
     */
    public void initDevice(Context context, final String miId, AppCallback<String> callback) {
        mBindCallback = callback;
        try {
            MiotHostManager.getInstance().bind(context, new CompletedListener() {
                @Override
                public void onSucceed() {
                    Log.i(TAG, "bind onSucceed");

                    try {
                        MiotHostManager.getInstance().start();
                    } catch (MiotException e) {
                        Log.e(TAG, "start fail!,msg=" + e.getMessage());
                        e.printStackTrace();
                    }

                    try {
                        creatDevice(miId);
                    } catch (MiotException e) {
                        Log.e(TAG, "creatDevice fail!,msg=" + e.getMessage());
                        e.printStackTrace();
                        mBindCallback.onFail(-4, "creatDevice fail!");
                    }

                }

                @Override
                public void onFailed(MiotError miotError) {
                    Log.e(TAG, "bind failed: " + miotError);
                    mBindCallback.onFail(-2, "bind fail!");
                }
            });
        } catch (MiotException e) {
            String error = e.getMessage();
            Log.e(TAG, "getErrorCode:" + e.getErrorCode() + ",getMessage:" + e.getMessage());
            e.printStackTrace();

        }
    }

    /***
     * 解除设备连接
     */

    public boolean unBindDevice(final AppCallback<String> callback) {
        try {
            Log.i(TAG, "reset device!");
            MiotHostManager.getInstance().reset(new CompletedListener() {
                @Override
                public void onSucceed() {
                    Log.e(TAG, "reset succss !");
                    setDeviceBindFlag(false);
                    Intent intent = new Intent(BroadcastAction.ACTION_DEVICE_UNBIND);
                    LocalBroadcastManager.getInstance(MyApplication.getContext()).sendBroadcast(intent);
                }

                @Override
                public void onFailed(MiotError miotError) {
                    Log.e(TAG, "reset error,msg=" + miotError.getMessage());
                    callback.onFail(-1, miotError.getMessage());
                }
            });
        } catch (MiotException e) {
            Log.e(TAG, "stopBindDevice error,msg=" + e.getMessage());
            e.printStackTrace();

            return false;
        }
        return true;
    }

    /***
     * 设备切换绑定账号
     */
    public boolean reBindDevice(final String userId, AppCallback<String> callback) {
        mBindCallback = callback;
        try {
            MiotHostManager.getInstance().reset(new CompletedListener() {
                @Override
                public void onSucceed() {
                    Log.e(TAG, "reset succss 111!");
                    try {
                        creatDevice(userId);
                    } catch (MiotException e) {
                        Log.e(TAG, "creatDevice fail!,msg=" + e.getMessage());
                        e.printStackTrace();
                        mBindCallback.onFail(-7, "creatDevice error!");
                    }
                }

                @Override
                public void onFailed(MiotError miotError) {
                    Log.e(TAG, "reset error,msg=" + miotError.getMessage());
                    mBindCallback.onFail(-5, "reset device fail!");
                }
            });
        } catch (MiotException e) {
            Log.e(TAG, "stopBindDevice error,msg=" + e.getMessage());
            e.printStackTrace();
            mBindCallback.onFail(-6, "reset device error!");
            return false;
        }
        return true;
    }

    /***
     * 停止服务
     * @param context
     */
    public boolean unBindService(Context context) {

        try {
            MiotHostManager.getInstance().stop();
            MiotHostManager.getInstance().unbind(context);
            return true;
        } catch (MiotException e) {
            e.printStackTrace();
        } catch (Exception e) {
            Log.e(TAG, "unbind error,msg=" + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    private void bindRegister() {
        try {
            Log.i(TAG, "registerBindListener");
            MiotHostManager.getInstance().registerBindListener(new OnBindListener() {
                @Override
                public void onBind() {
                    Log.i(TAG, "device bind!");
                    setDeviceBindFlag(true);
                    Intent intent = new Intent(BroadcastAction.ACTION_DEVICE_BIND);
                    LocalBroadcastManager.getInstance(MyApplication.getContext()).sendBroadcast(intent);
                }

                @Override
                public void onUnBind() {
                    Log.e(TAG, "device unbind!");
                    setDeviceBindFlag(false);
                    Intent intent = new Intent(BroadcastAction.ACTION_DEVICE_UNBIND);
                    LocalBroadcastManager.getInstance(MyApplication.getContext()).sendBroadcast(intent);
                }
            }, new CompletedListener() {
                @Override
                public void onSucceed() {
                    Log.i(TAG, "device bind success!");
                }

                @Override
                public void onFailed(MiotError miotError) {
                    Log.e(TAG, "device bind fail!msg=" + miotError.getMessage());
                }
            });
        } catch (MiotException e) {
            Log.e(TAG, "registerBindListener,msg=" + e.getMessage());
            e.printStackTrace();
        }
    }

    private void creatDevice(String userid) throws MiotException {
        MiIndentify miIndentify = PhoneUtil.getMiIdentify();
        log.d(TAG, "did=" + miIndentify.did + ",mac=" + miIndentify.mac + ",token=" + miIndentify.token);
        MiotDeviceInfo info = new MiotDeviceInfo();
        info.deviceId = miIndentify.did;
        info.macAddress = miIndentify.mac;
        info.miotToken = miIndentify.token;
        info.miotInfo = getMiotInfo(userid);
        log.d(TAG, "info.miotInfo=" + info.miotInfo);
        initDevice(info, userid);

    }

    private void initDevice(MiotDeviceInfo info, final String userid) throws MiotException {
        Log.i(TAG, "initDevice");
        /**
         * 1. Initialize Configuration
         */
        MiotDeviceConfig config = new MiotDeviceConfig();
        config.addDiscoveryType(DiscoveryType.MIOT);
        config.friendlyName("Viomi");
        config.deviceId(info.deviceId);
        config.macAddress(info.macAddress);
        config.manufacturer("viomi");
        DeviceConfig.MODEL = PhoneUtil.getDeviceModel();
        config.modelName(DeviceConfig.MODEL);
        config.miotToken(info.miotToken);
        config.miotInfo(info.miotInfo);

        /**
         * 2. Create Device
         */
        mDevice = new ViomiFridge(config);

        /**
         * 3. set Action Handler, setter & getter for property
         */
        if (mActionHandler == null) {
            mActionHandler = new FridgeService.ActionHandler() {
                @Override
                public void onsetOneKeyClean(String OneKeyClean) {
                    log.d(TAG, "onsetOneKeyClean=" + OneKeyClean);
                    if (TRUN_OFF_STR.equals(OneKeyClean)) {
                        ControlManager.getInstance().enableOneKeyClean(false);
                    } else if (TRUN_ON_STR.equals(OneKeyClean)) {
                        ControlManager.getInstance().enableOneKeyClean(true);
                    }
                }

                @Override
                public void onsetRCSetTemp(int RCSetTemp) {
                    log.d(TAG, "onsetRCSetTemp=" + RCSetTemp);
                    ControlManager.getInstance().setRoomTemp(RCSetTemp, SerialInfo.ROOM_COLD_COLSET, null);
                }

                @Override
                public void onsetFCMinTemp(int FCMinTemp) {
                }

                @Override
                public void onsetFilterLifeBase(int FilterLifeBase) {
                    log.d(TAG, "onsetFilterLifeBase=" + FilterLifeBase);
                }

                @Override
                public void onsetSceneChoose(String SceneChoose) {
                    log.d(TAG, "onsetSceneChoose=" + SceneChoose);
                    GlobalParams.getInstance().setSceneStr(SceneChoose);
                    Intent intent11 = new Intent(BroadcastAction.ACTION_REFRE_SCENE);
                    LocalBroadcastManager.getInstance(MyApplication.getContext()).sendBroadcast(intent11);
                }

                @Override
                public void onsetRCMaxTemp(int RCMaxTemp) {

                }

                @Override
                public void onsetPlayMusic(String PlayMusic) {
                    // VoiceManager.getInstance().playMusic();
                }

                @Override
                public void onsetSmartCool(String SmartCool) {
                    log.d(TAG, "onsetSmartCool=" + SmartCool);
                    if (TRUN_OFF_STR.equals(SmartCool)) {
                        ControlManager.getInstance().enableQuickCool(false, null);
                    } else if (TRUN_ON_STR.equals(SmartCool)) {
                        ControlManager.getInstance().enableQuickCool(true, null);
                    }
                }

                @Override
                public void onsetVoiceEnable(String VoiceEnable) {
                    if (VoiceEnable.equals(TRUN_ON_STR)) {
                        GlobalParams.getInstance().setVoiceEnabe(true);
                        //   VoiceManager.getInstance().enableVoice(true);
                    } else if (VoiceEnable.equals(TRUN_OFF_STR)) {
                        GlobalParams.getInstance().setVoiceEnabe(false);
                        //  VoiceManager.getInstance().enableVoice(false);
                    }


                }

                @Override
                public void onsetIndoorTemp(int IndoorTemp) {
                    log.d(TAG, "onsetIndoorTemp=" + IndoorTemp);
                }

                @Override
                public void onsetFCMaxTemp(int FCMaxTemp) {

                }

                @Override
                public void onsetLightUpScreen(String LightUpScreen) {
                    log.d(TAG, "LightUpScreen=" + LightUpScreen);
                    PowerManager pm = (PowerManager) MyApplication.getContext().getSystemService(Context.POWER_SERVICE);
                    //获取电源管理器对象
                    PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "bright");
                    wl.acquire();
                }

                @Override
                public void onsetScreenStatus(boolean ScreenStatus) {

                }

                @Override
                public void onsetComReceiveData(String ComReceiveData) {

                }

                @Override
                public void onsetSceneName(final String SceneName) {
                    log.d(TAG, "onsetSceneName SceneName=" + SceneName);

                    if ("".equals(SceneName)) {//自定义
                        GlobalParams.getInstance().setSceneChoose("");
                        Intent intent11 = new Intent(BroadcastAction.ACTION_REFRE_SCENE);
                        LocalBroadcastManager.getInstance(MyApplication.getContext()).sendBroadcast(intent11);
                        return;
                    }
                    final String name = GlobalParams.getInstance().getSceneChoose();
                    GlobalParams.getInstance().setSceneChoose(SceneName);
                    ControlManager.getInstance().setTCRoomScene(SceneName, new AppCallback<String>() {
                        @Override
                        public void onSuccess(String data) {
                            log.d(TAG, "onsetSceneName success");
                            Intent intent11 = new Intent(BroadcastAction.ACTION_REFRE_SCENE);
                            LocalBroadcastManager.getInstance(MyApplication.getContext()).sendBroadcast(intent11);
                        }

                        @Override
                        public void onFail(int errorCode, String msg) {
                            Log.e(TAG, "onsetSceneName fail");
                            GlobalParams.getInstance().setSceneChoose(name);
                        }
                    });

                }

                @Override
                public void onsetWakeup(String Wakeup) {

//                    VoiceManager.getInstance().wakeupProcess();
                }

                @Override
                public void onsetSmokeAlarm(boolean SmokeAlarm) {

                }

                @Override
                public void onsetOutdoorTemp(String OutdoorTemp) {
                    log.d(TAG, "onsetOutdoorTemp=" + OutdoorTemp);
                }

                @Override
                public void onsetRCMinTemp(int RCMinTemp) {

                }

                @Override
                public void onsetCoolBeverage(String CoolBeverage) {
                    log.d(TAG, "onsetCoolBeverage=" + CoolBeverage);
                    if (TRUN_OFF_STR.equals(CoolBeverage)) {
                        ControlManager.getInstance().enableIcedDrink(false, null);
                    } else if (TRUN_ON_STR.equals(CoolBeverage)) {
                        ControlManager.getInstance().enableIcedDrink(true, null);
                    }
                }

                @Override
                public void onsetIpAddr(String IpAddr) {

                }

                @Override
                public void onsetInvadeAlarm(boolean InvadeAlarm) {

                }

                @Override
                public void onsetMode(String Mode) {
                    log.d(TAG, "onsetMode=" + Mode);
                    if (Mode == null) {
                        Log.e(TAG, "setMode null!");
                        return;
                    }

                    if (Mode.equals(MODE_HOLIDAY)) {
                        ControlManager.getInstance().enableHolidayMode(true, null);
                    } else if (Mode.equals(MODE_SMART)) {
                        ControlManager.getInstance().enableSmartMode(true);
                    }
                }

                @Override
                public void onsetStartDays(int StartDays) {
                    log.d(TAG, "onsetStartDays=" + StartDays);
                }

                @Override
                public void onsetWeather(String Weather) {
//                    VoiceManager.getInstance().getWeather("", new AppCallback<String>() {
//                        @Override
//                        public void onSuccess(String data) {
//                            VoiceManager.getInstance().weatherProcess(data);
//                        }
//
//                        @Override
//                        public void onFail(int errorCode, String msg) {
//
//                        }
//                    });
                }

                @Override
                public void onsetCCMaxTemp(int CCMaxTemp) {

                }

                @Override
                public void onsetFilterLife(int FilterLife) {
                    log.d(TAG, "onsetFilterLife=" + FilterLife);
                    ControlManager.getInstance().setFilterLifeUsedTime(FilterLife);
                }

                @Override
                public void onsetFCSetTemp(int FCSetTemp) {
                    log.d(TAG, "onsetFCSetTemp=" + FCSetTemp);
                    ControlManager.getInstance().setRoomTemp(FCSetTemp, SerialInfo.ROOM_FREEZING_ROOM, null);
                }

                @Override
                public void onsetGasLeakAlarm(boolean GasLeakAlarm) {

                }

                @Override
                public void onsetSmartFreeze(String SmartFreeze) {
                    log.d(TAG, "onsetSmartFreeze=" + SmartFreeze);
                    if (TRUN_OFF_STR.equals(SmartFreeze)) {
                        ControlManager.getInstance().enableQuickFreeze(false, null);
                    } else if (TRUN_ON_STR.equals(SmartFreeze)) {
                        ControlManager.getInstance().enableQuickFreeze(true, null);
                    }
                }


                @Override
                public void onsetStopMusic(String StopMusic) {
                    Intent intent3 = new Intent(BroadcastAction.ACTION_STOP_MUSIC);
                    LocalBroadcastManager.getInstance(MyApplication.getContext()).sendBroadcast(intent3);
                }

                @Override
                public void onsetCCMinTemp(int CCMinTemp) {

                }

                @Override
                public void onsetSceneAll(String SceneAll) {
                }

                @Override
                public void onsetPlayLightMusic(String PlayLightMusic) {
                    // VoiceManager.getInstance().playLightMusic();

                }

                @Override
                public void onsetCCSetTemp(int CCSetTemp) {
                    log.d(TAG, "onsetCCSetTemp=" + CCSetTemp);
                    ControlManager.getInstance().setRoomTemp(CCSetTemp, SerialInfo.ROOM_CHANGEABLE_ROOM, null);
                }

                @Override
                public void onsetSceneMode(int SceneMode) {

                }

                @Override
                public void onsetRCSet(String RCSet) {
                    log.d(TAG, "onsetRCSet=" + RCSet);
                    if (TRUN_OFF_STR.equals(RCSet)) {
                        ControlManager.getInstance().enableRoom(false, SerialInfo.ROOM_COLD_COLSET, null);
                    } else if (TRUN_ON_STR.equals(RCSet)) {
                        ControlManager.getInstance().enableRoom(true, SerialInfo.ROOM_COLD_COLSET, null);
                    }
                }

                @Override
                public void onsetWaterAlarm(boolean WaterAlarm) {

                }

                @Override
                public void onsetCCSet(String CCSet) {
                    log.d(TAG, "onsetCCSet=" + CCSet);
                    if (TRUN_OFF_STR.equals(CCSet)) {
                        ControlManager.getInstance().enableRoom(false, SerialInfo.ROOM_CHANGEABLE_ROOM, null);
                    } else if (TRUN_ON_STR.equals(CCSet)) {
                        ControlManager.getInstance().enableRoom(true, SerialInfo.ROOM_CHANGEABLE_ROOM, null);
                    }
                }
            };
        }

        if (mPropertyGetter == null) {
            mPropertyGetter = new FridgeService.PropertyGetter() {
                @Override
                public String getMode() {
                    String modeStr = "";
                    int mode = ControlManager.getInstance().getDataSendInfo().mode;
                    switch (mode) {
                        case 0:
                            return MODE_NONE;
                        case SerialInfo.MODE_HOLIDAY:
                            return MODE_HOLIDAY;
                        case SerialInfo.MODE_SMART:
                            return MODE_SMART;
                        default:
                            return MODE_NONE;
                    }
                }

                @Override
                public int getRCSetTemp() {
                    return ControlManager.getInstance().getDataSendInfo().cold_closet_temp_set;
                }

                @Override
                public int getRCMinTemp() {
                    return 2;
                }

                @Override
                public int getRCMaxTemp() {
                    return 8;
                }

                @Override
                public int getCCSetTemp() {
                    return ControlManager.getInstance().getDataSendInfo().temp_changeable_room_temp_set;
                }

                @Override
                public int getCCMinTemp() {
                    return -18;
                }

                @Override
                public int getCCMaxTemp() {
                    int ccMaxTemp = 8;
                    if (DeviceConfig.MODEL.equals(AppConfig.VIOMI_FRIDGE_V2)) {
                        ccMaxTemp = -3;
                    }
                    return ccMaxTemp;
                }

                @Override
                public int getFCSetTemp() {
                    return ControlManager.getInstance().getDataSendInfo().freezing_room_temp_set;
                }

                @Override
                public int getFCMinTemp() {
                    int fcMinTemp = -25;
                    if (DeviceConfig.MODEL.equals(AppConfig.VIOMI_FRIDGE_V2)) {
                        fcMinTemp = -24;
                    }
                    return fcMinTemp;
                }

                @Override
                public int getFCMaxTemp() {
                    int fcMaxTemp = -15;
                    if (DeviceConfig.MODEL.equals(AppConfig.VIOMI_FRIDGE_V2)) {
                        fcMaxTemp = -16;
                    }
                    return fcMaxTemp;
                }

                @Override
                public int getRCRealTemp() {
                    return ControlManager.getInstance().getDataReceiveInfo().cold_closet_temp_real;
                }

                @Override
                public int getCCRealTemp() {
                    return ControlManager.getInstance().getDataReceiveInfo().temp_changeable_room_temp_real;
                }

                @Override
                public int getFCRealTemp() {
                    return ControlManager.getInstance().getDataReceiveInfo().freezing_room_temp_real;
                }

                @Override
                public String getRCSet() {
                    if (!ControlManager.getInstance().getDataSendInfo().cold_closet_room_enable) {
                        return TRUN_OFF_STR;
                    } else {
                        return TRUN_ON_STR;
                    }

                }

                @Override
                public String getCCSet() {
                    if (!ControlManager.getInstance().getDataSendInfo().temp_changeable_room_room_enable) {
                        return TRUN_OFF_STR;
                    } else {
                        return TRUN_ON_STR;
                    }
                }

                @Override
                public String getOneKeyClean() {
                    String str;
                    if (ControlManager.getInstance().isOneKeyCleanRunning()) {
                        str = TRUN_ON_STR;
                    } else {
                        str = TRUN_OFF_STR;
                    }
                    return str;
                }

                @Override
                public int getFilterLifeBase() {
                    return LIFTER_LIFE;
                }

                @Override
                public int getFilterLife() {
                    return ControlManager.getInstance().getFilterLifeUsedTime();
                }

                @Override
                public String getSceneAll() {
                    ArrayList<TCRoomScene> sceneList = RoomSceneManager.getInstance().getSceneAllList();
                    if (sceneList == null || sceneList.size() == 0) {
                        Log.e(TAG, "set input error!");
                        return "";
                    }
                    String str = "";
                    for (int i = 0; i < sceneList.size(); i++) {
                        str += sceneList.get(i).name + ",";
                        if (i != sceneList.size() - 1) {
                            str += sceneList.get(i).value + ";";
                        } else {
                            str += sceneList.get(i).value;
                        }
                    }
                    return str;
                }

                @Override
                public String getSceneChoose() {
                    String str = GlobalParams.getInstance().getSceneStr();
                    log.d(TAG, "getSceneChoose=" + str);
                    return str;
                }

                @Override
                public String getSceneName() {
                    String name = GlobalParams.getInstance().getSceneChoose();
                    log.d(TAG, "getSceneName=" + name);
                    return name;
                }

                @Override
                public String getOutdoorTemp() {
                    return GlobalParams.getInstance().getOutdoorTemp();
                }

                @Override
                public int getIndoorTemp() {
                    return ControlManager.getInstance().getDataReceiveInfo().indoor_temp;
                }

                @Override
                public int getStartDays() {
                    return GlobalParams.getInstance().getStartHour() / 24;
                }

                @Override
                public int getError() {
                    return ControlManager.getInstance().getDataReceiveInfo().error;
                }

                @Override
                public String getVoiceEnable() {
                    if (GlobalParams.getInstance().isVoiceEnabe()) {
                        return TRUN_ON_STR;
                    } else {
                        return TRUN_OFF_STR;
                    }
                }

                @Override
                public String getWakeup() {
                    return TRUN_ON_STR;
                }

                @Override
                public String getLightUpScreen() {
                    return TRUN_ON_STR;
                }

                @Override
                public String getWeather() {
                    return TRUN_ON_STR;
                }

                @Override
                public String getPlayMusic() {
                    return TRUN_ON_STR;
                }

                @Override
                public String getPlayLightMusic() {
                    return TRUN_ON_STR;
                }

                @Override
                public String getStopMusic() {
                    return TRUN_ON_STR;
                }


                @Override
                public String getSmartCool() {
                    String str;
                    if (ControlManager.getInstance().getDataSendInfo().quick_cold) {
                        str = TRUN_ON_STR;
                    } else {
                        str = TRUN_OFF_STR;
                    }
                    return str;
                }

                @Override
                public String getSmartFreeze() {
                    String str;
                    if (ControlManager.getInstance().getDataSendInfo().quick_freeze) {
                        str = TRUN_ON_STR;
                    } else {
                        str = TRUN_OFF_STR;
                    }
                    return str;
                }

                @Override
                public String getCoolBeverage() {
                    String str;
                    if (ControlManager.getInstance().isIcedDrink()) {
                        str = TRUN_ON_STR;
                    } else {
                        str = TRUN_OFF_STR;
                    }
                    return str;
                }

                @Override
                public String getComReceiveData() {
                    return SerialManager.getInstance().getDataReceiveStr();
                }

                @Override
                public int getSceneMode() {
                    return 0;
                }

                @Override
                public boolean getScreenStatus() {
                    return false;
                }

                @Override
                public boolean getWaterAlarm() {
                    return false;
                }

                @Override
                public boolean getSmokeAlarm() {
                    return false;
                }

                @Override
                public boolean getInvadeAlarm() {
                    return false;
                }

                @Override
                public boolean getGasLeakAlarm() {
                    return false;
                }

                @Override
                public String getIpAddr() {
                    return PhoneUtil.getIpAddr();
                }

            };
        }

        if (mPropertySetter == null) {
            mPropertySetter = new FridgeService.PropertySetter() {
                @Override
                public void setMode(String value) {
                    log.d(TAG, "setMode=" + value);

                }

                @Override
                public void setRCSetTemp(int value) {
                    log.d(TAG, "setClodClosetSetTemp=" + value);

                }

                @Override
                public void setRCMinTemp(int value) {

                }

                @Override
                public void setRCMaxTemp(int value) {

                }

                @Override
                public void setCCSetTemp(int value) {
                    log.d(TAG, "setChangeableRoomSetTemp=" + value);

                }

                @Override
                public void setCCMinTemp(int value) {

                }

                @Override
                public void setCCMaxTemp(int value) {

                }

                @Override
                public void setFCSetTemp(int value) {
                    log.d(TAG, "setFreezingRoomSetTemp=" + value);

                }

                @Override
                public void setFCMinTemp(int value) {

                }

                @Override
                public void setFCMaxTemp(int value) {

                }

                @Override
                public void setRCSet(String value) {
                    log.d(TAG, "setColdCloset=" + value);

                }

                @Override
                public void setCCSet(String value) {
                    log.d(TAG, "setChangeableRoom=" + value);

                }

                @Override
                public void setOneKeyClean(String value) {
                    log.d(TAG, "setOneKeyClean=" + value);

                }

                @Override
                public void setFilterLifeBase(int value) {

                }

                @Override
                public void setFilterLife(int value) {
                    log.d(TAG, "setFilterLife=" + value);

                }

                @Override
                public void setSceneAll(String value) {

                }

                @Override
                public void setSceneChoose(String value) {

                }

                @Override
                public void setSceneName(String value) {

                }

                @Override
                public void setOutdoorTemp(String value) {

                }

                @Override
                public void setIndoorTemp(int value) {

                }

                @Override
                public void setStartDays(int value) {

                }

                @Override
                public void setVoiceEnable(String value) {

                }

                @Override
                public void setWakeup(String value) {

                }

                @Override
                public void setLightUpScreen(String value) {

                }

                @Override
                public void setWeather(String value) {

                }

                @Override
                public void setPlayMusic(String value) {

                }

                @Override
                public void setPlayLightMusic(String value) {

                }

                @Override
                public void setStopMusic(String value) {

                }


                @Override
                public void setSmartCool(String value) {

                }

                @Override
                public void setSmartFreeze(String value) {

                }

                @Override
                public void setCoolBeverage(String value) {

                }

                @Override
                public void setComReceiveData(String value) {

                }

                @Override
                public void setSceneMode(int value) {

                }

                @Override
                public void setScreenStatus(boolean value) {

                }

                @Override
                public void setWaterAlarm(boolean value) {

                }

                @Override
                public void setSmokeAlarm(boolean value) {

                }

                @Override
                public void setInvadeAlarm(boolean value) {

                }

                @Override
                public void setGasLeakAlarm(boolean value) {

                }

                @Override
                public void setIpAddr(String value) {

                }
            };
        }


        mDevice.FridgeService().setHandler(mActionHandler, mPropertyGetter, mPropertySetter);

        /**
         * 4. Start
         */
        Log.i(TAG, " mDevice.start");
        mDevice.start(new CompletedListener() {
            @Override
            public void onSucceed() {
                Log.i(TAG, "start onSucceed,userid=" + userid);
                if (userid != null) {
                    setDeviceBindFlag(true);
                }
                if (mBindCallback != null) {
                    mBindCallback.onSuccess(null);
                }
            }

            @Override
            public void onFailed(MiotError miotError) {
                Log.e(TAG, "start onFailed: " + miotError);
                if (mBindCallback != null) {
                    mBindCallback.onFail(-5, "mDevice.start fail!,msg=" + miotError.getMessage());
                }
            }
        });

        //注册监听设备状态
        bindRegister();

    }

    /***
     * 设置失败事件
     */
    public void sendSetFail(String sendParam, String receiveParam) {

        String method = "event.set_fail";
        String params = "[" + sendParam + ";" + receiveParam + "]";
        Log.i(TAG, "method=" + method + ",params=" + params);
        if (mDevice != null) {
            try {
                mDevice.send(method, params);
            } catch (MiotException e) {
                e.printStackTrace();
            }
        }
    }

    /***
     * 异常发生事件
     */
    public void sendFaultHappen(int error) {

        String method = "event.fault_happen";
        String params = "[" + error + "]";
        Log.i(TAG, "method=" + method + ",params=" + params);
        if (mDevice != null) {
            try {
                mDevice.send(method, params);
            } catch (MiotException e) {
                e.printStackTrace();
            }
        }
    }

    /***
     * 异常恢复事件
     */
    public void sendFaultFix(int error) {
        String method = "event.fault_fixed";
        String params = "[" + error + "]";
        Log.i(TAG, "method=" + method + ",params=" + params);
        if (mDevice != null) {
            try {
                mDevice.send(method, params);
            } catch (MiotException e) {
                e.printStackTrace();
            }
        }
    }

    /***
     * 滤芯用尽
     */
    public void sendFilterLifeEnd() {
        String method = "event.filter_life_end";
        String params = "[true]";
        Log.i(TAG, "method=" + method + ",params=" + params);
        if (mDevice != null) {
            try {
                mDevice.send(method, params);
            } catch (MiotException e) {
                e.printStackTrace();
            }
        }
    }

    /***
     * 滤芯即将用尽
     */
    public void sendFilterLifeLow() {
        String method = "event.filter_life_low";
        String params = "[true]";
        Log.i(TAG, "method=" + method + ",params=" + params);
        if (mDevice != null) {
            try {
                mDevice.send(method, params);
            } catch (MiotException e) {
                e.printStackTrace();
            }
        }

    }


    /***
     * 滤芯复位
     */
    public void sendFilteLifeReset() {
        String method = "event.filter_life_reset";
        String params = "[true]";
        Log.i(TAG, "method=" + method + ",params=" + params);
        if (mDevice != null) {
            try {
                mDevice.send(method, params);
            } catch (MiotException e) {
                e.printStackTrace();
            }
        }
    }

    /***
     * 定时时间到
     */
    public void sendTimerEnd(int minute) {
        minute = minute / 60;
        int hour = minute / 60;
        int min = minute % 60;
        String method = "event.fixed_time_end";
        String params = "[" + hour + "," + min + "]";
        Log.i(TAG, "method=" + method + ",params=" + params);
        if (mDevice != null) {
            try {
                mDevice.send(method, params);
            } catch (MiotException e) {
                e.printStackTrace();
            }
        }
    }


    /***
     * 发送食材到期事件
     * @param name  食品名称
     * @param startTime 开始保存时间 时间戳 单位秒
     * @param endTime  到期时间  时间戳 单位秒
     * @param room   0:冷藏室；1：变温室；2：冷冻室
     */
    public void sendFoodExpire(String name, long startTime, int endTime, int room) {
        String method = "event.food_expire";
        String params = "[" + name + "," + startTime + "," + endTime + "," + room + "]";
        Log.i(TAG, "method=" + method + ",params=" + params);
        if (mDevice != null) {
            try {
                mDevice.send(method, params);
            } catch (MiotException e) {
                e.printStackTrace();
            }
        }
    }

    /***
     * 智冷模式启动
     */
    public void sendSmartCoolStart() {

    }

    /***
     * 智冷模式结束
     */
    public void sendSmartCoolEnd() {

    }

    /***
     * 智冻模式启动
     */
    public void sendSmartFreezeStart() {

    }

    /***
     * 智冻模式结束
     */
    public void sendSmartFreezeEnd() {

    }


    /***
     * 属性上报--模式
     */
    public void sendPropertyMode(int mode) {
        Log.i(TAG, "mode property upload!,params=" + mode);
        if (mDevice != null) {
            try {
                String modeStr = MODE_NONE;
                if (mode == SerialInfo.MODE_SMART) {
                    modeStr = MODE_SMART;
                } else if (mode == SerialInfo.MODE_HOLIDAY) {
                    modeStr = MODE_HOLIDAY;
                }
                mDevice.FridgeService().Mode().setValue(modeStr);
                mDevice.sendEvents();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /***
     * 属性上报--变温室开关
     */
    public void sendPropertyCCRoomEnable(boolean enable) {
        Log.i(TAG, "CCRoomEnable enable property upload!,params=" + enable);
        if (mDevice != null) {
            try {
                if (enable) {
                    mDevice.FridgeService().CCSet().setValue(TRUN_ON_STR);
                } else {
                    mDevice.FridgeService().CCSet().setValue(TRUN_OFF_STR);
                }
                mDevice.sendEvents();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /***
     * 属性上报--冷藏室开关
     */
    public void sendPropertyRCRoomEnable(boolean enable) {
        Log.i(TAG, "RCRoomEnable enable property upload!,params=" + enable);
        if (mDevice != null) {
            try {
                if (enable) {
                    mDevice.FridgeService().RCSet().setValue(TRUN_ON_STR);
                } else {
                    mDevice.FridgeService().RCSet().setValue(TRUN_OFF_STR);
                }
                mDevice.sendEvents();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /***
     * 属性上报--变温室设置温度
     */
    public void sendPropertyCCSetTemp(int temp) {
        Log.i(TAG, "CCSetTemp enable property upload!,params=" + temp);
        if (mDevice != null) {
            try {
                mDevice.FridgeService().CCSetTemp().setValue(temp);
                mDevice.sendEvents();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /***
     * 属性上报--冷藏室设置温度
     */
    public void sendPropertyRCSetTemp(int temp) {
        Log.i(TAG, "CCSetTemp enable property upload!,params=" + temp);
        if (mDevice != null) {
            try {
                mDevice.FridgeService().RCSetTemp().setValue(temp);
                mDevice.sendEvents();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /***
     * 属性上报--冷冻室设置温度
     */
    public void sendPropertyFZSetTemp(int temp) {
        Log.i(TAG, "FZSetTemp enable property upload!,params=" + temp);
        if (mDevice != null) {
            try {
                mDevice.FridgeService().FCSetTemp().setValue(temp);
                mDevice.sendEvents();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /***
     *设定设备信息
     * @return
     */
    private String getMiotInfo(String userId) {
        @SuppressLint("WifiManagerLeak") WifiManager wifiMng = (WifiManager) MyApplication.getContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wi = wifiMng.getConnectionInfo();
        DhcpInfo di = wifiMng.getDhcpInfo();
        JSONObject jo = new JSONObject();

        try {
            jo.put("method", "_internal.info");
            jo.put("partner_id", "");
            JSONObject jop = new JSONObject();
            jop.put("hw_ver", "Android");
            jop.put("fw_ver", ApkUtil.getVersion());
            JSONObject jopa = new JSONObject();
            jopa.put("ssid", wi.getSSID());
            jopa.put("bssid", wi.getBSSID());
            jop.put("ap", jopa);
            JSONObject jopn = new JSONObject();
            jopn.put("localIp", IpUtils.intToIp(di.ipAddress));
            jopn.put("mask", IpUtils.intToIp(di.netmask));
            jopn.put("gw", IpUtils.intToIp(di.gateway));
            jop.put("netif", jopn);
            if (userId != null) {
                jop.put("uid", userId);
            }
            jo.put("params", jop);
            return jo.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }


}
