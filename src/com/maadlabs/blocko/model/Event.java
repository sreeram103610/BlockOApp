package com.maadlabs.blocko.model;

import java.sql.Date;

public class Event {

	String mName;
	Date mDate;
	Integer mPoints;
	String mId;
	Integer mCount;
	
	public Event(String name, Date date, Integer points)
	{
		mName = name;
		mDate = date;
		mPoints = points; 
	}
	
	public String getId() {
		return mId;
	}

	public void setId(String mId) {
		this.mId = mId;
	}

	public Integer getPoints() {
		return mPoints;
	}
	public void setPoints(Integer points) {
		this.mPoints = points;
	}
	public Date getDate() {
		return mDate;
	}
	public void setDate(Date mDate) {
		this.mDate = mDate;
	}
	public String getmName() {
		return mName;
	}
	public void setmName(String mName) {
		this.mName = mName;
	}

	public void setCount(int size) {

		this.mCount = size;
	}
	
}
