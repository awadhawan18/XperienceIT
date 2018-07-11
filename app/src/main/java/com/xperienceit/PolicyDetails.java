package com.xperienceit;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class PolicyDetails extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_policy);

        String name = getIntent().getStringExtra("name");
        String details = getIntent().getStringExtra("details");

        try {
            getSupportActionBar().setTitle(name);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        } catch (NullPointerException e) {
            Log.e("Policy Details", e.toString());
        }

        String circle = "\u25CF";

        CardView policyCard = findViewById(R.id.description_card);
        TextView policyCardText = policyCard.findViewById(R.id.description_text);
        TextView polictyCardTitle = policyCard.findViewById(R.id.card_title);
        polictyCardTitle.setText(name);
        policyCardText.setText(circle.concat(" " + details.
                replaceAll(System.lineSeparator(), "\n" + circle + " ")));


    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
