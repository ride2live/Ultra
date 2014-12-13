package com.fallen.ultra.utils.media;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;

import com.fallen.ultra.callbacks.OnServiceTask;
import com.fallen.ultra.utils.Params;

public class MediaPlayerExtended extends MediaPlayer implements OnServiceTask {

	boolean isPrepeared = false;
	Context context;
	public MediaPlayerExtended(Context context) {
		// TODO Auto-generated constructor stub
		this.context = context;
		/*try {
			mMediaPlayer = new MediaPlayer();
			mMediaPlayer.setDataSource(this, Uri.parse(Params.LOCAL_SOCKET_STREAM_IP));
			mMediaPlayer.prepareAsync();
			
			//mMediaPlayer = MediaPlayer.create(getApplicationContext(),
				//	Uri.parse(Params.LOCAL_SOCKET_STREAM_IP));

		} catch (Exception e) {
			e.printStackTrace();
		}*/

	}



	@Override
	public Boolean onSocketCreated() {
		boolean isSuccess = false;
		try {
			
			setDataSource(context, Uri.parse(Params.LOCAL_SOCKET_STREAM_IP));
			prepareAsync();
			isSuccess = true;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return isSuccess;
	}

	@Override
	public void onBuffered() {
		// TODO Auto-generated method stub
		start();
	}

	@Override
	public Boolean stopAndReleasePlayer() {
		// TODO Auto-generated method stub
		boolean isSuccess = false;
		try {
			stop();
			release();
			isSuccess = true;
		} catch (Exception e) {
			// TODO: handle exception
		}
		return isSuccess;
	}
}
