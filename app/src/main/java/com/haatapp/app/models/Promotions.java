package com.haatapp.app.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Promotions {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("promo_code")
    @Expose
    private String promoCode;
    @SerializedName("promocode_type")
    @Expose
    private String promocodeType;
    @SerializedName("discount")
    @Expose
    private Integer discount;
    @SerializedName("expiration")
    @Expose
    private String expiration;
    @SerializedName("status")
    @Expose
    private String status;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPromoCode() {
        return promoCode;
    }

    public void setPromoCode(String promoCode) {
        this.promoCode = promoCode;
    }

    public String getPromocodeType() {
        return promocodeType;
    }

    public void setPromocodeType(String promocodeType) {
        this.promocodeType = promocodeType;
    }

    public Integer getDiscount() {
        return discount;
    }

    public void setDiscount(Integer discount) {
        this.discount = discount;
    }

    public String getExpiration() {
        return expiration;
    }

    public void setExpiration(String expiration) {
        this.expiration = expiration;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
