package com.haatapp.app.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.haatapp.app.CountryPicker.Country;
import com.haatapp.app.CountryPicker.CountryPicker;
import com.haatapp.app.CountryPicker.CountryPickerListener;
import com.haatapp.app.R;
import com.haatapp.app.build.api.ApiClient;
import com.haatapp.app.build.api.ApiInterface;
import com.haatapp.app.helper.ConnectionHelper;
import com.haatapp.app.helper.GlobalData;
import com.haatapp.app.helper.CustomDialog;
import com.haatapp.app.helper.SharedHelper;
import com.haatapp.app.models.ForgotPassword;
import com.haatapp.app.models.Otp;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class MobileNumberActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    @BindView(R.id.app_logo)
    ImageView appLogo;
    @BindView(R.id.et_mobile_number)
    EditText etMobileNumber;
    @BindView(R.id.next_btn)
    Button nextBtn;
    @BindView(R.id.connect_with)
    TextView connectWith;
    @BindView(R.id.facebook_login)
    ImageButton facebookLogin;
    @BindView(R.id.google_login)
    ImageButton googleLogin;
    ApiInterface apiInterface = ApiClient.getRetrofit().create(ApiInterface.class);
    @BindView(R.id.countryImage)
    ImageView mCountryFlagImageView;
    @BindView(R.id.countryNumber)
    TextView mCountryDialCodeTextView;
    @BindView(R.id.back_img)
    ImageView backImg;
    @BindView(R.id.already_have_aacount_txt)
    TextView alreadyHaveAacountTxt;
    private CountryPicker mCountryPicker;
    String country_code = "+91";
    Context context;
    boolean isSignUp = true;
    CustomDialog customDialog;


    /*----------Facebook Login---------------*/
    CallbackManager callbackManager;
    AccessTokenTracker accessTokenTracker;

    Button fb_login;
    JSONObject json;
    String fb_first_name = "", fb_id = "", profile_img = "", fb_email = "", fb_last_name = "";
    ConnectionHelper helper;
    Boolean isInternet;


    /*----------Google Login---------------*/
    GoogleApiClient mGoogleApiClient;
    private static final int RC_SIGN_IN = 100;
    private static final int REQ_SIGN_IN_REQUIRED = 100;
    public static int APP_REQUEST_CODE = 99;
    String accessToken = "";
    String loginBy = "";
    String TAG = "ActivitySocialLogin";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        setContentView(R.layout.activity_mobile_number);
        ButterKnife.bind(this);
        mCountryPicker = CountryPicker.newInstance("Select Country");
        context = MobileNumberActivity.this;
        customDialog = new CustomDialog(context);
        helper = new ConnectionHelper(context);
        isInternet = helper.isConnectingToInternet();

        callbackManager = CallbackManager.Factory.create();
        if (Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
                    /*----------Google Login---------------*/

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                //taken from google api console (Web api client id)
//                .requestIdToken("795253286119-p5b084skjnl7sll3s24ha310iotin5k4.apps.googleusercontent.com")
                .requestEmail()
                .build();
        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();


        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            isSignUp = bundle.getBoolean("signup", true);
        }
        // You can limit the displayed countries
        List<Country> countryList=Country.getAllCountries();
        Collections.sort(countryList, new Comparator<Country>() {
            @Override
            public int compare(Country s1, Country s2) {
                return s1.getName().compareToIgnoreCase(s2.getName());
            }
        });
        mCountryPicker.setCountriesList(countryList);
        setListener();

    }

    public void fbLogin() {
        if (isInternet) {

            LoginManager.getInstance().logInWithReadPermissions(
                    MobileNumberActivity.this, Arrays.asList("email"));

            LoginManager.getInstance().registerCallback(callbackManager,
                    new FacebookCallback<LoginResult>() {

                        public void onSuccess(LoginResult loginResult) {
                            if (AccessToken.getCurrentAccessToken() != null) {
                                Log.e("loginresult", "" + loginResult.getAccessToken().getToken());
                                SharedHelper.putKey(MobileNumberActivity.this, "access_token", loginResult.getAccessToken().getToken());
                                GlobalData.access_token = loginResult.getAccessToken().getToken();
                                RequestData();
                            }

                        }

                        @Override
                        public void onCancel() {
                            // App code
                        }

                        @Override
                        public void onError(FacebookException exception) {
                            // App code
                        }
                    });
        } else {
            //mProgressDialog.dismiss();
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage("Check your Internet").setCancelable(false);
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.setPositiveButton("Setting", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    Intent NetworkAction = new Intent(Settings.ACTION_SETTINGS);
                    startActivity(NetworkAction);

                }
            });
            builder.show();
        }

    }

    public void RequestData() {
        if (isInternet) {
            customDialog.show();
            GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                @Override
                public void onCompleted(JSONObject object, GraphResponse response) {
                    Log.e("response", "" + response);
                    json = response.getJSONObject();
                    Log.e("FB JSON", "" + json);
                    try {
                        if (json != null) {
                            GlobalData.name = json.optString("name");
                            GlobalData.email = json.optString("email");
                            com.facebook.Profile profile = com.facebook.Profile.getCurrentProfile();
                            String FBUserID = profile.getId();
                            Log.e("FBUserID", "" + FBUserID);
                            URL image_value = new URL("https://graph.facebook.com/" + FBUserID + "/picture?type=large");
                            GlobalData.imageUrl = image_value.toString();
                            Log.e("Connected FB", "" + GlobalData.name);
                            Log.e("Connected FB", "" + GlobalData.email);
                            Log.e("FBUserPhoto FB", GlobalData.imageUrl);
                            customDialog.dismiss();
                            GlobalData.loginBy = "facebook";
                            startActivity(new Intent(context, SignUpActivity.class));
                            overridePendingTransition(R.anim.slide_in_right, R.anim.anim_nothing);
                            finish();


                        } else {

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });
            Bundle parameters = new Bundle();
            parameters.putString("fields", "id,name,link,email,picture");
            request.setParameters(parameters);
            request.executeAsync();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage("Check your Internet").setCancelable(false);
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.setPositiveButton("Setting", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    Intent NetworkAction = new Intent(Settings.ACTION_SETTINGS);
                    startActivity(NetworkAction);

                }
            });
            builder.show();
        }


    }


    private void forgotPassord(String mobileNumber) {
        customDialog.show();
        Call<ForgotPassword> call = apiInterface.forgotPassword(mobileNumber);
        call.enqueue(new Callback<ForgotPassword>() {
            @Override
            public void onResponse(@NonNull Call<ForgotPassword> call, @NonNull Response<ForgotPassword> response) {
                customDialog.dismiss();



                if (response.isSuccessful()) {
                    Log.e("response scuess","he....");
                    GlobalData.profileModel = response.body().getUser();
                    GlobalData.otpValue = Integer.parseInt(response.body().getUser().getOtp());
                    Log.e("kali....",response.body().getMessage()+"----"+response.body().getUser());
                    startActivity(new Intent(context, OtpActivity.class).putExtra("signup", false));
                    overridePendingTransition(R.anim.slide_in_right, R.anim.anim_nothing);
                    finish();
                } else {
                    try {
                        Log.e("response scuess"," nahi he....");

                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Toast.makeText(context, jObjError.optString("phone"), Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ForgotPassword> call, @NonNull Throwable t) {
                t.printStackTrace();

                Log.e("test","fail");

            }
        });
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    public void getOtpVerification(HashMap<String, String> map) {
        customDialog.show();
        Call<Otp> call = apiInterface.postOtp(map);
        call.enqueue(new Callback<Otp>() {
            @Override
            public void onResponse(@NonNull Call<Otp> call, @NonNull Response<Otp> response) {
                customDialog.dismiss();

                if (response.isSuccessful()) {
                    Toast.makeText(MobileNumberActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    GlobalData.otpValue = response.body().getOtp();
                    Log.e("otp is ",GlobalData.otpValue+"");
                    startActivity(new Intent(MobileNumberActivity.this, OtpActivity.class));
                    finish();
                } else {
                    Log.e("error","error");
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        if (jObjError.has("phone")){
                            if(jObjError.optJSONArray("phone")!=null)
                            Toast.makeText(context, jObjError.optJSONArray("phone").get(0).toString(), Toast.LENGTH_LONG).show();
                        }
                        else if (jObjError.has("email"))
                            Toast.makeText(context, jObjError.optString("email"), Toast.LENGTH_LONG).show();
                        else
                            Toast.makeText(context, jObjError.optString("error"), Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<Otp> call, @NonNull Throwable t) {
                customDialog.dismiss();

            }
        });

    }

    private void setListener() {
        mCountryPicker.setListener(new CountryPickerListener() {
            @Override
            public void onSelectCountry(String name, String code, String dialCode,
                                        int flagDrawableResID) {
                mCountryDialCodeTextView.setText(dialCode);
                country_code=dialCode;
                mCountryFlagImageView.setImageResource(flagDrawableResID);
                mCountryPicker.dismiss();
            }
        });
        mCountryDialCodeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCountryPicker.show(getSupportFragmentManager(), "COUNTRY_PICKER");
            }
        });
        mCountryFlagImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCountryPicker.show(getSupportFragmentManager(), "COUNTRY_PICKER");
            }
        });
        getUserCountryInfo();
    }

    private void getUserCountryInfo() {
        Locale current = getResources().getConfiguration().locale;
        Country country = Country.getCountryFromSIM(MobileNumberActivity.this);
        if (country != null) {
            mCountryFlagImageView.setImageResource(country.getFlag());
            mCountryDialCodeTextView.setText(country.getDialCode());
            country_code = country.getDialCode();
        } else {
            Country us = new Country("IN", "India", "+91", R.drawable.flag_in);
            mCountryFlagImageView.setImageResource(us.getFlag());
            mCountryDialCodeTextView.setText(us.getDialCode());
            country_code = us.getDialCode();
            //Toast.makeText(MobileNumberActivity.this, "Required Sim", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
            if (requestCode == RC_SIGN_IN) {
                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                handleSignInResult(result);
            }
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        try {
            Log.d("Beginscreen", "handleSignInResult:" + result.isSuccess());
            if (result.isSuccess()) {
                // Signed in successfully, show authenticated UI.
                GoogleSignInAccount acct = result.getSignInAccount();
                GlobalData.name = acct.getDisplayName();
                GlobalData.email = acct.getEmail();
                GlobalData.imageUrl = "" + acct.getPhotoUrl();
                Log.d("Google", "display_name:" + acct.getDisplayName());
                Log.d("Google", "mail:" + acct.getEmail());
                Log.d("Google", "photo:" + acct.getPhotoUrl());
                new RetrieveTokenTask().execute(acct.getEmail());
            } else {
                Snackbar.make(this.findViewById(android.R.id.content), getResources().getString(R.string.google_login_failed), Snackbar.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class RetrieveTokenTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String accountName = params[0];
            String scopes = "oauth2:profile email";
            String token = null;
            try {
                token = GoogleAuthUtil.getToken(getApplicationContext(), accountName, scopes);
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
            } catch (UserRecoverableAuthException e) {
                startActivityForResult(e.getIntent(), REQ_SIGN_IN_REQUIRED);
            } catch (GoogleAuthException e) {
                Log.e(TAG, e.getMessage());
            }
            return token;
        }

        @Override
        protected void onPostExecute(String GoogleaccessToken) {
            super.onPostExecute(GoogleaccessToken);
            Log.e("Token", GoogleaccessToken);
            GlobalData.access_token = GoogleaccessToken;
            GlobalData.loginBy = "google";
            startActivity(new Intent(context, SignUpActivity.class));
            overridePendingTransition(R.anim.slide_in_right, R.anim.anim_nothing);
            finish();
        }
    }


    @OnClick({R.id.back_img, R.id.next_btn, R.id.already_have_aacount_txt, R.id.facebook_login, R.id.google_login})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back_img:
                onBackPressed();
                break;
            case R.id.already_have_aacount_txt:
                startActivity(new Intent(context, LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                overridePendingTransition(R.anim.slide_in_right, R.anim.anim_nothing);
                finish();
                break;
            case R.id.facebook_login:
                fbLogin();
//                Snackbar.make(this.findViewById(android.R.id.content), getResources().getString(R.string.coming_soon), Snackbar.LENGTH_SHORT).show();
                break;
            case R.id.google_login:
//                Snackbar.make(this.findViewById(android.R.id.content), getResources().getString(R.string.coming_soon), Snackbar.LENGTH_SHORT).show();
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, RC_SIGN_IN);
                break;

            case R.id.next_btn:
                String mobileNumber = country_code + etMobileNumber.getText().toString();
                if (isValidMobile(mobileNumber)) {
                    GlobalData.mobile = mobileNumber;
                    GlobalData.loginBy = "manual";
                    if (isSignUp) {
                        HashMap<String, String> map = new HashMap<>();
                        map.put("phone", mobileNumber);
                        getOtpVerification(map);
                    } else
                        forgotPassord(mobileNumber);
                } else {
                    Toast.makeText(this, "Please enter valid mobile number", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private boolean isValidMobile(String phone) {
        return !(phone == null || phone.length() < 6 || phone.length() > 13) && android.util.Patterns.PHONE.matcher(phone).matches();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.anim_nothing, R.anim.slide_out_right);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


}
