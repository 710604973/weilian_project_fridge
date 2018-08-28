package com.mode.fridge.bean.aidl;

import java.util.ArrayList;

public class Result {

    private ArrayList<StreamBean> control = new ArrayList<>();

    public ArrayList<StreamBean> getControl() {
        return control;
    }

    public void setControl(ArrayList<StreamBean> control) {
        this.control = control;
    }
}
