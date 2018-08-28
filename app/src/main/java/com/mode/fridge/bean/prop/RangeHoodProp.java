package com.mode.fridge.bean.prop;

import java.util.List;

/**
 * 烟机属性
 * Created by William on 2018/2/21.
 */

public class RangeHoodProp {
    private String run_time;// 设备工作时间
    private String power_state;// 设备电源状态: 0 - 关机，1 - 待机，2 - 开机，3 - 清洗，4 - 清洗复位
    private String wind_state;// 设备风机状态: bit0: 0 - 低风OFF，1 - 低风ON；bit2：0 - 爆炒OFF，1 - 爆炒ON；bit4：0 - 高风OFF，1 - 高风ON
    private String light_state;// 设备照明灯状态: 0 - OFF，1 - ON
    private String link_state;// 烟灶联动状态: 0 - 蓝牙未绑定，1 - 蓝牙绑定未连接，2 - 蓝牙连接
    private String stove1_data;// 烟灶 1：0 - 关闭，1 - 小火，2 - 大火
    private String stove2_data;// 烟灶 2：0 - 关闭，1 - 小火，2 - 大火
    //    private int pm2_5;// 1 - 空气质量优，2 - 空气质量良，3 - 空气质量轻度污染，4 - 空气质量中度污染，5 - 空气质量重度污染，6 - 空气质量严重污染
    private String battary_life;// 电池电量（0: 0%，100: 100%）
    private String poweroff_delaytime;// 延迟关机时间，单位/s
    //    private int aqi_state;// 空气质量监测状态 0 - OFF，1 - ON
//    private int aqi_thd;// 自动换新风阈值
//    private int aqi_hour;// 自动换新风起始时刻（时）
//    private int aqi_min;// 自动换新风起始时刻（分）
//    private int aqi_time;// 自动换新风时长
    private String curise_state;// 增压巡航
    private String light_sync_state;// 关机关灯

    public RangeHoodProp(List<Object> list) {
        run_time = String.valueOf(list.get(0));
        power_state = String.valueOf(list.get(1));
        wind_state = String.valueOf(list.get(2));
        light_state = String.valueOf(list.get(3));
        link_state = String.valueOf(list.get(4));
        stove1_data = String.valueOf(list.get(5));
        stove2_data = String.valueOf(list.get(6));
//        pm2_5 = (int) list.get(7);
        battary_life = String.valueOf(list.get(8));
        poweroff_delaytime = String.valueOf(list.get(9));
//        aqi_state = (int) list.get(10);
//        aqi_thd = (int) list.get(11);
//        aqi_hour = (int) list.get(12);
//        aqi_min = (int) list.get(13);
//        aqi_time = (int) list.get(14);
        curise_state = String.valueOf(list.get(14));
        light_sync_state = String.valueOf(list.get(15));
    }

    public String getRun_time() {
        return run_time;
    }

    public String getPower_state() {
        return power_state;
    }

    public String getWind_state() {
        return wind_state;
    }

    public String getLight_state() {
        return light_state;
    }

    public String getLink_state() {
        return link_state;
    }

    public String getStove1_data() {
        return stove1_data;
    }

    public String getStove2_data() {
        return stove2_data;
    }

//    public int getPm2_5() {
//        return pm2_5;
//    }

    public String getBattary_life() {
        return battary_life;
    }

    public String getPoweroff_delaytime() {
        return poweroff_delaytime;
    }

//    public int getAqi_state() {
//        return aqi_state;
//    }
//
//    public int getAqi_thd() {
//        return aqi_thd;
//    }
//
//    public int getAqi_hour() {
//        return aqi_hour;
//    }
//
//    public int getAqi_min() {
//        return aqi_min;
//    }
//
//    public int getAqi_time() {
//        return aqi_time;
//    }

    public String getCurise_state() {
        return curise_state;
    }

    public String getLight_sync_state() {
        return light_sync_state;
    }
}
