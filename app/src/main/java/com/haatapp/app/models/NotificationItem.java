package com.haatapp.app.models;

/**
 * Created by santhosh@appoets.com on 30-08-2017.
 */

public class NotificationItem {

    public String offerDescription, offerCode,validity;

    public NotificationItem(String name, String price,String validity) {
        this.offerDescription = name;
        this.offerCode = price;
        this.validity = validity;
    }
}
