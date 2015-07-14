package com.timlee1986.coolweather.helper;

import java.lang.reflect.Type;

import com.google.gson.Gson;

public class JsonHelper
{
	private static Gson gson = new Gson();

	public static String toJson(Object src)
	{
		return gson.toJson(src);
	}

	public static <T> T fromJson(String json, Class<T> classOfT)
	{
		//classOfT.cast(classOfT);
		return gson.fromJson(json, classOfT);		
	}

	public static Object fromJson(String json,Type type)
	{
		return gson.fromJson(json,type);
	}

}
