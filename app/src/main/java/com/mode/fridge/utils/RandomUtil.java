package com.mode.fridge.utils;

import java.util.Random;

/**
 * Created by young2 on 2017/2/27.
 */

public class RandomUtil {
    private static final Random RANDOM = new Random();

    private RandomUtil() {

    }

    public static int nextInt(int startInclusive) {
        return RANDOM.nextInt(startInclusive);
    }

    public static float nextFloat(float startInclusive) {
        return RANDOM.nextFloat() * startInclusive;
    }

    public static int nextInt(int startInclusive, int endExclusive) {
        return startInclusive == endExclusive ? startInclusive
                : startInclusive + RANDOM.nextInt(endExclusive - startInclusive);
    }

    public static float nextFloat(float startInclusive, float endInclusive) {
        return startInclusive == endInclusive ? startInclusive
                : startInclusive + (endInclusive - startInclusive) * RANDOM.nextFloat();
    }
}
