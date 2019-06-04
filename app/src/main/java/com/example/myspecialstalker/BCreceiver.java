package com.example.myspecialstalker;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.arch.lifecycle.MutableLiveData;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

public class BCreceiver extends BroadcastReceiver
{
    final  String notificationText = "Sending message...";
    private final MutableLiveData<String> CallMade = new MutableLiveData<>();
    Notification ntfc;
    public static final String CHANNEL_ID = "100";
    public static final int NOT_ID = 1;

    @Override
    public void onReceive(Context context, Intent intent) {

        createNotificationChannel(context);

        createnotification(context);

        NotificationManagerCompat.from(context).notify(NOT_ID,ntfc);

        if (intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)){
            CallMade.postValue(intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER));
        }
    }

    private void createNotificationChannel(Context context) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = context.getString(R.string.channel_name);
            String description = context.getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public MutableLiveData<String> status(){
        return CallMade;
    }

    private void createnotification(Context context){
         ntfc = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle(notificationText)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .build();


    }
}
