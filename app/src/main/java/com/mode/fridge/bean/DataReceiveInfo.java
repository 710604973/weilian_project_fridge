package com.mode.fridge.bean;

/**
 * Created by young2 on 2016/12/30.
 */

public class DataReceiveInfo extends SerialInfo {
    public static int BYTE_COUNT=17;//字节数
    public static int HEADER=(byte)0xaa;//开始位

    public int header=HEADER;//开始位
    public @Mode
    int  mode;//模式，智能模式0x01，速冷模式0x20，速冻模式0x10，假日模式0x04，加湿模式0x05
    public int cold_closet_temp_set=COLD_COLSET_DEFAULT_TEMP;//冷藏室温度设定，设定范围：52~58  代表2-8度,0代表冷藏关闭
    public int temp_changeable_room_temp_set=TEMP_CHANGEABLE_ROOM_DEFAULT_TEMP;//变温室温度设定 -18-8℃
    public int freezing_room_temp_set=FREEZING_ROOM_DEFAULT_TEMP;//冷冻室温度设定，-25--15℃
    public int status;//
    public int traffic_status;//通讯状态
    public int cold_closet_temp_real=3;//冷藏室实际温度,查询实际温度时有效，实际值为收到的数据-50，温度传感器故障为0xff
    public int temp_changeable_room_temp_real=3;//变温室实际温度,查询实际温度时有效，实际值为收到的数据-50，温度传感器故障为0xff
    public int freezing_room_temp_real=-20;//冷冻室实际温度,查询实际温度时有效，实际值为收到的数据-50，温度传感器故障为0xff
    public int indoor_temp;//环境温度,查询实际温度时有效，实际值为收到的数据-50，温度传感器故障为0xff
    public int evaporator_temp_real;//蒸发器实际温度，查询实际温度时有效，实际值为收到的数据-50，温度传感器故障为0xff
    public int humidity;//湿度，单位百分比
    public int data_check1;//查询数据1
    public int data_check2;//查询数据2
    public int version;//版本号
    public int model;//型号
    public int crc;//crc校验

    public String toString(){
        return "mode="+mode+",cold_closet_temp_set="+cold_closet_temp_set+",temp_changeable_room_temp_set="+temp_changeable_room_temp_set
                +",freezing_room_temp_set="+freezing_room_temp_set+",status="+status+",traffic_status="+traffic_status
                +",cold_closet_temp_real="+cold_closet_temp_real+",temp_changeable_room_temp_real="+temp_changeable_room_temp_real
                +",freezing_room_temp_real="+freezing_room_temp_real+",indoor_temp="+indoor_temp+",humidity="+humidity
                +",data_check1="+data_check1+",data_check2="+data_check2+",version="+version+",model="+model+",crc="+crc;
    }
}
