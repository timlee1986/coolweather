package com.timlee1986.coolweather.helper;

import java.lang.reflect.Type;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

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
		// classOfT.cast(classOfT);
		return gson.fromJson(json, classOfT);
	}

	public static Object fromJson(String json, Type type)
	{
		return gson.fromJson(json, type);
	}

	public static Object readJson(String json, String key) throws JSONException
	{
		JSONTokener jsonParser = new JSONTokener(json);
		JSONObject obj = (JSONObject) jsonParser.nextValue();
		return obj.get(key);
	}
	public static Object readJson(Object jsonObject, String key) throws JSONException
	{
		JSONObject obj = (JSONObject) jsonObject;
		return obj.get(key);
	}

}
