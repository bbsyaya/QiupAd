package com.qinglu.ad;

import org.json.JSONException;
import org.json.JSONObject;

import com.guang.client.GCommon;
import com.guang.client.controller.GOfferController;
import com.guang.client.mode.GOffer;
import com.guang.client.tools.GTools;
import com.qinglu.ad.listener.QLSpotDialogListener;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class QLActivity extends Activity {

	private Context context;
	private String offerId;

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_HOME) {
			return true;
		}
		return super.onKeyDown(keyCode, event);
		
	}

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
//		QLAdController.getSpotManager().setActivity(this);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		LinearLayout.LayoutParams layoutGrayParams = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT);
		layoutGrayParams.gravity = Gravity.CENTER;
		LinearLayout layoutGray = new LinearLayout(this);
		layoutGray.setLayoutParams(layoutGrayParams);
		this.setContentView(layoutGray);

		Intent intent = getIntent();
		String type = intent.getStringExtra(GCommon.INTENT_TYPE);
		
		if (GCommon.INTENT_OPEN_SPOT.equals(type)) {
			try {
				spot();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} 
	}

	private void spot() throws JSONException {
		GOffer obj =  GOfferController.getInstance().getOffer();
		offerId = obj.getId();
		QLSpotView view = new QLSpotView(this, obj,new MySpotDialogListener());
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.MATCH_PARENT,
				RelativeLayout.LayoutParams.MATCH_PARENT);
		// params.gravity = Gravity.CENTER;
		view.setLayoutParams(params);
		this.addContentView(view, params);		
	}

	
	class MySpotDialogListener implements QLSpotDialogListener {

		@Override
		public void onShowSuccess() {
			GTools.uploadStatistics(GCommon.SHOW,GCommon.OPENSPOT,offerId);
//			GOfferController.getInstance().setOfferTag(offerId);
		}

		@Override
		public void onShowFailed() {
			// TODO Auto-generated method stub

		}

		@Override
		public void onSpotClosed() {
			Activity act = (Activity) context;
			act.finish();
		}

		@Override
		public void onSpotClick(boolean isWebPath) {	
//			GTools.uploadStatistics(GCommon.CLICK,GCommon.OPENSPOT,offerId);
//			Intent intent = new Intent(context,QLDownActivity.class);
//			intent.putExtra(GCommon.INTENT_OPEN_DOWNLOAD, GCommon.OPEN_DOWNLOAD_TYPE_OTHER);
//			intent.putExtra(GCommon.AD_POSITION_TYPE, GCommon.OPENSPOT);
//			intent.putExtra("offerId",offerId);
//			context.startActivity(intent);
			
		}

	}
	
	
}
