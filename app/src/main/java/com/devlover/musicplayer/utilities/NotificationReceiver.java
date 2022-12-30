package com.devlover.musicplayer.utilities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.devlover.musicplayer.playback.MusicService;

import static com.devlover.musicplayer.playback.ApplicationClass.ACTION_NEXT;
import static com.devlover.musicplayer.playback.ApplicationClass.ACTION_PLAY;
import static com.devlover.musicplayer.playback.ApplicationClass.ACTION_PREV;

public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String actionName = intent.getAction();
        Intent service = new Intent(context, MusicService.class);
        if (actionName != null) {
            switch (actionName) {
                case ACTION_PLAY:
//                    Toast.makeText(context, "Playing", Toast.LENGTH_SHORT).show();
                    service.putExtra("ActionName", "playPause");
                    context.startService(service);
                    break;
                case ACTION_PREV:
//                    Toast.makeText(context, "Prev", Toast.LENGTH_SHORT).show();
                    service.putExtra("ActionName", "previous");
                    context.startService(service);
                    break;
                case ACTION_NEXT:
//                    Toast.makeText(context, "Next", Toast.LENGTH_SHORT).show();
                    service.putExtra("ActionName", "next");
                    context.startService(service);
                    break;
            }
        }
    }
}
