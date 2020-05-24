package com.haatapp.app.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.haatapp.app.R;
import com.haatapp.app.activities.HotelViewActivity;
import com.haatapp.app.models.Restaurant;

import java.util.List;

/**
 * Created by santhosh@appoets.com on 22-08-2017.
 */

public class OfferRestaurantAdapter extends RecyclerView.Adapter<OfferRestaurantAdapter.MyViewHolder> {
    private List<Restaurant> list;
    private Context context;

    public OfferRestaurantAdapter(List<Restaurant> list, Context con) {
        this.list = list;
        this.context = con;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.offer_restaurant_list_item, parent, false);

        return new MyViewHolder(itemView);
    }

    public void add(Restaurant item, int position) {
        list.add(position, item);
        notifyItemInserted(position);
    }

    public void remove(Restaurant item) {
        int position = list.indexOf(item);
        list.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Restaurant restaurant = list.get(position);
        holder.restaurantName.setText(restaurant.name);
        holder.category.setText(restaurant.category);
//        holder.offer.setText(restaurant.offer);
        holder.rating.setText(restaurant.rating);
        holder.distanceTime.setText(restaurant.distance);
        holder.price.setText(restaurant.price);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private LinearLayout itemView;
        private ImageView dishImg;
        private TextView restaurantName, category, offer, rating, distanceTime, price;

        private MyViewHolder(View view) {
            super(view);
            itemView = (LinearLayout) view.findViewById(R.id.item_view);
            dishImg = (ImageView) view.findViewById(R.id.dishImg);
            dishImg = (ImageView) view.findViewById(R.id.dishImg);
            restaurantName = (TextView) view.findViewById(R.id.restaurant_name);
            category = (TextView) view.findViewById(R.id.category);
            offer = (TextView) view.findViewById(R.id.offer);
            rating = (TextView) view.findViewById(R.id.rating);
            distanceTime = (TextView) view.findViewById(R.id.distance_time);
            price = (TextView) view.findViewById(R.id.price);
            itemView.setOnClickListener(this);
        }

        public void onClick(View v) {
            if (v.getId() == itemView.getId()) {
                context.startActivity(new Intent(context, HotelViewActivity.class));
                //Toast.makeText(v.getContext(), "ROW PRESSED = " + String.valueOf(getAdapterPosition()), Toast.LENGTH_SHORT).show();
            }
        }

    }


}
