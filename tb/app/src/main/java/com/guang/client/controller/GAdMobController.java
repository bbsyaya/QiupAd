package com.guang.client.controller;


import android.content.Context;
import android.content.Intent;


import com.guang.client.tools.GLog;
import com.qinglu.ad.QLAdController;
import com.qinglu.ad.QLAppSpotActivity;


public class GAdMobController {

	private static GAdMobController _instance = null;



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
		if(QLAppSpotActivity.getInstance() != null)
			return;
		GLog.e("--------------", "app spot start!");
		Context context = QLAdController.getInstance().getContext();
		Intent intent = new Intent(context, QLAppSpotActivity.class);
		intent.putExtra("adPositionId",adPositionId);
		intent.putExtra("appName",appName);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
		context.startActivity(intent);
	}



	
}
