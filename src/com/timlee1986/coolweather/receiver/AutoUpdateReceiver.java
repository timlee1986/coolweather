package com.timlee1986.coolweather.receiver;

import com.timlee1986.coolweather.service.AutoUpdateService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AutoUpdateReceiver extends BroadcastReceiver
{

	@Override
	public void onReceive(Context context, Intent intent)
	{
		Intent i = new Intent(context, AutoUpdateService.class);
		i.putExtra("city", intent.getStringExtra("city"));
		context.startService(i);
	}

}
