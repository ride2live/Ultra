package com.fallen.ultra.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;

import com.fallen.ultra.async.AsyncLoadStream;
import com.fallen.ultra.callbacks.AsyncLoadStreamCallback;
import com.fallen.ultra.callbacks.OnServiceTask;
import com.fallen.ultra.callbacks.ServiceCallback;
import com.fallen.ultra.creators.NotificationCreator;
import com.fallen.ultra.utils.Params;
import com.fallen.ultra.utils.UtilsUltra;
import com.fallen.ultra.utils.media.MediaPlayerExtended;

public class UltraPlayerService extends Service implements
		AsyncLoadStreamCallback {
	private NotificationManager mNotificationManager;
	//private MediaPlayer mMediaPlayer;
	private final IBinder mBinder = new LocalPlayerBinder();
	ServiceCallback mServiceCallback;
	AsyncLoadStream mAsycLoadStream;
	String currentArtist;
	String currentTrack;
	OnServiceTask serviceToPlayerCallback;



	@Override
	public IBinder onBind(Intent intent) {

		return mBinder;
	}

	public class LocalPlayerBinder extends Binder {
		public UltraPlayerService getService() {
			return UltraPlayerService.this;
		}
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// Bundle notificationInfo =
		// intent.getBundleExtra("NOTIFICATION_BUNDLE");
		String intentAction = intent.getAction();
		if (intentAction != null
				&& intentAction.equals(Params.ACTION_FROM_NOTIFIER_KEY)) {
			int flagToAction = intent.getIntExtra(Params.KEY_PLAY, 0);
			// .getInt(Params.ACTION_FROM_NOTIFIER_KEY);
			switch (flagToAction) {
			case Params.FLAG_PLAY:
				playStream();
				break;
			case Params.FLAG_STOP:
				stopStream();
				break;
			default:
				break;
			}
		}
		System.out.println("startcomm");
		return START_NOT_STICKY;
	}

	public void playStream() {
		serviceToPlayerCallback = new MediaPlayerExtended(this);
		mAsycLoadStream = new AsyncLoadStream(this,
				Params.ASYNC_ACTION_PLAY_STREAM);
		mAsycLoadStream.execute(Params.ULTRA_URL_HIGH);

	}

	private void createNotify(String tickerMessage) {
		if (mNotificationManager == null)
			mNotificationManager = UtilsUltra
					.createNotificationManager(getApplicationContext());

		Notification playerNotification = NotificationCreator
				.createPlayerNotification(getApplicationContext(),
						getPackageName(), currentArtist, currentTrack);
		startBackgroundWithNotifyer(playerNotification);
	}

	private void startBackgroundWithNotifyer(Notification playerNotification) {
		startForeground(Params.NOTIFICATION_ID, playerNotification);

	}

	private void updateNotify(ContentValues cv) {
		currentArtist = cv.getAsString(Params.TRACK_ARTIST_KEY);
		currentTrack = cv.getAsString(Params.TRACK_SONG_KEY);
		if (mNotificationManager != null)
			mNotificationManager.notify(Params.NOTIFICATION_ID,
					NotificationCreator.createPlayerNotification(
							getApplicationContext(), getPackageName(),
							currentArtist, currentTrack));
	}

	private void removeNotify() { // stopped service will close notify
		stopForeground(true);
		stopSelf();
	}

	public void stopStream() {
		if (serviceToPlayerCallback!=null)
			serviceToPlayerCallback.stopAndReleasePlayer();
		mAsycLoadStream.cancel(true);
		removeNotify();

	}

	@Override
	public void onCreate() {

		System.out.println("onCreateService");
		super.onCreate();
	}

	@Override
	public boolean onUnbind(Intent intent) {

		System.out.println("onUnbindService");
		return super.onUnbind(intent);
	}

	@Override
	public void onDestroy() {

		System.out.println("onDestroyService");
		super.onDestroy();
	}

	public void setCallback(ServiceCallback serviceCallback) {

		this.mServiceCallback = serviceCallback;
	}

	@Override
	public void onBuffered() {
		if (serviceToPlayerCallback!=null)
			serviceToPlayerCallback.onBuffered();
		createNotify(Params.ACTION_BUFFERED);
		
	}

	@Override
	public void onSocketStart() {
		serviceToPlayerCallback.onSocketCreated();

	}

	@Override
	public void onNewStreamTitleRetrieved(String stringTitle) {
		ContentValues cv = UtilsUltra.createBundleWithMetadata(stringTitle);
		updateNotify(cv);
	}

}
