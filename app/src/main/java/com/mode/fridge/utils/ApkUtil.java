package com.mode.fridge.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.mode.fridge.MyApplication;


/**
 * Created by young2 on 2016/12/19.
 */

public class ApkUtil {
    private static Context mContext = MyApplication.getContext();

    /***
     * 获取application里的meta_data的value信息
     * @param context
     * @param dataName
     * @return
     */
    public static String getApplicationMetaDataValue(Context context, String dataName) {
        ApplicationInfo appInfo = null;
        String msg = null;
        try {
            appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(),
                    PackageManager.GET_META_DATA);
            msg = appInfo.metaData.getString(dataName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return msg;
    }

    /***
     * 获取activity里的meta_data的value信息
     * @param context
     * @param componentName
     * @param dataName
     * @return
     */
    public static String getActivityMetaDataValue(Context context, ComponentName componentName, String dataName) {
        ActivityInfo info = null;
        String msg = null;
        try {
            info = context.getPackageManager().getActivityInfo(componentName,
                    PackageManager.GET_META_DATA);
            msg = info.metaData.getString(dataName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return msg;
    }

    /**
     * 获取版本号
     *
     * @return 当前应用的版本号
     */
    public static String getVersion() {
        try {
            PackageManager manager = mContext.getPackageManager();
            PackageInfo info = manager.getPackageInfo(mContext.getPackageName(), 0);
            String version = info.versionName;
            return version;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 获取包名
     *
     * @return 当前应用的包名
     */
    public static String getPackageName() {
        return mContext.getPackageName();
    }

    /***
     * 获取version code
     * @return
     */
    public static int getVersionCode() {
//        return 0;
        try {
            PackageManager manager = mContext.getPackageManager();
            PackageInfo info = manager.getPackageInfo(mContext.getPackageName(), 0);
            int code = info.versionCode;
            return code;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /***
     * 根据包名和aitivity名启动第三方app
     * @param context
     * @param packageName
     * @param activityName
     * @param isActivity 是否avtivity启动
     */
    public static void startOtherAppClass(Context context, String packageName, String activityName, boolean isActivity) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setClassName(packageName, activityName);
        if (!isActivity) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }

    /***
     * 根据包名启动第三方app
     * @param context
     * @param packageName
     * @param isActivity 是否avtivity启动
     */
    public static boolean startOtherApp(Context context, String packageName, boolean isActivity) {
        try {
            Intent intent = context.getPackageManager().getLaunchIntentForPackage(packageName);
            if (!isActivity) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
            context.startActivity(intent);
            return true;
        } catch (Exception e) {
            Log.e("startOtherApp", "packageName start fail!");
            e.printStackTrace();
            return false;
        }

    }

    /***
     * 判断是否存在第三方app
     * @param context
     * @param packageName
     * @return
     */
    public static boolean checkApkExist(Context context, String packageName) {
        if (packageName == null || "".equals(packageName))
            return false;
        try {
            ApplicationInfo info = context.getPackageManager()
                    .getApplicationInfo(packageName, PackageManager.GET_UNINSTALLED_PACKAGES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    //版本名
    public static String getVersionName(Context context) {
        return getPackageInfo(context).versionName;
    }

    //版本号
    public static int getVersionCode(Context context) {
        return getPackageInfo(context).versionCode;
    }

    //包名
    public static String getPackageName(Context context) {
        return getPackageInfo(context).packageName;
    }

    private static PackageInfo getPackageInfo(Context context) {
        PackageInfo pi = null;
        try {
            PackageManager pm = context.getPackageManager();
            pi = pm.getPackageInfo(context.getPackageName(),
                    PackageManager.GET_CONFIGURATIONS);
            return pi;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pi;
    }

    public static String getManufacturer(){
        return  android.os.Build.MANUFACTURER;
    }

    /**
     * 获取当前手机系统版本号
     *
     * @return 系统版本号
     */
    public static String getSystemVersion() {
        return android.os.Build.VERSION.RELEASE;
    }

//    public static String getJson(Context context, String key, String value) {
//        JSONObject object = new JSONObject();
//        try {
//            String appVersion = getVersion();
//            String systemVersion = getSystemVersion();
//            long time = System.currentTimeMillis();
//            String screen = PhoneUtil.getScreen(context);
//            String ip = PhoneUtil.getIpAddr();
//            String mac = PhoneUtil.getPhoneMac();
//            String mode = PhoneUtil.getPhoneModel();
//            String did = PhoneUtil.getImei();
//            String name = context.getString(R.string.app_name);
//            String ssid = PhoneUtil.getSSid();
//
//            long requestId = PreferenceUtils.getLong(context, AppConfig.REQUEST_ID, 0);
//            requestId++;
//            PreferenceUtils.putLong(context, AppConfig.REQUEST_ID, requestId);
//            object.put("requestId", requestId);
//            object.put("appVersion", appVersion);
//            object.put("systemVersion", systemVersion);
//            object.put("time", time);
//            object.put("ip", ip);
//            object.put("mac", mac);
//            object.put("screen", screen);
//            object.put("ssid", ssid);
//
//            JSONObject obj = new JSONObject();
//            obj.put("name", name);
//            obj.put("did", did);
//            obj.put("model", mode);
//            obj.put("key", key);
//            obj.put("value", value);
//            object.put("data", obj);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        return object.toString();
//    }
}
