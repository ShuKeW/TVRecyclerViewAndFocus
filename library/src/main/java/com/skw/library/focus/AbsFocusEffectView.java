package com.skw.library.focus;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;

import com.skw.library.R;
import com.skw.library.TVRecyclerView;

import java.lang.ref.WeakReference;

/**
 * @创建人 weishukai
 * @创建时间 17/1/20 上午10:22
 * @类描述 一句话说明这个类是干什么的
 */

public abstract class AbsFocusEffectView extends View implements ViewTreeObserver.OnGlobalFocusChangeListener {

	private String TAG = "AbsFocusEffectView";

	/**
	 * 焦点类型，不同的焦点类型，获取焦点的效果不一样
	 */
	public static final class FocusType {

		/**
		 * 普通焦点
		 */
		public static final String	FOCUS_NORMAL	= "focus_normal";

		/**
		 * tabView类型
		 */
		public static final String	FOCUS_TAB_VIEW	= "focus_tab_view";

		/**
		 * 海报类型
		 */
		public static final String	FOCUS_POSTER	= "focus_poster";

	}

	protected WeakReference<View>		oldFocusViewRef, currentFocusViewRef;

	private Rect						currentPaddingRect;

	private Rect						globalVisibleRect;

	/**
	 * 用来做移动焦点动画用的
	 */
	private TranslateRect				translateRect;

	protected ValueAnimator				animIn, animOut;

	protected AnimatorSet				translateAnimatorSet;

	protected FocusInUpdateListener		focusInUpdateListener;

	protected FocusOutUpdateListener	focusOutUpdateListener;

	protected FocusInListener			focusInListener;

	protected FocusOutListener			focusOutListener;

	protected TranslateListener			translateListener;

	private boolean						isHide;

	public AbsFocusEffectView(Context context) {
		super(context);
		init(context);
	}

	public AbsFocusEffectView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public AbsFocusEffectView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context);
	}

	private void init(Context context) {
		setId(R.id.focus_effect_view);
		focusInUpdateListener = new FocusInUpdateListener();
		focusOutUpdateListener = new FocusOutUpdateListener();
		focusInListener = new FocusInListener();
		focusOutListener = new FocusOutListener();
		translateListener = new TranslateListener();
		globalVisibleRect = new Rect();
		translateRect = new TranslateRect();
		registerListener();
	}

	/**
	 * 将效果view添加到activity
	 *
	 * @param activity
	 */
	public void bindActivity(Activity activity) {
		((ViewGroup) activity.getWindow().getDecorView()).addView(this);
	}

	public void bindDialog(Dialog dialog) {
		((ViewGroup) dialog.getWindow().getDecorView()).addView(this);
	}

	public void bindView(View view) {
		((ViewGroup) view.getRootView()).addView(this);
	}

	public void hide() {
		isHide = true;
		// setVisibility(INVISIBLE);
		releaseFocusInAnim();
		releaseFocusOutAnim();
		releaseTranslateAnim();
		if (oldFocusViewRef != null && oldFocusViewRef.get() != null) {
			View view = oldFocusViewRef.get();
			view.setScaleX(1.0f);
			view.setScaleY(1.0f);
			oldFocusViewRef.clear();
		}
		if (currentFocusViewRef != null && currentFocusViewRef.get() != null) {
			View view = currentFocusViewRef.get();
			view.setScaleX(1.0f);
			view.setScaleY(1.0f);
			currentFocusViewRef.clear();
		}
	}

	public void show() {
		isHide = false;
		// setVisibility(VISIBLE);
		Rect oldFocusEffectRect = new Rect();
		this.getGlobalVisibleRect(oldFocusEffectRect);
		Log.e(TAG, "show：" + oldFocusEffectRect.toString());
	}

	/**
	 * 注册监听事件
	 */
	public void registerListener() {
		this.getViewTreeObserver().addOnGlobalFocusChangeListener(this);
	}

	/**
	 * 取消监听事件
	 */
	public void unRegisterListener() {
		this.getViewTreeObserver().removeOnGlobalFocusChangeListener(this);
	}

	@Override public void onGlobalFocusChanged(View oldFocus, View newFocus) {
		Log.e(TAG, "onGlobalFocusChanged：" + newFocus.toString());
		if (isHide) {
			return;
		}

		if (getViewFocusType(newFocus) == null) {
			return;
		}

		onFocusOut();

		if (oldFocusViewRef != null) {
			oldFocusViewRef.clear();
		}
		if (currentFocusViewRef != null) {
			oldFocusViewRef = new WeakReference<View>(currentFocusViewRef.get());
			currentFocusViewRef.clear();
		}
		currentFocusViewRef = new WeakReference<View>(newFocus);

		onFocusIn();

	}

	public void updateLocation() {
		Log.d(TAG, "updateLocation:");
		/**
		 * 如果有移动效果，不需要中途去更新
		 */
		if (currentFocusViewRef != null) {
			View focusView = currentFocusViewRef.get();
			if (focusView != null && !getViewFocusTranslateAnim(focusView)) {
				if (currentPaddingRect == null) {
					currentPaddingRect = new Rect();
				}
				continueChangeFocusEffect(focusView, currentPaddingRect);
			}
		}

	}

	protected abstract void onFocusOut();

	protected abstract void onFocusIn();

	/**
	 * 获取view的焦点类型
	 *
	 * @param view
	 * @return
	 */
	protected String getViewFocusType(View view) {
		Object o = view.getTag(R.id.focus_type);
		String focusType = null;
		if (o == null) {
			Log.e(TAG, "没有设置焦点类型：" + view.toString());
			return null;
		} else {
			focusType = (String) o;
		}
		Log.d(TAG, "getViewFocusType:" + focusType);
		return focusType;
	}

	/**
	 * 获取view是否有获取焦点和失去焦点的放大缩小动画
	 *
	 * @param view
	 * @return
	 */
	protected boolean getViewFocusScaleAnim(View view) {
		Object o = view.getTag(R.id.focus_type_is_scale_anim);
		if (o != null) {
			return (boolean) o;
		}
		return false;
	}

	/**
	 * 获取view是否有焦点效果的移动动画
	 *
	 * @param view
	 * @return
	 */
	protected boolean getViewFocusTranslateAnim(View view) {
		Object o = view.getTag(R.id.focus_type_is_translate);
		if (o != null) {
			return (boolean) o;
		}
		return false;
	}

	protected void setPaddingRect(Rect paddingRect) {
		Log.d(TAG, "setPaddingRect:" + paddingRect.toString());
		this.currentPaddingRect = paddingRect;
	}

	/**
	 * 获取新的焦点框的位置
	 *
	 * @param newFocusEffectRect
	 * @param newFocus
	 * @return
	 */
	protected void getNewFocusEffectScaleRect(View newFocus, Rect newFocusEffectRect, float scale) {

		getGlobalVisibleRectOfDecorView(newFocus, newFocusEffectRect);
		Log.d(TAG, "getNewFocusEffectScaleRect:" + newFocusEffectRect.toString());
		if (scale == 1) {
			newFocusEffectRect.left -= currentPaddingRect.left;
			newFocusEffectRect.right += currentPaddingRect.right;
			newFocusEffectRect.top -= currentPaddingRect.top;
			newFocusEffectRect.bottom += currentPaddingRect.bottom;
			return;
		}

		ViewParent viewParent = newFocus.getParent();
		while (viewParent != null && !(viewParent instanceof TVRecyclerView)) {
			viewParent = viewParent.getParent();
		}
		if (viewParent != null) {
			TVRecyclerView TVRecyclerView = (TVRecyclerView) viewParent;
			int focusOffsetX = TVRecyclerView.getFocusItemOffsetX();
			int focusOffsetY = TVRecyclerView.getFocusItemOffsetY();
			newFocusEffectRect.offset(-focusOffsetX, -focusOffsetY);
			if (focusOffsetX < 0) {// 左边超出了，向右滑
				newFocusEffectRect.left = newFocusEffectRect.right - newFocus.getWidth();
			} else if (focusOffsetX > 0) {
				newFocusEffectRect.right = newFocusEffectRect.left + newFocus.getWidth();
			} else if (focusOffsetY < 0) {
				newFocusEffectRect.top = newFocusEffectRect.bottom - newFocus.getHeight();
			} else if (focusOffsetY > 0) {
				newFocusEffectRect.bottom = newFocusEffectRect.top + newFocus.getHeight();
			}

			Log.d(TAG, "getNewFocusEffectScaleRect:" + TVRecyclerView.getFocusItemOffsetX() + "  " + TVRecyclerView.getFocusItemOffsetY());
			TVRecyclerView.setFocusItemOffsetX(0);
			TVRecyclerView.setFocusItemOffsetY(0);
		}

		int widthAdd = Math.round((newFocus.getWidth() * scale - newFocus.getWidth()) / 2);
		int heightAdd = Math.round((newFocus.getHeight() * scale - newFocus.getHeight()) / 2);
		newFocusEffectRect.left -= widthAdd;
		newFocusEffectRect.right += widthAdd;
		newFocusEffectRect.top -= heightAdd;
		newFocusEffectRect.bottom += heightAdd;
		newFocusEffectRect.left -= currentPaddingRect.left;
		newFocusEffectRect.right += currentPaddingRect.right;
		newFocusEffectRect.top -= currentPaddingRect.top;
		newFocusEffectRect.bottom += currentPaddingRect.bottom;
		Log.d(TAG, "getNewFocusEffectScaleRect:" + newFocusEffectRect.toString());
	}

	/**
	 * 获取view转换到decorView下的显示出来的rect
	 *
	 * @param view
	 * @param rect
	 */
	private void getGlobalVisibleRectOfDecorView(View view, Rect rect) {
		view.getGlobalVisibleRect(rect);
		Log.d(TAG, "getGlobalVisibleRectOfDecorView    getGlobalVisibleRect：" + rect.toString());
		view.getDrawingRect(rect);
		Log.d(TAG, "getGlobalVisibleRectOfDecorView    getDrawingRect：" + rect.toString());
		ViewGroup rootView = (ViewGroup) this.getRootView();
		rootView.offsetDescendantRectToMyCoords(view, rect);
		Log.d(TAG, "getGlobalVisibleRectOfDecorView    offset：" + rect.toString());

		int widthAdd = Math.round((view.getWidth() * view.getScaleX() - view.getWidth()) / 2);
		int heightAdd = Math.round((view.getHeight() * view.getScaleY() - view.getHeight()) / 2);
		rect.left -= widthAdd;
		rect.right += widthAdd;
		rect.top -= heightAdd;
		rect.bottom += heightAdd;

		ViewParent viewParent = view.getParent();
		while (viewParent != null && !(viewParent instanceof TVRecyclerView)) {
			viewParent = viewParent.getParent();
		}
		if (viewParent != null && viewParent instanceof TVRecyclerView) {
			TVRecyclerView TVRecyclerView = (TVRecyclerView) viewParent;
			Rect rectRecyclerView = new Rect();
			TVRecyclerView.getGlobalVisibleRect(rectRecyclerView);
			Log.d(TAG, "getGlobalVisibleRectOfDecorView    recyclerview：" + rectRecyclerView.toString());
			if (rect.left < rectRecyclerView.left && rect.right > rectRecyclerView.left) {
				rect.left = rectRecyclerView.left;
			}
			if (rect.top < rectRecyclerView.top && rect.bottom > rectRecyclerView.top) {
				rect.top = rectRecyclerView.top;
			}
			if (rect.right > rectRecyclerView.right && rect.left < rectRecyclerView.right) {
				rect.right = rectRecyclerView.right;
			}
			if (rect.bottom > rectRecyclerView.bottom && rect.top < rectRecyclerView.bottom) {
				rect.bottom = rectRecyclerView.bottom;
			}
		}
		Log.d(TAG, "getGlobalVisibleRectOfDecorView    final：" + rect.toString());

	}

	/**
	 * 当在执行动画或者滑动的时候持续更新焦点框位置
	 *
	 * @param focusView
	 * @param padding
	 */
	private void continueChangeFocusEffect(View focusView, Rect padding) {
		Log.d(TAG, "continueChangeFocusEffect:" + focusView.toString());
		getGlobalVisibleRectOfDecorView(focusView, globalVisibleRect);
		globalVisibleRect.left -= padding.left;
		globalVisibleRect.right += padding.right;
		globalVisibleRect.top -= padding.top;
		globalVisibleRect.bottom += padding.bottom;
		Log.d(TAG, "continueChangeFocusEffect:" + globalVisibleRect.toString());
		changeFocusEffectLocation(globalVisibleRect);
	}

	/**
	 * 获取焦点的动画
	 *
	 * @param isHasAnim
	 * @param startScale
	 * @param endScale
	 * @param scaleDuration
	 */
	protected void focusInAnim(boolean isHasAnim, float startScale, float endScale, long scaleDuration) {
		if (isHasAnim) {
			animIn = ValueAnimator.ofFloat(startScale, endScale);
			animIn.setDuration(scaleDuration);
			animIn.setInterpolator(new DecelerateInterpolator());
			animIn.addUpdateListener(focusInUpdateListener);
			animIn.addListener(focusInListener);
			animIn.start();
		} else {
			View view = currentFocusViewRef.get();
			view.setScaleX(endScale);
			view.setScaleY(endScale);
		}
	}

	/**
	 * 失去焦点的动画
	 *
	 * @param isHasAnim
	 * @param startScale
	 * @param endScale
	 * @param scaleDuration
	 */
	protected void focusOutAnim(boolean isHasAnim, float startScale, float endScale, long scaleDuration) {
		View currentFocusView = null;
		if (currentFocusViewRef != null) {
			currentFocusView = currentFocusViewRef.get();
		}
		if (currentFocusView == null) {
			return;
		}
		if (isHasAnim) {
			animOut = ValueAnimator.ofFloat(startScale, endScale);
			animOut.setDuration(scaleDuration);
			animOut.setInterpolator(new DecelerateInterpolator());
			animOut.addUpdateListener(focusOutUpdateListener);
			animOut.addListener(focusOutListener);
			animOut.start();
		} else {
			currentFocusView.setScaleX(endScale);
			currentFocusView.setScaleY(endScale);
		}
	}

	/**
	 * 移动焦点框的动画
	 *
	 * @param startRect
	 * @param endRect
	 */
	protected void tranlateFocusEffect(Rect startRect, Rect endRect, long duration) {
		Log.d(TAG, "tranlateFocusEffect:" + "  " + startRect.toString() + "  " + endRect.toString());
		releaseTranslateAnim();

		if (startRect.equals(endRect)) {
			changeFocusEffectLocation(endRect);
			return;
		}

		ValueAnimator animLeft = ObjectAnimator.ofInt(translateRect, "left", startRect.left, endRect.left);
		ValueAnimator animTop = ObjectAnimator.ofInt(translateRect, "top", startRect.top, endRect.top);
		ValueAnimator animRight = ObjectAnimator.ofInt(translateRect, "right", startRect.right, endRect.right);
		ValueAnimator animBottom = ObjectAnimator.ofInt(translateRect, "bottom", startRect.bottom, endRect.bottom);

		translateAnimatorSet = new AnimatorSet();
		translateAnimatorSet.setDuration(duration);
		translateAnimatorSet.setInterpolator(new DecelerateInterpolator());
		translateAnimatorSet.addListener(translateListener);
		translateAnimatorSet.playTogether(animLeft, animTop, animRight, animBottom);
		translateAnimatorSet.start();
	}

	private class TranslateRect {

		private int	left;

		private int	top;

		private int	right;

		private int	bottom;

		public int getLeft() {
			return left;
		}

		public void setLeft(int left) {
			this.left = left;
			AbsFocusEffectView.this.setLeft(left);
		}

		public int getTop() {
			return top;
		}

		public void setTop(int top) {
			this.top = top;
			AbsFocusEffectView.this.setTop(top);
		}

		public int getRight() {
			return right;
		}

		public void setRight(int right) {
			this.right = right;
			AbsFocusEffectView.this.setRight(right);
		}

		public int getBottom() {
			return bottom;
		}

		public void setBottom(int bottom) {
			this.bottom = bottom;
			AbsFocusEffectView.this.setBottom(bottom);
		}
	}

	/**
	 * 获取焦点的动画监听
	 */
	protected class FocusInUpdateListener implements ValueAnimator.AnimatorUpdateListener {

		@Override public void onAnimationUpdate(ValueAnimator animation) {
			if (currentFocusViewRef != null) {
				View currentFocusView = currentFocusViewRef.get();
				if (currentFocusView != null) {
					float value = (float) animation.getAnimatedValue();
					currentFocusView.setScaleX(value);
					currentFocusView.setScaleY(value);
				}
				if (!getViewFocusTranslateAnim(currentFocusView)) {
					continueChangeFocusEffect(currentFocusView, currentPaddingRect);
				}
			}
		}
	}

	private class FocusInListener implements Animator.AnimatorListener {

		@Override public void onAnimationStart(Animator animation) {

		}

		@Override public void onAnimationEnd(Animator animation) {
			// View currentFocusView = currentFocusViewRef.get();
			// continueChangeFocusEffect(currentFocusView, currentPaddingRect);
			releaseFocusInAnim();
		}

		@Override public void onAnimationCancel(Animator animation) {

		}

		@Override public void onAnimationRepeat(Animator animation) {

		}
	}

	/**
	 * 失去焦点的动画监听
	 */
	protected class FocusOutUpdateListener implements ValueAnimator.AnimatorUpdateListener {

		@Override public void onAnimationUpdate(ValueAnimator animation) {
			if (oldFocusViewRef != null) {
				View currentFocusView = oldFocusViewRef.get();
				if (currentFocusView != null) {
					float value = (float) animation.getAnimatedValue();
					currentFocusView.setScaleX(value);
					currentFocusView.setScaleY(value);
				}
			}
		}
	}

	private class FocusOutListener implements Animator.AnimatorListener {

		@Override public void onAnimationStart(Animator animation) {

		}

		@Override public void onAnimationEnd(Animator animation) {
			releaseFocusOutAnim();
		}

		@Override public void onAnimationCancel(Animator animation) {

		}

		@Override public void onAnimationRepeat(Animator animation) {

		}
	}

	private class TranslateListener implements Animator.AnimatorListener {

		@Override public void onAnimationStart(Animator animation) {

		}

		@Override public void onAnimationEnd(Animator animation) {
			releaseTranslateAnim();
		}

		@Override public void onAnimationCancel(Animator animation) {

		}

		@Override public void onAnimationRepeat(Animator animation) {

		}
	}

	protected void releaseFocusInAnim() {
		if (animIn != null) {
			animIn.removeUpdateListener(focusInUpdateListener);
			animIn.removeListener(focusInListener);
			animIn.end();
			animIn.cancel();
			animIn = null;
		}
	}

	protected void releaseFocusOutAnim() {
		if (animOut != null) {
			animOut.removeUpdateListener(focusOutUpdateListener);
			animOut.removeListener(focusOutListener);
			animOut.end();
			animOut.cancel();
			animOut = null;
		}
	}

	protected void releaseTranslateAnim() {
		if (translateAnimatorSet != null) {
			translateAnimatorSet.removeListener(translateListener);
			translateAnimatorSet.end();
			translateAnimatorSet.cancel();
			translateAnimatorSet = null;
		}
	}

	/**
	 * 奇怪，当recyclerView notify之后，会将本view都设为0
	 *
	 * @param l
	 * @param t
	 * @param r
	 * @param b
	 */
	@Override public void layout(int l, int t, int r, int b) {
		if (l == 0 && t == 0 && r == 0 && b == 0) {
			Log.e(TAG, "layout 0 0 0 0");
		} else {
			super.layout(l, t, r, b);
		}
	}

	/**
	 * 改变焦点的位置
	 */
	protected void changeFocusEffectLocation(Rect newFocusEffectRect) {
		this.layout(newFocusEffectRect.left, newFocusEffectRect.top, newFocusEffectRect.right, newFocusEffectRect.bottom);
	}

	/**
	 * 退出时销毁资源
	 */
	public void destory() {
		unRegisterListener();
		releaseFocusOutAnim();
		releaseFocusInAnim();
		releaseTranslateAnim();
		focusOutUpdateListener = null;
		focusInUpdateListener = null;
		focusOutListener = null;
		focusInListener = null;
		translateListener = null;
		currentPaddingRect = null;
		globalVisibleRect = null;
		translateRect = null;
		if (oldFocusViewRef != null) {
			oldFocusViewRef.clear();
			oldFocusViewRef = null;
		}
		if (currentFocusViewRef != null) {
			currentFocusViewRef.clear();
			currentFocusViewRef = null;
		}
	}

}
