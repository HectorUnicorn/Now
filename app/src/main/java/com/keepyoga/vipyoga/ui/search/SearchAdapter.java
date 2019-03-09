package com.keepyoga.vipyoga.ui.search;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.keepyoga.vipyoga.R;
import com.keepyoga.vipyoga.data.model.entity.NowItem;
import com.keepyoga.vipyoga.ui.activity.BigImageActivity;
import com.keepyoga.vipyoga.ui.adapter.BaseRecyclerViewAdapter;

/**
 * Created on 2018/9/23.
 *
 * @author ice
 */
public class SearchAdapter extends BaseRecyclerViewAdapter<NowItem> {
    public SearchAdapter(Context context, List<NowItem> list, RecyclerView recyclerView) {
        super(context, list, recyclerView);
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.item_search;
    }

    @Override
    protected RecyclerView.ViewHolder getViewHolder(View view) {
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        NowItem item = mList.get(position);
        MyViewHolder myViewHolder = (MyViewHolder) holder;
        myViewHolder.mFromTv.setText(item.from);
        myViewHolder.mTitleTv.setText(item.title);
        final String imageUrl = item.imageUrl;
        Glide.with(context)
                .load(imageUrl)
                .into(myViewHolder.mImageIv);
        myViewHolder.mImageIv.setOnClickListener(v -> BigImageActivity.startThis(context, v, imageUrl));
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.title_tv) TextView mTitleTv;
        @BindView(R.id.from_tv) TextView mFromTv;
        @BindView(R.id.image_iv) AppCompatImageView mImageIv;

        MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
