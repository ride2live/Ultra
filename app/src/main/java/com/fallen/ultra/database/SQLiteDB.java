package com.fallen.ultra.database;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.fallen.ultra.utils.Params;
import com.fallen.ultra.utils.ParamsDB;
import com.fallen.ultra.utils.UtilsUltra;

import java.io.UnsupportedEncodingException;

public class SQLiteDB extends SQLiteOpenHelper {

    SQLiteDatabase db;

    public SQLiteDB(Context context) {
        super(context, ParamsDB.DB_NAME, null, ParamsDB.DB_VERSION);
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

    public int actionFavArtist(String name, String track) {
        //name = prepeareForSQL(name);
        //track = prepeareForSQL(track);
        int result_type;
        UtilsUltra.printLog("actionFav "+name +" " + track);
        if (db == null) return Params.DB_IS_NULL;
        //getAllFields();

        if (checkIfAlreadyAdded(name, track))
            if (removeArtistFromDB(name, track)) result_type = Params.DB_ARTIST_DELETED;
            else result_type = Params.DB_ERROR_ON_DELETE;
        else {
            ContentValues cv = new ContentValues();
            cv.put(ParamsDB.DB_ARTIST_COLUMN, name);
            cv.put(ParamsDB.DB_TRACK_COLUMN, track);
            long insertCheck = db.insert(ParamsDB.DB_TABLE_NAME, null, cv);
            if (insertCheck == -1)
                result_type = Params.DB_ERROR_ON_INSERT;
            else result_type = Params.DB_ADD_SUCCESS;
        }
        return result_type;

    }

    private String prepeareForSQL(String unprepearedText) {
        String prepearedText = "'" + Params.NO_TITLE + "'";
        if (unprepearedText ==null)
            unprepearedText = "";
        if (!unprepearedText.startsWith("'")) {
            prepearedText = DatabaseUtils.sqlEscapeString(unprepearedText);
            System.out.println("just now prepeared " + prepearedText);
        }
        else
        {
            System.out.println("Already Prepeared " + prepearedText);
        }
      //  if (prepearedText.length()>2) {
//            String unwanted = prepearedText.substring(0, 1);
//            char [] unwantedArray = unwanted.toCharArray();
//
//            System.out.println("unwanted character " + (int)unwantedArray [0]);
            //prepearedText = prepearedText.substring(1, prepearedText.length()-1);
       // }

        return prepearedText;
    }

    private boolean removeArtistFromDB(String name, String track) {
        name = prepeareForSQL(name);
        track = prepeareForSQL(track);
        UtilsUltra.printLog("removeArtistFromDB");
        //long deleteCountCheck = db.delete(ParamsDB.DB_TABLE_NAME, ParamsDB.DB_ARTIST_COLUMN + " LIKE '" + name + "' AND " + ParamsDB.DB_TRACK_COLUMN + " LIKE '" + track +"'", null);
        long deleteCountCheck = db.delete(ParamsDB.DB_TABLE_NAME, ParamsDB.DB_ARTIST_COLUMN + " LIKE " + name + " AND " + ParamsDB.DB_TRACK_COLUMN + " LIKE " + track +"", null);
        if (deleteCountCheck != -1) return true;
        else return false;


    }
    public boolean removeArtistFromDB(int id) {
        long deleteCountCheck = db.delete(ParamsDB.DB_TABLE_NAME, ParamsDB.DB_ID_COLUMN + " = " + id, null);
        if (deleteCountCheck != -1) return true;
        else return false;
    }

    public boolean checkIfAlreadyAdded(String name, String track) {
        name = prepeareForSQL(name);
        track = prepeareForSQL(track);
        UtilsUltra.printLog(ParamsDB.DB_ARTIST_COLUMN + " LIKE " + name + " AND " + ParamsDB.DB_TRACK_COLUMN + " LIKE " + track);
        //Cursor c = db.query(ParamsDB.DB_TABLE_NAME,null, ParamsDB.DB_ARTIST_COLUMN + " LIKE '" + name + "' AND " + ParamsDB.DB_TRACK_COLUMN + " LIKE '" + track+"'",null, null ,null, null);
        Cursor c = db.query(ParamsDB.DB_TABLE_NAME,null, ParamsDB.DB_ARTIST_COLUMN + " LIKE " + name + " AND " + ParamsDB.DB_TRACK_COLUMN + " LIKE " + track,null, null ,null, null);
        c.moveToFirst();
//        while (c.moveToNext())
//        {
//            UtilsUltra.printLog(String.valueOf(c.getString(c.getColumnIndex(ParamsDB.DB_ARTIST_COLUMN))) + " track " + String.valueOf(c.getString(c.getColumnIndex(ParamsDB.DB_TRACK_COLUMN))));
//        }
        if (c.getCount()>0) {
            UtilsUltra.printLog("true, go to delete");

            return true;
        }
        else {
            UtilsUltra.printLog("false, go to add");
            return false;
        }

    }

    public Cursor getAllFields ()
    {

        Cursor c = db.query(ParamsDB.DB_TABLE_NAME, null, null, null ,null, null, null);
        c.moveToFirst();

        return c;
    }


}
