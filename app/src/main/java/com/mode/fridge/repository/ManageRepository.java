package com.mode.fridge.repository;

import android.content.Context;
import android.os.Environment;

import com.miot.api.MiotManager;
import com.miot.common.exception.MiotException;
import com.mode.fridge.AppConstants;
import com.mode.fridge.bean.QRCodeBase;
import com.mode.fridge.utils.ToolUtil;
import com.mode.fridge.utils.logUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.ResponseBody;
import rx.Observable;

/**
 * 管理中心相关 Api
 * Created by William on 2018/1/30.
 */
public class ManageRepository {
    private static final String TAG = ManageRepository.class.getSimpleName();
    private static ManageRepository mInstance;

    public static ManageRepository getInstance() {
        if (mInstance == null) {
            synchronized (ManageRepository.class) {
                if (mInstance == null) {
                    mInstance = new ManageRepository();
                }
            }
        }
        return mInstance;
    }

    /**
     * 获取本地保存的云米账号信息
     */
    public Observable<QRCodeBase> getUser(Context context) {
        return Observable.just(ToolUtil.getFileObject(context, AppConstants.USER_INFO_FILE))
                .map(o -> (QRCodeBase) o);
    }

    /**
     * 退出登录
     */
    public Observable<Boolean> logout() {
        return Observable.create(subscriber -> {
            try {
                MiotManager.getPeopleManager().deletePeople();// 删除配置小米账号信息
                MiotRepository.getInstance().unbindDevice(subscriber);// 解除绑定
                logUtil.d(TAG, "deleteUser success");
            } catch (MiotException e) {
                e.printStackTrace();
                logUtil.e(TAG, e.getMessage());
                subscriber.onNext(false);
                subscriber.onCompleted();
            }
        });
    }

    /**
     * 保存下载更新 Apk
     */
    public Observable<String> saveApk(String url, ResponseBody responseBody) {
        return Observable.create(subscriber -> {
            try {
                File dir = new File(Environment.getExternalStorageDirectory(), AppConstants.PATH);
                if (!dir.exists()) logUtil.d(TAG, dir.mkdirs() + "");
                File futureStudioIconFile = new File(Environment.getExternalStorageDirectory(), AppConstants.PATH + getFileName(url));
                InputStream inputStream = null;
                OutputStream outputStream = null;
                try {
                    byte[] fileReader = new byte[4096];
                    long fileSize = responseBody.contentLength();
                    long fileSizeDownloaded = 0;
                    inputStream = responseBody.byteStream();
                    outputStream = new FileOutputStream(futureStudioIconFile);
                    while (true) {
                        int read = inputStream.read(fileReader);
                        if (read == -1) break;
                        outputStream.write(fileReader, 0, read);
                        fileSizeDownloaded += read;
                        logUtil.d(TAG, "file download: " + fileSizeDownloaded + " of " + fileSize);
                    }
                    outputStream.flush();
                    subscriber.onNext(Environment.getExternalStorageDirectory() + "/" + AppConstants.PATH + getFileName(url));
                    subscriber.onCompleted();
                } catch (IOException e) {
                    subscriber.onNext(null);
                    subscriber.onCompleted();
                    logUtil.e(TAG, e.toString());
                } finally {
                    if (inputStream != null) inputStream.close();
                    if (outputStream != null) outputStream.close();
                }
            } catch (IOException e) {
                logUtil.e(TAG, e.toString());
                subscriber.onNext(null);
                subscriber.onCompleted();
            }
        });
    }

    /**
     * 根据链接获取 Apk 名称
     */
    private String getFileName(String path) {
        int separatorIndex = path.lastIndexOf("/");
        return (separatorIndex < 0) ? path : path.substring(separatorIndex + 1, path.length());
    }
}