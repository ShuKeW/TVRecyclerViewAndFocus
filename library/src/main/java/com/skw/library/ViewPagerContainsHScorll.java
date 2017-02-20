package com.skw.library;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.KeyEvent;

/**
 * @创建人 weishukai
 * @创建时间 17/1/11 下午8:08
 * @类描述 内部包含左右滑动的控件的ViewPager，防止左右切换时切换了viewPager
 */

public class ViewPagerContainsHScorll extends ViewPager {

	public ViewPagerContainsHScorll(Context context) {
		super(context);
	}

	public ViewPagerContainsHScorll(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override public boolean executeKeyEvent(KeyEvent event) {
		return false;
	}
}
