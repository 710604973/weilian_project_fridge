package com.viomi.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.View;

import java.text.DecimalFormat;
import java.util.Calendar;

/**
 * 时钟 View
 * Created by William on 2018/1/24.
 */

public class ClockView extends View {
    private int mCenterX;// 圆心坐标
    private int mCenterY;// 圆心坐标
    private int mRadius;// 圆半径
    private Paint mCenterPaint;// 圆心画笔
    private Paint mCirclePaint;// 圆的画笔
    private Paint mHourMarkPaint;// 时刻度线画笔
    private Paint mMinuteMarkPaint;// 分刻度线画笔
    private Paint mHourPaint;// 时针画笔
    private Paint mMinutePaint;// 分针画笔
    private Paint mSecondPaint;// 秒针画笔
    private Bitmap mHourBitmap, mMinuteBitmap, mSecondBitmap;
    private Canvas mHourCanvas, mMinuteCanvas, mSecondCanvas;
    private int mCircleColor = Color.TRANSPARENT;// 圆颜色
    private int mHourColor = Color.WHITE;// 时针颜色
    private int mMinuteColor = Color.WHITE;// 分针颜色
    private int mSecondColor = 0x66FFFFFF;// 秒针颜色
    private int mHourMarkColor;// 时刻度线颜色
    private int mMinuteMarkColor;// 分刻度线颜色
    private boolean mIsDrawCenterCircle = false;// 是否绘制圆心

    // 获取时间监听
    private OnCurrentTimeListener onCurrentTimeListener;

    public void setOnCurrentTimeListener(OnCurrentTimeListener onCurrentTimeListener) {
        this.onCurrentTimeListener = onCurrentTimeListener;
    }

    public ClockView(Context context) {
        super(context);
        init();
    }

    public ClockView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ClockView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ClockView);
        mCircleColor = a.getColor(R.styleable.ClockView_circle_color, Color.TRANSPARENT);
        mHourColor = a.getColor(R.styleable.ClockView_hour_color, Color.WHITE);
        mMinuteColor = a.getColor(R.styleable.ClockView_minute_color, Color.WHITE);
        mSecondColor = a.getColor(R.styleable.ClockView_second_color, 0x66FFFFFF);
        mMinuteMarkColor = a.getColor(R.styleable.ClockView_minute_mark_color, 0x99FFFFFF);
        mHourMarkColor = a.getColor(R.styleable.ClockView_hour_mark_color, Color.WHITE);
        mIsDrawCenterCircle = a.getBoolean(R.styleable.ClockView_draw_center_circle, true);
        a.recycle();
        init();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        reMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        mCenterX = width / 2;
        mCenterY = height / 2;
        mRadius = Math.min(width, height) / 2;

        // 时针
        mHourBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        mHourCanvas = new Canvas(mHourBitmap);

        // 分针
        mMinuteBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        mMinuteCanvas = new Canvas(mMinuteBitmap);

        // 秒针
        mSecondBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        mSecondCanvas = new Canvas(mSecondBitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 绘制圆
        canvas.drawCircle(mCenterX, mCenterY, mRadius, mCirclePaint);
        // 绘制刻度线
        for (int i = 0; i < 60; i++) {
            int mark_length;
            if (i % 5 == 0) { // 时刻度
                mark_length = 16;
                canvas.drawLine(mCenterX, mCenterY - mRadius, mCenterX, mCenterY - mRadius + mark_length, mHourMarkPaint);
            } else { // 分刻度
                mark_length = 10;
                canvas.drawLine(mCenterX, mCenterY - mRadius, mCenterX, mCenterY - mRadius + mark_length, mMinuteMarkPaint);
            }
            canvas.rotate(6, mCenterX, mCenterY);
        }
        canvas.save();

        Calendar calendar = Calendar.getInstance();
        int hour12 = calendar.get(Calendar.HOUR);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);

        //（方案一）每过一小时（3600 秒）时针添加 30 度，所以每秒时针添加（1/120）度
        //（方案二）每过一小时（60 分钟）时针添加 30 度，所以每分钟时针添加（1/2）度
        mHourCanvas.save();
        // 清空画布
        mHourCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        mHourCanvas.rotate(hour12 * 30 + minute * 0.5f, mCenterX, mCenterY);
        int mHourLineLength = 70;
        mHourCanvas.drawLine(mCenterX, mCenterY, mCenterX, mCenterY - mHourLineLength, mHourPaint);
        mHourCanvas.restore();

        // 每过一分钟（60 秒）分针添加 6 度，所以每秒分针添加（1/10）度；当 minute 加 1 时，正好 second 是 0
        mMinuteCanvas.save();
        // 清空画布
        mMinuteCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        mMinuteCanvas.rotate(minute * 6 + second * 0.1f, mCenterX, mCenterY);
        int mMinuteLineLength = 100;
        mMinuteCanvas.drawLine(mCenterX, mCenterY, mCenterX, mCenterY - mMinuteLineLength, mMinutePaint);
        mMinuteCanvas.restore();

        // 每过一秒旋转 6 度
        mSecondCanvas.save();
        // 清空画布
        mSecondCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        mSecondCanvas.rotate(second * 6, mCenterX, mCenterY);
        int mSecondLineLength = 120;
        mSecondCanvas.drawLine(mCenterX, mCenterY, mCenterX, mCenterY - mSecondLineLength, mSecondPaint);
        mSecondCanvas.restore();

        // 绘制圆心
        if (mIsDrawCenterCircle) canvas.drawCircle(mCenterX, mCenterY, 5, mCenterPaint);

        canvas.drawBitmap(mHourBitmap, 0, 0, null);
        canvas.drawBitmap(mMinuteBitmap, 0, 0, null);
        canvas.drawBitmap(mSecondBitmap, 0, 0, null);

        // 每隔 1s 重新绘制
        postInvalidateDelayed(1000);

        if (onCurrentTimeListener != null) {
            // 小时采用 24 小时制返回
            int h = calendar.get(Calendar.HOUR_OF_DAY);
            String currentTime = intAdd0(h) + ":" + intAdd0(minute) + ":" + intAdd0(second);
            onCurrentTimeListener.currentTime(currentTime);
        }
    }

    /**
     * 初始化
     */
    private void init() {
        mCirclePaint = new Paint();
        mCirclePaint.setAntiAlias(true);
        mCirclePaint.setStyle(Paint.Style.FILL);
        mCirclePaint.setColor(mCircleColor);
        // 时刻度线
        mHourMarkPaint = new Paint();
        mHourMarkPaint.setAntiAlias(true);
        mHourMarkPaint.setStyle(Paint.Style.FILL);
        mHourMarkPaint.setStrokeCap(Paint.Cap.ROUND);
        mHourMarkPaint.setStrokeWidth(2);
        mHourMarkPaint.setColor(mHourMarkColor);
        // 分刻度线
        mMinuteMarkPaint = new Paint();
        mMinuteMarkPaint.setAntiAlias(true);
        mMinuteMarkPaint.setStyle(Paint.Style.FILL);
        mMinuteMarkPaint.setStrokeCap(Paint.Cap.ROUND);
        mMinuteMarkPaint.setStrokeWidth(2);
        mMinuteMarkPaint.setColor(mMinuteMarkColor);
        // 时针
        mHourPaint = new Paint();
        mHourPaint.setAntiAlias(true);
        mHourPaint.setColor(mHourColor);
        mHourPaint.setStyle(Paint.Style.FILL);
        mHourPaint.setStrokeCap(Paint.Cap.ROUND);
        mHourPaint.setStrokeWidth(3);
        // 分针
        mMinutePaint = new Paint();
        mMinutePaint.setAntiAlias(true);
        mMinutePaint.setColor(mMinuteColor);
        mMinutePaint.setStyle(Paint.Style.FILL);
        mMinutePaint.setStrokeCap(Paint.Cap.ROUND);
        mMinutePaint.setStrokeWidth(3);
        // 秒针
        mSecondPaint = new Paint();
        mSecondPaint.setAntiAlias(true);
        mSecondPaint.setColor(mSecondColor);
        mSecondPaint.setStyle(Paint.Style.FILL);
        mSecondPaint.setStrokeCap(Paint.Cap.ROUND);
        mSecondPaint.setStrokeWidth(2);
        // 圆心
        mCenterPaint = new Paint();
        mCenterPaint.setAntiAlias(true);
        mCenterPaint.setColor(0xFFFF7474);
        mSecondPaint.setStyle(Paint.Style.FILL);
    }

    /**
     * 重新设置 view 尺寸
     */
    private void reMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int measureWidth = MeasureSpec.getSize(widthMeasureSpec);
        int measureHeight = MeasureSpec.getSize(heightMeasureSpec);
        int measureWidthMode = MeasureSpec.getMode(widthMeasureSpec);
        int measureHeightMode = MeasureSpec.getMode(heightMeasureSpec);
        int DEFAULT_SIZE = 400;
        if (measureWidthMode == MeasureSpec.AT_MOST
                && measureHeightMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(DEFAULT_SIZE, DEFAULT_SIZE);
        } else if (measureWidthMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(DEFAULT_SIZE, measureHeight);
        } else if (measureHeightMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(measureWidth, DEFAULT_SIZE);
        }
    }

    public interface OnCurrentTimeListener {
        void currentTime(String time);
    }

    /**
     * int 小于 10 的添加 0
     */
    private String intAdd0(int i) {
        DecimalFormat df = new DecimalFormat("00");
        if (i < 10) {
            return df.format(i);
        } else {
            return i + "";
        }
    }
}
