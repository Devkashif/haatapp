package com.haatapp.app.fragments;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.graphics.drawable.AnimatedVectorDrawableCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ethanhua.skeleton.Skeleton;
import com.ethanhua.skeleton.SkeletonScreen;
import com.haatapp.app.HomeActivity;
import com.haatapp.app.R;
import com.haatapp.app.activities.FilterActivity;
import com.haatapp.app.activities.SetDeliveryLocationActivity;
import com.haatapp.app.adapter.BannerAdapter;
import com.haatapp.app.adapter.DiscoverAdapter;
import com.haatapp.app.adapter.OfferRestaurantAdapter;
import com.haatapp.app.adapter.RestaurantsAdapter;
import com.haatapp.app.build.api.ApiClient;
import com.haatapp.app.build.api.ApiInterface;
import com.haatapp.app.helper.ConnectionHelper;
import com.haatapp.app.helper.GlobalData;
import com.haatapp.app.models.Address;
import com.haatapp.app.models.Banner;
import com.haatapp.app.models.Cuisine;
import com.haatapp.app.models.Discover;
import com.haatapp.app.models.Restaurant;
import com.haatapp.app.models.RestaurantsData;
import com.haatapp.app.models.Shop;
import com.haatapp.app.utils.Utils;

import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;
import static com.haatapp.app.helper.GlobalData.addressList;
import static com.haatapp.app.helper.GlobalData.cuisineIdArrayList;
import static com.haatapp.app.helper.GlobalData.cuisineList;
import static com.haatapp.app.helper.GlobalData.isOfferApplied;
import static com.haatapp.app.helper.GlobalData.isPureVegApplied;
import static com.haatapp.app.helper.GlobalData.latitude;
import static com.haatapp.app.helper.GlobalData.longitude;
import static com.haatapp.app.helper.GlobalData.selectedAddress;


/**
 * Created by santhosh@appoets.com on 22-08-2017.
 */

public class HomeFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    @BindView(R.id.animation_line_image)
    ImageView animationLineImage;
    Context context;
    @BindView(R.id.catagoery_spinner)
    Spinner catagoerySpinner;
    @BindView(R.id.title)
    LinearLayout title;
    @BindView(R.id.restaurant_count_txt)
    TextView restaurantCountTxt;
    @BindView(R.id.offer_title_header)
    TextView offerTitleHeader;
    @BindView(R.id.error_layout)
    LinearLayout errorLayout;
    @BindView(R.id.impressive_dishes_layout)
    LinearLayout impressiveDishesLayout;
    private SkeletonScreen skeletonScreen, skeletonScreen2, skeletonText1, skeletonText2, skeletonSpinner;
    private TextView addressLabel;
    private TextView addressTxt;
    private LinearLayout locationAddressLayout;
    private RelativeLayout errorLoadingLayout;

    private Button filterBtn;

    @BindView(R.id.restaurants_offer_rv)
    RecyclerView restaurantsOfferRv;
    @BindView(R.id.impressive_dishes_rv)
    RecyclerView bannerRv;
    @BindView(R.id.restaurants_rv)
    RecyclerView restaurantsRv;
    @BindView(R.id.discover_rv)
    RecyclerView discoverRv;
    int ADDRESS_SELECTION = 1;
    int FILTER_APPLIED_CHECK = 2;
    ImageView filterSelectionImage;

    private ViewGroup toolbar;
    private View toolbarLayout;
    AnimatedVectorDrawableCompat avdProgress;
    public static ArrayList<Integer> cuisineSelectedList = null;

    String[] catagoery = {"Relevance", "Cost for Two", "Delivery Time", "Rating"};

    ApiInterface apiInterface = ApiClient.getRetrofit().create(ApiInterface.class);
    List<Shop> restaurantList;
    RestaurantsAdapter adapterRestaurant;
    public static boolean isFilterApplied = false;
    BannerAdapter bannerAdapter;
    List<Banner> bannerList;
    ConnectionHelper connectionHelper;
    Activity activity;
    SharedPreferences.Editor editor;
    String catid;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = getContext();
        activity = getActivity();

        SharedPreferences pref = activity.getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        editor = pref.edit();
        catid=pref.getString("catid", null);


    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            String message = intent.getStringExtra("message");

            Log.d("receiver", "Got message: " + message);

                errorLoadingLayout.setVisibility(View.GONE);
                locationAddressLayout.setVisibility(View.VISIBLE);
                if (selectedAddress != null && GlobalData.profileModel != null) {
                    GlobalData.addressHeader= selectedAddress.getType();
                    addressLabel.setText(selectedAddress.getType());

                    Log.e("kali9","chla he");
                    addressTxt.setText(selectedAddress.getMapAddress());
                    latitude = selectedAddress.getLatitude();
                    longitude = selectedAddress.getLongitude();
                    GlobalData.addressHeader = selectedAddress.getMapAddress();
                } else if (addressList != null && addressList.getAddresses().size() != 0 && GlobalData.profileModel != null) {
                    for (int i = 0; i < addressList.getAddresses().size(); i++) {
                        Address address1 = addressList.getAddresses().get(i);

                        Log.e("kali2","chla he");
                        if (getDoubleThreeDigits(latitude) == getDoubleThreeDigits(address1.getLatitude()) && getDoubleThreeDigits(longitude) == getDoubleThreeDigits(address1.getLongitude())) {
                            selectedAddress = address1;
                            addressLabel.setText(GlobalData.addressHeader);
                            addressTxt.setText(GlobalData.address);
                            addressLabel.setText(selectedAddress.getType());
                            addressTxt.setText(selectedAddress.getMapAddress());
                            latitude = selectedAddress.getLatitude();
                            longitude = selectedAddress.getLongitude();
                            break;
                        } else {
                            Log.e("kali1","chla he");
                            addressLabel.setText(GlobalData.addressHeader);
                            addressTxt.setText(GlobalData.address);
                        }
                    }
                } else {
try {
    Geocoder geocoder;
    List<Address> addresses;
    geocoder = new Geocoder(getActivity(), Locale.getDefault());
Log.e("values",GlobalData.addressHeader);
    Log.e("values1",GlobalData.address);

   List<android.location.Address>  addressess = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

   String address = addressess.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
    String city = addressess.get(0).getLocality();
    String state = addressess.get(0).getAdminArea();
    String country = addressess.get(0).getCountryName();
    String postalCode = addressess.get(0).getPostalCode();
    String knownName = addressess.get(0).getFeatureName();
    Log.e("kali3", "chla he");
    Log.e("vales",address+"--"+city+"=="+state);

    addressLabel.setText(address);
    addressTxt.setText(address);
}
catch (Exception e)
{
    e.printStackTrace();
}
                }
                findRestaurant();
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, view);
        return view;

    }


    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);
        System.out.println("HomeFragment");
        Log.d("Home", "onReceive: *****************************");

        connectionHelper = new ConnectionHelper(context);
        toolbar = (ViewGroup) getActivity().findViewById(R.id.toolbar);
        toolbar.setVisibility(View.VISIBLE);
        toolbarLayout = LayoutInflater.from(context).inflate(R.layout.toolbar_home, toolbar, false);
        addressLabel = (TextView) toolbarLayout.findViewById(R.id.address_label);
        addressTxt = (TextView) toolbarLayout.findViewById(R.id.address);
        locationAddressLayout = (LinearLayout) toolbarLayout.findViewById(R.id.location_ll);
        errorLoadingLayout = (RelativeLayout) toolbarLayout.findViewById(R.id.error_loading_layout);
        locationAddressLayout.setVisibility(View.INVISIBLE);
        errorLoadingLayout.setVisibility(View.VISIBLE);
        bannerList = new ArrayList<>();
        bannerAdapter = new BannerAdapter(bannerList, context, getActivity());
        bannerRv.setHasFixedSize(true);
        bannerRv.setItemViewCacheSize(20);
        bannerRv.setDrawingCacheEnabled(true);
        bannerRv.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        bannerRv.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        bannerRv.setItemAnimator(new DefaultItemAnimator());
        skeletonScreen2 = Skeleton.bind(bannerRv)
                .adapter(bannerAdapter)
                .load(R.layout.skeleton_impressive_list_item)
                .count(3)
                .show();
        skeletonText1 = Skeleton.bind(offerTitleHeader)
                .load(R.layout.skeleton_label)
                .show();
        skeletonText2 = Skeleton.bind(restaurantCountTxt)
                .load(R.layout.skeleton_label)
                .show();
        skeletonSpinner = Skeleton.bind(catagoerySpinner)
                .load(R.layout.skeleton_label)
                .show();
        HomeActivity.updateNotificationCount(context, GlobalData.notificationCount);

        //Spinner
        //Creating the ArrayAdapter instance having the country shopList
        ArrayAdapter aa = new ArrayAdapter(context, R.layout.spinner_layout, catagoery);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        catagoerySpinner.setAdapter(aa);
        catagoerySpinner.setOnItemSelectedListener(this);

        //Restaurant Adapter
        restaurantsRv.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        restaurantsRv.setItemAnimator(new DefaultItemAnimator());
        restaurantsRv.setHasFixedSize(true);
        restaurantList = new ArrayList<>();
        adapterRestaurant = new RestaurantsAdapter(restaurantList, context, getActivity());
        skeletonScreen = Skeleton.bind(restaurantsRv)
                .adapter(adapterRestaurant)
                .load(R.layout.skeleton_restaurant_list_item)
                .count(2)
                .show();

        final ArrayList<Restaurant> offerRestaurantList = new ArrayList<>();
        offerRestaurantList.add(new Restaurant("Madras Coffee House", "Cafe, South Indian", "", "3.8", "51 Mins", "$20", ""));
        offerRestaurantList.add(new Restaurant("Behrouz Biryani", "Biriyani", "", "3.7", "52 Mins", "$50", ""));
        offerRestaurantList.add(new Restaurant("SubWay", "American fast food", "Flat 20% offer on all orders", "4.3", "30 Mins", "$5", "Close soon"));
        offerRestaurantList.add(new Restaurant("Dominoz Pizza", "Pizza shop", "", "4.5", "25 Mins", "$5", ""));
        offerRestaurantList.add(new Restaurant("Pizza hut", "Cafe, Bakery", "", "4.1", "45 Mins", "$5", "Close soon"));
        offerRestaurantList.add(new Restaurant("McDonlad's", "Pizza Food, burger", "Flat 20% offer on all orders", "4.6", "20 Mins", "$5", ""));
        offerRestaurantList.add(new Restaurant("Chai Kings", "Cafe, Bakery", "", "3.3", "36 Mins", "$5", ""));
        offerRestaurantList.add(new Restaurant("sea sell", "Fish, Chicken, mutton", "Flat 30% offer on all orders", "4.3", "20 Mins", "$5", "Close soon"));
        //Offer Restaurant Adapter
        restaurantsOfferRv.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        restaurantsOfferRv.setItemAnimator(new DefaultItemAnimator());
        restaurantsOfferRv.setHasFixedSize(true);
        OfferRestaurantAdapter offerAdapter = new OfferRestaurantAdapter(offerRestaurantList, context);
        restaurantsOfferRv.setAdapter(offerAdapter);


        // Discover
        final List<Discover> discoverList = new ArrayList<>();
        discoverList.add(new Discover("Trending now ", "22 options", "1"));
        discoverList.add(new Discover("Offers near you", "51 options", "1"));
        discoverList.add(new Discover("Whats special", "7 options", "1"));
        discoverList.add(new Discover("Pocket Friendly", "44 options", "1"));

        DiscoverAdapter adapterDiscover = new DiscoverAdapter(discoverList, context);
        discoverRv.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        discoverRv.setItemAnimator(new DefaultItemAnimator());
        discoverRv.setAdapter(adapterDiscover);
        adapterDiscover.setOnItemClickListener(new DiscoverAdapter.ClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                Discover obj = discoverList.get(position);
            }
        });

        locationAddressLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (GlobalData.profileModel != null) {
                    startActivityForResult(new Intent(getActivity(), SetDeliveryLocationActivity.class).putExtra("get_address", true).putExtra("home_page", true), ADDRESS_SELECTION);
                    getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.anim_nothing);
                } else {
                    Toast.makeText(context, "Please login", Toast.LENGTH_SHORT).show();
                }
            }
        });
        filterBtn = (Button) toolbarLayout.findViewById(R.id.filter);
        filterSelectionImage = (ImageView) toolbarLayout.findViewById(R.id.filter_selection_image);
        filterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(context, FilterActivity.class), FILTER_APPLIED_CHECK);
                getActivity().overridePendingTransition(R.anim.slide_up, R.anim.anim_nothing);
            }
        });
        toolbar.addView(toolbarLayout);
        //intialize image line
        initializeAvd();

//Get cuisine values
        if (connectionHelper.isConnectingToInternet()) {
            getCuisines();
        } else {
            Utils.displayMessage(activity, context, getString(R.string.oops_connect_your_internet));
        }


    }

    private void findRestaurant() {
        HashMap<String, String> map = new HashMap<>();
        map.put("latitude", String.valueOf(latitude));
        map.put("longitude", String.valueOf(longitude));
        //get User Profile Data
        if (GlobalData.profileModel != null) {
            map.put("user_id", String.valueOf(GlobalData.profileModel.getId()));
        }
        if (isFilterApplied) {
            filterSelectionImage.setVisibility(View.VISIBLE);
            if (isOfferApplied)
                map.put("offer", "1");
            if (isPureVegApplied)
                map.put("pure_veg", "1");
            if (cuisineIdArrayList != null && cuisineIdArrayList.size() != 0) {
                for (int i = 0; i < cuisineIdArrayList.size(); i++) {
                    map.put("cuisine[" + "" + i + "]", cuisineIdArrayList.get(i).toString());
                }
            }

        } else {
            filterSelectionImage.setVisibility(View.GONE);
        }

        if (connectionHelper.isConnectingToInternet()) {
            getRestaurant();
        } else {
            Utils.displayMessage(activity, context, getString(R.string.oops_connect_your_internet));
        }

    }
    private void getRestaurant() {
        ApiInterface apiInterface1 = ApiClient.getRetrofit().create(ApiInterface.class);
        Call<RestaurantsData> getData = apiInterface1.getUsers("api/user/shops?cat="+catid);
        getData.enqueue(new Callback<RestaurantsData>() {
            @Override
            public void onResponse(Call<RestaurantsData> call, Response<RestaurantsData> response) {
                skeletonScreen.hide();
                skeletonScreen2.hide();
                skeletonText1.hide();
                skeletonText2.hide();
                skeletonSpinner.hide();
                Log.d("response", "onResponse: "+response);
                if(response.isSuccessful()){
                    if (response.body().getShops().size() == 0) {
                        title.setVisibility(View.GONE);
                        errorLayout.setVisibility(View.VISIBLE);
                    } else {
                        title.setVisibility(View.VISIBLE);
                        errorLayout.setVisibility(View.GONE);
                    }
                    //Check Banner list
                    if (response.body().getBanners().size() == 0||isFilterApplied)
                        impressiveDishesLayout.setVisibility(View.GONE);
                    else
                        impressiveDishesLayout.setVisibility(View.VISIBLE);

                    GlobalData.shopList = response.body().getShops();
                    restaurantList.clear();
                    restaurantList.addAll(GlobalData.shopList);
                    bannerList.clear();
                    bannerList.addAll(response.body().getBanners());
                    restaurantCountTxt.setText("" + restaurantList.size() + "Stores");
                    adapterRestaurant.notifyDataSetChanged();
                    bannerAdapter.notifyDataSetChanged();

                }else {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Toast.makeText(context, jObjError.optString("message"), Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<RestaurantsData> call,Throwable t) {
                Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show();

            }
        });
    }


    private void getRestaurant1() {

        Call<RestaurantsData> call = apiInterface.getshops("");
        call.enqueue(new Callback<RestaurantsData>() {
            @Override
            public void onResponse(Call<RestaurantsData> call,Response<RestaurantsData> response) {
                skeletonScreen.hide();
                skeletonScreen2.hide();
                skeletonText1.hide();
                skeletonText2.hide();
                skeletonSpinner.hide();

                if(response.isSuccessful()){
                    if (response.body().getShops().size() == 0) {
                        title.setVisibility(View.GONE);
                        errorLayout.setVisibility(View.VISIBLE);
                    } else {
                        title.setVisibility(View.VISIBLE);
                        errorLayout.setVisibility(View.GONE);
                    }
                    //Check Banner list
                    if (response.body().getBanners().size() == 0||isFilterApplied)
                        impressiveDishesLayout.setVisibility(View.GONE);
                    else
                        impressiveDishesLayout.setVisibility(View.VISIBLE);
                    GlobalData.shopList = response.body().getShops();
                    restaurantList.clear();
                    restaurantList.addAll(GlobalData.shopList);
                    bannerList.clear();
                    bannerList.addAll(response.body().getBanners());
                    restaurantCountTxt.setText("" + restaurantList.size() + " Restaurants");
                    adapterRestaurant.notifyDataSetChanged();
                    bannerAdapter.notifyDataSetChanged();

                }else {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Toast.makeText(context, jObjError.optString("message"), Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<RestaurantsData> call, @NonNull Throwable t) {
                Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show();

            }
        });


    }


    public void getCuisines() {
        Call<List<Cuisine>> call = apiInterface.getcuCuisineCall();
        call.enqueue(new Callback<List<Cuisine>>() {
            @Override
            public void onResponse(Call<List<Cuisine>> call, Response<List<Cuisine>> response) {
                if (response != null && !response.isSuccessful() && response.errorBody() != null) {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Toast.makeText(context, jObjError.optString("message"), Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else if (response.isSuccessful()) {
                    cuisineList = new ArrayList<>();
                    cuisineList.addAll(response.body());
                    Log.d("custion list", "onResponse: "+response.body());
                }
            }

            @Override
            public void onFailure(Call<List<Cuisine>> call, Throwable t) {

            }
        });


    }

    public double getDoubleThreeDigits(Double value) {
        return new BigDecimal(value.toString()).setScale(3, RoundingMode.HALF_UP).doubleValue();

    }

    Runnable action = new Runnable() {
        @Override
        public void run() {
            avdProgress.stop();
            if (animationLineImage != null)
                animationLineImage.setVisibility(View.INVISIBLE);
        }
    };

    private void initializeAvd() {
        avdProgress = AnimatedVectorDrawableCompat.create(context, R.drawable.avd_line);
        animationLineImage.setBackground(avdProgress);
        repeatAnimation();
    }

    private void repeatAnimation() {
        avdProgress.start();
        animationLineImage.
                postDelayed(action, 3000); // Will repeat animation in every 1 second
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        errorLayout.setVisibility(View.GONE);
        HomeActivity.updateNotificationCount(context, GlobalData.notificationCount);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mMessageReceiver,
                new IntentFilter("location"));
        if (!GlobalData.addressHeader.equalsIgnoreCase("")) {
            errorLoadingLayout.setVisibility(View.GONE);
            locationAddressLayout.setVisibility(View.VISIBLE);
            if (selectedAddress != null && GlobalData.profileModel != null) {
                GlobalData.addressHeader= selectedAddress.getType();
                addressLabel.setText(selectedAddress.getType());

                Log.e("kali4","chla he");
                Log.e("working...",selectedAddress.getMapAddress());
                addressTxt.setText(selectedAddress.getMapAddress());
                latitude = selectedAddress.getLatitude();
                longitude = selectedAddress.getLongitude();
                GlobalData.addressHeader = selectedAddress.getMapAddress();
            } else if (addressList != null && addressList.getAddresses().size() != 0 && GlobalData.profileModel != null) {
                for (int i = 0; i < addressList.getAddresses().size(); i++) {
                    Address address1 = addressList.getAddresses().get(i);
                    if (getDoubleThreeDigits(latitude) == getDoubleThreeDigits(address1.getLatitude()) && getDoubleThreeDigits(longitude) == getDoubleThreeDigits(address1.getLongitude())) {
                        selectedAddress = address1;

                        Log.e("kali5","chla he");
                        addressLabel.setText(GlobalData.addressHeader);
                        addressTxt.setText(GlobalData.address);
                        addressLabel.setText(selectedAddress.getType());
                        addressTxt.setText(selectedAddress.getMapAddress());
                        latitude = selectedAddress.getLatitude();
                        longitude = selectedAddress.getLongitude();
                        break;
                    } else {

                        Log.e("kali6","chla he");
                        addressLabel.setText(GlobalData.addressHeader);
                        addressTxt.setText(GlobalData.address);
                    }
                }
            } else {

                Log.e("kali7","chla he");
                addressLabel.setText(GlobalData.addressHeader);
                addressTxt.setText(GlobalData.address);
            }
            findRestaurant();

        }

        }


    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        activity = getActivity();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (toolbar != null) {
            toolbar.removeView(toolbarLayout);
        }
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mMessageReceiver);
        super.onDestroy();

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADDRESS_SELECTION && resultCode == Activity.RESULT_OK) {
            System.out.print("HomeFragment : Success");
            if (selectedAddress != null) {

                Log.e("kali8","chla he");
                addressLabel.setText(GlobalData.addressHeader);
                addressTxt.setText(GlobalData.address);
                addressLabel.setText(selectedAddress.getType());
                addressTxt.setText(selectedAddress.getMapAddress());
                latitude = selectedAddress.getLatitude();
                longitude = selectedAddress.getLongitude();
                skeletonScreen.show();
                skeletonScreen2.show();
                skeletonText1.show();
                skeletonText2.show();
                skeletonSpinner.show();
                findRestaurant();

            }
        } else if (requestCode == ADDRESS_SELECTION && resultCode == Activity.RESULT_CANCELED) {
            System.out.print("HomeFragment : Failure");

        }

        if (requestCode == FILTER_APPLIED_CHECK && resultCode == Activity.RESULT_OK) {
            System.out.print("HomeFragment : Filter Success");
            skeletonScreen.show();
            skeletonScreen2.show();
            skeletonText1.show();
            skeletonText2.show();
            skeletonSpinner.show();
            findRestaurant();

        } else if (requestCode == ADDRESS_SELECTION && resultCode == Activity.RESULT_CANCELED) {
            System.out.print("HomeFragment : Filter Failure");

        }

    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//        Toast.makeText(context, catagoery[position], Toast.LENGTH_LONG).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void showToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }


}