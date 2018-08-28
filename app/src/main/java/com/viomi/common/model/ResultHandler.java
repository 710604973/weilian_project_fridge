package com.viomi.common.model;

import android.os.Handler;
import android.os.Message;

/**
 * Created by young2 on 2016/9/10.
 */
public class ResultHandler {

    public static  void onFail(Handler handler, int what, int errorCode, String msg){
        if(handler!=null){
            ResultData resultData=ResultData.getInstance();
            resultData.isSuccess=false;
            resultData.errorCode=errorCode;
            resultData.errorMsg=msg;

            Message message=handler.obtainMessage();
            message.obj=resultData;
            message.what=what;
            handler.sendMessage(message);
        }
    }

    public static  void onSuccess(Handler handler, int what, Object object){
        if(handler!=null){
            ResultData resultData=ResultData.getInstance();
            resultData.isSuccess=true;
            resultData.object=object;

            Message message=handler.obtainMessage();
            message.obj=resultData;
            message.what=what;
            handler.sendMessage(message);
        }
    }



}
