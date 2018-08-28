package com.mode.fridge.presenter;


import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.mode.fridge.bean.WeatherBean;
import com.mode.fridge.contract.MainContract;
import com.mode.fridge.utils.logUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;
import javax.inject.Inject;

import rx.subscriptions.CompositeSubscription;

/**
 * 首页 Presenter
 * Created by William on 2018/1/25.
 */
public class MainPresenter implements MainContract.Presenter, AMapLocationListener {
    private static final String TAG = MainPresenter.class.getSimpleName();
    private CompositeSubscription mCompositeSubscription;
//    private CookMenuCategory mCookMenuCategory;// 菜谱分类
    private Context mContext;
    private boolean mIsLocated = false;// 是否已定位
    private AMapLocationClient mLocationClient;// 声明 LocationClient 对象
    private int mCount = 0;// 重试次数

    @Nullable
    private MainContract.View mView;

    @Inject
    MainPresenter(Context context) {
        this.mContext = context;
    }

    @Override
    public void subscribe(MainContract.View view) {
        logUtil.d(TAG, "subscribe");
        this.mView = view;
        mCompositeSubscription = new CompositeSubscription();
//        loadAlbum();// 读取相册
//        loadFridge();// 读取设备数据
        location();// 定位
//        loadMessageBoard();// 读取留言版
//        loadCookBook();// 读取菜谱
    }

    @Override
    public void unSubscribe() {
        logUtil.e(TAG, "unSubscribe");
        mLocationClient.stopLocation();// 停止定位
        if (mCompositeSubscription != null) {
            mCompositeSubscription.unsubscribe();
            mCompositeSubscription = null;
        }
        mView = null;
    }

//    @Override
//    public void loadAlbum() {
//        Subscription subscription = AlbumRepository.getInstance().getAlbumFromBanner()
//                .compose(RxSchedulerUtil.SchedulersTransformer1())
//                .onTerminateDetach()
//                .subscribe(list -> {
//                    if (mView != null) mView.displayAlbum(list);
//                }, throwable -> logUtil.e(TAG, throwable.getMessage()));
//        mCompositeSubscription.add(subscription);
//    }
//
//    @Override
//    public void loadMessageBoard() {
//        Subscription subscription = MessageBoardRepository.getInstance().getMessageBoardList()
//                .compose(RxSchedulerUtil.SchedulersTransformer1())
//                .onTerminateDetach()
//                .subscribe(list -> {
//                    if (mView != null) mView.refreshMessageBoard(list);
//                }, throwable -> logUtil.e(TAG, throwable.getMessage()));
//        mCompositeSubscription.add(subscription);
//    }
//
//    @Override
//    public void loadCookBook() {
//        Subscription subscription = Observable.interval(0, 5, TimeUnit.MINUTES)
//                .compose(RxSchedulerUtil.SchedulersTransformer2())
//                .onTerminateDetach()
//                .subscribe(aLong -> {
//                    mCount = 0;
//                    if (mCookMenuCategory == null) loadCookbookCategory();
//                    else loadCookbookRecommend();
//                }, throwable -> {
//                    logUtil.e(TAG, throwable.getMessage());
//                    retryLoadCookBook();
//                });
//        mCompositeSubscription.add(subscription);
//    }
//
//    @Override
//    public void loadCookbookCategory() {
//        Subscription subscription = ApiClient.getInstance().getApiService().getCookCategory()
//                .compose(RxSchedulerUtil.SchedulersTransformer2())
//                .onTerminateDetach()
//                .subscribe(cookMenuCategoryMobResult -> {
//                    mCookMenuCategory = cookMenuCategoryMobResult.getResult();
//                    loadCookbookRecommend();
//                }, throwable -> logUtil.e(TAG, throwable.getMessage()));
//        mCompositeSubscription.add(subscription);
//    }
//
//    @Override
//    public void loadCookbookRecommend() {
//        CookMenuCategory cookMenuCategory = mCookMenuCategory.getChilds().get(0);
//        Random random = new Random();
//        int position = random.nextInt(cookMenuCategory.getChilds().size());
//        CookMenuCategory cookMenuCategory2 = cookMenuCategory.getChilds().get(position);
//        Subscription subscription = ApiClient.getInstance().getApiService().getCookSearch(cookMenuCategory2.getCategoryInfo().getCtgId(),
//                null, 1, 1)
//                .onTerminateDetach()
//                .flatMap(cookMenuSearchMobResult -> {
//                    // 获取当前分类菜谱信息
//                    CookMenuSearch cookMenuSearch = cookMenuSearchMobResult.getResult();
//                    if (cookMenuSearch == null) {
//                        return null;
//                    }
//                    // 菜谱 id 规则：分类 id（10 位数）+ 菜谱编号（10 位数，不重复）
//                    // 根据最后一条菜谱id和当前分类的菜谱数量，生成随机的菜谱id
//                    int total = cookMenuSearch.getTotal();
//                    String lastMenuId = cookMenuSearch.getList().get(0).getMenuId();
//                    String ctgId = lastMenuId.substring(0, 10);
//                    String menuNum = lastMenuId.substring(10, 20);
//                    // 获取随机菜谱id
//                    int randomMenuNum = Integer.parseInt(menuNum) - random.nextInt(total);
//                    String randomMenuId = ctgId +
//                            String.format(Locale.getDefault(), "%010d", randomMenuNum);
//                    // 获取菜谱详情
//                    return ApiClient.getInstance().getApiService().getCookDetail(randomMenuId);
//                })
//                .compose(RxSchedulerUtil.SchedulersTransformer1())
//                .onTerminateDetach()
//                .subscribe(cookMenuDetailMobResult -> {
//                    CookMenuDetail cookMenuDetail = cookMenuDetailMobResult.getResult();
//                    if (cookMenuDetail == null ||
//                            cookMenuDetail.getRecipe() == null ||
//                            cookMenuDetail.getRecipe().getImg() == null ||
//                            TextUtils.isEmpty(cookMenuDetail.getRecipe().getImg()) ||
//                            cookMenuDetail.getRecipe().getMethod() == null ||
//                            TextUtils.isEmpty(cookMenuDetail.getRecipe().getMethod())) {
//                        if (mCount <= 5) {
//                            loadCookbookRecommend();// 重新获取
//                            mCount++;
//                        }
//                    } else { // 筛选缺图
//                        Gson gson = new Gson();
//                        List<CookMenuMethod> methodList = gson.fromJson(cookMenuDetail.getRecipe().getMethod(),
//                                new TypeToken<List<CookMenuMethod>>() {
//                                }.getType());
//                        boolean isReturn = false;
//                        for (CookMenuMethod cookMenuMethod : methodList) {
//                            if (cookMenuMethod.getImg() == null || cookMenuMethod.getImg().equals("")
//                                    || cookMenuMethod.getStep() == null || cookMenuMethod.getStep().equals("")) {
//                                isReturn = true;
//                                break;
//                            }
//                        }
//                        if (isReturn) {
//                            if (mCount <= 5) {
//                                loadCookbookRecommend();// 重新获取
//                                mCount++;
//                            }
//                        } else {
//                            if (mView != null) mView.refreshCookbook(cookMenuDetail);
//                        }
//                    }
//                }, throwable -> logUtil.e(TAG, throwable.getMessage()));
//        mCompositeSubscription.add(subscription);
//    }
//
//    @Override
//    public void loadFridge() {
//        Subscription subscription = Observable.interval(0, 3, TimeUnit.SECONDS)
//                .subscribeOn(Schedulers.io())
//                .onTerminateDetach()
//                .map(aLong -> FridgeRepository.getInstance().getComSendData())
//                .observeOn(AndroidSchedulers.mainThread())
//                .onTerminateDetach()
//                .subscribe(params -> {
//                    if (mView != null && params != null) mView.refreshFridgeUi(params);
//                }, throwable -> {
//                    logUtil.e(TAG, throwable.getMessage());
//                    retryLoadFridge();
//                });
//        mCompositeSubscription.add(subscription);
//    }

    @Override
    public void location() {
        mLocationClient = new AMapLocationClient(mContext);
        // 初始化定位参数
        AMapLocationClientOption locationOption = new AMapLocationClientOption();
        // 设置定位监听
        mLocationClient.setLocationListener(this);
        // 设置定位模式为高精度模式，Battery_Saving 为低功耗模式，Device_Sensors 是仅设备模式
        locationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        // 设置定位间隔,单位毫秒,默认为 2000 ms
        locationOption.setInterval(1000 * 60 * 30);// 30 分钟定位一次
        // 设置定位参数
        mLocationClient.setLocationOption(locationOption);
        // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
        // 注意设置合适的定位时间的间隔（最小间隔支持为 1000ms），并且在合适时间调用 stopLocation() 方法来取消定位请求
        // 在定位结束后，在合适的生命周期调用 onDestroy() 方法
        // 在单次定位情况下，定位无论成功与否，都无需调用 stopLocation() 方法移除请求，定位 sdk 内部会移除
        // 启动定位
        mLocationClient.startLocation();
    }

    @Override
    public void loadWeather(String cityName) {

//        Subscription subscription = Observable.create(subscriber -> VoiceManager.getInstance().getWeather(cityName, new AppCallback<String>() {
//            @Override
//            public void onSuccess(String data) {
//                logUtil.d(TAG, data);
//                subscriber.onNext(data);
//                subscriber.onCompleted();
//            }
//
//            @Override
//            public void onFail(int errorCode, String msg) {
//                subscriber.onError(new Exception("loadWeather error, errorCode = " + errorCode + ",error = " + msg));
//            }
//        })).subscribeOn(Schedulers.io())
//                .onTerminateDetach()
//                .map(o -> parseWeather((String) o))
//                .observeOn(AndroidSchedulers.mainThread())
//                .onTerminateDetach()
//                .subscribe(weatherBeanList -> {
//                    if (mView != null) {
//                        mIsLocated = true;
//                        mView.refreshWeather(weatherBeanList);
//                    }
//                }, throwable -> {
//                    mIsLocated = false;
//                    logUtil.e(TAG, throwable.getMessage());
//                });
//        mCompositeSubscription.add(subscription);
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        logUtil.d(TAG, "onLocationChanged");
        String cityName = aMapLocation.getCity();// 城市名称
        String cityCode = aMapLocation.getCityCode();// 城市编码
        if (cityName == null || cityName.equals("")) return;
//        FridgePreference.getInstance().saveCity(cityName);// 缓存城市名称
//        FridgePreference.getInstance().saveCityCode(cityCode);// 缓存城市编码
//        FridgePreference.getInstance().saveLatitude(String.valueOf(aMapLocation.getLatitude()));// 纬度
//        FridgePreference.getInstance().saveLongitude(String.valueOf(aMapLocation.getLongitude()));// 经度
        if (mView != null) mView.refreshCity(cityName);
        loadWeather(cityName);
//        if (!mIsLocated) loadWeather(cityName);
    }

//    /**
//     * 读取冰箱数据异常重试
//     */
//    private void retryLoadFridge() {
//        Subscription subscription = Observable.timer(3, TimeUnit.SECONDS)
//                .compose(RxSchedulerUtil.SchedulersTransformer2())
//                .onTerminateDetach()
//                .subscribe(aLong -> loadFridge(), throwable -> logUtil.e(TAG, throwable.getMessage()));
//        mCompositeSubscription.add(subscription);
//    }
//
//    /**
//     * 读取菜谱数据异常重试
//     */
//    private void retryLoadCookBook() {
//        Subscription subscription = Observable.timer(5, TimeUnit.MINUTES)
//                .compose(RxSchedulerUtil.SchedulersTransformer2())
//                .onTerminateDetach()
//                .subscribe(aLong -> loadCookBook(), throwable -> logUtil.e(TAG, throwable.getMessage()));
//        mCompositeSubscription.add(subscription);
//    }

    // 解析讯飞天气 JSON
    private List<WeatherBean> parseWeather(String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray jsonArray = jsonObject.optJSONObject("data").optJSONArray("result");
            List<WeatherBean> list = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                WeatherBean bean = JSON.parseObject(jsonArray.optJSONObject(i).toString(), new TypeReference<WeatherBean>() {
                });
                list.add(bean);
            }
//            FridgePreference.getInstance().saveOutdoorTemp(String.valueOf(list.get(0).getTemp()));// 缓存室外温度
//            String report = list.get(0).getCity() + "，" + list.get(0).getWeather() + "，" + list.get(0).getTempRange() + "，" +
//                    list.get(0).getWind() + "，空气质量：" + list.get(0).getAirQuality() + "，Pm2.5：" + list.get(0).getPm25();
//            FridgePreference.getInstance().saveWeatherReport(report);// 缓存天气预报
//            FridgePreference.getInstance().saveAirQuality(list.get(0).getAirQuality());// 空气质量
//            FridgePreference.getInstance().savePM25(list.get(0).getPm25());// PM2.5
            return list;
        } catch (JSONException e) {
            e.printStackTrace();
            logUtil.e(TAG, e.toString());
            return null;
        }
    }
}