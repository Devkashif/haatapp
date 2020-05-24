package com.haatapp.app.models;

/**
 * Created by Tamil on 11/15/2017.
 */
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FeaturedImage {

    @SerializedName("url")
    @Expose
    private String url;
    @SerializedName("position")
    @Expose
    private Integer position;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

}