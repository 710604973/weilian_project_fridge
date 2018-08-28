package com.mode.fridge.view.dialog.base;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.miot.common.abstractdevice.AbstractDevice;
import com.mode.fridge.AppConstants;
import com.mode.fridge.R;
import com.mode.fridge.activity.web.MallWebActivity;
import com.mode.fridge.adapter.base.BaseDeviceAdapter;
import com.mode.fridge.common.base.BaseFridgeDialog;
import com.mode.fridge.manager.ManagePreference;
import com.mode.fridge.view.coverflow.CoverFlowViewPager;

import java.util.ArrayList;

import butterknife.BindView;

public abstract class BaseDeviceDialog extends BaseFridgeDialog {
    private static final String TAG = BaseDeviceDialog.class.getSimpleName();
    public final String PARAM_NAME = "name";// 设备名称
    public final String PARAM_DID = "did";// 设备 id
    public final String PARAM_LIST = "list";// 设备 list
    private String mDid;

    @BindView(R.id.cover)
    CoverFlowViewPager mCover;

    @BindView(R.id.iv_delete)
    ImageView iv_delete;

    @BindView(R.id.tv_title)
    TextView tv_title;

    @BindView(R.id.rl_add_store)
    RelativeLayout rl_add_store;

    @BindView(R.id.view_no_data)
    View view_no_data;


    @Override
    protected void initWithOnCreate() {
        layoutId = R.layout.dialog_wash_machine;
    }

    @Override
    protected void initWithOnCreateDialog(View view) {
        mDid = getArguments() != null ? getArguments().getString(PARAM_DID) : "";
        initCover();
    }

    public abstract BaseDeviceAdapter getAdapter();

    private void initCover() {
        ArrayList<AbstractDevice> devices = getArguments() != null ? getArguments().getParcelableArrayList(PARAM_LIST) : null;
        if (devices != null && devices.size() != 0) {
            mCover.setVisibility(View.VISIBLE);
            BaseDeviceAdapter adapter = getAdapter();
            mCover.initViewPager(adapter);
            adapter.setDatas(devices);
            mCover.setCurrentPosition(devices.size() / 2);
        } else {
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) view_no_data.getLayoutParams();
            params.width = 373;
            params.height = 533;
            view_no_data.setLayoutParams(params);
            mCover.setVisibility(View.GONE);
            view_no_data.setVisibility(View.VISIBLE);
            rl_add_store.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), MallWebActivity.class);
                    String url;
                    if (ManagePreference.getInstance().getDebug()) {
                        url = AppConstants.URL_WASH_MACHINE_DEBUG;
                    } else {
                        url = AppConstants.URL__WASH_MACHINE_RELEASE;
                    }
                    intent.putExtra(AppConstants.WEB_URL, url);
                    startActivity(intent);
                }
            });
        }

        iv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    /**
     * 转换dip为px
     *
     * @param context
     * @param dip
     * @return
     */
    public static int dp2px(Context context, int dip) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dip * scale + 0.5f * (dip >= 0 ? 1 : -1));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        BaseDeviceAdapter adapter = getAdapter();
        if (adapter != null) {
            adapter.close();
        }
    }
}
