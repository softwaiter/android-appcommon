package com.puerlink.common;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.util.List;

public class AppUtils {
	
	public static boolean isBackground(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningTaskInfo> tasks = am.getRunningTasks(1);
        if (!tasks.isEmpty()) 
        {
            ComponentName topActivity = tasks.get(0).topActivity;
            if (!topActivity.getPackageName().equals(context.getPackageName())) 
            {
                return true;
            }
        }
        return false;
    }
	
	public static boolean closeInputboard(Activity activity)
	{
		if (activity != null)
		{
	        InputMethodManager imm = (InputMethodManager)  
	        		activity.getSystemService(Context.INPUT_METHOD_SERVICE);  
	        if (imm != null) {
	        	View focusView = activity.getCurrentFocus();
	        	if (focusView != null)
	        	{
	        		imm.hideSoftInputFromWindow(focusView.getWindowToken(), 0);
	        		return true;
	        	}
	        }
		}
        return false;
	}
	public static boolean isProcessRunning(Context context, String processName)
	{
		boolean result = false;

		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningAppProcessInfo> lists = am.getRunningAppProcesses();
		for (ActivityManager.RunningAppProcessInfo info : lists) {
			if (info.processName.equals(processName)) {
				result = true;
			}
		}

		return result;
	}

	public static boolean isServiceRunning(Context context, String serviceName)
	{
		boolean result = false;

		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningServiceInfo> lists = am.getRunningServices(Integer.MAX_VALUE);
		for (ActivityManager.RunningServiceInfo info : lists)
		{
			if (info.service.getClassName().toString().equals(serviceName)) {
				Log.e("wxm", "find service: " + info.service.getClassName().toString());
				return true;
			}
		}

		return result;
	}

}
