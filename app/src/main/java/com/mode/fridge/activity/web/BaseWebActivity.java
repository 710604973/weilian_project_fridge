package com.mode.fridge.activity.web;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.WindowManager;
import android.webkit.GeolocationPermissions;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.amap.api.location.AMapLocationClient;
import com.mode.fridge.R;
import com.mode.fridge.common.base.BaseActivity;
import com.mode.fridge.utils.RxSchedulerUtil;
import com.mode.fridge.utils.logUtil;
import com.tencent.sonic.sdk.SonicSession;
import com.viomi.common.module.sonicwebview.SonicJavaScriptInterface;
import com.viomi.common.module.sonicwebview.SonicSessionClientImpl;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscription;

/**
 * 全局浏览器 Activity
 */
@SuppressLint({"SetJavaScriptEnabled", "Registered"})
public class BaseWebActivity extends BaseActivity {
    private static final String TAG = BaseWebActivity.class.getSimpleName();

    @BindView(R.id.web_browser)
    protected WebView mWebView;// 浏览器

    @BindView(R.id.web_title_bar)
    protected View mTitleBarView;// 标题栏

    @Nullable
    @BindView(R.id.web_progress)
    protected ProgressBar mProgressBar;// 网页进度条

    @BindView(R.id.web_fail_layout)
    protected RelativeLayout mFailRelativeLayout;// 网络错误布局
    protected boolean mIsWebLoadFinish;
    private SonicSession mSonicSession;
    private SonicSessionClientImpl mSonicSessionClient = null;
    private AMapLocationClient mLocationClientSingle;// 高德定位
    private Subscription mSubscription;// 网页加载超时计时
    private boolean mIsError;// 是否网络错误

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE |
                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mWebView.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mWebView != null) {
            mWebView.stopLoading();
            // 退出时调用此方法，移除绑定的服务，否则某些特定系统会报错
            mWebView.getSettings().setJavaScriptEnabled(false);
            ViewParent parent = mWebView.getParent();
            if (parent != null) {
                ((ViewGroup) parent).removeView(mWebView);
            }
            mWebView.removeAllViews();
            mWebView.destroy();
            mWebView = null;
        }
        stopTimer();
        // 停止定位
        if (mLocationClientSingle != null) {
            mLocationClientSingle.stopAssistantLocation();
            mLocationClientSingle.onDestroy();
        }
        if (mSonicSession != null) {
            mSonicSession.destroy();
            mSonicSession = null;
        }
    }

    private void init() {
        startAssistLocation();
        // add java script interface
        // note:if api level lower than 17(android 4.2), addJavascriptInterface has security
        // issue, please use x5 or see https://developer.android.com/reference/android/webkit/
        // WebView.html#addJavascriptInterface(java.lang.Object, java.lang.String)
        Intent intent = new Intent();
        mWebView.removeJavascriptInterface("searchBoxJavaBridge_");
        intent.putExtra(SonicJavaScriptInterface.PARAM_LOAD_URL_TIME, System.currentTimeMillis());
        mWebView.addJavascriptInterface(new SonicJavaScriptInterface(mSonicSessionClient, intent), "sonic");

        // 初始化 WebView Settings
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setAllowFileAccess(true);// 允许访问文件
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);// 设置 WebView 底层布局算法
        webSettings.setSupportZoom(false);// 禁止手势缩放
        webSettings.setBuiltInZoomControls(false);// 设置 WebView 是否使用其内置的变焦机制
        webSettings.setUseWideViewPort(false);// 当该属性被设置为 false 时，加载页面的宽度总是适应 WebView 控件宽度
        webSettings.setSupportMultipleWindows(false);// 是否支持多屏窗口
        webSettings.setLoadWithOverviewMode(true);// 是否使用预览模式加载界面
        webSettings.setAppCacheEnabled(true);// 设置 Application 缓存 API 是否开启
        webSettings.setDatabaseEnabled(true);// 启用数据库
        webSettings.setDomStorageEnabled(true);// 设置是否开启 DOM 存储 API 权限
        webSettings.setJavaScriptEnabled(true);// 是否允许执行 JavaScript 脚本
        webSettings.setGeolocationEnabled(true);// 是否开启定位功能
        webSettings.setAppCacheMaxSize(Long.MAX_VALUE);// 缓存最大值
        webSettings.setAppCachePath(this.getDir("viomi", 0).getPath());// 缓存路径
        webSettings.setDatabasePath(this.getDir("viomi", 0).getPath());// 缓存路径
        webSettings.setGeolocationDatabasePath(this.getDir("viomi", 0).getPath());// 地理位置缓存路径       webSettings.setAllowContentAccess(true);
        webSettings.setLoadsImagesAutomatically(true);// 是否自动加载图片资源
        webSettings.setPluginState(WebSettings.PluginState.ON_DEMAND);
        webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        webSettings.setSavePassword(false);
        webSettings.setSaveFormData(false);
        webSettings.setAllowUniversalAccessFromFileURLs(true);// 设置 WebView 运行中的脚本可以是否访问任何原始起点内容
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                logUtil.d(TAG, "onReceivedTitle : " + title);
                if (mTitleTextView != null) mTitleTextView.setText(title);
            }

            @Override
            public void onReceivedIcon(WebView view, Bitmap icon) {
                super.onReceivedIcon(view, icon);
                logUtil.d(TAG, "onReceivedIcon");
            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if (mProgressBar != null) mProgressBar.setProgress(newProgress);
                logUtil.d(TAG, "progress:" + newProgress);
            }

            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                // 处理 javascript 中的 alert
                logUtil.d(TAG, "onJsAlert");
                return super.onJsAlert(view, url, message, result);
            }

            @Override
            public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
                // 处理 javascript 中的 confirm
                logUtil.d(TAG, "onJsConfirm");
                return super.onJsConfirm(view, url, message, result);
            }

            @Override
            public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
                // 处理定位权限请求
                callback.invoke(origin, true, false);
                logUtil.d(TAG, "onGeolocationPermissionsShowPrompt");
                super.onGeolocationPermissionsShowPrompt(origin, callback);
            }
        });

        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                logUtil.d(TAG, "shouldOverrideUrlLoading");
                return false;
            }

            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                logUtil.d(TAG, "shouldInterceptRequest");
                return super.shouldInterceptRequest(view, request);
            }

            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
                if (mSonicSession != null) {
                    return (WebResourceResponse) mSonicSession.getSessionClient().requestResource(url);
                }
                return null;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                logUtil.d(TAG, "onPageStarted = " + url);
                if (mProgressBar != null) mProgressBar.setVisibility(View.VISIBLE);
                stopTimer();
                mSubscription = Observable.timer(30, TimeUnit.SECONDS)
                        .compose(RxSchedulerUtil.SchedulersTransformer1())
                        .onTerminateDetach()
                        .subscribe(aLong -> showFail(), throwable -> logUtil.e(TAG, throwable.getMessage()));
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                logUtil.d(TAG, "onPageFinished = " + url);
                if (mProgressBar != null) mProgressBar.setVisibility(View.GONE);
                stopTimer();
                if (!mIsError) {
                    mWebView.setVisibility(View.VISIBLE);
                }
                mIsError = false;
                if (mSonicSession != null) {
                    mSonicSession.getSessionClient().pageFinish(url);
                }
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                logUtil.e(TAG, "onReceivedError");
                stopTimer();
                mIsError = true;
                mFailRelativeLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
//                super.onReceivedSslError(view, handler, error);
                handler.proceed();// 接受证书
            }
        });
    }

    @OnClick(R.id.web_error_retry)
    public void retry() { // 网络错误重试
        mWebView.reload();
        mWebView.setVisibility(View.GONE);
        mFailRelativeLayout.setVisibility(View.GONE);
    }

    /**
     * 停止计时
     */
    private void stopTimer() {
        if (mSubscription != null && !mSubscription.isUnsubscribed()) {
            mSubscription.unsubscribe();
            mSubscription = null;
        }
    }

    /**
     * 显示网络错误布局
     */
    protected void showFail() {
        mFailRelativeLayout.setVisibility(View.VISIBLE);
        mTitleBarView.setVisibility(View.VISIBLE);
    }

    /**
     * 启动 H5 辅助定位
     */
    private void startAssistLocation() {
        if (null == mLocationClientSingle) {
            mLocationClientSingle = new AMapLocationClient(this.getApplicationContext());
        }
        mLocationClientSingle.startAssistantLocation();
    }
}