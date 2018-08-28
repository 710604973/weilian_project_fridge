package com.mode.fridge.bean.prop;


import com.alibaba.fastjson.annotation.JSONField;

import java.util.List;


/**
 * 小米 RPC 接口返回接口
 * Created by William on 2018/2/6.
 */

public class RPCResult {
    @JSONField(name = "message")
    private String message;

    @JSONField(name = "result")
    private List<Object> list;

    @JSONField(name = "code")
    private int code;

    public String getMessage() {
        return message;
    }

    public List<Object> getList() {
        return list;
    }

    public int getCode() {
        return code;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setList(List<Object> list) {
        this.list = list;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
