package com.fallen.ultra.async;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;

import android.content.ContentValues;
import android.os.AsyncTask;
import android.util.Log;

import com.fallen.ultra.callbacks.Observer;
import com.fallen.ultra.callbacks.Observerable;
import com.fallen.ultra.creators.StatusObject;
import com.fallen.ultra.utils.Params;
import com.fallen.ultra.utils.UtilsUltra;

public class AsyncLoadStream extends AsyncTask<String, StatusObject, Void> implements Observerable {

	boolean isBuffered = false;
	ArrayList<Observer> observers;
	OutputStream emulatingStream;

	public AsyncLoadStream() {
		observers = new ArrayList<Observer>();
	
	}

	@Override
	protected Void doInBackground(String... params) {
		URL url;

		int metaInt = Params.NO_METAINT;
		int bytePreIntSum = 0;
		int bufferedSum = 0;
		int bufferBeforePlayback = Params.BUFFER_FOR_PLAYER_IN_BYTES;

		ServerSocket serverSocket = null;
		Socket client;
		// File fileToWrite = UtilsUltra.getTestFileToWrite();
		OutputStream out = null;
		try {
			url = new URL(params[0]);
			HttpURLConnection uc = getUrlConnection(url);
			uc.connect();
			metaInt = UtilsUltra.getMetInt(uc);
			UtilsUltra.printLog("metaInt " + metaInt, null, 0);
			if (bufferBeforePlayback < metaInt + Params.MAX_METAINT_VALUE)
				bufferBeforePlayback = metaInt + Params.MAX_METAINT_VALUE;
			UtilsUltra.printLog("bufferBeforePlayback " + bufferBeforePlayback, null, 0);
			System.out.println();
			InputStream radioStream = uc.getInputStream();
			byte[] buffer = new byte[Params.DEFAULT_INPUTSTREAM_BUFFER_BYTES];
			int bytesRealyRead = 0;
			int byteToRead = 1024;

			
			try {
				serverSocket = new ServerSocket(
						Integer.parseInt(Params.SOCKET_PORT));
				publishProgress(new StatusObject(Params.STATUS_SOCKET_CREATING, true));
				client = serverSocket.accept();
				emulatingStream = client.getOutputStream();
				emulatingStream = configureOutputstream(emulatingStream);
			} catch (NumberFormatException e) {

				e.printStackTrace();
			} catch (IOException e) {

				e.printStackTrace();
			}
			while ((bytesRealyRead = radioStream.read(buffer, 0, byteToRead)) != -1
					&& !isCancelled()) {
				if (metaInt != Params.NO_METAINT)
					bytePreIntSum += bytesRealyRead;

				if (!isBuffered) {
					bufferedSum += bytesRealyRead;

					if (bufferedSum > bufferBeforePlayback)
					{
						isBuffered = true;
						publishProgress(new StatusObject(Params.STATUS_BUFFERING,true));
					}

				}

				if (bytePreIntSum == metaInt && metaInt != Params.NO_METAINT) {

					int metadataLenght = radioStream.read();

					if (metadataLenght > 0) {

						byte[] metaDataBuffer = new byte[metadataLenght * 16];
						int realyReadMetaData = radioStream.read(metaDataBuffer);
						if (realyReadMetaData < metaDataBuffer.length)
							UtilsUltra.printLog("CATCH YOU, BITCH, read while in metadata!", null, Log.ERROR);
						String str = new String(metaDataBuffer, "UTF-8");
						ContentValues metadataParsed = UtilsUltra.createBundleWithMetadata(str);
						String artist = metadataParsed.getAsString(Params.TRACK_ARTIST_KEY);
						String track = metadataParsed.getAsString(Params.TRACK_SONG_KEY);
						publishProgress(new StatusObject(Params.STATUS_NEW_TITLE, artist, track));
						System.out.println(str);
						

					}
					bytePreIntSum = 0;
					byteToRead = Params.DEFAULT_INPUTSTREAM_BUFFER_BYTES;
				}
				emulatingStream.write(buffer, 0, bytesRealyRead);
				if (bytePreIntSum + Params.DEFAULT_INPUTSTREAM_BUFFER_BYTES > metaInt  && metaInt != Params.NO_METAINT) {
					byteToRead = metaInt - bytePreIntSum;
					
				}

			}
		} catch (MalformedURLException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		}
		UtilsUltra.printLog("Server rerturn -1, closing socket", null, Log.ERROR);
		try {
			if (out != null)
				out.close();
			if (serverSocket != null)
				serverSocket.close();
		} catch (IOException e) {

			e.printStackTrace();
		}
		return null;
	}

	private HttpURLConnection getUrlConnection(URL url) {
		HttpURLConnection urlConnection = null;
		try {
			UtilsUltra.printLog("opening connection " + url, null, 0);
			publishProgress(new StatusObject(Params.STATUS_CONNECTING, true));
			urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.addRequestProperty("Icy-MetaData", "1");
			
		} catch (IOException e) {
			e.printStackTrace();
			UtilsUltra.printLog("failed connection ", null, Log.ERROR);
		}
		
		return urlConnection;
	}

	

	private OutputStream configureOutputstream(OutputStream emulatingStream) {
		String responseCode = ("HTTP/1.1 200 OK\r\n");
		String cache = "Cache-Control: no-cache\r\n";
		String contentType = ("Content-Type: audio/mpeg\r\n\r\n");
		String responseFakeString = responseCode + cache + contentType;
		try {
			emulatingStream.write(responseFakeString.getBytes());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return emulatingStream;
	}

	@Override
	protected void onProgressUpdate(StatusObject... values) {
		notifyObservers(values[0]);
		/*switch (values[0].getStatus()) {
		case Params.ACTION_BUFFERED:
			streamCallback.onBuffered();
			break;
		case Params.ACTION_SOCKET_PREACCEPT_DELAY:


			new Handler().postDelayed(new Runnable() {

				@Override
				public void run() {
					streamCallback.onSocketStart();
				}
			}, 1000);
			
		 break;
		case Params.ACTION_NEW_TITLE:
			streamCallback.onNewStreamTitleRetrieved(values[0].getArtist(), values[0].getTrack());
			break;
		case Params.ACTION_CONNECTING:
			streamCallback.onConnecting(values[0].getStatus());
			break;
		default:
			break;
		}*/
		super.onProgressUpdate(values);
		

	}

	@Override
	protected void onPostExecute(Void result) {
		// TODO Auto-generated method stub

		super.onPostExecute(result);
	}

	@Override
	public void registerObserver(Observer o) {
		// TODO Auto-generated method stub
		observers.add(o);
	}

	@Override
	public void removeObserver(Observer o) {
		// TODO Auto-generated method stub
		observers.remove(o);
	}

	@Override
	public void notifyObservers(StatusObject sObject) {
		// TODO Auto-generated method stub
		for (Observer observer : observers) {
			observer.update(sObject);
		}
		
	}
}
