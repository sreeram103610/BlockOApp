package com.maadlabs.blocko.model;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

public class EventTable extends MyTable {

	public static final String KEY_ID = "id", KEY_PERSON_ID = "pid";
	EventsTable eventsTable;
	
	public EventTable(Context context) {
		super(context);
		eventsTable = new EventsTable(context);
		open();
	}

	public void createEvent(String event)
	{
		String eventId = STRING_EVENT + "_" + event;
		
		String createTableSQL = "CREATE TABLE IF NOT EXISTS " + eventId + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL" + ", " + KEY_PERSON_ID + " STRING" + " )";

		database.execSQL(createTableSQL);
	}
	
	public void addPerson(Person person, Event event)
	{
		String eventTableId = getTableIdEvent(event);
		
		ContentValues values = new ContentValues();
		
	    values.put(KEY_PERSON_ID, person.getId());
	    
	    if(database.insert(eventTableId, null, values) < 0)
	    {
	    	Log.i("Error inserting Id ", person.getId());
	    }
	}
	
	public ArrayList<HashMap<String, String>> getPeople(String tableId) {
		
		
		
		Cursor cursor = database.rawQuery("select *  from "+ tableId, null);
		ArrayList<HashMap<String, String>> values = null;
		
		Log.i("Cursor count", cursor.getCount()+"");
		
		if(cursor.getCount() > 0) {
			
			cursor.moveToFirst();
			values = new ArrayList<HashMap<String, String>>();
			
			do {
				
				
				HashMap<String, String> row = new HashMap<String, String>();
				row.put(KEY_ID, cursor.getInt(cursor.getColumnIndex(KEY_ID))+"");
				row.put(KEY_PERSON_ID, cursor.getString(cursor.getColumnIndex(KEY_PERSON_ID)));
				
				values.add(row);
			
			}while(cursor.moveToNext());
			
			return values;
		}
		
		return null;
	}
	
	public void removePerson(Person person, Event event)
	{
		String eventTableId = getTableIdEvent(event);
		database.delete(eventTableId, KEY_ID + " = " + person.getId() , null);
	}
	

	
	public void updateTable(ArrayList<String> people, Event event) {
		
		for(String person : people) {
			
			addPerson(new Person(person), event);
		}
		
		event.setCount(people.size());
	}
	
	public void updateCount(Event event) {
		
		eventsTable.updateCount(event);
	}

	public boolean hasTable(Event event) {


		String sql = null;
		Cursor cursor = null;
		
		try {

		sql = "select * from " + getTableIdEvent(event);
		cursor = database.rawQuery(sql, null);
		} catch(Exception e) {
			
			return false;
		}

		return true;
	}
}
