package com.skw.TVRecyclerViewAndFocus.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.skw.TVRecyclerViewAndFocus.R;
import com.skw.TVRecyclerViewAndFocus.adapter.GridPageHRVAdapter;
import com.skw.TVRecyclerViewAndFocus.model.RecommendModel;
import com.skw.library.PageTVRecyclerView;
import com.skw.library.decoration.DividerGridItemDecoration;

import java.util.ArrayList;
import java.util.List;

/**
 * @创建人 weishukai
 * @创建时间 17/1/10 下午2:45
 * @类描述 一句话说明这个类是干什么的
 */

public class GridPageFragment extends Fragment implements PageTVRecyclerView.OnPageChangeListener {

	private String					TAG			= "GridPageFragment";

	private PageTVRecyclerView recyclerView;

	private GridLayoutManager		gridLayoutManager;

	private GridPageHRVAdapter		rvAdapter;

	private List<RecommendModel>	data		= new ArrayList<>();

	private int						pageCount	= 80, pageNumber;

	private boolean					isLoadData;

	@Nullable @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		Log.d(TAG, "onCreateView");
		View view = inflater.inflate(R.layout.fragment_grid_page, container, false);
		initView(view);
		return view;
	}

	private void initView(View view) {
		recyclerView = (PageTVRecyclerView) view.findViewById(R.id.recyclerview_grid_page);
		recyclerView.setHasFixedSize(true);
		recyclerView.setLastLineItemHandKey(true, false, true, false);
		recyclerView.setPageSize(1792);
		// recyclerView.setDuration(2000);
		gridLayoutManager = new GridLayoutManager(getActivity().getApplicationContext(), 4, LinearLayoutManager.HORIZONTAL, false);
		recyclerView.setLayoutManager(gridLayoutManager);
		recyclerView.addItemDecoration(new DividerGridItemDecoration(28, 28, 0));
		recyclerView.setOnPageChangeListener(this);
		gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {

			@Override public int getSpanSize(int position) {
				switch (rvAdapter.getItemViewType(position)) {
					case 1:
					case 2:
						return 2;
					default:
						return 1;
				}
			}
		});
		isLoadData = false;
		pageNumber = 0;
		createData(pageNumber);
	}

	@Override public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "onCreate");
	}

	@Override public void onAttach(Context context) {
		super.onAttach(context);
		Log.d(TAG, "onAttach");
	}

	@Override public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		Log.d(TAG, "setUserVisibleHint:" + isVisibleToUser);
	}

	private void createData(int pageNumber) {
		Log.d(TAG, "createData:" + pageNumber);
		isLoadData = true;
		RecommendModel model;
		for (int i = pageNumber * pageCount; i < (pageNumber + 1) * pageCount; i++) {
			model = new RecommendModel();
			if (i == 0) {
				model.type = 1;
			}
			if (i == 1) {
				model.type = 2;
			}
			// if (i == (pageNumber + 1) * pageCount - 1) {
			// model.type = 2;
			// }
			// if (i == (pageNumber + 1) * pageCount - 2) {
			// model.type = 1;
			// }
			model.title = "" + i;
			model.imgResColor = getResources().getColor(R.color.color_eb641e);
			data.add(model);
		}
		if (rvAdapter == null) {
			rvAdapter = new GridPageHRVAdapter(getActivity().getApplicationContext(), data);
			recyclerView.setAdapter(rvAdapter);
		} else {
			recyclerView.setLoadMoreComplete();
			rvAdapter.notifyDataSetChanged();
		}
		isLoadData = false;
	}

	@Override public void onPageChange(boolean isFirstPage, boolean isLastPage) {
		if (isFirstPage && isLastPage) {
			Toast.makeText(getContext(), "既是第一页也是最后一页", Toast.LENGTH_SHORT).show();
		} else if (isFirstPage) {
			Toast.makeText(getContext(), "第一页", Toast.LENGTH_SHORT).show();
		} else if (isLastPage) {
			Toast.makeText(getContext(), "最后一页", Toast.LENGTH_SHORT).show();
			// if (!isLoadData) {
			// pageNumber++;
			// createData(pageNumber);
			// }
		}
	}

	@Override public void onDestroyView() {
		super.onDestroyView();
		Log.d(TAG, "onDestroyView");
	}

	@Override public void onDestroy() {
		super.onDestroy();
		Log.d(TAG, "onDestroy");
	}

}
