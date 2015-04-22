package com.fallen.ultra.callbacks;

import android.content.Context;
import android.database.Cursor;



public interface ActivityToFragmentFavListener {

	void setFavAdapter(Context context, Cursor cursor);
    void artistDeleted (Cursor cursor);

	
	
}
