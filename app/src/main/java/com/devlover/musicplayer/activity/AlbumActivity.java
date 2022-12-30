package com.devlover.musicplayer.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.devlover.musicplayer.R;
import com.devlover.musicplayer.adapters.AlbumTracksAdapter;
import com.devlover.musicplayer.model.SongData;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.ArrayList;

import static com.devlover.musicplayer.activity.MainActivity.songDataArrayList;

public class AlbumActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    ImageView albumArt;
    String albumName;
    ArrayList<SongData> albumTrack = new ArrayList<>();
    AlbumTracksAdapter albumTracksAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);
        recyclerView = findViewById(R.id.album_track_list);
        albumArt = findViewById(R.id.curr_album_art);
        albumName = getIntent().getStringExtra("albumName");
        int j = 0;
        for (int i = 0; i < songDataArrayList.size(); i++) {
            if (albumName.equals(songDataArrayList.get(i).getAlbum())) {
                albumTrack.add(j, songDataArrayList.get(i));
                j++;
            }
        }
        byte[] image = getAlbumArt(albumTrack.get(0).getPath());
        Bitmap bitmap;
        if(image!=null){
//            Glide.with(this).load(image).into(albumArt);
            bitmap = BitmapFactory.decodeByteArray(image,0,image.length);
            ImageAnimation(this,albumArt,bitmap);
            Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                @Override
                public void onGenerated(@Nullable Palette palette) {
                    Palette.Swatch swatch = palette.getDominantSwatch();
                    if (swatch != null) {
                        ImageView gradient = findViewById(R.id.album_image_over_gradient);
                        RelativeLayout container = findViewById(R.id.album_container);
                        gradient.setBackgroundResource(R.drawable.gradient_bg);
                        container.setBackgroundResource(R.drawable.main_bg);
                        GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, new int[]{swatch.getRgb(), 0x00000000});
                        gradient.setBackground(gradientDrawable);
                        GradientDrawable gradientDrawableBg = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, new int[]{swatch.getRgb(), swatch.getRgb()});
                        container.setBackground(gradientDrawableBg);
//                        trackName.setTextColor(swatch.getBodyTextColor());
//                        artistName.setTextColor(swatch.getTitleTextColor());
//                        albumName.setTextColor(swatch.getBodyTextColor());
//                        currentDuration.setTextColor(swatch.getBodyTextColor());
//                        totalDuration.setTextColor(swatch.getBodyTextColor());
//                        seekBarCtrl.setBackgroundColor(swatch.getRgb());
//                        seekBarCtrl.getProgressDrawable().setColorFilter(swatch.getBodyTextColor(), PorterDuff.Mode.MULTIPLY);
//                        seekBarCtrl.getThumb().setColorFilter(swatch.getBodyTextColor(), PorterDuff.Mode.SRC_ATOP);
                    } else {
                        ImageView gradient = findViewById(R.id.album_image_over_gradient);
                        RelativeLayout container = findViewById(R.id.album_container);
                        gradient.setBackgroundResource(R.drawable.gradient_bg);
                        container.setBackgroundResource(R.drawable.main_bg);
                        GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, new int[]{swatch.getRgb(), 0x00000000});
                        gradient.setBackground(gradientDrawable);
                        GradientDrawable gradientDrawableBg = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, new int[]{swatch.getRgb(), swatch.getRgb()});
                        container.setBackground(gradientDrawableBg);
//                        trackName.setTextColor(swatch.getBodyTextColor());
//                        artistName.setTextColor(swatch.getTitleTextColor());
//                        albumName.setTextColor(swatch.getBodyTextColor());
                    }
                }
            });
        }else{
//            Glide.with(this).load(R.drawable.ic_huawei_music_cover_round).into(albumArt);
            bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.ic_music_cover);
            ImageAnimation(this,albumArt,bitmap);
            Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                @Override
                public void onGenerated(@Nullable Palette palette) {
                    Palette.Swatch swatch = palette.getDominantSwatch();
                    if (swatch != null) {
                        ImageView gradient = findViewById(R.id.album_image_over_gradient);
                        RelativeLayout container = findViewById(R.id.album_container);
                        gradient.setBackgroundResource(R.drawable.gradient_bg);
                        container.setBackgroundResource(R.drawable.main_bg);
                        GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, new int[]{swatch.getRgb(), 0x00000000});
                        gradient.setBackground(gradientDrawable);
                        GradientDrawable gradientDrawableBg = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, new int[]{swatch.getRgb(), swatch.getRgb()});
                        container.setBackground(gradientDrawableBg);
//                        trackName.setTextColor(swatch.getBodyTextColor());
//                        artistName.setTextColor(swatch.getTitleTextColor());
//                        albumName.setTextColor(swatch.getBodyTextColor());
//                        currentDuration.setTextColor(swatch.getBodyTextColor());
//                        totalDuration.setTextColor(swatch.getBodyTextColor());
//                        seekBarCtrl.setBackgroundColor(swatch.getRgb());
//                        seekBarCtrl.getProgressDrawable().setColorFilter(swatch.getBodyTextColor(), PorterDuff.Mode.MULTIPLY);
//                        seekBarCtrl.getThumb().setColorFilter(swatch.getBodyTextColor(), PorterDuff.Mode.SRC_ATOP);
                    } else {
                        ImageView gradient = findViewById(R.id.album_image_over_gradient);
                        RelativeLayout container = findViewById(R.id.album_container);
                        gradient.setBackgroundResource(R.drawable.gradient_bg);
                        container.setBackgroundResource(R.drawable.main_bg);
                        GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, new int[]{swatch.getRgb(), 0x00000000});
                        gradient.setBackground(gradientDrawable);
                        GradientDrawable gradientDrawableBg = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, new int[]{swatch.getRgb(), swatch.getRgb()});
                        container.setBackground(gradientDrawableBg);
//                        trackName.setTextColor(swatch.getBodyTextColor());
//                        artistName.setTextColor(swatch.getTitleTextColor());
//                        albumName.setTextColor(swatch.getBodyTextColor());
                    }
                }
            });
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
    protected void onResume() {
        super.onResume();
        DividerItemDecoration itemDecorator = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        itemDecorator.setDrawable(ContextCompat.getDrawable(this, R.drawable.divider));
        if(!(albumTrack.size()<1)){
            albumTracksAdapter = new AlbumTracksAdapter(this,albumTrack);
            recyclerView.setAdapter(albumTracksAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this,RecyclerView.VERTICAL,false));
            recyclerView.addItemDecoration(itemDecorator);
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
}