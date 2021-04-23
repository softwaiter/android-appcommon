package com.puerlink.common;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import java.io.File;

public class DeviceUtils {

	public static String getDeviceId(Context context)
	{
		try
		{
			TelephonyManager tm = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
			String devId = tm.getDeviceId();
			devId = devId.replaceAll("0", "");
			return TextUtils.isEmpty(devId) ? null : devId;
		}
		catch (Exception exp)
		{
			return null;
		}
	}

	public static String getLine1Number(Context context)
	{
		try
		{
			TelephonyManager tm = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
			String lineNum = tm.getLine1Number();
			if (TextUtils.isEmpty(lineNum.replace("0", "")))
			{
				return null;
			}
			return lineNum;
		}
		catch (Exception exp)
		{
			return null;
		}
	}
	
	public static String getSerialNumber(Context context)
	{
		try
		{
			TelephonyManager tm = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
			return tm.getSimSerialNumber();
		}
		catch (Exception exp)
		{
			return null;
		}
	}
	
	public static String getPseudoId(Context context)
	{
		String pseudoId = "35" + //we make this look like a valid IMEI 
				Build.BOARD.length() % 10 + Build.BRAND.length() % 10 + 
				Build.CPU_ABI.length() % 10 + Build.DEVICE.length() % 10 + 
				Build.DISPLAY.length() % 10 + Build.HOST.length() % 10 + Build.ID.length() % 10 + 
				Build.MANUFACTURER.length() % 10 + Build.MODEL.length() % 10 + 
				Build.PRODUCT.length() % 10 + Build.TAGS.length() % 10 + 
				Build.TYPE.length()%10 + Build.USER.length()%10 ; //13 digits
		return pseudoId;
	}
	
	public static String getUniqueId(Context context)
	{
		String id = null;
		try
		{
			id = SPConfig2.getPropertyAsString(context, "unique_id", null);
			if (id == null)
			{
				id = getLine1Number(context);
				if (TextUtils.isEmpty(id))
				{
					id = getDeviceId(context);
				}
				if (TextUtils.isEmpty(id))
				{
					id = getSerialNumber(context);
				}
				if (TextUtils.isEmpty(id))
				{
					id = getPseudoId(context);
				}
				SPConfig2.setProperty(context, "unique_id", id);
			}
		}
		catch (Exception exp)
		{
			;
		}
		return id;
	}
	
	public static boolean isHasSdcard()
	{
		String status = Environment.getExternalStorageState();
		if (status.equals(Environment.MEDIA_MOUNTED)) {
		   return true;
		} else {
		   return false;
		}		
	}
	
	public static String getWritePath(String subDir)
	{
		String result;
		if (isHasSdcard())
		{
			result = Environment.getExternalStorageDirectory().getAbsolutePath();
		}
		else
		{
			result = Environment.getDataDirectory().getAbsolutePath();
		}

		if (!result.endsWith(File.separator))
		{
			result += File.separator;
		}
		
		if (!TextUtils.isEmpty(subDir))
		{
			if (subDir.startsWith(File.separator))
			{
				result += subDir.substring(1);
			}
			else
			{
				result += subDir;
			}

			if (!result.endsWith(File.separator))
			{
				result += File.separator;
			}
		}
		
        File destDir = new File(result);	//文件目录
        if (!destDir.exists())	//判断目录是否存在，不存在创建
        {
        	destDir.mkdirs();	//创建目录
        }
		
		return result;
	}

}
