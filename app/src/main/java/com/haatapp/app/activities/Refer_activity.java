package com.haatapp.app.activities;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.haatapp.app.R;

public class Refer_activity extends AppCompatActivity {
ImageView whatsup_send;
ImageView email_send;
ImageView more_send;
TextView personal_code;
TextView copy_code;
SharedPreferences sharedpreferences;
    String message;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refer_activity);
        whatsup_send=findViewById(R.id.whatsup_send);
        email_send=findViewById(R.id.gamil_send);
         sharedpreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

         message = "Download ShopJinu App,\n" +
                "an easy way to purchase with your shop, Download with this below link and enter refer code "+sharedpreferences.getString("refer_code","FABCDS")+" and earn 5kg aalu on your first order https://play.google.com/store/apps/details?id=com.shopjinuuser.app";

        more_send=findViewById(R.id.more_send);
        personal_code=findViewById(R.id.personal_code);
        personal_code.setText(sharedpreferences.getString("refer_code","FABCDS"));
        copy_code=findViewById(R.id.copy_code);
        whatsup_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PackageManager pm=getPackageManager();
                try {

                    Intent waIntent = new Intent(Intent.ACTION_SEND);
                    waIntent.setType("text/plain");
                    String text = message;

                    PackageInfo info=pm.getPackageInfo("com.whatsapp", PackageManager.GET_META_DATA);
                    //Check if package exists or not. If not then code
                    //in catch block will be called
                    waIntent.setPackage("com.whatsapp");

                    waIntent.putExtra(Intent.EXTRA_TEXT, text);
                    startActivity(Intent.createChooser(waIntent, "Share with"));

                } catch (PackageManager.NameNotFoundException e) {
                    Toast.makeText(getApplicationContext(), "WhatsApp not Installed", Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });
        email_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PackageManager pm=getPackageManager();
                try {

                    Intent waIntent = new Intent(Intent.ACTION_SEND);
                    waIntent.setType("text/plain");
                    String text = message;

                    PackageInfo info=pm.getPackageInfo("com.whatsapp", PackageManager.GET_META_DATA);
                    //Check if package exists or not. If not then code
                    //in catch block will be called
                    waIntent.setPackage("com.gmail");

                    waIntent.putExtra(Intent.EXTRA_TEXT, text);
                    startActivity(Intent.createChooser(waIntent, "Share with"));

                } catch (PackageManager.NameNotFoundException e) {
                    Toast.makeText(getApplicationContext(), "WhatsApp not Installed", Toast.LENGTH_SHORT)
                            .show();
                }

            }
        });
        more_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                     Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("text/plain");
                share.putExtra(Intent.EXTRA_TEXT, message);

                startActivity(Intent.createChooser(share, "Refer the app any where"));

            }
        });
        copy_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("refer code", personal_code.getText().toString());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(getApplicationContext(),"Code copy",Toast.LENGTH_SHORT).show();

            }
        });


    }
}
