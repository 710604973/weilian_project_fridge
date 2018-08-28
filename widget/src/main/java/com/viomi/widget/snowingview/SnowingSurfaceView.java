package com.viomi.widget.snowingview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.viomi.widget.R;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;
import rx.subscriptions.Subscriptions;


/**
 * Github: https://github.com/JeasonWong
 */

public class SnowingSurfaceView extends SurfaceView implements SurfaceHolder.Callback, SensorEventListener {

    Subscription mLooper = Subscriptions.empty();

    private final static long INVALID_TIME = -1;

    private final static int DEFAULT_SNOWFLAKE_BITMAP_VALUE = -1;

    private final static int DEFAULT_SNOWFLAKE_COUNT = 20;

    private final static int MSG_CALCULATE = 233;

    private final static int LOW_VELOCITY_Y = 150;

    private final static int HIGH_VELOCITY_Y = 2 * LOW_VELOCITY_Y;

    private final static float GRAVITATIONAL_ACCELERATION = 9.81F;

    private final static float MIN_OFFSET_X = 15.0F;

    private final static float MAX_OFFSET_X = 20.0F;

    private int mWidth;

    private int mHeight;

    private HandlerThread mCalculatePositionThread;

    private Handler mCalculateHandler;

    private float mSnowFlakeBitmapPivotX;

    private float mSnowFlakeBitmapPivotY;

    private Bitmap mSnowFlakeBitmap;

    private long mLastTimeMillis = INVALID_TIME;

    private Matrix mSnowFlakeMatrix;

    private Paint mSnowFlakePaint;

    private SnowFlake[] mSnowFlakes;

    private SensorManager mSensorManager;

    private Sensor mAccelerometerSensor;

    private float mAccelerationXPercentage;

    private int mLowVelocityY;
    private int mHighVelocityY;
    private int mMinOffsetY;
    private int mMaxOffsetY;
    private int mSnowFlakeCount = DEFAULT_SNOWFLAKE_COUNT;
    private SurfaceHolder mHolder;

    public SnowingSurfaceView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SnowingSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(attrs);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        createSnowFlakes();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();
        ;
        draw();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        stopFall();
    }

    private void draw() {

        Canvas canvas = null;
        try {
            canvas = mHolder.lockCanvas();
            if (canvas != null) {

                canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                if (mSnowFlakes == null) {
                    return;
                }
                for (SnowFlake snowFlake : mSnowFlakes) {
                    mSnowFlakeMatrix.setTranslate(0, 0);
                    if (snowFlake != null) {
                        mSnowFlakeMatrix.postScale(snowFlake.getScale(), snowFlake.getScale(), mSnowFlakeBitmapPivotX,
                                mSnowFlakeBitmapPivotY);
                        mSnowFlakeMatrix.postTranslate(snowFlake.getPositionX(), snowFlake.getPositionY());
                        mSnowFlakePaint.setColor(snowFlake.getTransparency());
                        canvas.drawBitmap(mSnowFlakeBitmap, mSnowFlakeMatrix, mSnowFlakePaint);
                    }
                }
                mCalculateHandler.sendEmptyMessage(MSG_CALCULATE);
            }
        } catch (Exception e) {
            Log.e("SnowingSurfaceView", "lockCanvas fail!msg=" + e.getMessage());
            e.printStackTrace();
        } finally {
            if (canvas != null) {
                mHolder.unlockCanvasAndPost(canvas);
            }
        }
    }

    private void initView(AttributeSet attrs) {
        TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.SnowingView);
        applyAttrsFromXML(array);
        array.recycle();

        initSensorManager();
        initCalculateThread();
        initCalculateHandler();
        initSnowFlakeMatrix();
        initSnowFlakePaint();

        mLowVelocityY = SnowingUtil.dipToPx(getContext(), LOW_VELOCITY_Y);
        mHighVelocityY = SnowingUtil.dipToPx(getContext(), HIGH_VELOCITY_Y);
        mMinOffsetY = SnowingUtil.dipToPx(getContext(), MIN_OFFSET_X);
        mMaxOffsetY = SnowingUtil.dipToPx(getContext(), MAX_OFFSET_X);

        mHolder = getHolder();
        setZOrderOnTop(true);
        getHolder().setFormat(PixelFormat.TRANSLUCENT);
        mHolder.addCallback(this);
        setBackgroundColor(Color.TRANSPARENT);
    }

    /**
     * 从XML文件中读取自定义的字段并赋值给成员
     *
     * @param array TypedArray
     */
    private void applyAttrsFromXML(TypedArray array) {
        mSnowFlakeBitmap = BitmapFactory.decodeResource(getResources(),
                array.getResourceId(R.styleable.SnowingView_src, R.drawable.icon_snowflake));
        mSnowFlakeBitmapPivotX = mSnowFlakeBitmap.getWidth() / 2.0F;
        mSnowFlakeBitmapPivotY = mSnowFlakeBitmap.getHeight() / 2.0F;
        mSnowFlakeCount = (int) array.getInt(R.styleable.SnowingView_snowCount, DEFAULT_SNOWFLAKE_COUNT);
    }

    /**
     * 初始化传感器
     */
    private void initSensorManager() {

        if (isInEditMode()) {
            return;
        }

        mSensorManager = (SensorManager) getContext().getSystemService(Context.SENSOR_SERVICE);
        mAccelerometerSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    /**
     * 初始化雪花矩阵
     */
    private void initSnowFlakeMatrix() {
        mSnowFlakeMatrix = new Matrix();
    }

    /**
     * 初始化雪花画笔
     */
    private void initSnowFlakePaint() {
        mSnowFlakePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    /**
     * 初始化工作线程
     */
    private void initCalculateThread() {
        mCalculatePositionThread = new HandlerThread("calculate_thread");
        mCalculatePositionThread.start();
    }

    /**
     * 初始化Handler
     */
    private void initCalculateHandler() {

        mCalculateHandler = new Handler(mCalculatePositionThread.getLooper()) {

            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                long currentTimeMillis = System.currentTimeMillis();

                if (mLastTimeMillis != INVALID_TIME) {

                    float deltaTime = (currentTimeMillis - mLastTimeMillis) / 1000.0F;

                    for (SnowFlake snowFlake : mSnowFlakes) {
                        if (snowFlake != null) {
                            float x = snowFlake.getPositionX() + randomOffsetX();
                            float y = snowFlake.getPositionY() + snowFlake.getVelocityY() * deltaTime;

                            if (outOfRange(x, y)) {
                                snowFlake.setPositionX(randomPositionX());
                                snowFlake.setPositionY(resetPositionY());
                            } else {
                                snowFlake.setPositionX(x);
                                snowFlake.setPositionY(y);
                            }
                        }

                    }
                }

                mLastTimeMillis = currentTimeMillis;
            }
        };
    }

    /**
     * 创建雪花数组
     */
    private void createSnowFlakes() {

        mSnowFlakes = new SnowFlake[mSnowFlakeCount];

        for (int index = 0; index < mSnowFlakes.length; index++) {

            SnowFlake snowFlake = new SnowFlake.Builder().setPositionX(randomPositionX())
                    .setPositionY(randomPositionY())
                    .setVelocityY(randomVelocityY())
                    .setTransparency(randomTransparency())
                    .setScale(randomScale())
                    .create();

            mSnowFlakes[index] = snowFlake;
        }
    }

    /**
     * 随机可能的X坐标
     *
     * @return 雪花的X坐标
     */
    private float randomPositionX() {
        return SnowingUtil.nextFloat(mWidth + 2 * mSnowFlakeBitmap.getWidth())
                - mSnowFlakeBitmap.getWidth();
    }

    /**
     * 随机可能的Y坐标
     *
     * @return 雪花的Y坐标
     */
    private float randomPositionY() {
        return SnowingUtil.nextFloat(mHeight + 2 * mSnowFlakeBitmap.getHeight())
                - mSnowFlakeBitmap.getHeight();
    }

    /**
     * 将雪花的Y坐标重置
     *
     * @return 雪花的Y坐标
     */
    private float resetPositionY() {
        return -mSnowFlakeBitmap.getHeight();
    }

    /**
     * 随机雪花在Y轴方向上的速度(2dp/s-4dp/s)
     *
     * @return y轴方向上的速度
     */
    private float randomVelocityY() {
        return SnowingUtil.nextFloat(mLowVelocityY, mHighVelocityY);
    }

    /**
     * 随机雪花的透明度
     *
     * @return 雪花的透明度
     */
    private int randomTransparency() {
        return SnowingUtil.nextInt(10, 255) << 24;
    }

    /**
     * 随机雪花的缩放比例
     *
     * @return 雪花的缩放比
     */
    private float randomScale() {
        return SnowingUtil.nextFloat(0.5F, 1.2F);
    }

    /**
     * 随机X轴的偏移量
     *
     * @return x轴上的偏移量
     */
    private float randomOffsetX() {
        return SnowingUtil.nextFloat(mMinOffsetY, mMaxOffsetY) * -mAccelerationXPercentage;
    }

    /**
     * 是否超出View的范围
     *
     * @return true表示超出范围
     */
    private boolean outOfRange(float x, float y) {

        if (x < -mSnowFlakeBitmap.getWidth() || x > mWidth + mSnowFlakeBitmap.getWidth()) {
            return true;
        }

        if (y > mHeight + mSnowFlakeBitmap.getHeight()) {
            return true;
        }

        return false;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float accelerationX = event.values[SensorManager.DATA_X];
        mAccelerationXPercentage = accelerationX / GRAVITATIONAL_ACCELERATION;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    /**
     * 通知HandlerThread停止执行
     */
    private void notifyCalculateThreadStop() {
        mCalculateHandler.removeMessages(MSG_CALCULATE);
    }

    private void startLooper() {
        mLooper.unsubscribe();
        mLooper = Observable.interval(1, TimeUnit.MILLISECONDS)
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        draw();
                    }
                });
    }

    private void stopLooper() {
        mLooper.unsubscribe();
    }

    /**
     * 开始下雪动画
     */
    public void startFall() {
        setVisibility(VISIBLE);
        mSensorManager.registerListener(this, mAccelerometerSensor, SensorManager.SENSOR_DELAY_GAME);
        startLooper();
    }

    /**
     * 停止下雪动画
     */
    public void stopFall() {
        setVisibility(GONE);
        mSensorManager.unregisterListener(this);
        notifyCalculateThreadStop();
        stopLooper();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopFall();
        if (mCalculatePositionThread != null) {
            mCalculatePositionThread.quit();
            mCalculatePositionThread = null;
        }
        if (mSnowFlakeBitmap != null) {
            mSnowFlakeBitmap.recycle();
            mSnowFlakeBitmap = null;
        }
    }
}