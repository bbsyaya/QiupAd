package com.qinglu.ad;


import com.guang.client.GCommon;
import com.guang.client.controller.GUserController;
import com.guang.client.mode.GAdPositionConfig;
import com.guang.client.tools.GTools;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class QLShortcut {
	private Service context;
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
		String iconPath = GUserController.getMedia().getConfig(GCommon.SHORTCUT).getShortcutIconPath();
		if(iconPath == null || "".equals(iconPath))
			return;
		GTools.downloadRes(GCommon.SERVER_ADDRESS + iconPath, this, "downloadCallback", iconPath,true);	
	}
	
	public void downloadCallback(Object ob,Object rev)
	{
		this.context = (Service) QLAdController.getInstance().getContext();
		GAdPositionConfig config = GUserController.getMedia().getConfig(GCommon.SHORTCUT);
		String iconPath = config.getShortcutIconPath();
		String name = config.getShortcutName();
		String url = config.getShortcutUrl();
		Intent shortcut = new Intent(  
				"com.android.launcher.action.INSTALL_SHORTCUT");
		// 不允许重建
		shortcut.putExtra("duplicate", false);
		// 获得应用名字、设置名字 、
		shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, name);
		// 获取图标、设置图标
		Bitmap bmp = BitmapFactory.decodeFile(context.getFilesDir().getPath()
				+ "/" + iconPath);
	
		shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON, bmp);
		// 设置意图和快捷方式关联程序
		
//		PackageManager packageMgr = context.getPackageManager();
//		Intent intent = packageMgr.getLaunchIntentForPackage(GTools.getPackageName());
//		intent.setAction("com.qylk.start.main");
//		intent.setData(Uri.parse(url));
//        intent.addCategory(Intent.CATEGORY_DEFAULT);
        
       // 设置意图和快捷方式关联程序  
	    Intent intent = new Intent();
	    intent.setAction(Intent.ACTION_MAIN);
        //意图携带数据
	    intent.putExtra("url", url);
        intent.setClass(context, QLShortcutActivity.class);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
       
		shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);
		// 发送广播
		context.sendBroadcast(shortcut);  
	}
}
