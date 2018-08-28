package com.mode.fridge.contract;

import com.mode.fridge.bean.QRCodeBase;
import com.mode.fridge.callback.BasePresenter;

/**
 * 管理中心 Contract
 * Created by William on 2018/1/29.
 */
public interface ManageContract {
    interface View {
        void showUserInfo(QRCodeBase qrCodeBase);// 显示用户信息
    }

    interface Presenter extends BasePresenter<View> {
        void loadUserInfo();// 读取用户信息

        void logout();// 注销登录
    }
}