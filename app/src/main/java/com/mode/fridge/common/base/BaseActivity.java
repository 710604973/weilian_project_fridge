package com.mode.fridge.common.base;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mode.fridge.R;
import com.mode.fridge.common.rxbus.BusEvent;
import com.mode.fridge.common.rxbus.RxBus;
import com.mode.fridge.utils.ToastUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.support.DaggerAppCompatActivity;

/**
 * 全局 Activity
 * Created by William on 2017/12/29.
 */
@SuppressLint("Registered")
public class BaseActivity extends DaggerAppCompatActivity {
    private static final int MIN_CLICK_DELAY_TIME = 1000;// 重复点击间隔
    private long lastClickTime;// 上次点击时间
    protected int layoutId;// 布局 id
    protected String mTitle = "";// 标题栏标题

    @Nullable
    @BindView(R.id.title_bar_title)
    protected TextView mTitleTextView;// 标题

    @Nullable
    @BindView(R.id.title_bar_back)
    protected ImageView mBackImageView;// 返回键

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layoutId);
        ButterKnife.bind(this);
        if (mTitleTextView != null) mTitleTextView.setText(mTitle);
        if (mBackImageView != null) mBackImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_OK,new Intent());
                finish();
            }
        });
//        PushAgent.getInstance(this).onAppStart();// 友盟统计
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
//        MobclickAgent.onPause(this);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ToastUtil.cancel();
//        RefWatcher refWatcher = FridgeApplication.getRefWatcher(this);
//        refWatcher.watch(this);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) { // 有触摸时会调用此方法
        RxBus.getInstance().post(BusEvent.MSG_SCREEN_SAVER_UPDATE);// 重置屏保时间
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 两次点击间隔不能少于 1000 毫秒（防止重复点击）
     */
    protected boolean isRepeatedClick() {
        boolean flag = true;
        long curClickTime = System.currentTimeMillis();
        if ((curClickTime - lastClickTime) >= MIN_CLICK_DELAY_TIME) {
            flag = false;
        }
        lastClickTime = curClickTime;
        return flag;
    }
}