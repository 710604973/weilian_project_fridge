package com.mode.fridge.view.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mode.fridge.R;
import com.mode.fridge.manager.ControlManager;

/**
 * Created by young2 on 2017/3/24.
 */

public class FilterDialog extends BaseDialog{
    private Context mContext;
    private TextView mFilterRemain;

    private OnResetClickListener mOnResetClickListener;

    public  interface OnResetClickListener {
        void onClick();
    }

    public void setResetClickListener(OnResetClickListener listener) {
        this.mOnResetClickListener = listener;
    }

    protected FilterDialog(Context context, boolean cancelable, DialogInterface.OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        mContext=context;
    }

    public FilterDialog(Context context, int themeResId) {
        super(context, themeResId);
        mContext=context;
    }
    public FilterDialog(Context context) {
        super(context);
        mContext=context;
    }

    @Override
    public void setView() {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.fridge_filter_layout);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowManager.LayoutParams lp = this.getWindow().getAttributes();
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity= Gravity.RIGHT;
        this.getWindow().setAttributes(lp);

        mFilterRemain= (TextView) findViewById(R.id.filter_remain);
        ImageView closeImage= (ImageView) findViewById(R.id.close_icon);
        closeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        TextView filter_reset= (TextView) findViewById(R.id.filter_reset);
        filter_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               if(mOnResetClickListener!=null){
                   mOnResetClickListener.onClick();
               }
            }
        });
        TextView filter_buy= (TextView) findViewById(R.id.filter_buy);
        filter_buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            Toast.makeText(mContext,"滤芯暂未上线，敬请期待",Toast.LENGTH_SHORT).show();
            }
        });

    }



    @Override
    public void show() {
        super.show();
        int progress= ControlManager.getInstance().getFilterLifeUsePercent();
        mFilterRemain.setText(mContext.getString(R.string.text_filter_remain)+(100- progress)+"%");
        if(progress>=100){
            mFilterRemain.setTextColor(mContext.getResources().getColor(R.color.red));
        }else if(progress>=90){
            mFilterRemain.setTextColor(mContext.getResources().getColor(R.color.orange));
        }


    }
}
