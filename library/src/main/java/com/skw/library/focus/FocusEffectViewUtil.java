package com.skw.library.focus;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;
import android.view.ViewGroup;

import com.skw.library.R;

/**
 * @创建人 weishukai
 * @创建时间 17/1/20 上午10:38
 * @类描述 一句话说明这个类是干什么的
 */

public class FocusEffectViewUtil {

	private static final String TAG = "FocusEffectViewUtil";

	/**
	 * 给activity添加焦点效果的view
	 * 
	 * @param activity
	 * @return
	 */
	public static AbsFocusEffectView bindFocusEffectView(Activity activity) {
		if (activity == null) {
			return null;
		}
		AbsFocusEffectView absFocusEffectView = new FocusEffectView(activity.getApplicationContext());
		absFocusEffectView.setLayoutParams(new ViewGroup.LayoutParams(0, 0));
		absFocusEffectView.bindActivity(activity);
		return absFocusEffectView;
	}

	public static AbsFocusEffectView bindFocusEffectView(Dialog dialog) {
		if (dialog == null) {
			return null;
		}
		AbsFocusEffectView absFocusEffectView = new FocusEffectView(dialog.getContext());
		absFocusEffectView.setLayoutParams(new ViewGroup.LayoutParams(0, 0));
		absFocusEffectView.bindDialog(dialog);
		return absFocusEffectView;
	}

	public static AbsFocusEffectView bindFocusEffectView(View view) {
		if (view == null) {
			return null;
		}
		AbsFocusEffectView absFocusEffectView = new FocusEffectView(view.getContext());
		absFocusEffectView.setLayoutParams(new ViewGroup.LayoutParams(0, 0));
		absFocusEffectView.bindView(view);
		return absFocusEffectView;
	}

	/**
	 * 更新焦点效果的位置
	 * 
	 * @param view
	 */
	public static void updateFocusEffect(View view) {
		if (view != null) {
			View rootView = view.getRootView();
			View focusEffectView = rootView.findViewById(R.id.focus_effect_view);
			if (focusEffectView != null) {
				AbsFocusEffectView absFocusEffectView = (AbsFocusEffectView) focusEffectView;
				absFocusEffectView.updateLocation();
			}
		}
	}

	public static void setFocusEffectVisible(View view, boolean isVisible) {
		if (view != null) {
			View rootView = view.getRootView();
			View focusEffectView = rootView.findViewById(R.id.focus_effect_view);
			if (focusEffectView != null) {
				AbsFocusEffectView absFocusEffectView = (AbsFocusEffectView) focusEffectView;
				if (isVisible) {
					absFocusEffectView.show();
				} else {
					absFocusEffectView.hide();
				}

			}
		}
	}
}
