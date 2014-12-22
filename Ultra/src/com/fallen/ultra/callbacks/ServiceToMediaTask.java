package com.fallen.ultra.callbacks;

public interface ServiceToMediaTask {

	boolean onSocketCreated();
	void onBuffered();
	boolean stopAndReleasePlayer();
	boolean setCanceled();
	
}
