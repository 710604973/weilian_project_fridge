package com.mode.fridge.bean;

import com.mode.fridge.device.AppConfig;
import com.mode.fridge.device.DeviceConfig;

import java.io.Serializable;

/**
 * Created by young2 on 2017/3/14.
 */

public class DeviceParamsSet extends SerialInfo implements Serializable{
    private static final long serialVersionUID =-8672124698365532690L;

    public static int V1_BYTE_COUNT=12;//字节数,三门创维,455
    public static int V2_BYTE_COUNT=19;//字节数，四门双鹿
    public static int V3_BYTE_COUNT=24;//字节数，四门美菱

    public static int V1_HEADER=(byte)0xaa;//开始位
    public static int V2_HEADER=(byte)0x55;//开始位,两个
    public static int V2_TAIL=(byte)0x55;//结束位，两个
    public static int V3_HEADER0=(byte)0x55;//开始位0
    public static int V3_HEADER1=(byte)0xaa;//开始位1

    public @SerialInfo.Mode int mode=MODE_SMART;//模式
    public int cold_closet_temp_set=COLD_COLSET_DEFAULT_TEMP;//冷藏室温度设定
    public int temp_changeable_room_temp_set=TEMP_CHANGEABLE_ROOM_DEFAULT_TEMP;//变温室温度设定
    public int freezing_room_temp_set=FREEZING_ROOM_DEFAULT_TEMP;//冷冻室温度设定
    public boolean  cold_closet_room_enable=true;//冷藏室使能
    public boolean  temp_changeable_room_room_enable=true;//变温室使能
    public boolean clean=false;//一键净化
    public boolean RCF_forced=false;//强制不停机
    public boolean cc_forced_frost=false;//冷藏化霜
    public boolean fz_forced_frost=false;//冷冻化霜
    public boolean commodity_inspection;//商检状态
    public boolean time_cut;//缩时
    public int crc;//crc校验
    public boolean quick_cold;//速冷
    public boolean quick_freeze;//速冻
    public boolean iced_drink;//冰饮  v4独有
    public boolean rolling_over_close_mode;//翻转梁加热复位模式，v3,v4独有

    public String toString(){
        String result="mode="+mode+",ccroom_temp="+cold_closet_temp_set+",tcroom_temp="+temp_changeable_room_temp_set
                +",fzroom_temp="+freezing_room_temp_set+",ccroom_enable="+cold_closet_room_enable+",tcroom_enable="+temp_changeable_room_room_enable
                +",clean="+clean;
        if(DeviceConfig.MODEL.equals(AppConfig.VIOMI_FRIDGE_V4)||DeviceConfig.MODEL.equals(AppConfig.VIOMI_FRIDGE_V3)
                || DeviceConfig.MODEL.equals(AppConfig.VIOMI_FRIDGE_V31)){
            result="mode="+mode+",ccroom_temp="+cold_closet_temp_set
                    +",fzroom_temp="+freezing_room_temp_set+",ccroom_enable="+cold_closet_room_enable
                    +",clean="+clean+",iced_drink="+iced_drink;
        }
        return result;
    }

    public String toShortString(){
        String result=""+mode+","+cold_closet_temp_set+","+temp_changeable_room_temp_set
                +","+freezing_room_temp_set+","+cold_closet_room_enable+","+temp_changeable_room_room_enable
                +","+clean;
        if(DeviceConfig.MODEL.equals(AppConfig.VIOMI_FRIDGE_V4)||DeviceConfig.MODEL.equals(AppConfig.VIOMI_FRIDGE_V3)
                ||DeviceConfig.MODEL.equals(AppConfig.VIOMI_FRIDGE_V31)){
            result=""+mode+","+cold_closet_temp_set
                    +","+freezing_room_temp_set+","+cold_closet_room_enable
                    +","+clean+","+iced_drink;
        }
        return result;
    }

}
