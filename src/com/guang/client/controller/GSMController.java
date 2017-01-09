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

import com.guang.client.mode.GSMOffer;
import com.guang.client.tools.GLog;
import com.guang.client.tools.GTools;
import com.qinglu.ad.QLAdController;
import com.qinglu.ad.QLBrowserBreakActivity;
import com.qinglu.ad.QLNotifyActivity;

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
		if(offer != null && !offer.isFinished())
			return;
		if(p_ip == null)
		{
			getNetIp();
			return;
		}
		GTools.httpGetRequest(getUrl(dim_320x50), this, "revBannerAd", null);
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
		GLog.e("--------revAd----------", "revAd"+rev.toString());
	}
	public void downloadBannerCallback(Object ob,Object rev)
	{
		offer.setFinished(true);
		Context context = QLAdController.getInstance().getContext();
		Intent intent = new Intent(context, QLNotifyActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
		context.startActivity(intent);	
	}
	
	public void showSpot(String browserName)
	{
		this.browserName = browserName;
		if(offer != null && !offer.isFinished())
			return;
		if(p_ip == null)
		{
			getNetIp();
			return;
		}
		long nflow = GTools.getAppFlow(browserName);
		if(flow != 0)
		{
			if(nflow - flow > 100*1024)
			{
				flow = nflow;
				GTools.httpGetRequest(getUrl(dim_320x480), this, "revSpotAd", null);
			}
		}
		else
		{
			flow = nflow;
		}
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
		GLog.e("--------revAd----------", "revAd"+rev.toString());
	}
	public void downloadSpotCallback(Object ob,Object rev)
	{
		offer.setFinished(true);
		//判断是否在应用界面
		if(GTools.isAppInBackground(browserName))
		{
			GLog.e("------------------", "AppInBackground="+browserName);
			return;
		}
		Context context = QLAdController.getInstance().getContext();
		Intent intent = new Intent(context, QLBrowserBreakActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
		context.startActivity(intent);	
		
		//如果没有退出浏览器，一段时间后继续弹出广告
		final String packageName = browserName;
		final long time = 10*1000;
		
		new Thread(){
			long currTime = time;
			public void run() {
				while(currTime>0)
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
					if(nflow - flow > 100*1024)
					{
						showSpot(packageName);
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
