package com.fallen.ultra.utils;

import java.io.File;
import java.io.IOException;
import java.net.URLConnection;

import android.app.NotificationManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.os.Environment;
import android.util.Log;

import com.fallen.ultra.R;
import com.fallen.ultra.services.UltraPlayerService;

public abstract class UtilsUltra {
	public static File getTestFileToWrite() {
		File d = new File(Environment.getExternalStorageDirectory(),
				"Ultratest");
		d.mkdirs();
		File f = new File(d, "test.m");
		if (!f.exists())
			try {
				f.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		return f;
	}

	public static void printLog(String logMessage, String tag, int logType) {
		if (tag == null)
			tag = Params.DEFAULT_LOG_TYPE;
		if (logType == 0)
			logType = Log.VERBOSE;
		switch (logType) {

		case Log.DEBUG:
			Log.d(tag, logMessage);
			break;

		case Log.ERROR:
			Log.e(tag, logMessage);
			break;

		case Log.INFO:
			Log.i(tag, logMessage);
			break;

		case Log.VERBOSE:
			Log.v(tag, logMessage);
			break;

		case Log.WARN:
			Log.w(tag, logMessage);

			break;

		default:
			break;
		}

	}

	public static void printLog(String logMessage) {
		printLog(logMessage, Params.DEFAULT_LOG_TYPE, Log.VERBOSE);
	}

	public static ContentValues createBundleWithMetadata(String stringTitle) {
		// TODO Auto-generated method stub
		ContentValues trackinfo = new ContentValues();
		if (!(stringTitle.contains(Params.STREAM_TITLE_KEYWORD) || stringTitle
				.contains("'"))) {
			trackinfo.put(Params.TRACK_ARTIST_KEY, "");
			trackinfo.put(Params.TRACK_SONG_KEY, Params.NO_TITLE);
			return trackinfo;
		}

		else {

			stringTitle = stringTitle.replace(Params.STREAM_TITLE_KEYWORD, "");
			if (!(stringTitle.startsWith("='")
					|| stringTitle.lastIndexOf("'") < 2 || stringTitle
						.contains(" - "))) {
				trackinfo.put(Params.TRACK_ARTIST_KEY, "");
				trackinfo.put(Params.TRACK_SONG_KEY, stringTitle);
				return trackinfo;
			} else {
				try {

					stringTitle = stringTitle.substring(2,
							stringTitle.lastIndexOf("'"));
					String[] parts = stringTitle.split(" - ");
					String artist = parts[0];
					String trackName = parts[1];
					trackinfo.put(Params.TRACK_ARTIST_KEY, artist);
					trackinfo.put(Params.TRACK_SONG_KEY, trackName);
					return trackinfo;
				} catch (Exception e) {
					// TODO: handle exception
					trackinfo.put(Params.TRACK_ARTIST_KEY, "");
					trackinfo.put(Params.TRACK_SONG_KEY, Params.NO_TITLE);
					return trackinfo;
				}

			}

		}

	}

	public static NotificationManager createNotificationManager(
			Context applicationContext) {
		// TODO Auto-generated method stub

		return (NotificationManager) applicationContext
				.getSystemService(Context.NOTIFICATION_SERVICE);
	}



	public static int getMetInt(URLConnection uc) {
		// TODO Auto-generated method stub
		int metaInt = 0;
		try {
			metaInt = Integer.parseInt(uc.getHeaderField("icy-metaint"));
			UtilsUltra.printLog("ICY retrieved " + metaInt, null, 0);
			if (metaInt == 0)
				return Params.NO_METAINT;
		} catch (Exception e) {
			UtilsUltra.printLog("ICY exeprion occured " + metaInt, null,
					Log.WARN);
			e.printStackTrace();
			return Params.NO_METAINT;
		}
		return metaInt;
	}

	/**
	 * will return quality param (like radio button clicked) or if not exist -
	 * 128 as default and save it in shared
	 */
	public static int getQualityFromPreferences(SharedPreferences myPreferences) {

		int qualityFromPrefs = myPreferences.getInt(
				Params.KEY_PREFERENCES_QUALITY_FIELD, -1);
		if (qualityFromPrefs == -1) {
			Editor myEditor = myPreferences.edit();
			myEditor.putInt(Params.KEY_PREFERENCES_QUALITY_FIELD,
					Params.QUALITY_128);
			myEditor.commit();
		}
		UtilsUltra.printLog("quality on create = " + qualityFromPrefs,
				"Ultra Activity", Log.ERROR);
		return qualityFromPrefs;

	}

	public static Intent createServiceIntentFromActivity(Context context,
			int action) {
		Intent intent = new Intent(context, UltraPlayerService.class);

		switch (action) {

		case Params.FLAG_STOP:
			intent.putExtra(Params.ACTION_FROM_CONTROLS, action);
			break;
		case Params.FLAG_BIND_ACTIVITY:
			intent.putExtra(Params.ACTION_FROM_CONTROLS, action);
			break;

		default:
			intent.putExtra(Params.ACTION_FROM_CONTROLS, Params.ACTION_WRONG);
			break;
		}
		return intent;

	}

	public static Intent createServiceIntentFromActivity(Context context,
			int action, int qualityKey) {
		Intent intent = new Intent(context, UltraPlayerService.class);
		String qualityUrl = null;
		if (action == Params.FLAG_PLAY) {
			intent.putExtra(Params.ACTION_FROM_CONTROLS, action);
			if (qualityKey == Params.QUALITY_128)
				qualityUrl = Params.ULTRA_URL_HIGH;
			else
				qualityUrl = Params.ULTRA_URL_LOW;
		}
		intent.putExtra(Params.KEY_PREFERENCES_QUALITY_FIELD, qualityUrl);
		return intent;

	}

	public static void putQualityToSharedPrefs(SharedPreferences myPreferences,
			int currentQualityKey) {
		myPreferences
				.edit()
				.putInt(Params.KEY_PREFERENCES_QUALITY_FIELD, currentQualityKey)
				.commit();

	}

	public static String getDescriptionByStatus(Context context, int status) {
		String description = null;
		Resources res = context.getResources();
		switch (status) {
		case Params.STATUS_BUFFERING:
			description = res.getString(R.string.buffering);
			break;
		case Params.STATUS_CONNECTING:
			description = res.getString(R.string.connecting);
			break;
		case Params.STATUS_PLAYING:
			description = res.getString(R.string.playing);
			break;
		case Params.STATUS_STOPED:
			description = res.getString(R.string.stopped);
			break;
		case Params.STATUS_ERROR:
			description = res.getString(R.string.error);
			break;

		default:
			description = Params.STATUS_DESCRIPTION_NOTHING_AT_ALL;
			break;
		}
		return description;
	}

	public static String createLastFMGetTrackRequest(String artist, String track) {
		// TODO Auto-generated method stub
		String urlGetTrackInfo = Params.LASTFM_TRACK_GET_INFO+"&api_key="+Params.LASTFM_DEV_KEY+"&artist="+artist+"&track="+track;
				//http://ws.audioscrobbler.com/2.0/?method=track.getInfo&api_key=73bb327ff0e5bd23f100274492105b4f&artist=cher&track=believe
		return urlGetTrackInfo;
	}
}
