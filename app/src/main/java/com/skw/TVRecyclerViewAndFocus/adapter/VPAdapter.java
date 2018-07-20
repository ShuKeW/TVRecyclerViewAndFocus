package com.skw.TVRecyclerViewAndFocus.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.skw.TVRecyclerViewAndFocus.fragment.GridListDiffSpanFragment;
import com.skw.TVRecyclerViewAndFocus.fragment.GridListFragment;
import com.skw.TVRecyclerViewAndFocus.fragment.GridListMiddleFocusFragment;
import com.skw.TVRecyclerViewAndFocus.fragment.GridPageFragment;
import com.skw.TVRecyclerViewAndFocus.fragment.LinearListFragment;
import com.skw.TVRecyclerViewAndFocus.fragment.LinearListHeaderFocusFragment;

/**
 * @创建人 weishukai
 * @创建时间 17/1/10 下午2:39
 * @类描述 一句话说明这个类是干什么的
 */

public class VPAdapter extends FragmentPagerAdapter {

	public VPAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override public Fragment getItem(int position) {
		switch (position) {
			case 0:
				return new LinearListFragment();
			case 1:
				return new GridListFragment();
			case 2:
				return new GridPageFragment();
			case 3:
				return new GridListDiffSpanFragment();
			case 4:
				return new GridListMiddleFocusFragment();
			case 5:
				return new LinearListHeaderFocusFragment();
		}
		return null;
	}

	@Override public int getCount() {
		return 6;
	}
}
