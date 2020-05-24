/*
package com.shopjinu.app.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.shopjinu.app.build.api.ApiClient;
import com.shopjinu.app.build.api.ApiInterface;
import com.shopjinu.app.model.Order;
import com.shopjinu.app.model.OrderModel;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.shopjinu.app.helper.GlobalData.isSelectedOrder;
import static com.shopjinu.app.helper.GlobalData.onGoingOrderList;
import static com.shopjinu.app.helper.GlobalData.pastOrderList;
import static com.shopjinu.app.helper.GlobalData.shouldContinueService;

*/
/**
 * Created by Tamil on 10/10/2017.
 *//*


public class OrderStatusService extends IntentService {
    */
/**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     *//*


    Handler handler;
    Context context;
    ApiInterface apiInterface = ApiClient.getRetrofit().create(ApiInterface.class);
    List<OrderModel> modelList = new ArrayList<>();
    String type;
    int id = 0;
    int ONGOING_ORDER_LIST_SIZE = 0;
    String previousStatus = "";

    */
/**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *//*

    public OrderStatusService() {
        super("Tamil");
    }


    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        if (intent != null) {
            Bundle extras = intent.getExtras();
            type = extras.getString("type");
            if (type.equalsIgnoreCase("SINGLE_ORDER")) {
                id = extras.getInt("order_id");
            } else if (type.equalsIgnoreCase("ORDER_LIST")) {
                id = 0;
            }
        } else {

        }


    }

    private void doStuff() {
        // check the condition
        if (shouldContinueService == false) {
            stopSelf();
            handler.removeCallbacks(null);
            return;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context=this;
        handler = new Handler();
        Runnable orderStatusRunnable = new Runnable() {
            public void run() {
                doStuff();
                if(shouldContinueService){
                    if (type.equalsIgnoreCase("ORDER_LIST"))
                        getOngoingOrders();
                    else if (type.equalsIgnoreCase("SINGLE_ORDER"))
                        getParticularOrders(id);
                }
                handler.postDelayed(this, 5000);
            }
        };
        handler.postDelayed(orderStatusRunnable, 5000);

    }

    private void getParticularOrders(int order_id) {
        Call<Order> call = apiInterface.getParticularOrders(order_id);
        call.enqueue(new Callback<Order>() {
            @Override
            public void onResponse(Call<Order> call, Response<Order> response) {

                if (response != null && !response.isSuccessful() && response.errorBody() != null) {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Toast.makeText(context, jObjError.optString("message"), Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
//                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else if (response.isSuccessful()) {
                    isSelectedOrder = response.body();
                    Log.i("isSelectedOrder : ", isSelectedOrder.toString());
                    if (!isSelectedOrder.getStatus().equalsIgnoreCase(previousStatus)) {
                        previousStatus = isSelectedOrder.getStatus();
                        Intent chat_intent = new Intent("SINGLE_ORDER");
                        // You can also include some extra data.
                        LocalBroadcastManager.getInstance(OrderStatusService.this).sendBroadcast(chat_intent);

                    }


                }
            }

            @Override
            public void onFailure(Call<Order> call, Throwable t) {

            }
        });
    }

    private void getOngoingOrders() {
        Call<List<Order>> call = apiInterface.getOngoingOrders();
        call.enqueue(new Callback<List<Order>>() {
            @Override
            public void onResponse(Call<List<Order>> call, Response<List<Order>> response) {
                if (response != null && !response.isSuccessful() && response.errorBody() != null) {

                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Toast.makeText(context, jObjError.optString("message"), Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
//                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else if (response.isSuccessful()) {
                    if (onGoingOrderList.size() == response.body().size()) {
                        onGoingOrderList = response.body();
                        Intent chat_intent = new Intent("ONGOING");
                        // You can also include some extra data.
                        LocalBroadcastManager.getInstance(OrderStatusService.this).sendBroadcast(chat_intent);
                    }
                    else {
                        updateOrderList();
                    }

//                    if (onGoingOrderList.size() == response.body().size()) {
//                        if(onGoingOrderList.size()!=0){
//                            for (int i = 0; i < onGoingOrderList.size(); i++) {
//                                if (!onGoingOrderList.get(i).getStatus().equalsIgnoreCase(response.body().get(i).getStatus()) ) {
//                                    onGoingOrderList = response.body();
//                                    updateOrderList();
//                                }
//                            }
//                        }
//                        Log.i("onGoingOrderList : ", onGoingOrderList.toString());
//                    } else {
//                        onGoingOrderList = response.body();
//                        updateOrderList();
//                    }
                }
            }

            @Override
            public void onFailure(Call<List<Order>> call, Throwable t) {

            }
        });
    }

    private void updateOrderList() {
        getPastOrders();
    }

    private void getPastOrders() {
        Call<List<Order>> call = apiInterface.getPastOders();
        call.enqueue(new Callback<List<Order>>() {
            @Override
            public void onResponse(Call<List<Order>> call, Response<List<Order>> response) {
                if (response != null && !response.isSuccessful() && response.errorBody() != null) {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Toast.makeText(context, jObjError.optString("message"), Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else if (response.isSuccessful()) {
                    pastOrderList = response.body();
                    Intent chat_intent = new Intent("ONGOING");
                    // You can also include some extra data.
                    LocalBroadcastManager.getInstance(OrderStatusService.this).sendBroadcast(chat_intent);
                }
            }

            @Override
            public void onFailure(Call<List<Order>> call, Throwable t) {

            }
        });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(null);
    }
}
*/
