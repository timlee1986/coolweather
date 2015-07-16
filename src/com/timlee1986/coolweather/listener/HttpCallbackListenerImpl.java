package com.timlee1986.coolweather.listener;

import com.timlee1986.coolweather.helper.ConvertHelper;
import com.timlee1986.coolweather.helper.JsonHelper;
import com.timlee1986.coolweather.helper.LogHelper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;

@SuppressLint("CommitPrefEdits")
public class HttpCallbackListenerImpl implements HttpCallbackListener
{
	private Context context = null;
	private Handler handler = null;
	private SharedPreferences.Editor editor =null;

	public HttpCallbackListenerImpl(Context context, Handler hanlder)
	{
		this.handler = hanlder;
		editor = PreferenceManager
				.getDefaultSharedPreferences(context).edit();
	}

	@Override
	public void onFinish(String response)
	{
		setSharedPreferences(response);
	}

	@Override
	public void onError(Exception e)
	{
		LogHelper.e(e.getLocalizedMessage());
		setSharedPreferences(null);
	}

	private void setSharedPreferences(String info)
	{
	
		editor.clear();
		try
		{
			if (info != null)
			{
				int code = Integer.parseInt(JsonHelper.readJson(info, "errNum")
						.toString());
				if (code > -1)
				{
					Object result = JsonHelper.readJson(info, "retData");
					editor.putString("code", code + "");
					editor.putString("temp1",
							JsonHelper.readJson(result, "l_tmp").toString()
									+ "℃");
					editor.putString("temp2",
							JsonHelper.readJson(result, "h_tmp").toString()
									+ "℃");
					editor.putString("weatherDesp",
							JsonHelper.readJson(result, "weather").toString());
					editor.putString("time", JsonHelper
							.readJson(result, "time").toString());
					editor.putString("temp", JsonHelper
							.readJson(result, "temp").toString()+ "℃");
					editor.putString("city", JsonHelper
							.readJson(result, "city").toString());
					editor.putString("currentDate", ConvertHelper.dateToString(
							ConvertHelper.stringToDate(
									JsonHelper.readJson(result, "date")
											.toString(), "yy-MM-dd"),
							"yyyy年M月d日"));
					editor.commit();
					sendMessage("同步成功..", 0);
					LogHelper.d("同步成功"+" "+JsonHelper
							.readJson(result, "city").toString());
					return;
				}
			}
		}
		catch (Exception e)
		{
			LogHelper.e(e.getLocalizedMessage());
			e.printStackTrace();
			
		}
		LogHelper.d(info);
		editor.putString("code", "-1");		
		editor.commit();
		sendMessage("同步失败..", -1);
		LogHelper.d("同步失败");
	}

	private void sendMessage(String result, int type)
	{
		if (handler != null)
		{
			Message message = new Message();
			message.obj = result;
			message.arg1 = type;
			message.what = type;
			handler.sendMessage(message);
		}
	}

}
