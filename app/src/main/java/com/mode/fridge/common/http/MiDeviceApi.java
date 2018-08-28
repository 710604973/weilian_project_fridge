package com.mode.fridge.common.http;

import android.content.Context;

import com.miot.api.DeviceManager;
import com.miot.api.MiotManager;
import com.miot.common.abstractdevice.AbstractDevice;
import com.miot.common.device.ConnectionType;
import com.miot.common.exception.MiotException;
import com.miot.common.people.People;
import com.miot.common.utils.NetworkUtils;
import com.mode.fridge.utils.logUtil;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;

/**
 * 获取小米设备 Api
 * Created by William on 2018/2/3.
 */
public class MiDeviceApi {
    private static final String TAG = MiDeviceApi.class.getSimpleName();

    /**
     * 获取设备列表
     */
    public static Observable<List<AbstractDevice>> getDeviceList(Context context) {
        return Observable.create(subscriber -> {
            People people = MiotManager.getPeople();
            if (people != null) {
                if (!NetworkUtils.isNetworkAvailable(context)) {
                    subscriber.onError(new Throwable("getDevice fail, Network is not available!"));
                    return;
                }
                if (MiotManager.getDeviceManager() == null) {
                    subscriber.onError(new Throwable("getWanDeviceList fail, getDeviceManager null!"));
                    return;
                }
                try {
                    MiotManager.getDeviceManager().getRemoteDeviceList(new DeviceManager.DeviceHandler() {
                        @Override
                        public void onSucceed(List<AbstractDevice> list) {
                            subscriber.onNext(filterDevices(list));
                            subscriber.onCompleted();
                        }

                        @Override
                        public void onFailed(int errorCode, String errorMsg) {
                            subscriber.onError(new Throwable("getDeviceList failed, errorCode:" + errorCode + "msg:" + errorMsg));
                        }
                    });
                } catch (MiotException e) {
                    e.printStackTrace();
                    subscriber.onError(new Throwable(e.getMessage()));
                }
            } else {
                subscriber.onError(new Throwable("Mi account is not login"));
            }
        });
    }

    /**
     * 过滤除 WAN 外设备
     */
    private static List<AbstractDevice> filterDevices(List<AbstractDevice> devices) {
        List<AbstractDevice> list = new ArrayList<>();
        for (AbstractDevice abstractDevice : devices) {
            ConnectionType connectionType = abstractDevice.getDevice().getConnectionType();
            logUtil.d(TAG, "found abstractDevice: " + abstractDevice.getName() + " " + abstractDevice.getAddress()
                    + " " + connectionType + " " + abstractDevice.getOwnership());
            switch (connectionType) {
                case MIOT_WAN:
                    list.add(abstractDevice);
                    break;
            }
        }
        return list;
    }
}