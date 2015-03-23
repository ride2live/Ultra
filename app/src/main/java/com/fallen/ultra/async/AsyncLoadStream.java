package com.fallen.ultra.async;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
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
		observers = new ArrayList<>();
	
	}

	@Override
	protected Void doInBackground(String... params) {
		URL url;

		int metaInt;
		int bytePreIntSum = 0;
		int bufferedSum = 0;
		int bufferBeforePlayback = Params.BUFFER_FOR_PLAYER_IN_BYTES;

		ServerSocket serverSocket = null;
		Socket client;
		// File fileToWrite = UtilsUltra.getTestFileToWrite();
		OutputStream out = null;
		InputStream radioStream = null;
		try {
			url = new URL(params[0]);
			HttpURLConnection uc = getUrlConnection(url);
			
			uc.setUseCaches(true);
			uc.setConnectTimeout(Params.CONNECTION_TIMEOUT);
			//uc.connect();
			metaInt = getMetInt(uc);
			UtilsUltra.printLog("metaInt " + metaInt, null, 0);
			if (bufferBeforePlayback < metaInt + Params.MAX_METAINT_VALUE)
				bufferBeforePlayback = metaInt + Params.MAX_METAINT_VALUE;
			UtilsUltra.printLog("bufferBeforePlayback " + bufferBeforePlayback, null, 0);
			
			radioStream = uc.getInputStream();
			byte[] buffer = new byte[Params.DEFAULT_INPUTSTREAM_BUFFER_BYTES];
			int bytesRealyRead;
			int byteToRead = 1024;

			
			try {
				serverSocket = new ServerSocket(
						Integer.parseInt(Params.SOCKET_PORT));
				UtilsUltra.printLog("socket created");
				publishProgress(new StatusObject(Params.STATUS_SOCKET_CREATING, true));
				UtilsUltra.printLog("update status, and waiting for mediaplayer connect");
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
						publishProgress(new StatusObject(Params.STATUS_NONE, true));
					}

				}

				if (bytePreIntSum == metaInt && metaInt != Params.NO_METAINT) {

					int metadataLenght = radioStream.read();

					if (metadataLenght > 0) {
						int byteSumMetadata = metadataLenght * 16;
						int realyReadMetaData;
						byte[] metaDataBuffer = new byte[metadataLenght * 16];
						//String str = new String(metaDataBuffer, "UTF-8");
						StringBuilder str = new StringBuilder();
						
						int realyReadMetaDataSum = 0;
						while (realyReadMetaDataSum  < byteSumMetadata) {
							realyReadMetaData = radioStream.read(metaDataBuffer, 0, metaDataBuffer.length);
							realyReadMetaDataSum = realyReadMetaDataSum + realyReadMetaData;
							String tempString  = new String(metaDataBuffer, "UTF-8");
							if (realyReadMetaDataSum < byteSumMetadata )
								metaDataBuffer = new byte [byteSumMetadata - realyReadMetaDataSum];
							str.append(tempString);
							
						}
					
						
						ContentValues metadataParsed = UtilsUltra.createBundleWithMetadata(str.toString());
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
		publishProgress(new StatusObject(Params.STATUS_STREAM_ENDS, true));
		try {
			if (out != null)
				out.close();
			if (serverSocket != null)
				serverSocket.close();
			
			if (radioStream !=null)
				radioStream.close();
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
			urlConnection.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/536.6 (KHTML, like Gecko) Chrome/20.0.1092.0 Safari/536.6");
			urlConnection.addRequestProperty("Content-Type", "audio/mpeg");
		} catch (IOException e) {
			e.printStackTrace();
			UtilsUltra.printLog("failed connection ", null, Log.ERROR);
		}
		UtilsUltra.printLog("opening connection done " + url, null, 0);
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
		super.onPostExecute(result);
	}

	@Override
	public void registerObserver(Observer o) {
		observers.add(o);
	}

	@Override
	public void removeObserver(Observer o) {
		observers.remove(o);
	}

	@Override
	public void notifyObservers(StatusObject sObject) {
		for (Observer observer : observers) {
			if (observer!=null)
				observer.update(sObject);
		}
		
	}
    public  int getMetInt(HttpURLConnection uc) {
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
}
