package com.mode.fridge.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mode.fridge.R;
import com.mode.fridge.bean.SerialInfo;
import com.mode.fridge.common.GlobalParams;
import com.mode.fridge.device.AppConfig;
import com.mode.fridge.device.DeviceConfig;

/**
 * Created by young2 on 2017/2/24.
 */

public class FridgeRoomView extends RelativeLayout {
    private TextView mTempView;
    private  SnowRatoteView mAnimationView;
    public FridgeRoomView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context,attrs);
    }

    public FridgeRoomView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public FridgeRoomView(Context context) {
        this(context,null);
    }

    private void init(Context context, AttributeSet attrs){

        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.FridgeRoomText);
        String roomText=typedArray.getString(R.styleable.FridgeRoomText_nameText);
        String animGravity=typedArray.getString(R.styleable.FridgeRoomText_animGravity);
        typedArray.recycle();
        View.inflate(context, R.layout.view_fridge_room,this);

        mAnimationView= (SnowRatoteView) findViewById(R.id.room_set_animation);
        mTempView= (TextView) findViewById(R.id.temp_view);
        mTempView.setTypeface(GlobalParams.getInstance().mDigitalTypeface);
        TextView room= (TextView) findViewById(R.id.room_name);

        room.setText(roomText+" ℃");
        if("up".equals(animGravity)){
            LayoutParams layoutParams= (LayoutParams) mTempView.getLayoutParams();
            layoutParams.topMargin=100;
            mTempView.setLayoutParams(layoutParams);

            //获取组件宽度
            int fridgeSetHeight= 140;
            int fridgeSetWidth= 140;
            LayoutParams fridgeSetParams= new LayoutParams(fridgeSetWidth, fridgeSetHeight);
            fridgeSetParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
            mAnimationView.setLayoutParams(fridgeSetParams);//重构图标位置
        }

    }

    public void setTemp(int temp){

        int mMinTemp=-25;//最低可显示温度，用于速冻时温度低于最大可调范围显示
        if(DeviceConfig.MODEL.equals(AppConfig.VIOMI_FRIDGE_V2)||DeviceConfig.MODEL.equals(AppConfig.VIOMI_FRIDGE_V3)
                ||DeviceConfig.MODEL.equals(AppConfig.VIOMI_FRIDGE_V31)){
            mMinTemp=-24;
        }
        if(temp<mMinTemp){
            temp=mMinTemp;
        }
        if(temp== SerialInfo.ROOM_CLOSE_TEMP){
            mTempView.setText("--");
        }else {
            mTempView.setText(""+temp);
        }
    }

    /***
     * 显示温度和开关
     * @param temp
     * @param enable
     */
    public void setTemp(int temp,boolean enable){

        int mMinTemp=-25;//最低可显示温度，用于速冻时温度低于最大可调范围显示
        if(DeviceConfig.MODEL.equals(AppConfig.VIOMI_FRIDGE_V2)||DeviceConfig.MODEL.equals(AppConfig.VIOMI_FRIDGE_V3)
                ||DeviceConfig.MODEL.equals(AppConfig.VIOMI_FRIDGE_V31)){
            mMinTemp=-24;
        }
        if(temp<mMinTemp){
            temp=mMinTemp;
        }
        if(!enable){
            mTempView.setText("--");
        }else {
            mTempView.setText(""+temp);
        }
    }

    /***
     * 显示动画
     * @param enable
     */
    public void showAnimation(boolean enable,int id,int type){
        if(enable){
            mAnimationView.setImage(id,type);
            mAnimationView.setVisibility(VISIBLE);
        }else {
            mAnimationView.setVisibility(INVISIBLE);
        }
    }

    public int getType(){
        return mAnimationView.getType();
    }

}
