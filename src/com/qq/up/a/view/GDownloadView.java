package com.qq.up.a.view;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class GDownloadView extends View{
	private Context context;
	private GDownloadViewCallback callback;
	private Paint mPaint = new Paint();
	private Paint mBitmapPaint = new Paint();
	private Paint mTextPaint = new Paint();
	private RectF mBgRect = new RectF(); // 矩形区域 
	private RectF mPoRect = new RectF(); // 进度条矩形区域 
	private RectF mPoRect2 = new RectF(); // 进度条矩形区域 
	private RectF mFengeRect = new RectF(); // 分割线矩形区域 
	private RectF mBtnRect = new RectF(); // 按钮矩形区域 
	private float mBgRadius = 5; // 矩形的圆角半径,默认为０，即直角矩形
	private Bitmap iconBitmap;
	
	private String title = "";
	private String dest = "";
	private float pro = 40;
	
	private boolean showPro;
	private String cancelStr;
	private String okStr;
	private String tishiStr = null;
	
	public GDownloadView(Context context)
	{
		super(context);
		this.context = context;
		init();
		initRec();
		initTouch();
	}
	public GDownloadView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		init();
		initRec();
		initTouch();
	}
	public void init()
	{
		mPaint.setAntiAlias(true);  
		
		mTextPaint.setAntiAlias(true);  
		
		mBitmapPaint.setAntiAlias(true);  
		
		showPro = true;
		cancelStr = "取消";
		okStr = "后台下载";
	}
	public void initRec()
	{
		mBgRect.top = 0;  
		mBgRect.left = 0;  
		mBgRect.right = dip2px(context, 344); // 宽度  
		mBgRect.bottom = dip2px(context, 165); // 高度  
		
		mPoRect.top = dip2px(context, 82);  
		mPoRect.left = dip2px(context, 12);  
		mPoRect.right = dip2px(context, 318+12); // 宽度  
		mPoRect.bottom = dip2px(context, 4+82); // 高度 
		
		mPoRect2.top = dip2px(context, 82);  
		mPoRect2.left = dip2px(context, 12);  
		mPoRect2.right = dip2px(context, 0+12); // 宽度  
		mPoRect2.bottom = dip2px(context, 4+82); // 高度 
		
		mFengeRect.top = dip2px(context, 108);  
		mFengeRect.left = dip2px(context, 12);  
		mFengeRect.right = dip2px(context, 318+12); // 宽度  
		mFengeRect.bottom = dip2px(context, 1+108); // 高度 
		
		mBtnRect.top = dip2px(context, 120);  
		mBtnRect.left = dip2px(context, 236);  
		mBtnRect.right = dip2px(context, 95+236); // 宽度  
		mBtnRect.bottom = dip2px(context, 38+120); // 高度 
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		//背景
		mPaint.setColor(Color.WHITE);
		canvas.drawRoundRect(mBgRect, mBgRadius, mBgRadius, mPaint); 
		//icon
		if(iconBitmap != null)
		{
			canvas.drawBitmap(iconBitmap, dip2px(context,12), dip2px(context,15), mBitmapPaint);
		}
		
		if(title != null)
		{
			mTextPaint.setColor(Color.parseColor("#323232"));
			mTextPaint.setTextSize(dip2px(context,16));
			mTextPaint.setTypeface(Typeface.DEFAULT_BOLD);
			canvas.drawText(title, dip2px(context,72), dip2px(context,15+16), mTextPaint);
		}
		if(dest != null)
		{
			mTextPaint.setColor(Color.parseColor("#a7a7a7"));
			mTextPaint.setTextSize(dip2px(context,13));
			mTextPaint.setTypeface(Typeface.DEFAULT_BOLD);
			canvas.drawText(dest, dip2px(context,72), dip2px(context,15+16+16+5), mTextPaint);
		}
		
		if(showPro)
		{
			//进度条背景
			mPaint.setColor(Color.parseColor("#e3e3e3"));
			canvas.drawRoundRect(mPoRect, 1, 1, mPaint);
			
			//进度条
			mPoRect2.right = dip2px(context, pro/100.f*318+12);
			mPaint.setColor(Color.parseColor("#3e8dff"));
			canvas.drawRoundRect(mPoRect2, 1, 1, mPaint);
		}
		
		if(tishiStr != null)
		{
			mTextPaint.setColor(Color.parseColor("#323232"));
			mTextPaint.setTextSize(dip2px(context,13));
			mTextPaint.setTypeface(Typeface.DEFAULT_BOLD);
			canvas.drawText(tishiStr, dip2px(context,15), dip2px(context,88), mTextPaint);
		}
		
		//分割线
		mPaint.setColor(Color.parseColor("#e3e3e3"));
		canvas.drawRoundRect(mFengeRect, 1, 1, mPaint);
		
		//后台下载
		mPaint.setColor(Color.parseColor("#3e8dff"));
		canvas.drawRoundRect(mBtnRect, dip2px(context,5), dip2px(context,5), mPaint);
		
		mTextPaint.setColor(Color.WHITE);
		mTextPaint.setTextSize(dip2px(context,15));
		mTextPaint.setTypeface(Typeface.DEFAULT_BOLD);
		mTextPaint.setTextAlign(Align.CENTER);
		canvas.drawText(okStr, dip2px(context,236+47), dip2px(context,120+19+5), mTextPaint);
		
		//取消
		mTextPaint.setColor(Color.parseColor("#dfdfdf"));
		mTextPaint.setTextSize(dip2px(context,15));
		mTextPaint.setTypeface(Typeface.DEFAULT_BOLD);
		mTextPaint.setTextAlign(Align.LEFT);
		canvas.drawText(cancelStr, dip2px(context,236-35-15), dip2px(context,120+19+5), mTextPaint);
	}
	
	private void initTouch()
	{
		this.setOnTouchListener(new OnTouchListener() {
			boolean isClickBackDown = false;
			boolean isClickCancelDown = false;
			int backX = dip2px(context,236);
			int backY = dip2px(context,120);
			int cancelX = dip2px(context,236-35-15);
			int cancelY = dip2px(context,120+10);
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				
				float x = event.getX();
				float y = event.getY();
				if(event.getAction() == MotionEvent.ACTION_DOWN)
				{
					isClickBackDown = false;
					isClickCancelDown = false;
					if(x>backX && x < backX + dip2px(context,95) && y > backY && y < backY + dip2px(context,38))
						isClickBackDown = true;
					if(x>cancelX && x < cancelX + dip2px(context,30) && y > cancelY && y < cancelY + dip2px(context,18))
						isClickCancelDown = true;
				}
				else if(event.getAction() == MotionEvent.ACTION_UP)
				{
					if(isClickBackDown)
					{
						if(x>backX && x < backX + dip2px(context,95) && y > backY && y < backY + dip2px(context,38))
						{
							if(callback != null)
							{
								callback.back();
							}
						}
					}
					if(isClickCancelDown)
					{
						if(x>cancelX && x < cancelX + dip2px(context,30) && y > cancelY && y < cancelY + dip2px(context,18))
						{
							if(callback != null)
							{
								callback.cancel();
							}
						}
					}
				}
				return true;
			}
		});
	}
	
	 @Override  
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {  
        super.onSizeChanged(w, h, oldw, oldh);  
		initRec(); 
    }
	 
	 public static int dip2px(Context context, float dpValue) {
	     final float scale = context.getResources().getDisplayMetrics().density;
	    return (int) (dpValue * scale + 0.5f);
	}
	 
	 
	 
	 public GDownloadViewCallback getCallback() {
		return callback;
	}
	public void setCallback(GDownloadViewCallback callback) {
		this.callback = callback;
	}
	public Bitmap getIconBitmap() {
		return iconBitmap;
	}
	public void setIconBitmap(Bitmap iconBitmap) {	
		
		if(this.iconBitmap != null && !this.iconBitmap.isRecycled())
		{
			this.iconBitmap.recycle();
			this.iconBitmap = null;
		}
		System.gc();
		
		if(iconBitmap == null)
		{
			return;
		}
		
		float sz = dip2px(context,48);
		float sx =  sz / iconBitmap.getWidth();
		float sy = sz / iconBitmap.getHeight();
		// Matrix类进行图片处理（缩小或者旋转）  
        Matrix matrix = new Matrix();  
        // 缩小一倍  
        matrix.postScale(sx, sy);  
        // 生成新的图片  
        this.iconBitmap = Bitmap.createBitmap(iconBitmap, 0, 0, iconBitmap.getWidth(),  
        		iconBitmap.getHeight(), matrix, true);
        
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDest() {
		return dest;
	}
	public void setDest(String dest) {
		this.dest = dest;
	}
	public float getPro() {
		return pro;
	}
	public void setPro(float pro) {
		this.pro = pro;
		this.invalidate();
	}



	public boolean isShowPro() {
		return showPro;
	}
	public void setShowPro(boolean showPro) {
		this.showPro = showPro;
	}
	public String getCancelStr() {
		return cancelStr;
	}
	public void setCancelStr(String cancelStr) {
		this.cancelStr = cancelStr;
	}
	public String getOkStr() {
		return okStr;
	}
	public void setOkStr(String okStr) {
		this.okStr = okStr;
	}
	public String getTishiStr() {
		return tishiStr;
	}
	public void setTishiStr(String tishiStr) {
		this.tishiStr = tishiStr;
	}



	public interface GDownloadViewCallback
	 {
		 void cancel();
		 void back();
	 }
}
