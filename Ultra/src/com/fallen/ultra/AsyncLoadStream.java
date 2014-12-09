package com.fallen.ultra;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import android.os.AsyncTask;

public class AsyncLoadStream extends AsyncTask<String, Void, Void>{

	int metaInt = -1;
	int bytePreIntSum = 0;
	int bufferedSum = 0;
	boolean isBuffered = false;
	AsyncLoadStreamCallback streamCallback;
	public AsyncLoadStream(AsyncLoadStreamCallback streamCallback) {
		// TODO Auto-generated constructor stub
		this.streamCallback =streamCallback;
	}
	@Override
	protected Void doInBackground(String... params) {
		// TODO Auto-generated method stub
		URL url;
		File fileToWrite = UtilsUltra.getTestFileToWrite();
		OutputStream out = null;
		try {
			url = new URL (params[0]);
			HttpURLConnection uc =  getUrlConnection (url);
			uc.connect();
			metaInt = getMetInt(uc);
			System.out.println("metaInt " +metaInt);
			InputStream radioStream = uc.getInputStream();
			out = new FileOutputStream(fileToWrite);
			byte [] buffer = new byte [1024]; 
			int bytesRealyRead = 0;
			int byteToRead = 1024;
			while ((bytesRealyRead = radioStream.read(buffer,0,byteToRead )) != -1 && !isCancelled()) {
				bytePreIntSum += bytesRealyRead;
				if (!isBuffered)
					bufferedSum += bytesRealyRead;
				if (bufferedSum > 200000)
				{
					isBuffered = true;
					streamCallback.onBuffered();
				}
				
				if (bytePreIntSum == metaInt)
				{
					
					int metadataLenght = radioStream.read();
					System.out.println(metadataLenght);
					if (metadataLenght > 0)
					{
						byte [] metaDataBuffer = new byte [metadataLenght*16]; 
						radioStream.read(metaDataBuffer);
						String str = new String(metaDataBuffer, "UTF-8");
						System.out.println(str);
					}
					bytePreIntSum = 0;
					byteToRead = 1024;
				}

					out.write(buffer, 0, bytesRealyRead);
				
					
				if (bytePreIntSum + 1024 > metaInt)
				{
					byteToRead = metaInt - bytePreIntSum;
					
//					byte [] preMetaintBuffer = new byte [metaInt - bytePreIntSum];
//					System.out.println ("need to read " +(metaInt - bytePreIntSum));
//					System.out.println ("read " +radioStream.read(preMetaintBuffer));
//					out.write(preMetaintBuffer);
					
				}
				
				
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			if (out!=null)
				out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	private HttpURLConnection getUrlConnection(URL url) {
		// TODO Auto-generated method stub
		
		HttpURLConnection urlConnection = null;
		try {
			urlConnection = (HttpURLConnection)url.openConnection();
			urlConnection.addRequestProperty("Icy-MetaData", "1");	//Need it always, cause we gonna inform user about current title anyway
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return urlConnection;
	}
	
	private int getMetInt(URLConnection uc) {
		// TODO Auto-generated method stub
		int metaInt=0;
		try {
			metaInt =Integer.parseInt(uc.getHeaderField("icy-metaint")); // how much byte I need to read before metadata block begin
			//System.out.println ("metaint " +metaInt);
			if (metaInt == 0)
				return -1;
			}
		catch (Exception e) {
			// TODO: handle exception	
			//System.out.println ("metadata infoheader exeption");
			return -1;
		}
		return metaInt;
	}

}
