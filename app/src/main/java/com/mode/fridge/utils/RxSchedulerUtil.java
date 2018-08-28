package com.mode.fridge.utils;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 统一管理观察者模式中线程调度（使用 RxJava 过程中会频繁
 * 调用 subscribeOn() 和 observeOn()，通过 Transformer 结合
 * Observable.compose() 可实现复用）
 * <p>
 * Created by William on 2018/1/3.
 */
public final class RxSchedulerUtil {

    public static <T> Observable.Transformer<T, T> SchedulersTransformer1() {
        return observable -> observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static <T> Observable.Transformer<T, T> SchedulersTransformer2() {
        return observable -> observable.subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io());
    }

    public static <T> Observable.Transformer<T, T> SchedulersTransformer3() {
        return observable -> observable.subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation());
    }

    public static <T> Observable.Transformer<T, T> SchedulersTransformer4() {
        return observable -> observable.subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static <T> Observable.Transformer<T, T> SchedulersTransformer5() {
        return observable -> observable.subscribeOn(Schedulers.computation())
                .observeOn(Schedulers.io());
    }

    public static <T> Observable.Transformer<T, T> SchedulersTransformer6() {
        return observable -> observable.subscribeOn(Schedulers.computation())
                .observeOn(Schedulers.computation());
    }
}