package com.fallen.ultra;

public interface AsyncLoadStreamCallback {

	void onBuffered();
	void onSocketStrart();
	void onNewStreamTitleRetrieved(String stringTitle);
}
