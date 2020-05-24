package com.haatapp.app.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by santhosh@appoets.com on 28-08-2017.
 */

public class FavListModel {

    String header;
    List<Available> availables = new ArrayList<>();

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public List<Available> getFav() {
        return availables;
    }

    public void setFav(List<Available> availables) {
        this.availables = availables;
    }
}
