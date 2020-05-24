package com.haatapp.app.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.sectionedrecyclerview.SectionedRecyclerViewAdapter;
import com.bumptech.glide.Glide;
import com.haatapp.app.R;
import com.haatapp.app.activities.HotelViewActivity;
import com.haatapp.app.helper.GlobalData;
import com.haatapp.app.models.Available;
import com.haatapp.app.models.FavListModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by santhosh@appoets.com on 24-08-2017.
 */

public class FavouritesAdapter extends SectionedRecyclerViewAdapter<FavouritesAdapter.ViewHolder> {

    private List<FavListModel> list = new ArrayList<>();
    private LayoutInflater inflater;
    Context context;

    public FavouritesAdapter(Context context, List<FavListModel> list) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.list = list;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        switch (viewType) {
            case VIEW_TYPE_HEADER:
                v = inflater.inflate(R.layout.header, parent, false);
                return new ViewHolder(v, true);
            case VIEW_TYPE_ITEM:
                v = inflater.inflate(R.layout.favorite_list_item, parent, false);
                return new ViewHolder(v, false);
            default:
                v = inflater.inflate(R.layout.favorite_list_item, parent, false);
                return new ViewHolder(v, false);
        }
    }

    @Override
    public int getSectionCount() {
        return list.size();
    }


    @Override
    public int getItemCount(int section) {
        return list.get(section).getFav().size();
    }

    @Override
    public void onBindHeaderViewHolder(FavouritesAdapter.ViewHolder holder, final int section) {
        holder.header.setText(list.get(section).getHeader());
        holder.header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println(list.get(section).getHeader());
            }
        });
    }

    @Override
    public void onBindViewHolder(FavouritesAdapter.ViewHolder holder, int section, int relativePosition, int absolutePosition) {
        final Available object = list.get(section).getFav().get(relativePosition);
        holder.shopName.setText(object.getShop().getName());
        holder.shopAddress.setText(object.getShop().getAddress());
        System.out.println(object.getShop().getAvatar());
        Glide.with(context).load(object.getShop().getAvatar()).into(holder.shopAvatar);

        holder.itemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GlobalData.selectedShop = object.getShop();
                context.startActivity(new Intent(context, HotelViewActivity.class).putExtra("is_fav", true));
            }
        });

    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView header;
        TextView shopAddress;
        TextView shopName;
        ImageView shopAvatar;
        RelativeLayout itemLayout;

        public ViewHolder(View itemView, boolean isHeader) {
            super(itemView);
            if (isHeader) {
                header = (TextView) itemView.findViewById(R.id.header);
            } else {
                itemLayout = (RelativeLayout) itemView.findViewById(R.id.item_layout);
                shopName = (TextView) itemView.findViewById(R.id.shop_name);
                shopAddress = (TextView) itemView.findViewById(R.id.shop_address);
                shopAvatar = (ImageView) itemView.findViewById(R.id.shop_avatar);
            }

        }


    }
}
