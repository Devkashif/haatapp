package com.haatapp.app.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by admin on 10/13/2017.
 */

public class PromotionResponse {

    @SerializedName("wallet_balance")
    @Expose
    private Integer walletMoney;

    public Integer getWalletMoney() {
        return walletMoney;
    }

    public void setWalletMoney(Integer walletMoney) {
        this.walletMoney = walletMoney;
    }
}
