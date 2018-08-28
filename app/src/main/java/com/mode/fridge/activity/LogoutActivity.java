package com.mode.fridge.activity;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.mode.fridge.R;
import com.mode.fridge.bean.QRCodeBase;
import com.mode.fridge.common.base.BaseActivity;
import com.mode.fridge.contract.ManageContract;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 注销activity
 */
public class LogoutActivity extends BaseActivity implements ManageContract.View {

    @BindView(R.id.iv_icon)
    SimpleDraweeView mSimpleDraweeView;// 头像

    @BindView(R.id.tv_nick)
    TextView tv_nick;// 用户名称

    @BindView(R.id.tv_back)
    TextView tv_back;// 返回

    @BindView(R.id.tv_account)
    TextView tv_account;// 用户名称

    @Inject
    ManageContract.Presenter mPresenter1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        layoutId = R.layout.activity_logout;
        super.onCreate(savedInstanceState);
        bindatas();
    }


    private void bindatas() {
        if (mPresenter1 != null) {
            mPresenter1.subscribe(this);
        }

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String nick = bundle.getString("nick", "");
            String account = bundle.getString("account", "");
            String hmg = bundle.getString("hmg", "");
            tv_nick.setText(nick);
            tv_account.setText(account);
            if (!TextUtils.isEmpty(hmg)) {
                Uri uri = Uri.parse(hmg);
                ImageRequest request = ImageRequestBuilder
                        .newBuilderWithSource(uri)
                        .setResizeOptions(new ResizeOptions(120, 120))
                        .build();
                PipelineDraweeController controller = (PipelineDraweeController) Fresco.newDraweeControllerBuilder()
                        .setOldController(mSimpleDraweeView.getController())
                        .setImageRequest(request)
                        .build();
                mSimpleDraweeView.setController(controller);
            }
        }

        tv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @OnClick(R.id.btn_logout)
    public void loginQuit() { // 登录或注销登录
        if (mPresenter1 != null) mPresenter1.logout();// 退出登录
        finish();
    }
//
//    @OnClick({R.id.btn_logout, R.id.btn_logout})
//    public void onClick(View view) { // 登录或注销登录
//        switch (view.getId()) {
//            case R.id.btn_logout:
//                if (mPresenter1 != null) mPresenter1.logout();// 退出登录
//                finish();
//                break;
//            case R.id.tv_back:
//                finish();
//                break;
//        }
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPresenter1 != null) {
            mPresenter1.unSubscribe();
            mPresenter1 = null;
        }
    }

    @Override
    public void showUserInfo(QRCodeBase qrCodeBase) {

    }
}
