package com.mode.fridge.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mode.fridge.R;
import com.mode.fridge.bean.TCRoomScene;

import java.util.ArrayList;

/**
 * Created by young2 on 2017/3/25.
 */

public class SceneAlternativeAdapter extends RecyclerView.Adapter<SceneAlternativeAdapter.MyViewHolder> implements View. OnClickListener {
    private ArrayList<TCRoomScene> mList;
    private Context mContext;
    private SceneAlternativeAdapter.OnRecyclerViewItemClickListener mOnItemClickListener = null;

    public SceneAlternativeAdapter(Context context, ArrayList<TCRoomScene> list){
        mContext=context;
        mList=list;
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(mContext).inflate(R.layout.item_scene, parent, false);
        SceneAlternativeAdapter.MyViewHolder holder = new SceneAlternativeAdapter.MyViewHolder(view);
        view.setOnClickListener(this);
        return holder;
    }

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.mNameText.setText(mList.get(position).name);
        holder.mIcon.setImageResource(R.mipmap.icon_add);
        holder.itemView.setTag(position);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public void onClick(View view) {
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClick(view,(int)view.getTag());
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder
    {

        private TextView mNameText;
        private ImageView mIcon;
        public MyViewHolder(View view)
        {
            super(view);
            mNameText = (TextView) view.findViewById(R.id.scene_text);
            mIcon = (ImageView) view.findViewById(R.id.icon_add);
        }
    }

    public  interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, int position);
    }

}
