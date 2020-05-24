package com.haatapp.app.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.haatapp.app.R;
import com.haatapp.app.adapter.WalletHistoryAdapter;
import com.haatapp.app.build.api.ApiClient;
import com.haatapp.app.build.api.ApiInterface;
import com.haatapp.app.helper.CustomDialog;
import com.haatapp.app.helper.GlobalData;
import com.haatapp.app.models.WalletHistory;

import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static com.haatapp.app.helper.GlobalData.currencySymbol;

public class WalletActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.add_btn)
    Button addBtn;
    @BindView(R.id.wallet_amount_txt)
    TextView walletAmountTxt;
    @BindView(R.id.wallet_history_recycler_view)
    RecyclerView walletHistoryRecyclerView;

    ApiInterface apiInterface = ApiClient.getRetrofit().create(ApiInterface.class);
    Context context = WalletActivity.this;
    CustomDialog customDialog;

    List<WalletHistory> walletHistoryHistoryList;
    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.title)
    TextView title;

    NumberFormat numberFormat = GlobalData.getNumberFormat();
    @BindView(R.id.error_layout)
    LinearLayout errorLayout;

    WalletHistoryAdapter walletHistoryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);
        ButterKnife.bind(this);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        customDialog = new CustomDialog(context);


        title.setText(context.getResources().getString(R.string.walletHistory));
        walletHistoryHistoryList = new ArrayList<>();
        walletHistoryRecyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        walletHistoryRecyclerView.setItemAnimator(new DefaultItemAnimator());
        walletHistoryRecyclerView.setHasFixedSize(true);

      if(getIntent().getStringExtra("add_hide")!=null)
      {
          if(getIntent().getStringExtra("add_hide").equals("hide"))
          {
              findViewById(R.id.add_btn).setVisibility(View.INVISIBLE);
          }
      }
        walletHistoryAdapter = new WalletHistoryAdapter(walletHistoryHistoryList);
        walletHistoryRecyclerView.setAdapter(walletHistoryAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            int walletMoney = GlobalData.profileModel.getWalletBalance();
            walletAmountTxt.setText(currencySymbol + " " + String.valueOf(walletMoney));
            getWalletHistory();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        }

    private void getWalletHistory() {
        customDialog.show();
        Call<List<WalletHistory>> call = apiInterface.getWalletHistory();
        call.enqueue(new Callback<List<WalletHistory>>() {
            @Override
            public void onResponse(@NonNull Call<List<WalletHistory>> call, @NonNull Response<List<WalletHistory>> response) {
                customDialog.dismiss();
                if (response.isSuccessful()) {
                    if (response.body() != null && response.body().size() > 0) {
                        walletHistoryHistoryList.addAll(response.body());
                        walletHistoryRecyclerView.getAdapter().notifyDataSetChanged();
                        errorLayout.setVisibility(View.GONE);
                    } else {
                        errorLayout.setVisibility(View.VISIBLE);
                    }
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
            public void onFailure(@NonNull Call<List<WalletHistory>> call, @NonNull Throwable t) {
                customDialog.dismiss();
            }
        });
    }

    @Override
    public void onBackPressed() {
         if(getIntent().getStringExtra("add_hide")!=null)
        {
            Log.e("under aa gya","gya...");
            super.onBackPressed();

        }
        else {
            startActivity(new Intent(this, AccountPaymentActivity.class).putExtra("is_show_wallet", true).putExtra("is_show_cash", false));
            finish();
            overridePendingTransition(R.anim.anim_nothing, R.anim.slide_out_right);
        }
        }

    @OnClick(R.id.add_btn)
    public void onViewClicked() {
        startActivity(new Intent(this, AddMoneyActivity.class));
        finish();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @OnClick(R.id.back)
    public void onBackClicked() {
        onBackPressed();
    }

}
