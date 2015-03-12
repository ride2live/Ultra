package com.fallen.ultra.callbacks;

import com.fallen.ultra.creators.StatusObject;

public interface AsyncLoadStreamCallback {

	void onStatusChange(StatusObject status);
	void onBuffered();
	void onSocketStart();
	void onNewStreamTitleRetrieved(String artist, String track);
	void onConnecting(int status);
}
