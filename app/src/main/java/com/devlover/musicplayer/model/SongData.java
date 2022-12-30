package com.devlover.musicplayer.model;

import android.net.Uri;

public class SongData {
    private String path;
    private String title;
    private String artist;
    private String album;
    private String duration;
    private Uri uri_data;

    public SongData(String path, String title, String artist, String album, String duration, Uri uri_data) {
        this.path = path;
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.duration = duration;
        this.uri_data = uri_data;
    }

    public SongData() {
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public Uri getUri_data() {
        return uri_data;
    }

    public void setUri_data(Uri uri_data) {
        this.uri_data = uri_data;
    }
}
