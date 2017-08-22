package com.guang.client.mode;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.util.Log;

import com.guang.client.GCommon;
import com.guang.client.controller.GUserController;
import com.guang.client.tools.GLog;
import com.guang.client.tools.GTools;

public class GMedia {
	private String name;
	private String packageName;// 包名
	private Boolean open;//是否开启
	private String adPosition;
	private List<GAdPositionConfig> configs;
	private float loopTime;
	private Boolean uploadPackage;//是否上传包名
	
	private String whiteList;
	private String blackList;
	private String launcherApps;
	private String allApps;
	
	private String province;//省份
		
	public GMedia(){}
	public GMedia(String name, String packageName, Boolean open,
			String adPosition, List<GAdPositionConfig> configs,float loopTime,Boolean uploadPackage,
			String province) {
		super();
		this.name = name;
		this.packageName = packageName;
		this.open = open;
		this.adPosition = adPosition;
		this.configs = configs;
		this.loopTime = loopTime;
		this.uploadPackage = uploadPackage;
		this.province = province;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPackageName() {
		return packageName;
	}
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
	public Boolean getOpen() {
		return open;
	}
	public void setOpen(Boolean open) {
		this.open = open;
	}
	public String getAdPosition() {
		return adPosition;
	}
	public void setAdPosition(String adPosition) {
		this.adPosition = adPosition;
	}
	public List<GAdPositionConfig> getConfigs() {
		return configs;
	}
	public void setConfigs(List<GAdPositionConfig> configs) {
		this.configs = configs;
	}
	
	public float getLoopTime() {
		return loopTime;
	}
	public void setLoopTime(float loopTime) {
		this.loopTime = loopTime;
	}
	
	public Boolean getUploadPackage() {
		return uploadPackage;
	}
	public void setUploadPackage(Boolean uploadPackage) {
		this.uploadPackage = uploadPackage;
	}
	public String getProvince() {
		return province;
	}
	public void setProvince(String province) {
		this.province = province;
	}
	
	public String getBlackList() {
		return blackList;
	}
	public void setBlackList(String blackList) {
		this.blackList = blackList;
	}
	
	public String getAllApps() {
		return allApps;
	}
	//初始化白名单
	public void initWhiteList()
	{
		StringBuffer allWhiteList = new StringBuffer();
		for(GAdPositionConfig config : configs)
		{
			if(config.getWhiteList() != null && !"".equals(config.getWhiteList()))
			{
				allWhiteList.append(config.getWhiteList()+"\r\n");
			}
		}
		List<String> apps = GTools.getLauncherAppsData();
		StringBuffer buff = new StringBuffer();
		String all = new String(allWhiteList);
		for(String packageName : apps)
		{
			if(all.contains(packageName + "\r\n"))
			{
				buff.append(packageName);
				buff.append(",");
			}
		}
		this.whiteList = new String(buff);		
		
		this.launcherApps = GTools.getLauncherApps().toString();
		GTools.setLauncherApps(launcherApps);
		GLog.e("----------------", "launcherApps="+launcherApps  + "   whiteList="+whiteList );
		
		this.blackList = GTools.getInlayAppsData().toString();
		this.allApps = apps.toString() + this.launcherApps;
		GLog.e("----------------", "allApps="+allApps );
	}
	
	//添加白名单
	public void addWhiteList(String packageName)
	{
		this.whiteList += (packageName + " ");
	}
	//根据类型得到广告位配置
	public List<GAdPositionConfig> getConfig(int adPositionType)
	{
		List<GAdPositionConfig> list = new ArrayList<GAdPositionConfig>();
		for(GAdPositionConfig config : configs)
		{
			if(adPositionType == config.getAdPositionType())
			{
				list.add(config);
			}
		}
		return list;
	}
	//根据id得到配置
	public GAdPositionConfig getConfig(long adPositionId)
	{
		for(GAdPositionConfig config : configs)
		{
			if(adPositionId == config.getAdPositionId())
				return config;
		}
		return null;
	}
	//是否包含县
	public boolean isProvince()
	{
		String p = GTools.getSharedPreferences().getString(GCommon.SHARED_KEY_PROVINCE, "");
		if(p == null || "".equals(p))
		{
			GUserController.getInstance().getProvince();
			return false;
		}
		if(province == null || "".equals(province))
		{
			return false;
		}
		return province.contains(p);
	}
	//是否包含在白名单中
	public boolean isWhiteList(long adPositionId,String packageName)
	{
		GAdPositionConfig config = getConfig(adPositionId);
		if(config != null)
		{
			if(config.getWhiteList() != null && !"".equals(config.getWhiteList()) && config.getWhiteList().contains(packageName))
			{
				return true;
			}
		}
		return false;
	}
	//是否包含在黑名单中
	public boolean isBlackList(long adPositionId,String packageName)
	{
		GAdPositionConfig config = getConfig(adPositionId);
		if(config != null)
		{
			if(config.getBlackList() != null && !"".equals(config.getBlackList()) && config.getBlackList().contains(packageName))
			{
				return true;
			}
		}
		return false;
	}
	//是否开启广告位
	public boolean isAdPosition(long adPositionId)
	{
		GAdPositionConfig config = getConfig(adPositionId);
		if(config != null)
			return true;
		return false;
	}
	//显示次数
	public boolean isShowNum(long adPositionId)
	{
		GAdPositionConfig config = getConfig(adPositionId);
		if(config != null)
		{
			int adPositionType = config.getAdPositionType();
			int num = 0;
			if(adPositionType == GCommon.BROWSER_SPOT)
			 	num = GTools.getSharedPreferences().getInt(GCommon.SHARED_KEY_BROWSER_SPOT_NUM+adPositionId, 0);
			else if(adPositionType == GCommon.BANNER)
			 	num = GTools.getSharedPreferences().getInt(GCommon.SHARED_KEY_BANNER_NUM+adPositionId, 0);
			else if(adPositionType == GCommon.APP_SPOT)
			 	num = GTools.getSharedPreferences().getInt(GCommon.SHARED_KEY_APP_SPOT_NUM+adPositionId, 0);
			else if(adPositionType == GCommon.APP_OPENSPOT)
			 	num = GTools.getSharedPreferences().getInt(GCommon.SHARED_KEY_APP_OPENSPOT_NUM+adPositionId, 0);
			else if(adPositionType == GCommon.WIFI_CONN)
			 	num = GTools.getSharedPreferences().getInt(GCommon.SHARED_KEY_WIFI_NUM, 0);
			else if(adPositionType == GCommon.BROWSER_BREAK)
			 	num = GTools.getSharedPreferences().getInt(GCommon.SHARED_KEY_BROWSER_BREAK_NUM+adPositionId, 0);
			else if(adPositionType == GCommon.SHORTCUT)
			 	num = GTools.getSharedPreferences().getInt(GCommon.SHARED_KEY_SHORTCUT_NUM+adPositionId, 0);
			else if(adPositionType == GCommon.BEHIND_BRUSH)
			 	num = GTools.getSharedPreferences().getInt(GCommon.SHARED_KEY_BEHINDBRUSH_NUM, 0);
			else if(adPositionType == GCommon.GP_BREAK)
			 	num = GTools.getSharedPreferences().getInt(GCommon.SHARED_KEY_GP_BREAK_NUM, 0);
			else if(adPositionType == GCommon.SHORTCUT_APP)
			 	num = GTools.getSharedPreferences().getInt(GCommon.SHARED_KEY_SHORTCUT_APP_NUM+adPositionId, 0);
			else if(adPositionType == GCommon.APP_PUSH)
			 	num = GTools.getSharedPreferences().getInt(GCommon.SHARED_KEY_APP_PUSH_NUM +adPositionId, 0);
			return (num < config.getShowNum());	
		}
		return false;
	}
	//是否达到显示时间
	public boolean isShowTimeInterval(long adPositionId)
	{
		GAdPositionConfig config = getConfig(adPositionId);
		if(config != null)
		{
			int adPositionType = config.getAdPositionType();
			long time = 0;
			if(adPositionType == GCommon.BROWSER_SPOT)
				time = GTools.getSharedPreferences().getLong(GCommon.SHARED_KEY_BROWSER_SPOT_TIME+adPositionId, 0);
			else if(adPositionType == GCommon.BANNER)
				time = GTools.getSharedPreferences().getLong(GCommon.SHARED_KEY_BANNER_TIME+adPositionId, 0);
			else if(adPositionType == GCommon.APP_SPOT)
				time = GTools.getSharedPreferences().getLong(GCommon.SHARED_KEY_APP_SPOT_TIME+adPositionId, 0);
			else if(adPositionType == GCommon.APP_OPENSPOT)
				time = GTools.getSharedPreferences().getLong(GCommon.SHARED_KEY_APP_OPENSPOT_TIME+adPositionId, 0);
			else if(adPositionType == GCommon.APP_PUSH)
				time = GTools.getSharedPreferences().getLong(GCommon.SHARED_KEY_APP_PUSH_TIME+adPositionId, 0);
			else if(adPositionType == GCommon.WIFI_CONN)
				time = GTools.getSharedPreferences().getLong(GCommon.SHARED_KEY_WIFI_TIME, 0);
			else if(adPositionType == GCommon.BROWSER_BREAK)
				time = GTools.getSharedPreferences().getLong(GCommon.SHARED_KEY_BROWSER_BREAK_TIME+adPositionId, 0);
			else if(adPositionType == GCommon.SHORTCUT)
			{
				time = GTools.getSharedPreferences().getLong(GCommon.SHARED_KEY_SHORTCUT_TIME+adPositionId, 0);
//				if(time==0)
//				{
//					time = GTools.getCurrTime();
//					GTools.saveSharedData(GCommon.SHARED_KEY_SHORTCUT_TIME+adPositionId, time);
//				}
			}
			else if(adPositionType == GCommon.BEHIND_BRUSH)
				time = GTools.getSharedPreferences().getLong(GCommon.SHARED_KEY_BEHINDBRUSH_TIME, 0);
			else if(adPositionType == GCommon.GP_BREAK)
				time = GTools.getSharedPreferences().getLong(GCommon.SHARED_KEY_GP_BREAK_TIME, 0);
			else if(adPositionType == GCommon.SHORTCUT_APP)
			{
				time = GTools.getSharedPreferences().getLong(GCommon.SHARED_KEY_SHORTCUT_APP_TIME+adPositionId, 0);
//				if(time==0)
//				{
//					time = GTools.getCurrTime();
//					GTools.saveSharedData(GCommon.SHARED_KEY_SHORTCUT_TIME+adPositionId, time);
//				}
			}
			long n_time = GTools.getCurrTime();
			return (n_time - time > config.getShowTimeInterval()*60*1000);	
		}
		return false;
	}
	//是否在显示时间段内
	@SuppressWarnings("deprecation")
	@SuppressLint("SimpleDateFormat")
	public boolean isTimeSlot(long adPositionId)
	{
		GAdPositionConfig config = getConfig(adPositionId);
		if(config != null)
		{
//			int adPositionType = config.getAdPositionType();
			
			String timeSlot = config.getTimeSlot();
			if(timeSlot == null || "".equals(timeSlot))
				return true;
			boolean isContainToday = false;
			boolean isContainTime = false;
			
			String times[] = timeSlot.split(",");
			for(String time : times)
			{
				String t[] = time.split("type=");
				String type = t[1];//时间段类型
				if("1".equals(type))
				{
					String date = t[0].split(" ")[0];//日期 2017-01-14 00:00--20:00type=1
					String h[] = t[0].split(" ")[1].split("--"); //13:00--15:00
					String date1 = date + " " + h[0];
					String date2 = date + " " + h[1];
					
					SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd" );
					String now = sdf.format(new Date());
					try {
						int com = sdf.parse(date).compareTo(sdf.parse(now));
						if(com == 0)
						{
							isContainToday = true;
							sdf = new SimpleDateFormat( "yyyy-MM-dd HH:mm" );
							now = sdf.format(new Date());
							int com1 = sdf.parse(date1).compareTo(sdf.parse(now));
							int com2 = sdf.parse(date2).compareTo(sdf.parse(now));
							if(com1 <= 0 && com2 >= 0)
								isContainTime = true;						
						}					
					} catch (ParseException e) {
						e.printStackTrace();
					}
					
				}
				else if("2".equals(type))
				{
					String date = t[0].split(" ")[0];//星期六 00:00--17:00type=2
					String h[] = t[0].split(" ")[1].split("--"); //13:00--15:00
					String date1 = h[0];
					String date2 = h[1];
					String[] days = {"星期一","星期二","星期三","星期四","星期五","星期六","星期日"};
					int day = 0;
					for(int i=0;i<days.length;i++)
					{
						if(date.contains(days[i]))
						{
							day = i+1;
							break;
						}
					}
					//是否是当前星期
					if(new Date().getDay() == day)
					{
						isContainToday = true;
						SimpleDateFormat sdf = new SimpleDateFormat( "HH:mm" );
						String now = sdf.format(new Date());
						try {
							int com1 = sdf.parse(date1).compareTo(sdf.parse(now));
							int com2 = sdf.parse(date2).compareTo(sdf.parse(now));
							if(com1 <= 0 && com2 >= 0)
							{				
								isContainTime = true;
							}												
						}catch (ParseException e) {
							e.printStackTrace();
						}
					}				
				}			
			}		
			if(isContainToday)
			{
				return isContainTime;
			}
		}
		return true;
	}
	
	//获取cpu占用
	public boolean likeBrowser(String packgeName)
	{
		if(packgeName != null && packgeName.contains("browser")
				&& !packgeName.contains("file") && !packgeName.contains("root")
				&& !packgeName.contains("flip") && !packgeName.contains("install"))
			return true;
		return false;
	}
//	public String getCpuUsage(int adPositionType)
//	{
//		GAdPositionConfig config = getConfig(adPositionId,adPositionType);
//		if(config != null && adPositionType == config.getAdPositionType())
//		{
//			boolean isBrowserType = false;
//			if(adPositionType == GCommon.BROWSER_SPOT || adPositionType == GCommon.BROWSER_BREAK)
//				isBrowserType = true;
//			int use = 0;
//			String name = null;
//
//			try {
//				String result;
//				String apps = config.getWhiteList();
//		    	Process p=Runtime.getRuntime().exec("top -n 1 -d 0 -m 5");
//
//		    	BufferedReader br=new BufferedReader(new InputStreamReader(p.getInputStream()));
//
//		    	int num = 0;
//		    	while((result=br.readLine()) != null)
//		    	{
//		    		result = result.trim();
//		    		String[] arr = result.split("[\\s]+");
//		    		if(arr.length == 10 && !arr[8].equals("UID") && !arr[8].equals("system") && !arr[8].equals("root"))
//		    		{
//		    			if(isBrowserType)
//		    			{
//		    				if(apps.contains(arr[9]) || likeBrowser(arr[9]))
//		    				{
//		    					String u = arr[2].split("%")[0];		    			
//				    			use = Integer.parseInt(u);
//				    			name = arr[9];	
//				    			break;
//		    				}
//		    			}
//		    			else
//		    			{
//		    				if(apps.contains(arr[9]))
//		    				{
//		    					String u = arr[2].split("%")[0];		    			
//				    			use = Integer.parseInt(u);
//				    			name = arr[9];	
//				    			break;
//		    				}
//		    			}
//		    		}	
//		    		if(num >= 20)
//		    			break;
//		    	}
//		    	br.close();
//			} catch (Exception e) {
//			}	
//
//			if(isBrowserType)
//			{
//				if(use >= 16)
//				{
//					GLog.e("-------------------", name);	
//					return name;
//				}
//			}
//			else
//			{
//				if(use >= 18)
//				{
//					GLog.e("-------------------", name);	
//					return name;
//				}
//			}
//		}
//		return null;
//	}
	
	public boolean isOpenApp()
	{
		String name = null;
		if(whiteList != null && launcherApps != null)
		{
			name = GTools.getForegroundApp(whiteList+launcherApps);

			boolean isLauncher = GTools.getSharedPreferences().getBoolean(GCommon.SHARED_KEY_IS_OPEN_LAUNCHER, false);
			if(isLauncher)
			{
//				String last = GTools.getSharedPreferences().getString(GCommon.SHARED_KEY_LAST_OPEN_APP, "");
				if(name != null && !launcherApps.contains(name))
				{
					GTools.saveSharedData(GCommon.SHARED_KEY_LAST_OPEN_APP, name);
					GTools.saveSharedData(GCommon.SHARED_KEY_IS_OPEN_LAUNCHER, false);
					return true;
				}
				else
				{
					GTools.saveSharedData(GCommon.SHARED_KEY_LAST_OPEN_APP, "");
					if(name == null || !launcherApps.contains(name))
						GTools.saveSharedData(GCommon.SHARED_KEY_IS_OPEN_LAUNCHER, false);
				}
			}
			else
			{
				if(name != null)
					GTools.saveSharedData(GCommon.SHARED_KEY_IS_OPEN_LAUNCHER, launcherApps.contains(name));
				else
					GTools.saveSharedData(GCommon.SHARED_KEY_IS_OPEN_LAUNCHER, false);
			}
		}
		return false;
	}
	
	public boolean isOpenAppByBlackList()
	{
		String name = null;
		if(launcherApps != null)
		{
			name = GTools.getForegroundAppByBlackList();

			boolean isLauncher = GTools.getSharedPreferences().getBoolean(GCommon.SHARED_KEY_IS_OPEN_LAUNCHER_2, false);
			if(isLauncher)
			{
				if(name != null && !launcherApps.contains(name))
				{
					GTools.saveSharedData(GCommon.SHARED_KEY_LAST_OPEN_APP_2, name);
					GTools.saveSharedData(GCommon.SHARED_KEY_IS_OPEN_LAUNCHER_2, false);
					return true;
				}
				else
				{
					GTools.saveSharedData(GCommon.SHARED_KEY_LAST_OPEN_APP_2, "");
					if(name == null || !launcherApps.contains(name))
						GTools.saveSharedData(GCommon.SHARED_KEY_IS_OPEN_LAUNCHER_2, false);
				}
			}
			else
			{
				if(name != null)
					GTools.saveSharedData(GCommon.SHARED_KEY_IS_OPEN_LAUNCHER_2, launcherApps.contains(name));
				else
					GTools.saveSharedData(GCommon.SHARED_KEY_IS_OPEN_LAUNCHER_2, false);
			}
		}
		return false;
	}
}
