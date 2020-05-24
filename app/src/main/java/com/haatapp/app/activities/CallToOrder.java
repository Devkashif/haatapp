package com.haatapp.app.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.haatapp.app.R;
import com.haatapp.app.databinding.ActivityCallToOrderBinding;
import com.haatapp.app.databinding.CalltoorderlistitemBinding;
public class CallToOrder extends AppCompatActivity {

    int image[] = {R.drawable.meatandfish, R.drawable.vagitable, R.drawable.grocery, R.drawable.restaurant, R.drawable.fasion, R.drawable.electision, R.drawable.bike, R.drawable.stationnary, R.drawable.panshop, R.drawable.petshop, R.drawable.kitchen, R.drawable.hardware, R.drawable.misscell, R.drawable.suppliment, R.drawable.automobile, R.drawable.sendpackege, R.drawable.furneture};
    ActivityCallToOrderBinding callToOrderBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        callToOrderBinding = DataBindingUtil.setContentView(this, R.layout.activity_call_to_order);
        callToOrderBinding.listOfOrderCall.setAdapter(new Adapter());

    }

    class Adapter extends BaseAdapter {

        CalltoorderlistitemBinding binding;

        @Override
        public int getCount() {
            return 17;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View container_view = view;
            if (container_view == null) {
                binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.calltoorderlistitem, viewGroup, false);
                binding.image.setImageResource(image[i]);
                binding.getRoot().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent callIntent = new Intent(Intent.ACTION_CALL);
                        callIntent.setData(Uri.parse("tel:"+binding.number.getText().toString()));
                        if (ActivityCompat.checkSelfPermission(CallToOrder.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            ActivityCompat.requestPermissions(CallToOrder.this,
                                    new String[]{Manifest.permission.CALL_PHONE},
                                    1);
                            return;
                        }
                        startActivity(callIntent);
                    }
                });
                container_view=binding.getRoot();

            }

            return container_view;
        }
    }
}
