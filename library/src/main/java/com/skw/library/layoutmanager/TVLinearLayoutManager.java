package com.skw.library.layoutmanager;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import com.skw.library.smoothscroller.TVSmoothScroller;

/**
 * Created by weishukai on 17/3/10.
 */

public class TVLinearLayoutManager extends LinearLayoutManager {
    public TVLinearLayoutManager(Context context) {
        super(context);
    }

    public TVLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    public TVLinearLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    /**
     * 可以让滑到的view获取焦点
     *
     * @param recyclerView
     * @param state
     * @param position
     */
    @Override
    public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
        TVSmoothScroller tvSmoothScroller =
                new TVSmoothScroller(recyclerView.getContext());
        tvSmoothScroller.setTargetPosition(position);
        startSmoothScroll(tvSmoothScroller);
    }
}
