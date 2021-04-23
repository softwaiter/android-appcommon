package com.puerlink.common;

import android.text.TextUtils;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * 日期时间工具类
 * @author wxm
 *
 */
public class DateTimeUtils {

	/**
	 * 判断指定日期字符串是否特定日期格式
	 * @param dateStr	日期字符串
	 * @param format	日期格式
	 * @return
	 */
	public static boolean isSpecialDateTime(String dateStr, String format)
	{
		if (!TextUtils.isEmpty(dateStr))
		{
	         SimpleDateFormat sdf = new SimpleDateFormat(format);
	         sdf.setLenient(false);
	         try {
	        	 sdf.format(sdf.parse(dateStr));
	             return true;
	         } catch (Exception e) {
	        	 ;
	         }
		}
		return false;
	}

	/**
	 * 判断指定日期是否是今天
	 * @param time
	 * @return
	 */
	public static boolean isToday(long time)
	{
		return isToday(new Date(time));
	}

	/**
	 * 返回两个日期之间相差的天数
	 * @param date
	 * @param date2
	 * @return
	 */
	public static long getDiffDay(Date date, Date date2)
	{
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int day = cal.get(Calendar.DAY_OF_YEAR);
		cal.setTime(date2);
		int day2 = cal.get(Calendar.DAY_OF_YEAR);
		return Math.abs(day2 - day);
	}

	/**
	 * 返回指定日期是星期几（星期一：1，星期二：2，...星期日：7）
	 * @param date
	 * @return
	 */
	public static int getDateWeek(Date date)
	{
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int week = cal.get(Calendar.DAY_OF_WEEK) - 1;
		if (week == 0)
		{
			week = 7;
		}
		return week;
	}

	static String[] mFullWeekNames = new String[] {"星期一", "星期二", "星期三", "星期四", "星期五", "星期六", "星期日"};
	static String[] mShortWeekNames = new String[] {"周一", "周二", "周三", "周四", "周五", "周六", "周日"};

	/**
	 * 根据星期几的索引返回中文名称
	 * @param index
	 * @param isFullName
	 * @return
	 */
	public static String getWeekName(int index, boolean isFullName)
	{
		if (index >= 1 && index <= 7)
		{
			if (isFullName)
			{
				return mFullWeekNames[index - 1];
			}
			else
			{
				return mShortWeekNames[index - 1];
			}
		}
		return "";
	}

	/**
	 * 判断指定日期是否是今天
	 * @param date
	 * @return
	 */
	public static boolean isToday(Date date)
	{
		Date now = new Date();

		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String nowStr = sdf.format(now);
		String dateStr = sdf.format(date);

		return nowStr.equals(dateStr);
	}
	
	/**
	 * 将日期转换为指定格式的字符串
	 * @param dateTime	要转换的日期对象
	 * @param format	要转换的格式
	 * @return
	 */
	public static String dateTime2Str(Date dateTime, String format)
	{
		if (dateTime != null)
		{
			if (!TextUtils.isEmpty(format))
			{
				SimpleDateFormat sdf = new SimpleDateFormat(format);
				return sdf.format(dateTime);
			}
			else
			{
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				return sdf.format(dateTime);
			}
		}
		return "";
	}
	
	/**
	 * sql时间戳转换成指定格式的日期字符串
	 * @param timestamp	时间戳
	 * @param format	格式化格式
	 */
	public static String timestamp2Str(Timestamp timestamp, String format)
	{
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(timestamp);
	}
	
	/**
	 * sql时间戳转换成日期对象
	 * @param timestamp	时间戳
	 * @return
	 */
	public static Date timestamp2Date(Timestamp timestamp)
	{
		try
		{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String dateStr = sdf.format(timestamp);
			return sdf.parse(dateStr);
		}
		catch (Exception exp)
		{
			;
		}
		return null;
	}
	
	/**
	 * unix时间戳转换为日期字符串
	 * @param timestamp
	 * @param format
	 * @return
	 */
	public static String timestamp2Str(long timestamp, String format)
	{
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(timestamp);
	}
	
	/**
	 * unix时间戳转换为日期时间
	 * @param timestamp
	 * @return
	 */
	public static Date timestamp2Date(long timestamp)
	{
		try
		{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String dateStr = sdf.format(timestamp);
			return sdf.parse(dateStr);
		}
		catch (Exception exp)
		{
			;
		}
		return null;
	}
	
	/**
	 * 获取指定时间对应的时间戳(13位)
	 * @param date
	 * @return
	 */
	public static long getTimestamp13(Date date)
	{
		try
		{
			if (date == null)
			{
				date = new Date();
			}
	        return date.getTime();
		}
		catch (Exception exp)
		{
			;
		}
		return -1;
	}
	
	/**
	 * 获取指定时间对应的时间戳(10位)
	 * @param date
	 * @return
	 */
	public static long getTimestamp10(Date date)
	{
		long timestamp = getTimestamp13(date);
		if (timestamp != -1)
		{
			return timestamp / 1000;
		}
		return -1;
	}

	/**
	 * 日期拼接
	 *
	 * @param year      年
	 * @param month     月
	 * @param date      日
	 * @return
	 */
	public static String appendString(String year, String month, String date) {
		StringBuilder sb = new StringBuilder();
		if (!TextUtils.isEmpty(year) || !TextUtils.isEmpty(month) || !TextUtils.isEmpty(date)) {
			if (!TextUtils.isEmpty(year)) {
				sb.append(year);
				if (!TextUtils.isEmpty(month)) {
					sb.append("-").append(month);
					if (!TextUtils.isEmpty(date)) {
						sb.append("-").append(date);
					}
				}
			} else {
				if (!TextUtils.isEmpty(month)) {
					sb.append(month);
					if (!TextUtils.isEmpty(date)) {
						sb.append("-").append(date);
					}
				} else {
					if (!TextUtils.isEmpty(date)) {
						sb.append(date);
					}
				}
			}
		}
		return sb.toString();
	}

	/**
	 * 根据时间，得到星期
	 *
	 * @param date
	 * @return
	 */
	public static String setWeek(String date) {
		String week = "";
		try {
			SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
			SimpleDateFormat format2 = new SimpleDateFormat("EEEE");
			Date chooseDate = format1.parse(date);
			week = format2.format(chooseDate);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return week;
	}

	public static Date parseDate(String template, String date) {
		Date parsedDate = null;
		SimpleDateFormat format = new SimpleDateFormat(template, Locale.getDefault());
		try {
			parsedDate = format.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return parsedDate;
	}

	public static String formatDate(String template, Date date) {
		SimpleDateFormat format = new SimpleDateFormat(template, Locale.getDefault());
		return format.format(date);
	}

	public static String getFriendlyDisplayTime(long time)
	{
		if (time > 0) {
			try {
				if (isToday(time)) {
					long currTime = new Date().getTime();
					long diffMinute = (currTime - time) / (1000 * 60);
					if (diffMinute > 0)
					{
						if (diffMinute < 60)
						{
							return diffMinute + "分钟前";
						}
						else
						{
							long diffHour = diffMinute / 60;
							if (diffHour < 6)
							{
								return diffHour + "小时前";
							}
						}
					}
					else
					{
						return "刚刚";
					}

					Calendar cal = Calendar.getInstance();
					cal.setTime(new Date(time));
					int hour = cal.get(Calendar.HOUR_OF_DAY);

					String dateFormat = "";
					if (hour > 17) {
						dateFormat = "晚上 HH:mm";
					} else if (hour >= 0 && hour <= 6) {
						dateFormat = "凌晨 HH:mm";
					} else if (hour > 11 && hour <= 17) {
						dateFormat = "下午 HH:mm";
					} else {
						dateFormat = "上午 HH:mm";
					}

					SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
					return sdf.format(new Date(time));
				} else {
					long diffDay = getDiffDay(new Date(time), new Date());
					if (diffDay == 1) {
						SimpleDateFormat sdf = new SimpleDateFormat("昨天 HH:mm");
						return sdf.format(new Date(time));
					} else if (diffDay == 2) {
						SimpleDateFormat sdf = new SimpleDateFormat("前天 HH:mm");
						return sdf.format(new Date(time));
					} else {
						int week = getDateWeek(new Date());
						if (week - diffDay >= 1) {
							String weekName = getWeekName((int) (week - diffDay), false);
							SimpleDateFormat sdf = new SimpleDateFormat(weekName + " HH:mm");
							return sdf.format(new Date(time));
						} else {
							SimpleDateFormat sdf = new SimpleDateFormat("M月d日 HH:mm");
							return sdf.format(new Date(time));
						}
					}
				}
			}
			catch (Exception exp)
			{
			}
		}
		return "";
	}

}
