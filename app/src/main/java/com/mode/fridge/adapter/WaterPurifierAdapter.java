package com.mode.fridge.adapter;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.miot.common.abstractdevice.AbstractDevice;
import com.mode.fridge.AppConstants;
import com.mode.fridge.R;
import com.mode.fridge.adapter.base.BaseDeviceAdapter;
import com.mode.fridge.bean.prop.CSeriesPurifierProp;
import com.mode.fridge.bean.prop.MiPurifierProp;
import com.mode.fridge.bean.prop.SSeriesPurifierProp;
import com.mode.fridge.bean.prop.VSeriesPurifierProp;
import com.mode.fridge.bean.prop.X3PurifierProp;
import com.mode.fridge.bean.prop.X5PurifierProp;
import com.mode.fridge.repository.WaterPurifierRepository;
import com.mode.fridge.utils.RxSchedulerUtil;
import com.mode.fridge.utils.logUtil;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class WaterPurifierAdapter extends BaseDeviceAdapter implements ViewPager.OnPageChangeListener {
    private static final String TAG = WaterPurifierAdapter.class.getSimpleName();
    private int mMinTemp = 40, mMaxTemp = 90;// 最低和最高设定出水温度

    public WaterPurifierAdapter(Context mContext) {
        super(mContext);
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
                R.layout.iot_water_purifier, null);
        if (viewHolder == null) {
            viewHolder = new ViewHolder(view);
        }
        bindView(viewHolder, data);
        container.addView(view, WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT);
        return view;
    }


    private void bindView(ViewHolder holder, final AbstractDevice data) {
        if (data.isOnline()) {
            String model = data.getDeviceModel();
            String mDid = data.getDeviceId();
            String name = data.getDevice().getName();
            Subscription subscription = null;
            switch (model) {
                case AppConstants.YUNMI_WATERPURI_V1:
                case AppConstants.YUNMI_WATERPURI_V2:
                    VSeriesPurifierProp prop = new VSeriesPurifierProp();
                    subscription = Observable.interval(0, 5, TimeUnit.SECONDS)
                            .subscribeOn(Schedulers.io())
                            .onTerminateDetach()
                            .flatMap(aLong -> WaterPurifierRepository.miGetProp(mDid))
                            .filter(rpcResult -> rpcResult.getCode() == 0)
                            .onTerminateDetach()
                            .flatMap(rpcResult -> {
                                prop.initGetProp(rpcResult.getList());
                                return WaterPurifierRepository.vGetProp(mDid);
                            })
                            .filter(rpcResult -> rpcResult.getCode() == 0)
                            .onTerminateDetach()
                            .map(rpcResult -> {
                                prop.initGetExtraProp(rpcResult.getList());
                                return prop;
                            })
                            .observeOn(AndroidSchedulers.mainThread())
                            .onTerminateDetach()
                            .subscribe(rpcResult -> {
                                if (rpcResult != null) {
                                    fillViewHolder(holder, name, mDid, rpcResult.getTds_out(), rpcResult.getF1_Life(),
                                            rpcResult.getF2_Life(), rpcResult.getF3_Life(), rpcResult.getF4_Life(),
                                            0, 0, 0);
                                }
                            }, throwable -> logUtil.e(TAG, throwable.getMessage()));
                    break;
                case AppConstants.YUNMI_WATERPURI_S1:
                case AppConstants.YUNMI_WATERPURI_S2:
                    subscription = Observable.interval(0, 5, TimeUnit.SECONDS)
                            .subscribeOn(Schedulers.io())
                            .onTerminateDetach()
                            .flatMap(aLong -> WaterPurifierRepository.miGetProp(mDid))
                            .filter(rpcResult -> rpcResult.getCode() == 0 && rpcResult.getList().size() > 0)
                            .onTerminateDetach()
                            .map(rpcResult -> new SSeriesPurifierProp(rpcResult.getList()))
                            .observeOn(AndroidSchedulers.mainThread())
                            .onTerminateDetach()
                            .subscribe(sSeriesPurifierProp -> {
                                if (sSeriesPurifierProp != null) {
                                    fillViewHolder(holder, name, mDid,
                                            sSeriesPurifierProp.getTds_out(),
                                            sSeriesPurifierProp.getF1_Life(),
                                            sSeriesPurifierProp.getF2_Life(),
                                            sSeriesPurifierProp.getF3_Life(),
                                            0,
                                            0, 0, 0);
                                }
                            }, throwable -> logUtil.e(TAG, throwable.getMessage()));
                    break;
                case AppConstants.YUNMI_WATERPURI_C1:
                case AppConstants.YUNMI_WATERPURI_C2:
                    CSeriesPurifierProp prop_cs = new CSeriesPurifierProp();
                    subscription = Observable.interval(0, 5, TimeUnit.SECONDS)
                            .subscribeOn(Schedulers.io())
                            .onTerminateDetach()
                            .flatMap(aLong -> WaterPurifierRepository.miGetProp(mDid))
                            .filter(rpcResult -> rpcResult.getCode() == 0 && rpcResult.getList().size() > 0)
                            .onTerminateDetach()
                            .flatMap(rpcResult -> {
                                prop_cs.initGetProp(rpcResult.getList());
                                return WaterPurifierRepository.cGetProp(mDid);
                            })
                            .filter(rpcResult -> rpcResult.getCode() == 0 && rpcResult.getList().size() > 0)
                            .onTerminateDetach()
                            .map(rpcResult -> {
                                prop_cs.initGetExtraProp(rpcResult.getList());
                                return prop_cs;
                            })
                            .observeOn(AndroidSchedulers.mainThread())
                            .onTerminateDetach()
                            .subscribe(cSeriesPurifierProp -> {
                                if (cSeriesPurifierProp != null) {
                                    fillViewHolder(holder, name, mDid, cSeriesPurifierProp.getTds_out(),
                                            cSeriesPurifierProp.getF1_Life(),
                                            cSeriesPurifierProp.getF2_Life(),
                                            cSeriesPurifierProp.getF3_Life(),
                                            cSeriesPurifierProp.getF4_Life(),
                                            0, 0, 0);
                                }
                            }, throwable -> logUtil.e(TAG, throwable.getMessage()));
                    break;
                case AppConstants.YUNMI_WATERPURI_X3:
                    X3PurifierProp prop_x3 = new X3PurifierProp();
                    subscription = Observable.interval(0, 5, TimeUnit.SECONDS)
                            .subscribeOn(Schedulers.io())
                            .onTerminateDetach()
                            .flatMap(aLong -> WaterPurifierRepository.miGetProp(mDid))
                            .filter(rpcResult -> rpcResult.getCode() == 0 && rpcResult.getList().size() > 0)
                            .onTerminateDetach()
                            .flatMap(rpcResult -> {
                                prop_x3.initGetProp(rpcResult.getList());
                                return WaterPurifierRepository.x3GetProp(mDid);
                            })
                            .filter(rpcResult -> rpcResult.getCode() == 0 && rpcResult.getList().size() > 0)
                            .onTerminateDetach()
                            .map(rpcResult -> {
                                prop_x3.initGetExtraProp(rpcResult.getList());
                                return prop_x3;
                            })
                            .observeOn(AndroidSchedulers.mainThread())
                            .onTerminateDetach()
                            .subscribe(x3PurifierProp -> {
                                if (x3PurifierProp != null) {
                                    int mMinTemp = x3PurifierProp.getMin_set_tempe();
                                    int max = mMaxTemp - mMinTemp;
                                    fillViewHolder(holder, name, mDid,
                                            x3PurifierProp.getTds_out(),
                                            x3PurifierProp.getF1_Life(),
                                            x3PurifierProp.getF2_Life(),
                                            x3PurifierProp.getF3_Life(),
                                            0,
                                            x3PurifierProp.getCustom_tempe1(),
                                            max, mMinTemp);
                                }
                            }, throwable -> logUtil.e(TAG, throwable.getMessage()));
                    break;
                case AppConstants.YUNMI_WATERPURI_X5:
                    X5PurifierProp prop_x5 = new X5PurifierProp();
                    subscription = Observable.interval(0, 5, TimeUnit.SECONDS)
                            .subscribeOn(Schedulers.io())
                            .onTerminateDetach()
                            .flatMap(aLong -> WaterPurifierRepository.miGetProp(mDid))
                            .filter(rpcResult -> rpcResult.getCode() == 0 && rpcResult.getList().size() > 0)
                            .onTerminateDetach()
                            .flatMap(rpcResult -> {
                                prop_x5.initGetProp(rpcResult.getList());
                                return WaterPurifierRepository.x5GetProp(mDid);
                            })
                            .filter(rpcResult -> rpcResult.getCode() == 0 && rpcResult.getList().size() > 0)
                            .onTerminateDetach()
                            .map(rpcResult -> {
                                prop_x5.initGetExtraProp(rpcResult.getList());
                                return prop_x5;
                            })
                            .observeOn(AndroidSchedulers.mainThread())
                            .onTerminateDetach()
                            .subscribe(x5PurifierProp -> {
                                if (x5PurifierProp != null) {
                                    int max = mMaxTemp - mMinTemp;
                                    fillViewHolder(holder, name, mDid,
                                            x5PurifierProp.getTds_out(),
                                            x5PurifierProp.getF1_Life(),
                                            x5PurifierProp.getF2_Life(),
                                            x5PurifierProp.getF3_Life(),
                                            x5PurifierProp.getF4_Life(),
                                            x5PurifierProp.getCustom_tempe1(),
                                            max, 0);
                                }
                            }, throwable -> logUtil.e(TAG, throwable.getMessage()));
                    break;
                case AppConstants.YUNMI_WATERPURIFIER_V1:
                case AppConstants.YUNMI_WATERPURIFIER_V2:
                case AppConstants.YUNMI_WATERPURIFIER_V3:
                case AppConstants.YUNMI_WATERPURI_LX2:
                case AppConstants.YUNMI_WATERPURI_LX3:
                    subscription = Observable.interval(0, 5, TimeUnit.SECONDS)
                            .subscribeOn(Schedulers.io())
                            .onTerminateDetach()
                            .flatMap(aLong -> WaterPurifierRepository.miGetProp(mDid))
                            .filter(rpcResult -> rpcResult.getCode() == 0 && rpcResult.getList().size() > 0)
                            .onTerminateDetach()
                            .map(rpcResult -> new MiPurifierProp(rpcResult.getList()))
                            .observeOn(AndroidSchedulers.mainThread())
                            .onTerminateDetach()
                            .subscribe(result -> {
                                if (result != null) {
                                    fillViewHolder(holder, name, mDid,
                                            result.getTds_out(),
                                            result.getF1_Life(),
                                            result.getF2_Life(),
                                            result.getF3_Life(),
                                            result.getF4_Life(),
                                            0, 0, 0);
                                }
                            }, throwable -> logUtil.e(TAG, throwable.getMessage()));
                    break;
            }
            mCompositeSubscription.add(subscription);
        } else {
            holder.tv_title.setText(data.getDevice().getName());
            holder.tv_status.setText("离线");
            holder.tv_temp.setText("--");
        }
    }

    private void fillViewHolder(ViewHolder holder,
                                String name, String mDid,
                                int Tds,
                                int filter1,
                                int filter2,
                                int filter3,
                                int filter4,
                                int custom_temp,
                                int max,
                                int minTemp) {
        if (max != 0) {
            holder.seekBar.setVisibility(View.VISIBLE);
            holder.rl_bot.setVisibility(View.VISIBLE);
            holder.line.setVisibility(View.VISIBLE);

            holder.tv_temp.setText("" + (custom_temp));
            holder.seekBar.setMax(max);
            holder.seekBar.setProgress(custom_temp - minTemp);
            holder.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    holder.tv_temp.setText("" + (progress + minTemp) + "℃");
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    setWaterTemp(mDid, seekBar.getProgress() + minTemp);
                }
            });
        } else {
            holder.seekBar.setVisibility(View.INVISIBLE);
            holder.rl_bot.setVisibility(View.INVISIBLE);
            holder.line.setVisibility(View.INVISIBLE);
//            holder.tv_tds.setText("--");
//            holder.tv1.setText("--");
//            holder.tv2.setText("--");
//            holder.tv3.setText("--");
//            holder.tv4.setText("--");
        }

        holder.tv_title.setText(name);
        holder.tv_status.setText("在线");
        holder.tv_tds.setText("" + Tds);

        if (filter1 > 0) {
            holder.tv1.setText(filter1 + "%");
        } else {
            holder.tv1.setText("--");
        }

        if (filter2 > 0) {
            holder.tv2.setText(filter2 + "%");
        } else {
            holder.tv2.setText("--");
        }
        if (filter3 > 0) {
            holder.tv3.setText(filter3 + "%");
        } else {
            holder.tv3.setText("--");
        }
        if (filter4 > 0) {
            holder.tv4.setText(filter4 + "%");
        } else {
            holder.tv4.setText("--");
        }
    }

    private void setWaterTemp(String mDid, int temp) {
        Subscription subscription = WaterPurifierRepository.setTemp(mDid, temp)
                .compose(RxSchedulerUtil.SchedulersTransformer1())
                .onTerminateDetach()
                .subscribe(rpcResult -> {

                }, throwable -> {
                    logUtil.e(TAG, throwable.getMessage());
                });
        mCompositeSubscription.add(subscription);
    }

    class ViewHolder {
        TextView tv_title;
        TextView tv_status;
        TextView tv_tds, tv_temp;
        SeekBar seekBar;
        TextView tv1, tv2, tv3, tv4;
        TextView tv_filter1, tv_filter2, tv_filter3, tv_filter4;
        RelativeLayout rl_bot;
        LinearLayout line;

        public ViewHolder(View view) {
            tv_title = (TextView) view.findViewById(R.id.tv_title);
            tv_status = (TextView) view.findViewById(R.id.tv_status);
            tv_tds = (TextView) view.findViewById(R.id.tv_tds);
            tv_temp = (TextView) view.findViewById(R.id.tv_temp);
            tv1 = (TextView) view.findViewById(R.id.tv1);
            tv2 = (TextView) view.findViewById(R.id.tv2);
            tv3 = (TextView) view.findViewById(R.id.tv3);
            tv4 = (TextView)view.findViewById(R.id.tv4);
            tv_filter1 = (TextView)view.findViewById(R.id.tv_filter1);
            tv_filter2 =(TextView) view.findViewById(R.id.tv_filter2);
            tv_filter3 = (TextView)view.findViewById(R.id.tv_filter3);
            tv_filter4 = (TextView)view.findViewById(R.id.tv_filter4);
            seekBar = (SeekBar) view.findViewById(R.id.seek_bar);
            rl_bot = (RelativeLayout) view.findViewById(R.id.rl_bot);
            line = (LinearLayout) view.findViewById(R.id.line2);
        }
    }
}
