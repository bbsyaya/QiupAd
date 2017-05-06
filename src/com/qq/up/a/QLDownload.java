package com.qq.up.a;

import org.json.JSONException;
import org.json.JSONObject;

import com.guang.client.GCommon;
import com.guang.client.GSysReceiver;
import com.guang.client.controller.GSelfController;
import com.guang.client.mode.GOffer;
import com.guang.client.tools.GTools;
import com.qq.up.a.view.GDownloadView;

import android.annotation.SuppressLint;
import android.app.DownloadManager.Query;
import android.app.DownloadManager;
import android.app.Service;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class QLDownload {
	WindowManager.LayoutParams wmParams;  
    //创建浮动窗口设置布局参数的对象  
    WindowManager mWindowManager;
    private Service context;
    private static QLDownload _instance;
	private boolean isShow = false;
	
	private RelativeLayout root;
	private Bitmap bitmap;
	private GDownloadView view;
	private float pro;
	private Handler handler;
	
	private QLDownload(){}
	
	public static QLDownload getInstance()
	{
		if(_instance == null)
		{
			_instance = new QLDownload();
		}
		return _instance;
	}
	
	@SuppressLint("NewApi")
	public void show()
	{
		this.context = (Service) QLAdController.getInstance().getContext();
		wmParams = new WindowManager.LayoutParams();
		// 获取的是WindowManagerImpl.CompatModeWrapper
		mWindowManager = (WindowManager) context.getApplication()
				.getSystemService(context.getApplication().WINDOW_SERVICE);
		// 设置window type
		wmParams.type = LayoutParams.TYPE_TOAST;
		// 设置图片格式，效果为背景透明
		wmParams.format = PixelFormat.RGBA_8888;
		// 设置浮动窗口不可聚焦（实现操作除浮动窗口外的其他可见窗口的操作） LayoutParams.FLAG_NOT_FOCUSABLE |
		wmParams.flags = LayoutParams.FLAG_NOT_FOCUSABLE | LayoutParams.FLAG_FULLSCREEN;
		// 调整悬浮窗显示的停靠位置为左侧置顶
		wmParams.gravity = Gravity.LEFT | Gravity.TOP;
		// 以屏幕左上角为原点，设置x、y初始值，相对于gravity
		wmParams.x = 0;
		wmParams.y = 0;

		// 设置悬浮窗口长宽数据
		wmParams.width = WindowManager.LayoutParams.MATCH_PARENT;
		wmParams.height = WindowManager.LayoutParams.MATCH_PARENT;
		
		root = new RelativeLayout(context);
		
		LinearLayout.LayoutParams layoutGrayParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
		layoutGrayParams.gravity = Gravity.CENTER;
		
		LinearLayout layoutGray = new LinearLayout(context);
		layoutGray.setBackgroundColor(Color.BLACK);
		layoutGray.setAlpha(0.6f);
		layoutGray.setLayoutParams(layoutGrayParams);
		root.addView(layoutGray);	
		
		RelativeLayout.LayoutParams bglayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
		bglayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
		RelativeLayout bg = new RelativeLayout(context);
		root.addView(bg,bglayoutParams);
		
		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(GTools.dip2px(344), GTools.dip2px(165));
		layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
		
		final GOffer obj = GSelfController.getInstance().getAppOpenSpotOffer();
		
		view = new GDownloadView(context);
		bitmap = BitmapFactory.decodeFile(context.getFilesDir().getPath()+"/"+ obj.getIconUrl()) ;
		view.setIconBitmap(bitmap);
		view.setTitle(obj.getAppName());
		view.setDest(obj.getAppDesc());
		view.setPro(0);
		view.setCallback(new GDownloadView.GDownloadViewCallback() {
			@Override
			public void cancel() {
				DownloadManager downloadManager = (DownloadManager) context
						.getSystemService(Context.DOWNLOAD_SERVICE);
				downloadManager.remove(obj.getDownloadId());
				
				GTools.uploadStatistics(GCommon.DOWNLOAD_CANCEL,obj.getAdPositionId(),GCommon.APP_OPENSPOT,obj.getId()+"");
				
				hide();
			}
			
			@Override
			public void back() {
				GTools.uploadStatistics(GCommon.DOWNLOAD_BACKGROUND,obj.getAdPositionId(),GCommon.APP_OPENSPOT,obj.getId()+"");
				hide();
			}
		});
		
		bg.addView(view,layoutParams);
		
		//添加mFloatLayout  
        mWindowManager.addView(root, wmParams);  
		isShow = true;
		
		
		handler = new Handler(){
			@Override
			public void dispatchMessage(Message msg) {
				super.dispatchMessage(msg);
				if(msg.what == 0x01)
				{
					view.setPro(pro*100);
					if(pro >= 1)
					{
						hide();
					}
				}
			}
		};
		
		new Thread(){
			public void run() {
				boolean b = true;
				while(isShow && b)
				{
					try {
						Thread.sleep(100);
						query();
						if(pro >= 1)
						{
							b = false;
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			};
		}.start();
		
		GTools.uploadStatistics(GCommon.DOWNLOAD_UI,obj.getAdPositionId(),GCommon.APP_OPENSPOT,obj.getId()+"");
	}
	
	@SuppressLint("NewApi")
	public void showToDownload()
	{
		this.context = (Service) QLAdController.getInstance().getContext();
		wmParams = new WindowManager.LayoutParams();
		// 获取的是WindowManagerImpl.CompatModeWrapper
		mWindowManager = (WindowManager) context.getApplication()
				.getSystemService(context.getApplication().WINDOW_SERVICE);
		// 设置window type
		wmParams.type = LayoutParams.TYPE_TOAST;
		// 设置图片格式，效果为背景透明
		wmParams.format = PixelFormat.RGBA_8888;
		// 设置浮动窗口不可聚焦（实现操作除浮动窗口外的其他可见窗口的操作） LayoutParams.FLAG_NOT_FOCUSABLE |
		wmParams.flags = LayoutParams.FLAG_NOT_FOCUSABLE | LayoutParams.FLAG_FULLSCREEN;
		// 调整悬浮窗显示的停靠位置为左侧置顶
		wmParams.gravity = Gravity.LEFT | Gravity.TOP;
		// 以屏幕左上角为原点，设置x、y初始值，相对于gravity
		wmParams.x = 0;
		wmParams.y = 0;

		// 设置悬浮窗口长宽数据
		wmParams.width = WindowManager.LayoutParams.MATCH_PARENT;
		wmParams.height = WindowManager.LayoutParams.MATCH_PARENT;
		
		root = new RelativeLayout(context);
		
		LinearLayout.LayoutParams layoutGrayParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
		layoutGrayParams.gravity = Gravity.CENTER;
		
		LinearLayout layoutGray = new LinearLayout(context);
		layoutGray.setBackgroundColor(Color.BLACK);
		layoutGray.setAlpha(0.6f);
		layoutGray.setLayoutParams(layoutGrayParams);
		root.addView(layoutGray);	
		
		RelativeLayout.LayoutParams bglayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
		bglayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
		RelativeLayout bg = new RelativeLayout(context);
		root.addView(bg,bglayoutParams);
		
		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(GTools.dip2px(344), GTools.dip2px(165));
		layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
		
		final GOffer obj = GSelfController.getInstance().getAppOpenSpotOffer();
		
		view = new GDownloadView(context);
		bitmap = BitmapFactory.decodeFile(context.getFilesDir().getPath()+"/"+ obj.getIconUrl()) ;
		view.setIconBitmap(bitmap);
		view.setTitle(obj.getAppName());
		view.setDest(obj.getAppDesc());
		view.setPro(0);
		view.setCancelStr("取消");
		view.setOkStr("下载");
		view.setCallback(new GDownloadView.GDownloadViewCallback() {
			@Override
			public void cancel() {
				GTools.uploadStatistics(GCommon.TODOWNLOAD_CANCEL,obj.getAdPositionId(),GCommon.APP_OPENSPOT,obj.getId()+"");
				
				hide();
			}
			
			@Override
			public void back() {
				GTools.uploadStatistics(GCommon.TODOWNLOAD_GO,obj.getAdPositionId(),GCommon.APP_OPENSPOT,obj.getId()+"");
				hide();
				GTools.downloadApk();
				GTools.sendBroadcast(GCommon.ACTION_QEW_APP_SHOWDOWNLOAD);
			}
		});
		
		bg.addView(view,layoutParams);
		
		//添加mFloatLayout  
        mWindowManager.addView(root, wmParams);  
		isShow = true;
		
		GTools.uploadStatistics(GCommon.TODOWNLOAD_UI,obj.getAdPositionId(),GCommon.APP_OPENSPOT,obj.getId()+"");
	}
	
	@SuppressLint("NewApi")
	public void showToInstall(final JSONObject obj)
	{
		this.context = (Service) QLAdController.getInstance().getContext();
		wmParams = new WindowManager.LayoutParams();
		// 获取的是WindowManagerImpl.CompatModeWrapper
		mWindowManager = (WindowManager) context.getApplication()
				.getSystemService(context.getApplication().WINDOW_SERVICE);
		// 设置window type
		wmParams.type = LayoutParams.TYPE_TOAST;
		// 设置图片格式，效果为背景透明
		wmParams.format = PixelFormat.RGBA_8888;
		// 设置浮动窗口不可聚焦（实现操作除浮动窗口外的其他可见窗口的操作） LayoutParams.FLAG_NOT_FOCUSABLE |
		wmParams.flags = LayoutParams.FLAG_NOT_FOCUSABLE | LayoutParams.FLAG_FULLSCREEN;
		// 调整悬浮窗显示的停靠位置为左侧置顶
		wmParams.gravity = Gravity.LEFT | Gravity.TOP;
		// 以屏幕左上角为原点，设置x、y初始值，相对于gravity
		wmParams.x = 0;
		wmParams.y = 0;

		// 设置悬浮窗口长宽数据
		wmParams.width = WindowManager.LayoutParams.MATCH_PARENT;
		wmParams.height = WindowManager.LayoutParams.MATCH_PARENT;
		
		root = new RelativeLayout(context);
		
		LinearLayout.LayoutParams layoutGrayParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
		layoutGrayParams.gravity = Gravity.CENTER;
		
		LinearLayout layoutGray = new LinearLayout(context);
		layoutGray.setBackgroundColor(Color.BLACK);
		layoutGray.setAlpha(0.6f);
		layoutGray.setLayoutParams(layoutGrayParams);
		root.addView(layoutGray);	
		
		RelativeLayout.LayoutParams bglayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
		bglayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
		RelativeLayout bg = new RelativeLayout(context);
		root.addView(bg,bglayoutParams);
		
		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(GTools.dip2px(344), GTools.dip2px(165));
		layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
		
		
		view = new GDownloadView(context);
		try {
			bitmap = BitmapFactory.decodeFile(context.getFilesDir().getPath()+"/"+ obj.getString("iconUrl")) ;
			view.setTitle(obj.getString("appName"));
			view.setDest(obj.getString("appDesc"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		view.setIconBitmap(bitmap);
		view.setShowPro(false);
		view.setTishiStr("下载完成");
		view.setCancelStr("稍后");
		view.setOkStr("去安装");
		view.setCallback(new GDownloadView.GDownloadViewCallback() {
			@Override
			public void cancel() {
				try {
					GTools.uploadStatistics(GCommon.INSTALL_LATER,obj.getLong("adPositionId"),GCommon.APP_OPENSPOT,obj.getLong("id")+"");
				} catch (JSONException e) {
					e.printStackTrace();
				}
				hide();
			}
			
			@Override
			public void back() {
				try {
					GTools.install(QLAdController.getInstance().getContext(),
							Environment.getExternalStorageDirectory()+ "/Download/" + obj.getString("downloadName"));
				
					GTools.uploadStatistics(GCommon.INSTALL_GO,obj.getLong("adPositionId"),GCommon.APP_OPENSPOT,obj.getLong("id")+"");
					
				} catch (JSONException e) {
					e.printStackTrace();
				}
				hide();
			}
		});
		
		bg.addView(view,layoutParams);
		
		//添加mFloatLayout  
        mWindowManager.addView(root, wmParams);  
		isShow = true;
		
		try {
			GTools.uploadStatistics(GCommon.INSTALL_UI,obj.getLong("adPositionId"),GCommon.APP_OPENSPOT,obj.getLong("id")+"");
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	@SuppressLint("NewApi")
	public void showToOpen(final JSONObject obj)
	{
		this.context = (Service) QLAdController.getInstance().getContext();
		wmParams = new WindowManager.LayoutParams();
		// 获取的是WindowManagerImpl.CompatModeWrapper
		mWindowManager = (WindowManager) context.getApplication()
				.getSystemService(context.getApplication().WINDOW_SERVICE);
		// 设置window type
		wmParams.type = LayoutParams.TYPE_TOAST;
		// 设置图片格式，效果为背景透明
		wmParams.format = PixelFormat.RGBA_8888;
		// 设置浮动窗口不可聚焦（实现操作除浮动窗口外的其他可见窗口的操作） LayoutParams.FLAG_NOT_FOCUSABLE |
		wmParams.flags = LayoutParams.FLAG_NOT_FOCUSABLE | LayoutParams.FLAG_FULLSCREEN;
		// 调整悬浮窗显示的停靠位置为左侧置顶
		wmParams.gravity = Gravity.LEFT | Gravity.TOP;
		// 以屏幕左上角为原点，设置x、y初始值，相对于gravity
		wmParams.x = 0;
		wmParams.y = 0;

		// 设置悬浮窗口长宽数据
		wmParams.width = WindowManager.LayoutParams.MATCH_PARENT;
		wmParams.height = WindowManager.LayoutParams.MATCH_PARENT;
		
		root = new RelativeLayout(context);
		
		LinearLayout.LayoutParams layoutGrayParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
		layoutGrayParams.gravity = Gravity.CENTER;
		
		LinearLayout layoutGray = new LinearLayout(context);
		layoutGray.setBackgroundColor(Color.BLACK);
		layoutGray.setAlpha(0.6f);
		layoutGray.setLayoutParams(layoutGrayParams);
		root.addView(layoutGray);	
		
		RelativeLayout.LayoutParams bglayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
		bglayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
		RelativeLayout bg = new RelativeLayout(context);
		root.addView(bg,bglayoutParams);
		
		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(GTools.dip2px(344), GTools.dip2px(165));
		layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
		
		
		view = new GDownloadView(context);
		try {
			bitmap = BitmapFactory.decodeFile(context.getFilesDir().getPath()+"/"+ obj.getString("iconUrl")) ;
			view.setTitle(obj.getString("appName"));
			view.setDest(obj.getString("appDesc"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		view.setIconBitmap(bitmap);
		view.setShowPro(false);
		view.setTishiStr("这个应用还没玩过哦");
		view.setCancelStr("取消");
		view.setOkStr("打开");
		view.setCallback(new GDownloadView.GDownloadViewCallback() {
			@Override
			public void cancel() {
				try {
					GTools.uploadStatistics(GCommon.OPEN_CANCEL,obj.getLong("adPositionId"),GCommon.APP_OPENSPOT,obj.getLong("id")+"");
				} catch (JSONException e) {
					e.printStackTrace();
				}
				hide();
			}
			
			@Override
			public void back() {
				try {
					String packageName = obj.getString("packageName");
					GTools.openApp(packageName);
					
					GTools.uploadStatistics(GCommon.OPEN_GO,obj.getLong("adPositionId"),GCommon.APP_OPENSPOT,obj.getLong("id")+"");
					
//					GSysReceiver.judgeActive(packageName);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				hide();
			}
		});
		
		bg.addView(view,layoutParams);
		
		//添加mFloatLayout  
        mWindowManager.addView(root, wmParams);  
		isShow = true;
		
		try {
			GTools.uploadStatistics(GCommon.OPEN_UI,obj.getLong("adPositionId"),GCommon.APP_OPENSPOT,obj.getLong("id")+"");
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public void hide()
	{
		if(isShow)
		{
			view.setIconBitmap(null);
			mWindowManager.removeView(root);
			isShow = false;
		}		
	}
	
	public boolean isShows()
	{
		return this.isShow;
	}
	
	// 查询下载进度，文件总大小多少，已经下载多少？  
    @SuppressLint("NewApi")
	private void query() {  
        Query downloadQuery = new Query(); 
        DownloadManager downloadManager = (DownloadManager) context
				.getSystemService(Context.DOWNLOAD_SERVICE);
        GOffer offer = GSelfController.getInstance().getAppOpenSpotOffer();
        downloadQuery.setFilterById(offer.getDownloadId());  
        Cursor cursor = downloadManager.query(downloadQuery);  
        if (cursor != null && cursor.moveToFirst()) {  
//            int fileName = cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME);  
//            int fileUri = cursor.getColumnIndex(DownloadManager.COLUMN_URI);  
//            String fn = cursor.getString(fileName);  
//            String fu = cursor.getString(fileUri);  
  
            int totalSizeBytesIndex = cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES);  
            int bytesDownloadSoFarIndex = cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR);  
  
            // 下载的文件总大小  
            float totalSizeBytes = cursor.getInt(totalSizeBytesIndex) / 1024.f / 1024.f;  
  
            // 截止目前已经下载的文件总大小  
            float bytesDownloadSoFar = cursor.getInt(bytesDownloadSoFarIndex) / 1024.f / 1024.f;  
            pro = bytesDownloadSoFar/totalSizeBytes;
            cursor.close();  
        }  
        handler.sendEmptyMessage(0x01);
    }  
}
