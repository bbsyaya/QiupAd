package com.qq.up.a.view;

import com.qq.up.a.QLBatteryLockActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.webkit.WebView;

public class GWebView extends WebView{
	private GOnClickListener clickListener;
  
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
    
    

    public GOnClickListener getClickListener() {
		return clickListener;
	}

	public void setClickListener(GOnClickListener clickListener) {
		this.clickListener = clickListener;
	}



	private QLBatteryLockActivity.MyOnTouchListener2 listener;
    
    public void setListener(QLBatteryLockActivity.MyOnTouchListener2 listener)
    {
    	this.listener = listener;
    }

    private float lastX;
	private float lastY;
	
	@Override  
    public boolean onTouchEvent(MotionEvent ev) {  
		
		if(listener != null)
		{
			listener.onTouch(null, ev);
		}
		if(ev.getAction() != MotionEvent.ACTION_MOVE)
			super.onTouchEvent(ev);
		
		if(ev.getAction() == MotionEvent.ACTION_DOWN)
		{
			lastX  = ev.getRawX();
			lastY  = ev.getRawY();
		}
		else if(ev.getAction() == MotionEvent.ACTION_UP)
		{
			if(Math.abs(ev.getRawX() - lastX)<20 && Math.abs(ev.getRawY() - lastY)<20)
			{
				if(clickListener != null)
					clickListener.click(ev);
			}
		}
	    return true;
    }  
	
	
	public interface GOnClickListener
	{
		void click(MotionEvent ev);
	}
}
