package com.qq.up.a;

import java.lang.reflect.Field;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.guang.client.GCommon;
import com.guang.client.controller.GSelfController;
import com.guang.client.controller.GUserController;
import com.guang.client.mode.GAdPositionConfig;
import com.guang.client.mode.GOffer;
import com.guang.client.tools.GTools;
import com.qq.up.MainActivity;
import com.qq.up.R;
import com.qq.up.l.GService;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

@SuppressLint("SetJavaScriptEnabled")
public class QLShortcutActivity extends Activity{
	private QLShortcutActivity activity;
	private RelativeLayout layout;
	private WebView webView;
	private ProgressBar bar;
	private int backNum = 0;
	private Handler handler;
	private String url;
	private JSONArray ads;
	private long adPositionId;
	private int adPositionType;
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			exit();
			return true;
		}
		return super.onKeyDown(keyCode, event);
		
	}
	
//	@Override
	protected void onCreate2(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		activity = this;
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		RelativeLayout lay = new RelativeLayout(this);
		this.setContentView(lay);
		
		Intent intent = new Intent();  
		intent.setAction(GCommon.ACTION_QEW_APP_PUSH);  
		sendBroadcast(intent); 
		
		this.finish();
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
				
		activity = this;
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		LayoutInflater inflater = LayoutInflater.from(getApplication());
		layout = (RelativeLayout) inflater.inflate((Integer)getResourceId("qew_shortcut", "layout"), null);
		this.setContentView(layout);
		
		url = getIntent().getStringExtra("url");
		adPositionId = getIntent().getLongExtra("adPositionId", -1);
		adPositionType = getIntent().getIntExtra("adPositionType", 14);
//		GAdPositionConfig config = GUserController.getMedia().getConfig(adPositionId);
//		adPositionType = config.getAdPositionType();
		
//		bar =  (ProgressBar) layout.findViewById((Integer)getResourceId("pb_shortcut_bar", "id"));
//		webView = (WebView) layout.findViewById((Integer)getResourceId("wv_shortcut_webView", "id"));
//		 
//		webView.getSettings().setJavaScriptEnabled(true);
//		webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
//		webView.setWebViewClient(new WebViewClient(){
//			 @Override
//			public boolean shouldOverrideUrlLoading(WebView view, String url2) {
//				 if(url2 != null && url2.equals(url))
//				 {
//					 view.loadUrl(url2);
//				 }
//				 else
//				 {
//					 if(adPositionType == GCommon.SHORTCUT_APP)
//					 {
//						 showAd(url2);
//					 }
//					 else
//					 {
//						 browserBreak(url2);
//					 }
//				 }
//				return true;
//			}
//		 });
//		 
//		 webView.setWebChromeClient(new WebChromeClient() {
//	          @Override
//	          public void onProgressChanged(WebView view, int newProgress) {
//	              if (newProgress == 100) {
//	                  bar.setVisibility(View.INVISIBLE);
//	              } else {
//	                  if (View.INVISIBLE == bar.getVisibility()) {
//	                      bar.setVisibility(View.VISIBLE);
//	                  }
//	                  bar.setProgress(newProgress);
//	              }
//	              super.onProgressChanged(view, newProgress);
//	          }
//	      });
//		 
//		 
//		 webView.loadUrl(url);
//		 
//		 handler = new Handler(){
//			 @Override
//			public void dispatchMessage(Message msg) {
//				super.dispatchMessage(msg);
//				if(msg.what == 0x01)
//				{
//					backNum = 0;
//				}
//			}
//		 };
		 
		if(QLAdController.getInstance().getContext() != null)
		 GTools.uploadStatistics(GCommon.CLICK,adPositionId,adPositionType,"self",-1);
		 
		 if(url != null && !"".equals(url))
			 GTools.openBrowser(url,this);
		 
		 finish();
//		 ads = null;
//		 if(adPositionType == GCommon.SHORTCUT_APP)
//			 GTools.httpGetRequest(GCommon.URI_GET_SELF_OFFER,this, "revAppAd", null);
	}
	
	public void revAppAd(Object ob,Object rev)
	{
		try{
			ads = new JSONArray(rev.toString());
		}
		catch (JSONException e) {
			e.printStackTrace();
		}	
	}
	
	public void showAd(String url)
	{
		Log.e("--------------","url="+url);
		if(ads != null && url != null)
		{
			for(int i=0;i<ads.length();i++)
			{
				try {
					JSONObject obj = ads.getJSONObject(i);
					String apkPath = obj.getString("apkPath");
					if(url.equals(apkPath))
					{
						long id = obj.getLong("id");
						String packageName = obj.getString("packageName");
						String appName = obj.getString("appName");
						String appDesc = obj.getString("appDesc");
						float apkSize = (float) obj.getDouble("apkSize");
						String iconPath = obj.getString("iconPath");
						String picPath = obj.getString("picPath");
						
						GOffer appOpenSpotOffer = new GOffer(id, packageName, appName, 
								appDesc, apkSize, iconPath, picPath, apkPath);
						appOpenSpotOffer.setAdPositionId(adPositionId);
						appOpenSpotOffer.setClick(true);
						GSelfController.getInstance().setAppOpenSpotOffer(appOpenSpotOffer);
						GTools.downloadRes(GCommon.CDN_ADDRESS+iconPath, this, "downloadAppCallback", iconPath, true);
						break;
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public void downloadAppCallback(Object ob,Object rev)
	{
		Context context = QLAdController.getInstance().getContext();
		Intent intent = new Intent();  
		intent.setAction(GCommon.ACTION_QEW_APP_SHOWTODOWNLOAD);  
		context.sendBroadcast(intent);
	}
	
	public void browserBreak(String url)
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
	
	public void exit()
	{
		if(backNum == 1)
		{
			this.finish();
		}
		else
		{
			backNum++;
			Toast.makeText(this, "Tap again to exit", Toast.LENGTH_SHORT).show();
			
			new Thread(){
				public void run() {
					try {
						Thread.sleep(800);
						handler.sendEmptyMessage(0x01);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				};
			}.start();
		}
	}
	
	//获取资源id
	public  Object getResourceId(String name, String type) 
	{
		Context context = activity;
		String className = context.getPackageName() +".R";
		try {
		Class<?> cls = Class.forName(className);
		for (Class<?> childClass : cls.getClasses()) 
		{
			String simple = childClass.getSimpleName();
			if (simple.equals(type)) 
			{
				for (Field field : childClass.getFields()) 
				{
					String fieldName = field.getName();
					if (fieldName.equals(name)) 
					{
						return field.get(null);
					}
				}
			}
		}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public int dip2px(float dipValue) {  
    	Context context = activity;
        final float scale = context.getResources().getDisplayMetrics().density;  
        return (int) (dipValue * scale + 0.5f);  
    }  
}
