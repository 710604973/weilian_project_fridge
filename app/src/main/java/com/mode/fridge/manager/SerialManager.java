package com.mode.fridge.manager;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.mediatek.factorymode.serial.jniSERIAL;
import com.mode.fridge.MyApplication;
import com.mode.fridge.bean.DataSendInfo;
import com.mode.fridge.bean.DeviceError;
import com.mode.fridge.bean.DeviceParamsGet;
import com.mode.fridge.bean.DeviceParamsSet;
import com.mode.fridge.bean.SerialInfo;
import com.mode.fridge.broadcast.BroadcastAction;
import com.mode.fridge.common.GlobalParams;
import com.mode.fridge.device.AppConfig;
import com.mode.fridge.device.DeviceConfig;
import com.mode.fridge.device.DeviceManager;
import com.mode.fridge.parser.SerialReceiveParser;
import com.mode.fridge.parser.SerialSendParser;
import com.mode.fridge.utils.CRC16;
import com.mode.fridge.utils.DataProcessUtil;
import com.mode.fridge.utils.FileUtil;
import com.mode.fridge.utils.log;
import com.viomi.common.callback.AppCallback;
import com.viomi.common.callback.ProgressCallback;

import java.io.IOException;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 串口通讯管理
 * Created by young2 on 2016/12/29.
 */

public class SerialManager {
    private static final String TAG=SerialManager.class.getSimpleName();
    private static SerialManager INSTANCE;
    private jniSERIAL mSerialPort;
    private ReadThread mReadThread=null;
//    private DataReceiveInfo mDataReceiveInfo=new DataReceiveInfo();//接收数据
//    private DataSendInfo mDataSendInfo=new DataSendInfo();//发送数据
    private int [] mDataReceive;//上一次接受数据
    private long mTimeReceive;//上一次接收数据时间戳
    private DeviceParamsGet mDeviceParamsGet=new DeviceParamsGet();//接收数据
    private DeviceParamsSet mDeviceParamsSet=new DeviceParamsSet();//发送数据

    private static final int MAX_CMD_WAIT_TIME=4000;//发命令等待回复最长时间
    public static byte HEADER=(byte)0xbb;//命令开始头
    private static byte UPGRADE_START=0x01;//功能位，升级开始位
    private static byte UPGRADE_DATA_SEND_SUCCESS=0x02;//功能位，升级发送数据位成功
    private static byte UPGRADE_DATA_SEND_FAIL=0x03;//功能位，升级发送数据位失败
    private static byte UPGRADE_SUCCESS=0x04;//功能位，升级成功
    private static byte NOEMAL_STATUS=0;//正常状态，不在升级中
    private int mUpgradeStep=NOEMAL_STATUS;
  //  private DeviceParamsSet mDeviceParamsSetBeforFrost=new DeviceParamsSet();//暂存化霜前数据
    private DeviceParamsSet mDeviceParamsSetTemp=new DeviceParamsSet();//暂存修改前数据，V2垃圾协议暂用
    private DeviceParamsGet mDeviceParamsGetTemp=new DeviceParamsGet();//暂存接收数据
    private boolean hasDataReceive;//是否收到串口数据，V2垃圾协议暂用
    private boolean isForcedFrost=false;//是否启动了强制化霜

    private AppCallback<String> mCMDWriteCallback;
    private Timer mTimer;
    private TimerTask mTimeTask;
    private Timer mTimer1;
    private TimerTask mTimeTask1;
    private int mWriteCount=0;//写命令计数
    private int mCommunicateCount;//通讯计时
    private boolean mCommunicateErrorFlag;//通讯异常是否已发生

    public int mCCRoomOpenCount,mTCRoomOpenCount,mFZRoomOpenCount;//门开报警计数，单位500ms，一分钟开始报警
    private boolean mIsCCRoomOpenAlert,mIsTCRoomOpenAlert,mIsFZRoomOpenAlert;//是否有报警
    public static int V1_ROOM_OPEN_COUNT=10;//三门开门记时，
    public static int V2_ROOM_OPEN_COUNT=130;//四门开门计时
    public static int V3_ROOM_OPEN_COUNT=60;//四门美菱开门计时
    private int DEVICE_ERROR=0;//异常判断位
    private DeviceError mDeviceError=new DeviceError();//异常
    private boolean mIsWriting;//是否正在发送串口数据

    public static SerialManager getInstance(){
        synchronized (SerialManager.class){
            if(INSTANCE==null){
                synchronized (SerialManager.class){
                    if(INSTANCE==null){
                        INSTANCE=new SerialManager();
                    }
                }
            }
        }
        return INSTANCE;
    }

//    public DataReceiveInfo getDataReceiveInfo(){
//        return mDataReceiveInfo;
//    }
//
//    public DataSendInfo getDataSendInfo(){
//        return mDataSendInfo;
//    }

        public DeviceParamsGet getDataReceiveInfo(){
        return mDeviceParamsGet;
    }

    public DeviceParamsSet getDataSendInfo(){
        return mDeviceParamsSet;
    }

    /***
     * 打开串口
     *@param model 型号
     */
    public void open(String model ){
        mSerialPort=new jniSERIAL();
        if(mSerialPort.no_serial_lib){
            Log.e(TAG,"open no_serial_lib");
            return;
        }

        int baudrate=9600;
        if(model.equals(AppConfig.VIOMI_FRIDGE_V2)){
            baudrate=4800;
        }
        Log.e(TAG,"open serial,baudrate="+baudrate);
        int result=mSerialPort.init(mSerialPort,baudrate,8,'n',1);

        Log.i(TAG,"open serial ,result="+result);
        if(mReadThread==null){
            mReadThread =new ReadThread();
            mReadThread.start();
        }

        if(mTimer1!=null){
            mTimer1.cancel();
            mTimer1=null;
        }
        if(mTimeTask1!=null){
            mTimeTask1.cancel();
            mTimeTask1=null;
        }
        mTimer1=new Timer();
        mTimeTask1=new TimerTask() {
            @Override
            public void run() {
                mDeviceParamsSet= DeviceParamsManager.getDeviceParams(MyApplication.getContext());

                cmdWrite(mDeviceParamsSet,true,null);
            }
        };
        mTimer1.schedule(mTimeTask1,5000);

    }


    /**
     * 关闭串口
     */
    public void close(){
        if(mReadThread!=null){
            mReadThread.interrupt();
            mReadThread=null;
        }
        if(mSerialPort!=null){
            if(!mSerialPort.no_serial_lib) {
                mSerialPort.exit();
            }
        }
    }

    /**
     * 串口接收数据
     * @param data
     */
    private synchronized void onDataReceive(int [] data,int size){

        if(DeviceConfig.MODEL.equals(AppConfig.VIOMI_FRIDGE_V1)|| DeviceConfig.MODEL.equals(AppConfig.VIOMI_FRIDGE_V4)){//三门通信故障判断
            mCommunicateCount++;
            if((!mCommunicateErrorFlag)&&mCommunicateCount>120){//一分钟后显示异常
                mDeviceParamsGet.error=mDeviceParamsGet.error|0x0001;
                sendCommunicateError(mDeviceParamsGet.error);
                mCommunicateErrorFlag=true;
            }
        }

        if(data==null||size<=0){
            return;
        }
        mTimeReceive=System.currentTimeMillis();
        mDataReceive=data;

        Log.d(TAG, "onDataReceive,length="+size+",model="+ DeviceConfig.MODEL);
        Log.d(TAG, "onDataReceive,data="+ DataProcessUtil.bytesToHexString(data,size));
        if(DeviceConfig.MODEL.equals(AppConfig.VIOMI_FRIDGE_V1)|| DeviceConfig.MODEL.equals(AppConfig.VIOMI_FRIDGE_V4)){
            if(checkUpgradeStep(data,size)){
                return;
            }
        }

        boolean result= SerialReceiveParser.parser(data,mDeviceParamsGet);
        Log.d(TAG,"result="+result+",mDataReceiveInfo:"+mDeviceParamsGet.toString());
        if(result){
            hasDataReceive=true;
            mCommunicateCount=0;
            mCommunicateErrorFlag=false;//异常解决
            roomOpenAlertprocess();//处理门开报警
            deviceParamsGetNotify(mDeviceParamsGetTemp,mDeviceParamsGet);
            copyDeviceParamset(mDeviceParamsGetTemp,mDeviceParamsGet);
            compareError(mDeviceParamsGet.error);
        }
    }

     private void sendCommunicateError(int error){
         DeviceError deviceError=new DeviceError();
         deviceError.parserErrorCode(error);
         DEVICE_ERROR=error;
         mDeviceError=deviceError;
         DeviceManager.getInstance().sendFaultHappen(DeviceError.int_error_traffic);
    }

    /***
     * 异常发生和修复上报
     * @param error
     */
    private void compareError(int error){
        if(error!=DEVICE_ERROR){
            DeviceError deviceError=new DeviceError();
            deviceError.parserErrorCode(error);

            if(deviceError.error_traffic!=mDeviceError.error_traffic){
                if(deviceError.error_traffic){
                    DeviceManager.getInstance().sendFaultHappen(DeviceError.int_error_traffic);
                }else {
                    DeviceManager.getInstance().sendFaultFix(DeviceError.int_error_traffic);
                }
            }
            if(deviceError.error_ccroom_sencor!=mDeviceError.error_ccroom_sencor){
                if(deviceError.error_ccroom_sencor){
                    DeviceManager.getInstance().sendFaultHappen(DeviceError.int_error_ccroom_sencor);
                }else {
                    DeviceManager.getInstance().sendFaultFix(DeviceError.int_error_ccroom_sencor);
                }
            }
            if(deviceError.error_tcroom_sencor!=mDeviceError.error_tcroom_sencor){
                if(deviceError.error_tcroom_sencor){
                    DeviceManager.getInstance().sendFaultHappen(DeviceError.int_error_tcroom_sencor);
                }else {
                    DeviceManager.getInstance().sendFaultFix(DeviceError.int_error_tcroom_sencor);
                }
            }
            if(deviceError.error_fzroom_sencor!=mDeviceError.error_fzroom_sencor){
                if(deviceError.error_fzroom_sencor){
                    DeviceManager.getInstance().sendFaultHappen(DeviceError.int_error_fzroom_sencor);
                }else {
                    DeviceManager.getInstance().sendFaultFix(DeviceError.int_error_fzroom_sencor);
                }
            }
            if(deviceError.error_cc_defrost_sencor!=mDeviceError.error_cc_defrost_sencor){
                if(deviceError.error_cc_defrost_sencor){
                    DeviceManager.getInstance().sendFaultHappen(DeviceError.int_error_cc_defrost_sencor);
                }else {
                    DeviceManager.getInstance().sendFaultFix(DeviceError.int_error_cc_defrost_sencor);
                }
            }
            if(deviceError.error_fz_defrost_sencor!=mDeviceError.error_fz_defrost_sencor){
                if(deviceError.error_fz_defrost_sencor){
                    DeviceManager.getInstance().sendFaultHappen(DeviceError.int_error_fz_defrost_sencor);
                }else {
                    DeviceManager.getInstance().sendFaultFix(DeviceError.int_error_fz_defrost_sencor);
                }
            }
            if(deviceError.error_indoor_sencor!=mDeviceError.error_indoor_sencor){
                if(deviceError.error_indoor_sencor){
                    DeviceManager.getInstance().sendFaultHappen(DeviceError.int_error_indoor_sencor);
                }else {
                    DeviceManager.getInstance().sendFaultFix(DeviceError.int_error_indoor_sencor);
                }
            }
            if(deviceError.error_air_door!=mDeviceError.error_air_door){
                if(deviceError.error_air_door){
                    DeviceManager.getInstance().sendFaultHappen(DeviceError.int_error_air_door);
                }else {
                    DeviceManager.getInstance().sendFaultFix(DeviceError.int_error_air_door);
                }
            }
//            if(deviceError.error_ccroom_fan!=mDeviceError.error_ccroom_fan){
//                if(deviceError.error_ccroom_fan){
//                    DeviceManager.getInstance().sendFaultHappen(DeviceError.int_error_ccroom_fan);
//                }else {
//                    DeviceManager.getInstance().sendFaultFix(DeviceError.int_error_ccroom_fan);
//                }
//            }
//            if(deviceError.error_tcroom_fan!=mDeviceError.error_tcroom_fan){
//                if(deviceError.error_tcroom_fan){
//                    DeviceManager.getInstance().sendFaultHappen(DeviceError.int_error_tcroom_fan);
//                }else {
//                    DeviceManager.getInstance().sendFaultFix(DeviceError.int_error_tcroom_fan);
//                }
//            }
//            if(deviceError.error_fzroom_fan!=mDeviceError.error_fzroom_fan){
//                if(deviceError.error_fzroom_fan){
//                    DeviceManager.getInstance().sendFaultHappen(DeviceError.int_error_fzroom_fan);
//                }else {
//                    DeviceManager.getInstance().sendFaultFix(DeviceError.int_error_fzroom_fan);
//                }
//            }
            DEVICE_ERROR=error;
            mDeviceError=deviceError;
        }
    }

    /***
     * 门开报警处理
     */
    private void roomOpenAlertprocess(){
        int timeOutCount=V1_ROOM_OPEN_COUNT;
        if(DeviceConfig.MODEL.equals(AppConfig.VIOMI_FRIDGE_V2)){
            timeOutCount=V2_ROOM_OPEN_COUNT;
        }else if(DeviceConfig.MODEL.equals(AppConfig.VIOMI_FRIDGE_V3)|| DeviceConfig.MODEL.equals(AppConfig.VIOMI_FRIDGE_V31)){
            timeOutCount=V3_ROOM_OPEN_COUNT;
        }
        if(mDeviceParamsGet.door_ccroom_open_alarm){
            if(mCCRoomOpenCount<=timeOutCount){
                mCCRoomOpenCount++;
            }
        }else {
            mCCRoomOpenCount=0;
        }
        if (DeviceConfig.MODEL.equals(AppConfig.VIOMI_FRIDGE_V1)|| DeviceConfig.MODEL.equals(AppConfig.VIOMI_FRIDGE_V2)){
            if(mDeviceParamsGet.door_tcroom_open_alarm){
                if(mTCRoomOpenCount<=timeOutCount){
                    mTCRoomOpenCount++;
                }
            }else {
                mTCRoomOpenCount=0;
            }
        }
        if(mDeviceParamsGet.door_fzroom_open_alarm){
            if(mFZRoomOpenCount<=timeOutCount){
                mFZRoomOpenCount++;
            }
        }else {
            mFZRoomOpenCount=0;
        }

        if(mCCRoomOpenCount==timeOutCount||mTCRoomOpenCount==timeOutCount||mFZRoomOpenCount==timeOutCount){
            if(mCCRoomOpenCount==timeOutCount){
                mIsCCRoomOpenAlert=true;
                DeviceManager.getInstance().sendFaultHappen((int) Math.pow(2,11));//冷藏门开报警上报事件
            }
            if(mTCRoomOpenCount==timeOutCount){
                mIsTCRoomOpenAlert=true;
                DeviceManager.getInstance().sendFaultHappen((int) Math.pow(2,12));//变温门开报警上报事件
            }
            if(mFZRoomOpenCount==timeOutCount){
                mIsFZRoomOpenAlert=true;
                DeviceManager.getInstance().sendFaultHappen((int) Math.pow(2,13));//冷冻门开报警上报事件
            }
            Intent intent=new Intent(BroadcastAction.ACTION_ROOM_OPEN_HAPPEN);//出现任何一个门开报警，弹出报警界面
            LocalBroadcastManager.getInstance(MyApplication.getContext()).sendBroadcast(intent);
        } else if(mCCRoomOpenCount<timeOutCount&&mTCRoomOpenCount<timeOutCount&&mFZRoomOpenCount<timeOutCount){
            if(mIsCCRoomOpenAlert||mIsTCRoomOpenAlert||mIsFZRoomOpenAlert){//解除全部门开报警，关闭报警窗口
                Intent intent=new Intent(BroadcastAction.ACTION_ROOM_OPEN_REMOVE);
                LocalBroadcastManager.getInstance(MyApplication.getContext()).sendBroadcast(intent);
            }
        }

        if(mIsCCRoomOpenAlert&&mCCRoomOpenCount<timeOutCount){//刚解除冷藏门开报警
            DeviceManager.getInstance().sendFaultFix((int) Math.pow(2,11));//门开报警解除上报事件
            mIsCCRoomOpenAlert=false;
         }
        if(mIsTCRoomOpenAlert&&mTCRoomOpenCount<timeOutCount){//刚解除变温门开报警
            DeviceManager.getInstance().sendFaultFix((int) Math.pow(2,12));//门开报警解除上报事件
            mIsTCRoomOpenAlert=false;
        }
        if(mIsFZRoomOpenAlert&&mFZRoomOpenCount<timeOutCount){//刚解除冷冻门开报警
            DeviceManager.getInstance().sendFaultFix((int) Math.pow(2,13));//门开报警解除上报事件
            mIsFZRoomOpenAlert=false;
        }

    }

    /***
     * 接收参数变化影响，一键净化完成、强制化霜完成、异常接收
     * @param lastDeviceParamsGet
     * @param currDeviceParamsGet
     */
    private void deviceParamsGetNotify(DeviceParamsGet lastDeviceParamsGet, DeviceParamsGet currDeviceParamsGet){

        if((!mIsWriting)&&(!currDeviceParamsGet.iced_drink) ){//冰饮退出
            mDeviceParamsSet.iced_drink=false;
        }

        if((!mIsWriting)&&(!currDeviceParamsGet.quick_cold)&&(lastDeviceParamsGet.quick_cold) ){//速冷退出
            log.d(TAG,"quick_cold quit");
            enableQuickCool(false,null);
        }

        if((!mIsWriting)&&(!currDeviceParamsGet.quick_freeze)&&(lastDeviceParamsGet.quick_freeze) ){//速冻退出
            log.d(TAG,"quick_freeze quit");
            enableQuickFreeze(false,null);
        }

        if(lastDeviceParamsGet.clean&&(!currDeviceParamsGet.clean)){//一键净化完成
            mDeviceParamsSet.clean=false;
        }

        if(DeviceConfig.MODEL.equals(AppConfig.VIOMI_FRIDGE_V1)|| DeviceConfig.MODEL.equals(AppConfig.VIOMI_FRIDGE_V4)){//强制化霜结束
            if(isForcedFrost &&currDeviceParamsGet.fz_forced_frost){
                isForcedFrost=false;
                reStoreSendData();
            }
        }

        if((currDeviceParamsGet.error!=0)&&(currDeviceParamsGet.error!=lastDeviceParamsGet.error)){//异常发生
            Intent intent=new Intent(BroadcastAction.ACTION_DEVICE_ERROR_HAPPEN);
            intent.putExtra(BroadcastAction.EXTRA_ERROR,currDeviceParamsGet.error);
            LocalBroadcastManager.getInstance(MyApplication.getContext()).sendBroadcast(intent);
        }

    }


//    /***
//     * 预设设置参数
//     * @param dataSendInfo
//     */
//    public void preSetSendData(DataSendInfo dataSendInfo){
//
//        dataSendInfo.mode=mDataReceiveInfo.mode;
//        dataSendInfo.cold_closet_temp_set=mDataReceiveInfo.cold_closet_temp_set;
//        dataSendInfo.temp_changeable_room_temp_set=mDataReceiveInfo.temp_changeable_room_temp_set;
//        dataSendInfo.freezing_room_temp_set=mDataReceiveInfo.freezing_room_temp_set;
//        boolean isClean=SerialReceiveParser.isOneKeyCleanWorking(mDataReceiveInfo.status);
//        dataSendInfo.diagnose2=SerialSendParser.oneKeyCleanCode(isClean,dataSendInfo.diagnose2);
//    }

    /***
     * 商检的步骤
     * A、通电后的前20min，关闭变温室，冷藏室设置为2℃，速冻模式。
     B、通电20—40min，关闭冷藏室，打开变温室，变温室设置为-7℃，速冻模式。
     C、通电40—45min钟，关闭变温室，关闭冷藏室，速冻模式。
     D、商检模式下，不影响故障报警功能。
     商检退出后设置为智能模式
     * @param step
     */
    public boolean commodityInspection(int step){
        if (DeviceConfig.MODEL.equals(AppConfig.VIOMI_FRIDGE_V1)){
            switch (step){
                case ControlManager.Step_Commodity_Inspection_0:
                    mDeviceParamsSet.mode=SerialInfo.MODE_NULL;
                    mDeviceParamsSet.commodity_inspection=true;
                    mDeviceParamsSet.cold_closet_temp_set= 2;
                    mDeviceParamsSet.temp_changeable_room_temp_set=SerialInfo.ROOM_CLOSE_TEMP;
                    mDeviceParamsSet.temp_changeable_room_room_enable=false;
                    mDeviceParamsSet.cold_closet_room_enable=true;
                    mDeviceParamsSet.freezing_room_temp_set=-25;
                    break;
                case ControlManager.Step_Commodity_Inspection_1:
                    mDeviceParamsSet.mode=SerialInfo.MODE_NULL;
                    mDeviceParamsSet.commodity_inspection=true;
                    mDeviceParamsSet.cold_closet_temp_set= SerialInfo.ROOM_CLOSE_TEMP;
                    mDeviceParamsSet.cold_closet_room_enable=false;
                    mDeviceParamsSet.temp_changeable_room_room_enable=true;
                    mDeviceParamsSet.temp_changeable_room_temp_set=-7;
                    mDeviceParamsSet.freezing_room_temp_set=-25;
                    break;
                case ControlManager.Step_Commodity_Inspection_2:
                    mDeviceParamsSet.mode=SerialInfo.MODE_NULL;
                    mDeviceParamsSet.commodity_inspection=true;
                    mDeviceParamsSet.cold_closet_temp_set= SerialInfo.ROOM_CLOSE_TEMP;
                    mDeviceParamsSet.temp_changeable_room_temp_set=SerialInfo.ROOM_CLOSE_TEMP;
                    mDeviceParamsSet.temp_changeable_room_room_enable=false;
                    mDeviceParamsSet.cold_closet_room_enable=false;
                    mDeviceParamsSet.freezing_room_temp_set=-25;
                    break;
                case ControlManager.Step_Commodity_Inspection_end:
                    mDeviceParamsSet.commodity_inspection=false;
                    return ControlManager.getInstance().enableSmartMode(true,true);
            }
            return cmdWrite(mDeviceParamsSet,false,null);
        }else if(DeviceConfig.MODEL.equals(AppConfig.VIOMI_FRIDGE_V4)){
            switch (step){
                case ControlManager.Step_Commodity_Inspection_0:
                    mDeviceParamsSet.mode=SerialInfo.MODE_NULL;
                    mDeviceParamsSet.commodity_inspection=true;
                    mDeviceParamsSet.cold_closet_temp_set= 2;
                    mDeviceParamsSet.cold_closet_room_enable=true;
                    mDeviceParamsSet.freezing_room_temp_set=-25;
                    break;
                case ControlManager.Step_Commodity_Inspection_1:
                    mDeviceParamsSet.mode=SerialInfo.MODE_NULL;
                    mDeviceParamsSet.commodity_inspection=true;
                    mDeviceParamsSet.cold_closet_temp_set= SerialInfo.ROOM_CLOSE_TEMP;
                    mDeviceParamsSet.cold_closet_room_enable=false;
                    mDeviceParamsSet.freezing_room_temp_set=-25;
                    break;
                case ControlManager.Step_Commodity_Inspection_end:
                    mDeviceParamsSet.commodity_inspection=false;
                    return ControlManager.getInstance().enableSmartMode(true,true);
            }
            return cmdWrite(mDeviceParamsSet,false,null);
        }else {
            mDeviceParamsSet.commodity_inspection=true;
            byte[] bytes=new byte[]{0x55,0x55 , (byte) 0xFC,0x00 ,0x00 ,0x00 ,0x00 ,0x5A ,0x2C ,0x46 ,0x00 ,0x7A ,0x04
                    ,0x00 ,0x00 ,0x00 ,0x46 , (byte) 0xAA, (byte) 0xAA};
            return cmdWrite(bytes);
        }
    }

    /***
     * 启动一键净化
     * @param enable
     * @return
     */
    public boolean oneKeyClean(boolean enable,AppCallback<String> callback){
        copyDeviceParamsSet(mDeviceParamsSetTemp,mDeviceParamsSet);
        mDeviceParamsSet.clean=enable;
        return cmdWrite(mDeviceParamsSet,false,callback);
    }

    /***
     * 设置冷藏室温度
     * @param temp
     * @param roomType
     * @return
     */
    public boolean setRoomTemp(int temp,int roomType,AppCallback<String> callback){
        copyDeviceParamsSet(mDeviceParamsSetTemp,mDeviceParamsSet);
        if(roomType==SerialInfo.ROOM_COLD_COLSET){
            mDeviceParamsSet.quick_cold=false;
            mDeviceParamsSet.cold_closet_temp_set=temp;
            mDeviceParamsSet.mode=SerialInfo.MODE_NULL;
        }else if(roomType==SerialInfo.ROOM_CHANGEABLE_ROOM){
            mDeviceParamsSet.temp_changeable_room_temp_set=temp;
            mDeviceParamsSet.mode=SerialInfo.MODE_NULL;
        }else if(roomType==SerialInfo.ROOM_FREEZING_ROOM){
            mDeviceParamsSet.quick_freeze=false;
            mDeviceParamsSet.freezing_room_temp_set=temp;
            mDeviceParamsSet.mode= SerialInfo.MODE_NULL;
        }
        return cmdWrite(mDeviceParamsSet,true,callback);
    }

    /***
     * 打开或关闭冷藏室/变温室
     * @param enable
     * @param roomType {@link SerialInfo.RoomType}.
     * @return
     */
    public boolean enableRoom(boolean enable,int roomType,AppCallback<String> callback){
        if(roomType!=SerialInfo.ROOM_COLD_COLSET&&roomType!=SerialInfo.ROOM_CHANGEABLE_ROOM){
            Log.e(TAG,"enableRoom type error!");
            return false;
        }
        copyDeviceParamsSet(mDeviceParamsSetTemp,mDeviceParamsSet);

        if(enable){
            int temp=0;
            if(roomType==SerialInfo.ROOM_COLD_COLSET){
                mDeviceParamsSet.quick_cold=false;
                mDeviceParamsSet.mode=SerialInfo.MODE_NULL;
                temp=GlobalParams.getInstance().getClodClosetTempBeforClose();
                if(temp==SerialInfo.ROOM_CLOSE_TEMP){
                    temp=SerialInfo.COLD_COLSET_DEFAULT_TEMP;
                }
                mDeviceParamsSet.cold_closet_temp_set=temp;
                mDeviceParamsSet.cold_closet_room_enable=enable;
            }else if(roomType==SerialInfo.ROOM_CHANGEABLE_ROOM) {
                temp=GlobalParams.getInstance().getChangeableRoomTempBeforClose();
                if(temp==SerialInfo.ROOM_CLOSE_TEMP){
                    temp=SerialInfo.TEMP_CHANGEABLE_ROOM_DEFAULT_TEMP;
                }
                mDeviceParamsSet.temp_changeable_room_temp_set=temp;
                mDeviceParamsSet.temp_changeable_room_room_enable=enable;
            }
        }else {
            if(roomType==SerialInfo.ROOM_COLD_COLSET){
                mDeviceParamsSet.quick_cold=false;
                mDeviceParamsSet.mode=SerialInfo.MODE_NULL;
                GlobalParams.getInstance().setClodClosetTempBeforClose(ControlManager.getInstance().getDataSendInfo().cold_closet_temp_set);
                if(DeviceConfig.MODEL.equals(AppConfig.VIOMI_FRIDGE_V1)|| DeviceConfig.MODEL.equals(AppConfig.VIOMI_FRIDGE_V4)){
                    mDeviceParamsSet.cold_closet_temp_set=SerialInfo.ROOM_CLOSE_TEMP;
                }
                mDeviceParamsSet.cold_closet_room_enable=enable;
            }else if(roomType==SerialInfo.ROOM_CHANGEABLE_ROOM) {
                GlobalParams.getInstance().setClodClosetTempBeforClose(ControlManager.getInstance().getDataSendInfo().temp_changeable_room_temp_set);
                if(DeviceConfig.MODEL.equals(AppConfig.VIOMI_FRIDGE_V1)|| DeviceConfig.MODEL.equals(AppConfig.VIOMI_FRIDGE_V4)){
                    mDeviceParamsSet.temp_changeable_room_temp_set=SerialInfo.ROOM_CLOSE_TEMP;
                }
                mDeviceParamsSet.temp_changeable_room_room_enable=enable;
            }

        }
        mDeviceParamsSet.mode=SerialInfo.MODE_NULL;
        return cmdWrite(mDeviceParamsSet,true,callback);
    }

    /***
     * 启动智能模式
     * @param enable
     * @return
     */
    public boolean enableSmartMode(boolean enable){
        copyDeviceParamsSet(mDeviceParamsSetTemp,mDeviceParamsSet);
        if(enable){
            mDeviceParamsSet.mode=SerialInfo.MODE_SMART;
            mDeviceParamsSet.quick_freeze=false;
            mDeviceParamsSet.quick_cold=false;
            mDeviceParamsSet.cold_closet_room_enable=true;
    //        mDeviceParamsSet.temp_changeable_room_room_enable=true;
            if(mDeviceParamsGet.indoor_temp<23){
                mDeviceParamsSet.cold_closet_temp_set=4;
//                mDeviceParamsSet.temp_changeable_room_temp_set=-4;
                mDeviceParamsSet.freezing_room_temp_set=-18;
            }else if(mDeviceParamsGet.indoor_temp>=23&&mDeviceParamsGet.indoor_temp<27) {
                mDeviceParamsSet.cold_closet_temp_set = 5;
//                mDeviceParamsSet.temp_changeable_room_temp_set = -4;
                mDeviceParamsSet.freezing_room_temp_set = -18;
            }else if(mDeviceParamsGet.indoor_temp>=27&&mDeviceParamsGet.indoor_temp<34) {
                mDeviceParamsSet.cold_closet_temp_set = 6;
//                mDeviceParamsSet.temp_changeable_room_temp_set = -4;
                mDeviceParamsSet.freezing_room_temp_set = -18;
            }else  {
                mDeviceParamsSet.cold_closet_temp_set = 7;
    //            mDeviceParamsSet.temp_changeable_room_temp_set = -3;
                mDeviceParamsSet.freezing_room_temp_set = -17;
            }
        }else {
            mDeviceParamsSet.mode=SerialInfo.MODE_NULL;
        }

        return cmdWrite(mDeviceParamsSet,true,null);
    }

    /***
     * 启动智能模式,商检后第一次
     * @param enable
     * @return
     */
    public boolean enableSmartMode(boolean enable,boolean isFrist){
        copyDeviceParamsSet(mDeviceParamsSetTemp,mDeviceParamsSet);
        if(enable){
            mDeviceParamsSet.quick_freeze=false;
            mDeviceParamsSet.quick_cold=false;
            mDeviceParamsSet.mode=SerialInfo.MODE_SMART;
            mDeviceParamsSet.cold_closet_room_enable=true;
            mDeviceParamsSet.temp_changeable_room_room_enable=true;
            if(mDeviceParamsGet.indoor_temp<23){
                mDeviceParamsSet.cold_closet_temp_set=4;
               mDeviceParamsSet.temp_changeable_room_temp_set=-4;
                mDeviceParamsSet.freezing_room_temp_set=-18;
            }else if(mDeviceParamsGet.indoor_temp>=23&&mDeviceParamsGet.indoor_temp<27) {
                mDeviceParamsSet.cold_closet_temp_set = 5;
                mDeviceParamsSet.temp_changeable_room_temp_set = -4;
                mDeviceParamsSet.freezing_room_temp_set = -18;
            }else if(mDeviceParamsGet.indoor_temp>=27&&mDeviceParamsGet.indoor_temp<34) {
                mDeviceParamsSet.cold_closet_temp_set = 6;
               mDeviceParamsSet.temp_changeable_room_temp_set = -4;
                mDeviceParamsSet.freezing_room_temp_set = -18;
            }else  {
                mDeviceParamsSet.cold_closet_temp_set = 7;
                mDeviceParamsSet.temp_changeable_room_temp_set = -3;
                mDeviceParamsSet.freezing_room_temp_set = -17;
            }
        }else {
            mDeviceParamsSet.mode=SerialInfo.MODE_NULL;
        }

        return cmdWrite(mDeviceParamsSet,true,null);
    }

    /***
     * 启动假日模式
     * @param enable
     * @return
     */
    public boolean enableHolidayMode(boolean enable,AppCallback<String> callbackk){
        //mDeviceParamsSetTemp=mDeviceParamsSet;
        copyDeviceParamsSet(mDeviceParamsSetTemp,mDeviceParamsSet);
        if(enable){
            mDeviceParamsSet.mode=SerialInfo.MODE_HOLIDAY;
            mDeviceParamsSet.quick_freeze=false;
            mDeviceParamsSet.quick_cold=false;
            mDeviceParamsSet.cold_closet_room_enable=true;
            mDeviceParamsSet.temp_changeable_room_room_enable=true;
            mDeviceParamsSet.cold_closet_temp_set = 8;
        //    mDeviceParamsSet.temp_changeable_room_temp_set = 0;
            mDeviceParamsSet.freezing_room_temp_set = -18;
        }else {
            mDeviceParamsSet.mode=SerialInfo.MODE_NULL;
        }

        return cmdWrite(mDeviceParamsSet,true,callbackk);
    }

    /***
     * 启动强制化霜
     * @param enable
     * @return
     */
    public boolean enableForcedFrost(boolean enable){
        mDeviceParamsSet.fz_forced_frost=enable;
       // copyDeviceParamsSet(mDeviceParamsSetBeforFrost,mDeviceParamsSet);
        isForcedFrost=true;
        return cmdWrite(mDeviceParamsSet);
    }

    /***
     * 恢复强制化霜前状态
     * @return
     */
    private boolean reStoreSendData(){
       log.d(TAG,"reStoreSendData ");
        mDeviceParamsSet.fz_forced_frost=false;
        return cmdWrite(mDeviceParamsSet,true,null);

//        if(mDeviceParamsSetBeforFrost!=null){
//      //      copyDeviceParamsSet(mDeviceParamsSet,mDeviceParamsSetBeforFrost);
//            mDeviceParamsSet.fz_forced_frost=false;
//            return cmdWrite(mDeviceParamsSet,true,null);
//        }else {
//            Log.e(TAG,"mDeviceParamsSetBeforFrost null");
//            return false;
//        }

    }

    /***
     * 使能冰饮功能
     * @return
     */
    public boolean enableIcedDrink(boolean enable,AppCallback<String> callback){
        log.d(TAG,"enableIcedDrink ");
        mDeviceParamsSet.iced_drink=enable;
        return cmdWrite(mDeviceParamsSet,true,callback);
    }

    /***
     * 使能翻转梁加热器复位模式
     * @return
     */
    public boolean enableRollingOverCloseMode(boolean enable,AppCallback<String> callback){
        log.d(TAG,"enableRollingOverCloseMode ");
        mDeviceParamsSet.rolling_over_close_mode=enable;
        return cmdWrite(mDeviceParamsSet,true,callback);
    }

    /***
     * 启动强制不停机
     * @param enable
     * @return
     */
    public boolean enableRCFForcedStart(boolean enable){
        mDeviceParamsSet.RCF_forced=enable;
        return cmdWrite(mDeviceParamsSet);
    }

    /***
     * 启动缩时功能
     * @param enable
     * @return
     */
    public boolean enableTimeCut(boolean enable){
        mDeviceParamsSet.time_cut=enable;
        return cmdWrite(mDeviceParamsSet);
    }

    /***
     * 速冻
     * @param enable
     * @return
     */
    public boolean enableQuickFreeze(boolean enable,AppCallback<String> callback){
        copyDeviceParamsSet(mDeviceParamsSetTemp,mDeviceParamsSet);
        if(enable){
            mDeviceParamsSet.quick_freeze=true;
            mDeviceParamsSet.mode=SerialInfo.MODE_NULL;
            GlobalParams.getInstance().setFreezeRoomTempBeforQuickFreeze(mDeviceParamsSet.freezing_room_temp_set);
            if(DeviceConfig.MODEL.equals(AppConfig.VIOMI_FRIDGE_V2)){
                mDeviceParamsSet.freezing_room_temp_set=-24;
            } else if(DeviceConfig.MODEL.equals(AppConfig.VIOMI_FRIDGE_V3)|| DeviceConfig.MODEL.equals(AppConfig.VIOMI_FRIDGE_V31)){
                mDeviceParamsSet.freezing_room_temp_set=-32;
            }else if(DeviceConfig.MODEL.equals(AppConfig.VIOMI_FRIDGE_V4)){
                mDeviceParamsSet.freezing_room_temp_set=-27;
                mDeviceParamsSet.quick_cold=false;//v4速冷和速冻不同时存在
//                mDeviceParamsSet.cold_closet_temp_set =  GlobalParams.getInstance().getRoomTempBeforQuickCool();
            }
        }else {
            mDeviceParamsSet.quick_freeze=false;
            mDeviceParamsSet.mode=SerialInfo.MODE_NULL;
            mDeviceParamsSet.freezing_room_temp_set =  GlobalParams.getInstance().getFreezeRoomTempBeforQuickFreeze();
        }
        return cmdWrite(mDeviceParamsSet,true,callback);
    }

    /***
     * 速冷
     * @param enable
     * @return
     */
    public boolean enableQuickCool(boolean enable,AppCallback<String> callback){
        copyDeviceParamsSet(mDeviceParamsSetTemp,mDeviceParamsSet);
        if(enable){
            mDeviceParamsSet.cold_closet_room_enable=true;
            mDeviceParamsSet.quick_cold=true;
            mDeviceParamsSet.mode=SerialInfo.MODE_NULL;
            int temp=mDeviceParamsSet.cold_closet_temp_set;
            if(!mDeviceParamsSet.cold_closet_room_enable){
                temp=SerialInfo.COLD_COLSET_DEFAULT_TEMP;
            }
            GlobalParams.getInstance().setRoomTempBeforQuickCool(temp);
            if(DeviceConfig.MODEL.equals(AppConfig.VIOMI_FRIDGE_V2)){
                mDeviceParamsSet.cold_closet_temp_set=2;
            }else if(DeviceConfig.MODEL.equals(AppConfig.VIOMI_FRIDGE_V3)|| DeviceConfig.MODEL.equals(AppConfig.VIOMI_FRIDGE_V31)){
                mDeviceParamsSet.cold_closet_temp_set=2;
            }else if(DeviceConfig.MODEL.equals(AppConfig.VIOMI_FRIDGE_V4)){
                mDeviceParamsSet.cold_closet_temp_set=2;
                mDeviceParamsSet.quick_freeze=false;//v4速冷和速冻不同时存在
//                mDeviceParamsSet.freezing_room_temp_set =  GlobalParams.getInstance().getFreezeRoomTempBeforQuickFreeze();
            }
        }else {
            mDeviceParamsSet.quick_cold=false;
            mDeviceParamsSet.mode=SerialInfo.MODE_NULL;
            mDeviceParamsSet.cold_closet_temp_set =  GlobalParams.getInstance().getRoomTempBeforQuickCool();
            if(mDeviceParamsSet.cold_closet_temp_set==SerialInfo.ROOM_CLOSE_TEMP){
                mDeviceParamsSet.cold_closet_temp_set=SerialInfo.COLD_COLSET_DEFAULT_TEMP;
            }
        }
        return cmdWrite(mDeviceParamsSet,true,callback);
    }

    /***
     * 固件升级
     * @param file 文件路径
     * @return
     */
    public boolean upgradeFirmware(String file, ProgressCallback callback){
        if(file==null){
            Log.e(TAG,"upgradeFirmware fail,file null!");
            return false;
        }
        callback.onProgress(0);
        //开始启动升级
        boolean result=upgradeFirmwareStep1();
        if(!result){
            Log.e(TAG,"upgradeFirmwareStep1 fail !");
            return false;
        }
        callback.onProgress(20);
        result=upgradeFirmwareStep2(file);
        if(!result){
            Log.e(TAG,"upgradeFirmwareStep2 fail !");
            return false;
        }
        callback.onProgress(80);
        result=upgradeFirmwareStep3();
        if(!result){
            Log.e(TAG,"upgradeFirmwareStep3 fail !");
            return false;
        }
        callback.onProgress(100);
        return true;
    }

    /***
     * 升级步骤1，开始升级
     * @return
     */
    public boolean upgradeFirmwareStep1(){
        mUpgradeStep=NOEMAL_STATUS;
        //开始启动升级
        byte[] bytes=new byte[4];
        bytes[0]=HEADER;
        bytes[1]=UPGRADE_START;
        int crc=CRC16.calcCrc16(bytes,2);
        bytes[2]= (byte) ((crc&0xff00)/256);
        bytes[3]= (byte) (crc&0x00ff);
        log.d(TAG,"cmdWrite,data="+ DataProcessUtil.bytesToHexString(bytes));
        boolean result=cmdWrite(bytes);
        if(!result){
            Log.e(TAG,"cmdWrite,data fail");
            return false;
        }
        int count=MAX_CMD_WAIT_TIME/100;//收到成功标注或者超时返回
        while (true){
            if(mUpgradeStep==UPGRADE_START){
                return true;
            }
            try {
                Thread.sleep(100);
                count--;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(count<=0){
                Log.e(TAG,"upgradeFirmwareStep1 time out");
                return false;
            }
        }

    }

    /***
     * 升级步骤2,发送bin文件数据
     * @param file
     * @return
     */
    public boolean upgradeFirmwareStep2(String file){
        if(file==null){
            return false;
        }
        byte[] fileBytes;
        try {
            fileBytes= FileUtil.readFileByBytes(file);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG,"readFileByBytes error!msg= "+e.getMessage());
            fileBytes=null;
            return false;
        }
        if(fileBytes==null||fileBytes.length<=1024){
            Log.e(TAG,"readFileByBytes file error!");
            fileBytes=null;
            return false;
        }

        int index=0;
        int perLength=256;//每次发送字节数
        int count=fileBytes.length/perLength;
        int remain=fileBytes.length%perLength;
        if(remain>0){
            count+=1;
        }
        byte[] bytes=new byte[1+perLength+2];
        for (int i=0;i<count;i++){
            bytes[0]=HEADER;
            if((remain>0)&&(i==count-1)){//最后一次不足256
                Arrays.fill(bytes, (byte) 0xff);
                System.arraycopy(fileBytes,index,bytes,1,remain);
                index+=remain;
            }else {
                System.arraycopy(fileBytes,index,bytes,1,perLength);
                index+=perLength;
            }
            int crc= CRC16.calcCrc16(bytes,1+perLength);
            bytes[1+perLength]= (byte) ((crc&0xff00)/256);
            bytes[1+perLength+1]= (byte) (crc&0x00ff);
            if(!pageSend(bytes)){
                Log.e(TAG,"upgradeFirmwareStep2 fail!pageSend,page index="+i);
                fileBytes=null;
                bytes=null;
                return false;
            }
        }
        fileBytes=null;
        bytes=null;
        return true;
    }

    /***
     * bin文件每页发送数据，失败的话重发五次
     * @param bytes
     * @return
     */
    private boolean pageSend(byte[] bytes){
        boolean result=false;
        for(int i=0;i<5;i++){
            result=perPageSend(bytes);
            if(result){
                return true;
            }
        }
        return false;
    }

    /***
     * bin文件数据单页发送
     * @param bytes
     * @return
     */
    private boolean perPageSend(byte[] bytes){
        mUpgradeStep=UPGRADE_START;
        boolean result=cmdWrite(bytes);
        if(!result){
            return false;
        }
        int count=MAX_CMD_WAIT_TIME/100;//收到成功标注或者超时返回
        while (true){
            if(mUpgradeStep==UPGRADE_DATA_SEND_SUCCESS){
                return true;
            } else if(mUpgradeStep==UPGRADE_DATA_SEND_FAIL){
                return false;
            }
            try {
                Thread.sleep(100);
                count--;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(count<=0){
                return false;
            }
        }

      }

    /***
     * 升级步骤3，检测升级成功
     * @return
     */
    public boolean upgradeFirmwareStep3(){
        mUpgradeStep=NOEMAL_STATUS;
        //开始启动升级
        byte[] bytes=new byte[4];
        bytes[0]=HEADER;
        bytes[1]=UPGRADE_SUCCESS;
        int crc=CRC16.calcCrc16(bytes,2);
        bytes[2]= (byte) ((crc&0xff00)/256);
        bytes[3]= (byte) (crc&0x00ff);
        boolean result=cmdWrite(bytes);
        if(!result){
            return false;
        }
        int count=MAX_CMD_WAIT_TIME/100;//收到成功标注或者超时返回
        while (true){
            if(mUpgradeStep==UPGRADE_SUCCESS){
                return true;
            }
            try {
                Thread.sleep(100);
                count--;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(count<=0){
                return false;
            }
        }

    }

    /***
     * 检测升级状态
     * @return false 不在升级状态，true升级中
     */
    private boolean checkUpgradeStep(int[] data,int size){

        if(data==null||size!=4){
         //   Log.e(TAG,"checkUpgradeStep1111");
            return false;
        }
        if(((byte)data[0])!=HEADER){
            Log.e(TAG,"checkUpgradeStep1122");
            return false;
        }
        if (data[1]!=UPGRADE_START&&data[1]!=UPGRADE_DATA_SEND_SUCCESS&&data[1]!=UPGRADE_DATA_SEND_FAIL&&data[1]!=UPGRADE_SUCCESS){
            Log.e(TAG,"checkUpgradeStep1133");
            return false;
        }
        int receiveRCR=data[2]*256+data[3];
        byte[] bytes=new byte[2];
        for(int i=0;i<bytes.length;i++){
            bytes[i]= (byte) (data[i]&0xff);
        }
        int crc=CRC16.calcCrc16(bytes,2);
        if(crc!=receiveRCR){
            Log.e(TAG,"checkUpgradeStep crc fail!");
            return false;
        }
//        Log.e(TAG,"checkUpgradeStep mUpgradeStep ="+mUpgradeStep);
        mUpgradeStep=bytes[1];
        return true;
    }


    /***
     * 写控制命令
     * @param dataSendInfo 冰箱发送数据
     */
    public boolean cmdWrite(DataSendInfo dataSendInfo){
        if(dataSendInfo==null){
            Log.e(TAG,"cmdWrite null");
            return false;
        }
        if(mSerialPort.no_serial_lib) {
            return false;
        }
        if(mSerialPort==null){
            open(DeviceConfig.MODEL);
            if(mSerialPort.no_serial_lib) {
                return false;
            }
        }
        mIsWriting=true;
        int[] data=new int[DataSendInfo.BYTE_COUNT];
        boolean result=SerialSendParser.parser(dataSendInfo,data);
        if(!result||data==null||data.length==0){
            Log.e(TAG,"cmdWrite params parser fail!");
            mIsWriting=false;
            return false;
        }
        mSerialPort.wr_data=data;
        log.d(TAG,"cmdWrite,data="+ DataProcessUtil.bytesToHexString(data));
        int leng=mSerialPort.serial_write(mSerialPort,mSerialPort.wr_data.length);
        mIsWriting=false;
        if(leng!=data.length){
            Log.e(TAG,"cmdWrite length fail!leng="+leng);
            return false;
        }
        return true;
    }

    /***
     * 写控制命令
     * @param deviceParamsSet 冰箱发送数据
     */
    public boolean cmdWrite(DeviceParamsSet deviceParamsSet){
        if(deviceParamsSet==null){
            Log.e(TAG,"cmdWrite null");
            return false;
        }
        if(mSerialPort.no_serial_lib) {
            return false;
        }
        if(mSerialPort==null){
            open(DeviceConfig.MODEL);
            if(mSerialPort.no_serial_lib) {
                return false;
            }
        }
        mIsWriting=true;
        int length=19;
        if(DeviceConfig.MODEL.equals(AppConfig.VIOMI_FRIDGE_V2)){
            length= DeviceParamsSet.V2_BYTE_COUNT;
        }else  if(DeviceConfig.MODEL.equals(AppConfig.VIOMI_FRIDGE_V1)|| DeviceConfig.MODEL.equals(AppConfig.VIOMI_FRIDGE_V4)){
            length= DeviceParamsSet.V1_BYTE_COUNT;
        }else  if(DeviceConfig.MODEL.equals(AppConfig.VIOMI_FRIDGE_V3)|| DeviceConfig.MODEL.equals(AppConfig.VIOMI_FRIDGE_V31)){
            length= DeviceParamsSet.V3_BYTE_COUNT;
        }
//        if(length<32){
//            length=32;
//        }
        int[] data=new int[length];
        boolean result=SerialSendParser.parser(deviceParamsSet,data);
        if(!result||data==null||data.length==0){
            Log.e(TAG,"cmdWrite params parser fail!");
            mIsWriting=false;
            return false;
        }
        mSerialPort.wr_data=data;
        log.d(TAG,"cmdWrite,data="+ DataProcessUtil.bytesToHexString(data));
        int leng=mSerialPort.serial_write(mSerialPort,mSerialPort.wr_data.length);
        mIsWriting=false;
        if(leng!=data.length){
            Log.e(TAG,"cmdWrite length fail!leng="+leng);
            return false;
        }
        return true;
    }

    /***
     * 写查询命令，v3专用
     * @param deviceParamsSet 冰箱发送数据
     */
    public boolean cmdCheckWrite(DeviceParamsSet deviceParamsSet){
        if(deviceParamsSet==null){
            Log.e(TAG,"cmdWrite null");
            return false;
        }
        if(mSerialPort.no_serial_lib) {
            return false;
        }
        if(mSerialPort==null){
            open(DeviceConfig.MODEL);
            if(mSerialPort.no_serial_lib) {
                return false;
            }
        }
        mIsWriting=true;
        int  length= DeviceParamsSet.V3_BYTE_COUNT;

        int[] data=new int[length];
        boolean result= SerialSendParser.parserCheckData(deviceParamsSet,data);
        if(!result||data==null||data.length==0){
            Log.e(TAG,"cmdWrite params parser fail!");
            mIsWriting=false;
            return false;
        }
        mSerialPort.wr_data=data;
        log.d(TAG,"cmdWrite,data="+ DataProcessUtil.bytesToHexString(data));
        int leng=mSerialPort.serial_write(mSerialPort,mSerialPort.wr_data.length);
        mIsWriting=false;
        if(leng!=data.length){
            Log.e(TAG,"cmdWrite length fail!leng="+leng);
            return false;
        }
        return true;
    }


    /***
     * 写控制命令
     * @param deviceParamsSet 冰箱发送数据
     * @param isSave 是否保存到flash，重启恢复
     * @param callback 回调
     */
    public boolean cmdWrite(DeviceParamsSet deviceParamsSet, final boolean isSave, AppCallback<String> callback){
        hasDataReceive=false;
        log.d(TAG,"@@@@9999="+deviceParamsSet.toString());
        mCMDWriteCallback=callback;
        if(deviceParamsSet==null){
            Log.e(TAG,"cmdWrite null");
            if(mCMDWriteCallback!=null){
                mCMDWriteCallback.onFail(-1,"deviceParamsSet null");
            }
            return false;
        }
        if(mSerialPort.no_serial_lib) {
            if(mCMDWriteCallback!=null) {
                mCMDWriteCallback.onFail(-1, "no_serial_lib");
            }
            return false;
        }
        if(mSerialPort==null){
            open(DeviceConfig.MODEL);
            if(mSerialPort.no_serial_lib) {
                if(mCMDWriteCallback!=null){
                    mCMDWriteCallback.onFail(-1,"no_serial_lib");
                }
                return false;
            }
        }
        mIsWriting=true;
        int length=19;
        if(DeviceConfig.MODEL.equals(AppConfig.VIOMI_FRIDGE_V2)){
            length= DeviceParamsSet.V2_BYTE_COUNT;
        }else if(DeviceConfig.MODEL.equals(AppConfig.VIOMI_FRIDGE_V1)|| DeviceConfig.MODEL.equals(AppConfig.VIOMI_FRIDGE_V4)){
            length= DeviceParamsSet.V1_BYTE_COUNT;
        }else if(DeviceConfig.MODEL.equals(AppConfig.VIOMI_FRIDGE_V3)|| DeviceConfig.MODEL.equals(AppConfig.VIOMI_FRIDGE_V31)){
            length= DeviceParamsSet.V3_BYTE_COUNT;
        }
        int[] data=new int[length];
        boolean result=SerialSendParser.parser(deviceParamsSet,data);
        if(!result||data==null||data.length==0){
            Log.e(TAG,"cmdWrite params parser fail!");
            if(mCMDWriteCallback!=null) {
                mCMDWriteCallback.onFail(-1, "params parser fail!");
            }
            mIsWriting=false;
            return false;
        }
        mSerialPort.wr_data=data;
        log.d(TAG,"cmdWrite,data="+ DataProcessUtil.bytesToHexString(data));
        stopTimer();

        int leng=mSerialPort.serial_write(mSerialPort,mSerialPort.wr_data.length);
        if(leng!=data.length){
            Log.e(TAG,"cmdWrite length fail!leng="+leng);
            if(mCMDWriteCallback!=null) {
                mCMDWriteCallback.onFail(-1, "cmdWrite length fail!leng=" + leng);
            }
            mIsWriting=false;
            return false;
        }

        mTimer=new Timer();
        mTimeTask=new TimerTask() {
            @Override
            public void run() {
                mWriteCount++;
                boolean reRecover=false;
                if(mWriteCount>=3){
                    reRecover=true;
                }
                if(verifyCMDResult(reRecover)){//设置成功，保存数据返回，停止计时
                    mIsWriting=false;
                    if(isSave){
                        DeviceParamsManager.saveDeviceParams(MyApplication.getContext(),mDeviceParamsSet);
                    }
                    Log.d(TAG,"mCMDWriteCallback.onSuccess");
                    stopTimer();
                    if(mCMDWriteCallback!=null) {
                        mCMDWriteCallback.onSuccess(null);
                    }

                    if(DeviceConfig.MODEL.equals(AppConfig.VIOMI_FRIDGE_V2)){//四门垃圾协议，发送成功后还要不停的发命令才可以真正设置成功
                        stopTimer();
                        mTimer=new Timer();
                        mTimeTask=new TimerTask() {
                            @Override
                            public void run() {
                                cmdWrite(mDeviceParamsSet);
                            }
                        };
                        mTimer.schedule(mTimeTask,1000,1000);
                    }else  if(DeviceConfig.MODEL.equals(AppConfig.VIOMI_FRIDGE_V3)|| DeviceConfig.MODEL.equals(AppConfig.VIOMI_FRIDGE_V31)){//v3协议，不断查询主板状态
                        stopTimer();
                        mTimer=new Timer();
                        mTimeTask=new TimerTask() {
                            @Override
                            public void run() {
                                cmdCheckWrite(mDeviceParamsSet);
                            }
                        };
                        mTimer.schedule(mTimeTask,1000,1000);
                    }
                }else {//设置失败
                    Log.e(TAG,"mCMDWriteCallback.fail，time="+mWriteCount);
                    if(mWriteCount>=3){//连续五次失败，返回错误
                        mIsWriting=false;
                        stopTimer();
                        if(mCMDWriteCallback!=null) {
                            mCMDWriteCallback.onFail(-1, "verifyCMDResult fail!");
                        }
                    }else {//设置失败，重试
                        cmdWrite(mDeviceParamsSet);
                    }
                }

            }
        };
        mWriteCount=0;
        mTimer.schedule(mTimeTask,1500,1500);

        return true;
    }

    /***
     * 校验设置结果
     * @param isRecover 失败是否恢复数据，五次判断，最后一次恢复
     * @return
     */
    private boolean verifyCMDResult(boolean isRecover){
        if(DeviceConfig.MODEL.equals(AppConfig.VIOMI_FRIDGE_V2)){
            if(!hasDataReceive){//V2接收数据即代表成功
                if(isRecover){
                    log.d(TAG,"V2 reSet send data 1");
                    copyDeviceParamsSet(mDeviceParamsSet,mDeviceParamsSetTemp);//不成功，恢复原来
                }else {
                    Log.e(TAG,"set,fail,retry!");
                }
            }
            return hasDataReceive;
        }else {
            DeviceParamsSet deviceParamsSet=new DeviceParamsSet();
            deviceParamsSet.cold_closet_temp_set=mDeviceParamsGet.cold_closet_temp_set;
            deviceParamsSet.temp_changeable_room_temp_set=mDeviceParamsGet.temp_changeable_room_temp_set;
            deviceParamsSet.freezing_room_temp_set=mDeviceParamsGet.freezing_room_temp_set;
            deviceParamsSet.mode=mDeviceParamsGet.mode;
            deviceParamsSet.cold_closet_room_enable=mDeviceParamsGet.cold_closet_room_enable;
            deviceParamsSet.temp_changeable_room_room_enable=mDeviceParamsGet.temp_changeable_room_room_enable;
            deviceParamsSet.clean=mDeviceParamsGet.clean;
            deviceParamsSet.iced_drink=mDeviceParamsGet.iced_drink;

            log.d(TAG,"@@@="+(mDeviceParamsSet.toString()+",\n###="+deviceParamsSet.toString()));
            if(deviceParamsSet.toString().equals(mDeviceParamsSet.toString())){
                return true;
            }else {//设置不成功，回复为实际
                if(GlobalParams.LOG_DEBUG){
                    DeviceManager.getInstance().sendSetFail(mDeviceParamsSet.toShortString(),deviceParamsSet.toShortString());
                }
                if(isRecover){
                    Log.e(TAG,"set,fail,restore get!");
                    mDeviceParamsSet.cold_closet_temp_set=mDeviceParamsGet.cold_closet_temp_set;
                    mDeviceParamsSet.temp_changeable_room_temp_set=mDeviceParamsGet.temp_changeable_room_temp_set;
                    mDeviceParamsSet.freezing_room_temp_set=mDeviceParamsGet.freezing_room_temp_set;
                    mDeviceParamsSet.mode=mDeviceParamsGet.mode;
                    mDeviceParamsSet.cold_closet_room_enable=mDeviceParamsGet.cold_closet_room_enable;
                    mDeviceParamsSet.temp_changeable_room_room_enable=mDeviceParamsGet.temp_changeable_room_room_enable;
                    mDeviceParamsSet.clean=mDeviceParamsGet.clean;
                    mDeviceParamsSet.iced_drink=mDeviceParamsGet.iced_drink;
                    if(DeviceConfig.MODEL.equals(AppConfig.VIOMI_FRIDGE_V3)|| DeviceConfig.MODEL.equals(AppConfig.VIOMI_FRIDGE_V31)){
                        mDeviceParamsSet.quick_cold=mDeviceParamsGet.quick_cold;
                        mDeviceParamsSet.quick_freeze=mDeviceParamsGet.quick_freeze;
                    }
                }else {
                    Log.e(TAG,"set,fail,retry!");
                }
                return false;
            }

        }

    }

    private void copyDeviceParamsSet(DeviceParamsSet dest, DeviceParamsSet src){
        if(dest==null||src==null){
            return;
        }
        dest.mode=src.mode;
        dest.cold_closet_temp_set=src.cold_closet_temp_set;
        dest.temp_changeable_room_temp_set=src.temp_changeable_room_temp_set;
        dest.freezing_room_temp_set=src.freezing_room_temp_set;
        dest.cold_closet_room_enable=src.cold_closet_room_enable;
        dest.temp_changeable_room_room_enable=src.temp_changeable_room_room_enable;
        dest.clean=src.clean;
        dest.RCF_forced=src.RCF_forced;
        dest.cc_forced_frost=src.cc_forced_frost;
        dest.fz_forced_frost=src.fz_forced_frost;
        dest.commodity_inspection=src.commodity_inspection;
        dest.time_cut=src.time_cut;
        dest.quick_cold=src.quick_cold;
        dest.quick_freeze=src.quick_freeze;
    }

    private void copyDeviceParamset(DeviceParamsGet dest, DeviceParamsGet src){
        if(dest==null||src==null){
            return;
        }
        dest.mode=src.mode;
        dest.cold_closet_temp_set=src.cold_closet_temp_set;
        dest.temp_changeable_room_temp_set=src.temp_changeable_room_temp_set;
        dest.freezing_room_temp_set=src.freezing_room_temp_set;
        dest.cold_closet_room_enable=src.cold_closet_room_enable;
        dest.temp_changeable_room_room_enable=src.temp_changeable_room_room_enable;
        dest.cold_closet_temp_real=src.cold_closet_temp_real;
        dest.temp_changeable_room_temp_real=src.temp_changeable_room_temp_real;
        dest.freezing_room_temp_real=src.freezing_room_temp_real;
        dest.cc_evaporator_temp=src.cc_evaporator_temp;
        dest.fz_evaporator_temp=src.fz_evaporator_temp;
        dest.clean=src.clean;
        dest.RCF_forced=src.RCF_forced;
        dest.cc_forced_frost=src.cc_forced_frost;
        dest.fz_forced_frost=src.fz_forced_frost;
        dest.commodity_inspection=src.commodity_inspection;
        dest.door_ccroom_open_alarm=src.door_ccroom_open_alarm;
        dest.door_tcroom_open_alarm=src.door_tcroom_open_alarm;
        dest.door_fzroom_open_alarm=src.door_fzroom_open_alarm;
        dest.indoor_temp=src.indoor_temp;
        dest.humidity=src.humidity;
        dest.version=src.version;
        dest.model=src.model;
        dest.error=src.error;
        dest.crc=src.crc;
        dest.quick_cold=src.quick_cold;
        dest.quick_freeze=src.quick_freeze;
    }

    private void copyDeviceParamsGet(DeviceParamsGet dest, DeviceParamsGet src){
        if(dest==null||src==null){
            return;
        }
        dest.mode=src.mode;
        dest.cold_closet_temp_set=src.cold_closet_temp_set;
        dest.temp_changeable_room_temp_set=src.temp_changeable_room_temp_set;
        dest.freezing_room_temp_set=src.freezing_room_temp_set;
        dest.cold_closet_room_enable=src.cold_closet_room_enable;
        dest.temp_changeable_room_room_enable=src.temp_changeable_room_room_enable;
        dest.cold_closet_temp_real=src.cold_closet_temp_real;
        dest.temp_changeable_room_temp_real=src.temp_changeable_room_temp_real;
        dest.freezing_room_temp_real=src.freezing_room_temp_real;
        dest.cc_evaporator_temp=src.cc_evaporator_temp;
        dest.fz_evaporator_temp=src.fz_evaporator_temp;
        dest.clean=src.clean;
        dest.RCF_forced=src.RCF_forced;
        dest.cc_forced_frost=src.cc_forced_frost;
        dest.fz_forced_frost=src.fz_forced_frost;
        dest.commodity_inspection=src.commodity_inspection;
        dest.door_ccroom_open_alarm=src.door_ccroom_open_alarm;
        dest.door_tcroom_open_alarm=src.door_tcroom_open_alarm;
        dest.door_fzroom_open_alarm=src.door_fzroom_open_alarm;
        dest.indoor_temp=src.indoor_temp;
        dest.humidity=src.humidity;
        dest.version=src.version;
        dest.model=src.model;
        dest.error=src.error;
        dest.crc=src.crc;
        dest.quick_cold=src.quick_cold;
        dest.quick_freeze=src.quick_freeze;
    }


    private void stopTimer(){
        if(mTimer!=null){
            mTimer.cancel();
            mTimer=null;
        }
        if(mTimeTask!=null){
            mTimeTask.cancel();
            mTimeTask=null;
        }
    }

    /***
     * 写字节数据
     * @param bytes
     */
    public boolean cmdWrite(byte[] bytes){
        if(bytes==null||bytes.length==0){
            Log.e(TAG,"cmdWrite bytes null");
            return false;
        }
        if(bytes.length>mSerialPort.MAX_WRITE_LENGTH){//溢出
            Log.e(TAG,"cmdWrite bytes overflow！");
            return false;
        }
        if(mSerialPort.no_serial_lib) {
            return false;
        }
        mIsWriting=true;
        mSerialPort.wr_data=new int[bytes.length];
        for(int i=0;i<bytes.length;i++){
            mSerialPort.wr_data[i]=bytes[i];
        }
       if(bytes.length<256){
            log.d(TAG,"cmdWrite bytes,data="+ DataProcessUtil.bytesToHexString(bytes));
       }
        int leng=mSerialPort.serial_write(mSerialPort,bytes.length);
        mIsWriting=false;
        if(leng!=bytes.length){
            Log.e(TAG,"cmdWrite bytes length fail!leng="+leng);
            return false;
        }
        return true;
    }

    /***
     * 返回上一次获取的串口数据信息
     * @return
     */
    public String getDataReceiveStr(){
        return ""+mTimeReceive+":"+ DataProcessUtil.bytesToHexString(mDataReceive);
    }

    class ReadThread extends Thread{
        @Override
        public  void run() {
            super.run();

            while (!isInterrupted()){
                try {
                    Thread.sleep(500);
                    int size=mSerialPort.serial_read(mSerialPort);
                    onDataReceive(mSerialPort.rd_data,size);
                }catch (Exception e){
                    Log.e(TAG,"serial read fail!msg: "+e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }
}
