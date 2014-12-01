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

public class UltraPlayerService extends Service {
	private NotificationManager mNotificationManager;
	private MediaPlayer mp;
	private final IBinder mBinder = new LocalPlayerBinder();

	public UltraPlayerService() {

	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub

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
		if (intentAction != null && intentAction.equals(Params.ACTION_FROM_NOTIFIER_KEY)) {
			int flagToAction = intent.getIntExtra(Params.KEY_PLAY, 0);
			// .getInt(Params.ACTION_FROM_NOTIFIER_KEY);
			switch (flagToAction) {
			case Params.FLAG_PLAY:
				playStream();
				break;
			case Params.FLAG_STOP:
				stopPlayStream();
				break;
			default:
				break;
			}
		}
		System.out.println("startcomm");
		return START_NOT_STICKY;
	}

	void playStream() {
		if (mp != null && mp.isPlaying())
			return; //do nothing
		try {
			mp = MediaPlayer.create(getApplicationContext(),
					Uri.parse("http://94.25.53.133:80/ultra-128.mp3"));
			mp.start();
			if (mp.isPlaying())
				createNotify();

		} catch (Exception e) {
			// TODO: handle exception

		}

	}

	private void createNotify() {
		if (mNotificationManager == null)
			mNotificationManager = createNotificationManager();
		
		Notification playerNotification = NotificationCreator
				.createPlayerNotification(getApplicationContext(),
						getPackageName());
		showNotify(playerNotification);
	}

	private NotificationManager createNotificationManager() {
		// TODO Auto-generated method stub
		return  (NotificationManager) getApplicationContext()
				.getSystemService(Context.NOTIFICATION_SERVICE);
	}

	private void showNotify(Notification playerNotification) {
		startForeground(Params.NOTIFICATION_ID, playerNotification);
	}

	private void updateNotify(ContentValues cv) {
		
	}

	private void removeNotufy() {
		
		mNotificationManager.cancel(Params.NOTIFICATION_ID);
	}

	void stopPlayStream() {
		mp.stop();
		removeNotufy();

	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		System.out.println("onCreate");
		super.onCreate();
	}

	@Override
	public boolean onUnbind(Intent intent) {
		// TODO Auto-generated method stub
		System.out.println("onUnbind");
		return super.onUnbind(intent);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		System.out.println("onDestroy");
		super.onDestroy();
	}

}
