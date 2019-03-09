package com.keepyoga.vipyoga.data.model.realm;

import com.google.gson.annotations.SerializedName;

import com.keepyoga.vipyoga.data.model.entity.Yujia;
import com.keepyoga.vipyoga.data.model.entity.Zcool;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Created by ice on 16/4/13 10:56.
 */
public class RealmYujia extends RealmObject implements AbsNowRealmObject<Yujia> {

    @PrimaryKey
    @Required
    @SerializedName("pk")
    public String pk;

    @SerializedName("url")
    public String url;

    @SerializedName("imgUrl")
    public String imgUrl;

    @SerializedName("title")
    public String title;

    @SerializedName("name")
    public String name;


    @Override
    public Yujia toEntity() {
        Yujia yujia = new Yujia();
        yujia.url = url;
        yujia.imgUrl = imgUrl;
        yujia.title = title;
        yujia.name = name;
        return yujia;
    }

    @Override
    public void setFromEntity(Yujia zcool) {
        url = zcool.url;
        imgUrl = zcool.imgUrl;
        title = zcool.title;
        name = zcool.name;
        pk = getPk(zcool);
    }

    public static String getPk(Yujia yujia) {
        return yujia.url + yujia.title;
    }

}
