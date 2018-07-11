package com.xperienceit;

import android.content.Intent;
import android.os.Parcel;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Booking extends AppCompatActivity {
    public static final String VALUE = "value";
    public static final String SEARCH_CODE = "search_code";
    public static final String DATE = "date";
    public static final String TIME = "time";
    public static final String NAME = "name";
    public static final String EMAIL = "email";
    public static final String MOBILE = "mobile";
    public static final String CUSTOMISATION = "customisation";
    public static final String PREBOOK = "prebook";
    private static final String Check_COUPON_URL = "https://xperienceit.in/back/checkCouponapi.php";
    private static final String Check_AVAILIBILITY = "https://xperienceit.in/back/checkavailapi.php";
    private static final String PREBOOK_URL = "https://xperienceit.in/back/prebookapi.php";
    public String availabilityCheck;
    String date, time, name, email, mobile, searchCode, amount;
    String[] customisations;
    XperienceObject object;
    private ArrayList<HashMap<String, String>> customizationList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        customizationList = (ArrayList<HashMap<String, String>>) getIntent().getSerializableExtra("customizations");

        //customisations=customizationList.toArray(new String[customizationList.size()]);
        name = getIntent().getStringExtra("userName");
        email = getIntent().getStringExtra("email");
        mobile = getIntent().getStringExtra("phone");
        date = getIntent().getStringExtra("date");
        time = getIntent().getStringExtra("time");
        amount = getIntent().getStringExtra("price");
        object = getIntent().getParcelableExtra("details");
        searchCode = object.getSearchCode();

        checkAvailability();
        //checkCoupon();
        //preBook();
    }

    public String checkAvailability() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Check_AVAILIBILITY, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.v("Availibility check ", response);
                //Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();
                availabilityCheck = response;

                try {
                    JSONObject obj = new JSONObject(response);
                    Log.d("Availability Response ", obj.toString());
                    availabilityCheck = obj.get("Success").toString();
                    if (availabilityCheck.equalsIgnoreCase("Available")) {
                        Intent intent = new Intent(getApplicationContext(), CouponCode.class);
                        intent.putExtra("username", name);
                        intent.putExtra("email", email);
                        intent.putExtra("phone", mobile);
                        intent.putExtra("date", date);
                        intent.putExtra("time", time);
                        intent.putExtra("details", object);
                        intent.putExtra("amount", amount);
                        intent.putExtra("customizations", customizationList);
                        startActivity(intent);
                    }

                } catch (Throwable t) {
                    Log.e("Availability Response", "Could not parse Availability response: \"" + t + "\"");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(SEARCH_CODE, searchCode);
                Log.v("searchCode", searchCode);
                params.put(DATE, date);
                Log.v("date", date);
                params.put(TIME, time);
                Log.v("time", time);
                //params.put(STATUS,"1");
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
        //Toast.makeText(getApplicationContext(),stringRequest.toString(),Toast.LENGTH_LONG).show();
        return availabilityCheck;
    }

    /*private void checkCoupon() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Check_COUPON_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(VALUE, "Test123");
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }*/



    /*private void preBook() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, PREBOOK_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String total, bookingRefer, custId;
                Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();
                try {
                    JSONObject res = new JSONObject(response);
                    total = res.getString("total");

                    bookingRefer = res.getString("booking_refer");
                    custId = res.getString("customer_id");
                    Intent intent = new Intent(getApplicationContext(), PaytmMain.class);
                    intent.putExtra("finalAmount", total);
                    intent.putExtra("refer", bookingRefer);
                    intent.putExtra("custId", custId);

                    startActivity(intent);
                } catch (JSONException e) {
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(NAME, name);
                params.put(EMAIL, email);
                params.put(MOBILE, mobile);
                params.put(DATE, date);
                params.put(TIME, time);

                params.put(PREBOOK, "");
                params.put(SEARCH_CODE, searchCode);
                //params.put(CUSTOMISATION,customizations);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }*/


}
