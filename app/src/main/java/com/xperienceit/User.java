package com.xperienceit;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by kshitiz on 17/3/18.
 */

public class User {
    SharedPreferences sharedPreferences;
    Context context;
    private String name;
    private String email;
    private String phone;

    private String userId;

    public User(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
    }

    public String getUserId() {
        userId = sharedPreferences.getString("userId", "");
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
        sharedPreferences.edit().putString("userId", userId).commit();
    }

    public String getEmail() {
        email = sharedPreferences.getString("email", "");
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
        sharedPreferences.edit().putString("email", email).commit();
    }

    public void remove() {
        sharedPreferences.edit().clear().commit();
    }

    public String getName() {
        name = sharedPreferences.getString("username", "");
        return name;
    }

    public void setName(String name) {
        this.name = name;
        sharedPreferences.edit().putString("username", name).commit();
    }

    public String getPhone() {
        phone = sharedPreferences.getString("phone", "");
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
        sharedPreferences.edit().putString("phone", phone).commit();
    }
}
