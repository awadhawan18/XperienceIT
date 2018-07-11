package com.xperienceit;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.animation.Animation;

import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.squareup.picasso.Picasso;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;
import com.synnapps.carouselview.ViewListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private RelativeLayout dimBackground, fab_menu_background;
    private FloatingActionButton floatingActionButton;
    private Animation fab_open, fab_close, showMenuAnimation, closeMenuAnimation;
    private CarouselView bannerView, trendingDeals, specialOffers;
    private MyGridView myGridView;
    private EditText searchBar;

    private ArrayList<String> bannerList;
    private ArrayList<XperienceObject> trendingList;
    private ArrayList<HashMap<String, String>> categories;
    private ArrayList<XperienceObject> specialOfferList;
    private ArrayList<String> citiesList;

    private String SPECIAL_PACKAGE_URL = "https://www.xperienceit.in/back/getResponse.php?table=packages&where=`hotdeal`=1";
    private String CATEGORY_URL = "https://xperienceit.in/back/getResponse.php?table=category&where=1";
    private String PHOTO_URL = "https://xperienceit.in/uploads/images/";
    private String EVENTHOMEPAGE_URL = "https://xperienceit.in/back/getResponse.php?table=eventhomepage&where=1";
    private String SEARCH_URL = "https://xperienceit.in/back/searchService.php";
    private String CITY_URL = "https://xperienceit.in/back/getResponse.php?table=cities&where=1";
    private NavigationView navigationView;
    private RequestQueue queue;
    private PreferenceManager pf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);
        categories = new ArrayList<>();
        bannerList = new ArrayList<>();
        trendingList = new ArrayList<>();
        specialOfferList = new ArrayList<>();
        citiesList = new ArrayList<>();

        pf = new PreferenceManager(getApplicationContext());
        //inflates toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        //initializes fab menu
        floatingActionButton = (FloatingActionButton) findViewById(R.id.floatingActionButton);

        //inflates custom navigation menu to main activity at bottom right corner
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View childView = inflater.inflate(R.layout.fab_menu_layout, null);
        fab_menu_background = findViewById(R.id.menu_layout);
        fab_menu_background.addView(childView);
        fab_menu_background.getLayoutParams().width = (int) getResources().getDimension(R.dimen.frame_menu_width);

        //dims background when menu opens
        dimBackground = findViewById(R.id.dim_background);
        dimBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeMenu();
            }
        });

        loadAnimations();  //loads opening and closing animations for fab and navigation menu
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMenu();
            }
        });

        //Navigation menu with listener for custom nav menu
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //adds carousel to main activity
        bannerView = findViewById(R.id.banner_view);
        trendingDeals = findViewById(R.id.trending_packages);
        specialOffers = findViewById(R.id.special_offers);

        //custom gridview initializer with adapter
        myGridView = findViewById(R.id.grid_layout);

        queue = MySingleton.getInstance(this.getApplicationContext()).getRequestQueue();

        searchBar = findViewById(R.id.search_bar);
        searchBar.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    Intent intent = new Intent(getApplicationContext(), SearchBarActivity.class);
                    intent.putExtra("query", String.valueOf(searchBar.getText()));
                    startActivity(intent);
                    return true;
                }
                return false;
            }
        });


        if (!isNetworkAvailable()) {
            networkNotAvailable();
        } else {
            if (pf.checkCityPreference()) {
                getEventPage(EVENTHOMEPAGE_URL + "&city=" + pf.getCityPreference());

            } else {
                getEventPage(EVENTHOMEPAGE_URL);
            }

            //getCategories();
            //getSpeciaOffers();

        }


    }

    public void getEventPage(String url) {
        JsonArrayRequest jsObjRequest = new JsonArrayRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            JSONObject object = response.getJSONObject(0);
                            JSONArray banners = object.getJSONArray("banners");
                            JSONArray jsonCategories = object.getJSONArray("categories");
                            JSONArray hotdeal = object.getJSONArray("hotdeal");
                            JSONArray trending = object.getJSONArray("trendingdeal");
                            for (int i = 0; i < banners.length(); i++) {

                                JSONObject bannerObject = banners.getJSONObject(i);
                                String title = bannerObject.getString("title");
                                String photo = bannerObject.getString("image");

                                if (!photo.equals("")) {
                                    //Log.v("photo",photo);
                                    photo = PHOTO_URL + photo;

                                    bannerList.add(photo);
                                }
                            }
                            Log.v("categories length", String.valueOf(jsonCategories.length()));
                            for (int i = 0; i < jsonCategories.length(); i++) {

                                JSONObject categoryObject = jsonCategories.getJSONObject(i);
                                //Log.v("json object", String.valueOf(xperienceObject));
                                String status = categoryObject.getString("status");
                                if (status.equalsIgnoreCase("1")) {
                                    String name = categoryObject.getString("name");
                                    String photo = categoryObject.getString("photo");
                                    photo = stringDecoder(photo);
                                    //Log.v("photo",photo);
                                    photo = PHOTO_URL + photo;
                                    HashMap<String, String> temp = new HashMap<>();
                                    temp.put("name", name);
                                    temp.put("photo", photo);
                                    //Log.v("samples", String.valueOf(temp));
                                    categories.add(temp);
                                    //Log.v("categories", String.valueOf(categories));
                                }

                            }
                            Log.v("hotdeal length", String.valueOf(hotdeal.length()));
                            for (int i = 0; i < hotdeal.length(); i++) {

                                JSONObject hotdealObject = hotdeal.getJSONObject(i);
                                XperienceObject deal = new XperienceObject(hotdealObject);
                                specialOfferList.add(deal);
                            }

                            for (int i = 0; i < trending.length(); i++) {

                                JSONObject trendingObject = trending.getJSONObject(i);
                                XperienceObject deal = new XperienceObject(trendingObject);
                                trendingList.add(deal);
                            }


                        } catch (final JSONException e) {

                        } finally {
                            bannerView.setImageListener(new ImageListener() {
                                @Override
                                public void setImageForPosition(final int position, final ImageView imageView) {

                                    Picasso.with(getApplicationContext()).load(bannerList.get(position)).resize(800, 0).into(imageView);

                                }
                            });
                            bannerView.setPageCount(bannerList.size());

                            if (!categories.isEmpty()) {
                                ProgressBar progressBar = findViewById(R.id.item_progress_bar);
                                progressBar.setVisibility(View.GONE);
                                myGridView.setAdapter(new GridAdaptor(getApplicationContext(), categories));
                                myGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        HashMap<String, String> temp = categories.get(position);
                                        Intent intent = new Intent(getApplicationContext(), XperiencesListActivity.class);
                                        intent.putExtra("name", temp.get("name"));
                                        startActivity(intent);

                                    }
                                });
                            }


                            if (!specialOfferList.isEmpty()) {
                                specialOffers.setViewListener(new ViewListener() {
                                    @Override
                                    public View setViewForPosition(final int position) {
                                        View customView = getLayoutInflater().inflate(R.layout.offers_card, null);
                                        customView.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                XperienceObject temp = specialOfferList.get(position);
                                                Intent intent = new Intent(getApplicationContext(), XperienceDetailsActivity.class);
                                                intent.putExtra("details", temp);
                                                startActivity(intent);
                                            }
                                        });
                                        ImageView imageView = customView.findViewById(R.id.xperience_image);
                                        TextView name = customView.findViewById(R.id.xperience_name);
                                        TextView price = customView.findViewById(R.id.xperience_price);
                                        XperienceObject temp = specialOfferList.get(position);
                                        name.setText(temp.getName());
                                        price.setText("Rs." + temp.getPrice());
                                        //desc.setText(temp.getSmallDescription());
                                        Picasso.with(getApplicationContext()).load(temp.getPhotoUrl().get(0)).resize(800, 0).into(imageView);

                                        return customView;
                                    }
                                });
                                specialOffers.setPageCount(specialOfferList.size());

                            } else {
                                LinearLayout specialOffersFrame = findViewById(R.id.special_offers_frame);
                                specialOffersFrame.setVisibility(View.GONE);
                            }

                            if (!trendingList.isEmpty()) {
                                trendingDeals.setViewListener(new ViewListener() {
                                    @Override
                                    public View setViewForPosition(final int position) {
                                        View customView = getLayoutInflater().inflate(R.layout.offers_card, null);
                                        customView.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                XperienceObject temp = trendingList.get(position);
                                                Intent intent = new Intent(getApplicationContext(), XperienceDetailsActivity.class);
                                                intent.putExtra("details", temp);
                                                startActivity(intent);
                                            }
                                        });

                                        ImageView imageView = customView.findViewById(R.id.xperience_image);
                                        TextView name = customView.findViewById(R.id.xperience_name);
                                        TextView price = customView.findViewById(R.id.xperience_price);
                                        XperienceObject temp = trendingList.get(position);
                                        name.setText(temp.getName());
                                        price.setText("Rs." + temp.getPrice());
                                        //desc.setText(temp.getSmallDescription());
                                        Picasso.with(getApplicationContext()).load(temp.getPhotoUrl().get(0)).resize(800, 0).into(imageView);

                                        return customView;
                                    }
                                });
                                trendingDeals.setPageCount(trendingList.size());

                            } else {
                                LinearLayout trendingPackagesFrame = findViewById(R.id.trending_packages_frame);
                                trendingPackagesFrame.setVisibility(View.GONE);
                            }


                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.v("Error main_activity", error.toString());
                        Toast.makeText(getApplicationContext(), "Sorry Couldn't fetch data", Toast.LENGTH_SHORT).show();
                    }
                });
        queue.add(jsObjRequest);


    }

    /*public void getCategories(){
        JsonArrayRequest jsObjRequest = new JsonArrayRequest
                (Request.Method.GET, CATEGORY_URL, null, new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            for(int i=0;i<response.length();i++){

                                JSONObject xperienceObject = response.getJSONObject(i);
                                //Log.v("json object", String.valueOf(xperienceObject));
                                String name = xperienceObject.getString("name");
                                String photo = xperienceObject.getString("photo");
                                photo = stringDecoder(photo);
                                //Log.v("photo",photo);
                                photo = PHOTO_URL+photo;
                                HashMap<String, String> temp = new HashMap<>();
                                temp.put("name", name);
                                temp.put("photo", photo);
                                //Log.v("samples", String.valueOf(temp));
                                categories.add(temp);
                                //Log.v("categories", String.valueOf(categories));
                            }


                        } catch (final JSONException e) {

                        } finally {
                            ProgressBar progressBar = findViewById(R.id.item_progress_bar);
                            progressBar.setVisibility(View.GONE);
                            myGridView.setAdapter(new GridAdaptor(getApplicationContext(),categories));
                            myGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    HashMap<String, String> temp = categories.get(position);
                                    Intent intent = new Intent(getApplicationContext(),XperiencesListActivity.class);
                                    intent.putExtra("name",temp.get("name"));
                                    startActivity(intent);

                                }
                            });
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.v("Error main_activity",error.toString());
                        Toast.makeText(getApplicationContext(),"Sorry Couldn't fetch data",Toast.LENGTH_SHORT).show();

                    }
                });
        queue.add(jsObjRequest);

    }

    public void getSpeciaOffers(){
        JsonArrayRequest jsObjRequest = new JsonArrayRequest
                (Request.Method.GET, SPECIAL_PACKAGE_URL, null, new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            for(int i=0;i<response.length();i++){

                                JSONObject xperienceListObject = response.getJSONObject(i);
                                XperienceObject object = new XperienceObject(xperienceListObject);
                                specialOfferList.add(object);
                            }


                        } catch (final JSONException e) {

                        } finally {


                            specialOffers.setViewListener(new ViewListener(){
                                @Override
                                public View setViewForPosition(final int position) {
                                    View customView = getLayoutInflater().inflate(R.layout.offers_card, null);
                                    customView.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            XperienceObject temp = specialOfferList.get(position);
                                            Intent intent = new Intent(getApplicationContext(),XperienceDetailsActivity.class);
                                            intent.putExtra("details",temp);
                                            startActivity(intent);
                                        }
                                    });
                                    ImageView imageView = customView.findViewById(R.id.xperience_image);
                                    TextView name = customView.findViewById(R.id.xperience_name);
                                    TextView price = customView.findViewById(R.id.xperience_price);
                                    XperienceObject temp = specialOfferList.get(position);
                                    name.setText(temp.getName());
                                    price.setText("Rs."+temp.getPrice());
                                    //desc.setText(temp.getSmallDescription());
                                    Picasso.with(getApplicationContext()).load(temp.getPhotoUrl().get(0)).resize(800,0).into(imageView);

                                    return customView;
                                }
                            });
                            specialOffers.setPageCount(specialOfferList.size());

                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(),"Sorry Couldn't fetch data",Toast.LENGTH_SHORT).show();

                    }
                });
        queue.add(jsObjRequest);


    }*/


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_select_city, menu);

        MenuItem menuItem = menu.findItem(R.id.select_city);
        menuItem.setTitle(pf.getCityPreference());
        if (isNetworkAvailable()) {
            getCities(menuItem);
        }


        /*menu.add(0, 0, 0, "Delhi")
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);*/
        /*menu.add(0, 1, 0, "Option2").setShortcut('3', 'c');
        menu.add(0, 2, 0, "Option3").setShortcut('4', 's');*/

        /*SubMenu sMenu = menu.addSubMenu(0, 3, 0, "Delhi").setHeaderIcon(R.drawable.city_icon); //If you want to add submenu
        sMenu.add(0, 4, 0, "SubOption1").setShortcut('5', 'z');
        sMenu.add(0, 5, 0, "SubOption2").setShortcut('5', 'z');*/
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case 0:
                return true;
            case 1:
                // code for option2
                return true;
            case 2:
                // code for option3
                return true;
            case 4:
                // code for subOption1
                return true;
            case 5:
                // code for subOption2
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        switch (item.getItemId()) {
            case R.id.user_profile:
                startActivity(new Intent(getApplicationContext(), UserProfile.class));
                return true;
            case R.id.user_cart:
                startActivity(new Intent(getApplicationContext(), Test.class));
                return true;
            case R.id.user_wishlist:
                startActivity(new Intent(getApplicationContext(), WishlistActivity.class));
                return true;
            case R.id.user_coupons:
                return true;
            case R.id.contact_us:
                startActivity(new Intent(getApplicationContext(), ContactActivity.class));
                return true;
            case R.id.terms_n_conditions:
                if (!isNetworkAvailable()) {
                    networkNotAvailable();
                } else {
                    Intent intent = new Intent(getApplicationContext(), TermsAndConditions.class);
                    intent.putExtra("name", "terms_n_condition");
                    startActivity(intent);
                }
                return true;
            case R.id.privacy_policy:
                if (!isNetworkAvailable()) {
                    networkNotAvailable();
                } else {
                    Intent intent = new Intent(getApplicationContext(), TermsAndConditions.class);
                    intent.putExtra("name", "privacy_policy");
                    startActivity(intent);
                }
                return true;
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        searchBar.getText().clear();
        searchBar.clearFocus();
        for (int i = 0; i < navigationView.getMenu().size(); i++) {
            navigationView.getMenu().getItem(i).setChecked(false);
        }
        if (!isNetworkAvailable()) {
            networkNotAvailable();
        }


    }

    @Override
    public void onBackPressed() {
        if (dimBackground.getVisibility() == View.VISIBLE) {
            closeMenu();
        } else {
            this.finish();
        }
    }

    //Opens menu and dims background
    private void openMenu() {
        floatingActionButton.startAnimation(fab_open);
        floatingActionButton.setVisibility(View.INVISIBLE);
        dimBackground.setVisibility(View.VISIBLE);
        fab_menu_background.startAnimation(showMenuAnimation);
        fab_menu_background.setVisibility(View.VISIBLE);

    }

    //closes menu
    private void closeMenu() {
        dimBackground.setVisibility(View.INVISIBLE);
        fab_menu_background.startAnimation(closeMenuAnimation);
        fab_menu_background.setVisibility(View.INVISIBLE);
        floatingActionButton.startAnimation(fab_close);
        floatingActionButton.setVisibility(View.VISIBLE);
    }

    //adds animation to fab and navigation menu
    private void loadAnimations() {
        fab_open = android.view.animation.AnimationUtils.loadAnimation(
                floatingActionButton.getContext(), R.anim.fab_scale_up);
        fab_close = android.view.animation.AnimationUtils.loadAnimation(
                floatingActionButton.getContext(), R.anim.fab_scale_down);
        showMenuAnimation = android.view.animation.AnimationUtils.loadAnimation(
                floatingActionButton.getContext(), R.anim.popup_show);
        closeMenuAnimation = android.view.animation.AnimationUtils.loadAnimation(
                floatingActionButton.getContext(), R.anim.popup_hide);
    }

    private void networkNotAvailable() {

        startActivity(new Intent(getApplicationContext(), NetworkUnavailable.class));
        this.finish();

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    //string decoder for removing [] and " from photo url
    private String stringDecoder(String str) {
        return str.replaceAll("[\\[\\]^\"|\"$]", "");
    }

    public void getCities(final MenuItem menuItem) {
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
                            if (!citiesList.isEmpty()) {
                                Collections.sort(citiesList);
                                SubMenu subMenu = menuItem.getSubMenu();
                                for (String city : citiesList) {
                                    subMenu.add(city).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                                        @Override
                                        public boolean onMenuItemClick(MenuItem item) {
                                            if (!item.toString().equalsIgnoreCase(menuItem.toString())) {
                                                menuItem.setTitle(item.getTitle());
                                                pf.writeCityPreference(item.toString());
                                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                                startActivity(intent);
                                                finish();


                                            }
                                            Log.v("city pref", pf.getCityPreference());
                                            return false;
                                        }
                                    });
                                }
                            }
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