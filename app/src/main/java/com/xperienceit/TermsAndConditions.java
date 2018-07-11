package com.xperienceit;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.webkit.WebView;

public class TermsAndConditions extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getSupportActionBar().hide();

        String name = getIntent().getStringExtra("name");
        WebView webview = new WebView(this);
        setContentView(webview);

        if (name.equals("terms_n_condition"))
            webview.loadUrl("https://xperienceit.in/terms_condition.php");
        else if (name.equals("privacy_policy"))
            webview.loadUrl("https://xperienceit.in/privacy_policy.php");
    }
}
