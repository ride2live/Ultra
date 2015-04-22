package com.fallen.ultra.callbacks;


import com.fallen.ultra.com.fallen.ultra.model.StatusObjectOverall;

public interface PlayerFragmentCallback {

	void buttonClicked(int buttonId);
	void onInit();
	void onFragmentCreatedNoInit();


	boolean checkIsCurrentTrackFav();

    void onQualityChangedPositiveClicked(int currentQuality);

    StatusObjectOverall getOverallStatus();
}
