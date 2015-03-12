package com.fallen.ultra.callbacks;

import com.fallen.ultra.creators.StatusObject;

public interface ServiceToActivityCallback {

	void onUnbindService ();

	void onRebindStatus(StatusObject statusObjectRebinded);

	void onImageBuffered();
}
