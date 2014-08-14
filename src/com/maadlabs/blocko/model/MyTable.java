package com.maadlabs.blocko.model;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class MyTable {

	protected SQLiteDatabase database;
	private MySQLiteHelper dbHelper;
	
	public static final String STRING_EVENT = "event";
	
	public MyTable(Context context) {
	    dbHelper = new MySQLiteHelper(context);
	  }

	  public void open() throws SQLException {
	    database = dbHelper.getWritableDatabase();
	  }

	  public void close() {
	    dbHelper.close();
	  }
	  
		public static String getTableIdEvent(Event event)
		{
			return STRING_EVENT + "_" + event.getId().toString();
		}
		
		
}
