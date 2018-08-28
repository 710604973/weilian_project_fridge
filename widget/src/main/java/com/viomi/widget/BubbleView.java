package com.viomi.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 气泡平移 View
 * Created by William on 2017/5/5.
 */

public class BubbleView extends View {

    //    private final static String TAG = BubbleView.class.getSimpleName();
    private List<Bubble> bubbles = new ArrayList<>();
    private Random random = new Random();
    private int width, height;
    private int time = 30;
    private boolean starting = false;
    private boolean isStop = true;
    private Paint mPaint = new Paint();
    private MyRunnable mRunnable = new MyRunnable(this);
    private MyHandler mHandler = new MyHandler(this);

    public BubbleView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    public BubbleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    public BubbleView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        width = getWidth();
        height = getHeight();

        if (!starting) {
            starting = true;
            mHandler.post(mRunnable);
        }

        if (!isStop) {
            mPaint.setColor(Color.WHITE);
            mPaint.setAlpha(200);     // 透明度
            for (int i = 0; i < bubbles.size(); i++) {
                Bubble bubble = bubbles.get(i);
                if (bubble == null || (Math.abs(bubble.getSpeedX()) < 0.1 && Math.abs(bubble.getSpeedY()) < 0.1)) {
                    bubbles.remove(bubble);
                    i--;
                } else {
                    int j = bubbles.indexOf(bubble);
                    bubble.setX(bubble.getX() + bubble.getSpeedX());
                    bubble.setY(bubble.getY() + bubble.getSpeedY());
                    bubble.setSpeedX((width / 2 - bubble.getX()) / time);
                    bubble.setSpeedY((height / 2 - bubble.getY()) / time);
                    bubbles.set(j, bubble);
                    canvas.drawCircle(bubble.getX(), bubble.getY(),
                            bubble.getRadius(), mPaint);
                }
            }
            postInvalidate();
        }
    }

    @Override
    public void postInvalidate() {
        super.postInvalidate();
    }

    public void setStop(boolean isStop) {
        this.isStop = isStop;
        if (!isStop) postInvalidate();
    }

    public void setTime(int time) {
        this.time = time;
    }

    public void recycle() {
        if (mHandler != null) mHandler.removeCallbacks(mRunnable);
        if (mRunnable != null) mRunnable = null;
        if (bubbles != null) bubbles = null;
    }

    private static class MyHandler extends Handler {

        WeakReference<BubbleView> weakReference;

        MyHandler(BubbleView view) {
            this.weakReference = new WeakReference<>(view);
        }
    }

    private static class MyRunnable implements Runnable {

        WeakReference<BubbleView> weakReference;

        MyRunnable(BubbleView view) {
            this.weakReference = new WeakReference<>(view);
        }

        @Override
        public void run() {
            if (this.weakReference != null) {
                BubbleView view = weakReference.get();

                if (!view.isStop) {
                    Bubble bubble = new Bubble();
                    int radius = view.random.nextInt(12);    // 圆半径
                    while (radius == 0) {
                        radius = view.random.nextInt(12);
                    }
                    int x = view.random.nextInt(view.width);  // 初始 x 坐标
                    int y = view.random.nextInt(view.height); // 初始 y 坐标
                    float speedX = (view.width / 2 - x) / view.time;  // x 轴方向速度
                    float speedY = (view.height / 2 - y) / view.time; // y 轴方向速度

                    bubble.setRadius(radius);
                    bubble.setSpeedX(speedX);
                    bubble.setSpeedY(speedY);
                    bubble.setX(x);
                    bubble.setY(y);
                    view.bubbles.add(bubble);
                    view.mHandler.postDelayed(view.mRunnable, 200);
                } else {
                    view.starting = false;
                }
            }
        }
    }

    private static class Bubble {
        /**
         * 气泡半径
         */
        private int radius;
        /**
         * 上升速度
         */
        private float speedY;
        /**
         * 平移速度
         */
        private float speedX;
        /**
         * 气泡x坐标
         */
        private float x;
        /**
         * 气泡y坐标
         */
        private float y;

        /**
         * @return the radius
         */
        private int getRadius() {
            return radius;
        }

        /**
         * @param radius the radius to set
         */
        private void setRadius(int radius) {
            this.radius = radius;
        }

        /**
         * @return the x
         */
        private float getX() {
            return x;
        }

        /**
         * @param x the x to set
         */
        private void setX(float x) {
            this.x = x;
        }

        /**
         * @return the y
         */
        private float getY() {
            return y;
        }

        /**
         * @param y the y to set
         */
        private void setY(float y) {
            this.y = y;
        }

        /**
         * @return the speedY
         */
        private float getSpeedY() {
            return speedY;
        }

        /**
         * @param speedY the speedY to set
         */
        private void setSpeedY(float speedY) {
            this.speedY = speedY;
        }

        /**
         * @return the speedX
         */
        private float getSpeedX() {
            return speedX;
        }

        /**
         * @param speedX the speedX to set
         */
        private void setSpeedX(float speedX) {
            this.speedX = speedX;
        }
    }

}
