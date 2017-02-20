package com.skw.TVRecyclerViewAndFocus.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import com.skw.TVRecyclerViewAndFocus.R;
import com.skw.library.focus.AbsFocusEffectView;
import com.skw.library.focus.FocusEffectViewUtil;

/**
 * @创建人 weishukai
 * @创建时间 17/2/6 下午4:24
 * @类描述 一句话说明这个类是干什么的
 */

public class NoScrollActivity extends Activity implements View.OnClickListener {

	private String				TAG	= "NoScrollActivity";

	private AbsFocusEffectView	absFocusEffectView;

	private int[]				ids	= new int[] { R.id.line1_1, R.id.line1_2, R.id.line1_3, R.id.line2_1, R.id.line2_2_1, R.id.line2_2_2, R.id.line2_2_3, R.id.line2_2_4, R.id.line2_3, R.id.line2_4_1,
			R.id.line2_4_2, R.id.line2_4_3, R.id.line2_4_4, R.id.line3_1, R.id.line3_2, R.id.line3_3, R.id.line3_4, R.id.line3_5 };

	@Override protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_no_scroll);
		absFocusEffectView = FocusEffectViewUtil.bindFocusEffectView(this);
		for (int i = 0; i < ids.length; i++) {
			View view = findViewById(ids[i]);
			view.setTag(R.id.focus_type, AbsFocusEffectView.FocusType.FOCUS_POSTER);
			view.setTag(R.id.focus_type_is_translate, true);
			view.setTag(R.id.focus_type_is_scale_anim, true);
			view.setOnClickListener(this);
		}
	}

	@Override public void onClick(View v) {
		switch (v.getId()) {
			case R.id.line1_3:
				showDefaultDialog();
				break;
			case R.id.line2_3:
				showCustomDialog();
				break;
			case R.id.line2_1:
				showPopupWindow();
				break;
			default:
				startActivity(new Intent(getApplicationContext(), MainActivity.class));
				// startActivity(new Intent(getApplicationContext(),
				// SystemFocusActivity.class));
				break;
		}
	}

	private void showDefaultDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("标题");
		builder.setMessage("内容内容内容内容内容");
		// builder.setPositiveButton("确定", new DialogInterface.OnClickListener()
		// {
		//
		// @Override public void onClick(DialogInterface dialog, int which) {
		// dialog.cancel();
		// }
		// });
		// builder.setNegativeButton("取消", new DialogInterface.OnClickListener()
		// {
		//
		// @Override public void onClick(DialogInterface dialog, int which) {
		// dialog.cancel();
		// }
		// });
		AlertDialog dialog = builder.create();
		dialog.setButton(DialogInterface.BUTTON_POSITIVE, "确定", new DialogInterface.OnClickListener() {

			@Override public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {

			@Override public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		// FocusEffectViewUtil.bindFocusEffectView(dialog);
		dialog.show();
		// dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTag(R.id.focus_type,
		// AbsFocusEffectView.FocusType.FOCUS_NORMAL);
		// dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTag(R.id.focus_type,
		// AbsFocusEffectView.FocusType.FOCUS_NORMAL);
	}

	private void showCustomDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		LayoutInflater layoutInflater = LayoutInflater.from(getApplicationContext());

		View view = layoutInflater.inflate(R.layout.dialog_view, null);
		View child = view.findViewById(R.id.item1);
		child.setTag(R.id.focus_type, AbsFocusEffectView.FocusType.FOCUS_POSTER);
		child.setTag(R.id.focus_type_is_scale_anim, true);
		child.setTag(R.id.focus_type_is_translate, true);
		child = view.findViewById(R.id.item2);
		child.setTag(R.id.focus_type, AbsFocusEffectView.FocusType.FOCUS_POSTER);
		child.setTag(R.id.focus_type_is_scale_anim, true);
		child.setTag(R.id.focus_type_is_translate, true);
		child = view.findViewById(R.id.item3);
		child.setTag(R.id.focus_type, AbsFocusEffectView.FocusType.FOCUS_POSTER);
		child.setTag(R.id.focus_type_is_scale_anim, true);
		child.setTag(R.id.focus_type_is_translate, true);
		child = view.findViewById(R.id.item4);
		child.setTag(R.id.focus_type, AbsFocusEffectView.FocusType.FOCUS_POSTER);
		child.setTag(R.id.focus_type_is_scale_anim, true);
		child.setTag(R.id.focus_type_is_translate, true);

		builder.setView(view);
		AlertDialog dialog = builder.create();
		FocusEffectViewUtil.bindFocusEffectView(dialog);
		dialog.show();
		// dialog.setContentView(R.layout.activity_no_scroll);
	}

	private void showPopupWindow() {
		View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.dialog_view, null);
		View child = view.findViewById(R.id.item1);
		child.setTag(R.id.focus_type, AbsFocusEffectView.FocusType.FOCUS_POSTER);
		child.setTag(R.id.focus_type_is_scale_anim, true);
		child.setTag(R.id.focus_type_is_translate, true);
		child = view.findViewById(R.id.item2);
		child.setTag(R.id.focus_type, AbsFocusEffectView.FocusType.FOCUS_POSTER);
		child.setTag(R.id.focus_type_is_scale_anim, true);
		child.setTag(R.id.focus_type_is_translate, true);
		child = view.findViewById(R.id.item3);
		child.setTag(R.id.focus_type, AbsFocusEffectView.FocusType.FOCUS_POSTER);
		child.setTag(R.id.focus_type_is_scale_anim, true);
		child.setTag(R.id.focus_type_is_translate, true);
		child = view.findViewById(R.id.item4);
		child.setTag(R.id.focus_type, AbsFocusEffectView.FocusType.FOCUS_POSTER);
		child.setTag(R.id.focus_type_is_scale_anim, true);
		child.setTag(R.id.focus_type_is_translate, true);

		FocusEffectViewUtil.bindFocusEffectView(view);

		PopupWindow popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
		popupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_popupwindow));
		// popupWindow.setAnimationStyle(R.style.popWinAnim);
		popupWindow.setTouchable(false);
		popupWindow.setOutsideTouchable(false);
		popupWindow.setFocusable(true);

		View parent = findViewById(R.id.parent);
		popupWindow.showAtLocation(parent, Gravity.BOTTOM, 0, 0);

	}

	@Override protected void onDestroy() {
		if (absFocusEffectView != null) {
			absFocusEffectView.destory();
		}
		super.onDestroy();
	}
}
