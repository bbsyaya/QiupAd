package com.qq.up.a.view;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;

public class GTimeButton extends View{

	private Paint mBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);  
	private Paint mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG); 
	private RectF mViewRect = new RectF(); // 矩形区域  
    private float mRoundRadius = 20; // 矩形的圆角半径,默认为０，即直角矩形
    
    private String text = "3s";
    private float textSize = 22;
    private float tx = 22;
    
    private int num = 3;
    private Handler handler;
    private GTimeButtonCallback callback;
    
    public GTimeButton(Context context)
    {
    	super(context);
    	mViewRect.top = 0;  
        mViewRect.left = 0;  
        mViewRect.right = getWidth(); // 宽度  
        mViewRect.bottom = getHeight(); // 高度  
        
        mBorderPaint.setColor(Color.BLACK);  
        mBorderPaint.setAntiAlias(true);  
        mBorderPaint.setAlpha(120);
        
        mTextPaint.setColor(Color.WHITE);
        mTextPaint.setAntiAlias(true); 
        mTextPaint.setTextSize(textSize); 
    }
    public GTimeButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		mViewRect.top = 0;  
        mViewRect.left = 0;  
        mViewRect.right = getWidth(); // 宽度  
        mViewRect.bottom = getHeight(); // 高度  
        
        mBorderPaint.setColor(Color.BLACK);  
        mBorderPaint.setAntiAlias(true);  
        mBorderPaint.setAlpha(120);
        
        mTextPaint.setColor(Color.WHITE);
        mTextPaint.setAntiAlias(true); 
        mTextPaint.setTextSize(textSize);   
	}
    
    public void start(GTimeButtonCallback callbac)
    {
    	this.callback = callbac;
    	this.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
		
			}
		});
    	handler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				
				if(msg.what == 0x01)
				{
					invalidate();
				}
				else if(msg.what == 0x02)
				{
					callback.end();
				}
				else if(msg.what == 0x03)
				{
					callback.timeout();
				}
			}
        };
        
        new Thread(){
        	public void run() {
        		while(num >= 0)
        		{
        			try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
        			num--;
					text = num+"s";
					if(num < 0)
					{
						tx = tx*2;
						text = "跳过";
						handler.sendEmptyMessage(0x03);
					}
						
					handler.sendEmptyMessage(0x01);
        		}
        		try {
					Thread.sleep(5000);
					handler.sendEmptyMessage(0x02);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
        	};
        }.start();
    }

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		canvas.drawRoundRect(mViewRect, mRoundRadius, mRoundRadius, mBorderPaint); 
		
		float p = tx*text.length()/4;
		canvas.drawText(text, mViewRect.right/2-p, mViewRect.bottom/2+textSize/3, mTextPaint);
	}
	
	 @Override  
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {  
        super.onSizeChanged(w, h, oldw, oldh);  
        mViewRect.top = 0;  
        mViewRect.left = 0;  
        mViewRect.right = getWidth(); // 宽度  
        mViewRect.bottom = getHeight(); // 高度  
    }

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public float getTextSize() {
		return textSize;
	}

	public void setTextSize(float textSize) {
		this.textSize = textSize;
		this.tx = textSize;
		 mTextPaint.setTextSize(textSize);   
	}  
	 
	public interface GTimeButtonCallback
	{
		void timeout();
		void end();
	}

	 
}
