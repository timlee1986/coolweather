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
	public static String Tag ="coolweather";

	public static void v(String msg)
	{
		if (LEVEL <= VERBOSE)
		{
			Log.v(Tag, msg);
		}
	}
	public static void d(String msg)
	{
		if (LEVEL <= DEBUG)
		{
			Log.d(Tag, msg);
		}
	}
	public static void i( String msg)
	{
		if (LEVEL <= INFO)
		{
			Log.i(Tag, msg);
		}
	}
	public static void w( String msg)
	{
		if (LEVEL <= WARN)
		{
			Log.w(Tag, msg);
		}
	}
	public static void e( String msg)
	{
		if (LEVEL <= ERROR)
		{
			Log.e(Tag, msg);
		}
	}
}
