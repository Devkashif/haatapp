package com.haatapp.app.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.haatapp.app.BottomNavigationViewHelper;
import com.haatapp.app.R;

public class aboutus extends AppCompatActivity {
TextView heading1;
TextView heading2;
TextView heading3;
    BottomNavigationView navigation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aboutus);
        heading1=findViewById(R.id.first_heading);
        heading2=findViewById(R.id.second_heading);
        heading3=findViewById(R.id.third_heading);
        heading1.setText("ShopJinu is an on-demand delivery service available in Agra. We ensure distance is no bar, while you enjoy great offers and deals! We pick up and deliver anything in your city: food - your favorite pizza or burger, pharmacy, groceries, daily use products, Tiffin at your next phone or any other electronic item. No cash, no hassle - use online payment options like debit or credit cards, net banking, or mobile wallets like Paytm and UPI. Live track your delivery right to your doorstep.\n" +
                "");
        heading2.setText("\n" +
                "Quick food delivery from restaurants near you, from those far away.\n" +
                "Enjoy hassle-free online grocery shopping and home delivery of a wide range of products including fresh fruits and vegetables across various brands. ");
        heading3.setText("Order all your essential medicines online whether allopathic, homeopathic or ayurvedic and Over the Counter (OTC) healthcare products from the convenience of your home\n" +
                "\n" +
                "If you feel like writing to us about your tasks, improvements or just a friendly hello, drop in a mail at info@shopjinu.com, and we shall get back to you.");

        navigation=findViewById(R.id.navigation);
        BottomNavigationViewHelper.disableShiftMode(navigation);
        navigation.setSelectedItemId(R.id.about_us);
        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
             if (item.getItemId() == R.id.contact) {
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
                 finish();

                } else if (item.getItemId() == R.id.home) {

                    Intent intent
                            =new Intent(getApplicationContext(),CategoryActivity.class);
                    startActivity(intent);
                    overridePendingTransition(0,0);


                }
                else {

                }
                return  true;
            }

        });
    }
}
