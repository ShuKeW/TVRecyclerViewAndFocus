package com.skw.TVRecyclerViewAndFocus.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.skw.TVRecyclerViewAndFocus.R;
import com.skw.TVRecyclerViewAndFocus.adapter.VPAdapter;
import com.skw.library.ViewPagerContainsHScorll;
import com.skw.library.focus.AbsFocusEffectView;
import com.skw.library.focus.FocusEffectViewUtil;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends FragmentActivity {

	private String						TAG		= "MainActivity";

	private AbsFocusEffectView			absFocusEffectView;

	private ViewPagerContainsHScorll	viewPager;

	private RelativeLayout				tabContent;

	private int[]						tabIds	= new int[] { R.id.tab1, R.id.tab2, R.id.tab3, R.id.tab4, R.id.tab5 , R.id.tab6 };

	private List<TextView>				tabs	= new ArrayList<>();

	// private AbsFocusView mFocusView;

	@Override protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		// mFocusView = FocusViewUtil.bindFocusView(this);
		absFocusEffectView = FocusEffectViewUtil.bindFocusEffectView(this);
		initView();

	}

	private void initView() {
		viewPager = (ViewPagerContainsHScorll) findViewById(R.id.viewPager);
		VPAdapter vpAdapter = new VPAdapter(getSupportFragmentManager());
		viewPager.setAdapter(vpAdapter);
		viewPager.setOffscreenPageLimit(5);
		tabContent = (RelativeLayout) findViewById(R.id.tabContent);
		tabContent.setOnFocusChangeListener(new View.OnFocusChangeListener() {

			@Override public void onFocusChange(View v, boolean hasFocus) {
				Log.d(TAG, "onFocusChange  tabContent");
				if (hasFocus) {
					tabs.get(viewPager.getCurrentItem()).requestFocus();
				}
			}
		});
		for (int i = 0; i < tabIds.length; i++) {
			TextView tab = (TextView) findViewById(tabIds[i]);
			tab.setTag(i);
			tab.setTag(R.id.focus_type, AbsFocusEffectView.FocusType.FOCUS_TAB_VIEW);
			tab.setTag(R.id.focus_type_is_translate, true);
			tab.setTag(R.id.focus_type_is_scale_anim, true);
			tab.setOnFocusChangeListener(new View.OnFocusChangeListener() {

				@Override public void onFocusChange(View v, boolean hasFocus) {
					if (hasFocus) {
						int position = (int) v.getTag();
						Log.d(TAG, "onFocusChange  tab:" + position);
						viewPager.setCurrentItem(position);
					}
				}
			});
			tabs.add(tab);
		}
	}

	@Override protected void onDestroy() {
		absFocusEffectView.destory();
		super.onDestroy();
	}
}
