package com.viomi.widget;

import android.animation.FloatEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;


public class CircleProgressView extends View {

    /**
     * 自定义属性：
     * <p/>
     * 1. 外层圆的颜色 roundColor
     * <p/>
     * 2. 弧形进度圈的颜色 rouncProgressColor
     * <p/>
     * 3. 中间百分比文字的颜色 textColor
     * <p/>
     * 4. 中间百分比文字的大小 textSize
     * <p/>
     * 5. 圆环的宽度（以及作为弧形进度的圆环宽度）
     * <p/>
     * 6. 圆环的风格（Paint.Style.FILL  Paint.Syle.Stroke）
     */

//    private static final String TAG = "MyRoundProcess";

    private int mWidth;
    private int mHeight;
    private String mText;

    private Paint mPaint;
    private Rect bounds;
    private Paint mTextPaint;

    private float progress = 0f;
    private int period = 10;

    // region 自定义属性的值
    private int roundColor;
    private int roundProgressColor;
    private int textColor;
    private float textSize;
    //endregion

    // 画笔的粗细
    private float mStrokeWidth = 0;
    private ValueAnimator mAnimator;
//    private float mLastProgress = -1;

    public CircleProgressView(Context context) {
        this(context, null);
    }

    public CircleProgressView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        // 初始化属性
        initAttrs(context, attrs, defStyleAttr);

        // 初始化点击事件
//        initClickListener();
    }

    /**
     * 初始化属性
     */
    private void initAttrs(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray a = null;
        try {
            a = context.obtainStyledAttributes(attrs, R.styleable.CircleProgressView);

            roundColor = a.getColor(R.styleable.CircleProgressView_roundColor, ContextCompat.getColor(context, R.color.color_filter_green));
            roundProgressColor = a.getColor(R.styleable.CircleProgressView_roundProgressColor, ContextCompat.getColor(context, android.R.color.white));
            textColor = a.getColor(R.styleable.CircleProgressView_textColor, ContextCompat.getColor(context, R.color.color_filter_green));
            textSize = a.getDimension(R.styleable.CircleProgressView_textSize, 38f);

        } finally {
            // 注意，别忘了 recycle
            if (a != null)
                a.recycle();
        }
    }

    /**
     * 当开始布局时候调用
     */
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        // 获取总的宽高
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();

        // 设置圆环画笔
        setupPaint();

        // 设置文字画笔
        setupTextPaint();
    }

    /**
     * 设置圆环画笔
     */
    private void setupPaint() {
        bounds = new Rect();
        // 创建圆环画笔
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(roundColor);
        mPaint.setStyle(Paint.Style.STROKE); // 边框风格
        mPaint.setStrokeWidth(mStrokeWidth);
    }

    /**
     * 设置文字画笔
     */
    private void setupTextPaint() {
        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setColor(textColor);
        mTextPaint.setTextSize(textSize);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mStrokeWidth == 0) mStrokeWidth = mWidth / 11f;

        // 第一步：绘制一个圆环
        mPaint.setStrokeWidth(mStrokeWidth);
        mPaint.setColor(roundColor);

        float cx = mWidth / 2.0f;
        float cy = mHeight / 2.0f;
        float radius = mWidth / 2.0f - mStrokeWidth / 2.0f;
        canvas.drawCircle(cx, cy, radius, mPaint);

        // 第二步：绘制文字
        String text = mText;
        mTextPaint.getTextBounds(text, 0, text.length(), bounds);
        int textWidth = (int) mTextPaint.measureText(text);
        canvas.drawText(text, mWidth / 2 - textWidth / 2f, mHeight / 2 + bounds.height() / 2f, mTextPaint);

        // 第三步：绘制动态进度圆环
        mPaint.setDither(true);
        mPaint.setStrokeJoin(Paint.Join.BEVEL);
        mPaint.setStrokeCap(Paint.Cap.ROUND); //  设置笔触为圆形

        mPaint.setStrokeWidth(mStrokeWidth);
        mPaint.setColor(roundProgressColor);
        RectF oval = new RectF(0 + mStrokeWidth / 2, 0 + mStrokeWidth / 2,
                mWidth - mStrokeWidth / 2, mHeight - mStrokeWidth / 2);

        canvas.drawArc(oval, 0, progress / 100f * 360, false, mPaint);
    }

    /**
     * 重新开启动画
     */
//    public void restartAnimate(int progress, int lastProgress) {
//        if (lastProgress > 0) {
//            // 取消动画
//            cancelAnimate();
//            // 重置进度
//            setProgress(progress);
//            // 重新开启动画
//            mLastProgress = lastProgress;
//
//            mAnimator = ValueAnimator.ofObject(new FloatEvaluator(), progress, lastProgress);
//            // 设置差值器
//            mAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
//            mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//                @Override
//                public void onAnimationUpdate(ValueAnimator animation) {
//                    float value = (float) animation.getAnimatedValue();
//                    setProgress(value);
//                }
//            });
//
//            mAnimator.setDuration((long) (lastProgress * 10));
//            mAnimator.start();
//        }
//    }

    /**
     * 设置当前显示的进度条
     *
     * @param progress: 进度
     */
    public void setProgress(float progress) {
        this.progress = progress;
        if (this.progress < 0) this.progress = 0;

        // 使用 postInvalidate 比 postInvalidate() 好，线程安全
        postInvalidate();
    }


    /**
     * 开始执行动画
     *
     * @param targetProgress 最终到达的进度
     */
    public void runAnimate(float targetProgress) {
        // 运行之前，先取消上一次动画
        cancelAnimate();

//        mLastProgress = targetProgress;

        mAnimator = ValueAnimator.ofObject(new FloatEvaluator(), 0, targetProgress);
        // 设置差值器
        mAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                setProgress(value);
            }
        });

        mAnimator.setDuration((long) (targetProgress * period));
        mAnimator.start();
    }

    /**
     * 取消动画
     */
    public void cancelAnimate() {
        if (mAnimator != null && mAnimator.isRunning()) {
            mAnimator.cancel();
        }
    }

    public void setText(String mText) {
        this.mText = mText;
    }

    public void setRoundColor(int roundColor) {
        this.roundColor = roundColor;
    }

    public void setStrokeWidth(int width) {
        mStrokeWidth = width;
    }

    public void setPeriod(int period) {
        this.period = period;
    }

}
