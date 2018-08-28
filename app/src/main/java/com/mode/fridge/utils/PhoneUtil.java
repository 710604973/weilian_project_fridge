package com.mode.fridge.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Point;
import android.net.DhcpInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.WindowManager;

import com.mode.fridge.MyApplication;
import com.mode.fridge.bean.MiIndentify;
import com.mode.fridge.device.AppConfig;
import com.mode.fridge.device.DeviceConfig;

/**
 * Created by young2 on 2016/12/15.
 */

public class PhoneUtil {
    public static Context mContext = MyApplication.getContext();

    //转换dip为px
    public static int dipToPx(Context context, float dip) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dip * scale + 0.5f * (dip >= 0 ? 1 : -1));
    }

    //转换px为dip
    public static float pxTodip(Context context, int px) {
        float scale = context.getResources().getDisplayMetrics().density;
        return px / scale + 0.5f * (px >= 0 ? 1 : -1);
    }

    public static int spToPx(Context context, float sp) {
        float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (sp * fontScale + 0.5f);
    }

    public static int pxToSp(Context context, float pxValue) {
        float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    /***
     * imei
     * @return
     */
    @SuppressLint("MissingPermission")
    public static String getImei() {
        TelephonyManager tm = (TelephonyManager) mContext.getSystemService(mContext.TELEPHONY_SERVICE);
        String imei = "";
        try {
            imei = tm.getDeviceId();
        } catch (SecurityException e) {
            Log.e("getImei", "fail,msg=" + e.getMessage());
        }
        return imei;
    }

    /***
     * 获取小米标识
     * @return
     */
    public static MiIndentify getMiIdentify() {
        MiIndentify miIndentify = new MiIndentify();
        miIndentify.mac = DeviceConfig.DefaultMac;
        miIndentify.did = DeviceConfig.DefaultDeviceId;
        miIndentify.token = DeviceConfig.DefaultMiotToken;
        String sn = SystemPropertiesProxy.get(MyApplication.getContext(), "gsm.serial");

        if (sn == null) {
            Log.e("getMiIdentify", "null");
            return miIndentify;
        } else {
            Log.d("getMiIdentify", sn + ",length=" + sn.length());
        }
        String[] list;
        if (sn.length() >= 24 && (!sn.contains("|"))) {
            list = new String[2];
            list[0] = sn.substring(0, 8);
            list[1] = sn.substring(8, 24);
        } else {
            list = sn.split("\\|");
            if (list == null || list.length < 2 || list[1].length() < 16) {
                Log.e("getMiIdentify", "error,sn=" + sn);
                return miIndentify;
            }
        }

        miIndentify.mac = getPhoneMac();
        miIndentify.did = list[0];
        miIndentify.token = list[1].substring(0, 16);
        return miIndentify;
    }

    public static String getIpAddr() {
        try {
            WifiManager wifiMng = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
            DhcpInfo di = wifiMng.getDhcpInfo();
            return IpUtils.intToIp(di.ipAddress);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    /***
     * 手机型号
     * @return
     */
    public static String getPhoneModel() {
        return Build.MODEL;
    }

    /***
     * 系统版本号
     * @return
     */
    public static String getSystemVersion() {
        return Build.VERSION.RELEASE;
    }

    /***
     * 获取手机mac
     * @return
     */
    public static String getPhoneMac() {
        String mac = "";
        try {
            WifiManager wifi = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
            @SuppressLint("MissingPermission") WifiInfo info = wifi.getConnectionInfo();
            mac = info.getMacAddress();
        } catch (Exception e) {
            Log.e("getLocalMacAddress", "fail,msg=" + e.getMessage());
        }
        return mac;
    }

    /**
     * 获取屏幕高度(px)
     */
    public static int getScreenHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    /**
     * 获取屏幕宽度(px)
     */
    public static int getScreenWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    public static String getScreen(Context context) {
        String str = getScreenWidth(context) + "*" + getScreenHeight(context);
        return str;
    }

    /***
     * 获取手机分辨率
     * @return
     */
    public static String getScreenResolution() {
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        Point size = new Point();
        wm.getDefaultDisplay().getSize(size);
        return (size.x + "*" + size.y);
    }

    /***
     * 获取系统版本
     * @return
     */
    public static int getSystemVersionCode() {

        String display = Build.DISPLAY;
        if (display == null) {
            return 0;
        }
        int index = display.lastIndexOf("_T");
        if (index == 0) {
            return 0;
        }
        String ver = display.substring(index + 2);
        float floatVersion = 0;
        try {
            floatVersion = Float.parseFloat(ver);
        } catch (NumberFormatException e) {
            return 0;
        }
        return (int) floatVersion;

    }


    /***
     * 获取系统显示版本
     * @return
     */
    public static String getSystemVersionCodeStr() {

        return Build.DISPLAY;
    }

    /***
     * 获取小米标识
     * @return
     */
//    public static MiIndentify getMiIdentify() {
//        MiIndentify miIndentify = new MiIndentify();
//        miIndentify.mac = DeviceConfig.DefaultMac;
//        miIndentify.did = DeviceConfig.DefaultDeviceId;
//        miIndentify.token = DeviceConfig.DefaultMiotToken;
//        String sn = SystemPropertiesProxy.get(ViomiApplication.getContext(), "gsm.serial");
//
//        if (sn == null) {
//            Log.e("getMiIdentify", "null");
//            return miIndentify;
//        } else {
//            Log.d("getMiIdentify", sn + ",length=" + sn.length());
//        }
//        String[] list;
//        if (sn.length() >= 24 && (!sn.contains("|"))) {
//            list = new String[2];
//            list[0] = sn.substring(0, 8);
//            list[1] = sn.substring(8, 24);
//        } else {
//            list = sn.split("\\|");
//            if (list == null || list.length < 2 || list[1].length() < 16) {
//                Log.e("getMiIdentify", "error,sn=" + sn);
//                return miIndentify;
//            }
//        }
//
//        miIndentify.mac = getPhoneMac();
//        miIndentify.did = list[0];
//        miIndentify.token = list[1].substring(0, 16);
//        return miIndentify;
//    }

    /***
     * 获取冰箱型号
     * @return
     */
    public static String getDeviceModel() {
        return AppConfig.VIOMI_FRIDGE_V3;
//        String defaultModel = AppConfig.VIOMI_FRIDGE_V1;
//        String type = SystemPropertiesProxy.get(ViomiApplication.getContext(), "ro.viomi.type");
//        Log.i("getPhoneModel", "type=" + type);
//        if (type == null) {
//            Log.e("getPhoneModel", "null");
//            return defaultModel;
//        }
//        switch (type) {
//            case "viomi_fridge_ulx001"://三门大屏
//                return AppConfig.VIOMI_FRIDGE_V1;
//
//            case "viomi_fridge_ulx002"://四门大屏
//                return AppConfig.VIOMI_FRIDGE_V2;
//
//            case "viomi_fridge_ulx003"://462或者455
//            {
//                String sn = SystemPropertiesProxy.get(ViomiApplication.getContext(), "gsm.serial");
//                if (sn == null) {
//                    Log.e("getDeviceModel", "sn null");
//                    return defaultModel;
//                } else {
//                    log.d("getDeviceModel", sn + ",length=" + sn.length());
//                }
//                if (sn.length() <= 24 || (!sn.contains("|"))) {
//                    Log.e("getDeviceModel", "sn error");
//                    return defaultModel;
//                }
//                String[] list = sn.split("\\|");
//                if (list == null || list.length != 3) {
//                    Log.e("getDeviceModel", "sn spit error");
//                    return defaultModel;
//                }
//                String typeStr = list[2];
//                switch (typeStr) {
//                    case "v03"://462
//                        return AppConfig.VIOMI_FRIDGE_V3;
//
//                    case "v04"://455
//                        return AppConfig.VIOMI_FRIDGE_V4;
//                }
//            }
//            return defaultModel;
//
//            case "viomi.fridge.jd1"://京东定制款462
//                return AppConfig.VIOMI_FRIDGE_V31;
//
//            default:
//                return defaultModel;
//        }
    }

//    public static String getIpAddr() {
//        try {
//            WifiManager wifiMng = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
//            DhcpInfo di = wifiMng.getDhcpInfo();
//            return IpUtils.intToIp(di.ipAddress);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
//
//    }
//
//    public static String getSSid() {
//        WifiManager wifiMgr = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
//        int wifiState = wifiMgr.getWifiState();
//        WifiInfo info = wifiMgr.getConnectionInfo();
//        return info != null ? info.getSSID() : null;
//    }
}
