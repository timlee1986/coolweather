package com.timlee1986.coolweather.db;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.timlee1986.coolweather.helper.LogHelper;
import com.timlee1986.coolweather.helper.SqlHelper;
import com.timlee1986.coolweather.model.City;
import com.timlee1986.coolweather.model.Country;
import com.timlee1986.coolweather.model.Province;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class CoolWeatherDB
{
	public static final String DB_NAME = "Cool_Weather.db";
	public static final int DB_VERSION = 1;
	private static CoolWeatherDB coolWeatherDB = null;
	private SQLiteDatabase db;

	private CoolWeatherDB(Context context)
	{
		SqlHelper helper = new SqlHelper(context, DB_NAME, null, DB_VERSION);
		db = helper.getReadableDatabase();
	}

	public static CoolWeatherDB getInstance(Context context)
	{
		if (coolWeatherDB == null)
		{
			coolWeatherDB = new CoolWeatherDB(context);
		}
		return coolWeatherDB;
	}

	public boolean saveCity(Province province)
	{
		if (province != null)
		{
			ContentValues values = new ContentValues();
			values.put("province_name", province.getProvinceName());
			values.put("province_code", province.getProvinceCode());
			long result = db.insert("Province", null, values);
			if (result > 1)
				return true;
		}
		return false;
	}

	public boolean saveCity(City city)
	{
		if (city != null)
		{
			ContentValues values = new ContentValues();
			values.put("city_name", city.getCityName());
			values.put("city_code", city.getCityCode());
			values.put("province_id", city.getProvinceId());
			long result = db.insert("City", null, values);
			if (result > 1)
				return true;
		}
		return false;
	}

	public boolean saveCountry(Country country)
	{
		if (country != null)
		{
			ContentValues values = new ContentValues();
			values.put("county_name", country.getCountryName());
			values.put("county_code", country.getCountryCode());
			values.put("city_id", country.getCityId());
			long result = db.insert("Country", null, values);
			if (result > 1)
				return true;
		}
		return false;
	}

	public List<Province> getProvinces()
	{
		List<Province> list = new ArrayList<Province>();
		Cursor cursor = db.query("Provice", null, null, null, null, null, null);
		if (cursor.moveToFirst())
		{
			do
			{
				Province province = new Province();
				province.setId(cursor.getInt(cursor.getColumnIndex("id")));
				province.setProvinceCode(cursor.getString(cursor
						.getColumnIndex("province_code")));
				province.setProvinceName(cursor.getString(cursor
						.getColumnIndex("province_name")));
				list.add(province);

			}
			while (cursor.moveToNext());
		}
		return list;

	}

	public List<City> getCitys(int provinceId)
	{
		List<City> list = new ArrayList<City>();
		Cursor cursor = db.query("City", null, "province_id = ?", new String[]
		{ provinceId + "" }, null, null, null);
		if (cursor.moveToFirst())
		{
			do
			{
				City city = new City();
				city.setId(cursor.getInt(cursor.getColumnIndex("id")));
				city.setCityCode(cursor.getString(cursor
						.getColumnIndex("city_code")));
				city.setCityName(cursor.getString(cursor
						.getColumnIndex("city_name")));
				city.setProvinceId(cursor.getInt(cursor.getColumnIndex("province_id")));
				list.add(city);

			}
			while (cursor.moveToNext());
		}
		return list;

	}

	public List<Country> getCounties(int cityId)
	{
		List<Country> list = new ArrayList<Country>();
		Cursor cursor = db.query("County ", null, "city_id = ?", new String[]
		{ cityId + "" }, null, null, null);
		if (cursor.moveToFirst())
		{
			do
			{
				Country country = new Country();
				country.setId(cursor.getInt(cursor.getColumnIndex("id")));
				country.setCountryCode(cursor.getString(cursor
						.getColumnIndex("county_code")));
				country.setCountryName(cursor.getString(cursor
						.getColumnIndex("county_name")));
				country.setId(cursor.getInt(cursor.getColumnIndex("city_id")));
				list.add(country);

			}
			while (cursor.moveToNext());
		}
		return list;
	}

	public boolean initData(Map<String, List<String>> cityMap)
	{
		String sql = "";
		int tmp=0;
		String tmpkey="";
		try
		{
			
			db.beginTransaction();
			List<String> provinces = cityMap.get("0");
			tmpkey+="";
			for (int i = 0; i < provinces.size(); i++)
			{
				tmpkey = "0_"+i;
				
				List<String> citys =cityMap.containsKey("0_"+i)? cityMap.get("0_"+i):null;
				sql = "Insert into Provice (province_name)values('"
						+ provinces.get(i) + "')";
				db.execSQL(sql);
				if(citys==null)continue;
				for (int j = 0; j < citys.size(); j++)
				{
					tmpkey = "0_"+i+"_"+j;
					List<String> countries = cityMap.containsKey("0_"+i+"_"+j)? cityMap.get("0_"+i+"_"+j):null;
					sql = "Insert into City (city_name,province_id)values('"
							+ citys.get(j) + "',"+(i+1)+")";
					db.execSQL(sql);
					tmp++;
					if(countries==null)continue;
					for (int k = 0; k < countries.size(); k++)
					{
						sql = "Insert into County (county_name,city_id)values('"
								+ countries.get(k) + "',"+tmp+")";
						db.execSQL(sql);
					}
				}				
			}
			db.setTransactionSuccessful();
			return true;
		}
		catch (Exception ex)
		{
			
			LogHelper.e(ex.getMessage());
			ex.printStackTrace();
		}
		finally
		{
			db.endTransaction();
		}
		return false;
	}

}
