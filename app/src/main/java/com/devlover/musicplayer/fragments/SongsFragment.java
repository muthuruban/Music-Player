package com.devlover.musicplayer.fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.devlover.musicplayer.R;
import com.devlover.musicplayer.activity.MainActivity;
import com.devlover.musicplayer.adapters.MusicAdapter;
import com.devlover.musicplayer.model.SongData;

import java.util.ArrayList;

import static com.devlover.musicplayer.activity.MainActivity.songDataArrayList;

public class SongsFragment extends Fragment {

    RecyclerView recyclerView;
    public static MusicAdapter musicAdapter;
    ArrayList<SongData> songFiles;

    public SongsFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.songs_fragment, container, false);
        DividerItemDecoration itemDecorator = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        itemDecorator.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.divider));
        recyclerView = view.findViewById(R.id.song_list);
        recyclerView.setHasFixedSize(true);
        if (!(songDataArrayList.size() < 1)) {
            musicAdapter = new MusicAdapter(getContext(), songDataArrayList);
            recyclerView.setAdapter(musicAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
            recyclerView.addItemDecoration(itemDecorator);
        }
        return view;
    }


}
