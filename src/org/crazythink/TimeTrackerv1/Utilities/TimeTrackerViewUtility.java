package org.crazythink.TimeTrackerv1.Utilities;

import org.crazythink.TimeTrackerv1.R;

import android.view.View;

public class TimeTrackerViewUtility {
	static public void setupStartingTaskView(View view) {
		view.setBackgroundResource(R.color.lightGreen);
		
		View topBorder = view.findViewById(R.id.taskTopBorder);
		View bottomBorder = view.findViewById(R.id.taskBottomBorder);
		topBorder.setBackgroundResource(R.color.lightGreenLightBorder);
		bottomBorder.setBackgroundResource(R.color.lightGreenDarkBorder);
	}
	
	static public void setupTaskView(View view) {
		view.setBackgroundResource(R.color.lightBlue);
		
		View topBorder = view.findViewById(R.id.taskTopBorder);
		View bottomBorder = view.findViewById(R.id.taskBottomBorder);
		topBorder.setBackgroundResource(R.color.lightBlueBorder);
		bottomBorder.setBackgroundResource(R.color.darkBlueBorder);
	}
	
	static public String formatTime(int totalMinutes) {
		int hours = totalMinutes / 60;
		int minutes = totalMinutes % 60;
		String formattedTime;
		if (hours > 0)
			formattedTime = hours + "h " + minutes + "m";
		else if (minutes > 0)
			formattedTime = minutes + "m";
		else
			formattedTime = ">1m";
		return formattedTime;
	}

}
