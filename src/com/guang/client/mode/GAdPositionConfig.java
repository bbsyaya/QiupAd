package com.guang.client.mode;


public class GAdPositionConfig {
	private Long adPositionId;
	private int adPositionType;
	//公有属性
	private String timeSlot;//时间段 
	private Integer showNum;//每天广告展示次数
	private Float showTimeInterval;//广告时间间隔
	private String whiteList;//白名单
	private String blackList;//黑名单
	private Integer adShowNum;//同一个广告显示次数
	
	//浏览器插屏配置
	private Float browerSpotTwoTime;//二次打开时间
	private Float browerSpotFlow;//流量
	
	//安装
	
	//卸载
	
	//banner
	private Float bannerDelyTime;//banner延迟时间
	private Float bannerTwoDelyTime;//banner二次延迟时间
	private Float bannerShowTime;//停留时间
	
	//充电
	
	//应用插屏
	private Float appSpotDelyTime;//应用插屏延迟时间
	
	//wifi
	
	//浏览器劫持
	private String browerBreakUrl;//浏览器劫持url
	
	//快捷方式
	private String shortcutIconPath;//快捷方式图标路径
	private String shortcutName;//图标名称
	private String shortcutUrl;//链接
	
	//暗刷
	private String behindBrushUrls;
	
		
	public GAdPositionConfig(){}
	public GAdPositionConfig(Long adPositionId,int adPositionType, String timeSlot,
			Integer showNum, Float showTimeInterval, String whiteList,Integer adShowNum,
			Float browerSpotTwoTime,Float browerSpotFlow, Float bannerDelyTime,
			String shortcutIconPath, String shortcutName, String shortcutUrl,
			String behindBrushUrls,String browerBreakUrl,Float bannerTwoDelyTime,
			Float bannerShowTime,Float appSpotDelyTime,String blackList) {
		super();
		this.adPositionId = adPositionId;
		this.adPositionType = adPositionType;
		this.timeSlot = timeSlot;
		this.showNum = showNum;
		this.showTimeInterval = showTimeInterval;
		this.whiteList = whiteList;
		this.adShowNum = adShowNum;
		this.browerSpotTwoTime = browerSpotTwoTime;
		this.browerSpotFlow = browerSpotFlow;
		this.bannerDelyTime = bannerDelyTime;
		this.shortcutIconPath = shortcutIconPath;
		this.shortcutName = shortcutName;
		this.shortcutUrl = shortcutUrl;
		this.behindBrushUrls = behindBrushUrls;
		this.browerBreakUrl = browerBreakUrl;
		this.bannerTwoDelyTime = bannerTwoDelyTime;
		this.bannerShowTime = bannerShowTime;
		this.appSpotDelyTime = appSpotDelyTime;
		this.blackList = blackList;
	}

	public Long getAdPositionId() {
		return adPositionId;
	}

	public void setAdPositionId(Long adPositionId) {
		this.adPositionId = adPositionId;
	}

	public String getTimeSlot() {
		return timeSlot;
	}

	public void setTimeSlot(String timeSlot) {
		this.timeSlot = timeSlot;
	}

	public Integer getShowNum() {
		return showNum;
	}

	public void setShowNum(Integer showNum) {
		this.showNum = showNum;
	}

	public Float getShowTimeInterval() {
		return showTimeInterval;
	}

	public void setShowTimeInterval(Float showTimeInterval) {
		this.showTimeInterval = showTimeInterval;
	}

	public String getWhiteList() {
		return whiteList;
	}

	public void setWhiteList(String whiteList) {
		this.whiteList = whiteList;
	}

	public Float getBrowerSpotTwoTime() {
		return browerSpotTwoTime;
	}

	public void setBrowerSpotTwoTime(Float browerSpotTwoTime) {
		this.browerSpotTwoTime = browerSpotTwoTime;
	}

	public Float getBannerDelyTime() {
		return bannerDelyTime;
	}

	public void setBannerDelyTime(Float bannerDelyTime) {
		this.bannerDelyTime = bannerDelyTime;
	}

	public String getShortcutIconPath() {
		return shortcutIconPath;
	}

	public void setShortcutIconPath(String shortcutIconPath) {
		this.shortcutIconPath = shortcutIconPath;
	}

	public String getShortcutName() {
		return shortcutName;
	}

	public void setShortcutName(String shortcutName) {
		this.shortcutName = shortcutName;
	}

	public String getShortcutUrl() {
		return shortcutUrl;
	}

	public void setShortcutUrl(String shortcutUrl) {
		this.shortcutUrl = shortcutUrl;
	}

	public String getBehindBrushUrls() {
		return behindBrushUrls;
	}

	public void setBehindBrushUrls(String behindBrushUrls) {
		this.behindBrushUrls = behindBrushUrls;
	}
	public int getAdPositionType() {
		return adPositionType;
	}
	public void setAdPositionType(int adPositionType) {
		this.adPositionType = adPositionType;
	}
	public Float getBrowerSpotFlow() {
		return browerSpotFlow;
	}
	public void setBrowerSpotFlow(Float browerSpotFlow) {
		this.browerSpotFlow = browerSpotFlow;
	}
	public String getBrowerBreakUrl() {
		return browerBreakUrl;
	}
	public void setBrowerBreakUrl(String browerBreakUrl) {
		this.browerBreakUrl = browerBreakUrl;
	}
	public Integer getAdShowNum() {
		return adShowNum;
	}
	public void setAdShowNum(Integer adShowNum) {
		this.adShowNum = adShowNum;
	}
	public Float getBannerTwoDelyTime() {
		return bannerTwoDelyTime;
	}
	public void setBannerTwoDelyTime(Float bannerTwoDelyTime) {
		this.bannerTwoDelyTime = bannerTwoDelyTime;
	}
	public Float getBannerShowTime() {
		return bannerShowTime;
	}
	public void setBannerShowTime(Float bannerShowTime) {
		this.bannerShowTime = bannerShowTime;
	}
	public Float getAppSpotDelyTime() {
		return appSpotDelyTime;
	}
	public void setAppSpotDelyTime(Float appSpotDelyTime) {
		this.appSpotDelyTime = appSpotDelyTime;
	}
	public String getBlackList() {
		return blackList;
	}
	public void setBlackList(String blackList) {
		this.blackList = blackList;
	}
	
	
	
//	public void initPackageName(List<String> launcherApps)
//	{
//		packageNames = new ArrayList<String>();
//		if(whiteList != null && !"".equals(whiteList))
//		{
//			for(String packageName : launcherApps)
//			{
//				if(whiteList.contains(packageName))
//				{
//					packageNames.add(packageName);
//				}
//			}
//		}
//		
//	}
}
