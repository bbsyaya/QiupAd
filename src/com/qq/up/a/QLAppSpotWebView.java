package com.qq.up.a;



import com.guang.client.tools.GTools;
import com.qq.up.a.view.GWebView;

import android.annotation.SuppressLint;
import android.app.Service;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

@SuppressLint("SetJavaScriptEnabled")
public class QLAppSpotWebView{
	WindowManager.LayoutParams wmParams;  
    //创建浮动窗口设置布局参数的对象  
    WindowManager mWindowManager;
    private Service context;
    private static QLAppSpotWebView _instance;
	private boolean isShow = false;
	
	private RelativeLayout root;
	private WebView webView;
	private String target = null;
	private Handler handler;
	private boolean isTimeOut = false;
	private ImageView close;
	
	private QLAppSpotWebView(){}
	public static QLAppSpotWebView getInstance()
	{
		if(_instance == null)
		{
			_instance = new QLAppSpotWebView();
		}
		return _instance;
	}
	
	@SuppressLint("NewApi") public void show(final String target)
	{
		this.target = target;
		this.context = (Service) QLAdController.getInstance().getContext();
		hide();
		
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
		
		RelativeLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT);
		layoutParams2.addRule(RelativeLayout.CENTER_IN_PARENT);
        webView = new WebView(context);
		//添加mFloatLayout  
        root.addView(webView,layoutParams2);  
		
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
				
		webView.setWebViewClient(new WebViewClient(){
			 @Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				 Log.e("-----------", "isTimeOut="+isTimeOut+"  "+url);
				 if(isTimeOut)
				 {
					 openBrowser(url);
					 hide();
				 }
				 else
				 {
					 webView.loadUrl(url);
					 handler.removeMessages(0x01);
	            	 handler.sendEmptyMessageDelayed(0x01, 3000);
				 }
				return true;
			}
			 
		 });
		
		webView.loadUrl(target);
		
		//关闭按钮
		RelativeLayout.LayoutParams closeLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
		int marginX = GTools.dip2px(10);
		int marginY = GTools.dip2px(10);
		closeLayoutParams.setMargins(0, marginY, marginX, 0);
		closeLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		closeLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		
		close = new ImageView(context);
		close.setImageResource((Integer)GTools.getResourceId("qew_browser_close", "drawable"));
		root.addView(close, closeLayoutParams);
		
		close.setVisibility(View.GONE);
		

		//关闭事件
		close.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				hide();
				
			}
		});

		
		handler = new Handler(){
			@Override
			public void dispatchMessage(Message msg) {
				super.dispatchMessage(msg);
				if(msg.what == 0x01)
				{
					isTimeOut = true;
					close.setVisibility(View.VISIBLE);
					Log.e("-----------", "dispatchMessage="+isTimeOut);
				}
			}
		};
				
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
	
	
	public void openBrowser(String url)
	{
		GTools.openBrowser(url, context);
	}
}
