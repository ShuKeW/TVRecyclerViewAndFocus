package com.skw.library;

import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.util.Log;
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

    @Override
    public boolean requestChildRectangleOnScreen(View child, Rect rect, boolean immediate) {
        focusItemOffsetX = focusItemOffsetY = 0;
        View focusView = child.findFocus();
        Rect rectFocus = new Rect();
        focusView.getDrawingRect(rectFocus);
        offsetDescendantRectToMyCoords(focusView, rectFocus);
        Log.e("middle", rectFocus.toString());
        Log.e("middle", "" + getWidth() / 2);
        Log.e("middle", "" + getHeight() / 2);

        LayoutManager layoutManager = getLayoutManager();
        if (layoutManager != null) {
            if (layoutManager instanceof LinearLayoutManager) {
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
                if (layoutManager.canScrollHorizontally()) {
                    if ((linearLayoutManager.findFirstVisibleItemPosition() == 0 && rectFocus.left < getWidth() / 2)
                            || (linearLayoutManager.findLastCompletelyVisibleItemPosition() == (linearLayoutManager.getItemCount() - 1) && (rectFocus.right - rectFocus.width() / 2) >= getWidth() / 2)
                            || (linearLayoutManager.findLastVisibleItemPosition() == (linearLayoutManager.getItemCount() - 1) && rectFocus.left > getWidth() / 2)) {
                        return super.requestChildRectangleOnScreen(child, rect, immediate);
                    }
                } else if (layoutManager.canScrollVertically()) {
                    if ((linearLayoutManager.findFirstVisibleItemPosition() == 0 && rectFocus.top < getHeight() / 2)
                            || (linearLayoutManager.findLastCompletelyVisibleItemPosition() == (linearLayoutManager.getItemCount() - 1) && (rectFocus.bottom - rectFocus.height() / 2) >= getHeight() / 2)
                            || (linearLayoutManager.findLastVisibleItemPosition() == (linearLayoutManager.getItemCount() - 1) && rectFocus.top > getHeight() / 2)) {
                        return super.requestChildRectangleOnScreen(child, rect, immediate);
                    }
                }

            } else if (layoutManager instanceof StaggeredGridLayoutManager) {
                StaggeredGridLayoutManager staggeredGridLayoutManager = (StaggeredGridLayoutManager) layoutManager;
                // TODO: 17/1/4
            }
        }

        int dx = 0, dy = 0;
        if (getLayoutManager().canScrollHorizontally()) {
            dx = rectFocus.left - (getWidth() - rectFocus.width()) / 2;
        } else if (getLayoutManager().canScrollVertically()) {
            dy = rectFocus.bottom - rectFocus.height() / 2 - getHeight() / 2;
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
