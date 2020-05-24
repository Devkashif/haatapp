package com.haatapp.app.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.haatapp.app.R;
import com.haatapp.app.adapter.AddOnsAdapter;
import com.haatapp.app.adapter.SliderPagerAdapter;
import com.haatapp.app.build.api.ApiClient;
import com.haatapp.app.build.api.ApiInterface;
import com.haatapp.app.helper.CustomDialog;
import com.haatapp.app.helper.GlobalData;
import com.haatapp.app.models.AddCart;
import com.haatapp.app.models.Addon;
import com.haatapp.app.models.ClearCart;
import com.haatapp.app.models.Image;
import com.haatapp.app.models.Product;
import com.haatapp.app.utils.Utils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static com.haatapp.app.adapter.AddOnsAdapter.list;
import static com.haatapp.app.helper.GlobalData.selectedShop;


public class ProductDetailActivity extends AppCompatActivity {

    @BindView(R.id.product_slider)
    ViewPager productSlider;
    @BindView(R.id.product_slider_dots)
    LinearLayout productSliderDots;

    SliderPagerAdapter sliderPagerAdapter;
    List<Image> slider_image_list;
    int page_position = 0;
    @BindView(R.id.add_ons_rv)
    RecyclerView addOnsRv;
    @BindView(R.id.product_name)
    TextView productName;
    @BindView(R.id.product_description)
    TextView productDescription;

    Product product;
    List<Addon> addonList;
    Context context;
    ApiInterface apiInterface = ApiClient.getRetrofit().create(ApiInterface.class);
    public static TextView addOnsTxt;
    int cartId = 0;
    int quantity = 0;

    CustomDialog customDialog;

    public static TextView itemText;
    public static TextView viewCart;
    public static RelativeLayout addItemLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // perform whatever you want on back arrow click
                onBackPressed();
            }
        });
        context = ProductDetailActivity.this;
        customDialog = new CustomDialog(context);

        //Intialize
        addOnsTxt = (TextView) findViewById(R.id.add_ons_txt);
        itemText = (TextView) findViewById(R.id.item_text);
        viewCart = (TextView) findViewById(R.id.view_cart);
        addItemLayout = (RelativeLayout) findViewById(R.id.view_cart_layout);
        product = GlobalData.isSelectedProduct;
        if (GlobalData.addCart != null) {
            if (GlobalData.addCart.getProductList().size() != 0) {
                for (int i = 0; i < GlobalData.addCart.getProductList().size(); i++) {
                    if (GlobalData.addCart.getProductList().get(i).getProductId().equals(product.getId())) {
                        cartId = GlobalData.addCart.getProductList().get(i).getId();
                        quantity = GlobalData.addCart.getProductList().get(i).getQuantity();
                    }
                }
            }
        }

        productName.setText(product.getName() + "\n" + product.getPrices().getCurrency() + product.getPrices().getPrice());
        itemText.setText("1 Item | " + product.getPrices().getCurrency() + product.getPrices().getPrice());
        productDescription.setText(product.getDescription());
        slider_image_list = new ArrayList<>();
        addonList = new ArrayList<>();
        addonList.addAll(product.getAddons());
        if (addonList.size() == 0)
            addOnsTxt.setVisibility(View.GONE);
        else
            addOnsTxt.setVisibility(View.VISIBLE);

        //Add ons Adapter
        addOnsRv.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
//        addOnsRv.setItemAnimator(new DefaultItemAnimator());
        addOnsRv.setHasFixedSize(false);
        addOnsRv.setNestedScrollingEnabled(false);

        AddOnsAdapter addOnsAdapter = new AddOnsAdapter(addonList, context);
        addOnsRv.setAdapter(addOnsAdapter);

        slider_image_list.addAll(product.getImages());
        sliderPagerAdapter = new SliderPagerAdapter(this, slider_image_list, true);
        productSlider.setAdapter(sliderPagerAdapter);
        addBottomDots(0);

        addItemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (GlobalData.profileModel == null) {
                    Toast.makeText(context, "Please login", Toast.LENGTH_SHORT).show();
                } else {
                    final HashMap<String, String> map = new HashMap<>();
                    map.put("product_id", product.getId().toString());
                    if (product.getCart() != null && product.getCart().size() == 1 && product.getAddons().isEmpty()) {
                        map.put("quantity", String.valueOf(product.getCart().get(0).getQuantity() + 1));
                        map.put("cart_id", String.valueOf(product.getCart().get(0).getId()));
                    } else if (product.getAddons().isEmpty() && cartId != 0) {
                        map.put("quantity", String.valueOf(quantity + 1));
                        map.put("cart_id", String.valueOf(cartId));
                    } else {
                        map.put("quantity", "1");
                        if (!list.isEmpty()) {
                            for (int i = 0; i < list.size(); i++) {
                                Addon addon = list.get(i);
                                if (addon.getAddon().getChecked()) {
                                    map.put("product_addons[" + "" + i + "]", addon.getId().toString());
                                    map.put("addons_qty[" + "" + i + "]", addon.getQuantity().toString());
                                }
                            }
                        }
                    }
                    Log.e("AddCart_add", map.toString());

                    if (!Utils.isShopChanged(product.getShopId())) {
                        addItem(map);
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle(context.getResources().getString(R.string.replace_cart_item))
                                .setMessage(context.getResources().getString(R.string.do_you_want_to_discart_the_selection_and_add_dishes_from_the_restaurant))
                                .setPositiveButton(context.getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // continue with delete
                                        clearCart();
                                        addItem(map);

                                    }
                                })
                                .setNegativeButton(context.getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // do nothing
                                        dialog.dismiss();

                                    }
                                });
                        AlertDialog alert = builder.create();
                        alert.show();
                        Button nbutton = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
                        nbutton.setTextColor(ContextCompat.getColor(context, R.color.theme));
                        nbutton.setTypeface(nbutton.getTypeface(), Typeface.BOLD);
                        Button pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
                        pbutton.setTextColor(ContextCompat.getColor(context, R.color.theme));
                        pbutton.setTypeface(pbutton.getTypeface(), Typeface.BOLD);
                    }


                }


            }
        });

        productSlider.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                addBottomDots(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void clearCart() {

        Call<ClearCart> call = apiInterface.clearCart();
        call.enqueue(new Callback<ClearCart>() {
            @Override
            public void onResponse(Call<ClearCart> call, Response<ClearCart> response) {

                if (response != null && !response.isSuccessful() && response.errorBody() != null) {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Toast.makeText(context, jObjError.optString("message"), Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else if (response.isSuccessful()) {
                    selectedShop = HotelViewActivity.Companion.getShops();
                    GlobalData.addCart.getProductList().clear();
                    GlobalData.notificationCount = 0;

                }

            }

            @Override
            public void onFailure(Call<ClearCart> call, Throwable t) {
                Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void addItem(HashMap<String, String> map) {

        customDialog.show();
        Call<AddCart> call = apiInterface.postAddCart(map);
        call.enqueue(new Callback<AddCart>() {
            @Override
            public void onResponse(@NonNull Call<AddCart> call, @NonNull Response<AddCart> response) {
                customDialog.dismiss();
                if (response.isSuccessful()) {
                    GlobalData.addCart = response.body();
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
            public void onFailure(@NonNull Call<AddCart> call, @NonNull Throwable t) {
                Toast.makeText(ProductDetailActivity.this, "ProductDetail : Something went wrong", Toast.LENGTH_SHORT).show();
                customDialog.dismiss();

            }
        });

    }

    private void addBottomDots(int currentPage) {
        TextView[] dots = new TextView[slider_image_list.size()];

        productSliderDots.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(Color.parseColor("#000000"));
            productSliderDots.addView(dots[i]);
        }

        if (dots.length > 0)
            dots[currentPage].setTextColor(Color.parseColor("#FFFFFF"));
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.anim_nothing, R.anim.slide_out_right);
        finish();
    }
}
