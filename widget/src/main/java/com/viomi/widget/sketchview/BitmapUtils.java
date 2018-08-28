package com.viomi.widget.sketchview;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.Log;
import android.view.WindowManager;

import java.io.InputStream;

/**
 * Created by TangentLu on 2015/8/19.
 */
public class BitmapUtils {

    public static boolean isLandScreen(Context context) {
        int ori = context.getResources().getConfiguration().orientation;// 获取屏幕方向
        return ori == Configuration.ORIENTATION_LANDSCAPE;
    }

    public static Bitmap decodeSampleBitMapFromFile(Context context, String filePath, float sampleScale) {
        // 先得到 bitmap 的高宽
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        //再用屏幕一半高宽、缩小后的高宽对比，取小值进行缩放
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        int reqWidth = wm.getDefaultDisplay().getWidth();
        int reqHeight = wm.getDefaultDisplay().getWidth();
        int scaleWidth = (int) (options.outWidth * sampleScale);
        int scaleHeight = (int) (options.outHeight * sampleScale);
        reqWidth = Math.min(reqWidth, scaleWidth);
        reqHeight = Math.min(reqHeight, scaleHeight);
        options = sampleBitmapOptions(context, options, reqWidth, reqHeight);
        Bitmap bm = BitmapFactory.decodeFile(filePath, options);
        Log.e("xxx", bm.getByteCount() + "");
        return bm;
    }

    public static Bitmap decodeSampleBitMapFromResource(Context context, int resId, int reqWidth, int reqHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(context.getResources(), resId, options);
        options = sampleBitmapOptions(context, options, reqWidth, reqHeight);
        Bitmap bm = BitmapFactory.decodeResource(context.getResources(), resId, options);
        Log.e("xxx", bm.getByteCount() + "");
        return bm;
    }

    public static Bitmap createBitmapThumbnail(Bitmap bitMap, boolean needRecycle, int newHeight, int newWidth) {
        int width = bitMap.getWidth();
        int height = bitMap.getHeight();
        // 计算缩放比例
        float scale = Math.min((float) newWidth / width, (float) (newHeight) / height);
        // 取得想要缩放的 matrix 参数
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);
        // 得到新的图片
        Bitmap newBitMap = Bitmap.createBitmap(bitMap, 0, 0, width, height, matrix, true);
        if (needRecycle)
            bitMap.recycle();
        return newBitMap;
    }

    public static BitmapFactory.Options sampleBitmapOptions(
            Context context, BitmapFactory.Options options, int reqWidth, int reqHeight) {
        int targetDensity = context.getResources().getDisplayMetrics().densityDpi;
        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        double xSScale = ((double) options.outWidth) / ((double) reqWidth);
        double ySScale = ((double) options.outHeight) / ((double) reqHeight);

        double startScale = xSScale > ySScale ? xSScale : ySScale;

        options.inScaled = true;
        options.inDensity = (int) (targetDensity * startScale);
        options.inTargetDensity = targetDensity;
        options.inJustDecodeBounds = false;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        return options;
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    public static Bitmap getBitmapFromAssets(Context context, String path) {
        InputStream open;
        Bitmap bitmap;
        try {
            open = context.getAssets().open(path);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options = sampleBitmapOptions(context, options, 10, 10);
            bitmap = BitmapFactory.decodeStream(open, null, options);
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @param str         需要转换的文本
     * @param width       当前需要显示区域的宽度（自动适配文字大小）
     * @param maxLineSize 每行最大的字符数
     * @param minLineSize 每行最少的字符数（如设置 3，实际只有两个字符的时候，会显示两个字符，但是字体的大小是适配 3 个字体的大小）
     * @param fontColor   字体颜色
     * @param backColor   图片的背景颜色
     */
    public static Bitmap getBitmapFromText(String str, int width, int maxLineSize, int minLineSize, int fontColor, int backColor) {
        if (width == 0 || maxLineSize == 0) {
            return null;
        }
        if (TextUtils.isEmpty(str)) {
            return null;
        }

        int size = str.length();//字体个数
        int fontSize = 0;//字体大小
        int line = 1;//字体行数
        int oneLineSize;//单行字数
        /**
         * 计算单行字数
         */
        if (size <= minLineSize) {
            oneLineSize = minLineSize;
        } else if (size < maxLineSize) {
            oneLineSize = size;
        } else {
            line = (size - 1) / maxLineSize + 1;
            oneLineSize = maxLineSize;
        }
        fontSize = width * 9 / 10 / oneLineSize;

        /**
         * 字体相关配置
         */
        Typeface font = Typeface.create("宋体", Typeface.BOLD);
        Paint p = new Paint();
        p.setColor(fontColor);
        p.setTypeface(font);
        p.setAntiAlias(true);//去除锯齿
        p.setFilterBitmap(true);//对位图进行滤波处理
        p.setTextSize(fontSize);

        /**
         * 先画背景
         */
        int height = (line) * fontSize + (fontSize / 3);
        Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(bmp);
        canvas.drawColor(backColor);
        /**
         * 一行一行的画文字
         */
        for (int i = 0; i < line; i++) {
            int start = i * oneLineSize;
            int end = (i + 1) * oneLineSize;
            if (end >= size) {
                end = size;
            }
            int x;
            if (size < oneLineSize) {
                // 字数小于最小的行字数，设置居中（如果不需要居中，直接用 else 部分即可）
                x = (width - (size * fontSize)) / 2;
            } else {
                x = (width - (oneLineSize * fontSize)) / 2;
            }
            int top = 0;
            int y = (i + 1) * fontSize + top;
            canvas.drawText(str, start, end, x, y, p);
        }
        return bmp;
    }
}