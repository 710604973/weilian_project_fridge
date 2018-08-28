package com.mode.fridge.view.dialog;

import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.mode.fridge.MyApplication;
import com.mode.fridge.R;
import com.mode.fridge.activity.LoginGuideActivity;
import com.mode.fridge.activity.ViomiDownloadActivity;
import com.mode.fridge.bean.QRCodeLogin;
import com.mode.fridge.common.base.BaseFridgeDialog;
import com.mode.fridge.common.rxbus.BusEvent;
import com.mode.fridge.common.rxbus.RxBus;
import com.mode.fridge.contract.LoginQRCodeContract;
import com.mode.fridge.presenter.LoginQRCodePresenter;
import com.mode.fridge.utils.ToastUtil;
import com.mode.fridge.utils.logUtil;
import com.viomi.widget.dialog.BaseProgressDialog;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 登录二维码生成 Dialog
 * Created by William on 2018/1/26.
 */
public class LoginQRCodeDialog extends BaseFridgeDialog implements LoginQRCodeContract.View {
    private static final String TAG = LoginQRCodeDialog.class.getSimpleName();
    private BaseProgressDialog mDialog;// 加载对话框
    private LoginQRCodeContract.Presenter mPresenter;

//    @BindView(R.id.login_qr_layout)
//    LinearLayout mQRCodeLinearLayout;// 生成二维码布局
//    @BindView(R.id.login_download_qr_layout)
//    LinearLayout mDownloadLinearLayout;// 下载 app 二维码布局

    @BindView(R.id.login_qr_code)
    SimpleDraweeView mSimpleDraweeView;// 登录二维码

    @BindView(R.id.login_retry)
    TextView mRetryTextView;// 重新获取

    @BindView(R.id.login_progressbar)
    ProgressBar mProgressBar;// 进度


    @Override
    protected void initWithOnCreate() {
        layoutId = R.layout.activity_login_viomi;
    }

    @Override
    protected void initWithOnCreateDialog(View view) {
        mPresenter = new LoginQRCodePresenter(MyApplication.getContext());
        mPresenter.subscribe(this);// 订阅
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mPresenter != null) {
            mPresenter.unSubscribe();// 取消订阅
            mPresenter = null;
        }
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.cancel();
            mDialog = null;
        }
    }

    @Override
    public void showQRCode(QRCodeLogin loginQRCode) { // 显示二维码
        mProgressBar.setVisibility(View.GONE);
        mSimpleDraweeView.setVisibility(View.VISIBLE);
        // 生成二维码
        ImageRequest request = ImageRequestBuilder
                .newBuilderWithSource(Uri.parse(loginQRCode.getResult()))
                .setResizeOptions(new ResizeOptions(220, 220))
                .build();
        PipelineDraweeController controller = (PipelineDraweeController) Fresco.newDraweeControllerBuilder()
                .setOldController(mSimpleDraweeView.getController())
                .setImageRequest(request)
                .build();
        mSimpleDraweeView.setController(controller);
        mPresenter.checkStatus();// 检查登录状态
    }

    @Override
    public void showRetry() { // 显示重试
        mSimpleDraweeView.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.GONE);
        mRetryTextView.setVisibility(View.VISIBLE);
    }

    @Override
    public void showLoading() { // 显示 Loading
        if (getActivity() != null) {
            mDialog = new BaseProgressDialog(getActivity(), MyApplication.getContext().getResources().getString(R.string.management_login_in));
            mDialog.show();
        }
    }

    @Override
    public void loginFail(String message) { // 登录失败
        logUtil.e(TAG, message);
        ToastUtil.showCenter(MyApplication.getContext(), message);
        if (mDialog != null && mDialog.isShowing()) mDialog.dismiss();
        if (mPresenter != null) mPresenter.loadQRCode();// 重新加载二维码
    }

    @Override
    public void loginSuccess() { // 登录成功
        logUtil.d(TAG, "loginSuccess");
        if (mDialog != null && mDialog.isShowing()) mDialog.dismiss();
        ToastUtil.showCenter(MyApplication.getContext(), MyApplication.getContext().getResources().getString(R.string.management_login_success));
        RxBus.getInstance().post(BusEvent.MSG_LOGIN_SUCCESS);
        dismiss();
        // 登录成功，上报属性
//        DeviceParams params = SerialManager.getInstance().getDeviceParamsSet();
//        MiotRepository.getInstance().sendPropertyMode(params.getMode());
//        MiotRepository.getInstance().sendPropertyRCRoomEnable(params.isCold_switch());
//        MiotRepository.getInstance().sendPropertyRCSetTemp(params.getCold_temp_set());
//        if (FridgePreference.getInstance().getModel().equals(AppConstants.MODEL_X5)) {
//            MiotRepository.getInstance().sendPropertyCCRoomEnable(params.isChangeable_switch());
//            MiotRepository.getInstance().sendPropertyCCSetTemp(params.getChangeable_temp_set());
//        }
//        MiotRepository.getInstance().sendPropertyFZSetTemp(params.getFreezing_temp_set());

        // 设置推送
//        PushManager.getInstance().setMallPushEnable(new PreferenceHelper(MyApplication.getContext()).getPushSetting().isMallEnable());
    }

    @OnClick(R.id.login_retry)
    public void retry() { // 重试
        mProgressBar.setVisibility(View.VISIBLE);
        mRetryTextView.setVisibility(View.GONE);
        if (mPresenter != null) mPresenter.loadQRCode();
    }


    @OnClick(R.id.tv_back)
    public void close() { // 关闭
        dismiss();
    }

    @OnClick(R.id.login_download_app)
    public void showDownload() { // 进入下载云米商城 Ui
//        mQRCodeLinearLayout.setVisibility(View.GONE);
//        mDownloadLinearLayout.setVisibility(View.VISIBLE);
//        mQRCodeLinearLayout.setAnimation(AnimationUtils.makeOutAnimation(MyApplication.getContext(), false));
//        mDownloadLinearLayout.setAnimation(AnimationUtils.makeInAnimation(MyApplication.getContext(), false));
        Intent intent = new Intent(getActivity(), ViomiDownloadActivity.class);
        startActivity(intent);
    }
//
//    @OnClick(R.id.login_back)
//    public void hideDownload() { // 进入二维码登录 Ui
//        mQRCodeLinearLayout.setVisibility(View.VISIBLE);
//        mDownloadLinearLayout.setVisibility(View.GONE);
//        mQRCodeLinearLayout.setAnimation(AnimationUtils.makeInAnimation(MyApplication.getContext(), true));
//        mDownloadLinearLayout.setAnimation(AnimationUtils.makeOutAnimation(MyApplication.getContext(), true));
//    }

    @OnClick(R.id.login_guide)
    public void guide() { // 进入登录指引
        Intent intent = new Intent(getActivity(), LoginGuideActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.login_qr_code)
    public void refresh() { // 点击刷新二维码
        mProgressBar.setVisibility(View.VISIBLE);
        mRetryTextView.setVisibility(View.GONE);
        mPresenter.unSubscribe();
        mPresenter.subscribe(this);
    }
}