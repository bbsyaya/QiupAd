package com.qq.up.a;

import java.util.List;

import com.guang.client.GCommon;
import com.guang.client.controller.GAdViewController;
import com.guang.client.controller.GAdinallController;
import com.guang.client.mode.GOffer;
import com.guang.client.mode.GOfferEs;
import com.guang.client.tools.GLog;
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
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnTouchListener;
import android.view.WindowManager.LayoutParams;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.view.animation.Animation.AnimationListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AbsoluteLayout;
import android.widget.RelativeLayout;

public class QLBanner {
	 	WindowManager.LayoutParams wmParams;  
	    //创建浮动窗口设置布局参数的对象  
	    WindowManager mWindowManager;
	    private Service context;
	    private static QLBanner _instance;
		private boolean isShow = false;
		
		private AbsoluteLayout root;
		private RelativeLayout view;
		private int l_height;
		private String target;
		private String adSource;
		
		private WebView webView;
		private WebView webView2;
		private Handler handler;
		private List<String>  imgtrackings;
		private List<String>  thclkurls;
		private List<GOfferEs> ess;
		private int type;
		private String currUrl;
		private GOffer offer = null;
		
		
		private QLBanner(){}
		
		public static QLBanner getInstance()
		{
			if(_instance == null)
			{
				_instance = new QLBanner();
			}
			return _instance;
		}
		
		
		public void show(final int type) {			
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
			wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;


			int w = GTools.dip2px(320);
			int h = l_height =  GTools.dip2px(50);

			root = new AbsoluteLayout(context);
	        AbsoluteLayout.LayoutParams rootlayoutParams = new AbsoluteLayout.LayoutParams(wmParams.width,h,0,0);
//	 		rootlayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
	        view = new RelativeLayout(context);
	 		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, h);
	 		layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
	 		view.setLayoutParams(layoutParams);
	 		
	 		root.addView(view);

			//添加mFloatLayout  
	        mWindowManager.addView(root, wmParams);  
			isShow = true;

			
			this.type = type;
			if(type == 1)
			{
				offer = GAdViewController.getInstance().getBannerOffer();
				adSource = "AdView";
			}
			else
			{
				offer = GAdinallController.getInstance().getBannerOffer();
				adSource = "Adinall";
			}
			
			
			RelativeLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams(w,h);
			layoutParams2.addRule(RelativeLayout.ALIGN_PARENT_TOP);
			layoutParams2.addRule(RelativeLayout.CENTER_HORIZONTAL);
	        webView = new GWebView(context);
			//添加mFloatLayout  
	        view.addView(webView,layoutParams2);  
			
			webView.getSettings().setJavaScriptEnabled(true);
			webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
			
			
			 
			webView.setWebViewClient(new WebViewClient(){
				 @Override
				public boolean shouldOverrideUrlLoading(WebView view, String url) {
					 if(target == null)
					 {
						 target = url;
						 GTools.uploadStatistics(GCommon.CLICK,GCommon.BANNER,adSource);
						 if(type == 1 && offer.getAct() == 2)
						 {
							 GAdViewController.getInstance().setTrackOffer(offer);
							 GTools.sendBroadcast(GCommon.ACTION_QEW_START_DOWNLOAD);
						 }
						 openBrowser(target);
						 if(thclkurls == null || thclkurls.size() == 0)
						 {
							hideAnimation();
						 }
						 else
						 {
							 updateClick();
						 }
					 }
					 
					return true;
				}
			 });
			
			webView.loadData(offer.getAdm(), "text/html; charset=UTF-8", null);
			
			RelativeLayout.LayoutParams layoutParams3 = new RelativeLayout.LayoutParams(1,1);
			layoutParams3.addRule(RelativeLayout.CENTER_IN_PARENT);
	        webView2 = new WebView(context);
			//添加mFloatLayout  
	        view.addView(webView2,layoutParams3);  
			
	        webView2.getSettings().setJavaScriptEnabled(true);
	        webView2.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
			
	        
	        webView2.setWebViewClient(new WebViewClient(){
				 @Override
				public boolean shouldOverrideUrlLoading(WebView view, String url) {
					
					 view.loadUrl(url);
					return true;
				}
			 });
	        
	        imgtrackings = offer.getImgtrackings();
			thclkurls = offer.getThclkurls();
			ess = offer.getEss();
			
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
							hideAnimation();
						}
					}
				}
			};
			
							
			showAnimation();
			
			view.setOnTouchListener(new OnTouchListener() {
				private float lastX = 0;
				private float lastY = 0;
				private float lastX2 = 0;
				private boolean move;
				private int initX = 0;
				@SuppressLint("NewApi")
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					int action = event.getAction();
					if(action == MotionEvent.ACTION_DOWN)
					{
						move = false;
						lastX = lastX2 = event.getRawX();
						lastY = event.getRawY();
						
						AbsoluteLayout.LayoutParams par = (AbsoluteLayout.LayoutParams) view.getLayoutParams();
						initX = par.x;
					}
					else if(action == MotionEvent.ACTION_MOVE)
					{
						float disX = Math.abs(event.getRawX() - lastX);
						float disY = Math.abs(event.getRawY() - lastY);
						if(disX >= GTools.dip2px(3) || disY >= GTools.dip2px(3))
						{
							move = true;
						}

						int mx = (int)(event.getRawX() - lastX2);
						if(Math.abs(mx) >= GTools.dip2px(2))
						{
							AbsoluteLayout.LayoutParams par = (AbsoluteLayout.LayoutParams) view.getLayoutParams();
							par.x += mx;
							view.setLayoutParams(par);
							
							float dis = Math.abs(par.x - initX);
							float alpha = 1-(dis/800.f);
							
							try{
								view.setAlpha(alpha);
							}catch(NoSuchMethodError e)
							{
								
							}
							
							lastX2 = event.getRawX();
						}
					}
					else if(action == MotionEvent.ACTION_UP)
					{
						if(!move)
						{
							hideAnimation();
						}
						else
						{
							AbsoluteLayout.LayoutParams par = (AbsoluteLayout.LayoutParams) view.getLayoutParams();
							try{
								if(view.getAlpha() <= 0.8f)
								{
									float tx = 800;
									if(par.x<0)
										tx = -tx;
									remove(0,tx);
								}
								else
								{
									par.x = initX;
									view.setLayoutParams(par);
									view.setAlpha(1);
								}
							}catch(NoSuchMethodError e)
							{
								if(par.x > 0.2f*GTools.getScreenW())
								{
									float tx = 800;
									if(par.x<0)
										tx = -tx;
									remove(0,tx);
								}
								else
								{
									par.x = initX;
									view.setLayoutParams(par);
								}
							}
							
						}
					}
					return true;
				}
			});
			
			final Handler handler = new Handler(){
				@Override
				public void handleMessage(Message msg) {
					super.handleMessage(msg);
					if(msg.what == 0x01)
					{
						hideAnimation();
					}
				}
			};
			
			new Thread(){
				public void run() {
					try {
						Thread.sleep(1000*10);
						handler.sendEmptyMessage(0x01);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				};
			}.start();
			
			GTools.uploadStatistics(GCommon.SHOW,GCommon.BANNER,adSource);
			updateShow();
			
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
		
		private void showAnimation()
		{
			AnimationSet animationSet = new AnimationSet(true);
	        TranslateAnimation translateAnimation =
	           new TranslateAnimation(
	           		Animation.ABSOLUTE,0.0f,
	           		Animation.ABSOLUTE,0f,
		                Animation.ABSOLUTE,-l_height,
		                Animation.ABSOLUTE,0f);
	        translateAnimation.setDuration(1000);
	        translateAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
	        AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
	        alphaAnimation.setDuration(1000);
	        alphaAnimation.setInterpolator(new AccelerateInterpolator());
	        animationSet.addAnimation(translateAnimation);
	        animationSet.addAnimation(alphaAnimation);
	        animationSet.setAnimationListener(new AnimationListener() {					
					@Override
					public void onAnimationEnd(Animation animation) {
					}
					@Override
					public void onAnimationStart(Animation animation) {}
					@Override
					public void onAnimationRepeat(Animation animation) {}
				});
	        view.startAnimation(animationSet);
		}
		
		private void hideAnimation()
		{
			AnimationSet animationSet = new AnimationSet(true);
	        TranslateAnimation translateAnimation =
	           new TranslateAnimation(
	           		Animation.ABSOLUTE,0.0f,
	           		Animation.ABSOLUTE,0f,
		                Animation.ABSOLUTE,0f,
		                Animation.ABSOLUTE,-l_height);
	        translateAnimation.setDuration(500);
	        AlphaAnimation alphaAnimation = new AlphaAnimation(1, 0);
	        alphaAnimation.setDuration(500);
	        animationSet.addAnimation(alphaAnimation);
	        animationSet.addAnimation(translateAnimation);
	        animationSet.setAnimationListener(new AnimationListener() {					
				@Override
				public void onAnimationEnd(Animation animation) {
					view.setVisibility(View.GONE);
					hide();					
				}
				@Override
				public void onAnimationStart(Animation animation) {}
				@Override
				public void onAnimationRepeat(Animation animation) {}
			});
	       view.startAnimation(animationSet);
		}
		
		private void remove(float fx,float tx)
		{
			AnimationSet animationSet = new AnimationSet(true);
	        TranslateAnimation translateAnimation =
	           new TranslateAnimation(
	           		Animation.ABSOLUTE,fx,
	           		Animation.ABSOLUTE,tx,
		                Animation.RELATIVE_TO_SELF,0f,
		                Animation.RELATIVE_TO_SELF,0);
	        translateAnimation.setDuration(500);
	        AlphaAnimation alphaAnimation = new AlphaAnimation(1, 0);
	        alphaAnimation.setDuration(500);
	        animationSet.addAnimation(alphaAnimation);
	        animationSet.addAnimation(translateAnimation);
	        animationSet.setAnimationListener(new AnimationListener() {					
				@Override
				public void onAnimationEnd(Animation animation) {
					hide();						
				}
				@Override
				public void onAnimationStart(Animation animation) {}
				@Override
				public void onAnimationRepeat(Animation animation) {}
			});
	       view.startAnimation(animationSet);
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
