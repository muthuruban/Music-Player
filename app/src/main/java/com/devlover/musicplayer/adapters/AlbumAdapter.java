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
import com.devlover.musicplayer.activity.AlbumActivity;
import com.devlover.musicplayer.model.SongData;

import java.util.ArrayList;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.AlbumViewHolder> {
    private Context context;
    private ArrayList<SongData> albumFiles;
    View view;

    public AlbumAdapter(Context context, ArrayList<SongData> albumFiles) {
        this.context = context;
        this.albumFiles = albumFiles;
    }

    @NonNull
    @Override
    public AlbumViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(context).inflate(R.layout.album_item, parent, false);
        return new AlbumViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlbumViewHolder holder, int position) {
        holder.album_name.setText(albumFiles.get(position).getAlbum());
        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {

                byte[] image = getAlbumArt(albumFiles.get(position).getPath());
                if (image != null) {
                    Glide.with(context).asDrawable()
                            .load(image)
                            .into(holder.album_image);
                } else {
                    Glide.with(context)
                            .load(R.drawable.ic_music_cover)
                            .into(holder.album_image);
                }
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, AlbumActivity.class);
                intent.putExtra("albumName",albumFiles.get(position).getAlbum());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return albumFiles.size();
    }

    public class AlbumViewHolder extends RecyclerView.ViewHolder {
        ImageView album_image;
        TextView album_name;

        public AlbumViewHolder(@NonNull View itemView) {
            super(itemView);
            album_image = view.findViewById(R.id.album_image);
            album_name = view.findViewById(R.id.album_name_frag);
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
