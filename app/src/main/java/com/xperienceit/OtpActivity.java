package com.xperienceit;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Context;
import android.annotation.SuppressLint;
import android.provider.Settings.Secure;
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

import java.util.HashMap;
import java.util.Map;

public class OtpActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String PHONE_NUMBER = "number";
    public static final String Submit = "submit";
    public static final String Random = "random";
    private static final String LOG_IN_URL = "https://xperienceit.in/back/otpLogin.php";
    private EditText etPhone;
    private Button otpButton, fbButton, googleButton;
    @SuppressLint("HardwareIds")
    private String Android_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);
        Android_id = Secure.getString(getApplicationContext().getContentResolver(), Secure.ANDROID_ID);
        etPhone = (EditText) findViewById(R.id.phone);
        //signup=(Button) findViewById(R.id.signup);
        otpButton = (Button) findViewById(R.id.get_Otp);
        fbButton = (Button) findViewById(R.id.fb_login);
        googleButton = (Button) findViewById(R.id.google_login);
        //signup.setOnClickListener(this);
        otpButton.setOnClickListener(this);
        fbButton.setOnClickListener(this);
        googleButton.setOnClickListener(this);
    }

    private void getOtp() {
        final String phone_Number = etPhone.getText().toString().trim();
        //final String password=etPassword.getText().toString().trim();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, LOG_IN_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();
                Intent i = new Intent(getBaseContext(), SignInActivity.class);
                i.putExtra("RANDOM", Android_id);
                i.putExtra("PHONE_NUMBER", phone_Number);
                startActivity(i);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(PHONE_NUMBER, phone_Number);
                params.put(Random, Android_id);
                params.put(Submit, "");
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    @Override
    public void onClick(View view) {
        if (view == otpButton) {
            getOtp();
        } else if (view == fbButton) {
            Intent i = new Intent(getApplicationContext(), FbLogin.class);
            startActivity(i);
        } else if (view == googleButton) {
            Intent i = new Intent(getApplicationContext(), GoogleLogin.class);
            startActivity(i);
        }
    }
}