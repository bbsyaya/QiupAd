package com.qq.up.a;

import java.util.List;

import com.guang.client.GCommon;
import com.guang.client.controller.GAdinallController;
import com.guang.client.mode.GOffer;
import com.guang.client.tools.GTools;
import com.qq.up.a.view.GWebView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

@SuppressLint("SetJavaScriptEnabled")
public class QLBrowserSpotActivity extends Activity{
	private QLBrowserSpotActivity activity;
	private RelativeLayout layout;
	private WebView webView;
	private WebView webView2;
	private String adSource;
	private String target = null;
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
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		activity = this;
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN,
                 WindowManager.LayoutParams.FLAG_FULLSCREEN );
		

		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT);
		layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
		layout = new RelativeLayout(this);
		layout.setLayoutParams(layoutParams);
		this.setContentView(layout);
		
		GOffer obj = GAdinallController.getInstance().getBrowserSpotOffer();
		adSource = "Adinall";
        
        
		int w = GTools.dip2px(300);
		int h =  GTools.dip2px(250);
		
		RelativeLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams(w,h);
		layoutParams2.addRule(RelativeLayout.CENTER_IN_PARENT);
        webView = new GWebView(this);
		//添加mFloatLayout  
        layout.addView(webView,layoutParams2);  
		
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
		
		
		 
		webView.setWebViewClient(new WebViewClient(){
			 @Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				 if(target == null)
				 {
					 target = url;
					 GTools.uploadStatistics(GCommon.CLICK,GCommon.BROWSER_SPOT,adSource);
					 openBrowser(target);
					 if(thclkurls == null || thclkurls.size() == 0)
					 {
						activity.finish();
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
		
		ImageView close = new ImageView(this);
		close.setImageResource((Integer)GTools.getResourceId("qew_browser_close", "drawable"));
		layout.addView(close, closeLayoutParams);
		
		
		RelativeLayout.LayoutParams layoutParams3 = new RelativeLayout.LayoutParams(1,1);
		layoutParams3.addRule(RelativeLayout.CENTER_IN_PARENT);
        webView2 = new WebView(this);
		//添加mFloatLayout  
        layout.addView(webView2,layoutParams3);  
		
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
						activity.finish();
					}
				}
			}
		};
        	
		
		//关闭事件
		close.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				activity.finish();
				
			}
		});
		

		show();
		
		GTools.uploadStatistics(GCommon.SHOW,GCommon.BROWSER_SPOT,adSource);
		
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
		AlphaAnimation animation = new AlphaAnimation(0, 1);
		animation.setDuration(500);
		animationSet.addAnimation(animation);
		layout.startAnimation(animationSet);
	}
	
	@Override
	protected void onDestroy() {
		recycle();
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
	
	public void recycle()
	{
//		if(bitmap != null && !bitmap.isRecycled()){   
//			bitmap.recycle();   
//			bitmap = null;   
//		}   
// 
//		System.gc(); 
	}
}
