package com.devlover.musicplayer.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaMetadataRetriever;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.ImageViewCompat;
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.devlover.musicplayer.R;
import com.devlover.musicplayer.activity.AlbumActivity;
import com.devlover.musicplayer.activity.NowPlayingActivity;
import com.devlover.musicplayer.model.SongData;

import java.util.ArrayList;

public class AlbumTracksAdapter extends RecyclerView.Adapter<AlbumTracksAdapter.AlbumTracksViewHolder> {
    private Context context;
    public static ArrayList<SongData> albumFiles;
    View view;

    public AlbumTracksAdapter(Context context, ArrayList<SongData> albumFiles) {
        this.context = context;
        this.albumFiles = albumFiles;
    }

    @NonNull
    @Override
    public AlbumTracksViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(context).inflate(R.layout.album_track_item, parent, false);
        return new AlbumTracksViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlbumTracksViewHolder holder, int position) {
        holder.trackName.setText(albumFiles.get(position).getTitle());
        holder.artist_album_name.setText(context.getResources().getString(R.string.artist_album, albumFiles.get(position).getArtist(), albumFiles.get(position).getAlbum()));
        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {

                byte[] image = getAlbumArt(albumFiles.get(position).getPath());
                if (image != null) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
                    Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                        @Override
                        public void onGenerated(@Nullable Palette palette) {
                            Palette.Swatch swatch = palette.getDominantSwatch();
                            if (swatch != null) {
                                holder.trackName.setTextColor(swatch.getBodyTextColor());
                                holder.artist_album_name.setTextColor(swatch.getBodyTextColor());
                            } else {
                                holder.trackName.setTextColor(Color.rgb(0,0,0));
                                holder.artist_album_name.setTextColor(Color.rgb(0,0,0));
                            }
                        }
                    });
                }else{
                    Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_music_cover);
                    Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                        @Override
                        public void onGenerated(@Nullable Palette palette) {
                            Palette.Swatch swatch = palette.getDominantSwatch();
                            if (swatch != null) {
                                holder.trackName.setTextColor(swatch.getBodyTextColor());
                                holder.artist_album_name.setTextColor(swatch.getBodyTextColor());
                            } else {
                                holder.trackName.setTextColor(Color.rgb(0,0,0));
                                holder.artist_album_name.setTextColor(Color.rgb(0,0,0));
                            }
                        }
                    });
                }
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, NowPlayingActivity.class);
                        intent.putExtra("sender", "fromAlbumTracks");
                        intent.putExtra("position", position);
                        context.startActivity(intent);
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return albumFiles.size();
    }

    public class AlbumTracksViewHolder extends RecyclerView.ViewHolder {
        //        ImageView album_image;
        TextView artist_album_name, trackName;

        public AlbumTracksViewHolder(@NonNull View itemView) {
            super(itemView);
//            album_image = view.findViewById(R.id.album_art_item);
            trackName = view.findViewById(R.id.albums_song_name);
            artist_album_name = view.findViewById(R.id.albums_artist_album_name);
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
}
