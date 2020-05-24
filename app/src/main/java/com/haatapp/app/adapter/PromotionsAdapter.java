package com.haatapp.app.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.haatapp.app.R;
import com.haatapp.app.models.Promotions;
import com.haatapp.app.models.Restaurant;

import java.util.List;

/**
 * Created by santhosh@appoets.com on 22-08-2017.
 */

public class PromotionsAdapter extends RecyclerView.Adapter<PromotionsAdapter.MyViewHolder> {
    private List<Promotions> list;
    PromotionListener promotionListener;

    public PromotionsAdapter(List<Promotions> list,PromotionListener promotionListener) {
        this.list = list;
        this.promotionListener=promotionListener;
    }

    public interface PromotionListener{
        void onApplyBtnClick(Promotions promotions);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.promotions_item, parent, false);

        return new MyViewHolder(itemView);
    }

    public void add(Promotions item, int position) {
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
        Promotions promotionsModel = list.get(position);
        holder.promoNameTxt.setText(promotionsModel.getPromoCode());
        holder.statusBtnTxt.setTag(promotionsModel);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView promoNameTxt;
        Button statusBtnTxt;

        private MyViewHolder(View view) {
            super(view);
            promoNameTxt= (TextView) view.findViewById(R.id.promo_name_txt);
            statusBtnTxt= (Button) view.findViewById(R.id.status_btn);

            statusBtnTxt.setOnClickListener(this);
        }

        public void onClick(View v) {
            Promotions promotions= (Promotions) v.getTag();
            promotionListener.onApplyBtnClick(promotions);
        }
    }
}
