package com.haatapp.app.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.graphics.drawable.AnimatedVectorDrawableCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.haatapp.app.R;
import com.haatapp.app.build.api.APIError;
import com.haatapp.app.build.api.ApiClient;
import com.haatapp.app.build.api.ApiInterface;
import com.haatapp.app.build.api.ErrorUtils;
import com.haatapp.app.helper.CustomDialog;
import com.haatapp.app.helper.GlobalData;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SaveDeliveryLocationActivity extends FragmentActivity implements OnMapReadyCallback,
        LocationListener, GoogleMap.OnCameraMoveListener, GoogleMap.OnCameraIdleListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    @BindView(R.id.address)
    EditText addressEdit;
    @BindView(R.id.flat_no)
    EditText flatNoEdit;
    @BindView(R.id.landmark)
    EditText landmark;
    @BindView(R.id.dummy_image_view)
    ImageView dummyImageView;
    @BindView(R.id.animation_line_cart_add)
    ImageView animationLineCartAdd;
    @BindView(R.id.type_radiogroup)
    RadioGroup typeRadiogroup;
    @BindView(R.id.home_radio)
    RadioButton homeRadio;
    @BindView(R.id.work_radio)
    RadioButton workRadio;
    @BindView(R.id.other_radio)
    RadioButton otherRadio;
    @BindView(R.id.imgCurrentLoc)
    ImageView imgCurrentLoc;
    @BindView(R.id.save)
    Button save;
    @BindView(R.id.bottom_sheet)
    CardView bottomSheet;
    @BindView(R.id.current_loc_img)
    ImageView currentLocImg;
    @BindView(R.id.other_address_header_et)
    EditText otherAddressHeaderEt;
    @BindView(R.id.cancel_txt)
    TextView cancelTxt;
    @BindView(R.id.other_address_title_layout)
    RelativeLayout otherAddressTitleLayout;
    @BindView(R.id.skip_txt)
    TextView skipTxt;

    private String TAG = "SaveDelivery";
    private String addressHeader = "";
    private BottomSheetBehavior behavior;
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private int value = 0;
    private Double crtLat;
    private Double crtLng;
    private Double srcLat;
    private Double srcLng;
    AnimatedVectorDrawableCompat avdProgress;
    FusedLocationProviderClient mFusedLocationClient;

    @BindView(R.id.backArrow)
    ImageView backArrow;
    @BindView(R.id.coordinatorLayout)
    CoordinatorLayout coordinatorLayout;
    com.haatapp.app.models.Address address = null;
    ApiInterface apiInterface = ApiClient.getRetrofit().create(ApiInterface.class);
    Animation slide_down, slide_up;
    boolean isAddressSave = false;
    boolean isSkipVisible = false;
    Context context;

    CustomDialog customDialog;
    Retrofit retrofit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_delivery_location);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ButterKnife.bind(this);
        context = SaveDeliveryLocationActivity.this;
        address = new com.haatapp.app.models.Address();
        customDialog = new CustomDialog(context);
        address.setType("other");
        //Intialize Animation line
        initializeAvd();
        //Load animation
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        slide_down = AnimationUtils.loadAnimation(context,
                R.anim.slide_down);
        slide_up = AnimationUtils.loadAnimation(context,
                R.anim.slide_up);

        isAddressSave = getIntent().getBooleanExtra("get_address", false);
        isSkipVisible = getIntent().getBooleanExtra("skip_visible", false);
        if (isSkipVisible)
            skipTxt.setVisibility(View.VISIBLE);
        else
            skipTxt.setVisibility(View.GONE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mFusedLocationClient.getLastLocation()
                        .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                // Got last known location. In some rare situations this can be null.
                                if (location != null) {
                                    // Logic to handle location object
                                    getAddress(location.getLatitude(), location.getLongitude());
//                                    getAddress(context, location.getLatitude(), location.getLongitude());
                                }
                            }
                        });
            } else {
                //Request Location Permission
            }
        } else {
            buildGoogleApiClient();
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                // Logic to handle location object
                                getAddress(location.getLatitude(), location.getLongitude());
//                                getAddress(context, location.getLatitude(), location.getLongitude());
                            }
                        }
                    });
        }
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        View bottomSheet = findViewById(R.id.bottom_sheet);
        behavior = BottomSheetBehavior.from(bottomSheet);

        //set state is expanded
        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        dummyImageView.setVisibility(View.VISIBLE);

        behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                // React to state change
                // to collapse to show
                if (BottomSheetBehavior.STATE_COLLAPSED == newState) {
                    dummyImageView.startAnimation(slide_down);
                    dummyImageView.setVisibility(View.GONE);

                } else if (BottomSheetBehavior.STATE_EXPANDED == newState) {
                    dummyImageView.setVisibility(View.VISIBLE);
                    dummyImageView.startAnimation(slide_up);

                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                // React to dragging events
                Log.e("Slide :", "" + slideOffset);
                if (slideOffset < 0.9) {
                    dummyImageView.setVisibility(View.GONE);
                    dummyImageView.startAnimation(slide_down);
                }

            }

        });

        otherRadio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentLocImg.setBackgroundResource(R.drawable.ic_other_marker);
                otherAddressTitleLayout.setVisibility(View.VISIBLE);
                Animation animation = AnimationUtils.loadAnimation(context, R.anim.slide_in_right);
                animation.setDuration(500);
                Animation animation2 = AnimationUtils.loadAnimation(context, R.anim.slide_out_left);
                animation2.setDuration(500);
                typeRadiogroup.startAnimation(animation2);
                otherAddressTitleLayout.startAnimation(animation);
                typeRadiogroup.setVisibility(View.GONE);
            }
        });

        typeRadiogroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton radioButton = (RadioButton) radioGroup.findViewById(i);
                if (radioButton.getText().toString().toLowerCase().equals("home"))
                    currentLocImg.setBackgroundResource(R.drawable.ic_hoem_marker);
                else if (radioButton.getText().toString().toLowerCase().equals("work"))
                    currentLocImg.setBackgroundResource(R.drawable.ic_work_marker);
                else if (radioButton.getText().toString().equalsIgnoreCase(getResources().getString(R.string.other))) {
                    currentLocImg.setBackgroundResource(R.drawable.ic_other_marker);
                    otherAddressTitleLayout.setVisibility(View.VISIBLE);
                    Animation animation = AnimationUtils.loadAnimation(context, R.anim.slide_in_right);
                    animation.setDuration(500);
                    Animation animation2 = AnimationUtils.loadAnimation(context, R.anim.slide_out_left);
                    animation2.setDuration(500);
                    typeRadiogroup.startAnimation(animation2);
                    otherAddressTitleLayout.startAnimation(animation);
                    typeRadiogroup.setVisibility(View.GONE);
                }

                System.out.println("typeRadiogroup " + radioButton.getText().toString().toLowerCase());
                address.setType(radioButton.getText().toString().toLowerCase());

            }
        });

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String id = extras.getString("place_id");
            String isEdit = extras.getString("edit");
            if (id != null) {
                Places.GeoDataApi.getPlaceById(mGoogleApiClient, id).setResultCallback(new ResultCallback<PlaceBuffer>() {
                    @Override
                    public void onResult(@NonNull PlaceBuffer places) {
                        if (places.getStatus().isSuccess() && places.getCount() > 0) {
                            Place place = places.get(0);
                            addressEdit.setText(place.getAddress());
                            LatLng latLng = place.getLatLng();
                            value = 1;
                            CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(16).build();
                            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                        } else {
                            System.out.println("Place not found");
                        }
                        places.release();
                    }
                });
            }

            if (isEdit != null && isEdit.equals("yes")) {
                if (GlobalData.selectedAddress != null) {
                    address = GlobalData.selectedAddress;
                    addressEdit.setText(address.getMapAddress());
                    flatNoEdit.setText(address.getBuilding());
                    landmark.setText(address.getLandmark());
                    switch (address.getType()) {
                        case "home":
                            homeRadio.setChecked(true);
                            break;
                        case "work":
                            workRadio.setChecked(true);
                            break;
                        default:
                            otherAddressHeaderEt.setText(address.getType());
                            otherRadio.setChecked(true);
                            break;
                    }
                }
            }

        }

    }

    Runnable action = new Runnable() {
        @Override
        public void run() {
            avdProgress.stop();
            if (animationLineCartAdd != null)
                animationLineCartAdd.setVisibility(View.INVISIBLE);
        }
    };

    private void initializeAvd() {
        avdProgress = AnimatedVectorDrawableCompat.create(getApplicationContext(), R.drawable.avd_line);
        animationLineCartAdd.setBackground(avdProgress);
        repeatAnimation();
    }

    private void repeatAnimation() {
        animationLineCartAdd.setVisibility(View.VISIBLE);
        avdProgress.start();
        animationLineCartAdd.postDelayed(action, 1500); // Will repeat animation in every 1 second
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        try {
            boolean success = googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.style_json));
            if (!success) {
                Log.i("Map:Style", "Style parsing failed.");
            } else {
                Log.i("Map:Style", "Style Applied.");
            }
        } catch (Resources.NotFoundException e) {
            Log.i("Map:Style", "Can't find style. Error: ");
        }

        mMap = googleMap;

        if (mMap != null) {
            mMap.getUiSettings().setCompassEnabled(false);
            mMap.setBuildingsEnabled(true);
            mMap.setOnCameraMoveListener(this);
            mMap.setOnCameraIdleListener(this);
            mMap.getUiSettings().setRotateGesturesEnabled(false);
            mMap.getUiSettings().setTiltGesturesEnabled(false);
        }


    }


    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        System.out.println("onLocationChanged ");
        if (value == 0) {
            value = 1;
            if (address.getId() == null) {
                LatLng loc = new LatLng(location.getLatitude(), location.getLongitude());
                CameraPosition cameraPosition = new CameraPosition.Builder().target(loc).zoom(16).build();
                mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            } else {
                LatLng loc = new LatLng(address.getLatitude(), address.getLongitude());
                CameraPosition cameraPosition = new CameraPosition.Builder().target(loc).zoom(16).build();
                mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
//            getAddress( location.getLatitude(), location.getLongitude());
            getAddress(location.getLatitude(), location.getLongitude());
        }
        crtLat = location.getLatitude();
        crtLng = location.getLongitude();
    }

    private void getAddress(double latitude, double longitude) {
        System.out.println("GetAddress " + latitude + " | " + longitude);
        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && addresses.size() > 0) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder();
                if (returnedAddress.getMaxAddressLineIndex() > 0) {
                    for (int j = 0; j < returnedAddress.getMaxAddressLineIndex(); j++) {
                        strReturnedAddress.append(returnedAddress.getAddressLine(j)).append("");
                    }
                } else {
                    strReturnedAddress.append(returnedAddress.getAddressLine(0)).append("");
                }
                addressEdit.setText(strReturnedAddress.toString());
                Address obj = addresses.get(0);
                address.setCity(obj.getLocality());
                address.setState(obj.getAdminArea());
                address.setCountry(obj.getCountryName());
                address.setLatitude(obj.getLatitude());
                address.setLongitude(obj.getLongitude());
                address.setPincode(obj.getPostalCode());
                addressHeader = obj.getFeatureName();
                //SharedHelper.putKey(context, "pickup_address", strReturnedAddress.toString());
            } else {
                getAddress(context, latitude, longitude);
            }
        } catch (Exception e) {
            e.printStackTrace();
            getAddress(context, latitude, longitude);
        }
    }

    public void getAddress(final Context context, final double latitude, final double longitude) {
        retrofit = new Retrofit.Builder()
                .baseUrl("https://maps.googleapis.com/maps/api/geocode/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiInterface = retrofit.create(ApiInterface.class);
        Call<ResponseBody> call = apiInterface.getResponse(latitude + "," + longitude,"AIzaSyA_D2rZeo_i_YJUGzQBMOOJAqbDGgG0MbA");
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                Log.e("sUCESS", "SUCESS" + response.body());
                if (response.body() != null) {
                    try {
                        String bodyString = new String(response.body().bytes());
                        Log.e("sUCESS", "bodyString" + bodyString);
                        JSONObject jsonObj = new JSONObject(bodyString);
                        JSONArray jsonArray = jsonObj.optJSONArray("results");
                        if (jsonArray.length() > 0) {
                            JSONArray addressArray = jsonArray.optJSONObject(0).optJSONArray("address_components");
                            addressArray.optJSONObject(0).optString("long_name");
                            address.setCity(addressArray.optJSONObject(2).optString("long_name"));
                            address.setState(addressArray.optJSONObject(3).optString("long_name"));
                            if (addressArray.optJSONObject(4).optString("long_name") != null)
                                address.setCountry(addressArray.optJSONObject(4).optString("long_name"));
                            address.setLatitude(latitude);
                            address.setLongitude(longitude);
                            address.setPincode(addressArray.optJSONObject(5).optString("long_name"));
                            addressHeader = addressArray.optJSONObject(0).optString("long_name");
                            String address = jsonArray.optJSONObject(0).optString("formatted_address");
                            addressEdit.setText(address);
                            addressHeader = address;
                            Log.v("Formatted Address", "" + GlobalData.addressHeader);
                        } else {
                            addressHeader = "" + latitude + "" + longitude;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    addressHeader = "" + latitude + "" + longitude;
                }
                //BroadCast Listner
                Intent intent = new Intent("location");
                // You can also include some extra data.
                intent.putExtra("message", "This is my message!");
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Log.e("onFailure", "onFailure" + call.request().url());
                addressHeader = "" + latitude + "" + longitude;

            }
        });


    }

    @Override
    public void onCameraIdle() {
        try {
            CameraPosition cameraPosition = mMap.getCameraPosition();
            srcLat = cameraPosition.target.latitude;
            srcLng = cameraPosition.target.longitude;

            //Intialize animation line
            initializeAvd();
            getAddress(srcLat, srcLng);
//            getAddress(context, srcLat, srcLng);
            skipTxt.setAlpha(1);
            skipTxt.setClickable(true);
            skipTxt.setEnabled(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCameraMove() {
        behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        dummyImageView.setVisibility(View.GONE);
        addressEdit.setText(this.getResources().getString(R.string.getting_address));
        animationLineCartAdd.setVisibility(View.VISIBLE);
        skipTxt.setAlpha((float) 0.5);
        skipTxt.setClickable(false);
        skipTxt.setEnabled(false);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    private void saveAddress() {
        if (address != null && address.getMapAddress() != null && validate()) {
            customDialog.show();
            apiInterface = ApiClient.getRetrofit().create(ApiInterface.class);
            Call<com.haatapp.app.models.Address> call = apiInterface.saveAddress(address);
            call.enqueue(new Callback<com.haatapp.app.models.Address>() {
                @Override
                public void onResponse(@NonNull Call<com.haatapp.app.models.Address> call, @NonNull Response<com.haatapp.app.models.Address> response) {
                    customDialog.dismiss();
                    if (response.isSuccessful()) {
                        if (isAddressSave) {
                            //select the address data and set to address in Cart fargment page
                            Intent returnIntent = new Intent();
                            GlobalData.selectedAddress = response.body();
                            GlobalData.addressList.getAddresses().add(response.body());
                            setResult(Activity.RESULT_OK, returnIntent);
                            finish();
                        } else {
                            GlobalData.selectedAddress = response.body();
                            GlobalData.addressList.getAddresses().add(response.body());
                            finish();
                        }
                    } else {
                        APIError error = ErrorUtils.parseError(response);
                        if(error.getType()!= null && error.getType().size()> 0){
                            Toast.makeText(SaveDeliveryLocationActivity.this, error.getType().get(0), Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<com.haatapp.app.models.Address> call, @NonNull Throwable t) {
                    Log.e(TAG, t.toString());
                    customDialog.dismiss();
                    Toast.makeText(SaveDeliveryLocationActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private void updateAddress() {
        if (address.getType().equalsIgnoreCase("other")) {
            address.setType(otherAddressHeaderEt.getText().toString());
        }
        if (address != null && address.getId() != null && validate()) {
            customDialog.show();
            apiInterface = ApiClient.getRetrofit().create(ApiInterface.class);
            Call<com.haatapp.app.models.Address> call = apiInterface.updateAddress(address.getId(), address);
            call.enqueue(new Callback<com.haatapp.app.models.Address>() {
                @Override
                public void onResponse(@NonNull Call<com.haatapp.app.models.Address> call, @NonNull Response<com.haatapp.app.models.Address> response) {
                    customDialog.dismiss();
                    if (response.isSuccessful()) {
                        GlobalData.selectedAddress = response.body();
                        finish();
                    } else {
                        APIError error = ErrorUtils.parseError(response);
                        Toast.makeText(SaveDeliveryLocationActivity.this, error.getType().get(0), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<com.haatapp.app.models.Address> call, @NonNull Throwable t) {
                    Log.e(TAG, t.toString());
                    customDialog.dismiss();
                    Toast.makeText(SaveDeliveryLocationActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private boolean validate() {
        if (address.getMapAddress().isEmpty() && address.getMapAddress().equals(getResources().getString(R.string.getting_address))) {
            Toast.makeText(this, "Please enter address", Toast.LENGTH_SHORT).show();
            return false;
        } else if (address.getBuilding().isEmpty()) {
            Toast.makeText(this, "Please enter Flat No", Toast.LENGTH_SHORT).show();
            return false;
        } else if (address.getLandmark().isEmpty()) {
            Toast.makeText(this, "Please enter landmark", Toast.LENGTH_SHORT).show();
            return false;
        } else if (address.getLatitude() == null || address.getLongitude() == null) {
            Toast.makeText(this, "Lat & long cannot be left blank", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (behavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
        finish();
        overridePendingTransition(R.anim.anim_nothing, R.anim.slide_out_right);
    }

    @OnClick({R.id.backArrow, R.id.save, R.id.imgCurrentLoc, R.id.cancel_txt, R.id.skip_txt})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.backArrow:
                onBackPressed();
                break;
            case R.id.imgCurrentLoc:
                if (crtLat != null && crtLng != null) {
                    LatLng loc = new LatLng(crtLat, crtLng);
                    CameraPosition cameraPosition = new CameraPosition.Builder().target(loc).zoom(16).build();
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                }
                break;
            case R.id.cancel_txt:
                otherAddressHeaderEt.setVisibility(View.VISIBLE);
                Animation animation2 = AnimationUtils.loadAnimation(context, R.anim.slide_out_right);
                animation2.setDuration(500);
                otherAddressHeaderEt.startAnimation(animation2);
                Animation animation = AnimationUtils.loadAnimation(context, R.anim.slide_in_left);
                animation.setDuration(500);
                typeRadiogroup.startAnimation(animation);
                typeRadiogroup.setVisibility(View.VISIBLE);
                otherRadio.setChecked(true);
                break;
            case R.id.save:
                address.setMapAddress(addressEdit.getText().toString());
                address.setBuilding(flatNoEdit.getText().toString());
                address.setLandmark(landmark.getText().toString());
                if (address.getType().equalsIgnoreCase("other") || address.getType().equalsIgnoreCase("")) {
                    address.setType(otherAddressHeaderEt.getText().toString());
                }
                if (address.getBuilding().equalsIgnoreCase("")) {
                    Toast.makeText(context, "Please enter House/ flat no ", Toast.LENGTH_SHORT).show();
                } else if (address.getLandmark().equalsIgnoreCase("")) {
                    Toast.makeText(context, "Please enter landmark ", Toast.LENGTH_SHORT).show();
                } else {
                    if (address.getType().equalsIgnoreCase(""))
                        address.setType("other");

                    if (address.getId() != null)
                        updateAddress();
                    else
                        saveAddress();
                }
                break;
            case R.id.skip_txt:
                address.setMapAddress(addressEdit.getText().toString());
                address.setType(addressHeader);
                GlobalData.selectedAddress = address;
                startActivity(new Intent(context, CategoryActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                finish();
                break;

        }
    }


}
