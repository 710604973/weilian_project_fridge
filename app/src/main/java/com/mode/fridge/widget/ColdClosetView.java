package com.mode.fridge.widget;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.mode.fridge.R;
import com.mode.fridge.bean.SerialInfo;
import com.mode.fridge.common.GlobalParams;
import com.mode.fridge.device.AppConfig;
import com.mode.fridge.device.DeviceConfig;
import com.mode.fridge.manager.ControlManager;
import com.viomi.common.widget.SwitchButton;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by young2 on 2017/1/3.
 */

public class ColdClosetView extends RelativeLayout {
    private final static String TAG=ColdClosetView.class.getSimpleName();
    private OnTempChangeListener mTempChangeListener;
    private SeekBar mSeekBar;
    private TextView mTempView;
    private SwitchButton mSwitchButton;
    private TextView mIcedDrinkView;
    private ImageView mInCreaseView,mDecreaseView;
    private int mSetTemp,mMinTemp,mMaxTemp;
    private boolean mRoomEnable;
    private int mSeekbarLength=180;
    private boolean mIgnoreChange;
    private TimerTask mTimerTask;
    private Timer mTimer;
    private boolean mIsSetting;//是否正在设置中，如正在拉动滑条或者连续点加减，这时暂时不修改ui


    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what==0){
                setTempText(mSetTemp,mRoomEnable);
                setBarProgress(mSetTemp);
                if(mTempChangeListener!=null){
                    mTempChangeListener.onTempChange(mSetTemp);
                }
            }
        }
    };

    public ColdClosetView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public ColdClosetView(Context context, AttributeSet attrs) {
        this(context,attrs,0);
    }

    public ColdClosetView(Context context) {
        this(context,null);
    }

    private void  init(Context context){
        View.inflate(context, R.layout.view_cold_closet,this);
        mMinTemp=2;
        mMaxTemp=8;
        mSeekbarLength=60;
        mSeekBar=(SeekBar)findViewById(R.id.seekbar);
        mTempView= (TextView) findViewById(R.id.temp);
        mTempView.setTypeface(GlobalParams.getInstance().mDigitalTypeface);
        mSwitchButton= (SwitchButton) findViewById(R.id.switch_button);
        mDecreaseView= (ImageView) findViewById(R.id.icon_decrease);
        mInCreaseView= (ImageView) findViewById(R.id.icon_increase);
        ImageView iconImage= (ImageView) findViewById(R.id.icon);
        mIcedDrinkView= (TextView) findViewById(R.id.iced_drink_button);

        if(DeviceConfig.MODEL.equals(AppConfig.VIOMI_FRIDGE_V4)){
            mIcedDrinkView.setVisibility(View.VISIBLE);
        }else {
            mIcedDrinkView.setVisibility(View.GONE);
        }

        if(DeviceConfig.MODEL.equals(AppConfig.VIOMI_FRIDGE_V2)){
            iconImage.setImageResource(R.mipmap.box_icon_up);
        //    mSwitchButton.setVisibility(INVISIBLE);
        }else if(DeviceConfig.MODEL.equals(AppConfig.VIOMI_FRIDGE_V3)|| DeviceConfig.MODEL.equals(AppConfig.VIOMI_FRIDGE_V31)
                || DeviceConfig.MODEL.equals(AppConfig.VIOMI_FRIDGE_V4)){
            iconImage.setImageResource(R.mipmap.fridge_two_door_up);
        }

        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int temp= (int) (((float)progress)*(mMaxTemp-mMinTemp)/mSeekbarLength+mMinTemp);
                setTempText(temp,mRoomEnable);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mIsSetting=true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mSetTemp= (int) (((float)seekBar.getProgress())*(mMaxTemp-mMinTemp)/mSeekbarLength+mMinTemp);
                setTempText(mSetTemp,mRoomEnable);
                if(mTempChangeListener!=null){
                    mTempChangeListener.onTempChange(mSetTemp);
                }
                mIsSetting=false;
            }
        });
        mSwitchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(mTempChangeListener!=null&&(!mIgnoreChange)){
                    if(!isChecked){
                        GlobalParams.getInstance().setClodClosetTempBeforClose(mSetTemp);
                        mSetTemp= SerialInfo.ROOM_CLOSE_TEMP;
                        mRoomEnable=false;
                    }else {
                        mSetTemp=GlobalParams.getInstance().getClodClosetTempBeforClose();
                        mRoomEnable=true;
                    }
                    setTempText(mSetTemp,mRoomEnable);
                    mTempChangeListener.onRoomEnable(mRoomEnable);

                }
                switchProcess(isChecked);
            }
        });
        mDecreaseView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!mRoomEnable){
                    return;
                }

                if(mSetTemp<=mMinTemp){
                    return;
                }
                mIsSetting=true;
                mSetTemp--;
                setTempText(mSetTemp,mRoomEnable);
                setBarProgress(mSetTemp);
                startTimer();
            }
        });

        mInCreaseView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!mRoomEnable){
                    return;
                }
                if(mSetTemp>=mMaxTemp){
                    return;
                }
                mIsSetting=true;
                mSetTemp++;
                setTempText(mSetTemp,mRoomEnable);
                setBarProgress(mSetTemp);
                startTimer();
            }
        });

        mIcedDrinkView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean enable;

                if(!ControlManager.getInstance().isIcedDrink()){
                    mIcedDrinkView.setTextColor(getResources().getColor(R.color.control_enable_color));
                    mIcedDrinkView.setBackground(getResources().getDrawable(R.drawable.control_button_check));
                    enable=true;
                }else {
                    mIcedDrinkView.setBackground(getResources().getDrawable(R.drawable.control_button_normal));
                    mIcedDrinkView.setTextColor(getResources().getColor(R.color.white));
                    enable=false;
                }
                if(mTempChangeListener!=null){
                    mTempChangeListener.onIcedDrinkEnabe(enable);
                }
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
     * @param enable
     */
    public void initData(int setTemp,boolean enable){

        mSetTemp=setTemp;
        mRoomEnable=enable;
        if(!enable){
            mIgnoreChange=true;
            mSwitchButton.setChecked(false);
            mIgnoreChange=false;
            switchProcess(false);
        }else {
            mIgnoreChange=true;
            mSwitchButton.setChecked(true);
            mIgnoreChange=false;
            switchProcess(true);
            setBarProgress(setTemp);
        }
        setTempText(mSetTemp,mRoomEnable);

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


    private void setTempText(int temp,boolean enable){
        String tempStr="";
        if(!enable){
            tempStr="OFF";
        }
        else {
            tempStr=""+temp;
        }
        mTempView.setText(tempStr);
    }


    /***
     * 设置温度
     * @param setTemp
     * @param enable
     */
    public void setTemp(int setTemp,boolean enable){
        if(mIsSetting){
            Log.e(TAG,"setting!");
            return;
        }

        if(mSetTemp!=setTemp||mRoomEnable!=enable){
            mSetTemp=setTemp;
            mRoomEnable=enable;
            if(!enable){
                mIgnoreChange=true;
                mSwitchButton.setChecked(false);
                mIgnoreChange=false;
                switchProcess(false);
            }else {
                mIgnoreChange=true;
                mSwitchButton.setChecked(true);
                mIgnoreChange=false;
                switchProcess(true);
                setBarProgress(setTemp);
            }
            setTempText(mSetTemp,mRoomEnable);
        }
    }

    /***
     * switch影响ui
     */
    private void switchProcess(boolean isChecked){
        if(!isChecked){
            mSeekBar.setEnabled(false);
            mDecreaseView.setEnabled(false);
            mInCreaseView.setEnabled(false);
        }else {
            mSeekBar.setEnabled(true);
            mDecreaseView.setEnabled(true);
            mInCreaseView.setEnabled(true);
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
        void onRoomEnable(boolean enable);
        void onIcedDrinkEnabe(boolean enable);
    }

    /***
     * 冰饮状态刷新
     * @param iced_drink
     */
    public   void setIcedDrinkStatus(boolean iced_drink){
        if(iced_drink){
            mIcedDrinkView.setTextColor(getResources().getColor(R.color.control_enable_color));
            mIcedDrinkView.setBackground(getResources().getDrawable(R.drawable.control_button_check));
        }else {
            mIcedDrinkView.setBackground(getResources().getDrawable(R.drawable.control_button_normal));
            mIcedDrinkView.setTextColor(getResources().getColor(R.color.white));
        }
    }


    public void  close(){
        mTempChangeListener=null;
        stopTimer();
        if(mHandler!=null){
            mHandler.removeMessages(0);
            mHandler=null;
        }
    }

}
