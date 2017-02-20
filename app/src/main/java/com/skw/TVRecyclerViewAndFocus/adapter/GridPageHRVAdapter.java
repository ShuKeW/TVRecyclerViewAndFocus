package com.skw.TVRecyclerViewAndFocus.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.skw.TVRecyclerViewAndFocus.R;
import com.skw.TVRecyclerViewAndFocus.holder.Holder;
import com.skw.TVRecyclerViewAndFocus.holder.HolderGroup;
import com.skw.TVRecyclerViewAndFocus.model.RecommendModel;
import com.skw.library.adapter.RVAdapter;
import com.skw.library.focus.AbsFocusEffectView;
import com.skw.library.holder.RVHolder;

import java.util.List;

/**
 * @创建人 weishukai
 * @创建时间 17/1/10 下午2:55
 * @类描述 一句话说明这个类是干什么的
 */

public class GridPageHRVAdapter extends RVAdapter<RecommendModel, RVHolder> {

	public GridPageHRVAdapter(Context context, List<RecommendModel> mDataList) {
		super(context, mDataList);
	}

	@Override public RVHolder newViewHolder(ViewGroup parent, int viewType) {
		View view = null;
		RVHolder holderLesports = null;
		switch (viewType) {
			case 1:
				view = mInflater.inflate(R.layout.item_grid_page_diff_span, parent, false);
				holderLesports = new Holder(view, AbsFocusEffectView.FocusType.FOCUS_POSTER, true, true);
				break;
			case 2:
				view = mInflater.inflate(R.layout.item_grid_page_group, parent, false);
				holderLesports = new HolderGroup(view, AbsFocusEffectView.FocusType.FOCUS_POSTER, true, true);
				break;
			default:
				view = mInflater.inflate(R.layout.item_grid_page_h, parent, false);
				holderLesports = new Holder(view, AbsFocusEffectView.FocusType.FOCUS_POSTER, true, true);
				break;
		}

		return holderLesports;
	}

	@Override public int getItemViewType(int position) {
		return getItem(position).type;
	}
}
