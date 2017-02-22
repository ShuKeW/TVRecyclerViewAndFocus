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

import com.skw.library.utils.RecyclerViewUtil;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @创建人 weishukai
 * @创建时间 17/1/4 下午4:33
 * @类描述 一屏翻页的Recyclerview
 */

public class PageTVRecyclerView extends TVRecyclerView {

	private static final String				TAG				= "PageTVRecyclerView";

	private int								pageSize;

	private OnPageChangeListener			onPageChangeListener;

	private boolean							isDataChange	= false;

	/**
	 * 翻页的时长，请自行按照自己的项目处理
	 */
	private int								duration		= 900;

	private boolean							isScrolling		= false;

	private DividerGridPageItemDecoration	pageItemDecoration;

	/**
	 * 当翻页的监听
	 */
	public interface OnPageChangeListener {

		void onPageChange(boolean isFirstPage, boolean isLastPage);
	}

	public PageTVRecyclerView(Context context) {
		super(context);
	}

	public PageTVRecyclerView(Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
	}

	public PageTVRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	/**
	 * 翻页模式，一页的大小,一般为recyclerview的宽度减掉两边的padding
	 *
	 * @param pageSize
	 */
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
		if (pageItemDecoration == null) {
			pageItemDecoration = new DividerGridPageItemDecoration(pageSize);
		} else {
			removeItemDecoration(pageItemDecoration);
		}
		addItemDecoration(pageItemDecoration);
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setOnPageChangeListener(OnPageChangeListener onPageChangeListener) {
		this.onPageChangeListener = onPageChangeListener;
	}

	@Override public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
		// pageRecyclerView不支持加载更多，请在OnPageChangeListener自行处理
		// super.setOnLoadMoreListener(onLoadMoreListener);
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	@Override public void setAdapter(Adapter adapter) {
		super.setAdapter(adapter);
		isDataChange = true;
	}

	@Override protected void dispatchDraw(Canvas canvas) {
		super.dispatchDraw(canvas);
		Log.d(TAG, "dispatchDraw");
		if (isDataChange) {
			isDataChange = false;
			checkPageChange();
		}
	}

	@Override public boolean dispatchKeyEvent(KeyEvent event) {
		Log.d(TAG, "dispatchKeyEvent:" + isScrolling);
		if (isScrolling) {
			return true;
		}
		return super.dispatchKeyEvent(event);
	}

	@Override public void onScrollStateChanged(int state) {
		super.onScrollStateChanged(state);
		Log.d(TAG, "onScrollStateChanged:" + state);
		if (state == SCROLL_STATE_IDLE) {
			isScrolling = false;
			checkPageChange();
			Log.d(TAG, "computeHorizontalScrollExtent:" + computeHorizontalScrollExtent());
			Log.d(TAG, "computeHorizontalScrollOffset:" + computeHorizontalScrollOffset());
			Log.d(TAG, "computeHorizontalScrollRange:" + computeHorizontalScrollRange());
		}
	}

	private void checkPageChange() {
		Log.d(TAG, "checkPageChange");
		if (onPageChangeListener != null) {
			LayoutManager layoutManager = getLayoutManager();
			if (layoutManager != null) {
				if (layoutManager instanceof LinearLayoutManager) {
					LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
					int firstPosition = linearLayoutManager.findFirstVisibleItemPosition();
					int lastPosition = linearLayoutManager.findLastVisibleItemPosition();
					int count = linearLayoutManager.getItemCount() - 1;
					if (firstPosition == 0 && lastPosition == count) {
						if (onPageChangeListener != null) {
							onPageChangeListener.onPageChange(true, true);
						}
					} else if (firstPosition == 0) {
						if (onPageChangeListener != null) {
							onPageChangeListener.onPageChange(true, false);
						}
					} else if (lastPosition == count) {
						if (onPageChangeListener != null) {
							onPageChangeListener.onPageChange(false, true);
						}
					}
				} else if (layoutManager instanceof StaggeredGridLayoutManager) {
					StaggeredGridLayoutManager staggeredGridLayoutManager = (StaggeredGridLayoutManager) layoutManager;
					// TODO: 17/1/4

				}
			}
		}
	}

	// @Override public boolean requestChildRectangleOnScreen(View child, Rect
	// rect, boolean immediate) {
	// Log.d(TAG, "requestChildRectangleOnScreen:" + rect.toString() + " " +
	// getLeft() + " " + getRight() + " " + getWidth() + " " + getPaddingLeft()
	// + " " + getPaddingRight());
	// // if (isFillLastPage) {//
	// // 如果使用的是DividerPageGridItemDecoration，表示按照页翻页，那么最后行焦点的rect置为view的大小
	// // rect.set(0, 0, child.getWidth(), child.getHeight());
	// // }
	// if (getLayoutManager().canScrollHorizontally()) {
	// if (pageSize <= 0) {
	// // pageSize = getWidth();
	// throw new IllegalArgumentException("请设置paseSize");
	// }
	// // TODO: 17/2/9
	// if (child.getRight() > pageSize) {// 下一页
	// rect.set(rect.left, rect.top, pageSize, rect.bottom);
	// } else if (child.getLeft() < 0) {// 上一页
	// rect.set(child.getWidth() - pageSize + getPaddingLeft() +
	// getPaddingRight(), rect.top, child.getLeft() + getPaddingLeft() +
	// getPaddingRight(), rect.bottom);
	// }
	// } else if (getLayoutManager().canScrollVertically()) {
	// if (pageSize <= 0) {
	// // pageSize = getHeight();
	// throw new IllegalArgumentException("请设置paseSize");
	// }
	// if (child.getBottom() > pageSize) {// 下一页
	// rect.set(rect.left, rect.top, rect.right, pageSize);
	// } else if (child.getTop() < 0) {// 上一页
	// rect.set(rect.left, child.getHeight() - pageSize + getPaddingTop() +
	// getPaddingBottom(), rect.right, child.getTop() + getPaddingTop() +
	// getPaddingBottom());
	// }
	//
	// }
	// Log.d(TAG, "requestChildRectangleOnScreen:" + child.getLeft() + " " +
	// child.getTop() + " " + child.getRight() + " " + child.getBottom() +
	// rect.toString());
	// return super.requestChildRectangleOnScreen(child, rect, immediate);
	//
	// }

	@Override public boolean requestChildRectangleOnScreen(View child, Rect rect, boolean immediate) {
		// return super.requestChildRectangleOnScreen(child, rect, immediate);
		focusItemOffsetX = focusItemOffsetY = 0;
		View focusView = child.findFocus();
		Rect rectFocus = new Rect();
		focusView.getDrawingRect(rectFocus);
		offsetDescendantRectToMyCoords(focusView, rectFocus);
		Log.d(TAG, "requestChildRectangleOnScreen:" + rectFocus.toString());
		int dx = 0;
		int dy = 0;
		if (getLayoutManager().canScrollHorizontally()) {
			if (pageSize <= 0) {
				// pageSize = getWidth();
				throw new IllegalArgumentException("请设置paseSize");
			}
			if (rectFocus.right > pageSize) {// 下一页
				dx = pageSize;
			} else if (rectFocus.left < getPaddingLeft()) {// 上一页
				dx = -pageSize;
			}
		} else if (getLayoutManager().canScrollVertically()) {
			if (pageSize <= 0) {
				// pageSize = getHeight();
				throw new IllegalArgumentException("请设置paseSize");
			}
			if (rectFocus.bottom > pageSize) {// 下一页
				dy = pageSize;
			} else if (rectFocus.top < getPaddingTop()) {// 上一页
				dy = -pageSize;
			}

		}

		if (dx != 0 || dy != 0) {
			if (immediate) {
				scrollBy(dx, dy);
			} else {
				smoothScrollBy(dx, dy);
			}
		}
		return true;
	}

	/**
	 * 重写，用于改变滑动时间
	 *
	 * @param dx
	 * @param dy
	 */
	@Override public void smoothScrollBy(int dx, int dy) {
		// 注释掉父类的实现，将recyclerview的实现拷贝下来，然后替换mViewFlinger的smoothScrollBy方法，来更改滑动速度
		// super.smoothScrollBy(dx, dy);
		focusItemOffsetX = dx;
		focusItemOffsetY = dy;
		if (getLayoutManager() == null) {
			Log.e(TAG, "Cannot smooth scroll without a LayoutManager set. " + "Call setLayoutManager with a non-null argument.");
			return;
		}
		if (isLayoutFrozen()) {
			return;
		}
		if (!getLayoutManager().canScrollHorizontally()) {
			dx = 0;
		}
		if (!getLayoutManager().canScrollVertically()) {
			dy = 0;
		}
		if (dx != 0 || dy != 0) {
			// mViewFlinger.smoothScrollBy(dx, dy);
			isScrolling = true;
			Class<?> clazz = this.getClass();
			while (clazz != RecyclerView.class) {
				clazz = clazz.getSuperclass();
			}
			try {
				Field field = clazz.getDeclaredField("mViewFlinger");
				if (field != null) {
					field.setAccessible(true);
					// 由于内部类是私有的，所以不能直接得到内部类名，
					// 通过mViewFlingerField.getType().getName()
					// 可以得到私有内部类的完整类名
					Class mViewFlinger = Class.forName(field.getType().getName());
					Method smoothScrollBy = mViewFlinger.getDeclaredMethod("smoothScrollBy", int.class, int.class, int.class);
					smoothScrollBy.setAccessible(true);
					smoothScrollBy.invoke(field.get(this), dx, dy, duration);
				}
			} catch (NoSuchFieldException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}

		}
	}

	@Override protected void onDataChanged() {
		super.onDataChanged();
		isDataChange = true;
	}

	@Override public void destory() {
		if (pageItemDecoration != null) {
			removeItemDecoration(pageItemDecoration);
			pageItemDecoration = null;
		}
		super.destory();
	}

	/**
	 * 用于填充最后的空白
	 */
	class DividerGridPageItemDecoration extends RecyclerView.ItemDecoration {

		private int mLastDividerSize;

		public DividerGridPageItemDecoration(int lastDividerSize) {
			this.mLastDividerSize = lastDividerSize;
		}

		@Override public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
			super.getItemOffsets(outRect, view, parent, state);
			GridLayoutManager gridLayoutManager = (GridLayoutManager) parent.getLayoutManager();
			if (gridLayoutManager.canScrollHorizontally()) {
				if (RecyclerViewUtil.isLastGridRaw(parent, view)) {// 如果是最后一列
					outRect.set(0, 0, mLastDividerSize, 0);
				}
			} else if (gridLayoutManager.canScrollVertically()) {
				if (RecyclerViewUtil.isLastGridRaw(parent, view)) {// 如果是最后一行，则不需要绘制底部
					outRect.set(0, 0, 0, mLastDividerSize);
				}
			}

		}
	}
}
