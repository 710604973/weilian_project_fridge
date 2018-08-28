package com.mode.fridge.callback;

/**
 * 所有 Presenter interface 必须实现此接口
 * Created by William on 2018/1/2.
 */
public interface BasePresenter<T> {

    /**
     * 订阅
     */
    void subscribe(T view);

    /**
     * 取消订阅
     */
    void unSubscribe();
}