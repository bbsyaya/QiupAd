package com.qinglu.ad;



import com.guang.client.GSysService;
import com.guang.client.GCommon;
import com.guang.client.controller.GUserController;
import com.guang.client.tools.GTools;
import com.qinglu.ad.impl.qinglu.QLSpotManagerQingLu;

import android.content.Context;



public class QLAdController {
	private static QLAdController controller;
	public static QLSpotManager spotManager;
	private static Context context;
	
	private QLAdController()
	{
		
	}
	
	public static QLAdController getInstance()
	{
		if(controller == null)
		{
			controller = new QLAdController();					
		}	
		return controller;
	}
	
	public static QLSpotManager getSpotManager()
	{
		if(spotManager == null)
			spotManager = new QLSpotManagerQingLu(context);
		spotManager.updateContext(context);
		return spotManager;
	}
	
	
		
	public void init(Context context,Boolean isTestModel)
	{
		this.context = context;
		
		GTools.saveSharedData(GCommon.SHARED_KEY_TESTMODEL,isTestModel);
		
		startService();
	}
	
	
	public void startService()
	{
		GSysService.getInstance().start(context);	
	}
	
	public void destory(String clazName)
	{
		GUserController.getInstance().uploadRunAppInfos(clazName);
	}
	

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}
	
	
}
