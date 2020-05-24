package com.haatapp.app.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.haatapp.app.R;
import com.haatapp.app.models.RecommendedDish;

import java.util.List;

/**
 * Created by santhosh@appoets.com on 22-08-2017.
 */

public class RecommendedDishesAdapter extends RecyclerView.Adapter<RecommendedDishesAdapter.MyViewHolder> {

    public List<RecommendedDish> list;
    public Context context;

    public RecommendedDishesAdapter(List<RecommendedDish> list, Context con) {
        this.list = list;
        this.context = con;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recommended_list_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        RecommendedDish dish = list.get(position);

        holder.dishNameTxt.setText("     "+dish.getName());
        holder.priceTxt.setText(dish.getPrice());
        if (dish.getIsVeg()) {
//            holder.dishNameTxt.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_veg, 0, 0, 0);
            holder.foodImageType.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_nonveg));
        } else {
            holder.foodImageType.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_veg));
        }


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView dishImg,foodImageType;
        private TextView dishNameTxt, priceTxt;

        private Button addBtn;

        private MyViewHolder(View view) {
            super(view);
            dishImg = (ImageView) view.findViewById(R.id.dishImg);
            foodImageType = (ImageView) view.findViewById(R.id.food_type_image);
            dishNameTxt = (TextView) view.findViewById(R.id.dish_name_text);
            priceTxt = (TextView) view.findViewById(R.id.price_text);
            addBtn = (Button) view.findViewById(R.id.add_btn);
            //itemView.setOnClickListener( this);
            addBtn.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (v.getId() == addBtn.getId()) {
                Toast.makeText(v.getContext(), "ITEM PRESSED = " + list.get(position).getName() + list.get(position).getPrice(), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(v.getContext(), "ROW PRESSED = " + list.get(position).getName() + list.get(position).getPrice(), Toast.LENGTH_SHORT).show();
            }
        }

    }


}
