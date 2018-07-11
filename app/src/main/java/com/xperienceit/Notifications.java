package com.xperienceit;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import static android.app.NotificationManager.IMPORTANCE_DEFAULT;

public class Notifications extends BroadcastReceiver {
    private static final String CHANNEL_ID = "com.xperienceit.channelId";
    String notifintent, contentText, tickerText;

    @Override
    public void onReceive(Context context, Intent intent) {

        Intent notificationIntent = new Intent(context, MainActivity.class);
        notifintent = intent.getStringExtra("Type");
        if (notifintent.equalsIgnoreCase("booking failed")) {
            contentText = "Oops. Booking failed due to some reason! Retry after sometime.";
            tickerText = "Booking Cancelled.";
        }
        if (notifintent.equalsIgnoreCase("booking successful")) {
            contentText = "Booking Successful. Xplore more with XperienceIT !";
            tickerText = "Booking Successful.";
        }
        if (notifintent.equalsIgnoreCase("app exit")) {
            contentText = "Dont wait. Get your Xclusive Deal with XperienceIT";
            tickerText = "Check Deals for you.";
        }

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(notificationIntent);

        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);


        Notification notification = builder.setContentTitle("XperienceIT")
                .setContentText(contentText)
                .setStyle(new NotificationCompat.BigTextStyle(builder).bigText(contentText))
                .setTicker(tickerText)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent).build();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(CHANNEL_ID);
        }

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "XperienceIT",
                    IMPORTANCE_DEFAULT
            );
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0, notification);
    }
}