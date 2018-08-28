package com.mode.fridge.bean;

/**
 * Created by young2 on 2017/3/14.
 */

public class DeviceParamsGet extends SerialInfo {
    public static int V1_BYTE_COUNT=17;//最少回复字节数，三门17
    public static int V2_BYTE_COUNT=19;//最少回复字节数，四门19
    public static int V3_BYTE_COUNT=48;//最少回复字节数，四门48,美菱主板

    public static int V1_HEADER=(byte)0xaa;//开始位
    public static int V2_HEADER=(byte)0x55;//开始位,两个
    public static int V2_TAIL=(byte)0xaa;//结束位，两个
    public static int V3_HEADER0=(byte)0x55;//开始位0
    public static int V3_HEADER1=(byte)0xaa;//开始位1

    public @SerialInfo.Mode int mode=MODE_SMART;//模式
    public int cold_closet_temp_set=COLD_COLSET_DEFAULT_TEMP;//冷藏室温度设定
    public int temp_changeable_room_temp_set=TEMP_CHANGEABLE_ROOM_DEFAULT_TEMP;//变温室温度设定
    public int freezing_room_temp_set=FREEZING_ROOM_DEFAULT_TEMP;//冷冻室温度设定
    public boolean  cold_closet_room_enable=true;//冷藏室关闭
    public boolean  temp_changeable_room_room_enable=true;//变温室关闭
    public int cold_closet_temp_real=COLD_COLSET_DEFAULT_TEMP;//冷藏室实际温度
    public int temp_changeable_room_temp_real=TEMP_CHANGEABLE_ROOM_DEFAULT_TEMP;//变温室实际温度
    public int freezing_room_temp_real=FREEZING_ROOM_DEFAULT_TEMP;//冷冻室实际温度
    public int cc_evaporator_temp;//蒸发器温度,冷藏化霜温度
    public int fz_evaporator_temp;//蒸发器温度,冷冻化霜温度
    public boolean clean=false;//一键净化
    public boolean RCF_forced=false;//强制不停机
    public boolean cc_forced_frost=false;//冷藏化霜测试结束
    public boolean fz_forced_frost=false;//冷冻化霜测试结束
    public boolean commodity_inspection;//商检状态
    public boolean door_ccroom_open_alarm;//冷藏室门开报警
    public boolean door_tcroom_open_alarm;//变温室门开报警
    public boolean door_fzroom_open_alarm;//冷冻室门开报警
    public int indoor_temp;//环境温度
    public int humidity;//湿度
    public int version;//版本
    public int model;//型号
    public int error;//异常，一个位一个异常
    public int crc;//crc校验
    public boolean quick_cold;//速冷
    public boolean quick_freeze;//速冻
    public boolean iced_drink;//冰饮，v4独有
    public  boolean rolling_over;//翻转梁加热状态，v4独有
    public  boolean rolling_over_close_mode;//翻转梁加热复位模式，v4独有
    public boolean fz_evaporator_heat;//冷冻化霜加热器状态

    public String toString(){
        String result="mode="+mode+",ccroom_temp="+cold_closet_temp_set+",tcroom_temp="+temp_changeable_room_temp_set +",fzroom_temp="+freezing_room_temp_set
                +",ccroom_real="+cold_closet_temp_real+",tcroom_real="+temp_changeable_room_temp_real +",fzroom_real="+freezing_room_temp_real
                +",ccroom_enable="+cold_closet_room_enable+",tcroom_enable=" +temp_changeable_room_room_enable +",clean="+clean+",indoorTemp="+indoor_temp+",iced_drink="+iced_drink;
        return result;
    }

    public String toShortString(){
        String result=""+mode+","+cold_closet_temp_set+","+temp_changeable_room_temp_set +","+freezing_room_temp_set
                +","+cold_closet_temp_real+","+temp_changeable_room_temp_real +","+freezing_room_temp_real
                +","+cold_closet_room_enable+"," +temp_changeable_room_room_enable +","+clean+","+indoor_temp+","+iced_drink;
        return result;
    }
}
