package com.puerlink.common;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.io.File;

public class PackageUtils {

	public static String getInstallPath(Context context)
	{
		return context.getApplicationContext().getPackageResourcePath();
	}
	
	public static String getInstallSubPath(Context context, String subPath, boolean create)
	{
		String path = getInstallPath(context) + subPath;
		if (create)
		{
			File file = new File(path);
			if (!file.exists())
			{
				file.mkdir();
			}
		}
		return path;
	}
	
	public static int getVersionCode(Context context)
	{
		try {
			PackageManager pm = context.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
			return pi.versionCode;
		} catch (Exception e) {
			;
		}
		return 20101010;
	}
	
	public static String getVersionName(Context context)
	{
		try {
			PackageManager pm = context.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
			return pi.versionName;		
		}
		catch (Exception exp)
		{
			;
		}
		return "1.0.0";
	}
	
	public static String getMetaData(Context context, String name)
	{
		try {
			ApplicationInfo ai = context.getPackageManager().
					getApplicationInfo(context.getPackageName(), 
					PackageManager.GET_META_DATA);
			return ai.metaData.getString(name);
		} catch (Exception e) {
			;
		}
		return "";
	}

	public static boolean isPackageInstalled(Context context, String pkgName) {
		PackageInfo pkgInfo = null;
		try
		{
			pkgInfo = context.getPackageManager().getPackageInfo(pkgName, 0);
		}
		catch (Exception exp)
		{
		}

		return pkgInfo != null;
	}

	public static void launchByPackageName(Context context, String pkgName) {
		final PackageManager pm = context.getPackageManager();
		Intent intent = pm.getLaunchIntentForPackage(pkgName);
		if (intent != null) {
			context.startActivity(intent);
		}
	}
	
}
