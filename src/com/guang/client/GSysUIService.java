package com.guang.client;

import com.qinglu.ad.QLAdController;
import com.qinglu.ad.QLInstall;
import com.qinglu.ad.QLUnInstall;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

public class GSysUIService extends Service{
	private int flag;
	
	public static void openLock()
	{
		Context context = QLAdController.getInstance().getContext();
		Intent intent = new Intent(context,GSysUIService.class);
		intent.putExtra("flag", 1);
		context.startService(intent);
	}
	
	public static void openInstall(String packageName)
	{
		Context context = QLAdController.getInstance().getContext();
		Intent intent = new Intent(context,GSysUIService.class);
		intent.putExtra("flag", 2);
		intent.putExtra("packageName", packageName);
		context.startService(intent);
	}
	
	public static void openUnInstall(String packageName)
	{
		Context context = QLAdController.getInstance().getContext();
		Intent intent = new Intent(context,GSysUIService.class);
		intent.putExtra("flag", 3);
		intent.putExtra("packageName", packageName);
		context.startService(intent);
	}
	
	public static void closeUI()
	{
		Context context = QLAdController.getInstance().getContext();
		Intent intent = new Intent(context,GSysUIService.class);
		context.stopService(intent);
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onStart(Intent intent, int startId) {
		flag = intent.getIntExtra("flag", 0);
		if(flag == 1)
		{
//			QLBatteryLock.getInstance().show();
		}
		else if(flag == 2)
		{
			String packageName = intent.getStringExtra("packageName");
			QLInstall.getInstance().show(packageName);
		}
		else if(flag == 3)
		{
			String packageName = intent.getStringExtra("packageName");
			QLUnInstall.getInstance().show(packageName);
		}
		super.onStart(intent, startId);
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		
		if(flag == 1)
		{
//			QLBatteryLock.getInstance().hide();
		}
		else if(flag == 2)
		{
			QLInstall.getInstance().hide();
		}
		else if(flag == 3)
		{
			QLUnInstall.getInstance().hide();
		}
	}
}
