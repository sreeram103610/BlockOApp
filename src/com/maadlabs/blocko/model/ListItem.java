package com.maadlabs.blocko.model;

import java.util.HashMap;

public class ListItem {
	
	public int listPosition;
	public int sectionPosition;
	public static final int SECTION = 0;
	public static final int ITEM = 1;
	
	HashMap<String, String> mData;
	
	int type;
	
	public ListItem(int type, HashMap<String, String> event)
	{
		this.type = type;
		this.mData = event;
	}

	public HashMap<String, String> getEvent() {
		return mData;
	}

	public void setEvent(HashMap<String, String> event) {
		this.mData = event;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
}
