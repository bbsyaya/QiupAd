package com.qinglu.ad;

import java.util.ArrayList;
import java.util.List;

import com.guang.client.GCommon;
import com.guang.client.controller.GAPPNextController;
import com.guang.client.controller.GOfferController;
import com.guang.client.mode.GOffer;
import com.guang.client.tools.GTools;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.Animation.AnimationListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class QLAppSpotActivity extends Activity{
	private static QLAppSpotActivity activity;
	private RelativeLayout layout;
	private Button bt_appstartup_close;
	private ImageView iv_appstartup_pic;
	private Button bt_appstartup_detail;
	private GOffer obj;
	private Bitmap bitmapPic;
	private Bitmap bitmapIcon;
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			return true;
		}
		return super.onKeyDown(keyCode, event);
		
	}
	
	public static QLAppSpotActivity getInstance()
	{
		return activity;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		activity = this;
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN,
                 WindowManager.LayoutParams.FLAG_FULLSCREEN );
		

		LayoutInflater inflater = LayoutInflater.from(getApplication());
		layout = (RelativeLayout) inflater.inflate((Integer)GTools.getResourceId("qew_appstartup", "layout"), null);
	
		this.setContentView(layout);
		
		obj = GAPPNextController.getInstance().getSpotOffer();
        String picPath = obj.getImageUrl();
        String iconPath = obj.getIconUrl();
        
        bt_appstartup_close = (Button) layout.findViewById((Integer)GTools.getResourceId("bt_appstartup_close", "id"));
        ImageView iv_appstartup_icon = (ImageView) layout.findViewById((Integer)GTools.getResourceId("iv_appstartup_icon", "id"));
        TextView tv_appstartup_appName = (TextView) layout.findViewById((Integer)GTools.getResourceId("tv_appstartup_appName", "id"));
        iv_appstartup_pic = (ImageView) layout.findViewById((Integer)GTools.getResourceId("iv_appstartup_pic", "id"));
        TextView tv_appstartup_dsc = (TextView) layout.findViewById((Integer)GTools.getResourceId("tv_appstartup_dsc", "id"));
        bt_appstartup_detail = (Button) layout.findViewById((Integer)GTools.getResourceId("bt_appstartup_detail", "id"));
        //图片
        bitmapPic = BitmapFactory.decodeFile(this.getFilesDir().getPath()+"/"+ picPath);
        iv_appstartup_pic.setImageBitmap(bitmapPic);
        bitmapIcon = BitmapFactory.decodeFile(this.getFilesDir().getPath()+"/"+ iconPath);
        iv_appstartup_icon.setImageBitmap(bitmapIcon);
        tv_appstartup_appName.setText(obj.getAppName());
        tv_appstartup_dsc.setText(obj.getAppDesc());
		
		show();
		
		GTools.uploadStatistics(GCommon.SHOW,GCommon.APP_SPOT,"00000");
	}
	
	private void show()
	{
		AnimationSet animationSet = new AnimationSet(true);
		AlphaAnimation animation = new AlphaAnimation(0, 1);
		animation.setDuration(500);
		animationSet.addAnimation(animation);
		animationSet.setAnimationListener(new AnimationListener() {					
			@Override
			public void onAnimationEnd(Animation animation) {
				//关闭事件
		        bt_appstartup_close.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						activity.finish();
					}
				});
		        
		        iv_appstartup_pic.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Uri uri = Uri.parse(obj.getUrlApp());
		                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
		                startActivity(intent);
		                
		                activity.finish();
					}
				});
				
				bt_appstartup_detail.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Uri uri = Uri.parse(obj.getUrlApp());
		                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
		                startActivity(intent);
		                
		                activity.finish();
					}
				});
//		        List<View> list = new ArrayList<View>();
//			    list.add(iv_appstartup_pic);
//			    list.add(bt_appstartup_detail);
//			    GOfferController.getInstance().registerView(GCommon.APP_SPOT,iv_appstartup_pic, list, obj.getCampaign());	
			}
			@Override
			public void onAnimationStart(Animation animation) {}
			@Override
			public void onAnimationRepeat(Animation animation) {}
		});
		layout.startAnimation(animationSet);
		
		
	}
	
	public static void hide()
	{
		if(activity!=null)
		{
			activity.finish();
			activity = null;
		}
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
