package com.mode.fridge.parser;

import android.support.annotation.IntDef;
import android.util.Log;

import com.mode.fridge.bean.DataSendInfo;
import com.mode.fridge.bean.DeviceParamsGet;
import com.mode.fridge.bean.DeviceParamsSet;
import com.mode.fridge.bean.SerialInfo;
import com.mode.fridge.device.AppConfig;
import com.mode.fridge.device.DeviceConfig;
import com.mode.fridge.utils.DataProcessUtil;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by young2 on 2017/1/4.
 */

public class SerialSendParser {
    private final static String TAG=SerialSendParser.class.getSimpleName();

    public static final int V3_OPERATE_TYPE_TEMP=0x01;//修改：各间室设定温度值
    public static final int V3_OPERATE_TYPE_MODE=0x02;//修改：冰箱运行模式,间室开关
    public static final int V3_OPERATE_TYPE_CHECK_STATUS=0x04;//查询冰箱状态
    public static final int V3_OPERATE_TYPE_SELF_CHECK=0x06;//自检
    public static final int V3_OPERATE_TYPE_MAINTAIN=0x07;//维修
    @IntDef({V3_OPERATE_TYPE_TEMP,V3_OPERATE_TYPE_MODE, V3_OPERATE_TYPE_CHECK_STATUS
    ,V3_OPERATE_TYPE_SELF_CHECK,V3_OPERATE_TYPE_MAINTAIN})
    @Retention(RetentionPolicy.SOURCE)
    public @interface V3OperateType {}//冰箱的室类型
    public static @V3OperateType int V3_Operate_Type=V3_OPERATE_TYPE_TEMP;

    public static boolean parser(DataSendInfo info, int[] data){
        boolean result=false;
        if(info==null){
            Log.e(TAG,"parser,info null !");
            return false;
        }
        if(data==null){
            Log.e(TAG,"parser,data error !");
            return false;
        }
        int i=0;
        data[i]= DataSendInfo.HEADER;
        i++;
        data[i]=info.mode;
        i++;
        data[i]=info.indoor_temp;
        i++;
        data[i]=info.cold_closet_temp_set+50;
        i++;
        data[i]=info.temp_changeable_room_temp_set+50;
        i++;
        data[i]=info.freezing_room_temp_set+50;
        i++;
        data[i]=info.action;
        i++;
        data[i]=info.diagnose1;
        i++;
        data[i]=info.diagnose2;
        i++;
        data[i]=info.reserve1;
        i++;
        data[i]=info.reserve2;
        i++;
        data[i]= DataProcessUtil.getCheckSum(data,i);
        return true;
    }

    public static boolean parser(DeviceParamsSet info, int[] data){
        boolean result=false;
        if(info==null){
            Log.e(TAG,"parser,info null !");
            return false;
        }
        if(data==null){
            Log.e(TAG,"parser,data error !");
            return false;
        }

        if(DeviceConfig.MODEL.equals(AppConfig.VIOMI_FRIDGE_V1)|| DeviceConfig.MODEL.equals(AppConfig.VIOMI_FRIDGE_V4)){
            int i=0;
            data[i]= DataSendInfo.HEADER;
            i++;
            data[i]=info.mode;
            if(info.quick_cold){
                data[i]= SerialInfo.MODE_QUICK_COOL;
            }
            if(info.quick_freeze){
                data[i]= SerialInfo.MODE_QUICK_FREEZE;
            }

            i++;
            data[i]=0;
            i++;
            if(info.cold_closet_room_enable){
                data[i]=info.cold_closet_temp_set+50;
            }else {
                data[i]=0;
            }
            i++;
            if(info.temp_changeable_room_room_enable){
                data[i]=info.temp_changeable_room_temp_set+50;
            }else {
                data[i]=0;
            }
            i++;
            data[i]=info.freezing_room_temp_set+50;
            i++;
            int action=0;
            action=RCFForcedStartCode(info.RCF_forced,action);
            action=forcedFrostCode(info.fz_forced_frost,action);
            if(DeviceConfig.MODEL.equals(AppConfig.VIOMI_FRIDGE_V4)) {
                action = rollingOverCloseModeCode(info.rolling_over_close_mode, action);
            }
            data[i]=action;
            i++;
            int diagnose1=0;
            data[i]=diagnose1;
            i++;

            int diagnose2=0;
            diagnose2=oneKeyCleanCode(info.clean,diagnose2);
            diagnose2=commodityInspectionCode(info.commodity_inspection,diagnose2);
            diagnose2=timeCut(info.time_cut,diagnose2);
            data[i]=diagnose2;
            i++;
            int reverse1=0;
            if(DeviceConfig.MODEL.equals(AppConfig.VIOMI_FRIDGE_V4)){
                reverse1=oneIcedDrinkCode(info.iced_drink,reverse1);
            }
            data[i]=reverse1|0x01;
            i++;
            data[i]=0;
            i++;
            data[i]= DataProcessUtil.getCheckSum(data,i);

        }else  if(DeviceConfig.MODEL.equals(AppConfig.VIOMI_FRIDGE_V2)){
            int i=0;
            data[i]= DeviceParamsSet.V2_HEADER;
            i++;
            data[i]= DeviceParamsSet.V2_HEADER;
            i++;
            data[i]=(byte)0xAA;
            i++;
            data[i]=0;
            i++;
            data[i]=0;
            i++;
            data[i]=0;
            i++;
            data[i]=0;
            i++;;//冷藏室设置温度
            data[i]=info.cold_closet_temp_set*2+80;
            i++;//冷冻室设置温度
            data[i]=info.freezing_room_temp_set*2+80;
            i++;//变温室设置温度
            data[i]=info.temp_changeable_room_temp_set*2+80;
            i++;
            data[i]=0;//预留
            i++;
            data[i]=0;//环境温度
            i++;
            int model=0;//模式选择

            if(info.cold_closet_room_enable){//冷藏室开关
                 model =model&0xFE;
            }else {
                model =model|0x01;
            }

            if(info.temp_changeable_room_room_enable){//变温室开关
                model =model&0xF7;
            }else {
                model =model|0x08;
            }

            if(info.mode== SerialInfo.MODE_SMART){
                model =model|0x04;
            }else {
                model =model&0xFB;
            }
            if(info.quick_cold){
                model =model|0x10;
            }else {
                model =model&0xEF;
            }
            if(info.quick_freeze){
                model =model|0x02;
            }else {
                model =model&0xFD;
            }
            data[i]=model;
            i++;
            int action=0;//化霜
            if(info.fz_forced_frost){
                action =action|0x01;
            }else {
                action =action&0xFE;
            }
            data[i]=action;
            i++;
            data[i]=0;
            i++;
            data[i]=0;
            i++;
            data[i]= DataProcessUtil.getCheckSum(data,2,i-2);
            i++;
            data[i]= DeviceParamsGet.V2_TAIL;
            i++;
            data[i]= DeviceParamsGet.V2_TAIL;
        }else  if(DeviceConfig.MODEL.equals(AppConfig.VIOMI_FRIDGE_V3)|| DeviceConfig.MODEL.equals(AppConfig.VIOMI_FRIDGE_V31)){
            int i=0;
            data[i]= DeviceParamsSet.V3_HEADER0;
            i++;
            data[i]= DeviceParamsSet.V3_HEADER1;
            i++;
            data[i]=V3_OPERATE_TYPE_TEMP;//设置类型
            i++;
            data[i]=info.cold_closet_temp_set+100;//冷藏室设置温度
            i++;
            data[i]=0;//变温室设置温度
            i++;
            data[i]=info.freezing_room_temp_set+100;//冷冻室设置温度
            i++;
            data[i]=10;
            i++;
            data[i]=10;
            i++;
            data[i]=10;
            i++;
            int model=0;
            if(info.mode== SerialInfo.MODE_SMART){
                model =model|0x01;
            }else {
                model =model&0xF;
            }
            if(info.mode== SerialInfo.MODE_HOLIDAY){
                model =model|0x04;
            }else {
                model =model&0xFB;
            }
            if(!info.cold_closet_room_enable){
                model =model|0x02;
            }else {
                model =model&0xFD;
            }
            if(info.quick_freeze){
                model =model|0x10;
            }else {
                model =model&0xEF;
            }
            if(info.quick_cold){
                model =model|0x20;
            }else {
                model =model&0xDF;
            }
            data[i]=model;//模式选择
            i++;
            int fuction=0;
            if(info.rolling_over_close_mode){
                fuction =fuction|0x02;
            }else {
                fuction =fuction&0xFD;
            }
            data[i]=fuction;///功能字节
            i++;
            data[i]=0;//维修运行模式（操作指令等于“0x7”时有效）
            i++;
            data[i]=0;//维修运行模式（操作指令等于“0x7”时有效）
            i++;
            data[i]=0;//保留
            i++;
            data[i]=0;//保留
            i++;
            data[i]=0;//维修运行模式：变频档位（范围：0～99）；
            i++;
            data[i]=0;//通信故障 不处理
            i++;
            data[i]=0;//保留
            i++;
            data[i]=0;//保留
            i++;
            data[i]=0;//保留
            i++;
            data[i]=0;//保留
            i++;
            data[i]=0;//保留
            i++;
            data[i]=0;//保留
            i++;
            data[i]= DataProcessUtil.getCheckSum(data,i);
        }else {
            Log.e(TAG,"model,send data,info null !");
            return false;
        }
        return true;
    }

    /***
     *查询数据编码，v3专用
     * @param info
     * @param data
     * @return
     */
    public static boolean parserCheckData(DeviceParamsSet info, int[] data){
        boolean result=false;
        if(info==null){
            Log.e(TAG,"parser,info null !");
            return false;
        }
        if(data==null){
            Log.e(TAG,"parser,data error !");
            return false;
        }

         if(DeviceConfig.MODEL.equals(AppConfig.VIOMI_FRIDGE_V3)|| DeviceConfig.MODEL.equals(AppConfig.VIOMI_FRIDGE_V31)){
            int i=0;
            data[i]= DeviceParamsSet.V3_HEADER0;
            i++;
            data[i]= DeviceParamsSet.V3_HEADER1;
            i++;
            data[i]=V3_OPERATE_TYPE_CHECK_STATUS;//设置类型
            i++;
            data[i]=info.cold_closet_temp_set+100;//冷藏室设置温度
            i++;
            data[i]=0;//变温室设置温度
            i++;
            data[i]=info.freezing_room_temp_set+100;//冷冻室设置温度
            i++;
            data[i]=10;
            i++;
            data[i]=10;
            i++;
            data[i]=10;
            i++;
            int model=0;
            if(info.mode== SerialInfo.MODE_SMART){
                model =model|0x01;
            }else {
                model =model&0xF;
            }
            if(info.mode== SerialInfo.MODE_HOLIDAY){
                model =model|0x04;
            }else {
                model =model&0xFB;
            }
            if(!info.cold_closet_room_enable){
                model =model|0x02;
            }else {
                model =model&0xFD;
            }
            if(info.quick_freeze){
                model =model|0x10;
            }else {
                model =model&0xEF;
            }
            if(info.quick_cold){
                model =model|0x20;
            }else {
                model =model&0xDF;
            }
            data[i]=model;//模式选择
            i++;
            int fuction=0;
            if(info.rolling_over_close_mode){
                fuction =fuction|0x02;
            }else {
                fuction =fuction&0xFD;
            }
            data[i]=fuction;///功能字节
            i++;
            data[i]=0;//维修运行模式（操作指令等于“0x7”时有效）
            i++;
            data[i]=0;//维修运行模式（操作指令等于“0x7”时有效）
            i++;
            data[i]=0;//保留
            i++;
            data[i]=0;//保留
            i++;
            data[i]=0;//维修运行模式：变频档位（范围：0～99）；
            i++;
            data[i]=0;//通信故障 不处理
            i++;
            data[i]=0;//保留
            i++;
            data[i]=0;//保留
            i++;
            data[i]=0;//保留
            i++;
            data[i]=0;//保留
            i++;
            data[i]=0;//保留
            i++;
            data[i]=0;//保留
            i++;
            data[i]= DataProcessUtil.getCheckSum(data,i);
        }else {
            Log.e(TAG,"model,send data,info null !");
            return false;
        }
        return true;
    }

    /***
     * 启动一键净化
     * @param data diagnose2字节数据
     * @return
     */
    public static int oneKeyCleanCode(boolean enable,int data){
        if(enable){
            return data|0x08;
        }else {
            return data&0xF7;
        }
    }

    /***
     * 启动冰饮
     * @param data reverse1字节数据
     * @return
     */
    public static int oneIcedDrinkCode(boolean enable,int data){
        if(enable){
            return data|0x02;
        }else {
            return data&0xFD;
        }
    }

    /***
     * 翻转梁加热复位模式
     * @param data action字节数据
     * @return
     */
    public static int rollingOverCloseModeCode(boolean enable,int data){
        if(enable){
            return data|0x40;
        }else {
            return data&0xBF;
        }
    }


    /***
     * 启动商检
     * @param data diagnose2字节数据
     * @return
     */
    public static int commodityInspectionCode(boolean enable,int data){
        if(enable){
            return data|0x20;
        }else {
            return data&0xcf;
        }
    }

    /***
     * 启动缩时
     * @param data diagnose2字节数据
     * @return
     */
    public static int timeCut(boolean enable,int data){
        if(enable){
            return data|0x10;
        }else {
            return data&0xef;
        }
    }

    /***
     * 强制化霜
     * @param data action字节数据
     * @return
     */
    public static int forcedFrostCode(boolean enable,int data){
        if(enable){
            return data|0x02;
        }else {
            return data&0xFD;
        }
    }

    /***
     * 强制不停机
     * @param data action字节数据
     * @return
     */
    public static int RCFForcedStartCode(boolean enable,int data){
        if(enable){
            return data|0x01;
        }else {
            return data&0xFE;
        }
    }


}
