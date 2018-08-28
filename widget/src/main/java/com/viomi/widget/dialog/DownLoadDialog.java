package com.viomi.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.viomi.widget.R;

import java.lang.ref.WeakReference;

/**
 * 下载 Dialog
 * Created by William on 2018/4/21.
 */
public class DownLoadDialog extends Dialog {
    private static final int MSG_REFRESH_UI = 1;
    private String mMessage, mBtn, mFinishText;
    private ProgressBar mProgressBar;// 进度条
    private TextView mPerTextView, mSizeTextView, mMessageTextView;// 下载实时信息
    private DialogHandler mHandler;
    private OnCancelClickListener onCancelClickListener;

    public DownLoadDialog(@NonNull Context context, String message, String btn, String finishText) {
        super(context);
        mMessage = message;
        mBtn = btn;
        mFinishText = finishText;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_download);
        Window window = this.getWindow();
        if (window != null) {
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(lp);
            window.setGravity(Gravity.CENTER);
        }
        setCancelable(false);

        TextView btnTextView = (TextView) findViewById(R.id.download_cancel);
        mMessageTextView = (TextView) findViewById(R.id.download_text);
        mPerTextView =  (TextView)findViewById(R.id.download_percent);
        mSizeTextView = (TextView) findViewById(R.id.download_size);
        mProgressBar = (ProgressBar) findViewById(R.id.download_progress);

        mMessageTextView.setText(mMessage);
        btnTextView.setText(mBtn);
        // 进度条
        mProgressBar.setProgress(0);
        String str = mProgressBar.getProgress() + "%";
        mPerTextView.setText(str);

        btnTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onCancelClickListener != null) onCancelClickListener.onCancel();
            }
        });

        mHandler = new DialogHandler(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
    }

    public interface OnCancelClickListener {
        void onCancel();
    }

    public void setOnCancelClickListener(OnCancelClickListener onCancelClickListener) {
        this.onCancelClickListener = onCancelClickListener;
    }

    public void setProgressBar(long currentBytes, long contentLength) {
        Data data = new Data();
        data.current = currentBytes;
        data.total = contentLength;
        if (mHandler != null) {
            Message message = new Message();
            message.what = MSG_REFRESH_UI;
            message.obj = data;
            mHandler.sendMessage(message);
        }
    }

    private class Data {
        long current;
        long total;
    }

    private static class DialogHandler extends Handler {
        WeakReference<DownLoadDialog> weakReference;

        DialogHandler(DownLoadDialog dialog) {
            this.weakReference = new WeakReference<>(dialog);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (this.weakReference != null) {
                DownLoadDialog dialog = this.weakReference.get();
                if (dialog != null && dialog.isShowing()) {
                    switch (msg.what) {
                        case MSG_REFRESH_UI:
                            Data data = (Data) msg.obj;
                            if (data.current <= 0 && data.total <= 0) return;
                            dialog.mProgressBar.setProgress((int) (data.current * 100 / data.total));
                            if (dialog.mProgressBar.getProgress() == 100)
                                dialog.mMessageTextView.setText(dialog.mFinishText);
                            else dialog.mMessageTextView.setText(dialog.mMessage);
                            String str = dialog.mProgressBar.getProgress() + "%";
                            dialog.mPerTextView.setText(str);
                            String str1 = data.current + " / " + data.total;
                            dialog.mSizeTextView.setText(str1);
                            break;
                    }
                }
            }
        }
    }
}