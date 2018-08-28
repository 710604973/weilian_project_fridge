package com.mode.fridge.defined;

import android.util.Log;

import com.xiaomi.miot.typedef.urn.ActionType;
import com.xiaomi.miot.typedef.urn.PropertyType;
import com.xiaomi.miot.typedef.urn.ServiceType;

public class ViomiDefined {

    private static final String TAG = "ViomiDefined";
    private static final String DOMAIN = "Viomi";
    private static final String _UUID = "-0000-1000-2000-000000AABBCC";

    private ViomiDefined() {
    }

    /**
     * Properties
     * urn:Viomi:property:Mode:0000
     * urn:Viomi:property:RCSetTemp:0000
     * urn:Viomi:property:RCMinTemp:0000
     * urn:Viomi:property:RCMaxTemp:0000
     * urn:Viomi:property:CCSetTemp:0000
     * urn:Viomi:property:CCMinTemp:0000
     * urn:Viomi:property:CCMaxTemp:0000
     * urn:Viomi:property:FCSetTemp:0000
     * urn:Viomi:property:FCMinTemp:0000
     * urn:Viomi:property:FCMaxTemp:0000
     * urn:Viomi:property:RCRealTemp:0000
     * urn:Viomi:property:CCRealTemp:0000
     * urn:Viomi:property:FCRealTemp:0000
     * urn:Viomi:property:RCSet:0000
     * urn:Viomi:property:CCSet:0000
     * urn:Viomi:property:OneKeyClean:0000
     * urn:Viomi:property:FilterLifeBase:0000
     * urn:Viomi:property:FilterLife:0000
     * urn:Viomi:property:SceneAll:0000
     * urn:Viomi:property:SceneChoose:0000
     * urn:Viomi:property:SceneName:0000
     * urn:Viomi:property:OutdoorTemp:0000
     * urn:Viomi:property:IndoorTemp:0000
     * urn:Viomi:property:StartDays:0000
     * urn:Viomi:property:Error:0000
     * urn:Viomi:property:VoiceEnable:0000
     * urn:Viomi:property:Wakeup:0000
     * urn:Viomi:property:LightUpScreen:0000
     * urn:Viomi:property:Weather:0000
     * urn:Viomi:property:PlayMusic:0000
     * urn:Viomi:property:PlayLightMusic:0000
     * urn:Viomi:property:StopMusic:0000
     * urn:Viomi:property:SmartCool:0000
     * urn:Viomi:property:SmartFreeze:0000
     * urn:Viomi:property:CoolBeverage:0000
     * urn:Viomi:property:ComReceiveData:0000
     * urn:Viomi:property:SceneMode:0000
     * urn:Viomi:property:ScreenStatus:0000
     * urn:Viomi:property:WaterAlarm:0000
     * urn:Viomi:property:SmokeAlarm:0000
     * urn:Viomi:property:InvadeAlarm:0000
     * urn:Viomi:property:GasLeakAlarm:0000
     * urn:Viomi:property:IpAddr:0000
     */
    public enum Property {
        Undefined(0),
        Mode(1),
        RCSetTemp(2),
        RCMinTemp(3),
        RCMaxTemp(4),
        CCSetTemp(5),
        CCMinTemp(6),
        CCMaxTemp(7),
        FCSetTemp(8),
        FCMinTemp(9),
        FCMaxTemp(10),
        RCRealTemp(11),
        CCRealTemp(12),
        FCRealTemp(13),
        RCSet(14),
        CCSet(15),
        OneKeyClean(16),
        FilterLifeBase(17),
        FilterLife(18),
        SceneAll(19),
        SceneChoose(20),
        SceneName(21),
        OutdoorTemp(22),
        IndoorTemp(23),
        StartDays(24),
        Error(25),
        VoiceEnable(26),
        Wakeup(27),
        LightUpScreen(28),
        Weather(29),
        PlayMusic(30),
        PlayLightMusic(31),
        StopMusic(32),
        SmartCool(33),
        SmartFreeze(34),
        CoolBeverage(35),
        ComReceiveData(36),
        SceneMode(37),
        ScreenStatus(38),
        WaterAlarm(39),
        SmokeAlarm(40),
        InvadeAlarm(41),
        GasLeakAlarm(42),
        IpAddr(43);

        private int value;

        Property(int value) {
            this.value = value;
        }

        public static Property valueOf(int value) {
            for (Property c : values()) {
                if (c.value() == value) {
                    return c;
                }
            }

            Log.e(TAG, "invalid value: " + value);

            return Undefined;
        }

        public static Property valueOf(PropertyType type) {
            if (!type.getDomain().equals(DOMAIN)) {
                return Undefined;
            }

            for (Property c : values()) {
                if (c.toString().equals(type.getSubType())) {
                    return c;
                }
            }

            return Undefined;
        }

        public int value() {
            return value;
        }

        public PropertyType toPropertyType() {
            return new PropertyType(DOMAIN, this.toString(), toShortUUID());
        }

        public String toUUID() {
            return String.format("%08X%s", value, _UUID);
        }

        public String toShortUUID() {
            return String.format("%04X", value);
        }
    }

    /**
     * Actions
     * urn:Viomi:action:setIndoorTemp:0000
     * urn:Viomi:action:setFCMaxTemp:0000
     * urn:Viomi:action:setFCSetTemp:0000
     * urn:Viomi:action:setGasLeakAlarm:0000
     * urn:Viomi:action:setSmartFreeze:0000
     * urn:Viomi:action:setPlayMusic:0000
     * urn:Viomi:action:setSmartCool:0000
     * urn:Viomi:action:setStartDays:0000
     * urn:Viomi:action:setWeather:0000
     * urn:Viomi:action:setCCMaxTemp:0000
     * urn:Viomi:action:setFilterLife:0000
     * urn:Viomi:action:setCCSetTemp:0000
     * urn:Viomi:action:setSceneMode:0000
     * urn:Viomi:action:setRCSet:0000
     * urn:Viomi:action:setWaterAlarm:0000
     * urn:Viomi:action:setVoiceEnable:0000
     * urn:Viomi:action:setLightUpScreen:0000
     * urn:Viomi:action:setScreenStatus:0000
     * urn:Viomi:action:setComReceiveData:0000
     * urn:Viomi:action:setSceneName:0000
     * urn:Viomi:action:setWakeup:0000
     * urn:Viomi:action:setSmokeAlarm:0000
     * urn:Viomi:action:setMode:0000
     * urn:Viomi:action:setStopMusic:0000
     * urn:Viomi:action:setCCMinTemp:0000
     * urn:Viomi:action:setSceneAll:0000
     * urn:Viomi:action:setPlayLightMusic:0000
     * urn:Viomi:action:setOneKeyClean:0000
     * urn:Viomi:action:setRCSetTemp:0000
     * urn:Viomi:action:setFCMinTemp:0000
     * urn:Viomi:action:setFilterLifeBase:0000
     * urn:Viomi:action:setSceneChoose:0000
     * urn:Viomi:action:setRCMaxTemp:0000
     * urn:Viomi:action:setOutdoorTemp:0000
     * urn:Viomi:action:setRCMinTemp:0000
     * urn:Viomi:action:setCoolBeverage:0000
     * urn:Viomi:action:setIpAddr:0000
     * urn:Viomi:action:setInvadeAlarm:0000
     * urn:Viomi:action:setCCSet:0000
     * ...
     */
    public enum Action {
        Undefined(0),
        setIndoorTemp(1),
        setFCMaxTemp(2),
        setFCSetTemp(3),
        setGasLeakAlarm(4),
        setSmartFreeze(5),
        setPlayMusic(6),
        setSmartCool(7),
        setStartDays(8),
        setWeather(9),
        setCCMaxTemp(10),
        setFilterLife(11),
        setCCSetTemp(12),
        setSceneMode(13),
        setRCSet(14),
        setWaterAlarm(15),
        setVoiceEnable(16),
        setLightUpScreen(17),
        setScreenStatus(18),
        setComReceiveData(19),
        setSceneName(20),
        setWakeup(21),
        setSmokeAlarm(22),
        setMode(23),
        setStopMusic(24),
        setCCMinTemp(25),
        setSceneAll(26),
        setPlayLightMusic(27),
        setOneKeyClean(28),
        setRCSetTemp(29),
        setFCMinTemp(30),
        setFilterLifeBase(31),
        setSceneChoose(32),
        setRCMaxTemp(33),
        setOutdoorTemp(34),
        setRCMinTemp(35),
        setCoolBeverage(36),
        setIpAddr(37),
        setInvadeAlarm(38),
        setCCSet(39);

        private int value;

        Action(int value) {
            this.value = value;
        }

        public static Action valueOf(int value) {
            for (Action c : values()) {
                if (c.value() == value) {
                    return c;
                }
            }

            Log.e(TAG, "invalid value: " + value);

            return Undefined;
        }

        public static Action valueOf(ActionType type) {
            if (! type.getDomain().equals(DOMAIN)) {
                return Undefined;
            }

            for (Action v : values()) {
                if (v.toString().equals(type.getSubType())) {
                    return v;
                }
            }

            return Undefined;
        }

        public int value() {
            return value;
        }

        public ActionType toActionType() {
            return new ActionType(DOMAIN, this.toString(), toShortUUID());
        }

        public String toUUID() {
            return String.format("%08X%s", value, _UUID);
        }

        public String toShortUUID() {
            return String.format("%04X", value);
        }
    }

    /**
     * Servics
     * urn:Viomi:service:FridgeService:0000
     */
    public enum Service {
        Undefined(0),
        FridgeService(1);

        private int value;

        Service(int value) {
            this.value = value;
        }

        public static Service valueOf(int value) {
            for (Service c : values()) {
                if (c.value() == value) {
                    return c;
                }
            }

            Log.e(TAG, "invalid value: " + value);

            return Undefined;
        }

        public static Service valueOf(ServiceType type) {
            if (! type.getDomain().equals(DOMAIN)) {
                return Undefined;
            }

            for (Service v : values()) {
                if (v.toString().equals(type.getSubType())) {
                    return v;
                }
            }

            return Undefined;
        }

        public int value() {
            return value;
        }

        public ServiceType toServiceType() {
            return new ServiceType(DOMAIN, this.toString(), toShortUUID());
        }

        public String toUUID() {
            return String.format("%08X%s", value, _UUID);
        }

        public String toShortUUID() {
            return String.format("%04X", value);
        }
    }
}