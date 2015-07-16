package com.timlee1986.coolweather.helper;

import android.util.Log;

public class LogHelper
{
	public static final int VERBOSE = 1;
	public static final int DEBUG = 2;
	public static final int INFO = 3;
	public static final int WARN = 4;
	public static final int ERROR = 5;
	public static final int NOTHING = 6;
	public static final int LEVEL = VERBOSE;
	public static String Tag = "coolweather";

	public static void v(String msg)
	{

		if (LEVEL <= VERBOSE)
		{
			try
			{
				Log.v(Tag, msg);
			}
			catch (Exception e)
			{}
		}
	}

	public static void d(String msg)
	{
		if (LEVEL <= DEBUG)
		{
			try
			{
				Log.d(Tag, msg);
			}
			catch (Exception e)
			{}
		}
	}

	public static void i(String msg)
	{
		if (LEVEL <= INFO)
		{
			try
			{
				Log.i(Tag, msg);
			}
			catch (Exception e)
			{}
		}
	}

	public static void w(String msg)
	{
		if (LEVEL <= WARN)
		{
			try
			{
				Log.w(Tag, msg);
			}
			catch (Exception e)
			{}
		}
	}

	public static void e(String msg)
	{
		if (LEVEL <= ERROR)
		{
			try
			{
				Log.e(Tag, msg);
			}
			catch (Exception e)
			{}
		}
	}
}
