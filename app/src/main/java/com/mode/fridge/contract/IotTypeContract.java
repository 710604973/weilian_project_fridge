package com.mode.fridge.contract;

import com.mode.fridge.bean.DeviceTypeEntity;
import com.mode.fridge.bean.QRCodeBase;
import com.mode.fridge.callback.BasePresenter;

import java.util.List;

/**
 * 设备分类 Contract
 * Created by William on 2018/3/2.
 */
public interface IotTypeContract {
    interface View {
        void refreshView();// 刷新 Ui

        void showUserInfo(QRCodeBase qrCodeBase);// 显示用户信息

//        void notifyItemView(int position);// 更新 item
        void notifyItemView(int position, String data);// 更新 item
    }

    interface Presenter extends BasePresenter<View> {
        void initDeviceList(List<DeviceTypeEntity> list);// 初始化设备列表

        void loadDeviceList(List<DeviceTypeEntity> list);// 加载设备列表

        void loadUserInfo();// 读取用户信息
    }
}