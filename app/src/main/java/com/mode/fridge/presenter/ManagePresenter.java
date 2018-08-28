package com.mode.fridge.presenter;

import android.content.Context;

import com.mode.fridge.AppConstants;
import com.mode.fridge.R;
import com.mode.fridge.common.rxbus.BusEvent;
import com.mode.fridge.common.rxbus.RxBus;
import com.mode.fridge.contract.ManageContract;
import com.mode.fridge.repository.ManageRepository;
import com.mode.fridge.utils.RxSchedulerUtil;
import com.mode.fridge.utils.ToastUtil;
import com.mode.fridge.utils.ToolUtil;
import com.mode.fridge.utils.logUtil;

import javax.annotation.Nullable;
import javax.inject.Inject;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * 管理中心 Presenter
 * Created by William on 2018/1/29.
 */
public class ManagePresenter implements ManageContract.Presenter {
    private static final String TAG = ManagePresenter.class.getSimpleName();
    private Context mContext;
    private CompositeSubscription mCompositeSubscription;

    @Nullable
    private ManageContract.View mView;

    @Inject
    ManagePresenter(Context context) {
        this.mContext = context;
    }

    @Override
    public void subscribe(ManageContract.View view) {
        this.mView = view;
        mCompositeSubscription = new CompositeSubscription();
        loadUserInfo();
    }

    @Override
    public void unSubscribe() {
        this.mView = null;
        if (mCompositeSubscription != null) {
            mCompositeSubscription.unsubscribe();
            mCompositeSubscription = null;
        }
    }

    @Override
    public void loadUserInfo() {
        Subscription subscription = ManageRepository.getInstance().getUser(mContext)
                .compose(RxSchedulerUtil.SchedulersTransformer1())
                .onTerminateDetach()
                .subscribe(qrCodeBase -> {
                    if (mView != null) mView.showUserInfo(qrCodeBase);
                }, throwable -> logUtil.e(TAG, throwable.getMessage()));
        mCompositeSubscription.add(subscription);
    }

    @Override
    public void logout() {
        Subscription subscription = ManageRepository.getInstance().logout()
                .subscribeOn(Schedulers.io())
                .onTerminateDetach()
                .map(aBoolean -> {
                    if (aBoolean)
                        ToolUtil.saveObject(mContext, AppConstants.USER_INFO_FILE, null);// 删除本地云米账号信息
                    return aBoolean;
                })
                .observeOn(AndroidSchedulers.mainThread())
                .onTerminateDetach()
                .subscribe(aBoolean -> {
                    if (aBoolean && mView != null) {
                        RxBus.getInstance().post(BusEvent.MSG_LOGOUT_SUCCESS);
                        // 注销推送
//                        PushManager.getInstance().setMallPushEnable(false);
                        mView.showUserInfo(null);
                    } else if (mView != null)
                        ToastUtil.showCenter(mContext, mContext.getResources().getString(R.string.management_logout_fail));
                }, throwable -> {
                    if (mView != null)
                        ToastUtil.showCenter(mContext, mContext.getResources().getString(R.string.management_logout_fail));
                    logUtil.e(TAG, throwable.getMessage());
                });
        mCompositeSubscription.add(subscription);
    }
}