package com.mode.fridge.utils;

import android.widget.ImageView;

import com.bumptech.glide.Glide;

/**
 * 图片加载库封装
 * Created by nanquan on 2018/1/15.
 */

public class ImageLoadUtil {

    private static ImageLoadUtil mInstance;

    public static ImageLoadUtil getInstance() {
        if (mInstance == null) {
            synchronized (ImageLoadUtil.class) {
                if (mInstance == null) {
                    mInstance = new ImageLoadUtil();
                    return mInstance;
                }
            }
        }
        return mInstance;
    }

    public void loadUrl(String url, ImageView imageView) {
        Glide
                .with(imageView)
                .load(url)
                .into(imageView);
    }

    public void loadResource(int resourceId, ImageView imageView) {
        Glide
                .with(imageView)
                .load(resourceId)
                .into(imageView);
    }

    public void clear(ImageView imageView) {
        Glide
                .with(imageView)
                .clear(imageView);
    }

}
