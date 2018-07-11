package com.xperienceit;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.xperienceit.R;

public class NetworkUnavailable extends AppCompatActivity {
    private TextView noNetwork;
    private ProgressBar progressBar;
    private Button checkNetwork;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network_unavailable);

        noNetwork = findViewById(R.id.no_network);
        progressBar = findViewById(R.id.progress_bar);
        checkNetwork = findViewById(R.id.check_network);

        checkNetwork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                noNetwork.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                if (isNetworkAvailable()) {
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();
                } else {
                    noNetwork.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(), "Network not Available", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
