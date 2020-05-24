package com.haatapp.app.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.haatapp.app.R;
import com.haatapp.app.fragments.OrderViewFragment;
import com.haatapp.app.helper.GlobalData;
import com.haatapp.app.models.Order;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static com.haatapp.app.helper.GlobalData.isSelectedOrder;

public class PastOrderDetailActivity extends AppCompatActivity {

    Fragment orderFullViewFragment;
    FragmentManager fragmentManager;

    Double priceAmount = 0.0;
    int discount = 0;
    int itemCount = 0;
    int itemQuantity = 0;
    String currency = "";
    @BindView(R.id.order_id_txt)
    TextView orderIdTxt;
    @BindView(R.id.order_item_txt)
    TextView orderItemTxt;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.source_image)
    ImageView sourceImage;
    @BindView(R.id.restaurant_name)
    TextView restaurantName;
    @BindView(R.id.restaurant_address)
    TextView restaurantAddress;
    @BindView(R.id.source_layout)
    RelativeLayout sourceLayout;
    @BindView(R.id.destination_image)
    ImageView destinationImage;
    @BindView(R.id.user_address_title)
    TextView userAddressTitle;
    @BindView(R.id.user_address)
    TextView userAddress;
    @BindView(R.id.destination_layout)
    RelativeLayout destinationLayout;
    @BindView(R.id.view_line2)
    View viewLine2;
    @BindView(R.id.order_succeess_image)
    ImageView orderSucceessImage;
    @BindView(R.id.order_status_txt)
    TextView orderStatusTxt;
    @BindView(R.id.order_status_layout)
    RelativeLayout orderStatusLayout;
    @BindView(R.id.order_detail_fargment)
    FrameLayout orderDetailFargment;
    @BindView(R.id.nested_scroll_view)
    NestedScrollView nestedScrollView;
    @BindView(R.id.dot_line_img)
    ImageView dotLineImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        ButterKnife.bind(this);
        //Toolbar
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        if (isSelectedOrder != null) {
            Order order = GlobalData.isSelectedOrder;
            orderIdTxt.setText("ORDER #000" + order.getId().toString());
            itemQuantity = order.getInvoice().getQuantity();
            priceAmount = order.getInvoice().getPayable();
            if (order.getStatus().equalsIgnoreCase("CANCELLED")) {
                orderStatusTxt.setText(getResources().getString(R.string.order_cancelled));
                orderSucceessImage.setImageResource(R.drawable.order_cancelled_img);
                dotLineImg.setBackgroundResource(R.drawable.order_cancelled_line);
                orderStatusTxt.setTextColor(ContextCompat.getColor(this, R.color.colorRed));
            } else {
                orderStatusTxt.setText(getResources().getString(R.string.order_delivered_successfully_on) + getFormatTime(order.getOrdertiming().get(7).getCreatedAt()));
                orderStatusTxt.setTextColor(ContextCompat.getColor(this, R.color.colorGreen));
                orderSucceessImage.setImageResource(R.drawable.ic_circle_tick);
                dotLineImg.setBackgroundResource(R.drawable.ic_line);
            }
            currency = order.getItems().get(0).getProduct().getPrices().getCurrency();
            if (itemQuantity == 1)
                orderItemTxt.setText(String.valueOf(itemQuantity) + " Item, " + currency + String.valueOf(priceAmount));
            else
                orderItemTxt.setText(String.valueOf(itemQuantity) + " Items, " + currency + String.valueOf(priceAmount));

            restaurantName.setText(order.getShop().getName());
            restaurantAddress.setText(order.getShop().getAddress());
            userAddressTitle.setText(order.getAddress().getType());
            userAddress.setText(order.getAddress().getMapAddress());

            //set Fragment
            orderFullViewFragment = new OrderViewFragment();
            fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.add(R.id.order_detail_fargment, orderFullViewFragment).commit();
        }

    }

    private String getFormatTime(String time) {
        System.out.println("Time : " + time);
        String value = "";
        try {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm aa", Locale.getDefault());

            if (time != null) {
                Date date = df.parse(time);
                value = sdf.format(date);
            }

        } catch (ParseException e) {
            e.printStackTrace();

        }
        return value;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.anim_nothing, R.anim.slide_out_right);
    }
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}