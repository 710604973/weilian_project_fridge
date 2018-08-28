package com.viomi.widget.sketchview;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.RectF;

/**
 * Created by William on 2018/5/2.
 */
public class TextRecord {
    public Bitmap bitmap;// 图形
    public String text;// 文字
    public Matrix matrix;// 图形
    public RectF photoRectSrc = new RectF();
    public float scaleMax = 3;
}
