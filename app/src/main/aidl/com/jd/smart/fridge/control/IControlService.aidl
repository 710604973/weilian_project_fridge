// IControlService.aidl
package com.jd.smart.fridge.control;
import com.jd.smart.fridge.control.NotifyCallBack;
interface IControlService {

    String onQueryDevice(String type);
    String onSetDevice(String cmd, String type);
//    String onSetDeviceCall(String cmd, String type, AppCallBack callback);
     //注册回调接口
    void registerCallBack(NotifyCallBack cb);
    void unregisterCallBack(NotifyCallBack cb);
}
