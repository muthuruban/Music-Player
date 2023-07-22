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
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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
import java.util.Random;

import static com.devlover.musicplayer.adapters.AlbumTracksAdapter.albumFiles;
import static com.devlover.musicplayer.adapters.MusicAdapter.songDataArrayList;

public class MainActivity extends AppCompatActivity implements ActionPlayControls, BottomNavigationView.OnNavigationItemSelectedListener, ServiceConnection {

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
    public static int _CURRENT_SONG_POS_ = 0;
    public static String _ARTIST_NAME_ = null;

    //BottomPlayer
    ImageView playButton, skipNextButton, skipPrevButton, musicQueueButton, albumArt;
    TextView songName, songArtist;
    RelativeLayout playerLayout;
    View view;
    ProgressBar progressBar;
    int position = -1;
    Uri uri;
    public static ArrayList<SongData> listOfSongs = new ArrayList<>();
    private Handler handler = new Handler();
    private Thread playThread, prevThread, nextThread;
    public static boolean TRACK_PLAYING = false;
    MusicService musicService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.home_nav);
        songArtist = findViewById(R.id.nowPlaying_songArtist_bottom);
        songName = findViewById(R.id.nowPlaying_songName_bottom);
        skipPrevButton = findViewById(R.id.skip_prev_bottom);
        skipPrevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                prevBtnClicked();
            }
        });
        playButton = findViewById(R.id.play_pause_bottom);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playPauseBtnClicked();
            }
        });
        skipNextButton = findViewById(R.id.skip_next_bottom);
        skipNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nextBtnClicked();
            }
        });
        musicQueueButton = findViewById(R.id.queue_list_bottom);
        albumArt = findViewById(R.id.nowPlaying_art_bottom);
        progressBar = findViewById(R.id.bottom_player_progressbar);
        playerLayout = findViewById(R.id.bottom_player_container);
        playerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, NowPlayingActivity.class);
                intent.putExtra("position", position);
                intent.putExtra("Activity", "BottomPlayer");
                startActivity(intent);
            }
        });
        songDataArrayList = getAllSongs(this);
        if (!getAllSongs(this).isEmpty()) {
            Toast.makeText(this,"Songs Fetched",Toast.LENGTH_SHORT).show();
            position = 1;
        }
        getIntentMethod();
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

    private void getIntentMethod() {
        position = getIntent().getIntExtra("position", 0);
        listOfSongs = songDataArrayList;
        if (listOfSongs != null) {
            playButton.setImageResource(R.drawable.ic_pause_circle);
            uri = Uri.parse(listOfSongs.get(position).getPath());
        }
        //Service Start
        Intent intent = new Intent(this, MusicService.class);
        intent.putExtra("servicePos", position);
        intent.putExtra("Activity", "MainActivity");
        startService(intent);
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

    @Override
    protected void onDestroy() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(0);
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        SharedPreferences sharedPref = this.getSharedPreferences(SONG_SORT_PREF, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("position", position);
        editor.apply();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = new Intent(this, MusicService.class);
        bindService(intent, this, BIND_AUTO_CREATE);
        SharedPreferences preferences = getSharedPreferences(MUSIC_FILE_LAST_PLAYED, MODE_PRIVATE);
        String value = preferences.getString(MUSIC_FILE, null);
        String songname = preferences.getString(SONG_NAME, null);
        String artistname = preferences.getString(ARTIST_NAME, null);
        if (musicService != null) {
            if (musicService.isPlaying()) {
                playButton.setImageResource(R.drawable.ic_round_pause);
            } else {
                playButton.setImageResource(R.drawable.ic_round_play_arrow);
            }
        } else {
            playButton.setImageResource(R.drawable.ic_round_play_arrow);
        }
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

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        Toast.makeText(this, "Service Connected", Toast.LENGTH_LONG).show();
        MusicService.LocalBinder localBinder = (MusicService.LocalBinder) iBinder;
        musicService = localBinder.getService();
        musicService.setCallBacks(this);
        songName.setText(listOfSongs.get(position).getTitle());
        songArtist.setText(listOfSongs.get(position).getArtist());
        Glide.with(this)
                .asBitmap()
                .load(getAlbumArt(uri.toString()))
                .placeholder(R.drawable.ic_music_cover)
                .into(albumArt);
        musicService.showNotification(R.drawable.round_play_arrow);
        musicService.OnCompleted();
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        musicService = null;
    }

    @Override
    public void playPauseBtnClicked() {
        Toast.makeText(getBaseContext(), "Play/Pause", Toast.LENGTH_LONG).show();
        if (musicService.isPlaying()) {
            playButton.setImageResource(R.drawable.ic_round_play_arrow);
            TRACK_PLAYING = false;
            musicService.showNotification(R.drawable.ic_round_play_arrow);
            musicService.pause();
        } else {
            playButton.setImageResource(R.drawable.ic_round_pause);
            TRACK_PLAYING = false;
            musicService.showNotification(R.drawable.ic_round_pause);
            musicService.start();
        }
    }

    private int getRandomPos(int i) {
        Random random = new Random();
        return random.nextInt(i + 1);
    }

    @Override
    public void nextBtnClicked() {
        Toast.makeText(getBaseContext(), "Next", Toast.LENGTH_LONG).show();
        if (musicService.isPlaying()) {
            musicService.stop();
            musicService.release();
            int i = position;
            if (orderBool) {
                position = ((position + 1) % listOfSongs.size());
            } else if (shuffleBool) {
                position = getRandomPos(listOfSongs.size() - 1);
            } else if (repeatBool) {
                position = ((position + 1) % listOfSongs.size());
            } else {
                position = i;
            }
            uri = Uri.parse(listOfSongs.get(position).getPath());
            musicService.createMediaPlayer(position);
            songName.setText(listOfSongs.get(position).getTitle());
            songArtist.setText(listOfSongs.get(position).getArtist());
            Glide.with(this)
                    .asBitmap()
                    .load(getAlbumArt(uri.toString()))
                    .placeholder(R.drawable.ic_music_cover)
                    .into(albumArt);
            musicService.OnCompleted();
            musicService.showNotification(R.drawable.round_pause_24);
            playButton.setImageResource(R.drawable.round_pause_24);
            musicService.start();
        } else {
            musicService.stop();
            musicService.release();
            int i = position;
            if (orderBool) {
                position = ((position + 1) % listOfSongs.size());
            } else if (shuffleBool) {
                position = getRandomPos(listOfSongs.size() - 1);
            } else if (repeatBool) {
                position = ((position + 1) % listOfSongs.size());
            } else {
                position = i;
            }
//            position = ((position + 1) % listOfSongs.size());
            uri = Uri.parse(listOfSongs.get(position).getPath());
            musicService.createMediaPlayer(position);
            songName.setText(listOfSongs.get(position).getTitle());
            songArtist.setText(listOfSongs.get(position).getArtist());
            Glide.with(this)
                    .asBitmap()
                    .load(getAlbumArt(uri.toString()))
                    .placeholder(R.drawable.ic_music_cover)
                    .into(albumArt);
            musicService.OnCompleted();
            musicService.showNotification(R.drawable.ic_pause_circle);
            playButton.setImageResource(R.drawable.ic_pause_circle);
            musicService.start();
        }
    }

    @Override
    public void prevBtnClicked() {
        Toast.makeText(getBaseContext(), "Prev", Toast.LENGTH_LONG).show();
        if (musicService.isPlaying()) {
            musicService.stop();
            musicService.release();
            int i = position;
            if (orderBool) {
                position = ((position - 1) < 0 ? (listOfSongs.size() - 1) : (position - 1));
            } else if (shuffleBool) {
                position = getRandomPos(listOfSongs.size() - 1);
            } else if (repeatBool) {
                position = ((position - 1) < 0 ? (listOfSongs.size() - 1) : (position - 1));
            } else {
                position = i;
            }
            uri = Uri.parse(listOfSongs.get(position).getPath());
            musicService.createMediaPlayer(position);
            songName.setText(listOfSongs.get(position).getTitle());
            songArtist.setText(listOfSongs.get(position).getArtist());
            musicService.OnCompleted();
            Glide.with(this)
                    .asBitmap()
                    .load(getAlbumArt(uri.toString()))
                    .placeholder(R.drawable.ic_music_cover)
                    .into(albumArt);
            musicService.showNotification(R.drawable.ic_pause_circle);
            playButton.setImageResource(R.drawable.ic_pause_circle);
            musicService.start();
        } else {
            musicService.stop();
            musicService.release();
            int i = position;
            if (orderBool) {
                position = ((position - 1) < 0 ? (listOfSongs.size() - 1) : (position - 1));
            } else if (shuffleBool) {
                position = getRandomPos(listOfSongs.size() - 1);
            } else if (repeatBool) {
                position = ((position - 1) < 0 ? (listOfSongs.size() - 1) : (position - 1));
            } else {
                position = i;
            }
            uri = Uri.parse(listOfSongs.get(position).getPath());
            musicService.createMediaPlayer(position);
            songName.setText(listOfSongs.get(position).getTitle());
            songArtist.setText(listOfSongs.get(position).getArtist());
            Glide.with(this)
                    .asBitmap()
                    .load(getAlbumArt(uri.toString()))
                    .placeholder(R.drawable.ic_music_cover)
                    .into(albumArt);
            musicService.OnCompleted();
            musicService.showNotification(R.drawable.ic_pause_circle);
            playButton.setImageResource(R.drawable.ic_pause_circle);
            musicService.start();
        }
    }
}
