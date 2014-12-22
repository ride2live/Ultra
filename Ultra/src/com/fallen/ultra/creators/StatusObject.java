package com.fallen.ultra.creators;

import com.fallen.ultra.utils.Params;

public class StatusObject {
	private int asyncStatus = Params.STATUS_NONE;
	private int playerStatus = Params.STATUS_NONE;
	private String artist = null;
	private String track = null;
	private String error = null;

	public StatusObject(int status, boolean isFromAsync) {
		// TODO Auto-generated constructor stub
		if (isFromAsync)
			setAsyncStatus(status);
		else
			setPlayerStatus(status);
	}

	public StatusObject(int status, String artist, String track) {
		// TODO Auto-generated constructor stub
		setAsyncStatus(status);
		setArtist(artist);
		setTrack(track);
	}

	public int getAsyncStatus() {
		return asyncStatus;
	}

	public void setAsyncStatus(int asyncStatus) {
		this.asyncStatus = asyncStatus;
	}

	public int getPlayerStatus() {
		return playerStatus;
	}

	public void setPlayerStatus(int playerStatus) {
		this.playerStatus = playerStatus;
	}

	public void setArtist(String artist) {
		this.artist = artist;
	}

	public void setTrack(String track) {
		this.track = track;
	}

	public void setError(String error) {
		this.error = error;
	}



	public void addInfo(StatusObject additionalObjectToStore) {

	}

	public String getTrack() {
		return track;
	}

	public int getStatus() {
		return asyncStatus;
	}

	public String getArtist() {
		return artist;
	}

	public String getError() {
		return error;
	}

}
