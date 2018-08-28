package com.mode.fridge.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;

import com.mode.fridge.common.base.BaseActivity;

import java.lang.ref.WeakReference;

/**
 * Created by young2 on 2016/12/17.
 */

public abstract class BaseHandler2Activity extends com.mode.fridge.activity.BaseActivity {

    protected MyHandler mHandler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHandler = new MyHandler(this);
    }

    protected static class MyHandler extends Handler {
        WeakReference<BaseHandler2Activity> weakReference;

        public MyHandler(BaseHandler2Activity activity) {
            this.weakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (this.weakReference != null) {
                BaseHandler2Activity activity = weakReference.get();
                try {
                    activity.processMsg(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
    }

    protected abstract void processMsg(Message msg);

}
