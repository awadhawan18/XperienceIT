package com.xperienceit;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.xperienceit.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;

public class XperiencesListActivity extends AppCompatActivity {
    private ArrayList<XperienceObject> xperiences_list;
    private String PACKAGE_URL = "https://xperienceit.in/back/getResponse.php?table=packages&where=category%20LIKE%20";
    private String PHOTO_URL = "https://xperienceit.in/uploads/images/";
    private RecyclerViewAdapter adapter;
    private String XPERIENCE_NAME;
    private LinearLayoutManager llm;
    private RecyclerView recyclerView;
    private PreferenceManager pf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xperiences_list);
        XPERIENCE_NAME = getIntent().getStringExtra("name");
        Log.v("xperience name", XPERIENCE_NAME);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(XPERIENCE_NAME);
        setSupportActionBar(toolbar);

        pf = new PreferenceManager(this);

        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        } catch (NullPointerException error) {
            Log.e("action bar error", error.toString());
        }


        recyclerView = findViewById(R.id.recyclerview);
        llm = new LinearLayoutManager(getApplicationContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(),
                DividerItemDecoration.VERTICAL));

        xperiences_list = new ArrayList<>();
        Log.v("encoded", addCity(getPACKAGE_URL(XPERIENCE_NAME), pf.getCityPreference()));

        if (!isNetworkAvailable()) {
            networkNotAvailable();
        } else if (pf.checkCityPreference()) {
            getXperienceList(addCity(getPACKAGE_URL(XPERIENCE_NAME), pf.getCityPreference()));
        } else {
            getXperienceList(getPACKAGE_URL(XPERIENCE_NAME));
        }


    }

    private void getXperienceList(String url) {

        RequestQueue queue = MySingleton.getInstance(XperiencesListActivity.this).getRequestQueue();
        JsonArrayRequest jsObjRequest = new JsonArrayRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        try {

                            for (int i = 0; i < response.length(); i++) {
                                //Toast.makeText(getApplicationContext(),response.getJSONObject(i).toString(),Toast.LENGTH_LONG);
                                JSONObject xperienceListObject = response.getJSONObject(i);
                                //Toast.makeText(getApplicationContext(), xperienceListObject.toString(), Toast.LENGTH_LONG);
                                XperienceObject object = new XperienceObject(xperienceListObject);
                                if (object.getStatusCode().equalsIgnoreCase("1")) {
                                    xperiences_list.add(object);
                                }

                            }

                        } catch (final JSONException e) {

                        } finally {
                            populateLayout();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.v("error", error.toString());
                        Toast.makeText(getApplicationContext(), "Sorry Couldn't fetch data", Toast.LENGTH_SHORT).show();
                        populateLayout();
                    }
                });
        queue.add(jsObjRequest);
    }

    private void networkNotAvailable() {
        Toast.makeText(getApplicationContext(), "Sorry Couldn't fetch data", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(getApplicationContext(), NetworkUnavailable.class));

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void populateLayout() {
        ProgressBar progressBar = findViewById(R.id.item_progress_bar);
        progressBar.setVisibility(View.GONE);
        if (!xperiences_list.isEmpty()) {
            adapter = new RecyclerViewAdapter(getApplicationContext(), xperiences_list);
            recyclerView.setAdapter(adapter);
        } else {
            TextView noXperiences = findViewById(R.id.no_xperiences);
            noXperiences.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public String getPACKAGE_URL(String packageName) {

        return PACKAGE_URL + "%27%25" + packageName.toLowerCase().replace(" ", "%20") + "%25%27";


    }

    public String addCity(String url, String city) {
        return url + URLEncoder.encode(" and `city` like '%\"" + city + "\"%'");
    }


}
