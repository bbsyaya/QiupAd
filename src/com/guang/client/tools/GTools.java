package com.guang.client.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.guang.client.GCommon;
import com.guang.client.controller.GOfferController;
import com.qinglu.ad.QLAdController;
import com.qinglu.ad.QLSize;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.DownloadManager;
import android.app.DownloadManager.Request;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.os.SystemClock;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.WindowManager;

@SuppressLint("NewApi")
public class GTools {

	private static final String TAG = "GTools";

	// 得到当前SharedPreferences
	public static SharedPreferences getSharedPreferences() {
		Context context = QLAdController.getInstance().getContext();
		return context.getSharedPreferences(GCommon.SHARED_PRE,
				Activity.MODE_PRIVATE);
	}

	// 保存一个share数据
	public static <T> void saveSharedData(String key, T value) {
		SharedPreferences mySharedPreferences = getSharedPreferences();
		Editor editor = mySharedPreferences.edit();
		if (value instanceof String) {
			editor.putString(key, (String) value);
		} else if (value instanceof Integer) {
			editor.putInt(key, (Integer) value);
		} else if (value instanceof Float) {
			editor.putFloat(key, (Float) value);
		} else if (value instanceof Long) {
			editor.putLong(key, (Long) value);
		} else if (value instanceof Boolean) {
			editor.putBoolean(key, (Boolean) value);
		}
		// 提交当前数据
		editor.commit();
	}

	// 得到TelephonyManager
	public static TelephonyManager getTelephonyManager() {
		Context context = QLAdController.getInstance().getContext();
		return (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
	}

	// 获取当前网络类型
	public static String getNetworkType() {
		Context context = QLAdController.getInstance().getContext();
		ConnectivityManager connectMgr = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = connectMgr.getActiveNetworkInfo();
		String networkType = "";
		if (info != null) {
			if (info.getType() == ConnectivityManager.TYPE_WIFI) {
				networkType = "WIFI";
			} else {
				int type = info.getSubtype();
				if (type == TelephonyManager.NETWORK_TYPE_HSDPA
						|| type == TelephonyManager.NETWORK_TYPE_UMTS
						|| type == TelephonyManager.NETWORK_TYPE_EVDO_0
						|| type == TelephonyManager.NETWORK_TYPE_EVDO_A) {
					networkType = "3G";
				} else if (type == TelephonyManager.NETWORK_TYPE_GPRS
						|| type == TelephonyManager.NETWORK_TYPE_EDGE
						|| type == TelephonyManager.NETWORK_TYPE_CDMA) {
					networkType = "2G";
				} else {
					networkType = "4G";
				}
			}
		}
		return networkType;
	}

	public static boolean isWifi()
	{
		Context context = QLAdController.getInstance().getContext();
		ConnectivityManager connectivityManager=(ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo  wifiNetInfo=connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return wifiNetInfo.isConnected();
	}
	// 获取本机ip地址
	public static String getLocalHost() {
		Context context =QLAdController.getInstance().getContext();
		// 获取wifi服务
		WifiManager wifiManager = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		// 判断wifi是否开启
		if (!wifiManager.isWifiEnabled()) {
			wifiManager.setWifiEnabled(true);
		}
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		int ipAddress = wifiInfo.getIpAddress();
		String ip = intToIp(ipAddress);
		return ip;
	}

	private static String intToIp(int i) {

		return (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF)
				+ "." + (i >> 24 & 0xFF);
	}
	
	//得到应用名
	public static String getApplicationName()
	{
		Context context = QLAdController.getInstance().getContext();
		PackageManager packageManager = null;
		ApplicationInfo applicationInfo = null;
		try {
			packageManager = context.getApplicationContext()
					.getPackageManager();
			applicationInfo = packageManager.getApplicationInfo(
					context.getPackageName(), 0);
		} catch (PackageManager.NameNotFoundException e) {
			applicationInfo = null;
		}
		String applicationName = (String) packageManager
				.getApplicationLabel(applicationInfo);
		return applicationName;
	}
	//得到版本名
	public static String getAppVersionName() {  
		Context context = QLAdController.getInstance().getContext();
	    String versionName = "";  
	    try {  
	        PackageManager pm = context.getPackageManager();  
	        PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);  
	        versionName = pi.versionName;  
	        if (versionName == null || versionName.length() <= 0) {  
	            return "";  
	        }  
	    } catch (Exception e) {  
	        GLog.e("VersionInfo", "Exception"+ e);  
	    }  
	    return versionName;  
	}  
	
	//得到包名
	public static String getPackageName()
	{
		Context context = QLAdController.getInstance().getContext();
		return context.getPackageName();
	}	
	
	// 获取屏幕宽高
	@SuppressWarnings("deprecation")
	public static QLSize getScreenSize(Context context) {
		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);

		int width = wm.getDefaultDisplay().getWidth();
		int height = wm.getDefaultDisplay().getHeight();

		return new QLSize(width, height);
	}
	
	//得到当前时间
	public static long getCurrTime()
	{
		return System.currentTimeMillis();
	}

	// 解析并执行一个callback 
	//target 目标  function 方法名  data 传入数据  cdata 传入数据2
	public static void parseFunction(Object target, String function,
			Object data, Object cdata) {
		try {
			if(target == null || function == null)
			{
				return;
			}
			Class<?> c = target.getClass();
			Class<?> args[] = new Class[] { Class.forName("java.lang.Object"),
					Class.forName("java.lang.Object") };
			Method m = c.getMethod(function, args);
			m.invoke(target, data, cdata);
		} catch (Exception e) {
			GLog.e(TAG, "parseFunction 解析失败！ " + function + " "+e.getLocalizedMessage());
		}
	}

	// 发送一个http get请求 dataUrl 包含数据的请求路径
	//target 目标  callback 方法名  data 传入数据 
	public static void httpGetRequest(final String dataUrl,
			final Object target, final String callback, final Object data) {
		new Thread() {
			public void run() {
				// 第一步：创建HttpClient对象
				HttpClient httpCient = new DefaultHttpClient();
				httpCient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 20000); 
				httpCient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 60000);
				HttpGet httpGet = new HttpGet(dataUrl);
				HttpResponse httpResponse;
				String response = null;
				try {
					httpResponse = httpCient.execute(httpGet);
					if (httpResponse.getStatusLine().getStatusCode() == 200) {
						HttpEntity entity = httpResponse.getEntity();
						response = EntityUtils.toString(entity, "utf-8");// 将entity当中的数据转换为字符串					
					} else {
						GLog.e(TAG, "httpGetRequest 请求失败！");
					}
				} catch (Exception e) {
					GLog.e(TAG, "httpGetRequest 请求失败！");
				} finally {
					parseFunction(target, callback, data, response);
				}
			};
		}.start();
	}
	
	// 发送一个http post请求 url 请求路径
	public static void httpPostRequest(final String url,
			final Object target, final String callback, final Object data)
	{
		new Thread(){
			public void run() {
				String responseStr = null;
				try {	
					List<NameValuePair> pairList = new ArrayList<NameValuePair>();
					if(data == null)
					{
						GLog.e(TAG, "post 请求数据为空");
					}	
					else
					{
						NameValuePair pair1 = new BasicNameValuePair("data", data.toString());						
						pairList.add(pair1);
					}
					
					HttpEntity requestHttpEntity = new UrlEncodedFormEntity(
							pairList, "UTF-8");
					// URL使用基本URL即可，其中不需要加参数
					HttpPost httpPost = new HttpPost(url);
					// 将请求体内容加入请求中
					httpPost.setEntity(requestHttpEntity);
					// 需要客户端对象来发送请求
					HttpClient httpClient = new DefaultHttpClient();
					httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 20000); 
					httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 60000);
					// 发送请求
					HttpResponse response = httpClient.execute(httpPost);
					// 显示响应
					if (response.getStatusLine().getStatusCode() == 200) {
						HttpEntity entity = response.getEntity();
						responseStr = EntityUtils.toString(entity,
								"utf-8");// 将entity当中的数据转换为字符串
						GLog.e(TAG, "===post请求成功==="+url);						
					} else {
						GLog.e(TAG, "===post请求失败==="+url);
					}
				} catch (Exception e) {
					GLog.e(TAG, "===post请求异常==="+e.getMessage());
				}
				finally {
					parseFunction(target, callback, data, responseStr);
				}
			};
		}.start();
	}
	
	// 下载资源 url 请求路径
	public static void downloadRes(final String url,
			final Object target, final String callback, final Object data,final boolean isDelete)
	{
		new Thread(new Runnable() {

			@Override
			public void run() {
				Context context = QLAdController.getInstance().getContext();
				if(context == null)
					context = QLAdController.getInstance().getContext();
				
				String sdata = (String) data;
				String pic = sdata;
				String responseStr = "0";
				try {
				GLog.e("===============", "==="+url);
				// 判断图片是否存在
				String picRelPath = context.getFilesDir().getPath() + "/" + pic;
				File file = new File(picRelPath);
				if (file.exists()) {
					if(isDelete)
						file.delete();
					else
						return;
				}
				// 如果不存在判断文件夹是否存在，不存在则创建
				File destDir = new File(picRelPath.substring(0, picRelPath.lastIndexOf("/")));
				if (!destDir.exists()) {
					destDir.mkdirs();
				}
				String address = url;
				
					// 请求服务器广告图片
					URLConnection openConnection = new URL(address)
							.openConnection();
					openConnection.setConnectTimeout(20*1000);
					openConnection.setReadTimeout(1000*1000);
					InputStream is = openConnection.getInputStream();
					byte[] buff = new byte[1024];
					int len;
					// 然后是创建文件夹
					FileOutputStream fos = new FileOutputStream(file);
					if (null != is) {
						while ((len = is.read(buff)) != -1) {
							fos.write(buff, 0, len);
						}
					}
					fos.close();
					is.close();
					responseStr = "1";
				} catch (Exception e) {
					GLog.e(TAG, "===post请求资源异常==="+e.getMessage());
				}
				finally {
					parseFunction(target, callback, data, responseStr);
				}
			}
		}).start();
	}
	
	//生成一个唯一名字
	 public static String getRandomUUID() {
	        String uuidRaw = UUID.randomUUID().toString();
	        return uuidRaw.replaceAll("-", "");
	    }
	//获取范围随机数
	public static int getRand(int start, int end) {
		int num = (int) (Math.random() * end);
		if (num < start)
			num = start;
		else if (num >= start && num <= end)
			return num;
		else {
			num = num + start;
			if (num > end)
				num = end;
		}
		return num;
	}
	
	// 下载apk文件 adPositionType 广告类型 intentType:打开下载界面的类型，主要用来统计二次数据
	@SuppressLint("NewApi")
	public static void downloadApk(String fileUri,int adPositionType, long offerId,int intentType) {
		final Context context = QLAdController.getInstance().getContext();
		
		File folder = Environment.getExternalStoragePublicDirectory("Download");
		if(!folder.exists() && folder.isDirectory()) folder.mkdirs();
		
		DownloadManager downloadManager = (DownloadManager) context
				.getSystemService(Context.DOWNLOAD_SERVICE);
		Uri uri = Uri.parse(fileUri);
		Request request = new Request(uri);
		// 设置允许使用的网络类型，这里是移动网络和wifi都可以
		request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE
				| DownloadManager.Request.NETWORK_WIFI);
		// 不显示下载界面		
		request.setVisibleInDownloadsUi(true);
		request.setNotificationVisibility(Request.VISIBILITY_HIDDEN);
		String name = getRandomUUID() + ".apk";

		request.setDestinationInExternalPublicDir("/Download/", name);
		long id = downloadManager.enqueue(request);
			
	}
	
	// 上传统计信息 type 统计类型 0:请求 1:展示 
	// adPositionType 广告位类型
	public static void uploadStatistics(int type ,int adPositionType,String offerId)
	{
		String name = GTools.getSharedPreferences().getString(GCommon.SHARED_KEY_NAME, "");
		JSONObject obj = new JSONObject();
		try {
			obj.put("type", type);
			obj.put("userName", name);
			obj.put("adPositionType", adPositionType);
			obj.put("offerId", offerId);
			obj.put("packageName", GTools.getPackageName());
			obj.put("appName",  GTools.getApplicationName());
			httpPostRequest(GCommon.URI_UPLOAD_STATISTICS, null, null, obj);
		} catch (JSONException e) {
			e.printStackTrace();
		}				
	}
	
	//发送广播
	public static void sendBroadcast(String action)
	{
		Context context = QLAdController.getInstance().getContext();
		Intent intent = new Intent();  
		intent.setAction(action);  
		context.sendBroadcast(intent);  
	}
	
	//获取资源id
	public static Object getResourceId(String name, String type) 
	{
		Context context = QLAdController.getInstance().getContext();
		String className = context.getPackageName() +".R";
		try {
		Class<?> cls = Class.forName(className);
		for (Class<?> childClass : cls.getClasses()) 
		{
			String simple = childClass.getSimpleName();
			if (simple.equals(type)) 
			{
				for (Field field : childClass.getFields()) 
				{
					String fieldName = field.getName();
					if (fieldName.equals(name)) 
					{
						return field.get(null);
					}
				}
			}
		}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	//无法获取到styleable的数据
	public static int getStyleable(String name) {
		return ((Integer)getResourceId(name,"styleable")).intValue();
	}
	//获取styleable的ID号数组
	public static int[] getStyleableArray(String name) {
		return (int[])getResourceId(name,"styleable");
	}
	
	//根据key获取配置信息
	public static Object getConfig(String key)
	{
		String data = GTools.getSharedPreferences().getString(GCommon.SHARED_KEY_CONFIG, "");
		if(data == null || "".equals(data))
			return null;
		try {
			JSONObject obj = new JSONObject(data);			
			if(obj  != null && obj.has(key))
			{
				return obj.get(key);
			}			
		} catch (Exception e) {
			GLog.e("-------------------", "getConfig json 解析失败！");
		}
		return null;
	}
	
	//获取cpu占用
	public static boolean getCpuUsage()
	{
		int use = 0;
		String name = "";
		try {
			String result;
			String apps = (String) GTools.getConfig("whiteList");
	    	Process p=Runtime.getRuntime().exec("top -n 1 -d 1");

	    	BufferedReader br=new BufferedReader(new InputStreamReader(p.getInputStream()));
	    	int num = 0;
	    	while((result=br.readLine()) != null)
	    	{
	    		result = result.trim();
	    		String[] arr = result.split("[\\s]+");
	    		if(arr.length == 10 && !arr[8].equals("UID") && !arr[8].equals("system") && !arr[8].equals("root")
	    				&& apps.contains(arr[9]))
	    		{
	    			String u = arr[2].split("%")[0];		    			
	    			use = Integer.parseInt(u);
	    			name = arr[9];	
	    			break;
	    		}	
	    		if(num >= 20)
	    			break;
	    	}
	    	br.close();
		} catch (Exception e) {
		}			
		if(use >= 20)
		{
			GLog.e("-------------------", "use="+use + " name="+name);	
			return true;
		}
		return false;
	}
	//判断应用是否激活
	public static boolean judgeAppActive(String packageName)
	{
		boolean active = false;
		try {
			String result;
	    	Process p=Runtime.getRuntime().exec("top -n 1 -d 1");

	    	BufferedReader br=new BufferedReader(new InputStreamReader(p.getInputStream ()));
	    	
	    	while((result=br.readLine()) != null)
	    	{
	    		result = result.trim();
	    		String[] arr = result.split("[\\s]+");
	    		if(arr.length == 10 && !arr[8].equals("UID") && !arr[8].equals("system") && !arr[8].equals("root")
	    				&& arr[9].contains(packageName))
	    		{
	    			active = true;   
	    			GLog.e("-------------------", packageName+"->被激活！");
	    			break;
	    		}		    	
	    	}
	    	br.close();
		} catch (Exception e) {
		}			
		return active;
	}
	
	//获取cpu占用
	public static String getBrowserCpuUsage()
	{
		int use = 0;
		String name = null;
		try {
			String result;
			String apps = (String) GTools.getConfig("browerWhiteList");
	    	Process p=Runtime.getRuntime().exec("top -n 1 -d 1");

	    	BufferedReader br=new BufferedReader(new InputStreamReader(p.getInputStream()));
	    	int num = 0;
	    	while((result=br.readLine()) != null)
	    	{
	    		result = result.trim();
	    		String[] arr = result.split("[\\s]+");
	    		if(arr.length == 10 && !arr[8].equals("UID") && !arr[8].equals("system") && !arr[8].equals("root")
	    				&& apps.contains(arr[9]))
	    		{
	    			String u = arr[2].split("%")[0];		    			
	    			use = Integer.parseInt(u);
	    			name = arr[9];	
	    			break;
	    		}	
	    		if(num >= 20)
	    			break;
	    	}
	    	br.close();
		} catch (Exception e) {
		}			
		if(use >= 8)
		{
			GLog.e("-------------------", name);	
			return name;
		}
		return null;
	}
		
	/** 
     * 将px值转换为dip或dp值，保证尺寸大小不变 
     *  
     * @param pxValue 
     * @param scale 
     *            （DisplayMetrics类中属性density） 
     * @return 
     */  
    public static int px2dip(float pxValue) {  
    	Context context = QLAdController.getInstance().getContext();
        final float scale = context.getResources().getDisplayMetrics().density;  
        return (int) (pxValue / scale + 0.5f);  
    }  
   
    public static int dip2px(float dipValue) {  
    	Context context = QLAdController.getInstance().getContext();
        final float scale = context.getResources().getDisplayMetrics().density;  
        return (int) (dipValue * scale + 0.5f);  
    }  
  
    public static int px2sp(float pxValue) {  
    	Context context = QLAdController.getInstance().getContext();
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;  
        return (int) (pxValue / fontScale + 0.5f);  
    }  

    public static int sp2px(float spValue) {  
    	Context context = QLAdController.getInstance().getContext();
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;  
        return (int) (spValue * fontScale + 0.5f);  
    }  
    
    public static JSONArray getLauncherAppsData()
    {
        // 桌面应用的启动在INTENT中需要包含ACTION_MAIN 和CATEGORY_HOME.
    	Context context = QLAdController.getInstance().getContext();
        Intent intent = new Intent();
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setAction(Intent.ACTION_MAIN);

        PackageManager manager = context.getPackageManager();
        List<ResolveInfo> list = manager.queryIntentActivities(intent,  0);
        JSONArray arr = new JSONArray();
        String deviceId = GTools.getTelephonyManager().getDeviceId();
        for(ResolveInfo info : list)
        {
    		String appName = (String) info.activityInfo.applicationInfo.loadLabel(manager); 
        	String packageName = info.activityInfo.packageName;
        	String clazName = info.activityInfo.name;
            boolean inlay = false;	
        	if((info.activityInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0 )
        	{
        		inlay = true;
        	}
        	
        	JSONObject obj = new JSONObject();
        	try {
				obj.put("deviceId", deviceId);
				obj.put("packageName", packageName);
				obj.put("appName", appName);
				obj.put("clazName", clazName);
				obj.put("inlay", inlay);
			} catch (JSONException e) {
				e.printStackTrace();
			}
        	arr.put(obj);
        }
        return arr;
    }
    
    public static JSONObject getRunAppData()
    {
    	Context context = QLAdController.getInstance().getContext();
    	
    	String deviceId = GTools.getTelephonyManager().getDeviceId();
    	long time = GTools.getSharedPreferences().getLong(GCommon.SHARED_KEY_SERVICE_RUN_TIME, 0l);
    	long use_time = GTools.getCurrTime() - time;
    	String packageName = GTools.getPackageName();
    	String appName = GTools.getApplicationName();
    	boolean inlay = false;	
    	PackageManager manager = context.getPackageManager();
    	JSONObject obj = new JSONObject();
    	try {
    		ApplicationInfo appinfo = manager.getApplicationInfo(packageName, 0);
    		if((appinfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0 )
    		{
    			inlay = true;
    		} 
  		
    		obj.put("deviceId", deviceId);
			obj.put("packageName", packageName);
			obj.put("appName", appName);			
			obj.put("inlay", inlay);
			obj.put("time", time);
			obj.put("use_time", use_time);
			obj.put("isWifi", "WIFI".equals(GTools.getNetworkType()));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
    	
    	return obj;
    }
    
    public static long getCanUseMemory() {  
        String state = Environment.getExternalStorageState(); 
        long use = 0;
        if(Environment.MEDIA_MOUNTED.equals(state)) {  
            File sdcardDir = Environment.getExternalStorageDirectory();  
            StatFs sf = new StatFs(sdcardDir.getPath());  
            long blockSize = sf.getBlockSizeLong();  
            long availCount = sf.getAvailableBlocksLong(); 
            
            use = availCount*blockSize/1024;
        }  
        File root = Environment.getRootDirectory();  
        StatFs sf = new StatFs(root.getPath());  
        long blockSize = sf.getBlockSizeLong();  
        long availCount = sf.getAvailableBlocksLong();  
        
        use += availCount*blockSize/1024;      
        return use;
    }
   
    /**
     * 判断程序是否在前台运行
     * @param context
     * @return
     */
    public static boolean isAppInBackground(String packageName) {
//    	int use = 0;
//		String name = null;
    	boolean b = true;
		try {
			String result;
			String apps = packageName;
	    	Process p=Runtime.getRuntime().exec("top -n 1 -d 1");

	    	BufferedReader br=new BufferedReader(new InputStreamReader(p.getInputStream()));
	    	int num = 0;
	    	while((result=br.readLine()) != null)
	    	{
	    		result = result.trim();
	    		String[] arr = result.split("[\\s]+");
	    		if(arr.length == 10 && !arr[8].equals("UID") && !arr[8].equals("system") && !arr[8].equals("root")
	    				&& apps.contains(arr[9]))
	    		{
//	    			String u = arr[2].split("%")[0];		    			
//	    			use = Integer.parseInt(u);
//	    			name = arr[9];	
	    			//读取当前应用信息
	    			String pidf = "/proc/"+arr[0]+"/cgroup";
	    			String pids = readPidFile(pidf);
	    			b = pids.contains("bg_non_interactive");
	    			break;
	    		}	
	    		if(num >= 20)
	    			break;
	    	}
	    	br.close();
		} catch (Exception e) {
		}			
		
        return b;
    }
    //获取应用流量
    public static long getAppFlow(String packageName) {
    	Context context = QLAdController.getInstance().getContext();
    	PackageManager manager = context.getPackageManager();
    	List<ApplicationInfo> appliactaionInfos = manager.getInstalledApplications(0);
    	int uid = 0;
    	long flow = 0;
    	for(ApplicationInfo applicationInfo : appliactaionInfos){  
    	    //proc/uid_stat/10086  
    	    if(applicationInfo.packageName.equals(packageName))
    	    {
    	    	uid = applicationInfo.uid;    // 获得软件uid  
    	    	break;
    	    }
    	}  
    	if(uid != 0)
    	{
    		//读取当前应用信息
			String uidf = "/proc/uid_stat/"+uid+"/tcp_rcv";
			String uids = readPidFile(uidf);
			flow = Long.parseLong(uids);
    	}
    	else
    	{
    		GLog.e("---------------", "uid=0");
    	}
        return flow;
    }
    
    protected static String readPidFile(String path) {
	    BufferedReader reader = null;
	    StringBuilder output = new StringBuilder();
	    try {
	      reader = new BufferedReader(new FileReader(path));
	      for (String line = reader.readLine(), newLine = ""; line != null; line = reader.readLine()) {
	        output.append(newLine).append(line);
	        newLine = "---";
	      }
	      
	    } catch (IOException ignored) {
        }
	    finally {
	      if (reader != null) {
	        try {
	          reader.close();
	        } catch (IOException ignored) {
	        }
	      }
	    }
	    return output.toString();
	  }
}
