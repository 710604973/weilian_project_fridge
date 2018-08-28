package com.mode.fridge.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mode.fridge.R;
import com.mode.fridge.bean.DeviceError;
import com.mode.fridge.bean.DeviceParamsGet;
import com.mode.fridge.bean.DeviceParamsSet;
import com.mode.fridge.bean.SerialInfo;
import com.mode.fridge.bean.aidl.StreamBean;
import com.mode.fridge.broadcast.BroadcastAction;
import com.mode.fridge.common.FridgeStreamId;
import com.mode.fridge.common.GlobalParams;
import com.mode.fridge.common.rxbus.BusEvent;
import com.mode.fridge.common.rxbus.RxBus;
import com.mode.fridge.device.AppConfig;
import com.mode.fridge.device.DeviceConfig;
import com.mode.fridge.device.DeviceManager;
import com.mode.fridge.manager.ControlManager;
import com.mode.fridge.manager.ControlViewManager;
import com.mode.fridge.manager.FridgeTipsManager;
import com.mode.fridge.services.AidlService;
import com.mode.fridge.utils.ApkUtil;
import com.mode.fridge.utils.ToolUtil;
import com.mode.fridge.utils.log;
import com.mode.fridge.view.dialog.BaseDialog;
import com.mode.fridge.view.dialog.FilterDialog;
import com.mode.fridge.view.dialog.SceneDialog;
import com.mode.fridge.widget.ColdClosetView;
import com.mode.fridge.widget.FreezingRoomView;
import com.mode.fridge.widget.FridgeRoomView;
import com.mode.fridge.widget.ProgressWheel;
import com.mode.fridge.widget.TempChangeableRoomView;
import com.mode.fridge.widget.snowingview.SnowingSurfaceView;
import com.viomi.common.callback.AppCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import rx.Subscription;


/**
 * Created by young2 on 2017/2/20.
 */

public class FridgeActivity extends BaseHandler2Activity implements View.OnClickListener {
    private final static String TAG = FridgeActivity.class.getSimpleName();
    private DrawerLayout mDrawerLayout;
    private RelativeLayout mNormalView;
    private CheckBox mSmartButton, mHolidayButton, mCleanButton, mQuickCoolButton, mQuickFrezzeButton;
    private boolean mSmartButtonIgnoreChange, mHolidayButtonIgnoreChange, mQiuckCoolButtonIgnoreChange, mQiuckFreezeButtonIgnoreChange, mCleanButtonIgnoreChange;
    private TempChangeableRoomView mTempChangeableRoomView;
    private ColdClosetView mColdClosetView;
    private FreezingRoomView mFreezingRoomView;
    private FridgeRoomView mCCFridgeRoomView, mTCFridgeRoomView, mFZFridgeRoomView;
    private ControlViewManager mControlViewManager;
    private ProgressWheel mProgressWheel;
    private TextView mFilterTextView;
    private SnowingSurfaceView mSnowingSurfaceView1, mSnowingSurfaceView2;
    private ImageView mXiaoxianImage;
    private final static int MAX_ANIMATION_SHOW_TIME = 20000;
    private final static int MSG_WHAT_CCROOM_ANIMATION_CLOSE = 0;
    private final static int MSG_WHAT_TCROOM_ANIMATION_CLOSE = 1;
    private final static int MSG_WHAT_FZROOM_ANIMATION_CLOSE = 2;
    private final static int MSG_WHAT_ALL_ROOM_ANIMATION_CLOSE = 3;
    private final static int MSG_WHAT_CLEAN_ANIMATION_CLOSE = 11;
    private final static int MSG_WHAT_UPDATE_TIPS = 10;
    private final static int MSG_WHAT_SETUP_FAIL = 12;
    private final static int MSG_WHAT_CLICK_MORE = 13;
    private final static int MSG_WHAT_USER_TIPS_CLOSE = 14;
    private final static int MSG_WHAT_REFER_FILTER = 15;
    private TimerTask mTimerTask;
    private Timer mTimer;
    private TextView mChatView, mFilterBuy, mFilterReset;
    private ImageView mWaveImageView1, mWaveImageView2, mWaveImageView3;
    private Animation mAnimation1, mAnimation2;
    private boolean mIsCleanAnimationStop = true;
    private AppCallback<String> mRCRoomTempCallback, mRCRoomEnableCallback, mRCRoomQuickCoolCallback;//冷藏室设置回调
    private AppCallback<String> mCCRoomTempCallback, mCCRoomEnableCallback, mCCRoomSceneCallback;//变温室设置回调
    private AppCallback<String> mFZRoomTempCallback, mFZRoomQuickColdCallback, mIcedDrinkCallBack, mSmartModeCallback, mHolidayModeCallback;
    private SceneDialog mSceneDialog;
    private FilterDialog mFilterDialog;
    private BaseDialog mConfirmDialog;
    private RelativeLayout mUserTipsLayout;
    private TextView mStatusView;
    private ViewGroup mFirdgeView;
    private int testCount;
    private Subscription mSubscription;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int layoutId = R.layout.activity_fridge;
        if (AppConfig.VIOMI_FRIDGE_V2.equals(DeviceConfig.MODEL)) {
            layoutId = R.layout.activity_fridge_four_door;
        } else if (AppConfig.VIOMI_FRIDGE_V3.equals(DeviceConfig.MODEL)
                || AppConfig.VIOMI_FRIDGE_V31.equals(DeviceConfig.MODEL)
                || AppConfig.VIOMI_FRIDGE_V4.equals(DeviceConfig.MODEL)) {
            layoutId = R.layout.activity_fridge_two_door;
        }
        setContentView(layoutId);
        init();
    }

    private void init() {
        mSubscription = RxBus.getInstance().subscribe(busEvent -> {
            switch (busEvent.getMsgId()) {
                case BusEvent.MSG_REFRESH_FRIDGE:
                    refreshView(ControlManager.getInstance().getDataSendInfo());
                    break;
            }
        });
        mFirdgeView = (ViewGroup) findViewById(R.id.firdge_view);
        mWaveImageView1 = (ImageView) findViewById(R.id.image_wave1);
        mWaveImageView2 = (ImageView) findViewById(R.id.image_wave2);
        mWaveImageView3 = (ImageView) findViewById(R.id.image_wave3);
        mAnimation1 = (Animation) AnimationUtils.loadAnimation(FridgeActivity.this, R.anim.wave_tranlate_up);
        mAnimation2 = (Animation) AnimationUtils.loadAnimation(FridgeActivity.this, R.anim.wave_tranlate_down);


        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerlayout);
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        mSmartButton = (CheckBox) findViewById(R.id.smart_button);
        mHolidayButton = (CheckBox) findViewById(R.id.holiday_button);
        mCleanButton = (CheckBox) findViewById(R.id.one_key_clean_button);
        mProgressWheel = (ProgressWheel) findViewById(R.id.progress_bar);
        mFilterTextView = (TextView) findViewById(R.id.filter_text);
        mSnowingSurfaceView1 = (SnowingSurfaceView) findViewById(R.id.snowing_view1);
        mSnowingSurfaceView2 = (SnowingSurfaceView) findViewById(R.id.snowing_view2);
        ImageView backIcon = (ImageView) findViewById(R.id.back_icon);
        backIcon.setOnClickListener(this);
        mXiaoxianImage = (ImageView) findViewById(R.id.xiaoxian);
        mChatView = (TextView) findViewById(R.id.chat_view);
        mUserTipsLayout = (RelativeLayout) findViewById(R.id.user_tips_layout);
        TextView user_tips = (TextView) findViewById(R.id.user_tips);
        if (GlobalParams.getInstance().isVoiceEnabe()) {
            user_tips.setText(getString(R.string.text_wakeup_tips));
        } else {
            user_tips.setText(getString(R.string.text_not_wakeup_tips));
        }

        ImageView tips_colse = (ImageView) findViewById(R.id.tips_colse);
        tips_colse.setOnClickListener(this);
        mStatusView = (TextView) findViewById(R.id.status_view);

        DeviceParamsSet deviceParamsSet = ControlManager.getInstance().getDataSendInfo();
        log.d(TAG, "@@@111=" + (deviceParamsSet.toString()));
        log.d(TAG, "@@@222=" + (ControlManager.getInstance().getDataReceiveInfo().toString()));
        log.d(TAG, "@@@333=" + GlobalParams.getInstance().getSceneChoose());

        mQuickCoolButton = (CheckBox) findViewById(R.id.qiuck_cool_button);
        mQuickFrezzeButton = (CheckBox) findViewById(R.id.qiuck_frezze_button);
        mFreezingRoomView = (FreezingRoomView) findViewById(R.id.freezing_room_layout);
        mFreezingRoomView.initData(deviceParamsSet.freezing_room_temp_set);
        mTempChangeableRoomView = (TempChangeableRoomView) findViewById(R.id.temp_changeable_room_layout);
        mTempChangeableRoomView.initData(deviceParamsSet.temp_changeable_room_temp_set, deviceParamsSet.temp_changeable_room_room_enable);
        mColdClosetView = (ColdClosetView) findViewById(R.id.cold_closet_layout);
        mColdClosetView.initData(deviceParamsSet.cold_closet_temp_set, deviceParamsSet.cold_closet_room_enable);

        mCCFridgeRoomView = (FridgeRoomView) findViewById(R.id.cc_room);
        mTCFridgeRoomView = (FridgeRoomView) findViewById(R.id.tc_room);
        mFZFridgeRoomView = (FridgeRoomView) findViewById(R.id.fz_room);
        mCCFridgeRoomView.setTemp(deviceParamsSet.cold_closet_temp_set, deviceParamsSet.cold_closet_room_enable);
        mTCFridgeRoomView.setTemp(deviceParamsSet.temp_changeable_room_temp_set, deviceParamsSet.temp_changeable_room_room_enable);
        mFZFridgeRoomView.setTemp(deviceParamsSet.freezing_room_temp_set);

        modelSwitch();

        final LinearLayout fridgeView = (LinearLayout) findViewById(R.id.firdge_view);
        fridgeView.setOnClickListener(this);
        ImageView closeIcon = (ImageView) findViewById(R.id.close_icon);
        closeIcon.setOnClickListener(this);
        //  mDrawerLayout.setScrimColor(Color.TRANSPARENT);
        //   mDrawerLayout.setStatusBarBackgroundColor(getResources().getColor(android.R.color.transparent));
        mNormalView = (RelativeLayout) findViewById(R.id.normal_view);

        mControlViewManager = new ControlViewManager();
        mControlViewManager.init(this);

        mColdClosetView.setOnTempChangeListener(new ColdClosetView.OnTempChangeListener() {
            @Override
            public void onTempChange(int temp) {
                Log.i(TAG, "mColdClosetView onTempChange,temp=" + temp);
                if (commodityInspectionRunningProcess()) {
                    return;
                }
                setHolidayButtonEnable(false);
                setSmartButtonEnable(false);
                mControlViewManager.paramsChange();
                if (mRCRoomTempCallback == null) {
                    mRCRoomTempCallback = new AppCallback<String>() {
                        @Override
                        public void onSuccess(String data) {
                            log.d(TAG, "onSuccess,deviceParamsSet=" + (ControlManager.getInstance().getDataSendInfo().toString()));
                            DeviceManager.getInstance().sendPropertyRCSetTemp(ControlManager.getInstance().getDataSendInfo().cold_closet_temp_set);
                            DeviceManager.getInstance().sendPropertyMode(SerialInfo.MODE_NULL);
                        }

                        @Override
                        public void onFail(int errorCode, String msg) {
                            Log.e(TAG, "onTempChange fail,code=" + errorCode + ",msg=" + msg);
                            if (mHandler != null) {
                                mHandler.sendEmptyMessage(MSG_WHAT_SETUP_FAIL);
                            }
                        }
                    };
                }
                boolean result = ControlManager.getInstance().setRoomTemp(temp, SerialInfo.ROOM_COLD_COLSET, mRCRoomTempCallback);
                if (!result) {
                    Toast.makeText(FridgeActivity.this, getString(R.string.toast_set_fail), Toast.LENGTH_LONG).show();
                } else {
//                    String json = getJson(FridgeStreamId.FRI_TEMP, String.valueOf(temp));
//                    RxBus.getInstance().post(BusEvent.MSG_REPORT_FRIDGE, json);
                    sendMsg();
                    closeModeAnimation();
                    mCCFridgeRoomView.showAnimation(true, R.mipmap.fan_rotota, 0);
                    if (mHandler != null) {
                        mHandler.removeMessages(MSG_WHAT_CCROOM_ANIMATION_CLOSE);
                        mHandler.removeMessages(MSG_WHAT_ALL_ROOM_ANIMATION_CLOSE);
                        mHandler.sendEmptyMessageDelayed(MSG_WHAT_CCROOM_ANIMATION_CLOSE, MAX_ANIMATION_SHOW_TIME);
                    }
                }

            }

            @Override
            public void onRoomEnable(boolean enable) {
                Log.i(TAG, "mColdClosetView onTempChange,enable=" + enable);
                if (commodityInspectionRunningProcess()) {
                    return;
                }
                setHolidayButtonEnable(false);
                setSmartButtonEnable(false);
                mControlViewManager.paramsChange();
                if (mRCRoomEnableCallback == null) {
                    mRCRoomEnableCallback = new AppCallback<String>() {
                        @Override
                        public void onSuccess(String data) {
                            log.d(TAG, "onSuccess,deviceParamsSet=" + (ControlManager.getInstance().getDataSendInfo().toString()));
                            DeviceManager.getInstance().sendPropertyRCSetTemp(ControlManager.getInstance().getDataSendInfo().cold_closet_temp_set);
                            DeviceManager.getInstance().sendPropertyRCRoomEnable(ControlManager.getInstance().getDataSendInfo().cold_closet_room_enable);
                            DeviceManager.getInstance().sendPropertyMode(SerialInfo.MODE_NULL);
                        }

                        @Override
                        public void onFail(int errorCode, String msg) {
                            Log.e(TAG, "onRoomEnable fail,code=" + errorCode + ",msg=" + msg);
                            if (mHandler != null) {
                                mHandler.sendEmptyMessage(MSG_WHAT_SETUP_FAIL);
                            }
                        }
                    };
                }
                boolean result = ControlManager.getInstance().enableRoom(enable, SerialInfo.ROOM_COLD_COLSET, mRCRoomEnableCallback);
                if (!result) {
                    Toast.makeText(FridgeActivity.this, getString(R.string.toast_set_fail), Toast.LENGTH_LONG).show();
                } else {
                    closeModeAnimation();
//                    String json = getJson(FridgeStreamId.FRI_POWER, getStatus(enable));
//                    RxBus.getInstance().post(BusEvent.MSG_REPORT_FRIDGE, json);
                    sendMsg();
                    if (enable) {
                        mCCFridgeRoomView.showAnimation(true, R.mipmap.fan_rotota, 0);
                    } else {
                        mCCFridgeRoomView.showAnimation(false, 0, 0);
                    }
                    if (mHandler != null) {
                        mHandler.removeMessages(MSG_WHAT_CCROOM_ANIMATION_CLOSE);
                        mHandler.removeMessages(MSG_WHAT_ALL_ROOM_ANIMATION_CLOSE);
                        mHandler.sendEmptyMessageDelayed(MSG_WHAT_CCROOM_ANIMATION_CLOSE, MAX_ANIMATION_SHOW_TIME);
                    }
                }

            }

            @Override
            public void onIcedDrinkEnabe(boolean enable) {
                if (commodityInspectionRunningProcess()) {
                    return;
                }

                mControlViewManager.paramsChange();

                if (mIcedDrinkCallBack == null) {
                    mIcedDrinkCallBack = new AppCallback<String>() {
                        @Override
                        public void onSuccess(String data) {
                            log.d(TAG, "onSuccess,deviceParamsSet=" + (ControlManager.getInstance().getDataSendInfo().toString()));
                        }

                        @Override
                        public void onFail(int errorCode, String msg) {
                            Log.e(TAG, "enableIcedDrink fail,code=" + errorCode + ",msg=" + msg);
                            if (mHandler != null) {
                                mHandler.sendEmptyMessage(MSG_WHAT_SETUP_FAIL);
                            }
                        }
                    };
                }
                boolean result = ControlManager.getInstance().enableIcedDrink(enable, mIcedDrinkCallBack);
                if (!result) {
                    Toast.makeText(FridgeActivity.this, getString(R.string.toast_set_fail), Toast.LENGTH_LONG).show();
                }
            }
        });

        mTempChangeableRoomView.setOnTempChangeListener(new TempChangeableRoomView.OnTempChangeListener() {
            @Override
            public void onTempChange(int temp) {
                Log.i(TAG, "mTempChangeableRoomView onTempChange,temp=" + temp);
                if (commodityInspectionRunningProcess()) {
                    return;
                }
                setHolidayButtonEnable(false);
                setSmartButtonEnable(false);
                mControlViewManager.paramsChange();
                if (mCCRoomTempCallback == null) {
                    mCCRoomTempCallback = new AppCallback<String>() {
                        @Override
                        public void onSuccess(String data) {
                            Log.d(TAG, "onSuccess,deviceParamsSet=" + (ControlManager.getInstance().getDataSendInfo().toString()));
                            DeviceManager.getInstance().sendPropertyCCSetTemp(ControlManager.getInstance().getDataSendInfo().temp_changeable_room_temp_set);
                        }

                        @Override
                        public void onFail(int errorCode, String msg) {
                            Log.e(TAG, "onTempChange fail,code=" + errorCode + ",msg=" + msg);
                            if (mHandler != null) {
                                mHandler.sendEmptyMessage(MSG_WHAT_SETUP_FAIL);
                            }
                        }
                    };
                }
                boolean result = ControlManager.getInstance().setRoomTemp(temp, SerialInfo.ROOM_CHANGEABLE_ROOM, mCCRoomTempCallback);
                if (!result) {
                    Toast.makeText(FridgeActivity.this, getString(R.string.toast_set_fail), Toast.LENGTH_LONG).show();
                } else {
//                    String json = getJson(FridgeStreamId.VAR_TEMP, String.valueOf(temp));
//                    RxBus.getInstance().post(BusEvent.MSG_REPORT_FRIDGE, json);
                    sendMsg();
                    closeModeAnimation();
                    if (temp >= 0) {
                        mTCFridgeRoomView.showAnimation(true, R.mipmap.fan_rotota, 0);
                    } else {
                        mTCFridgeRoomView.showAnimation(true, R.mipmap.snow_rotato, 0);
                    }
                    if (mHandler != null) {
                        mHandler.removeMessages(MSG_WHAT_TCROOM_ANIMATION_CLOSE);
                        mHandler.removeMessages(MSG_WHAT_ALL_ROOM_ANIMATION_CLOSE);
                        mHandler.sendEmptyMessageDelayed(MSG_WHAT_TCROOM_ANIMATION_CLOSE, MAX_ANIMATION_SHOW_TIME);
                    }
                }
            }

            @Override
            public void onRoomEnable(boolean enable) {
                Log.i(TAG, "mTempChangeableRoomView onRoomEnable,enable=" + enable);
                if (commodityInspectionRunningProcess()) {
                    return;
                }
                setHolidayButtonEnable(false);
                setSmartButtonEnable(false);
                mControlViewManager.paramsChange();
                if (mCCRoomEnableCallback == null) {
                    mCCRoomEnableCallback = new AppCallback<String>() {
                        @Override
                        public void onSuccess(String data) {
                            log.d(TAG, "onSuccess,deviceParamsSet=" + (ControlManager.getInstance().getDataSendInfo().toString()));
                            DeviceManager.getInstance().sendPropertyCCSetTemp(ControlManager.getInstance().getDataSendInfo().temp_changeable_room_temp_set);
                            DeviceManager.getInstance().sendPropertyCCRoomEnable(ControlManager.getInstance().getDataSendInfo().temp_changeable_room_room_enable);
                        }

                        @Override
                        public void onFail(int errorCode, String msg) {
                            Log.e(TAG, "onRoomEnable fail,code=" + errorCode + ",msg=" + msg);
                            if (mHandler != null) {
                                mHandler.sendEmptyMessage(MSG_WHAT_SETUP_FAIL);
                            }
                        }
                    };
                }
                boolean result = ControlManager.getInstance().enableRoom(enable, SerialInfo.ROOM_CHANGEABLE_ROOM, mCCRoomEnableCallback);
                if (!result) {
                    Toast.makeText(FridgeActivity.this, getString(R.string.toast_set_fail), Toast.LENGTH_LONG).show();
                } else {
//                    String json = getJson(FridgeStreamId.VAR_POWER, getStatus(enable));
//                    RxBus.getInstance().post(BusEvent.MSG_REPORT_FRIDGE, json);
                    sendMsg();
                    closeModeAnimation();
                    if (enable) {
                        if (ControlManager.getInstance().getDataSendInfo().temp_changeable_room_temp_set >= 0) {
                            mTCFridgeRoomView.showAnimation(true, R.mipmap.fan_rotota, 0);
                        } else {
                            mTCFridgeRoomView.showAnimation(true, R.mipmap.snow_rotato, 0);
                        }
                    } else {
                        mTCFridgeRoomView.showAnimation(false, 0, 0);
                    }

                    if (mHandler != null) {
                        mHandler.removeMessages(MSG_WHAT_TCROOM_ANIMATION_CLOSE);
                        mHandler.removeMessages(MSG_WHAT_ALL_ROOM_ANIMATION_CLOSE);
                        mHandler.sendEmptyMessageDelayed(MSG_WHAT_TCROOM_ANIMATION_CLOSE, MAX_ANIMATION_SHOW_TIME);
                    }
                }
            }

            @Override
            public void onMoreLayoutClick() {
                if (mHandler != null) {
                    mHandler.sendEmptyMessage(MSG_WHAT_CLICK_MORE);
                }
            }

            @Override
            public void onSceneChange(int temp, final String name) {
                Log.i(TAG, "mTempChangeableRoomView onTempChange,temp=" + temp + ",name=" + name);
                final String oldName = GlobalParams.getInstance().getSceneChoose();
                GlobalParams.getInstance().setSceneChoose(name);
                if (commodityInspectionRunningProcess()) {
                    return;
                }
                setHolidayButtonEnable(false);
                setSmartButtonEnable(false);
                mControlViewManager.paramsChange();
                if (mCCRoomSceneCallback == null) {
                    mCCRoomSceneCallback = new AppCallback<String>() {
                        @Override
                        public void onSuccess(String data) {

                            log.d(TAG, "onSuccess,deviceParamsSet=" + (ControlManager.getInstance().getDataSendInfo().toString()));
                            DeviceManager.getInstance().sendPropertyCCSetTemp(ControlManager.getInstance().getDataSendInfo().temp_changeable_room_temp_set);
                        }

                        @Override
                        public void onFail(int errorCode, String msg) {
                            Log.e(TAG, "onTempChange fail,code=" + errorCode + ",msg=" + msg);
                            GlobalParams.getInstance().setSceneChoose(oldName);
                            if (mHandler != null) {
                                mHandler.sendEmptyMessage(MSG_WHAT_SETUP_FAIL);
                            }
                        }
                    };
                }
                boolean result = ControlManager.getInstance().setRoomTemp(temp, SerialInfo.ROOM_CHANGEABLE_ROOM, mCCRoomSceneCallback);
                if (!result) {
                    Toast.makeText(FridgeActivity.this, getString(R.string.toast_set_fail), Toast.LENGTH_LONG).show();
                } else {
                    closeModeAnimation();
                    if (temp >= 0) {
                        mTCFridgeRoomView.showAnimation(true, R.mipmap.fan_rotota, 0);
                    } else {
                        mTCFridgeRoomView.showAnimation(true, R.mipmap.snow_rotato, 0);
                    }
                    if (mHandler != null) {
                        mHandler.removeMessages(MSG_WHAT_TCROOM_ANIMATION_CLOSE);
                        mHandler.removeMessages(MSG_WHAT_ALL_ROOM_ANIMATION_CLOSE);
                        mHandler.sendEmptyMessageDelayed(MSG_WHAT_TCROOM_ANIMATION_CLOSE, MAX_ANIMATION_SHOW_TIME);
                    }
                }
            }
        });

        mFreezingRoomView.setOnTempChangeListener(new FreezingRoomView.OnTempChangeListener() {
            @Override
            public void onTempChange(int temp) {
                Log.i(TAG, "mFreezingRoomView onTempChange,temp=" + temp);
                if (commodityInspectionRunningProcess()) {
                    return;
                }
                setHolidayButtonEnable(false);
                setSmartButtonEnable(false);
                mControlViewManager.paramsChange();
                if (mFZRoomTempCallback == null) {
                    mFZRoomTempCallback = new AppCallback<String>() {
                        @Override
                        public void onSuccess(String data) {
                            log.d(TAG, "onSuccess,deviceParamsSet=" + (ControlManager.getInstance().getDataSendInfo().toString()));
                            DeviceManager.getInstance().sendPropertyFZSetTemp(ControlManager.getInstance().getDataSendInfo().freezing_room_temp_set);
                            DeviceManager.getInstance().sendPropertyMode(SerialInfo.MODE_NULL);
                        }

                        @Override
                        public void onFail(int errorCode, String msg) {
                            Log.e(TAG, "onTempChange fail,code=" + errorCode + ",msg=" + msg);
                            if (mHandler != null) {
                                mHandler.sendEmptyMessage(MSG_WHAT_SETUP_FAIL);
                            }
                        }
                    };
                }
                boolean result = ControlManager.getInstance().setRoomTemp(temp, SerialInfo.ROOM_FREEZING_ROOM, mFZRoomTempCallback);
                if (!result) {
                    Toast.makeText(FridgeActivity.this, getString(R.string.toast_set_fail), Toast.LENGTH_LONG).show();
                } else {
//                    String json = getJson(FridgeStreamId.FRE_TEMP, String.valueOf(temp));
//                    RxBus.getInstance().post(BusEvent.MSG_REPORT_FRIDGE, json);
                    sendMsg();
                    closeModeAnimation();
                    mFZFridgeRoomView.showAnimation(true, R.mipmap.snow_rotato, 0);
                    if (mHandler != null) {
                        mHandler.removeMessages(MSG_WHAT_FZROOM_ANIMATION_CLOSE);
                        mHandler.removeMessages(MSG_WHAT_ALL_ROOM_ANIMATION_CLOSE);
                        mHandler.sendEmptyMessageDelayed(MSG_WHAT_FZROOM_ANIMATION_CLOSE, MAX_ANIMATION_SHOW_TIME);
                    }
                }
            }
        });

        mSmartButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (mSmartButtonIgnoreChange || ControlManager.getInstance().getDataSendInfo().mode == SerialInfo.MODE_SMART) {
                    return;
                }
                if (commodityInspectionRunningProcess()) {
                    return;
                }
                setHolidayButtonEnable(false);
                mControlViewManager.paramsChange();
                boolean result = ControlManager.getInstance().enableSmartMode(true);
                if (!result) {
                    Toast.makeText(FridgeActivity.this, getString(R.string.toast_set_fail), Toast.LENGTH_LONG).show();
                } else {
//                    String json = getJson(FridgeStreamId.SAMRT_MODE, "1");
//                    RxBus.getInstance().post(BusEvent.MSG_REPORT_FRIDGE, json);
                    sendMsg();
                    DeviceParamsSet deviceParamsSetTemp = ControlManager.getInstance().getDataSendInfo();
                    DeviceManager.getInstance().sendPropertyFZSetTemp(deviceParamsSetTemp.freezing_room_temp_set);
                    DeviceManager.getInstance().sendPropertyRCSetTemp(deviceParamsSetTemp.cold_closet_temp_set);
                    DeviceManager.getInstance().sendPropertyCCSetTemp(deviceParamsSetTemp.temp_changeable_room_temp_set);
                    DeviceManager.getInstance().sendPropertyRCRoomEnable(true);
                    DeviceManager.getInstance().sendPropertyMode(SerialInfo.MODE_SMART);
                    mCCFridgeRoomView.showAnimation(true, R.mipmap.gif_smart, 1);
                    //  mTCFridgeRoomView.showAnimation(true,R.mipmap.gif_smart,1);
                    mFZFridgeRoomView.showAnimation(true, R.mipmap.gif_smart, 1);

                    if (mHandler != null) {
                        mHandler.removeMessages(MSG_WHAT_CCROOM_ANIMATION_CLOSE);
                        mHandler.removeMessages(MSG_WHAT_TCROOM_ANIMATION_CLOSE);
                        mHandler.removeMessages(MSG_WHAT_FZROOM_ANIMATION_CLOSE);
                        mHandler.removeMessages(MSG_WHAT_ALL_ROOM_ANIMATION_CLOSE);
                        mHandler.sendEmptyMessageDelayed(MSG_WHAT_ALL_ROOM_ANIMATION_CLOSE, MAX_ANIMATION_SHOW_TIME);
                    }
                }
            }
        });

        mHolidayButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (mHolidayButtonIgnoreChange || ControlManager.getInstance().getDataSendInfo().mode == SerialInfo.MODE_HOLIDAY) {
                    return;
                }
                if (commodityInspectionRunningProcess()) {
                    return;
                }
                setSmartButtonEnable(false);
                mControlViewManager.paramsChange();
                if (mHolidayModeCallback == null) {
                    mHolidayModeCallback = new AppCallback<String>() {
                        @Override
                        public void onSuccess(String data) {
                            log.d(TAG, "onSuccess,deviceParamsSet=" + (ControlManager.getInstance().getDataSendInfo().toString()));
                            DeviceParamsSet deviceParamsSetTemp = ControlManager.getInstance().getDataSendInfo();
                            DeviceManager.getInstance().sendPropertyFZSetTemp(deviceParamsSetTemp.freezing_room_temp_set);
                            DeviceManager.getInstance().sendPropertyRCSetTemp(deviceParamsSetTemp.cold_closet_temp_set);
                            DeviceManager.getInstance().sendPropertyCCSetTemp(deviceParamsSetTemp.temp_changeable_room_temp_set);
                            DeviceManager.getInstance().sendPropertyRCRoomEnable(true);
                            DeviceManager.getInstance().sendPropertyMode(SerialInfo.MODE_HOLIDAY);
                        }

                        @Override
                        public void onFail(int errorCode, String msg) {
                            Log.e(TAG, "enableHolidayMode fail,code=" + errorCode + ",msg=" + msg);
                            if (mHandler != null) {
                                mHandler.sendEmptyMessage(MSG_WHAT_SETUP_FAIL);
                            }
                        }
                    };
                }
                boolean result = ControlManager.getInstance().enableHolidayMode(true, mHolidayModeCallback);
                if (!result) {
                    Toast.makeText(FridgeActivity.this, getString(R.string.toast_set_fail), Toast.LENGTH_LONG).show();
                } else {
//                    String json = getJson(FridgeStreamId.HOLIDAY_MODE, "1");
//                    RxBus.getInstance().post(BusEvent.MSG_REPORT_FRIDGE, json);
                    sendMsg();
                    mCCFridgeRoomView.showAnimation(true, R.mipmap.gif_holiday, 1);
                    //  mTCFridgeRoomView.showAnimation(true,R.mipmap.gif_holiday,1);
                    mFZFridgeRoomView.showAnimation(true, R.mipmap.gif_holiday, 1);

                    if (mHandler != null) {
                        mHandler.removeMessages(MSG_WHAT_CCROOM_ANIMATION_CLOSE);
                        mHandler.removeMessages(MSG_WHAT_TCROOM_ANIMATION_CLOSE);
                        mHandler.removeMessages(MSG_WHAT_FZROOM_ANIMATION_CLOSE);
                        mHandler.removeMessages(MSG_WHAT_ALL_ROOM_ANIMATION_CLOSE);
                        mHandler.sendEmptyMessageDelayed(MSG_WHAT_ALL_ROOM_ANIMATION_CLOSE, MAX_ANIMATION_SHOW_TIME);
                    }
                }
            }
        });

        mQuickCoolButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (mQiuckCoolButtonIgnoreChange) {
                    return;
                }
                if (commodityInspectionRunningProcess()) {
                    return;
                }
                mControlViewManager.paramsChange();
                if (mRCRoomQuickCoolCallback == null) {
                    mRCRoomQuickCoolCallback = new AppCallback<String>() {
                        @Override
                        public void onSuccess(String data) {
                            log.d(TAG, "onSuccess,deviceParamsSet=" + (ControlManager.getInstance().getDataSendInfo().toString()));
                            DeviceParamsSet deviceParamsSetTemp = ControlManager.getInstance().getDataSendInfo();
                            DeviceManager.getInstance().sendPropertyRCSetTemp(deviceParamsSetTemp.cold_closet_temp_set);
                            DeviceManager.getInstance().sendPropertyRCRoomEnable(true);
                            DeviceManager.getInstance().sendPropertyMode(SerialInfo.MODE_NULL);
                        }

                        @Override
                        public void onFail(int errorCode, String msg) {
                            Log.e(TAG, "enableQuickCool fail,code=" + errorCode + ",msg=" + msg);
                            if (mHandler != null) {
                                mHandler.sendEmptyMessage(MSG_WHAT_SETUP_FAIL);
                            }
                        }
                    };
                }
                boolean result = ControlManager.getInstance().enableQuickCool(isChecked, mRCRoomQuickCoolCallback);
                if (!result) {
                    Toast.makeText(FridgeActivity.this, getString(R.string.toast_set_fail), Toast.LENGTH_LONG).show();
                } else {
//                    String json = getJson(FridgeStreamId.FASTFRI_MODE, getStatus(isChecked));
//                    RxBus.getInstance().post(BusEvent.MSG_REPORT_FRIDGE, json);
                    sendMsg();
                    if (mHandler != null) {
                        closeModeAnimation();
                        mCCFridgeRoomView.showAnimation(true, R.mipmap.fan_rotota, 0);
                        if (mHandler != null) {
                            mHandler.removeMessages(MSG_WHAT_CCROOM_ANIMATION_CLOSE);
                            mHandler.removeMessages(MSG_WHAT_ALL_ROOM_ANIMATION_CLOSE);
                            mHandler.sendEmptyMessageDelayed(MSG_WHAT_CCROOM_ANIMATION_CLOSE, MAX_ANIMATION_SHOW_TIME);
                        }
                    }
                }
            }
        });

        mQuickFrezzeButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (mQiuckFreezeButtonIgnoreChange) {
                    return;
                }
                if (commodityInspectionRunningProcess()) {
                    return;
                }
                mControlViewManager.paramsChange();

                if (mFZRoomQuickColdCallback == null) {
                    mFZRoomQuickColdCallback = new AppCallback<String>() {
                        @Override
                        public void onSuccess(String data) {
                            log.d(TAG, "onSuccess,deviceParamsSet=" + (ControlManager.getInstance().getDataSendInfo().toString()));
                            DeviceManager.getInstance().sendPropertyFZSetTemp(ControlManager.getInstance().getDataSendInfo().freezing_room_temp_set);
                            DeviceManager.getInstance().sendPropertyMode(SerialInfo.MODE_NULL);
                        }

                        @Override
                        public void onFail(int errorCode, String msg) {
                            Log.e(TAG, "enableQuickFreeze fail,code=" + errorCode + ",msg=" + msg);
                            if (mHandler != null) {
                                mHandler.sendEmptyMessage(MSG_WHAT_SETUP_FAIL);
                            }
                        }
                    };
                }
                boolean result = ControlManager.getInstance().enableQuickFreeze(isChecked, mFZRoomQuickColdCallback);
                if (!result) {
                    Toast.makeText(FridgeActivity.this, getString(R.string.toast_set_fail), Toast.LENGTH_LONG).show();
                } else {
//                    String json = getJson(FridgeStreamId.FASTFRE_MODE, getStatus(isChecked));
//                    RxBus.getInstance().post(BusEvent.MSG_REPORT_FRIDGE, json);
                    sendMsg();
                    if (mHandler != null) {
                        closeModeAnimation();
                        mFZFridgeRoomView.showAnimation(true, R.mipmap.snow_rotato, 0);
                        if (mHandler != null) {
                            mHandler.removeMessages(MSG_WHAT_FZROOM_ANIMATION_CLOSE);
                            mHandler.removeMessages(MSG_WHAT_ALL_ROOM_ANIMATION_CLOSE);
                            mHandler.sendEmptyMessageDelayed(MSG_WHAT_FZROOM_ANIMATION_CLOSE, MAX_ANIMATION_SHOW_TIME);
                        }
                    }
                }
            }
        });

        mCleanButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (ControlManager.getInstance().isOneKeyCleanRunning()) {
                    return;
                }
                if (mCleanButtonIgnoreChange) {
                    return;
                }
                if (commodityInspectionRunningProcess()) {
                    return;
                }
                mControlViewManager.paramsChange();
                ControlManager.getInstance().enableOneKeyClean(true);
                startCleanAnimation();
                if (mHandler != null) {
                    mHandler.sendEmptyMessageDelayed(MSG_WHAT_CLEAN_ANIMATION_CLOSE, MAX_ANIMATION_SHOW_TIME);
                }
            }
        });

        mDrawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(View drawerView) {
                mUserTipsLayout.setVisibility(View.GONE);
//                  mDrawerLayout.setScrimColor(Color.TRANSPARENT);
//                   mDrawerLayout.setStatusBarBackgroundColor(getResources().getColor(android.R.color.transparent));
            }

            @Override
            public void onDrawerClosed(View drawerView) {
//                  mDrawerLayout.setScrimColor(getResources().getColor(R.color.white));
//                   mDrawerLayout.setStatusBarBackgroundColor(getResources().getColor(android.R.color.white));
            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });

        mProgressWheel.setOnClickListener(this);
        mProgressWheel.setProgress(ControlManager.getInstance().getFilterLifeUsePercent(), ProgressWheel.CHANGE_TYPE_DECREASE);
        startTimer();
        register();
        if (mHandler != null) {
            mHandler.sendEmptyMessageDelayed(MSG_WHAT_USER_TIPS_CLOSE, 10000);
        }
        startService(new Intent(FridgeActivity.this, AidlService.class));
    }


    private String getStatus(boolean enable) {
        if (enable)
            return "1";
        return "0";
    }

    private String getJson(String stream_id, String current_value) {
        JSONObject object = new JSONObject();
        try {
            object.put("code", 0);
            object.put("error_code", 202);
            object.put("mfrs", ApkUtil.getManufacturer());
            JSONArray array = new JSONArray();
            JSONObject obj = new JSONObject();
            obj.put("current_value", current_value);
            obj.put("stream_id", stream_id);
            array.put(obj);
            object.put("streams", array);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object.toString();
    }

    private void sendMsg() {
        String json = ToolUtil.queryDevice("status");
        if (!TextUtils.isEmpty(json)) {
            RxBus.getInstance().post(BusEvent.MSG_REPORT_FRIDGE, json);
        }
    }

    private String getJson(HashMap<String, String> map) {
        JSONObject object = new JSONObject();
        try {
            object.put("code", 0);
            object.put("error_code", 202);
            object.put("mfrs", ApkUtil.getManufacturer());
            JSONArray array = new JSONArray();
            for (Map.Entry<String, String> entry : map.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                JSONObject obj = new JSONObject();
                obj.put("current_value", value);
                obj.put("stream_id", key);
                array.put(obj);
            }
            object.put("streams", array);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object.toString();
    }

    /***
     * 不同型号冰箱页面
     */
    private void modelSwitch() {
        switch (DeviceConfig.MODEL) {
            case AppConfig.VIOMI_FRIDGE_V1:
                mQuickCoolButton.setVisibility(View.GONE);
                mQuickFrezzeButton.setVisibility(View.GONE);
                break;

            case AppConfig.VIOMI_FRIDGE_V2:
                mFirdgeView.setBackgroundResource(R.mipmap.fridge_four_box);
                mQuickCoolButton.setVisibility(View.VISIBLE);
                mQuickFrezzeButton.setVisibility(View.VISIBLE);
                mCleanButton.setVisibility(View.GONE);
                break;

            case AppConfig.VIOMI_FRIDGE_V3:
            case AppConfig.VIOMI_FRIDGE_V31:
                mFirdgeView.setBackgroundResource(R.mipmap.fridge_two_box);
                mQuickCoolButton.setVisibility(View.VISIBLE);
                mQuickFrezzeButton.setVisibility(View.VISIBLE);
                mCleanButton.setVisibility(View.GONE);
                break;

            case AppConfig.VIOMI_FRIDGE_V4:
                mFirdgeView.setBackgroundResource(R.mipmap.fridge_two_box);
                mQuickCoolButton.setVisibility(View.VISIBLE);
                mQuickFrezzeButton.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void onShowSceneDialog() {
        if (mSceneDialog == null) {
            Log.e(TAG, "stop animati############");
            mSceneDialog = new SceneDialog(FridgeActivity.this, R.style.fidge_dialog);
            mSceneDialog.setCancelable(false);
            mSceneDialog.setSaveClickListener(new SceneDialog.OnSaveClickListener() {
                @Override
                public void onClick() {
                    mTempChangeableRoomView.setSceneView(ControlManager.getInstance().getDataSendInfo().temp_changeable_room_temp_set);
                }
            });
        }
        mSceneDialog.show();

    }

    private boolean commodityInspectionRunningProcess() {
        if (ControlManager.getInstance().isCommodityInspectionRunning()) {
            Toast.makeText(FridgeActivity.this, R.string.toast_commodity_inspection_running, Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }

    /***
     * 使能智能模式，代码设置避免触发OnCheckedChangeListener，下同
     * @param enable
     */
    private void setSmartButtonEnable(boolean enable) {
        mSmartButtonIgnoreChange = true;
        mSmartButton.setChecked(enable);
        mSmartButtonIgnoreChange = false;
    }

    private void setHolidayButtonEnable(boolean enable) {
        mHolidayButtonIgnoreChange = true;
        mHolidayButton.setChecked(enable);
        mHolidayButtonIgnoreChange = false;
    }

    private void setQuickCoolButtonEnable(boolean enable) {
        mQiuckCoolButtonIgnoreChange = true;
        mQuickCoolButton.setChecked(enable);
        mQiuckCoolButtonIgnoreChange = false;
    }

    private void setQuickFreezeButtonEnable(boolean enable) {
        mQiuckFreezeButtonIgnoreChange = true;
        mQuickFrezzeButton.setChecked(enable);
        mQiuckFreezeButtonIgnoreChange = false;
    }

    private void setCleanButtonEnable(boolean enable) {
        mCleanButtonIgnoreChange = true;
        mCleanButton.setChecked(enable);
        mCleanButtonIgnoreChange = false;
        if (enable) {
            mCleanButton.setText(getString(R.string.button_cleanning));
        } else {
            mCleanButton.setText(getString(R.string.button_one_key_clean));
        }
    }

    /***
     * 刷新界面
     * @param info
     */
    public void refreshView(DeviceParamsSet info) {
        if (info == null) {
            return;
        }
        if (mControlViewManager.isSetting()) {
            log.d(TAG, "is settting");
            return;
        }
        log.d(TAG, "model=" + DeviceConfig.MODEL + ",deviceParamsSet=" + info.toString());
        DeviceParamsGet deviceParamsGet = ControlManager.getInstance().getDataReceiveInfo();
        log.d(TAG, "@@@@@@@@@,deviceParamsGet=" + deviceParamsGet.toString());
        mFreezingRoomView.setTemp(info.freezing_room_temp_set);
        mColdClosetView.setTemp(info.cold_closet_temp_set, info.cold_closet_room_enable);
        mTempChangeableRoomView.setTemp(info.temp_changeable_room_temp_set, info.temp_changeable_room_room_enable);

        mCCFridgeRoomView.setTemp(info.cold_closet_temp_set, info.cold_closet_room_enable);
        mTCFridgeRoomView.setTemp(info.temp_changeable_room_temp_set, info.temp_changeable_room_room_enable);
        mFZFridgeRoomView.setTemp(info.freezing_room_temp_set);
        if (info.mode == SerialInfo.MODE_SMART) {
            setSmartButtonEnable(true);
            setHolidayButtonEnable(false);
            mXiaoxianImage.setImageResource(R.mipmap.backgroud_smart);
        } else if (info.mode == SerialInfo.MODE_HOLIDAY) {
            setSmartButtonEnable(false);
            setHolidayButtonEnable(true);
            mXiaoxianImage.setImageResource(R.mipmap.backgroud_holiday);
        } else {
            setSmartButtonEnable(false);
            setHolidayButtonEnable(false);
            mXiaoxianImage.setImageResource(R.mipmap.xiaoxian);
        }
        if (ControlManager.getInstance().isOneKeyCleanRunning()) {
            setCleanButtonEnable(true);
        } else {
            setCleanButtonEnable(false);
        }

        log.d(TAG, "quick_cool=" + info.quick_cold + ",quick_freeze=" + info.quick_freeze);
        setQuickCoolButtonEnable(info.quick_cold);
        setQuickFreezeButtonEnable(info.quick_freeze);

        mColdClosetView.setIcedDrinkStatus(info.iced_drink);

//       deviceParamsGet.error=3+4+8+16+32+64+128+256;
        DeviceError deviceError = new DeviceError();
        deviceError.parserErrorCode(deviceParamsGet.error);
        String statusStr = "";
        if (deviceError.error_traffic) {
            statusStr += getString(R.string.text_error_traffic) + "，";
        }
        if (deviceError.error_ccroom_sencor) {
            statusStr += getString(R.string.text_error_ccroom_sencor) + "，";
        }
        if (deviceError.error_tcroom_sencor) {
            statusStr += getString(R.string.text_error_tcroom_sencor) + "，";
        }
        if (deviceError.error_fzroom_sencor) {
            statusStr += getString(R.string.text_error_fzroom_sencor) + "，";
        }
        if (deviceError.error_cc_defrost_sencor) {
            statusStr += getString(R.string.text_error_cc_defrost_sencor) + "，";
        }
        if (deviceError.error_fz_defrost_sencor) {
            statusStr += getString(R.string.text_error_fz_defrost_sencor) + "，";
        }
        if (deviceError.error_indoor_sencor) {
            statusStr += getString(R.string.text_error_indoor_sencor) + "，";
        }
        if (deviceError.error_air_door) {
            statusStr += getString(R.string.text_error_air_door) + "，";
        }
        if (deviceError.error_ccroom_fan) {
            statusStr += getString(R.string.text_error_ccroom_fan) + "，";
        }
        if (deviceError.error_tcroom_fan) {
            statusStr += getString(R.string.text_error_tcroom_fan) + "，";
        }
        if (deviceError.error_fzroom_fan) {
            statusStr += getString(R.string.text_error_fzroom_fan) + "，";
        }
        if (statusStr.length() == 0) {
            mStatusView.setVisibility(View.GONE);
        } else {
            mStatusView.setVisibility(View.VISIBLE);
            statusStr = statusStr.substring(0, statusStr.length() - 1);
            mStatusView.setText(statusStr);
        }
    }

    /***
     * 关闭智能和假日模式动画
     */
    private void closeModeAnimation() {
        if (mTCFridgeRoomView.getType() == 1) {
            mTCFridgeRoomView.showAnimation(false, 0, 0);
        }
        if (mCCFridgeRoomView.getType() == 1) {
            mCCFridgeRoomView.showAnimation(false, 0, 0);
        }
        if (mFZFridgeRoomView.getType() == 1) {
            mFZFridgeRoomView.showAnimation(false, 0, 0);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mHandler != null) {
            mHandler.sendEmptyMessageDelayed(MSG_WHAT_REFER_FILTER, 100);
        }
    }

    private void refreFilter() {
        int progress = ControlManager.getInstance().getFilterLifeUsePercent();
        mProgressWheel.setProgress(progress, ProgressWheel.CHANGE_TYPE_DECREASE);
        log.d(TAG, "refreFilter=" + progress);
        if (progress >= 100) {
            mProgressWheel.setFlueColor(0xFFFF0000);
            mFilterTextView.setTextColor(getResources().getColor(R.color.red));
            mFilterTextView.setText(R.string.text_need_alter_filter);
        } else if (progress >= 90) {
            mProgressWheel.setProgressColor(0xFFFFA500);
            mFilterTextView.setTextColor(getResources().getColor(R.color.orange));
            mFilterTextView.setText(R.string.text_filter_status);
        } else {
            mProgressWheel.setProgressColor(0xFF00d0be);
            mFilterTextView.setTextColor(getResources().getColor(R.color.control_enable_color));
            mFilterTextView.setText(R.string.text_filter_status);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mControlViewManager != null) {
            mControlViewManager.close();
            mControlViewManager = null;
        }

        if (mSubscription != null) {
            mSubscription.unsubscribe();
            mSubscription = null;
        }

        mFreezingRoomView.close();
        mColdClosetView.close();
        mTempChangeableRoomView.close();
        mSnowingSurfaceView1 = null;
        mSnowingSurfaceView2 = null;
        stopTimer();
        startCleanAnimation();
        mRCRoomTempCallback = null;
        mRCRoomEnableCallback = null;
        mRCRoomQuickCoolCallback = null;
        mCCRoomTempCallback = null;
        mCCRoomEnableCallback = null;
        mFZRoomTempCallback = null;
        mFZRoomQuickColdCallback = null;
        mIcedDrinkCallBack = null;
        mCCRoomSceneCallback = null;
        mSmartModeCallback = null;
        mHolidayModeCallback = null;
        if (mSceneDialog != null) {
            mSceneDialog.dismiss();
            mSceneDialog = null;
        }
        if (mFilterDialog != null) {
            mFilterDialog.dismiss();
            mFilterDialog = null;
        }
        unRegister();
    }

    @Override
    protected void processMsg(Message msg) {
        Log.e(TAG, "stop animation________=" + msg.what);
        switch (msg.what) {
            case MSG_WHAT_CCROOM_ANIMATION_CLOSE:
                if (mCCFridgeRoomView != null) {
                    mCCFridgeRoomView.showAnimation(false, 0, 0);
                }
                break;
            case MSG_WHAT_TCROOM_ANIMATION_CLOSE:
                if (mTCFridgeRoomView != null) {
                    mTCFridgeRoomView.showAnimation(false, 0, 0);
                }
                break;
            case MSG_WHAT_FZROOM_ANIMATION_CLOSE:
                if (mFZFridgeRoomView != null) {
                    mFZFridgeRoomView.showAnimation(false, 0, 0);
                }
                break;

            case MSG_WHAT_ALL_ROOM_ANIMATION_CLOSE:
                if (mFZFridgeRoomView != null) {
                    mFZFridgeRoomView.showAnimation(false, 0, 0);
                }
                if (mCCFridgeRoomView != null) {
                    mCCFridgeRoomView.showAnimation(false, 0, 0);
                }
                if (mTCFridgeRoomView != null) {
                    mTCFridgeRoomView.showAnimation(false, 0, 0);
                }
                break;
            case MSG_WHAT_UPDATE_TIPS:
                String text = (String) msg.obj;
                mChatView.setText(text);
                break;
            case MSG_WHAT_CLEAN_ANIMATION_CLOSE:
                Log.e(TAG, "stop animation________=" + msg.what);
                stopCleanAnimation();
                break;

            case MSG_WHAT_SETUP_FAIL:
                Toast.makeText(FridgeActivity.this, getString(R.string.toast_set_fail), Toast.LENGTH_LONG).show();
                break;
            case MSG_WHAT_CLICK_MORE:
                onShowSceneDialog();
                break;
            case MSG_WHAT_USER_TIPS_CLOSE:
                mUserTipsLayout.setVisibility(View.GONE);
                break;
            case MSG_WHAT_REFER_FILTER:
                refreFilter();
                break;
        }
    }

    private BroadcastReceiver myReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("@@@@@", "intent.getAction()=" + intent.getAction());
            switch (intent.getAction()) {
                case BroadcastAction.ACTION_REFRE_SCENE:
                    mTempChangeableRoomView.setSceneView(ControlManager.getInstance().getDataSendInfo().temp_changeable_room_temp_set);
                    break;
            }
        }
    };

    private void register() {
        IntentFilter intentFilter = new IntentFilter(BroadcastAction.ACTION_APP_UPGRADE_CHECK);
        intentFilter.addAction(BroadcastAction.ACTION_SYSTEM_UPGRADE_CHECK);
        intentFilter.addAction(BroadcastAction.ACTION_REFRE_SCENE);

        LocalBroadcastManager.getInstance(this).registerReceiver(myReceiver, intentFilter);
    }

    private void unRegister() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(myReceiver);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mSnowingSurfaceView1.startFall();
        mSnowingSurfaceView2.startFall();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mSnowingSurfaceView1.stopFall();
        mSnowingSurfaceView2.stopFall();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_icon:
                finish();
                break;

            case R.id.close_icon:
                //  mNormalView.setBackgroundColor(getResources().getColor(R.color.frige_backgroud_color));
                mDrawerLayout.closeDrawer(Gravity.LEFT);
                break;

            case R.id.firdge_view:
                // mNormalView.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                mDrawerLayout.openDrawer(Gravity.LEFT);
//
//                ControlManager.getInstance().getDataReceiveInfo().error=(int) Math.pow(2,testCount);
//                DeviceManager.getInstance().sendFaultHappen((int) Math.pow(2,testCount));
//                testCount++;
//                if(testCount>=13){
//                    testCount=0;
//                }
                break;

            case R.id.progress_bar:
                showFilterDialog();
                break;

            case R.id.tips_colse:
                mUserTipsLayout.setVisibility(View.GONE);
                break;

        }
    }


    private void showFilterDialog() {

        mFilterDialog = new FilterDialog(FridgeActivity.this, R.style.activity_dialogStyle);
        mFilterDialog.setCancelable(false);
        mFilterDialog.setResetClickListener(new FilterDialog.OnResetClickListener() {
            @Override
            public void onClick() {
                final View view = LayoutInflater.from(FridgeActivity.this).inflate(R.layout.dialog_confirm, null);
                mConfirmDialog = new BaseDialog(FridgeActivity.this) {
                    @Override
                    public void setView() {
                        mConfirmDialog.setContentView(view);
                    }
                };
                mConfirmDialog.show();
                TextView confirmView = (TextView) view.findViewById(R.id.confirm_button);
                TextView cancelView = (TextView) view.findViewById(R.id.cancel_button);
                cancelView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mConfirmDialog.hideBar();
                        mConfirmDialog.dismiss();
                        mConfirmDialog = null;
                    }
                });
                confirmView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mConfirmDialog.dismiss();
                        mConfirmDialog = null;
                        if (mFilterDialog != null) {
                            mFilterDialog.dismiss();
                            mFilterDialog = null;
                        }
                        GlobalParams.getInstance().setFilterLifeTime(0);
                        refreFilter();
                        Toast.makeText(FridgeActivity.this, "滤芯已复位", Toast.LENGTH_SHORT).show();
//                            DeviceManager.getInstance().sendFaultHappen(1);
                    }
                });
            }
        });
        mFilterDialog.show();


    }

    private void startTimer() {
        stopTimer();
        mTimer = new Timer();
        mTimerTask = new TimerTask() {
            @Override
            public void run() {
                if (mHandler != null) {
                    String text = FridgeTipsManager.getInstance().getTipString();
                    Message message = mHandler.obtainMessage();
                    message.obj = text;
                    message.what = MSG_WHAT_UPDATE_TIPS;
                    mHandler.sendMessage(message);
                }
            }
        };
        mTimer.schedule(mTimerTask, 3000, 10000);
    }

    private void stopTimer() {
        if (mTimerTask != null) {
            mTimerTask.cancel();
            mTimerTask = null;
        }
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
    }


    private void startCleanAnimation() {
        mIsCleanAnimationStop = false;
        mWaveImageView1.setVisibility(View.VISIBLE);
        mWaveImageView2.setVisibility(View.VISIBLE);
        mWaveImageView3.setVisibility(View.VISIBLE);
        mWaveImageView1.startAnimation(mAnimation1);
        mWaveImageView2.startAnimation(mAnimation1);
        mWaveImageView3.startAnimation(mAnimation1);
        mAnimation1.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (mIsCleanAnimationStop) {
                    return;
                }
                mWaveImageView1.startAnimation(mAnimation2);
                mWaveImageView2.startAnimation(mAnimation2);
                mWaveImageView3.startAnimation(mAnimation2);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        mAnimation2.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (mIsCleanAnimationStop) {
                    return;
                }
                mWaveImageView1.startAnimation(mAnimation1);
                mWaveImageView2.startAnimation(mAnimation1);
                mWaveImageView3.startAnimation(mAnimation1);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

    }

    private void stopCleanAnimation() {
        mIsCleanAnimationStop = true;
        mWaveImageView1.clearAnimation();
        mWaveImageView2.clearAnimation();
        mWaveImageView3.clearAnimation();
        mWaveImageView1.setVisibility(View.GONE);
        mWaveImageView2.setVisibility(View.GONE);
        mWaveImageView3.setVisibility(View.GONE);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == event.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
