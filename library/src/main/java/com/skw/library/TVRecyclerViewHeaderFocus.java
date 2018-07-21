package com.skw.library;

import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.StaggeredGridLayoutManager;
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

    @Override
    public boolean requestChildRectangleOnScreen(View child, Rect rect, boolean immediate) {
        // return super.requestChildRectangleOnScreen(child, rect, immediate);
        LayoutManager layoutManager = getLayoutManager();
        if (layoutManager instanceof LinearLayoutManager) {
            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
            if (linearLayoutManager.findLastVisibleItemPosition() == (linearLayoutManager.getItemCount() - 1)) {
                return super.requestChildRectangleOnScreen(child, rect, immediate);
            }
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            StaggeredGridLayoutManager staggeredGridLayoutManager = (StaggeredGridLayoutManager) layoutManager;
            // TODO: 18/7/21  
        }
        focusItemOffsetX = focusItemOffsetY = 0;
        View focusView = child.findFocus();
        Rect rectFocus = new Rect();
        focusView.getDrawingRect(rectFocus);
        offsetDescendantRectToMyCoords(focusView, rectFocus);
        int dx = 0, dy = 0;
        if (layoutManager.canScrollHorizontally()) {
//            dx = (int) (rectFocus.left - (rectFocus.width() * (FocusEffectView.scale - 1) / 2)) + 1;
            dy = rectFocus.left - getPaddingLeft();
        } else if (layoutManager.canScrollVertically()) {
//            dy = (int) (rectFocus.top - (rectFocus.height() * (FocusEffectView.scale - 1) / 2)) + 1;
            dy = rectFocus.top - getPaddingTop();
        }
        if (immediate) {
            scrollBy(dx, dy);
        } else {
            smoothScrollBy(dx, dy);
        }
        return true;
    }

    @Override
    public void destory() {
        super.destory();
    }
}
