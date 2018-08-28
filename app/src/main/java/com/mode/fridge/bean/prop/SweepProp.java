package com.mode.fridge.bean.prop;

import java.util.List;

/**
 * 烟机属性
 * Created by William on 2018/2/21.
 */

public class SweepProp {
    private int run_state;//模式
    private int s_area;//本次清扫面积
    private int battary_life;//电池剩余电量百分比，0 - 0%，100 - 100%
    private int s_time;//本次清扫时间
    private int start_time;//扫地开始时间，单位/s
//    private int order_time;//扫地机预约时间

    public SweepProp(List<Object> list) {
        run_state = (int) list.get(0);
        battary_life = (int) list.get(1);
        start_time = (int) list.get(2);
        if (list.size() > 3) {
            s_time = (int) list.get(3);
            s_area = (int) list.get(4);
        }
//        order_time = (int) list.get(3);
//        s_time = (int) list.get(3);
//        s_area = (int) list.get(4);
    }

    public int getRun_state() {
        return run_state;
    }

    public void setRun_state(int run_state) {
        this.run_state = run_state;
    }


    public int getS_time() {
        return s_time;
    }

    public void setS_time(int s_time) {
        this.s_time = s_time;
    }

    public int getStart_time() {
        return start_time;
    }

    public void setStart_time(int start_time) {
        this.start_time = start_time;
    }

//    public int getOrder_time() {
//        return order_time;
//    }
//    public void setOrder_time(int order_time) {
//        this.order_time = order_time;
//    }

    public int getS_area() {
        return s_area;
    }

    public void setS_area(int s_area) {
        this.s_area = s_area;
    }

    public int getBattary_life() {
        return battary_life;
    }

    public void setBattary_life(int battary_life) {
        this.battary_life = battary_life;
    }
}
