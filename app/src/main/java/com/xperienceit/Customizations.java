package com.xperienceit;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


public class Customizations extends AppCompatActivity {
    private String CUSTOMIZATION_URL = "https://xperienceit.in/back/getResponse.php?table=features&where=1";
    private String PHOTO_URL = "https://xperienceit.in/uploads/images/";
    private XperienceObject object;
    private ArrayList<Integer> ids;
    private ArrayList<HashMap<String, String>> customizationList;
    private CustomizationsAdapter adapter;
    private LinearLayoutManager llm;
    private RecyclerView recyclerView;
    private Button continueButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customizations);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.activity_customizations);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        if (!isNetworkAvailable()) {
            networkNotAvailable();
        }

        customizationList = new ArrayList<>();
        object = getIntent().getParcelableExtra("details");
        if (!object.getCustomisations().isEmpty()) {
            ids = splitString(object.getCustomisations());

            getCustomizations();


            recyclerView = findViewById(R.id.recyclerview);
            llm = new LinearLayoutManager(getApplicationContext());
            llm.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(llm);


        } else {
            ProgressBar progressBar = findViewById(R.id.item_progress_bar);
            progressBar.setVisibility(View.GONE);
            TextView textView = findViewById(R.id.no_customizations);
            textView.setVisibility(View.VISIBLE);
        }

        continueButton = findViewById(R.id.continue_to_checkout);
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), CheckoutPage.class);
                intent.putExtra("customizations", customizationList);
                intent.putExtra("details", object);
                startActivity(intent);
            }
        });


    }

    public void getCustomizations() {

        RequestQueue queue = MySingleton.getInstance(this.getApplicationContext()).
                getRequestQueue();
        JsonArrayRequest jsObjRequest = new JsonArrayRequest
                (Request.Method.GET, CUSTOMIZATION_URL, null, new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        try {


                            for (int i = 0; i < response.length(); i++) {

                                JSONObject customizationObject = response.getJSONObject(i);
                                //Log.v("json object", String.valueOf(customizationObject));
                                int id = Integer.parseInt(customizationObject.getString("id"));
                                if (ids.contains(id)) {
                                    if (customizationObject.getString("status").equalsIgnoreCase("1")) {
                                        String name = customizationObject.getString("name");
                                        String photo = customizationObject.getString("photo");
                                        String desc = customizationObject.getString("description");
                                        String price = customizationObject.getString("selling_price");
                                        String ids = customizationObject.getString("id");
                                        //Log.v("photo",photo);
                                        photo = PHOTO_URL + photo;
                                        HashMap<String, String> temp = new HashMap<>();
                                        temp.put("name", name);
                                        temp.put("photo", photo);
                                        temp.put("description", desc);
                                        temp.put("price", price);
                                        temp.put("switch", "false");
                                        temp.put("id", ids);
                                        //Log.v("samples", String.valueOf(temp));
                                        customizationList.add(temp);
                                    }


                                }

                                //Log.v("categories", String.valueOf(categories));
                            }


                        } catch (final JSONException e) {

                        } finally {
                            ProgressBar progressBar = findViewById(R.id.item_progress_bar);
                            progressBar.setVisibility(View.GONE);
                            if (!customizationList.isEmpty()) {
                                adapter = new CustomizationsAdapter(getApplicationContext(), customizationList);
                                recyclerView.setAdapter(adapter);


                            } else {
                                TextView textView = findViewById(R.id.no_customizations);
                                textView.setVisibility(View.VISIBLE);
                            }


                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.v("cant get customizations", error.toString());
                        Toast.makeText(getApplicationContext(), "Sorry Couldn't fetch data", Toast.LENGTH_SHORT).show();

                    }
                });
        queue.add(jsObjRequest);

    }

    public ArrayList<Integer> splitString(String str) {
        String[] str1 = str.split(",");
        ArrayList<Integer> array = new ArrayList<>();
        int i = 0;
        for (String s : str1) {
            array.add(Integer.parseInt(s.trim()));
        }
        return array;
    }

    private void networkNotAvailable() {
        Toast.makeText(getApplicationContext(), "Sorry Couldn't fetch data", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(getApplicationContext(), NetworkUnavailable.class));
        this.finish();

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
