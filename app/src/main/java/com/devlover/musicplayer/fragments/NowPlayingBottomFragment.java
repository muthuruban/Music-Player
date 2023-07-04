package com.devlover.musicplayer.fragments;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.devlover.musicplayer.R;
import com.devlover.musicplayer.activity.NowPlayingActivity;
import com.devlover.musicplayer.playback.MusicService;

import org.w3c.dom.Text;

import static android.content.Context.MODE_PRIVATE;
import static com.devlover.musicplayer.activity.MainActivity.ARTIST_NAME;
import static com.devlover.musicplayer.activity.MainActivity.MUSIC_FILE;
import static com.devlover.musicplayer.activity.MainActivity.MUSIC_FILE_LAST_PLAYED;
import static com.devlover.musicplayer.activity.MainActivity.PATH_TO_BOTTOMPLAYER;
import static com.devlover.musicplayer.activity.MainActivity.SHOW_BOTTOM_PLAYER;
import static com.devlover.musicplayer.activity.MainActivity.SONG_NAME;
import static com.devlover.musicplayer.activity.MainActivity._ARTIST_NAME_;
import static com.devlover.musicplayer.activity.MainActivity._SONG_NAME_;
import static com.devlover.musicplayer.activity.NowPlayingActivity.TRACK_PLAYING;

public class NowPlayingBottomFragment extends Fragment implements ServiceConnection {
    ImageView playButton, skipNextButton,skipPrevButton, musicQueueButton, albumArt;
    TextView songName, songArtist;
    RelativeLayout playerLayout;
    View view;
    ProgressBar progressBar;
    //Music Service
    MusicService musicService;
    int position = -1;
    Uri uri;
    private Context context;

    public NowPlayingBottomFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_now_playing_bottom, container, false);
        songArtist = view.findViewById(R.id.nowPlaying_songArtist_bottom);
        songName = view.findViewById(R.id.nowPlaying_songName_bottom);
        skipPrevButton = view.findViewById(R.id.skip_prev_bottom);
        playButton = view.findViewById(R.id.play_pause_bottom);
        skipNextButton = view.findViewById(R.id.skip_next_bottom);
        musicQueueButton = view.findViewById(R.id.queue_list_bottom);
        albumArt = view.findViewById(R.id.nowPlaying_art_bottom);
        progressBar = view.findViewById(R.id.bottom_player_progressbar);

        //New
        progressBar.setMax(100);
        progressBar.setProgress(100);
        playerLayout = view.findViewById(R.id.bottom_player_container);
        playerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(musicService!=null){
                Intent intent = new Intent(getContext(), NowPlayingActivity.class);
                intent.putExtra("position",musicService.position);
                getContext().startActivity(intent);
            }}
        });

        //
        skipNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (musicService != null) {
                    musicService.nextButtonClicked();
                    if (getActivity() != null) {
                        SharedPreferences.Editor editor = getActivity().getSharedPreferences(MUSIC_FILE_LAST_PLAYED, MODE_PRIVATE).edit();
                        editor.putString(MUSIC_FILE, musicService.songDataArrayList.get(musicService.position).getPath());
                        editor.putString(ARTIST_NAME, musicService.songDataArrayList.get(musicService.position).getArtist());
                        editor.putString(SONG_NAME, musicService.songDataArrayList.get(musicService.position).getTitle());
                        editor.apply();
                        SharedPreferences preferences = getActivity().getSharedPreferences(MUSIC_FILE_LAST_PLAYED, MODE_PRIVATE);
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
                        if (SHOW_BOTTOM_PLAYER) {
                            if (PATH_TO_BOTTOMPLAYER != null) {
                                byte[] art = getAlbumArt(PATH_TO_BOTTOMPLAYER);
                                Glide.with(getContext()).load(art)
                                        .placeholder(R.drawable.ic_music_cover)
                                        .into(albumArt);
                                songName.setText(_SONG_NAME_);
                                songArtist.setText(_ARTIST_NAME_);
                            }
                        }
                        if (TRACK_PLAYING) {
                            playButton.setImageResource(R.drawable.ic_round_pause);
                        } else {
                            playButton.setImageResource(R.drawable.ic_round_play_arrow);
                        }
                    }
                }
            }
        });
        skipPrevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (musicService != null) {
                    musicService.prevButtonClicked();
                    if (getActivity() != null) {
                        SharedPreferences.Editor editor = getActivity().getSharedPreferences(MUSIC_FILE_LAST_PLAYED, MODE_PRIVATE).edit();
                        editor.putString(MUSIC_FILE, musicService.songDataArrayList.get(musicService.position).getPath());
                        editor.putString(ARTIST_NAME, musicService.songDataArrayList.get(musicService.position).getArtist());
                        editor.putString(SONG_NAME, musicService.songDataArrayList.get(musicService.position).getTitle());
                        editor.apply();
                        SharedPreferences preferences = getActivity().getSharedPreferences(MUSIC_FILE_LAST_PLAYED, MODE_PRIVATE);
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
                        if (SHOW_BOTTOM_PLAYER) {
                            if (PATH_TO_BOTTOMPLAYER != null) {
                                byte[] art = getAlbumArt(PATH_TO_BOTTOMPLAYER);
                                Glide.with(getContext()).load(art)
                                        .placeholder(R.drawable.ic_music_cover)
                                        .into(albumArt);
                                songName.setText(_SONG_NAME_);
                                songArtist.setText(_ARTIST_NAME_);
                            }
                        }
                        if (TRACK_PLAYING) {
                            playButton.setImageResource(R.drawable.ic_round_pause);
                        } else {
                            playButton.setImageResource(R.drawable.ic_round_play_arrow);
                        }
                    }
                }
            }
        });
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (musicService != null) {
                    musicService.playPauseButtonClicked();
                    if (musicService.isPlaying()) {
                        playButton.setImageResource(R.drawable.ic_round_pause);
                    } else {
                        playButton.setImageResource(R.drawable.ic_round_play_arrow);
                    }
                }
            }
        });
        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        if (SHOW_BOTTOM_PLAYER) {
            if (PATH_TO_BOTTOMPLAYER != null) {
                byte[] art = getAlbumArt(PATH_TO_BOTTOMPLAYER);
                Glide.with(getContext()).load(art)
                        .placeholder(R.drawable.ic_music_cover)
                        .into(albumArt);
                songName.setText(_SONG_NAME_);
                songArtist.setText(_ARTIST_NAME_);
                Intent intent = new Intent(getContext(), MusicService.class);
                if (getContext() != null) {
                    getContext().bindService(intent, NowPlayingBottomFragment.this, Context.BIND_AUTO_CREATE);
                }
            }
            if (TRACK_PLAYING) {
                playButton.setImageResource(R.drawable.ic_round_pause);
            } else {
                playButton.setImageResource(R.drawable.ic_round_play_arrow);
            }
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        if (getContext() != null) {
//            getContext().unbindService(NowPlayingBottomFragment.this);
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

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        MusicService.LocalBinder binder = (MusicService.LocalBinder) iBinder;
        musicService = binder.getService();
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        musicService = null;
    }
}

