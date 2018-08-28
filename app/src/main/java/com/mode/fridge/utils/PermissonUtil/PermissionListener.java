package com.mode.fridge.utils.PermissonUtil;

/**
 * Created by GMARUnity on 2017/2/16.
 */
public interface PermissionListener {

    void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults);

    void onRequestPermissionSuccess();

    void onRequestPermissionError();

}
