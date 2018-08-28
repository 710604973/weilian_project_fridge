package com.mode.fridge.utils;

/**
 * Created by wenzhenwei on 16-7-1.
 */
public class IpUtils {
    public static String intToIp(int paramInt) {
        return (paramInt & 0xFF) + "." + (0xFF & paramInt >> 8) + "." + (0xFF & paramInt >> 16) + "."
                + (0xFF & paramInt >> 24);
    }
}
