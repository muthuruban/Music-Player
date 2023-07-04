package com.devlover.musicplayer.activity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaMetadataRetriever;
import android.media.session.MediaSession;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.widget.ImageViewCompat;
import androidx.palette.graphics.Palette;

import com.bumptech.glide.Glide;
import com.devlover.musicplayer.R;
import com.devlover.musicplayer.model.SongData;
import com.devlover.musicplayer.playback.ActionPlayControls;
import com.devlover.musicplayer.playback.MusicService;
import com.devlover.musicplayer.utilities.NotificationReceiver;

import java.util.ArrayList;
import java.util.Random;

import static com.devlover.musicplayer.activity.MainActivity.orderBool;
import static com.devlover.musicplayer.activity.MainActivity.repeatBool;
import static com.devlover.musicplayer.activity.MainActivity.repeatCurrBool;
import static com.devlover.musicplayer.activity.MainActivity.shuffleBool;
import static com.devlover.musicplayer.adapters.MusicAdapter.songDataArrayList;
import static com.devlover.musicplayer.adapters.AlbumTracksAdapter.albumFiles;
import static com.devlover.musicplayer.playback.ApplicationClass.ACTION_NEXT;
import static com.devlover.musicplayer.playback.ApplicationClass.ACTION_PLAY;
import static com.devlover.musicplayer.playback.ApplicationClass.ACTION_PREV;
import static com.devlover.musicplayer.playback.ApplicationClass.CHANNEL_ID_2;

//https://www.geeksforgeeks.org/how-to-implement-viewpager-with-dotsindicator-in-android/

public class NowPlayingActivity extends AppCompatActivity implements ActionPlayControls, ServiceConnection {
    TextView trackName, artistName, albumName, currentDuration, totalDuration;
    ImageView orderBtn, prevTrackBtn, playPauseBtn, nextTrackBtn, queueBtn;
    ImageView favBtn, moreInfoBtn, moreOptBtn, backBtn;
    SeekBar seekBarCtrl;
    ImageView albumArt;
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
        setContentView(R.layout.activity_now_playing);
        initView();
        getIntentMethod();
//        backBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                finish();
//            }
//        });

        seekBarCtrl.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean user) {
                if (musicService != null && user) {
                    musicService.seekTo(progress * 1000);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        NowPlayingActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (musicService != null) {
                    int currentPos = musicService.getCurrentPosition() / 1000;
                    seekBarCtrl.setProgress(currentPos);
                    currentDuration.setText(formattedTime(currentPos));
                }
                handler.postDelayed(this, 1000);
            }
        });

        orderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (orderBool) {
                    orderBool = false;
                    shuffleBool = true;
                    orderBtn.setImageResource(R.drawable.ic_round_shuffle);
                    Toast.makeText(getApplicationContext(), "Shuffle.", Toast.LENGTH_SHORT).show();
                } else if (shuffleBool) {
                    shuffleBool = false;
                    repeatBool = true;
                    orderBtn.setImageResource(R.drawable.ic_round_repeat_all);
                    Toast.makeText(getApplicationContext(), "Repeat list.", Toast.LENGTH_SHORT).show();
                } else if (repeatBool) {
                    repeatBool = false;
                    repeatCurrBool = true;
                    orderBtn.setImageResource(R.drawable.ic_round_repeat_current);
                    Toast.makeText(getApplicationContext(), "Repeat current song.", Toast.LENGTH_SHORT).show();
                } else {
                    repeatCurrBool = false;
                    orderBool = true;
                    orderBtn.setImageResource(R.drawable.ic_ordered);
                    Toast.makeText(getApplicationContext(), "Play in order.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private String formattedTime(int currentPos) {
        String totalout = "";
        String totalnew = "";
        String seconds = String.valueOf(currentPos % 60);
        String minutes = String.valueOf(currentPos / 60);
        totalout = minutes + ":" + seconds;
        totalnew = minutes + ":" + "0" + seconds;
        if (seconds.length() == 1) {
            return totalnew;
        } else {
            return totalout;
        }
    }


    private void getIntentMethod() {
        position = getIntent().getIntExtra("position", -1);
        String sender = getIntent().getStringExtra("sender");
        if (sender != null && sender.equals("fromAlbumTracks")) {
            listOfSongs = albumFiles;
        } else {
            listOfSongs = songDataArrayList;
        }
        if (listOfSongs != null) {
            playPauseBtn.setImageResource(R.drawable.ic_pause_circle);
            uri = Uri.parse(listOfSongs.get(position).getPath());
        }
//        if (mediaPlayer != null) {
//            mediaPlayer.stop();
//            mediaPlayer.release();
//            mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
//            mediaPlayer.start();
//        } else {
//            mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
//            mediaPlayer.start();
//        }
        //Service Start
        Intent intent = new Intent(this, MusicService.class);
        intent.putExtra("servicePos", position);
        startService(intent);
    }

    private void initView() {
        trackName = findViewById(R.id.playing_song_name);
        artistName = findViewById(R.id.playing_song_artist);
        albumName = findViewById(R.id.album_name);
        currentDuration = findViewById(R.id.current_duration);
        totalDuration = findViewById(R.id.total_duration);
        orderBtn = findViewById(R.id.queue_btn);
        prevTrackBtn = findViewById(R.id.prev_btn);
        playPauseBtn = findViewById(R.id.ply_pus_btn);
        nextTrackBtn = findViewById(R.id.next_btn);
        queueBtn = findViewById(R.id.queue_list);
        favBtn = findViewById(R.id.favorite_btn);
        moreInfoBtn = findViewById(R.id.song_info_btn);
        moreOptBtn = findViewById(R.id.more_option_btn);
        backBtn = findViewById(R.id.back_btn);
        seekBarCtrl = findViewById(R.id.seek_bar);
        albumArt = findViewById(R.id.album_art);
    }

    private void metaDataRetriever(Uri uri) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri.toString());
        int totalDur = Integer.parseInt(listOfSongs.get(position).getDuration()) / 1000;
        totalDuration.setText(formattedTime(totalDur));
        byte[] art = retriever.getEmbeddedPicture();
        Bitmap bitmap;
        if (art != null) {
//            Glide.with(this)
//                    .asBitmap()
//                    .load(art)
//                    .into(albumArt);
            bitmap = BitmapFactory.decodeByteArray(art, 0, art.length);
            ImageAnimation(this, albumArt, bitmap);
            Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                @Override
                public void onGenerated(@Nullable Palette palette) {
                    Palette.Swatch swatch = palette.getDominantSwatch();
                    if (swatch != null) {
                        ImageView gradient = findViewById(R.id.image_over_gradient);
                        RelativeLayout container = findViewById(R.id.player_container);
                        gradient.setBackgroundResource(R.drawable.gradient_bg);
                        container.setBackgroundResource(R.drawable.main_bg);
                        GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, new int[]{swatch.getRgb(), 0x00000000});
                        gradient.setBackground(gradientDrawable);
                        GradientDrawable gradientDrawableBg = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, new int[]{swatch.getRgb(), swatch.getRgb()});
                        container.setBackground(gradientDrawableBg);
                        trackName.setTextColor(swatch.getBodyTextColor());
                        artistName.setTextColor(swatch.getTitleTextColor());
                        albumName.setTextColor(swatch.getBodyTextColor());
                        currentDuration.setTextColor(swatch.getBodyTextColor());
                        totalDuration.setTextColor(swatch.getBodyTextColor());
                        seekBarCtrl.setBackgroundColor(swatch.getRgb());
                        seekBarCtrl.getProgressDrawable().setColorFilter(swatch.getBodyTextColor(), PorterDuff.Mode.MULTIPLY);
                        seekBarCtrl.getThumb().setColorFilter(swatch.getBodyTextColor(), PorterDuff.Mode.SRC_ATOP);
//                      play back controls tint color
                        ImageViewCompat.setImageTintList(playPauseBtn, ColorStateList.valueOf(swatch.getBodyTextColor()));
                        ImageViewCompat.setImageTintList(prevTrackBtn, ColorStateList.valueOf(swatch.getBodyTextColor()));
                        ImageViewCompat.setImageTintList(nextTrackBtn, ColorStateList.valueOf(swatch.getBodyTextColor()));
//                      other icon tint color
                        ImageViewCompat.setImageTintList(favBtn, ColorStateList.valueOf(swatch.getBodyTextColor()));
                        ImageViewCompat.setImageTintList(queueBtn, ColorStateList.valueOf(swatch.getBodyTextColor()));
                        ImageViewCompat.setImageTintList(orderBtn, ColorStateList.valueOf(swatch.getBodyTextColor()));
                        ImageViewCompat.setImageTintList(moreInfoBtn, ColorStateList.valueOf(swatch.getBodyTextColor()));
                        ImageViewCompat.setImageTintList(moreOptBtn, ColorStateList.valueOf(swatch.getBodyTextColor()));
                        ImageViewCompat.setImageTintList(backBtn, ColorStateList.valueOf(swatch.getBodyTextColor()));

                    } else {
                        ImageView gradient = findViewById(R.id.image_over_gradient);
                        RelativeLayout container = findViewById(R.id.player_container);
                        gradient.setBackgroundResource(R.drawable.gradient_bg);
                        container.setBackgroundResource(R.drawable.main_bg);
                        GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, new int[]{swatch.getRgb(), 0x00000000});
                        gradient.setBackground(gradientDrawable);
                        GradientDrawable gradientDrawableBg = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, new int[]{swatch.getRgb(), swatch.getRgb()});
                        container.setBackground(gradientDrawableBg);
                        trackName.setTextColor(swatch.getBodyTextColor());
                        artistName.setTextColor(swatch.getTitleTextColor());
                        albumName.setTextColor(swatch.getBodyTextColor());
                        currentDuration.setTextColor(swatch.getBodyTextColor());
                        totalDuration.setTextColor(swatch.getBodyTextColor());
                        seekBarCtrl.setBackgroundColor(swatch.getRgb());
                        seekBarCtrl.getProgressDrawable().setColorFilter(swatch.getBodyTextColor(), PorterDuff.Mode.MULTIPLY);
                        seekBarCtrl.getThumb().setColorFilter(swatch.getBodyTextColor(), PorterDuff.Mode.SRC_ATOP);
//                      play back controls tint color
                        ImageViewCompat.setImageTintList(playPauseBtn, ColorStateList.valueOf(swatch.getBodyTextColor()));
                        ImageViewCompat.setImageTintList(prevTrackBtn, ColorStateList.valueOf(swatch.getBodyTextColor()));
                        ImageViewCompat.setImageTintList(nextTrackBtn, ColorStateList.valueOf(swatch.getBodyTextColor()));
//                      other icon tint color
                        ImageViewCompat.setImageTintList(favBtn, ColorStateList.valueOf(swatch.getBodyTextColor()));
                        ImageViewCompat.setImageTintList(queueBtn, ColorStateList.valueOf(swatch.getBodyTextColor()));
                        ImageViewCompat.setImageTintList(orderBtn, ColorStateList.valueOf(swatch.getBodyTextColor()));
                        ImageViewCompat.setImageTintList(moreInfoBtn, ColorStateList.valueOf(swatch.getBodyTextColor()));
                        ImageViewCompat.setImageTintList(moreOptBtn, ColorStateList.valueOf(swatch.getBodyTextColor()));
                        ImageViewCompat.setImageTintList(backBtn, ColorStateList.valueOf(swatch.getBodyTextColor()));
                    }
                }
            });
        } else {
//            Glide.with(this)
//                    .asDrawable()
//                    .load(R.drawable.ic_huawei_music_cover_round)
//                    .into(albumArt);
            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_music_cover);
            ImageAnimation(this, albumArt, bitmap);
            Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                @Override
                public void onGenerated(@Nullable Palette palette) {
                    Palette.Swatch swatch = palette.getDominantSwatch();
                    if (swatch != null) {
                        ImageView gradient = findViewById(R.id.image_over_gradient);
                        RelativeLayout container = findViewById(R.id.player_container);
                        gradient.setBackgroundResource(R.drawable.gradient_bg);
                        container.setBackgroundResource(R.drawable.main_bg);
                        GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, new int[]{swatch.getPopulation(), 0x00000000});
                        gradient.setBackground(gradientDrawable);
                        GradientDrawable gradientDrawableBg = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, new int[]{swatch.getRgb(), swatch.getRgb()});
                        container.setBackground(gradientDrawableBg);
                        trackName.setTextColor(swatch.getBodyTextColor());
                        artistName.setTextColor(swatch.getTitleTextColor());
                        albumName.setTextColor(swatch.getBodyTextColor());
                        currentDuration.setTextColor(swatch.getBodyTextColor());
                        totalDuration.setTextColor(swatch.getBodyTextColor());
                        seekBarCtrl.setBackgroundColor(swatch.getRgb());
                        seekBarCtrl.getProgressDrawable().setColorFilter(swatch.getBodyTextColor(), PorterDuff.Mode.MULTIPLY);
                        seekBarCtrl.getThumb().setColorFilter(swatch.getBodyTextColor(), PorterDuff.Mode.SRC_ATOP);
//                      play back controls tint color
                        ImageViewCompat.setImageTintList(playPauseBtn, ColorStateList.valueOf(swatch.getBodyTextColor()));
                        ImageViewCompat.setImageTintList(prevTrackBtn, ColorStateList.valueOf(swatch.getBodyTextColor()));
                        ImageViewCompat.setImageTintList(nextTrackBtn, ColorStateList.valueOf(swatch.getBodyTextColor()));
//                      other icon tint color
                        ImageViewCompat.setImageTintList(favBtn, ColorStateList.valueOf(swatch.getBodyTextColor()));
                        ImageViewCompat.setImageTintList(queueBtn, ColorStateList.valueOf(swatch.getBodyTextColor()));
                        ImageViewCompat.setImageTintList(orderBtn, ColorStateList.valueOf(swatch.getBodyTextColor()));
                        ImageViewCompat.setImageTintList(moreInfoBtn, ColorStateList.valueOf(swatch.getBodyTextColor()));
                        ImageViewCompat.setImageTintList(moreOptBtn, ColorStateList.valueOf(swatch.getBodyTextColor()));
                        ImageViewCompat.setImageTintList(backBtn, ColorStateList.valueOf(swatch.getBodyTextColor()));
                    } else {
                        ImageView gradient = findViewById(R.id.image_over_gradient);
                        RelativeLayout container = findViewById(R.id.player_container);
                        gradient.setBackgroundResource(R.drawable.gradient_bg);
                        container.setBackgroundResource(R.drawable.main_bg);
                        GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, new int[]{swatch.getRgb(), 0x00000000});
                        gradient.setBackground(gradientDrawable);
                        GradientDrawable gradientDrawableBg = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, new int[]{swatch.getRgb(), swatch.getRgb()});
                        container.setBackground(gradientDrawableBg);
                        trackName.setTextColor(swatch.getBodyTextColor());
                        artistName.setTextColor(swatch.getTitleTextColor());
                        albumName.setTextColor(swatch.getBodyTextColor());
                        currentDuration.setTextColor(swatch.getBodyTextColor());
                        totalDuration.setTextColor(swatch.getBodyTextColor());
                        seekBarCtrl.setBackgroundColor(swatch.getRgb());
                        seekBarCtrl.getProgressDrawable().setColorFilter(swatch.getBodyTextColor(), PorterDuff.Mode.MULTIPLY);
                        seekBarCtrl.getThumb().setColorFilter(swatch.getBodyTextColor(), PorterDuff.Mode.SRC_ATOP);
//                      play back controls tint color
                        ImageViewCompat.setImageTintList(playPauseBtn, ColorStateList.valueOf(swatch.getBodyTextColor()));
                        ImageViewCompat.setImageTintList(prevTrackBtn, ColorStateList.valueOf(swatch.getBodyTextColor()));
                        ImageViewCompat.setImageTintList(nextTrackBtn, ColorStateList.valueOf(swatch.getBodyTextColor()));
//                      other icon tint color
                        ImageViewCompat.setImageTintList(favBtn, ColorStateList.valueOf(swatch.getBodyTextColor()));
                        ImageViewCompat.setImageTintList(queueBtn, ColorStateList.valueOf(swatch.getBodyTextColor()));
                        ImageViewCompat.setImageTintList(orderBtn, ColorStateList.valueOf(swatch.getBodyTextColor()));
                        ImageViewCompat.setImageTintList(moreInfoBtn, ColorStateList.valueOf(swatch.getBodyTextColor()));
                        ImageViewCompat.setImageTintList(moreOptBtn, ColorStateList.valueOf(swatch.getBodyTextColor()));
                        ImageViewCompat.setImageTintList(backBtn, ColorStateList.valueOf(swatch.getBodyTextColor()));
                    }
                }
            });
        }
    }

    @Override
    protected void onResume() {
        Intent intent = new Intent(this, MusicService.class);
        bindService(intent, this, BIND_AUTO_CREATE);
        playThreadBtn();
        nextThreadBtn();
        prevThreadBtn();
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unbindService(this);
    }

    private void playThreadBtn() {
        playThread = new Thread() {
            @Override
            public void run() {
                super.run();
                playPauseBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        playPauseBtnClicked();
                    }
                });
            }
        };
        playThread.start();
    }

    public void playPauseBtnClicked() {
        if (musicService.isPlaying()) {
            playPauseBtn.setImageResource(R.drawable.ic_round_play_circle);
            TRACK_PLAYING = false;
            musicService.showNotification(R.drawable.ic_round_play_arrow);
            musicService.pause();
            seekBarCtrl.setMax(musicService.getDuration() / 1000);
            NowPlayingActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (musicService != null) {
                        int currentPos = musicService.getCurrentPosition() / 1000;
                        seekBarCtrl.setProgress(currentPos);
                    }
                    handler.postDelayed(this, 1000);
                }
            });
        } else {
            playPauseBtn.setImageResource(R.drawable.ic_pause_circle);
            TRACK_PLAYING = true;
            musicService.showNotification(R.drawable.ic_pause_circle);
            musicService.start();
            seekBarCtrl.setMax(musicService.getDuration() / 1000);
            NowPlayingActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (musicService != null) {
                        int currentPos = musicService.getCurrentPosition() / 1000;
                        seekBarCtrl.setProgress(currentPos);
                    }
                    handler.postDelayed(this, 1000);
                }
            });
        }
    }

    private void nextThreadBtn() {
        nextThread = new Thread() {
            @Override
            public void run() {
                super.run();
                nextTrackBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        nextBtnClicked();
                    }
                });
            }
        };
        nextThread.start();
    }

    public void nextBtnClicked() {
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
//            position = ((position + 1) % listOfSongs.size());
            uri = Uri.parse(listOfSongs.get(position).getPath());
            musicService.createMediaPlayer(position);
            metaDataRetriever(uri);
            trackName.setText(listOfSongs.get(position).getTitle());
            artistName.setText(listOfSongs.get(position).getArtist());
            albumName.setText(listOfSongs.get(position).getAlbum());
            seekBarCtrl.setMax(musicService.getDuration() / 1000);
            NowPlayingActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (musicService != null) {
                        int currentPos = musicService.getCurrentPosition() / 1000;
                        seekBarCtrl.setProgress(currentPos);
                    }
                    handler.postDelayed(this, 1000);
                }
            });
            musicService.OnCompleted();
            musicService.showNotification(R.drawable.ic_pause_circle);
            playPauseBtn.setImageResource(R.drawable.ic_pause_circle);
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
            metaDataRetriever(uri);
            trackName.setText(listOfSongs.get(position).getTitle());
            artistName.setText(listOfSongs.get(position).getArtist());
            albumName.setText(listOfSongs.get(position).getAlbum());
            seekBarCtrl.setMax(musicService.getDuration() / 1000);
            NowPlayingActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (musicService != null) {
                        int currentPos = musicService.getCurrentPosition() / 1000;
                        seekBarCtrl.setProgress(currentPos);
                    }
                    handler.postDelayed(this, 1000);
                }
            });
            musicService.OnCompleted();
            musicService.showNotification(R.drawable.ic_pause_circle);
            playPauseBtn.setImageResource(R.drawable.ic_pause_circle);
            musicService.start();
        }
    }

    private int getRandomPos(int i) {
        Random random = new Random();
        return random.nextInt(i + 1);
    }

    private void prevThreadBtn() {
        prevThread = new Thread() {
            @Override
            public void run() {
                super.run();
                prevTrackBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        prevBtnClicked();
                    }
                });
            }
        };
        prevThread.start();
    }

    public void prevBtnClicked() {
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
//            ((position-1)<0?(listOfSongs.size()-1):(position-1))
            uri = Uri.parse(listOfSongs.get(position).getPath());
            musicService.createMediaPlayer(position);
            metaDataRetriever(uri);
            trackName.setText(listOfSongs.get(position).getTitle());
            artistName.setText(listOfSongs.get(position).getArtist());
            albumName.setText(listOfSongs.get(position).getAlbum());
            seekBarCtrl.setMax(musicService.getDuration() / 1000);
            NowPlayingActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (musicService != null) {
                        int currentPos = musicService.getCurrentPosition() / 1000;
                        seekBarCtrl.setProgress(currentPos);
                    }
                    handler.postDelayed(this, 1000);
                }
            });
            musicService.OnCompleted();
            musicService.showNotification(R.drawable.ic_pause_circle);
            playPauseBtn.setImageResource(R.drawable.ic_pause_circle);
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
//            ((position-1)<0?(listOfSongs.size()-1):(position-1))
            uri = Uri.parse(listOfSongs.get(position).getPath());
            musicService.createMediaPlayer(position);
            metaDataRetriever(uri);
            trackName.setText(listOfSongs.get(position).getTitle());
            artistName.setText(listOfSongs.get(position).getArtist());
            albumName.setText(listOfSongs.get(position).getAlbum());
            seekBarCtrl.setMax(musicService.getDuration() / 1000);
            NowPlayingActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (musicService != null) {
                        int currentPos = musicService.getCurrentPosition() / 1000;
                        seekBarCtrl.setProgress(currentPos);
                    }
                    handler.postDelayed(this, 1000);
                }
            });
            musicService.OnCompleted();
            musicService.showNotification(R.drawable.ic_pause_circle);
            playPauseBtn.setImageResource(R.drawable.ic_pause_circle);
            musicService.start();
        }
    }

    private void ImageAnimation(Context context, ImageView imageView, Bitmap bitmap) {
        Animation animOut = AnimationUtils.loadAnimation(context, android.R.anim.fade_out);
        Animation animIn = AnimationUtils.loadAnimation(context, android.R.anim.fade_in);
        animOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Glide.with(context).load(bitmap).into(imageView);
                animIn.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                imageView.startAnimation(animIn);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        imageView.startAnimation(animOut);
    }

//    @Override
//    public void onCompletion(MediaPlayer mediaPlayer) {
//        nextBtnClicked();
//        if (musicService != null) {
//            musicService.createMediaPlayer(position);
//            musicService.start();
//            musicService.OnCompleted();
//        }
//    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        super.onBackPressed();
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        MusicService.LocalBinder localBinder = (MusicService.LocalBinder) iBinder;
        musicService = localBinder.getService();
        musicService.setCallBacks(this);
        trackName.setText(listOfSongs.get(position).getTitle());
        artistName.setText(listOfSongs.get(position).getArtist());
        albumName.setText(listOfSongs.get(position).getAlbum());
        musicService.OnCompleted();
        seekBarCtrl.setMax(musicService.getDuration() / 1000);
        metaDataRetriever(uri);
        musicService.showNotification(R.drawable.ic_pause_circle);
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        musicService = null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}