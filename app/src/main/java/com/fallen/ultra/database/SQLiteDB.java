package com.fallen.ultra.database;


import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.fallen.ultra.utils.Params;
import com.fallen.ultra.utils.ParamsDB;

public class SQLiteDB extends SQLiteOpenHelper {

    SQLiteDatabase db;

    public SQLiteDB(Context context) {
        super(context, ParamsDB.DB_NAME_COLUMN, null, ParamsDB.DB_VERSION);
        db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {


        db.execSQL("CREATE TABLE " + ParamsDB.DB_TABLE_NAME + " (" + ParamsDB.DB_ID_COLUMN + " INTEGER PRIMARY KEY ASC AUTOINCREMENT, "
                + ParamsDB.DB_ARTIST_COLUMN + " 'text', " + ParamsDB.DB_TRACK_COLUMN + "'text', " + ParamsDB.DB_IMAGE + "'text', " + ParamsDB.DB_DATE_COLUMN + "'long')");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //will be added in next versions

    }

    public int putNewArtist(String name, String track) {
        int result_type = Params.DB_IDLE;
        if (db == null) return Params.DB_IS_NULL;

        if (checkIfAlreadyAdded())
            if (removeArtistFromDB(name, track)) result_type = Params.DB_ARTIST_DELETED;
            else result_type = Params.DB_ERROR_ON_DELETE;
        else {
            ContentValues cv = new ContentValues();
            cv.put(ParamsDB.DB_NAME_COLUMN, name);
            cv.put(ParamsDB.DB_TRACK_COLUMN, track);
            long insertCheck = db.insert(ParamsDB.DB_TABLE_NAME, null, cv);
            if (insertCheck != -1) result_type = Params.DB_ERROR_ON_INSERT;
            else result_type = Params.DB_ADD_SUCCESS;
        }

        return result_type;

    }

    private boolean removeArtistFromDB(String name, String track) {

        long deleteCountCheck = db.delete(ParamsDB.DB_TABLE_NAME, ParamsDB.DB_NAME_COLUMN + " = " + name + " AND " + ParamsDB.DB_TRACK_COLUMN + " = " + track, null);
        if (deleteCountCheck != -1) return true;
        else return false;


    }

    private boolean checkIfAlreadyAdded() {

        return false;
    }


}
