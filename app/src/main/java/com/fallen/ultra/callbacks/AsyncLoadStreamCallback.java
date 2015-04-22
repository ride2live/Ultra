package com.fallen.ultra.callbacks;

import com.fallen.ultra.com.fallen.ultra.model.StatusObjectOverall;

public interface AsyncLoadStreamCallback {

	void onStatusChange(StatusObjectOverall status);
	void onBuffered();
	void onSocketStart();
	void onNewStreamTitleRetrieved(String artist, String track);
	void onConnecting(int status);
}
