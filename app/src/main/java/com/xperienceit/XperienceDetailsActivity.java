package com.xperienceit;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;

public class XperienceDetailsActivity extends AppCompatActivity implements OnMapReadyCallback {
    private XperienceObject object;
    ImageListener imageListener = new ImageListener() {
        @Override
        public void setImageForPosition(int position, ImageView imageView) {
            Picasso.with(getApplicationContext()).load(object.getPhotoUrl().get(position)).resize(800, 0).into(imageView);
        }
    };
    private CarouselView carouselView;
    private Button bookNow;
    private String circle = "\u25CF";
    private GoogleMap mMap;
    private double latitude, longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xperience_details);
        object = getIntent().getParcelableExtra("details");

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(object.getName());
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        carouselView = findViewById(R.id.carouselView);
        carouselView.setImageListener(imageListener);
        carouselView.setPageCount(object.getPhotoUrl().size());

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        if (object.getLatitude() != 0) {
            latitude = object.getLatitude();
            longitude = object.getLongitude();
            mapFragment.setUserVisibleHint(false);
            mapFragment.getMapAsync(this);
        } else {
            mapFragment.getView().setVisibility(View.GONE);
        }

        CardView xperienceName = findViewById(R.id.xperience_name);
        TextView xperienceNameText = xperienceName.findViewById(R.id.description_text);
        xperienceNameText.setText(object.getName());

        CardView smallDescription = findViewById(R.id.small_description_card);
        TextView smallDescriptionText = smallDescription.findViewById(R.id.description_text);
        TextView smallDescriptionTitle = smallDescription.findViewById(R.id.card_title);
        smallDescriptionTitle.setText("Xperience in Brief");
        smallDescriptionText.setText(object.getSmallDescription());

        CardView description = findViewById(R.id.description_card);
        TextView descriptionText = description.findViewById(R.id.description_text);
        TextView longDescriptionTitle = description.findViewById(R.id.card_title);
        longDescriptionTitle.setText("Xperience Details");
        descriptionText.setText(object.getDescription());

        CardView include = findViewById(R.id.included_items_card);
        TextView includedText = include.findViewById(R.id.included_items_text);
        includedText.setText(circle.concat(" " + object.getInclude().replaceAll(System.lineSeparator(), "\n" + circle + " ")));

        CardView exclude = findViewById(R.id.excluded_items_card);
        TextView excludedText = exclude.findViewById(R.id.excluded_items_text);
        excludedText.setText(circle.concat(" " + object.getExclude().replaceAll(System.lineSeparator(), "\n" + circle + " ")));

        CardView specialFeatures = findViewById(R.id.special_features);
        TextView specialFeaturesText = specialFeatures.findViewById(R.id.description_text);
        TextView specialFeaturesTitle = specialFeatures.findViewById(R.id.card_title);
        specialFeaturesTitle.setText("Points To Note");
        specialFeaturesText.setText(circle.concat(" " + object.getSpecialFeatures().
                replaceAll(System.lineSeparator(), "\n" + circle + " ")));


        CardView aboutDeals = findViewById(R.id.about_deals_card);
        TextView persons = aboutDeals.findViewById(R.id.persons);
        TextView priceText = aboutDeals.findViewById(R.id.price);
        TextView strikePrice = aboutDeals.findViewById(R.id.strike_price);
        TextView city = aboutDeals.findViewById(R.id.city);
        TextView address = aboutDeals.findViewById(R.id.address);

        setPrice(priceText, strikePrice);


        String cities = object.getCity().replaceAll("^\"|\"$", " ");

        while (cities.contains("[") || cities.contains("]")) {
            cities = cities.replaceAll("[\\[\\]^\"|\"$]", " ");
        }

        city.setText(cities);
        address.setText(object.getAddress());
        persons.setText(object.getPersons());

        CardView tncCard = findViewById(R.id.terms_n_conditions);
        TextView tncText = tncCard.findViewById(R.id.policy_text);
        tncText.setText(R.string.terms_and_conditions);
        tncCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), PolicyDetails.class);
                intent.putExtra("name", getResources().getString(R.string.terms_and_conditions));
                intent.putExtra("details", object.getTermsConditions());
                startActivity(intent);
            }
        });

        CardView cancellationCard = findViewById(R.id.cancellation_card);
        TextView cancelText = cancellationCard.findViewById(R.id.policy_text);
        cancelText.setText(R.string.cancellation_policy);
        cancellationCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), PolicyDetails.class);
                intent.putExtra("name", getResources().getString(R.string.cancellation_policy));
                intent.putExtra("details", object.getCancelPolicy());
                startActivity(intent);
            }
        });

        CardView refundCard = findViewById(R.id.privacy_card);
        TextView refundCardTitle = refundCard.findViewById(R.id.policy_text);
        refundCardTitle.setText(R.string.refund_policy);
        refundCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), PolicyDetails.class);
                intent.putExtra("name", getResources().getString(R.string.refund_policy));
                intent.putExtra("details", object.getRefundPolicy());
                startActivity(intent);
            }
        });

        bookNow = findViewById(R.id.book_now);
        bookNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Customizations.class);
                intent.putExtra("details", object);
                startActivity(intent);
            }
        });


    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Venue Location")).showInfoWindow();
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        mMap.getUiSettings().setMapToolbarEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
    }

    public void setPrice(TextView priceView, TextView strikePrice) {
        if (object.getDiscount().equalsIgnoreCase("0")) {
            priceView.setText("Rs." + Helper.getPrice(object.getPrice(), object.getDiscount()));
            strikePrice.setVisibility(View.GONE);
        } else {
            strikePrice.setText("Rs." + Helper.getPrice(object.getPrice(), "0"));
            strikePrice.setPaintFlags(strikePrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            priceView.setText("Rs." + Helper.getPrice(object.getPrice(), object.getDiscount())
                    + " " + object.getDiscount() + "% discount");
        }


    }
}
