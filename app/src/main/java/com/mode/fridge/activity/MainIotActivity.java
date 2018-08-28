package com.mode.fridge.activity;

import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.miot.common.abstractdevice.AbstractDevice;
import com.mode.fridge.AppConstants;
import com.mode.fridge.MyApplication;
import com.mode.fridge.R;
import com.mode.fridge.activity.web.MallWebActivity;
import com.mode.fridge.adapter.DeviceTypeAdapter;
import com.mode.fridge.bean.DeviceTypeEntity;
import com.mode.fridge.bean.QRCodeBase;
import com.mode.fridge.common.base.BaseActivity;
import com.mode.fridge.common.rxbus.BusEvent;
import com.mode.fridge.common.rxbus.RxBus;
import com.mode.fridge.contract.IotTypeContract;
import com.mode.fridge.contract.ManageContract;
import com.mode.fridge.device.DeviceConfig;
import com.mode.fridge.manager.ManagePreference;
import com.mode.fridge.utils.DeviceIconUtil;
import com.mode.fridge.utils.FileUtil;
import com.mode.fridge.utils.ImageLoadUtil;
import com.mode.fridge.utils.PermissonUtil.PermissionListener;
import com.mode.fridge.utils.PermissonUtil.PermissionUtil;
import com.mode.fridge.utils.ToastUtil;
import com.mode.fridge.view.CircleImageView;
import com.mode.fridge.view.dialog.FanMachineDialog;
import com.mode.fridge.view.dialog.HoodMachineDialog;
import com.mode.fridge.view.dialog.LoginQRCodeDialog;
import com.mode.fridge.view.dialog.PurifierWaterDialog;
import com.mode.fridge.view.dialog.SweepMachineDialog;
import com.mode.fridge.view.dialog.WashDishMachineDialog;
import com.mode.fridge.view.dialog.WashMachineDialog;
import com.viomi.widget.sphere3d.Tag;
import com.viomi.widget.sphere3d.TagCloudView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Subscription;

//MainContract.View
public class MainIotActivity extends BaseActivity implements
        IotTypeContract.View,
        ManageContract.View,
        TagCloudView.OnTagClickListener {
    private static final String TAG = MainIotActivity.class.getSimpleName();
    private DeviceTypeAdapter mAdapter;
    private List<DeviceTypeEntity> mList;
    private Subscription mSubscription;
    private Context mContext;
    //    private DeviceListDialog mDeviceListDialog;// 设备列表 Dialog
    private HoodMachineDialog mHoodMachineDialog;
    //    private FridgeDialog mFridgeDialog;// 冰箱 Dialog
//    private FridgeMachineDialog fridgeMachineDialog;
    private FanMachineDialog fanMachineDialog;// 風扇 Dialog
    private WashDishMachineDialog washDishMachineDialog;// 洗碗机 Dialog
    private SweepMachineDialog sweepMachineDialog;// 扫地机器人 Dialog
    //    private WaterPurifierDialog mWaterPurifierDialog;// 净水器 Dialog
    private PurifierWaterDialog mPurifierWaterDialog;// 净水器 Dialog
    //    private HeatKettleDialog mHeatKettleDialog;// 即热饮水吧 Dialog
//    private PLMachineDialog mPLMachineDialog;// 管线机 Dialog
    private WashMachineDialog mWashMachineDialog;// 洗衣机 Dialog
//    private DevicesDialog mDevicesDialog;// 设备 Dialog

    @BindView(R.id.btn_back)
    Button btn_back;// 设备场景

//    @BindView(R.id.btn_GO)
//    Button btn_GO;//测试

    @BindView(R.id.iot_device_sphere)
    TagCloudView mTagCloudView;// 设备场景

    @BindView(R.id.manage_user_name)
    TextView mUserNameTextView;// 用户名称

//    @BindView(R.id.manage_account)
//    TextView mAccountTextView;// 账号

    @BindView(R.id.tv_weather_quality)
    TextView tvQuaulity;// 空气质量

    @BindView(R.id.tv_indoor_humidity)
    TextView tvHumidity;// 室内湿度

    @BindView(R.id.tv_weather)
    TextView tvWeather;// 天气

    @BindView(R.id.tv_wind)
    TextView tvWind;// 风力

//    @BindView(R.id.manage_head_icon)
//    SimpleDraweeView mSimpleDraweeView;// 头像

    @BindView(R.id.manage_head_icon)
    CircleImageView circleImageView;// 头像

    private Subscription mWeatherSubscription;// 每隔 30 分钟刷新一次天气

    private boolean isLogin = false;// 是否已登录
    private LoginQRCodeDialog mLoginQRCodeDialog;// 登录 Dialog
    private PermissionListener permissionListener;
//    @Inject
//    MainContract.Presenter mPresenter2;

    @Inject
    ManageContract.Presenter mPresenter1;

    @Inject
    IotTypeContract.Presenter mPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        layoutId = R.layout.activity_main_iot;
        super.onCreate(savedInstanceState);
        mContext = MainIotActivity.this;
//        initViews();
        getPermission();
    }

    private void getPermission() {
        permissionListener = new PermissionListener() {
            @Override
            public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
                PermissionUtil.onRequestPermissionsResult(this, requestCode, permissions, permissionListener);
            }

            @Override
            public void onRequestPermissionSuccess() {
                initViews();
            }

            @Override
            public void onRequestPermissionError() {
                ToastUtil.showCenter(MainIotActivity.this, getString(R.string.giving_system_location_permissions));
            }
        };
        PermissionUtil
                .with(this)
                .permissions(
                        PermissionUtil.PERMISSIONS_GROUP_LOACATION //相机权限
                ).request(permissionListener);
    }

    private void initViews() {
//        SerialManager.getInstance().open();// 打开串口通讯
//        startService();// 启动服务
//        registerBroadcastReceiver();// 注册广播
//        Beta.checkUpgrade();
//        VoiceManager.getInstance().understanderInit(mContext);
//        mPresenter2.subscribe(this);
//        Beta.checkUpgrade(false, false);
        mPresenter1.subscribe(this);// 订阅
        mSubscription = RxBus.getInstance().subscribe(busEvent -> {
            switch (busEvent.getMsgId()) {
                case BusEvent.MSG_LOGOUT_SUCCESS: // 注销登录成功
                case BusEvent.MSG_LOGIN_SUCCESS: // 登录成功
                    mPresenter1.loadUserInfo();
                    break;
                case BusEvent.MSG_LOADED_USER_END: // 用户显示成功
                    if (mPresenter != null && mList != null && mList.size() != 0) {
                        mPresenter.loadDeviceList(mList);
                    }
                    break;
                case BusEvent.MSG_LOGOUT_ACCOUNT_END: // 用户退出登录
//                    if (mPresenter != null && mList != null && mList.size() != 0) {
//                        mList.clear();
//                        mPresenter.loadDeviceList(mList);
//                    }
                    if (mAdapter != null) {
                        mAdapter.initDatas(getList());
                    }
                    break;
            }
        });

        if (mPresenter != null) {
            mPresenter.subscribe(this);// 订阅
            mList = new ArrayList<>();
            mPresenter.initDeviceList(mList);// 初始化设备
        }
//        if (mDeviceListDialog == null) mDeviceListDialog = new DeviceListDialog();
        mTagCloudView.startRevolve();// 开始旋转
        mTagCloudView.setIsDrawLine(true);
//        mSubscription = RxBus.getInstance().subscribe(busEvent -> {
//            switch (busEvent.getMsgId()) {
//                case BusEvent.MSG_HOME_START_SCROLL: // ViewPager 开始滑动
//                    if (mIsVisibleToUser) mTagCloudView.stopRevolve();
//                    break;
//                case BusEvent.MSG_HOME_STOP_SCROLL: // ViewPager 停止滑动
//                    if (mIsVisibleToUser) mTagCloudView.startRevolve();
//                    break;
//            }
//        });

        mTagCloudView.setOnTagClickListener(this);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
//        btn_GO.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent();
//                intent.setAction("android.intent.action.com.launcher.fridge");
//                intent.addCategory(Intent.CATEGORY_DEFAULT);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(intent);
//            }
//        });
    }

    private List<DeviceTypeEntity> getList() {
        List<DeviceTypeEntity> list = new ArrayList<>();
        String[] types = mContext.getResources().getStringArray(R.array.device_type_list);
//        String model = FridgePreference.getInstance().getModel();
        String model = DeviceConfig.MODEL;
        for (int i = 0; i < types.length; i++) {
            DeviceTypeEntity entity = new DeviceTypeEntity();
            entity.setType(types[i]);
            if (!model.equals(AppConstants.MODEL_JD) && i != 1 && i != 5 && i != 6 && i != 9 &&
                    i != 10 && i != 11 && i != 12 && i != 13 && i != 14) { // 是否已上架
                entity.setOnSale(true);
            }
            list.add(entity);
        }
        return list;
    }

//    private void startService() {
//        startService(new Intent(this, AlbumService.class));// 电子相册后台服务
//        startService(new Intent(this, BackgroundService.class));// 后台服务
//        startService(new Intent(this, TimerService.class));// 定时器服务
//    }
//
//    private void stopAllService() {
//        stopService(new Intent(this, AlbumService.class));
//        stopService(new Intent(this, BackgroundService.class));
//        stopService(new Intent(this, TimerService.class));
//    }

//    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            if (intent.getAction() != null) {
//                logUtil.d(TAG, intent.getAction());
//                switch (intent.getAction()) {
//                    case Intent.ACTION_TIME_TICK: // 系统分钟广播
//                        if (!ToolUtil.isServiceWork(mContext, "com.viomi.fridge.vertical.common.service.BackgroundService")) {
//                            logUtil.d(TAG, "restart BackgroundService success");
//                            startService(new Intent(mContext, BackgroundService.class));// 后台服务
//                        }
//                        if (!ToolUtil.isServiceWork(mContext, "com.viomi.fridge.vertical.album.AlbumService")) {
//                            logUtil.d(TAG, "restart AlbumService success");
//                            startService(new Intent(mContext, AlbumService.class));// 电子相册服务
//                        }
//                        if (!ToolUtil.isServiceWork(mContext, "com.viomi.fridge.vertical.timer.service.TimerService")) {
//                            logUtil.d(TAG, "restart TimerService success");
//                            startService(new Intent(mContext, TimerService.class));// 定时器服务
//                        }
//                        break;
//                }
//            }
//        }
//    };

//    /**
//     * 注册广播
//     */
//    private void registerBroadcastReceiver() {
//        IntentFilter intentFilter = new IntentFilter();
//        intentFilter.addAction(Intent.ACTION_TIME_TICK);// 系统每分钟广播
//        registerReceiver(mReceiver, intentFilter);
//    }

    private String hmg, nick, account;


    @OnClick({R.id.manage_user_name, R.id.manage_head_icon})
    public void loginAndQuit() { // 登录或注销登录
        if (mLoginQRCodeDialog == null) mLoginQRCodeDialog = new LoginQRCodeDialog();
        if (mLoginQRCodeDialog.isAdded()) return;
        if (!isLogin) { // 未登录
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            mLoginQRCodeDialog.show(fragmentTransaction, TAG);
        } else {
//            BaseAlertDialog dialog = new BaseAlertDialog(mContext, getResources().getString(R.string.management_logout_message),
//                    getResources().getString(R.string.cancel), getResources().getString(R.string.confirm));
//            dialog.show();
//            dialog.setOnLeftClickListener(dialog::dismiss);
//            dialog.setOnRightClickListener(() -> {
//                dialog.dismiss();
//                if (mPresenter1 != null) mPresenter1.logout();// 退出登录
//            });
            Intent intent = new Intent(mContext, LogoutActivity.class);
            intent.putExtra("hmg", hmg);
            intent.putExtra("nick", nick);
            intent.putExtra("account", account);
            startActivity(intent);
        }
    }

    @Override
    public void refreshView() {
        int count = 0;
        for (int i = 0; i < mList.size(); i++) {
            DeviceTypeEntity entity = mList.get(i);
            count = count + entity.getList().size();
        }
        if (mAdapter == null) {
            mAdapter = new DeviceTypeAdapter(mContext, mList);
            mTagCloudView.setAdapter(mAdapter);
        }
    }

    @Override
    public void showUserInfo(QRCodeBase qrCodeBase) {
        Uri uri;
        if (qrCodeBase != null) { // 已登录
            RxBus.getInstance().post(BusEvent.MSG_LOADED_USER_END);
            isLogin = true;
            if (qrCodeBase.getLoginQRCode().getUserInfo().getHeadImg() == null || qrCodeBase.getLoginQRCode().getUserInfo().getHeadImg().equals("null"))
                uri = Uri.parse("android.resource://" + MyApplication.getContext().getPackageName() + "/" + R.drawable.icon_user_head_default);
            else uri = Uri.parse(qrCodeBase.getLoginQRCode().getUserInfo().getHeadImg());
//            mLoginTextView.setText(getResources().getString(R.string.management_login_quit));
//            mLoginTextView.setBackgroundResource(R.drawable.btn_bg_green_corner_59);
//            mLoginTextView.setTextColor(0xFFFFFFFF);
            nick = qrCodeBase.getLoginQRCode().getUserInfo().getNickname();
            account = qrCodeBase.getLoginQRCode().getUserInfo().getAccount();
            mUserNameTextView.setText(nick);
//            mAccountTextView.setText(String.format(getResources().getString(R.string.management_account), account));
//            mAccountTextView.setVisibility(View.VISIBLE);
        } else {
            RxBus.getInstance().post(BusEvent.MSG_LOGOUT_ACCOUNT_END);
            isLogin = false;
            uri = Uri.parse("android.resource://" + MyApplication.getContext().getPackageName() + "/" + R.drawable.icon_user_head_default);
//            mLoginTextView.setText(getResources().getString(R.string.management_login));
//            mLoginTextView.setBackgroundResource(R.drawable.btn_rim_green_corner_59);
//            mLoginTextView.setTextColor(0xFF44D4A0);

            mUserNameTextView.setText(getResources().getString(R.string.management_no_login));
//            mAccountTextView.setVisibility(View.GONE);
        }

        hmg = uri.toString();
//        ImageRequest request = ImageRequestBuilder
//                .newBuilderWithSource(uri)
//                .setResizeOptions(new ResizeOptions(120, 120))
//                .build();
//        PipelineDraweeController controller = (PipelineDraweeController) Fresco.newDraweeControllerBuilder()
//                .setOldController(mSimpleDraweeView.getController())
//                .setImageRequest(request)
//                .build();
//        mSimpleDraweeView.setController(controller);

        ImageLoadUtil.getInstance().loadUrl(hmg, circleImageView);
    }

    @Override
    public void notifyItemView(int position, String str) {
        for (Tag tag : mTagCloudView.getTagCloud().getTagList()) {
            if (tag.getIdentification() == position) {
                View view;
                DeviceTypeEntity entity = mList.get(position);
                if (entity.getType().equals(MyApplication.getContext().getResources().getString(R.string.iot_device_fridge))) { // 冰箱
                    view = LayoutInflater.from(mContext).inflate(R.layout.view_holder_device_type_2, mTagCloudView, false);
                    // 图标
                    ImageView imageView = (ImageView) view.findViewById(R.id.holder_device_type_2_icon);
                    imageView.setImageResource(DeviceIconUtil.switchIconWithPosition(position));
                    // 设备分类
                    TextView textView = (TextView) view.findViewById(R.id.holder_device_type_2_name);
                    textView.setText(entity.getType());
                    // 背景
                    LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.holder_device_type_2_bg);
                    linearLayout.setBackgroundResource(DeviceIconUtil.switchDeviceBg(position));
                    // 数据
//                    TextView coldTextView = (TextView) view.findViewById(R.id.holder_device_type_fridge_cold);
//                    TextView changeableTextView = (TextView) view.findViewById(R.id.holder_device_type_fridge_changeable);
//                    TextView freezingTextView = (TextView) view.findViewById(R.id.holder_device_type_fridge_freezing);
//                    LinearLayout linearLayout1 = (LinearLayout) view.findViewById(R.id.holder_device_type_changeable_layout);
//                    View lineView = view.findViewById(R.id.holder_device_type_line);

                    LinearLayout linearLayout1 = (LinearLayout) view.findViewById(R.id.ll_bot);
                    setViewHint(linearLayout1);

//                    coldTextView.setTypeface(Typeface.createFromAsset(MyApplication.getContext().getAssets(), "fonts/DINCond-Medium.otf"));
//                    changeableTextView.setTypeface(Typeface.createFromAsset(MyApplication.getContext().getAssets(), "fonts/DINCond-Medium.otf"));
//                    freezingTextView.setTypeface(Typeface.createFromAsset(MyApplication.getContext().getAssets(), "fonts/DINCond-Medium.otf"));
////                    entity.getData()
//                    if (entity.getData() != null) { // 数据不为空
//                        String[] data = entity.getData().split(",");
//                        if (data.length >= 2) {
//                            if (data.length == 2) {
//                                linearLayout1.setVisibility(View.GONE);
//                                lineView.setVisibility(View.GONE);
//                                coldTextView.setText(data[0]);
//                                freezingTextView.setText(data[1]);
//                            } else {
//                                coldTextView.setText(data[0]);
//                                changeableTextView.setText(data[1]);
//                                freezingTextView.setText(data[2]);
//                            }
//                        }
//                    }
                } else {
                    view = LayoutInflater.from(mContext).inflate(R.layout.view_holder_device_type_1, mTagCloudView, false);
                    // 图标
                    ImageView imageView = (ImageView) view.findViewById(R.id.holder_device_type_1_icon);
                    imageView.setImageResource(DeviceIconUtil.switchIconWithPosition(position));
                    // 设备分类
                    TextView typeTextView = (TextView) view.findViewById(R.id.holder_device_type_1_name);
                    typeTextView.setText(entity.getType());
                    // 背景
                    LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.holder_device_type_1_bg);
                    if (entity.getList().size() > 0 && !entity.isExist()) { // 离线
                        linearLayout.setBackgroundResource(R.drawable.icon_device_circle_gray);
                    } else {
                        linearLayout.setBackgroundResource(DeviceIconUtil.switchDeviceBg(position));
                    }
                    // 显示数据
                    TextView dataTextView = (TextView) view.findViewById(R.id.holder_device_type_1_data);
                    if (entity.getList().size() > 0 && entity.isExist()) { // 在线
                        dataTextView.setText(entity.getData() == null ? "" : entity.getData());
                    } else if (entity.getList().size() > 0 && !entity.isExist()) { // 离线
                        dataTextView.setText(MyApplication.getContext().getResources().getString(R.string.iot_device_type_offline));
                    } else {
                        if (entity.getList().size() != 0) {
                            dataTextView.setText(mContext.getResources().getString(R.string.iot_please_look_forward));
                        } else {
                            dataTextView.setText(mContext.getResources().getString(R.string.iot_add_no_txt));
                        }
                    }
//                    else if (entity.getList().size() == 0 && entity.isOnSale()) { // 已上架
//                        dataTextView.setText(MyApplication.getContext().getResources().getString(R.string.iot_buy_confirm));
//                    } else {
//                        dataTextView.setText(MyApplication.getContext().getResources().getString(R.string.iot_please_look_forward));
//                    }
                }
                tag.setView(view);
                view.setOnClickListener(v -> {
                    if (mTagCloudView.getOnTagClickListener() != null) {
                        mTagCloudView.getOnTagClickListener().onItemClick(mTagCloudView, view, position);
                    }
                });
                break;
            }
        }
    }


    private void setViewHint(View view) {
        view.setVisibility(View.INVISIBLE);
    }


    private void showLogin() {
        if (!isLogin) { // 未登录
            if (mLoginQRCodeDialog == null) mLoginQRCodeDialog = new LoginQRCodeDialog();
            if (mLoginQRCodeDialog.isAdded()) return;
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            mLoginQRCodeDialog.show(fragmentTransaction, TAG);
            return;
        }
    }

    @Override
    public void onItemClick(ViewGroup parent, View view, int position) {
//        if (mIsVisibleToUser) mTagCloudView.stopRevolve();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        DeviceTypeEntity entity = mList.get(position);
        if (entity.getType().equals(MyApplication.getContext().getResources().getString(R.string.iot_device_fridge))) {
            startActivity(new Intent(MainIotActivity.this, FridgeActivity.class));
        } else {
            if (!isLogin) { // 未登录
                if (mLoginQRCodeDialog == null) mLoginQRCodeDialog = new LoginQRCodeDialog();
                if (mLoginQRCodeDialog.isAdded()) return;
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                mLoginQRCodeDialog.show(fragmentTransaction, TAG);
                return;
            } else {
                ArrayList<AbstractDevice> list = (ArrayList<AbstractDevice>) entity.getList();
                if (list != null && list.size() != 0) {
                    if (entity.getType().equals(MyApplication.getContext().getResources().getString(R.string.iot_device_washing_machine))) {
                        if (mWashMachineDialog == null)
                            mWashMachineDialog = new WashMachineDialog();
                        if (!mWashMachineDialog.isAdded()) {
                            Bundle bundle = new Bundle();
                            bundle.putParcelableArrayList(mWashMachineDialog.PARAM_LIST, list);
                            mWashMachineDialog.setArguments(bundle);
                            mWashMachineDialog.show(transaction, TAG);
                        }
                    } else if (entity.getType().equals(MyApplication.getContext().getResources().getString(R.string.iot_device_water_purifier))) {
                        if (mPurifierWaterDialog == null)
                            mPurifierWaterDialog = new PurifierWaterDialog();
                        if (!mPurifierWaterDialog.isAdded()) {
                            Bundle bundle = new Bundle();
                            bundle.putParcelableArrayList(mPurifierWaterDialog.PARAM_LIST, list);
                            mPurifierWaterDialog.setArguments(bundle);
                            mPurifierWaterDialog.show(transaction, TAG);
                        }
                    } else if (entity.getType().equals(MyApplication.getContext().getResources().getString(R.string.iot_device_range_hood))) {
                        if (mHoodMachineDialog == null)
                            mHoodMachineDialog = new HoodMachineDialog();
                        if (!mHoodMachineDialog.isAdded()) {
                            Bundle bundle = new Bundle();
                            bundle.putParcelableArrayList(mHoodMachineDialog.PARAM_LIST, list);
                            mHoodMachineDialog.setArguments(bundle);
                            mHoodMachineDialog.show(transaction, TAG);
                        }
                    } else if (entity.getType().equals(MyApplication.getContext().getResources().getString(R.string.iot_device_fan))) {
                        if (fanMachineDialog == null) fanMachineDialog = new FanMachineDialog();
                        if (!fanMachineDialog.isAdded()) {
                            Bundle bundle = new Bundle();
                            bundle.putParcelableArrayList(fanMachineDialog.PARAM_LIST, list);
                            fanMachineDialog.setArguments(bundle);
                            fanMachineDialog.show(transaction, TAG);
                        }
                    } else if (entity.getType().equals(MyApplication.getContext().getResources().getString(R.string.iot_device_dish_washing))) {
                        if (washDishMachineDialog == null)
                            washDishMachineDialog = new WashDishMachineDialog();
                        if (!washDishMachineDialog.isAdded()) {
                            Bundle bundle = new Bundle();
                            bundle.putParcelableArrayList(washDishMachineDialog.PARAM_LIST, list);
                            washDishMachineDialog.setArguments(bundle);
                            washDishMachineDialog.show(transaction, TAG);
                        }
                    } else if (entity.getType().equals(MyApplication.getContext().getResources().getString(R.string.iot_device_sweep_robot))) {
                        if (sweepMachineDialog == null)
                            sweepMachineDialog = new SweepMachineDialog();
                        if (!sweepMachineDialog.isAdded()) {
                            Bundle bundle = new Bundle();
                            bundle.putParcelableArrayList(sweepMachineDialog.PARAM_LIST, list);
                            sweepMachineDialog.setArguments(bundle);
                            sweepMachineDialog.show(transaction, TAG);
                        }
                    }
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
//        if (FridgePreference.getInstance().getModel().equals(AppConstants.MODEL_JD)) { // 京东冰箱
//            Intent intent = new Intent("com.jd.smart.fridge.launcher.onresume.call");
//            sendBroadcast(intent);
//        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        RxBus.getInstance().post(BusEvent.MSG_REMOVE_HOME_MALL);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        stopAllService();
//        unregisterReceiver(mReceiver);
        if (mWeatherSubscription != null) {
            mWeatherSubscription.unsubscribe();
            mWeatherSubscription = null;
        }

//        if (mPresenter2 != null) {
//            mPresenter2.unSubscribe();
//            mPresenter2 = null;
//        }

        if (mPresenter != null) {
            mPresenter.unSubscribe();
            mPresenter = null;
        }
        if (mSubscription != null) {
            mSubscription.unsubscribe();
            mSubscription = null;
        }
//        if (mDeviceListDialog != null && mDeviceListDialog.isAdded()) {
//            mDeviceListDialog.dismiss();
//            mDeviceListDialog = null;
//        }
//        if (mFridgeDialog != null && mFridgeDialog.isAdded()) {
//            mFridgeDialog.dismiss();
//            mFridgeDialog = null;
//        }
        if (mHoodMachineDialog != null && mHoodMachineDialog.isAdded()) {
            mHoodMachineDialog.dismiss();
            mHoodMachineDialog = null;
        }
//        if (mWaterPurifierDialog != null && mWaterPurifierDialog.isAdded()) {
//            mWaterPurifierDialog.dismiss();
//            mWaterPurifierDialog = null;
//        }

        if (mPurifierWaterDialog != null && mPurifierWaterDialog.isAdded()) {
            mPurifierWaterDialog.dismiss();
            mPurifierWaterDialog = null;
        }

//        if (mHeatKettleDialog != null && mHeatKettleDialog.isAdded()) {
//            mHeatKettleDialog.dismiss();
//            mHeatKettleDialog = null;
//        }
//        if (mPLMachineDialog != null && mPLMachineDialog.isAdded()) {
//            mPLMachineDialog.dismiss();
//            mPLMachineDialog = null;
//        }

        if (mWashMachineDialog != null && mWashMachineDialog.isAdded()) {
            mWashMachineDialog.dismiss();
            mWashMachineDialog = null;
        }

//        if (fridgeMachineDialog != null && fridgeMachineDialog.isAdded()) {
//            fridgeMachineDialog.dismiss();
//            fridgeMachineDialog = null;
//        }

        if (fanMachineDialog != null && fanMachineDialog.isAdded()) {
            fanMachineDialog.dismiss();
            fanMachineDialog = null;
        }

        if (washDishMachineDialog != null && washDishMachineDialog.isAdded()) {
            washDishMachineDialog.dismiss();
            washDishMachineDialog = null;
        }

        if (sweepMachineDialog != null && sweepMachineDialog.isAdded()) {
            sweepMachineDialog.dismiss();
            sweepMachineDialog = null;
        }

//        if (mDevicesDialog != null && mDevicesDialog.isAdded()) {
//            mDevicesDialog.dismiss();
//            mDevicesDialog = null;
//        }

        if (mPresenter1 != null) {
            mPresenter1.unSubscribe();
            mPresenter1 = null;
        }
        if (mLoginQRCodeDialog != null && mLoginQRCodeDialog.isAdded()) {
            mLoginQRCodeDialog.dismiss();
            mLoginQRCodeDialog = null;
        }
        if (mAdapter != null) mAdapter = null;
        if (mList != null) {
            mList.clear();
            mList = null;
        }
        mContext = null;
    }

//    @Override
//    public void displayAlbum(List<Uri> list) {
//
//    }

//    @Override
//    public void refreshFridgeUi(DeviceParams params) {
//
//    }

//    @Override
//    public void refreshCity(String city) {
//
//    }
//
//    @Override
//    public void refreshWeather(List<WeatherBean> list) {
//        WeatherBean bean = list.get(0);
//        if (bean != null) {
//            tvQuaulity.setText(bean.getAirQuality());
//            tvHumidity.setText(String.valueOf(bean.getTemp()));
//            tvWeather.setText(bean.getWeather());
//            tvWind.setText(bean.getWind());
//        }
//        Log.i("info", "================WeatherBean:" + list.get(0));
//
//        if (mWeatherSubscription != null) {
//            mWeatherSubscription.unsubscribe();
//            mWeatherSubscription = null;
//        }
//        mWeatherSubscription = Observable.timer(30, TimeUnit.MINUTES)
//                .compose(RxSchedulerUtil.SchedulersTransformer2())
//                .onTerminateDetach()
//                .subscribe(aLong -> {
//                    if (!FridgePreference.getInstance().getCity().equals("") && mPresenter != null) {
//                        mPresenter2.loadWeather(FridgePreference.getInstance().getCity());
//                    }
//                }, throwable -> logUtil.e(TAG, throwable.getMessage()));
//    }

//    @Override
//    public void refreshMessageBoard(List<MessageBoardEntity> list) {
//
//    }
//
//    @Override
//    public void refreshCookbook(CookMenuDetail cookMenuDetail) {
//
//    }
}
