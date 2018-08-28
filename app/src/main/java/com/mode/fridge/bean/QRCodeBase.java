package com.mode.fridge.bean;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;

/**
 * 登录二维码 Json
 * Created by William on 2018/1/27.
 */
public class QRCodeBase implements Serializable {
    @JSONField(name = "mobBaseRes")
    private QRCodeLogin loginQRCode;

    public QRCodeLogin getLoginQRCode() {
        return loginQRCode;
    }

    public void setLoginQRCode(QRCodeLogin loginQRCode) {
        this.loginQRCode = loginQRCode;
    }
}