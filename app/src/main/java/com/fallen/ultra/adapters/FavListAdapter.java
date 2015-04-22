package com.fallen.ultra.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.fallen.ultra.R;
import com.fallen.ultra.callbacks.ActivityToFragmentFavListener;
import com.fallen.ultra.callbacks.FavlistFragmentCallback;
import com.fallen.ultra.database.SQLiteDB;
import com.fallen.ultra.utils.ParamsDB;

/**
 * Created by Nolwe on 31.03.2015.
 */
public class FavListAdapter extends CursorAdapter {
    FavlistFragmentCallback favlistFragmentCallback;

    public FavListAdapter(Context context, Cursor c, boolean autoRequery,  FavlistFragmentCallback favlistFragmentCallback) {
        super(context, c, autoRequery);
        this.favlistFragmentCallback = favlistFragmentCallback;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {


        return LayoutInflater.from(context).inflate(R.layout.row_fav_list, null);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        String testString = cursor.getString(cursor.getColumnIndex(ParamsDB.DB_ARTIST_COLUMN))+" "+cursor.getString(cursor.getColumnIndex(ParamsDB.DB_TRACK_COLUMN));
        TextView testTextView = (TextView) view.findViewById(R.id.test);
        Button delButton = (Button) view.findViewById(R.id.deleteRowButton);
        final String currentText = testString;
        final Context currentContext = context;
        final int itemId = cursor.getInt(cursor.getColumnIndex(ParamsDB.DB_ID_COLUMN));
        delButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Toast.makeText(context,currentText, Toast.LENGTH_SHORT).show();
                if (favlistFragmentCallback != null)
                    favlistFragmentCallback.deleteItemClicked(itemId);

            }
        });
        testTextView.setText(testString);
    }
}
