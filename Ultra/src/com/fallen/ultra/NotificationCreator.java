package com.fallen.ultra;

import com.example.ultra.R;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;
import android.widget.RemoteViews;

public abstract class NotificationCreator {

	public static Notification createPlayerNotification(Context context, String packageName ) {
		// TODO Auto-generated method stub
		NotificationCompat.Builder notificationBuilder = new Builder(context);
		Intent resultIntent = new Intent(context, MainUltraActivity.class);
		RemoteViews playbackControls= new RemoteViews(packageName,R.layout.playback_controls);
		Intent startStreamIntent = new Intent(context, UltraPlayerService.class);
		startStreamIntent.setAction(Params.ACTION_FROM_NOTIFIER_KEY);
		startStreamIntent.putExtra(Params.KEY_PLAY, Params.FLAG_PLAY);
		Intent stopStreamIntent = new Intent(context, UltraPlayerService.class);
		stopStreamIntent.putExtra(Params.KEY_PLAY, Params.FLAG_STOP);
		stopStreamIntent.setAction(Params.ACTION_FROM_NOTIFIER_KEY);
		PendingIntent startPendingIntent = PendingIntent.getService(context, 12, startStreamIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		PendingIntent stopPendingIntent = PendingIntent.getService(context, 122, stopStreamIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		//playbackControls.setO
		
		playbackControls.setOnClickPendingIntent(R.id.startButton, startPendingIntent);
		playbackControls.setOnClickPendingIntent(R.id.stopButton, stopPendingIntent);
		notificationBuilder.setSmallIcon(R.drawable.ic_launcher);
		notificationBuilder.setContent(playbackControls);
		PendingIntent resultPendingIntent = PendingIntent.getActivity(context, 123, resultIntent, 0);
		notificationBuilder.setContentIntent(resultPendingIntent);
		return notificationBuilder.build();
	}

}
