package com.skw.library.smoothscroller;

import android.content.Context;
import android.support.v7.widget.LinearSmoothScroller;
import android.view.FocusFinder;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by weishukai on 17/3/10.
 */

public class TVSmoothScroller extends LinearSmoothScroller {
    public TVSmoothScroller(Context context) {
        super(context);
    }

    @Override
    protected void onStop() {
        super.onStop();
        View targetView = getLayoutManager().findViewByPosition(getTargetPosition());
        if (targetView != null) {
            /**
             * 如果本身是可以获取焦点的view
             */
            if (targetView.isFocusable() && (!targetView.isInTouchMode() || targetView.isFocusableInTouchMode())) {
                targetView.requestFocus();
            } else if (targetView instanceof ViewGroup) {
                ViewGroup parent = (ViewGroup) targetView;
                int count = parent.getChildCount();
                View child = null;
                for (int i = 0; i < count; i++) {
                    child = parent.getChildAt(i);
                    if (child.isFocusable() && (!child.isInTouchMode() || child.isFocusableInTouchMode())) {
                        /**
                         * 当左边和上边都没有焦点的时候，说明是最左上角的view，那么让他获取焦点
                         */
                        if (FocusFinder.getInstance().findNextFocus(parent, child, View.FOCUS_LEFT) == null && FocusFinder.getInstance().findNextFocus(parent, child, View.FOCUS_UP) == null) {
                            child.requestFocus();
                            break;
                        }
                    }
                }
            }
        }


    }
}
