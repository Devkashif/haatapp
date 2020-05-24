package com.haatapp.app.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.haatapp.app.BottomNavigationViewHelper;
import com.haatapp.app.HomeActivity;
import com.haatapp.app.R;
import com.haatapp.app.databinding.ActivityCategoryBinding;

public class CategoryActivity extends AppCompatActivity implements View.OnClickListener {
    SharedPreferences.Editor editor;
ImageView banner;
BottomNavigationView navigation;
ActivityCategoryBinding binding;
TextView refer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil.setContentView(this,R.layout.activity_category);
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        editor = pref.edit();
        setListener();
        navigation=findViewById(R.id.navigation);
        BottomNavigationViewHelper.disableShiftMode(navigation);
        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.about_us) {
                    Intent intent
                            =new Intent(getApplicationContext(),aboutus.class);
                    startActivity(intent);

                    overridePendingTransition(0,0);


                } else if (item.getItemId() == R.id.contact) {
                    Intent intent
                            =new Intent(getApplicationContext(),contact.class);
                    startActivity(intent);

                    overridePendingTransition(0,0);


                } else if (item.getItemId() == R.id.wallet) {
                    Intent intent
                            =new Intent(getApplicationContext(),WalletActivity.class);
                    intent.putExtra("add_hide","hide");
                    startActivity(intent);
                    overridePendingTransition(0,0);



                } else if (item.getItemId() == R.id.home) {

                    Intent intent
                            =new Intent(getApplicationContext(),CategoryActivity.class);
                    startActivity(intent);
                    overridePendingTransition(0,0);


                }
            return  true;
            }

            });
        banner=findViewById(R.id.banner);
//        refer=findViewById(R.id.refer);
//        refer.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                Intent intent
//                        =new Intent(getApplicationContext(),Refer_activity.class);
//                startActivity(intent);
//                overridePendingTransition(0,0);
//
//
//            }
//        });
//        banner.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent i=new Intent(CategoryActivity.this, HomeActivity.class);
//                i.putExtra("catid","5");
//                editor.putString("catid", "5");
//                editor.apply();
//                startActivity(i);
//            }
//        });
//        LinearLayout card_medical=(LinearLayout) findViewById(R.id.card_medical);
//        card_medical.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//            Intent i=new Intent(CategoryActivity.this, HomeActivity.class);
//                i.putExtra("catid","1");
//                editor.putString("catid", "1");
//                editor.apply();
//                startActivity(i);
//
//            }
//        });
//
//
//        LinearLayout card_kirana=(LinearLayout) findViewById(R.id.card_kirana);
//        card_kirana.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                Intent i=new Intent(CategoryActivity.this, HomeActivity.class);
//                i.putExtra("catid","2");
//                editor.putString("catid", "2");
//                editor.apply();
//                startActivity(i);
//
//            }
//        });


//        LinearLayout card_restaurant=(LinearLayout) findViewById(R.id.card_restaurant);
//        card_restaurant.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                Intent i=new Intent(CategoryActivity.this, HomeActivity.class);
//                i.putExtra("catid","3");
//                editor.putString("catid", "3");
//                editor.apply();
//                startActivity(i);
//
//            }
//        });

//        LinearLayout card_cosmetic=(LinearLayout) findViewById(R.id.card_cosmetic);
//        card_cosmetic.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                Intent i=new Intent(CategoryActivity.this, HomeActivity.class);
//                i.putExtra("catid","4");
//                editor.putString("catid", "4");
//                editor.apply();
//                startActivity(i);
//
//            }
//        });
//
//        LinearLayout card_shopjinu=(LinearLayout) findViewById(R.id.card_shopjinu);
//        card_shopjinu.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Log.e("clicked...","5");
//                Intent i=new Intent(CategoryActivity.this, HomeActivity.class);
//                i.putExtra("catid","5");
//                editor.putString("catid", "5");
//                editor.apply();
//                startActivity(i);
//
//            }
//        });

    }

    private void setListener() {
    binding.automobile.setOnClickListener(this);
        binding.bike.setOnClickListener(this);
        binding.cardKirana.setOnClickListener(this);
        binding.electronics.setOnClickListener(this);
        binding.fashion.setOnClickListener(this);
        binding.furneture.setOnClickListener(this);
        binding.grocery.setOnClickListener(this);
        binding.hardware.setOnClickListener(this);
        binding.kitchen.setOnClickListener(this);
        binding.meal.setOnClickListener(this);
        binding.vegitable.setOnClickListener(this);
        binding.restorent.setOnClickListener(this);
        binding.stationary.setOnClickListener(this);
        binding.petshop.setOnClickListener(this);
        binding.paanshop.setOnClickListener(this);
        binding.miscell.setOnClickListener(this);
        binding.suppliments.setOnClickListener(this);
        binding.sendpackage.setOnClickListener(this);
        binding.callToOrder.setOnClickListener(this);
//        binding.viewflipper.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View view, MotionEvent motionEvent) {
//                motion
//                return false;
//            }
//        });



    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.grocery:
            {
                callCategory("6");

                break;
            }
            case R.id.meal:
            {

                callCategory("7");

                break;
            }
            case R.id.fashion:
            {
                callCategory("8");

                break;
            }
            case R.id.petshop:
            {
                callCategory("9");

                break;
            }
            case R.id.electronics:
            {
                callCategory("10");

                break;
            }
            case R.id.bike:
            {
                callCategory("11");

                break;
            }
            case R.id.paanshop:
            {

                callCategory("13");
                break;
            }
            case R.id.sendpackage:
            {
              callCategory("12");

                break;
            }
            case R.id.suppliments:
            {
                callCategory("14");
                break;
            }
            case R.id.automobile:
            {
                callCategory("15");
                break;
            }
            case R.id.stationary:
            {
                callCategory("16");

                break;
            }
            case R.id.restorent:
            {
                callCategory("17");
                break;
            }
            case R.id.furneture:
            {
                callCategory("19");
                break;
            }
            case R.id.hardware:
            {
                callCategory("18");
                break;
            }
            case R.id.kitchen:
            {
                callCategory("20");
                break;
            }
            case R.id.miscell:
            {
                callCategory("21");
                break;
            }
            case R.id.vegitable:
            {
                callCategory("2");
                break;
            }

            case R.id.call_to_order:
                {
                callToOrder();
                break;
            }

        }
    }

    void callToOrder()
    {
        Intent  intent
                =new Intent(getApplicationContext(),CallToOrder.class);
        startActivity(intent);

    }
    void callCategory(String cat)
    {
        Intent i=new Intent(CategoryActivity.this, HomeActivity.class);
        i.putExtra("catid",cat);
        editor.putString("catid", cat);
        editor.apply();
        startActivity(i);
    }

}
