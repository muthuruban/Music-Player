package com.devlover.musicplayer.playback;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.session.MediaSession;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.devlover.musicplayer.R;
import com.devlover.musicplayer.activity.NowPlayingActivity;
import com.devlover.musicplayer.fragments.BottomPlayerUpdates;
import com.devlover.musicplayer.model.SongData;
import com.devlover.musicplayer.utilities.NotificationReceiver;

import java.util.ArrayList;

import static com.devlover.musicplayer.activity.MainActivity.songDataArrayList;
import static com.devlover.musicplayer.activity.NowPlayingActivity.TRACK_PLAYING;
import static com.devlover.musicplayer.activity.NowPlayingActivity.listOfSongs;
import static com.devlover.musicplayer.playback.ApplicationClass.ACTION_NEXT;
import static com.devlover.musicplayer.playback.ApplicationClass.ACTION_PLAY;
import static com.devlover.musicplayer.playback.ApplicationClass.ACTION_PREV;
import static com.devlover.musicplayer.playback.ApplicationClass.CHANNEL_ID_2;


public class MusicService extends Service implements MediaPlayer.OnCompletionListener {
    IBinder iBinder = new LocalBinder();
    MediaPlayer mediaPlayer;
    public ArrayList<SongData> songDataArrayListServ = new ArrayList<>();
    Uri uri;
    public int position = -1;
    ActionPlayControls actionPlayControls;
    BottomPlayerUpdates bottomPlayerUpdates;
    MediaSession mediaSession;
    MediaSessionCompat mediaSessionCompat;
    public static final String MUSIC_FILE_LAST_PLAYED = "LAST_PLAYED";
    public static final String SONG_NAME = "SONG_NAME";
    public static final String ARTIST_NAME = "ARTIST_NAME";
    public static final String MUSIC_FILE = "STORED_MUSIC";
    public static Boolean TRACK_COMPLETION = false;

    @Override
    public void onCreate() {
        super.onCreate();
        mediaSession = new MediaSession(getBaseContext(), "Music Player");
        mediaSessionCompat = new MediaSessionCompat(getBaseContext(), "Music Player");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return iBinder;
    }


    public class LocalBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int intentedPos = intent.getIntExtra("servicePos", 0);
        String actionName = intent.getStringExtra("ActionName");
        String activityFrom = intent.getStringExtra("Activity");
        if (activityFrom != null) {
            switch(activityFrom) {
                case "MainActivity":
                    Toast.makeText(this,"MainActivity",Toast.LENGTH_LONG).show();
                    loadSong(intentedPos);
                    break;
                case "SongListActivity":
                    Toast.makeText(this,"Song",Toast.LENGTH_LONG).show();
                    playSong(intentedPos);
                    break;
                case "BottomPlayer":
                    Toast.makeText(this,"Player",Toast.LENGTH_LONG).show();
                    if(mediaPlayer!=null){
                    mediaPlayer.pause();
                    mediaPlayer.start();}
                    break;
            }
        }
        playSong(intentedPos);
        if (actionName != null) {
            switch (actionName) {
                case "playPause":
                    Log.e("ActionName$", "playpause");
                    playPauseButtonClicked();
                    break;
                case "previous":
                    Log.e("ActionName$", "previous");
                    prevButtonClicked();
                    break;
                case "next":
                    Log.e("ActionName$", "next");
                    nextButtonClicked();
                    break;
            }
        }
        playSong(position);
        return START_STICKY;
    }

    private void playSong(int Initposition) {
        songDataArrayListServ = songDataArrayList;
        position = Initposition;
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            if (songDataArrayListServ != null) {
                createMediaPlayer(position);
                mediaPlayer.start();
                TRACK_PLAYING = true;
            }
        } else {
            createMediaPlayer(position);
            TRACK_PLAYING = true;
            mediaPlayer.start();
        }

    }

    private void loadSong(int Initposition) {
        songDataArrayListServ = songDataArrayList;
        position = Initposition;
        if (songDataArrayListServ != null) {
            createMediaPlayer(position);
            mediaPlayer.start();
            TRACK_PLAYING = false;

        } else {
            createMediaPlayer(position);
            TRACK_PLAYING = false;
            mediaPlayer.start();
        }
    }

    public void start() {
        TRACK_PLAYING = true;
        mediaPlayer.start();
    }

    public void pause() {
        TRACK_PLAYING = false;
        mediaPlayer.pause();
    }

    public boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }

    public void stop() {
        TRACK_PLAYING = false;
        mediaPlayer.stop();
    }

    public void release() {
        mediaPlayer.release();
    }

    public int getDuration() {
        return mediaPlayer.getDuration();
    }

    public void seekTo(int position) {
        mediaPlayer.seekTo(position);
    }

    public int getCurrentPosition() {
        return mediaPlayer.getCurrentPosition();
    }

    public void createMediaPlayer(int positionLocal) {
        position = positionLocal;
        uri = Uri.parse(songDataArrayListServ.get(position).getPath());
        //SharedPreferences
        SharedPreferences.Editor editor = getSharedPreferences(MUSIC_FILE_LAST_PLAYED, MODE_PRIVATE).edit();
        editor.putString(MUSIC_FILE, uri.toString());
        editor.putString(ARTIST_NAME, songDataArrayListServ.get(position).getArtist());
        editor.putString(SONG_NAME, songDataArrayListServ.get(position).getTitle());
        editor.apply();
        mediaPlayer = MediaPlayer.create(getBaseContext(), uri);
    }

    public void OnCompleted() {
        mediaPlayer.setOnCompletionListener(this);
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        if (actionPlayControls != null) {
            actionPlayControls.nextBtnClicked();
        }
        if (bottomPlayerUpdates != null)
            bottomPlayerUpdates.updateBottomPlayer();
        if (mediaPlayer != null) {
            createMediaPlayer(position);
            mediaPlayer.start();
            OnCompleted();
        }
    }

    public void setCallBacks(ActionPlayControls actionPlayControls) {
        this.actionPlayControls = actionPlayControls;
    }

    public void showNotification(int playPauseFlag) {
        Intent intent = new Intent(this, NowPlayingActivity.class);
        PendingIntent infoIntent = PendingIntent
                .getActivity(this, 0, intent, 0);
        Intent prevIntent = new Intent(this, NotificationReceiver.class)
                .setAction(ACTION_PREV);
        PendingIntent prevInfoIntent = PendingIntent
                .getBroadcast(this, 0, prevIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        Intent playIntent = new Intent(this, NotificationReceiver.class)
                .setAction(ACTION_PLAY);
        PendingIntent playInfoIntent = PendingIntent
                .getBroadcast(this, 0, playIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        Intent nextIntent = new Intent(this, NotificationReceiver.class).setAction(ACTION_NEXT);
        PendingIntent nextInfoIntent = PendingIntent
                .getBroadcast(this, 0, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        byte[] notifyPic = null;
        notifyPic = getAlbumArt(songDataArrayListServ.get(position).getPath());
        Bitmap notifyThumb = null;
        if (notifyPic != null) {
            notifyThumb = BitmapFactory.decodeByteArray(notifyPic, 0, notifyPic.length);
        } else {
            notifyThumb = BitmapFactory.decodeResource(getResources(), R.drawable.ic_music_cover);
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            //New Code
            RemoteViews mContentViews = new RemoteViews(getPackageName(), R.layout.custom_notification);
            mContentViews.setImageViewBitmap(R.id.notify_album_art, notifyThumb);
            mContentViews.setTextViewText(R.id.notify_music_name, songDataArrayListServ.get(position).getTitle());
            mContentViews.setTextViewText(R.id.notify_album_name, songDataArrayListServ.get(position).getAlbum());
            mContentViews.setOnClickPendingIntent(R.id.action_prev, prevInfoIntent);
            mContentViews.setOnClickPendingIntent(R.id.action_play, playInfoIntent);
            mContentViews.setOnClickPendingIntent(R.id.action_next, nextInfoIntent);

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID_2);
            mBuilder.setContentIntent(infoIntent);
            mBuilder.setSmallIcon(R.drawable.ic_round_music_ico_24);
            mBuilder.setSound(null);
            mBuilder.setSilent(true);

            Notification notification = mBuilder.build();
            notification.contentView = mContentViews;

            notification.flags |= Notification.FLAG_ONGOING_EVENT | Notification.FLAG_NO_CLEAR;
//            startForeground(2,notification.build());
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.notify(0, notification);

        } else {
            RemoteViews mContentViews = new RemoteViews(getPackageName(), R.layout.custom_notification);
            mContentViews.setImageViewBitmap(R.id.notify_album_art, notifyThumb);
            mContentViews.setTextViewText(R.id.notify_music_name, songDataArrayListServ.get(position).getTitle());
            mContentViews.setTextViewText(R.id.notify_album_name, songDataArrayListServ.get(position).getAlbum());
            mContentViews.setOnClickPendingIntent(R.id.action_prev, prevInfoIntent);
            mContentViews.setOnClickPendingIntent(R.id.action_play, playInfoIntent);
            mContentViews.setOnClickPendingIntent(R.id.action_next, nextInfoIntent);

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID_2);
            mBuilder.setContentIntent(infoIntent);
            mBuilder.setSmallIcon(R.drawable.ic_round_music_ico_24);
            mBuilder.setSound(null);
            mBuilder.setSilent(true);

            Notification notification = mBuilder.build();
            notification.contentView = mContentViews;

            notification.flags |= Notification.FLAG_ONGOING_EVENT | Notification.FLAG_NO_CLEAR;
//            startForeground(2,notification.build());
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.notify(0, notification);
        }

    }

    private byte[] getAlbumArt(String path) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(path);
            byte[] art = retriever.getEmbeddedPicture();
            retriever.release();
            return art;
        } catch (Exception e) {
            Log.e("AlbumArt : ", path);
        }
        return null;
    }

    public void playPauseButtonClicked() {
        if (actionPlayControls != null) {
            actionPlayControls.playPauseBtnClicked();
        }
    }

    public void prevButtonClicked() {
        if (actionPlayControls != null) {
            actionPlayControls.prevBtnClicked();
        }
    }

    public void nextButtonClicked() {
        if (actionPlayControls != null) {
            actionPlayControls.nextBtnClicked();
        }
    }
}
