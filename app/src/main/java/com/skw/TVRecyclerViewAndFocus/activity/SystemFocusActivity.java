package com.skw.TVRecyclerViewAndFocus.activity;

import android.app.Activity;
import android.os.Bundle;

import com.skw.TVRecyclerViewAndFocus.R;

/**
 * @创建人 weishukai
 * @创建时间 17/1/23 下午2:59
 * @类描述 固定view焦点测试，即不会滚动
 */

public class SystemFocusActivity extends Activity {

	@Override protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_system_focus);
	}

}
