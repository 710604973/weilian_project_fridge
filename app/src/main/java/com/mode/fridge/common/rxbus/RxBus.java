package com.mode.fridge.common.rxbus;


import com.mode.fridge.utils.logUtil;

import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

/**
 * 发送:
 * RxBus.getInstance().post(BusEvent.MSG_XXX, object);
 * 订阅:
 * subscription = RxBus.getInstance().subscribe();
 */
public class RxBus {
    private static volatile RxBus mInstance;
    private final Subject<Object, Object> mBus;

    private RxBus() {
        mBus = new SerializedSubject<>(PublishSubject.create());
    }

    /**
     * 单例模式
     *
     * @return RxBus
     */
    public static RxBus getInstance() {
        if (mInstance == null) {
            synchronized (RxBus.class) {
                if (mInstance == null) {
                    mInstance = new RxBus();
                }
            }
        }
        return mInstance;
    }

    /**
     * 发送消息
     *
     * @param msgId  消息id
     * @param object 内容
     */
    public void post(int msgId, Object object) {
        if (mBus.hasObservers())
            post(new BusEvent(msgId, object));
    }

    /**
     * 发送消息
     *
     * @param msgId 消息id
     */
    public void post(int msgId) {
        if (mBus.hasObservers())
            mBus.onNext(new BusEvent(msgId, null));
    }

    private void post(Object object) {
        if (mBus.hasObservers())
            mBus.onNext(object);
    }

    /**
     * 订阅消息
     *
     * @param onNext the on next
     * @return subscription
     */
    public final Subscription subscribe(final Action1<? super BusEvent> onNext) {
        return toObservable().onTerminateDetach().subscribe(onNext, throwable -> {
            // 发生异常会取消订阅
            logUtil.e("subscribe error", throwable.toString());
            throwable.printStackTrace();
        });
    }

    public Observable<BusEvent> toObservable() {
        return mBus.ofType(BusEvent.class);
    }
}