package com.xperienceit;

import android.content.Context;
import android.content.SharedPreferences;

import com.xperienceit.R;


public class PreferenceManager {
    private SharedPreferences sharedPreferences;
    private Context context;

    public PreferenceManager(Context context) {
        this.context = context;
        getSharedPreference();
    }

    private void getSharedPreference() {
        sharedPreferences = context.getSharedPreferences(context.getString(R.string.my_preference), context.MODE_PRIVATE);
    }

    public void writePreference() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(context.getString(R.string.my_preference_key), "INIT_OK");
        editor.commit();
    }

    public boolean checkPreference() {
        boolean status = false;
        if (sharedPreferences.getString(context.getString(R.string.my_preference_key), "null").equals("null")) {
            status = false;
        } else {
            status = true;
        }
        return status;
    }

    public boolean checkCityPreference() {
        boolean status;
        if (sharedPreferences.getString(context.getString(R.string.city_preference),
                "City").equals("City")) {
            status = false;
        } else {
            status = true;
        }

        return status;
    }

    public String getCityPreference() {
        return sharedPreferences.getString(context.getString(R.string.city_preference), "City");
    }

    public void writeCityPreference(String city) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(context.getString(R.string.city_preference), city);
        editor.apply();
    }

    public void clearPreference() {

        sharedPreferences.edit().clear().commit();
    }
}
