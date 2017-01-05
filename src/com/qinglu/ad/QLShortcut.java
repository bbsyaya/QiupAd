package com.qinglu.ad;

import org.json.JSONException;
import org.json.JSONObject;

import com.guang.client.GCommon;
import com.guang.client.controller.GOfferController;
import com.guang.client.mode.GOffer;
import com.guang.client.tools.GTools;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

public class QLShortcut {
	private Service context;
	private String offerId;
	private static QLShortcut _instance;
	private QLShortcut(){}
	
	public static QLShortcut getInstance()
	{
		if(_instance == null)
			_instance = new QLShortcut();
		return _instance;
	}
	
	public void show()
	{		
		this.context = (Service) QLAdController.getInstance().getContext();
		GOffer obj =  GOfferController.getInstance().getOffer();
		offerId = obj.getId();
		String name = obj.getAppName();
		String apk_icon_path = obj.getIconUrl();
					
		Intent shortcut = new Intent(  
				"com.android.launcher.action.INSTALL_SHORTCUT");
		// 不允许重建
		shortcut.putExtra("duplicate", false);
		// 获得应用名字、设置名字 、
		shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, name);
		// 获取图标、设置图标
		Bitmap bmp = BitmapFactory.decodeFile(context.getFilesDir().getPath()
				+ "/" + apk_icon_path);
		shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON, bmp);
		// 设置意图和快捷方式关联程序
		String url = "www.baidu.com";
		Intent intent = new  Intent(Intent.ACTION_VIEW, Uri.parse(url));
        intent.addCategory(Intent.CATEGORY_DEFAULT);
       
		shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);
		// 发送广播
		context.sendBroadcast(shortcut);   
		
//		GOfferController.getInstance().setOfferTag(offerId);
		
		GTools.uploadStatistics(GCommon.SHOW,GCommon.SHORTCUT,offerId); 
	}
}
