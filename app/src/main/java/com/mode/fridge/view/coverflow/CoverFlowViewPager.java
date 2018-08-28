package com.mode.fridge.view.coverflow;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import com.mode.fridge.R;

import java.util.List;

public class CoverFlowViewPager extends RelativeLayout implements OnPageSelectListener {
    private ViewPager mViewPager;
//        private CoverFlowAdapter mAdapter;
    private OnPageSelectListener onPageSelectListener;
    private Context mContext;
    private PagerAdapter mAdapter;

    public void setOnPageSelectListener(OnPageSelectListener listener) {
        onPageSelectListener = listener;
    }

    /**
     * 子元素的集合
     */
    private List<View> mViewList;

    public CoverFlowViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        inflate(context, R.layout.widget_cover_flow, this);
        mViewPager = (ViewPager) findViewById(R.id.vp_conver_flow);
//        initViewPager();
//        initTouch();
    }

    /**
     * 初始化方法
     */
    public void initViewPager(PagerAdapter adapter) {
        this.mAdapter=adapter;
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.setPageTransformer(true, new ScalePagerTransformer());
        //设置Pager之间的间距 30)
        mViewPager.setPageMargin(dp2px(mContext, 10));
//        mAdapter = new CoverFlowAdapter(mContext);
//        mAdapter.setOnPageSelectListener(this);
        mViewPager.setAdapter(mAdapter);
        initTouch();
    }

    private void initTouch() {
        //这里要把父类的touch事件传给子类，不然边上的会滑不动
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return mViewPager.dispatchTouchEvent(event);
            }
        });
//        mViewPager.addOnPageChangeListener(mAdapter);
    }

    public void setCurrentPosition(int position){
        mViewPager.setCurrentItem(position);
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

//    public void setType(int type) {
//        mAdapter.setType(type);
//    }

//    public void setDatas(List<AbstractDevice> datas) {
//        setVisibility(datas.size() > 0 ? VISIBLE : GONE);
//        mAdapter.setDatas(datas);
//        mViewPager.setCurrentItem(mAdapter.getCount() > 0 ? 1 : 0, true);
//    }
//
//    public void setSale(boolean flag) {
//        mAdapter.setSale(flag);
//    }
//
//    public void refreshItemView() {
//        mAdapter.refreshItemView();
//    }

    public int getCurrentDisplayItem() {
        if (mViewPager != null) {
            return mViewPager.getCurrentItem();
        }
        return 0;
    }

    @Override
    public void select(int position) {
        // 回调选择的接口
        if (onPageSelectListener != null) {
            onPageSelectListener.select(position);
        }
    }
}
