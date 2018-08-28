package com.mode.fridge.manager;

import android.content.Context;
import android.util.Log;

import com.mode.fridge.MyApplication;
import com.mode.fridge.R;
import com.mode.fridge.bean.TCRoomScene;
import com.mode.fridge.common.GlobalParams;
import com.mode.fridge.device.AppConfig;
import com.mode.fridge.device.DeviceConfig;

import java.util.ArrayList;

/**
 * Created by young2 on 2016/6/3.
 */
public class RoomSceneManager {
    private final static String TAG="UMSceneManager";
    private static RoomSceneManager INSTANCE;
    private Context mContext;
    private ArrayList<TCRoomScene> mSceneList=new ArrayList<>();
    ArrayList<TCRoomScene>  mChooseList=new ArrayList<>();

    public static RoomSceneManager getInstance(){
        synchronized (RoomSceneManager.class){
            if(INSTANCE==null){
                synchronized (RoomSceneManager.class){
                    if(INSTANCE==null){
                        INSTANCE=new RoomSceneManager();
                    }
                }
            }
        }
        return INSTANCE;
    }

    RoomSceneManager(){
        initData(MyApplication.getContext());
    }


    public void initData(Context context){
        mContext= context;
        TCRoomScene roomScene0=new TCRoomScene();
        roomScene0.value=-3;
        roomScene0.desc=mContext.getString(R.string.text_scene_fish_desc);
        roomScene0.name=mContext.getString(R.string.text_scene_fish);

        TCRoomScene roomScene1=new TCRoomScene();
        roomScene1.value=3;
        roomScene1.desc=mContext.getString(R.string.text_scene_tea_desc);
        roomScene1.name=mContext.getString(R.string.text_scene_tea);

        TCRoomScene roomScene2=new TCRoomScene();
        roomScene2.value=4;
        roomScene2.desc=mContext.getString(R.string.text_scene_ice_beer_desc);
        roomScene2.name=mContext.getString(R.string.text_scene_ice_beer);

        TCRoomScene roomScene3=new TCRoomScene();
        roomScene3.value=7;
        roomScene3.desc=mContext.getString(R.string.text_scene_mask_desc);
        roomScene3.name=mContext.getString(R.string.text_scene_mask);

        TCRoomScene roomScene4=new TCRoomScene();
        roomScene4.value=5;
        roomScene4.desc=mContext.getString(R.string.text_scene_unfreeze_desc);
        roomScene4.name=mContext.getString(R.string.text_scene_unfreeze);

        TCRoomScene roomScene5=new TCRoomScene();
        roomScene5.value=-5;
        roomScene5.desc=mContext.getString(R.string.text_scene_rtc_desc);
        roomScene5.name=mContext.getString(R.string.text_scene_rtc);

        TCRoomScene roomScene6=new TCRoomScene();
        roomScene6.value=0;
        roomScene6.desc=mContext.getString(R.string.text_scene_dry_cargo_desc);
        roomScene6.name=mContext.getString(R.string.text_scene_dry_cargo);

        TCRoomScene roomScene7=new TCRoomScene();
        roomScene7.value=-15;
        roomScene7.desc=mContext.getString(R.string.text_scene_ice_cream_desc);
        roomScene7.name=mContext.getString(R.string.text_scene_ice_cream);

        TCRoomScene roomScene8=new TCRoomScene();
        roomScene8.value=4;
        roomScene8.desc=mContext.getString(R.string.text_scene_egg_desc);
        roomScene8.name=mContext.getString(R.string.text_scene_egg);

        TCRoomScene roomScene9=new TCRoomScene();
        roomScene9.value=4;
        roomScene9.desc=mContext.getString(R.string.text_scene_cc_desc);
        roomScene9.name=mContext.getString(R.string.text_scene_cc);

        TCRoomScene roomScene10=new TCRoomScene();
        roomScene10.value=-18;
        roomScene10.desc=mContext.getString(R.string.text_scene_freeze_desc);
        roomScene10.name=mContext.getString(R.string.text_scene_freeze);

        TCRoomScene roomScene11=new TCRoomScene();
        roomScene11.value=-17;
        roomScene11.desc=mContext.getString(R.string.text_scene_beef_desc);
        roomScene11.name=mContext.getString(R.string.text_scene_beef);

        TCRoomScene roomScene12=new TCRoomScene();
        roomScene12.value=7;
        roomScene12.desc=mContext.getString(R.string.text_scene_vegetable_desc);
        roomScene12.name=mContext.getString(R.string.text_scene_vegetable);

        TCRoomScene roomScene13=new TCRoomScene();
        roomScene13.value=5;
        roomScene13.desc=mContext.getString(R.string.text_scene_fruit_desc);
        roomScene13.name=mContext.getString(R.string.text_scene_fruit);

        TCRoomScene roomScene14=new TCRoomScene();
        roomScene14.value=3;
        roomScene14.desc=mContext.getString(R.string.text_scene_ort_desc);
        roomScene14.name=mContext.getString(R.string.text_scene_ort);

        TCRoomScene roomScene15=new TCRoomScene();
        roomScene15.value=7;
        roomScene15.desc=mContext.getString(R.string.text_scene_fresh_beer_desc);
        roomScene15.name=mContext.getString(R.string.text_scene_fresh_beer);

        TCRoomScene roomScene16=new TCRoomScene();
        roomScene16.value=-18;
        roomScene16.desc=mContext.getString(R.string.text_scene_breast_milk_desc);
        roomScene16.name=mContext.getString(R.string.text_scene_breast_milk);


        mSceneList.add(roomScene0);
        if(!DeviceConfig.MODEL.equals(AppConfig.VIOMI_FRIDGE_V2)){
            mSceneList.add(roomScene1);
            mSceneList.add(roomScene2);
            mSceneList.add(roomScene3);
            mSceneList.add(roomScene4);
        }

        mSceneList.add(roomScene5);

        if(!DeviceConfig.MODEL.equals(AppConfig.VIOMI_FRIDGE_V2)){
            mSceneList.add(roomScene6);
        }
        mSceneList.add(roomScene7);
        if(!DeviceConfig.MODEL.equals(AppConfig.VIOMI_FRIDGE_V2)){
            mSceneList.add(roomScene8);
            mSceneList.add(roomScene9);
        }

        mSceneList.add(roomScene10);
        mSceneList.add(roomScene11);
        if(!DeviceConfig.MODEL.equals(AppConfig.VIOMI_FRIDGE_V2)) {
            mSceneList.add(roomScene12);
            mSceneList.add(roomScene13);
            mSceneList.add(roomScene14);
            mSceneList.add(roomScene15);
        }
        mSceneList.add(roomScene16);

        if(DeviceConfig.MODEL.equals(AppConfig.VIOMI_FRIDGE_V2)){
            if(getChooseSceneList().size()==0){
                mChooseList.add(roomScene0);
                mChooseList.add(roomScene5);
                mChooseList.add(roomScene7);
                mChooseList.add(roomScene10);
                mChooseList.add(roomScene11);
                mChooseList.add(roomScene16);
                setChooseSceneList(mChooseList);
            }
        }else {
            if(getChooseSceneList().size()==0){
                mChooseList.add(roomScene0);
                mChooseList.add(roomScene1);
                mChooseList.add(roomScene2);
                mChooseList.add(roomScene3);
                mChooseList.add(roomScene4);
                mChooseList.add(roomScene5);
                setChooseSceneList(mChooseList);
            }
        }
    }


    /***
     * 设置选择场景列表
     * @param list
     */
    public void setChooseSceneList(ArrayList<TCRoomScene> list){
        if(list==null||list.size()==0){
            Log.e(TAG,"set input error!");
            return;
        }
        String str="";
        for(int i=0;i<list.size();i++){
            str+=list.get(i).name+",";
            if(i!=list.size()-1){
                str+=list.get(i).value+";";
            }else {
                str+=list.get(i).value;
            }
        }
        GlobalParams.getInstance().setSceneStr(str);
    }

    /***
     * 获取选中场景列表
     * @return
     */
    public ArrayList<TCRoomScene> getChooseSceneList(){
        mChooseList.clear();
        String jsonStr= GlobalParams.getInstance().getSceneStr();
        if(jsonStr==null||jsonStr.length()==0){
            Log.e(TAG,"getChooseScene,jsonStr null");
            return mChooseList;
        }
        String[] list=jsonStr.split(";");
        if(list==null||list.length==0){
            return mChooseList;
        }
        for(int i=0;i<list.length;i++){
            String sceneStr=list[i];
            String[] scenelist=sceneStr.split(",");
            if(scenelist!=null&&scenelist.length==2){
                TCRoomScene tcRoomScene=new TCRoomScene();
                tcRoomScene.name=scenelist[0];
                int temp=0;
                try{
                    temp=Integer.parseInt(scenelist[1]);
                }catch (Exception e){
                    e.printStackTrace();
                }
                tcRoomScene.value=temp;
                for(int j=0;j<mSceneList.size();j++){
                    if(tcRoomScene.name.equals(mSceneList.get(j).name)){
                        tcRoomScene.desc=mSceneList.get(j).desc;
                        break;
                    }
                }
                mChooseList.add(tcRoomScene);
            }
        }

        for(int i=0;i<list.length;i++){
            Log.d(TAG,"mChooseList:index"+i+",name"+mChooseList.get(i).value);
        }
        return mChooseList;
    }

    /***
     * 设置选择场景
     * @param name
     */
    public void setChooseScene(String name ){
        if(name==null){
            return;
        }
        GlobalParams.getInstance().setSceneChoose(name);
    }

    /***
     * 获取选择场景
     */
    public String getChooseScene( ){

       String name= GlobalParams.getInstance().getSceneChoose();
        return name;
//       for(int i=0;i<mSceneList.size();i++){
//            if(mSceneList.get(i).name.equals(name)){
//                return mSceneList.get(i);
//            }
//       }
//        return null;
    }

//    public int[] getSceneIndexSet(){
//        String jsonStr=GlobalParams.getInstance().getSceneJson();
//        int[] set=new int[mChooseCount];
//        set[0]=0;
//        set[1]=1;
//        set[2]=2;
//        set[3]=3;
//        set[4]=4;
//        set[5]=5;
//        try {
//            JSONObject jsonObject=new JSONObject(jsonStr);
//            set[0]=jsonObject.getInt("index0");
//            set[1]=jsonObject.getInt("index1");
//            set[2]=jsonObject.getInt("index2");
//            set[3]=jsonObject.getInt("index3");
//            set[4]=jsonObject.getInt("index4");
//            set[5]=jsonObject.getInt("index5");
//        } catch (JSONException e) {
//            e.printStackTrace();
//            Log.e(TAG,"getChooseScene error!msg:"+e.getMessage());
//        }
//        return set;
//    }

    public ArrayList<TCRoomScene> getSceneAllList(){

        return mSceneList;
    }

    public ArrayList<TCRoomScene> getSceneChooseList(){

        return mChooseList;
    }

//    public void  close(){
//        mSceneList=null;
//        mChooseList=null;
//        INSTANCE=null;
//    }

}
