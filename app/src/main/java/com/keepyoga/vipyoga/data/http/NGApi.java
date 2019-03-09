package com.keepyoga.vipyoga.data.http;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import com.keepyoga.vipyoga.data.model.MonoTea;
import com.keepyoga.vipyoga.data.model.NGMainsResult;

/**
 * Created on 2018/7/29.
 *
 * @author ice
 */
public interface NGApi {

    // curl -H 'Host: dili.bdatu.com' --compressed 'http://dili.bdatu.com/jiekou/mains/p1.html'
    @POST("/jiekou/mains/p1.html")
    @Headers({"HOST: dili.bdatu.com"})
    Observable<NGMainsResult> getMains();


    // curl -H 'Host: dili.bdatu.com' --compressed 'http://dili.bdatu.com/jiekou/albums/a2037.html'
    @GET("jiekou/albums/a{albumId}.html") //date:2018-06-12
    @Headers({"HOST: dili.bdatu.com"})
    Observable<MonoTea> getAlbum(@Path("albumId") String albumId);
}
