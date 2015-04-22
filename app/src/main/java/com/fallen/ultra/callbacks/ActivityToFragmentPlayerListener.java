package com.fallen.ultra.callbacks;

import com.fallen.ultra.com.fallen.ultra.model.StatusObjectOverall;
import com.fallen.ultra.com.fallen.ultra.model.StatusObjectPlayer;

public interface ActivityToFragmentPlayerListener {

	void onStatusChanged(StatusObjectOverall stausObj);
	void onImageBuffered();
    void onFavoriteDefine (boolean isAlreadyInFav);
    void onRebindRestoreStatus(StatusObjectOverall currentStatusObjectOverall);

    void onRestartStream();
}
