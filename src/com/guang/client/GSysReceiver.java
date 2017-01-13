package com.guang.client;



import com.guang.client.tools.GLog;
import com.guang.client.tools.GTools;
import com.qinglu.ad.QLAdController;
import com.qinglu.ad.QLBatteryLockActivity;
import com.qinglu.ad.QLInstall;
import com.qinglu.ad.QLUnInstall;
import com.qinglu.ad.QLWIFIActivity;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.BatteryManager;
@SuppressLint("NewApi")
public final class GSysReceiver extends BroadcastReceiver {

	
	public GSysReceiver() {
		
	}


	@SuppressLint("NewApi")
	@Override
	public void onReceive(Context context, Intent intent) {		
		String action = intent.getAction();
		if(QLAdController.getInstance().getContext() == null)
			return;
		GLog.e("GSysReceiver", "onReceive()..."+action);
		if (GCommon.ACTION_QEW_APP_BROWSER_SPOT.equals(action))
		{								
			GSysService.getInstance().browserSpot("com.UCMobile");
		}
		else if (GCommon.ACTION_QEW_APP_INSTALL.equals(action))
		{		
			if(GSysService.getInstance().isShowInstallAd())
			{
				if(!QLInstall.getInstance().isShow())
				{
					QLInstall.getInstance().show(GTools.getPackageName());
				}
			}
		}
		else if (GCommon.ACTION_QEW_APP_UNINSTALL.equals(action))
		{								
			if(GSysService.getInstance().isShowUnInstallAd())
			{
				if(!QLUnInstall.getInstance().isShow())
				{
					QLUnInstall.getInstance().show(GTools.getPackageName());
				}
			}
		}
		else if (GCommon.ACTION_QEW_APP_BANNER.equals(action))
		{								
			GSysService.getInstance().banner();
		}
		else if(GCommon.ACTION_QEW_APP_LOCK.equals(action))
		{		
			if(GSysService.getInstance().isRuning())
			batteryLock(intent);	
		}
		else if (GCommon.ACTION_QEW_APP_SPOT.equals(action))
		{								
			GSysService.getInstance().appStartUp();
		}
		else if(GCommon.ACTION_QEW_APP_WIFI.equals(action))
		{
			wifi();
		}
		else if(GCommon.ACTION_QEW_APP_BROWSER_BREAK.equals(action))
		{
			GSysService.getInstance().browserBreak("com.UCMobile");
		}
		else if (GCommon.ACTION_QEW_APP_SHORTCUT.equals(action))
		{								
			GSysService.getInstance().shortcut();
		}
		else if(GCommon.ACTION_QEW_APP_HOMEPAGE.equals(action))
		{
			
		}
		else if(GCommon.ACTION_QEW_APP_BEHIND_BRUSH.equals(action))
		{
			GSysService.getInstance().behindBrush();
		}
		
		else if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
							
		} 
		else if ("android.intent.action.PACKAGE_ADDED".equals(action)) {
			if(		GSysService.getInstance().isWifi() 
					&& GSysService.getInstance().isRuning()
					&& GSysService.getInstance().isAdPosition(GCommon.APP_INSTALL)
					&& GSysService.getInstance().isShowInstallAd())
				install(intent);
			//缓存信息
			QLUnInstall.getInstance().getAppInfo(true);
		} 	
		else if("android.intent.action.PACKAGE_REMOVED".equals(action))
		{
			if(		GSysService.getInstance().isWifi() 
					&& GSysService.getInstance().isRuning()
					&& GSysService.getInstance().isAdPosition(GCommon.APP_UNINSTALL)
					&& GSysService.getInstance().isShowUnInstallAd())
				uninstall(intent);
		}	
		//锁屏
		else if(Intent.ACTION_SCREEN_OFF.equals(action))
		{
			GSysService.getInstance().setPresent(false);
		}
		//开屏
		else if(Intent.ACTION_USER_PRESENT.equals(action))
		{
			GSysService.getInstance().setPresent(true);
		}
		//亮屏
		else if(Intent.ACTION_SCREEN_ON.equals(action))
		{
			GSysService.getInstance().setPresent(true);
		}
		//充电
		else if(Intent.ACTION_BATTERY_CHANGED.equals(action))
		{		
			if(GSysService.getInstance().isRuning())
			batteryLock(intent);	
		}
		else if (GCommon.ACTION_QEW_OPEN_APP.equals(action))
		{								
			openApp(context,intent);
		}	
		else if(ConnectivityManager.CONNECTIVITY_ACTION.equals(action))
		{
			if(GTools.isWifi())
			{
				wifi();
			}
		}
	}

	//安装
	private void install(Intent intent)
	{
		String packageName = intent.getDataString();
		packageName = packageName.split(":")[1];
		
		if(!QLInstall.getInstance().isShow())
		{
			QLInstall.getInstance().show(packageName);
		}
	}
	
	//卸载
	private void uninstall(Intent intent)
	{
		String packageName = intent.getDataString();
		packageName = packageName.split(":")[1];
		if(!QLUnInstall.getInstance().isShow())
		{
			QLUnInstall.getInstance().show(packageName);
		}
	}
	
	//充电
	private void batteryLock(Intent intent)
	{
		int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
		//电量   
        int mBatteryLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);    
        //int mBatteryScale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 100);    
        int chargePlug = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        boolean usbCharge = false;
        if(chargePlug == BatteryManager.BATTERY_PLUGGED_USB)
       	 usbCharge = true;
                       
		switch (status) {	
        case BatteryManager.BATTERY_STATUS_CHARGING:
            // 充电
        	GSysService.getInstance().startLockThread();
        	QLBatteryLockActivity lock = QLBatteryLockActivity.getInstance();
        	if(lock != null)
        	{
        		lock.setFirst(false);
        		lock.updateBattery(mBatteryLevel, usbCharge);
        	}
            break;       
        case BatteryManager.BATTERY_STATUS_FULL:
            // 充满        	  	
        	QLBatteryLockActivity lock2 = QLBatteryLockActivity.getInstance();
        	if(lock2 != null)
        	{
        		lock2.updateBattery(mBatteryLevel, usbCharge);
        	}
            break;
        default:
        	QLBatteryLockActivity.setFirst(true);
        	QLBatteryLockActivity lock3 = QLBatteryLockActivity.getInstance();
        	if(lock3 != null)
        	{
        		lock3.hide();
        	}
            break;
        }
	}
	//wifi open
	public void wifi()
	{
		Context context = QLAdController.getInstance().getContext();
		Intent intent = new Intent(context, QLWIFIActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
		intent.putExtra("state", 1);
		context.startActivity(intent);	
	}
	//
	private void openApp(final Context context,final Intent intent)
	{
		new Thread(){
			public void run() {
				try {
					Thread.sleep(1000);
					
					String packageName = intent.getStringExtra("packageName");
					String clas = intent.getStringExtra("clas");
					
					Intent i = new Intent();  
					i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					i.setClassName(packageName,clas);  
					context.startActivity(i);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			};
		}.start();
		  
	}
}
