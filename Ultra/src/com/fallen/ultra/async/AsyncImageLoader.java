package com.fallen.ultra.async;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import android.os.AsyncTask;

import com.fallen.ultra.utils.UtilsUltra;

public class AsyncImageLoader extends AsyncTask<String, Void, String> {

	@Override
	protected String doInBackground(String... params) {
		// TODO Auto-generated method stub
		String bitmapArtPath = null;
		if (params != null && params.length == 2) {
			String artist;
			String track;
			try {
				artist = URLEncoder.encode(params[0], "UTF-8");
				track = URLEncoder.encode(params[1], "UTF-8");

				try {
					String urlString = UtilsUltra.createLastFMGetTrackRequest(
							artist, track);
					URL url = new URL(urlString);
					UtilsUltra.printLog(urlString);
					HttpURLConnection urlConnection = (HttpURLConnection) url
							.openConnection();
					urlConnection.connect();
					UtilsUltra.printLog(urlConnection.getResponseMessage());
					InputStream lastFMResponseStream =urlConnection.getInputStream();
					InputStreamReader reader = new InputStreamReader(lastFMResponseStream);
					BufferedReader bufferedReader = new BufferedReader(reader);
					
					String readLine;
					while ((readLine = bufferedReader.readLine()) !=null)
					{
						UtilsUltra.printLog(readLine);
					}
					
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		}

		return bitmapArtPath;
	}

}
