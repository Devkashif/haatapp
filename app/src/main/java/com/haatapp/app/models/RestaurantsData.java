
package com.haatapp.app.models;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RestaurantsData {

    @SerializedName("shops")
    @Expose
    private List<Shop> shops = new ArrayList<>();
    @SerializedName("banners")
    @Expose
    private List<Banner> banners = new ArrayList<>();

    public List<Shop> getShops() {
        return shops;
    }

    public void setShops(List<Shop> shops) {
        this.shops = shops;
    }

    public List<Banner> getBanners() {
        return banners;
    }

    public void setBanners(List<Banner> banners) {
        this.banners = banners;
    }

}
