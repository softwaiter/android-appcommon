package com.puerlink.common;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Stack;

import org.apache.http.util.EncodingUtils;
import org.xmlpull.v1.XmlPullParser;

import android.text.TextUtils;
import android.util.Xml;

public class XmlUtils {
	
	private static String joinString(Stack<String> input)
	{
		StringBuilder sbResult = new StringBuilder();
		for (int i = 0; i < input.size(); i++)
		{
			if (sbResult.length() > 0)
			{
				sbResult.append(".");
			}
			sbResult.append(input.elementAt(i));
		}
		return sbResult.toString();
	}

	private static String getFileContent(File file)
	{
		if (file.exists())
		{
			try {
				FileInputStream fis = new FileInputStream(file);
				try
				{
					int length = fis.available();
					byte[] buffer = new byte[length];
					fis.read(buffer);
					return EncodingUtils.getString(buffer, "UTF-8");
				} catch (Exception exp2)
				{
				} finally {
					fis.close();
				}
			}
			catch (Exception exp)
			{
			}
		}
		return "";
	}

	public static Hashtable<String, String> readXml(File file, String...paths)
	{
		String fileContent = getFileContent(file);
		if (!TextUtils.isEmpty(fileContent))
		{
			return readXml(fileContent, paths);
		}
		return new Hashtable<String, String>();
	}

	public static Hashtable<String, String> readXml(String xmlStr, String...paths)
	{
		Hashtable<String, String> result = new Hashtable<String, String>();
		
		ByteArrayInputStream bis = new ByteArrayInputStream(xmlStr.getBytes());				
		XmlPullParser xpp = Xml.newPullParser();
		try
		{
			Arrays.sort(paths);
			
			Stack<String> currPath = new Stack<String>();
			
			xpp.setInput(bis, "UTF-8");
			int eventType = xpp.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT)
			{
				switch (eventType)
				{
					case XmlPullParser.START_TAG:
						currPath.push(xpp.getName());
						
						String pathStr = joinString(currPath);
						int index = Arrays.binarySearch(paths, pathStr);
						if (index >= 0)
						{
							result.put(pathStr, xpp.nextText());
							if (xpp.getEventType() == XmlPullParser.END_TAG)
							{
								currPath.pop();
							}
						}
						
						break;
					case XmlPullParser.END_TAG:
						currPath.pop();
						break;
				}
				eventType = xpp.next();
			}
		}
		catch (Exception exp)
		{
			;
		}
		
		return result;
	}
	
}
