package com.xperienceit;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class UserProfile extends AppCompatActivity implements View.OnClickListener {
    TextView username, phone, email;
    Button otp, google, fb, logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        phone = (TextView) findViewById(R.id.phone);
        username = (TextView) findViewById(R.id.name);
        email = (TextView) findViewById(R.id.email);
        logout = (Button) findViewById(R.id.logout);
        User user = new User(UserProfile.this);
        email.setText(user.getEmail());
        phone.setText(user.getPhone());
        username.setText(user.getName());
        otp = (Button) findViewById(R.id.get_Otp);
        fb = (Button) findViewById(R.id.fb_login);
        google = (Button) findViewById(R.id.google_login);
        otp.setOnClickListener((View.OnClickListener) this);
        google.setOnClickListener((View.OnClickListener) this);
        fb.setOnClickListener((View.OnClickListener) this);
        logout.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == otp)
            startActivity(new Intent(getApplicationContext(), OtpActivity.class));
        if (view == google)
            startActivity(new Intent(getApplicationContext(), GoogleLogin.class));
        if (view == fb)
            startActivity(new Intent(getApplicationContext(), FbLogin.class));
        if (view == logout) {
            FbLogin fbLogin = new FbLogin();
            fbLogin.logOut();
            //GoogleLogin googleLogin=new GoogleLogin();
            //googleLogin.signout();
            User user = new User(UserProfile.this);
            user.remove();
            email.setText("");
            phone.setText("");
            username.setText("");
        }
    }
}
