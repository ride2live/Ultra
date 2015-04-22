package com.fallen.ultra.com.fallen.ultra.model;

import com.fallen.ultra.utils.Params;

public class StatusObjectOverall {
	private int statusAsync = Params.STATUS_NONE;
    private boolean isRecconecting= false;

    public boolean isRecconecting() {
        return isRecconecting;
    }

    public void setRecconecting(boolean isRecconecting) {
        this.isRecconecting = isRecconecting;
    }

    public int getStatusPlayer() {
        return statusPlayer;
    }

    public void setStatusPlayer(int statusPlayer) {
        this.statusPlayer = statusPlayer;
    }

    private int statusPlayer = Params.STATUS_NONE;
	//private int playerStatus = Params.STATUS_NONE;
	private String artist = "_ _ _";
	private String track = "_ _ _";
	private String error = null;
	//private boolean isAsync = false;
	private boolean isOnRebindKeeper;

	public boolean isOnRebindKeeper() {
		return isOnRebindKeeper;
	}
	/**
	 * use this constructor only for rebind statusAsync keep
	 */
	public StatusObjectOverall() {
		// TODO Auto-generated constructor stub
		isOnRebindKeeper = true;
	}

//	public boolean isAsync() {
//		return isAsync;
//	}

//	public StatusObject(int statusAsync, boolean isFromAsync) {
//		// TODO Auto-generated constructor stub
//		isAsync = isFromAsync;
//		if (isFromAsync)
//			setStatusAsync(statusAsync);
//		else
//			setPlayerStatus(statusAsync);
//	}


	public StatusObjectOverall(int statusAsync, String artist, String track) {
		// TODO Auto-generated constructor stub
		setStatusAsync(statusAsync);
		setArtist(artist);
		setTrack(track);

	}



	public int getStatusAsync() {
		return statusAsync;
	}

	public void setStatusAsync(int asyncStatus) {
		this.statusAsync = asyncStatus;
	}

//	public int getPlayerStatus() {
//		return playerStatus;
//	}

//	public void setPlayerStatus(int playerStatus) {
//		this.playerStatus = playerStatus;
//	}

	public void setArtist(String artist) {
		this.artist = artist;
	}

	public void setTrack(String track) {
		this.track = track;
	}

	public void setError(String error) {
		this.error = error;
	}



	public void addInfo(StatusObjectOverall additionalObjectToStore) {

	}

	public String getTrack() {
		return track;
	}



	public String getArtist() {
		return artist;
	}

	public String getError() {
		return error;
	}


}
