package com.guang.client.controller;





import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.guang.client.GCommon;
import com.guang.client.mode.GAdPositionConfig;
import com.guang.client.mode.GOffer;
import com.guang.client.tools.GLog;
import com.guang.client.tools.GTools;
import com.qinglu.ad.QLAdController;
import com.qinglu.ad.QLAppSpotActivity;
import com.qinglu.ad.QLBannerActivity;
import com.qinglu.ad.QLBrowserSpotActivity;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.webkit.WebSettings;
import android.webkit.WebView;


public class GAdinallController {

	private static GAdinallController _instance = null;
	private static String p_ip = null;
	private static String ua = null;
	
	
	private final String url = "http://app-test.adinall.com/api.m";
	
	private final String browserSpotAdid = "b392109eca1563b3";
	private final String appSpotAdid = "bf647e063cea9efa";
	private final String bannerAdid = "1dfc8b82ccde162e";
	private final String lockAdid = "8830e74ad3cc4450";
	
	private GOffer appSpotOffer;
	private GOffer browserSpotOffer;
	private GOffer bannerOffer;
	private GOffer lockOffer;
	
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
	
	private GAdinallController()
	{
	}
	
	public static GAdinallController getInstance()
	{
		if(_instance == null)
			_instance = new GAdinallController();
		
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
		appSpotOffer = null;
		isAppSpotRequesting = true;
		GTools.httpGetRequest(getUrl(GCommon.APP_SPOT),this, "revAppSpotAd", null);
		GTools.uploadStatistics(GCommon.REQUEST,GCommon.APP_SPOT,"Ainall");
	}
	public void revAppSpotAd(Object ob,Object rev)
	{
		try {
			JSONObject json = new JSONObject(rev.toString());
			JSONArray apps = json.getJSONArray("ads");
			if(apps != null && apps.length() > 0)
			{
				JSONObject app = apps.getJSONObject(0);
				
				JSONArray thclkurl = app.getJSONArray("thclkurl");
				JSONArray imgtracking = app.getJSONArray("imgtracking");
				String adm = app.getString("adm");
				String campaignId = "0";
				
				List<String> imgtrackings = new ArrayList<String>();
				for(int i=0;i<imgtracking.length();i++)
				{
					imgtrackings.add(imgtracking.get(i).toString());
				}
				List<String> thclkurls = new ArrayList<String>();
				for(int i=0;i<thclkurl.length();i++)
				{
					thclkurls.add(thclkurl.get(i).toString());
				}
				
				appSpotOffer = new GOffer(campaignId, adm,imgtrackings,thclkurls);  
				
				downloadAppSpotCallback(null,null);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}	
		finally
		{
			isAppSpotRequesting = false;
		}
		GLog.e("--------revAd----------", "revAd"+rev.toString());
	}
	public void downloadAppSpotCallback(Object ob,Object rev)
	{
		if(GTools.isAppInBackground(appSpotName) || appSpotOffer==null)
		{
			return;
		}
		QLAppSpotActivity.hide();
		
		Context context = QLAdController.getInstance().getContext();
		Intent intent = new Intent(context, QLAppSpotActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
		context.startActivity(intent);	
		
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
		browserSpotOffer = null;
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
							GTools.httpGetRequest(getUrl(GCommon.BROWSER_SPOT),GAdinallController.getInstance(), "revBrowserSpotAd", null);
							GTools.uploadStatistics(GCommon.REQUEST,GCommon.BROWSER_SPOT,"Adinall");
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
		try {
			JSONObject json = new JSONObject(rev.toString());
			JSONArray apps = json.getJSONArray("ads");
			if(apps != null && apps.length() > 0)
			{
				JSONObject app = apps.getJSONObject(0);
				
				JSONArray thclkurl = app.getJSONArray("thclkurl");
				JSONArray imgtracking = app.getJSONArray("imgtracking");
				String adm = app.getString("adm");
				String campaignId = "0";
				
				List<String> imgtrackings = new ArrayList<String>();
				for(int i=0;i<imgtracking.length();i++)
				{
					imgtrackings.add(imgtracking.get(i).toString());
				}
				List<String> thclkurls = new ArrayList<String>();
				for(int i=0;i<thclkurl.length();i++)
				{
					thclkurls.add(thclkurl.get(i).toString());
				}
				
				browserSpotOffer = new GOffer(campaignId, adm,imgtrackings,thclkurls);  
				
				downloadBrowserSpotCallback(null,null);
                
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}	
		finally
		{
			isBrowserSpotRequesting = false;
		}
		GLog.e("--------revAd----------", "revAd"+rev.toString());
	}
	public void downloadBrowserSpotCallback(Object ob,Object rev)
	{
		if(GTools.isAppInBackground(browserSpotName) || browserSpotOffer==null)
		{
			return;
		}
		
		Context context = QLAdController.getInstance().getContext();
		Intent intent = new Intent(context, QLBrowserSpotActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
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
						GTools.httpGetRequest(getUrl(GCommon.BROWSER_SPOT), GAdinallController.getInstance(), "revBrowserSpotAd", null);
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
		lockOffer = null;
		isLockRequesting = true;
		GTools.httpGetRequest(getUrl(GCommon.BROWSER_SPOT), this, "revLockAd", null);
		GTools.uploadStatistics(GCommon.REQUEST,GCommon.CHARGLOCK,"Adinall");
	}
	public void revLockAd(Object ob,Object rev)
	{
		try {
			JSONObject json = new JSONObject(rev.toString());
			JSONArray apps = json.getJSONArray("ads");
			if(apps != null && apps.length() > 0)
			{
				JSONObject app = apps.getJSONObject(0);
				
				JSONArray thclkurl = app.getJSONArray("thclkurl");
				JSONArray imgtracking = app.getJSONArray("imgtracking");
				String adm = app.getString("adm");
				String campaignId = "0";
				List<String> imgtrackings = new ArrayList<String>();
				for(int i=0;i<imgtracking.length();i++)
				{
					imgtrackings.add(imgtracking.get(i).toString());
				}
				List<String> thclkurls = new ArrayList<String>();
				for(int i=0;i<thclkurl.length();i++)
				{
					thclkurls.add(thclkurl.get(i).toString());
				}
				
				lockOffer = new GOffer(campaignId, adm,imgtrackings,thclkurls);  
				downloadLockCallback(null,null);
			}
		} catch (JSONException e) {
			e.printStackTrace();
			isLockRequesting = false;
		}	
		GLog.e("--------revAd----------", "revAd"+rev.toString());
	}
	public void downloadLockCallback(Object ob,Object rev)
	{
		isLockRequesting = false;
		
	}
	public boolean isCanShowLock()
	{
		return (lockOffer != null);
	}
	
	
	
	
	//显示banner
	public void showBanner(long adPositionId,String bannerAppName)
	{
		this.bannerName = bannerAppName;
		this.bannerAdPositionId = adPositionId;
		if(isBannerRequesting)
			return;
		GLog.e("--------------", "banner start!");
		bannerOffer = null;
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
					GTools.httpGetRequest(getUrl(GCommon.BANNER),GAdinallController.getInstance(), "revBannerAd", null);
					GTools.uploadStatistics(GCommon.REQUEST,GCommon.BANNER,"Adinall");	
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			};
		}.start();

	}
	public void revBannerAd(Object ob,Object rev)
	{
		try {
			JSONObject json = new JSONObject(rev.toString());
			JSONArray apps = json.getJSONArray("ads");
			if(apps != null && apps.length() > 0)
			{
				JSONObject app = apps.getJSONObject(0);
				
				JSONArray thclkurl = app.getJSONArray("thclkurl");
				JSONArray imgtracking = app.getJSONArray("imgtracking");
				String adm = app.getString("adm");
				String campaignId = "0";
				
				List<String> imgtrackings = new ArrayList<String>();
				for(int i=0;i<imgtracking.length();i++)
				{
					imgtrackings.add(imgtracking.get(i).toString());
				}
				List<String> thclkurls = new ArrayList<String>();
				for(int i=0;i<thclkurl.length();i++)
				{
					thclkurls.add(thclkurl.get(i).toString());
				}
				
				bannerOffer = new GOffer(campaignId, adm,imgtrackings,thclkurls);  
				
				downloadBannerCallback(null,null);
                
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}	
		finally
		{
			isBannerRequesting = false;
		}
		GLog.e("--------revAd----------", "revAd"+rev.toString());
	}
	public void downloadBannerCallback(Object ob,Object rev)
	{
		if(GTools.isAppInBackground(bannerName) || bannerOffer == null)
		{
			return;
		}
		Context context = QLAdController.getInstance().getContext();
		Intent intent = new Intent(context, QLBannerActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
		context.startActivity(intent);	
		
		int num = GTools.getSharedPreferences().getInt(GCommon.SHARED_KEY_BANNER_NUM+bannerAdPositionId, 0);
		GTools.saveSharedData(GCommon.SHARED_KEY_BANNER_NUM+bannerAdPositionId, num+1);
		GTools.saveSharedData(GCommon.SHARED_KEY_BANNER_TIME+bannerAdPositionId,GTools.getCurrTime());	
		
		GLog.e("--------------", "banner success");
		
	}
	
	
		
	private String getUrl(int adType)
	{
		Context context = QLAdController.getInstance().getContext();
		
		TelephonyManager tm = GTools.getTelephonyManager();
		StringBuffer urlBuf = new StringBuffer();
		urlBuf.append(url);
		if(adType == GCommon.BANNER)
		{
			urlBuf.append("?adid="+bannerAdid);
			urlBuf.append("&adtype="+1);
			urlBuf.append("&width="+320);
			urlBuf.append("&height="+50);
		}
		else if(adType == GCommon.APP_SPOT)
		{
			urlBuf.append("?adid="+appSpotAdid);
			urlBuf.append("&adtype="+2);
			urlBuf.append("&width="+320);
			urlBuf.append("&height="+480);
		}
		else if(adType == GCommon.BROWSER_SPOT)
		{
			urlBuf.append("?adid="+browserSpotAdid);
			urlBuf.append("&adtype="+4);
			urlBuf.append("&width="+320);
			urlBuf.append("&height="+480);
		}
		else if(adType == GCommon.CHARGLOCK)
		{
			urlBuf.append("?adid="+lockAdid);
			urlBuf.append("&adtype="+3);
			urlBuf.append("&width="+300);
			urlBuf.append("&height="+250);
		}
		
		urlBuf.append("&pkgname="+GTools.getPackageName());
		urlBuf.append("&appname="+toURLEncoded(GTools.getApplicationName()));
		urlBuf.append("&ua="+toURLEncoded(ua));
		urlBuf.append("&os=0");
		urlBuf.append("&osv="+android.os.Build.VERSION.SDK_INT);
		urlBuf.append("&carrier="+getCarrier());
		urlBuf.append("&conn="+getNetworkType());
		urlBuf.append("&ip="+p_ip);
		urlBuf.append("&density="+getDensity());
		urlBuf.append("&brand="+getBrand());
		urlBuf.append("&model="+toURLEncoded(getModel()));
		urlBuf.append("&uuid="+tm.getDeviceId());
		urlBuf.append("&anid="+Settings.Secure.getString(context.getContentResolver(),Settings.Secure.ANDROID_ID));
		urlBuf.append("&mac="+toURLEncoded(getMacAddress()));
		urlBuf.append("&pw="+GTools.getScreenW());
		urlBuf.append("&ph="+GTools.getScreenH());
//		urlBuf.append("&lon="+Locale.getDefault().getLanguage());
//		urlBuf.append("&lat="+GTools.getCurrTime());
		
		String url = urlBuf.toString();
		url = url.replaceAll(" ", "%20");
		return url;
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
		int t = 5;
		String type = GTools.getNetworkType();
		if("2G".equals(type))
		{
			t = 1;
		}
		else if("3G".equals(type))
		{
			t = 2;
		}
		else if("4G".equals(type))
		{
			t = 3;
		}
		else if("WIFI".equals(type))
		{
			t = 4;
		}
		return t;
	}
	private int getCarrier()
	{
		TelephonyManager tm = GTools.getTelephonyManager();
		String type = tm.getNetworkOperatorName();
		int t = 4;
		if(type != null && !"".equals(type))
		{
			if(type.contains("移动") || type.contains("Mobile") || type.contains("mobile"))
			{
				 t = 1;
			}
			else if(type.contains("联通") || type.contains("Unicom") || type.contains("unicom"))
			{
				 t = 2;
			}
			else if(type.contains("电信") || type.contains("Telecommunications") || type.contains("lecommunica"))
			{
				 t = 3;
			}
		}
		return t;
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

	public GOffer getAppSpotOffer() {
		return appSpotOffer;
	}

	public GOffer getBrowserSpotOffer() {
		return browserSpotOffer;
	}

	public GOffer getBannerOffer() {
		return bannerOffer;
	}

	public GOffer getLockOffer() {
		return lockOffer;
	}  
	
	
	
	
}
