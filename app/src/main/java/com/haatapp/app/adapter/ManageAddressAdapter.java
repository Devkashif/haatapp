package com.haatapp.app.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.haatapp.app.R;
import com.haatapp.app.activities.ManageAddressActivity;
import com.haatapp.app.activities.SaveDeliveryLocationActivity;
import com.haatapp.app.build.api.ApiClient;
import com.haatapp.app.build.api.ApiInterface;
import com.haatapp.app.helper.GlobalData;
import com.haatapp.app.models.Address;
import com.haatapp.app.models.Message;

import org.json.JSONObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by santhosh@appoets.com on 22-08-2017.
 */

public class ManageAddressAdapter extends RecyclerView.Adapter<ManageAddressAdapter.MyViewHolder> {

    private List<Address> list;
    private Context context;
    ApiInterface apiInterface = ApiClient.getRetrofit().create(ApiInterface.class);

    public ManageAddressAdapter(List<Address> list, Context con) {
        this.list = list;
        this.context = con;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.address_list_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Address obj = list.get(position);
        holder.addressLabelTxt.setText(obj.getType());
        holder.addressTxt.setText(obj.getMapAddress());
        setIcon(holder.iconImg, obj.getType());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private void setIcon(ImageView imgView, String id) {
        switch (id) {
            case "home":
                imgView.setImageResource(R.drawable.home);
                break;
            case "work":
                imgView.setImageResource(R.drawable.ic_work);
                break;
            default:
                imgView.setImageResource(R.drawable.ic_map_marker);
                break;
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView addressLabelTxt, addressTxt;
        private Button editBtn, deleteBtn;
        private ImageView iconImg;

        private MyViewHolder(View view) {
            super(view);
            addressLabelTxt = (TextView) view.findViewById(R.id.address_label);
            addressTxt = (TextView) view.findViewById(R.id.address);
            editBtn = (Button) view.findViewById(R.id.edit);
            deleteBtn = (Button) view.findViewById(R.id.delete);
            iconImg = (ImageView) view.findViewById(R.id.icon);
            //itemView.setOnClickListener( this);
            editBtn.setOnClickListener(this);
            deleteBtn.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (v.getId() == editBtn.getId()) {
                GlobalData.selectedAddress = list.get(position);
                Intent intent = new Intent(context, SaveDeliveryLocationActivity.class);
                intent.putExtra("edit", "yes");
                context.startActivity(intent);
            } else if (v.getId() == deleteBtn.getId()) {
                deleteAddress(list.get(position).getId(), position);
            }
        }

        private void deleteAddress(final Integer id, final int position) {
            if (id != null) {
                new AlertDialog.Builder(context)
                        .setMessage(context.getResources().getString(R.string.delete_confirm))
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {

                                Call<Message> call = apiInterface.deleteAddress(id);
                                call.enqueue(new Callback<Message>() {
                                    @Override
                                    public void onResponse(Call<Message> call, Response<Message> response) {

                                        if (response != null && !response.isSuccessful() && response.errorBody() != null) {
                                            try {
                                                JSONObject jObjError = new JSONObject(response.errorBody().string());
                                                Toast.makeText(context, jObjError.optString("message"), Toast.LENGTH_LONG).show();
                                            } catch (Exception e) {
                                                Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                                            }
                                        } else if (response != null && response.isSuccessful()) {
                                            Message message = response.body();
                                            Toast.makeText(context, message.getMessage(), Toast.LENGTH_LONG).show();
                                            list.remove(position);
                                            if(list.size()==0)
                                                ManageAddressActivity.errorLayout.setVisibility(View.VISIBLE);
                                            else{
                                                ManageAddressActivity.errorLayout.setVisibility(View.GONE);
                                                for (int i = 0; i < GlobalData.addressList.getAddresses().size(); i++) {
                                                    if( GlobalData.addressList.getAddresses().get(i).getId().equals(id)){
                                                        GlobalData.addressList.getAddresses().remove(i);
                                                    }
                                                }
                                            }

                                            notifyItemRemoved(position);
                                            notifyItemRangeChanged(position, list.size());
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<Message> call, Throwable t) {
                                        Toast.makeText(context, "Something went wrong", Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        }).setNegativeButton(android.R.string.no, null).show();
            }
        }

    }

}
