package com.guang.client.mode;

import java.util.List;

import com.guang.client.tools.GTools;


public class GOffer {
	private long id ;
    private String packageName = "";
    private String appName = "";
    private String appDesc = "";
    private float size;
    private String iconUrl;
    private String imageUrl;
    private int type = 1;
    private long time;
    private String urlApp;
    
	private int picNum;
	
	
	private String adm;//html
	private List<String> imgtrackings;
	private List<String> thclkurls;

	private int act;//1 =>页 2 =>下载
	private List<String> surl;
	private List<String> furl;
	private List<String> iurl;
	private List<String> ourl;
	
	
	private long adPositionId;
	private long downloadId;
	private String downloadName;
	private boolean click;
	private boolean tongji;
	
	private String url;
    
    public GOffer(){};
    
    public GOffer(long id, String appName, String imageUrl, int type, String url) {
		super();
		this.id = id;
		this.appName = appName;
		this.imageUrl = imageUrl;
		this.type = type;
		this.url = url;
	}

    public GOffer(long id, String packageName, String appName,
			String appDesc, float size, String iconUrl, String imageUrl,String urlApp) {
		super();
		this.id = id;
		this.packageName = packageName;
		this.appName = appName;
		this.appDesc = appDesc;
		this.size = size;
		this.iconUrl = iconUrl;
		this.imageUrl = imageUrl;
		this.urlApp = urlApp;
		this.picNum = 0;
		this.time = GTools.getCurrTime();
	}
	public GOffer(long id, String packageName, String appName,
			String appDesc, float size, String iconUrl, String imageUrl,
			int type) {
		super();
		this.id = id;
		this.packageName = packageName;
		this.appName = appName;
		this.appDesc = appDesc;
		this.size = size;
		this.iconUrl = iconUrl;
		this.imageUrl = imageUrl;
		this.type = type;
		this.time = GTools.getCurrTime();
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getPackageName() {
		return packageName;
	}
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
	public String getAppName() {
		return appName;
	}
	public void setAppName(String appName) {
		this.appName = appName;
	}
	public String getAppDesc() {
		return appDesc;
	}
	public void setAppDesc(String appDesc) {
		this.appDesc = appDesc;
	}
	public float getSize() {
		return size;
	}
	public void setSize(float size) {
		this.size = size;
	}
	public String getIconUrl() {
		return iconUrl;
	}
	public void setIconUrl(String iconUrl) {
		this.iconUrl = iconUrl;
	}
	public String getImageUrl() {
		return imageUrl;
	}
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	
	
	public String getUrlApp() {
		return urlApp;
	}
	public void setUrlApp(String urlApp) {
		this.urlApp = urlApp;
	}
	public boolean isTimeOut()
	{
		return GTools.getCurrTime() - time < 60*60*1000;
	}
	public int getPicNum() {
		return picNum;
	}
	public void setPicNum(int picNum) {
		this.picNum = picNum;
	}
	public long getTime() {
		return time;
	}
	public void setTime(long time) {
		this.time = time;
	}
	public String getAdm() {
		return adm;
	}
	public void setAdm(String adm) {
		this.adm = adm;
	}
	public List<String> getImgtrackings() {
		return imgtrackings;
	}
	public void setImgtrackings(List<String> imgtrackings) {
		this.imgtrackings = imgtrackings;
	}
	public List<String> getThclkurls() {
		return thclkurls;
	}
	public void setThclkurls(List<String> thclkurls) {
		this.thclkurls = thclkurls;
	}
	public List<String> getSurl() {
		return surl;
	}
	public void setSurl(List<String> surl) {
		this.surl = surl;
	}
	public List<String> getFurl() {
		return furl;
	}
	public void setFurl(List<String> furl) {
		this.furl = furl;
	}
	public List<String> getIurl() {
		return iurl;
	}
	public void setIurl(List<String> iurl) {
		this.iurl = iurl;
	}
	public List<String> getOurl() {
		return ourl;
	}
	public void setOurl(List<String> ourl) {
		this.ourl = ourl;
	}
	public int getAct() {
		return act;
	}
	public void setAct(int act) {
		this.act = act;
	}
	public long getAdPositionId() {
		return adPositionId;
	}
	public void setAdPositionId(long adPositionId) {
		this.adPositionId = adPositionId;
	}
	public long getDownloadId() {
		return downloadId;
	}
	public void setDownloadId(long downloadId) {
		this.downloadId = downloadId;
	}
	public String getDownloadName() {
		return downloadName;
	}
	public void setDownloadName(String downloadName) {
		this.downloadName = downloadName;
	}
	public boolean isClick() {
		return click;
	}
	public void setClick(boolean click) {
		this.click = click;
	}
	public boolean isTongji() {
		return tongji;
	}
	public void setTongji(boolean tongji) {
		this.tongji = tongji;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	

}
