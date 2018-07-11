package com.xperienceit;

import android.content.Intent;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

import java.util.ArrayList;


public class WelcomeActivity extends AppCompatActivity implements View.OnClickListener {
    private ViewPager mPager;
    private MPagerAdapter mPagerAdapter;
    private int[] layouts = {R.layout.first_intro_slide, R.layout.second_intro_slide, R.layout.third_intro_slide,
            R.layout.forth_intro_slide};
    private LinearLayout Dots_Layout;
    private ImageView[] dots;
    private Button bnSkip, bnNext;
    private ArrayList<String> citiesList;
    private String CITY_URL = "https://xperienceit.in/back/getResponse.php?table=cities&where=1";
    private RequestQueue queue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        citiesList = new ArrayList<>();
        queue = MySingleton.getInstance(this.getApplicationContext()).getRequestQueue();
        if (new PreferenceManager(this).checkPreference()) {
            loadHome();
        }
        if (Build.VERSION.SDK_INT >= 19) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        setContentView(R.layout.activity_welcome);
        mPager = (ViewPager) findViewById(R.id.viewPager);
        mPagerAdapter = new MPagerAdapter(layouts, this);
        mPager.setAdapter(mPagerAdapter);
        Dots_Layout = (LinearLayout) findViewById(R.id.dotsLayout);
        createDots(0);
        bnNext = (Button) findViewById(R.id.bnNext);
        bnSkip = (Button) findViewById(R.id.bnSkip);
        bnNext.setOnClickListener(this);
        bnSkip.setOnClickListener(this);


        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                createDots(position);
                if (position == layouts.length - 1) {
                    bnNext.setText("Start");
                    bnSkip.setVisibility(View.INVISIBLE);
                } else {
                    bnNext.setText("Next");
                    bnSkip.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    public void createDots(int currentPosition) {
        if (Dots_Layout != null) {
            Dots_Layout.removeAllViews();
        }
        dots = new ImageView[layouts.length];
        for (int i = 0; i < layouts.length; i++) {
            dots[i] = new ImageView(this);
            if (i == currentPosition) {
                dots[i].setImageDrawable(ContextCompat.getDrawable(this, R.drawable.active_dots));
            } else {
                dots[i].setImageDrawable(ContextCompat.getDrawable(this, R.drawable.default_dots));
            }
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(4, 0, 4, 0);
            Dots_Layout.addView(dots[i], params);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bnNext:
                loadNextSlide();
                break;
            case R.id.bnSkip:
                getCities();
                new PreferenceManager(this).writePreference();
                break;
        }
    }

    private void loadHome() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putStringArrayListExtra("cities", citiesList);
        startActivity(intent);
        finish();
    }

    private void loadNextSlide() {
        int nextSlide = mPager.getCurrentItem() + 1;
        if (nextSlide < layouts.length) {
            mPager.setCurrentItem(nextSlide);
        } else {
            getCities();
            new PreferenceManager(this).writePreference();
        }

    }

    public void getCities() {
        JsonArrayRequest jsObjRequest = new JsonArrayRequest
                (Request.Method.GET, CITY_URL, null, new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            for (int i = 0; i < response.length(); i++) {

                                JSONObject object = response.getJSONObject(i);
                                String city = object.getString("city_name");
                                citiesList.add(city);
                            }


                        } catch (final JSONException e) {

                        } finally {
                            loadHome();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "Sorry Couldn't fetch data", Toast.LENGTH_SHORT).show();

                    }
                });
        queue.add(jsObjRequest);


    }
}
