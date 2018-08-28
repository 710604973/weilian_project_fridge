package com.mode.fridge.adapter;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.miot.common.abstractdevice.AbstractDevice;
import com.mode.fridge.R;
import com.mode.fridge.adapter.base.BaseDeviceAdapter;
import com.mode.fridge.bean.prop.WashDishProp;
import com.mode.fridge.repository.WashDishRepository;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class WashDishMachineAdapter extends BaseDeviceAdapter implements ViewPager.OnPageChangeListener {
    private static final String TAG = WashDishMachineAdapter.class.getSimpleName();

    private static final int TIME = 65535;

    public WashDishMachineAdapter(Context mContext) {
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
                R.layout.iot_wash_dish_machine, null);
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
            Subscription subscription = Observable.interval(0, 5, TimeUnit.SECONDS)
                    .subscribeOn(Schedulers.io())
                    .onTerminateDetach()
                    .flatMap(aLong -> WashDishRepository.getProp(mDid))
                    .filter(rpcResult ->
                            rpcResult.getCode() == 0 && rpcResult.getList().size() > 0
                    )
                    .onTerminateDetach()
                    .map(rpcResult -> new WashDishProp(rpcResult.getList()))
                    .observeOn(AndroidSchedulers.mainThread())
                    .onTerminateDetach()
                    .subscribe(washDishProp -> {
                        if (washDishProp != null) {
                            fillViewHolder(holder, name, mDid, washDishProp);
                        }
                    });
            mCompositeSubscription.add(subscription);
        }
//        else {
//            holder.tv_title.setText(data.getDevice().getName());
//            holder.tv_status.setText("离线");
//        }
    }

    private void fillViewHolder(ViewHolder holder,
                                String name, String mDid, WashDishProp prop) {
        int program = prop.getProgram();
        int ldj_state = prop.getLdj_state();
        int salt_state = prop.getSalt_state();
        int left_time = prop.getLeft_time();
        int hours = prop.getBespeak_h();
        int minus = prop.getBespeak_min();
        int status = prop.getBespeak_status();
        int index = prop.getWash_process();
        int wash_status = prop.getWash_status();
        String mode = "";
        switch (program) {
            case 0:
                mode = res.getString(R.string.iot_wash_dish_standar);
                break;
            case 1:
                mode = res.getString(R.string.iot_wash_dish_economic);
                break;
            case 2:
                mode = res.getString(R.string.iot_wash_dish_fast);
                break;
            case 3:
                mode = res.getString(R.string.iot_wash_dish_custom);
                break;
        }
        String[] progress = res.getStringArray(R.array.progress);
        String washProgress = progress[index];
        holder.tv_status.setText(washProgress);
        if (!TextUtils.isEmpty(mode)) {
            holder.tv_model.setText(mode);
        }

        if (salt_state != 0)
            holder.tv_soft.setText(res.getString(R.string.iot_wash_dish_salt_yes));
        holder.tv_soft.setText(res.getString(R.string.iot_wash_dish_salt_no));

        if (ldj_state != 0)
            holder.tv_wash_agent.setText(res.getString(R.string.iot_wash_dish_ldj_yes));
        holder.tv_wash_agent.setText(res.getString(R.string.iot_wash_dish_ldj_no));

        if (wash_status == 0) {
            if (index != 0 && index != 6) {
                holder.tv_status.setText(washProgress + "-暂停中");
            }
        }
        if (status != 0) {
            String time = getTime(hours, minus);
            if (!TextUtils.isEmpty(time)) {
                holder.tv_status.setText("已预约");
                holder.rl_tips.setVisibility(View.VISIBLE);
                holder.rl_white.setVisibility(View.GONE);
                holder.tv_tips.setText(time + "开始洗涤");
            }else {
                holder.rl_tips.setVisibility(View.GONE);
                holder.rl_white.setVisibility(View.VISIBLE);
            }
        } else {
            if (left_time != 0) {
                String str = change(left_time);
                if (!TextUtils.isEmpty(str) && !washProgress.equals("空闲中")) {
                    holder.rl_tips.setVisibility(View.VISIBLE);
                    holder.rl_white.setVisibility(View.GONE);
                    holder.tv_tips.setText("洗涤剩余" + str);
                }else {
                    holder.rl_tips.setVisibility(View.GONE);
                    holder.rl_white.setVisibility(View.VISIBLE);
                }
            }
        }
    }


    public String getTime(int hours, int minus) {
        StringBuilder sb = new StringBuilder();
        if (hours != 0) sb.append(hours + "时");
        if (minus != 0) sb.append(minus + "分");
        return sb.toString();
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
//        return h + "时" + d + "分" + s + "秒";
    }

    class ViewHolder {
        TextView tv_title;
        TextView tv_status;
        TextView tv_model;
        TextView tv_soft;
        TextView tv_wash_agent;
        TextView tv_tips;
        RelativeLayout rl_tips, rl_white;

        public ViewHolder(View view) {
            tv_title = (TextView) view.findViewById(R.id.tv_title);
            tv_status = (TextView) view.findViewById(R.id.tv_status);
            tv_model = (TextView) view.findViewById(R.id.tv_model);
            tv_soft = (TextView) view.findViewById(R.id.tv_sorft);
            tv_wash_agent = (TextView) view.findViewById(R.id.tv_wash_agent);
            tv_tips = (TextView) view.findViewById(R.id.tv_tips);
            rl_tips = (RelativeLayout) view.findViewById(R.id.rl_tips);
            rl_white = (RelativeLayout) view.findViewById(R.id.rl_white);
        }
    }
}
