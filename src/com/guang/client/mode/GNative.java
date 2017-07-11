package com.guang.client.mode;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

public class GNative {
	private List<GImg> ctimg;
	private String title;
	private GImg logo;
	private String desc;//描述
	private String intro;//简介
	private String category;//分类
	private List<GImg> hiimg;//高清图
	private String starrate;//星级1-5
	private String comcnt;//评论数
	private String packagename;
	private int size;
	private int version;
	private String versionname;
	private String downcnt;//下载数
	private String inscnt;//安装数
	
	public GNative(){}
	public GNative(List<GImg> ctimg, String title, GImg logo, String desc,
			String intro, String category, List<GImg> hiimg, String starrate,
			String comcnt, String packagename, int size, int version,
			String versionname, String downcnt, String inscnt) {
		super();
		this.ctimg = ctimg;
		this.title = title;
		this.logo = logo;
		this.desc = desc;
		this.intro = intro;
		this.category = category;
		this.hiimg = hiimg;
		this.starrate = starrate;
		this.comcnt = comcnt;
		this.packagename = packagename;
		this.size = size;
		this.version = version;
		this.versionname = versionname;
		this.downcnt = downcnt;
		this.inscnt = inscnt;
	}
	public List<GImg> getCtimg() {
		return ctimg;
	}
	public void setCtimg(List<GImg> ctimg) {
		this.ctimg = ctimg;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public GImg getLogo() {
		return logo;
	}
	public void setLogo(GImg logo) {
		this.logo = logo;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public String getIntro() {
		return intro;
	}
	public void setIntro(String intro) {
		this.intro = intro;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public List<GImg> getHiimg() {
		return hiimg;
	}
	public void setHiimg(List<GImg> hiimg) {
		this.hiimg = hiimg;
	}
	public String getStarrate() {
		return starrate;
	}
	public void setStarrate(String starrate) {
		this.starrate = starrate;
	}
	public String getComcnt() {
		return comcnt;
	}
	public void setComcnt(String comcnt) {
		this.comcnt = comcnt;
	}
	public String getPackagename() {
		return packagename;
	}
	public void setPackagename(String packagename) {
		this.packagename = packagename;
	}
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}
	public int getVersion() {
		return version;
	}
	public void setVersion(int version) {
		this.version = version;
	}
	public String getVersionname() {
		return versionname;
	}
	public void setVersionname(String versionname) {
		this.versionname = versionname;
	}
	public String getDowncnt() {
		return downcnt;
	}
	public void setDowncnt(String downcnt) {
		this.downcnt = downcnt;
	}
	public String getInscnt() {
		return inscnt;
	}
	public void setInscnt(String inscnt) {
		this.inscnt = inscnt;
	}
	
	
	public void init(JSONObject gnative)
	{
		if(gnative == null)
			return;
		try {
			this.ctimg = GImg.getImgs(gnative.getJSONArray("ctimg"));
			this.title = gnative.getString("title");
			this.logo = GImg.getImg(gnative.getJSONObject("logo"));
			this.desc = gnative.getString("desc");
			this.intro = gnative.getString("intro");
			this.category = gnative.getString("category");
			this.hiimg = GImg.getImgs(gnative.getJSONArray("hiimg"));
			this.starrate = gnative.getString("starrate");
			this.comcnt = gnative.getString("comcnt");
			this.packagename = gnative.getString("packagename");
			this.size = gnative.getInt("size");
			this.version = gnative.getInt("version");
			this.versionname = gnative.getString("versionname");
			this.downcnt = gnative.getString("downcnt");
			this.inscnt = gnative.getString("inscnt");
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public static GNative getNative(JSONObject gnative)
	{
		GNative gNative2 = new GNative();
		gNative2.init(gnative);
		return gNative2;
	}
}
