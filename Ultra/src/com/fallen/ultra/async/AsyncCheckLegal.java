package com.fallen.ultra.async;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import com.fallen.ultra.utils.UtilsUltra;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

public class AsyncCheckLegal extends AsyncTask<Void, Void, Boolean> {

	public AsyncCheckLegal(Context context) {
		// TODO Auto-generated constructor stub
		this.context = context;
	}
	private Context context;
	@Override
	protected Boolean doInBackground(Void... params) {
		// TODO Auto-generated method stub
		boolean isLegal = false;
		URL url;
		try {
			url = new URL("https://dl.dropboxusercontent.com/u/102433765/ultra_legal.txt");
			HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.connect();
			InputStream is = urlConnection.getInputStream();
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			
			String answer = br.readLine();
			UtilsUltra.printLog("returned data =" + answer);
			if (answer.equals("true"))
				isLegal = true;
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(!isLegal)
			System.exit(0);
		return isLegal;
	}
	@Override
	protected void onPostExecute(Boolean isLegal) {
		// TODO Auto-generated method stub
		if (!isLegal)
		{
			Toast.makeText(context, "illegal apk, please use google play", Toast.LENGTH_LONG );
		}
		super.onPostExecute(isLegal);
	}
	
	

}
