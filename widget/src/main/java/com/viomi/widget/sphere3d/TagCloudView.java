package com.viomi.widget.sphere3d;

/*
 * Copyright © 2016 moxun
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the “Software”),
 * to deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE
 * OR OTHER DEALINGS IN THE SOFTWARE.
 */

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.IntDef;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.viomi.widget.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

public class TagCloudView extends ViewGroup implements Runnable, TagsAdapter.OnDataSetChangeListener {
    private static final float TOUCH_SCALE_FACTOR = .8f;
    private static final float TRACKBALL_SCALE_FACTOR = 10;
    private float speed = 5f;
    private TagCloud mTagCloud;
    private float mAngleX = 0.5f;
    private float mAngleY = 0.5f;
    private float centerX, centerY;
    private float radius;
    private float radiusPercent = 0.9f;
    private float[] darkColor = new float[]{1f, 0f, 0f, 1f};// rgba
    private float[] lightColor = new float[]{0.9412f, 0.7686f, 0.2f, 1f};// rgba
    private List<Float> list_x;
    private List<Float> list_y;
    private Paint mPaint;
    private boolean mIsDrawLine = false;
    private boolean mIsRevolving = false;

    @IntDef({MODE_DISABLE, MODE_DECELERATE, MODE_UNIFORM})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Mode {
    }

    public static final int MODE_DISABLE = 0;
    public static final int MODE_DECELERATE = 1;
    public static final int MODE_UNIFORM = 2;
    public int mode;

    private MarginLayoutParams layoutParams;
    private int minSize;

    private boolean isOnTouch = false;
    private Handler handler = new Handler();

    private TagsAdapter tagsAdapter = new NOPTagsAdapter();
    private OnTagClickListener onTagClickListener;

    public TagCloudView(Context context) {
        super(context);
        init(context, null);
    }

    public TagCloudView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public TagCloudView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        setFocusableInTouchMode(true);
        setWillNotDraw(false);
        mTagCloud = new TagCloud();
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.TagCloudView);

            String m = typedArray.getString(R.styleable.TagCloudView_autoScrollMode);
            mode = Integer.valueOf(m);

//            int light = typedArray.getColor(R.styleable.TagCloudView_lightColor, Color.WHITE);
//            setLightColor(light);

//            int dark = typedArray.getColor(R.styleable.TagCloudView_darkColor, Color.BLACK);
//            setDarkColor(dark);

            float p = typedArray.getFloat(R.styleable.TagCloudView_radiusPercent, radiusPercent);
            setRadiusPercent(p);

//            float s = typedArray.getFloat(R.styleable.TagCloudView_scrollSpeed, 2f);
//            setScrollSpeed(s);

            typedArray.recycle();
        }

        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        Point point = new Point();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            wm.getDefaultDisplay().getSize(point);
        } else {
            point.x = wm.getDefaultDisplay().getWidth();
            point.y = wm.getDefaultDisplay().getHeight();
        }
        int screenWidth = point.x;
        int screenHeight = point.y;
        minSize = screenHeight < screenWidth ? screenHeight : screenWidth;

        // 初始化
        list_x = new ArrayList<>();
        list_y = new ArrayList<>();
        for (int i = 0; i < 16; i++) {
            list_x.add(0f);
            list_y.add(0f);
        }

        mPaint = new Paint();
        mPaint.setAntiAlias(true);// 抗锯齿
        mPaint.setColor(0xFFB6CEEB);// 画笔颜色
        mPaint.setStrokeWidth((float) 1.0);// 像素大小
    }

    public void setAutoScrollMode(@Mode int mode) {
        this.mode = mode;
    }

    @Mode
    public int getAutoScrollMode() {
        return this.mode;
    }

    public final void setAdapter(TagsAdapter adapter) {
        tagsAdapter = adapter;
        tagsAdapter.setOnDataSetChangeListener(this);
        onChange();
    }

    public void setLightColor(int color) {
        float[] argb = new float[4];
        argb[3] = Color.alpha(color) / 1.0f / 0xff;
        argb[0] = Color.red(color) / 1.0f / 0xff;
        argb[1] = Color.green(color) / 1.0f / 0xff;
        argb[2] = Color.blue(color) / 1.0f / 0xff;

        lightColor = argb.clone();
        onChange();
    }

    public void setDarkColor(int color) {
        float[] argb = new float[4];
        argb[3] = Color.alpha(color) / 1.0f / 0xff;
        argb[0] = Color.red(color) / 1.0f / 0xff;
        argb[1] = Color.green(color) / 1.0f / 0xff;
        argb[2] = Color.blue(color) / 1.0f / 0xff;

        darkColor = argb.clone();
        onChange();
    }

    public void setRadiusPercent(float percent) {
        if (percent > 1f || percent < 0f) {
            throw new IllegalArgumentException("percent value not in range 0 to 1");
        } else {
            radiusPercent = percent;
            onChange();
        }
    }

    private void initFromAdapter() {
        this.postDelayed(new Runnable() {
            @Override
            public void run() {
                centerX = (getRight() - getLeft()) / 2;
                centerY = (getBottom() - getTop()) / 2;
                radius = Math.min(centerX * radiusPercent, centerY * radiusPercent);
                mTagCloud.setRadius((int) radius);

                mTagCloud.setTagColorLight(lightColor);// higher color
                mTagCloud.setTagColorDark(darkColor);// lower color

                mTagCloud.clear();
                removeAllViews();
                for (int i = 0; i < tagsAdapter.getCount(); i++) {
                    // binding view to each tag
                    Tag tag = new Tag(tagsAdapter.getPopularity(i));
                    View view = tagsAdapter.getView(getContext(), i, TagCloudView.this);
                    tag.setIdentification(i);// 唯一标识
                    tag.setView(view);
                    mTagCloud.add(tag);
                    addListener(view, i);
                }
                mTagCloud.create(true);
                mTagCloud.setAngleX(mAngleX);
                mTagCloud.setAngleY(mAngleY);
                mTagCloud.update();

                resetChildren();
            }
        }, 0);
    }

    private void addListener(View view, final int position) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
            if (!view.hasOnClickListeners() && onTagClickListener != null) {
                view.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onTagClickListener.onItemClick(TagCloudView.this, v, position);
                    }
                });
            }
        } else {
            if (onTagClickListener != null) {
                view.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onTagClickListener.onItemClick(TagCloudView.this, v, position);
                    }
                });
                Log.e("TagCloudView", "Build version is less than 15, the OnClickListener may be overwritten.");
            }
        }
    }

    public void setScrollSpeed(float scrollSpeed) {
        speed = scrollSpeed;
    }

    private void resetChildren() {
        Log.d("TagCloudView", "resetChildren");
        removeAllViews();
        // 必须保证 getChildAt(i) == mTagCloud.getTagList().get(i)
        for (Tag tag : mTagCloud.getTagList()) {
            addView(tag.getView());
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int contentWidth = MeasureSpec.getSize(widthMeasureSpec);
        int contentHeight = MeasureSpec.getSize(heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        if (layoutParams == null) {
            layoutParams = (MarginLayoutParams) getLayoutParams();
        }

        int dimensionX = widthMode == MeasureSpec.EXACTLY ? contentWidth : minSize - layoutParams.leftMargin - layoutParams.rightMargin;
        int dimensionY = heightMode == MeasureSpec.EXACTLY ? contentHeight : minSize - layoutParams.leftMargin - layoutParams.rightMargin;
        setMeasuredDimension(dimensionX, dimensionY);

        measureChildren(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!mIsDrawLine) return;
        for (int i = 0; i < list_x.size() - 1; i++) {
            if (i == list_x.size() - 2) {
                canvas.drawLine(list_x.get(i), list_y.get(i), list_x.get(0), list_y.get(0), mPaint);
            } else {
                canvas.drawLine(list_x.get(i), list_y.get(i), list_x.get(i + 1), list_y.get(i + 1), mPaint);
            }
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
            handler = null;
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            Tag tag = mTagCloud.get(i);
            if (child != null && child.getVisibility() != GONE) {
                tagsAdapter.onThemeColorChanged(child, tag.getScale());
//                child.setScaleX(tag.getScale());
//                child.setScaleY(tag.getScale());
                int left, top;
                left = (int) (centerX + tag.getLoc2DX()) - child.getMeasuredWidth() / 2;
                top = (int) (centerY + tag.getLoc2DY()) - child.getMeasuredHeight() / 2;

                list_x.set(tag.getIdentification(), (float) (left + child.getMeasuredWidth() / 2));
                list_y.set(tag.getIdentification(), (float) (top + child.getMeasuredHeight() / 2));
                child.layout(left, top, left + child.getMeasuredWidth(), top + child.getMeasuredHeight());
            }
        }
        postInvalidate();
    }

    public void reset() {
        mTagCloud.reset();
        resetChildren();
    }

    @Override
    public boolean onTrackballEvent(MotionEvent e) {
        float x = e.getX();
        float y = e.getY();

        mAngleX = (y) * speed * TRACKBALL_SCALE_FACTOR;
        mAngleY = (-x) * speed * TRACKBALL_SCALE_FACTOR;

        mTagCloud.setAngleX(mAngleX);
        mTagCloud.setAngleY(mAngleY);
        mTagCloud.update();

        resetChildren();
        return true;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        handleTouchEvent(ev);
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        handleTouchEvent(e);
        return true;
    }

    @Override
    public void onChange() {
        initFromAdapter();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        getParent().requestDisallowInterceptTouchEvent(true);// 解决与 ViewPager 冲突
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void run() {
        handler.removeCallbacks(this);
        if (!isOnTouch && mode != MODE_DISABLE) {
            if (mode == MODE_DECELERATE) {
                if (mAngleX > 0.3f) {
                    mAngleX -= 0.2f;
                }
                if (mAngleY > 0.3f) {
                    mAngleY -= 0.2f;
                }
                if (mAngleX < -0.3f) {
                    mAngleX += 0.2f;
                }
                if (mAngleY < -0.3f) {
                    mAngleY += 0.2f;
                }
            }
            processTouch();
        }
        handler.postDelayed(this, 33);
    }

    private float downX, downY;

    private void handleTouchEvent(MotionEvent e) {
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = e.getX();
                downY = e.getY();
                isOnTouch = true;
            case MotionEvent.ACTION_MOVE:
                // rotate elements depending on how far the selection point is from center of cloud
                float dx = e.getX() - downX;
                float dy = e.getY() - downY;
                if (isValidMove(dx, dy)) {
                    mAngleX = (dy / radius) * speed * TOUCH_SCALE_FACTOR;
                    mAngleY = (-dx / radius) * speed * TOUCH_SCALE_FACTOR;
                    processTouch();
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                isOnTouch = false;
                break;
        }
    }

    private boolean isValidMove(float dx, float dy) {
        int minDistance = ViewConfiguration.get(getContext()).getScaledTouchSlop();
        return (Math.abs(dx) > minDistance || Math.abs(dy) > minDistance);
    }

    private void processTouch() {
        if (mTagCloud != null) {
            mTagCloud.setAngleX(mAngleX);
            mTagCloud.setAngleY(mAngleY);
            mTagCloud.update();
        }
        resetChildren();
        if (!mIsRevolving) {
            startRevolve();
            mIsRevolving = true;
        }
    }

    /**
     * 开始旋转
     */
    public void startRevolve() {
        if (handler != null) handler.post(this);
    }

    /**
     * 停止旋转
     */
    public void stopRevolve() {
        if (!mIsRevolving) return;
        if (handler != null) {
            handler.removeCallbacks(this);
            mIsRevolving = false;
        }
    }

    public void setIsDrawLine(boolean isDrawLine) {
        mIsDrawLine = isDrawLine;
    }

    public TagCloud getTagCloud() {
        return mTagCloud;
    }

    public void setOnTagClickListener(OnTagClickListener listener) {
        onTagClickListener = listener;
    }

    public OnTagClickListener getOnTagClickListener() {
        return onTagClickListener;
    }

    public interface OnTagClickListener {
        void onItemClick(ViewGroup parent, View view, int position);
    }
}