package com.mode.fridge.bean.prop;


import com.mode.fridge.AppConstants;

import java.util.List;

public class WashMachineProp {
    private String program;
    private int wash_process;
    private int wash_status;
    private int water_temp;
    private int rinse_time;
    private int remain_time;
    private String spin_level;
    private int level;
    private int appoint_time;
    private int be_status;

    public WashMachineProp(String mode, List<Object> list) {
        program = (String) list.get(0);
        wash_process = (int) list.get(1);
        wash_status = (int) list.get(2);
        water_temp = (int) list.get(3);
        rinse_time = (int) list.get(4);
        remain_time = (int) list.get(5);
        if (mode.equals(AppConstants.VIOMI_WASHER_U1)) {
            spin_level = (String) list.get(6);
        } else {
            level = (int) list.get(6);
        }
        appoint_time = (int) list.get(7);
        if (list.size() > 8) {
            be_status = (int) list.get(8);
        }
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getProgram() {
        return program;
    }

    public void setProgram(String program) {
        this.program = program;
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

    public int getWater_temp() {
        return water_temp;
    }

    public void setWater_temp(int water_temp) {
        this.water_temp = water_temp;
    }

    public int getRinse_time() {
        return rinse_time;
    }

    public void setRinse_time(int rinse_time) {
        this.rinse_time = rinse_time;
    }

    public int getRemain_time() {
        return remain_time;
    }

    public void setRemain_time(int remain_time) {
        this.remain_time = remain_time;
    }

    public String getSpin_level() {
        return spin_level;
    }

    public void setSpin_level(String spin_level) {
        this.spin_level = spin_level;
    }

    public int getAppoint_time() {
        return appoint_time;
    }

    public void setAppoint_time(int appoint_time) {
        this.appoint_time = appoint_time;
    }

    public int getBe_status() {
        return be_status;
    }

    public void setBe_status(int be_status) {
        this.be_status = be_status;
    }
}
