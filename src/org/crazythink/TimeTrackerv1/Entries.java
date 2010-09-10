/*
 * Copyright (C) 2008 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.crazythink.TimeTrackerv1;

import org.crazythink.TimeTrackerv1.DbAdapter.ENTRIES_FILTER;

import android.app.ListActivity;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;

public class Entries extends ListActivity {

	private Long mTaskId;
	private DbAdapter mDbHelper;
	private SimpleCursorAdapter mListAdapter;
	int mFilterSelected;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mDbHelper = new DbAdapter(this);
		mDbHelper.open();
		setContentView(R.layout.main_entries);

		mTaskId = (savedInstanceState == null) ? null
				: (Long) savedInstanceState
						.getSerializable(DbAdapter.KEY_TASK_ID);
		if (mTaskId == null) {
			Bundle extras = getIntent().getExtras();
			mTaskId = extras != null ? extras.getLong(DbAdapter.KEY_TASK_ID)
					: null;
		}
		if (mTaskId != null) {
			Cursor task = mDbHelper.fetchTask(mTaskId);
			setTitle(task.getString(task.getColumnIndex(DbAdapter.KEY_NAME))
					+ " " + getString(R.string.entries));
			task.close();
		} else
			setTitle(R.string.all_task_entries);

		setupSpinner();

		setupList();

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		
		SharedPreferences preferences = getPreferences(MODE_PRIVATE);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putInt("FilterSelected", mFilterSelected);
		editor.commit();

		mDbHelper.close();
	}

	private void setupSpinner() {
		SharedPreferences preferences = getPreferences(MODE_PRIVATE);
		mFilterSelected = preferences.getInt("FilterSelected",ENTRIES_FILTER.ALL.ordinal());
		
		Spinner filterSpinner = (Spinner) findViewById(R.id.entriesFilterSpinner);
		ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, ENTRIES_FILTER.getList());
		spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		filterSpinner.setAdapter(spinnerAdapter);
		filterSpinner.setSelection(mFilterSelected);

		filterSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parentView,
					View selectedItemView, int position, long id) {
				// your code here
				mFilterSelected = position;
				Log.v("filterSpinner.onItemSelected", "start date of "
						+ ENTRIES_FILTER.getStartDate(mFilterSelected));
				Log.v("filterSpinner.onItemSelected", "stop date of "
						+ ENTRIES_FILTER.getStopDate(mFilterSelected));
				Cursor c = getEntriesCursor();
				mListAdapter.changeCursor(c);
				mListAdapter.notifyDataSetChanged();
			}

			public void onNothingSelected(AdapterView<?> parentView) {
				// your code here
			}

		});
	}

	private Cursor getEntriesCursor() {
		Cursor c;
		if (mTaskId != null) {
			c = mDbHelper.fetchEntriesByTask(mTaskId, mFilterSelected);
		} else {
			c = mDbHelper.fetchAllEntries();
		}
		startManagingCursor(c);
		return c;
	}

	private void setupList() {
		Cursor c;
		Log.d("DEBUG", "entries fillData on task " + mTaskId);
		c = this.getEntriesCursor();
		
		String[] from = new String[] { DbAdapter.KEY_START_TIME_HHMM,
				DbAdapter.KEY_DESCRIPTION, DbAdapter.KEY_MINUTES,
				DbAdapter.KEY_START_DATE_MMDD };
		int[] to = new int[] { R.id.timeTextView, R.id.descriptionTextView,
				R.id.minutesTextView, R.id.dateHeaderTextView };
		mListAdapter = new SimpleCursorAdapter(this, R.layout.entry, c, from,
				to);

		final int nStartDateIndex = c
				.getColumnIndex(DbAdapter.KEY_START_DATE_MMDD);
		final int nDurationIndex = c.getColumnIndex(DbAdapter.KEY_MINUTES);

		mListAdapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
			public boolean setViewValue(View view, Cursor cursor,
					int columnIndex) {
				if (columnIndex == nStartDateIndex) {
					// prepare for displaying project header if needed
					Log.v("Entries nStartDateIndex:",
							"header column: cursor is at position "
									+ cursor.getPosition() + " of "
									+ cursor.getCount());
					if (isHeaderVisible(cursor)) {
						view.setVisibility(View.VISIBLE);
						String headerName = cursor.getString(nStartDateIndex);
						((TextView) view).setText(headerName);
					} else {
						((TextView) view).setText("");
						view.setVisibility(View.GONE);
					}
					return true;
				} else if (columnIndex == nDurationIndex) {
					int totalTime = cursor.getInt(nDurationIndex);

					Log.v("Entries nDurationIndex:", "totalTime " + totalTime
							+ " view " + view);

					int hours = totalTime / 60;
					int minutes = totalTime % 60;
					String formattedTime;
					if (hours > 0)
						formattedTime = hours + "h " + minutes + "m";
					else if (minutes > 0)
						formattedTime = minutes + "m";
					else
						formattedTime = ">1m";
					((TextView) view).setText(formattedTime);
					return true;
				}
				return false;
			}
		});

		setListAdapter(mListAdapter);
	}

	protected boolean isHeaderVisible(Cursor cursor) {
		int startDateColumn = cursor
				.getColumnIndex(DbAdapter.KEY_START_DATE_MMDD);
		if (cursor.moveToPrevious()) {
			String prevHeaderId = cursor.getString(startDateColumn);
			cursor.moveToNext();
			String currHeaderId = cursor.getString(startDateColumn);
			if (currHeaderId.equals(prevHeaderId))
				return false;
		} else
			cursor.moveToFirst();
		return true;
	}

}
