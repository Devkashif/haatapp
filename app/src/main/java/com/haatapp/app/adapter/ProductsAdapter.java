package com.haatapp.app.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.sectionedrecyclerview.SectionedRecyclerViewAdapter;
import com.haatapp.app.HomeActivity;
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
import com.haatapp.app.models.ClearCart;
import com.haatapp.app.models.Product;
import com.haatapp.app.models.Shop;
import com.haatapp.app.utils.Utils;
import com.robinhood.ticker.TickerUtils;
import com.robinhood.ticker.TickerView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.haatapp.app.helper.GlobalData.notificationCount;
import static com.haatapp.app.helper.GlobalData.profileModel;
import static com.haatapp.app.helper.GlobalData.selectedShop;

/**
 * Created by Tamil on 28-08-2017.
 */

public class ProductsAdapter extends SectionedRecyclerViewAdapter<ProductsAdapter.ViewHolder> {

    List<Product> list = new ArrayList<>();
    private LayoutInflater inflater;
    public static Context context;
    public static Activity activity;
    public static int lastPosition = -1;
    public static int priceAmount = 0;
    public static int itemCount = 0;
    public static int itemQuantity = 0;
    public static Product product;
    public static List<Product> productList;
    public static ApiInterface apiInterface = ApiClient.getRetrofit().create(ApiInterface.class);
    public static AddCart addCart;
    public static Animation slide_down, slide_up;
    public static boolean isShopIsChanged = true;
    public static Shop currentShop = new Shop();
    public static CartChoiceModeFragment bottomSheetDialogFragment;

    //Animation number
    private static final char[] NUMBER_LIST = TickerUtils.getDefaultNumberList();

    public ProductsAdapter(Context context, Activity activity, List<Product> list) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.list = list;
        this.activity = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        switch (viewType) {
            case VIEW_TYPE_HEADER:
                v = inflater.inflate(R.layout.product_header, parent, false);
                return new ViewHolder(v, true);
            case VIEW_TYPE_ITEM:
                v = inflater.inflate(R.layout.product_search_list_item, parent, false);
                return new ViewHolder(v, false);
            default:
                v = inflater.inflate(R.layout.product_search_list_item, parent, false);
                return new ViewHolder(v, false);
        }
    }

    @Override
    public int getSectionCount() {
        return list.size();
    }


    @Override
    public int getItemCount(int section) {
        return 1;
    }

    @Override
    public void onBindHeaderViewHolder(ViewHolder holder, final int section) {
        holder.headerTxt.setText(list.get(section).getShop().getName());
        holder.headerTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println(list.get(section).getShop().getName());
            }
        });
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int section, final int relativePosition, final int absolutePosition) {
        product = list.get(section);
        holder.cardTextValueTicker.setCharacterList(NUMBER_LIST);
        holder.dishNameTxt.setText(product.getName());
        addCart = GlobalData.addCart;
        if (!product.getCart().isEmpty()) {
            selectedShop = HotelViewActivity.Companion.getShops();
            holder.cardAddTextLayout.setVisibility(View.GONE);
            holder.cardAddDetailLayout.setVisibility(View.VISIBLE);
            Integer quantity = 0;
            for (Cart cart : product.getCart()) {
                quantity += cart.getQuantity();
            }
            holder.cardTextValueTicker.setText(String.valueOf(quantity));
            holder.cardTextValue.setText(String.valueOf(quantity));
        } else {
            holder.cardAddTextLayout.setVisibility(View.VISIBLE);
            holder.cardAddDetailLayout.setVisibility(View.GONE);
            holder.cardTextValueTicker.setText(String.valueOf(1));
            holder.cardTextValue.setText(String.valueOf(1));
        }

        if (product.getAddons() != null && product.getAddons().size() != 0) {
            holder.customizableTxt.setVisibility(View.VISIBLE);
            holder.addOnsIconImg.setVisibility(View.VISIBLE);
        } else {
            holder.customizableTxt.setVisibility(View.GONE);
            holder.addOnsIconImg.setVisibility(View.GONE);
        }

        holder.priceTxt.setText(product.getPrices().getCurrency() + " " + product.getPrices().getPrice());

        if (!product.getFoodType().equalsIgnoreCase("veg")) {
            holder.foodImageType.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_nonveg));
        } else {
            holder.foodImageType.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_veg));
        }
        holder.cardAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("access_token2", GlobalData.accessToken);
                /** Press Add Card Add button */
                product = list.get(section);
                currentShop = list.get(section).getShop();
                if (product.getAddons() != null && !product.getAddons().isEmpty()) {
                    GlobalData.isSelectedProduct = product;
                    bottomSheetDialogFragment = new CartChoiceModeFragment();
                    bottomSheetDialogFragment.show(((AppCompatActivity) context).getSupportFragmentManager(), bottomSheetDialogFragment.getTag());
                    CartChoiceModeFragment.isViewcart = false;
                    CartChoiceModeFragment.isSearch = true;
                } else {
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
        });

        holder.cardMinusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /** Press Add Card Minus button */
                product = list.get(section);
                currentShop = list.get(section).getShop();
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

        holder.viewFullMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, HotelViewActivity.class).putExtra("position", section));
                selectedShop = list.get(section).getShop();
                activity.overridePendingTransition(R.anim.slide_in_right, R.anim.anim_nothing);
            }
        });

        holder.rootLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GlobalData.isSelectedProduct = list.get(section);
                context.startActivity(new Intent(context, ProductDetailActivity.class));
                activity.overridePendingTransition(R.anim.slide_in_right, R.anim.anim_nothing);
            }
        });


        holder.cardAddTextLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /** Press Add Card Text Layout */
                product = list.get(section);
                if (profileModel != null) {
                  if (Utils.isShopChanged(product.getShopId())) {
                        String message = String.format(activity.getResources().getString(R.string.reorder_confirm_message), product.getShop().getName(), GlobalData.addCart.getProductList().get(0).getProduct().getShop().getName());
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle(context.getResources().getString(R.string.replace_cart_item))
                                .setMessage(message)
                                .setPositiveButton(context.getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // continue with delete
                                        clearCart();
                                        isShopIsChanged = false;
                                        if (product.getAddons() != null && product.getAddons().size() != 0) {
                                            GlobalData.isSelectedProduct = product;
                                            context.startActivity(new Intent(context, ProductDetailActivity.class));
                                            activity.overridePendingTransition(R.anim.slide_in_right, R.anim.anim_nothing);
                                        } else {
                                            selectedShop = product.getShop();
                                            product = list.get(section);
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
                    } else {
                        currentShop = list.get(section).getShop();
                        if (product.getAddons() != null && product.getAddons().size() != 0) {
                            GlobalData.isSelectedProduct = product;
                            context.startActivity(new Intent(context, ProductDetailActivity.class));
                            activity.overridePendingTransition(R.anim.slide_in_right, R.anim.anim_nothing);
                        } else {
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
                } else {
                    activity.startActivity(new Intent(context, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                    activity.overridePendingTransition(R.anim.slide_in_left, R.anim.anim_nothing);
                    activity.finish();
                    Toast.makeText(context, context.getResources().getString(R.string.please_login_and_order_dishes), Toast.LENGTH_SHORT).show();
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
                GlobalData.addCartShopId = selectedShop.getId();
            }
        });

    }


    public static void addCart(HashMap<String, String> map) {
        Call<AddCart> call = apiInterface.postAddCart(map);
        call.enqueue(new Callback<AddCart>() {
            @Override
            public void onResponse(Call<AddCart> call, Response<AddCart> response) {
                selectedShop = currentShop;
                if (response != null && !response.isSuccessful() && response.errorBody() != null) {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Toast.makeText(context, jObjError.optString("message"), Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else if (response.isSuccessful()) {
                    GlobalData.addCartShopId = selectedShop.getId();
                    addCart = response.body();
                    GlobalData.addCart = response.body();

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
                    }
                    notificationCount = itemQuantity;
                    HomeActivity.updateNotificationCount(context, notificationCount);
                }
            }

            @Override
            public void onFailure(Call<AddCart> call, Throwable t) {

            }
        });

    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView headerTxt;
        private ImageView dishImg, foodImageType, cardAddBtn, cardMinusBtn, animationLineCartAdd, addOnsIconImg;
        private TextView dishNameTxt, priceTxt, cardTextValue, cardAddInfoText, cardAddOutOfStock, viewFullMenu, customizableTxt;
        TickerView cardTextValueTicker;
        RelativeLayout cardAddDetailLayout, cardAddTextLayout, cardInfoLayout, rootLayout;

        public ViewHolder(View itemView, boolean isHeader) {
            super(itemView);
            if (isHeader) {
                headerTxt = (TextView) itemView.findViewById(R.id.product_header);
            } else {
                dishImg = (ImageView) itemView.findViewById(R.id.dishImg);
                foodImageType = (ImageView) itemView.findViewById(R.id.food_type_image);
                animationLineCartAdd = (ImageView) itemView.findViewById(R.id.animation_line_cart_add);
                dishNameTxt = (TextView) itemView.findViewById(R.id.dish_name_text);
                priceTxt = (TextView) itemView.findViewById(R.id.price_text);
                addOnsIconImg = (ImageView) itemView.findViewById(R.id.add_ons_icon);
                customizableTxt = (TextView) itemView.findViewById(R.id.customizable_txt);

             /*    Add card Button Layout*/
                cardAddDetailLayout = (RelativeLayout) itemView.findViewById(R.id.add_card_layout);
                rootLayout = (RelativeLayout) itemView.findViewById(R.id.root_layout);
                cardAddTextLayout = (RelativeLayout) itemView.findViewById(R.id.add_card_text_layout);
                cardInfoLayout = (RelativeLayout) itemView.findViewById(R.id.add_card_info_layout);
                cardAddInfoText = (TextView) itemView.findViewById(R.id.avialablity_time);
                cardAddOutOfStock = (TextView) itemView.findViewById(R.id.out_of_stock);
                cardAddBtn = (ImageView) itemView.findViewById(R.id.card_add_btn);
                cardMinusBtn = (ImageView) itemView.findViewById(R.id.card_minus_btn);
                cardTextValue = (TextView) itemView.findViewById(R.id.card_value);
                viewFullMenu = (TextView) itemView.findViewById(R.id.view_full_menu);
                cardTextValueTicker = (TickerView) itemView.findViewById(R.id.card_value_ticker);

                //Load animation
                slide_down = AnimationUtils.loadAnimation(context,
                        R.anim.slide_down);
                slide_up = AnimationUtils.loadAnimation(context,
                        R.anim.slide_up);
            }

        }


    }
}
