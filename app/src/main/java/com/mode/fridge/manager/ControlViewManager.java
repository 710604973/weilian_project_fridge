package com.mode.fridge.manager;

import android.os.Handler;
import android.util.Log;

import com.mode.fridge.activity.FridgeActivity;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by young2 on 2017/1/4.
 */

public class ControlViewManager {
    private final static String TAG=ControlViewManager.class.getSimpleName();
    private ViewFreshThread mViewThread=null;
//    private ControlFragment mFragment;
    private FridgeActivity mAvtivity;
    private boolean mSettingFlag=false;
    private TimerTask mTimerTask;
    private Timer mTimer;

    private Handler mHandler = new Handler();

    class ViewFreshThread implements Runnable{
        @Override
        public void run() {
            try {
                if(mAvtivity!=null){
                    mAvtivity.refreshView(ControlManager.getInstance().getDataSendInfo());
                }
            }catch (Exception e){
                Log.e(TAG,"serial read fail!msg: "+e.getMessage());
                e.printStackTrace();
            }
            mHandler.postDelayed(this,500);
        }
    }


//    public void init(ControlFragment fragment){
//        mFragment=fragment;
//        mViewThread=new ViewFreshThread();
//        mHandler.postDelayed(mViewThread,500);
//    }

    public void init(FridgeActivity avtivity){
        mAvtivity=avtivity;
        mViewThread=new ViewFreshThread();
        mHandler.postDelayed(mViewThread,500);
    }

    /**
     * 是否正在设置，页面保持不动
     * @return
     */
    public boolean isSetting(){
        return mSettingFlag;
    }
    /***
     * 参数设置开始
     */
    public void paramsChange(){
        mSettingFlag=true;
        startTimer();
    }

    /***
     * 参数设置计时，延时1S后此刷新界面，防止页面跳动
     */
    private void startTimer(){
        stopTimer();
        mTimer=new Timer();
        mTimerTask=new TimerTask() {
            @Override
            public void run() {
                mSettingFlag=false;
            }
        };
        mTimer.schedule(mTimerTask,1500);
    }

    private void stopTimer(){
        if (mTimerTask!=null){
            mTimerTask.cancel();
            mTimerTask=null;
        }
        if (mTimer!=null){
            mTimer.cancel();
            mTimer=null;
        }
    }


    public void close(){

        if(mHandler!=null){
            mHandler.removeCallbacks(mViewThread);
            mHandler=null;
        }

    }
}
