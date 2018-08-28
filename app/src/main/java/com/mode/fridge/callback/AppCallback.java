package com.mode.fridge.callback;

/**
 * Created by young2 on 2015/12/31.
 */
public interface AppCallback<T> {

    /**
     * 成功时调用
     *
     * @param data 返回数据
     */
    void onSuccess(T data);

    /**
     * 失败时调用
     *
     * @param errorCode 错误码
     * @param msg       错误信息
     */
    void onFail(int errorCode, String msg);
}