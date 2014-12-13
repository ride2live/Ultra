package com.fallen.ultra;

import java.io.File;
import java.io.IOException;

import android.content.ContentValues;
import android.os.Environment;
import android.util.Log;

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
	
	public static ContentValues createBundleWithMetadata(String stringTitle) {
		// TODO Auto-generated method stub
		ContentValues trackinfo = new ContentValues();
		if (!(stringTitle.contains(Params.STREAM_TITLE_KEYWORD)&& stringTitle.contains("'")))
		{
			trackinfo.put(Params.TRACK_ARTIST_KEY, "");
			trackinfo.put(Params.TRACK_SONG_KEY, Params.NO_TITLE);
			return trackinfo;	
		}
			
		else
		{
			
			stringTitle = stringTitle.replace(Params.STREAM_TITLE_KEYWORD, "");
			if (!(stringTitle.startsWith("='")&&stringTitle.lastIndexOf("'")>2 &&stringTitle.contains(" - ")))
			{
				trackinfo.put(Params.TRACK_ARTIST_KEY, "");
				trackinfo.put(Params.TRACK_SONG_KEY, stringTitle);
				return trackinfo;	
			}
			else
			{
				try {
					
				
				stringTitle = stringTitle.substring(2, stringTitle.lastIndexOf("'"));
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

}
