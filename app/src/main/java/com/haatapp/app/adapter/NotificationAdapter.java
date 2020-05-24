package com.haatapp.app.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.haatapp.app.R;
import com.haatapp.app.models.NotificationItem;
import com.haatapp.app.models.Restaurant;

import java.util.List;

/**
 * Created by santhosh@appoets.com on 22-08-2017.
 */

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.MyViewHolder> {
    private List<NotificationItem> list;
    private Context context;

    public NotificationAdapter(List<NotificationItem> list, Context con) {
        this.list = list;
        this.context = con;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.notification_detail_item, parent, false);

        return new MyViewHolder(itemView);
    }

    public void add(NotificationItem item, int position) {
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
        NotificationItem NotificationItem = list.get(position);
        holder.validity.setText(NotificationItem.validity);
        holder.offerDescription.setText(NotificationItem.offerDescription);
        holder.offerCode.setText(NotificationItem.offerCode);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private LinearLayout notificatioLayout;
        private TextView validity, offerCode,offerDescription;

        private MyViewHolder(View view) {
            super(view);
            notificatioLayout = (LinearLayout) view.findViewById(R.id.notification_layout);
            validity = (TextView) view.findViewById(R.id.validity_date);
            offerDescription = (TextView) view.findViewById(R.id.offer_description);
            offerCode = (TextView) view.findViewById(R.id.offer_code);
//            notificatioLayout.setOnClickListener(this);
        }

        public void onClick(View v) {
            if (v.getId() == notificatioLayout.getId()) {
//                context.startActivity(new Intent(context, HotelViewActivity.class));
                //Toast.makeText(v.getContext(), "ROW PRESSED = " + String.valueOf(getAdapterPosition()), Toast.LENGTH_SHORT).show();
            }
        }

    }


}
