package com.timlee1986.coolweather.service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import com.timlee1986.coolweather.R;
import com.timlee1986.coolweather.activity.WeatherActivity;
import com.timlee1986.coolweather.helper.HttpHelper;
import com.timlee1986.coolweather.helper.LogHelper;
import com.timlee1986.coolweather.listener.HttpCallbackListenerImpl;
import com.timlee1986.coolweather.receiver.AutoUpdateReceiver;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;

public class AutoUpdateService extends Service
{
	private String cityName = null;
	// 定义我们要发送的事件
	private final String broadCastString = "com.timlee1986.coolweather.appWidgetUpdate";
	private HttpCallbackListenerImpl callback = null;
	private Handler handler = null;
	private SharedPreferences share = null;
	private NotificationManager mNotificationManager = null;
	private Notification.Builder builder = null;
	private Notification notification = null;

	@Override
	public IBinder onBind(Intent intent)
	{
		return null;
	}

	@Override
	public void onCreate()
	{
		share = PreferenceManager.getDefaultSharedPreferences(this);
		mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		handler = new Handler()
		{
			@Override
			public void handleMessage(Message msg)
			{
				if (msg.what > -1)
				{
					sendBroadcastToWidget(true);
				}
			}
		};
		callback = new HttpCallbackListenerImpl(getApplicationContext(),
				handler);
		super.onCreate();
	}

	private void sendBroadcastToWidget(boolean isNotification)
	{
		Intent boardcast = new Intent(broadCastString);
		String city = share.getString("city", "N");
		String temp1 = share.getString("temp1", "-℃");
		String temp2 = share.getString("temp2", "-℃");
		String temp = share.getString("temp", "-℃");
		String time = "今天"+share.getString("time", "12:00")+"发布";
		String weather = share.getString("weatherDesp", "N");
		boardcast.putExtra("city", city);
		boardcast.putExtra("weatherdesp", weather);
		boardcast.putExtra("weathertemp", temp1 + "～" + temp2);
		if (isNotification)
			showMsgOnNotification(city, temp1, temp2, temp, weather,time);
		sendBroadcast(boardcast);
	}

	@SuppressLint("NewApi")
	private void showMsgOnNotification(String city, String temp1, String temp2,
			String temp, String desp,String time)
	{
		// //定义通知栏展现的内容信息
		int icon = R.drawable.cool;
		String pageName = this.getPackageName();
		LogHelper.d(pageName);
		RemoteViews rv = new RemoteViews(pageName, R.layout.notification_layout);
		rv.setTextViewText(R.id.notification_city, city);
		rv.setTextViewText(R.id.notification_info, desp);
		rv.setTextViewText(R.id.notification_temp, temp);
		rv.setTextViewText(R.id.notification_time, time);
		// rv.setTextViewText(R.id.notification_temps, temp1 + "/" + temp2);
		// CharSequence tickerText = "我的通知栏标题";
		// long when = System.currentTimeMillis();
		// Notification notification = new Notification(icon, tickerText, when);
		//
		// //定义下拉通知栏时要展现的内容信息
		// Context context = getApplicationContext();
		// CharSequence contentTitle = "我的通知栏标展开标题";
		// CharSequence contentText = "我的通知栏展开详细内容";
		// Intent notificationIntent = new Intent(this, BootStartDemo.class);
		// PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
		// notificationIntent, 0);
		// notification.setLatestEventInfo(context, contentTitle, contentText,
		// contentIntent);
		//
		// //用mNotificationManager的notify方法通知用户生成标题栏消息通知
		// mNotificationManager.notify(1, notification);
		Intent notificationIntent = new Intent(this, WeatherActivity.class);
		notificationIntent.putExtra("City", city);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent,
				0);
		builder = new Notification.Builder(this);
		builder.setSmallIcon(icon)
				// 设置状态栏里面的图标（小图标）
				.setContentText("天气更新了..").setContentTitle("天气更新了..")
				.setTicker("天气更新了..") // 设置状态栏的显示的信息
				.setShowWhen(true)//是否允许setwhen
				.setWhen(System.currentTimeMillis())// 设置时间发生时间
				.setOngoing(true)//不能被用户x掉，会一直显示，如音乐播放等
				.setAutoCancel(false);// 设置可以清除
		builder.setContent(rv);// 自定义的remoteviews
		builder.setContentIntent(contentIntent);
		
		notification = builder.build();// 获取一个Notification
		notification.defaults = Notification.DEFAULT_SOUND;// 设置为默认的声音
		long[] vibrates = {0, 1000, 1000, 1000};
		notification.vibrate = vibrates;
		notification.ledARGB = Color.GREEN;
		notification.ledOnMS = 1000;
		notification.ledOffMS = 1000;
		notification.flags = Notification.FLAG_SHOW_LIGHTS;
		mNotificationManager.notify(1, notification);// 显示通知
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		if (intent != null)
		{
			boolean isNeedToUpate = intent.getBooleanExtra("isNeedToUpate",
					true);
			if (isNeedToUpate)
			{
				if (cityName == null)
					return super.onStartCommand(intent, flags, startId);
				LogHelper.d("Service to Update Weahter");
				updateWeather();
			}
			else
			{
				cityName = intent.getStringExtra("city");
				sendBroadcastToWidget(isNeedToUpate);
			}
			AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
			int anHour = 8*60*60 * 1000; // 这是8小时的毫秒数
			long triggerAtTime = SystemClock.elapsedRealtime() + anHour;
			Intent i = new Intent(this, AutoUpdateReceiver.class);
			i.putExtra("city", cityName);
			LogHelper.d("Service citye " + cityName);
			PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, 0);
			manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
			LogHelper.d("Service onStartCommand");
		}
		return super.onStartCommand(intent, flags, startId);
	}

	private void updateWeather()
	{
		String url;
		try
		{
			String city = cityName.replace("市", "");
			url = HttpHelper.httpUrl + URLEncoder.encode(city, "UTF-8");
			LogHelper.d(url);
			HttpHelper.sendHttpRequest(this,url, callback);
		}
		catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
		}
	}

}
