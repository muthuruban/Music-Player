package com.devlover.musicplayer.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.devlover.musicplayer.R;
import com.devlover.musicplayer.adapters.AlbumAdapter;
import com.devlover.musicplayer.adapters.MusicAdapter;
import com.devlover.musicplayer.model.SongData;

import java.util.ArrayList;

import static com.devlover.musicplayer.activity.MainActivity.albums;
import static com.devlover.musicplayer.activity.MainActivity.songDataArrayList;

public class AlbumsFragment extends Fragment {
    RecyclerView recyclerView;
    AlbumAdapter albumAdapter;
    ArrayList<SongData> songFiles;
    public AlbumsFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.albums_fragment,container,false);
        recyclerView = view.findViewById(R.id.albums_list);
        recyclerView.setHasFixedSize(true);
        if (!(albums.size() < 1)) {
            albumAdapter = new AlbumAdapter(getContext(), albums);
            recyclerView.setAdapter(albumAdapter);
            recyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));
        }
        return view;
    }
}
