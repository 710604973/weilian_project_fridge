package com.mode.fridge.adapter;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.miot.common.abstractdevice.AbstractDevice;
import com.mode.fridge.R;
import com.mode.fridge.adapter.base.BaseDeviceAdapter;
import com.mode.fridge.bean.prop.RangeHoodProp;
import com.mode.fridge.repository.RangeHoodRepository;
import com.mode.fridge.utils.RxSchedulerUtil;
import com.mode.fridge.utils.logUtil;
import com.mode.fridge.view.EaseSwitchButton;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class HoodAdapter extends BaseDeviceAdapter implements ViewPager.OnPageChangeListener {
    private static final String TAG = HoodAdapter.class.getSimpleName();
    protected CompositeSubscription mCompositeSubscription1;

    public HoodAdapter(Context mContext) {
        super(mContext);
        mCompositeSubscription1 = new CompositeSubscription();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Log.i("info", "===================container:" + container);
        position %= dataList.size();
        if (position < 0) {
            position = dataList.size() + position;
        }
        AbstractDevice data = dataList.get(position);

        ViewHolder viewHolder = null;
        View view = LayoutInflater.from(mContext).inflate(
                R.layout.iot_hood_machine, null);
        if (viewHolder == null) {
            viewHolder = new ViewHolder(view);
        }
        bindView(viewHolder, data);
        container.addView(view, WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT);
        return view;
    }


    private void bindView(ViewHolder holder, final AbstractDevice data) {
        String name = data.getDevice().getName();
        holder.tv_title.setText(name);
        holder.tv_status.setText("离线");
        if (data.isOnline()) {
            String model = data.getDeviceModel();
            String mDid = data.getDeviceId();
            holder.tv_off_on.setEnabled(false);
            holder.switchButton.setEnabled(false);
//            holder.switchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//                @Override
//                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                    light(buttonView, isChecked, mDid);
//                }
//            });

            holder.switchButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean checked = (boolean) v.getTag();
                    light((EaseSwitchButton) v, !checked, mDid);
                }
            });

            holder.tv_off_on.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean isOpen = (boolean) v.getTag();
                    String param = String.valueOf(0);
                    if (isOpen) {
                        param = String.valueOf(2);
                    }
                    Subscription subscription = RangeHoodRepository.setPower(param, mDid)
                            .compose(RxSchedulerUtil.SchedulersTransformer1())
                            .onTerminateDetach()
                            .subscribe(rpcResult -> {
                                if (rpcResult != null) {

                                }
                            }, throwable -> {
                                logUtil.e(TAG, throwable.getMessage());
                            });
                    mCompositeSubscription1.add(subscription);
                }
            });
            Subscription subscription = Observable.interval(0, 5, TimeUnit.SECONDS)
                    .subscribeOn(Schedulers.io())
                    .onTerminateDetach()
                    .flatMap(aLong -> RangeHoodRepository.getProp(mDid))
                    .filter(rpcResult ->
                            rpcResult.getCode() == 0 && rpcResult.getList().size() > 0
                    )
                    .onTerminateDetach()
                    .map(rpcResult -> new RangeHoodProp(rpcResult.getList()))
                    .observeOn(AndroidSchedulers.mainThread())
                    .onTerminateDetach()
                    .subscribe(rangeHoodProp -> {
                        if (rangeHoodProp != null) {
                            fillViewHolder(holder, name, mDid, rangeHoodProp);
                        }
                    });
            mCompositeSubscription1.add(subscription);
        } else {
//            holder.tv_title.setText(data.getDevice().getName());
//            holder.tv_status.setText("离线");
            holder.tv_model.setText("--");
            holder.tv_off_on.setEnabled(false);
            holder.switchButton.setEnabled(false);
        }
    }

    private void fillViewHolder(ViewHolder holder,
                                String name, String mDid, RangeHoodProp hoodProp) {
        String PowerState = hoodProp.getPower_state();
        holder.tv_off_on.setEnabled(true);
        holder.switchButton.setEnabled(true);
        if (PowerState.equals("0")) {//关机
            holder.tv_off_on.setText("开启");
            holder.tv_status.setText("关机");
            holder.tv_off_on.setTag(true);
        } else {
            if (PowerState.equals("1")) {
                holder.tv_status.setText("待机");
            } else {
                holder.tv_status.setText("工作中");
            }
            holder.tv_off_on.setText("关闭");
            holder.tv_off_on.setTag(false);
        }
        String mode = "";
        // 风挡
        switch (hoodProp.getWind_state()) {
            case "0": // 关闭
                mode = res.getString(R.string.iot_range_hood_close);
                break;
            case "1": // 低档
                mode = res.getString(R.string.iot_range_hood_low_desc);
                break;
            case "4": // 爆炒
                mode = res.getString(R.string.iot_range_hood_fry_desc);
                break;
            case "16": // 高档
                mode = res.getString(R.string.iot_range_hood_high_desc);
                break;
        }

        if (!TextUtils.isEmpty(mode)) {
            holder.tv_model.setText(mode);
        }
        // 灯光
        switch (hoodProp.getLight_state()) {
            case "0":
//                holder.switchButton.setChecked(false);
                holder.switchButton.closeSwitch();
                holder.switchButton.setTag(false);
                break;
            case "1":
//                holder.switchButton.setChecked(true);
                holder.switchButton.openSwitch();
                holder.switchButton.setTag(true);
                break;
        }
    }


    private void light(EaseSwitchButton buttonView, boolean checked, String did) {
        String param = String.valueOf(0);
        if (checked) {
            param = String.valueOf(1);
        }
        Subscription subscription = RangeHoodRepository.setLight(param, did)
                .compose(RxSchedulerUtil.SchedulersTransformer1())
                .onTerminateDetach()
                .subscribe(rpcResult -> {
                    if (rpcResult != null && rpcResult.getCode() == 0) {
//                        buttonView.setChecked(!checked);
                        if (checked) {
                            buttonView.openSwitch();
                        } else {
                            buttonView.closeSwitch();
                        }
                    }
//                    else {
//                        buttonView.setChecked(checked);
//                    }
                }, throwable -> {
                    logUtil.e(TAG, throwable.getMessage());
                });
        mCompositeSubscription1.add(subscription);
    }


    class ViewHolder {
        TextView tv_title;
        TextView tv_status;
        TextView tv_model;
        TextView tv_off_on;
        EaseSwitchButton switchButton;

        public ViewHolder(View view) {
            tv_title = (TextView) view.findViewById(R.id.tv_title);
            tv_status = (TextView) view.findViewById(R.id.tv_status);
            tv_model = (TextView) view.findViewById(R.id.tv_model);
            tv_off_on = (TextView) view.findViewById(R.id.tv_off_on);
            switchButton = (EaseSwitchButton) view.findViewById(R.id.room_setting_switch);
        }
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


    @Override
    public void close() {
        super.close();
        if (mCompositeSubscription1 != null) {
            mCompositeSubscription1.clear();
            mCompositeSubscription1.unsubscribe();// 统一取消订阅
            mCompositeSubscription1 = null;
        }
    }

    //    public void close(){
//        if (mCompositeSubscription1 != null) {
//            mCompositeSubscription1.unsubscribe();// 统一取消订阅
//            mCompositeSubscription1 = null;
//        }
//    }
}
