package com.mode.fridge.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mode.fridge.R;
import com.mode.fridge.common.base.BaseActivity;
import com.mode.fridge.utils.ToolUtil;

import butterknife.BindView;

/**
 * 登录指引 Activity
 * Created by William on 2018/1/30.
 */
public class LoginGuideActivity extends Activity {
    private TextView tvBack;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_guide);
        tvBack = findViewById(R.id.tv_back);
        tvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}