package com.maadlabs.blocko;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.maadlabs.blocko.model.Event;
import com.maadlabs.blocko.model.EventTable;
import com.maadlabs.blocko.model.EventsTable;
import com.maadlabs.blocko.model.MyTable;
import com.maadlabs.util.CustomFonts;

public class EventActivity extends Activity {

	public static final String EVENT_STATUS = "eventStatus";
	Button mEventStart, mEventResume, mEventEnd;
	TextView mEventName, mEventCount, mEventPoints;
	EventTable eventTable;
	EventsTable eventsTable;
	OnClickListener mOnClickListener;
	TableLayout mTableLayout;
	CustomFonts mCustomFonts;
	HashMap<String, String> event;
	EventStatus mEventStatus;
	ArrayList<HashMap<String, String>> mPeople;
	private TextView mEventDate;
	private boolean mNewDataDisplayed;
	private boolean mDisplayNewData;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_event);
	
		if(savedInstanceState != null) {
			
			restoreState(savedInstanceState);
		}
		
		initParentData();
		initData();
		init();
		initProperties();
		initListeners();
		
	}	

	static enum EventStatus {
		START, RESUME, END;
	}
	
	void restoreState(Bundle savedInstanceState) {
		
		if(savedInstanceState.containsKey(EVENT_STATUS))
		mEventStatus = EventStatus.valueOf((String) savedInstanceState.get(EVENT_STATUS));
		
	}
	@SuppressWarnings("unchecked")
	void initParentData() {
		
		Bundle bundle = getIntent().getExtras();
		event = (HashMap<String, String>) bundle.getSerializable("event");		
	}
	
	void initProperties() {
		
		mEventCount.setText(Integer.toString(eventsTable.getCount(hashToClass(event))));
		mEventPoints.setText(event.get(EventsTable.KEY_POINTS));
		mEventDate.setText(event.get(EventsTable.KEY_DATE_GENERIC));
		mEventName.setText(event.get(EventsTable.KEY_NAME));
		mCustomFonts.init(getApplicationContext(), getWindow().getDecorView());
		
		toggleVisibility(mEventStatus);
		if(mEventStatus != EventStatus.START)
			displayDetails();
	}

	
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onRestoreInstanceState(savedInstanceState);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		
		outState.putString(EVENT_STATUS, mEventStatus.name());
		super.onSaveInstanceState(outState);
	}

	void init() {
		 
		mEventStart = (Button) findViewById(R.id.buttonStartEvent);
		mEventName = (TextView) findViewById(R.id.textViewEventName);
		mEventStart = (Button) findViewById(R.id.buttonStartEvent);
		mEventResume = (Button) findViewById(R.id.buttonResumeEvent);
		mEventEnd = (Button) findViewById(R.id.buttonEndEvent);
		
		mEventDate = (TextView) findViewById(R.id.textViewEventDate);
		mEventPoints = (TextView) findViewById(R.id.textViewPointsValue);
		mEventCount = (TextView) findViewById(R.id.textViewCountValue);
		
		mTableLayout = (TableLayout) findViewById(R.id.usersTable);

	}
	
	void initData() {

		eventTable = new EventTable(this);
		eventsTable = new EventsTable(this);
		
		mCustomFonts = new CustomFonts();
		
		mDisplayNewData = false;
		updateEventStatus();
	}

	void updateEventStatus() {
		
		boolean hasTable = eventTable.hasTable(hashToClass(event));
		int count = eventsTable.getCount(hashToClass(event));
		
		if((hasTable) && (count > 0)) {
			
			mEventStatus = EventStatus.END;
		}
		else if((!hasTable) && (count == 0)) {
			
			mEventStatus = EventStatus.START;
		}
		else if(hasTable && (count == 0)) {
			
			mEventStatus = EventStatus.RESUME;
		}
	}
	public void barCodeScan() {
		Intent intent = new Intent("com.google.zxing.client.android.SCAN");
		intent.putExtra("SAVE_HISTORY", true);
		startActivityForResult(intent, 0);
	}
	
	public void displayDetails() {
		
		TextView textView;
		TableRow row;
		
		mEventCount.setText(Integer.toString(eventsTable.getCount(hashToClass(event))));
		
		mPeople = eventTable.getPeople(MyTable.getTableIdEvent(hashToClass(event)));

		if(mPeople != null) {
			
		TableLayout.LayoutParams rowParams = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT);
		rowParams.setMargins(5, 5, 5, 5);
		
		TableRow.LayoutParams cellParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
		cellParams.setMargins(5, 5, 5, 5);
		
		row = new TableRow(this);
		row.setLayoutParams(rowParams);
		
		textView = new TextView(this);
		textView.setLayoutParams(cellParams);
		textView.setText("Number");
		
		row.addView(textView);

		textView = new TextView(this);
		textView.setLayoutParams(cellParams);
		textView.setText("Person Id");
		
		row.addView(textView);
		
		mTableLayout.addView(row);
		for(HashMap<String, String> person : mPeople) {
			
			row = new TableRow(this);
			row.setLayoutParams(rowParams);
			
			for(Iterator<String> iter = person.keySet().iterator(); iter.hasNext();) {
				
				textView = new TextView(this);
				textView.setLayoutParams(cellParams);
				textView.setTextColor(Color.WHITE);
				textView.setPadding(5, 5, 5, 5);
				textView.setGravity(Gravity.CENTER);
				textView.setTextSize(18);
				textView.setText(person.get(iter.next()));
				textView.setBackgroundColor(R.drawable.table_border);
				
				row.addView(textView);
			}
			
			
			mTableLayout.addView(row);
		}
		
		}
	}
	private void initListeners() {
			
		mOnClickListener = new OnClickListener() {

			@Override
			public void onClick(View v) {

				if(v.getId() == mEventStart.getId()) {
					
					mEventStatus = EventStatus.RESUME;
					barCodeScan();
				}
				else if(v.getId() == mEventResume.getId()) {
					
					barCodeScan();
				}
				else if(v.getId() == mEventEnd.getId()) {
					
					mEventStatus = EventStatus.END;
					toggleVisibility(EventStatus.END);
					eventTable.updateCount(hashToClass(event));
				}
				
			}
			
		};
		
		mEventStart.setOnClickListener(mOnClickListener);
		mEventResume.setOnClickListener(mOnClickListener);
		mEventEnd.setOnClickListener(mOnClickListener);
	}
	
public String convertDateFormat(String date, String oldFormat, String newFormat) throws ParseException {
		
		SimpleDateFormat originalFormat = new SimpleDateFormat(oldFormat, Locale.ENGLISH);
		SimpleDateFormat targetFormat = new SimpleDateFormat(newFormat);
		java.util.Date tDate = originalFormat.parse(date);
		return targetFormat.format(tDate); 
	}

	public static Event hashToClass(HashMap<String, String> data) {
		
		Event event = null;
		try {
			event = new Event(data.get(EventsTable.KEY_NAME), Date.valueOf(data.get(EventsTable.KEY_DATE_GENERIC)), Integer.parseInt(data.get(EventsTable.KEY_POINTS)));
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		event.setId(data.get(EventsTable.KEY_ID));
		return event;
	
	}
	
	public void toggleVisibility(EventStatus key) {
		
		Log.i("estatus", key.name());
		switch(key) {
		
			case START:
				mEventStart.setVisibility(View.VISIBLE);
            	mEventResume.setVisibility(View.GONE);
            	mEventEnd.setVisibility(View.GONE);
            	break;
            
			case RESUME:
				mEventStart.setVisibility(View.GONE);
            	mEventResume.setVisibility(View.VISIBLE);
            	mEventEnd.setVisibility(View.VISIBLE);
            	break;
            	
			case END:
				mEventStart.setVisibility(View.GONE);
            	mEventResume.setVisibility(View.GONE);
            	mEventEnd.setVisibility(View.GONE);
            	break;
            	
		}
	}
	@Override
	public void onResume() {
		
		super.onResume();

		toggleVisibility(mEventStatus);
		
		if(mDisplayNewData == true)
		{
			mDisplayNewData = false;
			mTableLayout.removeAllViews();
			displayDetails();
		}
		
	}
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        ArrayList<String> results;
        
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
            	
            	results = new ArrayList<String>();
            	
            	results = data.getStringArrayListExtra("data");
            	
            	eventTable.createEvent(event.get(EventsTable.KEY_ID+""));
            	Event tEvent = hashToClass(event);
            	
            	Log.i("data", results.size()+"");
            	eventTable.updateTable(results, tEvent);
            
            	mDisplayNewData = true;
            } else 
            if (resultCode == RESULT_CANCELED) {
              // Handle cancel
            }
        }
    }
}
