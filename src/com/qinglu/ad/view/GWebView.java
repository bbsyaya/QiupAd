package com.qinglu.ad.view;

import com.guang.client.tools.GLog;
import com.qinglu.ad.QLBatteryLockActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.webkit.WebView;

public class GWebView extends WebView{
	
  
	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	public GWebView(Context context, AttributeSet attrs, int defStyle,
			boolean privateBrowsing) {
		super(context, attrs, defStyle, privateBrowsing);
		// TODO Auto-generated constructor stub
	}

	public GWebView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public GWebView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

    public GWebView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
    

    private QLBatteryLockActivity.MyOnTouchListener2 listener;
    
    public void setListener(QLBatteryLockActivity.MyOnTouchListener2 listener)
    {
    	this.listener = listener;
    }

	@Override  
    public boolean onTouchEvent(MotionEvent ev) {  
		
		if(listener != null)
		{
			listener.onTouch(null, ev);
		}
		if(ev.getAction() != MotionEvent.ACTION_MOVE)
			super.onTouchEvent(ev);
	    return true;
    }  
	
	
}
