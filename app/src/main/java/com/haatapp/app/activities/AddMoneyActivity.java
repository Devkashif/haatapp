package com.haatapp.app.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.haatapp.app.R;
import com.haatapp.app.adapter.AccountPaymentAdapter;
import com.haatapp.app.build.api.ApiClient;
import com.haatapp.app.build.api.ApiInterface;
import com.haatapp.app.helper.GlobalData;
import com.haatapp.app.helper.CustomDialog;
import com.haatapp.app.models.AddMoney;
import com.haatapp.app.models.Card;

import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static com.haatapp.app.helper.GlobalData.cardArrayList;
import static com.haatapp.app.helper.GlobalData.isCardChecked;

public class AddMoneyActivity extends AppCompatActivity {

    public static final String TAG = "AddMoneyActivity";

    ApiInterface apiInterface = ApiClient.getRetrofit().create(ApiInterface.class);
    Context context = AddMoneyActivity.this;
    CustomDialog customDialog;
    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.promo_layout)
    RelativeLayout promoLayout;
    @BindView(R.id.payment_method_lv)
    ListView paymentMethodLv;

    NumberFormat numberFormat = GlobalData.getNumberFormat();
    @BindView(R.id.amount_txt)
    EditText amountTxt;
    @BindView(R.id.pay_btn)
    Button payBtn;


    public static AccountPaymentAdapter accountPaymentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_amount);
        ButterKnife.bind(this);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        customDialog = new CustomDialog(context);
        title.setText(context.getResources().getString(R.string.add_money));
        cardArrayList = new ArrayList<>();
        accountPaymentAdapter = new AccountPaymentAdapter(context, cardArrayList, false);
        paymentMethodLv.setAdapter(accountPaymentAdapter);
        amountTxt.setHint(GlobalData.currencySymbol);

    }

    @Override
    protected void onResume() {
        super.onResume();
        getCardList();
    }

    //TODO update getCards API and the recyclerview
    private void getCardList() {
        customDialog.show();
        Call<List<Card>> call = apiInterface.getCardList();
        call.enqueue(new Callback<List<Card>>() {
            @Override
            public void onResponse(@NonNull Call<List<Card>> call, @NonNull Response<List<Card>> response) {
                customDialog.dismiss();
                if (response.isSuccessful()) {
                    cardArrayList.clear();
                    cardArrayList.addAll(response.body());
                    accountPaymentAdapter.notifyDataSetChanged();
                } else {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Toast.makeText(context, jObjError.optString("error"), Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Card>> call, @NonNull Throwable t) {
                customDialog.dismiss();
                Toast.makeText(AddMoneyActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();

            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, WalletActivity.class));
        overridePendingTransition(R.anim.anim_nothing, R.anim.slide_out_right);
        finish();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @OnClick({R.id.back, R.id.promo_layout, R.id.pay_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back:
                onBackPressed();
                break;
            case R.id.pay_btn:
                String amount = amountTxt.getText().toString();
                if (!isCardChecked) {
                    Toast.makeText(context, "Please choose your card", Toast.LENGTH_SHORT).show();
                } else if (amount.equalsIgnoreCase("")) {
                    Toast.makeText(context, "Please enter amount", Toast.LENGTH_SHORT).show();
                } else if (Integer.parseInt(amount) == 0) {
                    Toast.makeText(context, "Please enter valid amount", Toast.LENGTH_SHORT).show();
                } else if (isCardChecked) {
                    for (int i = 0; i < cardArrayList.size(); i++) {
                        if (cardArrayList.get(i).isChecked()) {
                            Card card = cardArrayList.get(i);
                            HashMap<String, String> map = new HashMap<>();
                            map.put("amount", "" + amountTxt.getText().toString());
                            map.put("card_id", card.getId().toString());
                            addMoney(map);
                            return;
                        }
                    }
                } else {
                    Toast.makeText(context, "Please select your card", Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.promo_layout:
                startActivity(new Intent(this, PromotionActivity.class).putExtra("tag", TAG));
                overridePendingTransition(R.anim.slide_in_right, R.anim.anim_nothing);
                finish();
                break;
        }
    }

    public void alertDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message)
                .setPositiveButton(getResources().getString(R.string.okay), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(context, AccountPaymentActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        overridePendingTransition(R.anim.slide_in_right, R.anim.anim_nothing);
                        finish();

                    }
                });
//                .setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        // do nothing
//                        dialog.dismiss();
//                    }
//                });
        AlertDialog alert = builder.create();
        alert.show();
//        Button nbutton = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
//        nbutton.setTextColor(getResources().getColor(R.color.theme));
//        nbutton.setTypeface(nbutton.getTypeface(), Typeface.BOLD);
        Button pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
        pbutton.setTextColor(ContextCompat.getColor(context, R.color.theme));
        pbutton.setTypeface(pbutton.getTypeface(), Typeface.BOLD);
    }

    private void addMoney(HashMap<String, String> map) {
        customDialog.show();
        Call<AddMoney> call = apiInterface.addMoney(map);
        call.enqueue(new Callback<AddMoney>() {
            @Override
            public void onResponse(@NonNull Call<AddMoney> call, @NonNull Response<AddMoney> response) {
                customDialog.dismiss();
                if (response.isSuccessful()) {
//                    Toast.makeText(AddMoneyActivity.this, context.getResources().getString(R.string.) , Toast.LENGTH_SHORT).show();
                    GlobalData.profileModel.setWalletBalance(response.body().getWalletBalance());
                    onBackPressed();
                } else {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Toast.makeText(context, jObjError.optString("card_id"), Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<AddMoney> call, @NonNull Throwable t) {
                customDialog.dismiss();
                Toast.makeText(AddMoneyActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        });

    }
}
