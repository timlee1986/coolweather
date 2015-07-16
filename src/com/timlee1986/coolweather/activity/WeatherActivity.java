package com.timlee1986.coolweather.activity;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import com.timlee1986.coolweather.R;
import com.timlee1986.coolweather.helper.HttpHelper;
import com.timlee1986.coolweather.helper.LogHelper;
import com.timlee1986.coolweather.listener.HttpCallbackListenerImpl;
import com.timlee1986.coolweather.service.AutoUpdateService;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class WeatherActivity extends Activity implements OnClickListener
{

	private LinearLayout weatherInfoLayout;
	private Button buttonHome = null;
	private Button buttonRefresh = null;
	private TextView text_cityName = null;
	private TextView text_Public = null;
	private TextView text_Temp1 = null;
	private TextView text_Temp2 = null;
	private TextView text_CurrentDate = null;
	private TextView text_WeatherDesp = null;
	private boolean isRequest = false;
	private HttpCallbackListenerImpl httpCallback = null;
	private Handler handler = null;
	private SharedPreferences share = null;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.weather_layout);
		handler = new Handler()
		{
			@Override
			public void handleMessage(Message msg)
			{
				isRequest = false;
				switch (msg.what)
				{
				case 0:
					showWeathers();
					Intent intent = new Intent(WeatherActivity.this,
							AutoUpdateService.class);
					intent.putExtra("city", text_cityName.getText());
					intent.putExtra("isNeedToUpate", false);
					startService(intent);
					break;
				case -1:
					weatherInfoLayout.setVisibility(View.INVISIBLE);
					text_Public.setText("同步失败..");
					break;
				default:
					break;
				}
			}
		};
		httpCallback = new HttpCallbackListenerImpl(this, handler);
		weatherInfoLayout = (LinearLayout) findViewById(R.id.weather_info_layout);
		buttonHome = (Button) findViewById(R.id.switch_city);
		buttonRefresh = (Button) findViewById(R.id.refresh_weather);
		buttonHome.setOnClickListener(this);
		buttonRefresh.setOnClickListener(this);
		text_cityName = (TextView) findViewById(R.id.city_name);
		text_Public = (TextView) findViewById(R.id.public_text);
		text_Temp1 = (TextView) findViewById(R.id.temp1);
		text_Temp2 = (TextView) findViewById(R.id.temp2);
		text_CurrentDate = (TextView) findViewById(R.id.current_date);
		text_WeatherDesp = (TextView) findViewById(R.id.weather_desp);
		String cityName = getIntent().getStringExtra("City");
		weatherInfoLayout.setVisibility(View.INVISIBLE);
		text_cityName.setText(cityName);
		share = PreferenceManager.getDefaultSharedPreferences(this);
		showWeathers();
	}

	@Override
	public void onClick(View v)
	{
		if (v.getId() == R.id.switch_city)
		{
			goBackCityList();
		}
		else if (v.getId() == R.id.refresh_weather)
		{
			refreshWeather();
		}
	}

	private void refreshWeather()
	{
		if (isRequest)
			return;
		text_Public.setText("同步中...");
		String url;
		try
		{
			String city = text_cityName.getText().toString().replace("市", "");
			url = HttpHelper.httpUrl + URLEncoder.encode(city, "UTF-8");
			LogHelper.d(url);
			HttpHelper.sendHttpRequest(this,url, httpCallback);
		}
		catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
		}
		isRequest = true;
	}

	private void goBackCityList()
	{
		// Intent intent = new Intent(this, ChooseAreaActivity.class);
		// intent.putExtra("from_weather_activity", true);
		// startActivity(intent);
		finish();
	}

	private void showWeathers()
	{
		try
		{
			int code  =Integer.parseInt( share.getString("code", "-1"));
			if(code<0){refreshWeather();return;}
			text_Temp1.setText(share.getString("temp1", ""));
			text_Temp2.setText(share.getString("temp2", ""));
			text_WeatherDesp.setText(share.getString("weatherDesp", ""));
			text_CurrentDate.setText(share.getString("currentDate", ""));
			text_Public.setText("今天" + share.getString("time", "") + "发布");
			weatherInfoLayout.setVisibility(View.VISIBLE);			
		}
		catch (Exception e)
		{
			LogHelper.e(e.getLocalizedMessage());
			e.printStackTrace();
			weatherInfoLayout.setVisibility(View.INVISIBLE);
			text_Public.setText("同步失败..");
		}
	}
}
