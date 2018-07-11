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

import java.util.HashMap;
import java.util.Map;

public class SignInActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String PHONE_NUMBER = "phone_no";
    public static final String OTP = "otp";
    public static final String Random = "random";
    public static final String Submit = "submit";
    private static final String OTP_URL = "https://xperienceit.in/back/otpLogin.php";
    String Android_id;
    String phone_Number;
    private EditText etOtp;
    private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        Bundle extras;
        extras = getIntent().getExtras();
        if (extras != null) {
            phone_Number = extras.getString("PHONE_NUMBER");
            Android_id = extras.getString("RANDOM");
        }
        etOtp = (EditText) findViewById(R.id.otp);
        //signup=(Button) findViewById(R.id.signup);
        loginButton = (Button) findViewById(R.id.login);
        //signup.setOnClickListener(this);
        loginButton.setOnClickListener(this);
    }

    public void loginUser() {
        final String otp = etOtp.getText().toString().trim();
        if (otp.toString().length() == 0) {
            Toast.makeText(getApplicationContext(), "Enter OTP", Toast.LENGTH_LONG).show();
        } else if (otp.toString().length() < 6 && otp.toString().length() > 0) {
            Toast.makeText(getApplicationContext(), "Enter a 6 digit Otp", Toast.LENGTH_LONG).show();
        } else {

            StringRequest stringRequest = new StringRequest(Request.Method.POST, OTP_URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    String userId, phone;
                    //Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();
                    try {
                        JSONObject res = new JSONObject(response);
                        if (res.has("id")) {
                            Toast.makeText(getApplicationContext(), "Login Success.", Toast.LENGTH_LONG).show();
                            userId = res.getString("id");
                            phone = res.getString("phone");
                            User user = new User(SignInActivity.this);
                            user.setUserId(userId);
                            user.setPhone(phone);
                        }
                    } catch (JSONException e) {
                        Log.e("Json prebook exception", e.toString());
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
                    params.put(PHONE_NUMBER, phone_Number);
                    params.put(Random, Android_id);
                    params.put(OTP, otp);
                    params.put(Submit, "");
                    return params;
                }
            };
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(stringRequest);
        }
    }

    @Override
    public void onClick(View view) {
        if (view == loginButton) {
            loginUser();
        }
    }
}