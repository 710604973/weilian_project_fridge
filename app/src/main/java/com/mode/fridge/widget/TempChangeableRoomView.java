package com.mode.fridge.widget;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.mode.fridge.R;
import com.mode.fridge.bean.SerialInfo;
import com.mode.fridge.bean.TCRoomScene;
import com.mode.fridge.common.GlobalParams;
import com.mode.fridge.device.AppConfig;
import com.mode.fridge.device.DeviceConfig;
import com.mode.fridge.manager.RoomSceneManager;
import com.viomi.common.widget.SwitchButton;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by young2 on 2017/1/3.
 */

public class TempChangeableRoomView extends RelativeLayout implements View.OnClickListener{
    private final static String TAG=TempChangeableRoomView.class.getSimpleName();
    private OnTempChangeListener mTempChangeListener;
    private SeekBar mSeekBar;
    private TextView mTempView;
    private SwitchButton mSwitchButton;
    private int mSetTemp,mMinTemp,mMaxTemp;
    private boolean mRoomEnable;
    private ImageView mInCreaseView,mDecreaseView;
    private int mSeekbarLength;
    private boolean mIgnoreChange;
    private TimerTask mTimerTask;
    private Timer mTimer;
    private boolean mSceneChange=false;//点击模式改变
    private boolean mIsSetting;//是否正在设置中，如正在拉动滑条或者连续点加减，这时暂时不修改ui
    private RadioButton mRadioButton0,mRadioButton1,mRadioButton2,mRadioButton3
            ,mRadioButton4,mRadioButton5,mRadioButtonMore,mRadioButtonCustom;
    private RadioGroup mRadioGroup;
    private TextView mSceneView;
    private ArrayList<TCRoomScene> mSceneList;

    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what==0){

                setTempText(mSetTemp,mRoomEnable);
                setBarProgress(mSetTemp);
                setSceneView(mSetTemp);
                if(mTempChangeListener!=null){
                    mTempChangeListener.onTempChange(mSetTemp);
                }
            }else if(msg.what==1){
                mSceneView.setText("");
            }
        }
    };

    public TempChangeableRoomView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public TempChangeableRoomView(Context context, AttributeSet attrs) {
        this(context,attrs,0);
    }

    public TempChangeableRoomView(Context context) {
        this(context,null);
    }

    private void  init(Context context){
        View.inflate(context, R.layout.view_temp_changeable_room,this);
        mMinTemp=-18;
        mMaxTemp=8;
        mSeekbarLength=260;
        if(DeviceConfig.MODEL.equals(AppConfig.VIOMI_FRIDGE_V2)){
            mMinTemp=-18;
            mMaxTemp=-3;
            mSeekbarLength=150;
        }
        mSeekBar=(SeekBar)findViewById(R.id.seekbar);
        mSeekBar.setMax(mSeekbarLength);
        mTempView= (TextView) findViewById(R.id.temp);
        mTempView.setTypeface(GlobalParams.getInstance().mDigitalTypeface);
        mSwitchButton= (SwitchButton) findViewById(R.id.switch_button);
        mDecreaseView= (ImageView) findViewById(R.id.icon_decrease);
        mInCreaseView= (ImageView) findViewById(R.id.icon_increase);
        mRadioGroup=(RadioGroup)findViewById(R.id.scene_radiogroup);
        mRadioButton0= (RadioButton) findViewById(R.id.scene0);
        mRadioButton1= (RadioButton) findViewById(R.id.scene1);
        mRadioButton2= (RadioButton) findViewById(R.id.scene2);
        mRadioButton3= (RadioButton) findViewById(R.id.scene3);
        mRadioButton4= (RadioButton) findViewById(R.id.scene4);
        mRadioButton5= (RadioButton) findViewById(R.id.scene5);
        mRadioButtonMore= (RadioButton) findViewById(R.id.scene_more);
        mRadioButtonCustom= (RadioButton) findViewById(R.id.scene_custom);
        mRadioButton0.setOnClickListener(this);
        mRadioButton1.setOnClickListener(this);
        mRadioButton2.setOnClickListener(this);
        mRadioButton3.setOnClickListener(this);
        mRadioButton4.setOnClickListener(this);
        mRadioButton5.setOnClickListener(this);
        mRadioButtonMore.setOnClickListener(this);
        mRadioButtonCustom.setOnClickListener(this);
        mDecreaseView.setOnClickListener(this);
        mInCreaseView.setOnClickListener(this);
        mSceneView= (TextView) findViewById(R.id.scecn_view);
        ImageView iconImage= (ImageView) findViewById(R.id.icon);
        if(DeviceConfig.MODEL.equals(AppConfig.VIOMI_FRIDGE_V2)){
            iconImage.setImageResource(R.mipmap.box_icon_right);
         //   mSwitchButton.setVisibility(INVISIBLE);
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
                setSceneView(mSetTemp);
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
                        GlobalParams.getInstance().setChangeableRoomTempBeforClose(mSetTemp);
                        mSetTemp= SerialInfo.ROOM_CLOSE_TEMP;
                        mRoomEnable=false;
                    }else {
                        mSetTemp=GlobalParams.getInstance().getChangeableRoomTempBeforClose();
                        mRoomEnable=true;
                    }
                    setTempText(mSetTemp,mRoomEnable);
                    mTempChangeListener.onRoomEnable(mRoomEnable);
                }

                switchProcess(isChecked);
            }
        });
        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup arg0, int arg1) {
                mSceneChange=true;
            }
        });
    }

    /***
     * 打开或关闭冰箱，禁止货使能其他页面动作
     */
    private void enableUi(boolean enable){
        if(enable){
            mRadioButton0.setEnabled(true);
            mRadioButton1.setEnabled(true);
            mRadioButton2.setEnabled(true);
            mRadioButton3.setEnabled(true);
            mRadioButton4.setEnabled(true);
            mRadioButton5.setEnabled(true);
            mRadioButtonMore.setEnabled(true);
            mRadioButtonCustom.setEnabled(true);
            mInCreaseView.setEnabled(true);
            mDecreaseView.setEnabled(true);

        }else {
            mRadioButton0.setEnabled(false);
            mRadioButton1.setEnabled(false);
            mRadioButton2.setEnabled(false);
            mRadioButton3.setEnabled(false);
            mRadioButton4.setEnabled(false);
            mRadioButton5.setEnabled(false);
            mRadioButtonMore.setEnabled(false);
            mRadioButtonCustom.setEnabled(false);
            mInCreaseView.setEnabled(false);
            mDecreaseView.setEnabled(false);
        }
    }

    public void  setSceneView(int temp){
        mSceneList=getScenesChooseList();
        boolean isChoose=false;
       String  chooseName= RoomSceneManager.getInstance().getChooseScene();
        int length=mSceneList.size();
        if(length>0){
            mRadioButton0.setText(mSceneList.get(0).name);
            mRadioButton0.setVisibility(VISIBLE);
            if(mSceneList.get(0).name.equals(chooseName)&&mSceneList.get(0).value==temp){
                mRadioButton0.setChecked(true);
                isChoose=true;
            }else {
                mRadioButton0.setChecked(false);
            }
        }else {
            mRadioButton0.setVisibility(INVISIBLE);
        }

        if(length>1){
            mRadioButton1.setText(mSceneList.get(1).name);
            mRadioButton1.setVisibility(VISIBLE);
            if(mSceneList.get(1).name.equals(chooseName)&&mSceneList.get(1).value==temp){
                mRadioButton1.setChecked(true);
                isChoose=true;
            }else {
                mRadioButton1.setChecked(false);
            }

         }else {
            mRadioButton1.setVisibility(INVISIBLE);
        }

        if(length>2){
            mRadioButton2.setText(mSceneList.get(2).name);
            mRadioButton2.setVisibility(VISIBLE);
            if(mSceneList.get(2).name.equals(chooseName)&&mSceneList.get(2).value==temp){
                mRadioButton2.setChecked(true);
                isChoose=true;
            }else {
                mRadioButton2.setChecked(false);
            }
        }else {
            mRadioButton2.setVisibility(INVISIBLE);
        }

        if(length>3){
            mRadioButton3.setText(mSceneList.get(3).name);
            mRadioButton3.setVisibility(VISIBLE);
            if(mSceneList.get(3).name.equals(chooseName)&&mSceneList.get(3).value==temp){
                mRadioButton3.setChecked(true);
                isChoose=true;
            }else {
                mRadioButton3.setChecked(false);
            }
        }else {
            mRadioButton3.setVisibility(INVISIBLE);
        }

        if(length>4){
            mRadioButton4.setText(mSceneList.get(4).name);
            mRadioButton4.setVisibility(VISIBLE);
            if(mSceneList.get(4).name.equals(chooseName)&&mSceneList.get(4).value==temp){
                mRadioButton4.setChecked(true);
                isChoose=true;
            }else {
                mRadioButton4.setChecked(false);
            }
        }else {
            mRadioButton4.setVisibility(INVISIBLE);
        }

        if(length>5){
            mRadioButton5.setText(mSceneList.get(5).name);
            mRadioButton5.setVisibility(VISIBLE);
            if(mSceneList.get(5).name.equals(chooseName)&&mSceneList.get(5).value==temp){
                mRadioButton5.setChecked(true);
                isChoose=true;
            }else {
                mRadioButton5.setChecked(false);
            }
        }else {
            mRadioButton5.setVisibility(INVISIBLE);
        }
        mRadioButtonMore.setChecked(false);
        if(isChoose){
            mRadioButtonCustom.setChecked(false);
        }else {
            mRadioButtonCustom.setChecked(true);
        }

//        SetSceneChooseButton(temp);
    }

    /***
     * 获取常用的场景
     * @return
     */
    private ArrayList<TCRoomScene> getScenesChooseList(){
        return RoomSceneManager.getInstance().getChooseSceneList();
    }

//    /***
//     * 场景的ui显示
//     * @param temp
//     */
//    private void SetSceneChooseButton(int temp){
//        int chooseIndex=GlobalParams.getInstance().getSceneChoose();
//        if(chooseIndex== RoomSceneManager.SCENE_CUSTOM_INDEX){//自定义方式
//            mRadioButton0.setChecked(false);
//            mRadioButton1.setChecked(false);
//            mRadioButton2.setChecked(false);
//            mRadioButton3.setChecked(false);
//            mRadioButton4.setChecked(false);
//            mRadioButton5.setChecked(false);
//            mRadioButtonCustom.setChecked(true);
//        }else if(chooseIndex<RoomSceneManager.mSumCount&&chooseIndex>=0){//场景模式
//
//            ArrayList<TCRoomScene>  lists=RoomSceneManager.getInstance().getSceneList();
//            if(lists.get(chooseIndex).value==temp){//获取温度属于选定场景
//                if(chooseIndex==0){
//                    mRadioButton0.setChecked(true);
//                }else {
//                    mRadioButton0.setChecked(false);
//                }
//                if(chooseIndex==1){
//                    mRadioButton1.setChecked(true);
//                }else {
//                    mRadioButton1.setChecked(false);
//                }
//                if(chooseIndex==2){
//                    mRadioButton2.setChecked(true);
//                }else {
//                    mRadioButton2.setChecked(false);
//                }
//                if(chooseIndex==3){
//                    mRadioButton3.setChecked(true);
//                }else {
//                    mRadioButton3.setChecked(false);
//                }
//                if(chooseIndex==4){
//                    mRadioButton4.setChecked(true);
//                }else {
//                    mRadioButton4.setChecked(false);
//                }
//                if(chooseIndex==5){
//                    mRadioButton5.setChecked(true);
//                }else {
//                    mRadioButton5.setChecked(false);
//                }
//                mRadioButtonCustom.setChecked(false);
//            }else {//获取温度不属于选定场景，重置为自定义
//                GlobalParams.getInstance().setSceneChooseIndex(RoomSceneManager.SCENE_CUSTOM_INDEX);
//                mRadioButton0.setChecked(false);
//                mRadioButton1.setChecked(false);
//                mRadioButton2.setChecked(false);
//                mRadioButton3.setChecked(false);
//                mRadioButton4.setChecked(false);
//                mRadioButton5.setChecked(false);
//                mRadioButtonCustom.setChecked(true);
//            }
//
//        }
//        mRadioButtonMore.setChecked(false);
//    }


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
        if(!mRoomEnable){
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
        setSceneView(mSetTemp);
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
            setSceneView(mSetTemp);
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
        void onMoreLayoutClick();
        void onSceneChange(int temp, String name);
    }

    @Override
    public void onClick(View view) {
        if(!mRoomEnable){
            return;
        }
        switch (view.getId()){

            case R.id.scene0:
                chooseScene(0);
            break;
            case R.id.scene1:
                chooseScene(1);
                break;
            case R.id.scene2:
                chooseScene(2);
                break;
            case R.id.scene3:
                chooseScene(3);
                break;
            case R.id.scene4:
                chooseScene(4);
                break;
            case R.id.scene5:
                chooseScene(5);
                break;
            case R.id.scene_more:
                if(mTempChangeListener!=null){
                    mTempChangeListener.onMoreLayoutClick();
                }
                break;
            case R.id.scene_custom:
                GlobalParams.getInstance().setSceneChoose(GlobalParams.Value_Scene_ChooseName);
                break;

            case R.id.icon_decrease:
                if(mSetTemp<=mMinTemp){
                    return;
                }
                mIsSetting=true;
                mSetTemp--;
                setTempText(mSetTemp,mRoomEnable);
                setBarProgress(mSetTemp);
                startTimer();
                break;

            case R.id.icon_increase:
                Log.e(TAG,"mSetTemp"+mSetTemp+",mMaxTemp="+mMaxTemp);
                if(mSetTemp>=mMaxTemp){
                    return;
                }
                mIsSetting=true;
                mSetTemp++;
                setTempText(mSetTemp,mRoomEnable);
                setBarProgress(mSetTemp);
                startTimer();
                break;
        }
    }

    private void chooseScene(int i){
        if(mSceneChange){
            mSceneChange=false;
            mSetTemp=mSceneList.get(i).value;
            if(mTempChangeListener!=null){
                mTempChangeListener.onSceneChange(mSetTemp,mSceneList.get(i).name);
            }
            setBarProgress(mSetTemp);
            setTempText(mSetTemp,mRoomEnable);
            mSceneView.setText(mSceneList.get(i).desc);
            if(mHandler!=null){
                mHandler.sendEmptyMessageDelayed(1,10000);
            }
//            GlobalParams.getInstance().setSceneChoose(mSceneList.get(i).name);
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

    public void  close(){
        mTempChangeListener=null;
        stopTimer();
        if(mHandler!=null){
            mHandler.removeMessages(0);
            mHandler.removeMessages(1);
            mHandler=null;
        }
    }
}
