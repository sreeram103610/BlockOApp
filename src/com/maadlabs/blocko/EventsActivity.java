package com.maadlabs.blocko;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.TextView;
import android.widget.TimePicker;

import com.hb.views.PinnedSectionListView;
import com.maadlabs.blocko.model.Event;
import com.maadlabs.blocko.model.EventsTable;
import com.maadlabs.blocko.model.ListItem;
import com.maadlabs.blocko.model.MyTable;
import com.maadlabs.blocko.view.MyOnScrollListener;
import com.maadlabs.blocko.view.MySimpleSectionsAdapter;
import com.maadlabs.util.CustomFonts;
import com.maadlabs.util.ExportDatabaseTask;
import com.maadlabs.util.QuickReturnType;
import com.nineoldandroids.view.animation.AnimatorProxy;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelSlideListener;

@SuppressLint("SimpleDateFormat")
public class EventsActivity extends Activity {

	private final static int ONE   = 1;
	private final static int TWO   = 2;
	private final static int THREE = 3;
	static final String EVENTS = "Events";
	static final String TAG = "slidingPanel";
	PinnedSectionListView mEventsListView;
	MySimpleSectionsAdapter mSimpleSectionsAdapter;
	ArrayList<HashMap<String, ?>> mEventsList;
	String[] eventsListFrom;
	EventsTable eventsTable;
	ArrayList<HashMap<String, ?>> mSectionsList;
	int[] eventsListTo, sectionsListTo;	
	CustomFonts customFonts;
	Button mAddEventFooterButton, mItemOptionsButton;
	QuickReturnType quickReturnType;
	SlidingUpPanelLayout mLayout;
	SlidingUpPanelLayout.LayoutParams layoutParams;
	EditText mDateEditText, mTimeEditText, mEventNameEditText, mEventPointsEditText;
	Button mSaveButton, mDiscardButton;
	View dividerView;
	OnClickListener mOnClickListener;
	private OnFocusChangeListener mOnFocusListener;
	private OnItemClickListener mOnItemClickListener;
	PopupMenu mPopupMenu;
	MenuInflater menuInflater;
	ActionBar actionBar;
	InputMethodManager inputManager;
	ArrayList<ListItem> adapterItems;
	private ArrayList<ListItem> mAdapterData;
	private TextView mNoEventsTextView;
	
	static enum DialogFlag {
		
		DATE, TIME;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);

		getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
		setContentView(R.layout.activity_main);
		
		
		init();
		initProperties();
		initAdapters();
		initListeners();
	}
	
	public void initProperties() {
		 
		 mEventsListView.setPadding(0, getActionBarHeight(), 0, 0);
		 quickReturnType = QuickReturnType.CUSTOM;
		 
		 mDateEditText.setClickable(true);
		 mDateEditText.setInputType(InputType.TYPE_NULL);
		 
		 mTimeEditText.setClickable(true);
		 mTimeEditText.setInputType(InputType.TYPE_NULL);
	
	}
	public void init()
	{
		customFonts = new CustomFonts();
		customFonts.init(getApplicationContext(), getWindow().getDecorView().findViewById(android.R.id.content));
		
		mAdapterData = new ArrayList<ListItem>();
		
		mEventsListView = (PinnedSectionListView) findViewById(R.id.listViewEvents);
		mEventsList = new ArrayList<HashMap<String, ?>>();
		eventsTable = new EventsTable(this);
		
		eventsListFrom = new String[] {EventsTable.KEY_NAME, EventsTable.KEY_DATE};
		eventsListTo = new int[] {R.id.textViewEventsName, R.id.textViewEventsDate};
		sectionsListTo = new int[] {R.id.textViewMonthSection};
	
		mAddEventFooterButton = (Button) findViewById(R.id.buttonAddNewEvent);
		
		mLayout = (SlidingUpPanelLayout) findViewById(R.id.slidingLayoutMainActivity);
		
		layoutParams = new SlidingUpPanelLayout.LayoutParams(mEventsListView.getLayoutParams());
		dividerView = (View) findViewById(R.id.dividerLine);
		mDateEditText = (EditText) findViewById(R.id.editTextEventDate);
		
		mSaveButton = (Button) findViewById(R.id.buttonSave);
		mDiscardButton = (Button) findViewById(R.id.buttonDiscard);
		mDateEditText = (EditText) findViewById(R.id.editTextEventDate);
		mTimeEditText = (EditText) findViewById(R.id.editTextEventTime);
		mEventPointsEditText = (EditText) findViewById(R.id.editTextEventPoints);
		mEventNameEditText = (EditText) findViewById(R.id.editTextEventName);
	//	mNoEventsTextView = (TextView) findViewById(R.id.textViewNoEvents);
		
		inputManager = (InputMethodManager) this
		            .getSystemService(Context.INPUT_METHOD_SERVICE);
		
	}

	/*
	 * @param: name - true  - date
	 * 				- false - time
	 */
	
	public void showDialog(DialogFlag value) {
		
		Calendar tCalendar = Calendar.getInstance();
		
		switch(value) {
			
		case DATE:
			int tYear, tMonth, tDay;
			tYear = tCalendar.get(Calendar.YEAR);
			tMonth = tCalendar.get(Calendar.MONTH);
			tDay = tCalendar.get(Calendar.DAY_OF_MONTH);
			DatePickerDialog tDatePickerDialog = new DatePickerDialog(EventsActivity.this, 
					new DatePickerDialog.OnDateSetListener() {
	
	            @Override
	            public void onDateSet(DatePicker view, int year,
	                    int monthOfYear, int dayOfMonth) {
	                // Display Selected date in textbox
	                mDateEditText.setText((monthOfYear + 1) + "/"
	                        + (dayOfMonth) + "/" + year);
	
	            }
	        }, tYear, tMonth, tDay);
			
			tDatePickerDialog.setCancelable(true);
			tDatePickerDialog.show();
			break;
			
		case TIME:
		
			int tHour, tMinutes;
			tHour = tCalendar.get(Calendar.HOUR_OF_DAY);
			tMinutes = tCalendar.get(Calendar.MINUTE);
			
			TimePickerDialog tTimePickerDialog = new TimePickerDialog(EventsActivity.this,
					new TimePickerDialog.OnTimeSetListener() {
						
						@Override
						public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

							mTimeEditText.setText(hourOfDay+ ":" + minute);
						}
					}, tHour, tMinutes, true);
			
			tTimePickerDialog.setCancelable(true);
			tTimePickerDialog.show();
			break;
		}
	}
	
	public void OnClickListItemOptions(View v) {
		
		final View myView = v;
		mPopupMenu = new PopupMenu(getBaseContext(), v);
		mPopupMenu.getMenuInflater().inflate(R.menu.popup_menu, mPopupMenu.getMenu());
		
		mPopupMenu.setOnMenuItemClickListener(new OnMenuItemClickListener()
		{
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				
				int position = mEventsListView.getPositionForView((View) myView.getParent());
				ListItem listItem = mSimpleSectionsAdapter.getItem(position);
				
				if(item.getTitle().equals("Delete")) {
					
					eventsTable.removeEvent(listItem.getEvent());

					if(mSimpleSectionsAdapter.getCount() >= position + 2) {
						if((mSimpleSectionsAdapter.getItem(position + 1).getType() == ListItem.SECTION) &&
								(mSimpleSectionsAdapter.getItem(position - 1).getType() == ListItem.SECTION)){
							
							mSimpleSectionsAdapter.remove(mSimpleSectionsAdapter.getItem(position - 1));
						}
					}
					else if(mSimpleSectionsAdapter.getCount() == position + 1) {
						if(mSimpleSectionsAdapter.getItem(position - 1).getType() == ListItem.SECTION)
							mSimpleSectionsAdapter.remove(mSimpleSectionsAdapter.getItem(position - 1));
					}
					
					mSimpleSectionsAdapter.remove(listItem);
					mSimpleSectionsAdapter.notifyDataSetChanged();
				}
				else if(item.getTitle().equals("View")) {
					
					startActivity(listItem);
				}
				else if(item.getTitle().equals("Export")) {
					
					ExportDatabaseTask exportDatabaseTask = new ExportDatabaseTask(EventsActivity.this, MyTable.getTableIdEvent(EventActivity.hashToClass((HashMap<String, String>) listItem.getEvent())), (String) listItem.getEvent().get(EventsTable.KEY_NAME));
					exportDatabaseTask.execute();
					
				}
				
				return false;
			}
			
		});
		
		mPopupMenu.show();
	}
	public void toggleVisibility(boolean visible) {
		
		if(visible)
		{
			mAddEventFooterButton.setVisibility(View.GONE);
            mSaveButton.setVisibility(View.VISIBLE);
            mDiscardButton.setVisibility(View.VISIBLE);
            dividerView.setVisibility(View.VISIBLE);
		}
		else
		{
			mAddEventFooterButton.setVisibility(View.VISIBLE);
            mSaveButton.setVisibility(View.GONE);
            mDiscardButton.setVisibility(View.GONE);
            dividerView.setVisibility(View.GONE);
		}
	}
	
	public String convertDateFormat(String date, String oldFormat, String newFormat) throws ParseException {
		
		SimpleDateFormat originalFormat = new SimpleDateFormat(oldFormat, Locale.ENGLISH);
		SimpleDateFormat targetFormat = new SimpleDateFormat(newFormat);
		java.util.Date tDate = originalFormat.parse(date);
		return targetFormat.format(tDate); 
	}
	
	public void addFormDataToTable() {
		String cDate = null;
		
		try {
			cDate = convertDateFormat(mDateEditText.getText().toString(), "mm/dd/yyyy", "yyyy-mm-dd");
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Event newEvent = new Event(mEventNameEditText.getText().toString(), Date.valueOf(cDate), Integer.parseInt(mEventPointsEditText.getText().toString()));
		eventsTable.addEvent(newEvent);
	}
	
	public void updateAdapterData() {
		
		ArrayList<HashMap<String, ?>> eventsData = eventsTable.getAllEvents();
		if(eventsData != null)
		{
			makeAdapterData(eventsData);
		}
	}
	
	public void startActivity(ListItem listItem) {
		
		Bundle bundle = new Bundle();
		bundle.putSerializable("event", listItem.getEvent());
		
		Intent intent = new Intent(EventsActivity.this, EventActivity.class);
		intent.putExtras(bundle);
		startActivity(intent);
	}
	
	public void initListeners()
	{
		mOnFocusListener = new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {

				if(hasFocus)
				switch(v.getId()) {
				
				case R.id.editTextEventDate:
					showDialog(DialogFlag.DATE);
					break;
					
				case R.id.editTextEventTime:
					showDialog(DialogFlag.TIME);
					break;
				}
			}
			
		};
	
		mOnItemClickListener = new OnItemClickListener() {
			
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				Log.i("onItemClick", "listItem");
				
				ListItem listItem = (ListItem) parent.getAdapter().getItem(position);
				
				if(listItem.getType() == ListItem.ITEM) {
				
					startActivity(listItem);
				}
				
			}
			
		};
		
		mOnClickListener = new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				switch(v.getId()) {
				
					case R.id.buttonSave:
						validateForm();
						addFormDataToTable();
						mLayout.collapsePanel();
						updateAdapterData();
					/*	if(mEventsListView.getVisibility() == View.GONE) {
							
							mEventsListView.setVisibility(View.VISIBLE);
							mNoEventsTextView.setVisibility(View.GONE);
							mEventsListView.setAdapter(mSimpleSectionsAdapter);
						} */
						mSimpleSectionsAdapter.notifyDataSetChanged();

						break;
					
					case R.id.imageButtonEventOptions:
						mPopupMenu.show();
						break;
						
					case R.id.buttonAddNewEvent:
						mLayout.expandPanel();
						break;
						
					case R.id.buttonDiscard:
						mLayout.collapsePanel();
						break;
						
					case R.id.editTextEventDate:
						showDialog(DialogFlag.DATE);
						break;
						
					case R.id.editTextEventTime:
						showDialog(DialogFlag.TIME);
						break;
					
				}
			}
			
		};
		
		mDateEditText.setOnFocusChangeListener(mOnFocusListener);
		mTimeEditText.setOnFocusChangeListener(mOnFocusListener);
		
		mAddEventFooterButton.setOnClickListener(mOnClickListener);
		mDateEditText.setOnClickListener(mOnClickListener);
		mTimeEditText.setOnClickListener(mOnClickListener);
		mSaveButton.setOnClickListener(mOnClickListener);
		mDiscardButton.setOnClickListener(mOnClickListener);
		
		mEventsListView.setOnItemClickListener(mOnItemClickListener);
		
	}
	
	public void validateForm() {
		
	}
	public void setAdapterData() {
		
		if(mAdapterData.size() == 0)
		{
			Log.i("not", "null");
			//if(mEventsListView.getVisibility() != View.VISIBLE)
			//	mNoEventsTextView.setVisibility(View.GONE);
		}
		else
		{
			Log.i("null", "null");
			mEventsListView.setVisibility(View.GONE);
		//	mNoEventsTextView.setVisibility(View.VISIBLE);
		//	Log.i("text", mNoEventsTextView.getText().toString());
		}
		mEventsListView.setAdapter(mSimpleSectionsAdapter);
	}
	public void initAdapters()
	{
	
		updateAdapterData();
		mSimpleSectionsAdapter = new MySimpleSectionsAdapter(this, R.layout.list_item_event, R.layout.list_item_section, eventsListFrom, eventsListTo, sectionsListTo, mAdapterData);
		setAdapterData();
		
		mLayout.post(new Runnable()
		{
			 @Override
			    public void run() {
				 	
				 	setFooterScrollListener();
			 }
		});
		
		mLayout.setPanelSlideListener(new PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                Log.i(TAG, "onPanelSlide, offset " + slideOffset);
                setActionBarTranslation(mLayout.getCurrentParalaxOffset());
            }

            @Override
            public void onPanelExpanded(View panel) {
                Log.i(TAG, "onPanelExpanded");

                toggleVisibility(true);
            }

            @Override
            public void onPanelCollapsed(View panel) {
                Log.i(TAG, "onPanelCollapsed");
                
                toggleVisibility(false);
            }

            @Override
            public void onPanelAnchored(View panel) {
                Log.i(TAG, "onPanelAnchored");
            }

            @Override
            public void onPanelHidden(View panel) {
                Log.i(TAG, "onPanelHidden");
            }
        });
		
		
	}
	
	public void setFooterScrollListener() {
		
		mLayout.setPanelHeight(mAddEventFooterButton.getHeight());
		
	/*	mEventsListView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				int last = mEventsListView.getLastVisiblePosition();
				if(last == mEventsListView.getCount() - 1 && mEventsListView.getChildAt(last).getBottom() <= mEventsListView.getHeight())
				{
					if(mLayout.isPanelHidden()) {
						mLayout.showPanel();
					}
				}
				else
				{
					
				}
				return false;
			}
			
		});  
		mEventsListView.setOnScrollListener(new MyOnScrollListener(quickReturnType, null, 0, null, 0)
		{

			@Override
			public void onScrollUp(int distanceY, int offset) {
				
				
					if(mLayout.isPanelHidden())
					{
						mLayout.showPanel();
					}
			}
			
			
			@Override
			public void onScrollDown(int distanceY, int offset) {
						
					Log.i("distY", distanceY+"");
					int last = mEventsListView.getLastVisiblePosition();
					if(!((last == (mEventsListView.getCount() - 1)) && (mEventsListView.getChildAt(last).getBottom() <= mEventsListView.getHeight())))
					{
						Log.i("list", "fit");
					}
					else
					{
						Log.i("list", "unfit");
					}
					/*if(!mLayout.isPanelHidden())
					{
						mLayout.hidePanel();
					} //close inner comment here
			}
		}); */
	}
	
	@SuppressWarnings("unchecked")
	public void makeAdapterData(ArrayList<HashMap<String, ?>> events) {
		
		SimpleDateFormat dateFormat = null;
		
		int mMonthCount = 1;
		HashMap<String, String> month = new HashMap<String, String>();
		
		Collections.sort(events, new Comparator<HashMap<String, ?>>() {
			public int compare(HashMap<String, ?> objectA, HashMap<String, ?> objectB) {
				
				Log.i("objA", objectA.get(EventsTable.KEY_DATE).toString());
				
				 if((Date.valueOf(objectA.get(EventsTable.KEY_DATE).toString())).after(Date.valueOf(objectB.get(EventsTable.KEY_DATE).toString())))
					 return -1;
				 else
					 return 1;
			}
		});
		
		String mMonth = "";
		
		ListItem listItem;
		int i = 0, sectionCount = 0;
		
		dateFormat = new SimpleDateFormat("MMMM");
		
		mMonth = dateFormat.format(Date.valueOf((String) (events.get(0).get(EventsTable.KEY_DATE))));
		month.put("month", mMonth);
		
		if(mAdapterData.size() > 0)
			mAdapterData.clear();
		listItem = new ListItem(ListItem.ITEM, (HashMap<String, String>) events.get(i));
		mAdapterData.add(listItem);
		
		
		for(i = 1; i < events.size(); i++)
		{
			mMonth = dateFormat.format(Date.valueOf((String) (events.get(i).get(EventsTable.KEY_DATE))));
			
			if(!mMonth.equals(month.get("month")))
			{
				
				month.put("count", mMonthCount+"");
				Log.i("cc", mMonthCount+"");
				
				listItem = new ListItem(ListItem.SECTION, month);
				mAdapterData.add(i - mMonthCount + sectionCount, listItem);
				
				Log.i("myCount", mMonthCount+"");
				
				month = new HashMap<String, String>();
				month.put("month", mMonth);
				
				sectionCount++;
				mMonthCount = 1;
			}
			else
			{
				mMonthCount++;
			}
			
			listItem = new ListItem(ListItem.ITEM, (HashMap<String, String>) events.get(i));
			mAdapterData.add(listItem);
			
		}
		
		month.put("count", ""+mMonthCount);
		
		mAdapterData.add(i - mMonthCount + sectionCount, new ListItem(ListItem.SECTION, month));
		
		try {
			mAdapterData = modifyDates(mAdapterData);
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public ArrayList<HashMap<String, ?>> grabSections(ArrayList<HashMap<String, ?>> events) {
		
		SimpleDateFormat dateFormat = null;
		ArrayList<HashMap<String, ?>> mDates = null;

		Collections.sort(events, new Comparator<HashMap<String, ?>>() {
			public int compare(HashMap<String, ?> objectA, HashMap<String, ?> objectB) {
				
				Log.i("objA", objectA.get(EventsTable.KEY_DATE).toString());
				
				 if((Date.valueOf(objectA.get(EventsTable.KEY_DATE).toString())).after(Date.valueOf(objectB.get(EventsTable.KEY_DATE).toString())))
					 return -1;
				 else
					 return 1;
			}
		});
		
		String mMonth = "";
		
		int mMonthCount = 1;
		HashMap<String, String> month = new HashMap<String, String>();
		
		mDates = new ArrayList<HashMap<String, ?>>();
		dateFormat = new SimpleDateFormat("MMMM");
	
		mEventsList = events;
		
		mMonth = dateFormat.format(Date.valueOf((String) (events.get(0).get(EventsTable.KEY_DATE))));
		month.put("month", mMonth);
		
		for(int i = 1; i < events.size(); i++)
		{
			mMonth = dateFormat.format(Date.valueOf((String) (events.get(i).get(EventsTable.KEY_DATE))));
			
			if(!mMonth.equals(month.get("month")))
			{
				month.put("count", mMonthCount+"");
				Log.i("cc", mMonthCount+"");
				mDates.add(month);
				
				Log.i("myCount", mMonthCount+"");
				
				month = new HashMap<String, String>();
				month.put("month", mMonth);
				
				mMonthCount = 1;
			}
			else
			{
				mMonthCount++;
			}
			
		}
		
		month.put("count", ""+mMonthCount);
		mDates.add(month);
		
		return mDates;
	}
	
	@SuppressWarnings("unchecked")
	public ArrayList<ListItem> modifyDates(ArrayList<ListItem> events) throws ParseException
	{
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd");
		ListItem listItem;
		
		for(int i = 0; i < events.size(); i++)
		{
			listItem = events.get(i);
			HashMap<String, String> mTemp = listItem.getEvent();
			
			if(listItem.getType() == ListItem.ITEM && (!mTemp.containsKey(EventsTable.KEY_DATE_GENERIC))) {
				
				mTemp.put(EventsTable.KEY_DATE_GENERIC, mTemp.get(EventsTable.KEY_DATE));
				Log.i("mDate", mTemp.toString());
				mTemp.put(EventsTable.KEY_DATE, (dateFormat.format(Date.valueOf(((String) mTemp.get(EventsTable.KEY_DATE_GENERIC))))));
				events.set(i, listItem);
			}
		}
		
		return events;
		
	}
	
	@SuppressLint("SimpleDateFormat")
	public void addValues() throws ParseException
	{
		Event event;
		
		String myDates[] = new String[] {"2014-04-12", "2014-04-04", "2014-07-22", "2014-06-11", "2014-02-02",
			"2013-12-12", "2013-03-18", "2014-04-29", "2014-10-15", "2014-07-01"};
		
		for(int i = 0; i < 10; i++)
		{
			event = new Event("Event " + i, Date.valueOf(myDates[i]), i);
			eventsTable.addEvent(event);
		}		
		
	}
	
	 private int getActionBarHeight() {
	        int actionBarHeight = 0;
	        TypedValue tv = new TypedValue();
	        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
	            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data,getResources().getDisplayMetrics());
	        }
	        return actionBarHeight;
	}
	 
	public void setActionBarTranslation(float y) {
	        // Figure out the actionbar height
	        int actionBarHeight = getActionBarHeight();
	        // A hack to add the translation to the action bar
	        ViewGroup content = ((ViewGroup) findViewById(android.R.id.content).getParent());
	        int children = content.getChildCount();
	        for (int i = 0; i < children; i++) {
	            View child = content.getChildAt(i);
	            if (child.getId() != android.R.id.content) {
	                if (y <= -actionBarHeight) {
	                    child.setVisibility(View.GONE);
	                } else {
	                    child.setVisibility(View.VISIBLE);
	                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
	                        child.setTranslationY(y);
	                    } else {
	                        AnimatorProxy.wrap(child).setTranslationY(y);
	                    }
	                }
	            }
	        }
	}
	
	 @Override
	 public void onBackPressed() {
	        if (mLayout != null && mLayout.isPanelExpanded() || mLayout.isPanelAnchored()) {
	            mLayout.collapsePanel();
	        } else {
	            super.onBackPressed();
	        }
	    }
}