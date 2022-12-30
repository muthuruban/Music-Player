package com.devlover.musicplayer.adapters;

import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.devlover.musicplayer.fragments.ArtistsFragment;
import com.devlover.musicplayer.fragments.AlbumsFragment;
import com.devlover.musicplayer.fragments.SongsFragment;

import org.jetbrains.annotations.NotNull;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    public ViewPagerAdapter(@NotNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        if (position == 0)
            fragment = new SongsFragment();
        else if (position == 1)
            fragment = new AlbumsFragment();
//        else if (position == 2)
//            fragment = new AlbumsFragment();
        return fragment;
    }

    @Override
    public void restoreState(@Nullable Parcelable state, @Nullable ClassLoader loader) {
        try {
            super.restoreState(state, loader);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        String title = null;
        if (position == 0)
            title = "Songs";
        else if (position == 1)
            title = "Albums";
//        else if (position == 2)
//            title = "Albums";
        return title;
    }
}
