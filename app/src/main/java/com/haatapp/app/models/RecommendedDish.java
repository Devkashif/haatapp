package com.haatapp.app.models;

/**
 * Created by CSS22 on 22-08-2017.
 */

public class RecommendedDish {
    String name, category, price, imgUrl, description,avaialable;
    Boolean isVeg;

    public RecommendedDish(String name, String category, String price, Boolean isVeg, String url, String description, String available) {
        this.name = name;
        this.category = category;
        this.price = price;
        this.isVeg = isVeg;
        this.imgUrl = url;
        this.description = description;
        this.avaialable = available;
    }

    public String getAvaialable() {
        return avaialable;
    }

    public void setAvaialable(String avaialable) {
        this.avaialable = avaialable;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public Boolean getIsVeg() {
        return isVeg;
    }

    public void setIsVeg(Boolean isVeg) {
        this.isVeg = isVeg;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String image) {
        this.imgUrl = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
