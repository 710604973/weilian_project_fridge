package com.viomi.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.viomi.widget.R;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 加载对话框
 * Created by William on 2018/2/24.
 */
public class BaseProgressDialog extends Dialog {
    private String message;
    private Timer mTimer;
    private TimerTask mTimerTask;
    private Context mContext;

    public BaseProgressDialog(@NonNull Context context, String message) {
        super(context);
        this.message = message;
        this.mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_base_progress);
        Window window = this.getWindow();
        if (window != null) {
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(lp);
            window.setGravity(Gravity.CENTER);
        }
        setCancelable(false);

        TextView textView = (TextView) findViewById(R.id.progress_text);
        textView.setText(message);
        SimpleDraweeView mSimpleDraweeView = (SimpleDraweeView) findViewById(R.id.progress_gif);
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setUri(Uri.parse("android.resource://" + mContext.getPackageName() + "/" + R.drawable.icon_loading))
                .setAutoPlayAnimations(true)
                .build();
        mSimpleDraweeView.setController(controller);

        mTimer = new Timer();
        mTimerTask = new TimerTask() {
            @Override
            public void run() {
                dismiss();
            }
        };
        mTimer.schedule(mTimerTask, 30000);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        if (mTimerTask != null) {
            mTimerTask.cancel();
            mTimerTask = null;
        }
    }
}