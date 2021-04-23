package com.puerlink.common;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import java.lang.reflect.Field;

public class DisplayUtils {

	private static int S_SCREEN_WIDTH_PX = 0;
	private static int S_SCREEN_HEIGHT_PX = 0;
	
	public static int getScreenWidthPx(Context context)
	{
		if (S_SCREEN_WIDTH_PX == 0)
		{
			DisplayMetrics dm = new DisplayMetrics();
			WindowManager wm = (WindowManager)context.getApplicationContext().
					getSystemService(Context.WINDOW_SERVICE);
			wm.getDefaultDisplay().getMetrics(dm);
			S_SCREEN_WIDTH_PX = dm.widthPixels;
		}
		return S_SCREEN_WIDTH_PX;
	}
	
	public static int getScreenHeightPx(Context context)
	{
		if (S_SCREEN_HEIGHT_PX == 0)
		{
			DisplayMetrics dm = new DisplayMetrics();
			WindowManager wm = (WindowManager)context.getApplicationContext().
					getSystemService(Context.WINDOW_SERVICE);
			wm.getDefaultDisplay().getMetrics(dm);
			S_SCREEN_HEIGHT_PX = dm.heightPixels;
		}
		return S_SCREEN_HEIGHT_PX;
	}
	
	public static int dp2px(Context context, float dip)
	{
		Resources resources = context.getApplicationContext().getResources();
		float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, resources.getDisplayMetrics());
		return Math.round(px);
	}
	
	public static int px2dp(Context context, float px)
	{
		Resources resources = context.getApplicationContext().getResources();
		final float scale = resources.getDisplayMetrics().density;
		return Math.round(px / scale + 0.5f);
	}
	
	public static int getStatusbarHeightPx(Context context)
	{
	    Class<?> c = null;
	    
	    Object obj = null;
	    
	    Field field = null;
	    
	    int x = 0, statusBarHeight = 0;
	    try
	    {
	        c = Class.forName("com.android.internal.R$dimen");
	        obj = c.newInstance();
	        field = c.getField("status_bar_height");
	        x = Integer.parseInt(field.get(obj).toString());
	        statusBarHeight = context.getResources().getDimensionPixelSize(x);
	    }
	    catch (Exception ex) 
	    {
	    }
	    return statusBarHeight;
	}

	public static boolean inputboardIsShowned(Activity activity)
	{
		if (activity != null && !activity.isFinishing()) {
			int screenHeight = activity.getWindow().getDecorView().getHeight();
			Rect rect = new Rect();
			activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
			return screenHeight - rect.bottom != 0;
		}
		return false;
	}
	
	public static boolean closeInputboard(Activity activity)
	{
		Context context = activity.getApplicationContext();
        InputMethodManager imm = (InputMethodManager)  
        		context.getSystemService(Context.INPUT_METHOD_SERVICE);  
        if (imm != null && imm.isActive()) 
        {
        	View focusView = activity.getCurrentFocus();
        	if (focusView != null)
        	{
        		imm.hideSoftInputFromWindow(focusView.getWindowToken(), 0);
        		return true;
        	}
        }  
        return false;
	}

	public static boolean openInputboard(Activity activity)
	{
		Context context = activity.getApplicationContext();
		InputMethodManager imm = (InputMethodManager)
				context.getSystemService(Context.INPUT_METHOD_SERVICE);
		if (imm != null && imm.isActive())
		{
			View focusView = activity.getCurrentFocus();
			if (focusView != null)
			{
				imm.showSoftInput(focusView, 0);
				return true;
			}
		}
		return false;
	}
	
}
