package com.maadlabs.blocko.view;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.hb.views.PinnedSectionListView.PinnedSectionListAdapter;
import com.maadlabs.blocko.R;
import com.maadlabs.blocko.model.ListItem;
import com.maadlabs.util.CustomFonts;

public class MySimpleSectionsAdapter extends ArrayAdapter<ListItem> implements PinnedSectionListAdapter{

	ArrayList<HashMap<String, String>> sections;
	ArrayList<ListItem> data;
	String fromResource[];
	int[] toListItemResource, toSectionResource;
	int listItemResource, sectionItemResource, myPosition;
	View row;
	Context context;
	ArrayList<View> myViewsHolder = null;
	CustomFonts customFonts;
	
	private static final int[] COLORS = new int[] {
        R.color.green_light,
        R.color.blue_light};

	
	public MySimpleSectionsAdapter(Context context, int resource, int resource2, String[] from,
			int[] to, int[] toSection, ArrayList<ListItem> data) {
		
		super(context, resource2, data);
		
		this.data = data;
		this.fromResource = from;
		this.toListItemResource = to;
		this.toSectionResource = toSection;
		this.context = context;
		this.listItemResource = resource;
		this.sectionItemResource = resource2;
		
		initValues();
	}
	
	public void initValues() {
		
		customFonts = new CustomFonts();
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		ListItem listItem = getItem(position);
		
		if(convertView == null)
		{
			LayoutInflater inflater = (LayoutInflater) context
			        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			if(listItem.getType() == ListItem.ITEM)
				row = inflater.inflate(listItemResource, parent, false);  // like a template to add values
			else
				row = inflater.inflate(sectionItemResource, parent, false);
			
			holderInitIf(position);
		}

		else
		{
			row = convertView;
			holderInitElse();
		}
		
		myPosition = position;
		setValues(listItem, parent);
	
		return row;
	}
	
	public void holderInitIf(int position) {
		
		ListItem item = getItem(position);
		int length;
		int[] mResources;
		
		if(item.getType() == ListItem.ITEM)
		{
			length = toListItemResource.length;
			mResources = toListItemResource;
		}
		else
		{
			length = toSectionResource.length;
			mResources = toSectionResource;
		}

		myViewsHolder = new ArrayList<View>();
		
		for(int i = 0; i < length; i++)
		{
			myViewsHolder.add(row.findViewById(mResources[i]));
		}
			
			row.setTag(myViewsHolder);
		}
	
	
	public void setValues(ListItem currItem, ViewGroup parent) {

		HashMap<String, String> mValues;
		
		if(currItem.getType() == ListItem.ITEM)
		{
			mValues = currItem.getEvent();
			
			for(int i = 0; i < myViewsHolder.size(); i++)
			{
				View mView = myViewsHolder.get(i);
				
				if(mView instanceof TextView) {

					TextView mTextView = (TextView) mView;
					Log.i("value", mValues.get(fromResource[i]).toString());
					
					mTextView.setText(mValues.get(fromResource[i]).toString());
				}
				
			}
		}
		else
		{
			for(int i = 0; i < myViewsHolder.size(); i++)
			{
				View mView = myViewsHolder.get(i);
				
				if(mView instanceof TextView) {
					
					TextView mTextView = (TextView) mView;
					mTextView.setText(currItem.getEvent().get("month").toString());
					mTextView.setBackgroundColor(parent.getResources().getColor(COLORS[(int) Math.rint((Math.random()*(COLORS.length - 1)))]));
				}
				
			}
		}
		
		customFonts.init(getContext(), row);
		
	}

	@SuppressWarnings("unchecked")
	public void holderInitElse() {
		
		myViewsHolder = (ArrayList<View>) row.getTag();
	}
	
	
	 @Override 
	 public int getViewTypeCount() {
         return 2;
     }

     @Override 
     public int getItemViewType(int position) {
    	 ListItem item = (ListItem) getItem(position);
         return item.getType();
     }

     @Override
     public boolean isItemViewTypePinned(int viewType) {
         return viewType == ListItem.SECTION;
     }
     
	public class Holder {
		
		int[] views;
		
		public Holder() {
			
			views = new int[toListItemResource.length];
		}
	}

}
