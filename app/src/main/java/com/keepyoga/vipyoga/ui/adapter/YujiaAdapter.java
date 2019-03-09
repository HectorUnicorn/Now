package com.keepyoga.vipyoga.ui.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.view.SimpleDraweeView;
import com.keepyoga.vipyoga.App;
import com.keepyoga.vipyoga.R;
import com.keepyoga.vipyoga.data.model.entity.Yujia;
import com.keepyoga.vipyoga.data.model.entity.Zcool;
import com.keepyoga.vipyoga.ui.activity.BigImageActivity;
import com.keepyoga.vipyoga.utils.NowAppUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by ice on 15/10/26.
 */
public class YujiaAdapter extends BaseListAdapter<Yujia> {

    public Integer IMAGE_WIDTH, IMAGE_HEIGHT;

    /**
     * fit px by px
     */
    public void setImageWidthAndHeight(int columns) {
        int d3 = context.getResources().getDimensionPixelSize(R.dimen.d3);
        IMAGE_WIDTH = (NowAppUtil.getWidth() - d3 * 2) / columns - d3 * 2;
        IMAGE_HEIGHT = IMAGE_WIDTH * 3 / 4;
    }


    public YujiaAdapter(Context context, List<Yujia> contents) {
        super(context, contents);
        setBigViewResId(R.layout.item_empty_head);
        setSmallViewResId(R.layout.item_zcool);
    }

    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;

        switch (viewType) {
            case TYPE_HEADER: {
                view = LayoutInflater.from(parent.getContext())
                        .inflate(getBigViewResId(), parent, false);
                return new CardViewHolder(view, TYPE_HEADER) {
                };
            }
            case TYPE_CELL: {
                view = LayoutInflater.from(parent.getContext())
                        .inflate(getSmallViewResId(), parent, false);
                return new CardViewHolder(view) {
                };
            }
        }
        return null;
    }

    @Override
    protected void bindCellViewHolder(RecyclerView.ViewHolder cellViewHolder, int position) {
        Yujia news = mList.get(position);
        CardViewHolder cardViewHolder = (CardViewHolder) cellViewHolder;
//        Uri imgUri = Uri.parse(news.imgUrl);
//        cardViewHolder.mSimpleDraweeView.setImageURI(imgUri);
        if (!TextUtils.isEmpty(news.imgUrl)) {
            Glide.with(context).load(news.imgUrl).into(cardViewHolder.mSimpleDraweeView);
            cardViewHolder.mSimpleDraweeView.setOnClickListener(v -> {
                BigImageActivity.startThis(context, v, news.imgUrl);
            });
        }
        cardViewHolder.mTitleTv.setText(news.title);
        cardViewHolder.mNameTv.setText("by " + news.name);
//        cardViewHolder.mReadTv.setText(news.readCount + " 看过");
//        cardViewHolder.mLikeTv.setText(news.likeCount + " 赞");
    }

    public static class CardViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.news_list_card_view)
        CardView mCardView;
        @BindView(R.id.title_textView)
        TextView mTitleTv;
        @BindView(R.id.name_textView)
        TextView mNameTv;
        @BindView(R.id.read_textView)
        TextView mReadTv;
        @BindView(R.id.like_textView)
        TextView mLikeTv;
        @BindView(R.id.simpleDraweeView)
        SimpleDraweeView mSimpleDraweeView;

        public CardViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);

            // because us Picasso loadView,so this is not work.
            if (NowAppUtil.isBelowLollipop()) {
                //set round corner
                RoundingParams roundingParams = new RoundingParams();
                int d2 = App.getInstance().getResources().getDimensionPixelSize(R.dimen.d2);
                roundingParams.setCornersRadii(d2, d2, 0, 0);
                mSimpleDraweeView.getHierarchy().setRoundingParams(roundingParams);
            }
        }

        public CardViewHolder(View v, int viewType) {
            super(v);
            if (viewType == TYPE_CELL)
                ButterKnife.bind(this, v);
        }
    }

}
