package com.keepyoga.vipyoga.ui.fragment;

import android.content.res.Configuration;
import android.support.annotation.NonNull;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;
import jp.wasabeef.recyclerview.animators.FadeInAnimator;
import com.keepyoga.vipyoga.App;
import com.keepyoga.vipyoga.Constants;
import com.keepyoga.vipyoga.R;
import com.keepyoga.vipyoga.data.http.BaseObserver;
import com.keepyoga.vipyoga.data.http.NowApi;
import com.keepyoga.vipyoga.data.model.Mono;
import com.keepyoga.vipyoga.data.model.MonoTea;
import com.keepyoga.vipyoga.data.model.MonoToken;
import com.keepyoga.vipyoga.data.model.entity.TeaBean;
import com.keepyoga.vipyoga.data.model.realm.RealmMono;
import com.keepyoga.vipyoga.ui.activity.WebActivity;
import com.keepyoga.vipyoga.ui.adapter.BaseListAdapter;
import com.keepyoga.vipyoga.ui.adapter.MonoAdapter;
import com.keepyoga.vipyoga.utils.PrefUtil;

/**
 * Created on 2018/6/12.
 *
 * @author ice
 */
public class MonoListFragment extends BaseListFragment<TeaBean.MeowBean, RealmMono> {

    public static MonoListFragment newInstance() {
        return new MonoListFragment();
    }

    @Override
    protected void initRecyclerView() {
        super.initRecyclerView();

        if (getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            //TODO default item_layout
        } else {
            //TODO land item_layout
        }

        mRecyclerView.setItemAnimator(new FadeInAnimator());
        AlphaInAnimationAdapter alphaAdapter = new AlphaInAnimationAdapter(mAdapter);
        ScaleInAnimationAdapter scaleAdapter = new ScaleInAnimationAdapter(alphaAdapter);
        mRecyclerView.setAdapter(scaleAdapter);

        mAdapter.setOnItemClickListener(news -> {
            WebActivity.startThis(getActivity(), news.getUrl(), news.title, news.getCover()
                    , getString(R.string.share_summary_mono));
        });

        if (mList.size() < 1) {
            getData();
        }
    }

    @Override
    public void getData() {
        if (!PrefUtil.isNeedRefresh(Constants.KEY_REFRESH_TIME_MONO)) {
            showList();
            return;
        }
        NowApi.getMonoApi().getToken(new Mono())
                .flatMap((Function<MonoToken, ObservableSource<MonoTea>>) monoToken -> {
                    App.sMonoToken = monoToken.access_token;
                    return NowApi.getMonoApi().getTea(monoToken.access_token, getDate());
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<MonoTea>() {
                    @Override
                    protected void onSucceed(MonoTea result) {
                        mList.clear();
                        addTea(result.afternoon_tea);
                        addTea(result.morning_tea);
                        if (mList.size() > 0)
                            PrefUtil.setRefreshTime(Constants.KEY_REFRESH_TIME_MONO, new Date().getTime());
                        saveData();
                        showList();
                    }

                    @Override
                    protected void onFailed(String msg) {
                        super.onFailed(msg);
                        showList();
                    }
                });
    }

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);

    private String getDate() {
        return DATE_FORMAT.format(new Date());
    }

    private void addTea(TeaBean teaBean) {
        if (teaBean == null || teaBean.entity_list == null) {
            return;
        }

        for (TeaBean.EntityListBean entityListBean : teaBean.entity_list) {
            if (entityListBean.meow != null)
                mList.add(entityListBean.meow);
        }
    }

    @NonNull
    @Override
    public Class<RealmMono> getNowRealmClass() {
        return RealmMono.class;
    }

    @Override
    public BaseListAdapter<TeaBean.MeowBean> getNowAdapter(List<TeaBean.MeowBean> list) {
        return new MonoAdapter(getActivity(), list);
    }

}
