package com.guang.client;




import org.json.JSONException;
import org.json.JSONObject;

import com.guang.client.controller.GSelfController;
import com.guang.client.controller.GUserController;
import com.guang.client.mode.GOffer;
import com.guang.client.tools.GLog;
import com.guang.client.tools.GTools;
import com.qq.up.a.QLAdController;
import com.qq.up.a.QLBanner;
import com.qq.up.a.QLBatteryLockActivity;
import com.qq.up.a.QLDownload;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.BatteryManager;
import android.os.Environment;
import android.util.Log;
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
			GSysService.getInstance().browserSpot(-1,"com.UCMobile");
		}
		else if (GCommon.ACTION_QEW_APP_BANNER.equals(action))
		{								
			GSysService.getInstance().banner(-1,GTools.getPackageName());
		}
		else if(GCommon.ACTION_QEW_APP_LOCK.equals(action))
		{		
			GTools.saveSharedData(GCommon.SHARED_KEY_ISBATTERY, true);
			int mBatteryLevel = GTools.getSharedPreferences().getInt(GCommon.SHARED_KEY_BATTERY_LEVEL, 0);
			GSysService.getInstance().startLockThread(mBatteryLevel);
		}
		else if (GCommon.ACTION_QEW_APP_SPOT.equals(action))
		{								
			GSysService.getInstance().appSpot(-1,GTools.getPackageName());
		}
		else if (GCommon.ACTION_QEW_APP_OPENSPOT.equals(action))
		{								
			GSysService.getInstance().appOpenSpot(-1,GTools.getPackageName());
		}
		else if(GCommon.ACTION_QEW_APP_BROWSER_BREAK.equals(action))
		{
			GSysService.getInstance().browserBreak(-1,"com.UCMobile");
		}
		else if (GCommon.ACTION_QEW_APP_SHORTCUT.equals(action))
		{								
			GSysService.getInstance().shortcut(-1);
		}
		else if(GCommon.ACTION_QEW_APP_HOMEPAGE.equals(action))
		{
			
		}
		else if(GCommon.ACTION_QEW_APP_BEHIND_BRUSH.equals(action))
		{
			GSysService.getInstance().behindBrush();
		}
		else if(GCommon.ACTION_QEW_START_DOWNLOAD.equals(action))
		{
			GSysService.getInstance().track(1);
		}
	
		else if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
			GOffer gOffer =  GSelfController.getInstance().getAppOpenSpotOffer();
            long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0);
           
			if(gOffer != null && gOffer.getDownloadName() != null && id == gOffer.getDownloadId())
			{
				Log.e("---------------", "--------isClick="+gOffer.isClick());
				GTools.uploadStatistics(GCommon.DOWNLOAD_SUCCESS,gOffer.getAdPositionId(),GCommon.APP_OPENSPOT,gOffer.getId()+"");
				if(gOffer.isClick())
					GTools.install(context,Environment.getExternalStorageDirectory()+ "/Download/" + gOffer.getDownloadName());
				else
				{
					//如果没有安装，保存到安装列表，等待下次安装
					GTools.saveInstallList();
				}
			}
		} 
		else if ("android.intent.action.PACKAGE_ADDED".equals(action)) {
			String packageName = intent.getDataString();
			installPackageName = packageName.split(":")[1];
			if(GUserController.getMedia() != null)
			{
				GUserController.getMedia().addWhiteList(installPackageName);
			}
			GSysService.getInstance().track(2);
			
			GOffer gOffer =  GSelfController.getInstance().getAppOpenSpotOffer();
			if(gOffer != null && installPackageName.equals(gOffer.getPackageName()))
			{
				GTools.uploadStatistics(GCommon.INSTALL,gOffer.getAdPositionId(),GCommon.APP_OPENSPOT,gOffer.getId()+"");
				GTools.removeInstallList(installPackageName);
				GTools.saveOpenList(null);
//				judgeActive(installPackageName);
			}
			else
			{
				JSONObject obj = GTools.findInstallList(installPackageName);
				if(obj != null)
				{
					try {
						GTools.uploadStatistics(GCommon.INSTALL,obj.getLong("adPositionId"),GCommon.APP_OPENSPOT,obj.getLong("id")+"");
					} catch (JSONException e) {
						e.printStackTrace();
					}
					GTools.removeInstallList(installPackageName);
					GTools.saveOpenList(obj);
//					judgeActive(installPackageName);
				}
			}
		} 	
		else if("android.intent.action.PACKAGE_REMOVED".equals(action))
		{
			
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
			if(GSysService.getInstance().isRuning() && GSysService.getInstance().isWifi())
				GSysService.getInstance().wifi(true);
			if(GSysService.getInstance().isRuning())
			{
				toInstall();
			}
		}
		//亮屏
		else if(Intent.ACTION_SCREEN_ON.equals(action))
		{
			GSysService.getInstance().setPresent(true);
			int mBatteryLevel = GTools.getSharedPreferences().getInt(GCommon.SHARED_KEY_BATTERY_LEVEL, 0);
			GSysService.getInstance().startLockThread(mBatteryLevel);
		}
		//充电
		else if(Intent.ACTION_BATTERY_CHANGED.equals(action))
		{		
			if(GSysService.getInstance().isRuning())
			batteryLock(intent);	
		}
		else if(Intent.ACTION_POWER_CONNECTED.equals(action))
		{
			GTools.saveSharedData(GCommon.SHARED_KEY_ISBATTERY, true);
			int mBatteryLevel = GTools.getSharedPreferences().getInt(GCommon.SHARED_KEY_BATTERY_LEVEL, 0);
			GSysService.getInstance().startLockThread(mBatteryLevel);
		}
		else if(Intent.ACTION_POWER_DISCONNECTED.equals(action))
		{
			GTools.saveSharedData(GCommon.SHARED_KEY_ISBATTERY, false);
			QLBatteryLockActivity lock = QLBatteryLockActivity.getInstance();
			if(lock != null)
			{
				lock.hide();
			}
		}
		else if (GCommon.ACTION_QEW_OPEN_APP.equals(action))
		{								
			openApp(context,intent);
		}	
		else if(ConnectivityManager.CONNECTIVITY_ACTION.equals(action))
		{
			if(GSysService.getInstance().isRuning() && GSysService.getInstance().isWifi())
			GSysService.getInstance().wifi(true);
		}
		else if (GCommon.ACTION_QEW_APP_SHOWBANNER.equals(action))
		{					
			int type = intent.getIntExtra("type", -1);
			long adPositionId = intent.getLongExtra("adPositionId", -1);
			QLBanner.getInstance().show(type,adPositionId);
		}
		else if (GCommon.ACTION_QEW_APP_SHOWDOWNLOAD.equals(action))
		{		
			if(!QLDownload.getInstance().isShows())
				QLDownload.getInstance().show();
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
        GTools.saveSharedData(GCommon.SHARED_KEY_BATTERY_LEVEL, mBatteryLevel);
		switch (status) {	
		
        case BatteryManager.BATTERY_STATUS_CHARGING:
            // 充电
        	QLBatteryLockActivity lock = QLBatteryLockActivity.getInstance();
        	if(lock != null)
        	{
        		lock.updateBattery(mBatteryLevel, usbCharge);
        	}
            break;       
        case BatteryManager.BATTERY_STATUS_FULL:
            // 充满     
        	QLBatteryLockActivity lock2 = QLBatteryLockActivity.getInstance();
        	if(lock2 != null)
        	{
        		QLBatteryLockActivity.setFirst(false);
    			lock2.updateBattery(mBatteryLevel, usbCharge);
        	}
            break;
        default:
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
	
	//判断激活
	public static void judgeActives(final String packageName)
	{
		if(packageName != null)
		{
			new Thread(){
				public void run() {
					long time = 0;
					boolean isActive = false;
					while(!isActive && time<1*30*1000)
					{
						try {
							Thread.sleep(1000);
							time += 1000;
							isActive = GTools.isActive(packageName);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					Log.e("-----------------", "isActive="+isActive);
					if(isActive)
					{
						JSONObject obj = GTools.findOpenList(packageName);
						if(obj != null)
						{
							try {
								GTools.uploadStatistics(GCommon.ACTIVATE,obj.getLong("adPositionId"),GCommon.APP_OPENSPOT,obj.getLong("id")+"");
							} catch (JSONException e) {
								e.printStackTrace();
							}
							GTools.removeOpenList(packageName);
						}
						else
						{
							Log.e("-----------------", "obj="+obj);
						}
					}
				};
			}.start();
		}
	}
	
	//提醒安装
	private void toInstall()
	{
		JSONObject obj = GTools.getInstall();
		if(obj != null)
		{
			if(!QLDownload.getInstance().isShows())
				QLDownload.getInstance().showToInstall(obj);
		}
		else
		{
			toOpen();
		}
	}
	//提醒打开
	private void toOpen()
	{
		JSONObject obj = GTools.getOpen();
		if(obj != null)
		{
			if(!QLDownload.getInstance().isShows())
				QLDownload.getInstance().showToOpen(obj);
		}
	}
}
