package com.qinglu.ad;


import org.json.JSONException;
import org.json.JSONObject;

import com.guang.client.GCommon;
import com.guang.client.controller.GOfferController;
import com.guang.client.controller.GSMController;
import com.guang.client.mode.GOffer;
import com.guang.client.mode.GSMOffer;
import com.guang.client.tools.GTools;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

@SuppressLint("NewApi")
public class QLNotifyActivity extends Activity{
	private Activity context;
	
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
		
		final int l_height = GTools.dip2px(80);
		
		final LayoutParams p = getWindow().getAttributes();  //获取对话框当前的参数值    
		//p.width = width-50;  
		p.height = l_height;    
        p.x = 0;
        p.y = -height/2 + l_height/2 + title_h;
        getWindow().setAttributes(p); 
        
		
                
        GSMOffer obj = GSMController.getInstance().getOffer();
        String bannerPicPath = obj.getLink();
        final String target = obj.getTarget();
		
		RelativeLayout.LayoutParams layoutGrayParams = new RelativeLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT);
		layoutGrayParams.addRule(RelativeLayout.CENTER_IN_PARENT);
		final RelativeLayout layoutGray = new RelativeLayout(this);
		layoutGray.setLayoutParams(layoutGrayParams);
		
		LayoutInflater inflater = LayoutInflater.from(context.getApplication());
		// 获取浮动窗口视图所在布局
		final RelativeLayout view = (RelativeLayout) inflater.inflate((Integer)GTools.getResourceId("qew_banner", "layout"), null);		
		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, l_height);

		ImageView iv_banner_banner = (ImageView) view.findViewById((Integer)GTools.getResourceId("iv_banner_banner", "id"));

		Bitmap bitmap = BitmapFactory.decodeFile(this.getFilesDir().getPath()+"/"+ bannerPicPath) ;
		iv_banner_banner.setImageBitmap(bitmap);
		
		layoutGray.addView(view,layoutParams);
		
		this.setContentView(layoutGray);
						
		AnimationSet animationSet = new AnimationSet(true);
         TranslateAnimation translateAnimation =
            new TranslateAnimation(
            		Animation.RELATIVE_TO_SELF,0.0f,
            		Animation.RELATIVE_TO_SELF,0f,
	                Animation.RELATIVE_TO_SELF,-l_height,
	                Animation.RELATIVE_TO_SELF,0f);
         translateAnimation.setDuration(1000);
         animationSet.addAnimation(translateAnimation);
         animationSet.setAnimationListener(new AnimationListener() {					
				@Override
				public void onAnimationEnd(Animation animation) {
//					GTools.uploadStatistics(GCommon.SHOW,GCommon.BANNER,offerId);
//					GOfferController.getInstance().setOfferTag(offerId);					
				}
				@Override
				public void onAnimationStart(Animation animation) {}
				@Override
				public void onAnimationRepeat(Animation animation) {}
			});
         view.startAnimation(animationSet);

		 //上传统计信息
		//GTools.uploadPushStatistics(GCommon.PUSH_TYPE_MESSAGE_PIC,GCommon.UPLOAD_PUSHTYPE_SHOWNUM,pushId);
		
		view.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				AnimationSet animationSet = new AnimationSet(true);
		         TranslateAnimation translateAnimation =
		            new TranslateAnimation(
		            		Animation.RELATIVE_TO_SELF,0.0f,
		            		Animation.RELATIVE_TO_SELF,0f,
			                Animation.RELATIVE_TO_SELF,0f,
			                Animation.RELATIVE_TO_SELF,-l_height);
		         translateAnimation.setDuration(1000);
		         animationSet.addAnimation(translateAnimation);
		         animationSet.setAnimationListener(new AnimationListener() {					
					@Override
					public void onAnimationEnd(Animation animation) {
//						GTools.uploadStatistics(GCommon.CLICK,GCommon.BANNER,offerId);
//						Intent intent = new Intent(context,QLDownActivity.class);
//						intent.putExtra(GCommon.INTENT_OPEN_DOWNLOAD, GCommon.OPEN_DOWNLOAD_TYPE_OTHER);
//						intent.putExtra(GCommon.AD_POSITION_TYPE, GCommon.BANNER);
//						intent.putExtra("offerId",offerId);
//						context.startActivity(intent);
						
						Uri uri = Uri.parse(target);
		                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
		                startActivity(intent);
		
						context.finish();						
					}
					@Override
					public void onAnimationStart(Animation animation) {}
					@Override
					public void onAnimationRepeat(Animation animation) {}
				});
		        view.startAnimation(animationSet);
			}
		});
		
		new Thread(){
			public void run() {
				try {
					Thread.sleep(1000*10);
					context.finish();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			};
		}.start();
		
		
	}
}
