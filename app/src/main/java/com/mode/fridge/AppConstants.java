package com.mode.fridge;

/**
 * 存放 App 全局常量
 * Created by William on 2018/1/2.
 */
public class AppConstants {
    // 设备相关
    public static final String MODEL_X1 = "viomi.fridge.x1";// 创维三门
    public static final String MODEL_X2 = "viomi.fridge.x2";// 双鹿 446 四门
    public static final String MODEL_X3 = "viomi.fridge.x3";// 美菱 462 法式
    public static final String MODEL_X4 = "viomi.fridge.x4";// 雪祺 450 对开门
    public static final String MODEL_X5 = "viomi.fridge.x5";// 美菱 521 十字四门
    public static final String MODEL_JD = "viomi.fridge.x41";// 京东冰箱

    public static final int X1_WRITE_BYTE_COUNT = 12;// X1 写入字节数
    public static final int X1_READ_BYTE_COUNT = 17;// X1 读取字节数
    public static final int X2_WRITE_BYTE_COUNT = 19;// X2 写入字节数
    public static final int X2_READ_BYTE_COUNT = 19;// X2 读取字节数
    public static final int X3_WRITE_BYTE_COUNT = 24;// X3 写入字节数
    public static final int X3_READ_BYTE_COUNT = 48;// X3 读取字节数
    public static final int X4_WRITE_BYTE_COUNT = 8;// X4 写入字节数
    public static final int X4_READ_BYTE_COUNT = 12;// X4 读取字节数
    public static final int X5_WRITE_BYTE_COUNT = 24;// X5 写入字节数
    public static final int X5_READ_BYTE_COUNT = 48;// X5 读取字节数

    public static final int X1_SERIAL_DATA_START = 0xAA;// X1 串口接收和发送数据开始位
    public static final int X2_SERIAL_DATA_START = (byte) 0x55;// X2 串口接收和发送数据开始位,两个
    public static final int X2_SERIAL_DATA_END = (byte) 0xaa;// X2 串口接收和发送数据结束位，两个
    public static final int X3_SERIAL_DATA_START_0 = (byte) 0x55;// X3 串口接收和发送数据开始位 0
    public static final int X3_SERIAL_DATA_START_1 = (byte) 0xAA;// X3 串口接收和发送数据开始位 1
    public static final int X3_OPERATE_TYPE_TEMP = 0x01;// 修改：各间室设定温度值
    public static final int X3_OPERATE_TYPE_MODE = 0x02;// 修改：冰箱运行模式,间室开关
    public static final int X3_OPERATE_TYPE_CHECK_STATUS = 0x04;// 查询冰箱状态
    public static final int X3_OPERATE_TYPE_SELF_CHECK = 0x06;// 自检
    public static final int X3_OPERATE_TYPE_MAINTAIN = 0x07;// 维修
    public static final int X4_SERIAL_DATA_START = 0xF5;// X4 串口接收和发送数据开始位
    public static final int X5_SERIAL_DATA_START_0 = (byte) 0x55;// X5 串口接收和发送数据开始位 0
    public static final int X5_SERIAL_DATA_START_1 = (byte) 0xAA;// X5 串口接收和发送数据开始位 1
    public static final int X5_OPERATE_TYPE_TEMP = 0x01;// 修改：各间室设定温度值
    public static final int X5_OPERATE_TYPE_MODE = 0x02;// 修改：冰箱运行模式
    public static final int X5_OPERATE_TYPE_CHECK_STATUS = 0x04;// 查询冰箱状态
    public static final int X5_OPERATE_TYPE_SELF_CHECK = 0x06;// 自检
    public static final int X5_OPERATE_TYPE_MAINTAIN = 0x07;// 维修


    public static final int MCU_UPDATE_HEADER = 0xBB;// MCU 升级开始头部
    public static final int MCU_UPDATE_START = 0x01;// 功能位，MCU 升级开始位
    public static final int MCU_UPDATE_SEND_SUCCESS = 0x02;// 功能位，MCU 升级发送成功
    public static final int MCU_UPDATE_SEND_FAIL = 0x03;// 功能位，MCU 升级发送失败
    public static final int MCU_UPDATE_SUCCESS = 0x04;// 功能位，MCU 升级成功
    public static final int ROOM_CLOSED_TEMP = -50;// 仓室关闭时温度判断
    public static final int ROOM_TYPE_COLD = 0;// 冷藏室
    public static final int ROOM_TYPE_CHANGEABLE = 1;// 变温室
    public static final int ROOM_TYPE_FREEZING = 2;// 冷冻室
    public static final int ROOM_TYPE_CC = 3;// 冷藏变温区
    public static final int COLD_TEMP_DEFAULT = 6;// 冷藏室默认温度
    public static final int CHANGEABLE_TEMP_DEFAULT = -4;// 变温室默认温度
    public static final int FREEZING_TEMP_DEFAULT = -18;// 冷冻室默认温度

    public static final int MODE_NULL = 0x00;// 无模式
    public static final int MODE_SMART = 0x01;// 智能模式
    public static final int MODE_HOLIDAY = 0x04;// 假日模式

//    public static final String MODE_NULL = "none";// 无模式
//    public static final String MODE_SMART = "smart";// 智能模式
//    public static final String MODE_HOLIDAY = "holiday";// 假日模式

    public static final int MODE_QUICK_COLD = 0x20;// 速冷
    public static final int MODE_QUICK_FREEZE = 0x10;// 速冻
    public static final int ERROR_COMMUNICATION = 0x0001;// 通讯故障
    public static final int ERROR_RC_SENSOR = 0x0002;// 冷藏传感器故障
    public static final int ERROR_CC_SENSOR = 0x0004;// 变温传感器故障
    public static final int ERROR_FC_SENSOR = 0x0008;// 冷冻传感器故障
    public static final int ERROR_RC_DEFROST_SENSOR = 0x0010;// 冷藏化霜传感器故障
    public static final int ERROR_FC_DEFROST_SENSOR = 0x0020;// 冷冻化霜传感器故障
    public static final int ERROR_INDOOR_SENSOR = 0x0040;// 环境温度传感器故障
    public static final int ERROR_FAN_DOOR = 0x0080;// 风门故障
    public static final int ERROR_RC_FAN = 0x0100;// 冷藏风扇故障
    public static final int ERROR_CC_FAN = 0x0200;// 冷凝风扇故障
    public static final int ERROR_FC_FAN = 0x0400;// 冷冻风扇故障
    public static final int ERROR_DEFROST = 0x4000;// 除霜不良
    public static final int ERROR_CC_DEFROST_SENSOR = 0x200000;// 变温化霜传感器故障
    public static final int ERROR_HUMIDITY_SENSOR = 0x400000;// 湿度传感器湿度故障
    public static final int ERROR_HUMIDITY_TEMP_SENSOR = 0x800000;// 湿度传感器温度故障
    public static final int ERROR_RC_CC_SENSOR = 0x1000000;// 冷藏变温传感器故障

    public static final String VIOMI_FRIDGE_V1_URL = "device/ddd_Fridge_V1.xml";
    public static final String VIOMI_FRIDGE_U2_URL = "device/ddd_Fridge_U2.xml";
    public static final String VIOMI_FRIDGE_U3_URL = "device/ddd_Fridge_U3.xml";

    public static final String VIOMI_WASHER_U1_URL = "device/Viomi_Washer_U1.xml";
    public static final String VIOMI_WASHER_U2_URL = "device/Viomi_Washer_U2.xml";

    public static final String VIOMI_FAN_V1_URL = "device/Viomi_Fan_V1.xml";
    public static final String VIOMI_DISH_WASHER_V01_URL = "device/Viomi_Dishwasher_V01.xml";
    public static final String VIOMI_VACUUM_V1_URL = "device/Viomi_Vacuum_V1.xml";

    // 设备 Model

    public static final String VIOMI_FRIDGE_V1 = "viomi.fridge.v1";      //智能冰箱 iLive语音版
    public static final String VIOMI_FRIDGE_V2 = "viomi.fridge.v2";      //智能冰箱 iLive四门语音版
    public static final String VIOMI_FRIDGE_V3 = "viomi.fridge.v3";      //云米462大屏金属门冰箱
    public static final String VIOMI_FRIDGE_V31 = "viomi.fridge.v31";     //云米462大屏金属门冰箱京东
    public static final String VIOMI_FRIDGE_V4 = "viomi.fridge.v4";      //云米455大屏玻璃门冰箱
    public static final String VIOMI_FRIDGE_U1 = "viomi.fridge.u1";      //智能冰箱 iLive
    public static final String VIOMI_FRIDGE_U2 = "viomi.fridge.u2";      //云米456对开门冰箱
    public static final String VIOMI_FRIDGE_U3 = "viomi.fridge.u3";      //云米520对开门冰箱

    public static final String VIOMI_FRIDGE_W1 = "viomi.fridge.w1";      //米家冰箱
    public static final String VIOMI_FRIDGE_W2 = "viomi.fridge.w2";      //米家两门冰箱
    public static final String VIOMI_FRIDGE_X1 = "viomi.fridge.x1";      //云米三门大屏冰箱
    public static final String VIOMI_FRIDGE_X2 = "viomi.fridge.x2";      //云米四门大屏冰箱428
    public static final String VIOMI_FRIDGE_X3 = "viomi.fridge.x3";      //云米对开门大屏冰箱462
    public static final String VIOMI_FRIDGE_X4 = "viomi.fridge.x4";      //云米大屏冰箱456
    public static final String VIOMI_FRIDGE_X41 = "viomi.fridge.x41";      //云米21Face大屏冰箱456京东
    public static final String VIOMI_FRIDGE_X5 = "viomi.fridge.x5";      //云米十字门521大屏冰箱

    public static final String VIOMI_WASHER_U1 = "viomi.washer.u1";      //云米十字门521大屏冰箱
    public static final String VIOMI_WASHER_U2 = "viomi.washer.u2";      //云米十字门521大屏冰箱

    public static final String VIOMI_DISHWASHER_V01 ="viomi.dishwasher.v01";      //云米洗碗机
    public static final String VIOMI_VACUUM_V1= "viomi.vacuum.v1";      //云米互联网扫地机器人
    public static final String VIOMI_FAN_V1= "viomi.fan.v1";      //云米互联网直流变频风扇


    public static final String YUNMI_WATERPURI_V1 = "yunmi.waterpuri.v1";// V1 乐享版 人工智能系列
    public static final String YUNMI_WATERPURI_V2 = "yunmi.waterpuri.v2";// V1 尊享版 人工智能系列
    public static final String YUNMI_WATERPURI_S1 = "yunmi.waterpuri.s1";// S1 优享版 智能 3 合 1 系列
    public static final String YUNMI_WATERPURI_S2 = "yunmi.waterpuri.s2";// S1 标准版 智能 3 合 1 系列
    public static final String YUNMI_WATERPURI_C1 = "yunmi.waterpuri.c1";// C1 厨上版 新鲜水系列
    public static final String YUNMI_WATERPURI_C2 = "yunmi.waterpuri.c2";// C1 厨下版 新鲜水系列
    public static final String YUNMI_WATERPURI_X3 = "yunmi.waterpuri.x3";// 即热直饮净水器 X3 (100G)
    public static final String YUNMI_WATERPURI_X5 = "yunmi.waterpuri.x5";// 即热直饮净水器 X5 (400G)

    public static final String YUNMI_WATERPURIFIER_V1 = "yunmi.waterpurifier.v1";// 小米净水器厨上版
    public static final String YUNMI_WATERPURIFIER_V2 = "yunmi.waterpurifier.v2";// 小米净水器厨上版
    public static final String YUNMI_WATERPURIFIER_V3 = "yunmi.waterpurifier.v3";// 小米净水器厨下式
    public static final String YUNMI_WATERPURI_LX2 = "yunmi.waterpuri.lx2";// 小米净水器厨上版
    public static final String YUNMI_WATERPURI_LX3 = "yunmi.waterpuri.lx3";// 小米净水器厨下式
    public static final String YUNMI_KETTLE_R1 = "yunmi.kettle.r1";// 云米智能即热饮水吧（MINI）
    public static final String YUNMI_PLMACHINE_MG2 = "yunmi.plmachine.mg2";// 云米 MG2 型即热管线机

    public static final String VIOMI_HOOD_A5 = "viomi.hood.a5";// 智能油烟机 Free
    public static final String VIOMI_HOOD_A6 = "viomi.hood.a6";// 智能油烟机 Hurri 语音版
    public static final String VIOMI_HOOD_A4 = "viomi.hood.a4";// 智能油烟机 Hurri
    public static final String VIOMI_HOOD_A7 = "viomi.hood.a7";// 智能油烟机 Free 语音版
    public static final String VIOMI_HOOD_C1 = "viomi.hood.c1";// 云米智能油烟机 Cross
    public static final String VIOMI_HOOD_H1 = "viomi.hood.h1";// 智能油烟机侧吸标准版
    public static final String VIOMI_HOOD_H2 = "viomi.hood.h2";// 智能油烟机 Hurri 尊享版

//    public static final String VIOMI_DISH_WASHER_V01 = "viomi.dishwasher.v01";// 云米互联网洗碗机（8 套嵌入式）

    //    public static final String VIOMI_WASHER_U1 = "viomi.washer.u1";// 云米互联网洗衣机（9KG 语音版）
//    public static final String VIOMI_WASHER_U2 = "viomi.washer.u2";// 云米互联网洗烘一体机
//    public static final String VIOMI_FAN_V1 = "viomi.fan.v1";// 云米互联网直流变频风扇
//    public static final String VIOMI_VACUUM_V1 = "viomi.vacuum.v1";// 云米互联网扫地机器人

    // 网址相关
    public static final String URL_VMALL_DEBUG = "http://viomi-fridgex-vmall-test.mi-ae.net";// 云米商城测试环境
    public static final String URL_VMALL_RELEASE = "https://viomi-fridgex-vmall.mi-ae.net";// 云米商城正式环境

    public static final String URL_WASH_MACHINE_DEBUG = "http://viomi-fridgex-vmall-test.mi-ae.net";// 云米商城测试环境
    public static final String URL__WASH_MACHINE_RELEASE = "https://viomi-fridgex-vmall.mi-ae.net";// 云米商城正式环境

    public static final String URL_WATER_MAP = "http://analyse-static.mi-ae.net/watermappad.html?exist=no";// 水质地图
    public static final String URL_KFC_DEBUG = "http://viomi-kfc-test.mi-ae.net/entrance.html";// KFC 活动测试环境
    public static final String URL_KFC_RELEASE = "http://viomi-kfc-test.mi-ae.net/entrance.html";// KFC 活动正式环境

    // 各 Model 使用说明
    public static final String URL_GUIDE_X1 = "https://viomi-faq.mi-ae.net/viomi/fridgeintroduce/index.html";
    public static final String URL_GUIDE_X2 = "http://viomi-resource.mi-ae.net/fridgehelp.html?type=446";
    public static final String URL_GUIDE_X3 = "https://viomi-resource.mi-ae.net/fridgehelp.html?type=462x";
    public static final String URL_GUIDE_X4 = "http://viomi-resource.mi-ae.net/fridgehelp.html?type=450";
    public static final String URL_GUIDE_X5 = "http://viomi-resource.mi-ae.net/fridgehelp.html?type=521";


//    http://apicloud.mob.com/v1/weather/query?key=26cf4c79820fd&city=%E5%B9%BF%E5%B7%9E
    public static final String WEATHER_URL="http://apicloud.mob.com/v1/weather/query?";

    // 文件保存相关
    public static final String PATH = "viomi/";// 所有文件保存主目录
    public static final String FOOD_MANAGE_PATH = "foodmanage/";// 食材管理相关保存目录
    public static final String FOOD_MANAGE_SAVE_FILE = "food_manage.xml";// 保存文件名
    public static final String ALBUM_SAVE_PATH = "album";// 相册相关保存目录
    public static final String MESSAGE_BOARD_PATH = "board/";// 留言版
    static final String CACHE_SAVE_PATH = "cache";// 缓存保存目录
    public static final String CAMERA_CACHE = "camera/";// 拍照缓存目录
    public static final String USER_INFO_FILE = "ViomiUser.dat";// 用户信息

    // 重要配置 Key
    public static final long OAUTH_ANDROID_APP_ID = 2882303761517454408L;// 云米 android
    public static final String OAUTH_ANDROID_APP_KEY = "5891745422408";
    public static final long OAUTH_IOS_APP_ID = 2882303761517484785L;// 云米 ios
    public static final String OAUTH_IOS_APP_KEY = "5701748476785";
    public static final String AIUI_APPID = "58a40571";// 讯飞 AIUI APPID

    // 传值相关
    public static final String PHOTO_LIST = "photo_list";// 图片集合
    public static final String PHOTO_INDEX = "photo_index";// 图片下标
    public static final String WEB_URL = "url";// 网址
    public static final String SPEECH_WAKE_UP = "speech_wake_up";// 语音唤醒
    public static final String CHOSE_ALBUM = "chose_album";// 选择电子相册
    public static final String CHOSE_ALBUM_RESULT = "chose_album_result";// 选择电子相册结果

    public static final int CODE_SELF_CHECKING = 1000;// 进入自检
    public static final int CODE_CAMERA = 1001;// 拍照
    public static final int CODE_CHOSE_ALBUM = 1002;// 选择电子相册
    public static final int CODE_CHOSE_ALBUM_SUCCESS = 1003;// 选择电子相册成功
    public static final int CODE_CAMERA_CROP = 1004;// 拍照裁剪
}