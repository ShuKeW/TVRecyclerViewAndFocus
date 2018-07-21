package com.skw.library.focus;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewParent;

import com.skw.library.R;
import com.skw.library.TVRecyclerView;

/**
 * @创建人 weishukai
 * @创建时间 17/1/20 上午11:38
 * @类描述 一句话说明这个类是干什么的
 */

public class FocusEffectView extends AbsFocusEffectView {

	private String		TAG				= "FocusEffectView";

	public static final float	scale			= 1.145f;

	private final long	scaleDuration	= 200;

	/**
	 * padding注意有效果图的边框的宽度
	 */

	private Rect		focusNormalPaddingRect;

	private Rect		focusTabViewPaddingRect;

	private Rect		focusPosterPaddingRect;

	public FocusEffectView(Context context) {
		super(context);
		init(context);
	}

	public FocusEffectView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public FocusEffectView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context);
	}

	private void init(Context context) {
		Resources resources = context.getResources();
		int normalPadding = resources.getDimensionPixelSize(R.dimen.focus_normal_padding) + resources.getDimensionPixelSize(R.dimen.focus_stroke);
		int tabViewPaddingH = resources.getDimensionPixelSize(R.dimen.focus_tab_view_padding_h) + resources.getDimensionPixelSize(R.dimen.focus_stroke);
		int tabViewPaddingV = resources.getDimensionPixelSize(R.dimen.focus_tab_view_padding_v) + resources.getDimensionPixelSize(R.dimen.focus_stroke);
		int posterPadding = resources.getDimensionPixelSize(R.dimen.focus_poster_padding) + resources.getDimensionPixelSize(R.dimen.focus_stroke);
		focusNormalPaddingRect = new Rect(normalPadding, normalPadding, normalPadding, normalPadding);
		focusTabViewPaddingRect = new Rect(tabViewPaddingH, tabViewPaddingV, tabViewPaddingH, tabViewPaddingV);
		focusPosterPaddingRect = new Rect(posterPadding, posterPadding, posterPadding, posterPadding);

	}

	@Override protected void onFocusOut() {
		Log.d(TAG, "onFocusOut");
		releaseFocusOutAnim();
		releaseFocusInAnim();

		if (oldFocusViewRef != null && oldFocusViewRef.get() != null) {
			View view = oldFocusViewRef.get();
			view.setScaleX(1.0f);
			view.setScaleY(1.0f);
			oldFocusViewRef.clear();
		}
		if (currentFocusViewRef != null) {
			View currentFocus = currentFocusViewRef.get();
			if (currentFocus != null) {
				Log.d(TAG, "onFocusOut:" + currentFocus.toString());
				switch (getViewFocusType(currentFocus)) {
					case FocusType.FOCUS_NORMAL:
						focusOutAnim(getViewFocusScaleAnim(currentFocus), scale, 1.0f, scaleDuration);
						break;
					case FocusType.FOCUS_POSTER:
						focusOutAnim(getViewFocusScaleAnim(currentFocus), scale, 1.0f, scaleDuration);
						break;
					case FocusType.FOCUS_TAB_VIEW:
						focusOutAnim(getViewFocusScaleAnim(currentFocus), scale, 1.0f, scaleDuration);
						break;
				}
			}
		}
	}

	@Override protected void onFocusIn() {
		View currentFocusView = currentFocusViewRef.get();

		/**
		 * 现在只做了一层处理，最好是给parent设置tag，表示是bringToFront的最终层
		 */
		ViewParent viewParent = currentFocusView.getParent();
		if (viewParent != null && !(viewParent instanceof TVRecyclerView)) {
			currentFocusView.bringToFront();
			((View) viewParent).postInvalidate();
		}

		Log.d(TAG, "onFocusIn:" + currentFocusView.toString());

		int focusRes = R.drawable.focus_normal;

		Rect newFocusEffectRect = new Rect();

		switch (getViewFocusType(currentFocusView)) {
			case FocusType.FOCUS_NORMAL:
				focusRes = R.drawable.focus_normal;
				setPaddingRect(focusNormalPaddingRect);
				break;
			case FocusType.FOCUS_POSTER://
				focusRes = R.drawable.focus_normal;
				setPaddingRect(focusPosterPaddingRect);
				break;
			case FocusType.FOCUS_TAB_VIEW://
				focusRes = R.drawable.focus_tab_view;
				setPaddingRect(focusTabViewPaddingRect);
				break;

		}

		if (getViewFocusTranslateAnim(currentFocusView)) {
			getNewFocusEffectScaleRect(currentFocusView, newFocusEffectRect, scale);
		} else {
			if (getViewFocusScaleAnim(currentFocusView)) {
				// 如果没有移动动画，那么焦点框和初始一样，让让放大动画的时候来撑大
				getNewFocusEffectScaleRect(currentFocusView, newFocusEffectRect, 1.0f);
			} else {
				// 如果没有放大动画，直接得到scale后的rect
				getNewFocusEffectScaleRect(currentFocusView, newFocusEffectRect, scale);
			}
		}
		focusInAnim(getViewFocusScaleAnim(currentFocusView), 1.0f, scale, scaleDuration);

		if (getViewFocusTranslateAnim(currentFocusView)) {
			Rect oldFocusEffectRect = new Rect();
			this.getGlobalVisibleRect(oldFocusEffectRect);
			tranlateFocusEffect(oldFocusEffectRect, newFocusEffectRect, scaleDuration);
		} else {
			changeFocusEffectLocation(newFocusEffectRect);
		}
		this.setBackgroundResource(focusRes);
	}

	@Override public void destory() {
		super.destory();
		focusNormalPaddingRect = null;
		focusTabViewPaddingRect = null;
		focusPosterPaddingRect = null;
	}
}
