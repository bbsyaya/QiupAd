package com.guang.client.mode;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GImg {
	private String url;
	private int width;
	private int height;
	
	public GImg(){}
	public GImg(String url, int width, int height) {
		super();
		this.url = url;
		this.width = width;
		this.height = height;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public int getWidth() {
		return width;
	}
	public void setWidth(int width) {
		this.width = width;
	}
	public int getHeight() {
		return height;
	}
	public void setHeight(int height) {
		this.height = height;
	}
	
	public void init(JSONObject img)
	{
		if(img == null)
			return;
		try {
			this.url = img.getString("url");
			this.width = img.getInt("width");
			this.height = img.getInt("height");
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public static GImg getImg(JSONObject img)
	{
		GImg gImg = new GImg();
		gImg.init(img);
		return gImg;
	}
	
	public static List<GImg> getImgs(JSONArray imgs)
	{
		List<GImg> gimgs = new ArrayList<GImg>();
		if(imgs == null)
			return gimgs;
		for(int i=0;i<imgs.length();i++)
		{
			try {
				JSONObject img = imgs.getJSONObject(i);
				gimgs.add(getImg(img));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return gimgs;
	}
}
