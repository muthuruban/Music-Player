package com.devlover.musicplayer.playback;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class ApplicationClass extends Application {
    public static final String CHANNEL_ID_1 = "channel1";
    public static final String CHANNEL_ID_2 = "channel2";
    public static final String ACTION_PREV = "actionprevious";
    public static final String ACTION_NEXT = "actionnext";
    public static final String ACTION_PLAY = "actionplay";
    @Override
    public void onCreate() {
        super.onCreate();
        createNotifyChannel();
    }

    private void createNotifyChannel() {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            NotificationChannel channel1 = new NotificationChannel(CHANNEL_ID_1,"Demo", NotificationManager.IMPORTANCE_LOW);
            channel1.setDescription("Channel 1 Description");
            NotificationChannel channel2 = new NotificationChannel(CHANNEL_ID_2,"Music Play", NotificationManager.IMPORTANCE_HIGH);
            channel2.setDescription("Channel 2 Description");
            channel2.setVibrationPattern(new long[]{ 0 });
            channel2.enableVibration(true);
            channel2.setSound(null,null);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel1);
            notificationManager.createNotificationChannel(channel2);
        }
    }
}
