package com.fallen.ultra.async;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.os.AsyncTask;
import android.util.Xml;

import com.fallen.ultra.callbacks.ImageLoader;
import com.fallen.ultra.utils.UtilsUltra;

public class AsyncImageLoader extends AsyncTask<String, Void, File> {

	File tempFile;
	private ImageLoader imageLoaderCallback;

	public AsyncImageLoader(File file, ImageLoader imageLoaderCallback) {
		// TODO Auto-generated constructor stub
		tempFile = file;
		this.imageLoaderCallback = imageLoaderCallback;
	}

	@Override
	protected File doInBackground(String... params) {
		// TODO Auto-generated method stub
		File bitmapArtFile = null;
		String bitmapArtUrl = null;
		if (params != null && params.length >= 2) {
			String artist;
			String track;
			try {
				artist = URLEncoder.encode(params[0], "UTF-8");
				track = URLEncoder.encode(params[1], "UTF-8");

				// BufferedReader bufferedReader = new BufferedReader(reader);
				// String readLine;

				XmlPullParser lastFmGetTrackParser = getLastFmParser(UtilsUltra
						.createLastFMRequest(artist, track));
				if (lastFmGetTrackParser != null)
					bitmapArtUrl = readGetTRackImage(lastFmGetTrackParser);
				if (bitmapArtUrl == null)
				{
					
					XmlPullParser lastFmGetArtistParser = getLastFmParser(UtilsUltra.createLastFMArtistRequest(artist));
					bitmapArtUrl = readGetTRackImage (lastFmGetArtistParser);
					
				}
				if (bitmapArtUrl != null)
					bitmapArtFile = loadArtToTemp(bitmapArtUrl);
					
				// while ((readLine = bufferedReader.readLine()) !=null)
				// {
				// UtilsUltra.printLog(readLine);
				// }

			} catch (UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		}

		return bitmapArtFile;
	}



	private XmlPullParser getLastFmParser(
			String createLastFMGetTrackRequest) {

		XmlPullParser lastFmParser = null;
		HttpURLConnection urlConnection;
		try {

			URL url = new URL(createLastFMGetTrackRequest);
			UtilsUltra.printLog(createLastFMGetTrackRequest);
			urlConnection = (HttpURLConnection) url.openConnection();

			urlConnection.connect();
			UtilsUltra.printLog(urlConnection.getResponseMessage());
			InputStream lastFMResponseStream = urlConnection.getInputStream();
			InputStreamReader reader = new InputStreamReader(
					lastFMResponseStream);
			lastFmParser = Xml.newPullParser();
			lastFmParser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES,
					false);
			lastFmParser.setInput(reader);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return lastFmParser;
	}

	private File loadArtToTemp(String bitmapArtStringUrl) {
		File bitmapArtPath = null;
		try {

			URL bitmapUrl = new URL(bitmapArtStringUrl);
			HttpURLConnection connection = (HttpURLConnection) bitmapUrl
					.openConnection();
			InputStream inputBitmap = connection.getInputStream();
			if (tempFile != null)
				bitmapArtPath = bitmatToOutput(inputBitmap);

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return bitmapArtPath;
	}

	private File bitmatToOutput(InputStream inputBitmap) {
		// TODO Auto-generated method stub
		FileOutputStream output = null;
		try {
			output = new FileOutputStream(tempFile);
			byte[] buffer = new byte[1024];
			int realRead;
			while ((realRead = inputBitmap.read(buffer)) != -1) {

				output.write(buffer, 0, realRead);
			}
			inputBitmap.close();
			output.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return tempFile;

	}

	private String readGetTRackImage(XmlPullParser lastFmParser) {
		// TODO Auto-generated method stub
		String resultArtUrl = null;
		String albumTag = "album";
		String trackTag = "track";
		String firstTag = "lfm";
		String imageTag = "image";
		String attr = null;

		try {
			// lastFmParser.nextTag();
			// lastFmParser.next()
			// lastFmParser.require(XmlPullParser.START_TAG, null, firstTag);
			boolean isImageFound = false;
			while (lastFmParser.next() != XmlPullParser.END_DOCUMENT && !isImageFound) {

				// UtilsUltra.printLog(lastFmParser.getText() +" getText");
				// if (lastFmParser.getName().equals(trackTag))
				// {
				// // lastFmParser.
				// }
				if (lastFmParser.getEventType() == XmlPullParser.START_TAG
						&& lastFmParser.getName().equals(imageTag)) {

					UtilsUltra.printLog(lastFmParser.getName() + " getName");
					if (lastFmParser.getAttributeCount() > 0) {
						UtilsUltra.printLog(lastFmParser.getAttributeName(0)
								+ " " + lastFmParser.getAttributeValue(0)
								+ " getAttr");
						attr = lastFmParser.getAttributeValue(0);
						lastFmParser.next();
						if (lastFmParser.getEventType() == XmlPullParser.TEXT
								&& attr.equals("extralarge")) {
							UtilsUltra.printLog(lastFmParser.getText()
									+ " getText");
							resultArtUrl = lastFmParser.getText();
							isImageFound = true;
						}

					}

				}

			}
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return resultArtUrl;

	}
	

	@Override
	protected void onPostExecute(File result) {
		// TODO Auto-generated method stub
		imageLoaderCallback.onImageBuffered();
		super.onPostExecute(result);
	}

}
