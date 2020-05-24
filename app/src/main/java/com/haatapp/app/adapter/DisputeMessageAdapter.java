package com.haatapp.app.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.haatapp.app.R;
import com.haatapp.app.activities.OtherHelpActivity;
import com.haatapp.app.models.DisputeMessage;

import java.util.List;

/**
 * Created by santhosh@appoets.com on 22-08-2017.
 */

public class DisputeMessageAdapter extends RecyclerView.Adapter<DisputeMessageAdapter.MyViewHolder> {
    private List<DisputeMessage> list;
    private Context context;
    private Activity activity;

    public DisputeMessageAdapter(List<DisputeMessage> list, Context con, Activity activity) {
        this.list = list;
        this.context = con;
        this.activity = activity;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.dispute_message_item, parent, false);

        return new MyViewHolder(itemView);
    }

    public void add(DisputeMessage item, int position) {
        list.add(position, item);
        notifyItemInserted(position);
    }

    public void remove(DisputeMessage item) {
        int position = list.indexOf(item);
        list.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final DisputeMessage disputeMessage = list.get(position);
        holder.diputeMessageTxt.setText(disputeMessage.getName());
        holder.rootLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, OtherHelpActivity.class).putExtra("type", disputeMessage.getName())
                        .putExtra("id",disputeMessage.getId()));
                activity.overridePendingTransition(R.anim.slide_in_right, R.anim.anim_nothing);

            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout rootLayout;
        private TextView diputeMessageTxt;


        private MyViewHolder(View view) {
            super(view);
            rootLayout = (LinearLayout) view.findViewById(R.id.root_layout);
            diputeMessageTxt = (TextView) view.findViewById(R.id.dispute_message);

        }


    }


}
