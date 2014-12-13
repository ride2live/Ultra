package com.fallen.ultra.callbacks;

public interface OnServiceTask {

	Boolean onSocketCreated();
	void onBuffered();
	Boolean stopAndReleasePlayer();
}
