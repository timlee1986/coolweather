package com.timlee1986.coolweather.helper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ConvertHelper
{
	public static String dateToString(Date date,String format)
	{
		SimpleDateFormat other = new SimpleDateFormat(format);
		return other.format(date);
	}
	public static Date stringToDate(String date,String format)
	{
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		try
		{
			return sdf.parse(date);
		}
		catch (ParseException e)
		{
			e.printStackTrace();
			return null;
		}
	}

}
