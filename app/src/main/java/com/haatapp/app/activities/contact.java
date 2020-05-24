package com.haatapp.app.activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;
import com.haatapp.app.BottomNavigationViewHelper;
import com.haatapp.app.R;
import com.haatapp.app.dbhealper;
import com.haatapp.app.pojo.Person;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class contact extends AppCompatActivity {
    BottomNavigationView navigation;
    ProgressDialog progressDialog;
    public static final String MyPREFERENCES = "MyPrefs" ;
    Button link_phonebook;
    private boolean permissionAlreadyGranted() {

        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);

        if (result == PackageManager.PERMISSION_GRANTED)
            return true;

        return false;
    }
    private void requestPermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_PHONE_STATE)) {

        }
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS,Manifest.permission.READ_SMS,Manifest.permission.READ_PHONE_STATE}, 21);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Wait...");
        if (permissionAlreadyGranted()) {
       Log.e("granded","true");

        }
        else {
            Log.e("granded","no");

            requestPermission();
        }
        navigation = findViewById(R.id.navigation);
        BottomNavigationViewHelper.disableShiftMode(navigation);
        link_phonebook = findViewById(R.id.link_phonebook);
        link_phonebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
           new ay().execute();
            }
        });
        navigation.setSelectedItemId(R.id.contact);
        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.about_us) {
                    Intent intent
                            = new Intent(getApplicationContext(), aboutus.class);
                    startActivity(intent);
                    overridePendingTransition(0, 0);


                } else if (item.getItemId() == R.id.contact) {
                    Intent intent
                            = new Intent(getApplicationContext(), contact.class);
                    startActivity(intent);
                    overridePendingTransition(0, 0);


                } else if (item.getItemId() == R.id.wallet) {
                    Intent intent
                            = new Intent(getApplicationContext(), WalletActivity.class);
                    intent.putExtra("add_hide", "hide");
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                    finish();


                } else if (item.getItemId() == R.id.home) {
                    Intent intent
                            = new Intent(getApplicationContext(), CategoryActivity.class);
                    startActivity(intent);
                    overridePendingTransition(0, 0);

                }
                return true;
            }

        });
    }

    private List<Person> getContactList() {
        ArrayList<Person> contactList = new ArrayList<Person>();

        Uri contactUri = ContactsContract.Contacts.CONTENT_URI;
        String[] PROJECTION = new String[]{
                ContactsContract.Contacts._ID,
                ContactsContract.Contacts.DISPLAY_NAME,
                ContactsContract.Contacts.HAS_PHONE_NUMBER,
        };
        String SELECTION = ContactsContract.Contacts.HAS_PHONE_NUMBER + "='1'";
        Cursor contacts = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, PROJECTION, SELECTION, null, null);


        if (contacts.getCount() > 0) {
            while (contacts.moveToNext()) {
                Person aContact = new Person();
                int idFieldColumnIndex = 0;
                int nameFieldColumnIndex = 0;
                int numberFieldColumnIndex = 0;

                String contactId = contacts.getString(contacts.getColumnIndex(ContactsContract.Contacts._ID));

                nameFieldColumnIndex = contacts.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME);
                if (nameFieldColumnIndex > -1) {
                    aContact.setName(contacts.getString(nameFieldColumnIndex));
                }

                PROJECTION = new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER};
                final Cursor phone = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, PROJECTION, ContactsContract.Data.CONTACT_ID + "=?", new String[]{String.valueOf(contactId)}, null);
                if (phone.moveToFirst()) {
                    while (!phone.isAfterLast()) {
                        numberFieldColumnIndex = phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                        if (numberFieldColumnIndex > -1) {
                            aContact.setPhoneNum(phone.getString(numberFieldColumnIndex));
                            phone.moveToNext();
                            TelephonyManager mTelephonyMgr;
                            mTelephonyMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {

                                Log.e("yaha ","aaa gya");


                            }
                            if (!mTelephonyMgr.getLine1Number().contains(aContact.getPhoneNum())) {
                                contactList.add(aContact);
                            }
                        }
                    }
                }
                phone.close();
            }

            contacts.close();
        }

        return contactList;
    }
    public void readPermission()
    {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_PERMISSIONS);
            if (info.requestedPermissions != null) {
                for (String p : info.requestedPermissions) {
                    Log.d("sdsdsadas", "Permission : " + p);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

public     class ay extends AsyncTask
    {
        @Override
        protected void onPreExecute() {
            progressDialog.show();
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            if(!new dbhealper(contact.this).getContact()) {
                List<Person> people = getContactList();
                JSONArray array = new JSONArray();
                for (Person person : people) {
                    JSONObject object = new JSONObject();
                    try {
                        SharedPreferences sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
                        String val = sharedpreferences.getString("id", "");
                        Log.e("kali...", val);
                        object.put("name", person.getName());
                        object.put("number", person.getPhoneNum());
                        object.put("id", val);
                    } catch (Exception e) {

                    }
                    array.put(object);
                }
                Log.e("lenth", array.length() + "");
                JsonRequest<JSONArray> request = new JsonArrayRequest(Request.Method.POST, "https://shopjinu.com/public/api/user/contact_sync", array, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.e("response", response.toString());
                        if (response.length() > 0) {
                            new dbhealper(contact.this).insert();
                            Toast.makeText(getApplicationContext(), "Inserted Successfully", Toast.LENGTH_LONG).show();
                            Log.e("inserted", "successfully");
                        } else {
                            Toast.makeText(getApplicationContext(), "Some problem occur", Toast.LENGTH_LONG).show();

                        }


                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "Internet problem", Toast.LENGTH_LONG).show();
                        error.printStackTrace();
                        //Log.e("response",response.toString());

                    }
                });


                Volley.newRequestQueue(getApplicationContext()).add(request);

            }
            else
            {
                contact.this.runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(contact.this,"Contact already sync",Toast.LENGTH_LONG).show();

                    }
                });
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
progressDialog.dismiss();
        }
    }
}