package com.mode.fridge.adapter;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.miot.common.abstractdevice.AbstractDevice;
import com.mode.fridge.R;
import com.mode.fridge.adapter.base.BaseDeviceAdapter;
import com.mode.fridge.bean.prop.SweepProp;
import com.mode.fridge.repository.SweepRepository;
import com.mode.fridge.utils.RxSchedulerUtil;
import com.mode.fridge.utils.logUtil;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class SweepMachineAdapter extends BaseDeviceAdapter implements ViewPager.OnPageChangeListener {
    private static final String TAG = SweepMachineAdapter.class.getSimpleName();

    private static final int TIME = 65535;

    public SweepMachineAdapter(Context mContext) {
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
                R.layout.iot_sweep_floor_machine, null);
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
            holder.tv_start.setEnabled(false);
            holder.tv_start.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Subscription subscription = SweepRepository.setPower(1, mDid)
                            .compose(RxSchedulerUtil.SchedulersTransformer1())
                            .onTerminateDetach()
                            .subscribe(rpcResult -> {
                                if (rpcResult != null && rpcResult.getCode() == 0) {
                                    setMode(0, mDid);
                                }
                            }, throwable -> {
                                logUtil.e(TAG, throwable.getMessage());
                            });
                    mCompositeSubscription.add(subscription);
                }
            });
            holder.tv_sweep.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Subscription subscription = SweepRepository.setPower(1, mDid)
                            .compose(RxSchedulerUtil.SchedulersTransformer1())
                            .onTerminateDetach()
                            .subscribe(rpcResult -> {
                                if (rpcResult != null && rpcResult.getCode() == 0) {
                                    setMode(0, mDid);
                                }
                            }, throwable -> {
                                logUtil.e(TAG, throwable.getMessage());
                            });
                    mCompositeSubscription.add(subscription);
                }
            });
            holder.tv_stop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setPower(0, mDid);
                }
            });
            holder.tv_back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Subscription subscription = SweepRepository.setMode(3, mDid)
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
            Subscription subscription = Observable.interval(0, TimeUnit.SECONDS)
                    .subscribeOn(Schedulers.io())
                    .onTerminateDetach()
                    .flatMap(aLong -> SweepRepository.getProp(mDid))
                    .filter(rpcResult ->
                            rpcResult.getCode() == 0 &&
                                    rpcResult.getList() != null &&
                                    rpcResult.getList().size() > 0
                    )
                    .onTerminateDetach()
                    .map(rpcResult -> new SweepProp(rpcResult.getList()))
                    .observeOn(AndroidSchedulers.mainThread())
                    .onTerminateDetach()
                    .subscribe(sweepProp -> {
                        if (sweepProp != null) {
                            fillViewHolder(holder, name, mDid, sweepProp);
                        }
                    });
            mCompositeSubscription.add(subscription);
        } else {
            holder.tv_title.setText(name);
            holder.tv_start.setVisibility(View.VISIBLE);
            holder.tv_start.setEnabled(false);
            holder.rl_bottom.setBackgroundResource(R.drawable.bg_water_machine_bottom_gray);
            holder.ll_bottom.setVisibility(View.GONE);
        }
    }

    private void fillViewHolder(ViewHolder holder,
                                String name, String mDid, SweepProp prop) {
        String workMode = getMode(prop.getRun_state());
        int area = prop.getS_area();
        int battary = prop.getBattary_life();

        int time = prop.getS_time();
        int start_time = prop.getStart_time();
//        int order_time = prop.getOrder_time();
//        if (!TextUtils.isEmpty(area)) {
//            holder.tv_area.setText(area);
//        }
//
//        if (!TextUtils.isEmpty(battary)) {
//            holder.tv_electricity.setText(battary);
//        }

        if (battary != 0) {
            holder.tv_electricity.setText(battary + "%");
        }

        if (area != 0) {
            holder.tv_area.setText("" + area + "m");
        }

        if (time != 0) {
            holder.tv_time.setText(change(time));
        }

        if (!TextUtils.isEmpty(workMode)) {
            if (workMode.equals("待机") ||
                    workMode.equals("睡眠/暂停")) {
                holder.tv_status.setText("空闲中");
                holder.tv_model.setText("智能清扫");
//                holder.tv_start.setVisibility(View.VISIBLE);
//                holder.tv_start.setEnabled(true);
//                holder.rl_bottom.setBackgroundResource(R.drawable.bg_water_machine_bottom_green);
//                holder.ll_bottom.setVisibility(View.GONE);
                holder.tv_start.setVisibility(View.GONE);
                holder.rl_bottom.setBackgroundResource(R.drawable.bg_water_machine_bottom_green);
                holder.ll_bottom.setVisibility(View.VISIBLE);
                holder.tv_back.setVisibility(View.VISIBLE);
                holder.tv_sweep.setVisibility(View.VISIBLE);
                holder.tv_stop.setVisibility(View.GONE);
            } else if (workMode.equals("智能清扫") ||
                    workMode.equals("自动拖地") ||
                    workMode.equals("重点清扫")) {
                holder.tv_status.setText("清扫中");
                holder.tv_model.setText(workMode);
                holder.tv_start.setVisibility(View.GONE);
                holder.rl_bottom.setBackgroundResource(R.drawable.bg_water_machine_bottom_green);
                holder.ll_bottom.setVisibility(View.VISIBLE);
                holder.tv_back.setVisibility(View.VISIBLE);
                holder.tv_sweep.setVisibility(View.GONE);
                holder.tv_stop.setVisibility(View.VISIBLE);
            } else if (workMode.equals("回充中")) {
                holder.tv_status.setText(workMode);
                holder.tv_model.setText("智能清扫");
                holder.tv_start.setVisibility(View.GONE);
                holder.rl_bottom.setBackgroundResource(R.drawable.bg_water_machine_bottom_green);
                holder.ll_bottom.setVisibility(View.VISIBLE);
                holder.tv_back.setVisibility(View.GONE);
                holder.tv_sweep.setVisibility(View.VISIBLE);
                holder.tv_stop.setVisibility(View.VISIBLE);
            } else if (workMode.equals("充电中")) {
                holder.tv_status.setText(workMode);
                holder.tv_model.setText("智能清扫");
                holder.tv_start.setVisibility(View.VISIBLE);
                holder.tv_start.setEnabled(true);
                holder.rl_bottom.setBackgroundResource(R.drawable.bg_water_machine_bottom_green);
                holder.ll_bottom.setVisibility(View.GONE);
            } else {
                holder.tv_status.setText("满电");
                holder.tv_model.setText("智能清扫");
                holder.tv_start.setVisibility(View.VISIBLE);
                holder.tv_start.setEnabled(true);
                holder.rl_bottom.setBackgroundResource(R.drawable.bg_water_machine_bottom_green);
                holder.ll_bottom.setVisibility(View.GONE);
//                holder.tv_status.setText(workMode);
//                holder.tv_model.setText(workMode);
//                holder.tv_start.setVisibility(View.VISIBLE);
//                holder.tv_start.setEnabled(true);
//                holder.rl_bottom.setBackgroundResource(R.drawable.bg_water_machine_bottom_gray);
//                holder.ll_bottom.setVisibility(View.GONE);
            }
        }
    }

    public String change(int second) {
        int h = 0;
        int d = 0;
        int s = 0;
        int temp = second % 3600;
        if (second > 3600) {
            h = second / 3600;
            if (temp != 0) {
                if (temp > 60) {
                    d = temp / 60;
                    if (temp % 60 != 0) {
                        s = temp % 60;
                    }
                } else {
                    s = temp;
                }
            }
        } else {
            d = second / 60;
            if (second % 60 != 0) {
                s = second % 60;
            }
        }
        StringBuilder sb = new StringBuilder();
        if (h != 0) sb.append(h + "时");
        if (d != 0) sb.append(d + "分");
        if (s != 0) sb.append(s + "秒");
        return sb.toString();
    }

    private String getMode(int code) {
        StringBuilder sb = new StringBuilder();
        String[] atts = res.getStringArray(R.array.sweep_models);
        String str = Integer.toBinaryString(code);
        String bitStr = new StringBuffer(str).reverse().toString();
        for (int i = 0; i < bitStr.length(); i++) {
            char s = bitStr.charAt(i);
            if (s == '1') {
                sb.append(atts[i]);
            }
        }
        return sb.toString();
    }

    private void setMode(int state, String mDid) {
        Subscription subscription = SweepRepository.setMode(state, mDid)
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

    private void setPower(int state, String mDid) {
        Subscription subscription = SweepRepository.setPower(state, mDid)
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

    class ViewHolder {
        TextView tv_title;
        TextView tv_status;
        TextView tv_model;

        TextView tv_area;
        TextView tv_electricity;
        TextView tv_time;
        TextView tv_start;
        TextView tv_back;
        TextView tv_sweep;
        TextView tv_stop;
        RelativeLayout rl_bottom;
        LinearLayout ll_bottom;

        public ViewHolder(View view) {
            tv_title = (TextView) view.findViewById(R.id.tv_title);
            tv_status = (TextView) view.findViewById(R.id.tv_status);
            tv_model =(TextView)  view.findViewById(R.id.tv_model);
            tv_area = (TextView) view.findViewById(R.id.tv_area);
            tv_electricity =(TextView)  view.findViewById(R.id.tv_electricity);
            tv_time =(TextView)  view.findViewById(R.id.tv_time);
            tv_start = (TextView) view.findViewById(R.id.tv_start);
            tv_back = (TextView) view.findViewById(R.id.tv_back);
            tv_sweep = (TextView) view.findViewById(R.id.tv_sweep);
            tv_stop = (TextView) view.findViewById(R.id.tv_stop);
            rl_bottom = (RelativeLayout) view.findViewById(R.id.rl_bottom);
            ll_bottom = (LinearLayout) view.findViewById(R.id.ll_green);
        }
    }
}
