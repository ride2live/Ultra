package com.fallen.ultra.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.fallen.ultra.activities.MainUltraActivity;
import com.fallen.ultra.async.AsyncCheckLegal;
import com.fallen.ultra.async.AsyncLoadStream;
import com.fallen.ultra.callbacks.Observer;
import com.fallen.ultra.callbacks.ObserverableMediaPlayer;
import com.fallen.ultra.callbacks.ServiceToActivityCallback;
import com.fallen.ultra.creators.NotificationCreator;
import com.fallen.ultra.creators.StatusObject;
import com.fallen.ultra.utils.Params;
import com.fallen.ultra.utils.UtilsUltra;
import com.fallen.ultra.utils.media.MediaPlayerExtended;

public class UltraPlayerService extends Service implements Observer {
	private NotificationManager mNotificationManager;
	// private MediaPlayer mMediaPlayer;
	private final IBinder mBinder = new LocalPlayerBinder();
	private ServiceToActivityCallback mServiceToActivityCallback;
	private AsyncLoadStream mAsycLoadStream;
	private String currentArtist;

	// object for newly binded or rebinded activities
	private StatusObject statusStreamObject;

	private String currentTrack;
	private ObserverableMediaPlayer serviceToPlayerCallback;
	private boolean isRunningBackground = false;
	private Observer activityObserver;

	@Override
	public IBinder onBind(Intent intent) {
		System.out.println("onBind Service");
		return mBinder;
	}

	public class LocalPlayerBinder extends Binder {
		public UltraPlayerService getService() {
			return UltraPlayerService.this;
		}
	}

	@Override
	public void onRebind(Intent intent) {
		// TODO Auto-generated method stub
		super.onRebind(intent);
		System.out.println("onRebind service");
		if (currentArtist != null || currentTrack != null) {
			if (mServiceToActivityCallback != null)
				mServiceToActivityCallback.newTitleRetrieved(currentArtist,
						currentTrack);
		}

	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (Params.USE_CHECKER) {
			AsyncCheckLegal checker = new AsyncCheckLegal(
					getApplicationContext());
			checker.execute();
		}
		if (intent.hasExtra(Params.ACTION_FROM_CONTROLS)) {
			int flagToAction = intent.getIntExtra(Params.ACTION_FROM_CONTROLS,
					0);
			switch (flagToAction) {
			case Params.FLAG_PLAY:
				UtilsUltra.printLog("onStrartCommand play");
				String qualityUrl = Params.ULTRA_URL_HIGH;
				if (intent.hasExtra(Params.KEY_PREFERENCES_QUALITY_FIELD))
					qualityUrl = intent
							.getStringExtra(Params.KEY_PREFERENCES_QUALITY_FIELD);
				playStream(qualityUrl);
				break;
			case Params.FLAG_STOP:
				UtilsUltra.printLog("onStrartCommand stop");
				stopStream();

			default:
				UtilsUltra.printLog("onStrartCommand default");
				break;
			}
		} else {
			UtilsUltra.printLog("onStrartCommand no extra");
		}
		System.out.println("startcomm");
		return START_NOT_STICKY;
	}

	public void playStream(String qualityURL) {
		if (serviceToPlayerCallback == null)
			serviceToPlayerCallback = new MediaPlayerExtended(this);

		if (mAsycLoadStream == null) {
			mAsycLoadStream = new AsyncLoadStream();
			mAsycLoadStream.registerObserver(this);
			mAsycLoadStream.registerObserver(activityObserver);
			mAsycLoadStream.execute(qualityURL);
		}

	}

	private void createNotify() {
		if (mNotificationManager == null)
			mNotificationManager = UtilsUltra
					.createNotificationManager(getApplicationContext());

		Notification playerNotification = NotificationCreator
				.createPlayerNotification(getApplicationContext(),
						getPackageName(), currentArtist, currentTrack);
		startBackgroundWithNotifyer(playerNotification);
	}

	private void startBackgroundWithNotifyer(Notification playerNotification) {
		try {
			startForeground(Params.NOTIFICATION_ID, playerNotification);
			isRunningBackground = true;
			UtilsUltra.printLog("started background", "Ultra service",
					Log.VERBOSE);
		} catch (Exception e) {
			UtilsUltra.printLog("starting background failed!", "Ultra service",
					Log.ERROR);
		}

	}

	private void updateNotify() {

		if (mNotificationManager != null)
			mNotificationManager.notify(Params.NOTIFICATION_ID,
					NotificationCreator.createPlayerNotification(
							getApplicationContext(), getPackageName(),
							currentArtist, currentTrack));
	}

	private void stoppingBackground() { // stopping service will close notify
		try {
			stopForeground(true);
			UtilsUltra.printLog("stopped background", "Ultra service",
					Log.VERBOSE);
			isRunningBackground = false;
		} catch (Exception e) {
			UtilsUltra.printLog("stopping background failed!", "Ultra service",
					Log.ERROR);
			forceClose();
		}
	}

	private void forceClose() {
		System.exit(0);
	}

	public void stopStream() {

		if (serviceToPlayerCallback != null) {
			boolean isPlayerReleased = serviceToPlayerCallback.setCanceled();
			if (isPlayerReleased)
				serviceToPlayerCallback = null;
			else
				forceClose();
		}
		if (mAsycLoadStream != null) {
			mAsycLoadStream.cancel(true);
			mAsycLoadStream = null;
		}
		stoppingBackground();

	}

	@Override
	public void onCreate() {

		System.out.println("onCreateService");
		super.onCreate();
	}

	@Override
	public boolean onUnbind(Intent intent) {
		if (!isRunningBackground)
			stopSelf();
		System.out.println("onUnbindService");
		return true;
	}

	@Override
	public void onDestroy() {
		System.out.println("onDestroyService");
		super.onDestroy();
	}

	public void setCallback(MainUltraActivity activity) {

		this.mServiceToActivityCallback = activity;
		this.activityObserver = activity;
		if (activity == null && mAsycLoadStream != null) {

			mAsycLoadStream.removeObserver(activity);
		} else if (activity != null && mAsycLoadStream != null)
			mAsycLoadStream.registerObserver(activity);

	}

	@Override
	public void update(StatusObject sObject) {
		// TODO Auto-generated method stub
		// onNewStreamTitleRetrieved
		// onSocketStart
		// onBuffered
		// onconnecting
		switch (sObject.getStatus()) {
		case Params.STATUS_BUFFERING:
			if (serviceToPlayerCallback != null)
				serviceToPlayerCallback.onBuffered();
			createNotify();
			break;
		case Params.STATUS_NEW_TITLE:
			currentArtist = sObject.getArtist();
			currentTrack = sObject.getTrack();
			updateNotify();
			break;
		case Params.STATUS_SOCKET_CREATING:
			new Handler().postDelayed(new Runnable() {

				@Override
				public void run() {
					if (serviceToPlayerCallback != null)
						serviceToPlayerCallback.onSocketCreated();
				}
			}, 1000);
			break;

		default:
			break;
		}
	}

	
	public StatusObject getStatusObject() {
		return statusStreamObject;
	}

}
