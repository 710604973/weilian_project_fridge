package com.mode.fridge.contract;

import com.mode.fridge.bean.QRCodeLogin;
import com.mode.fridge.callback.BasePresenter;

/**
 * 登录二维码生成 Contract
 * Created by William on 2018/1/27.
 */
public interface LoginQRCodeContract {
    interface View {
        void showQRCode(QRCodeLogin loginQRCode);// 显示二维码

        void showRetry();// 显示重试 Ui

        void showLoading();// 显示加载 Dialog

        void loginFail(String message);// 登录失败

        void loginSuccess();// 登录成功
    }

    interface Presenter extends BasePresenter<View> {
        void loadQRCode();// 获取二维码链接

        void checkStatus();// 检查登录状态
    }
}