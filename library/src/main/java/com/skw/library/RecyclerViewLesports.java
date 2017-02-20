package com.skw.library;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.Toast;

import com.skw.library.focus.FocusEffectViewUtil;
import com.skw.library.holder.RVHolder;

/**
 * @创建人 weishukai
 * @创建时间 16/12/9 下午5:46
 * @类描述 自定义RecyclerView，实现将获取焦点的item绘制在最上边
 */
public class RecyclerViewLesports extends RecyclerView implements View.OnKeyListener {

	private static final String	TAG							= "RecyclerViewLesports";

	/**
	 * 当前获取焦点的view的position,这个位置是在当前显示的view中，而不是全局的
	 */
	private int					currentFocusedChildPosition	= 0;

	/**
	 * 当前获取焦点的view的position,在全局中的位置
	 */
	private int					currentFocusedItemPosition	= -1;

	private View				nofityFocusView;

	/**
	 * 获取焦点的view所在的item，可能他就是foucesView，可能他的child是focusView
	 */
	private View				mFocusViewItem;

	private Rect				mFocusRect;

	private boolean				isLoadMoreComplete			= true;

	private OnLoadMoreListener	onLoadMoreListener;

	/**
	 * 最左边的view是否消耗掉按键，屏幕的上下左右
	 */
	private boolean				isLastLeftItemHandKey, isLastRightItemHandKey, isLastTopItemHandKey, isLastBottomItemHandKey;

	/**
	 * 获取焦点后要滚动的距离
	 */
	protected int				focusItemOffsetX			= 0, focusItemOffsetY = 0;

	private AdapterDataObserver	dataObserver;

	/**
	 * 加载更多
	 */
	public interface OnLoadMoreListener {

		void onLoadMore();
	}

	public RecyclerViewLesports(Context context) {
		super(context);
		init(context);
	}

	public RecyclerViewLesports(Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public RecyclerViewLesports(Context context, @Nullable AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	private void init(Context context) {
		setChildrenDrawingOrderEnabled(true);
		/**
		 * 每个方向给1像素的padding，防止下一个item还未显示，找不到下一个焦点
		 */
		int paddingLeft = getPaddingLeft() == 0 ? 1 : getPaddingLeft();
		int paddingRight = getPaddingRight() == 0 ? 1 : getPaddingRight();
		int paddingTop = getPaddingTop() == 0 ? 1 : getPaddingTop();
		int paddingBottom = getPaddingBottom() == 0 ? 1 : getPaddingBottom();
		this.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
		// 去掉动画
		this.setAnimation(null);
		/**
		 * 设置可以获取焦点，当notify子view失去焦点是获取焦点，免得让焦点默认移动到别的view
		 */
		setFocusable(true);
		setFocusableInTouchMode(true);
		setDescendantFocusability(FOCUS_AFTER_DESCENDANTS);

		dataObserver = new RVDataObservable();

	}

	@Override public void setAdapter(Adapter adapter) {
		Log.d(TAG, "setAdapter:" + adapter);
		if (getAdapter() != null) {
			getAdapter().unregisterAdapterDataObserver(dataObserver);
			getAdapter().onDetachedFromRecyclerView(this);
		}
		super.setAdapter(adapter);
		if (getAdapter() != null) {
			getAdapter().registerAdapterDataObserver(dataObserver);
			getAdapter().onAttachedToRecyclerView(this);
		}
	}

	@Override protected void dispatchDraw(Canvas canvas) {
		Log.d(TAG, "dispatchDraw1:" + currentFocusedChildPosition);
		if (mFocusViewItem != null) {
			int count = getChildCount();
			for (int i = 0; i < count; i++) {
				View child = getChildAt(i);
				if (mFocusViewItem == child) {
					currentFocusedChildPosition = i;
					break;
				}
			}
		} else {
			currentFocusedChildPosition = 0;
		}

		super.dispatchDraw(canvas);
		Log.d(TAG, "dispatchDraw2");
		// /**
		// * 当child绘制完成后，在判断是否加载更多
		// */
		// checkIsLoadMore();
		if (nofityFocusView != null && !nofityFocusView.isFocused()) {
			setDescendantFocusability(FOCUS_AFTER_DESCENDANTS);
			FocusEffectViewUtil.setFocusEffectVisible(this, true);
			nofityFocusView.requestFocus();
			nofityFocusView = null;
		}

	}

	@Override public void requestChildFocus(View child, View focused) {
		Log.d(TAG, "requestChildFocus1:" + child.toString() + "  " + focused.toString());

		if (child.getTag() != null) {
			Log.d(TAG, "requestChildFocus1    tag:" + child.getTag().toString());
		}
		boolean isNotEdge;
		mFocusViewItem = child;
		if (child != focused) {
			isNotEdge = true;
			if (mFocusRect == null) {
				mFocusRect = new Rect();
			}
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

			focused.getDrawingRect(mFocusRect);
			offsetDescendantRectToMyCoords(focused, mFocusRect);
			offsetRectIntoDescendantCoords(child, mFocusRect);
			Log.d(TAG, "requestChildFocus1：" + mFocusRect.toString());
		} else {
			mFocusRect = null;
			isNotEdge = isNotTheEdgeView(mFocusViewItem);
		}
		super.requestChildFocus(child, focused);
		Log.d(TAG, "requestChildFocus2");
		if (isNotEdge) {
			postInvalidate();
		}
		currentFocusedItemPosition = getChildAdapterPosition(mFocusViewItem);
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

	@Override protected int getChildDrawingOrder(int childCount, int i) {
		// Log.d(TAG, "getChildDrawingOrder:" + currentFocusedChildPosition + "
		// " + childCount);
		if (i == currentFocusedChildPosition) {
			return childCount - 1;
		} else if (i == childCount - 1) {
			return currentFocusedChildPosition;
		} else {
			return i;
		}
	}

	@Override public void onScrolled(int dx, int dy) {
		super.onScrolled(dx, dy);
		Log.d(TAG, "onScrolled");
		/**
		 * 滑动的过程中更新焦点效果view的位置
		 */
		FocusEffectViewUtil.updateFocusEffect(this);
	}

	@Override public void onScrollStateChanged(int state) {
		super.onScrollStateChanged(state);
		Log.d(TAG, "onScrollStateChanged");
		if (state == SCROLL_STATE_IDLE) {
			checkIsLoadMore();
		}
	}

	/**
	 * 判断是否要
	 */
	protected void checkIsLoadMore() {
		if (onLoadMoreListener != null) {
			LayoutManager layoutManager = getLayoutManager();
			if (layoutManager != null) {
				/*
				 * if (layoutManager instanceof GridLayoutManager) {
				 * GridLayoutManager gridLayoutManager = (GridLayoutManager)
				 * layoutManager; int lastPosition =
				 * gridLayoutManager.findLastVisibleItemPosition(); int count =
				 * gridLayoutManager.getItemCount() - 1; Log.d(TAG,
				 * "checkIsLoadMore--GridLayoutManager:" + lastPosition + "" +
				 * count); if (gridLayoutManager.findLastVisibleItemPosition()
				 * == (gridLayoutManager.getItemCount() - 1) &&
				 * isLoadMoreComplete && onLoadMoreListener != null) {
				 * isLoadMoreComplete = false; onLoadMoreListener.onLoadMore();
				 * } } else
				 */
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
					// if
					// (staggeredGridLayoutManager.findLastVisibleItemPositions(null)
					// == (staggeredGridLayoutManager.getItemCount() - 1) &&
					// isLoadMoreComplete && onLoadMoreListener != null) {
					// isLoadMoreComplete = false;
					// onLoadMoreListener.onLoadMore();
					// }
				}
			}
		}

	}

	@Override public boolean requestChildRectangleOnScreen(View child, Rect rect, boolean immediate) {
		focusItemOffsetX = focusItemOffsetY = 0;
		return super.requestChildRectangleOnScreen(child, rect, immediate);
	}

	@Override public void scrollBy(int x, int y) {
		focusItemOffsetX = x;
		focusItemOffsetY = y;
		super.scrollBy(x, y);
	}

	@Override public void smoothScrollBy(int dx, int dy) {
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
	 * @param left
	 *            屏幕左边
	 * @param top
	 *            屏幕上边
	 * @param right
	 *            屏幕右边
	 * @param bottom
	 *            屏幕下边
	 */
	public void setLastLineItemHandKey(boolean left, boolean top, boolean right, boolean bottom) {
		this.isLastLeftItemHandKey = left;
		this.isLastTopItemHandKey = top;
		this.isLastRightItemHandKey = right;
		this.isLastBottomItemHandKey = bottom;
	}

	/**
	 * 设置最左边的view是否消耗keyevent，如果消耗，在左边的view按左键没有效果
	 *
	 * @param isHand
	 *            true:消耗，左键不会响应
	 */
	public void setLastLeftItemHandKey(boolean isHand) {
		isLastLeftItemHandKey = isHand;

	}

	public void setLastRightItemHandKey(boolean isHand) {
		isLastRightItemHandKey = isHand;
	}

	public void setLastTopItemHandKey(boolean isHand) {
		isLastTopItemHandKey = isHand;
	}

	public void setLastBottomItemHandKey(boolean isHand) {
		isLastBottomItemHandKey = isHand;
	}

	@Override public void onChildAttachedToWindow(View child) {
		super.onChildAttachedToWindow(child);
		Log.d(TAG, "onChildAttachedToWindow:");

		int position = getChildAdapterPosition(child);
		if (position == currentFocusedItemPosition) {
			if (mFocusRect == null) {
				// child.requestFocus();
				nofityFocusView = child;
			} else {
				/**
				 * 多层处理
				 */
				findFocusView(child, child);
			}
		}
		// TODO: 17/2/17 多层的时候
		if (isLastLeftItemHandKey || isLastRightItemHandKey || isLastTopItemHandKey || isLastBottomItemHandKey) {
			child.setOnKeyListener(this);
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
				nofityFocusView = child;
			}
		} else if (child instanceof ViewGroup) {
			ViewGroup viewGroup = (ViewGroup) child;
			int count = viewGroup.getChildCount();
			for (int i = 0; i < count; i++) {
				findFocusView(parent, viewGroup.getChildAt(i));
			}
		}

		// if (view instanceof ViewGroup) {
		// ViewGroup viewGroup = (ViewGroup) view;
		// int count = viewGroup.getChildCount();
		// Rect rect = new Rect();
		// for (int i = 0; i < count; i++) {
		// View child = viewGroup.getChildAt(i);
		// child.getDrawingRect(rect);
		// offsetDescendantRectToMyCoords(child, rect);
		// offsetRectIntoDescendantCoords(view, rect);
		// Log.d(TAG, "onChildAttachedToWindow：" + rect.toString());
		// if (rect.contains(mFocusRect)) {
		// // viewGroup.getChildAt(i).requestFocus();
		// nofityFocusView = child;
		// break;
		// }
		// }
		// }
	}

	@Override public void onChildDetachedFromWindow(View child) {
		super.onChildDetachedFromWindow(child);
		Log.d(TAG, "onChildDetachedFromWindow:");
		child.setOnKeyListener(null);
	}

	@Override public boolean onKey(View v, int keyCode, KeyEvent event) {
		if (getLayoutManager().canScrollHorizontally()) {
			Log.d(TAG, "onKey  canScrollHorizontally");
			if (event.getAction() == KeyEvent.ACTION_DOWN) {
				switch (keyCode) {
					case KeyEvent.KEYCODE_DPAD_LEFT:
						return isLastLeftItemHandKey && isLastTopItem(v);
					case KeyEvent.KEYCODE_DPAD_RIGHT:
						return isLastRightItemHandKey && isLastBottomItem(v);
					case KeyEvent.KEYCODE_DPAD_UP:
						return isLastTopItemHandKey && isLastLeftItem(v);
					case KeyEvent.KEYCODE_DPAD_DOWN:
						return isLastBottomItemHandKey && isLastRightItem(v);
				}
			}
		} else if (getLayoutManager().canScrollVertically()) {
			Log.d(TAG, "onKey  canScrollVertically");
			if (event.getAction() == KeyEvent.ACTION_DOWN) {
				switch (keyCode) {
					case KeyEvent.KEYCODE_DPAD_LEFT:
						return isLastLeftItemHandKey && isLastLeftItem(v);
					case KeyEvent.KEYCODE_DPAD_RIGHT:
						return isLastRightItemHandKey && isLastRightItem(v);
					case KeyEvent.KEYCODE_DPAD_UP:
						return isLastTopItemHandKey && isLastTopItem(v);
					case KeyEvent.KEYCODE_DPAD_DOWN:
						return isLastBottomItemHandKey && isLastBottomItem(v);
				}
			}
		}

		return false;
	}

	/**
	 * 是否是最右边的item，如果是竖向，表示右边，如果是横向表示下边
	 *
	 * @param view
	 * @return
	 */
	public boolean isLastRightItem(View view) {
		LayoutManager layoutManager = getLayoutManager();
		if (layoutManager instanceof GridLayoutManager) {
			GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
			GridLayoutManager.SpanSizeLookup spanSizeLookUp = gridLayoutManager.getSpanSizeLookup();
			int childPosition = getChildAdapterPosition(view);
			int childSpanCount = 0;
			for (int i = 0; i <= childPosition; i++) {
				childSpanCount += spanSizeLookUp.getSpanSize(i);
			}
			if (childSpanCount % gridLayoutManager.getSpanCount() == 0) {
				Toast.makeText(getContext(), "拦截right", Toast.LENGTH_SHORT).show();
				return true;
			}
		} else if (layoutManager instanceof LinearLayoutManager) {
			Toast.makeText(getContext(), "拦截right", Toast.LENGTH_SHORT).show();
			return true;
		} else if (layoutManager instanceof StaggeredGridLayoutManager) {
			// TODO: 17/1/11
		}

		return false;
	}

	/**
	 * 是否是最左边的item，如果是竖向，表示左方，如果是横向，表示上边
	 *
	 * @param view
	 * @return
	 */
	public boolean isLastLeftItem(View view) {
		LayoutManager layoutManager = getLayoutManager();
		if (layoutManager instanceof GridLayoutManager) {
			GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
			GridLayoutManager.SpanSizeLookup spanSizeLookUp = gridLayoutManager.getSpanSizeLookup();
			int childPosition = getChildAdapterPosition(view);
			if (childPosition == 0) {
				Toast.makeText(getContext(), "拦截left", Toast.LENGTH_SHORT).show();
				return true;
			}
			int childSpanCount = 0;
			for (int i = 0; i <= childPosition; i++) {
				childSpanCount += spanSizeLookUp.getSpanSize(i);
			}
			if (childSpanCount % gridLayoutManager.getSpanCount() == 1) {
				Toast.makeText(getContext(), "拦截left", Toast.LENGTH_SHORT).show();
				return true;
			}
		} else if (layoutManager instanceof LinearLayoutManager) {
			Toast.makeText(getContext(), "拦截left", Toast.LENGTH_SHORT).show();
			return true;
		} else if (layoutManager instanceof StaggeredGridLayoutManager) {
			// TODO: 17/1/11
		}

		return false;
	}

	/**
	 * 是否是最上边的item，以recyclerview的方向做参考
	 *
	 * @param view
	 * @return
	 */
	public boolean isLastTopItem(View view) {
		LayoutManager layoutManager = getLayoutManager();
		if (layoutManager instanceof GridLayoutManager) {
			GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
			GridLayoutManager.SpanSizeLookup spanSizeLookUp = gridLayoutManager.getSpanSizeLookup();
			int childPosition = getChildAdapterPosition(view);
			if (childPosition < gridLayoutManager.getSpanCount()) {
				int childSpanCount = 0;
				for (int i = 0; i <= childPosition; i++) {
					childSpanCount += spanSizeLookUp.getSpanSize(i);
				}
				if (childSpanCount <= gridLayoutManager.getSpanCount()) {
					Toast.makeText(getContext(), "拦截top", Toast.LENGTH_SHORT).show();
					return true;
				}
			}

		} else if (layoutManager instanceof LinearLayoutManager) {
			if (getChildAdapterPosition(view) == 0) {
				Toast.makeText(getContext(), "拦截top", Toast.LENGTH_SHORT).show();
				return true;
			}
		} else if (layoutManager instanceof StaggeredGridLayoutManager) {
			// TODO: 17/1/11
		}

		return false;
	}

	/**
	 * 是否是最下边的item，以recyclerview的方向做参考
	 *
	 * @param view
	 * @return
	 */
	public boolean isLastBottomItem(View view) {
		LayoutManager layoutManager = getLayoutManager();
		if (layoutManager instanceof GridLayoutManager) {
			GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
			GridLayoutManager.SpanSizeLookup spanSizeLookUp = gridLayoutManager.getSpanSizeLookup();
			int childPosition = getChildAdapterPosition(view);
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
					Toast.makeText(getContext(), "拦截bottom", Toast.LENGTH_SHORT).show();
					return true;
				}
			}
		} else if (layoutManager instanceof LinearLayoutManager) {
			if (getChildAdapterPosition(view) == getLayoutManager().getItemCount() - 1) {
				Toast.makeText(getContext(), "拦截bottom", Toast.LENGTH_SHORT).show();
				return true;
			}
		} else if (layoutManager instanceof StaggeredGridLayoutManager) {
			// TODO: 17/1/11
		}

		return false;
	}

	private class RVDataObservable extends AdapterDataObserver {

		@Override public void onChanged() {
			super.onChanged();
			onDataChanged();
		}

		@Override public void onItemRangeChanged(int positionStart, int itemCount) {
			super.onItemRangeChanged(positionStart, itemCount);
		}

		@Override public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
			super.onItemRangeChanged(positionStart, itemCount, payload);
		}

		@Override public void onItemRangeInserted(int positionStart, int itemCount) {
			super.onItemRangeInserted(positionStart, itemCount);
		}

		@Override public void onItemRangeRemoved(int positionStart, int itemCount) {
			super.onItemRangeRemoved(positionStart, itemCount);
		}

		@Override public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
			super.onItemRangeMoved(fromPosition, toPosition, itemCount);
		}
	}

	protected void onDataChanged() {
		Log.e(TAG, "onDataChanged");
		FocusEffectViewUtil.setFocusEffectVisible(this, false);
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
