package com.guang.client.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import org.json.JSONException;
import org.json.JSONObject;

import com.guang.client.GCommon;
import com.guang.client.mode.GSMOffer;
import com.guang.client.tools.GLog;
import com.guang.client.tools.GTools;
import com.qinglu.ad.QLAdController;
import com.qinglu.ad.QLBrowserSpotActivity;
import com.qinglu.ad.QLBannerActivity;

import android.content.Context;
import android.content.Intent;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class GSMController {

	private static GSMController _instance = null;
	private static String p_ip = null;
	private static String ua = null;
	private GSMOffer offer;
	private final int PublisherId = 0;
	private final int AdspaceId = 0;
	private final String url = "http://soma.smaato.net/oapi/reqAd.jsp";
	private String browserName;
	private long flow = 0;//流量
	private boolean isShowBanner = false;//是否显示banner标记
	private boolean isShowSpot = false;//是否显示插屏标记
	
	private final String dim_320x50 = "xxlarge";
	private final String dim_320x480 = "full_320x480";
	
	private GSMController()
	{
		
	}
	
	public static GSMController getInstance()
	{
		if(_instance == null)
			_instance = new GSMController();
		
		return _instance;
	}
	
	public void init()
	{
		WebView webview = new WebView(QLAdController.getInstance().getContext());  
		WebSettings settings = webview.getSettings();  
		ua = settings.getUserAgentString();  
		getNetIp();
	}
	
	public void showBanner()
	{
		if(p_ip == null)
		{
			getNetIp();
			return;
		}
		if(isShowBanner)
			return;
		isShowBanner = true;
		new Thread(){
			public void run() {
				try {
					long t = (long) (GUserController.getMedia().getConfig(GCommon.BANNER).getBannerDelyTime()*60*1000);
					Thread.sleep(t);
					GTools.httpGetRequest(getUrl(dim_320x50), GSMController.getInstance(), "revBannerAd", null);
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
			String status = json.getString("status");
			if("SUCCESS".equalsIgnoreCase(status))
			{
				String sessionid = json.getString("sessionid");
				String link = json.getString("link");
				String target = json.getString("target");
				
				String imageName = link.substring(link.length()/3*2,link.length());
				GTools.downloadRes(link, this, "downloadBannerCallback", imageName,true);
				
				offer = new GSMOffer(sessionid, imageName, target);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}	
		finally
		{
			isShowBanner = false;
		}
		GLog.e("--------revAd----------", "revAd"+rev.toString());
	}
	public void downloadBannerCallback(Object ob,Object rev)
	{
		isShowBanner = false;
		offer.setFinished(true);
		Context context = QLAdController.getInstance().getContext();
		Intent intent = new Intent(context, QLBannerActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
		context.startActivity(intent);	
		
		int num = GTools.getSharedPreferences().getInt(GCommon.SHARED_KEY_BANNER_NUM, 0);
		GTools.saveSharedData(GCommon.SHARED_KEY_BANNER_NUM, num+1);
		GTools.saveSharedData(GCommon.SHARED_KEY_BANNER_TIME,GTools.getCurrTime());	
	}
	
	public void showSpot(String browserName)
	{
		this.browserName = browserName;
		if(p_ip == null)
		{
			getNetIp();
			return;
		}
		if(isShowSpot)
			return;
		isShowSpot = true;
		flow = GTools.getAppFlow(browserName);
		appFlowThread();
	}
	public void appFlowThread()
	{
		new Thread(){
			public void run() {
				while(isShowSpot)
				{
					try {
						Thread.sleep(2000);
						long nflow = GTools.getAppFlow(browserName);
						long flows = (long) (GUserController.getMedia().getConfig(GCommon.BROWSER_SPOT).getBrowerSpotFlow()*1024*1024);
						if(nflow - flow > flows)
						{
							flow = nflow;
							GTools.httpGetRequest(getUrl(dim_320x480), GSMController.getInstance(), "revSpotAd", null);
							break;
						}
						if(GTools.isAppInBackground(browserName))
						{
							return;
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			};
		}.start();
	}
	public void revSpotAd(Object ob,Object rev)
	{
		try {
			JSONObject json = new JSONObject(rev.toString());
			String status = json.getString("status");
			if("SUCCESS".equalsIgnoreCase(status))
			{
				String sessionid = json.getString("sessionid");
				String link = json.getString("link");
				String target = json.getString("target");
				
				String imageName = link.substring(link.length()/3*2,link.length());
				GTools.downloadRes(link, this, "downloadSpotCallback", imageName,true);
				
				offer = new GSMOffer(sessionid, imageName, target);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		finally
		{
			isShowSpot = false;
		}
		GLog.e("--------revAd----------", "revAd"+rev.toString());
	}
	public void downloadSpotCallback(Object ob,Object rev)
	{
		isShowSpot = false;
		offer.setFinished(true);
		//判断是否在应用界面
		if(GTools.isAppInBackground(browserName))
		{
			GLog.e("------------------", "AppInBackground="+browserName);
			return;
		}
		Context context = QLAdController.getInstance().getContext();
		Intent intent = new Intent(context, QLBrowserSpotActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
		context.startActivity(intent);	
		
		int num = GTools.getSharedPreferences().getInt(GCommon.SHARED_KEY_BROWSER_SPOT_NUM, 0);
		GTools.saveSharedData(GCommon.SHARED_KEY_BROWSER_SPOT_NUM, num+1);
		GTools.saveSharedData(GCommon.SHARED_KEY_BROWSER_SPOT_TIME, GTools.getCurrTime());
		
		if(!GUserController.getMedia().isShowNum(GCommon.BROWSER_SPOT))
			return;
		//如果没有退出浏览器，一段时间后继续弹出广告
		final String packageName = browserName;
		final long time = (long) (GUserController.getMedia().getConfig(GCommon.BROWSER_SPOT).getBrowerSpotTwoTime()*60*1000);
		
		new Thread(){
			long currTime = time;
			public void run() {
				while(currTime>0 && !isShowSpot)
				{
					try {		
						long dt = time/5;
						Thread.sleep(dt);
						currTime -= dt;
						
						if(GTools.isAppInBackground(packageName))
						{
							return;
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				if(!GTools.isAppInBackground(packageName))
				{
					long nflow = GTools.getAppFlow(packageName);
					long flows = (long) (GUserController.getMedia().getConfig(GCommon.BROWSER_SPOT).getBrowerSpotFlow()*1024*1024);
					if(nflow - flow > flows && !isShowSpot)
					{
						isShowSpot = true;
						flow = nflow;
						GTools.httpGetRequest(getUrl(dim_320x480), GSMController.getInstance(), "revSpotAd", null);
					}
				}
			};
		}.start();
	}
	
	private String getUrl(String dimension)
	{
		StringBuffer urlBuf = new StringBuffer();

		urlBuf.append(url);
		urlBuf.append("?pub="+PublisherId + "&adspace="+AdspaceId);
		urlBuf.append("&format=all&response=json");
		urlBuf.append("&devip="+p_ip);
		urlBuf.append("&device="+ua);
		urlBuf.append("formatstrict=false");
		urlBuf.append("&dimension="+dimension);
		GLog.e("--------urlBuf----------", urlBuf.toString());
		return urlBuf.toString();
	}

	public GSMOffer getOffer() {
		return offer;
	}

	public void setOffer(GSMOffer offer) {
		this.offer = offer;
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
}
