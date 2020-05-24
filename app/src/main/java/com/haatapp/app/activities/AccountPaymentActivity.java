package com.haatapp.app.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

//import com.braintreepayments.api.AndroidPay;
//import com.braintreepayments.api.BraintreeFragment;
//import com.braintreepayments.api.PayPal;
//import com.braintreepayments.api.dropin.DropInActivity;
//import com.braintreepayments.api.dropin.DropInRequest;
//import com.braintreepayments.api.dropin.DropInResult;
//import com.braintreepayments.api.dropin.utils.PaymentMethodType;
//import com.braintreepayments.api.exceptions.InvalidArgumentException;
//import com.braintreepayments.api.interfaces.BraintreeCancelListener;
//import com.braintreepayments.api.interfaces.BraintreeErrorListener;
//import com.braintreepayments.api.interfaces.PaymentMethodNonceCreatedListener;
//import com.braintreepayments.api.models.AndroidPayCardNonce;
//import com.braintreepayments.api.models.CardNonce;
//import com.braintreepayments.api.models.ClientToken;
//import com.braintreepayments.api.models.PayPalAccountNonce;
//import com.braintreepayments.api.models.PaymentMethodNonce;
//import com.braintreepayments.api.models.PostalAddress;
//import com.braintreepayments.api.models.VenmoAccountNonce;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.haatapp.app.R;
import com.haatapp.app.adapter.AccountPaymentAdapter;
//import com.shopjinu.app.braintree.CreateTransactionActivity;
//import com.shopjinu.app.braintree.Settings;
import com.haatapp.app.build.api.ApiClient;
import com.haatapp.app.build.api.ApiInterface;
import com.haatapp.app.build.configure.BuildConfigure;
import com.haatapp.app.fragments.CartFragment;
import com.haatapp.app.helper.CustomDialog;
import com.haatapp.app.helper.GlobalData;
import com.haatapp.app.models.Card;
import com.haatapp.app.models.Message;
import com.haatapp.app.models.Order;
//import com.google.android.gms.identity.intents.model.CountrySpecification;
//import com.google.android.gms.identity.intents.model.UserAddress;
//import com.google.android.gms.wallet.Cart;
//import com.google.android.gms.wallet.LineItem;

import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.haatapp.app.helper.GlobalData.cardArrayList;
import static com.haatapp.app.helper.GlobalData.currencySymbol;
import static com.haatapp.app.helper.GlobalData.isCardChecked;

public class AccountPaymentActivity extends AppCompatActivity  {
//    public class AccountPaymentActivity extends AppCompatActivity implements PaymentMethodNonceCreatedListener,
//            BraintreeCancelListener, BraintreeErrorListener, DropInResult.DropInResultListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.payment_method_lv)
    ListView paymentMethodLv;
    @BindView(R.id.wallet_amount_txt)
    TextView walletAmtTxt;
    @BindView(R.id.wallet_layout)
    RelativeLayout walletLayout;
    NumberFormat numberFormat = GlobalData.getNumberFormat();
    @BindView(R.id.add_new_cart)
    TextView addNewCart;

//    //Braintree integration
//    private static final String KEY_AUTHORIZATION = "com.braintreepayments.demo.KEY_AUTHORIZATION";
//    protected String mAuthorization = "sandbox_8hsjt2ms_p4rvqdnmvxcqdm76";
//    protected String mCustomerId;
//    protected BraintreeFragment mBraintreeFragment;
//    private static final int DROP_IN_REQUEST = 100;
//    private static final String KEY_NONCE = "nonce";
//
//    private PaymentMethodType mPaymentMethodType;
//    private PaymentMethodNonce mNonce;
//
//    private CardView mPaymentMethod;
//    private ImageView mPaymentMethodIcon;
//    private TextView mPaymentMethodTitle;
//    private TextView mPaymentMethodDescription;
//    private TextView mNonceString;
//    private TextView mNonceDetails;
//    private TextView mDeviceData;
//
//    private Button mAddPaymentMethodButton;
//    private Button mPurchaseButton;
//    private ProgressDialog mLoading;

    private boolean mShouldMakePurchase = false;
    private boolean mPurchased = false;
    public static ApiInterface apiInterface = ApiClient.getRetrofit().create(ApiInterface.class);
    public static CustomDialog customDialog;
    public static Context context;
    public static AccountPaymentAdapter accountPaymentAdapter;
    public static LinearLayout cashPaymentLayout;
    public static LinearLayout walletPaymentLayout;
    public static RadioButton cashCheckBox;
    public static Button proceedToPayBtn;
    boolean isWalletVisible = false;
    boolean isCashVisible = false;

String self_deliver="no";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_payment);
        ButterKnife.bind(this);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        context = AccountPaymentActivity.this;
        customDialog = new CustomDialog(context);
        cashPaymentLayout = (LinearLayout) findViewById(R.id.cash_payment_layout);
        walletPaymentLayout = (LinearLayout) findViewById(R.id.wallet_payment_layout);
        proceedToPayBtn = (Button) findViewById(R.id.proceed_to_pay_btn);
        cashCheckBox = (RadioButton) findViewById(R.id.cash_check_box);

        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        isWalletVisible = getIntent().getBooleanExtra("is_show_wallet", false);
        isCashVisible = getIntent().getBooleanExtra("is_show_cash", false);

        try
        {
            self_deliver= getIntent().getStringExtra("self_deliver");
        }
        catch (Exception e)
        {

        }
        cardArrayList = new ArrayList<>();
        accountPaymentAdapter = new AccountPaymentAdapter(AccountPaymentActivity.this, cardArrayList, !isCashVisible);
        paymentMethodLv.setAdapter(accountPaymentAdapter);

        if (isWalletVisible)
            walletPaymentLayout.setVisibility(VISIBLE);
        else
            walletPaymentLayout.setVisibility(GONE);
        if (isCashVisible)
            cashPaymentLayout.setVisibility(VISIBLE);
        else
            cashPaymentLayout.setVisibility(GONE);

        cashPaymentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cashCheckBox.setChecked(true);
                isCardChecked = false;
                accountPaymentAdapter.notifyDataSetChanged();
                proceedToPayBtn.setVisibility(VISIBLE);
                for (int i = 0; i < cardArrayList.size(); i++) {
                    cardArrayList.get(i).setChecked(false);
                }
                accountPaymentAdapter.notifyDataSetChanged();
            }
        });

        proceedToPayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isCardChecked) {
                    for (int i = 0; i < cardArrayList.size(); i++) {
                        if (cardArrayList.get(i).isChecked()) {
                            Card card = cardArrayList.get(i);
                            CartFragment.checkoutMap.put("payment_mode", "stripe");
                            CartFragment.checkoutMap.put("card_id", String.valueOf(card.getId()));
                            checkOut(CartFragment.checkoutMap);
                            return;
                        }
                    }
                } else if (cashCheckBox.isChecked()) {
                    CartFragment.checkoutMap.put("payment_mode", "cash");
                    checkOut(CartFragment.checkoutMap);
                } else {
                    Toast.makeText(context, "Please select payment mode", Toast.LENGTH_SHORT).show();
                }

            }
        });


//        if (savedInstanceState != null) {
//            if (savedInstanceState.containsKey(KEY_NONCE)) {
//                mNonce = savedInstanceState.getParcelable(KEY_NONCE);
//            }
//        }
    }

    private void checkOut(HashMap<String, String> map) {
        customDialog.show();
        if(self_deliver.equals("yes")) {
            Thread thread
                    = new Thread(new Runnable() {
                @Override
                public void run() {
                    StringRequest stringRequest = new StringRequest(Request.Method.GET, BuildConfigure.BASE_URL+"self", new com.android.volley.Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                        }
                    }, new com.android.volley.Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    }) {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> map1 = new HashMap<>();
                            SharedPreferences sharedPreferences = context.getSharedPreferences("MyPrefs", MODE_PRIVATE);

                            map1.put("refer_code", sharedPreferences.getString("refer_Code", "nan"));

                            return map1;
                        }
                    };
                    Volley.newRequestQueue(getApplicationContext()).add(stringRequest);
                }
            });
            thread.start();
        }


        final SharedPreferences pref = context.getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        SharedPreferences prefs = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
            Log.e("kalalal","http://shopjinu.com/public/offer?refer_code="+prefs.getString("refer_code","nothing")+"&refer_from="+prefs.getString("refer_from","nothing"));

                StringRequest stringRequest=new StringRequest(Request.Method.GET, BuildConfigure.BASE_URL+"offer_count?refer_code="+prefs.getString("refer_code","nothing")+"&refer_from="+prefs.getString("refer_from","nothing"), new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("response",response.toString());

            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();

            }
        });
        Volley.newRequestQueue(getApplicationContext()).add(stringRequest);


        Call<Order> call = apiInterface.postCheckout(map);
        call.enqueue(new Callback<Order>() {
            @Override
            public void onResponse(@NonNull Call<Order> call, @NonNull Response<Order> response) {
                customDialog.dismiss();
                if (response.isSuccessful()) {
                    GlobalData.addCart = null;
                    GlobalData.notificationCount = 0;
                    GlobalData.selectedShop = null;
                    GlobalData.profileModel.setWalletBalance(response.body().getUser().getWalletBalance());
                    GlobalData.isSelectedOrder = new Order();
                    GlobalData.isSelectedOrder = response.body();
                    startActivity(new Intent(context, CurrentOrderDetailActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
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
                Toast.makeText(AccountPaymentActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }

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
            }
        });

    }

    public static void deleteCard(final int id) {
        customDialog.show();
        Call<Message> call = apiInterface.deleteCard(id);
        call.enqueue(new Callback<Message>() {
            @Override
            public void onResponse(@NonNull Call<Message> call, @NonNull Response<Message> response) {
                customDialog.dismiss();
                if (response.isSuccessful()) {
                    Toast.makeText(context, "" + response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    for (int i = 0; i < GlobalData.cardArrayList.size(); i++) {
                        if (GlobalData.cardArrayList.get(i).getId().equals(id)) {
                            GlobalData.cardArrayList.remove(i);
                            accountPaymentAdapter.notifyDataSetChanged();
                            return;
                        }
                    }
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
            public void onFailure(@NonNull Call<Message> call, @NonNull Throwable t) {
                Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show();
                customDialog.dismiss();
            }
        });


    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.anim_nothing, R.anim.slide_out_right);
    }

//    public void launchDropIn(View v) {
//        DropInRequest dropInRequest = new DropInRequest()
//                .clientToken(mAuthorization)
//                .amount("1.00")
//                .requestThreeDSecureVerification(Settings.isThreeDSecureEnabled(this))
//                .collectDeviceData(Settings.shouldCollectDeviceData(this))
//                .androidPayCart(getAndroidPayCart())
//                .androidPayShippingAddressRequired(Settings.isAndroidPayShippingAddressRequired(this))
//                .androidPayPhoneNumberRequired(Settings.isAndroidPayPhoneNumberRequired(this))
//                .androidPayAllowedCountriesForShipping(Settings.getAndroidPayAllowedCountriesForShipping(this));
//
//        if (Settings.isPayPalAddressScopeRequested(this)) {
//            dropInRequest.paypalAdditionalScopes(Collections.singletonList(PayPal.SCOPE_ADDRESS));
//        }
//
//        startActivityForResult(dropInRequest.getIntent(this), DROP_IN_REQUEST);
//    }

//    private Cart getAndroidPayCart() {
//        return Cart.newBuilder()
//                .setCurrencyCode(Settings.getAndroidPayCurrency(this))
//                .setTotalPrice("1.00")
//                .addLineItem(LineItem.newBuilder()
//                        .setCurrencyCode("USD")
//                        .setDescription("Description")
//                        .setQuantity("1")
//                        .setUnitPrice("1.00")
//                        .setTotalPrice("1.00")
//                        .build())
//                .build();
//    }


    protected void showDialog(String message) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        safelyCloseLoadingView();
//
//        if (resultCode == RESULT_OK) {
//            DropInResult result = data.getParcelableExtra(DropInResult.EXTRA_DROP_IN_RESULT);
//            displayResult(result.getPaymentMethodNonce(), result.getDeviceData());
//            mPurchaseButton.setEnabled(true);
//        } else if (resultCode != RESULT_CANCELED) {
//            safelyCloseLoadingView();
//            showDialog(((Exception) data.getSerializableExtra(DropInActivity.EXTRA_ERROR))
//                    .getMessage());
//        }
//    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
//        if (mNonce != null) {
//            outState.putParcelable(KEY_NONCE, mNonce);
//        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        int walletMoney = GlobalData.profileModel.getWalletBalance();
        walletAmtTxt.setText(currencySymbol + " " + String.valueOf(walletMoney));
        getCardList();
//        if (mPurchased) {
//            mPurchased = false;
//            clearNonce();
//
//            try {
//                if (ClientToken.fromString(mAuthorization) instanceof ClientToken) {
//                    DropInResult.fetchDropInResult(this, mAuthorization, this);
//                } else {
//                    mAddPaymentMethodButton.setVisibility(VISIBLE);
//                }
//            } catch (InvalidArgumentException e) {
//                mAddPaymentMethodButton.setVisibility(VISIBLE);
//            }
//        }
    }


    @OnClick({R.id.wallet_layout, R.id.add_new_cart})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.wallet_layout:
                startActivity(new Intent(this, WalletActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.anim_nothing);
                finish();
                break;
            case R.id.add_new_cart:
//              launchDropIn(view);
                startActivity(new Intent(AccountPaymentActivity.this, AddCardActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.anim_nothing);
                break;
        }
    }

//    @Override
//    public void onResult(DropInResult result) {
//
//        mPaymentMethodType = result.getPaymentMethodType();
//
//        mPaymentMethodIcon.setImageResource(result.getPaymentMethodType().getDrawable());
//        if (result.getPaymentMethodNonce() != null) {
//            displayResult(result.getPaymentMethodNonce(), result.getDeviceData());
//        } else if (result.getPaymentMethodType() == PaymentMethodType.ANDROID_PAY) {
//            mPaymentMethodTitle.setText(PaymentMethodType.ANDROID_PAY.getLocalizedName());
//            mPaymentMethodDescription.setText("");
//            mPaymentMethod.setVisibility(VISIBLE);
//        }
//
//    }
//
//    @Override
//    public void onCancel(int requestCode) {
//
//    }
//
//    @Override
//    public void onError(Exception error) {
//
//    }
//
//    @Override
//    public void onPaymentMethodNonceCreated(PaymentMethodNonce paymentMethodNonce) {
//        displayResult(paymentMethodNonce, null);
//        safelyCloseLoadingView();
//        if (mShouldMakePurchase) {
//            purchase(null);
//        }
//
//    }
//
//
//    public void purchase(View v) {
//        if (mPaymentMethodType == PaymentMethodType.ANDROID_PAY && mNonce == null) {
//            ArrayList<CountrySpecification> countries = new ArrayList<>();
//            for (String countryCode : Settings.getAndroidPayAllowedCountriesForShipping(this)) {
//                countries.add(new CountrySpecification(countryCode));
//            }
//
//            mShouldMakePurchase = true;
//
//            AndroidPay.requestAndroidPay(mBraintreeFragment, getAndroidPayCart(),
//                    Settings.isAndroidPayShippingAddressRequired(this),
//                    Settings.isAndroidPayPhoneNumberRequired(this), countries);
//        } else {
//            Intent intent = new Intent(this, CreateTransactionActivity.class)
//                    .putExtra(CreateTransactionActivity.EXTRA_PAYMENT_METHOD_NONCE, mNonce);
//            startActivity(intent);
//
//
//            mPurchased = true;
//        }
//    }
//
//    private void displayResult(PaymentMethodNonce paymentMethodNonce, String deviceData) {
//        mNonce = paymentMethodNonce;
//        mPaymentMethodType = PaymentMethodType.forType(mNonce);
//
//        mPaymentMethodIcon.setImageResource(PaymentMethodType.forType(mNonce).getDrawable());
//        mPaymentMethodTitle.setText(paymentMethodNonce.getTypeLabel());
//        mPaymentMethodDescription.setText(paymentMethodNonce.getDescription());
//        mPaymentMethod.setVisibility(VISIBLE);
//
//        mNonceString.setText(getString(R.string.nonce) + ": " + mNonce.getNonce());
//        mNonceString.setVisibility(VISIBLE);
//
//        String details = "";
//        if (mNonce instanceof CardNonce) {
//            CardNonce cardNonce = (CardNonce) mNonce;
//
//            details = "Card Last Two: " + cardNonce.getLastTwo() + "\n";
//            details += "3DS isLiabilityShifted: " + cardNonce.getThreeDSecureInfo().isLiabilityShifted() + "\n";
//            details += "3DS isLiabilityShiftPossible: " + cardNonce.getThreeDSecureInfo().isLiabilityShiftPossible();
//        } else if (mNonce instanceof PayPalAccountNonce) {
//            PayPalAccountNonce paypalAccountNonce = (PayPalAccountNonce) mNonce;
//
//            details = "First name: " + paypalAccountNonce.getFirstName() + "\n";
//            details += "Last name: " + paypalAccountNonce.getLastName() + "\n";
//            details += "Email: " + paypalAccountNonce.getEmail() + "\n";
//            details += "Phone: " + paypalAccountNonce.getPhone() + "\n";
//            details += "Payer id: " + paypalAccountNonce.getPayerId() + "\n";
//            details += "Client metadata id: " + paypalAccountNonce.getClientMetadataId() + "\n";
//            details += "Billing address: " + formatAddress(paypalAccountNonce.getBillingAddress()) + "\n";
//            details += "Shipping address: " + formatAddress(paypalAccountNonce.getShippingAddress());
//        } else if (mNonce instanceof AndroidPayCardNonce) {
//            AndroidPayCardNonce androidPayCardNonce = (AndroidPayCardNonce) mNonce;
//
//            details = "Underlying Card Last Two: " + androidPayCardNonce.getLastTwo() + "\n";
//            details += "Email: " + androidPayCardNonce.getEmail() + "\n";
//            details += "Billing address: " + formatAddress(androidPayCardNonce.getBillingAddress()) + "\n";
//            details += "Shipping address: " + formatAddress(androidPayCardNonce.getShippingAddress());
//        } else if (mNonce instanceof VenmoAccountNonce) {
//            VenmoAccountNonce venmoAccountNonce = (VenmoAccountNonce) mNonce;
//
//            details = "Username: " + venmoAccountNonce.getUsername();
//        }
//
//        mNonceDetails.setText(details);
//        mNonceDetails.setVisibility(VISIBLE);
//
//        mDeviceData.setText("Device Data: " + deviceData);
//        mDeviceData.setVisibility(VISIBLE);
//
//        mAddPaymentMethodButton.setVisibility(GONE);
//        mPurchaseButton.setEnabled(true);
//    }
//
//    private void clearNonce() {
//        mPaymentMethod.setVisibility(GONE);
//        mNonceString.setVisibility(GONE);
//        mNonceDetails.setVisibility(GONE);
//        mDeviceData.setVisibility(GONE);
//        mPurchaseButton.setEnabled(false);
//    }

//    private String formatAddress(PostalAddress address) {
//        return address.getRecipientName() + " " + address.getStreetAddress() + " " +
//                address.getExtendedAddress() + " " + address.getLocality() + " " + address.getRegion() +
//                " " + address.getPostalCode() + " " + address.getCountryCodeAlpha2();
//    }
//
//    private String formatAddress(UserAddress address) {
//        if (address == null) {
//            return "null";
//        }
//        return address.getName() + " " + address.getAddress1() + " " + address.getAddress2() + " " +
//                address.getAddress3() + " " + address.getAddress4() + " " + address.getAddress5() + " " +
//                address.getLocality() + " " + address.getAdministrativeArea() + " " + address.getPostalCode() + " " +
//                address.getSortingCode() + " " + address.getCountryCode();
//    }

//
//    private void safelyCloseLoadingView() {
//        if (mLoading != null && mLoading.isShowing()) {
//            mLoading.dismiss();
//        }
//    }

}
