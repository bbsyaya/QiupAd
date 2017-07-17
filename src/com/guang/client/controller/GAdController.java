package com.guang.client.controller;





import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.guang.client.GCommon;
import com.guang.client.mode.GAdPositionConfig;
import com.guang.client.mode.GAds;
import com.guang.client.tools.GLog;
import com.guang.client.tools.GTools;
import com.qq.up.a.QLAdController;
import com.qq.up.a.QLBanner;
import com.qq.up.a.QLBrowserSpotActivity;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;


public class GAdController {

	private static GAdController _instance = null;
	private static String p_ip = null;
	private static String ua = null;
	
	
	private final String url = GCommon.URI_GET_OFFLINE_OFFER;
	
	private final String appId = "754";
	
	private final String browserSpotAdid = "20170707151620754";
	private final String appSpotAdid = "20170707151620754";
	private final String bannerAdid = "20170707152337754";
	private final String lockAdid = "20170707151620754";
	
	private GAds appSpotAd;
	private GAds browserSpotAd;
	private GAds bannerAd;
	private GAds lockAd;
	
	private boolean isAppSpotRequesting = false;
	private boolean isBrowserSpotRequesting = false;
	private boolean isBannerRequesting = false;
	private boolean isLockRequesting = false;
	
	private String appSpotName;
	private String browserSpotName;
	private String bannerName;
	private String lockName;
	
	private long appSpotAdPositionId;
	private long browserSpotAdPositionId;
	private long bannerAdPositionId;
	private long lockAdPositionId;
	
	private long flow = 0;//流量
		
	private boolean bannerTwo;
	
	private GAdController()
	{
	}
	
	public static GAdController getInstance()
	{
		if(_instance == null)
			_instance = new GAdController();
		
		return _instance;
	}
	
	public void init()
	{
		WebView webview = new WebView(QLAdController.getInstance().getContext());  
		WebSettings settings = webview.getSettings();  
		ua = settings.getUserAgentString();  
		getNetIp();
	}
	
	//显示应用插屏
	public void showAppSpot(long adPositionId,String name)
	{
		if(p_ip == null)
		{
			getNetIp();
			return;
		}
		this.appSpotName = name;
		this.appSpotAdPositionId = adPositionId;
		if(isAppSpotRequesting)
			return;
		GLog.e("--------------", "app spot start!");
		appSpotAd = null;
		isAppSpotRequesting = true;
		GLog.e("--------------", getUrl(GCommon.APP_SPOT));
		new Thread(){
			public void run() {
				try {
					long t = (long) (GUserController.getMedia().getConfig(appSpotAdPositionId).getAppSpotDelyTime()*60*1000);
					GLog.e("---------------------------", "app spot sleep="+t);
					Thread.sleep(t);
					if(GTools.isAppInBackground(appSpotName))
					{
						isAppSpotRequesting = false;
						return;
					}
					GLog.e("---------------------------", "Request app spot");					
					GTools.httpPostRequest(url,GAdController.getInstance(), "revAppSpotAd", getUrl(GCommon.APP_SPOT));
					GTools.uploadStatistics(GCommon.REQUEST,appSpotAdPositionId,GCommon.APP_SPOT,"kuxian",-1);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			};
		}.start();

	}
	public void revAppSpotAd(Object ob,Object rev)
	{
		GLog.e("--------revAd----------", "revAd"+rev.toString());
		try {
				JSONObject json = new JSONObject(rev.toString());
				if(json.getInt("retcode") == 0)
				{
					JSONArray ads = json.getJSONArray("ads");
					if(ads != null && ads.length() > 0)
					{
						int r = (int) (Math.random()*10%ads.length());
						JSONObject ad = ads.getJSONObject(r);
						appSpotAd = new GAds();
						appSpotAd.init(ad);
						appSpotAd.setAdPositionId(appSpotAdPositionId);
					}
				}
			
				if(appSpotAd != null && appSpotAd.getCreative() != null)
				{
					String url = appSpotAd.getCreative().get(0).getInteraction().getUrl();
					if(url != null && url.length() > 10)
					{
						downloadAppSpotCallback(null,null);
//						url = url.substring(url.length()/5*3, url.length()-1);
//						if(GUserController.getInstance().isAdNum(url, appSpotAdPositionId))
//						{
//							
//						}
					}
				}
						
		} catch (JSONException e) {
			e.printStackTrace();
		}	
		finally
		{
			isAppSpotRequesting = false;
		}
	}
	public void downloadAppSpotCallback(Object ob,Object rev)
	{
		if(GTools.isAppInBackground(appSpotName) || appSpotAd==null)
		{
			return;
		}
//		QLAppSpotActivity.hide();
//		
//		Context context = QLAdController.getInstance().getContext();
//		Intent intent = new Intent(context, QLAppSpotActivity.class);
//		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//		intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
//		intent.putExtra("type", 1);
//		intent.putExtra("actype", "spot");
//		context.startActivity(intent);	
		
		Context context = QLAdController.getInstance().getContext();
		Intent intent = new Intent();  
		intent.setAction(GCommon.ACTION_QEW_APP_SHOWAPPSPOT);  
		intent.putExtra("type", 1);
		context.sendBroadcast(intent); 
		
		int num = GTools.getSharedPreferences().getInt(GCommon.SHARED_KEY_APP_SPOT_NUM+appSpotAdPositionId, 0);
		GTools.saveSharedData(GCommon.SHARED_KEY_APP_SPOT_NUM+appSpotAdPositionId, num+1);
		GTools.saveSharedData(GCommon.SHARED_KEY_APP_SPOT_TIME+appSpotAdPositionId,GTools.getCurrTime());
		GLog.e("--------------", "app spot success!");
	}
	
	
	
	
	//显示浏览器插屏
	public void showBrowserSpot(long adPositionId,String name)
	{
		if(p_ip == null)
		{
			getNetIp();
			return;
		}
		this.browserSpotName = name;
		this.browserSpotAdPositionId = adPositionId;
		if(isBrowserSpotRequesting)
			return;
		browserSpotAd = null;
		isBrowserSpotRequesting = true;
		flow = GTools.getAppFlow(browserSpotName);
		appFlowThread();
		GLog.e("--------------", "browser spot start");
	}
	public void appFlowThread()
	{
		new Thread(){
			public void run() {
				while(isBrowserSpotRequesting)
				{
					try {
						Thread.sleep(2000);
						long nflow = GTools.getAppFlow(browserSpotName);
						long flows = (long) (GUserController.getMedia().getConfig(browserSpotAdPositionId).getBrowerSpotFlow()*1024*1024);
						if(nflow - flow > flows)
						{
							flow = nflow;
							GTools.httpPostRequest(url,GAdController.getInstance(), "revBrowserSpotAd", getUrl(GCommon.BROWSER_SPOT));
							GTools.uploadStatistics(GCommon.REQUEST,browserSpotAdPositionId,GCommon.BROWSER_SPOT,"kuxian",-1);
							break;
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			};
		}.start();
	}

	public void revBrowserSpotAd(Object ob,Object rev)
	{
//		GLog.e("--------revAd----------", "revAd"+rev.toString());
		try {
			
			JSONObject json = new JSONObject(rev.toString());
			if(json.getInt("retcode") == 0)
			{
				JSONArray ads = json.getJSONArray("ads");
				if(ads != null && ads.length() > 0)
				{
					int r = (int) (Math.random()*10%ads.length());
					JSONObject ad = ads.getJSONObject(r);
					browserSpotAd = new GAds();
					browserSpotAd.init(ad);
					browserSpotAd.setAdPositionId(browserSpotAdPositionId);
					
					if(browserSpotAd != null && browserSpotAd.getCreative() != null)
					{
						String url = browserSpotAd.getCreative().get(0).getInteraction().getUrl();
						if(url != null && url.length() > 10)
						{
							downloadBrowserSpotCallback(null,null);
//							url = url.substring(url.length()/5*3, url.length()-1);
//							if(GUserController.getInstance().isAdNum(url, browserSpotAdPositionId))
//							{
//								
//							}
						}
					}
				}
			}
			
		} catch (JSONException e) {
			e.printStackTrace();
		}	
		finally
		{
			isBrowserSpotRequesting = false;
		}
	}
	public void downloadBrowserSpotCallback(Object ob,Object rev)
	{
		if(GTools.isAppInBackground(browserSpotName) || browserSpotAd==null)
		{
			return;
		}
		
		Context context = QLAdController.getInstance().getContext();
		Intent intent = new Intent(context, QLBrowserSpotActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
		intent.putExtra("type", 1);
		context.startActivity(intent);	
		
		int num = GTools.getSharedPreferences().getInt(GCommon.SHARED_KEY_BROWSER_SPOT_NUM+browserSpotAdPositionId, 0);
		GTools.saveSharedData(GCommon.SHARED_KEY_BROWSER_SPOT_NUM+browserSpotAdPositionId, num+1);
		GTools.saveSharedData(GCommon.SHARED_KEY_BROWSER_SPOT_TIME+browserSpotAdPositionId, GTools.getCurrTime());
		
		GLog.e("--------------", "browser spot success");
		
		
		if(!GUserController.getMedia().isShowNum(browserSpotAdPositionId))
			return;
		//如果没有退出浏览器，一段时间后继续弹出广告
		final String packageName = browserSpotName;
		flow = GTools.getAppFlow(browserSpotName);
		final long time = (long) (GUserController.getMedia().getConfig(browserSpotAdPositionId).getBrowerSpotTwoTime()*60*1000);
		
		new Thread(){
			long currTime = time;
			public void run() {
				while(currTime>0 && !isBrowserSpotRequesting)
				{
					try {		
						long dt = time/5;
						Thread.sleep(dt);
						currTime -= dt;
						
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				if(!GTools.isAppInBackground(packageName))
				{
					long nflow = GTools.getAppFlow(packageName);
					long flows = (long) (GUserController.getMedia().getConfig(browserSpotAdPositionId).getBrowerSpotFlow()*1024*1024);
					if(nflow - flow > flows && !isBrowserSpotRequesting)
					{
						isBrowserSpotRequesting = true;
						flow = nflow;
						GTools.httpPostRequest(url, GAdController.getInstance(), "revBrowserSpotAd", getUrl(GCommon.BROWSER_SPOT));
					}
				}
			};
		}.start();
	}
	
	
	
	//显示充电锁
	public void showLock()
	{
		if(p_ip == null)
		{
			getNetIp();
			return;
		}
		
		if(isLockRequesting)
			return;
		GLog.e("--------------", "lock start!");
		List<GAdPositionConfig> list = GUserController.getMedia().getConfig(GCommon.CHARGLOCK);
		for(GAdPositionConfig config : list)
		{
			lockAdPositionId = config.getAdPositionId();
		}
		lockAd = null;
		isLockRequesting = true;
		GTools.httpPostRequest(url, this, "revLockAd", getUrl(GCommon.CHARGLOCK));
		GTools.uploadStatistics(GCommon.REQUEST,lockAdPositionId,GCommon.CHARGLOCK,"kuxian",-1);
	}
	public void revLockAd(Object ob,Object rev)
	{
//		GLog.e("--------revAd----------", "revAd"+rev.toString());
		try {
			JSONObject json = new JSONObject(rev.toString());
			if(json.getInt("retcode") == 0)
			{
				JSONArray ads = json.getJSONArray("ads");
				if(ads != null && ads.length() > 0)
				{
					int r = (int) (Math.random()*10%ads.length());
					JSONObject ad = ads.getJSONObject(r);
					lockAd = new GAds();
					lockAd.init(ad);
					lockAd.setAdPositionId(lockAdPositionId);
					
					if(lockAd != null && lockAd.getCreative() != null)
					{
						String url = lockAd.getCreative().get(0).getInteraction().getUrl();
						if(url != null && url.length() > 10)
						{
							downloadLockCallback(null,null);
//							url = url.substring(url.length()/5*3, url.length()-1);
//							if(GUserController.getInstance().isAdNum(url, lockAdPositionId))
//							{
//								
//							}
						}
					}
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
			isLockRequesting = false;
		}	
	}
	public void downloadLockCallback(Object ob,Object rev)
	{
		isLockRequesting = false;
		
	}
	public boolean isCanShowLock()
	{
		return (lockAd != null);
	}
	
	
	
	
	//显示banner
	public void showBanner(long adPositionId,String bannerAppName)
	{
		this.bannerName = bannerAppName;
		this.bannerAdPositionId = adPositionId;
		if(isBannerRequesting)
			return;
		GLog.e("--------------", "banner start!");
		bannerAd = null;
		isBannerRequesting = true;
		
		new Thread(){
			public void run() {
				try {
					long t = (long) (GUserController.getMedia().getConfig(bannerAdPositionId).getBannerDelyTime()*60*1000);
					GLog.e("---------------------------", "banner sleep="+t);
					Thread.sleep(t);
					if(GTools.isAppInBackground(bannerName))
					{
						isBannerRequesting = false;
						return;
					}
					GLog.e("---------------------------", "Request banner");
					GTools.httpPostRequest(url,GAdController.getInstance(), "revBannerAd", getUrl(GCommon.BANNER));
					GTools.uploadStatistics(GCommon.REQUEST,bannerAdPositionId,GCommon.BANNER,"kuxian",-1);	
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			};
		}.start();

	}
	public void revBannerAd(Object ob,Object rev)
	{
//		GLog.e("--------revAd----------", "revAd"+rev.toString());
		try {
			JSONObject json = new JSONObject(rev.toString());
			if(json.getInt("retcode") == 0)
			{
				JSONArray ads = json.getJSONArray("ads");
				if(ads != null && ads.length() > 0)
				{
					int r = (int) (Math.random()*10%ads.length());
					JSONObject ad = ads.getJSONObject(r);
					bannerAd = new GAds();
					bannerAd.init(ad);
					bannerAd.setAdPositionId(bannerAdPositionId);
					
					if(bannerAd != null && bannerAd.getCreative() != null)
					{
						String url = bannerAd.getCreative().get(0).getInteraction().getUrl();
						if(url != null && url.length() > 10)
						{
							downloadBannerCallback(null,null);
//							url = url.substring(url.length()/5*3, url.length()-1);
//							if(GUserController.getInstance().isAdNum(url, bannerAdPositionId))
//							{
//								
//							}
						}
					}
				}
			}
				
		} catch (JSONException e) {
			e.printStackTrace();
		}	
		finally
		{
			isBannerRequesting = false;
		}
	}
	public void downloadBannerCallback(Object ob,Object rev)
	{
		if(GTools.isAppInBackground(bannerName) || bannerAd == null || QLBanner.getInstance().isShowing())
		{
			return;
		}
//		Context context = QLAdController.getInstance().getContext();
//		Intent intent = new Intent(context, QLBannerActivity.class);
//		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//		intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
//		intent.putExtra("type", 1);
//		context.startActivity(intent);	
		
		Context context = QLAdController.getInstance().getContext();
		Intent intent = new Intent();  
		intent.setAction(GCommon.ACTION_QEW_APP_SHOWBANNER);  
		intent.putExtra("type", 1);
		intent.putExtra("adPositionId", bannerAdPositionId);
		context.sendBroadcast(intent); 
		
		int num = GTools.getSharedPreferences().getInt(GCommon.SHARED_KEY_BANNER_NUM+bannerAdPositionId, 0);
		GTools.saveSharedData(GCommon.SHARED_KEY_BANNER_NUM+bannerAdPositionId, num+1);
		GTools.saveSharedData(GCommon.SHARED_KEY_BANNER_TIME+bannerAdPositionId,GTools.getCurrTime());	
		
		GLog.e("--------------", "banner success");
		
		
		new Thread(){
			public void run() {
				try {
					if(bannerTwo)
					{
						bannerTwo = false;
						return;
					}
					bannerTwo = true;
					if(isBannerRequesting)
						return;
					GLog.e("--------------", "banner two start!");
					bannerAd = null;
					isBannerRequesting = true;
					
					long t = (long) (GUserController.getMedia().getConfig(bannerAdPositionId).getBannerTwoDelyTime()*60*1000);
					GLog.e("---------------------------", "banner two sleep="+t);
					Thread.sleep(t);
					if(GTools.isAppInBackground(bannerName))
					{
						isBannerRequesting = false;
						return;
					}
					GLog.e("---------------------------", "Request banner two");
					GTools.httpPostRequest(url,GAdController.getInstance(), "revBannerAd", getUrl(GCommon.BANNER));
					GTools.uploadStatistics(GCommon.REQUEST,bannerAdPositionId,GCommon.BANNER,"kuxian",-1);	
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			};
		}.start();
	}
	
	
		
	private String getUrl(int adType)
	{
		Context context = QLAdController.getInstance().getContext();
		
		TelephonyManager tm = GTools.getTelephonyManager();
		
		JSONObject data = new JSONObject();
		JSONObject app = new JSONObject();
		JSONArray addes = new JSONArray();
		JSONObject addesobj = new JSONObject();
		JSONObject device = new JSONObject();
		JSONObject user = new JSONObject();
		try {
			app.put("app_id", appId);
			app.put("sdk_channel", "tcsdjz");
			data.put("app", app);
			
			if(adType == GCommon.BANNER)
			{
				addesobj.put("ad_id", bannerAdid);
			}
			else if(adType == GCommon.APP_SPOT)
			{
				addesobj.put("ad_id", appSpotAdid);
			}	
			else if(adType == GCommon.BROWSER_SPOT)
			{
				addesobj.put("ad_id", browserSpotAdid);
			}	
			else if(adType == GCommon.CHARGLOCK)
			{
				addesobj.put("ad_id", lockAdid);
			}	
			addesobj.put("impression_num", 5);
			addes.put(addesobj);
			data.put("addes", addes);
			
			device.put("screen_width", GTools.getScreenW());
			device.put("screen_height", GTools.getScreenH());
			device.put("os_type", 2);
			device.put("imei", tm.getDeviceId());
			device.put("device_type", 2);
			device.put("androidid", Settings.Secure.getString(context.getContentResolver(),Settings.Secure.ANDROID_ID));
			device.put("model",  getModel());
			device.put("hardware", android.os.Build.HARDWARE);
			device.put("display", android.os.Build.DISPLAY);
			device.put("total_rom", GTools.getRom()+"");
			device.put("board", android.os.Build.BOARD);
			device.put("total_ram", GTools.getRam()+"");
			device.put("product", android.os.Build.PRODUCT);
			device.put("manufacturer", android.os.Build.MANUFACTURER);
			device.put("device", android.os.Build.DEVICE);
			device.put("brand", android.os.Build.BRAND);
			device.put("carrierId", getCarrier());
			
			data.put("device", device);
			
			user.put("ip", p_ip);
			user.put("imsi", tm.getSubscriberId());
			user.put("networktype", getNetworkType());
			user.put("lang", context.getResources().getConfiguration().locale.toString());
			
			data.put("user", user);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return data.toString();
	}
	
	public  String getDevid()
	{
		String id = "";
		for(int i=0;i<15;i++)
		{
			int r = (int) (Math.random()*10);
			id += r;
		}
		return id;
	}
	
	public String getMd5(long time,String did)
	{
		String token = appId + did + 0 + getCarrier() + GTools.getPackageName()+time;
		try {
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			md5.update(token.getBytes());  
	        byte[] m = md5.digest();//加密  
	        String result = "";
	        for (byte b : m) {
	            String temp = Integer.toHexString(b & 0xff);
	            if (temp.length() == 1) {
	                temp = "0" + temp;
	            }
	            result += temp;
	        }
	        token = result;
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}          
		return token;
	}

	public  String toURLEncoded(String paramString) {  
        if (paramString == null || paramString.equals("")) {  
            return "";  
        }  
          
        try  
        {  
            String str = new String(paramString.getBytes(), "UTF-8");  
            str = URLEncoder.encode(str, "UTF-8");  
            return str;  
        }  
        catch (Exception localException)  
        {  
        }  
          
        return "";  
    }  


	
	private int getNetworkType()
	{
		String type = GTools.getNetworkType();
		if("WIFI".equals(type))
			return 1;
		return 0;
	}
	private String getCarrier()
	{
		TelephonyManager tm = GTools.getTelephonyManager();
		String type = tm.getSubscriberId();
		if(type != null && type.length() > 4)
			return type.substring(0, 5);
		return "";
	}
	
	private float getDensity() {  
		Context context = QLAdController.getInstance().getContext();
	    DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();  
	    return displayMetrics.density; 
	}  
	private String getBrand()
	{
		String brand = Build.MANUFACTURER;
		if(brand != null && !"".equals(brand))
		{
			brand = brand.toLowerCase();
		}
		return brand;
	}
	private String getModel()
	{
		String model = android.os.Build.MODEL;
		if(model != null && !"".equals(model))
		{
			model = model.toLowerCase();
		}
		return model;
	}
	private String getMacAddress() {
		Context context = QLAdController.getInstance().getContext();
	    final WifiManager wm = (WifiManager) context.getSystemService(Service.WIFI_SERVICE);
	    // 如果本次开机后打开过WIFI，则能够直接获取到mac信息。立刻返回数据。
	    WifiInfo info = wm.getConnectionInfo();
	    if (info != null && info.getMacAddress() != null) {
	      
	      return info.getMacAddress();
	    }
	    return "";
	}
	
	public void getNetIp(){    
		new Thread(){
			public void run() {
				URL infoUrl = null;    
			    InputStream inStream = null;    
			    try {    
			        infoUrl = new URL("http://1212.ip138.com/ic.asp");    
			        URLConnection connection = infoUrl.openConnection();    
			        HttpURLConnection httpConnection = (HttpURLConnection)connection;  
			        httpConnection.setConnectTimeout(60*1000);
			        int responseCode = httpConnection.getResponseCode();  
			        if(responseCode == HttpURLConnection.HTTP_OK)    
			        {        
			            inStream = httpConnection.getInputStream();       
			            BufferedReader reader = new BufferedReader(new InputStreamReader(inStream,"gb2312"));    
			            StringBuilder strber = new StringBuilder();    
			            String line = null;    
			            while ((line = reader.readLine()) != null)     
			                strber.append(line );    
			            inStream.close(); 
			            String ips = strber.toString();
			            if(ips != null)
			            {
			            	 int start = ips.indexOf("[");
		                     int end = ips.indexOf("]");
					         p_ip =  ips.substring(start+1, end);    
					         GLog.e("--------getNetIp----------", "getNetIp="+p_ip);
			            }
			        } 
			        else
			        {
			        	GLog.e("--------getNetIp----------", "responseCode="+responseCode);
			        }
			    } catch (IOException e) {  
			        e.printStackTrace();    
			    }    
			};
		}.start();   
	}


	public GAds getAppSpotAd() {
		return appSpotAd;
	}

	public void setAppSpotAd(GAds appSpotAd) {
		this.appSpotAd = appSpotAd;
	}

	public GAds getBrowserSpotAd() {
		return browserSpotAd;
	}

	public void setBrowserSpotAd(GAds browserSpotAd) {
		this.browserSpotAd = browserSpotAd;
	}

	public GAds getBannerAd() {
		return bannerAd;
	}

	public void setBannerAd(GAds bannerAd) {
		this.bannerAd = bannerAd;
	}

	public GAds getLockAd() {
		return lockAd;
	}

	public void setLockAd(GAds lockAd) {
		this.lockAd = lockAd;
	}

	
	
	
	
}
