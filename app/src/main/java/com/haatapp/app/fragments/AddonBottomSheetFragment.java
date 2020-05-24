package com.haatapp.app.fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import com.haatapp.app.R;
import com.haatapp.app.adapter.CartAddOnsAdapter;
import com.haatapp.app.adapter.ViewCartAdapter;
import com.haatapp.app.helper.GlobalData;
import com.haatapp.app.models.Addon;
import com.haatapp.app.models.Cart;
import com.haatapp.app.models.Product;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.haatapp.app.adapter.CartAddOnsAdapter.list;


/**
 * Created by santhosh@appoets.com on 13-11-2017.
 */

public class AddonBottomSheetFragment extends BottomSheetDialogFragment {

    @BindView(R.id.add_ons_rv)
    RecyclerView addOnsRv;

    Context context;
    List<Addon> addonList;
    @BindView(R.id.food_type)
    ImageView foodType;
    @BindView(R.id.product_name)
    TextView productName;
    @BindView(R.id.product_price)
    TextView productPrice;

    @SuppressLint("RestrictedApi")
    public static TextView addons;
    public static TextView price;

    @BindView(R.id.update)
    TextView update;

    Unbinder unbinder;
    Product product;
   BottomSheetBehavior mBottomSheetBehavior ;

    public static Cart selectedCart;

    @SuppressLint("RestrictedApi")
    @Override
    public void setupDialog(final Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        final View contentView = View.inflate(getContext(), R.layout.addon_bottom_sheet_fragment, null);
        dialog.setContentView(contentView);
        ButterKnife.bind(this, contentView);
        context = getContext();
        addons = (TextView) contentView.findViewById(R.id.addons);
        price = (TextView) contentView.findViewById(R.id.price);
        addOnsRv.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        addOnsRv.setItemAnimator(new DefaultItemAnimator());
        addOnsRv.setHasFixedSize(false);
        addOnsRv.setNestedScrollingEnabled(false);
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) contentView.getParent()).getLayoutParams();
        CoordinatorLayout.Behavior behavior = params.getBehavior();

        if (behavior != null && behavior instanceof BottomSheetBehavior) {
            mBottomSheetBehavior = (BottomSheetBehavior) behavior;
            mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
                @Override
                public void onStateChanged(@NonNull View bottomSheet, int newState) {

                }

                @Override
                public void onSlide(@NonNull View bottomSheet, float slideOffset) {

                }
            });
            contentView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    contentView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    int height = contentView.getMeasuredHeight();
                    mBottomSheetBehavior.setPeekHeight(height);
                }
            });
        }
        addonList = new ArrayList<>();
        CartAddOnsAdapter addOnsAdapter = new CartAddOnsAdapter(addonList, context);
        addOnsRv.setAdapter(addOnsAdapter);
        if (selectedCart != null) {
            product = selectedCart.getProduct();
            GlobalData.cartAddons = selectedCart.getCartAddons();
            addonList.clear();
            addonList.addAll(product.getAddons());
            addOnsRv.getAdapter().notifyDataSetChanged();
        } else if (GlobalData.isSelectedProduct != null) {
            product = GlobalData.isSelectedProduct;
            addonList.clear();
            addonList.addAll(product.getAddons());
            addOnsRv.getAdapter().notifyDataSetChanged();
        }
        productName.setText(product.getName());
        productPrice.setText(product.getPrices().getCurrency() + " " + product.getPrices().getPrice());

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String, String> map = new HashMap<>();
                map.put("product_id", product.getId().toString());
                map.put("quantity", String.valueOf(GlobalData.isSelctedCart.getQuantity()));
                map.put("cart_id", String.valueOf(GlobalData.isSelctedCart.getId()));
                for (int i = 0; i < list.size(); i++) {
                    Addon addon = list.get(i);
                    if (addon.getAddon().getChecked()) {
                        map.put("product_addons[" + "" + i + "]", addon.getId().toString());
                        map.put("addons_qty[" + "" + i + "]", addon.getQuantity().toString());
                    }
                }
                Log.e("AddCart_add", map.toString());
                ViewCartAdapter.addCart(map);
                dismiss();
            }
        });

    }


    private void setAddOnsText() {
        int quantity = 0;
        int priceAmount = 0;
        boolean once=true;
        AddonBottomSheetFragment.addons.setText("");
        if (selectedCart != null) {
            Product product = AddonBottomSheetFragment.selectedCart.getProduct();
            quantity = AddonBottomSheetFragment.selectedCart.getQuantity();
            priceAmount = quantity * product.getPrices().getPrice();
            for (Addon addon : list) {
                if (addon.getAddon().getChecked()) {
                    if (once){
                        addons.append(addon.getAddon().getName());
                        once=false;
                    }
                    else{
                        AddonBottomSheetFragment.addons.append(", " + addon.getAddon().getName());
                    }

                    priceAmount = priceAmount + (quantity * (addon.getQuantity() * addon.getPrice()));
                }
            }
            if (quantity == 1)
                AddonBottomSheetFragment.price.setText(String.valueOf(quantity) + " Item | " + GlobalData.currencySymbol + priceAmount);
            else
                AddonBottomSheetFragment.price.setText(String.valueOf(quantity) + " Items | " + GlobalData.currencySymbol + priceAmount);

        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
