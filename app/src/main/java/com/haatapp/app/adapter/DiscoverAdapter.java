package com.haatapp.app.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.haatapp.app.R;
import com.haatapp.app.models.Discover;

import java.util.List;

/**
 * Created by santhosh@appoets.com on 22-08-2017.
 */

public class DiscoverAdapter extends RecyclerView.Adapter<DiscoverAdapter.MyViewHolder> {
    private static ClickListener clickListener;
    private List<Discover> list;

    public DiscoverAdapter(List<Discover> list, Context con) {
        this.list = list;
        Context context = con;
    }

    public void setOnItemClickListener(ClickListener clickListener) {
        DiscoverAdapter.clickListener = clickListener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.discover_list_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Discover obj = list.get(position);
        holder.title.setText(obj.title);
        holder.optionCount.setText(obj.optionCount);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public interface ClickListener {
        void onItemClick(int position, View v);

    }

    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView icon;
        public TextView title, optionCount;


        public MyViewHolder(View view) {
            super(view);
            icon = (ImageView) view.findViewById(R.id.icon);
            title = (TextView) view.findViewById(R.id.title);
            optionCount = (TextView) view.findViewById(R.id.option_count);

        }

        public void onClick(View v) {
            clickListener.onItemClick(getAdapterPosition(), v);
        }


    }


}
