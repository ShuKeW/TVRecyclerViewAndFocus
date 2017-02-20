package com.skw.library.decoration;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * @创建人 weishukai
 * @创建时间 17/1/8 上午10:46
 * @类描述 recyclerView使用LinearLayoutManager的时候绘制分隔线
 */

public class DividerLinearItemDecoration extends RecyclerView.ItemDecoration {

	private Drawable	mDivider;

	private int			mDividerSize;

	private Drawable	mLastDivider;

	private int			mLastDividerSize;

	public DividerLinearItemDecoration() {
		set(new ColorDrawable(Color.TRANSPARENT), mDividerSize, new ColorDrawable(Color.TRANSPARENT), mLastDividerSize);
	}

	/**
	 * 默认颜色透明
	 *
	 * @param dividerSize
	 * @param lastDividerSize
	 */
	public DividerLinearItemDecoration(int dividerSize, int lastDividerSize) {
		set(new ColorDrawable(Color.TRANSPARENT), dividerSize, new ColorDrawable(Color.TRANSPARENT), lastDividerSize);
	}

	public DividerLinearItemDecoration(Drawable divider, int dividerSize, Drawable lastDivider, int lastDividerSize) {
		set(divider, dividerSize, lastDivider, lastDividerSize);
	}

	/**
	 * @param dividerColor
	 *            分隔线的颜色
	 * @param dividerSize
	 *            分隔线的大小
	 * @param lastDividerColor
	 *            最后一行的分隔线颜色
	 * @param lastDividerSize
	 *            最后一行分隔线的大小，如果小于等于0，则不画
	 */
	public DividerLinearItemDecoration(@ColorInt int dividerColor, int dividerSize, @ColorInt int lastDividerColor, int lastDividerSize) {
		set(new ColorDrawable(dividerColor), dividerSize, new ColorDrawable(lastDividerColor), lastDividerSize);
	}

	private void set(Drawable divider, int dividerSize, Drawable lastDivider, int lastDividerSize) {
		this.mDivider = divider;
		this.mDividerSize = dividerSize;
		this.mLastDivider = lastDivider;
		this.mLastDividerSize = lastDividerSize;
	}

	@Override public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
		super.onDraw(c, parent, state);
		if (mDivider == null) {
			return;
		}
		if (parent.getLayoutManager().canScrollHorizontally()) {
			drawHorizontal(c, parent);
		} else if (parent.getLayoutManager().canScrollVertically()) {
			drawVertical(c, parent);
		}
	}

	private void drawHorizontal(Canvas c, RecyclerView parent) {
		final int top = parent.getPaddingTop();
		final int bottom = parent.getHeight() - parent.getPaddingBottom();

		int childCount = parent.getChildCount();
		for (int i = 0; i < childCount; i++) {
			final View child = parent.getChildAt(i);
			final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
			final int left = child.getRight() + params.rightMargin;
			boolean isLast = i == (childCount - 1);
			int dividerSize = getDividerSize(parent, isLast);
			if (dividerSize > 0) {
				final int right = left + dividerSize;
				if (isLast) {
					mLastDivider.setBounds(left, top, right, bottom);
					mLastDivider.draw(c);
				} else {
					mDivider.setBounds(left, top, right, bottom);
					mDivider.draw(c);
				}

			}
		}
	}

	private void drawVertical(Canvas c, RecyclerView parent) {
		final int left = parent.getPaddingLeft();
		final int right = parent.getWidth() - parent.getPaddingRight();

		int childCount = parent.getChildCount();
		for (int i = 0; i < childCount; i++) {
			final View child = parent.getChildAt(i);
			final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
			final int top = child.getBottom() + params.bottomMargin;
			boolean isLast = i == (childCount - 1);
			int dividerSize = getDividerSize(parent, isLast);
			if (dividerSize > 0) {
				final int bottom = top + dividerSize;
				if (isLast) {
					mLastDivider.setBounds(left, top, right, bottom);
					mLastDivider.draw(c);
				} else {
					mDivider.setBounds(left, top, right, bottom);
					mDivider.draw(c);
				}

			}
		}
	}

	@Override public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
		super.getItemOffsets(outRect, view, parent, state);
		boolean isLast = isLastRow(parent, view);
		if (isLast && mLastDividerSize <= 0) {
			return;
		}
		if (parent.getLayoutManager().canScrollHorizontally()) {
			outRect.right = getDividerSize(parent, isLast);
		} else if (parent.getLayoutManager().canScrollVertically()) {
			outRect.bottom = getDividerSize(parent, isLast);
		}

	}

	private int getDividerSize(RecyclerView parent, boolean isLast) {
		if (isLast) {
			return mLastDividerSize;
		} else {
			return mDividerSize != 0 ? mDividerSize : parent.getLayoutManager().canScrollHorizontally() ? mDivider.getIntrinsicWidth() : mDivider.getIntrinsicHeight();
		}
	}

	private boolean isLastRow(RecyclerView parent, View view) {
		int itemPosition = parent.getChildAdapterPosition(view);
		if (itemPosition == parent.getLayoutManager().getItemCount() - 1) return true;
		return false;
	}
}
