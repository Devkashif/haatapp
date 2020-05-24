package com.haatapp.app.fragments;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.haatapp.app.R;
import com.haatapp.app.activities.OtherHelpActivity;
import com.haatapp.app.activities.SplashActivity;
import com.haatapp.app.adapter.DisputeMessageAdapter;
import com.haatapp.app.build.api.ApiClient;
import com.haatapp.app.build.api.ApiInterface;
import com.haatapp.app.helper.CustomDialog;
import com.haatapp.app.helper.GlobalData;
import com.haatapp.app.models.Order;

import org.json.JSONObject;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


import static com.haatapp.app.helper.GlobalData.disputeMessageList;
import static com.haatapp.app.helper.GlobalData.isSelectedOrder;

/**
 * A simple {@link Fragment} subclass.
 */
public class OrderHelpFragment extends Fragment {


    Unbinder unbinder;
    Context context;
    DisputeMessageAdapter disputeMessageAdapter;
    @BindView(R.id.help_rv)
    RecyclerView helpRv;
    @BindView(R.id.other_help_layout)
    LinearLayout otherHelpLayout;
    @BindView(R.id.dispute)
    Button dispute;
    @BindView(R.id.chat_us)
    Button chatUs;


    Double priceAmount = 0.0;
    int DISPUTE_ID = 0;
    int itemQuantity = 0;
    String currency = "";
    String reason = "OTHERS";
    CustomDialog customDialog;
    String disputeType;
    Integer DISPUTE_HELP_ID = 0;
    ApiInterface apiInterface = ApiClient.getRetrofit().create(ApiInterface.class);

    public OrderHelpFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = getContext();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_order_help, container, false);
        unbinder = ButterKnife.bind(this, view);
        customDialog = new CustomDialog(context);

        if(disputeMessageList!=null){
            helpRv.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
            helpRv.setItemAnimator(new DefaultItemAnimator());
            helpRv.setHasFixedSize(true);
            disputeMessageAdapter = new DisputeMessageAdapter(disputeMessageList, context, getActivity());
            helpRv.setAdapter(disputeMessageAdapter);
            if (disputeMessageList.size() > 0) {
                otherHelpLayout.setVisibility(View.GONE);
            } else {
                otherHelpLayout.setVisibility(View.VISIBLE);
            }
        }else {
            startActivity(new Intent(context, SplashActivity.class));
            getActivity().finish();
        }



        return view;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void showDialog() {
        final String[] disputeArrayList = {"COMPLAINED", "CANCELED", "REFUND"};
        disputeType = "COMPLAINED";
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dispute_dialog, null);
        dialogBuilder.setView(dialogView);
        final EditText edt = (EditText) dialogView.findViewById(R.id.reason_edit);
        final Spinner disputeTypeSpinner = (Spinner) dialogView.findViewById(R.id.dispute_type);
        //Creating the ArrayAdapter instance having the country list
        ArrayAdapter arrayAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item, disputeArrayList);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        disputeTypeSpinner.setAdapter(arrayAdapter);
        disputeTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                disputeType = disputeArrayList[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        dialogBuilder.setTitle("ORDER #000" + isSelectedOrder.getId().toString());
        dialogBuilder.setMessage(reason);
        dialogBuilder.setPositiveButton("Submit", null);
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();
            }
        });
        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.setCancelable(false);
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {
                Button b = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                b.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (edt.getText().toString().equalsIgnoreCase("")) {
                            Toast.makeText(context, "Please enter reason", Toast.LENGTH_SHORT).show();
                        } else {
                            dialog.dismiss();
                            HashMap<String, String> map = new HashMap<>();
                            map.put("order_id", GlobalData.isSelectedOrder.getId().toString());
                            map.put("status", "CREATED");
                            map.put("description", edt.getText().toString());
                            map.put("dispute_type", disputeType);
                            map.put("created_by", "user");
                            map.put("created_to", "user");
                            postDispute(map);
                        }
                    }
                });
            }
        });
        alertDialog.show();

    }

    private void postDispute(HashMap<String, String> map) {
        customDialog.show();
        Call<Order> call = apiInterface.postDispute(map);
        call.enqueue(new Callback<Order>() {
            @Override
            public void onResponse(@NonNull Call<Order> call, @NonNull Response<Order> response) {
                customDialog.dismiss();
                if (response.isSuccessful()) {
                    Toast.makeText(context, "Dispute create successfully", Toast.LENGTH_SHORT).show();
                    getActivity().finish();
                } else {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Toast.makeText(context, jObjError.optString("message"), Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<Order> call, @NonNull Throwable t) {
                customDialog.dismiss();
                Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        });

    }


    @OnClick({R.id.chat_us, R.id.dispute})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.chat_us:
                startActivity(new Intent(getActivity(), OtherHelpActivity.class).putExtra("is_chat", true));
                break;
            case R.id.dispute:
                showDialog();
                break;
        }
    }
}
