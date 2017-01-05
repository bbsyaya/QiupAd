package com.qinglu.ad;


import org.json.JSONObject;

import com.guang.client.mode.GOffer;
import com.guang.client.tools.GTools;
import com.qinglu.ad.listener.QLSpotDialogListener;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class QLSpotView extends RelativeLayout{
	private  int s = 3;//关闭时间
	private Bitmap viewBm;
	private QLSpotDialogListener dialogListener;
	private QLSize size;
	
	public QLSpotView(Context context) {
		super(context);
	}
	
	
	public QLSpotView(Context context,GOffer obj,QLSpotDialogListener dialogListener) {
		super(context);
		this.dialogListener = dialogListener;
		this.getSpotViewApp(context,obj);
	}
	
	

	public QLSize getSize() {
		return size;
	}

	public void setSize(QLSize size) {
		this.size = size;
	}

	public QLSpotDialogListener getDialogListener() {
		return dialogListener;
	}

	public void setDialogListener(QLSpotDialogListener dialogListener) {
		this.dialogListener = dialogListener;
	}	
	
	@SuppressLint("NewApi")
	private void getSpotViewApp(final Context context,final GOffer obj)
	{
		ImageView view = new ImageView(context);
		try {			
			viewBm = BitmapFactory.decodeFile(context.getFilesDir().getPath()+"/"+ obj.getImageUrl()) ;
			view.setImageBitmap(viewBm);
			//底层容器			
			final QLSpotView layout = this;

			RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);		
			layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
			//int dir = context.getResources().getConfiguration().orientation;
			QLSize ss = GTools.getScreenSize(context);
			layoutParams.width = ss.width;
			layoutParams.height = ss.height;
			
			view.setId(1);
			view.setScaleType(ScaleType.CENTER_CROP);

			layout.addView(view, layoutParams);	

			RelativeLayout.LayoutParams paramsCloseText = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
			paramsCloseText.setMargins(0, 10, 10, 0);
			paramsCloseText.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			paramsCloseText.addRule(RelativeLayout.ALIGN_PARENT_TOP);
			
			final TextView closeText = new TextView(context);
			closeText.setText(s+" 跳过");
			//closeText.setTextColor(Color.WHITE);
			closeText.setTextSize(16);
			closeText.setLayoutParams(paramsCloseText);
			closeText.setGravity(Gravity.CENTER);
			closeText.setBackgroundResource((Integer)GTools.getResourceId("qew_button_shape", "drawable"));
			layout.addView(closeText);
			
			
			
			final Handler handler = new Handler(){
				@Override
				public void handleMessage(Message msg) {
					if(msg.what == 0x11)
					{
						if(s == -1)
						{
							layout.removeAllViews();
							ViewGroup parent = ( ViewGroup )layout.getParent();
							parent.removeView(layout);
							
							if(viewBm != null && !viewBm.isRecycled())
							{
								viewBm.recycle();
								viewBm = null;
							}					
							System.gc();
							
							if(dialogListener != null)
							{
								dialogListener.onSpotClosed();
							}
						}
						else
						{
							closeText.setText(s+" 跳过");
						}				
					}
					super.handleMessage(msg);
				}
			};
			
			Thread t = new Thread(){
				public void run() {
					try {
						
						while(s > -1)
						{
							Thread.sleep(1200);
							s --;
							handler.sendEmptyMessage(0x11);				
						}				
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				};
			};
			
			view.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					s = -2;
					layout.removeAllViews();
					ViewGroup parent = ( ViewGroup )layout.getParent();
					parent.removeView(layout);
					if(dialogListener != null)
					{
						dialogListener.onSpotClick(true);
					}
					if(viewBm != null && !viewBm.isRecycled())
					{
						viewBm.recycle();
						viewBm = null;
					}					
					System.gc();
					if(dialogListener != null)
					{
						dialogListener.onSpotClosed();
					}
				}
			});
			
			closeText.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					s = -2;
					layout.removeAllViews();
					ViewGroup parent = ( ViewGroup )layout.getParent();
					parent.removeView(layout);
					
					if(viewBm != null && !viewBm.isRecycled())
					{
						viewBm.recycle();
						viewBm = null;
					}					
					System.gc();
					
					if(dialogListener != null)
					{
						dialogListener.onSpotClosed();
					}
				}
			});
			if(dialogListener != null)
			{
				dialogListener.onShowSuccess();
			}
			t.start();
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}


}
