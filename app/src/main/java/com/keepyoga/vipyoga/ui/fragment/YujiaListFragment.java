package com.keepyoga.vipyoga.ui.fragment;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.util.Log;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.github.florent37.materialviewpager.MaterialViewPagerHelper;
import com.keepyoga.vipyoga.Constants;
import com.keepyoga.vipyoga.R;
import com.keepyoga.vipyoga.data.http.BaseObserver;
import com.keepyoga.vipyoga.data.http.Urls;
import com.keepyoga.vipyoga.data.model.entity.Yujia;
import com.keepyoga.vipyoga.data.model.realm.RealmYujia;
import com.keepyoga.vipyoga.ui.activity.WebActivity;
import com.keepyoga.vipyoga.ui.adapter.BaseListAdapter;
import com.keepyoga.vipyoga.ui.adapter.YujiaAdapter;
import com.keepyoga.vipyoga.utils.PrefUtil;

import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;
import jp.wasabeef.recyclerview.animators.FadeInAnimator;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created on 15/10/28.
 *
 * @author ice
 */
public class YujiaListFragment extends BaseListFragment<Yujia, RealmYujia> {

    private static final String TAG = "YujiaListFragment";

    public static YujiaListFragment newInstance() {
        return new YujiaListFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);
    }

    @Override
    protected void initRecyclerView() {
//        super.initRecyclerView();

        mAdapter = getNowAdapter(mList);
        if (getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
            ((YujiaAdapter) mAdapter).setImageWidthAndHeight(2);
            mAdapter.setHeadViewCount(2);
        } else {
            mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
            ((YujiaAdapter) mAdapter).setImageWidthAndHeight(3);
            mAdapter.setHeadViewCount(3);
        }

        int dp8 = getResources().getDimensionPixelSize(R.dimen.d3);
        mRecyclerView.setPadding(dp8, 0, dp8, 0);

        mRecyclerView.setItemAnimator(new FadeInAnimator());
        AlphaInAnimationAdapter alphaAdapter = new AlphaInAnimationAdapter(mAdapter);
        ScaleInAnimationAdapter scaleAdapter = new ScaleInAnimationAdapter(alphaAdapter);
        mRecyclerView.setAdapter(scaleAdapter);
        MaterialViewPagerHelper.registerRecyclerView(getActivity(), mRecyclerView);

        mAdapter.setOnItemClickListener(model -> {
            WebActivity.startThis(getActivity(), model.url, model.title, model.imgUrl,
                getString(R.string.share_summary_zcool));
        });

        mAdapter.setOnItemLongClickListener(model -> saveToNote(model.toNow()));

        if (mList.size() < 1) {
            getData();
        }
    }

    @Override
    public void getData() {
        if (!PrefUtil.isNeedRefresh(Constants.KEY_REFRESH_TIME_ZCOOL)) {
            showList();
            return;
        }

        Observable
            .create((ObservableOnSubscribe<Document>) observableEmitter -> {
                try {
//                    WebClient wc = new WebClient(BrowserVersion.CHROME);
//                    // 启用JS解释器，默认为true
//                    wc.getOptions().setJavaScriptEnabled(true);
//                    // 禁用css支持
//                    wc.getOptions().setCssEnabled(false);
//                    // js运行错误时，是否抛出异常
//                    wc.getOptions().setThrowExceptionOnScriptError(false);
//                    // 状态码错误时，是否抛出异常
//                    wc.getOptions().setThrowExceptionOnFailingStatusCode(false);
//                    // 设置连接超时时间 ，这里是5S。如果为0，则无限期等待
//                    wc.getOptions().setTimeout(5000);
//                    // 是否允许使用ActiveX
//                    wc.getOptions().setActiveXNative(false);
//                    // 等待js时间
//                    wc.waitForBackgroundJavaScript(1 * 1000);
//                    // 设置Ajax异步处理控制器即启用Ajax支持
//                    wc.setAjaxController(new NicelyResynchronizingAjaxController());
//                    // 不跟踪抓取
//                    wc.getOptions().setDoNotTrackEnabled(false);
//                    HtmlPage page = wc.getPage(Urls.YUJIAWANG_URL);
//                    // 以xml的形式获取响应文本
//                    String pageXml = page.asXml();
//                    Document document = Jsoup.parse(pageXml);

                    Document document = Jsoup.connect(Urls.YUJIAWANG_URL).get();
                    observableEmitter.onNext(document);

                } catch (Exception e) {

                    try {
                        Document document = Jsoup.connect(Urls.YUJIAWANG_URL).get();
                        observableEmitter.onNext(document);
                    } catch (IOException e1) {
                        e1.printStackTrace();
                        observableEmitter.onError(new Throwable("zcool get error"));
                    }
                    e.printStackTrace();
                }
            })
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.computation())
            .doOnNext(document -> {
                Log.i("xyz", "doOnNext");
                mList.clear();
                PrefUtil.setRefreshTime(Constants.KEY_REFRESH_TIME_ZCOOL, new Date().getTime());

                // Links
                Elements userWorks = document.body().getElementsByClass("aui-list-item aui-padded-l-10");

                for (Element element : userWorks) {
                    Yujia yujia = new Yujia();
                    yujia.url = pass(Urls.YUJIAWANG_BASE_URL +
                        element
                            .select("a")
                            .first()
                            .attr("href"));
                    yujia.imgUrl = pass(Urls.YUJIAWANG_BASE_URL +
                        element
                            .select("img")
                            .first()
                            .attr("src"));
                    Log.d(TAG, "img.url=" + yujia.imgUrl);
                    yujia.title = pass(element
                        .select("div.aui-list-item-title")
                        .first()
                        .ownText());
                    yujia.name = pass(element
                        .select("p.aui-ellipsis")
                        .first()
                        .ownText());
                    mList.add(yujia);
                }
            })
            .observeOn(AndroidSchedulers.mainThread())
            .doOnDispose(this::showList)
            .subscribe(new BaseObserver<Document>(getLifecycle()) {
                @Override
                protected void onSucceed(Document result) {
                    saveData();
                }
            });
    }

    @NonNull
    @Override
    public Class<RealmYujia> getNowRealmClass() {
        return RealmYujia.class;
    }

    @Override
    public BaseListAdapter<Yujia> getNowAdapter(List<Yujia> list) {
        return new YujiaAdapter(getActivity(), list);
    }

}
