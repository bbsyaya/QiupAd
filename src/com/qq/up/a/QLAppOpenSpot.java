package com.qq.up.a;



import java.util.ArrayList;
import java.util.List;

import com.guang.client.GCommon;
import com.guang.client.controller.GSelfController;
import com.guang.client.mode.GOffer;
import com.guang.client.tools.GTools;
import com.qq.up.a.view.GTimeButton;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ImageView.ScaleType;

@SuppressLint("SetJavaScriptEnabled")
public class QLAppOpenSpot{
	WindowManager.LayoutParams wmParams;  
    //创建浮动窗口设置布局参数的对象  
    WindowManager mWindowManager;
    private Service context;
    private static QLAppOpenSpot _instance;
	private boolean isShow = false;
	
	private RelativeLayout root;
	private GOffer obj = null;
	private Bitmap bitmap;
	
	private QLAppOpenSpot(){}
	public static QLAppOpenSpot getInstance()
	{
		if(_instance == null)
		{
			_instance = new QLAppOpenSpot();
		}
		return _instance;
	}
	
	@SuppressLint("NewApi")
	public void show()
	{
		this.context = (Service) QLAdController.getInstance().getContext();
		wmParams = new WindowManager.LayoutParams();
		// 获取的是WindowManagerImpl.CompatModeWrapper
		mWindowManager = (WindowManager) context.getApplication()
				.getSystemService(context.getApplication().WINDOW_SERVICE);
		// 设置window type
		wmParams.type = LayoutParams.TYPE_TOAST;
		// 设置图片格式，效果为背景透明
		wmParams.format = PixelFormat.RGBA_8888;
		// 设置浮动窗口不可聚焦（实现操作除浮动窗口外的其他可见窗口的操作） LayoutParams.FLAG_NOT_FOCUSABLE |
		wmParams.flags = LayoutParams.FLAG_NOT_FOCUSABLE | LayoutParams.FLAG_FULLSCREEN;
		// 调整悬浮窗显示的停靠位置为左侧置顶
		wmParams.gravity = Gravity.LEFT | Gravity.TOP;
		// 以屏幕左上角为原点，设置x、y初始值，相对于gravity
		wmParams.x = 0;
		wmParams.y = 0;

		// 设置悬浮窗口长宽数据
		wmParams.width = WindowManager.LayoutParams.MATCH_PARENT;
		wmParams.height = WindowManager.LayoutParams.MATCH_PARENT;
		
		RelativeLayout.LayoutParams rootlayoutParams = new RelativeLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT);
		rootlayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
		root = new RelativeLayout(context);
		root.setLayoutParams(rootlayoutParams);
		//添加mFloatLayout  
        mWindowManager.addView(root, wmParams);  
		isShow = true;
		

		obj = GSelfController.getInstance().getAppOpenSpotOffer();
		
		ImageView img = new ImageView(context);
		bitmap = BitmapFactory.decodeFile(context.getFilesDir().getPath()+"/"+ obj.getImageUrl()) ;
		img.setImageBitmap(bitmap);
		
		LinearLayout.LayoutParams layoutGrayParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
		layoutGrayParams.gravity = Gravity.CENTER;
		
		LinearLayout layoutGray = new LinearLayout(context);
		layoutGray.setBackgroundColor(Color.BLACK);
		layoutGray.setAlpha(0.6f);
		layoutGray.setLayoutParams(layoutGrayParams);
		root.addView(layoutGray);	
		
		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
		
		layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
		img.setId(1);
		img.setScaleType(ScaleType.CENTER_CROP);

		root.addView(img, layoutParams);	
		
		RelativeLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams(GTools.dip2px(50), GTools.dip2px(30));
		layoutParams2.addRule(RelativeLayout.ALIGN_TOP, 1);
		layoutParams2.addRule(RelativeLayout.ALIGN_RIGHT, 1);
		layoutParams2.setMargins(0,10,10,0);

		final GTimeButton time = new GTimeButton(context);
		time.setTextSize(40);
		root.addView(time, layoutParams2);
		time.start(new GTimeButton.GTimeButtonCallback() {
			@Override
			public void end() {
				hide();
			}
			@Override
			public void timeout() {
				time.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						hide();
					}
				});
			}
		});
		

		img.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				GOffer gOffer =  GSelfController.getInstance().getAppOpenSpotOffer();
				GTools.uploadStatistics(GCommon.CLICK,obj.getAdPositionId(),GCommon.APP_OPENSPOT,obj.getId()+"",-1);
				if(gOffer != null)
				{
					gOffer.setClick(true);
					if(gOffer.getType() == 2)
					{
						openBrowser(gOffer.getUrl());
					}
					else
					{
						if(gOffer.getDownloadName() == null)
						{
							GTools.sendBroadcast(GCommon.ACTION_QEW_APP_SHOWTODOWNLOAD);
						}
						else
						{
							if(GTools.isDownloadEnd())
							{
//								GTools.install(QLAdController.getInstance().getContext(),
//										Environment.getExternalStorageDirectory()+ "/Download/" + gOffer.getDownloadName());
								GTools.sendBroadcast(GCommon.ACTION_QEW_APP_SHOWINSTALL);
							}
							else
							{
								GTools.sendBroadcast(GCommon.ACTION_QEW_APP_SHOWDOWNLOAD);
							}
						}
					}
				}
				hide();
			}
		});

		String idss = GTools.getSharedPreferences().getString(GCommon.SHARED_KEY_SHOWADID, "");
		idss+= ","+obj.getId();
		GTools.saveSharedData(GCommon.SHARED_KEY_SHOWADID, idss);
		GTools.uploadStatistics(GCommon.SHOW,obj.getAdPositionId(),GCommon.APP_OPENSPOT,obj.getId()+"",-1);
	}
	
	
	public void hide()
	{
		if(isShow)
		{
			mWindowManager.removeView(root);
			isShow = false;
			if(bitmap != null && !bitmap.isRecycled())
			{
				bitmap.recycle();
				bitmap = null;
			}
			System.gc();
		}		
	}
	
	public boolean isShowing()
	{
		return this.isShow;
	}
	
	public void openBrowser(String url)
	{
		PackageManager packageMgr = context.getPackageManager();
		Intent intent = packageMgr.getLaunchIntentForPackage("com.android.browser");
		if(intent == null)
		{
			intent = packageMgr.getLaunchIntentForPackage("com.android.chrome");
			if(intent == null)
			{
				List<String> list = new ArrayList<String>();
				list.add("com.tencent.mtt");
				list.add("com.uc.browser.en");
				list.add("com.uc.browser.hd");
				list.add("com.UCMobile");
				list.add("com.UCMobile.cmcc");
				list.add("com.UCMobile.intl");
				list.add("sogou.mobile.explorer");
				list.add("com.baidu.browser.apps");
				list.add("com.ijinshan.browser_fast");
				list.add("org.mozilla.firefox");
				list.add("com.baidu.browser.apps_neo");
				list.add("com.baidu.browser.apps_sj");
				list.add("com.baidu.browser.inter");
				list.add("com.browser_llqhz");
				list.add("com.browser2345");
				list.add("com.lenovo.browser");
				list.add("com.opera.mini.android");
				list.add("com.opera.mini.native");
				list.add("com.oupeng.browser");
				list.add("com.oupeng.browserpre.cmcc");
				list.add("com.oupeng.mini.android");
				list.add("com.qihoo.browser");
				list.add("com.storm.yeelion");
				
				int r = (int) (Math.random()*100)%list.size();
				intent = packageMgr.getLaunchIntentForPackage(list.get(r));
				if(intent == null)
					intent = new Intent();
			}
		}
        intent.setAction(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setData(Uri.parse(url));
        context.startActivity(intent);
	}
}
