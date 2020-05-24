package com.haatapp.app.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.haatapp.app.Pubnub.ChatFragment;
import com.haatapp.app.R;
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
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class OtherHelpActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.chat_us)
    Button chatUs;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    @BindView(R.id.reason_title)
    TextView reasonTitle;
    @BindView(R.id.reason_description)
    TextView reasonDescription;
    @BindView(R.id.dispute)
    Button dispute;
    @BindView(R.id.order_id_txt)
    TextView orderIdTxt;
    @BindView(R.id.order_item_txt)
    TextView orderItemTxt;

    Double priceAmount = 0.0;
    int DISPUTE_ID = 0;
    int itemQuantity = 0;
    String currency = "";
    String reason;
    Context context;
    CustomDialog customDialog;
    String disputeType;
    Integer DISPUTE_HELP_ID = 0;

    ApiInterface apiInterface = ApiClient.getRetrofit().create(ApiInterface.class);
    boolean isChat = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_help);
        ButterKnife.bind(this);
        context = OtherHelpActivity.this;
        customDialog = new CustomDialog(context);
        fragmentManager = getSupportFragmentManager();
        reason = getIntent().getExtras().getString("type");
        DISPUTE_HELP_ID = getIntent().getExtras().getInt("id");
        Order order = GlobalData.isSelectedOrder;
        itemQuantity = order.getInvoice().getQuantity();
        priceAmount = order.getInvoice().getNet();
        currency = order.getItems().get(0).getProduct().getPrices().getCurrency();
        if (itemQuantity == 1)
            orderItemTxt.setText(String.valueOf(itemQuantity) + " Item, " + currency + String.valueOf(priceAmount));
        else
            orderItemTxt.setText(String.valueOf(itemQuantity) + " Items, " + currency + String.valueOf(priceAmount));
        orderIdTxt.setText("ORDER #000" + order.getId().toString());
        reasonTitle.setText(reason);
        isChat = getIntent().getBooleanExtra("is_chat", false);
        if (isChat)
            chatUs.performClick();
        //Toolbar
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    private void showDialog() {

        final String[] disputeArrayList = {"COMPLAINED", "CANCELED", "REFUND"};
        disputeType = "COMPLAINED";
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dispute_dialog, null);
        dialogBuilder.setView(dialogView);
        final EditText edt = (EditText) dialogView.findViewById(R.id.reason_edit);
        final Spinner disputeTypeSpinner = (Spinner) dialogView.findViewById(R.id.dispute_type);
        //Creating the ArrayAdapter instance having the country list
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, disputeArrayList);
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
        dialogBuilder.setTitle(orderIdTxt.getText().toString());
        dialogBuilder.setMessage(reason);
        dialogBuilder.setPositiveButton(getString(R.string.submit), null);
        dialogBuilder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
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
                            if (!disputeType.equalsIgnoreCase("others"))
                                map.put("disputehelp_id", DISPUTE_HELP_ID.toString());
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
                    Toast.makeText(OtherHelpActivity.this, "Dispute create successfully", Toast.LENGTH_SHORT).show();
                    finish();
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

            }
        });

    }


    @OnClick({R.id.chat_us, R.id.dispute})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.chat_us:
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.chat_fragment, new ChatFragment(), "Tamil");
                fragmentTransaction.commit();

                break;
            case R.id.dispute:
                showDialog();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.anim_nothing, R.anim.slide_out_right);
    }
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
