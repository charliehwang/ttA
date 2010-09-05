package org.crazythink.TimeTrackerv1;

import android.database.Cursor;
import android.widget.Adapter;

public class Section {
	Cursor cursor;
	Adapter adapter;
	
	Section(Cursor cursor, Adapter adapter) {
		this.cursor=cursor;
		this.adapter=adapter;
	}
}