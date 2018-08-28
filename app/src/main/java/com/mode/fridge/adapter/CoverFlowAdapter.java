package com.mode.fridge.adapter;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.miot.common.abstractdevice.AbstractDevice;
import com.mode.fridge.AppConstants;
import com.mode.fridge.R;
import com.mode.fridge.adapter.base.BaseDeviceAdapter;
import com.mode.fridge.bean.prop.WashMachineProp;
import com.mode.fridge.repository.WashRepository;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class CoverFlowAdapter extends BaseDeviceAdapter implements ViewPager.OnPageChangeListener {
    private static final String TAG = CoverFlowAdapter.class.getSimpleName();

    public CoverFlowAdapter(Context mContext) {
        super(mContext);
        this.mContext = mContext;
//        res = mContext.getResources();
//        mCompositeSubscription = new CompositeSubscription();
    }


    @Override
    public Object instantiateItem(ViewGroup container, int position) {
//        Log.i("info", "===================container:"+container);
        position %= dataList.size();
        if (position < 0) {
            position = dataList.size() + position;
        }
        AbstractDevice data = dataList.get(position);
        BaseViewHolder viewHolder = null;
        View view = LayoutInflater.from(mContext).inflate(
                R.layout.iot_water_machine_buy, null);
        if (viewHolder == null) {
            viewHolder = new WashMachineViewHolder(view);
        }
        bindWashMachineView((WashMachineViewHolder) viewHolder, data);
        container.addView(view, WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT);
        return view;
    }

    private void bindWashMachineView(WashMachineViewHolder holder, final AbstractDevice data) {
        String name = data.getDevice().getName();
        String mode = data.getDevice().getDeviceModel();
        holder.tv_title.setText(name);
        holder.tv_status.setText("离线");
        if (data.isOnline()) {
//            Subscription subscription = WashRepository.getProp(data.getDeviceId())
//                    .compose(RxSchedulerUtil.SchedulersTransformer1())
//                    .onTerminateDetach()
//                    .subscribe(rpcResult -> {
//                                if (rpcResult.getCode() != 0 || rpcResult.getList().size() == 0) return;
//                                List<Object> list = rpcResult.getList();
//                                String mode = data.getDevice().getDeviceModel();
//                                if (mode.equals(AppConstants.VIOMI_WASHER_U1)) {
//                                    fillWashMachineHolder(holder, data, list);
//                                } else {
//                                    fillWashMachineHolder2(holder, data, list);
//                                }
//                            },
//                            throwable -> logUtil.e(TAG, throwable.getMessage()));
            Subscription subscription = Observable.interval(0, 5, TimeUnit.SECONDS)
                    .subscribeOn(Schedulers.io())
                    .onTerminateDetach()
                    .flatMap(aLong -> WashRepository.getProp(data.getDeviceId()))
                    .filter(rpcResult ->
                            rpcResult.getCode() == 0 && rpcResult.getList().size() > 0
                    )
                    .onTerminateDetach()
                    .map(rpcResult -> new WashMachineProp(mode, rpcResult.getList()))
                    .observeOn(AndroidSchedulers.mainThread())
                    .onTerminateDetach()
                    .subscribe(washMachineProp -> {
                        if (washMachineProp != null) {
                            if (mode.equals(AppConstants.VIOMI_WASHER_U1)) {
                                fillWashMachineHolder(holder, data, washMachineProp);
                            } else {
                                fillWashMachineHolder2(holder, data, washMachineProp);
                            }
                        }
                    });
            mCompositeSubscription.add(subscription);
        } else {
            holder.tv_model.setText("--");
            holder.tv_temp.setText("--");
            holder.tv_wash_time.setText("--");
            holder.tv_speed.setText("--");
            holder.llModel.setVisibility(View.VISIBLE);
            holder.iv_device_icon.setVisibility(View.GONE);
            holder.rl_add_store.setVisibility(View.GONE);
            holder.rl_status.setVisibility(View.GONE);
            holder.rl_white.setVisibility(View.VISIBLE);
        }
    }

    private void fillWashMachineHolder(WashMachineViewHolder holder, final AbstractDevice data, WashMachineProp prop) {
        holder.llModel.setVisibility(View.VISIBLE);
        holder.iv_device_icon.setVisibility(View.GONE);
        holder.rl_add_store.setVisibility(View.GONE);
        holder.rl_status.setVisibility(View.GONE);
        holder.rl_white.setVisibility(View.VISIBLE);

        String program = (String) prop.getProgram();
        int wash_process = prop.getWash_process();
        int wash_status = prop.getWash_status();
        int water_temp = prop.getWater_temp();
        int rinse_time = prop.getRinse_time();
        int remain_time = prop.getRemain_time();
        String spin_level = prop.getSpin_level();
        int appoint_time = prop.getAppoint_time();
        int be_status = prop.getBe_status();

        String[] strings = res.getStringArray(R.array.wash_machine_progress);
        String status = strings[wash_process];

        String level = "";
        if (spin_level.equals("none")) {
            level = res.getString(R.string.level_none);
        } else if (spin_level.equals("gentle")) {
            level = res.getString(R.string.level_gentle);
        } else if (spin_level.equals("mild")) {
            level = res.getString(R.string.level_mild);
        } else if (spin_level.equals("middle")) {
            level = res.getString(R.string.level_middle);
        } else if (spin_level.equals("strong")) {
            level = res.getString(R.string.level_strong);
        }

        String model = "";
        if (program.equals("goldenwash")) {
            model = res.getString(R.string.pro_goldenwash);
        } else if (program.equals("drumclean")) {
            model = res.getString(R.string.pro_drumclean);
        } else if (program.equals("spin")) {
            model = res.getString(R.string.pro_spin);
        } else if (program.equals("antibacterial")) {
            model = res.getString(R.string.pro_antibacterial);
        } else if (program.equals("cottons")) {
            model = res.getString(R.string.pro_cottons);
        } else if (program.equals("shirts")) {
            model = res.getString(R.string.pro_shirts);
        } else if (program.equals("child_cloth")) {
            model = res.getString(R.string.pro_child_cloth);
        } else if (program.equals("jeans")) {
            model = res.getString(R.string.pro_jeans);
        } else if (program.equals("wool")) {
            model = res.getString(R.string.pro_wool);
        } else if (program.equals("down")) {
            model = res.getString(R.string.pro_down);
        } else if (program.equals("chiffon")) {
            model = res.getString(R.string.pro_chiffon);
        } else if (program.equals("outdoor")) {
            model = res.getString(R.string.pro_outdoor);
        } else if (program.equals("delicates")) {
            model = res.getString(R.string.pro_delicates);
        } else if (program.equals("underwears")) {
            model = res.getString(R.string.pro_underwears);
        } else if (program.equals("rinse_spin")) {
            model = res.getString(R.string.pro_spin);
        } else if (program.equals("cotton Eco")) {
            model = res.getString(R.string.pro_cottons);
        } else {
            model = res.getString(R.string.pro_MyTime);
        }

        holder.tv_status.setText(status);
        holder.tv_model.setText(model);
        holder.tv_temp.setText("" + water_temp + "℃");
        holder.tv_wash_time.setText("" + rinse_time + "次");
        holder.tv_speed.setText(level);
        if (wash_status == 0) {
            if (wash_process == 1 ||
                    wash_process == 2 ||
                    wash_process == 3 ||
                    wash_process == 4 ||
                    wash_process == 5) {
                holder.tv_status.setText("暂停中");
                holder.rl_status.setVisibility(View.GONE);
                holder.rl_add_store.setVisibility(View.GONE);
                holder.rl_white.setVisibility(View.VISIBLE);
//                if (remain_time != 0) {
//                    holder.rl_status.setVisibility(View.VISIBLE);
//                    holder.rl_add_store.setVisibility(View.GONE);
//                    holder.rl_white.setVisibility(View.GONE);
//                    holder.tv_tips.setText("剩余" + remain_time + "分钟洗涤结束");
//                }
            }
        } else {
            if (status.equals("空闲")) {
                if (be_status == 1) {
                    holder.tv_status.setText("预约中");
                    if (appoint_time != 0) {
                        holder.rl_status.setVisibility(View.VISIBLE);
                        holder.rl_add_store.setVisibility(View.GONE);
                        holder.rl_white.setVisibility(View.GONE);
                        holder.tv_tips.setText("预计" + change(appoint_time) + "小时后洗涤结束");
                    }
                } else {
                    holder.rl_status.setVisibility(View.GONE);
                    holder.rl_add_store.setVisibility(View.GONE);
                    holder.rl_white.setVisibility(View.VISIBLE);
                }
            } else {
                if (remain_time != 0) {
                    holder.rl_status.setVisibility(View.VISIBLE);
                    holder.rl_add_store.setVisibility(View.GONE);
                    holder.rl_white.setVisibility(View.GONE);
                    holder.tv_tips.setText("剩余" + remain_time + "分钟洗涤结束");
                }
            }
        }
    }

    public String change(int minus) {
        int h = 0;
        int d = 0;
        int temp = minus % 60;
        if (minus > 60) {
            h = minus / 60;
        }
        if (minus % 60 != 0) {
            d = minus % 60;
        }
        StringBuilder sb = new StringBuilder();
        if (h != 0) sb.append(h + "时");
        if (d != 0) sb.append(d + "分");
        return sb.toString();
    }

    private void fillWashMachineHolder2(WashMachineViewHolder holder, final AbstractDevice data, WashMachineProp prop) {
        holder.llModel.setVisibility(View.VISIBLE);
        holder.iv_device_icon.setVisibility(View.GONE);
        holder.rl_add_store.setVisibility(View.GONE);
        holder.rl_status.setVisibility(View.GONE);
        holder.rl_white.setVisibility(View.VISIBLE);

//        String program = (String) list.get(0);
//        int wash_process = (int) list.get(1);
////        int wash_status = (int) list.get(2);
//        int water_temp = (int) list.get(3);
//        int rinse_time = (int) list.get(4);
//        int remain_time = (int) list.get(5);
//        int spin_level = (int) list.get(6);
//        int appoint_time = (int) list.get(7);

        String program = (String) prop.getProgram();
        int wash_process = prop.getWash_process();
        int wash_status = prop.getWash_status();
        int water_temp = prop.getWater_temp();
        int rinse_time = prop.getRinse_time();
        int remain_time = prop.getRemain_time();
        int spin_level = prop.getLevel();
        int appoint_time = prop.getAppoint_time();
//        int be_status = prop.getBe_status();

        String[] progres = res.getStringArray(R.array.wash_progress);
        String status = progres[wash_process];

        String level = "";
        if (spin_level == 0) {
            level = "不脱水";
        } else if (spin_level == 40 * 10) {
            level = "400转";
        } else if (spin_level == 60 * 10) {
            level = "600转";
        } else if (spin_level == 80 * 10) {
            level = "800转";
        } else if (spin_level == 100 * 10) {
            level = "1000转";
        } else if (spin_level == 120 * 10) {
            level = "1200转";
        } else if (spin_level == 140 * 10) {
            level = "1400转";
        }

        String model = "";
        if (program.equals("goldenwash")) {//
            model = res.getString(R.string.pro_goldenwash);
        } else if (program.equals("shirts")) {
            model = res.getString(R.string.pro_shirts);
        } else if (program.equals("silk")) {
            model = res.getString(R.string.pro_silk);
        } else if (program.equals("drumclean")) {
            model = res.getString(R.string.pro_drumclean);
        } else if (program.equals("cottons")) {
            model = res.getString(R.string.pro_cottons);
        } else if (program.equals("jeans")) {
            model = res.getString(R.string.pro_jeans);
        } else if (program.equals("CoPro")) {
            model = res.getString(R.string.pro_CoPro);
        } else if (program.equals("SoWash")) {
            model = res.getString(R.string.pro_SoWash);
        } else if (program.equals("super_quick")) {
            model = res.getString(R.string.pro_super_quick);
        } else if (program.equals("wool")) {
            model = res.getString(R.string.pro_wool);
        } else if (program.equals("rinse_spin")) {
            model = res.getString(R.string.pro_rinse_spin);
        } else if (program.equals("BoWash")) {
            model = res.getString(R.string.pro_BoWash);
        } else if (program.equals("BigWash")) {
            model = res.getString(R.string.pro_BigWash);
        } else if (program.equals("down")) {
            model = res.getString(R.string.pro_down);
        } else if (program.equals("spin")) {
            model = res.getString(R.string.pro_spin);
        } else if (program.equals("Dry")) {
            model = res.getString(R.string.pro_Dry);
        }
        holder.tv_status.setText(status);
        holder.tv_model.setText(model);
        holder.tv_temp.setText("" + water_temp + "℃");
        holder.tv_wash_time.setText("" + rinse_time + "次");
        holder.tv_speed.setText(level);
//        if (wash_status == 0) {
//            if (wash_process != 0 ||
//                    wash_process != 1) {
//                holder.tv_status.setText("暂停中");
//                holder.rl_status.setVisibility(View.GONE);
//                holder.rl_add_store.setVisibility(View.GONE);
//                holder.rl_white.setVisibility(View.VISIBLE);
//
//                if (remain_time != 0) {
//                    holder.rl_status.setVisibility(View.VISIBLE);
//                    holder.rl_add_store.setVisibility(View.GONE);
//                    holder.rl_white.setVisibility(View.GONE);
//                    holder.tv_tips.setText("剩余" + remain_time + "分钟洗涤结束");
//                }
//            }
//        } else {
//
//        }

        if (status.equals("预约")) {
            holder.tv_status.setText("预约中");
            if (appoint_time != 0) {
                holder.rl_status.setVisibility(View.VISIBLE);
                holder.rl_add_store.setVisibility(View.GONE);
                holder.rl_white.setVisibility(View.GONE);
                holder.tv_tips.setText("预计" + change(appoint_time) + "小时后洗涤结束");
            }
        } else {
            if (remain_time != 0) {
                holder.rl_status.setVisibility(View.VISIBLE);
                holder.rl_add_store.setVisibility(View.GONE);
                holder.rl_white.setVisibility(View.GONE);
                holder.tv_tips.setText("剩余" + remain_time + "分钟洗涤结束");
            }
        }
    }

    class BaseViewHolder {
        TextView tv_title;
        TextView tv_status;
    }

    class WashMachineViewHolder extends BaseViewHolder {
        TextView tv_status;
        TextView tv_model, tv_temp, tv_wash_time, tv_speed, tv_tips;
        ImageView iv_device_icon;
        LinearLayout llModel;
        RelativeLayout rl_add_store, rl_status, rl_white;

        public WashMachineViewHolder(View view) {
            tv_title = (TextView) view.findViewById(R.id.tv_title);
            tv_status = (TextView) view.findViewById(R.id.tv_status);
            tv_tips = (TextView) view.findViewById(R.id.tv_tips);
            tv_model = (TextView) view.findViewById(R.id.tv_model);
            tv_temp = (TextView) view.findViewById(R.id.tv_temp);
            tv_wash_time = (TextView) view.findViewById(R.id.tv_wash_time);
            tv_speed = (TextView) view.findViewById(R.id.tv_speed);

            iv_device_icon = (ImageView) view.findViewById(R.id.iv_device_icon);
            llModel = (LinearLayout) view.findViewById(R.id.ll_model);
            rl_add_store = (RelativeLayout) view.findViewById(R.id.rl_add_store);
            rl_status = (RelativeLayout) view.findViewById(R.id.rl_status);
            rl_white = (RelativeLayout) view.findViewById(R.id.rl_white);

        }
    }
}
