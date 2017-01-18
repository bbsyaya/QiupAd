package com.qinglu.ad;



import com.guang.client.GCommon;
import com.guang.client.controller.GSMController;
import com.guang.client.mode.GSMOffer;
import com.guang.client.tools.GLog;
import com.guang.client.tools.GTools;

import android.R;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.KeyEvent;
import android.view.LayoutInflater;
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
import android.widget.AbsoluteLayout;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

@SuppressLint({ "NewApi", "HandlerLeak", "ResourceAsColor" })
public class QLBannerActivity extends Activity{
	private Activity context;
	private RelativeLayout view;
	private int l_height;
	private String target;
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
		
		l_height = GTools.dip2px(80);
		
		final LayoutParams p = getWindow().getAttributes();  //获取对话框当前的参数值    
//		p.width = width*2;  
		p.height = l_height;    
        p.x = 0;
        p.y = -height/2 + l_height/2 + title_h;
        getWindow().setAttributes(p); 

        
        GSMOffer obj = GSMController.getInstance().getOffer();
        String bannerPicPath = obj.getLink();
        target = obj.getTarget();
        
        AbsoluteLayout root = new AbsoluteLayout(this);
        AbsoluteLayout.LayoutParams rootlayoutParams = new AbsoluteLayout.LayoutParams(p.width,p.height,0,0);
//		rootlayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
		
		view = new RelativeLayout(this);
		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, l_height);
		layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
		view.setLayoutParams(layoutParams);
		
		root.addView(view);
		
		ImageView iv_banner_banner = new ImageView(this);
		
		RelativeLayout.LayoutParams imageLayoutParams = new RelativeLayout.LayoutParams((int) (width*0.85f), GTools.dip2px(50));		
        imageLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        imageLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		iv_banner_banner.setLayoutParams(imageLayoutParams);
		iv_banner_banner.setScaleType(ScaleType.FIT_XY);
		
		Bitmap bitmap = BitmapFactory.decodeFile(this.getFilesDir().getPath()+"/"+ bannerPicPath) ;
		iv_banner_banner.setImageBitmap(bitmap);
		view.addView(iv_banner_banner);
		
		this.setContentView(root,rootlayoutParams);
						
		show();
		
		view.setOnTouchListener(new OnTouchListener() {
			private float lastX = 0;
			private float lastY = 0;
			private float lastX2 = 0;
			private boolean move;
			private int initX = 0;
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
						view.setAlpha(alpha);
						
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
						if(view.getAlpha() < 0.4f)
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
					Thread.sleep(1000*60);
					handler.sendEmptyMessage(0x01);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			};
		}.start();
		
		GTools.uploadStatistics(GCommon.SHOW,GCommon.BANNER,"00000");
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
				if(isClick)
				{
					Uri uri = Uri.parse(target);
		            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
		            startActivity(intent);
		            GTools.uploadStatistics(GCommon.CLICK,GCommon.BANNER,"00000");
				}
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
}
