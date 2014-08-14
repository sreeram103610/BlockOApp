package com.maadlabs.util;

import android.R;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Handler;
import android.text.Editable.Factory;
import android.util.AttributeSet;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class CustomFonts {
	
	private ViewGroup viewGroup;
	private static Typeface customTypeface;
	public static String PATH_TYPEFACE_CUSTOM = "fonts/CabinSketch-Regular.otf";
	 
	public Typeface getCustomTypeface(Context context) {
	    if(customTypeface == null){
	        //Only do this once for each typeface used 
	        //or we will leak unnecessary memory.
	        customTypeface = Typeface.createFromAsset(context.getAssets(), PATH_TYPEFACE_CUSTOM);
	    }
	    return customTypeface;
	}
	
	public static void setTypeFace(Typeface typeFace, ViewGroup parent){
	    for (int i = 0; i < parent.getChildCount(); i++) {
	        View v = parent.getChildAt(i);
	        if (v instanceof ViewGroup) {
	            setTypeFace(typeFace, (ViewGroup) v);
	        } else if (v instanceof TextView) {
	            TextView tv = (TextView) v;
	            tv.setTypeface(typeFace);
	            tv.setTextColor(Color.BLACK);
	            //For making the font anti-aliased.
	            tv.setPaintFlags(tv.getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG);
	        }
	        else if (v instanceof Button) {
	        	Button b = (Button) v;
	        	b.setTypeface(typeFace);
	        	b.setTextColor(Color.BLACK);
	        	b.setPaintFlags(b.getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG);
	        }
	    }
	}
	
	public void init(Context context, View view)
	{
		viewGroup = (ViewGroup) view;
		setTypeFace(getCustomTypeface(context), viewGroup);
	}

}
