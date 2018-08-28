package com.mode.fridge.manager;

import android.util.Log;

import com.mode.fridge.bean.DeviceParamsGet;
import com.mode.fridge.bean.DeviceParamsSet;
import com.mode.fridge.bean.FridgeTime;
import com.mode.fridge.bean.SerialInfo;
import com.mode.fridge.bean.TCRoomScene;
import com.mode.fridge.common.GlobalParams;
import com.mode.fridge.device.AppConfig;
import com.mode.fridge.device.DeviceConfig;
import com.mode.fridge.utils.log;
import com.viomi.common.callback.AppCallback;
import com.viomi.common.callback.ProgressCallback;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 冰箱计时控制功能,商检/一键净化/智能模式
 * Created by young2 on 2017/1/7.
 */

public class ControlManager {
    private static final String TAG=ControlManager.class.getSimpleName();
    private static ControlManager INSTANCE;
    public static int FILTER_LIFE_SUM_TIME=8640;//滤芯的基准寿命，单位小时
    private boolean mCommodityInspection;//商检标志
    private boolean mOneKeyClean;//一键净化标志
    private boolean mSmartMode;//智能模式
    private int mCommodityInspectionTime;//商检计时 单位分钟
    private int mOneKeyCleanTime;//一键净化计时 单位分钟
    private int mSmartModeTime;//智能模式计时 单位分钟

    private int mQuickColdTime;//速冷计时 单位分钟,v4
    private int mQuickFreezeTime;//速冻计时 单位分钟 ,v4
    private boolean mQuickColdTimeStart;//速冷计时启动
    private boolean mQuickFreezeTimeStart;//速冻计时启动

    private TimerThread mTimerThread=null;
    private boolean isFristSmartMode=false;

//    private DataReceiveInfo mDataReceiveInfo=SerialManager.getInstance().getDataReceiveInfo();

    private TimerTask mTimerTask;
    private Timer mTimer;

    public final static int Step_Commodity_Inspection_0=0;//商检步骤
    public final static int Step_Commodity_Inspection_1=1;
    public final static int Step_Commodity_Inspection_2=2;
    public final static int Step_Commodity_Inspection_end=3;

    public static ControlManager getInstance(){
        synchronized (ControlManager.class){
            if(INSTANCE==null){
                synchronized (ControlManager.class){
                    if(INSTANCE==null){
                        INSTANCE=new ControlManager();
                    }
                }
            }
        }
        return INSTANCE;
    }

    public void init(){
        if(mTimerThread==null){
            mTimerThread =new TimerThread();
            mTimerThread.start();
        }
    }

    class TimerThread extends Thread{
        @Override
        public  void run() {
            super.run();

            while (!isInterrupted()){
                try {
                    Thread.sleep(1000*60/getCutTimeMult());
                    if(mCommodityInspection){
                        mCommodityInspectionTime++;

                        if(AppConfig.VIOMI_FRIDGE_V1.equals(DeviceConfig.MODEL)){
                            if(mCommodityInspectionTime<20){
                                SerialManager.getInstance().commodityInspection(Step_Commodity_Inspection_0);
                            }else  if(mCommodityInspectionTime<40){
                                SerialManager.getInstance().commodityInspection(Step_Commodity_Inspection_1);
                            }else  if(mCommodityInspectionTime<45){
                                SerialManager.getInstance().commodityInspection(Step_Commodity_Inspection_2);
                            }else {
                                log.d(TAG,"commodityInspection end!");
                                stopCommodityInspection();
                                Thread.sleep(1000*5);

                                enableForcedFrost(true);
//                            //商检结束，5分钟后启动强制化霜
//                            stopTimer();
//                            mTimer=new Timer();
//                            mTimerTask=new TimerTask() {
//                                @Override
//                                public void run() {
//                                    stopTimer();
//                                    enableForcedFrost(true);
//                                }
//                            };
//                            mTimer.schedule(mTimerTask,1000*60*5/getCutTimeMult());
                            }
                        }   else   if(AppConfig.VIOMI_FRIDGE_V4.equals(DeviceConfig.MODEL)){
                            if(mCommodityInspectionTime<30){
                                SerialManager.getInstance().commodityInspection(Step_Commodity_Inspection_0);
                            }else  if(mCommodityInspectionTime<45){
                                SerialManager.getInstance().commodityInspection(Step_Commodity_Inspection_1);
                            }else {
                                log.d(TAG,"commodityInspection end!");
                                stopCommodityInspection();

                                Thread.sleep(1000*5);
                                enableForcedFrost(true);    //商检结束，5分钟后启动强制化霜
                            }
                        }
                    }
                    if(mOneKeyClean){
                        mOneKeyCleanTime++;
                        if(mOneKeyCleanTime>60){
                            mOneKeyClean=false;
                            SerialManager.getInstance().oneKeyClean(false,null);
                        }
                    }
                    if(mSmartMode){
                        if(SerialManager.getInstance().getDataReceiveInfo().mode== SerialInfo.MODE_SMART){
                            mSmartModeTime++;
                         //   if(mSmartModeTime%20==0){
                            if(isFristSmartMode){
                                SerialManager.getInstance().enableSmartMode(true,true);
                            }else {
                                SerialManager.getInstance().enableSmartMode(true);
                            }
                          //  }
                        }else {
                            enableSmartMode(false);
                        }
                    }

                    if (AppConfig.VIOMI_FRIDGE_V4.equals(DeviceConfig.MODEL)){
                        if(mQuickColdTimeStart){//速冷计时到
                            mQuickColdTime++;
                            if(mQuickColdTime>=4*60){
                                mQuickColdTime=0;
                                mQuickColdTimeStart=false;
                                if(SerialManager.getInstance().getDataSendInfo().quick_cold){
                                    SerialManager.getInstance().enableQuickCool(false,null);
                                }
                            }
                        }

                        if(mQuickFreezeTimeStart){//速冻计时到
                            mQuickFreezeTime++;
                            if(mQuickFreezeTime>=18*60){
                                mQuickFreezeTime=0;
                                mQuickFreezeTimeStart=false;
                                if(SerialManager.getInstance().getDataSendInfo().quick_freeze){
                                    SerialManager.getInstance().enableQuickFreeze(false,null);
                                }
                            }
                        }
                    }
                }catch (Exception e){
                    Log.e(TAG,"timer fail!msg: "+e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }

    /***
     * 开始商检
     */
    public void startCommodityInspection(){
        mCommodityInspection=true;
        mCommodityInspectionTime=0;
        SerialManager.getInstance().commodityInspection(Step_Commodity_Inspection_0);
        GlobalParams.getInstance().setCommodityInspection(true);
        GlobalParams.getInstance().setVoiceEnabe(false);
//        VoiceManager.getInstance().enableVoice(false);

    }

    /***
     * 是否商检中
     * @return
     */
    public boolean isCommodityInspectionRunning()
    {
        return mCommodityInspection;
    }

    /***
     * 商检时间计时
     */
    public int getCommodityInspectionTime(){
        return mCommodityInspectionTime;
    }

    /***
     * 停止商检
     */
    public void stopCommodityInspection(){
        mCommodityInspection=false;
        SerialManager.getInstance().commodityInspection(Step_Commodity_Inspection_end);
        GlobalParams.getInstance().setCommodityInspection(false);
        GlobalParams.getInstance().setVoiceEnabe(true);
//        VoiceManager.getInstance().enableVoice(true);
    }



    /***
     * 是否一键净化中
     * @return
     */
    public boolean isOneKeyCleanRunning()
    {
        return mOneKeyClean&& SerialManager.getInstance().getDataSendInfo().clean;
    }

    /***
     * 一键净化计时
     */
    public int getOneKeyCleanTime(){
        return mOneKeyCleanTime;
    }

    /***
     * 使能一键净化
     */
    public boolean enableOneKeyClean(boolean enable){
        boolean result= SerialManager.getInstance().oneKeyClean(enable,null);
        if(enable){//开启
            if(result){//成功
                mOneKeyClean=true;
                mOneKeyCleanTime=0;
            }else {//失败
                mOneKeyClean=false;
            }
        }else {//关闭
            if(result){//成功
                mOneKeyClean=false;
            }else {//失败
                mOneKeyClean=true;
            }
        }
        return result;
    }


    /***
     * 使能智能模式，20分钟根据环境温度设置一次
     * @param enable
     */
    public boolean enableSmartMode(boolean enable){
        if(enable){
            isFristSmartMode=false;
            mSmartModeTime=0;
            mSmartMode=true;
           return SerialManager.getInstance().enableSmartMode(true);
        }else {
            mSmartModeTime=0;
            mSmartMode=false;
            return true;
        }
    }

    /***
     * 使能智能模式，20分钟根据环境温度设置一次
     * @param enable
     */
    public boolean enableSmartMode(boolean enable,boolean isFrist){
        if(enable){
            isFristSmartMode=true;
            mSmartModeTime=0;
            mSmartMode=true;
            return SerialManager.getInstance().enableSmartMode(true,isFrist);
        }else {
            mSmartModeTime=0;
            mSmartMode=false;
            return true;
        }
    }


    /***
     * 启动假日模式
     * @param enable
     * @return
     */
    public boolean enableHolidayMode(boolean enable,AppCallback<String> callback){
        if(enable){
            enableSmartMode(false);
        }
        return SerialManager.getInstance().enableHolidayMode(enable,callback);
    }

    /***
     * 设置各室温度
     * @param temp
     * @param roomType {@link SerialInfo.RoomType}.
     * @return
     */
    public boolean setRoomTemp(int temp,int roomType,AppCallback<String> callback) {
        enableSmartMode(false);
        return SerialManager.getInstance().setRoomTemp(temp,roomType,callback);
    }

    /***
     * 设置变温室场景
     * @param name
     * @param callback
     * @return
     */
    public boolean setTCRoomScene(String name,AppCallback<String> callback){
        int temp=100;
        ArrayList<TCRoomScene> list= RoomSceneManager.getInstance().getSceneAllList();
        for(int i=0;i<list.size();i++){
            if(list.get(i).name.equals(name)){
                temp=list.get(i).value;
                break;
            }
        }
        if(temp==100){
            return false;
        }
        enableSmartMode(false);
        return SerialManager.getInstance().setRoomTemp(temp, SerialInfo.ROOM_CHANGEABLE_ROOM,callback);
    }

    /***
     * 打开或关闭冷藏室/变温室
     * @param enable
     * @param roomType {@link SerialInfo.RoomType}.
     * @return
     */
    public boolean enableRoom(boolean enable,int roomType,AppCallback<String> callback){
        enableSmartMode(false);
        return SerialManager.getInstance().enableRoom(enable,roomType,callback);
    }

    /***
     * 获取冰箱设置数据
     * @return
     */
    public DeviceParamsSet getDataSendInfo(){
        return SerialManager.getInstance().getDataSendInfo();
    }

    /***
     * 获取冰箱状态数据
     * @return
     */
    public DeviceParamsGet getDataReceiveInfo(){
        return SerialManager.getInstance().getDataReceiveInfo();
    }


    /***
     * 获取滤芯消耗百分比
     * @return
     */
    public int getFilterLifeUsePercent(){
        int time=getFilterLifeUsedTime();
        if(time>FILTER_LIFE_SUM_TIME){
            time=FILTER_LIFE_SUM_TIME;
        }
       return (int) (time*1.0/FILTER_LIFE_SUM_TIME*100+0.5);
    }

    /***
     * 获取滤芯已用寿命，单位小时
     * @return
     */
    public int getFilterLifeUsedTime(){
        return GlobalParams.getInstance().getFilterLifeTime();
    }

    /***
     * 设置滤芯已用寿命，单位小时
     * @return
     */
    public void setFilterLifeUsedTime(int usedHour){
        if(usedHour<0){
            usedHour=0;
        }
        GlobalParams.getInstance().setFilterLifeTime(usedHour);
    }

    /***
     * 启动强制化霜
     * @param enable
     * @return
     */
    public boolean enableForcedFrost(boolean enable){
        return SerialManager.getInstance().enableForcedFrost(enable);
    }

    /***
     * 是否强制化霜中
     * @return
     */
    public boolean isForcedFrost(){
        return SerialManager.getInstance().getDataSendInfo().fz_forced_frost;
    }


    /***
     * 翻转梁加热丝复位模式
     * @return
     */
    public boolean isRollingOverColseMode(){
        return SerialManager.getInstance().getDataSendInfo().rolling_over_close_mode;
    }

    /***
     * 翻转梁加热丝状态
     * @return
     */
    public boolean isRollingOver(){
        return SerialManager.getInstance().getDataReceiveInfo().rolling_over;
    }

    /***
     * 冰饮状态
     * @return
     */
    public boolean isIcedDrink(){
        return SerialManager.getInstance().getDataSendInfo().iced_drink;
    }

    /***
     * 启动强制不停机
     * @param enable
     * @return
     */
    public boolean enableRCFForcedStart(boolean enable){
        return SerialManager.getInstance().enableRCFForcedStart(enable);
    }



    /***
     * 开启/关闭翻转梁加热器复位模式
     * @param enable
     * @return
     */
    public boolean enableRollingOverColseMode(boolean enable,AppCallback<String> callback){
        return SerialManager.getInstance().enableRollingOverCloseMode(enable,callback);
    }

    /***
     * 开启/关闭冰饮
     * @param enable
     * @return
     */
    public boolean enableIcedDrink(boolean enable,AppCallback<String> callback){
        return SerialManager.getInstance().enableIcedDrink(enable,callback);
    }

    /***
     * 启动缩时
     * @param enable
     * @return
     */
    public boolean enableTimeCut(boolean enable){
        return SerialManager.getInstance().enableTimeCut(enable);
    }

    /***
     * 是否强制不停机中
     * @return
     */
    public boolean isRCFForcedStart(){
        return SerialManager.getInstance().getDataSendInfo().RCF_forced;
    }


    /***
     * 启动速冻
     * @param enable
     * @return
     */
    public boolean enableQuickFreeze(boolean enable,AppCallback<String> callback){
        boolean result= SerialManager.getInstance().enableQuickFreeze(enable,callback);
        if (result&& AppConfig.VIOMI_FRIDGE_V4.equals(DeviceConfig.MODEL)){
            mQuickFreezeTimeStart=true;//速冻计时启动，18小时后取
            mQuickFreezeTime=0;
        }
        return result;
    }

    /***
     * 是否在速冻中
     * @return
     */
    public boolean isQuickFreezing(){
        if(SerialManager.getInstance().getDataSendInfo().quick_freeze){
            return true;
        }else {
            return false;
        }

    }

    /***
     * 启动速冷
     * @param enable
     * @return
     */
    public boolean enableQuickCool(boolean enable,AppCallback<String> callback){
        boolean result= SerialManager.getInstance().enableQuickCool(enable,callback);
        if (result&& AppConfig.VIOMI_FRIDGE_V4.equals(DeviceConfig.MODEL)){
            mQuickColdTimeStart=true;//速冷计时启动，4小时后取消
            mQuickColdTime=0;
        }
        return result;
    }

    /***
     * 是否在速冷中
     * @return
     */
    public boolean isQuickCooling(){

        if(SerialManager.getInstance().getDataSendInfo().quick_cold){
            return true;
        }else {
            return false;
        }

    }

    /***
     * 固件升级
     * @param file 文件路径
     * @return
     */
    public boolean upgradeFirmware(String file, ProgressCallback callback){
        return SerialManager.getInstance().upgradeFirmware(file,callback);
    }

    /***
     * 缩时倍数
     * @return
     */
    private int getCutTimeMult(){
        int mCurTimeMul=1;
        if(GlobalParams.getInstance().isTestCutTimeEnable()){
            mCurTimeMul= FridgeTime.TestCutTimeMult;
        }else {
            mCurTimeMul=1;
        }
        return mCurTimeMul;
    }

    private void stopTimer(){
        if(mTimer!=null){
            mTimer.cancel();
            mTimer=null;
        }
        if(mTimerTask!=null){
            mTimerTask.cancel();
            mTimerTask=null;
        }
    }


    public void close(){
        if(mTimerThread!=null){
            mTimerThread.interrupt();
            mTimerThread=null;
        }
    }

}
