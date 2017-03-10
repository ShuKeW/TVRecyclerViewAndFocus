package com.skw.library.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.skw.library.TVRecyclerView;
import com.skw.library.holder.RVHolder;

import java.util.List;

/**
 * @创建人 weishukai
 * @创建时间 17/1/6 下午6:55
 * @类描述 一句话说明这个类是干什么的
 */

public abstract class RVAdapter<T, V extends RVHolder> extends RecyclerView.Adapter<V> {

    protected String TAG = "RVAdapter";

    protected Context mContext;

    protected LayoutInflater mInflater;

    protected List<T> mDataList;

    private RVAdapter() {
    }

    public RVAdapter(Context context, List<T> mDataList) {
        this.mContext = context;
        this.mDataList = mDataList;
        mInflater = LayoutInflater.from(context);
    }

    public abstract V newViewHolder(ViewGroup parent, int viewType);

    @Override
    public V onCreateViewHolder(ViewGroup parent, int viewType) {
        V holder = newViewHolder(parent, viewType);
        holder.setRecyclerView((RecyclerView) parent);
        return holder;
    }

    @Override
    public void onBindViewHolder(V holder, int position) {
        holder.bindData(getItem(position), position);

    }

    @Override
    public int getItemCount() {
        if (mDataList != null) {
            return mDataList.size();
        }
        return 0;
    }

    public T getItem(int position) {
        if (mDataList != null) {
            return mDataList.get(position);
        }
        return null;
    }

    public void setDataList(List<T> mDataList) {
        this.mDataList = mDataList;
        notifyDataSetChanged();
    }

    public void addDataList(List<T> mDataList) {
        this.mDataList.addAll(mDataList);
        notifyDataSetChanged();
    }

}
