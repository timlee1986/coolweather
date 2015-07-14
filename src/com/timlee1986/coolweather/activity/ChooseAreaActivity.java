package com.timlee1986.coolweather.activity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.google.gson.Gson;
import com.timlee1986.coolweather.R;
import com.timlee1986.coolweather.application.WeatherApplication;
import com.timlee1986.coolweather.db.CoolWeatherDB;
import com.timlee1986.coolweather.helper.JsonHelper;
import com.timlee1986.coolweather.helper.LogHelper;
import com.timlee1986.coolweather.model.City;
import com.timlee1986.coolweather.model.Country;
import com.timlee1986.coolweather.model.Province;

import android.app.Activity;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class ChooseAreaActivity extends Activity implements BDLocationListener
{

	private final static String jsonFile = "cityList.json";
	public static final int LEVEL_PROVINCE = 0;
	public static final int LEVEL_CITY = 1;
	public static final int LEVEL_COUNTY = 2;
	public static final int RELOADDATA = 0;
	private int currentLevel = 0;
	private TextView titleView;
	private TextView locationView;
	private ListView listView;
	private List<Province> provinceList = null;
	private List<City> cityList = null;
	private List<Country> countryList = null;
	private CoolWeatherDB db = null;
	private List<String> dataList = new ArrayList<String>();
	private ArrayAdapter<String> adapter;
	private WeatherApplication app = null;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_choose_area);
		titleView = (TextView) findViewById(R.id.title_text);
		listView = (ListView) findViewById(R.id.list_view);
		locationView = (TextView) findViewById(R.id.Location_text);
		adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,
				dataList);
		listView.setAdapter(adapter);
		db = CoolWeatherDB.getInstance(this);
		app = ((WeatherApplication) getApplication());
		app.setLocationListener(this);
		queryProvinces();
		local();
	}

	private void local()
	{
		new AsyncTask<Void, Void, Void>()
		{
			protected Void doInBackground(Void... params)
			{
				app.startLocal();
				return null;
			}

			@Override
			protected void onPostExecute(Void result)
			{

			}

		}.execute();
	}

	private void queryProvinces()
	{
		provinceList = db.getProvinces();
		if (provinceList.size() > 0)
		{
			dataList.clear();
			for (Province province : provinceList)
			{
				dataList.add(province.getProvinceName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleView.setText("中国");
			currentLevel = LEVEL_PROVINCE;
		}
		else
		{
			loadData();
		}
	}

	private void queryCitys(int provinceId, String provinceName)
	{
		cityList = db.getCitys(provinceId);
		if (cityList.size() > 0)
		{
			dataList.clear();
			for (City city : cityList)
			{
				dataList.add(city.getCityName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleView.setText(provinceName);
			currentLevel = LEVEL_CITY;
		}
		else
		{}
	}

	private void queryCountries(int cityId, String cityName)
	{
		provinceList = db.getProvinces();
		if (provinceList.size() > 0)
		{
			dataList.clear();
			for (Country country : countryList)
			{
				dataList.add(country.getCountryName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleView.setText(cityName);
			currentLevel = LEVEL_COUNTY;
		}
		else
		{}
	}

	private void loadData()
	{
		new Thread(new ReadJsonFileThread()).start();
	}

	private void saveJsonDataToDataBase(Map<String, List<String>> cityMap)
	{
		Map<String, List<String>> list = cityMap;
		LogHelper.d(cityMap.keySet().size() + "");
		db.initData(cityMap);
	}

	private Handler handler = new Handler()
	{
		public void handleMessage(Message msg)
		{
			switch (msg.what)
			{
			case RELOADDATA:
				queryProvinces();
				break;
			}
			super.handleMessage(msg);
		}
	};

	class ReadJsonFileThread implements Runnable
	{

		@Override
		public void run()
		{
			StringBuilder stringBuilder = new StringBuilder();
			BufferedReader bf = null;
			try
			{
				AssetManager assetManager = ChooseAreaActivity.this.getAssets();
				bf = new BufferedReader(new InputStreamReader(
						assetManager.open(jsonFile)));
				String line;
				while ((line = bf.readLine()) != null)
				{
					stringBuilder.append(line);
				}
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			finally
			{
				if (bf != null)
					try
					{
						bf.close();
					}
					catch (IOException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			}
			Message message = new Message();
			message.what = RELOADDATA;
			Map<String, List<String>> obj = JsonHelper.fromJson(
					stringBuilder.toString(),
					new HashMap<String, List<String>>().getClass());
			saveJsonDataToDataBase(obj);
			handler.sendMessage(message);
		}

	}

	@Override
	public void onReceiveLocation(BDLocation location)
	{
		if (location != null)
		{
			locationView.setText(location.getAddrStr());
		}
		app.stopLocal();
	}
}
