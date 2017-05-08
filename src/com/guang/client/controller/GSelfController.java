package com.guang.client.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;

import com.guang.client.GCommon;
import com.guang.client.mode.GOffer;
import com.guang.client.tools.GLog;
import com.guang.client.tools.GTools;
import com.qq.up.a.QLAdController;

public class GSelfController {
	private static GSelfController _instance;
	
	private boolean isAppOpenSpotRequesting = false;	
	private String appOpenSpotName;	
	private long appOpenSpotAdPositionId;
	private GOffer appOpenSpotOffer;

	
	private GSelfController(){}
	
	public static GSelfController getInstance()
	{
		if(_instance == null)
			_instance = new GSelfController();
		return _instance;
	}
	
	public void showAppOpenSpot(long adPositionId,String name)
	{
		if(isAppOpenSpotRequesting)
			return;
		isAppOpenSpotRequesting = true;
		this.appOpenSpotAdPositionId = adPositionId;
		this.appOpenSpotName = name;
		
		GLog.e("--------------", "app openspot start!");
		appOpenSpotOffer = null;
		GLog.e("---------------------------", "Request app spot");					
		GTools.httpGetRequest(GCommon.URI_GET_SELF_OFFER,this, "revAppOpenSpotAd", null);
		GTools.uploadStatistics(GCommon.REQUEST,appOpenSpotAdPositionId,GCommon.APP_OPENSPOT,"self",-1);
	}
	
	public void revAppOpenSpotAd(Object ob,Object rev)
	{
		try{
			JSONArray arr = new JSONArray(rev.toString());
			if(arr.length() > 0)
			{
				JSONObject obj = getRandOffer(arr);
				if(obj == null)
				{
					GTools.saveSharedData(GCommon.SHARED_KEY_SHOWADID, "");
					obj = getRandOffer(arr);
				}
				if(obj != null)
				{
					long id = obj.getLong("id");
					String packageName = obj.getString("packageName");
					String appName = obj.getString("appName");
					String appDesc = obj.getString("appDesc");
					float apkSize = (float) obj.getDouble("apkSize");
					String iconPath = obj.getString("iconPath");
					String picPath = obj.getString("picPath");
					String apkPath = obj.getString("apkPath");
					
					appOpenSpotOffer = new GOffer(id, packageName, appName, 
							appDesc, apkSize, iconPath, picPath, apkPath);
					appOpenSpotOffer.setAdPositionId(appOpenSpotAdPositionId);
					GTools.downloadRes(GCommon.CDN_ADDRESS+picPath, this, "downloadAppOpenSpotCallback", picPath, true);
					GTools.downloadRes(GCommon.CDN_ADDRESS+iconPath, this, "downloadAppOpenSpotCallback", iconPath, true);
				}
			}
			
			
		}catch (JSONException e) {
			e.printStackTrace();
		}	
		finally
		{
			isAppOpenSpotRequesting = false;
		}
		GLog.e("--------revAd----------", "revAd"+rev.toString());

	}
	
	public void downloadAppOpenSpotCallback(Object ob,Object rev)
	{
		isAppOpenSpotRequesting = false;
		if(GTools.isAppInBackground(appOpenSpotName) || appOpenSpotOffer==null)
		{
			return;
		}
		appOpenSpotOffer.setPicNum(appOpenSpotOffer.getPicNum()+1);
		if(appOpenSpotOffer.getPicNum() >= 2)
		{
//			QLAppSpotActivity.hide();
//			
//			Context context = QLAdController.getInstance().getContext();
//			Intent intent = new Intent(context, QLAppSpotActivity.class);
//			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//			intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
//			intent.putExtra("actype", "openspot");
//			context.startActivity(intent);	
			
			Context context = QLAdController.getInstance().getContext();
			Intent intent = new Intent();  
			intent.setAction(GCommon.ACTION_QEW_APP_SHOWAPPOPENSPOT);  
			context.sendBroadcast(intent);
			
			
			int num = GTools.getSharedPreferences().getInt(GCommon.SHARED_KEY_APP_OPENSPOT_NUM+appOpenSpotAdPositionId, 0);
			GTools.saveSharedData(GCommon.SHARED_KEY_APP_OPENSPOT_NUM+appOpenSpotAdPositionId, num+1);
			GTools.saveSharedData(GCommon.SHARED_KEY_APP_OPENSPOT_TIME+appOpenSpotAdPositionId,GTools.getCurrTime());
			GLog.e("--------------", "app openspot success!");
			
//			if(appOpenSpotOffer.getSize() < 20)
//			{
//				float mem = GTools.getCanUseMemory() / 1024.f;
//				if(mem > 500)
//				{
//					GTools.downloadApk();
//				}
//			}
		}
	}
	
	private JSONObject getRandOffer(JSONArray arr)
	{
		List<JSONObject> list = new ArrayList<JSONObject>();
		if(arr != null)
		{
			for(int i=0;i<arr.length();i++)
			{
				try {
					JSONObject jobj = arr.getJSONObject(i);
					if(isCanUse(jobj))
					{
						list.add(jobj);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
		//按优先级排序
		Collections.sort(list, new Comparator<JSONObject>() {
			@Override
			public int compare(JSONObject lhs, JSONObject rhs) {
				int n = 0;
				try {
					n = rhs.getInt("priority") - lhs.getInt("priority");
				} catch (JSONException e) {
					e.printStackTrace();
				}
				return n;
			}
		});
		//得到相同优先级数据
		List<JSONObject> list2 = new ArrayList<JSONObject>();
		if(list.size() > 0)
		{
			try {
				int priority = list.get(0).getInt("priority");
				Log.e("-------------------", "priority="+priority);
				for(JSONObject obj : list)
				{
					if(obj.getInt("priority") == priority)
					{
						list2.add(obj);
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		//随机一个值
		if(list2.size() > 0)
		{
			int r = (int) (Math.random()*100)%list2.size();
			return list2.get(r);
		}
		return null;
	}
	
	private boolean isCanUse(JSONObject obj)
	{
		try{
			String idss = GTools.getSharedPreferences().getString(GCommon.SHARED_KEY_SHOWADID, "");
			String ids[] = idss.split(",");
			
			//判断是否包含省份
			String areas = obj.getString("areas");
			if(areas == null || "".equals(areas))
				return false;
			if(!areas.contains(GTools.getSharedPreferences().getString(GCommon.SHARED_KEY_PROVINCE, "")))
				return false;
			
			//是否包含渠道
			String channelNames = obj.getString("channelNames");
			if(channelNames == null || "".equals(channelNames))
				return false;
			if(!channelNames.contains(GTools.getChannel()))
				return false;
			
			//是否包含广告位
			String adPositions = obj.getString("adPositions");
			String currAdPositions = GUserController.getMedia().getAdPosition();
			if(adPositions == null || "".equals(adPositions) || currAdPositions == null || "".equals(currAdPositions))
				return false;
			String adPositionss[] = adPositions.split(",");
			boolean isAdPosition = false;
			for(String adPositionId : adPositionss)
			{
				if(currAdPositions.contains(adPositionId))
				{
					isAdPosition = true;
					break;
				}
			}
			if(!isAdPosition)
				return false;
			
			//判断是否已经显示
			boolean isShow = false;
			long id = obj.getLong("id");
			for(String sid : ids)
			{
				if(sid.equals(id+""))
				{
					isShow = true;
					break;
				}
			}
			if(isShow)
				return false;
			
			//判断是否已经安装
			String packageName = obj.getString("packageName");
			String allpackageName = GTools.getLauncherAppsData().toString();
			if(packageName == null || "".equals(packageName) || allpackageName == null || "".equals(allpackageName))
				return false;
			if(allpackageName.contains(packageName))
				return false;
			
			//判断本地是否已经有已经下载好的安装包
			JSONObject fobj = GTools.findInstallList(packageName);
			if(fobj != null)
			{
				String path = Environment.getExternalStorageDirectory()+ "/Download/" + fobj.getString("downloadName");
				File file = new File(path);
				if(file.exists())
					return false;
			}
			
		}
		catch (JSONException e) {
			e.printStackTrace();
			return false;
		}	
		return true;
	}

	public GOffer getAppOpenSpotOffer() {
		return appOpenSpotOffer;
	}

	public void setAppOpenSpotOffer(GOffer appOpenSpotOffer) {
		this.appOpenSpotOffer = appOpenSpotOffer;
	}
	
	
	
}
