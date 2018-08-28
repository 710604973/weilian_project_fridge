package com.mode.fridge.bean.prop;


import com.mode.fridge.MyApplication;
import com.mode.fridge.R;

import java.util.List;

/**
 * S 系列净水器属性
 * Created by William on 2018/2/7.
 */

public class SSeriesPurifierProp {
    private int f1_totaltime;// 滤芯的时间总寿命
    private int f1_totalFlow;// 滤芯的流量总寿命
    private int f1_usedtime;// 滤芯已经使用的时间
    private int f1_usedFlow;// 滤芯已经使用的流量
    private int f2_totaltime;// 滤芯的时间总寿命
    private int f2_totalFlow;// 滤芯的时间总流量
    private int f2_usedtime;// 滤芯已经使用的时间
    private int f2_usedFlow;// 滤芯已经使用的流量
    private int f3_totaltime;// 滤芯的时间总寿命
    private int f3_totalFlow;// 滤芯的流量总寿命
    private int f3_usedtime;// 滤芯已经使用的时间
    private int f3_usedFlow;// 滤芯已经使用的时间
    private int tds_out;// 本次制水出水水质（平均值）
    private int f1_Life;// 滤芯剩余寿命
    private int f2_Life;// 滤芯剩余寿命
    private int f3_Life;// 滤芯剩余寿命
    private int temperature;// 进水水温
    private String uv_state;// UV 状态 1 - 打开 0 - 关闭
    private String openStatus;// 制水状态 1 - 制水中 0 - 空闲中

    public SSeriesPurifierProp(List<Object> list) {
        f1_totaltime = (int) list.get(9);
        f1_totalFlow = (int) list.get(8);
        f1_usedtime = (int) list.get(3);
        f1_usedFlow = (int) list.get(2);
        f2_totaltime = (int) list.get(11);
        f2_totalFlow = (int) list.get(10);
        f2_usedtime = (int) list.get(5);
        f2_usedFlow = (int) list.get(4);
        f3_totaltime = (int) list.get(13);
        f3_totalFlow = (int) list.get(12);
        f3_usedtime = (int) list.get(7);
        f3_usedFlow = (int) list.get(6);
        tds_out = (int) list.get(1);
        temperature = (int) list.get(18);
        uv_state = (int) list.get(17) == 1 ? MyApplication.getContext().getResources().getString(R.string.iot_water_purifier_uv_working) :
                MyApplication.getContext().getResources().getString(R.string.iot_water_purifier_uv_free);
        openStatus = (int) list.get(21) == 1 ? MyApplication.getContext().getResources().getString(R.string.iot_water_purifier_water_rationing_working) :
                MyApplication.getContext().getResources().getString(R.string.iot_water_purifier_water_rationing_free);

        int f1_time = (f1_totaltime - f1_usedtime) * 100 / f1_totaltime;
        int f1_flow = (f1_totalFlow - f1_usedFlow) * 100 / f1_totalFlow;

        int f2_time = (f2_totaltime - f2_usedtime) * 100 / f2_totaltime;
        int f2_flow = (f2_totalFlow - f2_usedFlow) * 100 / f2_totalFlow;

        int f3_time = (f3_totaltime - f3_usedtime) * 100 / f3_totaltime;
        int f3_flow = (f3_totalFlow - f3_usedFlow) * 100 / f3_totalFlow;

        f1_Life = f1_time <= f1_flow ? f1_time : f1_flow;
        f2_Life = f2_time <= f2_flow ? f2_time : f2_flow;
        f3_Life = f3_time <= f3_flow ? f3_time : f3_flow;
    }

    public int getF1_totaltime() {
        return f1_totaltime;
    }

    public int getF1_usedtime() {
        return f1_usedtime;
    }

    public int getF2_totaltime() {
        return f2_totaltime;
    }

    public int getF2_usedtime() {
        return f2_usedtime;
    }

    public int getF3_totaltime() {
        return f3_totaltime;
    }

    public int getF3_usedtime() {
        return f3_usedtime;
    }

    public int getF1_totalFlow() {
        return f1_totalFlow;
    }

    public int getF1_usedFlow() {
        return f1_usedFlow;
    }

    public int getF2_totalFlow() {
        return f2_totalFlow;
    }

    public int getF2_usedFlow() {
        return f2_usedFlow;
    }

    public int getF3_totalFlow() {
        return f3_totalFlow;
    }

    public int getF3_usedFlow() {
        return f3_usedFlow;
    }

    public int getTds_out() {
        return tds_out;
    }

    public int getF1_Life() {
        return f1_Life;
    }

    public int getF2_Life() {
        return f2_Life;
    }

    public int getF3_Life() {
        return f3_Life;
    }

    public int getTemperature() {
        return temperature;
    }

    public String getUv_state() {
        return uv_state;
    }

    public String getOpenStatus() {
        return openStatus;
    }
}
