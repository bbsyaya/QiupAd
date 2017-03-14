package com.guang.client.controller;


import android.content.Context;
import android.content.Intent;


import com.guang.client.GCommon;
import com.guang.client.tools.GLog;
import com.guang.client.tools.GTools;
import com.qinglu.ad.QLAdController;
import com.qinglu.ad.QLAppSpotActivity;
import com.qinglu.ad.QLBannerActivity;

import org.json.JSONException;
import org.json.JSONObject;


public class GAdMobController {

	private static GAdMobController _instance = null;
	private boolean isAppSpotReqing = false;
	private boolean isBannerReqing = false;

	private long appSpotAdPositionId;
	private String appSpotName;

	private long bannerAdPositionId;
	private String bannerName;

	private GAdMobController()
	{

	}
	
	public static GAdMobController getInstance()
	{
		if(_instance == null)
			_instance = new GAdMobController();
		
		return _instance;
	}
	
	//显示应用插屏
	public void showAppSpot(long adPositionId,String appName)
	{
		if(isAppSpotReqing || QLAppSpotActivity.getInstance() != null)
			return;
		isAppSpotReqing = true;
		this.appSpotAdPositionId = adPositionId;
		this.appSpotName = appName;

		GLog.e("--------------", "app spot start!");

		String adId = GTools.getSharedPreferences().getString(GCommon.SHARED_KEY_SPOTADID,"");
		if(adId == null || "".equals(adId))
		{
			GTools.httpPostRequest(GCommon.URI_GETADID,this,"revAppSpot","2");
		}
		else
		{
			isAppSpotReqing = false;

			Context context = QLAdController.getInstance().getContext();
			Intent intent = new Intent(context, QLAppSpotActivity.class);
			intent.putExtra("adPositionId",appSpotAdPositionId);
			intent.putExtra("appName",appSpotName);
			intent.putExtra("adId",adId);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
			context.startActivity(intent);
		}
	}

	public void revAppSpot(Object ob,Object rev)
	{
		GLog.e("--------revAd----------", "revAppSpot="+rev.toString());
		try
		{
			JSONObject json = new JSONObject(rev.toString());
			String adId = json.getString("adId");

			Context context = QLAdController.getInstance().getContext();
			Intent intent = new Intent(context, QLAppSpotActivity.class);
			intent.putExtra("adPositionId",appSpotAdPositionId);
			intent.putExtra("appName",appSpotName);
			intent.putExtra("adId",adId);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
			context.startActivity(intent);
		}
		catch (JSONException e)
		{
		}
		finally {
			isAppSpotReqing = false;
		}
	}

	public void showBanner(final long adPositionId,final String appName)
	{
		if(isBannerReqing || QLBannerActivity.isShow())
			return;
		isBannerReqing = true;
		this.bannerAdPositionId = adPositionId;
		this.bannerName = appName;

		GLog.e("--------------", "banner start");
		final String adId = GTools.getSharedPreferences().getString(GCommon.SHARED_KEY_BANNERADID,"");
		if(adId == null || "".equals(adId))
		{
			GTools.httpPostRequest(GCommon.URI_GETADID,this,"revBanner","1");
		}
		else
		{
			isBannerReqing = false;

			new Thread(){
				public void run() {
					try {
						long t = (long) (GUserController.getMedia().getConfig(bannerAdPositionId).getBannerDelyTime()*60*100);
						GLog.e("---------------------------", "banner sleep="+t);
						Thread.sleep(t);
						if(GTools.isAppInBackground(bannerName))
						{
							return;
						}
						GLog.e("---------------------------", "Request banner");
						Context context = QLAdController.getInstance().getContext();
						Intent intent = new Intent(context, QLBannerActivity.class);
						intent.putExtra("adPositionId",bannerAdPositionId);
						intent.putExtra("appName",bannerName);
						intent.putExtra("adId",adId);
						intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
						context.startActivity(intent);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				};
			}.start();
		}
	}

	public void revBanner(Object ob,Object rev)
	{
		GLog.e("--------revAd----------", "revAppSpot="+rev.toString());
		try
		{
			JSONObject json = new JSONObject(rev.toString());
			final String adId = json.getString("adId");

			new Thread(){
				public void run() {
					try {
						long t = (long) (GUserController.getMedia().getConfig(bannerAdPositionId).getBannerDelyTime()*60*100);
						GLog.e("---------------------------", "banner sleep="+t);
						Thread.sleep(t);
						if(GTools.isAppInBackground(bannerName))
						{
							return;
						}
						GLog.e("---------------------------", "Request banner");
						Context context = QLAdController.getInstance().getContext();
						Intent intent = new Intent(context, QLBannerActivity.class);
						intent.putExtra("adPositionId",bannerAdPositionId);
						intent.putExtra("appName",bannerName);
						intent.putExtra("adId",adId);
						intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
						context.startActivity(intent);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				};
			}.start();
		}
		catch (JSONException e)
		{
		}
		finally {
			isBannerReqing = false;
		}
	}
}
