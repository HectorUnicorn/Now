package com.keepyoga.vipyoga.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.alibaba.fastjson.JSONArray;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.keepyoga.vipyoga.Constants;
import com.keepyoga.vipyoga.R;
import com.keepyoga.vipyoga.data.model.entity.TeaBean;
import com.keepyoga.vipyoga.ui.BaseSwipeBackCompatActivity;
import com.keepyoga.vipyoga.ui.adapter.MonoImageAdapter;

/**
 * Mono多图list。
 * Created on 2018/6/17.
 *
 * @author ice
 */
public class MonoImageListActivity extends BaseSwipeBackCompatActivity {

    public static Intent getIntent(@NonNull Context context, @NonNull List<TeaBean.AvatarBean> images) {
        String imagesStr = JSONArray.toJSONString(images);
        Intent intent = new Intent(context, MonoImageListActivity.class);
        intent.putExtra(Constants.KEY_IMAGES_STR, imagesStr);
        return intent;
    }

    @BindView(R.id.recyclerView) RecyclerView mRecyclerView;
    private MonoImageAdapter mMonoImageAdapter;
    private List<TeaBean.AvatarBean> mList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_list);
        ButterKnife.bind(this);

        String imagesStr = getIntent().getStringExtra(Constants.KEY_IMAGES_STR);
        mList = JSONArray.parseArray(imagesStr, TeaBean.AvatarBean.class);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mMonoImageAdapter = new MonoImageAdapter(this, mList);
        mRecyclerView.setAdapter(mMonoImageAdapter);
    }
}
