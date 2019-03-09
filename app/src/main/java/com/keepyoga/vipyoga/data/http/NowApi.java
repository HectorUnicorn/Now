package com.keepyoga.vipyoga.data.http;

import android.text.TextUtils;

import com.orhanobut.logger.Logger;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import io.reactivex.schedulers.Schedulers;
import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import com.keepyoga.vipyoga.App;

/*
 * Thanks to
 * https://github.com/amitshekhariitbhu/RxJava2-Android-Samples
 *
 * Created by ice on 3/13/16.
 */
public final class NowApi {

    public static final String TAG = "RetrofitUtil";

    public static ZhihuApi getZhihuApi() {
        return get(Urls.ZHIHU_NEWS).build().create(ZhihuApi.class);
    }


    public static GankApi getGankApi() {
        return get(Urls.GANK).build().create(GankApi.class);
    }

    public static GankApi getGankApi(boolean isCache) {
        Cache cache = null;
        if (isCache)
            cache = new Cache(App.getInstance().getCacheDir(), 1024 * 1024 * 10);
        return get(Urls.GANK, cache).build().create(GankApi.class);
    }

    public static DribbbleApi getDribbbleApi() {
        return get(Urls.AUTH_ENDPOINT).build().create(DribbbleApi.class);
    }

    public static MonoApi getMonoApi() {
        return get(Urls.MONO).build().create(MonoApi.class);
    }


    private static Retrofit.Builder get(String baseUrl) {
        return get(baseUrl, null);
    }

    private static Retrofit.Builder get(String baseUrl, Cache cache) {
        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS);
        if (cache != null) {
            /*TODO 缓存优化（缓存不限时）*/
            httpClientBuilder.cache(cache);
            httpClientBuilder.addInterceptor(chain -> {
                Request request = chain.request();
                Logger.i(TAG, "url: " + request.url().toString());
                return chain.proceed(request);
            });
            httpClientBuilder.addNetworkInterceptor(chain -> {
                Request request = chain.request();
                String cacheControl = request.cacheControl().toString();
                if (TextUtils.isEmpty(cacheControl)) {
                    int cacheSeconds = 60 * 60 * 24 * 365;
                    cacheControl = "public, max-age=" + cacheSeconds;
                }
                Response response = chain.proceed(request);
                return response.newBuilder()
                        .header("Cache-Control", cacheControl)
                        .build();
            });
        } else {
            httpClientBuilder.addNetworkInterceptor(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request request = chain.request();
                    Logger.i(request.toString());
                    Response response = chain.proceed(request);
                    Logger.i(response.toString());
                    return response;
                }
            });
        }
        Retrofit.Builder builder = new Retrofit.Builder();
        builder.client(httpClientBuilder.build())
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
//                .addCallAdapterFactory(RxAndroidCallAdapterFactory.createWithScheduler(Schedulers.io(), AndroidSchedulers.mainThread()))
        ;

        return builder;
    }

}
