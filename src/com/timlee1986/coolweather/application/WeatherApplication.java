package com.timlee1986.coolweather.application;

import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.timlee1986.coolweather.helper.LogHelper;

import android.app.Application;

public class WeatherApplication extends Application
{
	public LocationClient mLocationClient;
	private LocationMode tempMode = LocationMode.Hight_Accuracy;
	private String tempcoor="gcj02";

	@Override
	public void onCreate()
	{
		super.onCreate();
		mLocationClient = new LocationClient(this.getApplicationContext());
		InitLocation();
	}
	private void InitLocation(){
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(tempMode);//设置定位模式
		option.setCoorType(tempcoor);//返回的定位结果是百度经纬度，默认值gcj02
		int span=5000;		
		option.setScanSpan(span);//设置发起定位请求的间隔时间为5000ms
		option.setIsNeedAddress(true);
		mLocationClient.setLocOption(option);
	}
	

	public void setLocationListener(BDLocationListener lisetener)
	{
		mLocationClient.registerLocationListener(lisetener);
	}

	public void startLocal()
	{
		mLocationClient.start();
		LogHelper.d("startLocal");
	}

	public void stopLocal()
	{
		mLocationClient.stop();
		LogHelper.d("stopLocal");
	}

}
