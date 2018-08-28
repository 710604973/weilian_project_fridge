package com.mode.fridge.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.mode.fridge.R;
import com.mode.fridge.device.DeviceConfig;
import com.mode.fridge.utils.PhoneUtil;
import com.mode.fridge.view.dialog.BaseDialog;
import com.mode.fridge.widget.Loading;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.HashSet;

/**
 * Created by young2 on 2016/12/15.
 */

public class BaseActivity extends AppCompatActivity {

    private ScreenThread screenThread;
    private BaseHandler baseHandler;
    private BaseDialog mVoiceTipsDialog;
    private SharedPreferences sp;
    private Loading loading;

    protected Context mContext = this;
    public final String TAG = mContext.getClass().getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String model = PhoneUtil.getDeviceModel();
        if (model != null) {
            DeviceConfig.MODEL = model;
        }
        baseHandler = new BaseHandler(this);
        sp = getSharedPreferences("screen_imgs", MODE_PRIVATE);
    }


    /***
     * 隐藏导航栏
     */
    public void hideNavigationBar() {
        int uiFlags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                | View.SYSTEM_UI_FLAG_FULLSCREEN; // hide status bar

        if (android.os.Build.VERSION.SDK_INT >= 19) {
            uiFlags |= 0x00001000;    //SYSTEM_UI_FLAG_IMMERSIVE_STICKY: hide navigation bars - compatibility: building API level is lower thatn 19, use magic number directly for higher API target level
        } else {
            uiFlags |= View.SYSTEM_UI_FLAG_LOW_PROFILE;
        }
        try {
            getWindow().getDecorView().setSystemUiVisibility(uiFlags);
        } catch (Exception e) {
            e.printStackTrace();
        }
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideNavigationBar();
        }
    }


    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        super.onKeyUp(keyCode, event);
        if ((keyCode == KeyEvent.KEYCODE_VOLUME_UP || keyCode == KeyEvent.KEYCODE_VOLUME_DOWN)) {
            this.hideNavigationBar();
        }
        return false;
    }


    @Override
    protected void onResume() {
        super.onResume();
        startScreenTimer();
        hideNavigationBar();
//        StatsManager.recordActivityStart(this);
    }

    protected String getText(TextView textView) {
        return textView.getText().toString().trim();
    }

    public void openActivity(Class<?> clazz) {
        openActivity(clazz, null);
    }

    public void openActivity(Class<?> clazz, Bundle bundle) {
        Intent intent = new Intent(this, clazz);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
    }

    public void openActvityForResult(Class<?> clazz, int requestCode) {
        Intent intent = new Intent(this, clazz);
        startActivityForResult(intent, requestCode);
    }

    public void openActvityForResult(Class<?> clazz, int requestCode, Bundle bundle) {
        Intent intent = new Intent(this, clazz);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivityForResult(intent, requestCode);
    }

    /**
     * @param resId  资源ID
     * @param length true为长时间，false为短时间
     * @return: void
     */
    protected void showToast(int resId, boolean length) {
        Toast.makeText(this, resId, length ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT).show();
    }

    /**
     * @param msg    内容
     * @param length true为长时间，false为短时间
     * @return: void
     */
    protected void showToast(String msg, boolean length) {
        if (TextUtils.isEmpty(msg)) {
            return;
        }
        Toast.makeText(this, msg, length ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT).show();
    }

    private void startScreenTimer() {
        if (screenThread != null) {
            screenThread.interrupt();
        }

        boolean screen_switch = sp.getBoolean("screen_switch", false);
        HashSet<String> img_set = (HashSet<String>) sp.getStringSet("screen_set", new HashSet<String>());
        boolean b = false;
        for (String value : img_set) {
            File file = new File(value);
            if (file.exists()) {
                b = true;
            }
        }

        if (screen_switch && b) {
            screenThread = new ScreenThread(this);
            screenThread.start();
        }
    }

    public void creatVoiceDialog() {
        if (mVoiceTipsDialog == null) {
            final View view = LayoutInflater.from(BaseActivity.this).inflate(R.layout.dialog_confirm, null);
            mVoiceTipsDialog = new BaseDialog(BaseActivity.this) {
                @Override
                public void setView() {
                    mVoiceTipsDialog.setContentView(view);
                }
            };
        }
        mVoiceTipsDialog.show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (screenThread != null) {
            screenThread.interrupt();
        }
//        StatsManager.recordActivityEnd(this);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        startScreenTimer();
        return super.dispatchTouchEvent(ev);
    }


    private static class BaseHandler extends Handler {

        private WeakReference<BaseActivity> weakReference;

        public BaseHandler(BaseActivity activity) {
            this.weakReference = new WeakReference<BaseActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            BaseActivity mActivity = weakReference.get();

            switch (msg.what) {
                case 0: {
//                    Intent intent = new Intent(mActivity, ScreenSaverActivity.class);
//                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
//                    mActivity.startActivity(intent);
                }
                break;
                case 1: {
                }
                break;
            }
        }
    }


    private static class ScreenThread extends Thread {

        private WeakReference<BaseActivity> weakReference;
        private BaseActivity mActivity;

        public ScreenThread(BaseActivity activity) {
            this.weakReference = new WeakReference<BaseActivity>(activity);
            mActivity = weakReference.get();
        }

        @Override
        public void run() {

            int screen_time = mActivity.sp.getInt("screen_time", 60 * 1000);
            screen_time = screen_time / 1000;

//            screen_time = 3;

            for (int i = 0; i < screen_time; i++) {
                if (isInterrupted()) {
                    return;
                }
                SystemClock.sleep(1000);
            }
            if (isInterrupted()) {
                return;
            }


            mActivity.baseHandler.sendEmptyMessage(0);
        }
    }

    public void notifyScreen() {
        startScreenTimer();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void initLoading() {
        if (loading == null) {
            loading = new Loading(mContext);
        }
    }

    public void showLoading() {
        if (isFinishing())
            return;
        initLoading();
        loading.setCancelable(true);
        if (!loading.isShowing()) {
            loading.show();
        }
    }

    public void showUnCancelLoading() {
        if (isFinishing())
            return;
        initLoading();
        loading.setCancelable(false);
        if (!loading.isShowing()) {
            loading.show();
        }
    }

    public void hideLoading() {
        if (loading != null && loading.isShowing())
            loading.cancel();
    }

    public boolean getLoadingStatus() {
        initLoading();
        return loading.isShowing();
    }
}
