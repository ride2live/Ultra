package com.fallen.ultra.callbacks;

import com.fallen.ultra.creators.StatusObject;

public interface ActivityToFragmentListener {

	void onTitleChanged (String artist, String title);
	void updateAll(String currentArtist, String currentTrack,
			String currentStatus);
	void onStatusChanged(String status);
	void onStatusChanged(StatusObject stausObj);
	void updateOnRebind (StatusObject statusObj);

	
	
}
