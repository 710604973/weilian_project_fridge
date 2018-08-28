package com.mode.fridge.bean;

/**
 * Created by young2 on 2016/12/30.
 */

public class DataSendInfo extends SerialInfo {
    public static int BYTE_COUNT=12;//字节数
    public static int HEADER=(byte)0xaa;//开始位

    public int header=HEADER;//开始位
    public @Mode int mode;//智能模式0x01，速冷模式0x20，速冻模式0x10，假日模式0x04，加湿模式0x05
    public int indoor_temp;//环境温度,废弃
    public int cold_closet_temp_set;//冷藏室温度设定，设定范围：51~59  代表1-9度,0代表冷藏关闭
    public int temp_changeable_room_temp_set;//变温室温度设定
    public int freezing_room_temp_set;//冷冻室温度设定，20~38 代表-30 - -12度
    public int action;//冷动力测试,强制化霜
    public int diagnose1;//诊断1
    public int diagnose2;//诊断2
    public int reserve1;//预留1
    public int reserve2;//预留2
    public int crc;//crc校验

    public String toString(){
        return "mode="+mode+",indoor_temp="+indoor_temp+",cold_closet_temp_set="+cold_closet_temp_set+",temp_changeable_room_temp_set="+temp_changeable_room_temp_set
                +",freezing_room_temp_set="+freezing_room_temp_set+",action="+action+",diagnose1="+diagnose1
                +",diagnose2="+diagnose2+",reserve1="+reserve1 +",reserve2="+reserve2+",crc="+crc;
    }
}
