package com.mode.fridge.widget;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.widget.RelativeLayout;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.mode.fridge.R;
import com.mode.fridge.view.dialog.BaseDialog;

import static com.mode.fridge.utils.ApkUtil.getPackageName;

/**
 * Created by Ljh on 2017/11/6
 */

public class Loading extends BaseDialog implements View.OnClickListener {
    private Context mContext;
    private SimpleDraweeView sImg;
    boolean mCancelable = true;
    DraweeController draweeController;

    public Loading(Context context) {
        super(context, R.style.Dialog);
        this.mContext = context;
    }

    @Override
    public void setView() {
        //this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.loading);
        RelativeLayout root = (RelativeLayout) findViewById(R.id.root);
        sImg = (SimpleDraweeView) findViewById(R.id.sImg);
        draweeController = Fresco.newDraweeControllerBuilder()
                .setAutoPlayAnimations(true)
                .setUri(Uri.parse("res://" + getPackageName() + "/" + R.drawable.loading_g))
                .build();
        sImg.setController(draweeController);
        root.setOnClickListener(this);
    }

    @Override
    public void setCancelable(boolean flag) {
        mCancelable = flag;
        super.setCancelable(flag);
    }

    @Override
    public void onClick(View v) {
        if (mCancelable)
            this.cancel();
    }

}