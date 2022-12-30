package com.devlover.musicplayer.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.view.menu.MenuView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.devlover.musicplayer.R;
import com.devlover.musicplayer.adapters.MusicAdapter;
import com.devlover.musicplayer.adapters.ViewPagerAdapter;
import com.devlover.musicplayer.model.SongData;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

import static com.devlover.musicplayer.activity.MainActivity.songDataArrayList;

public class HomeFragment extends Fragment implements SearchView.OnQueryTextListener {
    TabLayout tabLayout;
    ViewPager viewPager;
    ViewPagerAdapter viewPagerAdapter;
    Toolbar topToolBar;
    SearchView searchBox;
    String SONG_SORT_PREF = "SortingOrder";

    public HomeFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.home_fragment, container, false);
        viewPager = (ViewPager) view.findViewById(R.id.view_pager);
        tabLayout = view.findViewById(R.id.tab_layout);
        viewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager());
        viewPager.setSaveEnabled(true);
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        topToolBar = view.findViewById(R.id.home_toolbar);
        Menu menu = topToolBar.getMenu();
        MenuItem menuItem = menu.findItem(R.id.search_button);
        MenuItem menuItem2 = menu.findItem(R.id.options);
        searchBox = (SearchView) menuItem.getActionView();
        searchBox.setOnQueryTextListener(this);
        topToolBar.setOnMenuItemClickListener(this::onOptionsItemSelected);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        String inputFromUsr = s.toLowerCase();
        ArrayList<SongData> songFiles = new ArrayList<>();
        for (SongData song : songDataArrayList) {
            if (song.getTitle().toLowerCase().contains(inputFromUsr)) {
                songFiles.add(song);
            }
        }
        SongsFragment.musicAdapter.updateTrackList(songFiles);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        SharedPreferences.Editor editor = getActivity().getSharedPreferences(SONG_SORT_PREF, Context.MODE_PRIVATE).edit();
        switch (item.getItemId()) {
            case R.id.sort_by_name:
                Toast.makeText(getContext(),"Sort By Name",Toast.LENGTH_SHORT).show();
                editor.putString("sorting", "sortByName");
                editor.apply();
                getActivity().recreate();
                break;
            case R.id.sort_by_date:
                Toast.makeText(getContext(),"Sort By Date",Toast.LENGTH_SHORT).show();
                editor.putString("sorting", "sortByDate");
                editor.apply();
                getActivity().recreate();
                break;
            case R.id.sort_by_size:
                Toast.makeText(getContext(),"Sort By Size",Toast.LENGTH_SHORT).show();
                editor.putString("sorting", "sortBySize");
                editor.apply();
                getActivity().recreate();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
