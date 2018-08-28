package com.mode.fridge.manager;

import android.content.Context;
import android.util.Log;

import com.mode.fridge.bean.DeviceParamsSet;
import com.mode.fridge.bean.SerialInfo;
import com.mode.fridge.utils.FileUtil;
import com.mode.fridge.utils.log;

/**
 * Created by young2 on 2016/9/7.
 */
public class DeviceParamsManager {
    private final static String TAG=DeviceParamsManager.class.getSimpleName();
    public final static String  DeviceParamsFile="DeviceParams.dat";

    public static void saveDeviceParams(Context context, DeviceParamsSet params){
        FileUtil.saveObject(context,DeviceParamsFile,params);
    }

    public static DeviceParamsSet getDeviceParams(Context context){
        DeviceParamsSet params=null;
        try{
             params= (DeviceParamsSet) FileUtil.getObject(context,DeviceParamsFile);
             log.d(TAG,"getDeviceParams,params="+params.toString());
        }catch (Exception e){
            Log.e(TAG,"getDeviceParams error,msg="+e.getMessage());
            e.printStackTrace();
        }
        if(params==null){
            params=new DeviceParamsSet();
            params.mode= SerialInfo.MODE_SMART;
            params.cold_closet_temp_set=SerialInfo.COLD_COLSET_DEFAULT_TEMP;
            params.temp_changeable_room_temp_set=SerialInfo.TEMP_CHANGEABLE_ROOM_DEFAULT_TEMP;
            params.freezing_room_temp_set=SerialInfo.FREEZING_ROOM_DEFAULT_TEMP;
            params.cold_closet_room_enable=true;
            params.temp_changeable_room_room_enable=true;
            params.clean=false;
            params.RCF_forced=false;
            params.cc_forced_frost=false;
            params.fz_forced_frost=false;
            params.iced_drink=false;
            params.rolling_over_close_mode=false;
        }
        params.commodity_inspection=false;
        params.time_cut=false;
        return params;
    }

    public static void deleteParamsSet(Context context){
        FileUtil.saveObject(context,DeviceParamsFile,null);
    }


    private static String formatNull(String input){
        if(input==null||input.equals("null")){
            return null;
        }
        return input;
    }
}
