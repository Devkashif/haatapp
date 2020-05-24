package com.haatapp.app.models;

/**
 * Created by Tamil on 10/9/2017.
 */

public class OrderFlow {
    public   int statusImage;
    public  String statusTitle,statusDescription,status;

    public  OrderFlow(String statusTitle,String statusDescription, int statusImage,String status){
        this.statusImage=statusImage;
        this.statusTitle=statusTitle;
        this.statusDescription=statusDescription;
        this.status=status;
    }


}
