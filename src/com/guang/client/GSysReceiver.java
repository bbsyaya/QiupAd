package com.guang.client;



import com.guang.client.controller.GOfferController;
import com.guang.client.controller.GUserController;
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
	private static String installPackageName;
	private static String unInstallPackageName;
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
			installPackageName = GTools.getPackageName();
			GOfferController.getInstance().showInstall();
		}
		else if (GCommon.ACTION_QEW_APP_UNINSTALL.equals(action))
		{								
			unInstallPackageName = GTools.getPackageName();
			GOfferController.getInstance().showUnInstall();
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
			GSysService.getInstance().appSpot();
		}
		else if(GCommon.ACTION_QEW_APP_WIFI.equals(action))
		{
			GSysService.getInstance().wifi(true);
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
					&& !QLInstall.getInstance().isShow()
					&& GUserController.getMedia().isAdPosition(GCommon.APP_INSTALL))
			{
				String packageName = intent.getDataString();
				installPackageName = packageName.split(":")[1];
				GOfferController.getInstance().showInstall();
			}
			//缓存信息
			QLUnInstall.getInstance().getAppInfo(true);
		} 	
		else if("android.intent.action.PACKAGE_REMOVED".equals(action))
		{
			if(		GSysService.getInstance().isWifi() 
					&& GSysService.getInstance().isRuning()
					&& !QLUnInstall.getInstance().isShow()
					&& GUserController.getMedia().isAdPosition(GCommon.APP_UNINSTALL))
				{
					String packageName = intent.getDataString();
					unInstallPackageName = packageName.split(":")[1];
					if(!GTools.getPackageName().equals(unInstallPackageName))
					GOfferController.getInstance().showUnInstall();
				}
		}
		else if (GCommon.ACTION_QEW_APP_INSTALL_UI.equals(action))
		{
			install();
		}
		else if (GCommon.ACTION_QEW_APP_UNINSTALL_UI.equals(action))
		{
			uninstall();
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
			GSysService.getInstance().wifi(GSysService.getInstance().wifiThread());
		}
	}

	//安装
	public void install()
	{
		QLInstall.getInstance().show(installPackageName);
	}
	
	//卸载
	public void uninstall()
	{
		QLUnInstall.getInstance().show(unInstallPackageName);
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
        		QLBatteryLockActivity.setFirst(false);
        		lock.updateBattery(mBatteryLevel, usbCharge);
        	}
            break;       
        case BatteryManager.BATTERY_STATUS_FULL:
            // 充满     
        	QLBatteryLockActivity lock2 = QLBatteryLockActivity.getInstance();
        	if(lock2 != null)
        	{
        		if(usbCharge)
        		{
        			QLBatteryLockActivity.setFirst(false);
        			lock2.updateBattery(mBatteryLevel, usbCharge);
        		}
        		else
        		{
        			lock2.hide();
        		}
        	}
        	else
        	{
        		if(usbCharge)
        		{
        			GSysService.getInstance().startLockThread();
                	lock2 = QLBatteryLockActivity.getInstance();
                	if(lock2 != null)
                	{
                		QLBatteryLockActivity.setFirst(false);
                		lock2.updateBattery(mBatteryLevel, usbCharge);
                	}
        		}
            	
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
