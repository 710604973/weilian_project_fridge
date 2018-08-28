package com.mode.fridge.view.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mode.fridge.R;
import com.mode.fridge.adapter.SceneAlternativeAdapter;
import com.mode.fridge.adapter.SceneChooseAdapter;
import com.mode.fridge.bean.TCRoomScene;
import com.mode.fridge.manager.RoomSceneManager;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by young2 on 2017/3/24.
 */

public class SceneDialog extends BaseDialog{
    private Context mContext;
    private RecyclerView mSceneAlternativeView;
    private RecyclerView mSceneChooseView;
    private GridLayoutManager mSceneAlternativeLayoutManager;
    private GridLayoutManager mSceneChooseLayoutManager;
    private SceneAlternativeAdapter mSceneAlternativeAdapter;
    private SceneChooseAdapter mSceneChooseAdapter;
    private ArrayList<TCRoomScene> mChooseList=new ArrayList<>();
    private ArrayList<TCRoomScene> mAlternativeList=new ArrayList<>();
    private final static int TYPE_ADD=0;
    private final static int TYPE_DELETE=1;
    private OnSaveClickListener mOnSaveClickListener;

    public  interface OnSaveClickListener {
        void onClick();
    }

    public void setSaveClickListener(OnSaveClickListener listener) {
        this.mOnSaveClickListener = listener;
    }

    protected SceneDialog(Context context, boolean cancelable, DialogInterface.OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        mContext=context;
    }

    public SceneDialog(Context context, int themeResId) {
        super(context, themeResId);
        mContext=context;
    }
    public SceneDialog(Context context) {
        super(context);
        mContext=context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.width = 600;
        getWindow().setAttributes(lp);

        mSceneChooseView= (RecyclerView) findViewById(R.id.scene_choose_list);
        mSceneAlternativeView= (RecyclerView) findViewById(R.id.scene_alternative_list);
        mChooseList.addAll(RoomSceneManager.getInstance().getChooseSceneList());
        mAlternativeList.addAll(RoomSceneManager.getInstance().getSceneAllList());

        Iterator<TCRoomScene> iterator=mAlternativeList.iterator();
        while (iterator.hasNext()){
            TCRoomScene tcRoomScene=(TCRoomScene)iterator.next();
            for(int j=0;j<mChooseList.size();j++){
                if(tcRoomScene.name.equals(mChooseList.get(j).name)){
                    iterator.remove();
                    break;
                }
            }
        }

        mSceneAlternativeLayoutManager=new GridLayoutManager(mContext,6,GridLayoutManager.VERTICAL,false);
        mSceneAlternativeView.setLayoutManager(mSceneAlternativeLayoutManager);
        mSceneAlternativeAdapter=new SceneAlternativeAdapter(mContext, mAlternativeList);
        mSceneAlternativeView.setAdapter(mSceneAlternativeAdapter);
        mSceneAlternativeView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.set(10,10,10,10);
            }
        });

        mSceneChooseLayoutManager=new GridLayoutManager(mContext,6,GridLayoutManager.VERTICAL,false);
        mSceneChooseView.setLayoutManager(mSceneChooseLayoutManager);
        mSceneChooseAdapter=new SceneChooseAdapter(mContext, mChooseList);
        mSceneChooseView.setAdapter(mSceneChooseAdapter);
        mSceneChooseView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.set(10,10,10,10);
            }
        });

        mSceneChooseAdapter.setOnItemClickListener(new SceneChooseAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                refreSceneList(position,TYPE_DELETE);
            }
        });
        mSceneAlternativeAdapter.setOnItemClickListener(new SceneAlternativeAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                refreSceneList(position,TYPE_ADD);
            }
        });

        TextView saveView= (TextView) findViewById(R.id.scene_save);
        saveView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RoomSceneManager.getInstance().setChooseSceneList(mChooseList);
                if(mOnSaveClickListener!=null){
                    mOnSaveClickListener.onClick();
                }
                dismiss();
            }
        });
        ImageView closeView= (ImageView) findViewById(R.id.scene_colse);
        closeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mOnSaveClickListener!=null){
                    mOnSaveClickListener.onClick();
                }
                dismiss();
            }
        });

    }

    @Override
    public void setView() {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_scene);
    }

    /***
     * 更新场景
     * @param position
     * @param type {@link #TYPE_ADD {@link #TYPE_DELETE}}
     */
    private void refreSceneList(int position,int type){
        if(type==TYPE_ADD){
            if(mChooseList.size()>=6){
                Toast.makeText(mContext,mContext.getString(R.string.toast_max_scene_arrive),Toast.LENGTH_SHORT).show();
                return;
            }
            TCRoomScene tcRoomScene=mAlternativeList.get(position);
            mChooseList.add(tcRoomScene);
           mAlternativeList.remove(position);
            mSceneChooseAdapter.notifyDataSetChanged();
            mSceneAlternativeAdapter.notifyDataSetChanged();
        }else  if(type==TYPE_DELETE){
            if(mChooseList.size()<=2){
                Toast.makeText(mContext,mContext.getString(R.string.toast_min_scene_arrive),Toast.LENGTH_SHORT).show();
                return;
            }
            TCRoomScene tcRoomScene=mChooseList.get(position);
            mAlternativeList.add(tcRoomScene);
            mChooseList.remove(position);
            mSceneChooseAdapter.notifyDataSetChanged();
            mSceneAlternativeAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void show() {
        super.show();
        mChooseList.clear();
        mAlternativeList.clear();
        mChooseList.addAll(RoomSceneManager.getInstance().getChooseSceneList());
        mAlternativeList.addAll(RoomSceneManager.getInstance().getSceneAllList());

        Iterator<TCRoomScene> iterator=mAlternativeList.iterator();
        while (iterator.hasNext()){
            TCRoomScene tcRoomScene=(TCRoomScene)iterator.next();
            for(int j=0;j<mChooseList.size();j++){
                if(tcRoomScene.name.equals(mChooseList.get(j).name)){
                    iterator.remove();
                    break;
                }
            }
        }
        mSceneChooseAdapter.notifyDataSetChanged();
        mSceneAlternativeAdapter.notifyDataSetChanged();
    }
}
