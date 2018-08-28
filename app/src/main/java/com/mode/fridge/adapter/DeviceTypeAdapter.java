package com.mode.fridge.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mode.fridge.R;
import com.mode.fridge.bean.DeviceTypeEntity;
import com.mode.fridge.utils.DeviceIconUtil;
import com.viomi.widget.sphere3d.TagsAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * 设备分类适配器
 * Created by William on 16/3/4.
 */
public class DeviceTypeAdapter extends TagsAdapter {
    private List<DeviceTypeEntity> mList;
    private Context mContext;

    public DeviceTypeAdapter(Context context, List<DeviceTypeEntity> list) {
        mList = list;
        mContext = context;
        if (mList == null) mList = new ArrayList<>();
    }


    public void initDatas(List<DeviceTypeEntity> list) {
        if (mList != null) {
            mList.clear();
            mList.addAll(list);
            notifyDataSetChanged();
        }
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public View getView(Context context, int position, ViewGroup parent) {
        DeviceTypeEntity entity = mList.get(position);
        View view;
        if (entity.getType().equals(mContext.getResources().getString(R.string.iot_device_fridge))) { // 冰箱
            view = LayoutInflater.from(context).inflate(R.layout.view_holder_device_type_2, parent, false);
            // 图标
            ImageView imageView = (ImageView) view.findViewById(R.id.holder_device_type_2_icon);
            imageView.setImageResource(DeviceIconUtil.switchIconWithPosition(position));
            // 设备分类
            TextView textView = (TextView) view.findViewById(R.id.holder_device_type_2_name);
            textView.setText(entity.getType());
            // 背景
            LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.holder_device_type_2_bg);
            linearLayout.setBackgroundResource(DeviceIconUtil.switchDeviceBg(position));
            // 数据
            TextView coldTextView = (TextView) view.findViewById(R.id.holder_device_type_fridge_cold);
            TextView changeableTextView = (TextView) view.findViewById(R.id.holder_device_type_fridge_changeable);
            TextView freezingTextView = (TextView) view.findViewById(R.id.holder_device_type_fridge_freezing);
            LinearLayout linearLayout1 = (LinearLayout) view.findViewById(R.id.holder_device_type_changeable_layout);
            View lineView = view.findViewById(R.id.holder_device_type_line);
            coldTextView.setTypeface(Typeface.createFromAsset(mContext.getAssets(), "fonts/DINCond-Medium.otf"));
            changeableTextView.setTypeface(Typeface.createFromAsset(mContext.getAssets(), "fonts/DINCond-Medium.otf"));
            freezingTextView.setTypeface(Typeface.createFromAsset(mContext.getAssets(), "fonts/DINCond-Medium.otf"));
            String Data = entity.getData();
            if (Data != null) { // 数据不为空
                String[] data = Data.split(",");
                if (data.length >= 2) {
                    if (data.length == 2) {
                        linearLayout1.setVisibility(View.GONE);
                        lineView.setVisibility(View.GONE);
                        coldTextView.setText(data[0]);
                        freezingTextView.setText(data[1]);
                        Log.i("info", "=============data:" + "data[0]:" + data[0] + "  data[1]:" + data[1]);
                    } else {
                        coldTextView.setText(data[0]);
                        changeableTextView.setText(data[1]);
                        freezingTextView.setText(data[2]);
                        Log.i("info", "=============data:" + "data[0]:" + data[0] + "  data[1]:" + data[1] + "  data[2]:" + data[2]);
                    }
                }
            }
        } else {
            view = LayoutInflater.from(mContext).inflate(R.layout.view_holder_device_type_1, parent, false);
            // 图标
            ImageView imageView = (ImageView) view.findViewById(R.id.holder_device_type_1_icon);
            imageView.setImageResource(DeviceIconUtil.switchIconWithPosition(position));
            // 设备分类
            TextView typeTextView = (TextView) view.findViewById(R.id.holder_device_type_1_name);
            typeTextView.setText(entity.getType());
            // 背景
            LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.holder_device_type_1_bg);
            if (entity.getList().size() > 0 && !entity.isExist()) { // 离线
                linearLayout.setBackgroundResource(R.drawable.icon_device_circle_gray);
            } else {
                linearLayout.setBackgroundResource(DeviceIconUtil.switchDeviceBg(position));
            }
            // 显示数据
            TextView dataTextView = (TextView) view.findViewById(R.id.holder_device_type_1_data);
            if (entity.getList().size() > 0 && entity.isExist()) { // 在线
                dataTextView.setText(entity.getData() == null ? "--" : entity.getData());
            } else if (entity.getList().size() > 0 && !entity.isExist()) { // 离线
                dataTextView.setText(mContext.getResources().getString(R.string.iot_device_type_offline));
            } else {
                if (entity.getList().size() != 0) {
                    dataTextView.setText(mContext.getResources().getString(R.string.iot_please_look_forward));
                }else {
                    dataTextView.setText(mContext.getResources().getString(R.string.iot_add_no_txt));
                }
            }
//            else if (entity.getList().size() == 0 && entity.isOnSale()) { // 已上架
//                dataTextView.setText(mContext.getResources().getString(R.string.iot_buy_confirm));
//            } else {
//                dataTextView.setText(mContext.getResources().getString(R.string.iot_please_look_forward));
//            }
        }
        return view;
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public int getPopularity(int position) {
        return position % 5;
    }

    @Override
    public void onThemeColorChanged(View view, float alpha) {
        view.setAlpha(alpha - 0.2f);
    }
}