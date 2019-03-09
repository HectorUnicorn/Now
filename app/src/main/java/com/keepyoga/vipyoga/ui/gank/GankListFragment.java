package com.keepyoga.vipyoga.ui.gank;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.keepyoga.vipyoga.R;
import com.keepyoga.vipyoga.data.model.entity.Gank;
import com.keepyoga.vipyoga.ui.fragment.BaseFragment;

/**
 * Created on 16/8/4.
 *
 * @author ice
 */
public class GankListFragment extends BaseFragment {

    @BindView(R.id.recyclerView) RecyclerView mRecyclerView;

    GankAdapter mGankAdapter;
    ArrayList<Gank> mList = new ArrayList<>();

    public static final String GANK_LIST = "gank_list";

    public static GankListFragment get(ArrayList<Gank> gankList) {
        GankListFragment recyclerViewFragment = new GankListFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(GANK_LIST, gankList);
        recyclerViewFragment.setArguments(bundle);
        return recyclerViewFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_gank, null);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecyclerView.setHasFixedSize(false);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        if (getArguments() != null) {
            mList.clear();
            mList.addAll(getArguments().getParcelableArrayList(GANK_LIST));
        }
        mGankAdapter = new GankAdapter(getActivity(), mList, mRecyclerView);
        mRecyclerView.setAdapter(mGankAdapter);

    }
}
