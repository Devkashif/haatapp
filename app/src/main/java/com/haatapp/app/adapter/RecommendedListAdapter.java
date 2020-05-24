package com.haatapp.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.haatapp.app.R;
import com.haatapp.app.activities.RecommendedListActivity;
import com.haatapp.app.models.RecommendedDish;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by santhosh@appoets.com on 24-08-2017.
 */

public class RecommendedListAdapter extends BaseAdapter {

    private Context context;
    private List<RecommendedDish> list;

    public RecommendedListAdapter(Context context, List<RecommendedDish> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return this.list.size();
    }

    @Override
    public Object getItem(int position) {
        return this.list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        final RecommendedDish obj = list.get(position);

        if (convertView != null) {
            holder = (ViewHolder) convertView.getTag();
        } else {
            LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.recommended_list_item_2, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }

        holder.dishNameTxt.setText(obj.getName());
        holder.priceTxt.setText(obj.getPrice());
        holder.descriptionTxt.setText(obj.getDescription());

        holder.addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println(obj.getName());
                if (context instanceof RecommendedListActivity) {
                    ((RecommendedListActivity) context).addItemToCart();
                }
            }
        });

        return convertView;
    }

    static class ViewHolder {
        @BindView(R.id.dish_img)
        ImageView dishImg;
        @BindView(R.id.add)
        Button addBtn;
        @BindView(R.id.dish_name)
        TextView dishNameTxt;
        @BindView(R.id.price)
        TextView priceTxt;
        @BindView(R.id.description)
        TextView descriptionTxt;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
