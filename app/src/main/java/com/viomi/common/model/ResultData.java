package com.viomi.common.model;


/**
 * Created by young2 on 2016/9/10.
 */
public class ResultData {

    private static ResultData INSTANCE=null;

    public static ResultData getInstance(){
        synchronized (ResultData.class){
            if(INSTANCE==null){
                synchronized (ResultData.class){
                    if(INSTANCE==null){
                        INSTANCE=new ResultData();
                    }
                }
            }
        }
        return INSTANCE;
    }

    public boolean isSuccess;
    public int errorCode;
    public String errorMsg;
    public Object object;
}
