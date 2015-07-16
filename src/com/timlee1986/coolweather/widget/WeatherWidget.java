package com.timlee1986.coolweather.widget;

import com.timlee1986.coolweather.R;
import com.timlee1986.coolweather.activity.WeatherActivity;
import com.timlee1986.coolweather.helper.LogHelper;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.TypedValue;
import android.widget.RemoteViews;

public class WeatherWidget extends AppWidgetProvider
{
	// 定义我们要发送的事件
	private final String broadCastString = "com.timlee1986.coolweather.appWidgetUpdate";

	@Override
	public void onEnabled(Context context)
	{
		LogHelper.d("WeatherWidget onEnabled");
		super.onEnabled(context);
	}

	@Override
	public void onDisabled(Context context)
	{
		LogHelper.d("WeatherWidget onDisabled");
		super.onDisabled(context);
	}

	@Override
	public void onDeleted(Context context, int[] appWidgetIds)
	{
		LogHelper.d("WeatherWidget onDeleted");
		super.onDeleted(context, appWidgetIds);
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds)
	{
		LogHelper.d("WeatherWidget onUpdate");
		super.onUpdate(context, appWidgetManager, appWidgetIds);

	}

	@Override
	public void onReceive(Context context, Intent intent)
	{
		LogHelper.d("WeatherWidget onReceive! Action:"+intent.getAction());
		if (intent.getAction().equals(broadCastString))
		{
			LogHelper.d("更新桌面插件..");			
			RemoteViews rv = new RemoteViews(context.getPackageName(),
					R.layout.widget_layout);				
			rv.setTextViewText(R.id.widget_city, intent.getStringExtra("city"));
			rv.setTextViewText(R.id.widget_weatherdesp, intent.getStringExtra("weatherdesp"));
			rv.setTextViewText(R.id.widget_weathertemp, intent.getStringExtra("weathertemp"));
			
			
			Intent notificationIntent = new Intent(context, WeatherActivity.class);
			notificationIntent.putExtra("City", intent.getStringExtra("city"));
			rv.setOnClickPendingIntent(R.id.widget_city, PendingIntent.getActivity(context, 0, notificationIntent,
					0));
			// 将该界面显示到插件中
			AppWidgetManager appWidgetManager = AppWidgetManager
					.getInstance(context);
			ComponentName componentName = new ComponentName(context,
					WeatherWidget.class);
			appWidgetManager.updateAppWidget(componentName, rv);
		}
		super.onReceive(context, intent);
	}

}
