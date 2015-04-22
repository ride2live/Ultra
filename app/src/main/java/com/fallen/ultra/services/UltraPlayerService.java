package com.fallen.ultra.services;

import java.io.File;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.fallen.ultra.activities.MainUltraActivity;
import com.fallen.ultra.async.AsyncCheckLegal;
import com.fallen.ultra.async.AsyncImageLoader;
import com.fallen.ultra.async.AsyncLoadStream;
import com.fallen.ultra.callbacks.AsyncToServiceCallback;
import com.fallen.ultra.callbacks.ImageLoader;
import com.fallen.ultra.callbacks.PlayerToServiceCallBack;
import com.fallen.ultra.callbacks.ServiceToActivityCallback;
import com.fallen.ultra.com.fallen.ultra.model.StatusObjectPlayer;
import com.fallen.ultra.com.fallen.ultra.model.TempAsyncStatus;
import com.fallen.ultra.creators.NotificationCreator;
import com.fallen.ultra.com.fallen.ultra.model.StatusObjectOverall;
import com.fallen.ultra.listeners.PhoneCallCallback;
import com.fallen.ultra.utils.Params;
import com.fallen.ultra.utils.UtilsUltra;
import com.fallen.ultra.utils.media.MediaPlayerExtended;
import com.fallen.ultra.callbacks.ServiceToPlayerCallback;

public class UltraPlayerService extends Service implements
		ImageLoader, PhoneCallCallback, AsyncToServiceCallback, PlayerToServiceCallBack {
	private NotificationManager mNotificationManager;
	// private MediaPlayer mMediaPlayer;
	private final IBinder mBinder = new LocalPlayerBinder();
	private ServiceToActivityCallback serviceToActivityCallback;
	private AsyncLoadStream mAsycLoadStream;
	// object for newly binded or rebinded activities
	private StatusObjectOverall statusOveralObject;
    private String currentArtist;
	private String currentTrack;
	private ServiceToPlayerCallback serviceToPlayerCallback;
	private boolean isRunningBackground = false;
    private String currentQuality;

    @Override
	public IBinder onBind(Intent intent) {
		System.out.println("onBind Service");
		statusOveralObject = new StatusObjectOverall();
		// IncomingCallReciever phoneCallReciver = new
		// IncomingCallReciever(this);
		// registerReceiver(phoneCallReciver, null);
		return mBinder;
	}

    @Override
    public void onStatusChanged(TempAsyncStatus tempAsyncStatus) {
        int status = tempAsyncStatus.getStatus();
        statusOveralObject.setStatusAsync(status);
        switch (status) {
            case Params.STATUS_BUFFERING:
                if (serviceToPlayerCallback != null)
                    serviceToPlayerCallback.onBuffered();
                createNotify();
                break;
            case Params.STATUS_NEW_TITLE:
                if (statusOveralObject.isRecconecting())
                    statusOveralObject.setRecconecting(false);
                currentArtist = tempAsyncStatus.getArtist();
                currentTrack = tempAsyncStatus.getTrack();
                loadImage(currentArtist, currentTrack);
                statusOveralObject.setArtist(currentArtist);
                statusOveralObject.setTrack(currentTrack);
                updateNotify();
                break;
            case Params.STATUS_SOCKET_CREATING:
                UtilsUltra.printLog("status socket created recieved");
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        if (serviceToPlayerCallback != null)
                            serviceToPlayerCallback.onSocketCreated();
                    }
                }, 1000);
                break;
            case Params.STATUS_STREAM_ENDS:
				stopStream();
                break;


            default:
                break;
        }
        if (serviceToActivityCallback!=null && status!=Params.STATUS_SOCKET_CREATING)
            serviceToActivityCallback.onUpdateStatus(statusOveralObject);
    }

    @Override
    public void onStatusPlayerChange(StatusObjectPlayer statusObjectPlayer) {
        if (statusOveralObject!=null ) {
            statusOveralObject.setStatusPlayer(statusObjectPlayer.getPlayerStatus());
            if (serviceToActivityCallback !=null)
                serviceToActivityCallback.onUpdateStatus(statusOveralObject);
        }
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
		loadImage(currentArtist, currentTrack);

	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (Params.USE_CHECKER) {
			AsyncCheckLegal checker = new AsyncCheckLegal(
					getApplicationContext());
			checker.execute();
		}
		if (intent != null) {

			if (intent.hasExtra(Params.ACTION_FROM_CONTROLS)) {
				int flagToAction = intent.getIntExtra(
						Params.ACTION_FROM_CONTROLS, 0);
				switch (flagToAction) {
				case Params.FLAG_PLAY:
					UtilsUltra.printLog("onStrartCommand play");
                    setNewCurrentGuality(intent);
                    playStream();
					break;
				case Params.FLAG_STOP:
					UtilsUltra.printLog("onStrartCommand stop");
                    if (statusOveralObject!=null)
                        statusOveralObject.setStatusAsync(Params.STATUS_STOPPING_IN_PROCESS);

                    stopStream();
                    break;
                case Params.FLAG_RESTART:
                    UtilsUltra.printLog("onStrartCommand restart");
                    if (statusOveralObject!=null)
                        statusOveralObject.setRecconecting(true);
                    setNewCurrentGuality(intent);
                    stopStream();
                    break;
				default:
					UtilsUltra.printLog("onStrartCommand default");
					break;
				}

			} else if (intent.hasExtra(Params.ACTION_FROM_BROADCAST_PHONE)) {
				UtilsUltra.printLog("ACTION_FROM_BROADCAST_PHONE");
				if (serviceToPlayerCallback != null)
					serviceToPlayerCallback.onPhoneAction(intent.getIntExtra(
							Params.ACTION_FROM_BROADCAST_PHONE, 0));

			}
            else if (intent.hasExtra(Params.ACTION_FROM_BROADCAST_NOISY))
            {
                if (serviceToPlayerCallback != null)
                    serviceToPlayerCallback.onNoisy();

            }
            else {
				UtilsUltra.printLog("onStrartCommand no extra");
			}
		}
		System.out.println("startcomm");
		return START_NOT_STICKY;
	}
    public void playStream()
    {
        if (serviceToPlayerCallback == null) {
            serviceToPlayerCallback = new MediaPlayerExtended(this, this);

        }

        if (mAsycLoadStream == null) {
            mAsycLoadStream = new AsyncLoadStream(this);

            mAsycLoadStream.execute(currentQuality);
        }
    }
	public void setNewCurrentGuality(Intent intent) {
        String qualityUrl = Params.ULTRA_URL_192;
        if (intent.hasExtra(Params.KEY_PREFERENCES_QUALITY_FIELD)) {
             qualityUrl= intent
                    .getStringExtra(Params.KEY_PREFERENCES_QUALITY_FIELD);
        }
        currentQuality = qualityUrl;


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
        TempAsyncStatus t = new TempAsyncStatus();
        t.setStatus(Params.STATUS_STOPED);
        onStatusChanged(t);
        if (statusOveralObject!=null && statusOveralObject.isRecconecting())
           playStream();

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

		this.serviceToActivityCallback = activity;
		//serviceToActivityCallback.onRebindStatus(statusOveralObject);

	}

//	@Override
//	public void updateAsyncStatusObject(StatusObjectOverall sObject) {
//
//		if (sObject.isAsync()) {
//			int status = sObject.getStatusAsync();
//			switch (status) {
//			case Params.STATUS_BUFFERING:
//				if (serviceToPlayerCallback != null)
//					serviceToPlayerCallback.onBuffered();
//				statusOveralObject.setStatusAsync(status);
//				createNotify();
//				break;
//			case Params.STATUS_NEW_TITLE:
//
//				currentArtist = sObject.getArtist();
//				currentTrack = sObject.getTrack();
//				loadImage(currentArtist, currentTrack);
//				statusOveralObject.setArtist(currentArtist);
//				statusOveralObject.setTrack(currentTrack);
//				updateNotify();
//				break;
//			case Params.STATUS_SOCKET_CREATING:
//				UtilsUltra.printLog("status socket created recieved");
//				new Handler().postDelayed(new Runnable() {
//
//					@Override
//					public void run() {
//						if (serviceToPlayerCallback != null)
//							serviceToPlayerCallback.onSocketCreated();
//					}
//				}, 1000);
//				break;
//			case Params.STATUS_STREAM_ENDS:
//				statusOveralObject.setPlayerStatus(Params.STATUS_STOPED);
//				stopStream();
//
//			default:
//				break;
//			}
//		} else {
//			int status = sObject.getPlayerStatus();
//			statusOveralObject.setPlayerStatus(status);
//		}
//	}

	private void loadImage(String artist, String track) {
        if (!UtilsUltra.getIsArtEnabledFromPreferences(getSharedPreferences(Params.KEY_PREFERENCES ,0)))
            return;
		File file = new File(getApplicationContext().getFilesDir(),
				Params.TEMP_FILE_NAME);
		if (serviceToActivityCallback != null) {

			if (artist != null && !artist.equals(Params.NO_TITLE)
					&& !artist.equals("") && track != null) {

				AsyncImageLoader async = new AsyncImageLoader(file, this);
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
					async.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
							artist, track);
				else
					async.execute(artist, track);

			} else {
				UtilsUltra.printLog("artist wrong on load image");
				file.delete();
				onImageBuffered();
			}
		}
	}

	public StatusObjectOverall getStatusObject() {
		return statusOveralObject;
	}

	@Override
	public void onImageBuffered() {
		// TODO Auto-generated method stub
		if (serviceToActivityCallback != null)
			serviceToActivityCallback.onImageBuffered();
	}

	@Override
	public void onRecievePhoneEvent(String action) {
		// TODO Auto-generated method stub
		UtilsUltra.printLog("RECIEVE PHONE CALL " + action);
	}

}
