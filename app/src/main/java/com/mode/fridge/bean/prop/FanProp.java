package com.mode.fridge.bean.prop;

import java.util.List;

/**
 * 烟机属性
 * Created by William on 2018/2/21.
 */

public class FanProp {
    private String power_state;// 0 - 风扇停止工作  1 - 风扇开始工作
    private String wind_mode;// 0 - 标准风 1 - 自然风  2 - 节能风
    private String shake_state;//0 - 关闭摇头功能 1 - 开启摇头功能
    private int work_time;//定时剩余时间（单位/min）65535 - 当前无定时

    public FanProp(List<Object> list) {
        power_state = String.valueOf(list.get(2));
        wind_mode = String.valueOf(list.get(0));
        shake_state = String.valueOf(list.get(1));
        work_time = (Integer) list.get(3);
    }

    public String getWind_mode() {
        return wind_mode;
    }

    public String getShake_state() {
        return shake_state;
    }

    public int getWork_time() {
        return work_time;
    }

    public String getPower_state() {
        return power_state;
    }
}
