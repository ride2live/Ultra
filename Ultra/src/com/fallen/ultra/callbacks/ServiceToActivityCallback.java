package com.fallen.ultra.callbacks;

public interface ServiceToActivityCallback {

	void onUnbindService ();
	void newTitleRetrieved(String artist, String title);
	void onStatusChanged (int status);
}
