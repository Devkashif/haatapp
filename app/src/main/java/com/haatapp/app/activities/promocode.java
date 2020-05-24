package com.haatapp.app.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.haatapp.app.HomeActivity;
import com.haatapp.app.R;
import com.haatapp.app.build.configure.BuildConfigure;

import org.json.JSONArray;

import java.util.ArrayList;

public class promocode extends AppCompatActivity {
ListView discount_show;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_promocode2);
        discount_show=findViewById(R.id.discount_show);
        discount_show.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(getApplicationContext(),HomeActivity.class);
                TextView name=view.findViewById(R.id.name_promo);
                TextView discount=view.findViewById(R.id.amount);

                intent.putExtra("transfer",name.getText().toString());
                intent.putExtra("name",name.getText().toString());
                intent.putExtra("discount",discount.getText().toString());

                startActivity(intent);
                finish();
                overridePendingTransition(0,0);

            }
        });
        load_Vollay();
    }
    ArrayList<String> id=new ArrayList<>();
    ArrayList<String> name=new ArrayList<>();
    ArrayList<String> promocode_type=new ArrayList<>();
    ArrayList<String> value=new ArrayList<>();
    public void load_Vollay()
    {
        StringRequest stringRequest=new StringRequest(Request.Method.GET, BuildConfigure.BASE_URL+"promocode", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    for(int i=0;i<jsonArray.length();i++)
                    {
                        id.add(jsonArray.getJSONObject(i).getString("id"));
                        name.add(jsonArray.getJSONObject(i).getString("promo_code"));
                        promocode_type.add(jsonArray.getJSONObject(i).getString("promocode_type"));
                        value.add(jsonArray.getJSONObject(i).getString("discount"));

                    }
                    discount_show.setAdapter(new adabtor());

                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        Volley.newRequestQueue(getApplicationContext()).add(stringRequest);
    }
    class adabtor extends BaseAdapter
    {

        @Override
        public int getCount() {
            return id.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
          View main_view;
           if(convertView==null)
           {
               main_view=getLayoutInflater().inflate(R.layout.promo,null);
               TextView id_show=main_view.findViewById(R.id.id);
               TextView name_show=main_view.findViewById(R.id.name_promo);
               TextView price_show=main_view.findViewById(R.id.amount);
               id_show.setText(id.get(position));
               name_show.setText(name.get(position));
               price_show.setText(value.get(position));




           }
           else
           {
               main_view=convertView;
           }
           return  main_view;
        }
    }
}
