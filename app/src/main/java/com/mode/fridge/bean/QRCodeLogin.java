package com.mode.fridge.bean;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;


/**
 * 登录二维码 Json
 * Created by William on 2018/1/27.
 */
public class QRCodeLogin implements Serializable {
    @JSONField(name = "code")
    private int code;// 返回码
    @JSONField(name = "desc")
    private String desc;// 返回信息
    @JSONField(name = "result")
    private String result;// 二维码链接
    @JSONField(name = "token")
    private String token;// 云米账号 Token
    @JSONField(name = "LoginData")
    private QRCodeLoginResult loginResult;
    @JSONField(name = "appendAttr")
    private String appendAttr;// 包含云米账号和小米账号信息
    private UserInfo userInfo;// 包含云米账号和小米账号信息

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getAppendAttr() {
        return appendAttr;
    }

    public void setAppendAttr(String appendAttr) {
        this.appendAttr = appendAttr;
    }

    public QRCodeLoginResult getLoginResult() {
        return loginResult;
    }

    public void setLoginResult(QRCodeLoginResult loginResult) {
        this.loginResult = loginResult;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public void parseAppendAttr() {
        userInfo = JSON.parseObject(appendAttr, UserInfo.class);
    }
}