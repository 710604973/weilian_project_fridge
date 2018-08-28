package com.mediatek.factorymode.serial;

import android.util.Log;

public class jniMultiSerial {
    private static final String TAG = "jniSERIAL";
    public int[] rd_data = new int[32];
    public int[] wr_data = new int[32];
    public static boolean no_serial_lib = false;
    static {
        try {
            System.loadLibrary("fm_multi_serial_jni");
            //no_serial_lib = true;

        } catch(Throwable e) {
            Log.e(TAG,"loadLibrary fm_multi_serial_jni error!");
            e.printStackTrace(System.out);
            no_serial_lib = true;
        }
    }

    public native int exit(int port);

    public native int init(Object obj, int port, int freq, int length, char parity, int stopbits);

    public native int serial_read(Object obj, int port);

    public native int serial_write(Object obj, int port, int len);
}