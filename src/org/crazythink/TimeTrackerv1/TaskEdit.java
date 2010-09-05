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

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class TaskEdit extends Activity {

    private EditText mNameText;
    private EditText mDescText;
    private Long mRowId;
    private Long mProjectId;
    private DbAdapter mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        mDbHelper = new DbAdapter(this);
        mDbHelper.open();
        
        setContentView(R.layout.task_edit);
        setTitle(R.string.edit_task);

        mNameText = (EditText) findViewById(R.id.taskname);
        mDescText = (EditText) findViewById(R.id.description);

        Button confirmButton = (Button) findViewById(R.id.confirm);

        mRowId = (savedInstanceState == null) ? null :
        	(Long) savedInstanceState.getSerializable(DbAdapter.KEY_ROWID);
        
        Log.d("DEBUG:","onCreate 1 mRowId " + mRowId);
        
        if (mRowId == null && mProjectId == null) {
        	Bundle extras = getIntent().getExtras();
            mRowId = extras.containsKey(DbAdapter.KEY_ROWID) ? extras.getLong(DbAdapter.KEY_ROWID)
            						: null;
            mProjectId = extras.containsKey(DbAdapter.KEY_PROJECT_ID) ? extras.getLong(DbAdapter.KEY_PROJECT_ID)
					: null;
        }
        
        Log.d("DEBUG:","onCreate 2 mRowId " + mRowId);
        Log.d("DEBUG:","onCreate 2 mProjectId " + mProjectId);
        
        populateFields();
        
        confirmButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                setResult(RESULT_OK);
                finish();
            }

        });
    }
    
	@Override
	public void onDestroy() {
		super.onDestroy();
		
		mDbHelper.close();
	}
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
    	super.onSaveInstanceState(outState);
    	saveState();
    	outState.putSerializable(DbAdapter.KEY_ROWID, mRowId);
    }
    
    @Override
    protected void onPause() {
    	super.onPause();
    	saveState();
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
    	populateFields();
    }
    
    private void populateFields() {
    	if (mRowId != null) {
    		Log.d("DEBUG:","populateFields mRowId " + mRowId);
    		Cursor task = mDbHelper.fetchTask(mRowId);
    		mProjectId = task.getLong(
    				task.getColumnIndexOrThrow(DbAdapter.KEY_PROJECT_ID));
    		mNameText.setText(task.getString(
    				task.getColumnIndexOrThrow(DbAdapter.KEY_NAME)));
    		mDescText.setText(task.getString(
    				task.getColumnIndexOrThrow(DbAdapter.KEY_DESCRIPTION)));
    		task.close();
    	}
    }
    
    private void saveState() {
    	String name = mNameText.getText().toString();
    	String desc = mDescText.getText().toString();
    	
    	if (mRowId == null) {
    		Log.v("DEBUG:","task create save with name desc projectid "+ name + desc + mProjectId);
    		long id = mDbHelper.createTask(name, desc, mProjectId);
    		if (id > 0) {
    			mRowId = id;
    		}
    	}
    	else {
    		Log.v("DEBUG:","task update save with rowid name desc projectid "+ mRowId + name + desc + mProjectId);
    		mDbHelper.updateTask(mRowId, name, desc, mProjectId);
    	}
    }
}
