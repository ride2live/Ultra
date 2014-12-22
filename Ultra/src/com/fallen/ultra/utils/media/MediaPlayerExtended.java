package com.fallen.ultra.utils.media;

import java.util.ArrayList;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;

import com.fallen.ultra.callbacks.Observer;
import com.fallen.ultra.callbacks.ObserverableMediaPlayer;
import com.fallen.ultra.creators.StatusObject;
import com.fallen.ultra.utils.Params;
import com.fallen.ultra.utils.UtilsUltra;

public class MediaPlayerExtended extends MediaPlayer implements ObserverableMediaPlayer  {

	private boolean isPrepeared = false;
	Context context;
	private boolean isBuffered = false;
	//private boolean isSocketConnected = false;
	private boolean isCanceled = false;
	private boolean isDataWasSetSuccessfully = false; 
	private ArrayList<Observer> observers;

	public MediaPlayerExtended(Context context) {
		// TODO Auto-generated constructor stub
		this.context = context;
		
		/*
		 * try { mMediaPlayer = new MediaPlayer();
		 * mMediaPlayer.setDataSource(this,
		 * Uri.parse(Params.LOCAL_SOCKET_STREAM_IP));
		 * mMediaPlayer.prepareAsync();
		 * 
		 * //mMediaPlayer = MediaPlayer.create(getApplicationContext(), //
		 * Uri.parse(Params.LOCAL_SOCKET_STREAM_IP));
		 * 
		 * } catch (Exception e) { e.printStackTrace(); }
		 */

	}

	@Override
	public boolean onSocketCreated() {
		
		boolean isSuccess = preparePlayer();
		isDataWasSetSuccessfully = isSuccess;
		UtilsUltra.printLog("onSocket and setData = " + isSuccess, "Ultra", Log.VERBOSE);
		return  isSuccess; 

	}

	private void playMedia() {
		// TODO Auto-generated method stub
		if (!isPlaying())
		{
			try {
				start();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		else
		{
			UtilsUltra.printLog("player is already playing", "Ultra", Log.ERROR);
		}
		
	}

	private boolean preparePlayer() {
		// TODO Auto-generated method stub
		boolean isSetDataSuccess = false;
		try {
			
			setDataSource(context, Uri.parse(Params.LOCAL_SOCKET_STREAM_IP));
			isSetDataSuccess = true;
			setOnPreparedListener(new OnPreparedListener() {
				
				@Override
				public void onPrepared(MediaPlayer mp) {
					// TODO Auto-generated method stub
					isPrepeared = true;
					tryTostart();
				}
			});
			prepareAsync();
			

		} catch (Exception e) {
			e.printStackTrace();
			isSetDataSuccess = false;
		}
		return isSetDataSuccess;
	}

	private void tryTostart() {
		// TODO Auto-generated method stub
		if (isPrepeared && isBuffered && isDataWasSetSuccessfully && !isCanceled)
			playMedia();
	}

	@Override
	public void onBuffered() {
		// TODO Auto-generated method stub
		isBuffered = true;
		UtilsUltra.printLog("onBuddered media", "Ultra", Log.VERBOSE);
		tryTostart();
	}

	@Override
	public boolean stopAndReleasePlayer() {
		
		// TODO Auto-generated method stub
		boolean isSuccess = false;
		try {
			stop();
			release();
			isSuccess = true;
		} catch (Exception e) {
			UtilsUltra.printLog("Something realy bad happens! Cant stop media", null, Log.ERROR);
			e.printStackTrace();
			System.exit(0);
		}
		return isSuccess;
	}

	@Override
	public boolean setCanceled() {
		// TODO Auto-generated method stub
		isCanceled = true;
		return stopAndReleasePlayer();
	}

	@Override
	public void registerObserver(Observer o) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeObserver(Observer o) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void notifyObservers(StatusObject sObject) {
		// TODO Auto-generated method stub
		
	}

	
}
