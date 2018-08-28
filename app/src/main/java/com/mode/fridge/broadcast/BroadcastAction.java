package com.mode.fridge.broadcast;

/**
 * Created by young2 on 2017/1/6.
 */

public class BroadcastAction {

    public static final String ACTION_SET_STATUS_VISIABLE = "com.viomi.fridge.action.ACTION_SET_STATUS_VISIABLE";//状态栏显示
    public static final String ACTION_SET_STATUS_INVISIABLE = "com.viomi.fridge.action.ACTION_SET_STATUS_INVISIABLE";//状态栏隐藏
    public static final String ACTION_LOCATION = "com.viomi.fridge.action.ACTION_LOCATION";//定位
    public static final String ACTION_WEATHER_GET = "com.viomi.fridge.action.ACTION_WEATHER_GET";//天气获取

    public static final String ACTION_APP_UPGRADE_CHECK = "com.viomi.fridge.action.ACTION_APP_UPGRADE_CHECK";//app版本检查
    public static final String ACTION_APP_DOWNLOAD_FINISH = "com.viomi.fridge.action.ACTION_APP_DOWNLOAD_FINISH";//app下载结果
    public static final String ACTION_APP_DOWNLOAD_PROGRESS = "com.viomi.fridge.action.ACTION_APP_DOWNLOAD_PROGRESS";//app下载进度
    public static final String ACTION_START_APP_UPGRADE = "com.viomi.fridge.action.ACTION_START_APP_UPGRADE";//启动app升级

    public static final String ACTION_SYSTEM_UPGRADE_CHECK = "com.viomi.fridge.action.ACTION_SYSTEM_UPGRADE_CHECK";//系统版本检查
    public static final String ACTION_SYSTEM_DOWNLOAD_FINISH = "com.viomi.fridge.action.ACTION_SYSTEM_DOWNLOAD_FINISH";//系统下载结果
    public static final String ACTION_SYSTEM_DOWNLOAD_PROGRESS = "com.viomi.fridge.action.ACTION_SYSTEM_DOWNLOAD_PROGRESS";//系统下载进度
    public static final String ACTION_START_SYSTEM_UPGRADE = "com.viomi.fridge.action.ACTION_START_SYSTEM_UPGRADE";//启动系统升级

    public static final String ACTION_MCU_UPGRADE_CHECK = "com.viomi.fridge.action.ACTION_MCU_UPGRADE_CHECK";//mcu版本检查
    public static final String ACTION_MCU_DOWNLOAD_FINISH = "com.viomi.fridge.action.ACTION_MCU_UPGRADE";//mcu下载结果
    public static final String ACTION_MCU_DOWNLOAD_PROGRESS = "com.viomi.fridge.action.ACTION_MCU_UPGRADE_PROGRESS";//mcu下载进度
    public static final String ACTION_MCU_UPGRADE_FINISH = "com.viomi.fridge.action.ACTION_MCU_UPGRADE_FINISH";//启动mcu,升级
    public static final String ACTION_MCU_UPGRADE_PROGRESS = "com.viomi.fridge.action.ACTION_MCU_UPGRADE_PROGRESS";//mcu升级进度

    public static final String ACTION_STOP_MUSIC = "com.viomi.fridge.action.ACTION_STOP_MUSIC";//停止音乐
    public static final String ACTION_DEVICE_ERROR_HAPPEN = "com.viomi.fridge.action.ACTION_DEVICE_ERROR_HAPPEN";//异常发生
    public static final String EXTRA_ERROR = "error";

    public static final String ACTION_REFRE_SCENE = "com.viomi.fridge.action.ACTION_REFRE_SCENE";//场景变换

    public static final String ACTION_VOICE_VISIBLE = "com.viomi.fridge.action.ACTION_VOICE_VISIBLE";//显示语音唤醒dailog
    public static final String ACTION_VOICE_GONE = "com.viomi.fridge.action.ACTION_VOICE_GONE";//隐藏语音唤醒dailog
    public static final String ACTION_ROOM_OPEN_HAPPEN= "com.viomi.fridge.action.ACTION_ROOM_OPEN_HAPPEN";//门开报警
    public static final String ACTION_UPLOAD_EVENT= "com.viomi.fridge.action.ACTION_UPLOAD_EVENT";//开启定时器（上传事件信息）
    public static final String ACTION_ROOM_OPEN_REMOVE= "com.viomi.fridge.action.ACTION_ROOM_OPEN_REMOVE";//门开报警消除
    public static final String ACTION_VOICE_INTRODUCE_VISIBLE = "com.viomi.fridge.action.ACTION_VOICE_INTRODUCE_VISIBLE";//显示语音介绍dailog
    public static final String ACTION_VOICE_INTRODUCE_GONE = "com.viomi.fridge.action.ACTION_VOICE_INTRODUCE_GONE";//隐藏语音介绍dailog

    public static final String ACTION_FAULT_HAPPEN= "com.viomi.fridge.action.ACTION_FAULT_HAPPEN";//异常发生
    public static final String ACTION_FAULT_REMOVE= "com.viomi.fridge.action.ACTION_REMOVE";//异常解除

    public static final String ACTION_VOICE_TALK_TEXT = "com.viomi.fridge.action.ACTION_VOICE_TALK_TEXT";//语音聊天字符
    public static final String EXTRA_VOICE_TALK_TYPE = "EXTRA_VOICE_TALK_TYPE";//听还是说
    public static final String EXTRA_VOICE_TALK_CONTEXT = "EXTRA_VOICE_TALK_CONTEXT";//说话内容

    public static final String ACTION_REPORT_PUSH_DEVICE= "com.viomi.fridge.action.ACTION_REPORT_PUSH_DEVICE";//setalias
    public static final String ACTION_REPORT_PUSH_USER= "com.viomi.fridge.action.ACTION_REPORT_PUSH_USER";//setalias
    public static final String ACTION_REPORT_PUSH_ADVERT= "com.viomi.fridge.action.ACTION_REPORT_PUSH_ADVERT";//
    public static final String ACTION_REPORT_PUSH_SET_TIME= "com.viomi.fridge.action.ACTION_REPORT_PUSH_SET_TIME";//setalias
    public static final String EXTRA_PUSH = "EXTRA_PUSH";

    public static final String ACTION_PUSH_MESSAGE= "com.viomi.fridge.action.ACTION_PUSH_MESSAGE";//推送到通知栏的
    public static final String EXTRA_PUSH_MSG = "EXTRA_PUSH_MSG";

    public static final String ACTION_DEVICE_CHOOSE= "com.viomi.fridge.action.ACTION_DEVICE_CHOOSE";//语音设备选择
    public static final String ACTION_WATERPURI_TEMP= "com.viomi.fridge.action.ACTION_WATERPURI_TEMP";//语音设备水温
    public static final String EXTRA_DATA = "EXTRA_DATA";

    public static final String ACTION_DEVICE_BIND = "com.viomi.fridge.action.ACTION_DEVICE_BIND";//设备被小米账号绑定
    public static final String ACTION_DEVICE_ALREADY_BIND = "com.viomi.fridge.action.ACTION_DEVICE_ALREADY_BIND";//设备已经被小米账号绑定
    public static final String ACTION_DEVICE_UNBIND = "com.viomi.fridge.action.ACTION_DEVICE_UNBIND";//设备被解绑

    public static final String ACTION_VERSION_UPDATE_CLICK = "com.viomi.fridge.action.ACTION_VERSION_UPDATE_CLICK";//点击了版本更新详情

    public static final String FLOATBTN_ON_FF = "com.viomi.fridge.action.FLOATBTN_ON_FF";

    public static final String ACTION_JINGDONG= "com.jd.smart.fridge.launcher.onresume.call";//京东升级广播

    public static final String INIT_APP_PROGRESS = "com.viomi.fridge.init.app.progress";//開啟应用进程
    public static final String CLOSE_APP_PROGRESS = "com.viomi.fridge.close.app.progress";//关闭应用进程

}
