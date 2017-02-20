package com.skw.library.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.skw.library.R;

/**
 * @创建人 weishukai
 * @创建时间 17/1/6 下午7:07
 * @类描述 一句话说明这个类是干什么的
 */

public abstract class RVHolder<T> extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnFocusChangeListener {

	public RVHolder(View itemView) {
		super(itemView);

	}

	public RVHolder(View itemView, String focusViewType, boolean isFocusScaleAnim, boolean isTranslateAnim) {
		super(itemView);
		if (itemView.isFocusable() && itemView.isFocusableInTouchMode()) {
			initView(itemView, focusViewType, isFocusScaleAnim, isTranslateAnim);
		} else {
			if (itemView instanceof ViewGroup) {
				ViewGroup viewGroup = (ViewGroup) itemView;
				int count = viewGroup.getChildCount();
				View child;
				for (int i = 0; i < count; i++) {
					child = viewGroup.getChildAt(i);
					if (child.isFocusable() && child.isFocusableInTouchMode()) {
						initView(child, focusViewType, isFocusScaleAnim, isTranslateAnim);
					}
				}
			}
		}
	}

	public void initView(View view, String focusViewType, boolean isFocusScaleAnim, boolean isTranslateAnim) {
		view.setOnFocusChangeListener(this);
		view.setOnClickListener(this);
		view.setTag(R.id.focus_type, focusViewType);
		view.setTag(R.id.focus_type_is_scale_anim, isFocusScaleAnim);
		view.setTag(R.id.focus_type_is_translate, isTranslateAnim);
	}

	public abstract void bindData(T model, int position);

	@Override public void onClick(View v) {
		onItemClick(v);
	}

	@Override public void onFocusChange(View v, boolean hasFocus) {
		if (hasFocus) {
			focusIn(v);
		} else {
			focusOut(v);
		}
	}

	public void focusIn(View focusView) {
		// FocusUtil.onFocusIn(focusView);
	}

	public void focusOut(View focusView) {
		// FocusUtil.onFocusOut(focusView);
	}

	public abstract void onItemClick(View view);

	/**
	 * 销毁资源
	 */
	public abstract void destory();

}
