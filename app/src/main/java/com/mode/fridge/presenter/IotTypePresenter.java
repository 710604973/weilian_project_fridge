package com.mode.fridge.presenter;

import android.content.Context;

import com.miot.common.abstractdevice.AbstractDevice;
import com.mode.fridge.AppConstants;
import com.mode.fridge.R;
import com.mode.fridge.bean.DeviceTypeEntity;
import com.mode.fridge.common.http.MiDeviceApi;
import com.mode.fridge.contract.IotTypeContract;
import com.mode.fridge.device.DeviceConfig;
import com.mode.fridge.repository.FanRepository;
import com.mode.fridge.repository.FridgeRepository;
import com.mode.fridge.repository.HeatKettleRepository;
import com.mode.fridge.repository.ManageRepository;
import com.mode.fridge.repository.RangeHoodRepository;
import com.mode.fridge.repository.WashRepository;
import com.mode.fridge.repository.WaterPurifierRepository;
import com.mode.fridge.utils.RxSchedulerUtil;
import com.mode.fridge.utils.logUtil;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nullable;
import javax.inject.Inject;

import rx.Observable;
import rx.Subscription;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * 互联网家设备分类 Presenter
 * Created by William on 2018/3/3.
 */
public class IotTypePresenter implements IotTypeContract.Presenter {
    private static final String TAG = IotTypePresenter.class.getSimpleName();
    private CompositeSubscription mCompositeSubscription;
    private CompositeSubscription mHttpCompositeSubscription;
    private Context mContext;

    @Nullable
    private IotTypeContract.View mView;

    @Inject
    IotTypePresenter(Context context) {
        mContext = context;
    }

    @Override
    public void subscribe(IotTypeContract.View view) {
        this.mView = view;
        mCompositeSubscription = new CompositeSubscription();
        mHttpCompositeSubscription = new CompositeSubscription();
        loadUserInfo();
    }

    @Override
    public void unSubscribe() {
        this.mView = null;
        if (mCompositeSubscription != null) {
            mCompositeSubscription.unsubscribe();
            mCompositeSubscription = null;
        }
        if (mHttpCompositeSubscription != null) {
            mHttpCompositeSubscription.unsubscribe();
            mHttpCompositeSubscription = null;
        }
    }

    @Override
    public void initDeviceList(List<DeviceTypeEntity> list) {
        String[] types = mContext.getResources().getStringArray(R.array.device_type_list);
//        String model = FridgePreference.getInstance().getModel();
        String model = DeviceConfig.MODEL;
        for (int i = 0; i < types.length; i++) {
            DeviceTypeEntity entity = new DeviceTypeEntity();
            entity.setType(types[i]);
            if (!model.equals(AppConstants.MODEL_JD) && i != 1 && i != 5 && i != 6 && i != 9 &&
                    i != 10 && i != 11 && i != 12 && i != 13 && i != 14) { // 是否已上架
                entity.setOnSale(true);
            }
            list.add(entity);
        }
//        // 冰箱
//        DeviceParams params = SerialManager.getInstance().getDeviceParamsSet();
//        Log.i("info", "===================params:" + params);
//        String data = "";
//        data = data + (params.isCold_switch() ? params.getCold_temp_set() : "--") + ",";
//        if (model.equals(AppConstants.MODEL_X5)) {
//            data = data + (params.isChangeable_switch() ? params.getChangeable_temp_set() : "--") + ",";
//        }
//        if (model.equals(AppConstants.MODEL_X3) || model.equals(AppConstants.MODEL_X5)) {
//            data = data + (params.getFreezing_temp_set() < -24 ? -24 : params.getFreezing_temp_set());
//        } else data = data + params.getFreezing_temp_set();
//        list.get(0).setData(data);
        if (mView != null) mView.refreshView();
        loadDeviceList(list);
    }

    @Override
    public void loadDeviceList(List<DeviceTypeEntity> list) {
        Subscription subscription = Observable.interval(0, 10, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .onTerminateDetach()
                .flatMap(aLong -> {
                    if (mHttpCompositeSubscription != null) mHttpCompositeSubscription.clear();
                    return MiDeviceApi.getDeviceList(mContext);
                })// 获取 Miot 设备列表
                .subscribeOn(Schedulers.computation())
                .onTerminateDetach()
                .subscribe(list1 -> {
                    classifyMiDevice(list1, list);// 对设备进行分类
                }, throwable -> {
                    String msg = throwable.getMessage();
                    logUtil.e(TAG, "==msg:" + throwable.getMessage());
//                    logUtil.e(TAG, throwable.getMessage());
                });
        mCompositeSubscription.add(subscription);
    }

    @Override
    public void loadUserInfo() {
        Subscription subscription = ManageRepository.getInstance().getUser(mContext)
                .compose(RxSchedulerUtil.SchedulersTransformer1())
                .onTerminateDetach()
                .subscribe(qrCodeBase -> {
                    if (mView != null) mView.showUserInfo(qrCodeBase);
                }, throwable -> {
                    logUtil.e(TAG, throwable.getMessage());
                    if (mView != null) mView.showUserInfo(null);
                });
        mCompositeSubscription.add(subscription);
    }

    /**
     * 根据 Model 进行分类
     *
     * @param miList: Miot 设备集合
     * @param list:   分类集合
     */
    private void classifyMiDevice(List<AbstractDevice> miList, List<DeviceTypeEntity> list) {
        for (DeviceTypeEntity entity : list) {
            entity.getList().clear();// 先清除
            entity.setAsking(false);
            entity.setData(null);
            entity.setExist(false);
        }
        for (AbstractDevice device : miList) {
            switch (device.getDeviceModel()) {
                // 冰箱
                case AppConstants.VIOMI_FRIDGE_V1:
                case AppConstants.VIOMI_FRIDGE_V2:
                case AppConstants.VIOMI_FRIDGE_V3:
                case AppConstants.VIOMI_FRIDGE_V31:
                case AppConstants.VIOMI_FRIDGE_V4:
                case AppConstants.VIOMI_FRIDGE_U1:
                case AppConstants.VIOMI_FRIDGE_U2:
                case AppConstants.VIOMI_FRIDGE_U3:
                case AppConstants.VIOMI_FRIDGE_W1:
                case AppConstants.VIOMI_FRIDGE_X1:
                case AppConstants.VIOMI_FRIDGE_X2:
                case AppConstants.VIOMI_FRIDGE_X3:
                case AppConstants.VIOMI_FRIDGE_X4:
                case AppConstants.VIOMI_FRIDGE_X41:
                case AppConstants.VIOMI_FRIDGE_X5:
                    list.get(0).getList().add(device);// 分类添加
                    if (device.isOnline() && !list.get(0).isExist())
                        list.get(0).setExist(true);// 有在线设备
                    if (device.isOnline() && list.get(0).getData() == null && !list.get(0).isAsking())// 该分类无请求数据
                        loadFridge(device, list);// 获取在冰箱数据
                    break;
                // 烟机
                case AppConstants.VIOMI_HOOD_A4:
                case AppConstants.VIOMI_HOOD_A5:
                case AppConstants.VIOMI_HOOD_A6:
                case AppConstants.VIOMI_HOOD_A7:
                case AppConstants.VIOMI_HOOD_C1:
                case AppConstants.VIOMI_HOOD_H1:
                case AppConstants.VIOMI_HOOD_H2:
                    list.get(2).getList().add(device);// 分类添加
                    if (device.isOnline() && !list.get(2).isExist())
                        list.get(2).setExist(true);// 有在线设备
                    if (device.isOnline() && list.get(2).getData() == null && !list.get(2).isAsking())// 该分类无请求数据
                        loadRangeHood(device, list);// 获取在线烟机数据
                    break;
                // 净水器
                case AppConstants.YUNMI_WATERPURI_V1:
                case AppConstants.YUNMI_WATERPURI_V2:
                case AppConstants.YUNMI_WATERPURI_S1:
                case AppConstants.YUNMI_WATERPURI_S2:
                case AppConstants.YUNMI_WATERPURI_C1:
                case AppConstants.YUNMI_WATERPURI_C2:
                case AppConstants.YUNMI_WATERPURI_X3:
                case AppConstants.YUNMI_WATERPURI_X5:
                case AppConstants.YUNMI_WATERPURIFIER_V1:
                case AppConstants.YUNMI_WATERPURIFIER_V2:
                case AppConstants.YUNMI_WATERPURIFIER_V3:
                case AppConstants.YUNMI_WATERPURI_LX2:
                case AppConstants.YUNMI_WATERPURI_LX3:
                    list.get(3).getList().add(device);// 分类添加
                    if (device.isOnline() && !list.get(3).isExist())
                        list.get(3).setExist(true);// 有在线设备
                    if (device.isOnline() && list.get(3).getData() == null && !list.get(3).isAsking())
                        loadWaterPurifier(device, list);// 获取在线净水器数据
                    break;
                // 即热饮水吧
                case AppConstants.YUNMI_KETTLE_R1:
                    list.get(4).getList().add(device);// 分类添加
                    if (device.isOnline() && !list.get(4).isExist())
                        list.get(4).setExist(true);// 有在线设备
                    if (device.isOnline() && list.get(4).getData() == null && !list.get(4).isAsking())
                        loadHeatKettle(device, list);// 获取在线即热饮水吧数据
                    break;
                // 洗碗机
                case AppConstants.VIOMI_DISHWASHER_V01:
                    list.get(7).getList().add(device);// 分类添加
                    if (device.isOnline() && !list.get(7).isExist())
                        list.get(7).setExist(true);// 有在线设备
                    // TODO 获取数据
                    break;
                // 洗衣机
                case AppConstants.VIOMI_WASHER_U1:
                case AppConstants.VIOMI_WASHER_U2:
                    list.get(8).getList().add(device);// 分类添加
                    if (device.isOnline() && !list.get(8).isExist())
                        list.get(8).setExist(true);// 有在线设备
                    String data = list.get(8).getData();
                    if (device.isOnline() && data == null && !list.get(8).isAsking())
                        loadWasher(device, list);
                    // TODO 获取数据
                    break;
                // 风扇
                case AppConstants.VIOMI_FAN_V1:
                    list.get(11).getList().add(device);// 分类添加
                    if (device.isOnline() && !list.get(11).isExist())
                        list.get(11).setExist(true);// 有在线设备
                    String Data = list.get(11).getData();
                    if (device.isOnline() && Data == null && !list.get(11).isAsking())
                        loadFan(device, list);
                    // TODO 获取数据
                    break;
                // 扫地机器人
                case AppConstants.VIOMI_VACUUM_V1:
                    list.get(14).getList().add(device);// 分类添加
                    if (device.isOnline() && !list.get(14).isExist())
                        list.get(14).setExist(true);// 有在线设备
                    // TODO 获取数据
                    break;
            }
        }
        refrshView(list);
    }

    private void loadFridge(AbstractDevice device, List<DeviceTypeEntity> list) {
        // 冰箱
        String model = device.getDeviceModel();
        list.get(0).setAsking(true);
        Subscription subscription = FridgeRepository.getProp(device.getDeviceId())
                .compose(RxSchedulerUtil.SchedulersTransformer1())
                .onTerminateDetach()
                .subscribe(rpcResult -> {
                            if (rpcResult.getCode() != 0 || rpcResult.getList().size() == 0) return;
                            String data = "";
                            String RCSet = (String) rpcResult.getList().get(0);
                            String CCSet = (String) rpcResult.getList().get(2);
                            int RCSetTemp = (int) rpcResult.getList().get(1);
                            int CCSetTemp = (int) rpcResult.getList().get(3);
                            int FCSetTemp = (int) rpcResult.getList().get(4);
                            if (RCSet.equals("on")) {
                                data = data + RCSetTemp + ",";
                            } else {
                                data = data + "--" + ",";
                            }
                            if (model.equals(AppConstants.MODEL_X5)) {
                                if (CCSet.equals("on")) {
                                    data = data + CCSetTemp + ",";
                                } else {
                                    data = data + "--" + ",";
                                }
                            }
                            if (model.equals(AppConstants.MODEL_X3) || model.equals(AppConstants.MODEL_X5)) {
                                if (FCSetTemp < -24) {
                                    data = data + "-24";
                                } else {
                                    data = data + FCSetTemp;
                                }
                            } else {
                                data = data + FCSetTemp;
                            }
                            list.get(0).setData(data);
                            if (mView != null) mView.notifyItemView(0, data);
//                            refrshView(list);
                        },
                        throwable -> logUtil.e(TAG, throwable.getMessage()));
        mHttpCompositeSubscription.add(subscription);
    }

    private void refrshView(List<DeviceTypeEntity> list) {
        Subscription subscription = Observable.just(list)
                .compose(RxSchedulerUtil.SchedulersTransformer1())
                .onTerminateDetach()
                .subscribe(list1 -> {
                    for (int i = 0; i < list1.size(); i++) {
                        if (!list1.get(i).isAsking() && mView != null) mView.notifyItemView(i, "");
                    }
                    if (mView != null) mView.refreshView();
                }, throwable -> logUtil.e(TAG, throwable.getMessage()));
        mCompositeSubscription.add(subscription);
    }

    // 获取烟机工作状态
    private void loadRangeHood(AbstractDevice device, List<DeviceTypeEntity> list) {
        list.get(2).setAsking(true);
        Subscription subscription = RangeHoodRepository.getProp(device.getDeviceId())
                .compose(RxSchedulerUtil.SchedulersTransformer1())
                .onTerminateDetach()
                .subscribe(rpcResult -> {
                    if (rpcResult.getCode() != 0 || rpcResult.getList().size() == 0) return;
                    if (((int) rpcResult.getList().get(1)) == 0) list.get(2).setData("已关机");
                    else {
                        if (((int) rpcResult.getList().get(2)) == 0)
                            list.get(2).setData("空闲中");
                        else if (((int) rpcResult.getList().get(2)) == 1)
                            list.get(2).setData("低档排烟");
                        else if (((int) rpcResult.getList().get(2)) == 4)
                            list.get(2).setData("爆炒排烟");
                        else if (((int) rpcResult.getList().get(2)) == 16)
                            list.get(2).setData("高档排烟");
                    }
                    if (mView != null) mView.notifyItemView(2, "");
                }, throwable -> logUtil.e(TAG, throwable.getMessage()));
        mHttpCompositeSubscription.add(subscription);
    }

    // 获取净水器 TDS 值
    private void loadWaterPurifier(AbstractDevice device, List<DeviceTypeEntity> list) {
        list.get(3).setAsking(true);
        Subscription subscription = WaterPurifierRepository.miGetProp(device.getDeviceId())
                .compose(RxSchedulerUtil.SchedulersTransformer1())
                .onTerminateDetach()
                .subscribe(rpcResult -> {
                            if (rpcResult.getCode() != 0 || rpcResult.getList().size() == 0) return;
                            list.get(3).setData("TDS：" + String.valueOf(rpcResult.getList().get(1)));
                            if (mView != null) mView.notifyItemView(3, "");
                        },
                        throwable -> logUtil.e(TAG, throwable.getMessage()));
        mHttpCompositeSubscription.add(subscription);
    }

    // 获取洗衣机
    private void loadWasher(AbstractDevice device, List<DeviceTypeEntity> list) {
        list.get(8).setAsking(true);
        Subscription subscription = WashRepository.getProp(device.getDeviceId())
                .compose(RxSchedulerUtil.SchedulersTransformer1())
                .onTerminateDetach()
                .subscribe(rpcResult -> {
                            if (rpcResult.getCode() != 0 || rpcResult.getList().size() == 0) return;
                            String str = String.valueOf(rpcResult.getList().get(1));
                            list.get(8).setData(str);
                            if (mView != null) mView.notifyItemView(8, "");
                        },
                        throwable -> logUtil.e(TAG, throwable.getMessage()));
        mHttpCompositeSubscription.add(subscription);
    }

    // 获取风扇
    private void loadFan(AbstractDevice device, List<DeviceTypeEntity> list) {
        list.get(11).setAsking(true);
        Subscription subscription = FanRepository.getProp(device.getDeviceId())
                .compose(RxSchedulerUtil.SchedulersTransformer1())
                .onTerminateDetach()
                .subscribe(rpcResult -> {
                            if (rpcResult.getCode() != 0 || rpcResult.getList().size() == 0) return;
                            String str = String.valueOf(rpcResult.getList().get(1));
                            list.get(11).setData(str);
                            if (mView != null) mView.notifyItemView(11, "");
                        },
                        throwable -> logUtil.e(TAG, throwable.getMessage()));
        mHttpCompositeSubscription.add(subscription);
    }

    // 获取即热饮水吧出水温度
    private void loadHeatKettle(AbstractDevice device, List<DeviceTypeEntity> list) {
        list.get(4).setAsking(true);
        Subscription subscription = HeatKettleRepository.getProp(device.getDeviceId())
                .compose(RxSchedulerUtil.SchedulersTransformer1())
                .onTerminateDetach()
                .subscribe(rpcResult -> {
                            if (rpcResult.getCode() != 0 || rpcResult.getList().size() == 0) return;
                            list.get(4).setData(String.valueOf(rpcResult.getList().get(0)) + "℃");
                            if (mView != null) mView.notifyItemView(4, "");
                        },
                        throwable -> logUtil.e(TAG, throwable.getMessage()));
        mHttpCompositeSubscription.add(subscription);
    }
}