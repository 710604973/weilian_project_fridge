package com.mode.fridge.bean.prop;

import java.util.List;

/**
 * 烟机属性
 * Created by William on 2018/2/21.
 */

public class WashDishProp {

    private int program;
    private int ldj_state;
    private int salt_state;
    private int left_time;//洗涤剩余时间
    private int bespeak_h;//预约洗，当前设置的小时数值
    private int bespeak_min;//预约洗，当前设置的分钟数值
    private int bespeak_status;//预约状态，只有空闲才判断
    private int wash_process;//设备洗涤过程
    private int wash_status;//设备工作状态，工作中才判断

    public WashDishProp(List<Object> list) {
        program = (int) list.get(0);
        ldj_state = (int) list.get(1);
        salt_state = (int) list.get(2);
        left_time = (int) list.get(3);
        bespeak_h = (int) list.get(4);
        bespeak_min = (int) list.get(5);
        bespeak_status = (int) list.get(6);
        wash_process = (int) list.get(7);
        wash_status = (int) list.get(8);
    }

    public int getProgram() {
        return program;
    }

    public void setProgram(int program) {
        this.program = program;
    }

    public int getLdj_state() {
        return ldj_state;
    }

    public void setLdj_state(int ldj_state) {
        this.ldj_state = ldj_state;
    }

    public int getSalt_state() {
        return salt_state;
    }

    public void setSalt_state(int salt_state) {
        this.salt_state = salt_state;
    }

    public int getLeft_time() {
        return left_time;
    }

    public void setLeft_time(int left_time) {
        this.left_time = left_time;
    }

    public int getBespeak_h() {
        return bespeak_h;
    }

    public void setBespeak_h(int bespeak_h) {
        this.bespeak_h = bespeak_h;
    }

    public int getBespeak_min() {
        return bespeak_min;
    }

    public void setBespeak_min(int bespeak_min) {
        this.bespeak_min = bespeak_min;
    }

    public int getBespeak_status() {
        return bespeak_status;
    }

    public void setBespeak_status(int bespeak_status) {
        this.bespeak_status = bespeak_status;
    }

    public int getWash_process() {
        return wash_process;
    }

    public void setWash_process(int wash_process) {
        this.wash_process = wash_process;
    }

    public int getWash_status() {
        return wash_status;
    }

    public void setWash_status(int wash_status) {
        this.wash_status = wash_status;
    }
}
