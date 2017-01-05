package com.guang.client.mode;

import com.guang.client.tools.GTools;
import com.mobvista.msdk.out.Campaign;


public class GOffer {
	private String id = "";
    private String packageName = "";
    private String appName = "";
    private String appDesc = "";
    private String size = "";
    private String iconUrl = "";
    private String imageUrl = "";
    private int type = 1;
    private Campaign campaign;
    private long time;
    
    public GOffer(){};
	public GOffer(String id, String packageName, String appName,
			String appDesc, String size, String iconUrl, String imageUrl,
			int type,Campaign campaign) {
		super();
		this.id = id;
		this.packageName = packageName;
		this.appName = appName;
		this.appDesc = appDesc;
		this.size = size;
		this.iconUrl = iconUrl;
		this.imageUrl = imageUrl;
		this.type = type;
		this.campaign = campaign;
		this.time = GTools.getCurrTime();
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
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
	public String getSize() {
		return size;
	}
	public void setSize(String size) {
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
	public Campaign getCampaign() {
		return campaign;
	}
	public void setCampaign(Campaign campaign) {
		this.campaign = campaign;
	}
	
	public boolean isTimeOut()
	{
		return GTools.getCurrTime() - time < 60*60*1000;
	}
}
