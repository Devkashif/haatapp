package com.haatapp.app.helper;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;


public class ConnectionHelper {

    private Context context;

    public ConnectionHelper(Context context) {
        this.context = context;
    }

    public boolean isConnectingToInternet() {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivity.getActiveNetworkInfo();
        //Toast.makeText(context, "Oops ! Connect your Internet", Toast.LENGTH_LONG).show();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();

    }
}
