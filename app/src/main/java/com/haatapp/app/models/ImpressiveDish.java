package com.haatapp.app.models;

/**
 * Created by CSS22 on 22-08-2017.
 */

public class ImpressiveDish {
    String name, imgUrl;

    public ImpressiveDish(String name, String url) {
        this.name = name;
        this.imgUrl = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String image) {
        this.imgUrl = image;
    }

}
