package com.fallen.ultra.callbacks;

public interface AsyncLoadStreamCallback {

	void onBuffered();
	void onSocketStart();
	void onNewStreamTitleRetrieved(String stringTitle);
}
