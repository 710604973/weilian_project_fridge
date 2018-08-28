package com.mode.fridge.activity.web;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;

import com.miot.api.MiotManager;
import com.miot.common.people.People;
import com.mode.fridge.AppConstants;
import com.mode.fridge.MyApplication;
import com.mode.fridge.R;
import com.mode.fridge.bean.QRCodeBase;
import com.mode.fridge.common.rxbus.BusEvent;
import com.mode.fridge.common.rxbus.RxBus;
import com.mode.fridge.repository.ManageRepository;
import com.mode.fridge.utils.FileUtil;
import com.mode.fridge.utils.RxSchedulerUtil;
import com.mode.fridge.utils.ToolUtil;
import com.mode.fridge.utils.logUtil;
import com.mode.fridge.view.dialog.LoginQRCodeDialog;

import org.json.JSONException;
import org.json.JSONObject;

import rx.Subscription;

/**
 * 云米商城网页 Activity
 * Created by young2 on 2017/2/21.
 */
public class MallWebActivity extends BaseWebActivity {
    private final static String TAG = MallWebActivity.class.getSimpleName();
    private VMallJavaScriptInterface mJavaScriptInterface;// JS 接口
    private Subscription mSubscription;// 消息订阅
    private LoginQRCodeDialog mLoginQRCodeDialog;// 登录对话框

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mTitle = "";
        layoutId = R.layout.activity_webview;
        super.onCreate(savedInstanceState);
        String url = getIntent().getStringExtra(AppConstants.WEB_URL);
        mWebView.loadUrl(url);
        mJavaScriptInterface = new VMallJavaScriptInterface();
        mWebView.addJavascriptInterface(mJavaScriptInterface, "H5ToNative");

        mSubscription = RxBus.getInstance().subscribe(busEvent -> {
            switch (busEvent.getMsgId()) {
                case BusEvent.MSG_LOGIN_SUCCESS: // 登录成功
                    mWebView.reload();
                    break;
                case BusEvent.MSG_LOGOUT_SUCCESS: // 注销成功
                    mWebView.reload();
                    break;
            }
        });
        if (mBackImageView != null) mBackImageView.setOnClickListener(v -> {
            if (mWebView != null && mWebView.canGoBack()) {
                mWebView.goBack();
            } else {
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mIsWebLoadFinish) {
            String call1 = "javascript:NativeToH5.onWebPageBack()";
            mWebView.loadUrl(call1);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mJavaScriptInterface = null;
        if (mSubscription != null) {
            mSubscription.unsubscribe();
            mSubscription = null;
        }
        if (mLoginQRCodeDialog != null && mLoginQRCodeDialog.isAdded()) {
            mLoginQRCodeDialog.dismiss();
            mLoginQRCodeDialog = null;
        }
    }

    private class VMallJavaScriptInterface {
        /**
         * 清除帐号信息
         */
        @JavascriptInterface
        public void onClearAcount() {
            logUtil.d(TAG, "onClearAcount");
            ManageRepository.getInstance().logout()
                    .compose(RxSchedulerUtil.SchedulersTransformer1())
                    .subscribe(aBoolean -> {
                        if (aBoolean) {
                            ToolUtil.saveObject(MyApplication.getContext(), AppConstants.USER_INFO_FILE, null);
                            mWebView.reload();
                        }
                    });
        }

        /**
         * 打开新 H5 页面
         *
         * @param title 新页面标题
         * @param url   新页面链接
         */
        @JavascriptInterface
        public void onWebPageJump(String title, String url) {
            logUtil.d(TAG, "onWebPageJump");
            runOnUiThread(() -> {
                Intent intent = new Intent(MallWebActivity.this, MallWebActivity.class);
                intent.putExtra("url", url);
                startActivity(intent);
            });
        }

        /**
         * 关闭当前页面，返回上一页面
         */
        @JavascriptInterface
        public void onWebPageReturn() {
            logUtil.d(TAG, "onWebPageReturn");
            runOnUiThread(MallWebActivity.this::finish);
        }

        /**
         * 跳转到登陆页面
         */
        @JavascriptInterface
        public void onLoginPageJump() {
            logUtil.d(TAG, "onLoginPageJump");
            runOnUiThread(() -> {
                if (mLoginQRCodeDialog == null) mLoginQRCodeDialog = new LoginQRCodeDialog();
                if (mLoginQRCodeDialog.isAdded()) return;
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                mLoginQRCodeDialog.show(fragmentTransaction, TAG);
            });
        }

        /**
         * 获取城市名称
         */
        @JavascriptInterface
        public String getCityName() {
            logUtil.d(TAG, "getCityName");
//            return FridgePreference.getInstance().getCity();
            return null;
        }

        /**
         * 获取城市编码
         */
        @JavascriptInterface
        public String getCityCode() {
            logUtil.d(TAG, "getCityCode");
//            return FridgePreference.getInstance().getCityCode();
            return null;
        }

        /**
         * 获取用户信息
         * return 未登录，返回 null;已登陆返回 json 字符串
         */
        @JavascriptInterface
        public String getUserInfo() {
            logUtil.d(TAG, "getUserInfo");
            QRCodeBase qrCodeBase = (QRCodeBase) FileUtil.getObject(MallWebActivity.this, AppConstants.USER_INFO_FILE);
            if (qrCodeBase == null) {
                return null;
            }
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("account", qrCodeBase.getLoginQRCode().getUserInfo().getAccount());
                jsonObject.put("userCode", qrCodeBase.getLoginQRCode().getUserInfo().getUserCode());
                jsonObject.put("token", qrCodeBase.getLoginQRCode().getUserInfo().getToken());
                jsonObject.put("cid", qrCodeBase.getLoginQRCode().getUserInfo().getCid());
                People mPeople = MiotManager.getPeople();
                if (mPeople != null && !TextUtils.isEmpty(mPeople.getUserId())) {
                    jsonObject.put("miid", mPeople.getUserId());
                }
                return jsonObject.toString();
            } catch (JSONException e) {
                logUtil.d(TAG, e.getMessage());
                e.printStackTrace();
                return null;
            }
        }

        // 编辑器关闭时调用，隐藏系统底部导航栏
        @JavascriptInterface
        public void hideBottomNavigation() {
            logUtil.d(TAG, "hideBottomNavigation");
            runOnUiThread(() -> {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
                int uiFlags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN; // hide status bar

                if (Build.VERSION.SDK_INT >= 19) {
                    uiFlags |= 0x00001000;    //SYSTEM_UI_FLAG_IMMERSIVE_STICKY: hide navigation bars - compatibility: building API level is lower thatn 19, use magic number directly for higher API target level
                } else {
                    uiFlags |= View.SYSTEM_UI_FLAG_LOW_PROFILE;
                }
                try {
                    getWindow().getDecorView().setSystemUiVisibility(uiFlags);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }

        // 有数据更新是清除本地缓存，H5 调用
        @JavascriptInterface
        public void clearNativeCache() {
            logUtil.d(TAG, "clearNativeCache");
            runOnUiThread(() -> {
                logUtil.d(TAG, "clearNativeCache");
                mWebView.clearCache(true);
            });
        }

        // H5 加载失败后调用
        @JavascriptInterface
        public void loadFail() {
            logUtil.d(TAG, "loadFail");
            runOnUiThread(() -> {
                logUtil.d(TAG, "loadFail");
                showFail();
            });
        }
    }
}