package com.mode.fridge.parser;

import android.util.Log;

import com.mode.fridge.bean.DataReceiveInfo;
import com.mode.fridge.bean.DeviceError;
import com.mode.fridge.bean.DeviceParamsGet;
import com.mode.fridge.bean.SerialInfo;
import com.mode.fridge.device.AppConfig;
import com.mode.fridge.device.DeviceConfig;
import com.mode.fridge.utils.DataProcessUtil;

/**
 * Created by young2 on 2017/1/4.
 */

public class SerialReceiveParser {
    private final static String TAG=SerialReceiveParser.class.getSimpleName();

    /***
     * 解析串口接收数据
     * @param data 数据
     * @param info
     * @return
     */
    public static  boolean parser(int[] data,DataReceiveInfo info){
        boolean result=false;
        if(info==null){
            Log.e(TAG,"parser,info null !");
            return false;
        }
        if(data==null||data.length<DataReceiveInfo.BYTE_COUNT){
            Log.e(TAG,"parser,data error !");
            return false;
        }
        int i=0;
        if(data[0]!=(0xff& DataReceiveInfo.HEADER)){
            Log.e(TAG,"parser,header error !data[0]="+data[0]+",HEADER="+(0xff&DataReceiveInfo.HEADER));
            return false;
        }
        int checkSum= DataProcessUtil.getCheckSum(data,DataReceiveInfo.BYTE_COUNT-1);
        if(checkSum!=data[DataReceiveInfo.BYTE_COUNT-1]){
            Log.e(TAG,"parser,crc error !crc="+data[DataReceiveInfo.BYTE_COUNT-1]+",checkSum="+checkSum);
            return false;
        }

        i++;
        info.mode=data[i];
        i++;
        info.cold_closet_temp_set=data[i]-50;
        i++;
        info.temp_changeable_room_temp_set=data[i]-50;
        i++;
        info.freezing_room_temp_set=data[i]-50;
        i++;
        info.status=data[i];
        i++;
        info.traffic_status=data[i];
        i++;
        info.cold_closet_temp_real=data[i]-50;
        i++;
        info.temp_changeable_room_temp_real=data[i]-50;
        i++;
        info.freezing_room_temp_real=data[i]-50;
        i++;
        info.indoor_temp=data[i]-50;
        i++;
        info.evaporator_temp_real=data[i]-50;
        i++;
        info.humidity=0xff&data[i];
        i++;
        info.data_check1=data[i];
        i++;
        info.data_check2=data[i];
        i++;
        info.version=((byte)data[i])&0x0f;
        info.model=(((byte)data[i])&((byte)0xf0))/16;
        i++;
        info.crc=data[i];
       // DeviceConfig.MODEL= AppConfig.VIOMI_FRIDGE_V1;
        return true;
    }


    /***
     * 解析串口接收数据
     * @param data 数据
     * @param info
     * @return
     */
    public static  boolean parser(int[] data,DeviceParamsGet info){
        boolean result=false;
        if(info==null){
            Log.e(TAG,"parser,info null !");
            return false;
        }
        int length=data.length;
        int minByteCount=Math.min(DeviceParamsGet.V1_BYTE_COUNT, DeviceParamsGet.V2_BYTE_COUNT);
        if(data==null||length<minByteCount){
            Log.e(TAG,"parser,data error !");
            return false;
        }
        int i=0;

        if(DeviceConfig.MODEL.equals(AppConfig.VIOMI_FRIDGE_V2)){
            if(data[0]==(0xff& DeviceParamsGet.V2_HEADER)&&data[1]==(0xff& DeviceParamsGet.V2_HEADER)
                    &&data[DeviceParamsGet.V2_BYTE_COUNT-1]==(0xff& DeviceParamsGet.V2_TAIL) &&data[DeviceParamsGet.V2_BYTE_COUNT-2]==(0xff& DeviceParamsGet.V2_TAIL)){
            }else {
                Log.e(TAG,"parser,header error !data[0]="+data[0]);
                return false;
            }
        }else if(DeviceConfig.MODEL.equals(AppConfig.VIOMI_FRIDGE_V3)|| DeviceConfig.MODEL.equals(AppConfig.VIOMI_FRIDGE_V31)){
            if(data[0]==(0xff& DeviceParamsGet.V3_HEADER0)&&data[1]==(0xff& DeviceParamsGet.V3_HEADER1)){
            }else {
                Log.e(TAG,"parser,header error !data[0]="+data[0]);
                return false;
            }
        }else{
            if(data[0]==(0xff& DeviceParamsGet.V1_HEADER)){//判断型号
                //DeviceConfig.MODEL= AppConfig.VIOMI_FRIDGE_V1;
            }else {
                Log.e(TAG, "parser,header error !data[0]=" + data[0]);
                return false;
            }
        }


        DeviceError deviceError=new DeviceError();
        if( DeviceConfig.MODEL.equals(AppConfig.VIOMI_FRIDGE_V1)|| DeviceConfig.MODEL.equals(AppConfig.VIOMI_FRIDGE_V4)){//三门冰箱
            int checkSum= DataProcessUtil.getCheckSum(data, DeviceParamsGet.V1_BYTE_COUNT-1);
            if(checkSum!=data[DeviceParamsGet.V1_BYTE_COUNT-1]){
                Log.e(TAG,"parser,crc error !crc="+data[DeviceParamsGet.V1_BYTE_COUNT-1]+",checkSum="+checkSum);
                return false;
            }

            i++;
            info.mode=data[i];//模式

            if(SerialInfo.MODE_QUICK_COOL== info.mode){
                info.quick_cold=true;
                info.quick_freeze=false;
                info.mode= SerialInfo.MODE_NULL;
            }else if(SerialInfo.MODE_QUICK_FREEZE== info.mode){
                info.quick_cold=false;
                info.quick_freeze=true;
                info.mode= SerialInfo.MODE_NULL;
            }else {
                info.quick_cold=false;
                info.quick_freeze=false;
            }
            i++;
            info.cold_closet_temp_set=data[i]-50;//冷藏室温度，开关
            if(info.cold_closet_temp_set== SerialInfo.ROOM_CLOSE_TEMP){
                info.cold_closet_room_enable=false;
            }else {
                info.cold_closet_room_enable=true;
            }

            i++;
            info.temp_changeable_room_temp_set=data[i]-50;//变温室温度，开关
            if(info.temp_changeable_room_temp_set== SerialInfo.ROOM_CLOSE_TEMP){
                info.temp_changeable_room_room_enable=false;
            }else {
                info.temp_changeable_room_room_enable=true;
            }

            i++;//冷冻室温度
            info.freezing_room_temp_set=data[i]-50;

            i++;
            int status=data[i];//强制不停机，强制化霜，一键净化，风机报警，冷藏室门开报警
            info.clean=isOneKeyCleanWorking(status);
            if(DeviceConfig.MODEL.equals(AppConfig.VIOMI_FRIDGE_V4)){
                info.iced_drink=isIceDrinkWorking(status);
            }
            info.RCF_forced=isRCFForcedStart(status);
            info.fz_forced_frost=isForcedFrost(status);
            deviceError.error_fzroom_fan=isFanAlarm(status);
            info.door_ccroom_open_alarm=isDoorOpenAlarm(status);

            i++;
            int traffic_status=data[i];//通讯状态
            if(traffic_status==0xa0){
                deviceError.error_traffic=false;
            }else {
                deviceError.error_traffic=false;
            }

            i++;
            if(data[i]==0xff){//冷藏室实际温度
                deviceError.error_ccroom_sencor=true;
            }else {
                deviceError.error_ccroom_sencor=false;
                info.cold_closet_temp_real=data[i]-50;
            }
            i++;
            if(data[i]==0xff){//变温室实际温度
                deviceError.error_tcroom_sencor=true;
            }else {
                deviceError.error_tcroom_sencor=false;
                info.temp_changeable_room_temp_real=data[i]-50;
            }
            i++;
            if(data[i]==0xff){//冷冻室实际温度
                deviceError.error_fzroom_sencor=true;
            }else {
                deviceError.error_fzroom_sencor=false;
                info.freezing_room_temp_real=data[i]-50;
            }
            i++;
            if(data[i]==0xff){//室内温度
                deviceError.error_indoor_sencor=true;
                info.indoor_temp=data[i];
            }else {
                deviceError.error_indoor_sencor=false;
                info.indoor_temp=data[i]-50;
            }
            i++;
            if(data[i]==0xff){//冷冻化霜温度
                deviceError.error_fz_defrost_sencor=true;
            }else {
                deviceError.error_fz_defrost_sencor=false;
                info.fz_evaporator_temp=data[i]-50;
            }
            i++;
            info.humidity=0xff&data[i];//湿度
            i++;
            int check1=data[i];//查询数据1，冷冻化霜加热状态
            info.fz_evaporator_heat=isFzEvaporatorHeat(check1);
            i++;
            int check2=data[i];//翻转梁加热丝，翻转梁加热器关闭模式
            if(DeviceConfig.MODEL.equals(AppConfig.VIOMI_FRIDGE_V4)){
                info.rolling_over=isRollingOver(check2);
                info.rolling_over_close_mode=isRollingOverCloseMode(check2);
            }
            i++;
            info.version=((byte)data[i])&0x0f;//型号和版本
            info.model=(((byte)data[i])&((byte)0xf0))/16;
            i++;
            info.crc=data[i];//校验
            info.error=deviceError.getErrorCode();//异常
            return true;
        }else if( DeviceConfig.MODEL.equals(AppConfig.VIOMI_FRIDGE_V2)){
            int checkSum= DataProcessUtil.getCheckSum(data,2, DeviceParamsGet.V2_BYTE_COUNT-4-1);
            if(checkSum!=data[DeviceParamsGet.V2_BYTE_COUNT-2-1]){
                Log.e(TAG,"parser,crc error !crc="+data[DeviceParamsGet.V2_BYTE_COUNT-2-1]+",checkSum="+checkSum);
                return false;
            }
            i++;
            i++;
            i++;
            info.cold_closet_temp_real=(data[i]-80)/2;//冷藏室实际温度
            i++;
            info.cc_evaporator_temp=(data[i]-80)/2;//冷藏化霜温度
            i++;
            info.freezing_room_temp_real=(data[i]-80)/2;//冷冻室温度
            i++;
            info.temp_changeable_room_temp_real=(data[i]-80)/2;//变温室温度
            i++;
            info.indoor_temp=(data[i]-80)/2;//室内温度
            i++;
            info.fz_evaporator_temp=(data[i]-80)/2;//冷冻化霜温度
            i++;
            i++;
            i++;
            if((data[i]&0x02)==0x02) {//设备状态，商检
                info.commodity_inspection=true;
            }else {
                info.commodity_inspection=false;
            }
            if((data[i]&0x20)==0x20) {//冷藏室门开报警
                info.door_ccroom_open_alarm=true;
            }else {
                info.door_ccroom_open_alarm=false;
            }
            if((data[i]&0x40)==0x40) {//变温室门开报警
                info.door_tcroom_open_alarm=true;
            }else {
                info.door_tcroom_open_alarm=false;
            }
            if((data[i]&0x80)==0x80) {//冷冻室门开报警
                info.door_fzroom_open_alarm=true;
            }else {
                info.door_fzroom_open_alarm=false;
            }
            i++;
            if((data[i]&0x10)==0x10) {//工作状态 化霜
                info.cc_forced_frost=true;
            }else {
                info.cc_forced_frost=false;
            }
            if((data[i]&0x20)==0x20) {
                info.fz_forced_frost=true;
            }else {
                info.fz_forced_frost=false;
            }

            i++;//故障信息
            if((data[i]&0x01)==0x01) {
               deviceError.error_tcroom_sencor=true;
            }else {
                deviceError.error_tcroom_sencor=false;
            }
            if((data[i]&0x02)==0x02) {
                deviceError.error_ccroom_sencor=true;
            }else {
                deviceError.error_ccroom_sencor=false;
            }
            if((data[i]&0x04)==0x04) {
                deviceError.error_cc_defrost_sencor=true;
            }else {
                deviceError.error_cc_defrost_sencor=false;
            }
            if((data[i]&0x08)==0x08) {
                deviceError.error_fz_defrost_sencor=true;
            }else {
                deviceError.error_fz_defrost_sencor=false;
            }
            if((data[i]&0x10)==0x10) {
                deviceError.error_indoor_sencor=true;
            }else {
                deviceError.error_indoor_sencor=false;
            }
            if((data[i]&0x40)==0x40) {
                deviceError.error_fzroom_sencor=true;
            }else {
                deviceError.error_fzroom_sencor=false;
            }
            i++;//故障信息2
            if((data[i]&0x02)==0x02) {
                deviceError.error_air_door=true;
            }else {
                deviceError.error_air_door=false;
            }
            if((data[i]&0x04)==0x04) {
                deviceError.error_tcroom_fan=true;
            }else {
                deviceError.error_tcroom_fan=false;
            }
            if((data[i]&0x10)==0x10) {
                deviceError.error_fzroom_fan=true;
            }else {
                deviceError.error_fzroom_fan=false;
            }
            if((data[i]&0x40)==0x40) {
                deviceError.error_ccroom_fan=true;
            }else {
                deviceError.error_ccroom_fan=false;
            }
            info.error=deviceError.getErrorCode();//异常
            i++;
            i++;
            info.crc=data[i];//校验
            return true;
        }else if( DeviceConfig.MODEL.equals(AppConfig.VIOMI_FRIDGE_V3)|| DeviceConfig.MODEL.equals(AppConfig.VIOMI_FRIDGE_V31)){
            int checkSum= DataProcessUtil.getCheckSum(data,0, DeviceParamsGet.V3_BYTE_COUNT-1);
            if(checkSum!=data[DeviceParamsGet.V3_BYTE_COUNT-1]){
                Log.e(TAG,"parser,crc error !crc="+data[DeviceParamsGet.V3_BYTE_COUNT-1]+",checkSum="+checkSum);
                return false;
            }
            //通信起始命令
            i++;//通信起始命令 ,data1
            i++;// data2
            if((data[i]&0x01)==0x01) {//箱运行状态1（运行模式类）
                info.mode= DeviceParamsGet.MODE_SMART;
            }else if((data[i]&0x04)==0x04) {
                info.mode= DeviceParamsGet.MODE_HOLIDAY;
            }else {
                info.mode= SerialInfo.MODE_NULL;
            }

            if((data[i]&0x10)==0x10) {
                info.quick_freeze=true;
            }else{
                info.quick_freeze=false;
            }

            if((data[i]&0x20)==0x20) {
                info.quick_cold=true;
            }else{
                info.quick_cold=false;
            }

            if((data[i]&0x02)==0x02) {
                info.cold_closet_room_enable=false;
            }else {
                info.cold_closet_room_enable=true;
            }
            i++;//冰箱运行状态2,data3
            if((data[i]&0x10)==0x10) {//翻转梁加热丝关闭模式
                info.rolling_over_close_mode=true;
            }else {
                info.rolling_over_close_mode=false;
            }
            i++;//冰箱运行状态3,data4
            if((data[i]&0x01)==0x01) {//冷藏门开关状态位
                info.door_ccroom_open_alarm=true;
            }else {
                info.door_ccroom_open_alarm=false;
            }
            if(((data[i]&0x02)==0)&&((data[i]&0x04)==0)) {//冷冻门开关
                info.door_fzroom_open_alarm=false;
            }else {
                info.door_fzroom_open_alarm=true;
            }
            i++;//冰箱冷藏室设定温度值（设定温度值整数位+100）,data5
            info.cold_closet_temp_set=data[i]-100;
            i++;//冰箱变温室设定温度值（设定温度值整数位+100）,,data6
            info.temp_changeable_room_temp_set=0;
            i++;//冰箱冷冻室设定温度值（设定温度值整数位+100）,data7
            info.freezing_room_temp_set=data[i]-100;
            i++;//冰箱冷藏室设定温度值小数位（设定温度值小数位*10+10）,data8
            i++;//冰箱变温室设定温度值小数位（设定温度值小数位*10+10）,data9
            i++;//冰箱冷冻室设定温度值小数位（设定温度值小数位*10+10）,data10

            i++;//冰箱冷藏室传感器温度值（温度值+100，此值若=255表示该传感器故障）,data11
            info.cold_closet_temp_real=data[i]-100;
            if(data[i]==0xff){
                deviceError.error_ccroom_sencor=true;
            }else {
                deviceError.error_ccroom_sencor=false;
            }
            i++;//冰箱变温室传感器温度值（温度值+100，此值若=255表示该传感器故障）,data12
            info.temp_changeable_room_temp_real=data[i]-100;
//            if(data[i]==0xff){
//                deviceError.error_ccroom_sencor=true;
//            }
            i++;//冰箱冷冻室传感器温度值（温度值+100，此值若=255表示该传感器故障）,data13
            info.freezing_room_temp_real=data[i]-100;
            if(data[i]==0xff){
                deviceError.error_fzroom_sencor=true;
            }else {
                deviceError.error_fzroom_sencor=false;
            }

            i++;//冰箱环境温度传感器温度值（传送实际温度值（0~50），若环温低于0度，此值为0。此值若=70表示该传感器故障）,data14
            info.indoor_temp=data[i];
            if(data[i]==70){
                deviceError.error_indoor_sencor=true;
            }else {
                deviceError.error_indoor_sencor=false;
            }
            i++;//冰箱冷藏室传感器温度值小数位（温度值小数位*10+10）,data15
            i++;//冰箱变温室传感器温度值小数位（温度值小数位*10+10）,data16
            i++;//冰箱冷冻室传感器温度值小数位（温度值小数位*10+10）,data17
            i++;//冰箱环境温度传感器温度值小数位（温度值小数位*10+10）,data18
            i++;//冰箱运行状态4,data19
            if((data[i]&0x80)==0x80) {//翻转梁加热丝
                info.rolling_over=true;
            }else {
                info.rolling_over=false;
            }
            i++;//冰箱运行状态5,data20
            i++;//,data21
            i++;//,data22
            i++;//变频档位（范围：0～99）,data23
            i++;//冰箱冷冻室蒸发传感器温度值（温度值+100，此值若=255表示该传感器故障）,data24
            info.fz_evaporator_temp=data[i]-100;
            if(data[i]==0xff){
                deviceError.error_fz_defrost_sencor=true;
            }else {
                deviceError.error_fz_defrost_sencor=false;
            }
            i++;//冰箱运行状态7（冰箱故障判断类1）,data25
//            if((data[i]&0x01)==0x01) {
//                deviceError.error_traffic=true;
//            }else {
//                deviceError.error_traffic=false;
//            }
            if((data[i]&0x02)==0x02) {
                deviceError.error_fzroom_fan=true;
            }else {
                deviceError.error_fzroom_fan=false;
            }
            i++;//箱运行状态8（冰箱故障判断类）,data26
            i+=12;//保留,data27
            i++;//冰箱型号预定义，值为零（DATA39 = 0）,data28
            i++;//冰箱型号（冰箱产品代号，高位放在DATA40字节,低位放在DATA41字节）,data29
            i++;//,data30
            i+=5;//保留,data31
            i++;//,data32
            info.error=deviceError.getErrorCode();//异常
//                PushMsg pushMsg = new PushMsg();
//                pushMsg.title = DataProcessUtil.bytesToHexString(data);
//                pushMsg.content =info.toShortString();
//                pushMsg.type=PushMsg.PUSH_MESSAGE_TYPE_ADVERT;
//                PushManager.getInstance().pushNotificationToStatusBar(pushMsg);
            return true;
        }else {
            Log.e(TAG, "model error=" + DeviceConfig.MODEL);
            return false;
        }
    }

    public static boolean test(){
        int i=0;
        DeviceError deviceError=new DeviceError();
        int[] data=new int[]{0x55 ,0xAA ,0x00 ,0x00 ,0x02 ,0x67 ,0x00 ,0x52 ,0x0A ,0x00 ,0x0A ,0x79 ,0x00 ,0x79 ,0x14 ,0x0F
                ,0x00 ,0x11 ,0x0A ,0x01 ,0x00 ,0x00 ,0x00 ,0x5C ,0x79 ,0x00 ,0x00 ,0x00 ,0x00 ,0x00 ,0x00 ,0x00 ,0x00 ,0x00
                ,0x00 ,0x00 ,0x00 ,0x00 ,0x00 ,0x00 ,0x11 ,0x0C ,0x00 ,0x00 ,0x00 ,0x00 ,0x00 ,0xF1};

        int length=data.length;
        int minByteCount=Math.min(DeviceParamsGet.V1_BYTE_COUNT, DeviceParamsGet.V2_BYTE_COUNT);
        if(data==null||length<minByteCount){
            Log.e(TAG,"parser,data error !");
            return false;
        }
        if(data[0]==(0xff& DeviceParamsGet.V3_HEADER0)&&data[1]==(0xff& DeviceParamsGet.V3_HEADER1)){
        }else {
            Log.e(TAG,"parser,header error !data[0]="+data[0]);
            return false;
        }

        DeviceParamsGet info=new DeviceParamsGet();
        int checkSum= DataProcessUtil.getCheckSum(data,0, DeviceParamsGet.V3_BYTE_COUNT-1);
        if(checkSum!=data[DeviceParamsGet.V3_BYTE_COUNT-1]){
            Log.e(TAG,"parser,crc error !crc="+data[DeviceParamsGet.V3_BYTE_COUNT-1]+",checkSum="+checkSum);
            return false;
        }
        //通信起始命令
        i++;//通信起始命令
        i++;
        if((data[i]&0x01)==0x01) {//箱运行状态1（运行模式类）
            info.mode= DeviceParamsGet.MODE_SMART;
        }else if((data[i]&0x04)==0x04) {
            info.mode= DeviceParamsGet.MODE_HOLIDAY;
        }else {
            info.mode= SerialInfo.MODE_NULL;
        }

        if((data[i]&0x10)==0x10) {
            info.quick_freeze=true;
        }else{
            info.quick_freeze=false;
        }

        if((data[i]&0x20)==0x20) {
            info.quick_cold=true;
        }else{
            info.quick_cold=false;
        }

        if((data[i]&0x02)==0x02) {
            info.cold_closet_room_enable=false;
        }else {
            info.cold_closet_room_enable=true;
        }
        i++;//冰箱运行状态2
        i++;//冰箱运行状态3
        if((data[i]&0x01)==0x01) {//冷藏门开关状态位
            info.door_ccroom_open_alarm=true;
        }else {
            info.door_ccroom_open_alarm=false;
        }
        if(((data[i]&0x02)==0)&&((data[i]&0x04)==0)) {//冷冻门开关
            info.door_fzroom_open_alarm=false;
        }else {
            info.door_fzroom_open_alarm=true;
        }
        i++;//冰箱冷藏室设定温度值（设定温度值整数位+100）
        info.cold_closet_temp_set=data[i]-100;
        i++;//冰箱变温室设定温度值（设定温度值整数位+100）
        info.temp_changeable_room_temp_set=0;
        i++;//冰箱冷冻室设定温度值（设定温度值整数位+100）
        info.freezing_room_temp_set=data[i]-100;
        i++;//冰箱冷藏室设定温度值小数位（设定温度值小数位*10+10）
        i++;//冰箱变温室设定温度值小数位（设定温度值小数位*10+10）
        i++;//冰箱冷冻室设定温度值小数位（设定温度值小数位*10+10）

        i++;//冰箱冷藏室传感器温度值（温度值+100，此值若=255表示该传感器故障）
        info.cold_closet_temp_real=data[i]-100;
        if(data[i]==0xff){
            deviceError.error_ccroom_sencor=true;
        }else {
            deviceError.error_ccroom_sencor=false;
        }
        i++;//冰箱变温室传感器温度值（温度值+100，此值若=255表示该传感器故障）
        info.temp_changeable_room_temp_real=data[i]-100;
//            if(data[i]==0xff){
//                deviceError.error_ccroom_sencor=true;
//            }
        i++;//冰箱冷冻室传感器温度值（温度值+100，此值若=255表示该传感器故障）
        info.freezing_room_temp_real=data[i]-100;
        if(data[i]==0xff){
            deviceError.error_fzroom_sencor=true;
        }else {
            deviceError.error_fzroom_sencor=false;
        }

        i++;//冰箱环境温度传感器温度值（传送实际温度值（0~50），若环温低于0度，此值为0。此值若=70表示该传感器故障）
        info.indoor_temp=data[i]-100;
        if(data[i]==70){
            deviceError.error_fz_defrost_sencor=true;
        }else {
            deviceError.error_fz_defrost_sencor=false;
        }
        i++;//冰箱冷藏室传感器温度值小数位（温度值小数位*10+10）
        i++;//冰箱变温室传感器温度值小数位（温度值小数位*10+10）
        i++;//冰箱冷冻室传感器温度值小数位（温度值小数位*10+10）
        i++;//冰箱环境温度传感器温度值小数位（温度值小数位*10+10）
        i++;//冰箱运行状态4
        i++;//冰箱运行状态5
        i++;
        i++;
        i++;//变频档位（范围：0～99）
        i++;//冰箱冷冻室蒸发传感器温度值（温度值+100，此值若=255表示该传感器故障）
        info.fz_evaporator_temp=data[i]-100;
        if(data[i]==0xff){
            deviceError.error_indoor_sencor=true;
        }else {
            deviceError.error_indoor_sencor=false;
        }
        i++;//冰箱运行状态7（冰箱故障判断类1）
//            if((data[i]&0x01)==0x01) {
//                deviceError.error_traffic=true;
//            }else {
//                deviceError.error_traffic=false;
//            }
        if((data[i]&0x02)==0x02) {
            deviceError.error_fzroom_fan=true;
        }else {
            deviceError.error_fzroom_fan=false;
        }
        i++;//箱运行状态8（冰箱故障判断类）
        i+=12;//保留
        i++;//冰箱型号预定义，值为零（DATA39 = 0）
        i++;//冰箱型号（冰箱产品代号，高位放在DATA40字节,低位放在DATA41字节）
        i++;
        i+=5;//保留
        i++;
        info.error=deviceError.getErrorCode();//异常
        return true;
    }

    /***
     * 是否一键净化中,
     * @param data status
     * @return
     */
    public static boolean isOneKeyCleanWorking(int data){
        if((data&0x40)==0x40) {
            return true;
        }else {
            return false;
        }
    }

    /***
     * 是否冷饮功能中
     * @param data status
     * @return
     */
    public static boolean isIceDrinkWorking(int data){
        if((data&0x10)==0x10) {
            return true;
        }else {
            return false;
        }
    }

    /***
     * 冷冻化霜加热状态
     * @param data check2
     * @return
     */
    public static boolean isFzEvaporatorHeat(int data){
        if((data&0x20)==0x20) {
            return true;
        }else {
            return false;
        }
    }

    /***
     * 翻转梁加热丝状态
     * @param data check2
     * @return
     */
    public static boolean isRollingOver(int data){
        if((data&0x10)==0x10) {
            return true;
        }else {
            return false;
        }
    }

    /***
     * 翻转梁加热器复位模式
     * @param data check2
     * @return
     */
    public static boolean isRollingOverCloseMode(int data){
        if((data&0x08)==0x08) {
            return true;
        }else {
            return false;
        }
    }

    /***
     * 是否强制化霜结束
     * @param data status
     * @return
     */
    public static boolean isForcedFrost(int data){
        if((data&0x04)==0x04) {
            return true;
        }else {
            return false;
        }
    }

    /***
     * 是否强制不停机
     * @param data status
     * @return
     */
    public static boolean isRCFForcedStart(int data){
        if((data&0x02)==0x02) {
            return true;
        }else {
            return false;
        }
    }

    /***
     * 是否风机报警
     * @param data status
     * @return
     */
    public static boolean isFanAlarm(int data){
        if((data&0x20)==0x20) {
            return true;
        }else {
            return false;
        }
    }

    /***
     * 是否冷藏室门开报警
     * @param data status
     * @return
     */
    public static boolean isDoorOpenAlarm(int data){
        if((data&0x01)==0x01) {
            return true;
        }else {
            return false;
        }
    }


}
