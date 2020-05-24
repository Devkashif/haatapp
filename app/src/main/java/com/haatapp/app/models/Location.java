package com.haatapp.app.models;

/**
 * Created by santhosh@appoets.com on 28-08-2017.
 */

public class Location {
    public String name, address;
    public Integer icon_id;

    public Location(String name, String address, Integer icon_id) {
        this.name = name;
        this.address = address;
        this.icon_id = icon_id;
    }
}


// icon_id [other = 0 | home = 1 | work = 2]