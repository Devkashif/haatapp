package com.haatapp.app.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.haatapp.app.R;
import com.haatapp.app.adapter.FilterAdapter;
import com.haatapp.app.helper.GlobalData;
import com.haatapp.app.models.Cuisine;
import com.haatapp.app.models.FilterModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static com.haatapp.app.fragments.HomeFragment.isFilterApplied;
import static com.haatapp.app.helper.GlobalData.cuisineIdArrayList;
import static com.haatapp.app.helper.GlobalData.isOfferApplied;
import static com.haatapp.app.helper.GlobalData.isPureVegApplied;

public class FilterActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.filter_rv)
    RecyclerView filterRv;
    public static Button applyFilterBtn;
    public static TextView resetTxt;
    public static boolean isReset = false;
    @BindView(R.id.close_img)
    ImageView closeImg;


    private FilterAdapter adapter;
    private List<FilterModel> modelListReference = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);
        ButterKnife.bind(this);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        applyFilterBtn = (Button) findViewById(R.id.apply_filter);
        resetTxt = (TextView) findViewById(R.id.reset_txt);
        closeImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.anim_nothing, R.anim.slide_down);
            }
        });

        List<FilterModel> modelList = new ArrayList<>();
        List<String> filters = new ArrayList<>();
        Cuisine cuisine1 = new Cuisine();
        cuisine1.setName("Offers");
        Cuisine cuisine2 = new Cuisine();
        cuisine2.setName("Pure veg");
        List<Cuisine> cuisineList1 = new ArrayList<>();
        cuisineList1.add(cuisine1);
        cuisineList1.add(cuisine2);
        FilterModel model = new FilterModel();
        model.setHeader("Show Restaurants With");
        model.setCuisines(cuisineList1);
        modelList.add(model);
        filters = new ArrayList<>();
        if (GlobalData.cuisineList != null) {
            for (Cuisine obj : GlobalData.cuisineList) {
                filters.add(obj.getName());
            }
            model = new FilterModel();
            model.setHeader("Cuisines");
            model.setCuisines(GlobalData.cuisineList);
            modelList.add(model);
            modelListReference.clear();
            modelListReference.addAll(modelList);
            LinearLayoutManager manager = new LinearLayoutManager(this);
            filterRv.setLayoutManager(manager);
            adapter = new FilterAdapter(this, modelListReference);
            if (isFilterApplied)
                isReset = false;
            else
                isReset = true;
            filterRv.setAdapter(adapter);
        }
        resetTxt.setOnClickListener(this);
        applyFilterBtn.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        FilterAdapter.cuisineIdList.clear();
        isReset = true;
        finish();
        overridePendingTransition(R.anim.anim_nothing, R.anim.slide_down);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.reset_txt:
                isReset = true;
                adapter.notifyDataSetChanged();
                break;

            case R.id.apply_filter:
                isPureVegApplied = FilterAdapter.isPureVegApplied;
                isOfferApplied = FilterAdapter.isOfferApplied;
                cuisineIdArrayList = new ArrayList<>();
                GlobalData.cuisineIdArrayList.addAll(FilterAdapter.cuisineIdList);
                isFilterApplied = false;
                if (isOfferApplied)
                    isFilterApplied = true;
                if (isPureVegApplied)
                    isFilterApplied = true;
                if (cuisineIdArrayList != null && cuisineIdArrayList.size() != 0)
                    isFilterApplied = true;
                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
                break;

        }

    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
