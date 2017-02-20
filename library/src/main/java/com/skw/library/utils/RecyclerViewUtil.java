package com.skw.library.utils;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;

/**
 * @创建人 weishukai
 * @创建时间 17/1/22 下午4:13
 * @类描述 一句话说明这个类是干什么的
 */

public class RecyclerViewUtil {

	/**
	 * 是否是最后一列，相对于recyclerView的方向而言
	 *
	 * @param parent
	 * @param view
	 * @return
	 */
	public static boolean isLastGridColum(RecyclerView parent, View view) {
		RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
		if (layoutManager instanceof GridLayoutManager) {
			GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
			GridLayoutManager.SpanSizeLookup spanSizeLookUp = gridLayoutManager.getSpanSizeLookup();
			int childPosition = parent.getChildAdapterPosition(view);
			int spanCountChild = 0;
			for (int i = 0; i <= childPosition; i++) {
				spanCountChild += spanSizeLookUp.getSpanSize(i);
			}
			if (spanCountChild >= gridLayoutManager.getSpanCount() && spanCountChild % gridLayoutManager.getSpanCount() == 0) {
				return true;
			}
		} else if (layoutManager instanceof StaggeredGridLayoutManager) {
			// TODO: 17/1/16
		}
		return false;
	}

	/**
	 * 是否是最后一行，相对于recyclerView的方向而言,横向表示最右边，竖向表示最下边
	 *
	 * @param parent
	 * @param view
	 * @return
	 */
	public static boolean isLastGridRaw(RecyclerView parent, View view) {
		RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
		if (layoutManager instanceof GridLayoutManager) {
			GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
			GridLayoutManager.SpanSizeLookup spanSizeLookUp = gridLayoutManager.getSpanSizeLookup();
			int childPosition = parent.getChildAdapterPosition(view);
			int itemCount = gridLayoutManager.getItemCount();
			int spanCountTotal = 0;
			int spanCountChild = 0;
			if (childPosition >= itemCount - gridLayoutManager.getSpanCount()) {
				for (int i = 0; i < itemCount; i++) {
					spanCountTotal += spanSizeLookUp.getSpanSize(i);
					if (i <= childPosition) {
						spanCountChild += spanSizeLookUp.getSpanSize(i);
					}
				}
				int lastRowCount = spanCountTotal % gridLayoutManager.getSpanCount();
				if (lastRowCount == 0) {
					lastRowCount = gridLayoutManager.getSpanCount();
				}
				if (spanCountChild > spanCountTotal - lastRowCount) {
					return true;
				}
			}

		} else if (layoutManager instanceof StaggeredGridLayoutManager) {
			// TODO: 17/1/16
		}
		return false;
	}

}
