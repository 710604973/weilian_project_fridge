package com.mode.fridge.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Environment;

import com.mode.fridge.MyApplication;
import com.mode.fridge.bean.SerialInfo;
//import com.viomi.fridge.manager.H5UrlManager;

/**
 * Created by young2 on 2016/12/15.
 */

public class GlobalParams {

    public static boolean LOG_DEBUG = false;//log输出调试模式
    public static boolean HTTP_DEBUG = false;//后台接口调试模式，ture：云米净水器测试环境，false：云米商城正式环境
    public static boolean H5_DEBUG = false;//H5插件调试模式，true：服务器插件，false：插件加载本地，asset或者下载
    public static boolean STATS_ENABLE = true;//埋点统计使能


    private static GlobalParams INSTANCE;
    private Context mContext = MyApplication.getContext();
    public Typeface mDigitalTypeface = Typeface.createFromAsset(mContext.getAssets(), "fonts/DINCond-Medium.otf");

    private SharedPreferences sharedPreferences = mContext.getSharedPreferences(SharedPreferencesStr, Context.MODE_PRIVATE);
    private SharedPreferences.Editor editor = sharedPreferences.edit();
    private static String SharedPreferencesStr = "Params";

    //    public static boolean isTrueDevice=true;//是否真机器，接了主控板
    public static String Menu_Url = null;//菜谱连接
    public static boolean Clock_Start = false;//定时器是否开启

    public static String Key_App_Start_Time = "Key_App_Start_Time"; //app启动时间
    public static long Value_App_Start_Time;

    public static String Key_Commodity_Inspection_Enable = "Key_Commodity_Inspection_Enable"; //是否在商检模式
    public static boolean Value_Commodity_Inspection_Enable;

    public static String Key_Clod_Closet_Temp_Befor_Close = "Key_Clod_Closet_Temp_Befor_Close"; //关闭前的冷藏室温度，用于重新开启时
    public static int Value_Clod_Closet_Temp_Befor_Close = SerialInfo.COLD_COLSET_DEFAULT_TEMP;

    public static String Key_Changeable_Room_Temp_Befor_Close = "Key_Changeable_Room_Temp_Befor_Close"; //关闭前的变温室温度，用于重新开启时
    public static int Value_Changeable_Room_Temp_Befor_Close = SerialInfo.TEMP_CHANGEABLE_ROOM_DEFAULT_TEMP;

    public static String Key_Freeze_Room_Temp_Befor_Quick_Freeze = "Key_Feeeze_Room_Temp_Befor_Quick_Freeze"; //进入速冻前的变温室温度，用于重新开启时
    public static int Value_Freeze_Room_Temp_Befor_Quick_Freeze = SerialInfo.FREEZING_ROOM_DEFAULT_TEMP;

    public static String Key_CC_Room_Temp_Befor_Quick_Cool = "Key_CC_Room_Temp_Befor_Quick_Cool"; //进入速冷前的变温室温度，用于重新开启时
    public static int Value_CC_Room_Temp_Befor_Quick_Cool = SerialInfo.COLD_COLSET_DEFAULT_TEMP;


    public static String Key_Filter_Life_Time = "Key_Filter_Life_Time"; //滤芯寿命
    public static int Value_Filter_Life_Time = 0;

    public static String Key_AppUpgradeUrl = "Key_AppUpgradeUrl";//app升级url
    public static String Value_AppUpgradeUrl = "";

    public static String Key_Version_Vmall_H5 = "Key_Version_Vmall_H5";
    public static int Value_Version_Vmall_H5;

    public static String Key_Domain = "Key_Domain";//域名
    public static String Value_Domain = "192.168.0.129:3000";

    public static String Key_Scene_Str = "Key_Scene_Str"; //保存场景列表
    public static String Value_Scene_Str = "";

    public static String Key_Scene_ChooseName = "Key_Scene_ChooseName"; //保存场景
    public static String Value_Scene_ChooseName = "";

    public static String Key_Test_CutTime_Enable = "Key_CutTime_Enable"; //是否启动检测缩时功能
    public static boolean Value_Test_CutTime_Enable;

    public static String Key_OutDoor_Temp = "Key_OutDoor_Temp"; //室外温度
    public static String Value_OutDoor_Temp = "--";

    public static String Key_Voice_Enable = "Key_Voice_Enable"; //是否语音开启
    public static boolean Value_Voice_Enable = true;

    public static String Key_App_VersionCode = "Key_App_VersionCode";
    public static int Value_App_VersionCode;


    public static String Key_WeatherReport = "Key_WeatherReport";
    public static String Value_WeatherReport = "";

    public static int Version_Vmall_H5;//商城h5版本

    public static String Key_Vmall_Debug_Enable = "Key_Vmall_Debug_Enable"; //是否启动商城调试
    public static boolean Value_Vmall_Debug_Enable = false;

    public static String Key_Vmall_Http_Enable = "Key_Vmall_Http_Enable"; //是否启动商城测试环境
    public static boolean Value_Vmall_Http_Enable = false;

    public static String Key_Upgrade_Test_Enable = "Key_Upgrade_Test_Enable"; //是否启动升级测试环境
    public static boolean Value_Upgrade_Test_Enable = false;

    public static String Key_Vmall_Debug_Url = "Key_Vmall_Debug_Url"; //商城调试url,"0"用后台地址，192开头是本地调试
    public static String Value_Vmall_Debug_Url = "";

    public static String Key_Start_Hour = "Key_Start_Day"; //启动天数，存小时
    public static int Value_Start_Hour = 1;

    public static String Key_Location_City_Name = "Key_Location_City_Name"; //定位城市名称
    public static String Value_Location_City_Name = "";

    public static String Key_Location_City_Code = "Key_Location_City_Code"; //定位城市编码
    public static String Value_Location_City_Code = "";

    public static String Key_Voice_Never_TIPS = "Key_Voice_Never_TIPS"; //不再显示语音提醒
    public static boolean Value_Voice_Never_TIPS = false;

    public static String Key_UserId = "Key_UserId";
    public static int Value_UserId;

    public static String Key_Scan_Phone_Type = "Key_Scan_Phone_Type";//扫描登录手机类型，0：android；1：ios
    public static int Value_Scan_Phone_Type = 0;

    public static String Key_Viomi_Login_Time = "Key_Viomi_Login_Time";//第一次启动app或登录云米帐号时间
    public static long Vlaue_Viomi_Login_Time = 0;

    public static String Key_Xiaomi_Login_Time = "Key_Xiaomi_Login_Time";//第一次启动app或登录小米帐号时间
    public static long Vlaue_Xiaomi_Login_Time = 0;

    public static String Key_Voice_First_KFC = "Key_Voice_First_KFC"; //第一次进入肯德基
    public static boolean Value_Voice_First_KFC = true;

    public static String Key_Device_Bind_Flag = "Key_Device_Bind_Flag"; //保存设备是否绑定
    public static boolean Value_Device_Bind_Flag = false;


    public static String HUMANSENSOR = "humansensor";


    public static GlobalParams getInstance() {
        if (INSTANCE == null) {
            synchronized (GlobalParams.class) {
                if (INSTANCE == null) {
                    INSTANCE = new GlobalParams();
                }
            }
        }
        return INSTANCE;
    }

    public SharedPreferences getSharedPreferences() {
        return sharedPreferences;
    }

    public SharedPreferences.Editor getgetSharedPreferencesEdior() {
        return editor;
    }

    public void clearSharedPreferences() {
        editor.clear();
        editor.commit();
    }

    /**
     * 是否存在SDCard
     */
    public static boolean hasSdcard() {
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }

    public long getAppStartTime() {
        return sharedPreferences.getLong(Key_App_Start_Time, Value_App_Start_Time);
    }

    public boolean setAppStartTime(long time) {
        editor.putLong(Key_App_Start_Time, time);
        return editor.commit();
    }

    public boolean isCommodityInspection() {
        return sharedPreferences.getBoolean(Key_Commodity_Inspection_Enable, Value_Commodity_Inspection_Enable);
    }

    public boolean setCommodityInspection(boolean enable) {
        editor.putBoolean(Key_Commodity_Inspection_Enable, enable);
        return editor.commit();
    }

    /***
     * 获取变温室关闭前的温度
     *
     * @return
     */
    public int getChangeableRoomTempBeforClose() {
        return sharedPreferences.getInt(Key_Changeable_Room_Temp_Befor_Close, Value_Changeable_Room_Temp_Befor_Close);
    }

    /***
     * 设置变温室关闭前的温度
     *
     * @param temp
     * @return
     */
    public boolean setChangeableRoomTempBeforClose(int temp) {
        editor.putInt(Key_Changeable_Room_Temp_Befor_Close, temp);
        return editor.commit();
    }

    /***
     * 获取冷藏室关闭前的温度
     *
     * @return
     */
    public int getClodClosetTempBeforClose() {
        return sharedPreferences.getInt(Key_Clod_Closet_Temp_Befor_Close, Value_Clod_Closet_Temp_Befor_Close);
    }

    /***
     * 设置冷藏室关闭前的温度
     *
     * @param temp
     * @return
     */
    public boolean setClodClosetTempBeforClose(int temp) {
        editor.putInt(Key_Clod_Closet_Temp_Befor_Close, temp);
        return editor.commit();
    }

    /***
     * 获取滤芯寿命
     *
     * @return
     */
    public int getFilterLifeTime() {
        return sharedPreferences.getInt(Key_Filter_Life_Time, Value_Filter_Life_Time);
    }

    /***
     * 设置滤芯寿命
     *
     * @return
     */
    public boolean setFilterLifeTime(int time) {
        editor.putInt(Key_Filter_Life_Time, time);
        return editor.commit();
    }

    /***
     * 获取速冻前的温度
     *
     * @return
     */
    public int getFreezeRoomTempBeforQuickFreeze() {
        return sharedPreferences.getInt(Key_Freeze_Room_Temp_Befor_Quick_Freeze, Value_Freeze_Room_Temp_Befor_Quick_Freeze);
    }

    /***
     * 设置速冻前的温度
     *
     * @param temp
     * @return
     */
    public boolean setFreezeRoomTempBeforQuickFreeze(int temp) {
        editor.putInt(Key_Freeze_Room_Temp_Befor_Quick_Freeze, temp);
        return editor.commit();
    }


    /***
     * 获取速冷前的温度
     *
     * @return
     */
    public int getRoomTempBeforQuickCool() {
        return sharedPreferences.getInt(Key_CC_Room_Temp_Befor_Quick_Cool, Value_CC_Room_Temp_Befor_Quick_Cool);
    }

    /***
     * 设置速冷前的温度
     *
     * @param temp
     * @return
     */
    public boolean setRoomTempBeforQuickCool(int temp) {
        editor.putInt(Key_CC_Room_Temp_Befor_Quick_Cool, temp);
        return editor.commit();
    }

    /***
     * 取app升级下载路径
     *
     * @return
     */
    public String getAppUpgradeUrl() {
        return sharedPreferences.getString(Key_AppUpgradeUrl, Value_AppUpgradeUrl);
    }

    /***
     * 设置app升级下载路径
     *
     * @return 结果
     */
    public boolean setAppUpgradeUrl(String url) {
        editor.putString(Key_AppUpgradeUrl, url);
        return editor.commit();
    }

    /***
     * 设置H5版本
     *
     * @param version
     * @return 结果
     */
    public boolean setVmallH5Version(int version) {
        editor.putInt(Key_Version_Vmall_H5, version);
        return editor.commit();
    }

    /***
     * 取H5版本
     *
     * @return
     */
    public int getVmallH5Version() {
        return sharedPreferences.getInt(Key_Version_Vmall_H5, Value_Version_Vmall_H5);
    }

    /***
     * 取商城域名
     *
     * @return 商城域名
     */
    public String getVmallDomain() {
        return sharedPreferences.getString(Key_Domain, Value_Domain);
    }

    /***
     * 设置商城域名
     *
     * @param domain
     * @return 结果
     */
    public boolean setVmallDomain(String domain) {
        editor.putString(Key_Domain, domain);
        return editor.commit();
    }

    /***
     * 用户场景保存
     *
     * @param json
     */
    public void setSceneStr(String json) {
        if (sharedPreferences != null && editor != null) {
            editor.putString(Key_Scene_Str, json);
            editor.commit();
        }
    }

    /***
     * 用户场景获取
     *
     * @return
     */
    public String getSceneStr() {
        return sharedPreferences.getString(Key_Scene_Str, Value_Scene_Str);
    }


    /***
     * 获取选中的自定义场景
     *
     * @return
     */
    public String getSceneChoose() {
        return sharedPreferences.getString(Key_Scene_ChooseName, Value_Scene_ChooseName);
    }

    /***
     * 设置选中的自定义场景

     * @return
     */
    public boolean setSceneChoose(String name) {
        editor.putString(Key_Scene_ChooseName, name);
        return editor.commit();
    }

    public boolean isTestCutTimeEnable() {
        return sharedPreferences.getBoolean(Key_Test_CutTime_Enable, Value_Test_CutTime_Enable);
    }

    public boolean setTestCutTimeEnable(boolean enable) {
        editor.putBoolean(Key_Test_CutTime_Enable, enable);
        return editor.commit();
    }

    public String getOutdoorTemp() {
        return sharedPreferences.getString(Key_OutDoor_Temp, Value_OutDoor_Temp);
    }

    public boolean setOutdoorTemp(String temp) {
        editor.putString(Key_OutDoor_Temp, temp);
        return editor.commit();
    }

    public String getWeatherReport() {
        return sharedPreferences.getString(Key_WeatherReport, Value_WeatherReport);
    }

    //首页天气保存
    public void setWeatherReport(String weatherReport) {
        editor.putString(Key_WeatherReport, weatherReport);
        editor.commit();
    }


    public boolean isVoiceEnabe() {
        return sharedPreferences.getBoolean(Key_Voice_Enable, Value_Voice_Enable);
    }

    public boolean setVoiceEnabe(boolean enable) {
        editor.putBoolean(Key_Voice_Enable, enable);
        return editor.commit();
    }


    public int getAppVersionCode() {
        return sharedPreferences.getInt(Key_App_VersionCode, Value_App_VersionCode);
    }

    public boolean setAppVersionCode(int versionCode) {
        editor.putInt(Key_App_VersionCode, versionCode);
        return editor.commit();
    }

    public boolean isVmallDebug() {
        return sharedPreferences.getBoolean(Key_Vmall_Debug_Enable, Value_Vmall_Debug_Enable);
    }

    public boolean setVmallDebug(boolean enable) {
        editor.putBoolean(Key_Vmall_Debug_Enable, enable);
        return editor.commit();
    }

    public boolean isVmallHttpDebug() {
        return sharedPreferences.getBoolean(Key_Vmall_Http_Enable, Value_Vmall_Http_Enable);
    }

    public boolean setVmallHttpDebug(boolean enable) {
        editor.putBoolean(Key_Vmall_Http_Enable, enable);
        return editor.commit();
    }

    public boolean isUpgradeTestEnable() {
        return sharedPreferences.getBoolean(Key_Upgrade_Test_Enable, Value_Upgrade_Test_Enable);
    }

    public boolean setUpgradeTestEnable(boolean enable) {
        editor.putBoolean(Key_Upgrade_Test_Enable, enable);
        return editor.commit();
    }

    public String getVmallDebugUrl() {
        return sharedPreferences.getString(Key_Vmall_Debug_Url, Value_Vmall_Debug_Url);
    }

    public boolean setVmallDebugUrl(String url) {
        editor.putString(Key_Vmall_Debug_Url, url);
        return editor.commit();
    }

    /***
     * 获取启动天数
     *
     * @return
     */
    public int getStartHour() {
        return sharedPreferences.getInt(Key_Start_Hour, Value_Start_Hour);
    }

    /***
     * 设置启动天数
     *
     * @return
     */
    public boolean setStartHour(int hour) {
        editor.putInt(Key_Start_Hour, hour);
        return editor.commit();
    }

    /***
     * 获取定位的城市名称
     * @return
     */
    public String getLocationCityName() {
        return sharedPreferences.getString(Key_Location_City_Name, Value_Location_City_Name);
    }

    /***
     * 保存定位的城市名称
     * @param city
     * @return
     */
    public boolean setLocationCityName(String city) {
        editor.putString(Key_Location_City_Name, city);
        return editor.commit();
    }

    /***
     * 获取定位的城市编码
     * @return
     */
    public String getLocationCityCode() {
        return sharedPreferences.getString(Key_Location_City_Code, Value_Location_City_Code);
    }

    /***
     * 保存定位的城市编码
     * @param cityCode
     * @return
     */
    public boolean setLocationCityCode(String cityCode) {
        editor.putString(Key_Location_City_Code, cityCode);
        return editor.commit();
    }

    /***
     * 不再提示语音唤醒界面
     * @return
     */
    public boolean getVoiceNerverTips() {
        return sharedPreferences.getBoolean(Key_Voice_Never_TIPS, Value_Voice_Never_TIPS);
    }

    /***
     * 设置不再提示语音唤醒界面
     * @param enable
     * @return
     */
    public boolean setVoiceNerverTips(boolean enable) {
        editor.putBoolean(Key_Voice_Never_TIPS, enable);
        return editor.commit();
    }

    /***
     * 获取USerId
     *
     * @return
     */
    public int getUserId() {
        return sharedPreferences.getInt(Key_UserId, Value_UserId);
    }

    /***
     * 设置USerId
     *
     * @return
     */
    public boolean setUserId(int id) {
        editor.putInt(Key_UserId, id);
        return editor.commit();
    }

    /***
     * 获取扫描登录手机类型
     *
     * @return
     */
    public int getScanPhoneType() {
        return sharedPreferences.getInt(Key_Scan_Phone_Type, Value_Scan_Phone_Type);
    }

    /***
     * 设置扫描登录手机类型
     *
     * @return
     */
    public boolean setScanPhoneType(int type) {
        editor.putInt(Key_Scan_Phone_Type, type);
        return editor.commit();
    }

    public boolean isHumanSensorSwitch() {
        return sharedPreferences.getBoolean(HUMANSENSOR, true);
    }

    public void setHumanSensorSwitch(boolean humanSensorSwitch) {
        editor.putBoolean(HUMANSENSOR, humanSensorSwitch);
        editor.commit();
    }

    /***
     * 获取第一次启动app或登录云米帐号时间
     * @return
     */
    public long getViomiLoginTime() {
        return sharedPreferences.getLong(Key_Viomi_Login_Time, Vlaue_Viomi_Login_Time);
    }

    /***
     * 设置第一次启动app或登录云米帐号时间
     * @param time
     * @return
     */
    public boolean setViomiLoginTime(long time) {
        editor.putLong(Key_Viomi_Login_Time, time);
        return editor.commit();
    }

    /***
     * 获取第一次启动app或登录小米帐号时间
     * @return
     */
    public long getXiaomiLoginTime() {
        return sharedPreferences.getLong(Key_Xiaomi_Login_Time, Vlaue_Xiaomi_Login_Time);
    }

    /***
     * 设置第一次启动app或登录小米帐号时间
     * @param time
     * @return
     */
    public boolean setXiaomiLoginTime(long time) {
        editor.putLong(Key_Xiaomi_Login_Time, time);
        return editor.commit();
    }

    /***
     * 是否第一次语音进入kfc
     * @return
     */
    public boolean isVoiceFirstKFC() {
        return sharedPreferences.getBoolean(Key_Voice_First_KFC, Value_Voice_First_KFC);
    }

    /***
     * 设置第一次语音进入kfc
     * @param enable
     * @return
     */
    public boolean setVoiceFirstKFC(boolean enable) {
        editor.putBoolean(Key_Voice_First_KFC, enable);
        return editor.commit();
    }

    /***
     * 是否设备绑定
     * @return
     */
    public boolean isDeviceBindFlag() {
        return sharedPreferences.getBoolean(Key_Device_Bind_Flag, Value_Device_Bind_Flag);
    }

    /***
     * 设置设备绑定标志
     * @param enable
     * @return
     */
    public boolean setDeviceBindFlag(boolean enable) {
        editor.putBoolean(Key_Device_Bind_Flag, enable);
        return editor.commit();
    }

    public int getGuideVersion() {
        return sharedPreferences.getInt("guideversion",0);
    }

    public void setGuideVersion(int guideVersion) {
        editor.putInt("guideversion", guideVersion);
        editor.commit();
    }

}
