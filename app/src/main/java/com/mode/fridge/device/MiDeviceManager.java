package com.mode.fridge.device;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.miot.api.CommonHandler;
import com.miot.api.CompletionHandler;
import com.miot.api.DeviceManager;
import com.miot.api.MiotManager;
import com.miot.common.abstractdevice.AbstractDevice;
import com.miot.common.device.ConnectionType;
import com.miot.common.device.Device;
import com.miot.common.device.DeviceDefinition;
import com.miot.common.device.DiscoveryType;
import com.miot.common.exception.MiotException;
import com.miot.common.field.FieldList;
import com.miot.common.share.ShareStatus;
import com.miot.common.share.SharedRequest;
import com.miot.common.share.SharedUser;
import com.miot.common.utils.NetworkUtils;
import com.mode.fridge.MyApplication;
import com.mode.fridge.utils.log;
import com.viomi.common.callback.AppCallback;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;


public class MiDeviceManager {
    private static final String TAG = MiDeviceManager.class.getSimpleName();
    private static MiDeviceManager sInstance;
    private AbstractDevice mCurrentDevice;

    public static synchronized MiDeviceManager getInstance() {
        if (sInstance == null) {
            sInstance = new MiDeviceManager();
        }
        return sInstance;
    }

    private Context mContext;
    private LocalBroadcastManager mBroadcastManager;
    private Map<String, AbstractDevice> mWanDevices = new Hashtable<>();
    private Map<String, AbstractDevice> mWifiDevices = new Hashtable<>();
 //   private SharedRequest mRequest;

    private MiDeviceManager() {
        mContext = MyApplication.getContext();
        mBroadcastManager = LocalBroadcastManager.getInstance(mContext);

    }

    public List<AbstractDevice> getWanDevices() {
        List<AbstractDevice> devices = new ArrayList<>();
        devices.addAll(mWanDevices.values());
        return devices;
    }

    public AbstractDevice getWanDevice(String deviceId) {

        if(deviceId==null||mWanDevices==null){
            Log.e(TAG,"getWanDevice null");
            return null;
        }
//        for (Map.Entry<String, AbstractDevice> abstractDevice:mWanDevices.entrySet()){
//            Log.e(TAG,"KEY="+abstractDevice.getKey());
//        }
        return mWanDevices.get(deviceId);
    }

    public synchronized void removeWanDevice(String deviceId) {
        mWanDevices.remove(deviceId);
    }

    public synchronized void putWanDevice(String deviceId, AbstractDevice device) {
        mWanDevices.put(deviceId, device);
    }

    public List<AbstractDevice> getWifiDevices() {
        List<AbstractDevice> devices = new ArrayList<>();

        for (AbstractDevice device : mWifiDevices.values()) {
            devices.add(device);
        }
        return devices;
    }

    public AbstractDevice getWifiDevice(String address) {
        return mWifiDevices.get(address);
    }

    public void clearWifiDevices() {
        mWifiDevices.clear();
    }

    public void clearDevices() {
        mWanDevices.clear();
        mWifiDevices.clear();
    }

    public void getWanDeviceList() {
        Log.i(TAG,"getWanDeviceList");
        if (!NetworkUtils.isNetworkAvailable(mContext)) {
            Log.e(TAG,"getWanDeviceList fail, Networknot available!");
            return;
        }

        if (MiotManager.getDeviceManager() == null) {
            Log.e(TAG,"getWanDeviceList fail, getDeviceManager null!");
            return;
        }

        try {
            mWanDevices.clear();
            MiotManager.getDeviceManager().getRemoteDeviceList(mWanDeviceHandler);
        } catch (MiotException e) {
            Log.e(TAG,"getRemoteDeviceList fail!msg:"+e.getMessage());
//            try {
//                MiotManager.getPeopleManager().deletePeople();
//            } catch (MiotException e1) {
//                e1.printStackTrace();
//                Log.e(TAG, "deletePeople MiotException,msg=" + e1.getMessage());
//            }
        }
    }

    public void startScan() {
        Log.i(TAG, "startScan");
        if (MiotManager.getDeviceManager() == null) {
            Log.e(TAG, "startScan fail!getDeviceManager null ! ");
            return;
        }
        List<DiscoveryType> types = new ArrayList<>();
        types.add(DiscoveryType.MIOT_WIFI);
        try {
            Log.d(TAG, "startScan");
            MiotManager.getDeviceManager().startScan(types, mWifiDeviceHandler);
        } catch (MiotException e) {
            e.printStackTrace();
            Log.e(TAG, "startScan MiotException,msg=" + e.getMessage());
        }
    }

    public void stopScan() {
        if (MiotManager.getDeviceManager() == null) {
            return;
        }
        try {
            MiotManager.getDeviceManager().stopScan();
        } catch (MiotException e) {
         //   e.printStackTrace();
            Log.e(TAG,"stopScan fail,msg="+e.getMessage());
        }
    }

    private DeviceManager.DeviceHandler mWifiDeviceHandler = new DeviceManager.DeviceHandler() {
        @Override
        public void onSucceed(List<AbstractDevice> devices) {
            if (devices != null) {
                Log.d(TAG, "startScan,device found ,count= " + devices.size());
            }else {
                Log.e(TAG, "startScan,device null" );
                return;
            }

            foundDevices(devices);

            Intent intent = new Intent(AppConfig.ACTION_DISCOVERY_DEVICE_SUCCEED);
            mBroadcastManager.sendBroadcast(intent);
        }

        @Override
        public void onFailed(int errCode, String description) {
            Log.e(TAG, "startScan onFailed: " + errCode + description);//1015
            Intent intent = new Intent(AppConfig.ACTION_DISCOVERY_DEVICE_FAILED);
            intent.putExtra("errorCode",errCode);
            mBroadcastManager.sendBroadcast(intent);
        }
    };

    private DeviceManager.DeviceHandler mWanDeviceHandler = new DeviceManager.DeviceHandler() {
        @Override
        public void onSucceed(List<AbstractDevice> devices) {
            Log.i(TAG,"mWanDeviceHandler,device size:"+devices.size());
            foundDevices(devices);

            Intent intent = new Intent(AppConfig.ACTION_DISCOVERY_DEVICE_SUCCEED);
            mBroadcastManager.sendBroadcast(intent);
        }

        @Override
        public void onFailed(int errCode, String description) {
            Log.e(TAG, "getRemoteDeviceList onFailed: " + errCode + description);
            if(errCode==-13){
                try {
                    MiotManager.getPeopleManager().deletePeople();
                } catch (MiotException e1) {
                    e1.printStackTrace();
                    Log.e(TAG, "deletePeople MiotException,msg=" + e1.getMessage());
                }
            }
            Intent intent = new Intent(AppConfig.ACTION_DISCOVERY_DEVICE_FAILED);
            mBroadcastManager.sendBroadcast(intent);
        }
    };

    private void foundDevices(List<AbstractDevice> devices) {
        for (AbstractDevice abstractDevice : devices) {
            ConnectionType connectionType = abstractDevice.getDevice().getConnectionType();
            log.d(TAG, "found abstractDevice: " + abstractDevice.getName() + " " + abstractDevice.getAddress()
                    + " " + connectionType + " " + abstractDevice.getOwnership());

            switch (connectionType) {
                case MIOT_WAN:
                    // Log.d(TAG, "did="+abstractDevice.getDeviceId()+"ownership=" + abstractDevice.getOwnership());
                    if (abstractDevice.getOwnership() == Device.Ownership.NOONES) {
                        // Log.d(TAG, "@@@@@@@@@@@@@did="+abstractDevice.getDeviceId());
                        //    bindDevice(abstractDevice);
                    }
                    
                    mWanDevices.put(abstractDevice.getDeviceId(), abstractDevice);
                    break;

                case MIOT_WIFI:
                    if (!mWifiDevices.containsKey(abstractDevice.getAddress())) {
                        mWifiDevices.put(abstractDevice.getAddress(), abstractDevice);
                    }
                    break;
            }
        }
    }

    public void bindDevice(final AbstractDevice device) {
        try {
            MiotManager.getDeviceManager().takeOwnership(device, new CompletionHandler() {
                @Override
                public void onSucceed() {
                    String log = "takeOwnership succeed";

                    Intent intent = new Intent(AppConfig.ACTION_TAKE_OWNERSHIP_SUCCEED);
                    intent.putExtra(AppConfig.MI_DEVICE_ID, device.getAddress());
                    mBroadcastManager.sendBroadcast(intent);
                }

                @Override
                public void onFailed(int errCode, String description) {
                    String log = "takeOwnership onFailed " + errCode + description;
                    Log.e(TAG, log);

                    Intent intent = new Intent(AppConfig.ACTION_TAKE_OWNERSHIP_FAILED);
                    intent.putExtra(AppConfig.MI_DEVICE_ID, device.getAddress());
                    mBroadcastManager.sendBroadcast(intent);
                }
            });
        } catch (MiotException e) {
            e.printStackTrace();
        }
    }

    private AppCallback mBindDeviceCallback;
    public void bindDevice(AbstractDevice device, AppCallback callback) {
        mBindDeviceCallback=callback;
        try {
            MiotManager.getDeviceManager().takeOwnership(device, new CompletionHandler() {
                @Override
                public void onSucceed() {
                    String log = "takeOwnership succeed";
                    Log.e(TAG, log);
                    mBindDeviceCallback.onSuccess(null);
                }

                @Override
                public void onFailed(int errCode, String description) {
                    String log = "takeOwnership onFailed " + errCode + description;
                    Log.e(TAG, log);
                    mBindDeviceCallback.onFail(errCode, description);
                }
            });
        } catch (MiotException e) {
            e.printStackTrace();
            mBindDeviceCallback.onFail(-100, e.getMessage());
        }
    }

    /***
     * 解绑设备
     *
     * @param device
     */
    private AppCallback mUnBindDeviceCallback;
    public void unBindDevice(AbstractDevice device, AppCallback callback) {
        mUnBindDeviceCallback=callback;
        if (device == null) {
            callback.onFail(-100, "device null !");
            return;
        }
        try {
            MiotManager.getDeviceManager().disclaimOwnership(device, new CompletionHandler() {
                @Override
                public void onSucceed() {
                    Log.d(TAG, "disclaimOwnership onSucceed");
                    mUnBindDeviceCallback.onSuccess(null);
                }

                @Override
                public void onFailed(int errCode, String description) {
                    Log.e(TAG, "disclaimOwnership onFialed: " + errCode + " " + description);
                    mUnBindDeviceCallback.onFail(errCode, description);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "unBindDevice fail!,msg:" + e.getMessage());
            callback.onFail(-110, "unBindDevice fail!msg;"+e.getMessage());
        }
    }


    public void connectDevice( AbstractDevice device) {
        try {
            MiotManager.getDeviceConnector().connectToCloud(device, new CompletionHandler() {
                @Override
                public void onSucceed() {
                    Log.d(TAG, "connect device onSucceed");
                }

                @Override
                public void onFailed(int errCode, String description) {
                    Log.d(TAG, "connect device onFailed: " + errCode + description);
                }
            });
        } catch (MiotException e) {
            e.printStackTrace();
            Log.e(TAG, "connectDevice error,msg: " + e.getMessage());
        }

    }

    /***
     * 重命名
     * @param device
     * @param newName
     */
    private AppCallback mRenameDeviceCallback;
    public void onRenameDevice(AbstractDevice device, String newName, AppCallback callback) {
        mRenameDeviceCallback=callback;
        if (device == null) {
            mRenameDeviceCallback.onFail(-100, "device null !");
            return;
        }
        FieldList fieldList=new FieldList();
        fieldList.initField(DeviceDefinition.Name,newName);
        try {
            MiotManager.getDeviceManager().modifyDevice(device, fieldList, new CompletionHandler() {
                @Override
                public void onSucceed() {
                    String log = "onRenameDevice succeed";
                    Log.e(TAG, log);
                    mRenameDeviceCallback.onSuccess(null);
                }

                @Override
                public void onFailed(int i, String s) {
                    Log.e(TAG, "onRenameDevice onFialed: " + i + " " +s );
                    mRenameDeviceCallback.onFail(i, s);
                }
            });
        } catch (MiotException e) {
            e.printStackTrace();
            Log.e(TAG, "onRenameDevice fail!,msg:" + e.getMessage());
            mRenameDeviceCallback.onFail(-100, "onRenameDevice fail!msg:"+ e.getMessage());
        }

    }

    /***
     * 共享设备
     * @param device 共享的设备
     * @param userId 被共享的用户id
     * @param callback
     */
    private AppCallback mShareDeviceCallback;
    public void shareDevice(AbstractDevice device, String userId, AppCallback callback) {
        mShareDeviceCallback=callback;
        try {
            MiotManager.getDeviceManager().shareDevice(device, userId, new CompletionHandler() {
                @Override
                public void onSucceed() {
                    Log.d(TAG,"shareDevice: onSucceed");
                    mShareDeviceCallback.onSuccess(null);
                }

                @Override
                public void onFailed(int errCode, String description) {
                    Log.e(TAG,"shareDevice: failed: " + errCode + " - " + description);
                    mShareDeviceCallback.onFail(errCode, description);
                }
            });
        } catch (MiotException e) {
            e.printStackTrace();
            Log.e(TAG, "shareDevice fail!,msg:" + e.getMessage());
            mShareDeviceCallback.onFail(-100, "shareDevice fail!msg:"+ e.getMessage());
        }
    }

    /***
     * 查询分享用户
     * @param device
     * @param callback
     */
    private AppCallback mUsersCallback;
    public void queryShareUsers(AbstractDevice device, AppCallback<List> callback) {
        mUsersCallback=callback;
        try {
            MiotManager.getDeviceManager().querySharedUsers(device, new CommonHandler<List<SharedUser>>() {
                @Override
                public void onSucceed(List<SharedUser> result) {
                    for (SharedUser sharedUser : result) {
                        log.d(TAG, sharedUser.getUserId() + "  " + sharedUser.getUserName() + " " + sharedUser.getStatus());
                  //      log.d(TAG, "@@@,"+sharedUser.getIcon() + "  " + sharedUser.getShareTime() + " " + sharedUser.getStatus());
                    }
                    Log.d(TAG,"queryShareUsers: onSucceed");
                    mUsersCallback.onSuccess(result);
                }

                @Override
                public void onFailed(int errCode, String description) {
                    Log.e(TAG,"queryShareUsers: failed: " + errCode + " - " + description);
                    mUsersCallback.onFail(errCode, description);
                }
            });
        } catch (MiotException e) {
            e.printStackTrace();
            Log.e(TAG, "queryShareUsers fail!,msg:" + e.getMessage());
            mUsersCallback.onFail(-100, "queryShareUsers fail!msg:"+ e.getMessage());
        }
    }

    /***
     * 取消分享
     * @param device
     * @param userId 共享的设备
     * @param callback
     */
    private AppCallback mCancelShareCallback;
    public void cancelShare(AbstractDevice device, String userId, AppCallback callback) {
        try {
            MiotManager.getDeviceManager().cancelShare(device, userId, new CompletionHandler() {
                @Override
                public void onSucceed() {
                    Log.d(TAG,"cancelShare: onSucceed");
                    mCancelShareCallback.onSuccess(null);
                }

                @Override
                public void onFailed(int errCode, String description) {
                    Log.e(TAG,"cancelShare: failed: " + errCode + " - " + description);
                    mCancelShareCallback.onFail(errCode, description);
                }
            });
        } catch (MiotException e) {
            e.printStackTrace();
            Log.e(TAG, "cancelShare fail!,msg:" + e.getMessage());
            mCancelShareCallback.onFail(-100, "cancelShare fail!msg:"+ e.getMessage());
        }
    }

    /***
     * 查询分享邀请
     * @param callback
     */
    private AppCallback mQuaryShareCallback;
    public void querySharedRequests(  AppCallback<List> callback) {
        mQuaryShareCallback=callback;
        try {
            MiotManager.getDeviceManager().querySharedRequests(new CommonHandler<List<SharedRequest>>() {
                @Override
                public void onSucceed(List<SharedRequest> result) {
                    for (SharedRequest request : result) {
                      //  mRequest = request;
                        logSharedRequest(request);
                        mQuaryShareCallback.onSuccess(result);
                    }
                }

                @Override
                public void onFailed(int errCode, String description) {
                    Log.e(TAG, "querySharedRequests: " + errCode + description);
                    mQuaryShareCallback.onFail(errCode, description);
                }
            });
        } catch (MiotException e) {
            e.printStackTrace();
            Log.e(TAG, "querySharedRequests fail!,msg:" + e.getMessage());
            mQuaryShareCallback.onFail(-100, "querySharedRequests fail!msg:"+ e.getMessage());
        }
    }

    /***
     * 接受和拒绝共享
     * @param request 共享邀请
     * @param accept 是否接受
     * @param callback
     */
    private AppCallback mShareCallback;
    public void acceptSharedRequest(SharedRequest request, boolean accept, AppCallback<List> callback) {
        mShareCallback=callback;
        if(accept){
            request.setShareStatus(ShareStatus.accept);
        }else {
            request.setShareStatus(ShareStatus.reject);
        }

        try {
            MiotManager.getDeviceManager().replySharedRequest(request, new CompletionHandler() {
                @Override
                public void onSucceed() {
                    Log.e(TAG, "replySharedRequest onSucceed");
                    mShareCallback.onSuccess(null);
                }

                @Override
                public void onFailed(int errCode, String description) {
                    Log.e(TAG, "replySharedRequest: " + errCode + description);
                    mShareCallback.onFail(errCode, description);
                }
            });
        } catch (MiotException e) {
            e.printStackTrace();
            Log.e(TAG, "acceptSharedRequest fail!,msg:" + e.getMessage());
            mShareCallback.onFail(-100, "acceptSharedRequest fail!msg:"+ e.getMessage());
        }
    }

    private void logSharedRequest(SharedRequest request) {
        StringBuilder sb = new StringBuilder();
        Device device = request.getSharedDevice();
        sb.append("invitedId=").append(request.getInvitedId())
                .append("  messageId=").append(request.getMessageId())
                .append("  status=").append(request.getShareStatus().toString())
                .append("  deviceId=").append(device.getDeviceId())
                .append("  owner=").append(device.getOwnerInfo().getUserId())
                .append("  ").append(device.getOwnerInfo().getUserName());
        log.d(TAG, "shareRequest: " + sb.toString());
    }


    public void setCurrentDevice(AbstractDevice abstractDevice){
        mCurrentDevice=abstractDevice;
    }

    public AbstractDevice getCurrentDevice(){
        return mCurrentDevice;
    }



}
