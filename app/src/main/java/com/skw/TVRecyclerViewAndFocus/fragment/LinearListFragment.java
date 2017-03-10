package com.skw.TVRecyclerViewAndFocus.fragment;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.skw.TVRecyclerViewAndFocus.R;
import com.skw.TVRecyclerViewAndFocus.adapter.LinearListRVAdapter;
import com.skw.TVRecyclerViewAndFocus.model.RecommendModel;
import com.skw.library.TVRecyclerView;
import com.skw.library.decoration.DividerLinearItemDecoration;
import com.skw.library.layoutmanager.TVLinearLayoutManager;

import java.util.ArrayList;
import java.util.List;

/**
 * @创建人 weishukai
 * @创建时间 17/1/10 下午2:45
 * @类描述 一句话说明这个类是干什么的
 */

public class LinearListFragment extends Fragment implements TVRecyclerView.OnLoadMoreListener {

    private String TAG = "LinearListFragment";

    private TVRecyclerView recyclerView;

    private TVLinearLayoutManager linearLayoutManager;

    private LinearListRVAdapter rvAdapter;

    private List<RecommendModel> data = new ArrayList<>();

    private int pageCount = 20, pageNumber;

    private View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_linear_list, container, false);
            initView(view);
        }
        return view;
    }

    private void initView(View view) {
        recyclerView = (TVRecyclerView) view.findViewById(R.id.recyclerview_linear_list);
        recyclerView.setHasFixedSize(true);
//        recyclerView.setLastLineItemHandKey(true, false, true, false);
        linearLayoutManager = new TVLinearLayoutManager(getActivity().getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setOnLoadMoreListener(this);
        recyclerView.addItemDecoration(new DividerLinearItemDecoration(Color.GRAY, 20, Color.GREEN, 20));
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                Log.d(TAG, "onScrollStateChanged");
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                Log.d(TAG, "onScrolled");
            }
        });
        pageNumber = 0;
        createData(pageNumber);

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d(TAG, "onAttach");
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        Log.d(TAG, "setUserVisibleHint:" + isVisibleToUser);
    }

    private void createData(int pageNumber) {
        Log.d(TAG, "createData:" + pageNumber);
        for (int i = pageNumber * pageCount; i < (pageNumber + 1) * pageCount; i++) {
            RecommendModel model = new RecommendModel();
            model.title = "" + i;
            model.imgResColor = getResources().getColor(R.color.color_ac38d5);
            data.add(model);
        }
        if (rvAdapter == null) {
            rvAdapter = new LinearListRVAdapter(getActivity().getApplicationContext(), data);
            recyclerView.setAdapter(rvAdapter);
        } else {
            recyclerView.setLoadMoreComplete();
            rvAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onLoadMore() {
        Toast.makeText(getContext(), "翻页。。。", Toast.LENGTH_SHORT).show();
        pageNumber++;
        createData(pageNumber);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }

}
