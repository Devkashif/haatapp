package com.haatapp.app.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.braintreepayments.cardform.view.CardForm;
import com.haatapp.app.R;
import com.haatapp.app.build.api.ApiClient;
import com.haatapp.app.build.api.ApiInterface;
import com.haatapp.app.build.configure.BuildConfigure;
import com.haatapp.app.helper.CustomDialog;
import com.haatapp.app.models.Message;
import com.haatapp.app.utils.Utils;
import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.exception.AuthenticationException;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;

import org.json.JSONObject;

import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class AddCardActivity extends AppCompatActivity {


    String Card_Token = "";
    CustomDialog customDialog;

    static final Pattern CODE_PATTERN = Pattern
            .compile("([0-9]{0,4})|([0-9]{4}-)+|([0-9]{4}-[0-9]{0,4})+");
    @BindView(R.id.card_form)
    CardForm cardForm;
    @BindView(R.id.addCard)
    Button addCard;
    @BindView(R.id.activity_add_card)
    LinearLayout activityAddCard;
    Context context;
    Activity activity;
    ApiInterface apiInterface = ApiClient.getRetrofit().create(ApiInterface.class);
    @BindView(R.id.toolbar)
    Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setTheme(R.style.Mytheme);
        setContentView(R.layout.activity_add_card);
        ButterKnife.bind(this);
        context = AddCardActivity.this;
        activity = AddCardActivity.this;
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        cardForm.cardRequired(true)
                .expirationRequired(true)
                .cvvRequired(true)
                .postalCodeRequired(false)
                .mobileNumberRequired(false)
                .actionLabel("Add CardDetails")
                .setup(activity);

        addCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customDialog = new CustomDialog(context);
                customDialog.setCancelable(false);
                if (customDialog != null)
                    customDialog.show();
                if (cardForm.getCardNumber() == null || cardForm.getExpirationMonth() == null || cardForm.getExpirationYear() == null || cardForm.getCvv() == null) {
                    if ((customDialog != null) && (customDialog.isShowing()))
                        customDialog.dismiss();
                    displayMessage(context.getResources().getString(R.string.enter_card_details));
                } else {
                    if (cardForm.getCardNumber().equals("") || cardForm.getExpirationMonth().equals("") || cardForm.getExpirationYear().equals("") || cardForm.getCvv().equals("")) {
                        if ((customDialog != null) && (customDialog.isShowing()))
                            customDialog.dismiss();
                        Log.e("card info",cardForm.getCardNumber()+" -- "+cardForm.getExpirationMonth()+"--  "+cardForm.getExpirationYear()+" -- "+cardForm.getCvv() );
                        displayMessage(context.getResources().getString(R.string.enter_card_details));
                    } else {
                        String cardNumber = cardForm.getCardNumber();
                        int month = Integer.parseInt(cardForm.getExpirationMonth());
                        int year = Integer.parseInt(cardForm.getExpirationYear());
                        String cvv = cardForm.getCvv();
                        Utils.print("MyTest", "CardDetails Number: " + cardNumber + "Month: " + month + " Year: " + year);

                        Card card = new Card(cardNumber, month, year, cvv);
                        try {
                            Stripe stripe = new Stripe(BuildConfigure.STRIPE_PK);
                            stripe.createToken(
                                    card,
                                    new TokenCallback() {
                                        public void onSuccess(Token token) {
                                            // Send token to your server
                                            Utils.print("CardToken:", " " + token.getId());
                                            Utils.print("CardToken:", " " + token.getCard().getLast4());
                                            Card_Token = token.getId();
                                            addCardToAccount(Card_Token);
                                        }

                                        public void onError(Exception error) {
                                            // Show localized error message
                                            displayMessage(context.getResources().getString(R.string.enter_card_details));
                                            if ((customDialog != null) && (customDialog.isShowing()))
                                                customDialog.dismiss();
                                        }
                                    }
                            );
                        } catch (AuthenticationException e) {
                            e.printStackTrace();
                            if ((customDialog != null) && (customDialog.isShowing()))
                                customDialog.dismiss();
                        }
                    }

                }
            }
        });

    }


    public void addCardToAccount(final String cardToken) {
        Log.e("stripe_token", cardToken);
        Call<Message> call = apiInterface.addCard(cardToken);
        call.enqueue(new Callback<Message>() {
            @Override
            public void onResponse(@NonNull Call<Message> call, @NonNull Response<Message> response) {
                customDialog.dismiss();
                if (response.isSuccessful()) {
                    Toast.makeText(context, "" + response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    try {
                        Log.e("kali...error","error");
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Toast.makeText(context, jObjError.optString("error"), Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<Message> call, @NonNull Throwable t) {
                Toast.makeText(AddCardActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                customDialog.dismiss();
                Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        });

    }


    public void displayMessage(String toastString) {
        Snackbar.make(getCurrentFocus(), toastString, Snackbar.LENGTH_SHORT)
                .setAction("Action", null).show();
        // Toast.makeText(context, ""+toastString, Toast.LENGTH_SHORT).show();
    }

//    private void refreshAccessToken() {
//
//
//        JSONObject object = new JSONObject();
//        try {
//
//            object.put("grant_type", "refresh_token");
//            object.put("client_id", URLHelper.client_id);
//            object.put("client_secret", URLHelper.client_secret);
//            object.put("refresh_token", SharedHelper.getKey(context, "refresh_token"));
//            object.put("scope", "");
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.login, object, new com.android.volley.Response.Listener<JSONObject>() {
//            @Override
//            public void onResponse(JSONObject response) {
//
//                Log.v("SignUpResponse", response.toString());
//                SharedHelper.putKey(context, "access_token", response.optString("access_token"));
//                SharedHelper.putKey(context, "refresh_token", response.optString("refresh_token"));
//                SharedHelper.putKey(context, "token_type", response.optString("token_type"));
//                addCardToAccount(Card_Token);
//            }
//        }, new com.android.volley.Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                String json = null;
//                String Message;
//                NetworkResponse response = error.networkResponse;
//
//                if (response != null && response.data != null) {
//                    SharedHelper.putKey(context, "loggedIn", context.getResources().getString(R.string.False));
//                    GoToBeginActivity();
//                } else {
//                    if (error instanceof NoConnectionError) {
//                        displayMessage(context.getResources().getString(R.string.oops_connect_your_internet));
//                    } else if (error instanceof NetworkError) {
//                        displayMessage(context.getResources().getString(R.string.oops_connect_your_internet));
//                    } else if (error instanceof TimeoutError) {
//                        refreshAccessToken();
//                    }
//                }
//            }
//        }) {
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                HashMap<String, String> headers = new HashMap<String, String>();
//                headers.put("X-Requested-With", "XMLHttpRequest");
//                return headers;
//            }
//        };
//
//        TranxitApplication.getInstance().addToRequestQueue(jsonObjectRequest);
//
//    }


    public void GoToBeginActivity() {
        Intent mainIntent = new Intent(activity, CategoryActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(mainIntent);
        activity.finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
