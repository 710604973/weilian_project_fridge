package com.mode.fridge.utils;

import android.util.Log;

import com.mode.fridge.common.GlobalParams;


public class log {
	public static boolean DEBUG = GlobalParams.LOG_DEBUG;
	
	public static void d(String tag, String msg) {
		if(DEBUG){
			Log.d(tag,msg);
		}
	}

	public static void myE(String tag, String msg) {
		if(DEBUG){
			Log.e(tag,msg);
		}
	}
}
