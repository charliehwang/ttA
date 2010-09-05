package org.crazythink.TimeTrackerv1;

import android.database.Cursor;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class MergeProjectsTasksAdapter extends BaseAdapter {
	private Section projects;
	private Section tasks;

	public MergeProjectsTasksAdapter(Section projects, Section tasks) {
		super();
		this.projects = projects;
		this.tasks = tasks;
	}

	@Override
	public Object getItem(int position) {
		int projectIndex=0;
		int taskIndex=0;

		Log.v("DEBUG:", "getView initial call position " + position);
		
		/* Iterate through projects - seeing whether the requested position
		 * is within its scope.
		 * 
		 */
		if (projects.cursor.moveToFirst()) {
			do {
				if(position==0) {
					return(projects.adapter.getItem(projectIndex));
				}
				
				int size = projects.cursor.getInt(projects.cursor.getColumnIndex("child_count")) + 1;
				if (position < size) {
					int tasksPosition = taskIndex + position - 1;
					return(tasks.adapter.getItem(tasksPosition));
				}
				position-=size;
				projectIndex++;
				taskIndex+=size-1;
			} while (projects.cursor.moveToNext());
		}
		return(null);
	}

	@Override
	public int getCount() {
		int count = tasks.cursor.getCount() + projects.cursor.getCount();
		Log.v("DEBUG:", "getCount return " + count);
		return count;
	}

	@Override
	public int getViewTypeCount() {
		int total = tasks.adapter.getViewTypeCount() + projects.adapter.getViewTypeCount();
		
		Log.v("DEBUG:", "getViewTypeCount return " + total);
		
		return(total);
	}

	@Override
	public int getItemViewType(int position) {
		
		if (projects.cursor.moveToFirst()) {
			do {
				if(position==0) {
					Log.v("DEBUG:", "getItemViewType return 0");
					return 0;
				}
				
				int size = projects.cursor.getInt(projects.cursor.getColumnIndex("child_count")) + 1;
				if (position < size) {
					Log.v("DEBUG:", "getItemViewType return 1");
					return 1;
				}
				position-=size;
			} while (projects.cursor.moveToNext());
		}
		
		return(-1);
	}

	public boolean areAllItemsSelectable() {
		return(true);
	}

	@Override
	public View getView(int position, View convertView,
											ViewGroup parent) {
		int projectIndex=0;
		int taskIndex=0;
		int projectsNameColumnIndex= projects.cursor.getColumnIndex(DbAdapter.KEY_NAME);
		int tasksNameColumnIndex= tasks.cursor.getColumnIndex(DbAdapter.KEY_NAME);

		Log.v("DEBUG:", "getView ------------- position " + position + " ----------------------");
		
		if (projects.cursor.moveToFirst()) {
			do {
				if(position==0) {
					Log.v("DEBUG:", "getView project position " + taskIndex + " " + projects.cursor.getString(projectsNameColumnIndex));
					return(projects.adapter.getView(projectIndex, convertView, parent));
				}
				
				int size = projects.cursor.getInt(projects.cursor.getColumnIndex("child_count")) + 1;
				if (position < size) {
					int tasksPosition = taskIndex + position - 1;
					tasks.cursor.moveToPosition(tasksPosition);
					tasks.cursor.moveToPosition(tasksPosition);
					Log.v("DEBUG:", "getView task position " + tasksPosition + " " + tasks.cursor.getString(tasksNameColumnIndex));
					return(tasks.adapter.getView(tasksPosition, convertView, parent));
				}
				position-=size;
				projectIndex++;
				taskIndex+=size-1;
			} while (projects.cursor.moveToNext());
		}
		
		return(null);
	}

	@Override
	public long getItemId(int position) {
		int projectIndex=0;
		int taskIndex=0;
		
		if (projects.cursor.moveToFirst()) {
			do {
				if(position==0) {
					return(projects.cursor.getLong(projects.cursor.getColumnIndex(DbAdapter.KEY_ROWID)));
				}
				
				int size = projects.cursor.getInt(projects.cursor.getColumnIndex("child_count")) + 1;
				if (position < size) {
					int tasksPosition = taskIndex + position - 1;
					tasks.cursor.moveToPosition(tasksPosition);
					return(tasks.cursor.getLong(tasks.cursor.getColumnIndex(DbAdapter.KEY_ROWID)));
				}
				position-=size;
				projectIndex++;
				taskIndex+=size-1;
			} while (projects.cursor.moveToNext());
		}
		
		return(-1);
	}
	
	public Cursor getProjectsCursor() {
		return (projects.cursor);
	}
	
	public Cursor getTasksCursor() {
		return (tasks.cursor);
	}
}