package com.mode.fridge.bean;

import com.miot.common.abstractdevice.AbstractDevice;

import java.util.ArrayList;
import java.util.List;

/**
 * 设备分类
 * Created by William on 2018/3/2.
 */
public class DeviceTypeEntity {
    private String type = "";// 设备分类名称
    private boolean isExist = false;// 分类是否存在设备
    private boolean isOnSale = false;// 设备是否已上架
    private boolean isAsking = false;// 是否正在获取数据
    private String data;// 显示数据
    private List<AbstractDevice> list;// 小米 IOT 设备集合

    public boolean isAsking() {
        return isAsking;
    }

    public void setAsking(boolean asking) {
        isAsking = asking;
    }

    public DeviceTypeEntity() {
        list = new ArrayList<>();
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isExist() {
        return isExist;
    }

    public void setExist(boolean exist) {
        isExist = exist;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public boolean isOnSale() {
        return isOnSale;
    }

    public void setOnSale(boolean onSale) {
        isOnSale = onSale;
    }

    public List<AbstractDevice> getList() {
        return list;
    }

    public void setList(List<AbstractDevice> list) {
        this.list = list;
    }
}