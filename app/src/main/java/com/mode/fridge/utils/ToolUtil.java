package com.mode.fridge.utils;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.mode.fridge.AppConstants;
import com.mode.fridge.MyApplication;
import com.mode.fridge.bean.DeviceParamsSet;
import com.mode.fridge.bean.MiIdentification;
import com.mode.fridge.bean.SerialInfo;
import com.mode.fridge.common.FridgeStreamId;
import com.mode.fridge.manager.ControlManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.util.List;

/**
 * 公共工具类
 * Created by William on 2018/1/20.
 */

public class ToolUtil {
    private static final String TAG = ToolUtil.class.getSimpleName();

    /**
     * dp 转 px
     */
    public static int dpToPx(Context context, float dip) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dip * scale + 0.5f * (dip >= 0 ? 1 : -1));
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

    /**
     * 获取小米标识
     */
    public static MiIdentification getMiIdentification() {
        MiIdentification miIdentification = new MiIdentification();
        miIdentification.setMac("7C:49:EB:0F:42:82");
        miIdentification.setDeviceId("85396802");
        miIdentification.setMiToken("vGP581G2tSvzelnY");

        String sn = get(MyApplication.getContext(), "gsm.serial");
        if (sn == null) {
            logUtil.e(TAG, "sn code is null");
            return miIdentification;
        } else {
            logUtil.d(TAG, "sn = " + sn + ",length = " + sn.length());
        }
        String[] list;
        if (sn.length() >= 24 && (!sn.contains("|"))) {
            list = new String[2];
            list[0] = sn.substring(0, 8);
            list[1] = sn.substring(8, 24);
        } else {
            list = sn.split("\\|");
            if (list.length < 2 || list[1].length() < 16) {
                logUtil.e("getMiIdentification", "error,sn=" + sn);
                return miIdentification;
            }
        }
        miIdentification.setMac(getMac());
        miIdentification.setDeviceId(list[0]);
        miIdentification.setMiToken(list[1].substring(0, 16));
        return miIdentification;
    }

    /**
     * 根据 Key 获取系统底层属性
     *
     * @return 如果不存在该 key 则返回空字符串
     * @throws IllegalArgumentException 如果 key 超过 32 个字符则抛出该异常
     */
    private static String get(Context context, String key) throws IllegalArgumentException {
        String ret;
        try {
            ClassLoader classLoader = context.getClassLoader();
            @SuppressLint("PrivateApi") @SuppressWarnings("rawtypes")
            Class SystemProperties = classLoader.loadClass("android.os.SystemProperties");
            // 参数类型
            @SuppressWarnings("rawtypes")
            Class[] paramTypes = new Class[1];
            paramTypes[0] = String.class;

            Method get = SystemProperties.getMethod("get", paramTypes);
            // 参数
            Object[] params = new Object[1];
            params[0] = key;
            ret = (String) get.invoke(SystemProperties, params);
        } catch (IllegalArgumentException iAE) {
            throw iAE;
        } catch (Exception e) {
            ret = "";
        }
        return ret;
    }

    /**
     * 获取 mac 地址
     */
    private static String getMac() {
        String mac = "";
        String str = "";
        try {
            Process pp = Runtime.getRuntime().exec("cat /sys/class/net/wlan0/address ");
            InputStreamReader ir = new InputStreamReader(pp.getInputStream());
            LineNumberReader input = new LineNumberReader(ir);
            for (; null != str; ) {
                str = input.readLine();
                if (str != null) {
                    mac = str.trim();// 去空格
                    break;
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return mac;
    }

    /**
     * 获取冰箱型号
     */
    public static String getDeviceModel() {
        String defaultModel = AppConstants.MODEL_X2;
        String type = SystemPropertiesProxy.get(MyApplication.getContext(), "ro.viomi.type");
        logUtil.i("getPhoneModel", "type=" + type);
        if (type == null) {
            logUtil.e("getPhoneModel", "null");
            return defaultModel;
        }
        switch (type) {
            case "viomi.fridge.x2": // 428 四门
                return AppConstants.MODEL_X2;

            case "viomi.fridge.jd1": // 京东
                return AppConstants.MODEL_JD;

            case "viomi.fridge.x3": // 462，450，521
            {
                String sn = SystemPropertiesProxy.get(MyApplication.getContext(), "gsm.serial");
                if (sn == null) {
                    logUtil.e("getDeviceModel", "sn null");
                    return defaultModel;
                } else {
                    logUtil.d("getDeviceModel", sn + ",length=" + sn.length());
                }
                if (sn.length() <= 24 || (!sn.contains("|"))) {
                    logUtil.e("getDeviceModel", "sn error");
                    return defaultModel;
                }
                String[] list = sn.split("\\|");
                if (list.length != 3) {
                    logUtil.e("getDeviceModel", "sn spit error");
                    return defaultModel;
                }
                String typeStr = list[2];
                switch (typeStr) {
                    case "v01": // 462
                        return AppConstants.MODEL_X3;

                    case "v02": // 450
                        return AppConstants.MODEL_X4;

                    case "v03": // 521
                        return AppConstants.MODEL_X5;
                }
            }
            return defaultModel;

            default:
                return defaultModel;
        }
    }

    /**
     * 判断 Activity 是否正在运行
     *
     * @param name: Activity 名称（如: com.viomi.fridge.vertical.album.activity.AlbumActivity）
     */
    public static boolean isActivityRunning(Context context, String name) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager != null) {
            List<ActivityManager.RunningTaskInfo> tasks = activityManager.getRunningTasks(1);// 获取栈最大数量
            ActivityManager.RunningTaskInfo task = tasks.get(0);// 获取栈顶
            if (task != null) {
                return TextUtils.equals(task.topActivity.getPackageName(), "com.viomi.fridge.vertical")
                        && TextUtils.equals(task.topActivity.getClassName(), name);
            }
        }
        return false;
    }

    /**
     * 判断某个服务是否正在运行
     *
     * @param serviceName 是包名+ 服务的类名
     * @return true 代表正在运行，false 代表服务没有正在运行
     */
    public static boolean isServiceWork(Context mContext, String serviceName) {
        boolean isWork = false;
        ActivityManager myAM = (ActivityManager) mContext
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> myList = myAM.getRunningServices(Integer.MAX_VALUE);
        if (myList.size() <= 0) {
            return false;
        }
        for (int i = 0; i < myList.size(); i++) {
            String mName = myList.get(i).service.getClassName();
            if (mName.equals(serviceName)) {
                isWork = true;
                break;
            }
        }
        return isWork;
    }

    /**
     * 判断网络连接是否可用
     */
    public static Boolean isNetworkConnected(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (manager != null) {
            NetworkInfo networkinfo = manager.getActiveNetworkInfo();
            return networkinfo != null && networkinfo.isConnected() && networkinfo.isAvailable();
        }
        return false;
    }

    /**
     * 获取版本号
     *
     * @return 当前应用的版本号
     */
    public static String getVersion() {
        try {
            PackageManager manager = MyApplication.getContext().getPackageManager();
            PackageInfo info = manager.getPackageInfo(MyApplication.getContext().getPackageName(), 0);
            return info.versionName;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * int ip 地址格式化
     */
    public static String intToIp(int paramInt) {
        return (paramInt & 0xFF) + "." + (0xFF & paramInt >> 8) + "." + (0xFF & paramInt >> 16) + "."
                + (0xFF & paramInt >> 24);
    }

    /**
     * 时间格式化
     *
     * @param time：时间（单位：分钟）
     * @return 字符串
     */
    public static String timeFormat(int time) {
        String str;
        int hour = time / 60;
        int minute = time % 60;
        if (hour == 0) str = "<font><big><big>" + minute + "</big></big></font>分钟";
        else
            str = "<font><big><big>" + hour + "</big></big></font>小时" + "<font><big><big>" + minute + "</big></big></font>分钟";
        return str;
    }

    /**
     * 获取version code
     */
    public static int getVersionCode(Context context) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            return info.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 设置 IPC 录像保存路径
     */
    public static String getRecordDirectory(String did) {
        String dir = "SDKTestbed/" + did + "/Video/";
        final File recDir = new File(Environment.getExternalStorageDirectory(), dir);
        boolean suc = recDir.mkdirs();
        String dirPath = recDir.getAbsolutePath();
        logUtil.d("LiveRecord", "save Dir:" + dirPath + ",mkdir:" + suc);
        return dirPath;
    }

    /**
     * 保存一个对象数据
     *
     * @param fileName: 文件名
     * @param object:   数据对象
     */
    public static void saveObject(Context context, String fileName, Object object) {
        FileOutputStream fos = null;
        ObjectOutputStream oos = null;
        try {
            fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(object);

            // 将 fos 的数据保存到内核缓冲区
            // 不能确保数据保存到物理存储设备上，如突然断电可能导致文件未保存
            fos.flush();

            // 将数据同步到达物理存储设备
            FileDescriptor fd = fos.getFD();
            fd.sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (oos != null) {
                try {
                    oos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 从本地（手机内部存储）读取保存的对象
     *
     * @param filename 文件名称
     */
    public static Object getFileObject(Context context, String filename) {
        FileInputStream fis = null;
        ObjectInputStream ois = null;
        try {
            fis = context.openFileInput(filename);
            ois = new ObjectInputStream(fis);
            return ois.readObject();
        } catch (Exception e) {
            logUtil.e(TAG, "getObject error,msg=" + e.getMessage());
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (ois != null) {
                try {
                    ois.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * 读取 asset 目录下文件。
     *
     * @param file 文件
     * @param code 字符编码 如"utf-8"
     * @return content
     */
    public static String readAssetsFile(Context context, String file, String code) {
        int len;
        byte[] buf;
        String result = "";
        try {
            InputStream in = context.getAssets().open(file);
            len = in.available();
            buf = new byte[len];
            in.read(buf, 0, len);
            result = new String(buf, code);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

//    // 将 Json 数据解析成相应的映射对象
//    public static <T> T parseJsonWithGson(String jsonData, Class<T> type) {
//        Gson gson = new Gson();
//        T result = null;
//        try {
//            result = gson.fromJson(jsonData, type);
//        } catch (Exception e) {
//            logUtil.e("GsonUtil", "parseJsonWithGson Error!msg=" + e.getMessage());
//            e.printStackTrace();
//        }
//        return result;
//    }

    /**
     * 判断是否存在第三方 app
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

    /**
     * 根据包名启动第三方 app
     */
    public static void startOtherApp(Context context, String packageName, boolean isActivity) {
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(packageName);
        if (!isActivity && intent != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }

    /**
     * 是否存在SDCard
     */
    public static boolean hasSdcard() {
        String state = Environment.getExternalStorageState();
        return state.equals(Environment.MEDIA_MOUNTED);
    }

    public static String getIpAddr(Context context) {
        try {
            WifiManager wifiMng = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            DhcpInfo di = wifiMng.getDhcpInfo();
            return IpUtils.intToIp(di.ipAddress);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取某个百分比位置的颜色
     *
     * @param radio 取值[0,1]
     * @return color
     */
    public static int getColor(float radio, int[] colorArr, float[] positionArr, int alpha) {
        int startColor;
        int endColor;
        if (radio >= 1) {
            return colorArr[colorArr.length - 1];
        }
        for (int i = 0; i < positionArr.length; i++) {
            if (radio <= positionArr[i]) {
                if (i == 0) {
                    return colorArr[0];
                }
                startColor = colorArr[i - 1];
                endColor = colorArr[i];
                float areaRadio = getAreaRadio(radio, positionArr[i - 1], positionArr[i]);
                return getColorFrom(startColor, endColor, areaRadio, alpha);
            }
        }
        return -1;
    }

    /**
     * 获取某个百分比位置的颜色
     *
     * @param radio 取值[0,1]
     * @return color
     */
    public static int getColor(float radio, int[] colorArr, float[] positionArr) {
        int startColor;
        int endColor;
        if (radio >= 1) {
            return colorArr[colorArr.length - 1];
        }
        for (int i = 0; i < positionArr.length; i++) {
            if (radio <= positionArr[i]) {
                if (i == 0) {
                    return colorArr[0];
                }
                startColor = colorArr[i - 1];
                endColor = colorArr[i];
                float areaRadio = getAreaRadio(radio, positionArr[i - 1], positionArr[i]);
                return getColorFrom(startColor, endColor, areaRadio);
            }
        }
        return -1;
    }

    private static float getAreaRadio(float radio, float startPosition, float endPosition) {
        return (radio - startPosition) / (endPosition - startPosition);
    }

    /**
     * 取两个颜色间的渐变区间 中的某一点的颜色
     *
     * @param startColor s
     * @param endColor   e
     * @param radio      r
     * @return color
     */
    private static int getColorFrom(int startColor, int endColor, float radio) {
        int redStart = Color.red(startColor);
        int blueStart = Color.blue(startColor);
        int greenStart = Color.green(startColor);
        int redEnd = Color.red(endColor);
        int blueEnd = Color.blue(endColor);
        int greenEnd = Color.green(endColor);

        int red = (int) (redStart + ((redEnd - redStart) * radio + 0.5));
        int greed = (int) (greenStart + ((greenEnd - greenStart) * radio + 0.5));
        int blue = (int) (blueStart + ((blueEnd - blueStart) * radio + 0.5));
        return Color.argb(255, red, greed, blue);
    }

    /**
     * 取两个颜色间的渐变区间 中的某一点的颜色
     */
    private static int getColorFrom(int startColor, int endColor, float radio, int alpha) {
        int redStart = Color.red(startColor);
        int blueStart = Color.blue(startColor);
        int greenStart = Color.green(startColor);
        int redEnd = Color.red(endColor);
        int blueEnd = Color.blue(endColor);
        int greenEnd = Color.green(endColor);

        int red = (int) (redStart + ((redEnd - redStart) * radio + 0.5));
        int greed = (int) (greenStart + ((greenEnd - greenStart) * radio + 0.5));
        int blue = (int) (blueStart + ((blueEnd - blueStart) * radio + 0.5));
        return Color.argb(alpha, red, greed, blue);
    }

    /**
     * 设置颜色透明度
     */
    public static int setColorAlpha(int color, int alpha) {
        int red = Color.red(color);
        int blue = Color.blue(color);
        int green = Color.green(color);
        return Color.argb(alpha, red, green, blue);
    }

    /**
     * 临时存储拍照照片
     */
    public static Uri getCameraCacheUri(String path) {
        String fileName = System.currentTimeMillis() + ".jpg";
        File out = new File(path);
        if (!out.exists()) {
            logUtil.d(TAG, "getCameraCacheUri " + out.mkdirs());
        }
        out = new File(path, fileName);
        return Uri.fromFile(out);
    }

    /**
     * 通过 URI 获得文件路径
     *
     * @param context    上下文
     * @param contentUri 目标uri
     */
    public static String getPathFromUri(Context context, Uri contentUri) {
        if (contentUri != null) {
            if (contentUri.getScheme().compareTo("content") == 0) {
                String[] proj = {MediaStore.Images.Media.DATA};
                CursorLoader loader = new CursorLoader(context, contentUri,
                        proj, null, null, null);
                Cursor cursor = loader.loadInBackground();
                int index = cursor
                        .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                return cursor.getString(index);
            } else if (contentUri.getScheme().compareTo("file") == 0) {
                return contentUri.toString().replace("file://", "");
            }
        }
        return null;
    }

    /**
     * 获取图片宽高
     */
    public static int[] getImageSize(String path) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        /**
         * 最关键在此，把options.inJustDecodeBounds = true;
         * 这里再decodeFile()，返回的bitmap为空，但此时调用options.outHeight时，已经包含了图片的高了
         */
        options.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(path, options); // 此时返回的 bitmap 为 null
        /**
         *options.outHeight 为原始图片的高
         */
        return new int[]{options.outWidth, options.outHeight};
    }

    /**
     * 加载大图
     */
    public static Bitmap readBitmap(Context context, int resId) {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.RGB_565;
        opt.inPurgeable = true;
        opt.inInputShareable = true;
        // 获取资源图片
        InputStream is = context.getResources().openRawResource(resId);
        return BitmapFactory.decodeStream(is, null, opt);
    }

    /**
     * 返回 app 运行状态
     * 1: 程序在前台运行
     * 2: 程序在后台运行
     * 3: 程序未启动
     * 注意：需要配置权限<uses-permission android:name="android.permission.GET_TASKS" />
     */
    public static int getAppStatus(Context context, String pageName) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(20);
        // 判断程序是否在栈顶
        if (list.get(0).topActivity.getPackageName().equals(pageName)) {
            return 1;
        } else {
            // 判断程序是否在栈里
            for (ActivityManager.RunningTaskInfo info : list) {
                if (info.topActivity.getPackageName().equals(pageName)) {
                    return 2;
                }
            }
            return 3;// 栈里找不到，返回 3
        }
    }

    public static String queryDevice(String type) {
        DeviceParamsSet info = ControlManager.getInstance().getDataSendInfo();
        JSONObject object = new JSONObject();
        if (info != null) {
            if (type.equals("status")||type.equals("fridge_control")) {
                try {
                    object.put("code", 0);
                    object.put("error_code", 202);
                    object.put("mfrs", ApkUtil.getManufacturer());
                    JSONArray array = getArray(info);
                    object.put("streams", array);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return object.toString();
    }

    private static JSONArray getArray(DeviceParamsSet info) throws JSONException {
        JSONArray array = new JSONArray();
        JSONObject obj1 = new JSONObject();
        JSONObject obj2 = new JSONObject();
        if (info.mode == SerialInfo.MODE_SMART) {
            obj1.put("current_value", "1");
            obj1.put("stream_id", FridgeStreamId.SAMRT_MODE);

            obj2.put("current_value", "0");
            obj2.put("stream_id", FridgeStreamId.HOLIDAY_MODE);
        } else if (info.mode == SerialInfo.MODE_HOLIDAY) {
            obj1.put("current_value", "0");
            obj1.put("stream_id", FridgeStreamId.SAMRT_MODE);

            obj2.put("current_value", "1");
            obj2.put("stream_id", FridgeStreamId.HOLIDAY_MODE);
        } else {
            obj1.put("current_value", "0");
            obj1.put("stream_id", FridgeStreamId.SAMRT_MODE);

            obj2.put("current_value", "0");
            obj2.put("stream_id", FridgeStreamId.HOLIDAY_MODE);
        }
        array.put(obj1);
        array.put(obj2);

        JSONObject obj3 = new JSONObject();
        obj3.put("current_value", info.freezing_room_temp_set);
        obj3.put("stream_id", FridgeStreamId.FRE_TEMP);
        array.put(obj3);

        JSONObject obj4 = new JSONObject();
        obj4.put("current_value", info.cold_closet_temp_set);
        obj4.put("stream_id", FridgeStreamId.FRI_TEMP);
        array.put(obj4);

        JSONObject obj5 = new JSONObject();
        obj5.put("current_value", info.temp_changeable_room_temp_set);
        obj5.put("stream_id", FridgeStreamId.VAR_TEMP);
        array.put(obj5);

        JSONObject obj6 = new JSONObject();
        obj6.put("current_value", getStatus(info.cold_closet_room_enable));
        obj6.put("stream_id", FridgeStreamId.FRI_POWER);
        array.put(obj6);

        JSONObject obj7 = new JSONObject();
        obj7.put("current_value", getStatus(info.temp_changeable_room_room_enable));
        obj7.put("stream_id", FridgeStreamId.VAR_POWER);
        array.put(obj7);

        JSONObject obj8 = new JSONObject();
        obj8.put("current_value", getStatus(info.quick_cold));
        obj8.put("stream_id", FridgeStreamId.FASTFRI_MODE);
        array.put(obj8);

        JSONObject obj9 = new JSONObject();
        obj9.put("current_value", getStatus(info.quick_freeze));
        obj9.put("stream_id", FridgeStreamId.FASTFRE_MODE);
        array.put(obj9);
        return array;
    }

    private static String getStatus(boolean enable) {
        if (enable)
            return "1";
        return "0";
    }
}