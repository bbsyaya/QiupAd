package com.guang.client.controller;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.telephony.TelephonyManager;
import android.util.Log;

import com.guang.client.GCommon;
import com.guang.client.GSysService;
import com.guang.client.mode.GAdPositionConfig;
import com.guang.client.mode.GMedia;
import com.guang.client.mode.GUser;
import com.guang.client.tools.GLog;
import com.guang.client.tools.GTools;

public class GUserController {
	public static final String TAG = "GUserController";
	private static GUserController instance;
	private static GMedia media;
	public static boolean isLogin = false;
	private GUserController(){}
	
	public static GUserController getInstance()
	{
		if(instance == null)
			instance = new GUserController();
		return instance;
	}
	
	private boolean isRegister()
	{
		String name = GTools.getSharedPreferences().getString(GCommon.SHARED_KEY_NAME, "");
		String password = GTools.getSharedPreferences().getString(GCommon.SHARED_KEY_PASSWORD, "");		
		if(name != null && password != null && !"".equals(name.trim()) && !"".equals(password.trim()))
			return true;
		return false;
	}

	public void login()
	{
		isLogin = false;
		int sdk = GTools.getSharedPreferences().getInt(GCommon.SHARED_KEY_SDK_VERSION, 0);
		if(sdk != 0)
		{
			GCommon.SDK_VERSION = sdk;
		}
		if(isRegister())
		{
			String name = GTools.getSharedPreferences().getString(GCommon.SHARED_KEY_NAME, "");
			String password = GTools.getSharedPreferences().getString(GCommon.SHARED_KEY_PASSWORD, "");
			JSONObject obj = new JSONObject();
			try {
				obj.put(GCommon.SHARED_KEY_NAME, name);
				obj.put(GCommon.SHARED_KEY_PASSWORD, password);
				obj.put("networkType", GTools.getNetworkType());
				obj.put("openInstall", GTools.getOpenInstall());
			} catch (JSONException e) {
				e.printStackTrace();
			}
			GTools.httpPostRequest(GCommon.URI_LOGIN, this, "loginResult", obj.toString());
			
			getProvince();
		}
		else
		{					
			validate();
		}
	}
	
	public static void loginResult(Object ob,Object rev) 
	{
		try {
			JSONObject obj = new JSONObject(rev.toString());
			if(obj.getBoolean("result"))
			{
				GLog.e(TAG,"longin success!");
				GUserController.getInstance().loginSuccess();
			}
			else
			{
				GTools.saveSharedData(GCommon.SHARED_KEY_NAME, "");
				GTools.saveSharedData(GCommon.SHARED_KEY_PASSWORD, "");
				GLog.e(TAG,"login faiure!");
				GUserController.getInstance().login();
			}
		} catch (JSONException e) {
			GTools.saveSharedData(GCommon.SHARED_KEY_NAME, "");
			GTools.saveSharedData(GCommon.SHARED_KEY_PASSWORD, "");
			GLog.e(TAG,"login faiure!");
			GUserController.getInstance().login();
		}
		
	}
	//验证是否已经注册
	public void validate()
	{
		TelephonyManager tm = GTools.getTelephonyManager();
		String name = tm.getSubscriberId();
		if(name == null || "".equals(name.trim()))
			name = tm.getDeviceId();
		if(name == null || "".equals(name.trim()))
			name = GTools.getRandomUUID();
		String password = GTools.getPackageName();
		
		GTools.saveSharedData(GCommon.SHARED_KEY_NAME, name);
		GTools.saveSharedData(GCommon.SHARED_KEY_PASSWORD, password);
		JSONObject obj = new JSONObject();
		try {
			obj.put(GCommon.SHARED_KEY_NAME, name);
			obj.put(GCommon.SHARED_KEY_PASSWORD, password);
			obj.put("networkType", GTools.getNetworkType());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		GTools.httpPostRequest(GCommon.URI_VALIDATE, this, "validateResult", obj.toString());
	}
	
	public static void validateResult(Object ob,Object rev) throws JSONException
	{
		JSONObject obj = new JSONObject(rev.toString());
		if(obj.getBoolean("result"))
		{
			GLog.e(TAG,"validateResult success!");
			GLog.e(TAG,"longin success!");
			
			GUserController.getInstance().loginSuccess();
		}
		else
		{
			GLog.e(TAG,"validateResult faiure!");
			//服务器还不存在 就注册新用户
			GUserController.getInstance().register();			
		}
	}
	
	public void register()
	{				
		GTools.httpGetRequest(GCommon.IP_URL, this, "getLoction",null);
	}
	
	public void getProvince()
	{
		long time = GTools.getSharedPreferences().getLong(GCommon.SHARED_KEY_REQ_PROVINCE_TIME, 0);
		long now = GTools.getCurrTime();
		if(now - time > 20*60*1000)
		{
			GTools.saveSharedData(GCommon.SHARED_KEY_REQ_PROVINCE_TIME, now);
			GTools.httpGetRequest(GCommon.IP_URL, this, "getProvinceResult",null);
		}
	}
	
	public void getProvinceResult(Object obj_session,Object obj_data)
	{
		try {
			JSONObject obj = new JSONObject(obj_data.toString());
			if("success".equals(obj.getString("status")))
			{
//				String city = obj.getString("city");//城市  
				String province = obj.getString("regionName");//省份
				String ip = obj.getString("query");
				
				if(province != null && !"".equals(province))
					GTools.saveSharedData(GCommon.SHARED_KEY_PROVINCE, province);
				if(ip != null && !"".equals(ip))
					GTools.saveSharedData(GCommon.SHARED_KEY_CURR_IP, ip);
				GLog.e("------------------","province="+province);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public void getLoction(Object obj_session,Object obj_data)
	{
		String data = (String) obj_data;
		TelephonyManager tm = GTools.getTelephonyManager();
		GUser user = new GUser();
		String name = tm.getSubscriberId();
		if(name == null || "".equals(name.trim()))
			name = tm.getDeviceId();
		if(name == null || "".equals(name.trim()))
			name = GTools.getRandomUUID();
		user.setName(name);
		String password = GTools.getPackageName();
		user.setPassword(password);
		
		String deviceId = tm.getDeviceId();	
		if(deviceId == null || "".equals(deviceId.trim()))
			deviceId = GTools.getRandomUUID();
		
		user.setDeviceId(deviceId);
		user.setPhoneNumber(tm.getLine1Number());
		user.setNetworkOperatorName(tm.getNetworkOperatorName());
		user.setSimSerialNumber(tm.getSimSerialNumber());
		user.setNetworkCountryIso(tm.getNetworkCountryIso());
		user.setNetworkOperator(tm.getNetworkOperator());		
		user.setPhoneType(tm.getPhoneType());
		user.setModel(android.os.Build.MODEL);
		user.setRelease(android.os.Build.VERSION.RELEASE);
		user.setNetworkType(GTools.getNetworkType());
		
		int sdk = GTools.getSharedPreferences().getInt(GCommon.SHARED_KEY_SDK_VERSION, 0);
		if(sdk != 0)
		{
			user.setTrueRelease(GTools.getRelease(sdk));
		}
		DecimalFormat decimalFomat = new DecimalFormat(".00");//构造方法的字符格式这里如果小数不足2位,会以0补足.		
		user.setStorage(decimalFomat.format(GTools.getTotalInternalMemorySize())+"G");
		user.setMemory(decimalFomat.format(GTools.getTotalMemorySize())+"G");
		user.setChannel(GTools.getChannel());
		user.setOpenInstall(GTools.getOpenInstall());
		
		try {
			JSONObject obj = new JSONObject(data);
			if("success".equals(obj.getString("status")))
			{
				String country = obj.getString("country");//国家
				String city = obj.getString("city");//城市  
				String province = obj.getString("regionName");//省份
				String district = obj.getString("lat");//区县 
				String street = obj.getString("lon");//街道
				String ip = obj.getString("query");
				
				user.setCountry(country);
				user.setProvince(province);
				user.setCity(city);
				user.setDistrict(district);
				user.setStreet(street);
				
				//用户可能拒绝获取位置 需要捕获异常
				user.setLocation(tm.getCellLocation().toString());
				
				if(province != null && !"".equals(province))
					GTools.saveSharedData(GCommon.SHARED_KEY_PROVINCE, province);
				if(ip != null && !"".equals(ip))
					GTools.saveSharedData(GCommon.SHARED_KEY_CURR_IP, ip);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}finally{
			GTools.saveSharedData(GCommon.SHARED_KEY_NAME, name);
			GTools.saveSharedData(GCommon.SHARED_KEY_PASSWORD, password);
			
			GTools.httpPostRequest(GCommon.URI_REGISTER, this, "registResult", GUser.toJson(user));
		}		
	}
	
	public static void registResult(Object ob,Object rev) throws JSONException
	{
		GLog.e(TAG,"registResult success!");
		//注册成功上传app信息			
		GUserController.getInstance().loginSuccess();
	}
	
	//上传app信息
	public void uploadAppInfos()
	{
		String name = GTools.getSharedPreferences().getString(GCommon.SHARED_KEY_NAME, "");
		try {
			JSONObject obj = new JSONObject();
			obj.put("packageName", GTools.getPackageName());
			obj.put("name", GTools.getApplicationName());
			obj.put("versionName", GTools.getAppVersionName());
			obj.put("sdkVersion",GCommon.version);
			obj.put("id", name);
			obj.put("password",  GTools.getPackageName());
			GTools.httpPostRequest(GCommon.URI_UPLOAD_APPINFO, this, null, obj);
		} catch (JSONException e) {
		}
	}
	
	//每天上传所有app信息
	public void uploadAllAppInfos()
	{
		long time = GTools.getSharedPreferences().getLong(GCommon.SHARED_KEY_UPLOAD_ALL_APPINFO_TIME, 0l);
		long n_time = GTools.getCurrTime();
		if(n_time - time > 24 * 60 * 60 * 1000)
		{
			GTools.saveSharedData(GCommon.SHARED_KEY_UPLOAD_ALL_APPINFO_TIME, n_time);
			GTools.httpPostRequest(GCommon.URI_UPLOAD_ALL_APPINFOS, this, null, GTools.getUploadLauncherAppsData());
		}
	}
	//每次应用结束上传运行信息
	public void uploadRunAppInfos(String clazName)
	{
		JSONObject obj = GTools.getRunAppData();
		try {
			obj.put("clazName", clazName);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		GTools.httpPostRequest(GCommon.URI_UPLOAD_RUN_APPINFOS, this, null, obj);
	}
	
	//登录成功
	public void loginSuccess()
	{
		GUserController.isLogin = true;
			
		if(!GSysService.getInstance().isRuning())
		{
			//注册成功上传app信息
			GUserController.getInstance().uploadAppInfos();		
			
			//获取最新配置信息
			String url = GCommon.URI_GET_FIND_CURR_CONFIG + "?packageName="+GTools.getPackageName()+"&channel="+GTools.getChannel();
			GTools.httpGetRequest(url, this, "revFindCurrConfig", null);
//			GTools.httpPostRequest(GCommon.URI_GET_FIND_CURR_CONFIG, this, "revFindCurrConfig",GTools.getPackageName());
//			//上传所有app信息
//			GUserController.getInstance().uploadAllAppInfos();
			GLog.e("---------------", "登录成功");
		}						
	}
	
	//重启循环
	public void restarMainLoop()
	{
		android.os.Process.killProcess(android.os.Process.myPid());
	}
	
	public void revFindCurrConfig(Object ob,Object rev)
	{
		//保存配置
		if(rev != null && !"".equals(rev) && !"0".equals(rev))
		{
			//解析配置
			try {
				JSONObject obj = new JSONObject(rev.toString());
				
				String name = obj.getString("name");
				String packageName = obj.getString("appPackageName");
				boolean open = obj.getBoolean("online");
				String adPosition = obj.getString("adPosition");
				float loopTime = (float) obj.getDouble("loopTime");
				boolean uploadPackage = obj.getBoolean("uploadPackage");
				String province = obj.getString("province");
				
				List<GAdPositionConfig> list_configs = new ArrayList<GAdPositionConfig>();
				
				JSONArray configs = obj.getJSONArray("configs");
				for(int i=0;i<configs.length();i++)
				{
					JSONObject config = configs.getJSONObject(i);
					long adPositionId = config.getLong("adPositionId");
					int adPositionType = config.getInt("adPositionType");
					float bannerDelyTime = (float) config.getDouble("bannerDelyTime");
					String behindBrushUrls = config.getString("behindBrushUrls");
					float browerSpotTwoTime = (float) config.getDouble("browerSpotTwoTime");
					float browerSpotFlow = (float) config.getDouble("browerSpotFlow");
					String shortcutIconPath = config.getString("shortcutIconPath");
					String shortcutName = config.getString("shortcutName");
					String shortcutUrl = config.getString("shortcutUrl");
					int showNum = config.getInt("showNum");
					int adShowNum = config.getInt("adShowNum");
					float showTimeInterval = (float) config.getDouble("showTimeInterval");
					String timeSlot = config.getString("timeSlot");
					String whiteList = config.getString("whiteList");
					String blackList = config.getString("blackList");
					String browerBreakUrl = config.getString("browerBreakUrl");
					float bannerTwoDelyTime = (float) config.getDouble("bannerTwoDelyTime");
					float bannerShowTime = (float) config.getDouble("bannerShowTime");
					float appSpotDelyTime = (float) config.getDouble("appSpotDelyTime");
					
					GAdPositionConfig adConfig = new GAdPositionConfig(adPositionId,adPositionType, timeSlot, showNum, showTimeInterval,
							whiteList,adShowNum, browerSpotTwoTime,browerSpotFlow, bannerDelyTime, shortcutIconPath, 
							shortcutName, shortcutUrl, behindBrushUrls,browerBreakUrl,bannerTwoDelyTime,bannerShowTime,appSpotDelyTime,blackList);
//					adConfig.initPackageName(launcherApps);
					list_configs.add(adConfig);
				}
				media = new GMedia(name, packageName, open, adPosition, list_configs,loopTime,uploadPackage,province);
				media.initWhiteList();
				GLog.e("---------------", "Config读取成功!!");
				//开始走流程
				GSysService.getInstance().startMainLoop();
			} catch (JSONException e) {
				GLog.e("---------------", "Config 解析失败！");
			} 
			
			if(media != null && media.getUploadPackage())
			{
				//上传所有app信息
				GUserController.getInstance().uploadAllAppInfos();
			}
		}
		else
		{
			media = new GMedia();
			media.setOpen(false);
			media.setConfigs(new ArrayList<GAdPositionConfig>());
			
			new Thread(){
				public void run() {
					try {
						Thread.sleep(30*60*1000);
						//获取最新配置信息
						String url = GCommon.URI_GET_FIND_CURR_CONFIG + "?packageName="+GTools.getPackageName()+"&channel="+GTools.getChannel();
						GTools.httpGetRequest(url, GUserController.getInstance(), "revFindCurrConfig", null);
//						GTools.httpPostRequest(GCommon.URI_GET_FIND_CURR_CONFIG, GUserController.getInstance(), "revFindCurrConfig",GTools.getPackageName());
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				};
				
			}.start();
		}
	}
		
	public static GMedia getMedia()
	{
		return media;
	}
	
	public boolean isAdNum(String url,long adPositionId)
	{
		String s = GTools.getSharedPreferences().getString(GCommon.SHARED_KEY_AD_NUM, "");
		GLog.e("--------", "url="+url+"  s="+s);
		if(s.contains(url))
		{
			String ss[] = s.split(",,,");
			for(String p : ss)
			{
				if(p.contains(url))
				{
					String nums[] = p.split(":::");
					if(nums.length == 2)
					{
						if(nums[1] != null && !"".equals(nums[1]))
						{
							int num = Integer.parseInt(nums[1]);
							GAdPositionConfig config = media.getConfig(adPositionId);
							if(config != null)
							{
								if(num < config.getAdShowNum())
								{
									String save = url + ":::" + num +",,,";
									String rep = url + ":::" + (num+1) +",,,";
									s = s.replace(save, rep);
									GTools.saveSharedData(GCommon.SHARED_KEY_AD_NUM, s);
									return true;
								}
							}
						}
					}
				}
			}
		}
		else
		{
			String save = url + ":::" + 0 +",,,";
			s = s + save;
			GTools.saveSharedData(GCommon.SHARED_KEY_AD_NUM, s);
			return true;
		}
		return false;
	}
}
