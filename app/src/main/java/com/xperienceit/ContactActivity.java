package com.xperienceit;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.xperienceit.R;

public class ContactActivity extends AppCompatActivity {
    private LinearLayout callUs, emailUs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        try {
            getSupportActionBar().setTitle(getResources().getString(R.string.contact_us));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        } catch (NullPointerException e) {
            Log.e("Contact Activity", e.toString());
        }


        callUs = findViewById(R.id.contact_phone);
        emailUs = findViewById(R.id.contact_email);

        callUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = getResources().getString(R.string.contact_phone);
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
                startActivity(intent);
            }
        });

        emailUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto", getResources().getString(R.string.contact_email), null));
                startActivity(Intent.createChooser(emailIntent, "Send email..."));
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
