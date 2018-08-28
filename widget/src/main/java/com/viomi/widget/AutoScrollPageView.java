package com.viomi.widget;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.Scroller;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeController;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.viomi.widget.util.WidgetTool;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * 自动滚动横幅 banner
 * Created by William on 2018/1/25.
 */

public class AutoScrollPageView extends ViewPager {
    private static final String TAG = AutoScrollPageView.class.getSimpleName();
    private static final long DEFAULT_DELAY_TIME = 4000;
    private static final int MSG_AUTO_SCROLL = 0;
    private Context mContext;
    private long delayTime;
    private List<Uri> mPhotoList;// 图片集合
    private ViewHandler mHandler;
    private OnViewPageChangeListener onViewPageChangeListener;
    private OnViewPageClickListener onViewPageClickListener;
    private List<SimpleDraweeView> mImgList;// 图片控件集合
    private boolean mIsScreenSaver = false;// 是否屏保

    public AutoScrollPageView(Context context) {
        this(context, null);
    }

    public AutoScrollPageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        mHandler = new ViewHandler(this);
        mImgList = new ArrayList<>();

        for (int i = 0; i < 4; i++) {
            SimpleDraweeView simpleDraweeView = new SimpleDraweeView(context);
            simpleDraweeView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onViewPageClickListener != null) onViewPageClickListener.onPageClick();
                }
            });
            mImgList.add(simpleDraweeView);
        }
        setSliderTransformDuration(900);
    }

    public void setCustomAdapterDatas(List<Uri> list) {
        this.mPhotoList = list;
        CustomViewPagerAdapter adapter = new CustomViewPagerAdapter(list);
        this.setAdapter(adapter);
        setListener();
    }

    public void setOnViewPageChangeListener(OnViewPageChangeListener onViewPageChangeListener) {
        this.onViewPageChangeListener = onViewPageChangeListener;
    }

    private void setListener() {
        if (onViewPageChangeListener != null) {
            this.addOnPageChangeListener(new OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    onViewPageChangeListener.onPageScrolled(position % mPhotoList.size(), positionOffset, positionOffsetPixels);
                }

                @Override
                public void onPageSelected(int position) {
                    onViewPageChangeListener.onPageSelected(position % mPhotoList.size());
                }

                @Override
                public void onPageScrollStateChanged(int state) {
                    onViewPageChangeListener.onPageScrollStateChanged(state);
                }
            });
        }
    }

    public void setDelayTime(long delayTime) {
        this.delayTime = delayTime;
    }

    public void startAutoScroll() {
        if (delayTime != 0) {
            startAutoScroll(delayTime);
        } else {
            startAutoScroll(DEFAULT_DELAY_TIME);
        }
    }

    /**
     * 开始划
     */
    public void startAutoScroll(long delayTime) {
        this.delayTime = delayTime;
        mHandler.removeMessages(MSG_AUTO_SCROLL);
        mHandler.sendEmptyMessageDelayed(MSG_AUTO_SCROLL, delayTime);
    }

    /**
     * 停止划
     */
    public void stopAutoScroll() {
        mHandler.removeMessages(MSG_AUTO_SCROLL);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_UP:
                startAutoScroll();
                break;
            default:
                stopAutoScroll();
                break;
        }
        return super.onTouchEvent(ev);
    }

    public void setSliderTransformDuration(int duration) {
        try {
            Field mScroller = ViewPager.class.getDeclaredField("mScroller");
            mScroller.setAccessible(true);
            FixedSpeedScroller scroller = new FixedSpeedScroller(mContext, null, duration);
            mScroller.set(this, scroller);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class FixedSpeedScroller extends Scroller {
        private int mDuration = 1000;

        public FixedSpeedScroller(Context context) {
            super(context);
        }

        FixedSpeedScroller(Context context, Interpolator interpolator) {
            super(context, interpolator);
        }

        FixedSpeedScroller(Context context, Interpolator interpolator, int duration) {
            this(context, interpolator);
            mDuration = duration;
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy, int duration) {
            // Ignore received duration, use fixed one instead
            super.startScroll(startX, startY, dx, dy, mDuration);
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy) {
            // Ignore received duration, use fixed one instead
            super.startScroll(startX, startY, dx, dy, mDuration);
        }
    }

    public interface OnViewPageChangeListener {
        void onPageScrolled(int position, float positionOffset, int positionOffsetPixels);

        void onPageSelected(int position);

        void onPageScrollStateChanged(int state);
    }

    public interface OnViewPageClickListener {
        void onPageClick();
    }

    public void setOnViewPageClickListener(OnViewPageClickListener onViewPageClickListener) {
        this.onViewPageClickListener = onViewPageClickListener;
    }

    public void setIsScreenSaver(boolean isScreenSaver) {
        mIsScreenSaver = isScreenSaver;
    }

    private class CustomViewPagerAdapter extends PagerAdapter {
        private List<Uri> list;

        CustomViewPagerAdapter(List<Uri> list) {
            this.list = list;
        }

        @Override
        public int getCount() {
            return Integer.MAX_VALUE;
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            SimpleDraweeView simpleDraweeView = mImgList.get(position % mImgList.size());
            if (!mIsScreenSaver) {
                int width = 392, height = 541;// TODO 采样有待测试观察性能
                ImageRequest request = ImageRequestBuilder
                        .newBuilderWithSource(list.get(position % list.size()))
                        .setResizeOptions(new ResizeOptions(width, height))
                        .build();
                PipelineDraweeController controller = (PipelineDraweeController) Fresco.newDraweeControllerBuilder()
                        .setOldController(simpleDraweeView.getController())
                        .setImageRequest(request)
                        .build();
                simpleDraweeView.setController(controller);
            } else {
                // 先采样
                int[] size = WidgetTool.getImageSize(WidgetTool.getImageAbsolutePath(mContext, list.get(position % list.size())));// 获取图片宽高
                int imageWidth = size[0];// 图片高度
                int imageHeight = size[1];// 图片宽度
                int sampleHeight;// 采样高度
                int sampleWidth;// 采样宽度
                Log.d(TAG, "height:" + imageHeight + ",width:" + imageWidth);
                if (imageHeight >= imageWidth) { // 竖图
                    if (imageHeight > WidgetTool.getScreenHeight(mContext))
                        sampleHeight = WidgetTool.getScreenHeight(mContext);
                    else sampleHeight = imageHeight;
                    sampleHeight = (int) (sampleHeight * 0.8);
                    sampleWidth = (int) ((float) (imageWidth * sampleHeight) / (float) imageHeight);
                    sampleWidth = (int) (sampleWidth * 0.8);
                } else { // 横图
                    if (imageWidth > WidgetTool.getScreenWidth(mContext))
                        sampleWidth = WidgetTool.getScreenWidth(mContext);
                    else sampleWidth = imageWidth;
                    sampleWidth = (int) (sampleWidth * 0.8);
                    sampleHeight = (int) ((float) (imageHeight * sampleWidth) / (float) imageWidth);
                    sampleHeight = (int) (sampleHeight * 0.8);
                }
                Log.d(TAG, "height:" + sampleHeight + ",width:" + sampleWidth);

                ImageRequest request = ImageRequestBuilder
                        .newBuilderWithSource(list.get(position % list.size()))
                        .setResizeOptions(new ResizeOptions(sampleWidth, sampleHeight))
                        .build();
                PipelineDraweeController controller = (PipelineDraweeController) Fresco.newDraweeControllerBuilder()
                        .setOldController(simpleDraweeView.getController())
                        .setImageRequest(request)
                        .build();
                GenericDraweeHierarchy hierarchy = simpleDraweeView.getHierarchy();
                hierarchy.setActualImageScaleType(ScalingUtils.ScaleType.FIT_CENTER);
                // 加载图片
                simpleDraweeView.setHierarchy(hierarchy);
                simpleDraweeView.setController(controller);
            }
            // 确保移除父 View
            ViewGroup parent = (ViewGroup) simpleDraweeView.getParent();
            if (parent != null) {
                parent.removeAllViews();
            }
            container.addView(simpleDraweeView);
            return simpleDraweeView;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView(mImgList.get(position % mImgList.size()));
        }
    }

    private static class ViewHandler extends Handler {
        WeakReference<AutoScrollPageView> weakReference;

        ViewHandler(AutoScrollPageView view) {
            this.weakReference = new WeakReference<>(view);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (this.weakReference != null) {
                AutoScrollPageView view = this.weakReference.get();
                if (view != null) {
                    switch (msg.what) {
                        case MSG_AUTO_SCROLL:
                            view.setCurrentItem(view.getCurrentItem() + 1, true);
                            sendEmptyMessageDelayed(MSG_AUTO_SCROLL, view.delayTime);
                            break;
                    }
                }
            }
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
        super.onDetachedFromWindow();
    }
}