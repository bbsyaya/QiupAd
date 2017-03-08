package com.qinglu.ad;



import java.util.List;

import com.guang.client.GCommon;
import com.guang.client.controller.GAdinallController;
import com.guang.client.mode.GOffer;
import com.guang.client.tools.GTools;
import com.qinglu.ad.view.GWebView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AbsoluteLayout;
import android.widget.RelativeLayout;

@SuppressLint("SetJavaScriptEnabled")
@SuppressWarnings("deprecation")
public class QLBannerActivity extends Activity{
	private QLBannerActivity context;
	private RelativeLayout view;
	private int l_height;
	private String target;
	Bitmap bitmapPic;
	
	private String adSource;
	
	private WebView webView;
	private WebView webView2;
	private Handler handler;
	private List<String>  imgtrackings;
	private List<String>  thclkurls;
	
	public void onResume() {
	    super.onResume();
	}
	public void onPause() {
	    super.onPause();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			return true;
		}
		return super.onKeyDown(keyCode, event);
		
	}

	
	@SuppressLint("HandlerLeak")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN,
                 WindowManager.LayoutParams.FLAG_FULLSCREEN );
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
		
		WindowManager wm = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
		int width = wm.getDefaultDisplay().getWidth();
		int height = wm.getDefaultDisplay().getHeight();
		
		int title_h = 0;
		int resourceId = getResources().getIdentifier("status_bar_height",
				"dimen", "android");
		if (resourceId > 0) {
			title_h = getResources().getDimensionPixelSize(resourceId);
		}
		
		l_height = (int) (height*0.66f*0.156f);;
		
		final LayoutParams p = getWindow().getAttributes();  //获取对话框当前的参数值    
//		p.width = width*2;  
		p.height = l_height;    
        p.x = 0;
        p.y = -height/2 + l_height/2 + title_h;
        getWindow().setAttributes(p); 
        
        AbsoluteLayout root = new AbsoluteLayout(this);
        AbsoluteLayout.LayoutParams rootlayoutParams = new AbsoluteLayout.LayoutParams(p.width,p.height,0,0);
// 		rootlayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        view = new RelativeLayout(this);
 		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, l_height);
 		layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
 		view.setLayoutParams(layoutParams);
 		
 		root.addView(view);
 		
 		GOffer offer = GAdinallController.getInstance().getBannerOffer();
		adSource = "Adinall";
		
		int w = (int) (GTools.getScreenW()*0.66f);
		int h = (int) (w*0.156f);
		
		RelativeLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams(w,h);
		layoutParams2.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		layoutParams2.addRule(RelativeLayout.CENTER_HORIZONTAL);
        webView = new GWebView(this);
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
					 openBrowser(target);
					 if(thclkurls == null || thclkurls.size() == 0)
					 {
						hide(false);
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
        webView2 = new WebView(this);
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
		
		handler = new Handler(){
			@Override
			public void dispatchMessage(Message msg) {
				super.dispatchMessage(msg);
				if(msg.what == 0x01)
				{
					webView2.loadUrl(imgtrackings.get(0));
					imgtrackings.remove(0);
				}
				else if(msg.what == 0x02)
				{
					webView2.loadUrl(thclkurls.get(0));
					thclkurls.remove(0);
					if(thclkurls.size() == 0)
					{
						hide(false);
					}
				}
			}
		};
		
        
 		this.setContentView(root,rootlayoutParams);
		
						
		show();
		
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
						hide(true);
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
					hide(false);
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
	
	
	private void updateShow()
	{
		new Thread(){
			public void run() {
				while(imgtrackings != null && imgtrackings.size() > 0)
				{
					handler.sendEmptyMessage(0x01);
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
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
	
	private void hide(final boolean isClick)
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
				context.finish();						
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
				context.finish();						
			}
			@Override
			public void onAnimationStart(Animation animation) {}
			@Override
			public void onAnimationRepeat(Animation animation) {}
		});
       view.startAnimation(animationSet);
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	public void openBrowser(String url)
	{
		PackageManager packageMgr = getPackageManager();
		Intent intent = packageMgr.getLaunchIntentForPackage("com.android.chrome");
		if(intent == null)
		{
			intent = new Intent();
		}
        intent.setAction(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setData(Uri.parse(url));
        startActivity(intent);
	}
	
}
