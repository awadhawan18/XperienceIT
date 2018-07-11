package com.xperienceit;

import android.annotation.SuppressLint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

//package bootcamp.android.demoapp;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class Test extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        AppData appData = new AppData(Test.this);
        //Toast.makeText(this,,Toast.LENGTH_LONG).show();
        //Toast.makeText(this,getCurrentTime(),Toast.LENGTH_LONG).show();

    }


    @Override
    protected void onDestroy() {
        //if (appData.getDate().equalsIgnoreCase("")) {
        String lastTime, currentTime, strCount;
        int count;
        AppData appData = new AppData(Test.this);
        //Toast.makeText(this,"ondestroy ",Toast.LENGTH_LONG).show();
        if (!getCurrentDate().equals(appData.getDate())) {
            appData.setDate(getCurrentDate());
            appData.setTime(getCurrentTime());
            Toast.makeText(this, "first if ", Toast.LENGTH_LONG).show();
        }
        if (appData.getDate().equalsIgnoreCase(getCurrentDate())) {
            lastTime = appData.getTime();
            currentTime = getCurrentTime();
            Toast.makeText(this, "last time " + lastTime, Toast.LENGTH_LONG).show();
            Toast.makeText(this, "current time " + currentTime, Toast.LENGTH_LONG).show();
            if (checkHours(lastTime, currentTime) <= 1) {
                strCount = appData.getAppExit();
                count = Integer.parseInt(strCount);
                Toast.makeText(this, Integer.toString(count), Toast.LENGTH_LONG).show();
                if (count >= 2) {
                    appData.rem();
                    appData.setToZero();
                    notifyOnAppExit();
                    //Toast.makeText(this,"Appexit "+appData.getAppExitCount(),Toast.LENGTH_LONG).show();
                }
                appData.setAppExit();
                //Toast.makeText(this,"Appexit "+appData.getAppExitCount(),Toast.LENGTH_LONG).show();
                appData.setTime(getCurrentTime());
            } else {
                appData.setDate(getCurrentDate());
                appData.setTime(getCurrentTime());
            }

        }
        super.onDestroy();
    }

    public int checkHours(String lastTime, String currentTime) {
        //lastTime="17:04:00";
        //currentTime="18:03:00";
        java.text.DateFormat df = new java.text.SimpleDateFormat("hh:mm:ss");
        Date date1 = null;
        try {
            date1 = df.parse(lastTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date date2 = null;
        try {
            date2 = df.parse(currentTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long diff = date2.getTime() - date1.getTime();
        int timeInSeconds = (int) diff / 1000;
        int hours, minutes;
        hours = timeInSeconds / 3600;
        timeInSeconds = timeInSeconds - (hours * 3600);
        minutes = timeInSeconds / 60;

        //Toast.makeText(this,"HOURS "+hours,Toast.LENGTH_LONG).show();
        //Toast.makeText(this,"Minutes "+minutes,Toast.LENGTH_LONG).show();
        return hours;
    }

    public String getCurrentTime() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat mdformat = new SimpleDateFormat("HH:mm:ss");
        String strDate = mdformat.format(calendar.getTime());
        return strDate;
    }

    public String getCurrentDate() {
        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        return date;
    }

    public void notifyOnAppExit() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Intent notificationIntent = new Intent(this, Notifications.class);
        notificationIntent.putExtra("Type", "app exit");
        PendingIntent broadcast = PendingIntent.getBroadcast(this, 100, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.SECOND, 5);

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), broadcast);
    }
}
