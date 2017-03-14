package com.xugu.qewadlib;




import com.qinglu.ad.QLAdController;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;


public class GService extends Service{
	private Context context;
	//private int count = 0;
	

	@Override
	public void onCreate() {
		context = this;
		QLAdController.getInstance().init(this, true);
		
		super.onCreate();
	}

	@Override
	public void onStart(Intent intent, int startId) {
		Notification notification = new Notification();  
		notification.flags = Notification.FLAG_ONGOING_EVENT;  
		notification.flags |= Notification.FLAG_NO_CLEAR;  
		notification.flags |= Notification.FLAG_FOREGROUND_SERVICE;  
		startForeground(0, notification); 
		super.onStart(intent, startId);
	}
	@Override
	public void onDestroy() {
		stopForeground(true);
		super.onDestroy();
	}
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
}
