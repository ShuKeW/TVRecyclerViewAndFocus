package com.skw.library;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.FocusFinder;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.Toast;

import com.skw.library.focus.FocusEffectViewUtil;
import com.skw.library.holder.RVHolder;

import java.lang.ref.WeakReference;

/**
 * @创建人 weishukai
 * @创建时间 16/12/9 下午5:46
 * @类描述 自定义RecyclerView，实现将获取焦点的item绘制在最上边
 */
public class TVRecyclerView extends RecyclerView {

    private static final String TAG = "TVRecyclerView";

    /**
     * 当前获取焦点的view的position,这个位置是在当前显示的view中，而不是全局的
     */
    private int currentFocusedChildPosition = 0;

    /**
     * 当前获取焦点的view的position,在全局中的位置
     */
    private int currentFocusedItemPosition = -1;

    private WeakReference<View> nofityFocusView;

    /**
     * 获取焦点的view所在的item，可能他就是foucesView，可能他的child是focusView
     */
    private WeakReference<View> mFocusViewItem;

    private Rect mFocusRect;

    private boolean isLoadMoreComplete = true;

    private OnLoadMoreListener onLoadMoreListener;

    /**
     * 获取焦点后要滚动的距离
     */
    protected int focusItemOffsetX = 0, focusItemOffsetY = 0;

    private AdapterDataObserver dataObserver;


    /**
     * 最左边的view是否消耗掉按键，屏幕的上下左右
     */
    private boolean isHandLastLeftKey, isHandLastRightKey, isHandLastUpKey, isHandLastDownKey;

    private boolean isNotifyData;

    /**
     * 是否需要notifydata，如果需要则设为true，不需要就设为false，避免不需要notify的时候多余的操作
     */
    private boolean isNeedNotifyData;

    /**
     * 加载更多
     */
    public interface OnLoadMoreListener {

        void onLoadMore();
    }

    public TVRecyclerView(Context context) {
        super(context);
        init(context, null);
    }

    public TVRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public TVRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        boolean drawChildOrder = false;
        if (attrs != null) {
            TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.TVRecyclerView, 0, 0);
            try {
                drawChildOrder = array.getBoolean(R.styleable.TVRecyclerView_drawChildOrderEnable, drawChildOrder);
                isNeedNotifyData = array.getBoolean(R.styleable.TVRecyclerView_isNeedNotifyData, isNeedNotifyData);
                isHandLastLeftKey = array.getBoolean(R.styleable.TVRecyclerView_handLastLeftKey, isHandLastLeftKey);
                isHandLastRightKey = array.getBoolean(R.styleable.TVRecyclerView_handLastRightKey, isHandLastRightKey);
                isHandLastUpKey = array.getBoolean(R.styleable.TVRecyclerView_handLastUpKey, isHandLastUpKey);
                isHandLastDownKey = array.getBoolean(R.styleable.TVRecyclerView_handLastDownKey, isHandLastDownKey);
            } finally {
                array.recycle();
            }
        }
        setChildrenDrawingOrderEnabled(drawChildOrder);
        /**
         * 每个方向给1像素的padding，防止下一个item还未显示，找不到下一个焦点
         */
        int paddingLeft = getPaddingLeft() == 0 ? 1 : getPaddingLeft();
        int paddingRight = getPaddingRight() == 0 ? 1 : getPaddingRight();
        int paddingTop = getPaddingTop() == 0 ? 1 : getPaddingTop();
        int paddingBottom = getPaddingBottom() == 0 ? 1 : getPaddingBottom();
        this.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
        // 去掉动画
        this.setItemAnimator(null);
        if (isNeedNotifyData) {
            /**
             * 设置可以获取焦点，当notify子view失去焦点是获取焦点，免得让焦点默认移动到别的view
             */
            setFocusable(true);
            setFocusableInTouchMode(true);
            setDescendantFocusability(FOCUS_AFTER_DESCENDANTS);

            dataObserver = new RVDataObservable();
        }

    }

    @Override
    public void setAdapter(Adapter adapter) {
        Log.d(TAG, "setAdapter:" + adapter);
        if (isNeedNotifyData) {
            if (getAdapter() != null) {
                getAdapter().unregisterAdapterDataObserver(dataObserver);
                getAdapter().onDetachedFromRecyclerView(this);
            }
            super.setAdapter(adapter);
            if (getAdapter() != null) {
                getAdapter().registerAdapterDataObserver(dataObserver);
                getAdapter().onAttachedToRecyclerView(this);
            }
        } else {
            super.setAdapter(adapter);
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        View nextFocusView = null;
        switch (event.getAction()) {
            case KeyEvent.ACTION_DOWN:
                switch (event.getKeyCode()) {
                    case KeyEvent.KEYCODE_DPAD_UP:
                        if (isHandLastUpKey) {
                            nextFocusView = FocusFinder.getInstance().findNextFocus(this, findFocus(), FOCUS_UP);
                            if (nextFocusView == null) {
                                Toast.makeText(getContext(), "拦截up", Toast.LENGTH_SHORT).show();
                                return true;
                            }
                        }
                        break;
                    case KeyEvent.KEYCODE_DPAD_DOWN:
                        if (isHandLastDownKey) {
                            nextFocusView = FocusFinder.getInstance().findNextFocus(this, findFocus(), FOCUS_DOWN);
                            if (nextFocusView == null) {
                                Toast.makeText(getContext(), "拦截down", Toast.LENGTH_SHORT).show();
                                return true;
                            }
                        }
                        break;
                    case KeyEvent.KEYCODE_DPAD_RIGHT:
                        if (isHandLastRightKey) {
                            nextFocusView = FocusFinder.getInstance().findNextFocus(this, findFocus(), FOCUS_RIGHT);
                            if (nextFocusView == null) {
                                Toast.makeText(getContext(), "拦截right", Toast.LENGTH_SHORT).show();
                                return true;
                            }
                        }
                        break;
                    case KeyEvent.KEYCODE_DPAD_LEFT:
                        if (isHandLastLeftKey) {
                            nextFocusView = FocusFinder.getInstance().findNextFocus(this, findFocus(), FOCUS_LEFT);
                            if (nextFocusView == null) {
                                Toast.makeText(getContext(), "拦截left", Toast.LENGTH_SHORT).show();
                                return true;
                            }
                        }
                        break;
                }
                break;
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        if (isChildrenDrawingOrderEnabled()) {
            Log.d(TAG, "dispatchDraw1:" + currentFocusedChildPosition);
            if (mFocusViewItem != null && mFocusViewItem.get() != null) {
                View focusViewItem = mFocusViewItem.get();
                int count = getChildCount();
                for (int i = 0; i < count; i++) {
                    View child = getChildAt(i);
                    if (focusViewItem == child) {
                        currentFocusedChildPosition = i;
                        break;
                    }
                }
            } else {
                currentFocusedChildPosition = 0;
            }
        }

        super.dispatchDraw(canvas);
        Log.d(TAG, "dispatchDraw2");
        // /**
        // * 当child绘制完成后，在判断是否加载更多
        // */
        // checkIsLoadMore();
        if (nofityFocusView != null) {
            View notifyView = nofityFocusView.get();
            if (notifyView != null && !notifyView.isFocused()) {
                isNotifyData = false;
                setDescendantFocusability(FOCUS_AFTER_DESCENDANTS);
//                FocusEffectViewUtil.setFocusEffectVisible(this, true);
                notifyView.requestFocus();
                nofityFocusView = null;
            }

        }

    }

    @Override
    public void requestChildFocus(View child, View focused) {
        Log.d(TAG, "requestChildFocus1:" + child.toString() + "  " + focused.toString());

        boolean isNotEdge = false;
        if (child != focused) {
            isNotEdge = true;

            /**
             * 解决多层嵌套
             */
            focused.bringToFront();
            ViewParent viewParent = focused.getParent();
            while (viewParent != child) {
                View parent = (View) viewParent;
                parent.postInvalidate();
                parent.bringToFront();
                viewParent = parent.getParent();
            }
            child.postInvalidate();

            if (isNeedNotifyData) {
                if (mFocusRect == null) {
                    mFocusRect = new Rect();
                }

                focused.getDrawingRect(mFocusRect);
                offsetDescendantRectToMyCoords(focused, mFocusRect);
                offsetRectIntoDescendantCoords(child, mFocusRect);
                Log.d(TAG, "requestChildFocus1：" + mFocusRect.toString());
            }
        } else {
            mFocusRect = null;
            if (isChildrenDrawingOrderEnabled()) {
                isNotEdge = isNotTheEdgeView(child);
            }
        }
        super.requestChildFocus(child, focused);
        Log.d(TAG, "requestChildFocus2");
        if (isNeedNotifyData) {
            if (mFocusViewItem != null) {
                mFocusViewItem.clear();
                mFocusViewItem = null;
            }
            mFocusViewItem = new WeakReference<View>(child);
            currentFocusedItemPosition = getChildAdapterPosition(child);
        }
        if (isNotEdge && isChildrenDrawingOrderEnabled()) {
            postInvalidate();
        }
    }

    /**
     * 是否是边缘的view
     *
     * @param view
     * @return
     */
    private boolean isNotTheEdgeView(View view) {
        int position = getChildAdapterPosition(view);
        if (position > -1) {
            LayoutManager layoutManager = getLayoutManager();
            if (layoutManager instanceof LinearLayoutManager) {
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
                if (position >= linearLayoutManager.findFirstCompletelyVisibleItemPosition() && position <= linearLayoutManager.findLastCompletelyVisibleItemPosition()) {
                    return true;
                }
            } else if (layoutManager instanceof GridLayoutManager) {
                GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
                if (position >= gridLayoutManager.findFirstCompletelyVisibleItemPosition() && position <= gridLayoutManager.findLastCompletelyVisibleItemPosition()) {
                    return true;
                }
            } else if (layoutManager instanceof StaggeredGridLayoutManager) {
                // TODO: 17/1/4
                StaggeredGridLayoutManager staggeredGridLayoutManager = (StaggeredGridLayoutManager) layoutManager;
            }
        }
        return false;
    }

    @Override
    protected int getChildDrawingOrder(int childCount, int i) {
        Log.d(TAG,"getChildDrawingOrder");
        if (i == currentFocusedChildPosition) {
            return childCount - 1;
        } else if (i == childCount - 1) {
            return currentFocusedChildPosition;
        } else {
            return i;
        }
    }

    @Override
    public void onScrolled(int dx, int dy) {
        super.onScrolled(dx, dy);
        Log.d(TAG, "onScrolled");
        /**
         * 滑动的过程中更新焦点效果view的位置
         */
//        FocusEffectViewUtil.updateFocusEffect(this);
    }

    @Override
    public void onScrollStateChanged(int state) {
        super.onScrollStateChanged(state);
        Log.d(TAG, "onScrollStateChanged");
        if (state == SCROLL_STATE_IDLE) {
            checkIsLoadMore();
        }
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        Log.d(TAG, "onScrollChanged");
    }

    /**
     * 判断是否要
     */
    protected void checkIsLoadMore() {
        if (onLoadMoreListener != null) {
            LayoutManager layoutManager = getLayoutManager();
            if (layoutManager != null) {
                if (layoutManager instanceof LinearLayoutManager) {
                    LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
                    Log.d(TAG, "checkIsLoadMore:" + linearLayoutManager.findLastVisibleItemPosition() + "  " + linearLayoutManager.getItemCount());
                    if (linearLayoutManager.findLastVisibleItemPosition() == (linearLayoutManager.getItemCount() - 1) && isLoadMoreComplete && onLoadMoreListener != null) {
                        isLoadMoreComplete = false;
                        onLoadMoreListener.onLoadMore();
                    }
                } else if (layoutManager instanceof StaggeredGridLayoutManager) {
                    StaggeredGridLayoutManager staggeredGridLayoutManager = (StaggeredGridLayoutManager) layoutManager;
                    // TODO: 17/1/4
                }
            }
        }

    }

    @Override
    public boolean requestChildRectangleOnScreen(View child, Rect rect, boolean immediate) {
        focusItemOffsetX = focusItemOffsetY = 0;
        return super.requestChildRectangleOnScreen(child, rect, immediate);
    }

    @Override
    public void scrollBy(int x, int y) {
        focusItemOffsetX = x;
        focusItemOffsetY = y;
        super.scrollBy(x, y);
    }

    @Override
    public void smoothScrollBy(int dx, int dy) {
        focusItemOffsetX = dx;
        focusItemOffsetY = dy;
        super.smoothScrollBy(dx, dy);
    }

    public int getFocusItemOffsetX() {
        return focusItemOffsetX;
    }

    public int getFocusItemOffsetY() {
        return focusItemOffsetY;
    }

    public void setFocusItemOffsetX(int focusItemOffsetX) {
        this.focusItemOffsetX = focusItemOffsetX;
    }

    public void setFocusItemOffsetY(int focusItemOffsetY) {
        this.focusItemOffsetY = focusItemOffsetY;
    }

    /**
     * 设置加载更多监听
     *
     * @param onLoadMoreListener
     */
    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    protected OnLoadMoreListener getOnLoadMoreListener() {
        return onLoadMoreListener;
    }

    /**
     * 当加载更多完成的时候调用
     */
    public void setLoadMoreComplete() {
        isLoadMoreComplete = true;
    }

    protected boolean isLoadMoreComplete() {
        return isLoadMoreComplete;
    }

    /**
     * 设置四个边缘是否消耗按键，屏幕的上下左右
     * 典型的例子是在viewPager中使用recyclerView，如果recyclerview滑到坐左边，再按左键，会滑到viewPager的上一页，true：消耗掉事件，
     *
     * @param left   屏幕左边
     * @param top    屏幕上边
     * @param right  屏幕右边
     * @param bottom 屏幕下边
     */
    public void setLastLineItemHandKey(boolean left, boolean top, boolean right, boolean bottom) {
        this.isHandLastLeftKey = left;
        this.isHandLastUpKey = top;
        this.isHandLastRightKey = right;
        this.isHandLastDownKey = bottom;
    }


    @Override
    public void onChildAttachedToWindow(View child) {
        super.onChildAttachedToWindow(child);
        if (isNotifyData && isNeedNotifyData) {
            int position = getChildAdapterPosition(child);
            Log.d(TAG, "onChildAttachedToWindow:" + position);
            if (position == currentFocusedItemPosition) {
                if (mFocusRect == null) {
                    // child.requestFocus();
                    if (nofityFocusView != null) {
                        nofityFocusView.clear();
                        nofityFocusView = null;
                    }
                    nofityFocusView = new WeakReference<View>(child);
                } else {
                    /**
                     * 多层处理
                     */
                    findFocusView(child, child);
                }
            }

        }

    }

    private void findFocusView(View parent, View child) {
        if (child.isFocusable() && child.isFocusableInTouchMode()) {
            Rect rect = new Rect();
            child.getDrawingRect(rect);
            offsetDescendantRectToMyCoords(child, rect);
            offsetRectIntoDescendantCoords(parent, rect);
            Log.d(TAG, "onChildAttachedToWindow：" + rect.toString());
            if (rect.contains(mFocusRect)) {
                // viewGroup.getChildAt(i).requestFocus();
                if (nofityFocusView != null) {
                    nofityFocusView.clear();
                    nofityFocusView = null;
                }
                nofityFocusView = new WeakReference<View>(child);
            }
        } else if (child instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) child;
            int count = viewGroup.getChildCount();
            for (int i = 0; i < count; i++) {
                findFocusView(parent, viewGroup.getChildAt(i));
            }
        }

    }

    @Override
    public void onChildDetachedFromWindow(View child) {
        Log.d(TAG, "onChildDetachedFromWindow:");
        if (mFocusViewItem != null && child == mFocusViewItem.get()) {
            mFocusViewItem.clear();
            mFocusViewItem = null;
        }
        super.onChildDetachedFromWindow(child);
    }

    @Override
    public void smoothScrollToPosition(int position) {
        super.smoothScrollToPosition(position);
        onDataChanged();
    }

    private class RVDataObservable extends AdapterDataObserver {

        @Override
        public void onChanged() {
            super.onChanged();
            onDataChanged();
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            super.onItemRangeChanged(positionStart, itemCount);
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
            super.onItemRangeChanged(positionStart, itemCount, payload);
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            super.onItemRangeInserted(positionStart, itemCount);
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            super.onItemRangeRemoved(positionStart, itemCount);
        }

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            super.onItemRangeMoved(fromPosition, toPosition, itemCount);
        }
    }

    protected void onDataChanged() {
        Log.e(TAG, "onDataChanged");
        isNotifyData = true;
//        FocusEffectViewUtil.setFocusEffectVisible(this, false);
        setDescendantFocusability(FOCUS_BEFORE_DESCENDANTS);
        this.requestFocus();
    }

    /**
     * 退出时销毁
     */
    public void destory() {
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            RecyclerView.ViewHolder viewHolder = getChildViewHolder(child);
            if (viewHolder != null && viewHolder instanceof RVHolder) {
                RVHolder baseViewHolder = (RVHolder) viewHolder;
                baseViewHolder.destory();
            }
            child = null;
        }
        nofityFocusView = null;
        mFocusViewItem = null;
        mFocusRect = null;
        dataObserver = null;
    }

}
