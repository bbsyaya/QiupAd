package com.guang.client.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.guang.client.GCommon;
import com.guang.client.mode.GOffer;
import com.guang.client.mode.GSMOffer;
import com.guang.client.tools.GLog;
import com.guang.client.tools.GTools;
import com.qinglu.ad.QLAdController;
import com.qinglu.ad.QLAppSpotActivity;
import com.qinglu.ad.QLBrowserSpotActivity;
import com.qinglu.ad.QLBannerActivity;

import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class GAPPNextController {

	private static GAPPNextController _instance = null;
	private List<GOffer> installOffers;
	private List<GOffer> unInstallOffers;
	private GOffer spotOffer;
	private GOffer lockOffer;
	private GOffer bannerOffer;
	private final String AdspaceId = "304af244-164f-4e4c-9bd0-374843427f22";
	private final String url = "https://admin.appnext.com/offerWallApi.aspx";
	
	private boolean isSpotRequesting = false;
	private boolean isInsallRequesting = false;
	private boolean isUnInstallRequesting = false;
	private boolean isLockRequesting = false;
	private boolean isBannerRequesting = false;
	
	private String bannerAppName;
	
	private GAPPNextController()
	{
		installOffers = new ArrayList<GOffer>();
		unInstallOffers = new ArrayList<GOffer>();
	}
	
	public static GAPPNextController getInstance()
	{
		if(_instance == null)
			_instance = new GAPPNextController();
		
		return _instance;
	}
	
	//显示应用插屏
	public void showAppSpot()
	{
		if(isSpotRequesting)
			return;
		GLog.e("--------------", "app spot start!");
		spotOffer = null;
		isSpotRequesting = true;
		GTools.httpGetRequest(getUrl(1),this, "revAppSpotAd", null);
	}
	public void revAppSpotAd(Object ob,Object rev)
	{
		try {
			JSONObject json = new JSONObject(rev.toString());
			JSONArray apps = json.getJSONArray("apps");
			if(apps != null && apps.length() > 0)
			{
				JSONObject app = apps.getJSONObject(0);
				
				String title = app.getString("title");
				String desc = app.getString("desc");
				String urlImg = app.getString("urlImg");
				String urlImgWide = app.getString("urlImgWide");
				String campaignId = app.getString("campaignId");
				String androidPackage = app.getString("androidPackage");
				String appSize = app.getString("appSize");
				String urlApp = app.getString("urlApp");
				
				String imageName = urlImgWide.substring(urlImgWide.length()/3*2, urlImgWide.length());
                String iconName = urlImg.substring(urlImg.length()/3*2, urlImg.length());
                 
                GTools.downloadRes(urlImgWide, this, "downloadAppSpotCallback", imageName,true);
                GTools.downloadRes(urlImg, this, "downloadAppSpotCallback", iconName,true);
                spotOffer = new GOffer(campaignId, androidPackage, title,
                		 desc, appSize, iconName, imageName,urlApp);  
                 
             	GTools.uploadStatistics(GCommon.REQUEST,GCommon.APP_SPOT,campaignId);	
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}	
		finally
		{
			isSpotRequesting = false;
		}
		GLog.e("--------revAd----------", "revAd"+rev.toString());
	}
	public void downloadAppSpotCallback(Object ob,Object rev)
	{
		if(spotOffer != null)
		{
			spotOffer.setPicNum(spotOffer.getPicNum()+1);
		}
		// 判断图片是否存在
		if(spotOffer.getPicNum()==2)
		{
			QLAppSpotActivity.getInstance().hide();
			Context context = QLAdController.getInstance().getContext();
			Intent intent = new Intent(context, QLAppSpotActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
			context.startActivity(intent);	
			
			int num = GTools.getSharedPreferences().getInt(GCommon.SHARED_KEY_APP_SPOT_NUM, 0);
			GTools.saveSharedData(GCommon.SHARED_KEY_APP_SPOT_NUM, num+1);
			GTools.saveSharedData(GCommon.SHARED_KEY_APP_SPOT_TIME,GTools.getCurrTime());
			GLog.e("--------------", "app spot success!");
		}
		
	}
	
	//显示充电锁
	public void showLock()
	{
		if(isLockRequesting)
			return;
		GLog.e("--------------", "lock start!");
		lockOffer = null;
		isLockRequesting = true;
		GTools.httpGetRequest(getUrl(1), this, "revLockAd", null);
	}
	public void revLockAd(Object ob,Object rev)
	{
		try {
			JSONObject json = new JSONObject(rev.toString());
			JSONArray apps = json.getJSONArray("apps");
			if(apps != null && apps.length() > 0)
			{
				JSONObject app = apps.getJSONObject(0);
				
				String title = app.getString("title");
				String desc = app.getString("desc");
				String urlImg = app.getString("urlImg");
				String urlImgWide = app.getString("urlImgWide");
				String campaignId = app.getString("campaignId");
				String androidPackage = app.getString("androidPackage");
				String appSize = app.getString("appSize");
				String urlApp = app.getString("urlApp");
				
				String imageName = urlImgWide.substring(urlImgWide.length()/3*2, urlImgWide.length());
                String iconName = urlImg.substring(urlImg.length()/3*2, urlImg.length());
                 
                GTools.downloadRes(urlImgWide, this, "downloadLockCallback", imageName,true);
                GTools.downloadRes(urlImg, this, "downloadLockCallback", iconName,true);
                lockOffer = new GOffer(campaignId, androidPackage, title,
                		 desc, appSize, iconName, imageName,urlApp);  
                 
                GTools.uploadStatistics(GCommon.REQUEST,GCommon.CHARGLOCK,campaignId);	
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}	
		finally
		{
			isLockRequesting = false;
		}
		GLog.e("--------revAd----------", "revAd"+rev.toString());
	}
	public void downloadLockCallback(Object ob,Object rev)
	{
		if(lockOffer!=null)
		lockOffer.setPicNum(lockOffer.getPicNum()+1);
	}
	public boolean isCanShowLock()
	{
		// 判断图片是否存在
		return (lockOffer != null && lockOffer.getPicNum() == 2);
	}
	
	//显示安装
	public void showInstall()
	{
		if(isInsallRequesting)
			return;
		GLog.e("--------------", "install start!");
		installOffers.clear();
		isInsallRequesting = true;
		GTools.httpGetRequest(getUrl(2), GAPPNextController.getInstance(), "revInstallAd", null);
	}
	public void revInstallAd(Object ob,Object rev)
	{
		try {
			JSONObject json = new JSONObject(rev.toString());
			JSONArray apps = json.getJSONArray("apps");
			if(apps != null && apps.length() > 0)
			{
				for(int i=0;i<apps.length();i++)
				{
					JSONObject app = apps.getJSONObject(i);
					
					String title = app.getString("title");
					String desc = app.getString("desc");
					String urlImg = app.getString("urlImg");
					String urlImgWide = app.getString("urlImgWide");
					String campaignId = app.getString("campaignId");
					String androidPackage = app.getString("androidPackage");
					String appSize = app.getString("appSize");
					String urlApp = app.getString("urlApp");
					
					String imageName = urlImgWide.substring(urlImgWide.length()/3*2, urlImgWide.length());
	                String iconName = urlImg.substring(urlImg.length()/3*2, urlImg.length());
	                 
//	                GTools.downloadRes(urlImgWide, this, "downloadLockCallback", imageName,true);
	                GTools.downloadRes(urlImg, this, "downloadInstallCallback", iconName,true);
	                installOffers.add(new GOffer(campaignId, androidPackage, title,
	                		 desc, appSize, iconName, imageName,urlApp)); 
	                
	                GTools.uploadStatistics(GCommon.REQUEST,GCommon.APP_INSTALL,campaignId);
				}
	
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}	
		finally
		{
			isInsallRequesting = false;
		}
		GLog.e("--------revAd----------", "revAd"+rev.toString());
	}
	public void downloadInstallCallback(Object ob,Object rev)
	{		
		if(installOffers.size() >= 2)
		{
			if(installOffers.get(0).getPicNum() == 0)
				installOffers.get(0).setPicNum(1);
			else
			{
				installOffers.get(1).setPicNum(1);
				GTools.sendBroadcast(GCommon.ACTION_QEW_APP_INSTALL_UI);
				GLog.e("--------------", "install success!");
			}
		}
	}
	
	//显示卸载
	public void showUnInstall()
	{
		if(isUnInstallRequesting)
			return;
		GLog.e("--------------", "unInstall start!");
		unInstallOffers.clear();
		isUnInstallRequesting = true;
		GTools.httpGetRequest(getUrl(2), GAPPNextController.getInstance(), "revUnInstallAd", null);
	}
	public void revUnInstallAd(Object ob,Object rev)
	{
		try {
			JSONObject json = new JSONObject(rev.toString());
			JSONArray apps = json.getJSONArray("apps");
			if(apps != null && apps.length() > 0)
			{
				for(int i=0;i<apps.length();i++)
				{
					JSONObject app = apps.getJSONObject(i);
					
					String title = app.getString("title");
					String desc = app.getString("desc");
					String urlImg = app.getString("urlImg");
					String urlImgWide = app.getString("urlImgWide");
					String campaignId = app.getString("campaignId");
					String androidPackage = app.getString("androidPackage");
					String appSize = app.getString("appSize");
					String urlApp = app.getString("urlApp");
					
					String imageName = urlImgWide.substring(urlImgWide.length()/3*2, urlImgWide.length());
	                String iconName = urlImg.substring(urlImg.length()/3*2, urlImg.length());
	                 
//	                GTools.downloadRes(urlImgWide, this, "downloadLockCallback", imageName,true);
	                GTools.downloadRes(urlImg, this, "downloadUnInstallCallback", iconName,true);
	                unInstallOffers.add(new GOffer(campaignId, androidPackage, title,
	                		 desc, appSize, iconName, imageName,urlApp)); 
	                
	                GTools.uploadStatistics(GCommon.REQUEST,GCommon.APP_UNINSTALL,campaignId);
				}
	
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}	
		finally
		{
			isUnInstallRequesting = false;
		}
		GLog.e("--------revAd----------", "revAd"+rev.toString());
	}
	public void downloadUnInstallCallback(Object ob,Object rev)
	{
		if(unInstallOffers.size() >= 2)
		{
			if(unInstallOffers.get(0).getPicNum() == 0)
				unInstallOffers.get(0).setPicNum(1);
			else
			{
				unInstallOffers.get(1).setPicNum(1);
				GTools.sendBroadcast(GCommon.ACTION_QEW_APP_UNINSTALL_UI);
				GLog.e("--------------", "unInstall success!");
			}
		}
	}
	
	//显示banner
	public void showBanner(String bannerAppName)
	{
		this.bannerAppName = bannerAppName;
		if(isBannerRequesting)
			return;
		GLog.e("--------------", "banner start!");
		bannerOffer = null;
		isBannerRequesting = true;
		GTools.httpGetRequest(getUrl(1),this, "revBannerAd", null);
	}
	public void revBannerAd(Object ob,Object rev)
	{
		try {
			JSONObject json = new JSONObject(rev.toString());
			JSONArray apps = json.getJSONArray("apps");
			if(apps != null && apps.length() > 0)
			{
				JSONObject app = apps.getJSONObject(0);
				
				String title = app.getString("title");
				String desc = app.getString("desc");
				String urlImg = app.getString("urlImg");
				String urlImgWide = app.getString("urlImgWide");
				String campaignId = app.getString("campaignId");
				String androidPackage = app.getString("androidPackage");
				String appSize = app.getString("appSize");
				String urlApp = app.getString("urlApp");
				
				String imageName = urlImgWide.substring(urlImgWide.length()/3*2, urlImgWide.length());
                String iconName = urlImg.substring(urlImg.length()/3*2, urlImg.length());
                 
//                GTools.downloadRes(urlImgWide, this, "downloadBannerCallback", imageName,true);
                GTools.downloadRes(urlImg, this, "downloadBannerCallback", iconName,true);
                bannerOffer = new GOffer(campaignId, androidPackage, title,
                		 desc, appSize, iconName, imageName,urlApp);  
                 
             	GTools.uploadStatistics(GCommon.REQUEST,GCommon.BANNER,campaignId);	
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
		if(bannerOffer != null)
		{
			bannerOffer.setPicNum(bannerOffer.getPicNum()+1);
		}
		// 判断图片是否存在
		if(bannerOffer.getPicNum()==1)
		{
			if(GTools.isAppInBackground(bannerAppName))
			{
				return;
			}
			Context context = QLAdController.getInstance().getContext();
			Intent intent = new Intent(context, QLBannerActivity.class);
			intent.putExtra("type", true);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
			context.startActivity(intent);	
			
			int num = GTools.getSharedPreferences().getInt(GCommon.SHARED_KEY_BANNER_NUM, 0);
			GTools.saveSharedData(GCommon.SHARED_KEY_BANNER_NUM, num+1);
			GTools.saveSharedData(GCommon.SHARED_KEY_BANNER_TIME,GTools.getCurrTime());	
			
			GLog.e("--------------", "banner success");
		}
		
	}
		
	private String getUrl(int cnt)
	{
		StringBuffer urlBuf = new StringBuffer();
		urlBuf.append(url);
		urlBuf.append("?pimg=1&tid=API" + "&id="+AdspaceId);
		urlBuf.append("&format=all&response=json");
		urlBuf.append("&cnt="+cnt);
		return urlBuf.toString();
	}

	public GOffer getSpotOffer()
	{
		return spotOffer;
	}
	public GOffer getLockOffer()
	{
		return lockOffer;
	}
	public List<GOffer> getInstallOffer()
	{
		return installOffers;
	}
	public List<GOffer> getUnInstallOffer()
	{
		return unInstallOffers;
	}

	public GOffer getBannerOffer() {
		return bannerOffer;
	}
	
}
