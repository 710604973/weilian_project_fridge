package com.mode.fridge.common.rxbus;

/**
 * RxBus 消息定义
 * Created by nanquan on 2018/1/25.
 */
public class BusEvent {
    private int msgId;
    private Object msgObject;

    BusEvent(int msgId, Object msgObject) {
        this.msgId = msgId;
        this.msgObject = msgObject;
    }

    public int getMsgId() {
        return msgId;
    }

    public Object getMsgObject() {
        return msgObject;
    }

    private static final int MSG_BASE = 100;

    /**
     * 计时器 开始计时
     */
    public static final int MSG_TIMER_START = MSG_BASE + 1;

    /**
     * 计时器 结束计时
     */
    public static final int MSG_TIMER_STOP = MSG_BASE + 2;

    /**
     * 计时器 播放音乐
     */
    public static final int MSG_TIMER_PLAY_MUSIC = MSG_BASE + 3;

    /**
     * 计时器 结束音乐
     */
    public static final int MSG_TIMER_STOP_MUSIC = MSG_BASE + 4;

    /**
     * 计时器 倒计时
     */
    public static final int MSG_TIMER_TIMING = MSG_BASE + 5;

    /**
     * 收到消息推送
     */
    public static final int MSG_MESSAGE_PUSH = MSG_BASE + 6;

    /**
     * 打开消息中心
     */
    public static final int MSG_OPEN_MESSAGE_CENTER = MSG_BASE + 7;

    /**
     * 停止播放音乐
     */
    public static final int MSG_MUSIC_STOP = MSG_BASE + 8;

    /**
     * 日历天气更新
     */
    public static final int MSG_WEATHER_UPDATE = MSG_BASE + 9;

    /**
     * 食材更新
     */
    public static final int MSG_FOOD_UPDATE = MSG_BASE + 10;

    /**
     * 报警
     */
    public static final int MSG_WARN_ALARM = MSG_BASE + 11;

    /**
     * 互联网家设备集合
     */
    public static final int MSG_IOT_DEVICES = MSG_BASE + 12;

    /**
     * 关闭语音对话
     */
    public static final int MSG_CLOSE_SPEECH = MSG_BASE + 13;

    /**
     * 删除拍照缓存
     */
    public static final int MSG_DELETE_CAMERA_CACHE = MSG_BASE + 14;

    /**
     * 语音对话更新
     */
    public static final int MSG_SPEECH_UPDATE = MSG_BASE + 16;

    /**
     * 电子相册更新
     */
    public static final int MSG_ALBUM_UPDATE = MSG_BASE + 17;

    /**
     * 每分钟监听
     */
    public static final int MSG_TIME_MINUTE = MSG_BASE + 18;

    /**
     * 变温室场景更新
     */
    public static final int MSG_CHANGEABLE_SCENE_UPDATE = MSG_BASE + 19;

    /**
     * 登录成功
     */
    public static final int MSG_LOGIN_SUCCESS = MSG_BASE + 20;

    /**
     * App 版本状态
     */
    public static final int MSG_VERSION_UPDATE = MSG_BASE + 21;

    /**
     * 开始更新食材
     */
    public static final int MSG_START_FOOD_UPDATE = MSG_BASE + 22;

    /**
     * 首页开始滑动
     */
    public static final int MSG_HOME_START_SCROLL = MSG_BASE + 23;

    /**
     * 首页结束滑动
     */
    public static final int MSG_HOME_STOP_SCROLL = MSG_BASE + 24;

    /**
     * 服务器环境改变
     */
    public static final int MSG_SERVER_CHANGE = MSG_BASE + 25;

    /**
     * 注销成功
     */
    public static final int MSG_LOGOUT_SUCCESS = MSG_BASE + 26;

    /**
     * 显示门开报警
     */
    public static final int MSG_OPEN_ALARM_DISPLAY = MSG_BASE + 27;

    /**
     * 隐藏门开报警
     */
    public static final int MSG_OPEN_ALARM_DISMISS = MSG_BASE + 28;

    /**
     * 电子相册开始滚动
     */
    public static final int MSG_ALBUM_START_STROLL = MSG_BASE + 29;

    /**
     * 电子相册停止滚动
     */
    public static final int MSG_ALBUM_STOP_STROLL = MSG_BASE + 30;

    /**
     * 屏保开关
     */
    public static final int MSG_SCREEN_SAVER_UPDATE = MSG_BASE + 31;

    /**
     * 留言板更新
     */
    public static final int MSG_MESSAGE_BOARD_UPDATE = MSG_BASE + 32;

    /**
     * 食材编辑状态改变
     */
    public static final int MSG_FOOD_MANAGE_EDIT_CHANGE = MSG_BASE + 33;

    /**
     * 删除食材
     */
    public static final int MSG_FOOD_MANAGE_DELETE = MSG_BASE + 34;

    /**
     * 计时器语音结束
     */
    public static final int MSG_TIMER_SPEECH_STOP = MSG_BASE + 35;

    /**
     * 推送消息
     */
    public static final int MSG_PUSH_MESSAGE = MSG_BASE + 36;

    /**
     * 回收首页 WebView
     */
    public static final int MSG_REMOVE_HOME_MALL = MSG_BASE + 37;

    /**
     * 删除文件
     */
    public static final int MSG_FILE_DELETE = MSG_BASE + 38;

    /**
     * 暂停屏保计时
     */
    public static final int MSG_STOP_SCREEN_TIMER = MSG_BASE + 39;

    /**
     * 开始屏保计时
     */
    public static final int MSG_START_SCREEN_TIMER = MSG_BASE + 40;
    /**
     * 用戶显示成功
     */
    public static final int MSG_LOADED_USER_END = MSG_BASE + 41;
    /**
     * 用戶退出登录
     */
    public static final int MSG_LOGOUT_ACCOUNT_END = MSG_BASE + 42;
    /**
     * 进入商城
     */
    public static final int MSG_ENTER_SHOPPING = MSG_BASE + 43;
    /**
     * 刷新冰箱
     */
    public static final int MSG_REFRESH_FRIDGE = MSG_BASE + 44;
    /**
     * 上報消息
     */
    public static final int MSG_REPORT_FRIDGE = MSG_BASE + 45;
}