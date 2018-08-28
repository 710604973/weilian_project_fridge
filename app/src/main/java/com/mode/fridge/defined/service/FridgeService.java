package com.mode.fridge.defined.service;

import android.util.Log;

import com.mode.fridge.defined.ViomiDefined;
import com.mode.fridge.defined.action.SetCCMaxTemp;
import com.mode.fridge.defined.action.SetCCMinTemp;
import com.mode.fridge.defined.action.SetCCSet;
import com.mode.fridge.defined.action.SetCCSetTemp;
import com.mode.fridge.defined.action.SetComReceiveData;
import com.mode.fridge.defined.action.SetCoolBeverage;
import com.mode.fridge.defined.action.SetFCMaxTemp;
import com.mode.fridge.defined.action.SetFCMinTemp;
import com.mode.fridge.defined.action.SetFCSetTemp;
import com.mode.fridge.defined.action.SetFilterLife;
import com.mode.fridge.defined.action.SetFilterLifeBase;
import com.mode.fridge.defined.action.SetGasLeakAlarm;
import com.mode.fridge.defined.action.SetIndoorTemp;
import com.mode.fridge.defined.action.SetInvadeAlarm;
import com.mode.fridge.defined.action.SetIpAddr;
import com.mode.fridge.defined.action.SetLightUpScreen;
import com.mode.fridge.defined.action.SetMode;
import com.mode.fridge.defined.action.SetOneKeyClean;
import com.mode.fridge.defined.action.SetOutdoorTemp;
import com.mode.fridge.defined.action.SetPlayLightMusic;
import com.mode.fridge.defined.action.SetPlayMusic;
import com.mode.fridge.defined.action.SetRCMaxTemp;
import com.mode.fridge.defined.action.SetRCMinTemp;
import com.mode.fridge.defined.action.SetRCSet;
import com.mode.fridge.defined.action.SetRCSetTemp;
import com.mode.fridge.defined.action.SetSceneAll;
import com.mode.fridge.defined.action.SetSceneChoose;
import com.mode.fridge.defined.action.SetSceneMode;
import com.mode.fridge.defined.action.SetSceneName;
import com.mode.fridge.defined.action.SetScreenStatus;
import com.mode.fridge.defined.action.SetSmartCool;
import com.mode.fridge.defined.action.SetSmartFreeze;
import com.mode.fridge.defined.action.SetSmokeAlarm;
import com.mode.fridge.defined.action.SetStartDays;
import com.mode.fridge.defined.action.SetStopMusic;
import com.mode.fridge.defined.action.SetVoiceEnable;
import com.mode.fridge.defined.action.SetWakeup;
import com.mode.fridge.defined.action.SetWaterAlarm;
import com.mode.fridge.defined.action.SetWeather;
import com.mode.fridge.defined.property.CCMaxTemp;
import com.mode.fridge.defined.property.CCMinTemp;
import com.mode.fridge.defined.property.CCRealTemp;
import com.mode.fridge.defined.property.CCSet;
import com.mode.fridge.defined.property.CCSetTemp;
import com.mode.fridge.defined.property.ComReceiveData;
import com.mode.fridge.defined.property.CoolBeverage;
import com.mode.fridge.defined.property.Error;
import com.mode.fridge.defined.property.FCMaxTemp;
import com.mode.fridge.defined.property.FCMinTemp;
import com.mode.fridge.defined.property.FCRealTemp;
import com.mode.fridge.defined.property.FCSetTemp;
import com.mode.fridge.defined.property.FilterLife;
import com.mode.fridge.defined.property.FilterLifeBase;
import com.mode.fridge.defined.property.GasLeakAlarm;
import com.mode.fridge.defined.property.IndoorTemp;
import com.mode.fridge.defined.property.InvadeAlarm;
import com.mode.fridge.defined.property.IpAddr;
import com.mode.fridge.defined.property.LightUpScreen;
import com.mode.fridge.defined.property.Mode;
import com.mode.fridge.defined.property.OneKeyClean;
import com.mode.fridge.defined.property.OutdoorTemp;
import com.mode.fridge.defined.property.PlayLightMusic;
import com.mode.fridge.defined.property.PlayMusic;
import com.mode.fridge.defined.property.RCMaxTemp;
import com.mode.fridge.defined.property.RCMinTemp;
import com.mode.fridge.defined.property.RCRealTemp;
import com.mode.fridge.defined.property.RCSet;
import com.mode.fridge.defined.property.RCSetTemp;
import com.mode.fridge.defined.property.SceneAll;
import com.mode.fridge.defined.property.SceneChoose;
import com.mode.fridge.defined.property.SceneMode;
import com.mode.fridge.defined.property.SceneName;
import com.mode.fridge.defined.property.ScreenStatus;
import com.mode.fridge.defined.property.SmartCool;
import com.mode.fridge.defined.property.SmartFreeze;
import com.mode.fridge.defined.property.SmokeAlarm;
import com.mode.fridge.defined.property.StartDays;
import com.mode.fridge.defined.property.StopMusic;
import com.mode.fridge.defined.property.VoiceEnable;
import com.mode.fridge.defined.property.Wakeup;
import com.mode.fridge.defined.property.WaterAlarm;
import com.mode.fridge.defined.property.Weather;
import com.xiaomi.miot.typedef.data.value.Vbool;
import com.xiaomi.miot.typedef.data.value.Vint;
import com.xiaomi.miot.typedef.data.value.Vstring;
import com.xiaomi.miot.typedef.device.Action;
import com.xiaomi.miot.typedef.device.ActionInfo;
import com.xiaomi.miot.typedef.device.operable.ServiceOperable;
import com.xiaomi.miot.typedef.error.MiotError;
import com.xiaomi.miot.typedef.property.Property;
import com.xiaomi.miot.typedef.urn.ServiceType;

public class FridgeService extends ServiceOperable {

    public static final ServiceType TYPE = ViomiDefined.Service.FridgeService.toServiceType();
    private static final String TAG = "FridgeService";

    public FridgeService(boolean hasOptionalProperty) {
        super(TYPE);

        super.addProperty(new Mode());
        super.addProperty(new RCSetTemp());
        super.addProperty(new RCMinTemp());
        super.addProperty(new RCMaxTemp());
        super.addProperty(new CCSetTemp());
        super.addProperty(new CCMinTemp());
        super.addProperty(new CCMaxTemp());
        super.addProperty(new FCSetTemp());
        super.addProperty(new FCMinTemp());
        super.addProperty(new FCMaxTemp());
        super.addProperty(new RCRealTemp());
        super.addProperty(new CCRealTemp());
        super.addProperty(new FCRealTemp());
        super.addProperty(new RCSet());
        super.addProperty(new CCSet());
        super.addProperty(new OneKeyClean());
        super.addProperty(new FilterLifeBase());
        super.addProperty(new FilterLife());
        super.addProperty(new SceneAll());
        super.addProperty(new SceneChoose());
        super.addProperty(new SceneName());
        super.addProperty(new OutdoorTemp());
        super.addProperty(new IndoorTemp());
        super.addProperty(new StartDays());
        super.addProperty(new Error());
        super.addProperty(new VoiceEnable());
        super.addProperty(new Wakeup());
        super.addProperty(new LightUpScreen());
        super.addProperty(new Weather());
        super.addProperty(new PlayMusic());
        super.addProperty(new PlayLightMusic());
        super.addProperty(new StopMusic());
        super.addProperty(new SmartCool());
        super.addProperty(new SmartFreeze());
        super.addProperty(new CoolBeverage());
        super.addProperty(new ComReceiveData());
        super.addProperty(new SceneMode());
        super.addProperty(new ScreenStatus());
        super.addProperty(new WaterAlarm());
        super.addProperty(new SmokeAlarm());
        super.addProperty(new InvadeAlarm());
        super.addProperty(new GasLeakAlarm());
        super.addProperty(new IpAddr());

        if (hasOptionalProperty) {
        }

        super.addAction(new SetIndoorTemp());
        super.addAction(new SetFCMaxTemp());
        super.addAction(new SetFCSetTemp());
        super.addAction(new SetGasLeakAlarm());
        super.addAction(new SetSmartFreeze());
        super.addAction(new SetPlayMusic());
        super.addAction(new SetSmartCool());
        super.addAction(new SetStartDays());
        super.addAction(new SetWeather());
        super.addAction(new SetCCMaxTemp());
        super.addAction(new SetFilterLife());
        super.addAction(new SetCCSetTemp());
        super.addAction(new SetSceneMode());
        super.addAction(new SetRCSet());
        super.addAction(new SetWaterAlarm());
        super.addAction(new SetVoiceEnable());
        super.addAction(new SetLightUpScreen());
        super.addAction(new SetScreenStatus());
        super.addAction(new SetComReceiveData());
        super.addAction(new SetSceneName());
        super.addAction(new SetWakeup());
        super.addAction(new SetSmokeAlarm());
        super.addAction(new SetMode());
        super.addAction(new SetStopMusic());
        super.addAction(new SetCCMinTemp());
        super.addAction(new SetSceneAll());
        super.addAction(new SetPlayLightMusic());
        super.addAction(new SetOneKeyClean());
        super.addAction(new SetRCSetTemp());
        super.addAction(new SetFCMinTemp());
        super.addAction(new SetFilterLifeBase());
        super.addAction(new SetSceneChoose());
        super.addAction(new SetRCMaxTemp());
        super.addAction(new SetOutdoorTemp());
        super.addAction(new SetRCMinTemp());
        super.addAction(new SetCoolBeverage());
        super.addAction(new SetIpAddr());
        super.addAction(new SetInvadeAlarm());
        super.addAction(new SetCCSet());
    }

    /**
     * Properties
     */
    public Mode Mode() {
        Property p = super.getProperty(Mode.TYPE);
        if (p != null) {
            if (p instanceof Mode) {
                return (Mode) p;
            }
        }

        return null;
    }
    public RCSetTemp RCSetTemp() {
        Property p = super.getProperty(RCSetTemp.TYPE);
        if (p != null) {
            if (p instanceof RCSetTemp) {
                return (RCSetTemp) p;
            }
        }

        return null;
    }
    public RCMinTemp RCMinTemp() {
        Property p = super.getProperty(RCMinTemp.TYPE);
        if (p != null) {
            if (p instanceof RCMinTemp) {
                return (RCMinTemp) p;
            }
        }

        return null;
    }
    public RCMaxTemp RCMaxTemp() {
        Property p = super.getProperty(RCMaxTemp.TYPE);
        if (p != null) {
            if (p instanceof RCMaxTemp) {
                return (RCMaxTemp) p;
            }
        }

        return null;
    }
    public CCSetTemp CCSetTemp() {
        Property p = super.getProperty(CCSetTemp.TYPE);
        if (p != null) {
            if (p instanceof CCSetTemp) {
                return (CCSetTemp) p;
            }
        }

        return null;
    }
    public CCMinTemp CCMinTemp() {
        Property p = super.getProperty(CCMinTemp.TYPE);
        if (p != null) {
            if (p instanceof CCMinTemp) {
                return (CCMinTemp) p;
            }
        }

        return null;
    }
    public CCMaxTemp CCMaxTemp() {
        Property p = super.getProperty(CCMaxTemp.TYPE);
        if (p != null) {
            if (p instanceof CCMaxTemp) {
                return (CCMaxTemp) p;
            }
        }

        return null;
    }
    public FCSetTemp FCSetTemp() {
        Property p = super.getProperty(FCSetTemp.TYPE);
        if (p != null) {
            if (p instanceof FCSetTemp) {
                return (FCSetTemp) p;
            }
        }

        return null;
    }
    public FCMinTemp FCMinTemp() {
        Property p = super.getProperty(FCMinTemp.TYPE);
        if (p != null) {
            if (p instanceof FCMinTemp) {
                return (FCMinTemp) p;
            }
        }

        return null;
    }
    public FCMaxTemp FCMaxTemp() {
        Property p = super.getProperty(FCMaxTemp.TYPE);
        if (p != null) {
            if (p instanceof FCMaxTemp) {
                return (FCMaxTemp) p;
            }
        }

        return null;
    }
    public RCRealTemp RCRealTemp() {
        Property p = super.getProperty(RCRealTemp.TYPE);
        if (p != null) {
            if (p instanceof RCRealTemp) {
                return (RCRealTemp) p;
            }
        }

        return null;
    }
    public CCRealTemp CCRealTemp() {
        Property p = super.getProperty(CCRealTemp.TYPE);
        if (p != null) {
            if (p instanceof CCRealTemp) {
                return (CCRealTemp) p;
            }
        }

        return null;
    }
    public FCRealTemp FCRealTemp() {
        Property p = super.getProperty(FCRealTemp.TYPE);
        if (p != null) {
            if (p instanceof FCRealTemp) {
                return (FCRealTemp) p;
            }
        }

        return null;
    }
    public RCSet RCSet() {
        Property p = super.getProperty(RCSet.TYPE);
        if (p != null) {
            if (p instanceof RCSet) {
                return (RCSet) p;
            }
        }

        return null;
    }
    public CCSet CCSet() {
        Property p = super.getProperty(CCSet.TYPE);
        if (p != null) {
            if (p instanceof CCSet) {
                return (CCSet) p;
            }
        }

        return null;
    }
    public OneKeyClean OneKeyClean() {
        Property p = super.getProperty(OneKeyClean.TYPE);
        if (p != null) {
            if (p instanceof OneKeyClean) {
                return (OneKeyClean) p;
            }
        }

        return null;
    }
    public FilterLifeBase FilterLifeBase() {
        Property p = super.getProperty(FilterLifeBase.TYPE);
        if (p != null) {
            if (p instanceof FilterLifeBase) {
                return (FilterLifeBase) p;
            }
        }

        return null;
    }
    public FilterLife FilterLife() {
        Property p = super.getProperty(FilterLife.TYPE);
        if (p != null) {
            if (p instanceof FilterLife) {
                return (FilterLife) p;
            }
        }

        return null;
    }
    public SceneAll SceneAll() {
        Property p = super.getProperty(SceneAll.TYPE);
        if (p != null) {
            if (p instanceof SceneAll) {
                return (SceneAll) p;
            }
        }

        return null;
    }
    public SceneChoose SceneChoose() {
        Property p = super.getProperty(SceneChoose.TYPE);
        if (p != null) {
            if (p instanceof SceneChoose) {
                return (SceneChoose) p;
            }
        }

        return null;
    }
    public SceneName SceneName() {
        Property p = super.getProperty(SceneName.TYPE);
        if (p != null) {
            if (p instanceof SceneName) {
                return (SceneName) p;
            }
        }

        return null;
    }
    public OutdoorTemp OutdoorTemp() {
        Property p = super.getProperty(OutdoorTemp.TYPE);
        if (p != null) {
            if (p instanceof OutdoorTemp) {
                return (OutdoorTemp) p;
            }
        }

        return null;
    }
    public IndoorTemp IndoorTemp() {
        Property p = super.getProperty(IndoorTemp.TYPE);
        if (p != null) {
            if (p instanceof IndoorTemp) {
                return (IndoorTemp) p;
            }
        }

        return null;
    }
    public StartDays StartDays() {
        Property p = super.getProperty(StartDays.TYPE);
        if (p != null) {
            if (p instanceof StartDays) {
                return (StartDays) p;
            }
        }

        return null;
    }
    public Error Error() {
        Property p = super.getProperty(Error.TYPE);
        if (p != null) {
            if (p instanceof Error) {
                return (Error) p;
            }
        }

        return null;
    }
    public VoiceEnable VoiceEnable() {
        Property p = super.getProperty(VoiceEnable.TYPE);
        if (p != null) {
            if (p instanceof VoiceEnable) {
                return (VoiceEnable) p;
            }
        }

        return null;
    }
    public Wakeup Wakeup() {
        Property p = super.getProperty(Wakeup.TYPE);
        if (p != null) {
            if (p instanceof Wakeup) {
                return (Wakeup) p;
            }
        }

        return null;
    }
    public LightUpScreen LightUpScreen() {
        Property p = super.getProperty(LightUpScreen.TYPE);
        if (p != null) {
            if (p instanceof LightUpScreen) {
                return (LightUpScreen) p;
            }
        }

        return null;
    }
    public Weather Weather() {
        Property p = super.getProperty(Weather.TYPE);
        if (p != null) {
            if (p instanceof Weather) {
                return (Weather) p;
            }
        }

        return null;
    }
    public PlayMusic PlayMusic() {
        Property p = super.getProperty(PlayMusic.TYPE);
        if (p != null) {
            if (p instanceof PlayMusic) {
                return (PlayMusic) p;
            }
        }

        return null;
    }
    public PlayLightMusic PlayLightMusic() {
        Property p = super.getProperty(PlayLightMusic.TYPE);
        if (p != null) {
            if (p instanceof PlayLightMusic) {
                return (PlayLightMusic) p;
            }
        }

        return null;
    }
    public StopMusic StopMusic() {
        Property p = super.getProperty(StopMusic.TYPE);
        if (p != null) {
            if (p instanceof StopMusic) {
                return (StopMusic) p;
            }
        }

        return null;
    }
    public SmartCool SmartCool() {
        Property p = super.getProperty(SmartCool.TYPE);
        if (p != null) {
            if (p instanceof SmartCool) {
                return (SmartCool) p;
            }
        }

        return null;
    }
    public SmartFreeze SmartFreeze() {
        Property p = super.getProperty(SmartFreeze.TYPE);
        if (p != null) {
            if (p instanceof SmartFreeze) {
                return (SmartFreeze) p;
            }
        }

        return null;
    }
    public CoolBeverage CoolBeverage() {
        Property p = super.getProperty(CoolBeverage.TYPE);
        if (p != null) {
            if (p instanceof CoolBeverage) {
                return (CoolBeverage) p;
            }
        }

        return null;
    }
    public ComReceiveData ComReceiveData() {
        Property p = super.getProperty(ComReceiveData.TYPE);
        if (p != null) {
            if (p instanceof ComReceiveData) {
                return (ComReceiveData) p;
            }
        }

        return null;
    }
    public SceneMode SceneMode() {
        Property p = super.getProperty(SceneMode.TYPE);
        if (p != null) {
            if (p instanceof SceneMode) {
                return (SceneMode) p;
            }
        }

        return null;
    }
    public ScreenStatus ScreenStatus() {
        Property p = super.getProperty(ScreenStatus.TYPE);
        if (p != null) {
            if (p instanceof ScreenStatus) {
                return (ScreenStatus) p;
            }
        }

        return null;
    }
    public WaterAlarm WaterAlarm() {
        Property p = super.getProperty(WaterAlarm.TYPE);
        if (p != null) {
            if (p instanceof WaterAlarm) {
                return (WaterAlarm) p;
            }
        }

        return null;
    }
    public SmokeAlarm SmokeAlarm() {
        Property p = super.getProperty(SmokeAlarm.TYPE);
        if (p != null) {
            if (p instanceof SmokeAlarm) {
                return (SmokeAlarm) p;
            }
        }

        return null;
    }
    public InvadeAlarm InvadeAlarm() {
        Property p = super.getProperty(InvadeAlarm.TYPE);
        if (p != null) {
            if (p instanceof InvadeAlarm) {
                return (InvadeAlarm) p;
            }
        }

        return null;
    }
    public GasLeakAlarm GasLeakAlarm() {
        Property p = super.getProperty(GasLeakAlarm.TYPE);
        if (p != null) {
            if (p instanceof GasLeakAlarm) {
                return (GasLeakAlarm) p;
            }
        }

        return null;
    }
    public IpAddr IpAddr() {
        Property p = super.getProperty(IpAddr.TYPE);
        if (p != null) {
            if (p instanceof IpAddr) {
                return (IpAddr) p;
            }
        }

        return null;
    }

    /**
     * Actions
     */
    public SetIndoorTemp setIndoorTemp(){
        Action a = super.getAction(SetIndoorTemp.TYPE);
        if (a != null) {
            if (a instanceof SetIndoorTemp) {
                return (SetIndoorTemp) a;
            }
        }

        return null;
    }
    public SetFCMaxTemp setFCMaxTemp(){
        Action a = super.getAction(SetFCMaxTemp.TYPE);
        if (a != null) {
            if (a instanceof SetFCMaxTemp) {
                return (SetFCMaxTemp) a;
            }
        }

        return null;
    }
    public SetFCSetTemp setFCSetTemp(){
        Action a = super.getAction(SetFCSetTemp.TYPE);
        if (a != null) {
            if (a instanceof SetFCSetTemp) {
                return (SetFCSetTemp) a;
            }
        }

        return null;
    }
    public SetGasLeakAlarm setGasLeakAlarm(){
        Action a = super.getAction(SetGasLeakAlarm.TYPE);
        if (a != null) {
            if (a instanceof SetGasLeakAlarm) {
                return (SetGasLeakAlarm) a;
            }
        }

        return null;
    }
    public SetSmartFreeze setSmartFreeze(){
        Action a = super.getAction(SetSmartFreeze.TYPE);
        if (a != null) {
            if (a instanceof SetSmartFreeze) {
                return (SetSmartFreeze) a;
            }
        }

        return null;
    }
    public SetPlayMusic setPlayMusic(){
        Action a = super.getAction(SetPlayMusic.TYPE);
        if (a != null) {
            if (a instanceof SetPlayMusic) {
                return (SetPlayMusic) a;
            }
        }

        return null;
    }
    public SetSmartCool setSmartCool(){
        Action a = super.getAction(SetSmartCool.TYPE);
        if (a != null) {
            if (a instanceof SetSmartCool) {
                return (SetSmartCool) a;
            }
        }

        return null;
    }
    public SetStartDays setStartDays(){
        Action a = super.getAction(SetStartDays.TYPE);
        if (a != null) {
            if (a instanceof SetStartDays) {
                return (SetStartDays) a;
            }
        }

        return null;
    }
    public SetWeather setWeather(){
        Action a = super.getAction(SetWeather.TYPE);
        if (a != null) {
            if (a instanceof SetWeather) {
                return (SetWeather) a;
            }
        }

        return null;
    }
    public SetCCMaxTemp setCCMaxTemp(){
        Action a = super.getAction(SetCCMaxTemp.TYPE);
        if (a != null) {
            if (a instanceof SetCCMaxTemp) {
                return (SetCCMaxTemp) a;
            }
        }

        return null;
    }
    public SetFilterLife setFilterLife(){
        Action a = super.getAction(SetFilterLife.TYPE);
        if (a != null) {
            if (a instanceof SetFilterLife) {
                return (SetFilterLife) a;
            }
        }

        return null;
    }
    public SetCCSetTemp setCCSetTemp(){
        Action a = super.getAction(SetCCSetTemp.TYPE);
        if (a != null) {
            if (a instanceof SetCCSetTemp) {
                return (SetCCSetTemp) a;
            }
        }

        return null;
    }
    public SetSceneMode setSceneMode(){
        Action a = super.getAction(SetSceneMode.TYPE);
        if (a != null) {
            if (a instanceof SetSceneMode) {
                return (SetSceneMode) a;
            }
        }

        return null;
    }
    public SetRCSet setRCSet(){
        Action a = super.getAction(SetRCSet.TYPE);
        if (a != null) {
            if (a instanceof SetRCSet) {
                return (SetRCSet) a;
            }
        }

        return null;
    }
    public SetWaterAlarm setWaterAlarm(){
        Action a = super.getAction(SetWaterAlarm.TYPE);
        if (a != null) {
            if (a instanceof SetWaterAlarm) {
                return (SetWaterAlarm) a;
            }
        }

        return null;
    }
    public SetVoiceEnable setVoiceEnable(){
        Action a = super.getAction(SetVoiceEnable.TYPE);
        if (a != null) {
            if (a instanceof SetVoiceEnable) {
                return (SetVoiceEnable) a;
            }
        }

        return null;
    }
    public SetLightUpScreen setLightUpScreen(){
        Action a = super.getAction(SetLightUpScreen.TYPE);
        if (a != null) {
            if (a instanceof SetLightUpScreen) {
                return (SetLightUpScreen) a;
            }
        }

        return null;
    }
    public SetScreenStatus setScreenStatus(){
        Action a = super.getAction(SetScreenStatus.TYPE);
        if (a != null) {
            if (a instanceof SetScreenStatus) {
                return (SetScreenStatus) a;
            }
        }

        return null;
    }
    public SetComReceiveData setComReceiveData(){
        Action a = super.getAction(SetComReceiveData.TYPE);
        if (a != null) {
            if (a instanceof SetComReceiveData) {
                return (SetComReceiveData) a;
            }
        }

        return null;
    }
    public SetSceneName setSceneName(){
        Action a = super.getAction(SetSceneName.TYPE);
        if (a != null) {
            if (a instanceof SetSceneName) {
                return (SetSceneName) a;
            }
        }

        return null;
    }
    public SetWakeup setWakeup(){
        Action a = super.getAction(SetWakeup.TYPE);
        if (a != null) {
            if (a instanceof SetWakeup) {
                return (SetWakeup) a;
            }
        }

        return null;
    }
    public SetSmokeAlarm setSmokeAlarm(){
        Action a = super.getAction(SetSmokeAlarm.TYPE);
        if (a != null) {
            if (a instanceof SetSmokeAlarm) {
                return (SetSmokeAlarm) a;
            }
        }

        return null;
    }
    public SetMode setMode(){
        Action a = super.getAction(SetMode.TYPE);
        if (a != null) {
            if (a instanceof SetMode) {
                return (SetMode) a;
            }
        }

        return null;
    }
    public SetStopMusic setStopMusic(){
        Action a = super.getAction(SetStopMusic.TYPE);
        if (a != null) {
            if (a instanceof SetStopMusic) {
                return (SetStopMusic) a;
            }
        }

        return null;
    }
    public SetCCMinTemp setCCMinTemp(){
        Action a = super.getAction(SetCCMinTemp.TYPE);
        if (a != null) {
            if (a instanceof SetCCMinTemp) {
                return (SetCCMinTemp) a;
            }
        }

        return null;
    }
    public SetSceneAll setSceneAll(){
        Action a = super.getAction(SetSceneAll.TYPE);
        if (a != null) {
            if (a instanceof SetSceneAll) {
                return (SetSceneAll) a;
            }
        }

        return null;
    }
    public SetPlayLightMusic setPlayLightMusic(){
        Action a = super.getAction(SetPlayLightMusic.TYPE);
        if (a != null) {
            if (a instanceof SetPlayLightMusic) {
                return (SetPlayLightMusic) a;
            }
        }

        return null;
    }
    public SetOneKeyClean setOneKeyClean(){
        Action a = super.getAction(SetOneKeyClean.TYPE);
        if (a != null) {
            if (a instanceof SetOneKeyClean) {
                return (SetOneKeyClean) a;
            }
        }

        return null;
    }
    public SetRCSetTemp setRCSetTemp(){
        Action a = super.getAction(SetRCSetTemp.TYPE);
        if (a != null) {
            if (a instanceof SetRCSetTemp) {
                return (SetRCSetTemp) a;
            }
        }

        return null;
    }
    public SetFCMinTemp setFCMinTemp(){
        Action a = super.getAction(SetFCMinTemp.TYPE);
        if (a != null) {
            if (a instanceof SetFCMinTemp) {
                return (SetFCMinTemp) a;
            }
        }

        return null;
    }
    public SetFilterLifeBase setFilterLifeBase(){
        Action a = super.getAction(SetFilterLifeBase.TYPE);
        if (a != null) {
            if (a instanceof SetFilterLifeBase) {
                return (SetFilterLifeBase) a;
            }
        }

        return null;
    }
    public SetSceneChoose setSceneChoose(){
        Action a = super.getAction(SetSceneChoose.TYPE);
        if (a != null) {
            if (a instanceof SetSceneChoose) {
                return (SetSceneChoose) a;
            }
        }

        return null;
    }
    public SetRCMaxTemp setRCMaxTemp(){
        Action a = super.getAction(SetRCMaxTemp.TYPE);
        if (a != null) {
            if (a instanceof SetRCMaxTemp) {
                return (SetRCMaxTemp) a;
            }
        }

        return null;
    }
    public SetOutdoorTemp setOutdoorTemp(){
        Action a = super.getAction(SetOutdoorTemp.TYPE);
        if (a != null) {
            if (a instanceof SetOutdoorTemp) {
                return (SetOutdoorTemp) a;
            }
        }

        return null;
    }
    public SetRCMinTemp setRCMinTemp(){
        Action a = super.getAction(SetRCMinTemp.TYPE);
        if (a != null) {
            if (a instanceof SetRCMinTemp) {
                return (SetRCMinTemp) a;
            }
        }

        return null;
    }
    public SetCoolBeverage setCoolBeverage(){
        Action a = super.getAction(SetCoolBeverage.TYPE);
        if (a != null) {
            if (a instanceof SetCoolBeverage) {
                return (SetCoolBeverage) a;
            }
        }

        return null;
    }
    public SetIpAddr setIpAddr(){
        Action a = super.getAction(SetIpAddr.TYPE);
        if (a != null) {
            if (a instanceof SetIpAddr) {
                return (SetIpAddr) a;
            }
        }

        return null;
    }
    public SetInvadeAlarm setInvadeAlarm(){
        Action a = super.getAction(SetInvadeAlarm.TYPE);
        if (a != null) {
            if (a instanceof SetInvadeAlarm) {
                return (SetInvadeAlarm) a;
            }
        }

        return null;
    }
    public SetCCSet setCCSet(){
        Action a = super.getAction(SetCCSet.TYPE);
        if (a != null) {
            if (a instanceof SetCCSet) {
                return (SetCCSet) a;
            }
        }

        return null;
    }

    /**
     * PropertyGetter
     */
    public interface PropertyGetter {
        String getMode();

        int getRCSetTemp();

        int getRCMinTemp();

        int getRCMaxTemp();

        int getCCSetTemp();

        int getCCMinTemp();

        int getCCMaxTemp();

        int getFCSetTemp();

        int getFCMinTemp();

        int getFCMaxTemp();

        int getRCRealTemp();

        int getCCRealTemp();

        int getFCRealTemp();

        String getRCSet();

        String getCCSet();

        String getOneKeyClean();

        int getFilterLifeBase();

        int getFilterLife();

        String getSceneAll();

        String getSceneChoose();

        String getSceneName();

        String getOutdoorTemp();

        int getIndoorTemp();

        int getStartDays();

        int getError();

        String getVoiceEnable();

        String getWakeup();

        String getLightUpScreen();

        String getWeather();

        String getPlayMusic();

        String getPlayLightMusic();

        String getStopMusic();

        String getSmartCool();

        String getSmartFreeze();

        String getCoolBeverage();

        String getComReceiveData();

        int getSceneMode();

        boolean getScreenStatus();

        boolean getWaterAlarm();

        boolean getSmokeAlarm();

        boolean getInvadeAlarm();

        boolean getGasLeakAlarm();

        String getIpAddr();

    }

    /**
     * PropertySetter
     */
    public interface PropertySetter {
        void setMode(String value);

        void setRCSetTemp(int value);

        void setRCMinTemp(int value);

        void setRCMaxTemp(int value);

        void setCCSetTemp(int value);

        void setCCMinTemp(int value);

        void setCCMaxTemp(int value);

        void setFCSetTemp(int value);

        void setFCMinTemp(int value);

        void setFCMaxTemp(int value);

        void setRCSet(String value);

        void setCCSet(String value);

        void setOneKeyClean(String value);

        void setFilterLifeBase(int value);

        void setFilterLife(int value);

        void setSceneAll(String value);

        void setSceneChoose(String value);

        void setSceneName(String value);

        void setOutdoorTemp(String value);

        void setIndoorTemp(int value);

        void setStartDays(int value);

        void setVoiceEnable(String value);

        void setWakeup(String value);

        void setLightUpScreen(String value);

        void setWeather(String value);

        void setPlayMusic(String value);

        void setPlayLightMusic(String value);

        void setStopMusic(String value);

        void setSmartCool(String value);

        void setSmartFreeze(String value);

        void setCoolBeverage(String value);

        void setComReceiveData(String value);

        void setSceneMode(int value);

        void setScreenStatus(boolean value);

        void setWaterAlarm(boolean value);

        void setSmokeAlarm(boolean value);

        void setInvadeAlarm(boolean value);

        void setGasLeakAlarm(boolean value);

        void setIpAddr(String value);

    }

    /**
     * ActionsHandler
     */
    public interface ActionHandler {
        void onsetIndoorTemp(int IndoorTemp);
        void onsetFCMaxTemp(int FCMaxTemp);
        void onsetFCSetTemp(int FCSetTemp);
        void onsetGasLeakAlarm(boolean GasLeakAlarm);
        void onsetSmartFreeze(String SmartFreeze);
        void onsetPlayMusic(String PlayMusic);
        void onsetSmartCool(String SmartCool);
        void onsetStartDays(int StartDays);
        void onsetWeather(String Weather);
        void onsetCCMaxTemp(int CCMaxTemp);
        void onsetFilterLife(int FilterLife);
        void onsetCCSetTemp(int CCSetTemp);
        void onsetSceneMode(int SceneMode);
        void onsetRCSet(String RCSet);
        void onsetWaterAlarm(boolean WaterAlarm);
        void onsetVoiceEnable(String VoiceEnable);
        void onsetLightUpScreen(String LightUpScreen);
        void onsetScreenStatus(boolean ScreenStatus);
        void onsetComReceiveData(String ComReceiveData);
        void onsetSceneName(String SceneName);
        void onsetWakeup(String Wakeup);
        void onsetSmokeAlarm(boolean SmokeAlarm);
        void onsetMode(String Mode);
        void onsetStopMusic(String StopMusic);
        void onsetCCMinTemp(int CCMinTemp);
        void onsetSceneAll(String SceneAll);
        void onsetPlayLightMusic(String PlayLightMusic);
        void onsetOneKeyClean(String OneKeyClean);
        void onsetRCSetTemp(int RCSetTemp);
        void onsetFCMinTemp(int FCMinTemp);
        void onsetFilterLifeBase(int FilterLifeBase);
        void onsetSceneChoose(String SceneChoose);
        void onsetRCMaxTemp(int RCMaxTemp);
        void onsetOutdoorTemp(String OutdoorTemp);
        void onsetRCMinTemp(int RCMinTemp);
        void onsetCoolBeverage(String CoolBeverage);
        void onsetIpAddr(String IpAddr);
        void onsetInvadeAlarm(boolean InvadeAlarm);
        void onsetCCSet(String CCSet);
    }


    private MiotError onsetIndoorTemp(ActionInfo action) {
        int IndoorTemp1 = ((Vint) action.getArgumentValue(IndoorTemp.TYPE)).getValue();
        actionHandler.onsetIndoorTemp(IndoorTemp1);

        return MiotError.OK;
    }
    private MiotError onsetFCMaxTemp(ActionInfo action) {
        int FCMaxTemp1 = ((Vint) action.getArgumentValue(FCMaxTemp.TYPE)).getValue();
        actionHandler.onsetFCMaxTemp(FCMaxTemp1);

        return MiotError.OK;
    }
    private MiotError onsetFCSetTemp(ActionInfo action) {
        int FCSetTemp1 = ((Vint) action.getArgumentValue(FCSetTemp.TYPE)).getValue();
        actionHandler.onsetFCSetTemp(FCSetTemp1);

        return MiotError.OK;
    }
    private MiotError onsetGasLeakAlarm(ActionInfo action) {
        boolean GasLeakAlarm1 = ((Vbool) action.getArgumentValue(GasLeakAlarm.TYPE)).getValue();
        actionHandler.onsetGasLeakAlarm(GasLeakAlarm1);

        return MiotError.OK;
    }
    private MiotError onsetSmartFreeze(ActionInfo action) {
        String SmartFreeze1 = ((Vstring) action.getArgumentValue(SmartFreeze.TYPE)).getValue();
        actionHandler.onsetSmartFreeze(SmartFreeze1);

        return MiotError.OK;
    }
    private MiotError onsetPlayMusic(ActionInfo action) {
        String PlayMusic1 = ((Vstring) action.getArgumentValue(PlayMusic.TYPE)).getValue();
        actionHandler.onsetPlayMusic(PlayMusic1);

        return MiotError.OK;
    }
    private MiotError onsetSmartCool(ActionInfo action) {
        String SmartCool1 = ((Vstring) action.getArgumentValue(SmartCool.TYPE)).getValue();
        actionHandler.onsetSmartCool(SmartCool1);

        return MiotError.OK;
    }
    private MiotError onsetStartDays(ActionInfo action) {
        int StartDays1 = ((Vint) action.getArgumentValue(StartDays.TYPE)).getValue();
        actionHandler.onsetStartDays(StartDays1);

        return MiotError.OK;
    }
    private MiotError onsetWeather(ActionInfo action) {
        String Weather1 = ((Vstring) action.getArgumentValue(Weather.TYPE)).getValue();
        actionHandler.onsetWeather(Weather1);

        return MiotError.OK;
    }
    private MiotError onsetCCMaxTemp(ActionInfo action) {
        int CCMaxTemp1 = ((Vint) action.getArgumentValue(CCMaxTemp.TYPE)).getValue();
        actionHandler.onsetCCMaxTemp(CCMaxTemp1);

        return MiotError.OK;
    }
    private MiotError onsetFilterLife(ActionInfo action) {
        int FilterLife1 = ((Vint) action.getArgumentValue(FilterLife.TYPE)).getValue();
        actionHandler.onsetFilterLife(FilterLife1);

        return MiotError.OK;
    }
    private MiotError onsetCCSetTemp(ActionInfo action) {
        int CCSetTemp1 = ((Vint) action.getArgumentValue(CCSetTemp.TYPE)).getValue();
        actionHandler.onsetCCSetTemp(CCSetTemp1);

        return MiotError.OK;
    }
    private MiotError onsetSceneMode(ActionInfo action) {
        int SceneMode1 = ((Vint) action.getArgumentValue(SceneMode.TYPE)).getValue();
        actionHandler.onsetSceneMode(SceneMode1);

        return MiotError.OK;
    }
    private MiotError onsetRCSet(ActionInfo action) {
        String RCSet1 = ((Vstring) action.getArgumentValue(RCSet.TYPE)).getValue();
        actionHandler.onsetRCSet(RCSet1);

        return MiotError.OK;
    }
    private MiotError onsetWaterAlarm(ActionInfo action) {
        boolean WaterAlarm1 = ((Vbool) action.getArgumentValue(WaterAlarm.TYPE)).getValue();
        actionHandler.onsetWaterAlarm(WaterAlarm1);

        return MiotError.OK;
    }
    private MiotError onsetVoiceEnable(ActionInfo action) {
        String VoiceEnable1 = ((Vstring) action.getArgumentValue(VoiceEnable.TYPE)).getValue();
        actionHandler.onsetVoiceEnable(VoiceEnable1);

        return MiotError.OK;
    }
    private MiotError onsetLightUpScreen(ActionInfo action) {
        String LightUpScreen1 = ((Vstring) action.getArgumentValue(LightUpScreen.TYPE)).getValue();
        actionHandler.onsetLightUpScreen(LightUpScreen1);

        return MiotError.OK;
    }
    private MiotError onsetScreenStatus(ActionInfo action) {
        boolean ScreenStatus1 = ((Vbool) action.getArgumentValue(ScreenStatus.TYPE)).getValue();
        actionHandler.onsetScreenStatus(ScreenStatus1);

        return MiotError.OK;
    }
    private MiotError onsetComReceiveData(ActionInfo action) {
        String ComReceiveData1 = ((Vstring) action.getArgumentValue(ComReceiveData.TYPE)).getValue();
        actionHandler.onsetComReceiveData(ComReceiveData1);

        return MiotError.OK;
    }
    private MiotError onsetSceneName(ActionInfo action) {
        String SceneName1 = ((Vstring) action.getArgumentValue(SceneName.TYPE)).getValue();
        actionHandler.onsetSceneName(SceneName1);

        return MiotError.OK;
    }
    private MiotError onsetWakeup(ActionInfo action) {
        String Wakeup1 = ((Vstring) action.getArgumentValue(Wakeup.TYPE)).getValue();
        actionHandler.onsetWakeup(Wakeup1);

        return MiotError.OK;
    }
    private MiotError onsetSmokeAlarm(ActionInfo action) {
        boolean SmokeAlarm1 = ((Vbool) action.getArgumentValue(SmokeAlarm.TYPE)).getValue();
        actionHandler.onsetSmokeAlarm(SmokeAlarm1);

        return MiotError.OK;
    }
    private MiotError onsetMode(ActionInfo action) {
        String Mode1 = ((Vstring) action.getArgumentValue(Mode.TYPE)).getValue();
        actionHandler.onsetMode(Mode1);

        return MiotError.OK;
    }
    private MiotError onsetStopMusic(ActionInfo action) {
        String StopMusic1 = ((Vstring) action.getArgumentValue(StopMusic.TYPE)).getValue();
        actionHandler.onsetStopMusic(StopMusic1);

        return MiotError.OK;
    }
    private MiotError onsetCCMinTemp(ActionInfo action) {
        int CCMinTemp1 = ((Vint) action.getArgumentValue(CCMinTemp.TYPE)).getValue();
        actionHandler.onsetCCMinTemp(CCMinTemp1);

        return MiotError.OK;
    }
    private MiotError onsetSceneAll(ActionInfo action) {
        String SceneAll1 = ((Vstring) action.getArgumentValue(SceneAll.TYPE)).getValue();
        actionHandler.onsetSceneAll(SceneAll1);

        return MiotError.OK;
    }
    private MiotError onsetPlayLightMusic(ActionInfo action) {
        String PlayLightMusic1 = ((Vstring) action.getArgumentValue(PlayLightMusic.TYPE)).getValue();
        actionHandler.onsetPlayLightMusic(PlayLightMusic1);

        return MiotError.OK;
    }
    private MiotError onsetOneKeyClean(ActionInfo action) {
        String OneKeyClean1 = ((Vstring) action.getArgumentValue(OneKeyClean.TYPE)).getValue();
        actionHandler.onsetOneKeyClean(OneKeyClean1);

        return MiotError.OK;
    }
    private MiotError onsetRCSetTemp(ActionInfo action) {
        int RCSetTemp1 = ((Vint) action.getArgumentValue(RCSetTemp.TYPE)).getValue();
        actionHandler.onsetRCSetTemp(RCSetTemp1);

        return MiotError.OK;
    }
    private MiotError onsetFCMinTemp(ActionInfo action) {
        int FCMinTemp1 = ((Vint) action.getArgumentValue(FCMinTemp.TYPE)).getValue();
        actionHandler.onsetFCMinTemp(FCMinTemp1);

        return MiotError.OK;
    }
    private MiotError onsetFilterLifeBase(ActionInfo action) {
        int FilterLifeBase1 = ((Vint) action.getArgumentValue(FilterLifeBase.TYPE)).getValue();
        actionHandler.onsetFilterLifeBase(FilterLifeBase1);

        return MiotError.OK;
    }
    private MiotError onsetSceneChoose(ActionInfo action) {
        String SceneChoose1 = ((Vstring) action.getArgumentValue(SceneChoose.TYPE)).getValue();
        actionHandler.onsetSceneChoose(SceneChoose1);

        return MiotError.OK;
    }
    private MiotError onsetRCMaxTemp(ActionInfo action) {
        int RCMaxTemp1 = ((Vint) action.getArgumentValue(RCMaxTemp.TYPE)).getValue();
        actionHandler.onsetRCMaxTemp(RCMaxTemp1);

        return MiotError.OK;
    }
    private MiotError onsetOutdoorTemp(ActionInfo action) {
        String OutdoorTemp1 = ((Vstring) action.getArgumentValue(OutdoorTemp.TYPE)).getValue();
        actionHandler.onsetOutdoorTemp(OutdoorTemp1);

        return MiotError.OK;
    }
    private MiotError onsetRCMinTemp(ActionInfo action) {
        int RCMinTemp1 = ((Vint) action.getArgumentValue(RCMinTemp.TYPE)).getValue();
        actionHandler.onsetRCMinTemp(RCMinTemp1);

        return MiotError.OK;
    }
    private MiotError onsetCoolBeverage(ActionInfo action) {
        String CoolBeverage1 = ((Vstring) action.getArgumentValue(CoolBeverage.TYPE)).getValue();
        actionHandler.onsetCoolBeverage(CoolBeverage1);

        return MiotError.OK;
    }
    private MiotError onsetIpAddr(ActionInfo action) {
        String ipAddr = ((Vstring) action.getArgumentValue(IpAddr.TYPE)).getValue();
        actionHandler.onsetIpAddr(ipAddr);

        return MiotError.OK;
    }
    private MiotError onsetInvadeAlarm(ActionInfo action) {
        boolean InvadeAlarm1 = ((Vbool) action.getArgumentValue(InvadeAlarm.TYPE)).getValue();
        actionHandler.onsetInvadeAlarm(InvadeAlarm1);

        return MiotError.OK;
    }
    private MiotError onsetCCSet(ActionInfo action) {
        String CCSet1 = ((Vstring) action.getArgumentValue(CCSet.TYPE)).getValue();
        actionHandler.onsetCCSet(CCSet1);

        return MiotError.OK;
    }

    /**
     * Handle actions invocation & properties operation
     */
    private ActionHandler actionHandler;
    private PropertyGetter propertyGetter;
    private PropertySetter propertySetter;

    public void setHandler(ActionHandler handler, PropertyGetter getter, PropertySetter setter) {
        actionHandler = handler;
        propertyGetter = getter;
        propertySetter = setter;
    }

    @Override
    public MiotError onSet(Property property) {
        Log.e(TAG, "onSet");

        if (propertySetter == null) {
            return super.onSet(property);
        }

        ViomiDefined.Property p = ViomiDefined.Property.valueOf(property.getDefinition().getType());
        switch (p) {
            case Mode:
                propertySetter.setMode(((Vstring) property.getCurrentValue()).getValue());
                break;
            case RCSetTemp:
                propertySetter.setRCSetTemp(((Vint) property.getCurrentValue()).getValue());
                break;
            case RCMinTemp:
                propertySetter.setRCMinTemp(((Vint) property.getCurrentValue()).getValue());
                break;
            case RCMaxTemp:
                propertySetter.setRCMaxTemp(((Vint) property.getCurrentValue()).getValue());
                break;
            case CCSetTemp:
                propertySetter.setCCSetTemp(((Vint) property.getCurrentValue()).getValue());
                break;
            case CCMinTemp:
                propertySetter.setCCMinTemp(((Vint) property.getCurrentValue()).getValue());
                break;
            case CCMaxTemp:
                propertySetter.setCCMaxTemp(((Vint) property.getCurrentValue()).getValue());
                break;
            case FCSetTemp:
                propertySetter.setFCSetTemp(((Vint) property.getCurrentValue()).getValue());
                break;
            case FCMinTemp:
                propertySetter.setFCMinTemp(((Vint) property.getCurrentValue()).getValue());
                break;
            case FCMaxTemp:
                propertySetter.setFCMaxTemp(((Vint) property.getCurrentValue()).getValue());
                break;
            case RCSet:
                propertySetter.setRCSet(((Vstring) property.getCurrentValue()).getValue());
                break;
            case CCSet:
                propertySetter.setCCSet(((Vstring) property.getCurrentValue()).getValue());
                break;
            case OneKeyClean:
                propertySetter.setOneKeyClean(((Vstring) property.getCurrentValue()).getValue());
                break;
            case FilterLifeBase:
                propertySetter.setFilterLifeBase(((Vint) property.getCurrentValue()).getValue());
                break;
            case FilterLife:
                propertySetter.setFilterLife(((Vint) property.getCurrentValue()).getValue());
                break;
            case SceneAll:
                propertySetter.setSceneAll(((Vstring) property.getCurrentValue()).getValue());
                break;
            case SceneChoose:
                propertySetter.setSceneChoose(((Vstring) property.getCurrentValue()).getValue());
                break;
            case SceneName:
                propertySetter.setSceneName(((Vstring) property.getCurrentValue()).getValue());
                break;
            case OutdoorTemp:
                propertySetter.setOutdoorTemp(((Vstring) property.getCurrentValue()).getValue());
                break;
            case IndoorTemp:
                propertySetter.setIndoorTemp(((Vint) property.getCurrentValue()).getValue());
                break;
            case StartDays:
                propertySetter.setStartDays(((Vint) property.getCurrentValue()).getValue());
                break;
            case VoiceEnable:
                propertySetter.setVoiceEnable(((Vstring) property.getCurrentValue()).getValue());
                break;
            case Wakeup:
                propertySetter.setWakeup(((Vstring) property.getCurrentValue()).getValue());
                break;
            case LightUpScreen:
                propertySetter.setLightUpScreen(((Vstring) property.getCurrentValue()).getValue());
                break;
            case Weather:
                propertySetter.setWeather(((Vstring) property.getCurrentValue()).getValue());
                break;
            case PlayMusic:
                propertySetter.setPlayMusic(((Vstring) property.getCurrentValue()).getValue());
                break;
            case PlayLightMusic:
                propertySetter.setPlayLightMusic(((Vstring) property.getCurrentValue()).getValue());
                break;
            case StopMusic:
                propertySetter.setStopMusic(((Vstring) property.getCurrentValue()).getValue());
                break;
            case SmartCool:
                propertySetter.setSmartCool(((Vstring) property.getCurrentValue()).getValue());
                break;
            case SmartFreeze:
                propertySetter.setSmartFreeze(((Vstring) property.getCurrentValue()).getValue());
                break;
            case CoolBeverage:
                propertySetter.setCoolBeverage(((Vstring) property.getCurrentValue()).getValue());
                break;
            case ComReceiveData:
                propertySetter.setComReceiveData(((Vstring) property.getCurrentValue()).getValue());
                break;
            case SceneMode:
                propertySetter.setSceneMode(((Vint) property.getCurrentValue()).getValue());
                break;
            case ScreenStatus:
                propertySetter.setScreenStatus(((Vbool) property.getCurrentValue()).getValue());
                break;
            case WaterAlarm:
                propertySetter.setWaterAlarm(((Vbool) property.getCurrentValue()).getValue());
                break;
            case SmokeAlarm:
                propertySetter.setSmokeAlarm(((Vbool) property.getCurrentValue()).getValue());
                break;
            case InvadeAlarm:
                propertySetter.setInvadeAlarm(((Vbool) property.getCurrentValue()).getValue());
                break;
            case GasLeakAlarm:
                propertySetter.setGasLeakAlarm(((Vbool) property.getCurrentValue()).getValue());
                break;
            case IpAddr:
                propertySetter.setIpAddr(((Vstring) property.getCurrentValue()).getValue());
                break;

            default:
                return MiotError.IOT_RESOURCE_NOT_EXIST;
        }

        return MiotError.OK;
    }

    @Override
    public MiotError onGet(Property property) {
        Log.e(TAG, "onGet");

        if (propertyGetter == null) {
            return super.onGet(property);
        }

        ViomiDefined.Property p = ViomiDefined.Property.valueOf(property.getDefinition().getType());
        switch (p) {
            case Mode:
                property.setValue(propertyGetter.getMode());
                break;
            case RCSetTemp:
                property.setValue(propertyGetter.getRCSetTemp());
                break;
            case RCMinTemp:
                property.setValue(propertyGetter.getRCMinTemp());
                break;
            case RCMaxTemp:
                property.setValue(propertyGetter.getRCMaxTemp());
                break;
            case CCSetTemp:
                property.setValue(propertyGetter.getCCSetTemp());
                break;
            case CCMinTemp:
                property.setValue(propertyGetter.getCCMinTemp());
                break;
            case CCMaxTemp:
                property.setValue(propertyGetter.getCCMaxTemp());
                break;
            case FCSetTemp:
                property.setValue(propertyGetter.getFCSetTemp());
                break;
            case FCMinTemp:
                property.setValue(propertyGetter.getFCMinTemp());
                break;
            case FCMaxTemp:
                property.setValue(propertyGetter.getFCMaxTemp());
                break;
            case RCRealTemp:
                property.setValue(propertyGetter.getRCRealTemp());
                break;
            case CCRealTemp:
                property.setValue(propertyGetter.getCCRealTemp());
                break;
            case FCRealTemp:
                property.setValue(propertyGetter.getFCRealTemp());
                break;
            case RCSet:
                property.setValue(propertyGetter.getRCSet());
                break;
            case CCSet:
                property.setValue(propertyGetter.getCCSet());
                break;
            case OneKeyClean:
                property.setValue(propertyGetter.getOneKeyClean());
                break;
            case FilterLifeBase:
                property.setValue(propertyGetter.getFilterLifeBase());
                break;
            case FilterLife:
                property.setValue(propertyGetter.getFilterLife());
                break;
            case SceneAll:
                property.setValue(propertyGetter.getSceneAll());
                break;
            case SceneChoose:
                property.setValue(propertyGetter.getSceneChoose());
                break;
            case SceneName:
                property.setValue(propertyGetter.getSceneName());
                break;
            case OutdoorTemp:
                property.setValue(propertyGetter.getOutdoorTemp());
                break;
            case IndoorTemp:
                property.setValue(propertyGetter.getIndoorTemp());
                break;
            case StartDays:
                property.setValue(propertyGetter.getStartDays());
                break;
            case Error:
                property.setValue(propertyGetter.getError());
                break;
            case VoiceEnable:
                property.setValue(propertyGetter.getVoiceEnable());
                break;
            case Wakeup:
                property.setValue(propertyGetter.getWakeup());
                break;
            case LightUpScreen:
                property.setValue(propertyGetter.getLightUpScreen());
                break;
            case Weather:
                property.setValue(propertyGetter.getWeather());
                break;
            case PlayMusic:
                property.setValue(propertyGetter.getPlayMusic());
                break;
            case PlayLightMusic:
                property.setValue(propertyGetter.getPlayLightMusic());
                break;
            case StopMusic:
                property.setValue(propertyGetter.getStopMusic());
                break;
            case SmartCool:
                property.setValue(propertyGetter.getSmartCool());
                break;
            case SmartFreeze:
                property.setValue(propertyGetter.getSmartFreeze());
                break;
            case CoolBeverage:
                property.setValue(propertyGetter.getCoolBeverage());
                break;
            case ComReceiveData:
                property.setValue(propertyGetter.getComReceiveData());
                break;
            case SceneMode:
                property.setValue(propertyGetter.getSceneMode());
                break;
            case ScreenStatus:
                property.setValue(propertyGetter.getScreenStatus());
                break;
            case WaterAlarm:
                property.setValue(propertyGetter.getWaterAlarm());
                break;
            case SmokeAlarm:
                property.setValue(propertyGetter.getSmokeAlarm());
                break;
            case InvadeAlarm:
                property.setValue(propertyGetter.getInvadeAlarm());
                break;
            case GasLeakAlarm:
                property.setValue(propertyGetter.getGasLeakAlarm());
                break;
            case IpAddr:
                property.setValue(propertyGetter.getIpAddr());
                break;

            default:
                return MiotError.IOT_RESOURCE_NOT_EXIST;
        }

        return MiotError.OK;
    }

    @Override
    public MiotError onAction(ActionInfo action) {
        Log.e(TAG, "onAction: " + action.getType().toString());

        if (actionHandler == null) {
            return super.onAction(action);
        }

        ViomiDefined.Action a = ViomiDefined.Action.valueOf(action.getType());
        switch (a) {
            case setIndoorTemp:
                return onsetIndoorTemp(action);
            case setFCMaxTemp:
                return onsetFCMaxTemp(action);
            case setFCSetTemp:
                return onsetFCSetTemp(action);
            case setGasLeakAlarm:
                return onsetGasLeakAlarm(action);
            case setSmartFreeze:
                return onsetSmartFreeze(action);
            case setPlayMusic:
                return onsetPlayMusic(action);
            case setSmartCool:
                return onsetSmartCool(action);
            case setStartDays:
                return onsetStartDays(action);
            case setWeather:
                return onsetWeather(action);
            case setCCMaxTemp:
                return onsetCCMaxTemp(action);
            case setFilterLife:
                return onsetFilterLife(action);
            case setCCSetTemp:
                return onsetCCSetTemp(action);
            case setSceneMode:
                return onsetSceneMode(action);
            case setRCSet:
                return onsetRCSet(action);
            case setWaterAlarm:
                return onsetWaterAlarm(action);
            case setVoiceEnable:
                return onsetVoiceEnable(action);
            case setLightUpScreen:
                return onsetLightUpScreen(action);
            case setScreenStatus:
                return onsetScreenStatus(action);
            case setComReceiveData:
                return onsetComReceiveData(action);
            case setSceneName:
                return onsetSceneName(action);
            case setWakeup:
                return onsetWakeup(action);
            case setSmokeAlarm:
                return onsetSmokeAlarm(action);
            case setMode:
                return onsetMode(action);
            case setStopMusic:
                return onsetStopMusic(action);
            case setCCMinTemp:
                return onsetCCMinTemp(action);
            case setSceneAll:
                return onsetSceneAll(action);
            case setPlayLightMusic:
                return onsetPlayLightMusic(action);
            case setOneKeyClean:
                return onsetOneKeyClean(action);
            case setRCSetTemp:
                return onsetRCSetTemp(action);
            case setFCMinTemp:
                return onsetFCMinTemp(action);
            case setFilterLifeBase:
                return onsetFilterLifeBase(action);
            case setSceneChoose:
                return onsetSceneChoose(action);
            case setRCMaxTemp:
                return onsetRCMaxTemp(action);
            case setOutdoorTemp:
                return onsetOutdoorTemp(action);
            case setRCMinTemp:
                return onsetRCMinTemp(action);
            case setCoolBeverage:
                return onsetCoolBeverage(action);
            case setIpAddr:
                return onsetIpAddr(action);
            case setInvadeAlarm:
                return onsetInvadeAlarm(action);
            case setCCSet:
                return onsetCCSet(action);

            default:
                Log.e(TAG, "invalid action: " + a);
                break;
        }

        return MiotError.IOT_RESOURCE_NOT_EXIST;
    }
}