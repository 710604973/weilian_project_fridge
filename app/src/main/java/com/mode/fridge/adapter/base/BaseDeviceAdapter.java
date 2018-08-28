package com.mode.fridge.adapter.base;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;

import com.miot.common.abstractdevice.AbstractDevice;

import java.util.ArrayList;
import java.util.List;

import rx.subscriptions.CompositeSubscription;

public abstract class BaseDeviceAdapter extends PagerAdapter implements ViewPager.OnPageChangeListener {
    private static final String TAG = BaseDeviceAdapter.class.getSimpleName();
    /**
     * 上下文对象
     */
    protected Context mContext;
    protected CompositeSubscription mCompositeSubscription;
    protected ArrayList<AbstractDevice> dataList = new ArrayList<>();
    protected Resources res;

    public BaseDeviceAdapter(Context mContext) {
        this.mContext = mContext;
        res = mContext.getResources();
        mCompositeSubscription = new CompositeSubscription();
    }


    public void setDatas(List<AbstractDevice> list) {
        if (list.size() <= 0) {
            dataList.clear();
            notifyDataSetChanged();
            return;
        } else {
            dataList.clear();
            dataList.addAll(list);
            notifyDataSetChanged();
        }
    }

    /**
     * 滑动监听的回调接口
     */
    private OnPageSelectListener listener;

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {

    }

    @Override
    public int getCount() {
        return dataList == null ? 0 : dataList.size();
//        return Integer.MAX_VALUE;
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        Log.i("info", "===================isViewFromObject()");
        return view == object;
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        Log.i("info", "===================onPageSelected()");
        // 回调选择的接口
        if (listener != null) {
            listener.select(position);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    /**
     * 当将某一个作为最中央时的回调
     *
     * @param listener
     */
    public void setOnPageSelectListener(OnPageSelectListener listener) {
        this.listener = listener;
    }

    /**
     * dp 转 px
     *
     * @param dp
     * @return
     */
    public int dp2px(int dp) {
        int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, mContext.getResources().getDisplayMetrics());
        return px;
    }

    public void close(){
//        if (mCompositeSubscription != null) {
//            mCompositeSubscription.unsubscribe();// 统一取消订阅
//            mCompositeSubscription = null;
//        }
    }
}
