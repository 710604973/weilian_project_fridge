package com.mode.fridge;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Process;
import android.text.TextUtils;
import android.util.Log;

import com.facebook.cache.disk.DiskCacheConfig;
import com.facebook.common.internal.Supplier;
import com.facebook.common.util.ByteConstants;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.cache.MemoryCacheParams;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.imagepipeline.decoder.SimpleProgressiveJpegConfig;
import com.miot.api.MiotManager;
import com.miot.common.ReturnCode;
import com.miot.common.config.AppConfiguration;
import com.miot.common.model.DeviceModel;
import com.miot.common.model.DeviceModelException;
import com.miot.common.model.DeviceModelFactory;
import com.mode.fridge.common.GlobalParams;
import com.mode.fridge.common.component.DaggerApplicationComponent;
import com.mode.fridge.common.http.ApiClient;
import com.mode.fridge.device.AppConfig;
import com.mode.fridge.device.MiDeviceManager;
import com.mode.fridge.device.WaterPurifierBase;
import com.mode.fridge.repository.ManageRepository;
import com.mode.fridge.repository.MiotRepository;
import com.mode.fridge.utils.RxSchedulerUtil;
import com.mode.fridge.utils.ToolUtil;
import com.mode.fridge.utils.logUtil;

import java.io.File;

import dagger.android.AndroidInjector;
import dagger.android.support.DaggerApplication;

public class MyApplication extends DaggerApplication {
    private static final String TAG = MyApplication.class.getSimpleName();
    private static Context mContext;
    private static MyApplication application;

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
        mContext = getApplicationContext();
        if (isMainProcess()) {
            Log.d(TAG, "MiotManager init");
            MiotManager.getInstance().initialize(this);
            new MiotOpenTask().execute();
        }
        Fresco.initialize(this);
        initDevice();// 设备初始化
        initFresco();// Fresco 初始化
        ApiClient.getInstance().init();// Retrofit 初始化
    }

    public static Context getContext() {
        return mContext;
    }

    public static MyApplication getInstance() {
        return application;
    }


    /**
     * Fresco 初始化
     */
    private void initFresco() {
        final MemoryCacheParams memoryCacheParams = new MemoryCacheParams(
                30 * ByteConstants.MB,// 内存缓存中总图片的最大大小,以字节为单位
                Integer.MAX_VALUE,// 内存缓存中图片的最大数量
                30 * ByteConstants.MB,// 内存缓存中准备清除但尚未被删除的总图片的最大大小,以字节为单位
                Integer.MAX_VALUE,// 内存缓存中准备清除的总图片的最大数量
                Integer.MAX_VALUE);// 内存缓存中单个图片的最大大小
        Supplier<MemoryCacheParams> memoryCacheParamsSupplier = () -> memoryCacheParams;

        DiskCacheConfig diskCacheConfig = DiskCacheConfig.newBuilder(this)
                .setBaseDirectoryPath(new File(Environment.getExternalStorageDirectory(), AppConstants.PATH))
                .setBaseDirectoryName(AppConstants.CACHE_SAVE_PATH)
                .setMaxCacheSize(50 * ByteConstants.MB)
                .setMaxCacheSizeOnLowDiskSpace(30 * ByteConstants.MB)
                .setMaxCacheSizeOnVeryLowDiskSpace(10 * ByteConstants.MB)
                .build();
        // 日志
//        Set<RequestListener> requestListeners = new HashSet<>();
//        requestListeners.add(new RequestLoggingListener());
        ImagePipelineConfig config = ImagePipelineConfig.newBuilder(this)
                // 三级缓存中已解码图片的内存缓存配置
                .setBitmapMemoryCacheParamsSupplier(memoryCacheParamsSupplier)
                // 自定义缓存键值对
                //.setCacheKeyFactory(cacheKeyFactory)
                // 是否支持自动缩放（缩放必须要设置ResizeOptions）
                .setDownsampleEnabled(true)
                // 对网络图进行 Resize 处理，减少内存开销
                .setResizeAndRotateEnabledForNetwork(true)
                // 是否支持 WebP 图片
                //.setWebpSupportEnabled(true)
                // 三级缓存中编码图片的内存缓存
                .setEncodedMemoryCacheParamsSupplier(memoryCacheParamsSupplier)
                // 各种线程池
                //.setExecutorSupplier(executorSupplier)
                // 缓存的统计数据追踪器。它是一个接口，提供了各个缓存中图片 Hit 与 Miss 的回调方法，通常可以使用它来统计缓存命中率
                //.setImageCacheStatsTracker(imageCacheStatsTracker)
                // 三级缓存中硬盘缓存的配置，默认缓存目录在 app 自身 CacheDir 的 image_cache 目录下
                .setMainDiskCacheConfig(diskCacheConfig)
                // 注册一个内存调节器，它将根据不同的 MemoryTrimType 回收类型在需要降低内存使用时候进行回收一些内存缓存资源(Bitmap和Encode)。数值越大，表示要回收的资源越多
                //.setMemoryTrimmableRegistry(memoryTrimmableRegistry)
                // 网络图片下载请求类
                //.setNetworkFetchProducer(networkFetchProducer)
                //.setPoolFactory(poolFactory)
                // 渐进式显示网络的 JPEG 图的配置，不过要使用渐进式显示图片，需要在 ImageRequest 中显示的设置是否支持渐进式显示：setProgressiveRenderingEnabled(true)
                .setProgressiveJpegConfig(new SimpleProgressiveJpegConfig())
                //.setRequestListeners(requestListeners)
                // 小图的硬盘缓存配置，默认是和主硬盘缓存目录是共用的。如果需要把小图和普通图片分开，则需重新配置
                .setSmallImageDiskCacheConfig(diskCacheConfig)
                // 打印日志
                // .setRequestListeners(requestListeners)
                // 对透明度无要求
                .setBitmapsConfig(Bitmap.Config.RGB_565)
                .build();
        Fresco.initialize(this, config);
//        FLog.setMinimumLoggingLevel(FLog.VERBOSE);// 日志级别
    }

    /**
     * 初始化 MiIot Device
     */
    private void initDevice() {
        if (isMainProcess()) {
//            Object object = ToolUtil.getFileObject(mContext, AppConstants.USER_INFO_FILE);
//            if (object == null)
//                return;
            ManageRepository.getInstance().getUser(this)
                    .compose(RxSchedulerUtil.SchedulersTransformer1())
                    .onTerminateDetach()
                    .subscribe(qrCodeBase -> MiotRepository.getInstance().initDevice(this, qrCodeBase),
                            throwable -> logUtil.e(TAG, throwable.getMessage()));
        }
    }
//    private void initImageLoader() {
//        DisplayImageOptions mOptions = new DisplayImageOptions.Builder()
//                // 设置图片在下载期间显示的图片
//                .showImageOnLoading(android.R.color.white)
//                // 设置图片Uri为空或是错误的时候显示的图片
//                .showImageForEmptyUri(android.R.color.white)
//                // 设置图片加载/解码过程中错误时候显示的图片
//                .showImageOnFail(android.R.color.white)
//                // 设置下载的图片是否缓存在内存中
//                .cacheInMemory(true)
//                // 设置下载的图片是否缓存在SD卡中
//                .cacheOnDisc(true)
//                // 是否考虑JPEG图像EXIF参数（旋转，翻转）
//                .considerExifParams(true)
//                // 设置图片以如何的编码方式显示
//                .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
//                // 设置图片的解码类型
//                .bitmapConfig(Bitmap.Config.RGB_565)
//                // 设置图片的解码配置
//                // .decodingOptions(options)
//                // .delayBeforeLoading(int delayInMillis)//int
//                // delayInMillis为你设置的下载前的延迟时间
//                // 设置图片加入缓存前，对bitmap进行设置
//                // .preProcessor(BitmapProcessor preProcessor)
//                // 设置图片在下载前是否重置，复位
//                .resetViewBeforeLoading(true)
//                // 是否设置为圆角，弧度为多少
////                .displayer(new RoundedBitmapDisplayer(PhoneUtil.dipToPx(context, 12)))
//                // 是否图片加载好后渐入的动画时间
////                .displayer(new FadeInBitmapDisplayer(100))
//                // 构建完成
//                .build();
//        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(mContext)
//                .defaultDisplayImageOptions(DisplayImageOptions.createSimple())
//                .diskCacheSize(50 * 1024 * 1024)
//                .diskCacheFileCount(100)
//                .defaultDisplayImageOptions(mOptions)//设置默认的显示图片选择
//                .denyCacheImageMultipleSizesInMemory()
//                .memoryCacheExtraOptions(400, 400)
//                .memoryCache(new WeakMemoryCache())
//                .threadPriority(Thread.NORM_PRIORITY - 2) // 线程数量,默认5个
//                .tasksProcessingOrder(QueueProcessingType.LIFO)//设置任务订单处理(队列处理类型)
//                // default
//                .denyCacheImageMultipleSizesInMemory()//否认在内存中缓存图像多种尺寸
//                .memoryCache(new LruMemoryCache(2 * 1024 * 1024))
//                .memoryCacheSize(2 * 1024 * 1024)
//                .memoryCacheSizePercentage(13)
//                //.diskCache(new UnlimitedDiscCache(StorageUtils.getCacheDirectory(this, true)))
//                .build();
//        ImageLoader.getInstance().init(config);
//    }


    private class MiotOpenTask extends AsyncTask<Void, Void, Integer> {
        @Override
        protected Integer doInBackground(Void... params) {
            AppConfiguration appConfig = new AppConfiguration();
            if (GlobalParams.getInstance().getScanPhoneType() == 0) {
                appConfig.setAppId(AppConfig.OAUTH_ANDROID_APP_ID);
                appConfig.setAppKey(AppConfig.OAUTH_ANDROID_APP_KEY);
            } else {
                appConfig.setAppId(AppConfig.OAUTH_IOS_APP_ID);
                appConfig.setAppKey(AppConfig.OAUTH_IOS_APP_KEY);
            }

            MiotManager.getInstance().setAppConfig(appConfig);

            try {
                //冰箱
                DeviceModel water17 = DeviceModelFactory.createDeviceModel(MyApplication.getContext(), AppConstants.VIOMI_FRIDGE_V1,
                        AppConstants.VIOMI_FRIDGE_V1_URL, WaterPurifierBase.class);
                MiotManager.getInstance().addModel(water17);

                DeviceModel fridgeV2Model = DeviceModelFactory.createDeviceModel(MyApplication.getContext(), AppConstants.VIOMI_FRIDGE_V2,
                        AppConstants.VIOMI_FRIDGE_V1_URL, WaterPurifierBase.class);
                MiotManager.getInstance().addModel(fridgeV2Model);

                DeviceModel fridgeV3Model = DeviceModelFactory.createDeviceModel(MyApplication.getContext(), AppConstants.VIOMI_FRIDGE_V3,
                        AppConstants.VIOMI_FRIDGE_V1_URL, WaterPurifierBase.class);
                MiotManager.getInstance().addModel(fridgeV3Model);

                DeviceModel fridgeV31Model = DeviceModelFactory.createDeviceModel(MyApplication.getContext(), AppConstants.VIOMI_FRIDGE_V31,
                        AppConstants.VIOMI_FRIDGE_V1_URL, WaterPurifierBase.class);
                MiotManager.getInstance().addModel(fridgeV31Model);

                DeviceModel fridgeV4Model = DeviceModelFactory.createDeviceModel(MyApplication.getContext(), AppConstants.VIOMI_FRIDGE_V4,
                        AppConstants.VIOMI_FRIDGE_V1_URL, WaterPurifierBase.class);
                MiotManager.getInstance().addModel(fridgeV4Model);

                DeviceModel fridgeU1Model = DeviceModelFactory.createDeviceModel(MyApplication.getContext(), AppConstants.VIOMI_FRIDGE_U1,
                        AppConstants.VIOMI_FRIDGE_V1_URL, WaterPurifierBase.class);
                MiotManager.getInstance().addModel(fridgeU1Model);

                DeviceModel fridgeU2Model = DeviceModelFactory.createDeviceModel(MyApplication.getContext(), AppConstants.VIOMI_FRIDGE_U2,
                        AppConstants.VIOMI_FRIDGE_U2_URL, WaterPurifierBase.class);
                MiotManager.getInstance().addModel(fridgeU2Model);

                DeviceModel fridgeU3Model = DeviceModelFactory.createDeviceModel(MyApplication.getContext(), AppConstants.VIOMI_FRIDGE_U3,
                        AppConstants.VIOMI_FRIDGE_U3_URL, WaterPurifierBase.class);
                MiotManager.getInstance().addModel(fridgeU3Model);

                DeviceModel fridgeX1Model = DeviceModelFactory.createDeviceModel(MyApplication.getContext(), AppConstants.VIOMI_FRIDGE_X1,
                        AppConstants.VIOMI_FRIDGE_V1_URL, WaterPurifierBase.class);
                MiotManager.getInstance().addModel(fridgeX1Model);

                DeviceModel fridgeX2Model = DeviceModelFactory.createDeviceModel(MyApplication.getContext(), AppConstants.VIOMI_FRIDGE_X2,
                        AppConstants.VIOMI_FRIDGE_V1_URL, WaterPurifierBase.class);
                MiotManager.getInstance().addModel(fridgeX2Model);

                DeviceModel fridgeX3Model = DeviceModelFactory.createDeviceModel(MyApplication.getContext(), AppConstants.VIOMI_FRIDGE_X3,
                        AppConstants.VIOMI_FRIDGE_V1_URL, WaterPurifierBase.class);
                MiotManager.getInstance().addModel(fridgeX3Model);

                DeviceModel fridgeX4Model = DeviceModelFactory.createDeviceModel(MyApplication.getContext(), AppConstants.VIOMI_FRIDGE_X4,
                        AppConstants.VIOMI_FRIDGE_V1_URL, WaterPurifierBase.class);
                MiotManager.getInstance().addModel(fridgeX4Model);

                DeviceModel fridgeX41Model = DeviceModelFactory.createDeviceModel(MyApplication.getContext(), AppConstants.VIOMI_FRIDGE_X41,
                        AppConstants.VIOMI_FRIDGE_V1_URL, WaterPurifierBase.class);
                MiotManager.getInstance().addModel(fridgeX41Model);

                DeviceModel fridgeX5Model = DeviceModelFactory.createDeviceModel(MyApplication.getContext(), AppConstants.VIOMI_FRIDGE_X5,
                        AppConstants.VIOMI_FRIDGE_V1_URL, WaterPurifierBase.class);
                MiotManager.getInstance().addModel(fridgeX5Model);

                // 洗衣机
                DeviceModel WASHERU1Model = DeviceModelFactory.createDeviceModel(MyApplication.getContext(), AppConstants.VIOMI_WASHER_U1,
                        AppConstants.VIOMI_WASHER_U1_URL, WaterPurifierBase.class);
                MiotManager.getInstance().addModel(WASHERU1Model);

                DeviceModel WASHERU2Model = DeviceModelFactory.createDeviceModel(MyApplication.getContext(), AppConstants.VIOMI_WASHER_U2,
                        AppConstants.VIOMI_WASHER_U2_URL, WaterPurifierBase.class);
                MiotManager.getInstance().addModel(WASHERU2Model);

                DeviceModel FANV1Model = DeviceModelFactory.createDeviceModel(MyApplication.getContext(), AppConstants.VIOMI_FAN_V1,
                        AppConstants.VIOMI_FAN_V1_URL, WaterPurifierBase.class);
                MiotManager.getInstance().addModel(FANV1Model);

                DeviceModel WASHEV01Model = DeviceModelFactory.createDeviceModel(MyApplication.getContext(), AppConstants.VIOMI_DISHWASHER_V01,
                        AppConstants.VIOMI_DISH_WASHER_V01_URL, WaterPurifierBase.class);
                MiotManager.getInstance().addModel(WASHEV01Model);

                DeviceModel VACUUMV1Model = DeviceModelFactory.createDeviceModel(MyApplication.getContext(), AppConstants.VIOMI_VACUUM_V1,
                        AppConstants.VIOMI_VACUUM_V1_URL, WaterPurifierBase.class);
                MiotManager.getInstance().addModel(VACUUMV1Model);

                // 净水器
                DeviceModel water1 = DeviceModelFactory.createDeviceModel(MyApplication.getContext(), AppConstants.YUNMI_WATERPURIFIER_V1,
                        "device/ddd_WaterPurifier.xml", WaterPurifierBase.class);
                MiotManager.getInstance().addModel(water1);
                DeviceModel water2 = DeviceModelFactory.createDeviceModel(MyApplication.getContext(), AppConstants.YUNMI_WATERPURIFIER_V2,
                        "device/ddd_WaterPurifier.xml", WaterPurifierBase.class);
                MiotManager.getInstance().addModel(water2);
                DeviceModel water3 = DeviceModelFactory.createDeviceModel(MyApplication.getContext(), AppConstants.YUNMI_WATERPURIFIER_V3,
                        "device/ddd_WaterPurifier.xml", WaterPurifierBase.class);
                MiotManager.getInstance().addModel(water3);
                DeviceModel water4 = DeviceModelFactory.createDeviceModel(MyApplication.getContext(), AppConstants.YUNMI_WATERPURI_LX2,
                        "device/ddd_WaterPurifier.xml", WaterPurifierBase.class);
                MiotManager.getInstance().addModel(water4);
                DeviceModel water5 = DeviceModelFactory.createDeviceModel(MyApplication.getContext(), AppConstants.YUNMI_WATERPURI_LX3,
                        "device/ddd_WaterPurifier.xml", WaterPurifierBase.class);
                MiotManager.getInstance().addModel(water5);
                DeviceModel water6 = DeviceModelFactory.createDeviceModel(MyApplication.getContext(), AppConstants.YUNMI_WATERPURI_V1,
                        "device/ddd_WaterPuri_V1.xml", WaterPurifierBase.class);
                MiotManager.getInstance().addModel(water6);
                DeviceModel water7 = DeviceModelFactory.createDeviceModel(MyApplication.getContext(), AppConstants.YUNMI_WATERPURI_V2,
                        "device/ddd_WaterPuri_V2.xml", WaterPurifierBase.class);
                MiotManager.getInstance().addModel(water7);
                DeviceModel water8 = DeviceModelFactory.createDeviceModel(MyApplication.getContext(), AppConstants.YUNMI_WATERPURI_S1,
                        "device/ddd_WaterPuri_S1.xml", WaterPurifierBase.class);
                MiotManager.getInstance().addModel(water8);
                DeviceModel water9 = DeviceModelFactory.createDeviceModel(MyApplication.getContext(), AppConstants.YUNMI_WATERPURI_C1,
                        "device/ddd_WaterPuri_C1.xml", WaterPurifierBase.class);
                MiotManager.getInstance().addModel(water9);
                DeviceModel water10 = DeviceModelFactory.createDeviceModel(MyApplication.getContext(), AppConstants.YUNMI_WATERPURI_C2,
                        "device/ddd_WaterPuri_C2.xml", WaterPurifierBase.class);
                MiotManager.getInstance().addModel(water10);
                DeviceModel water11 = DeviceModelFactory.createDeviceModel(MyApplication.getContext(), AppConstants.YUNMI_WATERPURI_S2,
                        "device/ddd_WaterPuri_S2.xml", WaterPurifierBase.class);
                MiotManager.getInstance().addModel(water11);
                DeviceModel water12 = DeviceModelFactory.createDeviceModel(MyApplication.getContext(), AppConstants.YUNMI_WATERPURI_X3,
                        "device/ddd_WaterPuri_X3.xml", WaterPurifierBase.class);
                MiotManager.getInstance().addModel(water12);
                DeviceModel water13 = DeviceModelFactory.createDeviceModel(MyApplication.getContext(), AppConstants.YUNMI_WATERPURI_X5,
                        "device/ddd_WaterPuri_X5.xml", WaterPurifierBase.class);
                MiotManager.getInstance().addModel(water13);

                // 烟机
                DeviceModel hoodA6Model = DeviceModelFactory.createDeviceModel(MyApplication.getContext(), AppConstants.VIOMI_HOOD_A6,
                        "device/Viomi_Hood_A6.xml", WaterPurifierBase.class);
                MiotManager.getInstance().addModel(hoodA6Model);
                DeviceModel hoodA7Model = DeviceModelFactory.createDeviceModel(MyApplication.getContext(), AppConstants.VIOMI_HOOD_A7,
                        "device/Viomi_Hood_A7.xml", WaterPurifierBase.class);
                MiotManager.getInstance().addModel(hoodA7Model);
                DeviceModel hoodA4Model = DeviceModelFactory.createDeviceModel(MyApplication.getContext(), AppConstants.VIOMI_HOOD_A4,
                        "device/Viomi_Hood_A4.xml", WaterPurifierBase.class);
                MiotManager.getInstance().addModel(hoodA4Model);
                DeviceModel hoodA5Model = DeviceModelFactory.createDeviceModel(MyApplication.getContext(), AppConstants.VIOMI_HOOD_A5,
                        "device/Viomi_Hood_A5.xml", WaterPurifierBase.class);
                MiotManager.getInstance().addModel(hoodA5Model);
                DeviceModel hoodC1Model = DeviceModelFactory.createDeviceModel(MyApplication.getContext(), AppConstants.VIOMI_HOOD_C1,
                        "device/Viomi_Hood_C1.xml", WaterPurifierBase.class);
                MiotManager.getInstance().addModel(hoodC1Model);
                DeviceModel hoodH1Model = DeviceModelFactory.createDeviceModel(MyApplication.getContext(), AppConstants.VIOMI_HOOD_H1,
                        "device/Viomi_Hood_H1.xml", WaterPurifierBase.class);
                MiotManager.getInstance().addModel(hoodH1Model);
                DeviceModel hoodH2Model = DeviceModelFactory.createDeviceModel(MyApplication.getContext(), AppConstants.VIOMI_HOOD_H2,
                        "device/Viomi_Hood_H2.xml", WaterPurifierBase.class);
                MiotManager.getInstance().addModel(hoodH2Model);

                // 水壶
                DeviceModel R1Model = DeviceModelFactory.createDeviceModel(MyApplication.getContext(), AppConstants.YUNMI_KETTLE_R1,
                        "device/Viomi_Kettle_R1.xml", WaterPurifierBase.class);
                MiotManager.getInstance().addModel(R1Model);

                // 管线机
                DeviceModel mg2Model = DeviceModelFactory.createDeviceModel(MyApplication.getContext(), AppConstants.YUNMI_PLMACHINE_MG2,
                        "device/Viomi_Plmachine_Mg2.xml", WaterPurifierBase.class);
                MiotManager.getInstance().addModel(mg2Model);
            } catch (DeviceModelException e) {
                e.printStackTrace();
            }
            return MiotManager.getInstance().open();
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);

            do {
                int result = integer;
                Log.d(TAG, "MiotOpen result: " + result);
                Intent intent = new Intent(AppConfig.ACTION_BIND_SERVICE_FAILED);
                if (result == ReturnCode.OK) {
                    intent = new Intent(AppConfig.ACTION_BIND_SERVICE_SUCCEED);
                    MiDeviceManager.getInstance().getWanDeviceList();
                }
//                mBindBroadcastManager.sendBroadcast(intent);
            }
            while (false);
        }

    }

    private boolean isMainProcess() {
        String mainProcessName = getPackageName();
        String processName = getProcessName();
        return TextUtils.equals(processName, mainProcessName);
    }

    private String getProcessName() {
        int pid = Process.myPid();
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo processInfo : activityManager.getRunningAppProcesses()) {
            if (processInfo.pid == pid) {
                return processInfo.processName;
            }
        }
        return null;
    }

    private class MiotCloseTask extends AsyncTask<Void, Void, Integer> {
        @Override
        protected Integer doInBackground(Void... params) {
            return MiotManager.getInstance().close();
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);

            do {
                int result = integer;
                Log.d(TAG, "MiotClose result: " + result);
            }
            while (false);
        }
    }

    @Override
    protected AndroidInjector<? extends DaggerApplication> applicationInjector() {
        return DaggerApplicationComponent.builder().application(this).build();
    }
}
