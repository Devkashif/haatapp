package com.haatapp.app.models;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Tamil on 10/11/2017.
 */

public class FavoriteList {

    @SerializedName("available")
    @Expose
    private List<Available> available = null;
    @SerializedName("un_available")
    @Expose
    private List<UnAvailable> unAvailable = null;

    public List<Available> getAvailable() {
        return available;
    }

    public void setAvailable(List<Available> available) {
        this.available = available;
    }

    public FavoriteList withAvailable(List<Available> available) {
        this.available = available;
        return this;
    }

    public List<UnAvailable> getUnAvailable() {
        return unAvailable;
    }

    public void setUnAvailable(List<UnAvailable> unAvailable) {
        this.unAvailable = unAvailable;
    }

    public FavoriteList withUnAvailable(List<UnAvailable> unAvailable) {
        this.unAvailable = unAvailable;
        return this;
    }


}