package com.mode.fridge.bean.aidl;

public class StreamBean {
    private String current_value="";
    private String stream_id="";

    public String getCurrent_value() {
        return current_value;
    }

    public void setCurrent_value(String current_value) {
        this.current_value = current_value;
    }

    public String getStream_id() {
        return stream_id;
    }

    public void setStream_id(String stream_id) {
        this.stream_id = stream_id;
    }
}
