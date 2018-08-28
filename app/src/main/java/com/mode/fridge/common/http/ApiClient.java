package com.mode.fridge.common.http;


import com.baronzhang.retrofit2.converter.FastJsonConverterFactory;
import com.mode.fridge.AppConstants;
import com.mode.fridge.manager.ManagePreference;
import com.mode.fridge.preference.LoginPreference;
import com.mode.fridge.utils.logUtil;

import org.json.JSONObject;

import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;

/**
 * Retrofit 配置初始化
 * Created by William on 2018/1/4.
 */
public class ApiClient {
    private static final String TAG = ApiClient.class.getSimpleName();
    private static ApiClient mInstance;// 单例
    private final String APP_UPDATE_HOST = "https://app.mi-ae.com.cn/";// App 检查更新
    private final String MI_OPEN_HOST = "https://openapp.io.mi.com/";// 小米 IOT 接口
    private final String MALL_RELEASE_HOST = "https://vmall-auth.mi-ae.net/";// 扫码登录商城正式环境
    private final String MALL_DEBUG_HOST = "https://auth.mi-ae.net/";// 扫码登录商城测试环境
    private static final String STORE_RELEASE_HOST = "https://vmall-grey.mi-ae.net";// 正式服-灰度1（兼职管理员需求）
    private static final String STORE_DEBUG_HOST = "https://vj.viomi.com.cn";// 调试服 0727/by胡师傅
    private static final String MESSAGE_RELEASE_HOST = "https://s.viomi.com.cn"; // 消息中心正式环境
    private static final String MOB_HOST = "https://apicloud.mob.com"; // mob 接口
    private String MALL_HOST = MALL_RELEASE_HOST;// 扫码登录商城
    private ApiService mApiService;// Http 请求 Api
    private ProgressListener mProgressListener;
    private final String DOMAIN = "domain";// 根据 Header key 动态修改 BaseUrl

    public static ApiClient getInstance() {
        if (mInstance == null) {
            synchronized (ApiClient.class) {
                if (mInstance == null) {
                    mInstance = new ApiClient();
                }
            }
        }
        return mInstance;
    }

    public void init() {
        long time_out = 20 * 1000;// 超时时间
        // OkHttp 初始化
        OkHttpClient mOkHttpClient = new OkHttpClient
                .Builder()
                // 连接超时
                .connectTimeout(time_out, TimeUnit.MILLISECONDS)
                // 读取超时
                .readTimeout(time_out, TimeUnit.MILLISECONDS)
                // 写入超时
                .writeTimeout(time_out, TimeUnit.MILLISECONDS)
                // 添加应用拦截器
                .addInterceptor(chain -> {
                    // 获取 Request
                    Request request = chain.request();
                    // 获取 Request 的创建者 Builder
                    Request.Builder builder = request.newBuilder();
                    // 从 Request 中获取 headers，通过给定的键：domain
                    List<String> headerValues = request.headers(DOMAIN);
                    if (headerValues == null || headerValues.size() == 0) // 返回原来
                        return chain.proceed(request);
                    if (headerValues.size() > 1)
                        throw new IllegalArgumentException("Only one domain in the headers");
                    logUtil.d(TAG, request.header(DOMAIN));
                    // 如果有这个 header，先将配置的 header 删除，因此 header 仅用作 app 和 OkHttp 之间使用
                    builder.removeHeader(DOMAIN);
                    // 匹配获得新的 BaseUrl
                    String headerValue = headerValues.get(0);
                    HttpUrl newBaseUrl;
                    // 从 request 中获取原有的 HttpUrl 实例
                    HttpUrl oldHttpUrl = request.url();
                    // 此处动态添加 BaseUrl
                    if ("vmall".equals(headerValue)) { // 商城登录
                        newBaseUrl = HttpUrl.parse(MALL_HOST);
                    } else if ("debug".equals(headerValue)) { // 测试环境
                        newBaseUrl = HttpUrl.parse(STORE_DEBUG_HOST);
                    } else if ("vmall_debug".equals(headerValue)) { // 商城登录测试环境
                        newBaseUrl = HttpUrl.parse(MALL_DEBUG_HOST);
                    } else if ("xiaomi".equals(headerValue)) { // 小米接口
                        newBaseUrl = HttpUrl.parse(MI_OPEN_HOST);
                    } else if ("download".equals(headerValue)) { // App 检查更新
                        newBaseUrl = HttpUrl.parse(APP_UPDATE_HOST);
                    } else if ("store".equals(headerValue)) {
                        newBaseUrl = HttpUrl.parse(STORE_RELEASE_HOST);
                    } else if ("message".equals(headerValue)) {
                        newBaseUrl = HttpUrl.parse(MESSAGE_RELEASE_HOST);
                    } else if ("mob".equals(headerValue)) {
                        newBaseUrl = HttpUrl.parse(MOB_HOST);
                    } else {
                        newBaseUrl = oldHttpUrl;
                    }

                    // 重建新的 HttpUrl，修改需要修改的 url 部分
                    HttpUrl newFullUrl = oldHttpUrl
                            .newBuilder()
                            .scheme(newBaseUrl.scheme())
                            .host(newBaseUrl.host())
                            .port(newBaseUrl.port())
                            .build();
                    // 重建这个 request，通过 builder.url(newFullUrl).build()
                    // 然后返回一个 response 至此结束修改
                    return chain.proceed(builder.url(newFullUrl).build());
                })
                // 添加日志拦截器
                .addInterceptor(new HttpLoggingInterceptor(message -> logUtil.d(TAG, "Request->" + message))
                        .setLevel(HttpLoggingInterceptor.Level.BODY))
                // 添加下载进度拦截器
                .addInterceptor(chain -> {
                    // 拦截
                    Response originalResponse = chain.proceed(chain.request());
                    // 包装响应体并返回
                    return originalResponse.newBuilder()
                            .body(new ProgressResponseBody(originalResponse.body(), mProgressListener))
                            .build();
                })
                .build();

        // Retrofit 初始化
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MALL_RELEASE_HOST)
                .addConverterFactory(FastJsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(mOkHttpClient)
                .build();

        mApiService = retrofit.create(ApiService.class);
    }

    public ApiService getApiService() {
        return mApiService;
    }

    public void setProgressListener(ProgressListener progressListener) {
        mProgressListener = progressListener;
    }

    public String getMiClientId() {
        if (LoginPreference.getInstance().getPhoneType().equals("android")) {
            return String.valueOf(AppConstants.OAUTH_ANDROID_APP_ID);
        } else {
            return String.valueOf(AppConstants.OAUTH_IOS_APP_ID);
        }
    }

    public RequestBody getRequestBody(JSONObject jsonObject) {
        return RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());
    }

    public void changeServer() {
        if (ManagePreference.getInstance().getDebug()) {
            MALL_HOST = MALL_DEBUG_HOST;
        } else {
            MALL_HOST = MALL_RELEASE_HOST;
        }
    }
}