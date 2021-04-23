package com.puerlink.common;

import android.text.TextUtils;
import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.StringTokenizer;

public class StringUtils {

	public static String md5(String string) {
		byte[] hash;
		try {
			hash = MessageDigest.getInstance("MD5").digest(
					string.getBytes("UTF-8"));
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("Huh, MD5 should be supported?", e);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("Huh, UTF-8 should be supported?", e);
		}

		StringBuilder hex = new StringBuilder(hash.length * 2);
		for (byte b : hash) 
		{
			if ((b & 0xFF) < 0x10)
				hex.append("0");
			hex.append(Integer.toHexString(b & 0xFF));
		}

		return hex.toString();
	}
	
	public static String encrypt(String str)
	{
		if (!TextUtils.isEmpty(str))
		{
			try
			{
				byte[] buf = str.getBytes("ISO-8859-1");
				
				String text = "";
				for (int i = 0; i < buf.length; i++)
				{
					int c = buf[i];
					text += (c + 27) + "%";
				}
				
				return Base64.encodeToString(text.getBytes(), Base64.DEFAULT);
			}
			catch (Exception exp)
			{
			}
		}
		
		return null;
	}
	
	public static String decrypt(String str)
	{
		if (!TextUtils.isEmpty(str))
		{
			try
			{
				byte[] buf = Base64.decode(str, Base64.DEFAULT);
				String str2 = new String(buf);
				
				String text = "";
				StringTokenizer tokens = new StringTokenizer(str2, "%");
				while (tokens.hasMoreElements())
				{
					int chr =  Integer.parseInt((String)tokens.nextElement()) - 27;
					text += (char)chr;
				}
				
				return text;
			}
			catch (Exception exp)
			{
			}
		}
		
		return null;
	}
	
}
