package com.skw.library;

import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * @创建人 weishukai
 * @创建时间 16/12/9 下午5:46
 * @类描述 焦点在头部的Recyclerview,
 */
public class TVRecyclerViewHeaderFocus extends TVRecyclerView {

	public TVRecyclerViewHeaderFocus(Context context) {
		super(context);
	}

	public TVRecyclerViewHeaderFocus(Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
	}

	public TVRecyclerViewHeaderFocus(Context context, @Nullable AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override public boolean requestChildRectangleOnScreen(View child, Rect rect, boolean immediate) {
		// return super.requestChildRectangleOnScreen(child, rect, immediate);
		focusItemOffsetX = focusItemOffsetY = 0;
		View focusView = child.findFocus();
		Rect rectFocus = new Rect();
		focusView.getDrawingRect(rectFocus);
		offsetDescendantRectToMyCoords(focusView, rectFocus);
		int dx = 0, dy = 0;
		if (getLayoutManager().canScrollHorizontally()) {
			dx = rectFocus.left;
		} else if (getLayoutManager().canScrollVertically()) {
			dy = rectFocus.top;
		}
		if (immediate) {
			scrollBy(dx, dy);
		} else {
			smoothScrollBy(dx, dy);
		}
		return true;
	}

	@Override public void destory() {
		super.destory();
	}
}
