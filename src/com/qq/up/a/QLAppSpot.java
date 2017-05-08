package com.qq.up.a;



import java.util.List;

import com.guang.client.GCommon;
import com.guang.client.controller.GAdViewController;
import com.guang.client.controller.GAdinallController;
import com.guang.client.mode.GOffer;
import com.guang.client.mode.GOfferEs;
import com.guang.client.tools.GTools;
import com.qq.up.a.view.GWebView;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

@SuppressLint("SetJavaScriptEnabled")
public class QLAppSpot{
	WindowManager.LayoutParams wmParams;  
    //创建浮动窗口设置布局参数的对象  
    WindowManager mWindowManager;
    private Service context;
    private static QLAppSpot _instance;
	private boolean isShow = false;
	
	private RelativeLayout root;
	private WebView webView;
	private WebView webView2;
	private String adSource;
	private String target = null;
	private Handler handler;
	private List<String>  imgtrackings;
	private List<String>  thclkurls;
	private List<GOfferEs> ess;
	private int type;
	private String currUrl;
	private GOffer obj = null;
	
	private QLAppSpot(){}
	public static QLAppSpot getInstance()
	{
		if(_instance == null)
		{
			_instance = new QLAppSpot();
		}
		return _instance;
	}
	
	public void show(final int type)
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
		
		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT);
		layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
		root = new RelativeLayout(context);
		root.setLayoutParams(layoutParams);
		//添加mFloatLayout  
        mWindowManager.addView(root, wmParams);  
		isShow = true;
		
		
		if(type == 1)
		{
			obj = GAdViewController.getInstance().getAppSpotOffer();
			adSource = "AdView";
		}
		else
		{
			obj = GAdinallController.getInstance().getAppSpotOffer();
			adSource = "Adinall";
		}
		final long adPositionId = obj.getAdPositionId();

		int w = GTools.dip2px(300);
		int h =  GTools.dip2px(250);
		
		RelativeLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams(w,h);
		layoutParams2.addRule(RelativeLayout.CENTER_IN_PARENT);
        webView = new GWebView(context);
		//添加mFloatLayout  
        root.addView(webView,layoutParams2);  
		
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
		
		
		 
		webView.setWebViewClient(new WebViewClient(){
			 @Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				 if(target == null)
				 {
					 target = url;
					 GTools.uploadStatistics(GCommon.CLICK,adPositionId,GCommon.APP_SPOT,adSource,-1);
					 if(type == 1 && obj.getAct() == 2)
					 {
						 GAdViewController.getInstance().setTrackOffer(obj);
						 GTools.sendBroadcast(GCommon.ACTION_QEW_START_DOWNLOAD);
					 }
					 openBrowser(target);
					 if(thclkurls == null || thclkurls.size() == 0)
					 {
						hide();
					 }
					 else
					 {
						 updateClick();
					 }
				 }
				return true;
			}
		 });
		
		webView.loadData(obj.getAdm(), "text/html; charset=UTF-8", null);

		int right = (GTools.getScreenW()-w)/2;
		int top = (GTools.getScreenH() - h)/2;
		//关闭按钮
		RelativeLayout.LayoutParams closeLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
		int marginX = right-GTools.dip2px(10);
		int marginY = top-GTools.dip2px(10);
		closeLayoutParams.setMargins(0, marginY, marginX, 0);
		closeLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		closeLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		
		ImageView close = new ImageView(context);
		close.setImageResource((Integer)GTools.getResourceId("qew_browser_close", "drawable"));
		root.addView(close, closeLayoutParams);
		
		
		RelativeLayout.LayoutParams layoutParams3 = new RelativeLayout.LayoutParams(1,1);
		layoutParams3.addRule(RelativeLayout.CENTER_IN_PARENT);
        webView2 = new WebView(context);
		//添加mFloatLayout  
        root.addView(webView2,layoutParams3);  
		
        webView2.getSettings().setJavaScriptEnabled(true);
        webView2.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
		
        
        webView2.setWebViewClient(new WebViewClient(){
			 @Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				 view.loadUrl(url);
				return true;
			}
		 });
        
        imgtrackings = obj.getImgtrackings();
		thclkurls = obj.getThclkurls();
		ess = obj.getEss();
		
		handler = new Handler(){
			@Override
			public void dispatchMessage(Message msg) {
				super.dispatchMessage(msg);
				if(msg.what == 0x01)
				{
					if(type == 1)
					{
						webView2.loadUrl(currUrl);
						
					}else
					{
						webView2.loadUrl(imgtrackings.get(0));
						imgtrackings.remove(0);
					}
				}
				else if(msg.what == 0x02)
				{
					webView2.loadUrl(thclkurls.get(0));
					thclkurls.remove(0);
					if(thclkurls.size() == 0)
					{
						hide();
					}
				}
				else if(msg.what == 0x03)
				{
					hide();
				}
			}
		};
        	
		
		//关闭事件
		close.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				hide();
				
			}
		});
		
		show();
		
		GTools.uploadStatistics(GCommon.SHOW,adPositionId,GCommon.APP_SPOT,adSource,-1);
		
		updateShow();

		new Thread(){
			public void run() {
				try {
					Thread.sleep(1000*15);
					handler.sendEmptyMessage(0x03);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			};
		}.start();

	}
	
	
	public void hide()
	{
		if(isShow)
		{
			mWindowManager.removeView(root);
			isShow = false;
		}		
	}
	
	public boolean isShowing()
	{
		return this.isShow;
	}
	
	private void updateShow()
	{
		new Thread(){
			public void run() {
				if(type == 1)
				{
					while(ess != null && ess.size() > 0)
					{
						try {
							Thread.sleep(ess.get(0).getTime()*1000+100);
							ess.get(0).setTime(0);
							if(ess.get(0).getUrl().size() > 0)
							{
								currUrl = ess.get(0).getUrl().get(0);
								ess.get(0).getUrl().remove(0);
								handler.sendEmptyMessage(0x01);
							}
							if(ess.get(0).getUrl().size() == 0)
							{
								ess.remove(0);
							}
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
				else
				{
					while(imgtrackings != null && imgtrackings.size() > 0)
					{
						handler.sendEmptyMessage(0x01);
						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
				
			};
		}.start();
	}
	
	private void updateClick()
	{
		new Thread(){
			public void run() {
				while(thclkurls != null && thclkurls.size() > 0)
				{
					handler.sendEmptyMessage(0x02);
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			};
		}.start();
		
	}
	
	private void show()
	{
		AnimationSet animationSet = new AnimationSet(true);
		AlphaAnimation animation = new AlphaAnimation(0, 1);
		animation.setDuration(500);
		animationSet.addAnimation(animation);
		root.startAnimation(animationSet);
	}
	
	public void openBrowser(String url)
	{
		PackageManager packageMgr = context.getPackageManager();
		Intent intent = packageMgr.getLaunchIntentForPackage("com.android.chrome");
		if(intent == null)
		{
			intent = new Intent();
		}
        intent.setAction(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setData(Uri.parse(url));
        context.startActivity(intent);
	}
}
