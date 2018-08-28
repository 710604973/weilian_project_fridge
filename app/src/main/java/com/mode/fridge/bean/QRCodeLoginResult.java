package com.mode.fridge.bean;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;
import java.util.List;

/**
 * 登录二维码 Json
 * Created by William on 2018/1/29.
 */
public class QRCodeLoginResult implements Serializable {
    @JSONField(name = "accountType")
    private int accountType;
    @JSONField(name = "loginType")
    private int loginType;
    @JSONField(name = "userId")
    private int userId;
    @JSONField(name = "userCode")
    private String userCode;
    @JSONField(name = "roles")
    private List<String> roles;// 账号角色
    @JSONField(name = "headImg")
    private String headImg;// 微信头像链接

    public int getAccountType() {
        return accountType;
    }

    public void setAccountType(int accountType) {
        this.accountType = accountType;
    }

    public int getLoginType() {
        return loginType;
    }

    public void setLoginType(int loginType) {
        this.loginType = loginType;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public String getHeadImg() {
        return headImg;
    }

    public void setHeadImg(String headImg) {
        this.headImg = headImg;
    }
}