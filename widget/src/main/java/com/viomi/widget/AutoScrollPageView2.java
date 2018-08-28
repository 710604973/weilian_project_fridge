package com.viomi.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.RelativeLayout;
import android.widget.Scroller;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by William on 2018/4/16.
 */
public class AutoScrollPageView2 extends RelativeLayout {
    private ViewPager mViewPager;
    private int WHAT_AUTO_PLAY = 1000;
    private boolean mIsAutoPlay = true;// 是否自动播放
    private boolean mIsZoomable = true;// 是否缩放
    private int mItemCount;// Page 数量
    private int mAutoPlayDuration = 4000;// 滚动相隔时间
    private int mScrollDuration = 900;// 一次滚动持续时间
    private int mCurrentPosition;// 当前位置

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        startAutoPlay();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopAutoPlay();
    }

    private OnPageItemClickListener onBannerItemClickListener;

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == WHAT_AUTO_PLAY) {
                if (mViewPager != null && mIsAutoPlay) {
                    mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1, true);
                    handler.sendEmptyMessageDelayed(WHAT_AUTO_PLAY, mAutoPlayDuration);
                }
            }
            return false;
        }
    });

    public AutoScrollPageView2(Context context) {
        this(context, null);
    }

    public AutoScrollPageView2(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AutoScrollPageView2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr);
    }

    private void init(AttributeSet attrs, int defStyle) {
        TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.AutoScrollPageView2, defStyle, 0);
        mAutoPlayDuration = array.getInt(R.styleable.AutoScrollPageView2_autoPlayDuration, mAutoPlayDuration);
        mScrollDuration = array.getInt(R.styleable.AutoScrollPageView2_scrollDuration, mScrollDuration);
        mIsAutoPlay = array.getBoolean(R.styleable.AutoScrollPageView2_isAutoPlay, mIsAutoPlay);
        array.recycle();
    }

    // 添加本地图片路径
    public void setViewUris(List<Uri> uris) {
        List<SimpleDraweeView> views = new ArrayList<>();
        mItemCount = uris.size();
        // 主要是解决当 item 为小于 3 个的时候滑动有问题，这里将其拼凑成 3 个以上
        if (mItemCount < 1) { // 当 item 个数 0
            throw new IllegalStateException("item count not equal zero");
        } else if (mItemCount < 2) { // 当 item 个数为 1
            views.add(getImageView(uris.get(0), 0));
            views.add(getImageView(uris.get(0), 0));
            views.add(getImageView(uris.get(0), 0));
        } else if (mItemCount < 3) { // 当 item 个数为 2
            views.add(getImageView(uris.get(0), 0));
            views.add(getImageView(uris.get(1), 1));
            views.add(getImageView(uris.get(0), 0));
            views.add(getImageView(uris.get(1), 1));
        } else {
            for (int i = 0; i < uris.size(); i++) {
                views.add(getImageView(uris.get(i), i));
            }
        }
        setViews(views);
    }

    @NonNull
    private SimpleDraweeView getImageView(Uri uri, final int position) {
        SimpleDraweeView simpleDraweeView = new SimpleDraweeView(getContext());
        simpleDraweeView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onBannerItemClickListener != null) {
                    onBannerItemClickListener.onItemClick(position);
                }
            }
        });
        if (mIsZoomable) {
            int width = 550, height = 550;// TODO 采样有待测试观察性能
            ImageRequest request = ImageRequestBuilder
                    .newBuilderWithSource(uri)
                    .setResizeOptions(new ResizeOptions(width, height))
                    .build();
            PipelineDraweeController controller = (PipelineDraweeController) Fresco.newDraweeControllerBuilder()
                    .setOldController(simpleDraweeView.getController())
                    .setImageRequest(request)
                    .build();
            simpleDraweeView.setController(controller);
        } else {
            simpleDraweeView.setImageURI(uri);
        }
        return simpleDraweeView;
    }

    public void setIsZoomable(boolean isZoomable) {
        this.mIsZoomable = isZoomable;
    }

    public ViewPager getPager() {
        if (mViewPager != null) {
            return mViewPager;
        }
        return null;
    }

    // 添加任意 View 视图
    public void setViews(final List<SimpleDraweeView> views) {
        removeAllViews();
        // 初始化 pager
        mViewPager = new ViewPager(getContext());
        // 添加 viewpager 到 SliderLayout
        addView(mViewPager);
        setSliderTransformDuration(mScrollDuration);
        LoopPagerAdapter pagerAdapter = new LoopPagerAdapter(views);// ViewPager 适配器
        mViewPager.setAdapter(pagerAdapter);

        // 设置当前 item 到 Integer.MAX_VALUE 中间的一个值，看起来像无论是往前滑还是往后滑都是 ok 的
        // 如果不设置，用户往左边滑动的时候已经划不动了
        int targetItemPosition = Integer.MAX_VALUE / 2 - Integer.MAX_VALUE / 2 % mItemCount;
        mCurrentPosition = targetItemPosition;
        mViewPager.setCurrentItem(targetItemPosition);
        mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                mCurrentPosition = position;
            }
        });
        if (mIsAutoPlay) {
            startAutoPlay();
        }
    }

    public void setSliderTransformDuration(int duration) {
        try {
            Field mScroller = ViewPager.class.getDeclaredField("mScroller");
            mScroller.setAccessible(true);
            FixedSpeedScroller scroller = new FixedSpeedScroller(mViewPager.getContext(), null, duration);
            mScroller.set(mViewPager, scroller);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 开始自动轮播
     */
    public void startAutoPlay() {
        stopAutoPlay();// 避免重复消息
        if (mIsAutoPlay) {
            handler.sendEmptyMessageDelayed(WHAT_AUTO_PLAY, mAutoPlayDuration);
        }
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        if (visibility == VISIBLE) {
            startAutoPlay();
        } else {
            stopAutoPlay();
        }
    }

    /**
     * 停止自动轮播
     */
    public void stopAutoPlay() {
        if (mViewPager != null) {
            mViewPager.setCurrentItem(mViewPager.getCurrentItem(), false);
        }
        if (mIsAutoPlay) {
            handler.removeMessages(WHAT_AUTO_PLAY);
            if (mViewPager != null) {
                mViewPager.setCurrentItem(mViewPager.getCurrentItem(), false);
            }
        }
    }

    /**
     * @param autoPlay 是否自动轮播
     */
    public void setAutoPlay(boolean autoPlay) {
        mIsAutoPlay = autoPlay;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                stopAutoPlay();
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                startAutoPlay();
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    public void setOnBannerItemClickListener(OnPageItemClickListener onBannerItemClickListener) {
        this.onBannerItemClickListener = onBannerItemClickListener;
    }

    public interface OnPageItemClickListener {
        void onItemClick(int position);
    }

    private class LoopPagerAdapter extends PagerAdapter {
        private List<SimpleDraweeView> views;

        LoopPagerAdapter(List<SimpleDraweeView> views) {
            this.views = views;
        }

        @Override
        public int getCount() {
            return Integer.MAX_VALUE;
        }

        @Override
        public boolean isViewFromObject(@Nullable View view, @Nullable Object object) {
            return view == object;
        }


        @NonNull
        @Override
        public Object instantiateItem(@Nullable ViewGroup container, int position) {
            if (container != null && views.size() > 0) {
                // position % view.size() 是指虚拟的 position 会在[0，view.size()）之间循环
                View view = views.get(position % views.size());
                ViewGroup parent = (ViewGroup) view.getParent();
                if (parent != null) parent.removeAllViews();
                container.addView(view);
                return view;
            }
            return new View(getContext());
        }

        @Override
        public void destroyItem(@Nullable ViewGroup container, int position, @Nullable Object object) {
            if (container != null) {
                container.removeView(views.get(position % views.size()));
            }
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
}