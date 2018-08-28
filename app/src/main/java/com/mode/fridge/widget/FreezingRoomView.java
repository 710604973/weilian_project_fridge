package com.mode.fridge.widget;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.mode.fridge.R;
import com.mode.fridge.common.GlobalParams;
import com.mode.fridge.device.AppConfig;
import com.mode.fridge.device.DeviceConfig;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by young2 on 2017/1/3.
 */

public class FreezingRoomView extends RelativeLayout {
    private final static String TAG=ColdClosetView.class.getSimpleName();
    private OnTempChangeListener mTempChangeListener;
    private SeekBar mSeekBar;
    private TextView mTempView;
    private int mSetTemp,mMinTemp,mMaxTemp;
    private ImageView mInCreaseView,mDecreaseView;
    private int mSeekbarLength;
    private TimerTask mTimerTask;
    private Timer mTimer;
    private boolean mIsSetting;//是否正在设置中，如正在拉动滑条或者连续点加减，这时暂时不修改ui


    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what==0){
                mTempView.setText(""+mSetTemp);
                setBarProgress(mSetTemp);
                if(mTempChangeListener!=null){
                    mTempChangeListener.onTempChange(mSetTemp);
                }
            }
        }
    };

    public FreezingRoomView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public FreezingRoomView(Context context, AttributeSet attrs) {
        this(context,attrs,0);
    }

    public FreezingRoomView(Context context) {
        super(context,null);
    }

    private void  init(Context context){

        mMinTemp=-25;
        mMaxTemp=-15;
        mSeekbarLength=100;
        if(DeviceConfig.MODEL.equals(AppConfig.VIOMI_FRIDGE_V2)|| DeviceConfig.MODEL.equals(AppConfig.VIOMI_FRIDGE_V3)
                || DeviceConfig.MODEL.equals(AppConfig.VIOMI_FRIDGE_V31)){
            mMinTemp=-24;
            mMaxTemp=-16;
            mSeekbarLength=80;
        }
        View.inflate(context, R.layout.view_freezing_room,this);
        mSeekBar=(SeekBar)findViewById(R.id.seekbar);
        mSeekBar.setMax(mSeekbarLength);
        mTempView= (TextView) findViewById(R.id.temp);
        mTempView.setTypeface(GlobalParams.getInstance().mDigitalTypeface);

        mDecreaseView= (ImageView) findViewById(R.id.icon_decrease);
        mInCreaseView= (ImageView) findViewById(R.id.icon_increase);
        ImageView iconImage= (ImageView) findViewById(R.id.icon);

        if(DeviceConfig.MODEL.equals(AppConfig.VIOMI_FRIDGE_V2)){
            iconImage.setImageResource(R.mipmap.box_icon_left);
        }else if(DeviceConfig.MODEL.equals(AppConfig.VIOMI_FRIDGE_V3)|| DeviceConfig.MODEL.equals(AppConfig.VIOMI_FRIDGE_V31)
                || DeviceConfig.MODEL.equals(AppConfig.VIOMI_FRIDGE_V4)){
            iconImage.setImageResource(R.mipmap.fridge_two_door_down);
        }

        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int temp= (int) (((float)progress)*(mMaxTemp-mMinTemp)/mSeekbarLength+mMinTemp);
                 mTempView.setText(""+temp);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mIsSetting=true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mSetTemp= (int) (((float)seekBar.getProgress())*(mMaxTemp-mMinTemp)/mSeekbarLength+mMinTemp);
                mTempView.setText(""+mSetTemp);
                if(mTempChangeListener!=null){
                    mTempChangeListener.onTempChange(mSetTemp);
                }
                mIsSetting=false;
            }
        });
        mDecreaseView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mSetTemp<=mMinTemp){
                    return;
                }
                mIsSetting=true;
                mSetTemp--;
                mTempView.setText(""+mSetTemp);
                setBarProgress(mSetTemp);
                startTimer();
            }
        });

        mInCreaseView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mSetTemp>=mMaxTemp){
                    return;
                }
                mIsSetting=true;
                mSetTemp++;
                mTempView.setText(""+mSetTemp);
                setBarProgress(mSetTemp);
                startTimer();
            }
        });
    }

    private void startTimer(){
        stopTimer();
        mTimer=new Timer();
        mTimerTask=new TimerTask() {
            @Override
            public void run() {

                if(mHandler!=null){
                    mHandler.sendEmptyMessage(0);
                }

                mIsSetting=false;
            }
        };
        mTimer.schedule(mTimerTask,1000);
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

    /***
     * 初始化
     * @param setTemp
     */
    public void initData(int setTemp){
        if(setTemp<mMinTemp){
            setTemp=mMinTemp;
        }else if(setTemp>mMaxTemp){
            setTemp=mMaxTemp;
        }

        mSetTemp=setTemp;
        mTempView.setText(""+mSetTemp);
        setBarProgress(mSetTemp);

    }

    /***
     * 设置温度
     * @param setTemp
     */
    public void setTemp(int setTemp){
        if(mIsSetting){
            Log.e(TAG,"setting!");
            return;
        }
        if(setTemp<mMinTemp){
            setTemp=mMinTemp;
        }else if(setTemp>mMaxTemp){
            setTemp=mMaxTemp;
        }

        if(mSetTemp!=setTemp){
            mSetTemp=setTemp;
            mTempView.setText(""+mSetTemp);
            setBarProgress(setTemp);
        }


    }


    /***
     * 滑动修改温度监听
     * @param listener
     */
    public void setOnTempChangeListener(OnTempChangeListener listener){
        mTempChangeListener=listener;
    }

    public interface OnTempChangeListener {
        void onTempChange(int temp);
    }

    public void  close(){
        mTempChangeListener=null;
        stopTimer();
        if(mHandler!=null){
            mHandler.removeMessages(0);
            mHandler=null;
        }
    }

    private void setBarProgress(int setTemp){
        int progress=(setTemp-mMinTemp)*mSeekbarLength/(mMaxTemp-mMinTemp);
        if(progress<0){
            progress=0;
        }else if(progress>mSeekbarLength){
            progress=mSeekbarLength;
        }
        mSeekBar.setProgress(progress);
    }
}
