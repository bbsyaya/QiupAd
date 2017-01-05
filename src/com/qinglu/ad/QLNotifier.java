package com.qinglu.ad;




import java.util.List;

import org.json.JSONObject;

import com.guang.client.controller.GOfferController;
import com.guang.client.mode.GOffer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.RemoteViews;

@SuppressLint("NewApi")
public class QLNotifier {

	private static QLNotifier _instance = null;
	private Activity activity;
	public static QLNotifier getInstance()
	{
		if(_instance == null)
		{
			_instance = new QLNotifier();
		}
		return _instance;
	}
	
	public void setActivity(Activity activity) {
		this.activity = activity;
	}
	
	
	public void showNotify()
	{
		if(this.activity != null)
		{
			this.activity.finish();
			this.activity = null;
		}
		
		new Thread(){
			public void run() {
				try {
					Thread.sleep(1000*10);
					
					Context context = QLAdController.getInstance().getContext();
					GOffer obj =  GOfferController.getInstance().getOffer();
					if(obj != null && !isOpenDownActivity())
					{
						String offerId = obj.getId();
						
						Intent intent = new Intent(context, QLNotifyActivity.class);
						intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						intent.putExtra("offerId",offerId);
						//intent.putExtra(GCommon.INTENT_TYPE, GCommon.INTENT_OPEN_SPOT);
						context.startActivity(intent);
					}				
				} catch (Exception e) {
					e.printStackTrace();
				}				
			};
		}.start();
	}
	
	private boolean isOpenDownActivity()
	{
//		Context context = QLAdController.getInstance().getContext();
//		ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);  
//		List<RunningTaskInfo> list = manager.getRunningTasks(1);
//		if(list.size() > 0)
//		{
//			RunningTaskInfo info = list.get(0);
//			if(info.topActivity.getClassName().equals(QLDownActivity.class.getName()))
//			{
//				return true;
//			}
//		}
		return false;
	}
}
