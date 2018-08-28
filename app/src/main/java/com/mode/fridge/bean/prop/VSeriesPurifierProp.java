package com.mode.fridge.bean.prop;

import com.mode.fridge.MyApplication;
import com.mode.fridge.R;

import java.util.List;

/**
 * V 系列净水器属性
 * Created by William on 2018/2/7.
 */

public class VSeriesPurifierProp {
    private int f1_totaltime;// 滤芯的时间总寿命
    private int f1_usedtime;// 滤芯已经使用的时间
    private int f2_totaltime;// 滤芯的时间总寿命
    private int f2_usedtime;// 滤芯已经使用的时间
    private int f3_totaltime;// 滤芯的时间总寿命
    private int f3_usedtime;// 滤芯已经使用的时间
    private int f4_totaltime;// 滤芯的时间总寿命
    private int f4_usedtime;// 滤芯已经使用的时间
    private int tds_out;// 本次制水出水水质（平均值）
    private int f1_Life;// 滤芯剩余寿命
    private int f2_Life;// 滤芯剩余寿命
    private int f3_Life;// 滤芯剩余寿命
    private int f4_Life;// 滤芯剩余寿命
    private int temperature;// 进水水温
    private String uv_state;// UV 状态 1 - 打开 0 - 关闭
    private int press;// 用水自来水水压
    private String elecval_state;// 电动阀状态1 - 打开 0 - 关闭

    public void initGetProp(List<Object> list) {
        f1_totaltime = (int) list.get(11);
        f1_usedtime = (int) list.get(3);
        f2_totaltime = (int) list.get(13);
        f2_usedtime = (int) list.get(5);
        f3_totaltime = (int) list.get(15);
        f3_usedtime = (int) list.get(7);
        f4_totaltime = (int) list.get(17);
        f4_usedtime = (int) list.get(9);
        tds_out = (int) list.get(1);

        f1_Life = (f1_totaltime - f1_usedtime) * 100 / f1_totaltime;
        f2_Life = (f2_totaltime - f2_usedtime) * 100 / f2_totaltime;
        f3_Life = (f3_totaltime - f3_usedtime) * 100 / f3_totaltime;
        f4_Life = (f4_totaltime - f4_usedtime) * 100 / f4_totaltime;
    }

    public void initGetExtraProp(List<Object> list) {
        temperature = (int) list.get(0);
        uv_state = (int) list.get(1) == 1 ? MyApplication.getContext().getResources().getString(R.string.iot_water_purifier_uv_working) :
                MyApplication.getContext().getResources().getString(R.string.iot_water_purifier_uv_free);
        press = (int) list.get(2);
        elecval_state = (int) list.get(3) == 1 ? MyApplication.getContext().getResources().getString(R.string.iot_water_purifier_water_switch_open) :
                MyApplication.getContext().getResources().getString(R.string.iot_water_purifier_water_switch_close);
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

    public int getF4_totaltime() {
        return f4_totaltime;
    }

    public int getF4_usedtime() {
        return f4_usedtime;
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

    public int getF4_Life() {
        return f4_Life;
    }

    public int getTemperature() {
        return temperature;
    }

    public String getUv_state() {
        return uv_state;
    }

    public int getPress() {
        return press;
    }

    public String getElecval_state() {
        return elecval_state;
    }
}
