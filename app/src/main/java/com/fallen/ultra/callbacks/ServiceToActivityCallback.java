package com.fallen.ultra.callbacks;

import com.fallen.ultra.com.fallen.ultra.model.StatusObjectOverall;

public interface ServiceToActivityCallback {

	void onUnbindService ();


	void onImageBuffered();

    void onStreamStop();

    void onUpdateStatus(StatusObjectOverall statusObjectOverall);
}
