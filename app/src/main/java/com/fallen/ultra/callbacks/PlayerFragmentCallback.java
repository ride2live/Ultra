package com.fallen.ultra.callbacks;


public interface PlayerFragmentCallback {

	void buttonClicked(int buttonId);
	void onInit();
	void onFragmentCreatedNoInit();
	
	/**
	 * This metod will called when radiobuttons status change
	 * @param qualityKey to identify quality, Params.QUALITY_64 or Params.QUALITY_128
	 */
			
	void setQuality (int qualityKey);
}
