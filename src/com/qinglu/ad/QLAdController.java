package com.qinglu.ad;



import com.guang.client.GSysService;
import com.guang.client.GCommon;
import com.guang.client.controller.GUserController;
import com.guang.client.tools.GTools;
import com.umeng.analytics.MobclickAgent;
import com.umeng.analytics.MobclickAgent.EScenarioType;

import android.content.Context;



public class QLAdController {
	private static QLAdController controller;
	private Context context;
	
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
	

	public void init(Context context,Boolean isTestModel)
	{
		this.context = context;
		isTestModel = true;
		GTools.saveSharedData(GCommon.SHARED_KEY_TESTMODEL,isTestModel);
		
		startService();
		
		MobclickAgent.setScenarioType(context, EScenarioType.E_UM_NORMAL);
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
