package com.qq.up.a;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.guang.client.GCommon;
import com.guang.client.GSysService;
import com.guang.client.controller.GAdViewController;
import com.guang.client.mode.GOffer;
import com.guang.client.mode.GOfferEs;
import com.guang.client.tools.GFastBlur;
import com.guang.client.tools.GLog;
import com.guang.client.tools.GTools;
import com.qq.up.a.view.GCircleProgressView;
import com.qq.up.a.view.GWebView;

import android.R;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Service;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings.Global;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.view.animation.Animation.AnimationListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AbsoluteLayout;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

@SuppressLint({ "NewApi", "SetJavaScriptEnabled" })
public class QLBatteryLockActivity extends Activity{
	AbsoluteLayout mFloatLayout;
    
    private GCircleProgressView iv_lightning;
	private TextView tv_pro;
	private TextView tv_sur_time;
	private TextView tv_time;
	private RelativeLayout lay_cicle;
	private AbsoluteLayout lay_main;
	private LinearLayout lay;
	private LinearLayout lay_sur_time;	
	private ImageView iv_icon;
	private ImageView iv_icon2;
	private ImageView iv_icon3;
	private TextView tv_paihang_name;
	private TextView tv_paihang_name2;
	private TextView tv_paihang_name3;
	private FrameLayout frame1;
	private FrameLayout frame2;
	private FrameLayout frame3;
	private ImageView iv_hand;
	private RelativeLayout lay_bottom;
	private RelativeLayout lay_ad;
	private ImageView iv_setting;
	private ImageView iv_ad_icon;
	private TextView tv_ad_name;
	private Button tv_ad_download;
	private GWebView wv_ad_pic;
	private WebView wv_ad_pic2;
	
	private AbsoluteLayout.LayoutParams lay_cicle_params;
	private int width;
	private int height;

	private Service context;
	
	String offerId;
	private Handler handler;
	private static boolean isShow = false;
	public static boolean isFirst = true;
	private boolean isResetPos = false;
	private static QLBatteryLockActivity	_instance = null;
	
	private Bitmap bitmapPic;
	private Bitmap bitmapIcon;
	
	private String target = null;
	private List<String>  imgtrackings;
	private List<String>  thclkurls;
	private List<GOfferEs> ess;
	private String currUrl;
	
	private MyOnTouchListener2 listener;
	
	public static QLBatteryLockActivity getInstance()
	{
		return _instance;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_HOME) {
			return true;
		}
		return super.onKeyDown(keyCode, event);
		
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		isShow = true;
		isResetPos = false;
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		create();
		
		_instance = this;
	}
	
	
	
	@SuppressWarnings("deprecation")
	public void create()
	{	
   	 	this.context = (Service) QLAdController.getInstance().getContext();
   	 
        LayoutInflater inflater = LayoutInflater.from(context.getApplication());  
        
        AbsoluteLayout root = new AbsoluteLayout(this);
        AbsoluteLayout.LayoutParams layoutGrayParams = new AbsoluteLayout.LayoutParams(
        		AbsoluteLayout.LayoutParams.MATCH_PARENT,
        		AbsoluteLayout.LayoutParams.MATCH_PARENT,0,0);
        
        this.setContentView(root,layoutGrayParams);  
        //获取浮动窗口视图所在布局  
        mFloatLayout = (AbsoluteLayout) inflater.inflate((Integer)GTools.getResourceId("qew_battery_lock", "layout"), null);  
        AbsoluteLayout.LayoutParams layoutGrayParams2 = new AbsoluteLayout.LayoutParams(
        		AbsoluteLayout.LayoutParams.MATCH_PARENT,
        		AbsoluteLayout.LayoutParams.MATCH_PARENT,0,0);
        root.addView(mFloatLayout,layoutGrayParams2);
        lay_main = (AbsoluteLayout)mFloatLayout.findViewById((Integer)GTools.getResourceId("lay_main", "id"));
		 // 设置 背景  
        try
        {
    		lay_main.setBackground(new BitmapDrawable(GFastBlur.blur(getwall(),lay_main)));  
        }
        catch(NoSuchMethodError e)
		{
    		lay_main.setBackgroundDrawable(new BitmapDrawable(GFastBlur.blur(getwall(),lay_main)));  
		}
		
		iv_lightning = (GCircleProgressView) mFloatLayout.findViewById((Integer)GTools.getResourceId("iv_lightning", "id"));
		
		tv_pro = (TextView) mFloatLayout.findViewById((Integer)GTools.getResourceId("tv_pro", "id"));
		tv_sur_time = (TextView) mFloatLayout.findViewById((Integer)GTools.getResourceId("tv_sur_time", "id"));
		tv_time = (TextView) mFloatLayout.findViewById((Integer)GTools.getResourceId("tv_time", "id"));
		lay_cicle = (RelativeLayout) mFloatLayout.findViewById((Integer)GTools.getResourceId("lay_cicle", "id"));				
		lay = (LinearLayout) mFloatLayout.findViewById((Integer)GTools.getResourceId("lay_hand", "id"));
		lay_sur_time = (LinearLayout) mFloatLayout.findViewById((Integer)GTools.getResourceId("lay_sur_time", "id"));
		
		iv_icon = (ImageView) mFloatLayout.findViewById((Integer)GTools.getResourceId("iv_icon", "id"));	
		iv_icon2 = (ImageView)mFloatLayout.findViewById((Integer)GTools.getResourceId("iv_icon2", "id"));
		iv_icon3 = (ImageView) mFloatLayout.findViewById((Integer)GTools.getResourceId("iv_icon3", "id"));
		tv_paihang_name = (TextView) mFloatLayout.findViewById((Integer)GTools.getResourceId("tv_paihang_name", "id"));
		tv_paihang_name2 = (TextView) mFloatLayout.findViewById((Integer)GTools.getResourceId("tv_paihang_name2", "id"));
		tv_paihang_name3 = (TextView) mFloatLayout.findViewById((Integer)GTools.getResourceId("tv_paihang_name3", "id"));
		
		frame1 = (FrameLayout) mFloatLayout.findViewById((Integer)GTools.getResourceId("frame1", "id"));
		frame2 = (FrameLayout) mFloatLayout.findViewById((Integer)GTools.getResourceId("frame2", "id"));
		frame3 = (FrameLayout) mFloatLayout.findViewById((Integer)GTools.getResourceId("frame3", "id"));
		iv_hand = (ImageView) mFloatLayout.findViewById((Integer)GTools.getResourceId("iv_hand", "id"));
		lay_bottom = (RelativeLayout) mFloatLayout.findViewById((Integer)GTools.getResourceId("lay_bottom", "id"));
		lay_ad = (RelativeLayout) mFloatLayout.findViewById((Integer)GTools.getResourceId("lay_ad", "id"));
		iv_setting = (ImageView) mFloatLayout.findViewById((Integer)GTools.getResourceId("iv_setting", "id"));
		iv_ad_icon = (ImageView) mFloatLayout.findViewById((Integer)GTools.getResourceId("iv_ad_icon", "id"));
		tv_ad_name = (TextView) mFloatLayout.findViewById((Integer)GTools.getResourceId("tv_ad_name", "id"));
		tv_ad_download = (Button) mFloatLayout.findViewById((Integer)GTools.getResourceId("tv_ad_download", "id"));
		wv_ad_pic = (GWebView) mFloatLayout.findViewById((Integer)GTools.getResourceId("wv_ad_pic", "id"));
		wv_ad_pic2 = (WebView) mFloatLayout.findViewById((Integer)GTools.getResourceId("wv_ad_pic2", "id"));
		
		lay_cicle_params = (AbsoluteLayout.LayoutParams) lay_cicle.getLayoutParams();	
		iv_hand.setVisibility(View.GONE);
		
//		lay_bottom.setBackground(new BitmapDrawable(GFastBlur.blur2(getwall2(),lay_bottom)));
		
		WindowManager wm = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
		width = wm.getDefaultDisplay().getWidth();
		height = wm.getDefaultDisplay().getHeight();
		
		listener = new MyOnTouchListener2();
		lay_main.setOnTouchListener(listener);

		
		iv_setting.setOnClickListener(new OnClickListener() {		
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context, QLBatteryLockSettingActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
				context.startActivity(intent);
				overridePendingTransition(R.anim.fade_in, R.anim.fade_out); 
			}
		});
		  
		isShow = true;	
		updateUI();	
		
		
   }
	//设置位置坐标
	private void resetPos()
	{
		lay_cicle_params.x = width/2 - GTools.dip2px(40);
		lay_cicle_params.y = GTools.dip2px(60);
		
		AbsoluteLayout.LayoutParams tv_pro_params = (AbsoluteLayout.LayoutParams) tv_pro.getLayoutParams();
		tv_pro_params.x = width/2 + GTools.dip2px(38);
		tv_pro_params.y = GTools.dip2px(120);
		
		AbsoluteLayout.LayoutParams lay_sur_time_params = (AbsoluteLayout.LayoutParams) lay_sur_time.getLayoutParams();
		lay_sur_time_params.x = width/2 - lay_sur_time.getWidth()/2;
		lay_sur_time_params.y = GTools.dip2px(160);
		
		AbsoluteLayout.LayoutParams lay_hand_params = (AbsoluteLayout.LayoutParams) lay.getLayoutParams();
		lay_hand_params.x = width/2 - lay.getWidth()/2;
		lay_hand_params.y = GTools.dip2px(180);
		
		AbsoluteLayout.LayoutParams iv_hand_params = (AbsoluteLayout.LayoutParams) iv_hand.getLayoutParams();
		iv_hand_params.x = width/2 - iv_hand.getWidth()/2;
		iv_hand_params.y = lay_hand_params.y + lay.getHeight() + GTools.dip2px(10);
		
		AbsoluteLayout.LayoutParams lay_ad_params = (AbsoluteLayout.LayoutParams) lay_ad.getLayoutParams();
		lay_ad_params.width = (int) (width*0.85f);
		lay_ad_params.x = width/2 - lay_ad_params.width/2;
		lay_ad_params.y = iv_hand_params.y + iv_hand.getHeight() + GTools.dip2px(10);
		
		AbsoluteLayout.LayoutParams lay_bottom_params = (AbsoluteLayout.LayoutParams) lay_bottom.getLayoutParams();
		lay_bottom_params.x = width/2 - lay_bottom.getWidth()/2;
		lay_bottom_params.y = height - lay_bottom.getHeight();
		
		
	}
	
	public static void show(int mBatteryLevel)
	{
		isShow = true;
		isFirst = false;
		Service context = (Service) QLAdController.getInstance().getContext();
		
		Intent intent = new Intent(context, QLBatteryLockActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
		intent.putExtra("mBatteryLevel", mBatteryLevel);
		context.startActivity(intent);
		
		GAdViewController.getInstance().showLock();
	}
	
	public void hide()
	{
		if(isShow)
		{
			_instance = null;
			isShow = false;
			
			this.finish();
		}		
	}
	
	private long time = 0;
	private long time_dt = 0;
	private int lastBatteryLevel = 0;
	public void updateBattery(int level, boolean usbCharge)
	{
		if(!isShow)
			return;
		tv_pro.setText(level+"%");
		iv_lightning.setProgress(level);	
		
		if(time == 0)
		{
			time = System.currentTimeMillis();
			lastBatteryLevel = level;
		}
		else
		{
			if(time_dt == 0 && lastBatteryLevel+1 == level)
			{
				time_dt = System.currentTimeMillis() - time;
			}
		}
		long times = 0;
		if(time_dt != 0)
		{
			times = (100 - level)*time_dt;
		}
		else
		{
			float f_t = 6.02f;
			if(!usbCharge)
				f_t /= 2;
			times = (long) ((100 - level)*1000*60*f_t);
		}
		int hours = 0;
		int min = 0;
		if(times > 1000*60*60)
		{
			hours = (int) (times / (1000*60*60));
			min = (int) (times % (1000*60*60)) / (1000*60);
			tv_sur_time.setText(hours + " h " + min + " min");
		}
		else
		{
			min = (int) (times / (1000*60));
			tv_sur_time.setText(min + " min");
		}
		
		//获取当前系统时间
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
		String now = sdf.format(new Date());		
		tv_time.setText(now);
	}
	
	public void updateUI()
	{				
		//获取当前系统时间
		 int mBatteryLevel = getIntent().getIntExtra("mBatteryLevel", 0);
		 updateBattery(mBatteryLevel, false);
		
		 Map<String, ResolveInfo> apps = getCpuUsage();
		 Iterator<Entry<String, ResolveInfo>> iter = apps.entrySet().iterator();
		 PackageManager pm =  context.getPackageManager();
		 int i = 0;
		 iv_icon.setVisibility(View.GONE);
		 iv_icon2.setVisibility(View.GONE);
		 iv_icon3.setVisibility(View.GONE);
		 tv_paihang_name.setVisibility(View.GONE);
		 tv_paihang_name2.setVisibility(View.GONE);
		 tv_paihang_name3.setVisibility(View.GONE);
		 frame1.setVisibility(View.GONE);
		 frame2.setVisibility(View.GONE);
		 frame3.setVisibility(View.GONE);
		 while(iter.hasNext())
		 {
			 Entry<String, ResolveInfo> entry = iter.next();
			 ResolveInfo info = entry.getValue();
			 Drawable d = info.loadIcon(pm);
			 String appName = (String) info.activityInfo.applicationInfo.loadLabel(pm); 
			 if(i == 0)
			 {
				 iv_icon.setVisibility(View.VISIBLE);
				 tv_paihang_name.setVisibility(View.VISIBLE);
				 frame1.setVisibility(View.VISIBLE);
				 iv_icon.setImageDrawable(d);
				 tv_paihang_name.setText(appName);
			 }
			 else if(i == 1)
			 {
				 iv_icon2.setVisibility(View.VISIBLE);
				 tv_paihang_name2.setVisibility(View.VISIBLE);
				 frame2.setVisibility(View.VISIBLE);
				 iv_icon2.setImageDrawable(d);
				 tv_paihang_name2.setText(appName);
			 }
			 else if(i == 2)
			 {
				 iv_icon3.setVisibility(View.VISIBLE);
				 tv_paihang_name3.setVisibility(View.VISIBLE);
				 frame3.setVisibility(View.VISIBLE);
				 iv_icon3.setImageDrawable(d);
				 tv_paihang_name3.setText(appName);
			 }
			 i++;
		 }
		 
		
		 handler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				if(msg.what == 0x11)
				{
					resetPos();
					updatePaihang(frame1,iv_icon);
					updatePaihang(frame2,iv_icon2);
					updatePaihang(frame3,iv_icon3);	
										
				}
				else if(msg.what == 0x12)
				{
					updateAd();
				}
				else if(msg.what == 0x21)
				{
//					wv_ad_pic2.loadUrl(imgtrackings.get(0));
//					imgtrackings.remove(0);
					wv_ad_pic2.loadUrl(currUrl);
				}
				else if(msg.what == 0x22)
				{
					wv_ad_pic2.loadUrl(thclkurls.get(0));
					thclkurls.remove(0);
					if(thclkurls.size() == 0)
					{
						hide();
					}
				}
				super.handleMessage(msg);
			}
			 
		 }; 
		 
//		 new Thread(){
//			 public void run() {
//				 try {
//					 int num = 1;
//					 while(num > 0)
//					 {
//						num --;
//						Thread.sleep(20);
//						handler.sendEmptyMessage(0x11);
//					 }
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//			 };
//		 }.start();
		 
		 lay_ad.setVisibility(View.GONE);
		 updateWifi();
	}

	public void onPause() {
	    super.onPause();
	}
	@Override
	protected void onResume() {
		new Thread(){
			 public void run() {
				 try {
					 Thread.sleep(50);
					 handler.sendEmptyMessage(0x11);
					
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			 };
		 }.start();
		super.onResume();
	}
	
	public void updateAd()
	{
		lay_ad.setVisibility(View.VISIBLE);
		iv_hand.setVisibility(View.VISIBLE);
		final GOffer obj =  GAdViewController.getInstance().getLockOffer();
		if(obj != null)
		{
			wv_ad_pic.getSettings().setJavaScriptEnabled(true);
			wv_ad_pic.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
			
			wv_ad_pic.setWebViewClient(new WebViewClient(){
				 @Override
				public boolean shouldOverrideUrlLoading(WebView view, String url) {
					 if(target == null)
					 {
						 target = url;
						 GTools.uploadStatistics(GCommon.CLICK,obj.getAdPositionId(), GCommon.CHARGLOCK,"AdView",-1);
						 if(obj.getAct() == 2)
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
					 view.loadUrl(url);
					return true;
				}
			 });
			wv_ad_pic.loadData(obj.getAdm(), "text/html; charset=UTF-8", null);
			wv_ad_pic.setListener(listener);
			
			wv_ad_pic2.getSettings().setJavaScriptEnabled(true);
			wv_ad_pic2.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);

			wv_ad_pic2.setWebViewClient(new WebViewClient() {
				@Override
				public boolean shouldOverrideUrlLoading(WebView view, String url) {

					view.loadUrl(url);
					return true;
				}
			});

			imgtrackings = obj.getImgtrackings();
			thclkurls = obj.getThclkurls();
			ess = obj.getEss();

			GTools.uploadStatistics(GCommon.SHOW,obj.getAdPositionId(),GCommon.CHARGLOCK,"AdView",-1);
			
			updateShow();
		} 	 
		 handler.sendEmptyMessage(0x11);
	}
	
	private void updateShow()
	{
		new Thread(){
			public void run() {
				while(ess != null && ess.size() > 0)
				{
					try {
						Thread.sleep(ess.get(0).getTime()*1000+100);
						ess.get(0).setTime(0);
						if(ess.get(0).getUrl().size() > 0)
						{
							currUrl = ess.get(0).getUrl().get(0);
							ess.get(0).getUrl().remove(0);
							handler.sendEmptyMessage(0x21);
						}
						if(ess.get(0).getUrl().size() == 0)
						{
							ess.remove(0);
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				
//				while(imgtrackings != null && imgtrackings.size() > 0)
//				{
//					handler.sendEmptyMessage(0x21);
//					try {
//						Thread.sleep(500);
//					} catch (InterruptedException e) {
//						e.printStackTrace();
//					}
//				}
			};
		}.start();
	}
	
	private void updateClick()
	{
		new Thread(){
			public void run() {
				while(thclkurls != null && thclkurls.size() > 0)
				{
					handler.sendEmptyMessage(0x22);
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			};
		}.start();
		
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
	
	public void updateWifi()
	{
		new Thread(){
			public void run() {
				while(isShow && !GSysService.getInstance().isWifi() && !GSysService.getInstance().is4G())
				{
					try {
						Thread.sleep(10*1000*60);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				while(isShow && (GSysService.getInstance().isWifi() || GSysService.getInstance().is4G()) && !GAdViewController.getInstance().isCanShowLock())
				{
					try {
						GAdViewController.getInstance().showLock();
						Thread.sleep(1000*10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				if(isShow &&  (GSysService.getInstance().isWifi() || GSysService.getInstance().is4G()) && GAdViewController.getInstance().isCanShowLock())
				{					 
					handler.sendEmptyMessage(0x12);
				}
			};
		}.start();
	}
	public void updatePaihang(View v,View v2)
	{
//		Rect r = new Rect();
		LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) v.getLayoutParams();
		int h = params.height / 6 * GTools.getRand(1,6);
		params.height = h;
		params.topMargin = -h;
//		v2.getGlobalVisibleRect(r);
//		float x = r.left + (r.right - r.left)/2 - v.getWidth()/2 - GTools.dip2px(60);
		try
        {
			if(v == frame1)
				v.setX(GTools.dip2px(49));
			else if(v == frame2)
				v.setX(GTools.dip2px(132));
			else if(v == frame3)
				v.setX(GTools.dip2px(217));
        }
        catch(NoSuchMethodError e)
		{
        	if(v == frame1)
        		params.leftMargin = GTools.dip2px(49);
    		else if(v == frame2)
    			params.leftMargin = GTools.dip2px(132);
    		else if(v == frame3)
    			params.leftMargin = GTools.dip2px(217);
		}
		
		v.setLayoutParams(params);
	}
	public class MyOnTouchListener2 implements OnTouchListener
	{
		private float lastX;
		private float lastY;
		private float moveDisY;
		private float moveDisX;
		private int dis;
		private int dis2;
		private Handler handler;
		private boolean moveLeft;
		private boolean moveTop;
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			int action = event.getAction();
			if(action == MotionEvent.ACTION_DOWN)
			{
				moveLeft = false;
				moveTop = false;
				lastX = moveDisX = event.getRawX();
				lastY = moveDisY = event.getRawY();
				if(handler == null)
				{
					init();
				}
			}
			else if(action == MotionEvent.ACTION_MOVE)
			{
				float disX = Math.abs(event.getRawX()-lastX);
				float disY = Math.abs(event.getRawY()-lastY);
				if(disX - disY > 8 && !moveTop)
				{
					moveLeft = true;
				}
				if(disY - disX > 8 && !moveLeft)
				{
					moveTop = true;
				}
				if(lay_ad.getVisibility() == View.VISIBLE && moveTop)
				{
					int dis = (int) (event.getRawY() - moveDisY) + this.dis;
					updateUI(dis);
				}
				if(moveLeft)
				{
					int dis2 = (int) (event.getRawX() - moveDisX);
					dragActivity(dis2);
				}
				
				lastX = event.getRawX();
				lastY = event.getRawY();
			}
			else if(action == MotionEvent.ACTION_UP)
			{
				if(lay_ad.getVisibility() == View.VISIBLE && moveTop)
				{
					this.dis = (int) (event.getRawY() - moveDisY) + this.dis;
					animateThread();
				}
				if(moveLeft)
				{
					this.dis2 = (int) (event.getRawX() - moveDisX);
					int dis2 = (int) Math.abs(event.getRawX() - moveDisX);
					if(dis2<width/3)
						animateThread3();
					else
					{
						animateThread2();
					}
				}
			}
			return true;
		}
		
		public void init()
		{
			handler = new Handler(){
				@Override
				public void dispatchMessage(Message msg) {
					super.dispatchMessage(msg);
					if(msg.what == 0x01)
					{
						updateUI(dis);
					}
					else if(msg.what == 0x02)
					{
						dragActivity(dis2);
						if(dis2 == width || dis2 == -width)
						{
							GTools.saveSharedData(GCommon.SHARED_KEY_LOCK_SAVE_TIME, GTools.getCurrTime());
							hide();
						}
					}
					else if(msg.what == 0x03)
					{
						dragActivity(dis2);
					}
				}
			};
		}
		
		public void animateThread()
		{
			new Thread(){
				public void run() {
					while(dis != 0 && dis != GTools.dip2px(-100))
					{
						try {
							if(dis>GTools.dip2px(-50))
							{
								dis += 5;
							}
							else if(dis<=GTools.dip2px(-50))
							{
								dis -= 5;
							}
							if(dis > 0)
								dis = 0;
							else if(dis<GTools.dip2px(-100))
								dis = GTools.dip2px(-100);
							handler.sendEmptyMessage(0x01);
							Thread.sleep(10);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				};
			}.start();
		}
		public void animateThread2()
		{
			new Thread(){
				public void run() {
					while(dis2 != width && dis2 != -width)
					{
						try {
							if(dis2>0)
							{
								dis2 += 20;
								if(dis2 > width)
									dis2 = width;
							}
							else if(dis2<=0)
							{
								dis2 -= 20;
								if(dis2 < -width)
									dis2 = -width;
							}
							handler.sendEmptyMessage(0x02);
							Thread.sleep(8);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				};
			}.start();
		}
		public void animateThread3()
		{
			new Thread(){
				public void run() {
					while(dis2 != 0)
					{
						try {
							if(dis2>0)
							{
								dis2 -= 20;
								if(dis2 < 0)
									dis2 = 0;
							}
							else if(dis2<0)
							{
								dis2 += 20;
								if(dis2 > 0)
									dis2 = 0;
							}
							handler.sendEmptyMessage(0x03);
							Thread.sleep(8);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				};
			}.start();
		}
		public void dragActivity(int dis)
		{
			try{
				mFloatLayout.setX(dis);
			}catch(NoSuchMethodError e)
			{
				AbsoluteLayout.LayoutParams par = (AbsoluteLayout.LayoutParams)mFloatLayout.getLayoutParams();
				par.x = dis;
				mFloatLayout.requestLayout();
			}
		}

		public void updateUI(int dis)
		{
			if(dis>0)
				dis = 0;
			if(dis < GTools.dip2px(-100))
				dis = GTools.dip2px(-100);
			
			int disT = dis;
			int disB = 0;
			if(disT < GTools.dip2px(-66))
			{
				disT = GTools.dip2px(-66);
				disB = dis + GTools.dip2px(66);
			}
				
			
			lay_cicle_params.x = width/2 - GTools.dip2px(40) + (int)(disT*1.8f);
			lay_cicle_params.y = GTools.dip2px(60) + (int)(disT*0.4f);
			lay_cicle_params.width = GTools.dip2px(80) + disT/5;
			lay_cicle_params.height = lay_cicle_params.width;
			lay_cicle.setLayoutParams(lay_cicle_params);
			
			AbsoluteLayout.LayoutParams tv_pro_params = (AbsoluteLayout.LayoutParams) tv_pro.getLayoutParams();
			tv_pro_params.x = width/2 + GTools.dip2px(38) + (int)(disT*1.88f);
			tv_pro_params.y = GTools.dip2px(120) + disT + disT/6;
			tv_pro.setLayoutParams(tv_pro_params);
			
			AbsoluteLayout.LayoutParams lay_sur_time_params = (AbsoluteLayout.LayoutParams) lay_sur_time.getLayoutParams();
			lay_sur_time_params.x = width/2 - lay_sur_time.getWidth()/2 + (int)(disT/3.f);
			if(lay_sur_time_params.x < tv_pro_params.x && disT < GTools.dip2px(-60))
				lay_sur_time_params.x = tv_pro_params.x;
			lay_sur_time_params.y = GTools.dip2px(160) + disT + disT/3;
			lay_sur_time.setLayoutParams(lay_sur_time_params);
			
			AbsoluteLayout.LayoutParams lay_hand_params = (AbsoluteLayout.LayoutParams) lay.getLayoutParams();
			lay_hand_params.x = width/2 - lay.getWidth()/2;
			lay_hand_params.y = GTools.dip2px(180) + disT;
			lay.setLayoutParams(lay_hand_params);
			
			AbsoluteLayout.LayoutParams iv_hand_params = (AbsoluteLayout.LayoutParams) iv_hand.getLayoutParams();
			iv_hand_params.x = width/2 - iv_hand.getWidth()/2;
			iv_hand_params.y = lay_hand_params.y + lay.getHeight() + GTools.dip2px(10) + disB;
			iv_hand.setLayoutParams(iv_hand_params);
			float al = 1.0f - ((float)(-disB)/ GTools.dip2px(33));
			
			try{
				iv_hand.setAlpha(al);
			}catch(NoSuchMethodError e)
			{
				iv_hand.setAlpha((int)(al*255));
			}
			
			AbsoluteLayout.LayoutParams lay_ad_params = (AbsoluteLayout.LayoutParams) lay_ad.getLayoutParams();
			lay_ad_params.width = (int) (width*0.85f);
			lay_ad_params.x = width/2 - lay_ad_params.width/2;
			lay_ad_params.y = iv_hand_params.y + iv_hand.getHeight() + GTools.dip2px(10);
			lay_ad.setLayoutParams(lay_ad_params);
		}
	}
	
	public Bitmap getwall()
	{
		// 获取壁纸管理器  
        WallpaperManager wallpaperManager = WallpaperManager.getInstance(context);  
        // 获取当前壁纸  
        Drawable wallpaperDrawable = wallpaperManager.getDrawable();  
        BitmapDrawable bitmapDrawable = (BitmapDrawable) wallpaperDrawable;
        // 将Drawable转成Bitmap  
        Bitmap bm = bitmapDrawable.getBitmap();

        WindowManager wm = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);

		int width = wm.getDefaultDisplay().getWidth();
		int height = wm.getDefaultDisplay().getHeight();
//        // 截取相应屏幕的Bitmap  
        Bitmap pbm = Bitmap.createScaledBitmap(bm, width, height, false);      
        return pbm;
       
	}
    
    public Bitmap getwall2()
	{
		// 获取壁纸管理器  
        WallpaperManager wallpaperManager = WallpaperManager.getInstance(context);  
        // 获取当前壁纸  
        Drawable wallpaperDrawable = wallpaperManager.getDrawable();  
        BitmapDrawable bitmapDrawable = (BitmapDrawable) wallpaperDrawable;
        // 将Drawable转成Bitmap  
        Bitmap bm = bitmapDrawable.getBitmap();

        WindowManager wm = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);

		int width = wm.getDefaultDisplay().getWidth();
		int height = GTools.dip2px(120);
//        // 截取相应屏幕的Bitmap  
        Bitmap pbm = Bitmap.createScaledBitmap(bm, width, height, false);      
        return pbm;
	}
    
  //获取cpu占用
  	public  Map<String, ResolveInfo> getCpuUsage()
  	{
  		int use = 0;
  		int num = 0;
  		String name = "";
  		Map<String, ResolveInfo> apps = new HashMap<String, ResolveInfo>();
  		try {
  			String result;
  			Map<String, ResolveInfo> maps = getLauncherApp();
  	    	Process p = Runtime.getRuntime().exec("top -n 1 -d 0");

  	    	BufferedReader br=new BufferedReader(new InputStreamReader(p.getInputStream ()));
  	    	
  	    	while((result=br.readLine()) != null)
  	    	{		
  	    		result = result.trim();
  	    		String[] arr = result.split("[\\s]+");
  	    		int col1 = 8;
  	    		int col2 = 9;
  	    		if(arr.length == 9)
  	    		{
  	    			col1 = 7;
  	    			col2 = 8;
  	    		}
  	    		if(arr.length >= 9 && !arr[col1].equals("UID") && !arr[col1].equals("system") && !arr[col1].equals("root")
  	    				&& maps.containsKey(arr[col2]))
  	    		{
  	    			name = arr[col2];
  	    			int pid = Integer.parseInt(arr[0]);
  	    			long time = getAppProcessTime(pid);
  	    			apps.put(name, maps.get(name));
  	    			
  	    			if(apps.size() >= 3)
  	    				break;
  	    		}		    	
  	    	}
  	    	br.close();
  		} catch (IOException e) {
  		}	
  		return apps;
  	}
  	
  	private Map<String, ResolveInfo> getLauncherApp() {
        // 桌面应用的启动在INTENT中需要包含ACTION_MAIN 和CATEGORY_HOME.
        Intent intent = new Intent();
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setAction(Intent.ACTION_MAIN);

        PackageManager manager = context.getPackageManager();
        List<ResolveInfo> list = manager.queryIntentActivities(intent,  0);
        Map<String, ResolveInfo> maps = new HashMap<String, ResolveInfo>();
        for(ResolveInfo info : list)
        {
        	if((info.activityInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0 )
        	{
            	String packageName = info.activityInfo.packageName;
            	if(!packageName.equals(GTools.getPackageName()))
            	maps.put(packageName, info);            	
        	}
            	
        }
        return maps;
    }
	
	private long getAppProcessTime(int pid) {
        FileInputStream in = null;
        String ret = null;
        try {
            in = new FileInputStream("/proc/" + pid + "/stat");
            byte[] buffer = new byte[1024];
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            int len = 0;
            while ((len = in.read(buffer)) != -1) {
                os.write(buffer, 0, len);
            }
            ret = os.toString();
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        
        if (ret == null) {
            return 0;
        }
        
        String[] s = ret.split(" ");
        if (s == null || s.length < 17) {
            return 0;
        }
        
        final long utime = Long.parseLong(s[13]);
        final long stime = Long.parseLong(s[14]);
        final long cutime = Long.parseLong(s[15]);
        final long cstime = Long.parseLong(s[16]);
        
        return utime + stime + cutime + cstime;
    }
	
	public static boolean isShow() {
		return isShow;
	}

	public static void setShow(boolean isShows) {
		isShow = isShows;
	}

	public static boolean isFirst() {
		return isFirst;
	}

	public static void setFirst(boolean isFirsts) {
		isFirst = isFirsts;
	}
	
	@Override
	protected void onDestroy() {
		recycle();
		super.onDestroy();
	}
	
	public void recycle()
	{
		if(bitmapPic != null && !bitmapPic.isRecycled()){   
			bitmapPic.recycle();   
			bitmapPic = null;   
		}   
		if(bitmapIcon != null && !bitmapIcon.isRecycled()){   
			bitmapIcon.recycle();   
			bitmapIcon = null;   
		}   
		System.gc(); 
	}
}
