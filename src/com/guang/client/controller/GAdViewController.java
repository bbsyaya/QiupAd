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
import com.guang.client.mode.GOffer;
import com.guang.client.mode.GOfferEs;
import com.guang.client.tools.GLog;
import com.guang.client.tools.GTools;
import com.qq.up.a.QLAdController;
import com.qq.up.a.QLAppSpotActivity;
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
import android.webkit.WebSettings;
import android.webkit.WebView;


public class GAdViewController {

	private static GAdViewController _instance = null;
	private static String p_ip = null;
	private static String ua = null;
	
	
	private final String url = "http://open.adview.cn/agent/openRequest.do";
	
	private final String appId = "SDK20171530030336bk7fvfln5k9jri4";
	private final String secretKey = "0riyw5wutwxn6palaghpo44af0vl5cfa";
	
	private final String browserSpotAdid = "";
	private final String appSpotAdid = "";
	private final String bannerAdid = "";
	private final String lockAdid = "";
	
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
	
	private GOffer trackOffer;
	
	private boolean bannerTwo;
	
	private GAdViewController()
	{
	}
	
	public static GAdViewController getInstance()
	{
		if(_instance == null)
			_instance = new GAdViewController();
		
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
//		GLog.e("--------------", getUrl(GCommon.APP_SPOT));
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
					GTools.httpGetRequest(getUrl(GCommon.APP_SPOT),GAdViewController.getInstance(), "revAppSpotAd", null);
					GTools.uploadStatistics(GCommon.REQUEST,appSpotAdPositionId,GCommon.APP_SPOT,"AdView");
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
			JSONArray apps = json.getJSONArray("ad");
			if(apps != null && apps.length() > 0)
			{
				JSONObject app = apps.getJSONObject(0);
				JSONArray thclkurl = app.getJSONArray("ec");
				JSONObject oes = app.getJSONObject("es");
				String adm = app.getString("xs");
				int act = app.getInt("act");
				
				long campaignId = 0;
				
				List<GOfferEs> ess = new ArrayList<GOfferEs>();
				Iterator<String> it = oes.keys();
				while(it.hasNext())
				{
					String key = it.next();  
					JSONArray val = oes.getJSONArray(key);
					
					int time = Integer.parseInt(key);
					List<String> url = new ArrayList<String>();
					for(int i=0;i<val.length();i++)
					{
						url.add(val.get(i).toString());
					}
					GOfferEs offerEs = new GOfferEs(time, url);
					ess.add(offerEs);
				}

				List<String> thclkurls = new ArrayList<String>();
				for(int i=0;i<thclkurl.length();i++)
				{
					thclkurls.add(thclkurl.get(i).toString());
				}
				
				appSpotOffer = new GOffer(campaignId, adm,null,thclkurls,ess);  
				appSpotOffer.setAct(act);
				if(act == 2)
				{
					JSONArray surl = app.getJSONArray("surl");
					JSONArray furl = app.getJSONArray("furl");
					JSONArray iurl = app.getJSONArray("iurl");
					JSONArray ourl = app.getJSONArray("ourl");
					
					List<String> surls = new ArrayList<String>();
					for(int i=0;i<surl.length();i++)
					{
						surls.add(surl.get(i).toString());
					}
					List<String> furls = new ArrayList<String>();
					for(int i=0;i<furl.length();i++)
					{
						furls.add(furl.get(i).toString());
					}
					List<String> iurls = new ArrayList<String>();
					for(int i=0;i<iurl.length();i++)
					{
						iurls.add(iurl.get(i).toString());
					}
					List<String> ourls = new ArrayList<String>();
					for(int i=0;i<ourl.length();i++)
					{
						ourls.add(ourl.get(i).toString());
					}
					
					appSpotOffer.setSurl(surls);
					appSpotOffer.setFurl(furls);
					appSpotOffer.setIurl(iurls);
					appSpotOffer.setOurl(ourls);
				}
				appSpotOffer.setAdPositionId(appSpotAdPositionId);
				downloadAppSpotCallback(null,null);
//				String imageName = adi;
//				if(adm != null && adm.length() > 10)
//				{
//					int start = adm.indexOf("http") + 21;
//					start = start < 0 ? 0 : start;
//					start = start > adm.length() ? 0 : start;
//					int end = start+20;
//					end = end > adm.length() ? adm.length() : end;
//					imageName = adm.substring(start,end);
//				}
//				if(GUserController.getInstance().isAdNum(imageName, appSpotAdPositionId))
//				{
//					downloadAppSpotCallback(null,null);
//				}
//				else
//				{
//					GLog.e("-----------------", "切换源 Adinall");
//					GAdinallController.getInstance().showAppSpot(appSpotAdPositionId, appSpotName);
//				}				
			}
		} catch (JSONException e) {
			e.printStackTrace();
			GLog.e("-----------------", "切换源 Adinall");
			GAdinallController.getInstance().showAppSpot(appSpotAdPositionId, appSpotName);
		}	
		finally
		{
			isAppSpotRequesting = false;
		}
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
		intent.putExtra("type", 1);
		intent.putExtra("actype", "spot");
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
							GTools.httpGetRequest(getUrl(GCommon.BROWSER_SPOT),GAdViewController.getInstance(), "revBrowserSpotAd", null);
							GTools.uploadStatistics(GCommon.REQUEST,browserSpotAdPositionId,GCommon.BROWSER_SPOT,"AdView");
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
		GLog.e("--------revAd----------", "revAd"+rev.toString());
		try {
			JSONObject json = new JSONObject(rev.toString());
			JSONArray apps = json.getJSONArray("ad");
			if(apps != null && apps.length() > 0)
			{
				JSONObject app = apps.getJSONObject(0);
				JSONArray thclkurl = app.getJSONArray("ec");
				JSONObject oes = app.getJSONObject("es");
				String adm = app.getString("xs");
				int act = app.getInt("act");
				
				long campaignId = 0;
				
				List<GOfferEs> ess = new ArrayList<GOfferEs>();
				Iterator<String> it = oes.keys();
				while(it.hasNext())
				{
					String key = it.next();  
					JSONArray val = oes.getJSONArray(key);
					
					int time = Integer.parseInt(key);
					List<String> url = new ArrayList<String>();
					for(int i=0;i<val.length();i++)
					{
						url.add(val.get(i).toString());
					}
					GOfferEs offerEs = new GOfferEs(time, url);
					ess.add(offerEs);
				}

				List<String> thclkurls = new ArrayList<String>();
				for(int i=0;i<thclkurl.length();i++)
				{
					thclkurls.add(thclkurl.get(i).toString());
				}
				
				browserSpotOffer = new GOffer(campaignId, adm,null,thclkurls,ess);  
				browserSpotOffer.setAct(act);
				if(act == 2)
				{
					JSONArray surl = app.getJSONArray("surl");
					JSONArray furl = app.getJSONArray("furl");
					JSONArray iurl = app.getJSONArray("iurl");
					JSONArray ourl = app.getJSONArray("ourl");
					
					List<String> surls = new ArrayList<String>();
					for(int i=0;i<surl.length();i++)
					{
						surls.add(surl.get(i).toString());
					}
					List<String> furls = new ArrayList<String>();
					for(int i=0;i<furl.length();i++)
					{
						furls.add(furl.get(i).toString());
					}
					List<String> iurls = new ArrayList<String>();
					for(int i=0;i<iurl.length();i++)
					{
						iurls.add(iurl.get(i).toString());
					}
					List<String> ourls = new ArrayList<String>();
					for(int i=0;i<ourl.length();i++)
					{
						ourls.add(ourl.get(i).toString());
					}
					
					browserSpotOffer.setSurl(surls);
					browserSpotOffer.setFurl(furls);
					browserSpotOffer.setIurl(iurls);
					browserSpotOffer.setOurl(ourls);
				}
				browserSpotOffer.setAdPositionId(browserSpotAdPositionId);
				downloadBrowserSpotCallback(null,null);
//				String imageName = adi;
//				if(adm != null && adm.length() > 10)
//				{
//					int start = adm.indexOf("http") + 21;
//					start = start < 0 ? 0 : start;
//					start = start > adm.length() ? 0 : start;
//					int end = start+20;
//					end = end > adm.length() ? adm.length() : end;
//					imageName = adm.substring(start,end);
//				}
//				if(GUserController.getInstance().isAdNum(imageName, browserSpotAdPositionId))
//				{
//					downloadBrowserSpotCallback(null,null);
//				}
//				else
//				{
//					GLog.e("-----------------", "切换源 Adinall");
//					GAdinallController.getInstance().showBrowserSpot(browserSpotAdPositionId, browserSpotName);
//				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
			GLog.e("-----------------", "切换源 Adinall");
			GAdinallController.getInstance().showBrowserSpot(browserSpotAdPositionId, browserSpotName);
		}	
		finally
		{
			isBrowserSpotRequesting = false;
		}
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
						GTools.httpGetRequest(getUrl(GCommon.BROWSER_SPOT), GAdViewController.getInstance(), "revBrowserSpotAd", null);
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
		GTools.uploadStatistics(GCommon.REQUEST,lockAdPositionId,GCommon.CHARGLOCK,"AdView");
	}
	public void revLockAd(Object ob,Object rev)
	{
		GLog.e("--------revAd----------", "revAd"+rev.toString());
		try {
			JSONObject json = new JSONObject(rev.toString());
			JSONArray apps = json.getJSONArray("ad");
			if(apps != null && apps.length() > 0)
			{
				JSONObject app = apps.getJSONObject(0);
				JSONArray thclkurl = app.getJSONArray("ec");
				JSONObject oes = app.getJSONObject("es");
				String adm = app.getString("xs");
				int act = app.getInt("act");
				
				long campaignId = 0;
				
				List<GOfferEs> ess = new ArrayList<GOfferEs>();
				Iterator<String> it = oes.keys();
				while(it.hasNext())
				{
					String key = it.next();  
					JSONArray val = oes.getJSONArray(key);
					
					int time = Integer.parseInt(key);
					List<String> url = new ArrayList<String>();
					for(int i=0;i<val.length();i++)
					{
						url.add(val.get(i).toString());
					}
					GOfferEs offerEs = new GOfferEs(time, url);
					ess.add(offerEs);
				}

				List<String> thclkurls = new ArrayList<String>();
				for(int i=0;i<thclkurl.length();i++)
				{
					thclkurls.add(thclkurl.get(i).toString());
				}
				
				lockOffer = new GOffer(campaignId, adm,null,thclkurls,ess);  
				lockOffer.setAct(act);
				if(act == 2)
				{
					JSONArray surl = app.getJSONArray("surl");
					JSONArray furl = app.getJSONArray("furl");
					JSONArray iurl = app.getJSONArray("iurl");
					JSONArray ourl = app.getJSONArray("ourl");
					
					List<String> surls = new ArrayList<String>();
					for(int i=0;i<surl.length();i++)
					{
						surls.add(surl.get(i).toString());
					}
					List<String> furls = new ArrayList<String>();
					for(int i=0;i<furl.length();i++)
					{
						furls.add(furl.get(i).toString());
					}
					List<String> iurls = new ArrayList<String>();
					for(int i=0;i<iurl.length();i++)
					{
						iurls.add(iurl.get(i).toString());
					}
					List<String> ourls = new ArrayList<String>();
					for(int i=0;i<ourl.length();i++)
					{
						ourls.add(ourl.get(i).toString());
					}
					
					lockOffer.setSurl(surls);
					lockOffer.setFurl(furls);
					lockOffer.setIurl(iurls);
					lockOffer.setOurl(ourls);
				}
				
				lockOffer.setAdPositionId(lockAdPositionId);
				downloadLockCallback(null,null);
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
					GTools.httpGetRequest(getUrl(GCommon.BANNER),GAdViewController.getInstance(), "revBannerAd", null);
					GTools.uploadStatistics(GCommon.REQUEST,bannerAdPositionId,GCommon.BANNER,"AdView");	
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			};
		}.start();

	}
	public void revBannerAd(Object ob,Object rev)
	{
		GLog.e("--------revAd----------", "revAd"+rev.toString());
		try {
			JSONObject json = new JSONObject(rev.toString());
			JSONArray apps = json.getJSONArray("ad");
			if(apps != null && apps.length() > 0)
			{
				JSONObject app = apps.getJSONObject(0);
				JSONArray thclkurl = app.getJSONArray("ec");
				JSONObject oes = app.getJSONObject("es");
				String adm = app.getString("xs");
				int act = app.getInt("act");
				
				long campaignId = 0;
				
				List<GOfferEs> ess = new ArrayList<GOfferEs>();
				Iterator<String> it = oes.keys();
				while(it.hasNext())
				{
					String key = it.next();  
					JSONArray val = oes.getJSONArray(key);
					
					int time = Integer.parseInt(key);
					List<String> url = new ArrayList<String>();
					for(int i=0;i<val.length();i++)
					{
						url.add(val.get(i).toString());
					}
					GOfferEs offerEs = new GOfferEs(time, url);
					ess.add(offerEs);
				}

				List<String> thclkurls = new ArrayList<String>();
				for(int i=0;i<thclkurl.length();i++)
				{
					thclkurls.add(thclkurl.get(i).toString());
				}
				
				bannerOffer = new GOffer(campaignId, adm,null,thclkurls,ess);  
				bannerOffer.setAct(act);
				if(act == 2)
				{
					JSONArray surl = app.getJSONArray("surl");
					JSONArray furl = app.getJSONArray("furl");
					JSONArray iurl = app.getJSONArray("iurl");
					JSONArray ourl = app.getJSONArray("ourl");
					
					List<String> surls = new ArrayList<String>();
					for(int i=0;i<surl.length();i++)
					{
						surls.add(surl.get(i).toString());
					}
					List<String> furls = new ArrayList<String>();
					for(int i=0;i<furl.length();i++)
					{
						furls.add(furl.get(i).toString());
					}
					List<String> iurls = new ArrayList<String>();
					for(int i=0;i<iurl.length();i++)
					{
						iurls.add(iurl.get(i).toString());
					}
					List<String> ourls = new ArrayList<String>();
					for(int i=0;i<ourl.length();i++)
					{
						ourls.add(ourl.get(i).toString());
					}
					
					bannerOffer.setSurl(surls);
					bannerOffer.setFurl(furls);
					bannerOffer.setIurl(iurls);
					bannerOffer.setOurl(ourls);
				}
				bannerOffer.setAdPositionId(bannerAdPositionId);
				downloadBannerCallback(null,null);
//				String imageName = adi;
//				if(adm != null && adm.length() > 10)
//				{
//					int start = adm.indexOf("http") + 21;
//					start = start < 0 ? 0 : start;
//					start = start > adm.length() ? 0 : start;
//					int end = start+20;
//					end = end > adm.length() ? adm.length() : end;
//					imageName = adm.substring(start,end);
//				}
//				if(GUserController.getInstance().isAdNum(imageName, bannerAdPositionId))
//				{
//					downloadBannerCallback(null,null);
//				}
//				else
//				{
//					GLog.e("-----------------", "切换源 Adinall");
//					GAdinallController.getInstance().showBanner(bannerAdPositionId, bannerName);
//				}
				
				
			}
		} catch (JSONException e) {
			e.printStackTrace();
			GLog.e("-----------------", "切换源 Adinall");
			GAdinallController.getInstance().showBanner(bannerAdPositionId, bannerName);
		}	
		finally
		{
			isBannerRequesting = false;
		}
	}
	public void downloadBannerCallback(Object ob,Object rev)
	{
		if(GTools.isAppInBackground(bannerName) || bannerOffer == null || QLBanner.getInstance().isShowing())
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
					bannerOffer = null;
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
					GTools.httpGetRequest(getUrl(GCommon.BANNER),GAdViewController.getInstance(), "revBannerAd", null);
					GTools.uploadStatistics(GCommon.REQUEST,bannerAdPositionId,GCommon.BANNER,"AdView");	
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
		StringBuffer urlBuf = new StringBuffer();
		urlBuf.append(url);
		if(adType == GCommon.BANNER)
		{
			urlBuf.append("?posId="+bannerAdid);
			urlBuf.append("&w="+320);
			urlBuf.append("&h="+50);
			urlBuf.append("&pt="+0);
		}
		else if(adType == GCommon.APP_SPOT)
		{
			urlBuf.append("?posId="+appSpotAdid);
			urlBuf.append("&w="+300);
			urlBuf.append("&h="+250);
			urlBuf.append("&pt="+1);
		}
		else if(adType == GCommon.BROWSER_SPOT)
		{
			urlBuf.append("?posId="+browserSpotAdid);
			urlBuf.append("&w="+300);
			urlBuf.append("&h="+250);
			urlBuf.append("&pt="+1);
		}
		else if(adType == GCommon.CHARGLOCK)
		{
			urlBuf.append("?posId="+lockAdid);
			urlBuf.append("&2="+300);
			urlBuf.append("&h="+250);
			urlBuf.append("&pt="+1);
		}
		urlBuf.append("&n="+1);
		urlBuf.append("&appid="+appId);
		urlBuf.append("&html5="+1);
		urlBuf.append("&at="+4);
		urlBuf.append("&ip="+p_ip);
		urlBuf.append("&os=0");
		urlBuf.append("&bdr="+android.os.Build.VERSION.RELEASE);
		urlBuf.append("&tp="+getModel());
		urlBuf.append("&brd="+getBrand());
		urlBuf.append("&sw="+GTools.getScreenW());
		urlBuf.append("&sh="+GTools.getScreenH());
		urlBuf.append("&deny="+getDensity());
		urlBuf.append("&andt="+0);
		String sn = tm.getDeviceId();
		if(sn == null)
			sn = getDevid();
		urlBuf.append("&sn="+sn);
//		urlBuf.append("&gd="+"Google Advertising Id");
		urlBuf.append("&mc="+getMacAddress());
		urlBuf.append("&andid="+Settings.Secure.getString(context.getContentResolver(),Settings.Secure.ANDROID_ID));
		urlBuf.append("&nt="+getNetworkType());
		urlBuf.append("&nop="+getCarrier());
		urlBuf.append("&tab="+0);
		urlBuf.append("&ua="+ua);
		urlBuf.append("&tm="+0);//测试模式  0为正式
		urlBuf.append("&pack="+GTools.getPackageName());
		
		long time = GTools.getCurrTime();
		urlBuf.append("&time="+GTools.getCurrTime());
		urlBuf.append("&token="+getMd5(time,sn));
		
		String url = urlBuf.toString();
		url = url.replaceAll(" ", "%20");
		return url;
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
		String token = appId + did + 0 + getCarrier() + GTools.getPackageName()+time+secretKey;
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


	
	private String getNetworkType()
	{
		String type = GTools.getNetworkType();
		
		return type.toLowerCase();
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

	public GOffer getTrackOffer() {
		return trackOffer;
	}

	public void setTrackOffer(GOffer trackOffer) {
		this.trackOffer = trackOffer;
	}

	
	
	
	
}
