package com.qinglu.ad;



import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.guang.client.tools.GLog;
import com.guang.client.tools.GTools;
import com.qinglu.ad.view.AVLoadingIndicatorView;
import com.qinglu.ad.view.indicators.PacmanIndicator;
import com.qinglu.ad.view.indicators.SemiCircleSpinIndicator;
import com.qinglu.ad.view.indicators.SquareSpinIndicator;
import com.qinglu.ad.view.indicators.TriangleSkewSpinIndicator;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

public class QLAppSpotActivity extends Activity{
	private static QLAppSpotActivity activity;
	private RelativeLayout layout;

	private long spotAdPositionId;
	private String appName;

	private InterstitialAd mInterstitialAd;

	private int num = 0;
	private List<String> loads = new ArrayList<String>();
	private List<String> bgColors = new ArrayList<String>();
	private List<String> loadColors = new ArrayList<String>();
	private AVLoadingIndicatorView vl;
	
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


		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT);
		layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
		layout = new RelativeLayout(this);
		layout.setLayoutParams(layoutParams);
		this.setContentView(layout);

		initLoads();

		int loadNum = GTools.getSharedPreferences().getInt(GTools.getPackageName()+"load",-1);
		if(loadNum == -1)
		{
			loadNum = GTools.getSharedPreferences().getInt("loadNum",-1);
			loadNum += 1;
			if(loadNum >= 10)
				loadNum = 0;

			GTools.saveSharedData("loadNum",loadNum);
			GTools.saveSharedData(GTools.getPackageName()+"load",loadNum);
		}

		layout.setBackgroundColor(Color.parseColor(bgColors.get(loadNum)));

		vl = new AVLoadingIndicatorView(this);
		RelativeLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		layoutParams2.addRule(RelativeLayout.CENTER_IN_PARENT);
		vl.setIndicatorColor(Color.parseColor(loadColors.get(loadNum)));
		layout.addView(vl,layoutParams2);
		vl.setIndicator(loads.get(loadNum));



		this.spotAdPositionId = getIntent().getLongExtra("adPositionId",0);
		this.appName = getIntent().getStringExtra("appName");



//		layout.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//
//				layout.removeView(vl);
//
//				num++;
//				if(num >= loads.size())
//					num = 0;
//				vl = new AVLoadingIndicatorView(activity);
//				RelativeLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams(
//						LinearLayout.LayoutParams.WRAP_CONTENT,
//						LinearLayout.LayoutParams.WRAP_CONTENT);
//				layoutParams2.addRule(RelativeLayout.CENTER_IN_PARENT);
//				layout.addView(vl,layoutParams2);
//				vl.setIndicator(loads.get(num));
//			}
//		});

		showAppSpot();
		
//		GTools.uploadStatistics(GCommon.SHOW,GCommon.APP_SPOT,"AdMob");
	}

	private void initLoads()
	{
		loads.add("BallClipRotatePulseIndicator");
		loads.add("BallGridPulseIndicator");
		loads.add("BallPulseIndicator");
		loads.add("BallRotateIndicator");
//		loads.add("BallSpinFadeLoaderIndicator");
		loads.add("BallTrianglePathIndicator");
		loads.add("LineScaleIndicator");
		loads.add("PacmanIndicator");
		loads.add("SemiCircleSpinIndicator");
		loads.add("SquareSpinIndicator");
		loads.add("TriangleSkewSpinIndicator");

		bgColors.add("#ffffff");
		bgColors.add("#ffffff");
		bgColors.add("#ffffff");
		bgColors.add("#ffffff");
		bgColors.add("#ffffff");
		bgColors.add("#ffffff");
		bgColors.add("#ffffff");
		bgColors.add("#ffffff");
		bgColors.add("#ffffff");
		bgColors.add("#ffffff");

		loadColors.add("#0500ff");
		loadColors.add("#0020ff");
		loadColors.add("#0030ff");
		loadColors.add("#0040ff");
		loadColors.add("#0050ff");
		loadColors.add("#0060ff");
		loadColors.add("#0070ff");
		loadColors.add("#0080ff");
		loadColors.add("#0090ff");
		loadColors.add("#1000ff");
	}

	public void showAppSpot()
	{
		mInterstitialAd = new InterstitialAd(this);
		mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");

		mInterstitialAd.setAdListener(new AdListener() {
			@Override
			public void onAdLoaded() {
				super.onAdLoaded();
				GLog.e("--------------", "onAdLoaded");
				mInterstitialAd.show();
			}

			@Override
			public void onAdOpened() {
				super.onAdOpened();
				hide();
				GLog.e("--------------", "onAdOpened");
			}

			@Override
			public void onAdFailedToLoad(int i) {
				super.onAdFailedToLoad(i);
				hide();
				GLog.e("--------------", "onAdFailedToLoad");
			}

			@Override
			public void onAdClosed() {
				super.onAdClosed();
				hide();
				GLog.e("--------------", "onAdClosed");
			}

			@Override
			public void onAdLeftApplication() {
				super.onAdLeftApplication();
				GLog.e("--------------", "onAdLeftApplication");
			}
		});

		AdRequest adRequest = new AdRequest.Builder()
				.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
				.build();

		mInterstitialAd.loadAd(adRequest);


	}
	
	public static void hide()
	{
		if(activity!=null)
		{
			activity.finish();
			activity = null;
		}
	}
	


}
