package com.skw.TVRecyclerViewAndFocus.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.skw.TVRecyclerViewAndFocus.R;
import com.skw.TVRecyclerViewAndFocus.holder.Holder;
import com.skw.TVRecyclerViewAndFocus.model.RecommendModel;
import com.skw.library.adapter.RVAdapter;
import com.skw.library.focus.AbsFocusEffectView;

import java.util.List;

/**
 * @创建人 weishukai
 * @创建时间 17/1/10 下午2:55
 * @类描述 一句话说明这个类是干什么的
 */

public class GridListVRVAdapter extends RVAdapter<RecommendModel, Holder> {

	public GridListVRVAdapter(Context context, List<RecommendModel> mDataList) {
		super(context, mDataList);
	}

	@Override public Holder newViewHolder(ViewGroup parent, int viewType) {
		View view = mInflater.inflate(R.layout.item_grid_list_v, parent, false);
		return new Holder(view, AbsFocusEffectView.FocusType.FOCUS_POSTER, true, true);
	}
}
