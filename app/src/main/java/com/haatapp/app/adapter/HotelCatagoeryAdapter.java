package com.haatapp.app.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.graphics.drawable.AnimatedVectorDrawableCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.sectionedrecyclerview.SectionedRecyclerViewAdapter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.haatapp.app.R;
import com.haatapp.app.activities.HotelViewActivity;
import com.haatapp.app.activities.LoginActivity;
import com.haatapp.app.activities.ProductDetailActivity;
import com.haatapp.app.activities.ViewCartActivity;
import com.haatapp.app.build.api.ApiClient;
import com.haatapp.app.build.api.ApiInterface;
import com.haatapp.app.fragments.CartChoiceModeFragment;
import com.haatapp.app.helper.GlobalData;
import com.haatapp.app.models.AddCart;
import com.haatapp.app.models.Cart;
import com.haatapp.app.models.CartAddon;
import com.haatapp.app.models.Category;
import com.haatapp.app.models.ClearCart;
import com.haatapp.app.models.Product;
import com.haatapp.app.models.ShopDetail;
import com.haatapp.app.utils.Utils;
import com.robinhood.ticker.TickerUtils;
import com.robinhood.ticker.TickerView;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.haatapp.app.helper.GlobalData.selectedShop;

/**
 * Created by Tamil on 28-08-2017.
 */

public class HotelCatagoeryAdapter extends SectionedRecyclerViewAdapter<HotelCatagoeryAdapter.ViewHolder> {

    private List<Category> list = new ArrayList<>();
    private LayoutInflater inflater;
    public static Context context;
    Activity activity;
    int lastPosition = -1;
    public static int priceAmount = 0;
    public static int itemCount = 0;
    public static int itemQuantity = 0;
    public static Product product;
    List<Product> productList;
    public static ApiInterface apiInterface = ApiClient.getRetrofit().create(ApiInterface.class);
    public static AddCart addCart;
    public static Animation slide_down, slide_up;
    public static CartChoiceModeFragment bottomSheetDialogFragment;
    public static AnimatedVectorDrawableCompat avdProgress;
    public static Dialog dialog;
    public static Runnable action;
    public static boolean dataResponse = false;

    //Animation number
    private static final char[] NUMBER_LIST = TickerUtils.getDefaultNumberList();

    public HotelCatagoeryAdapter(Context context, Activity activity, List<Category> list) {
        HotelCatagoeryAdapter.context = context;
        this.inflater = LayoutInflater.from(context);
        this.list = list;
        this.activity = activity;
        if (GlobalData.addCart != null && GlobalData.addCart.getProductList().size() != 0) {
            addCart = GlobalData.addCart;
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        switch (viewType) {
            case VIEW_TYPE_HEADER:
                v = inflater.inflate(R.layout.category_header, parent, false);
                return new ViewHolder(v, true);
            case VIEW_TYPE_ITEM:
                v = inflater.inflate(R.layout.accompainment_list_item, parent, false);
                return new ViewHolder(v, false);
            default:
                v = inflater.inflate(R.layout.accompainment_list_item, parent, false);
                return new ViewHolder(v, false);
        }
    }

    @Override
    public int getSectionCount() {
        return list.size();
    }


    @Override
    public int getItemCount(int section) {
        return list.get(section).getProducts().size();
    }

    @Override
    public void onBindHeaderViewHolder(ViewHolder holder, final int section) {

        if (list.get(section).getName().equalsIgnoreCase(context.getResources().getString(R.string.featured_products))) {
            holder.featureProductsTitle.setVisibility(View.VISIBLE);
            holder.categoryHeaderLayout.setVisibility(View.GONE);
        } else {
            holder.featureProductsTitle.setVisibility(View.GONE);
            holder.categoryHeaderLayout.setVisibility(View.VISIBLE);
            holder.headerTxt.setText(list.get(section).getName());
        }

        holder.headerTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println(list.get(section).getName());
            }
        });
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int section, final int relativePosition, int absolutePosition) {
        Category category = list.get(section);
        product = list.get(section).getProducts().get(relativePosition);
        productList = list.get(section).getProducts();
        holder.cardTextValueTicker.setCharacterList(NUMBER_LIST);
        holder.dishNameTxt.setText(product.getName());
        holder.cardTextValueTicker.setVisibility(View.GONE);
        holder.cardTextValue.setVisibility(View.VISIBLE);
        if (category.getName().equalsIgnoreCase(context.getResources().getString(R.string.featured_products))) {
            holder.featuredImage.setVisibility(View.VISIBLE);
            Glide.with(context).load(product.getFeaturedImages().get(0).getUrl())
                    .thumbnail(0.5f)
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.ic_banner)
                    .error((R.drawable.ic_banner))
                    .into(holder.featuredImage);
//            Glide.with(context).load(product.getFeaturedImages().get(0).getUrl()).placeholder(R.drawable.ic_banner).dontAnimate()
//                    .error(R.drawable.ic_banner).into(holder.featuredImage);

        } else {
            holder.featuredImage.setVisibility(View.GONE);
        }
        Picasso.with(context).load(product.getImages().get(0).getUrl()).into(holder.dishImg);

        //Check if product is already added
        if (product.getCart() != null && product.getCart().size() != 0) {

            selectedShop = HotelViewActivity.Companion.getShops();
            holder.cardAddTextLayout.setVisibility(View.GONE);
            holder.cardAddDetailLayout.setVisibility(View.VISIBLE);
            Integer quantity = 0;
            http://haatservices.com//public/storage/products/c51e26a13c28e099e1942d4d1bd63a5a.jpeg
          //  String product_image = product.getImages().get(0).getUrl();


            for (Cart cart : product.getCart()) {
                quantity += cart.getQuantity();
                Log.e("kali",product.getImages().toString());
            }
            if(!holder.cardTextValue.getText().toString().equalsIgnoreCase(String.valueOf(quantity))){
                holder.cardTextValueTicker.setText(String.valueOf(quantity));
                holder.cardTextValue.setText(String.valueOf(quantity));
            }

        } else {
            holder.cardAddTextLayout.setVisibility(View.VISIBLE);
            holder.cardAddDetailLayout.setVisibility(View.GONE);
            holder.cardTextValueTicker.setText(String.valueOf(1));
            holder.cardTextValue.setText(String.valueOf(1));
        }
        //Check if add-ons is available
        if (product.getAddons() != null && product.getAddons().size() != 0) {
            holder.customizableTxt.setVisibility(View.VISIBLE);
            holder.addOnsIconImg.setVisibility(View.VISIBLE);
        } else {
            holder.customizableTxt.setVisibility(View.GONE);
            holder.addOnsIconImg.setVisibility(View.GONE);
        }

        holder.rootLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GlobalData.isSelectedProduct = list.get(section).getProducts().get(relativePosition);
                context.startActivity(new Intent(context, ProductDetailActivity.class));
                activity.overridePendingTransition(R.anim.slide_in_right, R.anim.anim_nothing);

            }
        });

        if (product.getPrices().getCurrency() != null)
            holder.priceTxt.setText(product.getPrices().getCurrency() + " " + product.getPrices().getPrice());

        if (!product.getFoodType().equalsIgnoreCase("veg")) {
            holder.foodImageType.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_nonveg));
        } else {
            holder.foodImageType.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_veg));
        }
        holder.cardAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                product = list.get(section).getProducts().get(relativePosition);
                if(!Utils.isShopChanged(HotelViewActivity.Companion.getShops().getId())){
                    /** Intilaize Animation View Image */
                    avdProgress = AnimatedVectorDrawableCompat.create(context, R.drawable.add_cart_avd_line);
                    holder.animationLineCartAdd.setBackground(avdProgress);
                    avdProgress.start();
                    action = new Runnable() {
                        @Override
                        public void run() {
                            if (!dataResponse) {
                                avdProgress.start();
                                holder.animationLineCartAdd.postDelayed(action, 3000);
                            }

                        }
                    };
                    holder.animationLineCartAdd.postDelayed(action, 3000);
                    Log.e("access_token2", GlobalData.accessToken);
                    /** Press Add Card Add button */
                    if (product.getAddons() != null && !product.getAddons().isEmpty()) {
                        GlobalData.isSelectedProduct = product;
                        bottomSheetDialogFragment = new CartChoiceModeFragment();
                        bottomSheetDialogFragment.show(((AppCompatActivity) context).getSupportFragmentManager(), bottomSheetDialogFragment.getTag());
                        CartChoiceModeFragment.isViewcart = false;
                        CartChoiceModeFragment.isSearch = false;
                    } else {
                        holder.cardTextValueTicker.setVisibility(View.VISIBLE);
                        holder.cardTextValue.setVisibility(View.GONE);
                        holder.animationLineCartAdd.setVisibility(View.VISIBLE);
                        int cartId = 0;
                        for (int i = 0; i < addCart.getProductList().size(); i++) {
                            if (addCart.getProductList().get(i).getProductId().equals(product.getId())) {
                                cartId = addCart.getProductList().get(i).getId();
                            }
                        }
                        int countValue = Integer.parseInt(holder.cardTextValue.getText().toString()) + 1;
                        holder.cardTextValue.setText("" + countValue);
                        holder.cardTextValueTicker.setText("" + countValue);
                        HashMap<String, String> map = new HashMap<>();
                        map.put("product_id", product.getId().toString());
                        map.put("quantity", holder.cardTextValue.getText().toString());
                        map.put("cart_id", String.valueOf(cartId));
                        Log.e("AddCart_add", map.toString());
                        addCart(map);
                    }
                }
            }
        });
        holder.cardAddDetailLayout.setClickable(false);
        holder.cardAddDetailLayout.setEnabled(false);

        holder.cardMinusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /** Intilaize Animation View Image */
                holder.animationLineCartAdd.setVisibility(View.VISIBLE);
                holder.cardTextValueTicker.setVisibility(View.VISIBLE);
                holder.cardTextValue.setVisibility(View.GONE);
                //Intialize
                avdProgress = AnimatedVectorDrawableCompat.create(context, R.drawable.add_cart_avd_line);
                holder.animationLineCartAdd.setBackground(avdProgress);
                avdProgress.start();
                action = new Runnable() {
                    @Override
                    public void run() {
                        if (!dataResponse) {
                            avdProgress.start();
                            holder.animationLineCartAdd.postDelayed(action, 3000);
                        }

                    }
                };
                holder.animationLineCartAdd.postDelayed(action, 3000);

                /** Press Add Card Minus button */
                product = list.get(section).getProducts().get(relativePosition);
                int cartId = 0;
                for (int i = 0; i < addCart.getProductList().size(); i++) {
                    if (addCart.getProductList().get(i).getProductId().equals(product.getId())) {
                        cartId = addCart.getProductList().get(i).getId();
                    }
                }
                if (holder.cardTextValue.getText().toString().equalsIgnoreCase("1")) {
                    int countMinusValue = Integer.parseInt(holder.cardTextValue.getText().toString()) - 1;
                    holder.cardTextValue.setText("" + countMinusValue);
                    holder.cardTextValueTicker.setText("" + countMinusValue);
                    holder.cardAddDetailLayout.setVisibility(View.GONE);
                    if (addCart.getProductList().size() == 0 && addCart != null)
                        HotelViewActivity.Companion.getViewCartLayout().setVisibility(View.GONE);
                    holder.cardAddTextLayout.setVisibility(View.VISIBLE);
                    HashMap<String, String> map = new HashMap<>();
                    map.put("product_id", product.getId().toString());
                    map.put("quantity", "0");
                    map.put("cart_id", String.valueOf(cartId));
                    Log.e("AddCart_Minus", map.toString());
                    addCart(map);
                } else {
                    if (product.getCart().size() == 1) {
                        int countMinusValue = Integer.parseInt(holder.cardTextValue.getText().toString()) - 1;
                        holder.cardTextValue.setText("" + countMinusValue);
                        holder.cardTextValueTicker.setText("" + countMinusValue);
                        HashMap<String, String> map = new HashMap<>();
                        map.put("product_id", product.getId().toString());
                        map.put("quantity", holder.cardTextValue.getText().toString());
                        map.put("cart_id", String.valueOf(cartId));
                        List<CartAddon> cartAddonList=product.getCart().get(0).getCartAddons();
                        for (int i = 0; i < cartAddonList.size(); i++) {
                            CartAddon cartAddon = cartAddonList.get(i);
                            map.put("product_addons[" + "" + i + "]", cartAddon.getAddonProduct().getId().toString());
                            map.put("addons_qty[" + "" + i + "]", cartAddon.getQuantity().toString());
                        }
                        Log.e("AddCart_Minus", map.toString());
                        addCart(map);
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle(context.getResources().getString(R.string.remove_item_from_cart))
                                .setMessage(context.getResources().getString(R.string.remove_item_from_cart_description))
                                .setPositiveButton(context.getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // continue with delete
                                        context.startActivity(new Intent(context, ViewCartActivity.class));
                                        activity.overridePendingTransition(R.anim.slide_in_right, R.anim.anim_nothing);

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

        holder.cardAddTextLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                product = list.get(section).getProducts().get(relativePosition);
                if(GlobalData.profileModel==null){
                    activity.startActivity(new Intent(context, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                    activity.overridePendingTransition(R.anim.slide_in_left, R.anim.anim_nothing);
                    activity.finish();
                    Toast.makeText(context, context.getResources().getString(R.string.please_login_and_order_dishes), Toast.LENGTH_SHORT).show();
                }
                else if(!Utils.isShopChanged(HotelViewActivity.Companion.getShops().getId())){
                    /** Intilaize Animation View Image */
                    holder.animationLineCartAdd.setVisibility(View.VISIBLE);
                    //Intialize
                    avdProgress = AnimatedVectorDrawableCompat.create(context, R.drawable.add_cart_avd_line);
                    holder.animationLineCartAdd.setBackground(avdProgress);
                    avdProgress.start();
                    action = new Runnable() {
                        @Override
                        public void run() {
                            if (!dataResponse) {
                                avdProgress.start();
                                holder.animationLineCartAdd.postDelayed(action, 3000);
                            }

                        }
                    };
                    holder.animationLineCartAdd.postDelayed(action, 3000);

                    if (product.getAddons() != null && product.getAddons().size() != 0) {
                        GlobalData.isSelectedProduct = product;
                        context.startActivity(new Intent(context, ProductDetailActivity.class));
                        activity.overridePendingTransition(R.anim.slide_in_right, R.anim.anim_nothing);
                    } else {
                        holder.cardAddDetailLayout.setVisibility(View.VISIBLE);
                        holder.cardAddTextLayout.setVisibility(View.GONE);
                        holder.cardTextValue.setText("1");
                        holder.cardTextValueTicker.setText("1");
                      //  holder.dishImg.setImageResource();
                        HashMap<String, String> map = new HashMap<>();
                        map.put("product_id", product.getId().toString());
                        map.put("quantity", holder.cardTextValue.getText().toString());
                        Log.e("AddCart_Text", map.toString());
                        addCart(map);
                    }

                }
                else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle(context.getResources().getString(R.string.replace_cart_item))
                            .setMessage(context.getResources().getString(R.string.do_you_want_to_discart_the_selection_and_add_dishes_from_the_restaurant))
                            .setPositiveButton(context.getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // continue with delete
                                    clearCart();
                                    if (product.getAddons() != null && product.getAddons().size() != 0) {
                                        GlobalData.isSelectedProduct = product;
                                        context.startActivity(new Intent(context, ProductDetailActivity.class));
                                        activity.overridePendingTransition(R.anim.slide_in_right, R.anim.anim_nothing);
                                    } else {
                                        selectedShop = HotelViewActivity.Companion.getShops();
                                        product = list.get(section).getProducts().get(relativePosition);
                                        holder.cardAddDetailLayout.setVisibility(View.VISIBLE);
                                        holder.cardAddTextLayout.setVisibility(View.GONE);
                                        holder.cardTextValue.setText("1");
                                        holder.cardTextValueTicker.setText("1");
                                        HashMap<String, String> map = new HashMap<>();
                                        map.put("product_id", product.getId().toString());
                                        map.put("quantity", holder.cardTextValue.getText().toString());
                                        Log.e("AddCart_Text", map.toString());
                                        addCart(map);
                                    }

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
                    GlobalData.notificationCount=0;
                }

            }

            @Override
            public void onFailure(Call<ClearCart> call, Throwable t) {
                Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public static void addCart(HashMap<String, String> map) {
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.empty_dialog);
        dialog.setCancelable(false);
        dataResponse = false;
        dialog.show();
        Call<AddCart> call = apiInterface.postAddCart(map);
        call.enqueue(new Callback<AddCart>() {
            @Override
            public void onResponse(Call<AddCart> call, Response<AddCart> response) {
                selectedShop = HotelViewActivity.Companion.getShops();
                if (response != null && !response.isSuccessful() && response.errorBody() != null) {
                    dialog.dismiss();
                    dataResponse=true;
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Toast.makeText(context, jObjError.optString("message"), Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else if (response.isSuccessful()) {
                    if (selectedShop != null)
                        GlobalData.addCartShopId = selectedShop.getId();
                    addCart = response.body();
                    GlobalData.addCart = response.body();
                    setViewcartBottomLayout(addCart);
                    //get User Profile Data
                    if (GlobalData.profileModel != null) {
                        HashMap<String, String> map = new HashMap<>();
                        map.put("shop", String.valueOf(HotelViewActivity.Companion.getShops().getId()));
                        map.put("user_id", String.valueOf(GlobalData.profileModel.getId()));
                        getCategories(map);
                    } else {
                        HashMap<String, String> map = new HashMap<>();
                        map.put("shop", String.valueOf(HotelViewActivity.Companion.getShops().getId()));
                        getCategories(map);
                    }
                }
            }

            @Override
            public void onFailure(Call<AddCart> call, Throwable t) {
                dialog.dismiss();
                dataResponse=true;
                Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public static void getCategories(HashMap<String, String> map) {
        Call<ShopDetail> call = apiInterface.getCategories(map);
        call.enqueue(new Callback<ShopDetail>() {
            @Override
            public void onResponse(@NonNull Call<ShopDetail> call, @NonNull Response<ShopDetail> response) {
                dataResponse = true;
                dialog.dismiss();
                HotelViewActivity.Companion.getCategoryList().clear();
                Category category = new Category();
                category.setName(context.getResources().getString(R.string.featured_products));
                category.setProducts(response.body().getFeaturedProducts());
                HotelViewActivity.Companion.getCategoryList().add(category);
                HotelViewActivity.Companion.getCategoryList().addAll(response.body().getCategories());
                GlobalData.categoryList = HotelViewActivity.Companion.getCategoryList();
                GlobalData.selectedShop.setCategories(HotelViewActivity.Companion.getCategoryList());
                HotelViewActivity.Companion.getCatagoeryAdapter().notifyDataSetChanged();
            }

            @Override
            public void onFailure(@NonNull Call<ShopDetail> call, @NonNull Throwable t) {
                Toast.makeText(context, "Some thing went wrong", Toast.LENGTH_SHORT).show();
                dataResponse = true;
                dialog.dismiss();

            }
        });


    }


    private static void setViewcartBottomLayout(AddCart addCart) {
        priceAmount = 0;
        itemQuantity = 0;
        itemCount = 0;
        //get Item Count
        itemCount = addCart.getProductList().size();
        for (int i = 0; i < itemCount; i++) {
            //Get Total item Quantity
            itemQuantity = itemQuantity + addCart.getProductList().get(i).getQuantity();
            //Get addon price
            if (addCart.getProductList().get(i).getProduct().getPrices().getPrice() != null)
                priceAmount = priceAmount + (addCart.getProductList().get(i).getQuantity() * addCart.getProductList().get(i).getProduct().getPrices().getPrice());
            if (addCart.getProductList().get(i).getCartAddons() != null && !addCart.getProductList().get(i).getCartAddons().isEmpty()) {
                for (int j = 0; j < addCart.getProductList().get(i).getCartAddons().size(); j++) {
                    priceAmount = priceAmount + (addCart.getProductList().get(i).getQuantity() * (addCart.getProductList().get(i).getCartAddons().get(j).getQuantity() *
                            addCart.getProductList().get(i).getCartAddons().get(j).getAddonProduct().getPrice()));
                }
            }
        }
        GlobalData.notificationCount = itemQuantity;
        if (addCart.getProductList().isEmpty()) {
            HotelViewActivity.Companion.getViewCartLayout().setVisibility(View.GONE);
            // Start animation
            HotelViewActivity.Companion.getViewCartLayout().startAnimation(slide_down);
        }
        else {
            if (!Objects.equals(HotelViewActivity.Companion.getShops().getId(), GlobalData.addCart.getProductList().get(0).getProduct().getShopId())) {
                HotelViewActivity.Companion.getViewCartShopName().setVisibility(View.VISIBLE);
                HotelViewActivity.Companion.getViewCartShopName().setText("From : " + GlobalData.addCart.getProductList().get(0).getProduct().getShop().getName());
            } else {
                HotelViewActivity.Companion.getViewCartShopName().setVisibility(View.GONE);
            }
            String currency = addCart.getProductList().get(0).getProduct().getPrices().getCurrency();
            String itemMessage=context.getResources().getQuantityString(R.plurals.item,itemQuantity,itemQuantity);
            itemMessage=itemMessage+ " | " + currency + String.valueOf(priceAmount);
            Log.d("itemMessage",itemMessage);
            HotelViewActivity.Companion.getItemText().setText(itemMessage);
            if (HotelViewActivity.Companion.getViewCartLayout().getVisibility() == View.GONE) {
                // Start animation
                HotelViewActivity.Companion.getViewCartLayout().setVisibility(View.VISIBLE);
                HotelViewActivity.Companion.getViewCartLayout().startAnimation(slide_up);
            }

        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView headerTxt, featureProductsTitle;
        private ImageView dishImg, foodImageType, cardAddBtn, cardMinusBtn, animationLineCartAdd, addOnsIconImg, featuredImage;
        private TextView dishNameTxt, priceTxt, cardTextValue, cardAddInfoText, cardAddOutOfStock, customizableTxt;
        TickerView cardTextValueTicker;
        RelativeLayout cardAddDetailLayout, cardAddTextLayout, cardInfoLayout;
        LinearLayout rootLayout, categoryHeaderLayout;

        public ViewHolder(View itemView, boolean isHeader) {
            super(itemView);
            if (isHeader) {
                headerTxt = (TextView) itemView.findViewById(R.id.category_header);
                featureProductsTitle = (TextView) itemView.findViewById(R.id.featured_product_title);
                categoryHeaderLayout = (LinearLayout) itemView.findViewById(R.id.category_header_layout);

            } else {
                dishImg = (ImageView) itemView.findViewById(R.id.dishImagekali);
                foodImageType = (ImageView) itemView.findViewById(R.id.food_type_image);
                featuredImage = (ImageView) itemView.findViewById(R.id.featured_image);
                addOnsIconImg = (ImageView) itemView.findViewById(R.id.add_ons_icon);
                animationLineCartAdd = (ImageView) itemView.findViewById(R.id.animation_line_cart_add);
                dishNameTxt = (TextView) itemView.findViewById(R.id.dish_name_text);
                customizableTxt = (TextView) itemView.findViewById(R.id.customizable_txt);
                priceTxt = (TextView) itemView.findViewById(R.id.price_text);

             /*    Add card Button Layout*/
                cardAddDetailLayout = (RelativeLayout) itemView.findViewById(R.id.add_card_layout);
                rootLayout = (LinearLayout) itemView.findViewById(R.id.root_layout);
                cardAddTextLayout = (RelativeLayout) itemView.findViewById(R.id.add_card_text_layout);
                cardInfoLayout = (RelativeLayout) itemView.findViewById(R.id.add_card_info_layout);
                cardAddInfoText = (TextView) itemView.findViewById(R.id.avialablity_time);
                cardAddOutOfStock = (TextView) itemView.findViewById(R.id.out_of_stock);
                cardAddBtn = (ImageView) itemView.findViewById(R.id.card_add_btn);
                cardMinusBtn = (ImageView) itemView.findViewById(R.id.card_minus_btn);
                cardTextValue = (TextView) itemView.findViewById(R.id.card_value);
                cardTextValueTicker = (TickerView) itemView.findViewById(R.id.card_value_ticker);
                //itemView.setOnClickListener( this);

                //Load animation
                slide_down = AnimationUtils.loadAnimation(context,
                        R.anim.slide_down);
                slide_up = AnimationUtils.loadAnimation(context,
                        R.anim.slide_up);
            }

        }

    }

}
