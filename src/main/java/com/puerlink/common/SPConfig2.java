package com.puerlink.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class SPConfig2 {

	private static SharedPreferences mSettings = null;
	
	private static SharedPreferences getConfig(Context context)
	{
		if (mSettings == null)
		{
			String cfgFileName = context.getApplicationContext().getPackageName();
			mSettings = context.getApplicationContext().getSharedPreferences(cfgFileName, Context.MODE_PRIVATE);
		}
		return mSettings;
	}

	public static void setProperty(Context context, String name, boolean value)
	{
		SharedPreferences sp = getConfig(context);
		sp.edit().putBoolean(name, value).commit();
	}
	
	public static void setProperty(Context context, String name, int value)
	{
		SharedPreferences sp = getConfig(context);
		sp.edit().putInt(name, value).commit();
	}
	
	public static void setProperty(Context context, String name, long value)
	{
		SharedPreferences sp = getConfig(context);
		sp.edit().putLong(name, value).commit();
	}
	
	public static void setProperty(Context context, String name, String value)
	{
		SharedPreferences sp = getConfig(context);
		sp.edit().putString(name, value).commit();
	}
	
	public static boolean getPropertyAsBoolean(Context context, String name, boolean defValue)
	{
		SharedPreferences sp = getConfig(context);
		return sp.getBoolean(name, defValue);
	}

	public static int getPropertyAsInt(Context context, String name, int defValue)
	{
		SharedPreferences sp = getConfig(context);
		return sp.getInt(name, defValue);
	}

	public static long getPropertyAsLong(Context context, String name, long defValue)
	{
		SharedPreferences sp = getConfig(context);
		return sp.getLong(name, defValue);
	}
	
	public static String getPropertyAsString(Context context, String name, String defValue)
	{
		SharedPreferences sp = getConfig(context);
		return sp.getString(name, defValue);
	}
	
}
