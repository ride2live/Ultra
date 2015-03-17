package com.fallen.ultra.callbacks;

import com.fallen.ultra.creators.StatusObject;

public interface ObserverableMediaPlayer {
    void registerObserver(Observer o);
    void removeObserver(Observer o);
    void notifyObservers(StatusObject sObject);
	boolean onSocketCreated();
	void onBuffered();
	boolean stopAndReleasePlayer();
	boolean setCanceled();
	void onPhoneAction(int intExtra);
    void onNoisy();
}
