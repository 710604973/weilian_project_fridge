package com.mode.fridge.receiver;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.mode.fridge.broadcast.BroadcastAction;

import static com.mode.fridge.utils.ApkUtil.getPackageName;

public class MyReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        switch (intent.getAction()) {
            case BroadcastAction.CLOSE_APP_PROGRESS:
                Log.i("info", "=====================close.progres__fridge");
                exitApp(context);
                break;
        }
    }

    @SuppressLint("MissingPermission")
    private void exitApp(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE); //获取应用程序管理器
        manager.killBackgroundProcesses(getPackageName());
    }
}
