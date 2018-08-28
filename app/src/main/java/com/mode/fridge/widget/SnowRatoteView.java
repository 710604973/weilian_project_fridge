package com.mode.fridge.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.mode.fridge.R;


/**
 * Created by young2 on 2017/2/27.
 */

public class SnowRatoteView extends View {

    private Bitmap mBitmap;
    private Paint mBitmapPaint;
    private Matrix mBitmapMatrix;
    private float mRototaDegrees;
    private int mWidth;
    private int mHeight;
    private int defaultSize=160;
    private int mType=0;//0，旋转，1，闪烁
    private int mAlpha=0;
    private int mChangeStyle=0;

    public SnowRatoteView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public SnowRatoteView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public SnowRatoteView(Context context) {
        this(context,null);
    }

    private void init(Context context,AttributeSet attrs){

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ImageRotate);
        int picture_id = a.getResourceId(R.styleable.ImageRotate_iamgeSrc, R.mipmap.fan_rotota);

        mBitmap=BitmapFactory.decodeResource(this.getContext().getResources(),picture_id);
        mBitmapPaint=new Paint(Paint.ANTI_ALIAS_FLAG);
        mBitmapMatrix= new Matrix();

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(mType==0){
            mRototaDegrees+=  13;
            mBitmapPaint.setAlpha(255);
            drawRotateBitmap(canvas,mBitmapMatrix,mBitmapPaint,mBitmap,mRototaDegrees,mWidth/2-mBitmap.getWidth()/2,mHeight/2-mBitmap.getHeight()/2);
            if(mRototaDegrees>=360){
                mRototaDegrees=0;
            }
        }else {
            int offsetX = mBitmap.getWidth() / 2;
            int offsetY = mBitmap.getHeight() / 2;
            if(mChangeStyle==0){
                mAlpha+=8;
            }else {
                mAlpha-=8;
            }

            if(mAlpha>=255){
                mChangeStyle=1;
                mAlpha=255;
            }else if(mAlpha<=0){
                mChangeStyle=0;
                mAlpha=0;
            }
            mBitmapPaint.setAlpha(mAlpha);
            canvas.drawBitmap(mBitmap, mWidth/2-offsetX, mHeight/2-offsetY,mBitmapPaint);

        }

        invalidate();
    }

    /** * 绘制自旋转位图     *
     * @param canvas
     * @param paint
     * @param bitmap     * 位图对象
     * @param rotation   *  旋转度数
     * @param posX    在canvas的位置坐标
     * @param posY
     * */
    private void drawRotateBitmap(Canvas canvas,Matrix matrix, Paint paint, Bitmap bitmap,
            float rotation, float posX, float posY)
    {
        int offsetX = bitmap.getWidth() / 2;
        int offsetY = bitmap.getHeight() / 2;
        matrix.setRotate(rotation,offsetX,offsetY);
        matrix.postTranslate(posX , posY );
        canvas.drawBitmap(bitmap, matrix, paint);
    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec),
                getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec));
        mWidth=getWidth();
        mHeight=getHeight();
    }

    public static int getDefaultSize(int size, int measureSpec) {
        int result = size;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        switch (specMode) {
            case MeasureSpec.UNSPECIFIED:
                result = size;
                break;
            case MeasureSpec.AT_MOST:
            case MeasureSpec.EXACTLY:
                result = specSize;
                break;
        }
        return result;
    }

    /***
     * 设置动画
     * @param id
     * @param type
     */
    public void setImage(int id,int type){
        mType=type;
        mBitmap=BitmapFactory.decodeResource(this.getContext().getResources(),id);
    }

    /***
     * 获取旋转方式
     * @return
     */
    public int getType(){
        return mType;
    }
}
