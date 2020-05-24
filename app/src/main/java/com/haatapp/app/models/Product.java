
package com.haatapp.app.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Product {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("shop_id")
    @Expose
    private Integer shopId;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("position")
    @Expose
    private Integer position;
    @SerializedName("food_type")
    @Expose
    private String foodType;
    @SerializedName("avalability")
    @Expose
    private Integer avalability;
    @SerializedName("max_quantity")
    @Expose
    private Integer maxQuantity;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("prices")
    @Expose
    private Prices prices;
    @SerializedName("featured_images")
    @Expose
    private List<FeaturedImage> featuredImages = null;
    @SerializedName("images")
    @Expose
    private List<Image> images = null;
    @SerializedName("addons")
    @Expose
    private List<Addon> addons = null;
    @SerializedName("cart")
    @Expose
    private List<Cart> cart;
    @SerializedName("shop")
    @Expose
    private Shop shop;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getShopId() {
        return shopId;
    }

    public void setShopId(Integer shopId) {
        this.shopId = shopId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public String getFoodType() {
        return foodType;
    }

    public void setFoodType(String foodType) {
        this.foodType = foodType;
    }

    public Integer getAvalability() {
        return avalability;
    }

    public void setAvalability(Integer avalability) {
        this.avalability = avalability;
    }

    public Integer getMaxQuantity() {
        return maxQuantity;
    }

    public void setMaxQuantity(Integer maxQuantity) {
        this.maxQuantity = maxQuantity;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Prices getPrices() {
        return prices;
    }

    public void setPrices(Prices prices) {
        this.prices = prices;
    }

    public List<FeaturedImage> getFeaturedImages() {
        return featuredImages;
    }

    public void setFeaturedImages(List<FeaturedImage> featuredImages) {
        this.featuredImages = featuredImages;
    }

    public List<Cart> getCart() {
        return cart;
    }

    public void setCart(List<Cart> cart) {
        this.cart = cart;
    }

    public List<Addon> getAddons() {
        return addons;
    }

    public void setAddons(List<Addon> addons) {
        this.addons = addons;
    }
    public List<Image> getImages() {
        return images;
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }

    public Shop getShop() {
        return shop;
    }

    public void setShop(Shop shop) {
        this.shop = shop;
    }

}