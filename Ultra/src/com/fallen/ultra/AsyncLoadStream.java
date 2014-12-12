package com.fallen.ultra;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;

import com.fallen.ultra.Params;

import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

public class AsyncLoadStream extends AsyncTask<String, String, Void> {

	boolean isBuffered = false;
	AsyncLoadStreamCallback streamCallback;
	int asyncAction;
	OutputStream emulatingStream;

	public AsyncLoadStream(AsyncLoadStreamCallback streamCallback,
			int asyncAction) {
		this.streamCallback = streamCallback;
		this.asyncAction = asyncAction;
	}

	@Override
	protected Void doInBackground(String... params) {
		URL url;
		int metaInt = Params.NO_METAINT;
		int bytePreIntSum = 0;
		int bufferedSum = 0;
		ServerSocket serverSocket = null;
		Socket client;
		// File fileToWrite = UtilsUltra.getTestFileToWrite();
		OutputStream out = null;
		try {
			url = new URL(params[0]);
			HttpURLConnection uc = getUrlConnection(url);
			uc.connect();
			metaInt = getMetInt(uc);
			UtilsUltra.printLog("metaInt " + metaInt, null, 0);
			System.out.println();
			InputStream radioStream = uc.getInputStream();
			byte[] buffer = new byte[Params.DEFAULT_INPUTSTREAM_BUFFER_BYTES];
			int bytesRealyRead = 0;
			int byteToRead = 1024;

			try {
				serverSocket = new ServerSocket(
						Integer.parseInt(Params.SOCKET_PORT));
				publishProgress(Params.ACTION_SOCKET_PREACCEPT_DELAY);
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
				bytePreIntSum += bytesRealyRead;
				if (!isBuffered)
					bufferedSum += bytesRealyRead;
				if (bufferedSum > Params.BUFFER_FOR_PLAYER_IN_BYTES
						&& !isBuffered) {
					isBuffered = true;
					publishProgress(Params.ACTION_BUFFERED);
				}
				if (bytePreIntSum == metaInt && metaInt != Params.NO_METAINT) {

					int metadataLenght = radioStream.read();

					if (metadataLenght > 0) {
						byte[] metaDataBuffer = new byte[metadataLenght * 16];
						radioStream.read(metaDataBuffer);
						String str = new String(metaDataBuffer, "UTF-8");
						System.out.println(str);
						streamCallback.onNewStreamTitleRetrieved(str);

					}
					bytePreIntSum = 0;
					byteToRead = Params.DEFAULT_INPUTSTREAM_BUFFER_BYTES;
				}
				emulatingStream.write(buffer, 0, bytesRealyRead);
				if (bytePreIntSum + Params.DEFAULT_INPUTSTREAM_BUFFER_BYTES > metaInt) {
					byteToRead = metaInt - bytePreIntSum;
				}

			}
		} catch (MalformedURLException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		}
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
			urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.addRequestProperty("Icy-MetaData", "1");

		} catch (IOException e) {
			e.printStackTrace();
		}

		return urlConnection;
	}

	private int getMetInt(URLConnection uc) {
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
	protected void onProgressUpdate(String... values) {
		if (values[0].equals(Params.ACTION_BUFFERED)) {
			streamCallback.onBuffered();
		}
		if (values[0].equals(Params.ACTION_SOCKET_PREACCEPT_DELAY)) {

			new Handler().postDelayed(new Runnable() {

				@Override
				public void run() {
					streamCallback.onSocketStrart();
				}
			}, 1000);
			super.onProgressUpdate(values);
		}

	}

	@Override
	protected void onPostExecute(Void result) {
		// TODO Auto-generated method stub

		super.onPostExecute(result);
	}
}
