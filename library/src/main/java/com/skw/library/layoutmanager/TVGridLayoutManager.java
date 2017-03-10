package com.skw.library.layoutmanager;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import com.skw.library.smoothscroller.TVSmoothScroller;

/**
 * @创建人 weishukai
 * @创建时间 17/3/10 15:40
 * @类描述 一句话描述 你的类
 */

public class TVGridLayoutManager extends GridLayoutManager {

    public TVGridLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public TVGridLayoutManager(Context context, int spanCount) {
        super(context, spanCount);
    }

    public TVGridLayoutManager(Context context, int spanCount, int orientation, boolean reverseLayout) {
        super(context, spanCount, orientation, reverseLayout);
    }

    @Override
    public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
        TVSmoothScroller tvSmoothScroller =
                new TVSmoothScroller(recyclerView.getContext());
        tvSmoothScroller.setTargetPosition(position);
        startSmoothScroll(tvSmoothScroller);
    }
}
