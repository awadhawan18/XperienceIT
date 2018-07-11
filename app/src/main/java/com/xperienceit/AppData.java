package com.xperienceit;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class AppData {
    SharedPreferences sharedPreferences;
    Context context;
    private String appExitCount = "0";
    private int count;
    private int appExitCount1 = 0;
    private int i = 0;
    private String date;
    private String time;
    private String appExit = "0";

    public AppData(Context context) {
        this.context = context;
        appExitCount = "0";
        appExit = "0";
        sharedPreferences = context.getSharedPreferences("appData", Context.MODE_PRIVATE);
    }

    public void setToZero() {
        sharedPreferences.edit().putString("appExit", "0").commit();
    }

    public String getAppExit() {
        this.appExit = sharedPreferences.getString("appExit", appExit);
        return appExit;
    }

    public void setAppExit() {
        int count;
        String temp;
        count = Integer.parseInt(appExit);
        count++;
        temp = String.valueOf(count);
        sharedPreferences.edit().putString("appExit", temp).commit();
    }

    public int getAppExitCount1() {
        i = sharedPreferences.getInt("appExitCount1", -1);
        return i;
    }

    public void setAppExitCount1() {
        i++;
        sharedPreferences.edit().putInt("appExitCount1", i).commit();
    }

    public String getTime() {
        time = sharedPreferences.getString("time", "");
        return time;
    }

    public void setTime(String time) {
        this.time = time;
        sharedPreferences.edit().putString("time", time).commit();
    }


    public void remove() {
        sharedPreferences.edit().clear().commit();
    }

    public void rem() {
        sharedPreferences.edit().clear().commit();
    }

    public String getDate() {
        this.date = sharedPreferences.getString("date", "");
        return date;
    }

    public void setDate(String date) {
        this.date = date;
        sharedPreferences.edit().putString("date", date).commit();
    }

    public String getAppExitCount() {
        this.appExitCount = sharedPreferences.getString("appExitCount", "");
        return (appExitCount);
    }

    public void setAppExitCount() {
        int count;
        String temp;
        count = Integer.parseInt(appExitCount);
        count++;
        temp = String.valueOf(count);
        sharedPreferences.edit().putString("appExitCount", temp).commit();
    }

}
