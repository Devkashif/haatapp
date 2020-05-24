package com.haatapp.app;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.aurelhubert.ahbottomnavigation.notification.AHNotification;
import com.haatapp.app.build.api.ApiClient;
import com.haatapp.app.build.api.ApiInterface;
import com.haatapp.app.fragments.CartFragment;
import com.haatapp.app.fragments.HomeFragment;
import com.haatapp.app.fragments.ProfileFragment;
import com.haatapp.app.fragments.SearchFragment;
import com.haatapp.app.helper.GlobalData;
import com.haatapp.app.helper.ConnectionHelper;
import com.haatapp.app.helper.SharedHelper;
import com.haatapp.app.models.DisputeMessage;
import com.facebook.FacebookSdk;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static com.haatapp.app.helper.GlobalData.disputeMessageList;
import static com.haatapp.app.helper.GlobalData.notificationCount;
import static com.haatapp.app.helper.GlobalData.profileModel;

public class HomeActivity extends AppCompatActivity implements LocationListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, ActivityCompat.OnRequestPermissionsResultCallback {

    private static final int TIME_DELAY = 2000;
    private static long back_pressed;
    public static AHBottomNavigation bottomNavigation;
    private ConnectionHelper connectionHelper;
    private Fragment fragment;
    public static FragmentManager fragmentManager;

    // LogCat tag
    private static final String TAG = "HomeActivity";
    private final static int PLAY_SERVICES_REQUEST = 1000;
    private final static int REQUEST_CHECK_SETTINGS = 2000;

    private Location mLastLocation;

    // Google client to interact with Google API
    private GoogleApiClient mGoogleApiClient;

    public static double latitude;
    public static double longitude;
    LocationRequest mLocationRequest;

    int itemCount = 0;
    ApiInterface apiInterface = ApiClient.getRetrofit().create(ApiInterface.class);
    Context context = HomeActivity.this;
    public static AHNotification notification;
    private static final int ASK_MULTIPLE_PERMISSION_REQUEST_CODE = 0;

    FusedLocationProviderClient mFusedLocationClient;
    boolean isChangePassword=false;
    Retrofit retrofit;
    public static FragmentTransaction transaction;
    public static  String catid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (SharedHelper.getKey(context, "login_by").equals("facebook"))
            FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_home);
        Log.e("home ","working..");
        connectionHelper = new ConnectionHelper(this);

        isChangePassword = getIntent().getBooleanExtra("change_language", false);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mFusedLocationClient.getLastLocation()
                        .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                // Got last known location. In some rare situations this can be null.
                                if (location != null) {
                                    // Logic to handle location object
                                    latitude = location.getLatitude();
                                    longitude = location.getLongitude();
                                    GlobalData.latitude = location.getLatitude();
                                    GlobalData.longitude = location.getLongitude();
                                    Log.e("latitude3", "" + location.getLatitude());
                                    Log.e("longitude3", "" + location.getLongitude());
                                    Log.e("GlobalData.latitude3", "" + GlobalData.latitude);
                                    Log.e("GlobalData.longitude3 ", "" + GlobalData.longitude);
                                    getAddress();
                                }
                            }
                        });

            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, ASK_MULTIPLE_PERMISSION_REQUEST_CODE);
            }
        } else {
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                // Logic to handle location object
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                                GlobalData.latitude = location.getLatitude();
                                GlobalData.longitude = location.getLongitude();
                                Log.e("latitude3", "" + location.getLatitude());
                                Log.e("longitude3", "" + location.getLongitude());
                                Log.e("GlobalData.latitude3", "" + GlobalData.latitude);
                                Log.e("GlobalData.longitude3 ", "" + GlobalData.longitude);
                                getAddress();
                            }
                        }
                    });
        }

        // check availability of play services
        if (checkPlayServices()) {
            // Building the GoogleApi client
            buildGoogleApiClient();
        }

        if (profileModel != null)
            getNotificationItemCount();

        fragmentManager = getSupportFragmentManager();
        transaction = fragmentManager.beginTransaction();

        bottomNavigation = (AHBottomNavigation) findViewById(R.id.bottom_navigation);

        // Create items
        AHBottomNavigationItem item1 = new AHBottomNavigationItem(R.string.home, R.drawable.ic_home, R.color.grey);
        AHBottomNavigationItem item2 = new AHBottomNavigationItem(R.string.search, R.drawable.ic_search, R.color.grey);
        AHBottomNavigationItem item3 = new AHBottomNavigationItem("Cart", R.drawable.ic_cart, R.color.grey);
        AHBottomNavigationItem item4 = new AHBottomNavigationItem("Profile", R.drawable.ic_user, R.color.grey);
        // Add items
        bottomNavigation.addItem(item1);
        bottomNavigation.addItem(item2);
        bottomNavigation.addItem(item3);
        bottomNavigation.addItem(item4);

        // Set background color
        bottomNavigation.setDefaultBackgroundColor(Color.parseColor("#FEFEFE"));

        // Disable the translation inside the CoordinatorLayout
        bottomNavigation.setBehaviorTranslationEnabled(true);
        bottomNavigation.setTranslucentNavigationEnabled(true);

        bottomNavigation.setTitleState(AHBottomNavigation.TitleState.ALWAYS_HIDE);

// Change colors
        bottomNavigation.setAccentColor(Color.parseColor("#FF5722"));
        bottomNavigation.setInactiveColor(Color.parseColor("#747474"));


        // Set current item programmatically
        if(isChangePassword){
            fragment = new ProfileFragment();
            transaction.add(R.id.main_container, fragment).commit();
            bottomNavigation.setCurrentItem(3);
        }
        else
        {
            fragment = new HomeFragment();
            transaction.add(R.id.main_container, fragment).commit();
            bottomNavigation.setCurrentItem(0);
        }
        // Set listeners
        bottomNavigation.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
            @Override
            public boolean onTabSelected(int position, boolean wasSelected) {
                // Do something cool here...
                switch (position) {
                    case 0:
                        Log.e("home ","0..");

                        fragment = new HomeFragment();
                        break;
                    case 1:
                        Log.e("home ","1..");

                        fragment = new SearchFragment();
                        break;
                    case 2:
                        Log.e("home ","2....");

                        fragment = new CartFragment();
                        break;
                    case 3:
                        Log.e("home ","3..");

                        fragment = new ProfileFragment();
                        break;
                }
                transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.main_container, fragment).commit();
                return true;
            }
        });
        if (profileModel != null)
            getDisputeMessage();
        if(getIntent().getStringExtra("transfer")!=null)
        {
            bottomNavigation.setCurrentItem(2);

            Log.e("ma...ke","working...");
            Fragment fragment = new CartFragment();

            Bundle bundle = new Bundle();
            bundle.putString("prmoname", getIntent().getStringExtra("name"));
            bundle.putString("promoper", getIntent().getStringExtra("discount"));

            fragment.setArguments(bundle);
            transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.main_container, fragment).commit();
             }
    }

    private void getDisputeMessage() {
        Call<List<DisputeMessage>> call = apiInterface.getDisputeList();
        call.enqueue(new Callback<List<DisputeMessage>>() {
            @Override
            public void onResponse(Call<List<DisputeMessage>> call, Response<List<DisputeMessage>> response) {
                if (response.isSuccessful()) {
                    Log.e("Dispute List : ", response.toString());
                    disputeMessageList = new ArrayList<>();
                    disputeMessageList.addAll(response.body());
                } else {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().toString());
                        Toast.makeText(context, jObjError.optString("message"), Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<DisputeMessage>> call, Throwable t) {
                Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show();

            }
        });


    }

    /**
     * Method to display the location on UI
     */

    private void getLocation() {
        try {
                mLastLocation = LocationServices.FusedLocationApi
                    .getLastLocation(mGoogleApiClient);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
        if (mLastLocation == null) {
            mLocationRequest = new LocationRequest();
            mLocationRequest = new LocationRequest();
            mLocationRequest.setInterval(1000);
            mLocationRequest.setFastestInterval(1000);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {

                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

            }
        } else {
            latitude = mLastLocation.getLatitude();
            longitude = mLastLocation.getLongitude();
            GlobalData.latitude = mLastLocation.getLatitude();
            GlobalData.longitude = mLastLocation.getLongitude();
            Log.e("latitude", "" + mLastLocation.getLatitude());
            Log.e("longitude", "" + mLastLocation.getLongitude());
            Log.e("GlobalData.latitude", "" + GlobalData.latitude);
            Log.e("GlobalData.longitude ", "" + GlobalData.longitude);
            getAddress();
        }

    }

    public void getAddress(){
         retrofit = new Retrofit.Builder()
                .baseUrl("https://maps.googleapis.com/maps/api/geocode/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
         apiInterface=retrofit.create(ApiInterface.class);
        Call<ResponseBody> call = apiInterface.getResponse(latitude+","+longitude,"AIzaSyA_D2rZeo_i_YJUGzQBMOOJAqbDGgG0MbA");
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.e("sUCESS","SUCESS"+response.body());
                if (response.body() != null){
                    //BroadCast Listner
                    Intent intent = new Intent("location");
                    // You can also include some extra data.
                    intent.putExtra("message", "This is my message!");
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

                    try {
                        String bodyString = new String(response.body().bytes());
                        Log.e("sUCESS","bodyString"+bodyString);
                        JSONObject jsonObj = null;
                        try {
                            jsonObj = new JSONObject(bodyString);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        JSONArray jsonArray = jsonObj.optJSONArray("results");
                        if (jsonArray.length() > 0){
                            if(GlobalData.addressHeader.equalsIgnoreCase("")){
                                GlobalData.addressHeader = jsonArray.optJSONObject(0).optString("formatted_address");
                                GlobalData.address = jsonArray.optJSONObject(0).optString("formatted_address");
                                Log.v("Formatted Address", ""+ GlobalData.addressHeader );
                            }

                        }else{
                            if(GlobalData.addressHeader.equalsIgnoreCase("")){
                                GlobalData.addressHeader=""+latitude+""+longitude;
                                GlobalData.address=""+latitude+""+longitude;
                            }

                        }
                    } catch (IOException e) {
                        e.printStackTrace();

                    }
                }else{
                    GlobalData.addressHeader=""+latitude+""+longitude;
                    GlobalData.address=""+latitude+""+longitude;
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("onFailure","onFailure"+call.request().url());

            }
        });

    }

    /**
     * Creating google api client object
     */

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();

        mGoogleApiClient.connect();

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());

        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult locationSettingsResult) {

                final Status status = locationSettingsResult.getStatus();

                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can initialize location requests here
                        getLocation();
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(HomeActivity.this, REQUEST_CHECK_SETTINGS);

                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        break;
                }
            }
        });


    }


    /**
     * Method to verify google play services on the device
     */

    private boolean checkPlayServices() {

        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();

        int resultCode = googleApiAvailability.isGooglePlayServicesAvailable(this);

        if (resultCode != ConnectionResult.SUCCESS) {
            if (googleApiAvailability.isUserResolvableError(resultCode)) {
                googleApiAvailability.getErrorDialog(this, resultCode,
                        PLAY_SERVICES_REQUEST).show();
            } else {
                Toast.makeText(getApplicationContext(),
                        "This device is not supported.", Toast.LENGTH_LONG)
                        .show();
                finish();
            }
            return false;
        }
        return true;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

//        final LocationSettingsStates states = LocationSettingsStates.fromIntent(data);
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        // All required changes were successfully made
                        getLocation();
                        break;
                    case Activity.RESULT_CANCELED:
                        // The user was asked to change settings, but chose not to
                        break;
                    default:
                        break;
                }
                break;
        }
    }

    public void getNotificationItemCount() {
        if (GlobalData.addCart != null && GlobalData.addCart.getProductList().size() != 0) {
            itemCount = GlobalData.addCart.getProductList().size();
            int itemQuantity = 0;
            for (int i = 0; i < itemCount; i++) {
                //Get Total item Quantity
                itemQuantity = itemQuantity + GlobalData.addCart.getProductList().get(i).getQuantity();
                //Get product price
            }
            notificationCount = itemQuantity;
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        connectionHelper.isConnectingToInternet();
        updateNotificationCount(context, notificationCount);

    }

    @Override
    public void onBackPressed() {
        if (back_pressed + TIME_DELAY > System.currentTimeMillis()) {
            super.onBackPressed();
        } else {
            Toast.makeText(getBaseContext(), "Press once again to exit!", Toast.LENGTH_SHORT).show();
        }
        back_pressed = System.currentTimeMillis();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    /**
     * Google api callback methods
     */
    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = "
                + result.getErrorCode());
    }

    @Override
    public void onConnected(Bundle arg0) {

        // Once connected with google api, get the location
        getLocation();
    }

    @Override
    public void onConnectionSuspended(int arg0) {
        mGoogleApiClient.connect();
    }
    // Permission check functions


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case ASK_MULTIPLE_PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {
                    boolean FINE_LOCATIONPermission = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean COARSE_LOCATIONPermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (FINE_LOCATIONPermission && COARSE_LOCATIONPermission) {
                        getLocation();

                    } else {
                        Snackbar.make(this.findViewById(android.R.id.content),
                                "Please Grant Permissions to start service",
                                Snackbar.LENGTH_INDEFINITE).setAction("ENABLE",
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        ActivityCompat.requestPermissions(HomeActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, ASK_MULTIPLE_PERMISSION_REQUEST_CODE);
                                    }
                                }).show();
                    }
                }
                break;
        }
    }

    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public static void updateNotificationCount(Context context, int itemCount) {
        if (itemCount == 0) {
            notification = null;
            if (bottomNavigation != null)
            bottomNavigation.setNotification(notification, 2);
        } else if (bottomNavigation != null) {
            bottomNavigation.setNotificationBackgroundColor(ContextCompat.getColor(context, R.color.theme));
            bottomNavigation.setNotification(String.valueOf(itemCount), 2);
        }

    }


    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        latitude = mLastLocation.getLatitude();
        longitude = mLastLocation.getLongitude();
        GlobalData.latitude = mLastLocation.getLatitude();
        GlobalData.longitude = mLastLocation.getLongitude();
        Log.e("latitude2", "" + mLastLocation.getLatitude());
        Log.e("longitude2", "" + mLastLocation.getLongitude());
        Log.e("GlobalData.latitude2", "" + GlobalData.latitude);
        Log.e("GlobalData.longitude2 ", "" + GlobalData.longitude);
        getAddress();
    }
}