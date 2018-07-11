package com.xperienceit;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class CouponCode extends AppCompatActivity implements View.OnClickListener {
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
    private static final String PREBOOK_URL = "https://xperienceit.in/back/prebookapi.php";
    String date, time, name, email, mobile, searchCode, coupon;
    String discount, type, minAmount, amount, totalAmount;
    String[] customisations;
    XperienceObject object;
    int disc;
    private EditText editText;
    private Button button;
    private ArrayList<HashMap<String, String>> customizationList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coupon_code);
        editText = (EditText) findViewById(R.id.coupon);
        button = (Button) findViewById(R.id.get_coupon);

        customizationList = (ArrayList<HashMap<String, String>>) getIntent().getSerializableExtra("customizations");

        //customisations=customizationList.toArray(new String[customizationList.size()]);
        name = getIntent().getStringExtra("username");
        email = getIntent().getStringExtra("email");
        mobile = getIntent().getStringExtra("phone");
        date = getIntent().getStringExtra("date");
        time = getIntent().getStringExtra("time");
        amount = getIntent().getStringExtra("amount");
        object = getIntent().getParcelableExtra("details");
        searchCode = object.getSearchCode();

        button.setOnClickListener((View.OnClickListener) this);

    }

    private void preBook() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, PREBOOK_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.v("Prebook Response", response);
                String total, bookingRefer, custId;
                //Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();
                try {
                    JSONObject res = new JSONObject(response);
                    total = res.getString("total");
                    bookingRefer = res.getString("booking_refer");
                    custId = res.getString("customer_id");
                    Intent intent = new Intent(getApplicationContext(), PaytmMain1.class);
                    intent.putExtra("finalAmount", total);
                    intent.putExtra("refer", bookingRefer);
                    intent.putExtra("custId", custId);
                    intent.putExtra("details", object);
                    intent.putExtra(NAME, name);
                    intent.putExtra(EMAIL, email);
                    intent.putExtra(MOBILE, mobile);
                    intent.putExtra(DATE, date);
                    intent.putExtra(TIME, time);
                    Toast.makeText(getApplicationContext(), String.valueOf(total), Toast.LENGTH_SHORT).show();

                    intent.putExtra("amount", amount);
                    intent.putExtra("customizations", customizationList);
                    startActivity(intent);
                } catch (JSONException e) {
                    Log.e("Json prebook exception", e.toString());
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Coupon class error", Toast.LENGTH_SHORT).show();
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
                for (HashMap<String, String> map : customizationList) {
                    if (map.get("switch").equals("true")) {
                        String id = map.get("id");
                        String name = map.get("name");
                        String custom = "customisation[" + id + "]";
                        params.put(custom, name);
                        Log.v("customization Id", id);
                    }

                }
                Log.v("name", name);
                Log.v("email", email);
                Log.v("mobile", mobile);
                Log.v("date", date);
                Log.v("time", time);
                Log.v("prebook", "");
                Log.v("search_code", searchCode);

                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    private void checkCoupon() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Check_COUPON_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();
                Log.v("Coupon Response", response);
                try {
                    JSONObject obj = new JSONObject(response);
                    String temp, maxDiscount;
                    int amnt;
                    if (obj.has("discount")) {
                        //Toast.makeText(getApplicationContext(),"Discount",Toast.LENGTH_LONG).show();
                        discount = obj.getString("discount");
                        type = obj.getString("type");
                        maxDiscount = obj.getString("max");

                        if (type == "0") {
                            disc = Integer.parseInt(discount);
                        }
                        if (type == "1") {
                            disc = (Integer.parseInt(amount) / 100) * disc;
                            if (Integer.parseInt(amount) > Integer.parseInt(maxDiscount)) {
                                disc = Integer.parseInt(maxDiscount);
                            }
                        }
                        amnt = Integer.parseInt(amount) - disc;
                        totalAmount = String.valueOf(amnt);
                        //preBook();
                    }

                } catch (Throwable t) {
                    Log.e("Availability Response", "Could not parse Availability response: \"" + t + "\"");
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Coupon class error", Toast.LENGTH_SHORT).show();
                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(VALUE, coupon);
                Log.v("value", coupon);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    @Override
    public void onClick(View view) {
        if (view == button) {
            preBook();
            coupon = editText.getText().toString();

        }
    }
}
