package com.xperienceit;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SearchBarActivity extends AppCompatActivity {
    private RequestQueue queue;
    private SearchView searchBar;
    private String SEARCH_URL = "https://xperienceit.in/back/searchService.php";
    private ArrayList<XperienceObject> searches_list;
    private LinearLayoutManager llm;
    private RecyclerView recyclerView;
    private RecyclerViewAdapter adapter;
    private ProgressBar progressBar;
    private TextView unavailable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_bar);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        queue = MySingleton.getInstance(getApplicationContext()).getRequestQueue();

        recyclerView = findViewById(R.id.recyclerview);
        llm = new LinearLayoutManager(getApplicationContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(),
                DividerItemDecoration.VERTICAL));

        String query = getIntent().getExtras().getString("query");

        searchBar = findViewById(R.id.searchView);
        progressBar = findViewById(R.id.item_progress_bar);
        unavailable = findViewById(R.id.no_xperiences);

        if (!isNetworkAvailable()) {
            networkNotAvailable();
        }


        searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchBar.clearFocus();
                progressBar.setVisibility(View.VISIBLE);
                unavailable.setVisibility(View.GONE);
                postSearchQuery(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });


        searchBar.setQuery(query, true);
        searchBar.setIconified(false);
        searchBar.clearFocus();


    }

    public void postSearchQuery(final String query) {
        try {
            InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            in.hideSoftInputFromWindow(searchBar.getWindowToken(), 0);
        } catch (NullPointerException error) {
            Log.e("keyboard hide error", error.toString());
        }

        searches_list = new ArrayList<>();
        StringRequest sr = new StringRequest(Request.Method.POST, SEARCH_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.v("search result", response);


                try {
                    JSONArray jsonArray = new JSONArray(response);
                    Log.v("search json", jsonArray.toString());

                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject xperienceListObject = jsonArray.getJSONObject(i);
                        XperienceObject object = new XperienceObject(xperienceListObject);
                        searches_list.add(object);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    populateLayout();
                }

            }


        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("volley error", error.toString());
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("search", query);

                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }
        };
        queue.add(sr);
    }

    private void populateLayout() {
        progressBar.setVisibility(View.GONE);
        if (!searches_list.isEmpty()) {
            unavailable.setVisibility(View.GONE);
            adapter = new RecyclerViewAdapter(getApplicationContext(), searches_list);
            recyclerView.setAdapter(adapter);
        } else {
            adapter = new RecyclerViewAdapter(getApplicationContext(), searches_list);
            recyclerView.setAdapter(adapter);
            unavailable.setVisibility(View.VISIBLE);
        }

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void networkNotAvailable() {
        Toast.makeText(getApplicationContext(), "Sorry Couldn't fetch data", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(getApplicationContext(), NetworkUnavailable.class));

    }

}
