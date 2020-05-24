package com.haatapp.app.adapter;

import android.content.Context;
import android.support.graphics.drawable.AnimatedVectorDrawableCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.haatapp.app.R;
import com.haatapp.app.activities.HotelViewActivity;
import com.haatapp.app.models.RecommendedDish;
import com.robinhood.ticker.TickerUtils;
import com.robinhood.ticker.TickerView;

import java.util.List;

/**
 * Created by santhosh@appoets.com on 22-08-2017.
 */

public class AccompanimentDishesAdapter extends RecyclerView.Adapter<AccompanimentDishesAdapter.MyViewHolder> {

    private List<RecommendedDish> list;
    private Context context;
    AnimatedVectorDrawableCompat avdProgress;
    int priceAmount = 0;
    int itemCount = 0;
    //Animation number
    private static final char[] NUMBER_LIST = TickerUtils.getDefaultNumberList();

    public AccompanimentDishesAdapter(List<RecommendedDish> list, Context con) {
        this.list = list;
        this.context = con;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.accompainment_list_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        RecommendedDish dish = list.get(position);
        holder.cardTextValueTicker.setCharacterList(NUMBER_LIST);
        holder.dishNameTxt.setText(dish.getName());
        holder.priceTxt.setText("$"+dish.getPrice());

       /* check Availablity*/
        if (dish.getAvaialable().equalsIgnoreCase("available")) {
            holder.cardAddTextLayout.setVisibility(View.VISIBLE);
            holder.cardTextValueTicker.setText(String.valueOf(1));
            holder.cardInfoLayout.setVisibility(View.GONE);
        } else if (dish.getAvaialable().equalsIgnoreCase("out of stock")) {
            holder.cardAddTextLayout.setVisibility(View.GONE);
            holder.cardInfoLayout.setVisibility(View.VISIBLE);
            holder.cardAddInfoText.setVisibility(View.GONE);
            holder.cardAddOutOfStock.setVisibility(View.VISIBLE);
        } else {
            holder.cardAddTextLayout.setVisibility(View.GONE);
            holder.cardInfoLayout.setVisibility(View.VISIBLE);
            holder.cardAddInfoText.setVisibility(View.VISIBLE);
            holder.cardAddOutOfStock.setVisibility(View.GONE);
            holder.cardAddInfoText.setText(dish.getAvaialable());
        }

        if (dish.getIsVeg()) {
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
        private ImageView dishImg, foodImageType, cardAddBtn, cardMinusBtn, animationLineCartAdd;
        private TextView dishNameTxt, priceTxt,cardTextValue , cardAddInfoText, cardAddOutOfStock;
        TickerView cardTextValueTicker;
        RelativeLayout cardAddDetailLayout, cardAddTextLayout, cardInfoLayout;


        private MyViewHolder(View view) {
            super(view);
            dishImg = view.findViewById(R.id.dishImg);
            foodImageType = view.findViewById(R.id.food_type_image);
            animationLineCartAdd = view.findViewById(R.id.animation_line_cart_add);
            dishNameTxt = view.findViewById(R.id.dish_name_text);
            priceTxt = view.findViewById(R.id.price_text);

             /*    Add card Button Layout*/
            cardAddDetailLayout = view.findViewById(R.id.add_card_layout);
            cardAddTextLayout = view.findViewById(R.id.add_card_text_layout);
            cardInfoLayout = view.findViewById(R.id.add_card_info_layout);
            cardAddInfoText = view.findViewById(R.id.avialablity_time);
            cardAddOutOfStock = view.findViewById(R.id.out_of_stock);
            cardAddBtn = view.findViewById(R.id.card_add_btn);
            cardMinusBtn = view.findViewById(R.id.card_minus_btn);
            cardTextValue = view.findViewById(R.id.card_value);
            cardTextValueTicker = view.findViewById(R.id.card_value_ticker);

            //itemView.setOnClickListener( this);


            /*  Click Events*/
            cardAddTextLayout.setOnClickListener(this);
            cardAddBtn.setOnClickListener(this);
            cardMinusBtn.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.add_card_text_layout:
                    cardAddDetailLayout.setVisibility(View.VISIBLE);
                    HotelViewActivity.Companion.getViewCartLayout().setVisibility(View.VISIBLE);
                    itemCount = itemCount + 1;
                    priceAmount = priceAmount + Integer.parseInt(list.get(getAdapterPosition()).getPrice());
                    HotelViewActivity.Companion.getItemText().setText("" + itemCount + " Item | $" + "" + priceAmount);
                    cardAddTextLayout.setVisibility(View.GONE);
                    break;

                case R.id.card_add_btn:
                    int countValue = Integer.parseInt(cardTextValue.getText().toString()) + 1;
                    itemCount = itemCount + 1;
                    priceAmount = priceAmount + Integer.parseInt(list.get(getAdapterPosition()).getPrice());
                    HotelViewActivity.Companion.getItemText().setText("" + itemCount + " Items | $" + "" + priceAmount);
                    cardTextValue.setText("" + countValue);
                    cardTextValueTicker.setText("" + countValue);
                    break;
                case R.id.card_minus_btn:
                    if (cardTextValue.getText().toString().equalsIgnoreCase("1")) {
                        cardAddDetailLayout.setVisibility(View.GONE);
                        HotelViewActivity.Companion.getViewCartLayout().setVisibility(View.GONE);
                        cardAddTextLayout.setVisibility(View.VISIBLE);
                    } else {
                        int countMinusValue = Integer.parseInt(cardTextValue.getText().toString()) - 1;
                        itemCount = itemCount - 1;
                        priceAmount = priceAmount - Integer.parseInt(list.get(getAdapterPosition()).getPrice());
                        HotelViewActivity.Companion.getItemText().setText("" + itemCount + " Items | $" + "" + priceAmount);
                        cardTextValue.setText("" + countMinusValue);
                        cardTextValueTicker.setText("" + countMinusValue);

                    }
                    break;
            }

        }


    }


}
