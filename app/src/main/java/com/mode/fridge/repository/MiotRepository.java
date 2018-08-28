package com.mode.fridge.repository;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import com.miot.api.MiotManager;
import com.mode.fridge.AppConstants;
import com.mode.fridge.MyApplication;
import com.mode.fridge.bean.MiIdentification;
import com.mode.fridge.bean.QRCodeBase;
import com.mode.fridge.common.rxbus.BusEvent;
import com.mode.fridge.common.rxbus.RxBus;
import com.mode.fridge.defined.device.ViomiFridge;
import com.mode.fridge.defined.service.FridgeService;
import com.mode.fridge.device.DeviceConfig;
import com.mode.fridge.preference.LoginPreference;
import com.mode.fridge.utils.ToolUtil;
import com.mode.fridge.utils.WakeAndLock;
import com.mode.fridge.utils.logUtil;
import com.xiaomi.miot.host.manager.MiotDeviceConfig;
import com.xiaomi.miot.host.manager.MiotHostManager;
import com.xiaomi.miot.typedef.device.DiscoveryType;
import com.xiaomi.miot.typedef.error.MiotError;
import com.xiaomi.miot.typedef.exception.MiotException;
import com.xiaomi.miot.typedef.listener.CompletedListener;
import com.xiaomi.miot.typedef.listener.OnBindListener;

import org.json.JSONException;
import org.json.JSONObject;

import rx.Observable;
import rx.Subscriber;


/**
 * MiIOT 相关 Api
 * Created by William on 2018/1/8.
 */
public class MiotRepository {
    private static final String TAG = MiotRepository.class.getSimpleName();
    private static MiotRepository mInstance;// 实例
    private ViomiFridge mDevice;
    //    private FridgeService.ActionHandler mActionHandler;
//    private FridgeService.PropertySetter mPropertySetter;
//    private FridgeService.PropertyGetter mPropertyGetter;
    public final int FILTER_LIFE_BASE = 360 * 24;// 滤芯总寿命（单位: 小时）
    final String MODE_NONE = "none";// 无模式
    final String SWITCH_ON = "on";// 开
    private final String SWITCH_OFF = "off";// 关

    public static MiotRepository getInstance() {
        if (mInstance == null) {
            synchronized (MiotRepository.class) {
                if (mInstance == null) {
                    mInstance = new MiotRepository();
                }
            }
        }
        return mInstance;
    }

    /**
     * 初始化设备
     *
     * @param qrCodeBase: 小米 id
     */
    public void initDevice(Context context, QRCodeBase qrCodeBase) {

        try {
            MiotHostManager.getInstance().bind(context, new CompletedListener() {
                @Override
                public void onSucceed() {
                    logUtil.d(TAG, "bind device success");
                    try {
                        MiotHostManager.getInstance().start();
                        logUtil.d(TAG, "start device success");
                    } catch (MiotException e) {
                        e.printStackTrace();
                        logUtil.e(TAG, e.getMessage());
                    }
                    try {
                        createDevice(qrCodeBase, null);// 创建设备
                    } catch (Exception e) {
                        e.printStackTrace();
                        logUtil.e(TAG, e.toString());
                    }
                }

                @Override
                public void onFailed(MiotError miotError) {
                    logUtil.e(TAG, miotError.getMessage());
                }
            });
        } catch (MiotException e) {
            e.printStackTrace();
            logUtil.e(TAG, e.getMessage());
        }
    }

    /**
     * 创建设备
     */
    private void createDevice(QRCodeBase qrCodeBase, Subscriber<? super QRCodeBase> subscriber) {
        MiIdentification identification = ToolUtil.getMiIdentification();
        if (qrCodeBase == null) identification.setMiInfo(null);
        else
            identification.setMiInfo(getMiIotInfo(qrCodeBase.getLoginQRCode().getUserInfo().getMiUserInfo().getMiId()));
        // 初始化配置
        MiotDeviceConfig config = new MiotDeviceConfig();
        config.addDiscoveryType(DiscoveryType.MIOT);
        config.friendlyName("Viomi");
        config.deviceId(identification.getDeviceId());
        config.macAddress(identification.getMac());
        config.manufacturer("viomi");
//        config.modelName(FridgePreference.getInstance().getModel());
        config.modelName(DeviceConfig.MODEL);
        config.miotToken(identification.getMiToken());
        config.miotInfo(identification.getMiInfo());
        // 创建设备
        mDevice = new ViomiFridge(config);
        // set Action Handler, setter & getter for property
//        if (mActionHandler == null) {
//            mActionHandler = new FridgeService.ActionHandler() {
//                @Override
//                public void onsetIndoorTemp(int IndoorTemp) {
//                    logUtil.d(TAG, "onsetIndoorTemp = " + IndoorTemp);
//                }
//
//                @Override
//                public void onsetFCMaxTemp(int FCMaxTemp) {
//                    logUtil.d(TAG, "onsetFCMaxTemp = " + FCMaxTemp);
//                }
//
//                @Override
//                public void onsetFCSetTemp(int FCSetTemp) {
//                    logUtil.d(TAG, "onsetFCSetTemp = " + FCSetTemp);
//                    FridgeRepository.getInstance().setFCRoomTemp(FCSetTemp, null);
//                }
//
//                @Override
//                public void onsetWulian(String Wulian) {
//
//                }
//
//                @Override
//                public void onsetGasLeakAlarm(boolean GasLeakAlarm) {
//                    logUtil.d(TAG, "onsetGasLeakAlarm = " + GasLeakAlarm);
//                    RxBus.getInstance().post(BusEvent.MSG_WARN_ALARM, 3);
//                }
//
//                @Override
//                public void onsetSmartFreeze(String SmartFreeze) {
//                    logUtil.d(TAG, "onsetSmartFreeze = " + SmartFreeze);
//                    boolean enable = SmartFreeze.equals(SWITCH_ON);
//                    FridgeRepository.getInstance().setSmartFreeze(enable, null);
//                }
//
//                @Override
//                public void onsetPlayMusic(String PlayMusic) {
//                    logUtil.d(TAG, "onsetPlayMusic = " + PlayMusic);
//                }
//
//                @Override
//                public void onsetSmartCool(String SmartCool) {
//                    logUtil.d(TAG, "onsetSmartCool = " + SmartCool);
//                    boolean enable = SmartCool.equals(SWITCH_ON);
//                    FridgeRepository.getInstance().setSmartCool(enable, null);
//                }
//
//                @Override
//                public void onsetStartDays(int StartDays) {
//                    logUtil.d(TAG, "onsetStartDays = " + StartDays);
//                }
//
//                @Override
//                public void onsetWeather(String Weather) {
//                    logUtil.d(TAG, "onsetWeather = " + Weather);
//                }
//
//                @Override
//                public void onsetCCMaxTemp(int CCMaxTemp) {
//                    logUtil.d(TAG, "onsetCCMaxTemp = " + CCMaxTemp);
//                }
//
//                @Override
//                public void onsetFilterLife(int FilterLife) {
//                    logUtil.d(TAG, "onsetFilterLife = " + FilterLife);
//                    FridgePreference.getInstance().saveUsedFilterLife(FilterLife);
//                }
//
//                @Override
//                public void onsetCCSetTemp(int CCSetTemp) {
//                    logUtil.d(TAG, "onsetCCSetTemp = " + CCSetTemp);
//                    FridgeRepository.getInstance().setCCRoomTemp(CCSetTemp, null);
//                }
//
//                @Override
//                public void onsetSceneMode(int SceneMode) {
//                    logUtil.d(TAG, "onsetSceneMode = " + SceneMode);
//                    WakeAndLock wakeAndLock = new WakeAndLock();
//                    if (SceneMode == 0) { // 回家模式
//                        wakeAndLock.screenOn();
//                    } else if (SceneMode == 1) { // 离家模式
//                        wakeAndLock.screenOff();
//                    } else if (SceneMode == 2) { // 睡眠模式
//                        wakeAndLock.screenOff();
//                    } else if (SceneMode == 3) { // 起床模式
//                        wakeAndLock.screenOn();
//                    }
//                }
//
//                @Override
//                public void onsetRCSet(String RCSet) {
//                    logUtil.d(TAG, "onsetRCSet = " + RCSet);
//                    boolean enable = RCSet.equals(SWITCH_ON);
//                    FridgeRepository.getInstance().setRCSet(enable, null);
//                }
//
//                @Override
//                public void onsetWaterAlarm(boolean WaterAlarm) {
//                    logUtil.d(TAG, "onsetWaterAlarm = " + WaterAlarm);
//                    RxBus.getInstance().post(BusEvent.MSG_WARN_ALARM, 0);
//                }
//
//                @Override
//                public void onsetVoiceEnable(String VoiceEnable) {
//                    logUtil.d(TAG, "onsetVoiceEnable = " + VoiceEnable);
//                }
//
//                @Override
//                public void onsetLightUpScreen(String LightUpScreen) {
//                    logUtil.d(TAG, "onsetLightUpScreen = " + LightUpScreen);
//                }
//
//                @Override
//                public void onsetScreenStatus(boolean ScreenStatus) {
//                    logUtil.d(TAG, "onsetScreenStatus = " + ScreenStatus);
//                }
//
//                @Override
//                public void onsetComReceiveData(String ComReceiveData) {
//                    logUtil.d(TAG, "onsetComReceiveData = " + ComReceiveData);
//                }
//
//                @Override
//                public void onsetSceneName(String SceneName) {
//                    logUtil.d(TAG, "onsetSceneName = " + SceneName);
//                    // TODO 缓存设置场景
//                }
//
//                @Override
//                public void onsetWakeup(String Wakeup) {
//                    logUtil.d(TAG, "onsetWakeup = " + Wakeup);
//                }
//
//                @Override
//                public void onsetSmokeAlarm(boolean SmokeAlarm) {
//                    logUtil.d(TAG, "onsetSmokeAlarm = " + SmokeAlarm);
//                    RxBus.getInstance().post(BusEvent.MSG_WARN_ALARM, 2);
//                }
//
//                @Override
//                public void onsetMode(String Mode) {
//                    logUtil.d(TAG, "onsetMode = " + Mode);
//                    Mode = Mode == null ? MODE_NONE : Mode;
//                    FridgeRepository.getInstance().setMode(Mode, null);
//                }
//
//                @Override
//                public void onsetStopMusic(String StopMusic) {
//                    logUtil.d(TAG, "onsetStopMusic = " + StopMusic);
//                }
//
//                @Override
//                public void onsetCCMinTemp(int CCMinTemp) {
//                    logUtil.d(TAG, "onsetCCMinTemp = " + CCMinTemp);
//                }
//
//                @Override
//                public void onsetSceneAll(String SceneAll) {
//                    logUtil.d(TAG, "onsetSceneAll = " + SceneAll);
//                }
//
//                @Override
//                public void onsetPlayLightMusic(String PlayLightMusic) {
//                    logUtil.d(TAG, "onsetPlayLightMusic = " + PlayLightMusic);
//                }
//
//                @Override
//                public void onsetOneKeyClean(String OneKeyClean) {
//                    logUtil.d(TAG, "onsetOneKeyClean = " + OneKeyClean);
//                    boolean enable = OneKeyClean.equals(SWITCH_ON);
//                    FridgeRepository.getInstance().setOneKeyClean(enable);
//                }
//
//                @Override
//                public void onsetRCSetTemp(int RCSetTemp) {
//                    logUtil.d(TAG, "onsetRCSetTemp = " + RCSetTemp);
//                    FridgeRepository.getInstance().setRCRoomTemp(RCSetTemp, null);
//                }
//
//                @Override
//                public void onsetFCMinTemp(int FCMinTemp) {
//                    logUtil.d(TAG, "onsetFCMinTemp = " + FCMinTemp);
//                }
//
//                @Override
//                public void onsetFilterLifeBase(int FilterLifeBase) {
//                    logUtil.d(TAG, "onsetFilterLifeBase = " + FilterLifeBase);
//                }
//
//                @Override
//                public void onsetSceneChoose(String SceneChoose) {
//                    logUtil.d(TAG, "onsetSceneChoose = " + SceneChoose);
//                    // TODO 缓存选择场景
//                }
//
//                @Override
//                public void onsetRCMaxTemp(int RCMaxTemp) {
//                    logUtil.d(TAG, "onsetRCMaxTemp = " + RCMaxTemp);
//                }
//
//                @Override
//                public void onsetOutdoorTemp(String OutdoorTemp) {
//                    logUtil.d(TAG, "onsetOutdoorTemp = " + OutdoorTemp);
//                }
//
//                @Override
//                public void onsetRCMinTemp(int RCMinTemp) {
//                    logUtil.d(TAG, "onsetRCMinTemp = " + RCMinTemp);
//                }
//
//                @Override
//                public void onsetCoolBeverage(String CoolBeverage) {
//                    logUtil.d(TAG, "onsetCoolBeverage = " + CoolBeverage);
//                    boolean enable = CoolBeverage.equals(SWITCH_ON);
//                    FridgeRepository.getInstance().setCoolBeverage(enable, null);
//                }
//
//                @Override
//                public void onsetIpAddr(String IpAddr) {
//
//                }
//
//                @Override
//                public void onsetRCCMode(int RCCMode) {
//                    if (RCCMode == 1) { // 鲜果
//                        FridgeRepository.getInstance().setFreshFruit(true, null);
//                    } else if (RCCMode == 2) { // 0 ℃保鲜
//                        FridgeRepository.getInstance().setRetainFresh(true, null);
//                    } else if (RCCMode == 3) { // 冰镇
//                        FridgeRepository.getInstance().setIced(true, null);
//                    } else {
//                        FridgeRepository.getInstance().closeCC(null);
//                    }
//                }
//
//                @Override
//                public void onsetInvadeAlarm(boolean InvadeAlarm) {
//                    RxBus.getInstance().post(BusEvent.MSG_WARN_ALARM, 1);
//                }
//
//                @Override
//                public void onsetCCSet(String CCSet) {
//                    logUtil.d(TAG, "onsetCCSet = " + CCSet);
//                    boolean enable = CCSet.equals(SWITCH_ON);
//                    FridgeRepository.getInstance().setCCSet(enable, null);
//                }
//            };
//        }
//
//        if (mPropertyGetter == null) {
//            String model = FridgePreference.getInstance().getModel();
//            mPropertyGetter = new FridgeService.PropertyGetter() {
//                @Override
//                public String getMode() {
//                    return FridgeRepository.getInstance().getMode();
//                }
//
//                @Override
//                public int getRCCMode() {
//                    if (FridgeRepository.getInstance().isFreshFruit()) {
//                        return 1;
//                    } else if (FridgeRepository.getInstance().isRetainFresh()) {
//                        return 2;
//                    } else if (FridgeRepository.getInstance().isIced()) {
//                        return 3;
//                    } else return 0;
//                }
//
//                @Override
//                public int getRCSetTemp() {
//                    return FridgeRepository.getInstance().getRCSetTemp();
//                }
//
//                @Override
//                public int getRCMinTemp() {
//                    return 2;
//                }
//
//                @Override
//                public int getRCMaxTemp() {
//                    return 8;
//                }
//
//                @Override
//                public int getCCSetTemp() {
//                    return FridgeRepository.getInstance().getCCSetTemp();
//                }
//
//                @Override
//                public int getCCMinTemp() {
//                    return -18;
//                }
//
//                @Override
//                public int getCCMaxTemp() {
//                    switch (model) {
//                        case AppConstants.MODEL_X2:
//                            return -3;
//                        case AppConstants.MODEL_X5:
//                            return 5;
//                        default:
//                            return 8;
//                    }
//                }
//
//                @Override
//                public int getFCSetTemp() {
//                    return FridgeRepository.getInstance().getFCSetTemp();
//                }
//
//                @Override
//                public int getFCMinTemp() {
//                    switch (model) {
//                        case AppConstants.MODEL_X2:
//                        case AppConstants.MODEL_X3:
//                            return -24;
//                        case AppConstants.MODEL_X4:
//                        case AppConstants.MODEL_JD:
//                            return -23;
//                        default:
//                            return -25;
//                    }
//                }
//
//                @Override
//                public int getFCMaxTemp() {
//                    if (model.equals(AppConstants.MODEL_X2) || model.equals(AppConstants.MODEL_X3))
//                        return -16;
//                    else return -15;
//                }
//
//                @Override
//                public int getRCRealTemp() {
//                    return FridgeRepository.getInstance().getRCRealTemp();
//                }
//
//                @Override
//                public int getCCRealTemp() {
//                    return FridgeRepository.getInstance().getCCRealTemp();
//                }
//
//                @Override
//                public int getFCRealTemp() {
//                    return FridgeRepository.getInstance().getFCRealTemp();
//                }
//
//                @Override
//                public String getRCSet() {
//                    return FridgeRepository.getInstance().getRCSet();
//                }
//
//                @Override
//                public String getCCSet() {
//                    return FridgeRepository.getInstance().getCCSet();
//                }
//
//                @Override
//                public String getOneKeyClean() {
//                    return FridgeRepository.getInstance().getOneKeyClean();
//                }
//
//                @Override
//                public int getFilterLifeBase() {
//                    return FILTER_LIFE_BASE;
//                }
//
//                @Override
//                public int getFilterLife() {
//                    return FridgeRepository.getInstance().getFilterLifeUsedTime();
//                }
//
//                @Override
//                public String getSceneAll() {
//                    // TODO 返回所有场景
//                    return "";
//                }
//
//                @Override
//                public String getSceneChoose() {
//                    // TODO 返回选择场景
//                    return "";
//                }
//
//                @Override
//                public String getSceneName() {
//                    // TODO 返回场景名称
//                    return "";
//                }
//
//                @Override
//                public String getOutdoorTemp() {
//                    return FridgePreference.getInstance().getOutdoorTemp();
//                }
//
//                @Override
//                public int getIndoorTemp() {
//                    return FridgeRepository.getInstance().getIndoorTemp();
//                }
//
//                @Override
//                public int getStartDays() {
//                    return FridgePreference.getInstance().getStartHours() / 24;
//                }
//
//                @Override
//                public int getError() {
//                    return FridgeRepository.getInstance().getError();
//                }
//
//                @Override
//                public String getVoiceEnable() {
//                    // TODO 返回语音是否开启
//                    return "";
//                }
//
//                @Override
//                public String getWakeup() {
//                    return SWITCH_OFF;
//                }
//
//                @Override
//                public String getLightUpScreen() {
//                    return SWITCH_OFF;
//                }
//
//                @Override
//                public String getWeather() {
//                    return SWITCH_OFF;
//                }
//
//                @Override
//                public String getPlayMusic() {
//                    return SWITCH_OFF;
//                }
//
//                @Override
//                public String getPlayLightMusic() {
//                    return SWITCH_OFF;
//                }
//
//                @Override
//                public String getStopMusic() {
//                    return SWITCH_OFF;
//                }
//
//                @Override
//                public String getSmartCool() {
//                    return FridgeRepository.getInstance().getSmartCool();
//                }
//
//                @Override
//                public String getSmartFreeze() {
//                    return FridgeRepository.getInstance().getSmartFreeze();
//                }
//
//                @Override
//                public String getCoolBeverage() {
//                    return FridgeRepository.getInstance().getCoolBeverage();
//                }
//
//                @Override
//                public String getComReceiveData() {
//                    return SerialManager.getInstance().getLastDataReceive();
//                }
//
//                @Override
//                public int getSceneMode() {
//                    return 0;
//                }
//
//                @Override
//                public boolean getScreenStatus() {
//                    return false;
//                }
//
//                @Override
//                public boolean getWaterAlarm() {
//                    return false;
//                }
//
//                @Override
//                public boolean getSmokeAlarm() {
//                    return false;
//                }
//
//                @Override
//                public boolean getInvadeAlarm() {
//                    return false;
//                }
//
//                @Override
//                public boolean getGasLeakAlarm() {
//                    return false;
//                }
//
//                @Override
//                public String getIpAddr() {
//                    return ToolUtil.getIpAddr(FridgeApplication.getInstance().getApplicationContext());
//                }
//
//                @Override
//                public String getWulian() {
//                    return null;
//                }
//
//            };
//        }
//
//        if (mPropertySetter == null) {
//            mPropertySetter = new FridgeService.PropertySetter() {
//                @Override
//                public void setMode(String value) {
//                    logUtil.d(TAG, "setMode = " + value);
//                }
//
//                @Override
//                public void setRCCMode(int value) {
//                    logUtil.d(TAG, "setRCCMode = " + value);
//                }
//
//                @Override
//                public void setRCSetTemp(int value) {
//                    logUtil.d(TAG, "setRCRoomTemp = " + value);
//                }
//
//                @Override
//                public void setRCMinTemp(int value) {
//                    logUtil.d(TAG, "setRCMinTemp = " + value);
//                }
//
//                @Override
//                public void setRCMaxTemp(int value) {
//                    logUtil.d(TAG, "setRCMaxTemp = " + value);
//                }
//
//                @Override
//                public void setCCSetTemp(int value) {
//                    logUtil.d(TAG, "setCCRoomTemp = " + value);
//                }
//
//                @Override
//                public void setCCMinTemp(int value) {
//                    logUtil.d(TAG, "setCCMinTemp = " + value);
//                }
//
//                @Override
//                public void setCCMaxTemp(int value) {
//                    logUtil.d(TAG, "setCCMaxTemp = " + value);
//                }
//
//                @Override
//                public void setFCSetTemp(int value) {
//                    logUtil.d(TAG, "setFCSetTemp = " + value);
//                }
//
//                @Override
//                public void setFCMinTemp(int value) {
//                    logUtil.d(TAG, "setFCMinTemp = " + value);
//                }
//
//                @Override
//                public void setFCMaxTemp(int value) {
//                    logUtil.d(TAG, "setFCMaxTemp = " + value);
//                }
//
//                @Override
//                public void setRCSet(String value) {
//                    logUtil.d(TAG, "setRCSet = " + value);
//                }
//
//                @Override
//                public void setCCSet(String value) {
//                    logUtil.d(TAG, "setCCSet = " + value);
//                }
//
//                @Override
//                public void setOneKeyClean(String value) {
//                    logUtil.d(TAG, "setOneKeyClean = " + value);
//                }
//
//                @Override
//                public void setFilterLifeBase(int value) {
//                    logUtil.d(TAG, "setFilterLifeBase = " + value);
//                }
//
//                @Override
//                public void setFilterLife(int value) {
//                    logUtil.d(TAG, "setFilterLife = " + value);
//                }
//
//                @Override
//                public void setSceneAll(String value) {
//                    logUtil.d(TAG, "setSceneAll = " + value);
//                }
//
//                @Override
//                public void setSceneChoose(String value) {
//                    logUtil.d(TAG, "setSceneChoose = " + value);
//                }
//
//                @Override
//                public void setSceneName(String value) {
//                    logUtil.d(TAG, "setSceneName = " + value);
//                }
//
//                @Override
//                public void setOutdoorTemp(String value) {
//                    logUtil.d(TAG, "setOutdoorTemp = " + value);
//                }
//
//                @Override
//                public void setIndoorTemp(int value) {
//                    logUtil.d(TAG, "setIndoorTemp = " + value);
//                }
//
//                @Override
//                public void setStartDays(int value) {
//                    logUtil.d(TAG, "setStartDays = " + value);
//                }
//
//                @Override
//                public void setVoiceEnable(String value) {
//                    logUtil.d(TAG, "setVoiceEnable = " + value);
//                }
//
//                @Override
//                public void setWakeup(String value) {
//                    logUtil.d(TAG, "setWakeup = " + value);
//                }
//
//                @Override
//                public void setLightUpScreen(String value) {
//                    logUtil.d(TAG, "setLightUpScreen = " + value);
//                }
//
//                @Override
//                public void setWeather(String value) {
//                    logUtil.d(TAG, "setWeather = " + value);
//                }
//
//                @Override
//                public void setPlayMusic(String value) {
//                    logUtil.d(TAG, "setPlayMusic = " + value);
//                }
//
//                @Override
//                public void setPlayLightMusic(String value) {
//                    logUtil.d(TAG, "setPlayLightMusic = " + value);
//                }
//
//                @Override
//                public void setStopMusic(String value) {
//                    logUtil.d(TAG, "setStopMusic = " + value);
//                }
//
//                @Override
//                public void setSmartCool(String value) {
//                    logUtil.d(TAG, "setSmartCool = " + value);
//                }
//
//                @Override
//                public void setSmartFreeze(String value) {
//                    logUtil.d(TAG, "setSmartFreeze = " + value);
//                }
//
//                @Override
//                public void setCoolBeverage(String value) {
//                    logUtil.d(TAG, "setCoolBeverage = " + value);
//                }
//
//                @Override
//                public void setComReceiveData(String value) {
//                    logUtil.d(TAG, "setComReceiveData = " + value);
//                }
//
//                @Override
//                public void setSceneMode(int value) {
//
//                }
//
//                @Override
//                public void setScreenStatus(boolean value) {
//
//                }
//
//                @Override
//                public void setWaterAlarm(boolean value) {
//
//                }
//
//                @Override
//                public void setSmokeAlarm(boolean value) {
//
//                }
//
//                @Override
//                public void setInvadeAlarm(boolean value) {
//
//                }
//
//                @Override
//                public void setGasLeakAlarm(boolean value) {
//
//                }
//
//                @Override
//                public void setIpAddr(String value) {
//
//                }
//
//                @Override
//                public void setWulian(String value) {
//
//                }
//
//            };
//        }
//        mDevice.FridgeService().setHandler(mActionHandler, mPropertyGetter, mPropertySetter);
        try {
            mDevice.start(new CompletedListener() {
                @Override
                public void onSucceed() {
                    logUtil.d(TAG, "onSucceed");
                    LoginPreference.getInstance().saveBindStatus(true);
                    if (subscriber != null) {
                        subscriber.onNext(qrCodeBase);
                        subscriber.onCompleted();
                    }
                }

                @Override
                public void onFailed(MiotError miotError) {
                    logUtil.e(TAG, miotError.getMessage());
                    if (subscriber != null) {
                        subscriber.onNext(null);
                        subscriber.onCompleted();
                    }
                }
            });
        } catch (MiotException e) {
            e.printStackTrace();
            if (subscriber != null) {
                subscriber.onNext(null);
                subscriber.onCompleted();
            }
            logUtil.e(TAG, e.getMessage());
        }
        bindRegister();
    }

    /**
     * 小米账号与设备绑定
     *
     * @param qrCodeBase: 登录返回信息
     */
    public Observable<QRCodeBase> bindDevice(QRCodeBase qrCodeBase) {
        return Observable.create(subscriber -> {
            try {
                MiotHostManager.getInstance().reset(new CompletedListener() {
                    @Override
                    public void onSucceed() {
                        qrCodeBase.getLoginQRCode().parseAppendAttr();
                        try {
                            createDevice(qrCodeBase, subscriber);
                        } catch (Exception e) {
                            logUtil.e(TAG, e.toString());
                            subscriber.onNext(null);
                            subscriber.onCompleted();
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailed(MiotError miotError) {
                        logUtil.e(TAG, miotError.getMessage());
                        subscriber.onNext(null);
                        subscriber.onCompleted();
                    }
                });
            } catch (MiotException e) {
                logUtil.e(TAG, e.getMessage());
                e.printStackTrace();
                subscriber.onNext(null);
                subscriber.onCompleted();
            }
        });
    }

    /**
     * 解除设备绑定
     */
    public void unbindDevice(Subscriber<? super Boolean> subscriber) {
        try {
            MiotHostManager.getInstance().reset(new CompletedListener() {
                @Override
                public void onSucceed() {
                    logUtil.d(TAG, "unbind device success");
                    LoginPreference.getInstance().saveBindStatus(false);
                    createDevice(null, null);
                    subscriber.onNext(true);
                    subscriber.onCompleted();
                }

                @Override
                public void onFailed(MiotError miotError) {
                    logUtil.e(TAG, "unbind error, msg = " + miotError.getMessage());
                    subscriber.onNext(false);
                    subscriber.onCompleted();
                }
            });
        } catch (MiotException e) {
            e.printStackTrace();
            logUtil.e(TAG, e.getMessage());
            subscriber.onNext(false);
            subscriber.onCompleted();
        }
    }

    /**
     * 监听设备绑定状态
     */
    private void bindRegister() {
        try {
            logUtil.i(TAG, "registerBindListener");
            MiotHostManager.getInstance().registerBindListener(new OnBindListener() {
                @Override
                public void onBind() {
                    logUtil.i(TAG, "device bind!");
                    LoginPreference.getInstance().saveBindStatus(true);
                }

                @Override
                public void onUnBind() {
                    logUtil.e(TAG, "device unbind!");
                    RxBus.getInstance().post(BusEvent.MSG_LOGOUT_SUCCESS);
                    LoginPreference.getInstance().saveBindStatus(false);
                    ToolUtil.saveObject(MyApplication.getContext(), AppConstants.USER_INFO_FILE, null);// 删除本地云米账号信息
                    try {
                        MiotManager.getPeopleManager().deletePeople();// 删除配置小米账号信息
                    } catch (com.miot.common.exception.MiotException e) {
                        e.printStackTrace();
                    }
                }
            }, new CompletedListener() {
                @Override
                public void onSucceed() {
                    logUtil.i(TAG, "device bind success!");
                }

                @Override
                public void onFailed(MiotError miotError) {
                    logUtil.e(TAG, "device bind fail!msg=" + miotError.getMessage());
                }
            });
        } catch (MiotException e) {
            logUtil.e(TAG, "registerBindListener,msg=" + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 获取设备信息
     *
     * @param userId: 小米 id
     */
    private String getMiIotInfo(String userId) {
        WifiManager wifiManager = (WifiManager) MyApplication.getInstance().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifiManager != null) {
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            DhcpInfo di = wifiManager.getDhcpInfo();
            JSONObject jo = new JSONObject();
            try {
                jo.put("method", "_internal.info");
                jo.put("partner_id", "");
                JSONObject jop = new JSONObject();
                jop.put("hw_ver", "Android");
                jop.put("fw_ver", ToolUtil.getVersion());
                JSONObject jopa = new JSONObject();
                jopa.put("ssid", wifiInfo.getSSID());
                jopa.put("bssid", wifiInfo.getBSSID());
                jop.put("ap", jopa);
                JSONObject jopn = new JSONObject();
                jopn.put("localIp", ToolUtil.intToIp(di.ipAddress));
                jopn.put("mask", ToolUtil.intToIp(di.netmask));
                jopn.put("gw", ToolUtil.intToIp(di.gateway));
                jop.put("netif", jopn);
                if (userId != null) {
                    jop.put("uid", userId);
                }
                jo.put("params", jop);
                return jo.toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 异常发生事件
     */
    public void sendFaultHappen(int error) {
        String method = "event.fault_happen";
        String params = "[" + error + "]";
        logUtil.i(TAG, "method=" + method + ",params=" + params);
        if (mDevice != null) {
            try {
                mDevice.send(method, params);
            } catch (MiotException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 异常恢复事件
     */
    public void sendFaultFix(int error) {
        String method = "event.fault_fixed";
        String params = "[" + error + "]";
        logUtil.i(TAG, "method=" + method + ",params=" + params);
        if (mDevice != null) {
            try {
                mDevice.send(method, params);
            } catch (MiotException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 滤芯用尽
     */
    public void sendFilterLifeEnd() {
        String method = "event.filter_life_end";
        String params = "[true]";
        logUtil.i(TAG, "method=" + method + ",params=" + params);
        if (mDevice != null) {
            try {
                mDevice.send(method, params);
            } catch (MiotException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 滤芯即将用尽
     */
    public void sendFilterLifeLow() {
        String method = "event.filter_life_low";
        String params = "[true]";
        logUtil.i(TAG, "method=" + method + ",params=" + params);
        if (mDevice != null) {
            try {
                mDevice.send(method, params);
            } catch (MiotException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 计时器定时时间
     */
    public void sendTimerEnd(int minute) {
        minute = minute / 60;
        int hour = minute / 60;
        int min = minute % 60;
        String method = "event.fixed_time_end";
        String params = "[" + hour + "," + min + "]";
        logUtil.i(TAG, "method=" + method + ",params=" + params);
        if (mDevice != null) {
            try {
                mDevice.send(method, params);
            } catch (MiotException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 发送食材到期事件
     *
     * @param name      食品名称
     * @param startTime 开始保存时间 时间戳 单位秒
     * @param endTime   到期时间  时间戳 单位秒
     * @param room      0:冷藏室；1：变温室；2：冷冻室
     */
    public void sendFoodExpire(String name, long startTime, int endTime, int room) {
        String method = "event.food_expire";
        String params = "[" + name + "," + startTime + "," + endTime + "," + room + "]";
        logUtil.i(TAG, "method=" + method + ",params=" + params);
        if (mDevice != null) {
            try {
                mDevice.send(method, params);
            } catch (MiotException e) {
                e.printStackTrace();
            }
        }
    }

//    /**
//     * 属性上报（工作模式）
//     */
//    public void sendPropertyMode(int mode) {
//        logUtil.i(TAG, "mode property upload!,params = " + mode);
//        if (mDevice != null) {
//            try {
//                String modeStr = MODE_NONE;
//                if (mode == AppConstants.MODE_SMART) {
//                    modeStr = FridgeRepository.getInstance().MODE_SMART;
//                } else if (mode == AppConstants.MODE_HOLIDAY) {
//                    modeStr = FridgeRepository.getInstance().MODE_HOLIDAY;
//                }
//                mDevice.FridgeService().Mode().setValue(modeStr);
//                mDevice.sendEvents();
//            } catch (MiotException e) {
//                e.printStackTrace();
//            }
//        }
//    }

    /**
     * 属性上报（冷藏室开关）
     */
    public void sendPropertyRCRoomEnable(boolean enable) {
        logUtil.i(TAG, "RCRoomEnable enable property upload!,params = " + enable);
        if (mDevice != null) {
            try {
                if (enable) {
                    mDevice.FridgeService().RCSet().setValue(SWITCH_ON);
                } else {
                    mDevice.FridgeService().RCSet().setValue(SWITCH_OFF);
                }
                mDevice.sendEvents();
            } catch (MiotException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 属性上报（变温室开关）
     */
    public void sendPropertyCCRoomEnable(boolean enable) {
        logUtil.i(TAG, "CCRoomEnable enable property upload!,params = " + enable);
        if (mDevice != null) {
            try {
                if (enable) {
                    mDevice.FridgeService().CCSet().setValue(SWITCH_ON);
                } else {
                    mDevice.FridgeService().CCSet().setValue(SWITCH_OFF);
                }
                mDevice.sendEvents();
            } catch (MiotException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 属性上报（冷藏室设置温度）
     */
    public void sendPropertyRCSetTemp(int temp) {
        logUtil.i(TAG, "CCSetTemp enable property upload!,params = " + temp);
        if (mDevice != null) {
            try {
                mDevice.FridgeService().RCSetTemp().setValue(temp);
                mDevice.sendEvents();
            } catch (MiotException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 属性上报（变温室设置温度）
     */
    public void sendPropertyCCSetTemp(int temp) {
        logUtil.i(TAG, "CCSetTemp enable property upload!,params = " + temp);
        if (mDevice != null) {
            try {
                mDevice.FridgeService().CCSetTemp().setValue(temp);
                mDevice.sendEvents();
            } catch (MiotException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 属性上报（冷冻室设置温度）
     */
    public void sendPropertyFZSetTemp(int temp) {
        logUtil.i(TAG, "FZSetTemp enable property upload!,params = " + temp);
        if (mDevice != null) {
            try {
                mDevice.FridgeService().FCSetTemp().setValue(temp);
                mDevice.sendEvents();
            } catch (MiotException e) {
                e.printStackTrace();
            }
        }
    }
}