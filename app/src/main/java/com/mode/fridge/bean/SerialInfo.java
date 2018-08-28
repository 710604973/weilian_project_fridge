package com.mode.fridge.bean;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by young2 on 2017/1/4.
 */

public class SerialInfo {

    public static int ROOM_CLOSE_TEMP=-50;//冷藏关闭
    public static int COLD_COLSET_DEFAULT_TEMP=6;//冷藏室默认温度
    public static int TEMP_CHANGEABLE_ROOM_DEFAULT_TEMP=-4;//变温室默认温度
    public static int FREEZING_ROOM_DEFAULT_TEMP=-18;//变温室默认温度

     public static final int MODE_QUICK_COOL=0x20;//速冷
     public static final int MODE_QUICK_FREEZE=0x10;//速冻

    public static final int MODE_NULL=0x00;//取消模式
    public static final int MODE_SMART=0x01;//智能模式
    public static final int MODE_HOLIDAY=0x04;//假日模式
    public static final int MODE_HUMIDIFY=0x05;//加湿
    @IntDef({MODE_NULL,MODE_SMART,MODE_HOLIDAY,MODE_HUMIDIFY})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Mode {}//工作模式

    public static final int ROOM_COLD_COLSET=0;//冷藏室
    public static final int ROOM_CHANGEABLE_ROOM=1;//变温室室
    public static final int ROOM_FREEZING_ROOM=2;//冷冻室
    @IntDef({ROOM_COLD_COLSET,ROOM_CHANGEABLE_ROOM, ROOM_FREEZING_ROOM})
    @Retention(RetentionPolicy.SOURCE)
    public @interface RoomType {}//冰箱的室类型
}
