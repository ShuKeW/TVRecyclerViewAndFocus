package com.skw.library.decoration;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.skw.library.utils.RecyclerViewUtil;

/**
 * @创建人 weishukai
 * @创建时间 17/1/8 上午10:46
 * @类描述 recyclerview使用GridLayoutManager的时候绘制分隔线
 *      <p>
 *      发现：如果是横向的grid，然后把item的height给为match_parent,那么本类会自动不绘制底部的分隔，那么底部的item的height会比其他的高，因为每个item高度为view高度加上divider的高度，最底部没有分隔，那么view就会高一些，故加上底部的偏移
 */

public class DividerGridItemDecoration extends RecyclerView.ItemDecoration {

	private Drawable	mDivider;

	private int			mDividerHSpacing;

	private int			mDividerVSpacing;

	private Drawable	mLastDivider;

	private int			mLastDividerSize;

	public DividerGridItemDecoration() {
		set(new ColorDrawable(Color.TRANSPARENT), mDividerHSpacing, mDividerVSpacing, new ColorDrawable(Color.TRANSPARENT), mLastDividerSize);
	}

	public DividerGridItemDecoration(int horizaontalSpacing, int verticalSpacing, int lastDividerSize) {
		set(new ColorDrawable(Color.TRANSPARENT), horizaontalSpacing, verticalSpacing, new ColorDrawable(Color.TRANSPARENT), lastDividerSize);
	}

	public DividerGridItemDecoration(Drawable divider, int horizaontalSpacing, int verticalSpacing, Drawable lastDivider, int lastDividerSize) {
		set(divider, horizaontalSpacing, verticalSpacing, lastDivider, lastDividerSize);
	}

	public DividerGridItemDecoration(@ColorInt int dividerColor, int horizaontalSpacing, int verticalSpacing, @ColorInt int lastDividerColor, int lastDividerSize) {
		set(new ColorDrawable(dividerColor), horizaontalSpacing, verticalSpacing, new ColorDrawable(lastDividerColor), lastDividerSize);
	}

	/**
	 * @param divider
	 * @param horizaontalSpacing
	 *            横向间距
	 * @param verticalSpacing
	 *            纵向间距
	 * @param lastDivider
	 * @param lastDividerSize
	 *            最后一行的后边距离，大于0才会生效
	 */
	private void set(Drawable divider, int horizaontalSpacing, int verticalSpacing, Drawable lastDivider, int lastDividerSize) {
		this.mDivider = divider;
		this.mDividerHSpacing = horizaontalSpacing;
		this.mDividerVSpacing = verticalSpacing;
		this.mLastDivider = lastDivider;
		this.mLastDividerSize = lastDividerSize;
	}

	@Override public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
		super.onDraw(c, parent, state);
		if (mDivider == null) {
			return;
		}
		if (mDividerHSpacing > 0) {
			drawHorizontalSpacing(c, parent);
		}
		if (mDividerVSpacing > 0) {
			drawVerticalSpacing(c, parent);
		}
		if (mLastDividerSize > 0) {
			drawLastSpacing(c, parent);
		}
	}

	/**
	 * 绘制横向间距,不包括最后一列
	 *
	 * @param c
	 * @param parent
	 */
	private void drawHorizontalSpacing(Canvas c, RecyclerView parent) {
		int childCount = parent.getChildCount();
		for (int i = 0; i < childCount; i++) {
			final View child = parent.getChildAt(i);
			// 前边不用考虑是否是最后一列
			if (i < childCount - ((GridLayoutManager) parent.getLayoutManager()).getSpanCount()
					|| (parent.getLayoutManager().canScrollVertically() || !RecyclerViewUtil.isLastGridRaw(parent, child))) {
				drawHorizontal(c, child, mDivider, mDividerHSpacing);
			}
		}
	}

	private void drawHorizontal(Canvas c, View child, Drawable divider, int spacing) {
		final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
		final int left = child.getRight() + params.rightMargin;
		final int right = left + spacing;
		final int top = child.getTop() - params.topMargin;
		final int bottom = child.getBottom() + params.bottomMargin;
		divider.setBounds(left, top, right, bottom);
		divider.draw(c);
	}

	/**
	 * 绘制纵向间距，不包括最后一行
	 *
	 * @param c
	 * @param parent
	 */
	private void drawVerticalSpacing(Canvas c, RecyclerView parent) {
		int childCount = parent.getChildCount();
		for (int i = 0; i < childCount; i++) {
			final View child = parent.getChildAt(i);
			// 前边不用考虑是否是最后一行
			if (i < childCount - ((GridLayoutManager) parent.getLayoutManager()).getSpanCount()
					|| (parent.getLayoutManager().canScrollHorizontally() || !RecyclerViewUtil.isLastGridRaw(parent, child))) {
				drawVertical(c, child, mDivider, mDividerVSpacing);
			}
		}
	}

	private void drawVertical(Canvas c, View child, Drawable divider, int spacing) {
		final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
		final int left = child.getLeft() - params.leftMargin;
		final int right = child.getRight() + params.rightMargin;
		final int top = child.getBottom() + params.bottomMargin;
		final int bottom = top + spacing;
		divider.setBounds(left, top, right, bottom);
		divider.draw(c);
	}

	/**
	 * 绘制最后
	 *
	 * @param c
	 * @param parent
	 */
	private void drawLastSpacing(Canvas c, RecyclerView parent) {
		int childCount = parent.getChildCount();
		int i = childCount - ((GridLayoutManager) parent.getLayoutManager()).getSpanCount();
		for (; i < childCount; i++) {
			final View child = parent.getChildAt(i);
			if (RecyclerViewUtil.isLastGridRaw(parent, child)) {
				if (parent.getLayoutManager().canScrollHorizontally()) {
					drawHorizontal(c, child, mLastDivider, mLastDividerSize);
				} else if (parent.getLayoutManager().canScrollVertically()) {
					drawVertical(c, child, mLastDivider, mLastDividerSize);
				}
			}
		}

	}

	@Override public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
		super.getItemOffsets(outRect, view, parent, state);
		GridLayoutManager gridLayoutManager = (GridLayoutManager) parent.getLayoutManager();
		if (gridLayoutManager.canScrollHorizontally()) {
			if (RecyclerViewUtil.isLastGridRaw(parent, view)) {// 如果是最后一列
				if (parent.getChildAdapterPosition(view) == gridLayoutManager.getItemCount() - 1) {// 最后一个
					outRect.set(0, 0, mLastDividerSize, 0);
				} else {
					outRect.set(0, 0, mLastDividerSize, mDividerVSpacing);
				}
			} /*
				 * else if (RecyclerViewUtil.isLastGridColum(parent, view)) {//
				 * 如果是最后一行，则不需要绘制底部 outRect.set(0, 0, mDividerHSpacing, 0); }
				 */ else {
				outRect.set(0, 0, mDividerHSpacing, mDividerVSpacing);
			}
		} else if (gridLayoutManager.canScrollVertically()) {
			if (RecyclerViewUtil.isLastGridRaw(parent, view)) {// 如果是最后一行，则不需要绘制底部
				if (parent.getChildAdapterPosition(view) == gridLayoutManager.getItemCount() - 1) {
					outRect.set(0, 0, 0, mLastDividerSize);
				} else {
					outRect.set(0, 0, mDividerHSpacing, mLastDividerSize);
				}
			} /*
				 * else if (RecyclerViewUtil.isLastGridColum(parent, view)) {//
				 * 如果是最后一列，则不需要绘制右边 outRect.set(0, 0, 0, mDividerVSpacing); }
				 */ else {
				outRect.set(0, 0, mDividerHSpacing, mDividerVSpacing);
			}
		}

	}

}
