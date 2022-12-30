package com.devlover.musicplayer.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.devlover.musicplayer.R;
import com.devlover.musicplayer.fragments.FavoriteFragment;
import com.devlover.musicplayer.fragments.HomeFragment;
import com.devlover.musicplayer.fragments.PlaylistFragment;
import com.devlover.musicplayer.fragments.SettingsFragment;
import com.devlover.musicplayer.playback.ActionPlayControls;
import com.devlover.musicplayer.playback.MusicService;
import com.devlover.musicplayer.preference.SettingsPreference;
import com.devlover.musicplayer.model.SongData;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    public static boolean SHOW_BOTTOM_PLAYER = false;
    String SONG_SORT_PREF = "SortingOrder";
    BottomNavigationView bottomNavigationView;
    public static ArrayList<SongData> songDataArrayList;
    public static ArrayList<SongData> albums = new ArrayList<>();
    public static boolean shuffleBool = false, repeatBool = false, repeatCurrBool = false, orderBool = true;
    public static final String MUSIC_FILE_LAST_PLAYED = "LAST_PLAYED";
    public static final String MUSIC_FILE = "STORED_MUSIC";
    public static final String SONG_NAME = "SONG_NAME";
    public static final String ARTIST_NAME = "ARTIST_NAME";
    public static String PATH_TO_BOTTOMPLAYER = null;
    public static String _SONG_NAME_ = null;
    public static String _ARTIST_NAME_ = null;
    int position = -1;
    MusicService musicService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.home_nav);
        permission();

    }

    private void permission() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            songDataArrayList = getAllSongs(this);
        } else {
            Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
        }
    }

    HomeFragment homeFragment = new HomeFragment();
    FavoriteFragment favoriteFragment = new FavoriteFragment();
    PlaylistFragment playlistFragment = new PlaylistFragment();
    SettingsFragment settingsFragment = new SettingsFragment();

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.home_nav:
                getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, homeFragment).commit();
                return true;
            case R.id.favourites:
                getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, favoriteFragment).commit();
                return true;
            case R.id.playlists:
                getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, playlistFragment).commit();
                return true;
            case R.id.settings:
                getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, settingsFragment).commit();
                return true;
        }
        return false;
    }

    public ArrayList<SongData> getAllSongs(Context context) {
        SharedPreferences preferences = getSharedPreferences(SONG_SORT_PREF, MODE_PRIVATE);
        String sortOrder = preferences.getString("sorting", "sortByName");
        ArrayList<String> tempDup = new ArrayList<>();
        ArrayList<SongData> songDataArrayList = new ArrayList<>();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String order = null;
        switch (sortOrder) {
            case "sortByName":
                order = MediaStore.MediaColumns.TITLE + " asc";
                Log.e("Sorting By NAME", order);
                break;
            case "sortByDate":
                order = MediaStore.MediaColumns.DATE_ADDED + " asc";
                Log.e("Sorting By DATE", order);
                break;
            case "sortBySize":
                order = MediaStore.MediaColumns.SIZE + " desc";
                Log.e("Sorting By SIZE", order);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + sortOrder);
        }
        String[] projection = {
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.ARTIST,
        };
        Cursor cursor = context.getContentResolver().query(uri, projection,
                null, null, order);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String album = cursor.getString(0);
                String title = cursor.getString(1);
                String duration = cursor.getString(2);
                String path = cursor.getString(3);
                String artist = cursor.getString(4);
                SongData songData = new SongData(path, title, artist, album, duration, uri);
                songDataArrayList.add(songData);
                if (!tempDup.contains(album)) {
                    albums.add(songData);
                    tempDup.add(album);
                }
            }
            cursor.close();
        }
        return songDataArrayList;
    }

    @Override
    protected void onDestroy() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(0);


        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences preferences = getSharedPreferences(MUSIC_FILE_LAST_PLAYED, MODE_PRIVATE);
        String value = preferences.getString(MUSIC_FILE, null);
        String songname = preferences.getString(SONG_NAME, null);
        String artistname = preferences.getString(ARTIST_NAME, null);
        if (value != null) {
            SHOW_BOTTOM_PLAYER = true;
            PATH_TO_BOTTOMPLAYER = value;
            _SONG_NAME_ = songname;
            _ARTIST_NAME_ = artistname;
        } else {
            SHOW_BOTTOM_PLAYER = false;
            PATH_TO_BOTTOMPLAYER = null;
            _SONG_NAME_ = null;
            _ARTIST_NAME_ = null;
        }
    }

}
