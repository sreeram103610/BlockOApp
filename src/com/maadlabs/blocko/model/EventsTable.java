package com.maadlabs.blocko.model;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

public class EventsTable extends MyTable{

	public static final String TABLE_EVENTS = "events";
	 
    public static final String KEY_ID = "id";
    public static final String KEY_NAME = "name";
    public static final String KEY_DATE = "date";
    public static final String KEY_POINTS = "points";
    public static final String KEY_COUNT = "count";

	public static final String KEY_DATE_GENERIC = "genericFormatDate";
    
	public EventsTable(Context context) {
		super(context);
		open();
		createTable();
		
	}

	public void createTable()
	{
		
		
		String CREATE_EVENTS_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_EVENTS + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " + KEY_NAME + " TEXT, "
                + KEY_DATE + " STRING, " + KEY_POINTS + " INTEGER, " + KEY_COUNT + " INTEGER DEFAULT 0" + ")";
        
		database.execSQL(CREATE_EVENTS_TABLE);
		
		
	}
	
	public void deleteTable()
	{
	
		database.execSQL("DROP TABLE IF EXISTS " + TABLE_EVENTS);
		
	}
	
	public ArrayList<HashMap<String, ?>> getAllEvents()
	{
		
		
		Cursor cursor = database.rawQuery("select *  from "+TABLE_EVENTS, null);
		ArrayList<HashMap<String, ?>> values = null;
		
		Log.i("Cursor count", cursor.getCount()+"");
		
		if(cursor.getCount() > 0) {
			
			cursor.moveToLast();
			values = new ArrayList<HashMap<String, ?>>();
			
			do
			{
				HashMap<String, String> row = new HashMap<String, String>();
				row.put(KEY_ID, Integer.toString(cursor.getInt(cursor.getColumnIndex(KEY_ID))));
				row.put(KEY_NAME, cursor.getString(cursor.getColumnIndex(KEY_NAME)));
				row.put(KEY_DATE, cursor.getString(cursor.getColumnIndex(KEY_DATE)));
				row.put(KEY_POINTS, Integer.toString(cursor.getInt(cursor.getColumnIndex(KEY_POINTS))));
				row.put(KEY_COUNT, (Integer.toString(cursor.getInt(cursor.getColumnIndex(KEY_COUNT))) + "person"));
			
				values.add(row);
				
				if(cursor.isFirst())
					break;
				
				cursor.moveToPrevious();
				
			}while(true);
		
			Log.i("sizeEventsTable", values.size()+"");
		}
		
		return values;
	}
	
	public void addEvent(Event event)
	{
		
		ContentValues values = new ContentValues();
		
		values.put(KEY_NAME, event.getmName());
		values.put(KEY_DATE, dateToTable(event.getDate()));
		values.put(KEY_POINTS, event.getPoints());
		
		database.insert(TABLE_EVENTS, null, values);
		
	}
	
	public boolean removeEvent(HashMap<String, ?> event) {
		
		
		return database.delete(TABLE_EVENTS, KEY_ID + "=" + event.get(KEY_ID), null) > 0;
		
	}
	
	public String dateToTable(Date date)
	{
		return new SimpleDateFormat("yyyy-MM-dd").format(date);
	}

	public int getCount(Event event) {

	
		String sql = "select * from " + TABLE_EVENTS + " where " + KEY_ID + " = " + event.getId();
		Log.i("tableId", event.getId());
		Cursor cursor = database.rawQuery(sql, null);
		
		
		if(cursor.getCount() > 0) {
			Log.i("count", cursor.getCount()+"");
			cursor.moveToFirst();
			
			return cursor.getInt(cursor.getColumnIndex(KEY_COUNT));
		}
		
		return 0;
		
	}
	
	public void updateCount(Event event) {
		
		
		Cursor cursor = database.rawQuery("select *  from "+ getTableIdEvent(event), null);
		
		ContentValues values = new ContentValues();
		values.put(EventsTable.KEY_COUNT, cursor.getCount());
		database.update(EventsTable.TABLE_EVENTS, values, EventsTable.KEY_ID + " = " + event.getId(), null);
		
	}
	
}
