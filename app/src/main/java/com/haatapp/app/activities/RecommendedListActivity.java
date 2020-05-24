package com.haatapp.app.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.haatapp.app.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class RecommendedListActivity extends AppCompatActivity {

    @BindView(R.id.title)
    TextView titleTxt;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.recommended_dishes_lv)
    ListView recommendedDishesLv;
    @BindView(R.id.cart_header)
    LinearLayout ll;
    @BindView(R.id.item)
    TextView itemTxt;
    @BindView(R.id.open_cart)
    TextView openCartTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommended_list);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_close);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

//        ArrayList<RecommendedDish> list = new ArrayList<>();
//        list.add(new RecommendedDish("Non", "Starter", "$20", true, "url", "Lorem ipsum dolor sit amet, consectetur adipiscing elit"));
//        list.add(new RecommendedDish("Dosa", "Breakfast", "$10", true, "url", "Amet lorem ipsum dolor sit amet, consectetur adipiscing elit"));
//        list.add(new RecommendedDish("Biriyani", "Lunch", "$25", false, "url", "Consectetur iorem ipsum dolor sit amet, consectetur adipiscing elit"));
//        list.add(new RecommendedDish("Icecream", "Desert", "$10", true, "url", "Dolor lorem ipsum dolor sit amet, adipiscing elit"));
//        RecommendedListAdapter adbPerson = new RecommendedListAdapter(RecommendedListActivity.this, list);
//        recommendedDishesLv.setAdapter(adbPerson);

    }


    public void addItemToCart() {
        ll.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.open_cart)
    public void onViewClicked() {

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
}