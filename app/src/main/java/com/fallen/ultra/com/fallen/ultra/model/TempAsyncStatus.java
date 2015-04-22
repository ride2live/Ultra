package com.fallen.ultra.com.fallen.ultra.model;

import com.fallen.ultra.utils.Params;

/**
 * Created by Nolwe on 15.04.2015.
 */
public class TempAsyncStatus {
    private int status = Params.STATUS_NONE;
    private String artist = null;
    private String track = null;
    private String error = null;

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getTrack() {
        return track;
    }

    public void setTrack(String track) {
        this.track = track;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public void setStatus(int statusNewTitle, String artist, String track) {
        setArtist(artist);
        setTrack(track);
        setStatus(statusNewTitle);
    }
}
