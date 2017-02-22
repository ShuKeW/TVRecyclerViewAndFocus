package com.skw.library;

import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * @创建人 weishukai
 * @创建时间 16/12/9 下午5:46
 * @类描述 焦点在中间的Recyclerview, 不支持移动的焦点效果
 */
public class TVRecyclerViewMiddleFocus extends TVRecyclerView {

	public TVRecyclerViewMiddleFocus(Context context) {
		super(context);
	}

	public TVRecyclerViewMiddleFocus(Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
	}

	public TVRecyclerViewMiddleFocus(Context context, @Nullable AttributeSet attrs, int defStyle) {
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
			dx = rectFocus.left - (getWidth() - rectFocus.width()) / 2;
		} else if (getLayoutManager().canScrollVertically()) {
			dy = rectFocus.top - (getHeight() - rectFocus.height() / 2);
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
