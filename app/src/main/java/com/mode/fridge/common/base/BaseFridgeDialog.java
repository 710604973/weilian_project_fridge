package com.mode.fridge.common.base;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.mode.fridge.common.rxbus.BusEvent;
import com.mode.fridge.common.rxbus.RxBus;
import com.mode.fridge.utils.ToastUtil;
import com.mode.fridge.utils.ToolUtil;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 全局 Dialog
 * Created by William on 2018/1/31.
 */
public abstract class BaseFridgeDialog extends DialogFragment {
    private static final int MIN_CLICK_DELAY_TIME = 1000;// 两次点击间隔
    private long lastClickTime;// 上次点击时间
    protected int layoutId;// 布局 id
    protected Unbinder unbinder;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initWithOnCreate();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(layoutId, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);
        unbinder = ButterKnife.bind(this, view);
        initWithOnCreateDialog(view);
        RxBus.getInstance().post(BusEvent.MSG_STOP_SCREEN_TIMER);
        return builder.create();
    }



    @Override
    public void onResume() {
        super.onResume();
//        Window dialogWindow = getDialog().getWindow();
//        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
////        dialogWindow.setGravity(Gravity.LEFT | Gravity.TOP);
////        lp.x = 0;
////        lp.y = 0;
//        lp.width = lp.MATCH_PARENT;
//        lp.height = 600;
////        lp.alpha = 0.7f;
//        dialogWindow.setAttributes(lp);
        Window window = getDialog().getWindow();
        if (window != null) {
            window.setBackgroundDrawableResource(android.R.color.transparent);
            WindowManager.LayoutParams windowParams = window.getAttributes();
            windowParams.dimAmount = 0.5f;
            windowParams.width= ToolUtil.getScreenWidth(getActivity());
            window.setAttributes(windowParams);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        // 设置背景
//        Window window = getDialog().getWindow();
//        if (window != null) {
//            window.setBackgroundDrawableResource(android.R.color.transparent);
//            WindowManager.LayoutParams windowParams = window.getAttributes();
//            windowParams.dimAmount = 0.5f;
////            windowParams.width=ToolUtil.getScreenWidth(getActivity());
//            window.setAttributes(windowParams);
//        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
//        if (getActivity() != null) {
//            RefWatcher refWatcher = FridgeApplication.getRefWatcher(getActivity());
//            refWatcher.watch(this);
//        }
        ToastUtil.cancel();
        RxBus.getInstance().post(BusEvent.MSG_START_SCREEN_TIMER);// 重置屏保时间
    }

    protected abstract void initWithOnCreate();

    protected abstract void initWithOnCreateDialog(View view);

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