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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.util.Log;

public class ProjectEdit extends Activity {

    private EditText mNameText;
    private Long mRowId;
    private DbAdapter mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Log.d("DEBUG","onCreate");
        
        mDbHelper = new DbAdapter(this);
        mDbHelper.open();
        
        setContentView(R.layout.project_edit);
        setTitle(R.string.edit_project);

        mNameText = (EditText) findViewById(R.id.projectname);

        Button confirmButton = (Button) findViewById(R.id.confirm);

        mRowId = (savedInstanceState == null) ? null :
        	(Long) savedInstanceState.getSerializable(DbAdapter.KEY_ROWID);
        if (mRowId == null) {
        	Bundle extras = getIntent().getExtras();
            mRowId = extras != null ? extras.getLong(DbAdapter.KEY_ROWID)
            						: null;
        }
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
    	Log.d("DEBUG","populateFields");
    	if (mRowId != null) {
    		Cursor task = mDbHelper.fetchProject(mRowId);
    		mNameText.setText(task.getString(
    				task.getColumnIndexOrThrow(DbAdapter.KEY_NAME)));
    		task.close();
    	}
    }
    
    private void saveState() {
    	String name = mNameText.getText().toString();
    	
    	if (mRowId == null) {
    		long id = mDbHelper.createProject(name);
    		if (id > 0) {
    			mRowId = id;
    		}
    	}
    	else {
    		mDbHelper.updateProject(mRowId, name);
    	}
    }
}
