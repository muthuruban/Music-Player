package com.devlover.musicplayer.adapters;

import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.devlover.musicplayer.R;
import com.devlover.musicplayer.activity.NowPlayingActivity;
import com.devlover.musicplayer.model.SongData;

import java.util.ArrayList;


public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.SongViewHolder> {

    private Context context;
    public static ArrayList<SongData> songDataArrayList;

    public MusicAdapter(Context context, ArrayList<SongData> songDataArrayList) {
        this.songDataArrayList = songDataArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.music_item, parent, false);
        return new SongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SongViewHolder holder, int position) {
        holder.songName.setText(songDataArrayList.get(position).getTitle());
        holder.albumArtistName.setText(context.getResources().getString(R.string.artist_album, songDataArrayList.get(position).getArtist(), songDataArrayList.get(position).getAlbum()));
        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                byte[] image = getAlbumArt(songDataArrayList.get(position).getPath());
                if (image != null) {
                    Glide.with(context).asDrawable()
                            .load(image)
                            .into(holder.albumArt);
                }
                else {
                    Glide.with(context)
                            .load(R.drawable.ic_music_cover)
                            .into(holder.albumArt);
                }
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, NowPlayingActivity.class);
                intent.putExtra("position",position);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return songDataArrayList.size();
    }

    public class SongViewHolder extends RecyclerView.ViewHolder {
        TextView songName;
        ImageView albumArt;
        TextView albumArtistName;

        public SongViewHolder(@NonNull View itemView) {
            super(itemView);
            songName = itemView.findViewById(R.id.song_name);
            albumArtistName = itemView.findViewById(R.id.artist_album_name);
            albumArt = itemView.findViewById(R.id.album_art_item);
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
    public void updateTrackList(ArrayList<SongData> updateSongData){
        songDataArrayList = new ArrayList<>();
        songDataArrayList.addAll(updateSongData);
        notifyDataSetChanged();
    }

}
