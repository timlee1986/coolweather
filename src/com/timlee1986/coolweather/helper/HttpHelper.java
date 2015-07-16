package com.timlee1986.coolweather.helper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import com.timlee1986.coolweather.listener.HttpCallbackListener;

import android.app.PendingIntent.OnFinished;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class HttpHelper
{
	public final static String httpUrl = "http://apis.baidu.com/apistore/weatherservice/cityname?cityname=";

	public static void sendHttpRequest(Context context, final String address,
			final HttpCallbackListener listener)
	{
		if (isNetworkConnected(context))
		{
			new Thread(new Runnable()
			{
				HttpURLConnection connection = null;

				@Override
				public void run()
				{
					try
					{
						URL url = new URL(address);
						connection = (HttpURLConnection) url.openConnection();
						connection.setRequestMethod("GET");
						// 填入apikey到HTTP header
						connection.setRequestProperty("apikey",
								"5a3829ddce032ae115303d2b80524c91");
						connection.connect();
						InputStream in = connection.getInputStream();
						BufferedReader reader = new BufferedReader(
								new InputStreamReader(in, "UTF-8"));
						String line = null;
						StringBuilder builder = new StringBuilder();
						while ((line = reader.readLine()) != null)
						{
							builder.append(line);
						}
						reader.close();
						if (listener != null)
							listener.onFinish(builder.toString());
					}
					catch (Exception ex)
					{
						if (listener != null)
							listener.onError(ex);
					}
					finally
					{
						if (connection != null)
							connection.disconnect();
					}

				}
			}).start();
		}
		else
		{
			if (listener != null)
				listener.onError(new Exception("网络不可以用.."));
			LogHelper.e("网络不可以用..");
		}

	}

	public static boolean isNetworkConnected(Context context)
	{
		if (context != null)
		{
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mNetworkInfo = mConnectivityManager
					.getActiveNetworkInfo();
			if (mNetworkInfo != null)
			{
				return mNetworkInfo.isAvailable();
			}
		}
		return false;
	}

}
