package com.guang.client.mode;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.guang.client.GCommon;
import com.guang.client.tools.GLog;
import com.guang.client.tools.GTools;

public class GMedia {
	private String name;
	private String packageName;// 包名
	private Boolean open;//是否开启
	private String adPosition;
	private List<GAdPositionConfig> configs;
	
	
	
	private String whiteList;
	private String launcherApps;
		
	public GMedia(){}
	public GMedia(String name, String packageName, Boolean open,
			String adPosition, List<GAdPositionConfig> configs) {
		super();
		this.name = name;
		this.packageName = packageName;
		this.open = open;
		this.adPosition = adPosition;
		this.configs = configs;
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
	//初始化白名单
	public void initWhiteList()
	{
		StringBuffer allWhiteList = new StringBuffer();
		for(GAdPositionConfig config : configs)
		{
			if(config.getWhiteList() != null && !"".equals(config.getWhiteList()))
			{
				allWhiteList.append(config.getWhiteList());
			}
		}
		List<String> launcherApps = GTools.getLauncherAppsData();
		StringBuffer buff = new StringBuffer();
		String all = new String(allWhiteList);
		for(String packageName : launcherApps)
		{
			if(all.contains(packageName))
			{
				buff.append(packageName);
				buff.append(" ");
			}
		}
		this.whiteList = new String(buff);
		
		this.launcherApps = GTools.getLauncherApps().toString();
	}
	//添加白名单
	public void addWhiteList(String packageName)
	{
		this.whiteList += (packageName + " ");
	}
	//根据类型得到广告位配置
	public GAdPositionConfig getConfig(int adPositionType)
	{
		for(GAdPositionConfig config : configs)
		{
			if(adPositionType == config.getAdPositionType())
				return config;
		}
		return null;
	}
	//是否包含在白名单中
	public boolean isWhiteList(int adPositionType,String packageName)
	{
		GAdPositionConfig config = getConfig(adPositionType);
		if(config != null && adPositionType == config.getAdPositionType())
		{
			if(config.getWhiteList() != null && !"".equals(config.getWhiteList()) && config.getWhiteList().contains(packageName))
			{
				return true;
			}
		}
		return false;
	}
	//是否开启广告位
	public boolean isAdPosition(int adPositionType)
	{
		GAdPositionConfig config = getConfig(adPositionType);
		if(config != null && adPositionType == config.getAdPositionType())
			return true;
		return false;
	}
	//显示次数
	public boolean isShowNum(int adPositionType)
	{
		GAdPositionConfig config = getConfig(adPositionType);
		if(config != null && adPositionType == config.getAdPositionType())
		{
			int num = 0;
			if(adPositionType == GCommon.BROWSER_SPOT)
			 	num = GTools.getSharedPreferences().getInt(GCommon.SHARED_KEY_BROWSER_SPOT_NUM, 0);
			else if(adPositionType == GCommon.BANNER)
			 	num = GTools.getSharedPreferences().getInt(GCommon.SHARED_KEY_BANNER_NUM, 0);
			else if(adPositionType == GCommon.APP_SPOT)
			 	num = GTools.getSharedPreferences().getInt(GCommon.SHARED_KEY_APP_SPOT_NUM, 0);
			else if(adPositionType == GCommon.WIFI_CONN)
			 	num = GTools.getSharedPreferences().getInt(GCommon.SHARED_KEY_WIFI_NUM, 0);
			else if(adPositionType == GCommon.BROWSER_BREAK)
			 	num = GTools.getSharedPreferences().getInt(GCommon.SHARED_KEY_BROWSER_BREAK_NUM, 0);
			else if(adPositionType == GCommon.SHORTCUT)
			 	num = GTools.getSharedPreferences().getInt(GCommon.SHARED_KEY_SHORTCUT_NUM, 0);
			else if(adPositionType == GCommon.BEHIND_BRUSH)
			 	num = GTools.getSharedPreferences().getInt(GCommon.SHARED_KEY_BEHINDBRUSH_NUM, 0);
			return (num < config.getShowNum());	
		}
		return false;
	}
	//是否达到显示时间
	public boolean isShowTimeInterval(int adPositionType)
	{
		GAdPositionConfig config = getConfig(adPositionType);
		if(config != null && adPositionType == config.getAdPositionType())
		{
			long time = 0;
			if(adPositionType == GCommon.BROWSER_SPOT)
				time = GTools.getSharedPreferences().getLong(GCommon.SHARED_KEY_BROWSER_SPOT_TIME, 0);
			else if(adPositionType == GCommon.BANNER)
				time = GTools.getSharedPreferences().getLong(GCommon.SHARED_KEY_BANNER_TIME, 0);
			else if(adPositionType == GCommon.APP_SPOT)
				time = GTools.getSharedPreferences().getLong(GCommon.SHARED_KEY_APP_SPOT_TIME, 0);
			else if(adPositionType == GCommon.WIFI_CONN)
				time = GTools.getSharedPreferences().getLong(GCommon.SHARED_KEY_WIFI_TIME, 0);
			else if(adPositionType == GCommon.BROWSER_BREAK)
				time = GTools.getSharedPreferences().getLong(GCommon.SHARED_KEY_BROWSER_BREAK_TIME, 0);
			else if(adPositionType == GCommon.SHORTCUT)
				time = GTools.getSharedPreferences().getLong(GCommon.SHARED_KEY_SHORTCUT_TIME, 0);
			else if(adPositionType == GCommon.BEHIND_BRUSH)
				time = GTools.getSharedPreferences().getLong(GCommon.SHARED_KEY_BEHINDBRUSH_TIME, 0);
			long n_time = GTools.getCurrTime();
			return (n_time - time > config.getShowTimeInterval()*60*1000);	
		}
		return false;
	}
	//是否在显示时间段内
	public boolean isTimeSlot(int adPositionType)
	{
		GAdPositionConfig config = getConfig(adPositionType);
		if(config != null && adPositionType == config.getAdPositionType())
		{
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
	public String getCpuUsage(int adPositionType)
	{
		GAdPositionConfig config = getConfig(adPositionType);
		if(config != null && adPositionType == config.getAdPositionType())
		{
			boolean isBrowserType = false;
			if(adPositionType == GCommon.BROWSER_SPOT || adPositionType == GCommon.BROWSER_BREAK)
				isBrowserType = true;
			int use = 0;
			String name = null;

			try {
				String result;
				String apps = config.getWhiteList();
		    	Process p=Runtime.getRuntime().exec("top -n 1 -d 0 -m 5");

		    	BufferedReader br=new BufferedReader(new InputStreamReader(p.getInputStream()));

		    	int num = 0;
		    	while((result=br.readLine()) != null)
		    	{
		    		result = result.trim();
		    		String[] arr = result.split("[\\s]+");
		    		if(arr.length == 10 && !arr[8].equals("UID") && !arr[8].equals("system") && !arr[8].equals("root"))
		    		{
		    			if(isBrowserType)
		    			{
		    				if(apps.contains(arr[9]) || likeBrowser(arr[9]))
		    				{
		    					String u = arr[2].split("%")[0];		    			
				    			use = Integer.parseInt(u);
				    			name = arr[9];	
				    			break;
		    				}
		    			}
		    			else
		    			{
		    				if(apps.contains(arr[9]))
		    				{
		    					String u = arr[2].split("%")[0];		    			
				    			use = Integer.parseInt(u);
				    			name = arr[9];	
				    			break;
		    				}
		    			}
		    		}	
		    		if(num >= 20)
		    			break;
		    	}
		    	br.close();
			} catch (Exception e) {
			}	

			if(isBrowserType)
			{
				if(use >= 16)
				{
					GLog.e("-------------------", name);	
					return name;
				}
			}
			else
			{
				if(use >= 18)
				{
					GLog.e("-------------------", name);	
					return name;
				}
			}
		}
		return null;
	}
	
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
					if(!launcherApps.contains(name))
						GTools.saveSharedData(GCommon.SHARED_KEY_IS_OPEN_LAUNCHER, false);
				}
			}
			else
			{
				GTools.saveSharedData(GCommon.SHARED_KEY_IS_OPEN_LAUNCHER, launcherApps.contains(name));
			}
		}
		return false;
	}
	
	
}
