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
import com.timlee1986.coolweather.R;
import com.timlee1986.coolweather.application.WeatherApplication;
import com.timlee1986.coolweather.db.CoolWeatherDB;
import com.timlee1986.coolweather.helper.JsonHelper;
import com.timlee1986.coolweather.model.City;
import com.timlee1986.coolweather.model.Country;
import com.timlee1986.coolweather.model.Province;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
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
	private String localCity = null;
	/**
	 * 选中的省份
	 */
	private Province selectedProvince;
	/**
	 * 选中的城市
	 */
	private City selectedCity;

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
		locationView.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				if (localCity != null)
				{
					Intent intent = new Intent(ChooseAreaActivity.this,
							WeatherActivity.class);
					intent.putExtra("City", localCity);
					startActivity(intent);
				}
			}
		});
		listView.setOnItemClickListener(new OnItemClickListener()
		{

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id)
			{
				if (currentLevel == LEVEL_PROVINCE)
				{
					selectedProvince = provinceList.get(position);
					queryCitys(selectedProvince.getId(),
							selectedProvince.getProvinceName());
				}
				else if (currentLevel == LEVEL_CITY)
				{
					selectedCity = cityList.get(position);
					// LogHelper.d("----"+selectedCity.getCityName()+" "+selectedCity.getId());
					// queryCountries(selectedCity.getId(),
					// selectedCity.getCityName());
					Intent intent = new Intent(ChooseAreaActivity.this,
							WeatherActivity.class);
					intent.putExtra("City", selectedCity.getCityName());
					startActivity(intent);
				}
				else if (currentLevel == LEVEL_COUNTY)
				{

				}
			}
		});
		db = CoolWeatherDB.getInstance(this);
		app = ((WeatherApplication) getApplication());
		app.setLocationListener(this);
		queryProvinces();
		locate();
	}

	@Override
	protected void onRestart()
	{
		super.onRestart();
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

	private void locate()
	{
		new AsyncTask<Void, Void, Void>()
		{
			protected Void doInBackground(Void... params)
			{
				app.startLocal();
				return null;
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
	}

	private void queryCountries(int cityId, String cityName)
	{
		countryList = db.getCounties(cityId);
		if (countryList.size() > 0)
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
	}

	private void loadData()
	{
		new Thread(new ReadJsonFileThread()).start();
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

			Map<String, List<String>> obj = JsonHelper.fromJson(
					stringBuilder.toString(),
					new HashMap<String, List<String>>().getClass());
			Message message = new Message();
			if (db.initData(obj))
				message.what = RELOADDATA;
			handler.sendMessage(message);
		}

	}

	@Override
	public void onBackPressed()
	{
		if (currentLevel == LEVEL_CITY)
		{
			queryProvinces();
		}
		else if (currentLevel == LEVEL_COUNTY)
		{
			queryCitys(selectedProvince.getId(),
					selectedProvince.getProvinceName());
		}
		else
		{
			super.onBackPressed();
		}
	}

	@Override
	public void onReceiveLocation(BDLocation location)
	{
		if (location != null)
		{
			localCity = location.getCity();
			locationView.setText("您当前所在的城市：" + location.getCity());
		}
		app.stopLocal();
	}
}
