package com.keepyoga.vipyoga.data.model.entity;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;


/**
 * @author guojialin
 * @date 2019/3/8
 */
public class Yujia implements Serializable, INow {

    @SerializedName("url")
    public String url;
    @SerializedName("imgUrl")
    public String imgUrl;
    @SerializedName("title")
    public String title;
    @SerializedName("name")
    public String name;
    @SerializedName("category")
    public String category;

    @Override
    public NowItem toNow() {
        NowItem nowItem = new NowItem();
        nowItem.url = this.url;
        nowItem.collectedDate = new Date().getTime();
        nowItem.imageUrl = this.imgUrl;
        nowItem.title = this.title;
        nowItem.subTitle = this.category;
        nowItem.from = "YUJIA";
        return nowItem;
    }
}
