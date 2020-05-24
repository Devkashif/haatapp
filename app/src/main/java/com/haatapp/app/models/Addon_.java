package com.haatapp.app.models;

/**
 * Created by Tamil on 11/10/2017.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Addon_ {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("shop_id")
    @Expose
    private Integer shopId;
    @SerializedName("deleted_at")
    @Expose
    private Object deletedAt;

    private  boolean isChecked=false;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Addon_ withId(Integer id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Addon_ withName(String name) {
        this.name = name;
        return this;
    }

    public Integer getShopId() {
        return shopId;
    }

    public void setShopId(Integer shopId) {
        this.shopId = shopId;
    }

    public Addon_ withShopId(Integer shopId) {
        this.shopId = shopId;
        return this;
    }

    public void setChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }

    public boolean getChecked() {
        return isChecked;
    }
    public Object getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Object deletedAt) {
        this.deletedAt = deletedAt;
    }

    public Addon_ withDeletedAt(Object deletedAt) {
        this.deletedAt = deletedAt;
        return this;
    }

}