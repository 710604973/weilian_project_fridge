package com.mode.fridge.adapter;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.miot.common.abstractdevice.AbstractDevice;
import com.mode.fridge.R;
import com.mode.fridge.adapter.base.BaseDeviceAdapter;
import com.mode.fridge.bean.prop.FanProp;
import com.mode.fridge.repository.FanRepository;
import com.mode.fridge.utils.RxSchedulerUtil;
import com.mode.fridge.utils.logUtil;
import com.mode.fridge.view.EaseSwitchButton;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class FanMachineAdapter extends BaseDeviceAdapter implements ViewPager.OnPageChangeListener {
    private static final String TAG = FanMachineAdapter.class.getSimpleName();

    private static final int TIME = 65535;

    public FanMachineAdapter(Context mContext) {
        super(mContext);
        this.mContext = mContext;
    }


    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        position %= dataList.size();
        if (position < 0) {
            position = dataList.size() + position;
        }
        AbstractDevice data = dataList.get(position);

        ViewHolder viewHolder = null;
        View view = LayoutInflater.from(mContext).inflate(
                R.layout.iot_fan_machine, null);
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
            holder.switchButton.setEnabled(false);
            holder.tv_off_on.setEnabled(false);
//            holder.switchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//                @Override
//                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                    shake(buttonView, isChecked, mDid);
//                }
//            });
            holder.switchButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean checked = (boolean) v.getTag();
                    shake((EaseSwitchButton) v, !checked, mDid);
                }
            });
            holder.tv_off_on.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean isOpen = (boolean) v.getTag();
                    String param = String.valueOf(0);
                    if (isOpen) {
                        param = String.valueOf(1);
                    }
                    Subscription subscription = FanRepository.setPower(param, mDid)
                            .compose(RxSchedulerUtil.SchedulersTransformer1())
                            .onTerminateDetach()
                            .subscribe(rpcResult -> {
                                if (rpcResult != null) {

                                }
                            }, throwable -> {
                                logUtil.e(TAG, throwable.getMessage());
                            });
                    mCompositeSubscription.add(subscription);
                }
            });
            Subscription subscription = Observable.interval(0, 5, TimeUnit.SECONDS)
                    .subscribeOn(Schedulers.io())
                    .onTerminateDetach()
                    .flatMap(aLong -> FanRepository.getProp(mDid))
                    .filter(rpcResult ->
                            rpcResult.getCode() == 0 &&
                                    rpcResult.getList() != null &&
                                    rpcResult.getList().size() > 0
                    )
                    .onTerminateDetach()
                    .map(rpcResult -> new FanProp(rpcResult.getList()))
                    .observeOn(AndroidSchedulers.mainThread())
                    .onTerminateDetach()
                    .subscribe(fanProp -> {
                        if (fanProp != null) {
                            fillViewHolder(holder, name, mDid, fanProp);
                        }
                    });
            mCompositeSubscription.add(subscription);
        } else {
            holder.tv_title.setText(data.getDevice().getName());
            holder.tv_status.setText("离线");
            holder.tv_model.setText("--");
            holder.tv_off_on.setEnabled(false);
            holder.switchButton.setEnabled(false);
        }
    }

    private void fillViewHolder(ViewHolder holder,
                                String name, String mDid, FanProp prop) {
        String PowerState = prop.getPower_state();
        int workTime = prop.getWork_time();
        holder.switchButton.setEnabled(true);
        holder.tv_off_on.setEnabled(true);

        if (PowerState.equals("0")) {//关机
            holder.tv_off_on.setText("开启");
            holder.tv_status.setText("关机");
            holder.tv_off_on.setTag(true);
        } else {
            holder.tv_off_on.setText("关闭");
            holder.tv_off_on.setTag(false);
            holder.tv_status.setText("工作中");
            String status = getStatus(workTime);
            if (!TextUtils.isEmpty(status)) {
                holder.tv_status.setText(status);
            }
        }


        // 风挡
        switch (prop.getWind_mode()) {
            case "0": // 标准风
                holder.tv_model.setText(res.getString(R.string.iot_fan_standard));
                break;
            case "1": // 自然风
                holder.tv_model.setText(res.getString(R.string.iot_fan_natural));
                break;
            case "2": // 节能风
                holder.tv_model.setText(res.getString(R.string.iot_fan_saving));
                break;
        }

        // 灯光
        switch (prop.getShake_state()) {
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

    private String getStatus(int time) {
        String str = "";
        if (time != TIME) {
            int hour = time / 60;
            int minus = time % 60;
            if (hour != 0) {
                if (minus != 0) {
                    str = hour + "小时" + minus + "分" + "后关闭";
                } else {
                    str = hour + "小时" + "后关闭";
                }
            } else {
                if (minus != 0)
                    str = minus + "分" + "后关闭";
            }
        }
        return str;
    }

    private void shake(EaseSwitchButton buttonView, boolean checked, String did) {
        String param = String.valueOf(0);
        if (checked) {
            param = String.valueOf(1);
        }
        Subscription subscription = FanRepository.setShake(param, did)
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
                }, throwable -> {
                    logUtil.e(TAG, throwable.getMessage());
                });
        mCompositeSubscription.add(subscription);
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
}
