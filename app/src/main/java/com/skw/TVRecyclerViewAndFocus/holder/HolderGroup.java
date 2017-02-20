package com.skw.TVRecyclerViewAndFocus.holder;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.skw.TVRecyclerViewAndFocus.R;
import com.skw.TVRecyclerViewAndFocus.model.RecommendModel;
import com.skw.library.holder.RVHolder;

/**
 * @创建人 weishukai
 * @创建时间 17/1/10 下午2:55
 * @类描述 一句话说明这个类是干什么的
 */

public class HolderGroup extends RVHolder<RecommendModel> {

	private FrameLayout	item1, item2, item3, item4, item5;

	private ImageView	bg;

	private TextView	title;

	private ImageView	bg2;

	private TextView	title2;

	private ImageView	bg3;

	private TextView	title3;

	private ImageView	bg4;

	private TextView	title4;

	private ImageView	bg5;

	private TextView	title5;

	public HolderGroup(View itemView, String focusViewType, boolean isFocusScaleAnim, boolean isTranslateAnim) {
		super(itemView, focusViewType, isFocusScaleAnim, isTranslateAnim);
		bg = (ImageView) itemView.findViewById(R.id.recommend_img);
		title = (TextView) itemView.findViewById(R.id.recommend_title);
		bg2 = (ImageView) itemView.findViewById(R.id.recommend_img2);
		title2 = (TextView) itemView.findViewById(R.id.recommend_title2);
		bg3 = (ImageView) itemView.findViewById(R.id.recommend_img3);
		title3 = (TextView) itemView.findViewById(R.id.recommend_title3);
		bg4 = (ImageView) itemView.findViewById(R.id.recommend_img4);
		title4 = (TextView) itemView.findViewById(R.id.recommend_title4);
		bg5 = (ImageView) itemView.findViewById(R.id.recommend_img5);
		title5 = (TextView) itemView.findViewById(R.id.recommend_title5);
		item1 = (FrameLayout) itemView.findViewById(R.id.item1);
		item2 = (FrameLayout) itemView.findViewById(R.id.item2);
		item3 = (FrameLayout) itemView.findViewById(R.id.item3);
		item4 = (FrameLayout) itemView.findViewById(R.id.item4);
		item5 = (FrameLayout) itemView.findViewById(R.id.item5);
		initView(item1, focusViewType, isFocusScaleAnim, isTranslateAnim);
		initView(item2, focusViewType, isFocusScaleAnim, isTranslateAnim);
		initView(item3, focusViewType, isFocusScaleAnim, isTranslateAnim);
		initView(item4, focusViewType, isFocusScaleAnim, isTranslateAnim);
		initView(item5, focusViewType, isFocusScaleAnim, isTranslateAnim);
	}

	@Override public void bindData(RecommendModel model, int position) {
		if (model != null) {
			bg.setBackgroundColor(model.imgResColor);
			title.setText(model.title);
			bg2.setBackgroundColor(model.imgResColor);
			title2.setText(model.title);
			bg3.setBackgroundColor(model.imgResColor);
			title3.setText(model.title);
			bg4.setBackgroundColor(model.imgResColor);
			title4.setText(model.title);
			bg5.setBackgroundColor(model.imgResColor);
			title5.setText(model.title);
		}
	}

	@Override public void onItemClick(View view) {
		Toast.makeText(view.getContext(), "点击：" + getAdapterPosition(), Toast.LENGTH_SHORT).show();
	}

	@Override public void destory() {

	}
}
