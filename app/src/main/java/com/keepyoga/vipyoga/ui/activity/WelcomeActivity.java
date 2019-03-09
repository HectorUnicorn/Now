package com.keepyoga.vipyoga.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.AppCompatImageView;
import android.text.TextUtils;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.bumptech.glide.Glide;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.keepyoga.vipyoga.App;
import com.keepyoga.vipyoga.BuildConfig;
import com.keepyoga.vipyoga.Constants;
import com.keepyoga.vipyoga.PreferencesHelper;
import com.keepyoga.vipyoga.R;
import com.keepyoga.vipyoga.data.http.BaseHttpObserver;
import com.keepyoga.vipyoga.data.http.NowApi;
import com.keepyoga.vipyoga.data.model.GankMeizhiResult;
import com.keepyoga.vipyoga.data.model.entity.Gank;
import com.keepyoga.vipyoga.ui.BaseCompatActivity;
import com.keepyoga.vipyoga.utils.NowAppUtil;

/**
 * Created on 15/11/22.
 *
 * @author ice
 */
public class WelcomeActivity extends BaseCompatActivity {

    @BindView(R.id.imageView) AppCompatImageView mImageView;
    @BindView(R.id.textView) TextView mTextView;

    private Date mStartDate;
    final long WELCOME_TIME = 1500;
    PreferencesHelper mPreferencesHelper = new PreferencesHelper(App.getInstance());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_welcome);
        ButterKnife.bind(this);

        mStartDate = new Date();
//        String coverImgUrl = mPreferencesHelper.getCoverImage();
//        if (!TextUtils.isEmpty(coverImgUrl)) {
//            Glide.with(this).load(coverImgUrl).into(mImageView);
//        } else {
//            mImageView.setImageResource(R.mipmap.img_first_welcome);
//        }

        String version = String.format(getResources().getString(R.string.app_version), BuildConfig.VERSION_NAME);
        mTextView.setText(pass(version));

        int type = mPreferencesHelper.getHeadImageType();
        switch (type) {
            case Constants.TYPE_GANK_MEIZHI:
                if (NowAppUtil.isWifiConnected() || TextUtils.isEmpty(mPreferencesHelper.getHeadImages()))
                    getCoverImgsThenToMainPage();
                else
                    toMainPage();
                break;
            default:
                toMainPage();
                break;
        }

    }

    private void getCoverImgsThenToMainPage() {
        NowApi.getGankApi().getGankMeizhi()
                .subscribe(new BaseHttpObserver<GankMeizhiResult>(getLifecycle()) {
                    @Override
                    protected void onSucceed(GankMeizhiResult result) {
                        JSONArray jsonArray = new JSONArray();
                        for (Gank item : result.results) {
                            if (item.url != null)
                                jsonArray.add(item.url);
                        }
                        mPreferencesHelper.setHeadImages(jsonArray.toJSONString());
                        toMainPage();
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        toMainPage();
                    }
                });
    }

    private void toMainPage() {
        if (getWaitTime() <= 0)
            go();
        else
            mTextView.postDelayed(this::go, getWaitTime());
    }

    private int getWaitTime() {
        long waitTime = WELCOME_TIME - ((new Date()).getTime() - mStartDate.getTime());
        return (int) waitTime;
    }

    private void go() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivityForResult(intent, Constants.WELCOME_ACTIVITY);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        if (NowAppUtil.isBelowLollipop()) {
            finish();
        } else {
            finishAfterTransition();
        }
    }


}
