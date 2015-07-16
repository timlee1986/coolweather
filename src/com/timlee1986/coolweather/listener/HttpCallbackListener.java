package com.timlee1986.coolweather.listener;

public interface HttpCallbackListener
{
	void onFinish(String response);
	void onError(Exception e);

}
