package com.haatapp.app.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.haatapp.app.R;
import com.haatapp.app.models.CartAddon;
import com.haatapp.app.models.Item;

import java.util.List;

/**
 * Created by santhosh@appoets.com on 22-08-2017.
 */

public class OrderDetailAdapter extends RecyclerView.Adapter<OrderDetailAdapter.MyViewHolder> {
    private List<Item> list;
    private Context context;

    public OrderDetailAdapter(List<Item> list, Context con) {
        this.list = list;
        this.context = con;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.order_detail_list_item, parent, false);

        return new MyViewHolder(itemView);
    }

    public void add(Item item, int position) {
        list.add(position, item);
        notifyItemInserted(position);
    }

    public void remove(Item item) {
        int position = list.indexOf(item);
        list.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Item item = list.get(position);
        holder.dishName.setText(item.getProduct().getName() + " x " + String.valueOf(item.getQuantity()));
        Integer priceAmount = item.getProduct().getPrices().getPrice()*item.getQuantity();
        if (list.get(position).getCartAddons() != null && !list.get(position).getCartAddons().isEmpty()) {
            for (int j = 0; j < list.get(position).getCartAddons().size(); j++) {
                priceAmount = priceAmount + (list.get(position).getQuantity() * (list.get(position).getCartAddons().get(j).getQuantity() *
                        list.get(position).getCartAddons().get(j).getAddonProduct().getPrice()));
            }
        }
        holder.price.setText(item.getProduct().getPrices().getCurrency() + priceAmount);
        if (item.getProduct().getFoodType().equalsIgnoreCase("veg"))
            holder.dishImg.setImageResource(R.drawable.ic_veg);
        else
            holder.dishImg.setImageResource(R.drawable.ic_nonveg);

        if (item.getCartAddons() != null && !item.getCartAddons().isEmpty()) {
            List<CartAddon> cartAddonList = item.getCartAddons();
            for (int i = 0; i < cartAddonList.size(); i++) {
                if (i == 0)
                    holder.addons.setText(cartAddonList.get(i).getAddonProduct().getAddon().getName());
                else
                    holder.addons.append(", " + cartAddonList.get(i).getAddonProduct().getAddon().getName());
            }

            holder.addons.setVisibility(View.VISIBLE);
        } else {
            holder.addons.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private LinearLayout itemView;
        private ImageView dishImg;
        private TextView dishName, addons, price;

        private MyViewHolder(View view) {
            super(view);
            itemView = (LinearLayout) view.findViewById(R.id.item_view);
            dishName = (TextView) view.findViewById(R.id.restaurant_name);
            addons = (TextView) view.findViewById(R.id.addons);
            dishImg = (ImageView) view.findViewById(R.id.food_type_image);
            price = (TextView) view.findViewById(R.id.price);
            itemView.setOnClickListener(this);
        }

        public void onClick(View v) {
            if (v.getId() == itemView.getId()) {
//                context.startActivity(new Intent(context, HotelViewActivity.class));
                //Toast.makeText(v.getContext(), "ROW PRESSED = " + String.valueOf(getAdapterPosition()), Toast.LENGTH_SHORT).show();
            }
        }

    }


}
