package com.fallen.ultra.database;



import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.fallen.ultra.utils.ParamsDB;

public class SQLiteDB extends SQLiteOpenHelper{

	public SQLiteDB(Context context) {
		super(context, ParamsDB.DB_NAME, null, ParamsDB.DB_VERSION);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		
		db.execSQL("CREATE TABLE " +ParamsDB.DB_TABLE_NAME+" ("+ParamsDB.DB_ID_COLUMN+" INTEGER PRIMARY KEY ASC AUTOINCREMENT, "
				+ ParamsDB.DB_ARTIST_COLUMN+" 'text', "+ ParamsDB.DB_TRACK_COLUMN + "'text', " + ParamsDB.DB_DATE_COLUMN + "'long')");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}
	
	

}
