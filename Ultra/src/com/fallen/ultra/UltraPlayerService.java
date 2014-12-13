package com.fallen.ultra;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;

public class UltraPlayerService extends Service implements
		AsyncLoadStreamCallback {
	private NotificationManager mNotificationManager;
	private MediaPlayer mMediaPlayer;
	private final IBinder mBinder = new LocalPlayerBinder();
	ServiceCallback mServiceCallback;
	AsyncLoadStream mAsycLoadStream;
	String currentArtist;
	String currentTrack;

	public UltraPlayerService() {

	}

	@Override
	public IBinder onBind(Intent intent) {

		return mBinder;
	}

	public class LocalPlayerBinder extends Binder {
		UltraPlayerService getService() {
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

	void playStream() {
		mAsycLoadStream = new AsyncLoadStream(this,
				Params.ASYNC_ACTION_PLAY_STREAM);
		mAsycLoadStream.execute(Params.ULTRA_URL_HIGH);

	}

	private void createNotify() {
		if (mNotificationManager == null)
			mNotificationManager = createNotificationManager();

		Notification playerNotification = NotificationCreator
				.createPlayerNotification(getApplicationContext(),
						getPackageName(), currentArtist, currentTrack);
		showNotify(playerNotification);
	}

	private NotificationManager createNotificationManager() {

		return (NotificationManager) getApplicationContext().getSystemService(
				Context.NOTIFICATION_SERVICE);
	}

	private void showNotify(Notification playerNotification) {
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

	void stopStream() {
		if (mMediaPlayer != null) {

			mMediaPlayer.stop();
			mMediaPlayer.release();
			mMediaPlayer = null;
		}
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

		mMediaPlayer.start();
		if (mMediaPlayer.isPlaying())
			createNotify();
	}

	@Override
	public void onSocketStrart() {
		if (mMediaPlayer != null && mMediaPlayer.isPlaying())
			return; // do nothing, already playing
		try {
			mMediaPlayer = MediaPlayer.create(getApplicationContext(),
					Uri.parse(Params.LOCAL_SOCKET_STREAM_IP));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onNewStreamTitleRetrieved(String stringTitle) {
		ContentValues cv = UtilsUltra.createBundleWithMetadata(stringTitle);

		updateNotify(cv);
	}

}
