package com.mode.fridge.presenter;

import android.content.Context;
import android.text.TextUtils;

import com.mode.fridge.R;
import com.mode.fridge.contract.LoginQRCodeContract;
import com.mode.fridge.preference.LoginPreference;
import com.mode.fridge.repository.LoginRepository;
import com.mode.fridge.repository.MiotRepository;
import com.mode.fridge.utils.RxSchedulerUtil;
import com.mode.fridge.utils.logUtil;

import java.util.concurrent.TimeUnit;

import javax.annotation.Nullable;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * 登录二维码生成 Presenter
 * Created by William on 2018/1/27.
 */
public class LoginQRCodePresenter implements LoginQRCodeContract.Presenter {
    private static final String TAG = LoginQRCodePresenter.class.getSimpleName();
    private CompositeSubscription mCompositeSubscription;
    private Context mContext;

    @Nullable
    private LoginQRCodeContract.View mView;


    public LoginQRCodePresenter(Context context) {
        mContext = context;
    }

    @Override
    public void subscribe(LoginQRCodeContract.View view) {
        this.mView = view;
        mCompositeSubscription = new CompositeSubscription();
        loadQRCode();
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
    public void loadQRCode() {
        Subscription subscription = LoginRepository.getInstance().createQRCode(mContext)
                .compose(RxSchedulerUtil.SchedulersTransformer1())
                .filter(result -> result != null && !TextUtils.isEmpty(result.getLoginQRCode().getResult()))
                .onTerminateDetach()
                .subscribe(result -> {
                    if (mView != null) mView.showQRCode(result.getLoginQRCode());
                }, throwable -> {
                    logUtil.e(TAG, throwable.getMessage());
                    if (mView != null) mView.showRetry();
                });
        mCompositeSubscription.add(subscription);
    }

    @Override
    public void checkStatus() {
        String clientId = LoginPreference.getInstance().getClientId();
        Subscription subscription = Observable.interval(0, 3, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .onTerminateDetach()
                .flatMap(aLong -> LoginRepository.getInstance().getLoginStatus(clientId))
                .takeUntil(result -> result != null && result.getLoginQRCode().getToken() != null && result.getLoginQRCode().getAppendAttr() != null
                        && result.getLoginQRCode().getLoginResult() != null && result.getLoginQRCode().getLoginResult().getUserCode() != null)
                .filter(result -> result != null && result.getLoginQRCode().getToken() != null && result.getLoginQRCode().getAppendAttr() != null
                        && result.getLoginQRCode().getLoginResult() != null && result.getLoginQRCode().getLoginResult().getUserCode() != null)
                .observeOn(AndroidSchedulers.mainThread())
                .onTerminateDetach()
                .map(qrCodeBase -> {
                    if (mView != null) mView.showLoading();
                    return qrCodeBase;
                })
                .observeOn(Schedulers.io())
                .onTerminateDetach()
                .flatMap(qrCodeBase -> MiotRepository.getInstance().bindDevice(qrCodeBase))
                .onTerminateDetach()
                .map(qrCodeBase -> qrCodeBase != null && LoginRepository.getInstance().saveUserInfo(mContext, qrCodeBase))
                .observeOn(AndroidSchedulers.mainThread())
                .onTerminateDetach()
                .subscribe(aBoolean -> {
                    if (aBoolean) {
                        if (mView != null) mView.loginSuccess();
                    } else if (mView != null)
                        mView.loginFail(mContext.getResources().getString(R.string.management_login_error_tip));
                }, throwable -> {
                    logUtil.e(TAG, throwable.getMessage());
                    if (mView != null)
                        mView.loginFail(mContext.getResources().getString(R.string.management_login_error_tip));
                });
        mCompositeSubscription.add(subscription);
    }
}