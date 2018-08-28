package com.viomi.widget.snowingview;


import android.content.Context;

import java.util.Random;

public class SnowingUtil {
    private static final Random RANDOM = new Random();

    // 转换 dip 为 px
    public static int dipToPx(Context context, float dip) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dip * scale + 0.5f * (dip >= 0 ? 1 : -1));
    }

    public static float nextFloat(float startInclusive) {
        return RANDOM.nextFloat() * startInclusive;
    }

    public static float nextFloat(float startInclusive, float endInclusive) {
        return startInclusive == endInclusive ? startInclusive
                : startInclusive + (endInclusive - startInclusive) * RANDOM.nextFloat();
    }

    public static int nextInt(int startInclusive, int endExclusive) {
        return startInclusive == endExclusive ? startInclusive
                : startInclusive + RANDOM.nextInt(endExclusive - startInclusive);
    }
}
