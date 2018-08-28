package com.mode.fridge.manager;

import com.mode.fridge.bean.SerialInfo;
import com.mode.fridge.common.GlobalParams;
import com.mode.fridge.device.AppConfig;
import com.mode.fridge.device.DeviceConfig;
import com.mode.fridge.utils.RandomUtil;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 冰箱提示轮询管理
 * Created by young2 on 2017/3/6.
 */

public class FridgeTipsManager {

    private static FridgeTipsManager INSTANCE;
    private final static String  KEY_CCROOM_TIP="KEY_CCROOM_TIP";
    private final static String  KEY_TCROOM_TIP="KEY_TCROOM_TIP";
    private final static String  KEY_FZROOM_TIP="KEY_FZROOM_TIP";
    private final static String  KEY_QUICK_COLD_TIP="KEY_QUICK_COLD_TIP";
    private final static String  KEY_QUICK_FREEZE_TIP="KEY_QUICK_FREEZE_TIP";
    private final static String  KEY_WAKEUP_TIP="KEY_WAKEUP_TIP";
    private final static String  KEY_NORMAL_TIP="KEY_NORMAL_TIP";
    private final static String  KEY_CLEAN_TIP="KEY_CLEAN_TIP";
    private final static String  KEY_SMART_MODE_TIP="KEY_SMART_MODE_TIP";
    private final static String  KEY_HOLIDAY_MODE_TIP="KEY_HOLIDAY_MODE_TIP";
    private final static String  KEY_WEATHER_MODE_TIP="KEY_WEATHER_MODE_TIP";

    private final static String  VALUE_CCROOM_TIP="温馨提醒：冷藏室可以为我们的新鲜食物冻龄哦,比如放置新鲜的水果和蔬菜";
    private final static String  VALUE_TCROOM_TIP="温馨提醒：您可以根据放入的食材选择系统推荐温度，也可以自定义设置变温室的温度";
    private final static String  VALUE_FZROOM_TIP="温馨提示：冷冻室和冷藏室一样，也必须生熟分开，荤素分开。而且所有食品必须独立分装，避免相互污染哦";
    private final static String  VALUE_QUICK_COLD_TIP="主人您好，冷藏室进入速冷模式了哦。感觉自己棒棒哒！速冷进行中..";
    private final static String   VALUE_QUICK_FREEZE_TIP="冷冻室现在进入速冻模式啦，有木有感觉到丝丝寒冷！速冷进行中...";
    private final static String  VALUE_WAKEUP_TIP="主人您好，你可以语音呼叫“小鲜小鲜”、“云米冰箱”或“冰箱冰箱”唤醒我哦~快来试试吧！";
    private final static String  VALUE_NORMAL_TIP="小鲜永远当您的健康小卫士，记得常来看看我哦~";
    private final static String VALUE_CLEAN_TIP="什么都不怕，一键净化来帮您！正在净化中...";
    private final static String  VALUE_SMART_MODE_TIP="智能模式中，全面为您的食材与健康保驾护航！";
    private final static String  VALUE_HOLIDAY_MODE_TIP="假日模式已开启，长假出游小鲜帮您轻松搞定";

    private HashMap<String,String> mHashMap=new HashMap<>();
    private ArrayList<String> mList=new ArrayList<>();

    public static FridgeTipsManager getInstance(){
        synchronized (FridgeTipsManager.class){
            if(INSTANCE==null){
                synchronized (FridgeTipsManager.class){
                    if(INSTANCE==null){
                        INSTANCE=new FridgeTipsManager();
                    }
                }
            }
        }
        return INSTANCE;
    }

    public FridgeTipsManager(){
        mHashMap.put(KEY_CCROOM_TIP,VALUE_CCROOM_TIP);
        if((!AppConfig.VIOMI_FRIDGE_V3.equals(DeviceConfig.MODEL))&&(!AppConfig.VIOMI_FRIDGE_V31.equals(DeviceConfig.MODEL))
                &&(!AppConfig.VIOMI_FRIDGE_V31.equals(DeviceConfig.MODEL))
                &&(!AppConfig.VIOMI_FRIDGE_V4.equals(DeviceConfig.MODEL))){
            mHashMap.put(KEY_TCROOM_TIP,VALUE_TCROOM_TIP);
        }
        mHashMap.put(KEY_FZROOM_TIP,VALUE_FZROOM_TIP);

        mHashMap.put(KEY_WAKEUP_TIP,VALUE_WAKEUP_TIP);
        mHashMap.put(KEY_NORMAL_TIP,VALUE_NORMAL_TIP);
        mHashMap.put(KEY_WAKEUP_TIP,VALUE_WAKEUP_TIP);
        mHashMap.put(KEY_NORMAL_TIP,VALUE_NORMAL_TIP);
    }

    /***
     *更新状态
     */
    private void updateTips(){
        int mode=ControlManager.getInstance().getDataReceiveInfo().mode;
        if(mode== SerialInfo.MODE_SMART){
            mHashMap.put(KEY_SMART_MODE_TIP,VALUE_SMART_MODE_TIP);
            if(mHashMap.containsKey(KEY_HOLIDAY_MODE_TIP)){
                mHashMap.remove(KEY_HOLIDAY_MODE_TIP);
            }
        }else if(mode== SerialInfo.MODE_SMART){
            mHashMap.put(KEY_HOLIDAY_MODE_TIP,VALUE_HOLIDAY_MODE_TIP);
            if(mHashMap.containsKey(KEY_SMART_MODE_TIP)){
                mHashMap.remove(KEY_SMART_MODE_TIP);
            }
        }else  if(ControlManager.getInstance().getDataReceiveInfo().quick_cold){
            if(mHashMap.containsKey(KEY_HOLIDAY_MODE_TIP)){
                mHashMap.remove(KEY_HOLIDAY_MODE_TIP);
            }
            if(mHashMap.containsKey(KEY_SMART_MODE_TIP)){
                mHashMap.remove(KEY_SMART_MODE_TIP);
            }
            mHashMap.put(KEY_QUICK_COLD_TIP,VALUE_QUICK_COLD_TIP);
        }else if(ControlManager.getInstance().getDataReceiveInfo().quick_freeze){
            if(mHashMap.containsKey(KEY_HOLIDAY_MODE_TIP)){
                mHashMap.remove(KEY_HOLIDAY_MODE_TIP);
            }
            if(mHashMap.containsKey(KEY_SMART_MODE_TIP)){
                mHashMap.remove(KEY_SMART_MODE_TIP);
            }
            mHashMap.put(KEY_QUICK_FREEZE_TIP,VALUE_QUICK_FREEZE_TIP);
        }else {
            if(mHashMap.containsKey(KEY_HOLIDAY_MODE_TIP)){
                mHashMap.remove(KEY_HOLIDAY_MODE_TIP);
            }
            if(mHashMap.containsKey(KEY_SMART_MODE_TIP)){
                mHashMap.remove(KEY_SMART_MODE_TIP);
            }
        }
        if(ControlManager.getInstance().isOneKeyCleanRunning()){
            mHashMap.put(KEY_CLEAN_TIP,VALUE_CLEAN_TIP);
        }else {
            if(mHashMap.containsKey(KEY_CLEAN_TIP)){
                mHashMap.remove(KEY_CLEAN_TIP);
            }
        }
        String report=GlobalParams.getInstance().getWeatherReport();
        if(!report.equals(GlobalParams.Value_WeatherReport)){
                mHashMap.put(KEY_WEATHER_MODE_TIP,report);
            }else {
                if(mHashMap.containsKey(KEY_WEATHER_MODE_TIP)){
                    mHashMap.remove(KEY_WEATHER_MODE_TIP);
                }
            }
    }

    /***
     * 获取随机tips
     * @return
     */
    public String getTipString(){
        updateTips();
        int count=mHashMap.size();

        int index = RandomUtil.nextInt(0,count-1);
        mList.clear();
        for(String key : mHashMap.keySet()){
            mList.add(mHashMap.get(key));
        }
       return mList.get(index);
    }


}
