package com.haatapp.app.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.sectionedrecyclerview.SectionedRecyclerViewAdapter;
import com.haatapp.app.R;
import com.haatapp.app.activities.CurrentOrderDetailActivity;
import com.haatapp.app.activities.PastOrderDetailActivity;
import com.haatapp.app.activities.ViewCartActivity;
import com.haatapp.app.build.api.ApiClient;
import com.haatapp.app.build.api.ApiInterface;
import com.haatapp.app.helper.CustomDialog;
import com.haatapp.app.helper.GlobalData;
import com.haatapp.app.models.AddCart;
import com.haatapp.app.models.Item;
import com.haatapp.app.models.Order;
import com.haatapp.app.models.OrderModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by santhosh@appoets.com on 28-08-2017.
 */

public class OrdersAdapter extends SectionedRecyclerViewAdapter<OrdersAdapter.ViewHolder> {

    private List<OrderModel> list = new ArrayList<>();
    private LayoutInflater inflater;
    Context context1;
    Activity activity;
    int lastPosition = -1;
    List<Item> itemList;
    CustomDialog customDialog;
    ApiInterface apiInterface = ApiClient.getRetrofit().create(ApiInterface.class);

    public OrdersAdapter(Context context, Activity activity, List<OrderModel> list) {
        this.context1 = context;
        this.inflater = LayoutInflater.from(context);
        this.list = list;
        this.activity = activity;
        customDialog = new CustomDialog(context);

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        switch (viewType) {
            case VIEW_TYPE_HEADER:
                v = inflater.inflate(R.layout.header_order, parent, false);
//                setScaleAnimation(v);
                return new ViewHolder(v, true);
            case VIEW_TYPE_ITEM:
                v = inflater.inflate(R.layout.orders_list_item, parent, false);
//                setScaleAnimation(v);
                return new ViewHolder(v, false);
            default:
                v = inflater.inflate(R.layout.orders_list_item, parent, false);
//                setScaleAnimation(v);
                return new ViewHolder(v, false);
        }
    }

    @Override
    public int getSectionCount() {
        return list.size();
    }


    @Override
    public int getItemCount(int section) {
        return list.get(section).getOrders().size();
    }

    @Override
    public void onBindHeaderViewHolder(ViewHolder holder, final int section) {
        holder.headerTxt.setText(list.get(section).getHeader());
        holder.headerTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println(list.get(section).getHeader());
            }
        });
    }

    private void setScaleAnimation(View view) {
        ScaleAnimation anim = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        anim.setDuration(1000);
        view.startAnimation(anim);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int section, final int relativePosition, int absolutePosition) {
        final Order object = list.get(section).getOrders().get(relativePosition);
        holder.restaurantNameTxt.setText(object.getShop().getName());
        holder.restaurantAddressTxt.setText(object.getShop().getAddress());
        holder.reorderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final HashMap<String, String> map = new HashMap<>();
                map.put("order_id", String.valueOf(object.getId()));
                if (GlobalData.addCart != null && !GlobalData.addCart.getProductList().isEmpty()) {
                    String message = String.format(activity.getResources().getString(R.string.reorder_confirm_message), GlobalData.addCart.getProductList().get(0).getProduct().getShop().getName(), object.getShop().getName());
                    new AlertDialog.Builder(activity)
                            .setTitle("Reorder")
                            .setMessage(message)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    Reorder(map);
                                }
                            })
                            .setNegativeButton(android.R.string.no, null).show();
                } else {
                    Reorder(map);
                }
            }
        });
        int lastPostion = relativePosition + 1;
        if (list.get(section).getOrders().size() == 1) {
            holder.dividerLine.setVisibility(View.GONE);
        } else if (list.get(section).getOrders().size() == relativePosition + 1) {
            holder.dividerLine.setVisibility(View.GONE);
        } else {
            holder.dividerLine.setVisibility(View.VISIBLE);
        }

        if(object.getDispute()!=null){
            holder.disputeLayout.setVisibility(View.VISIBLE);
            if(object.getDispute().equalsIgnoreCase("CREATED")){
                holder.disputeStatusImage.setBackgroundResource(R.drawable.dispute);
                holder.disputeTxt.setText(context1.getResources().getString(R.string.dispute)+" "+object.getDispute());
            }else {
                holder.disputeStatusImage.setBackgroundResource(R.drawable.dispute_success);
                holder.disputeTxt.setText(context1.getResources().getString(R.string.dispute)+" "+object.getDispute());
            }
        }
        else {
            holder.disputeLayout.setVisibility(View.GONE);
        }
        holder.disputeTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                showTooltip(v, R.string.dispute_created, Tooltip.BOTTOM, true,
//                        TooltipAnimation.SCALE,
//                        tooltipSize,
//                        ViewGroup.LayoutParams.WRAP_CONTENT);
            }
        });

        holder.totalAmount.setText(GlobalData.currencySymbol + object.getInvoice().getNet().toString());
        //set Item List Values
        itemList = new ArrayList<>();
        itemList.addAll(object.getItems());
        String dishNameValue = "";
        for (int i = 0; i < itemList.size(); i++) {
            if (i == 0)
                dishNameValue = object.getItems().get(i).getProduct().getName() + " (" + object.getItems().get(i).getQuantity() + ")";
            else
                dishNameValue = dishNameValue + ", " + object.getItems().get(i).getProduct().getName() + " (" + object.getItems().get(i).getQuantity() + ")";
        }
        holder.dishNameTxt.setText(dishNameValue);
        holder.dateTimeTxt.setText(getTimeFromString(object.getInvoice().getCreatedAt()));
        holder.itemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (list.get(section).getHeader().equalsIgnoreCase("Current Orders")) {
                    GlobalData.isSelectedOrder = list.get(section).getOrders().get(relativePosition);
                    context1.startActivity(new Intent(context1, CurrentOrderDetailActivity.class).putExtra("is_order_page",true));
                } else {
                    GlobalData.isSelectedOrder = list.get(section).getOrders().get(relativePosition);
                    context1.startActivity(new Intent(context1, PastOrderDetailActivity.class));
                }
                activity.overridePendingTransition(R.anim.slide_in_right, R.anim.anim_nothing);
            }
        });


    }

    private void Reorder(HashMap<String, String> map) {
        customDialog.show();
        System.out.println(map.toString());
        Call<AddCart> call=apiInterface.reOrder(map);
        call.enqueue(new Callback<AddCart>() {
            @Override
            public void onResponse(@NonNull Call<AddCart> call, @NonNull Response<AddCart> response) {
                customDialog.dismiss();
                if(response.isSuccessful()){
                    GlobalData.addCart=response.body();
                    activity.startActivity(new Intent(activity, ViewCartActivity.class));
                }
            }

            @Override
            public void onFailure(@NonNull Call<AddCart> call,@NonNull Throwable t) {
                customDialog.dismiss();
            }
        });

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView headerTxt;
        TextView restaurantNameTxt,disputeTxt, restaurantAddressTxt, totalAmount, dishNameTxt, dateTimeTxt;
        Button reorderBtn;
        ImageView disputeStatusImage;
        View dividerLine;
        LinearLayout itemLayout,disputeLayout;
        public ViewHolder(View itemView, boolean isHeader) {
            super(itemView);
            if (isHeader) {
                headerTxt = (TextView) itemView.findViewById(R.id.header);
            } else {
                disputeStatusImage = (ImageView) itemView.findViewById(R.id.dispute_status_image);
                disputeLayout = (LinearLayout) itemView.findViewById(R.id.dispute_layout);
                disputeTxt = (TextView) itemView.findViewById(R.id.dispute_txt);

                itemLayout = (LinearLayout) itemView.findViewById(R.id.item_layout);
                restaurantNameTxt = (TextView) itemView.findViewById(R.id.restaurant_name);
                restaurantAddressTxt = (TextView) itemView.findViewById(R.id.restaurant_address);
                totalAmount = (TextView) itemView.findViewById(R.id.total_amount);
                reorderBtn = (Button) itemView.findViewById(R.id.reorder);
                dishNameTxt = (TextView) itemView.findViewById(R.id.dish_name);
                dateTimeTxt = (TextView) itemView.findViewById(R.id.date_time);
                dividerLine = (View) itemView.findViewById(R.id.divider_line);
            }


        }

    }

    private String getTimeFromString(String time) {
        System.out.println("Time : " + time);
        String value = "";
        try {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            SimpleDateFormat sdf = new SimpleDateFormat("d MMM yyyy, hh:mm aa");

            if (time != null) {
                Date date = df.parse(time);
                value = sdf.format(date);
            }

        } catch (ParseException e) {
            e.printStackTrace();

        }
        return value;
    }

}
