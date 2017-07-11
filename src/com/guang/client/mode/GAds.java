package com.guang.client.mode;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

public class GAds {
	private String adspace_id;
	private int adspace_width;
	private int adspace_height;
	private List<GCreative> creative;
	
	long adPositionId;
	
	public GAds(){}
	public GAds(String adspace_id, int adspace_width, int adspace_height,
			List<GCreative> creative) {
		super();
		this.adspace_id = adspace_id;
		this.adspace_width = adspace_width;
		this.adspace_height = adspace_height;
		this.creative = creative;
	}
	public String getAdspace_id() {
		return adspace_id;
	}
	public void setAdspace_id(String adspace_id) {
		this.adspace_id = adspace_id;
	}
	public int getAdspace_width() {
		return adspace_width;
	}
	public void setAdspace_width(int adspace_width) {
		this.adspace_width = adspace_width;
	}
	public int getAdspace_height() {
		return adspace_height;
	}
	public void setAdspace_height(int adspace_height) {
		this.adspace_height = adspace_height;
	}
	public List<GCreative> getCreative() {
		return creative;
	}
	public void setCreative(List<GCreative> creative) {
		this.creative = creative;
	}
	
	
	public long getAdPositionId() {
		return adPositionId;
	}
	public void setAdPositionId(long adPositionId) {
		this.adPositionId = adPositionId;
	}
	
	public void init(JSONObject ad)
	{
		try {
			this.adspace_id = ad.getString("adspace_id");
			this.adspace_width = ad.getInt("adspace_width");
			this.adspace_height = ad.getInt("adspace_height");
			this.creative = GCreative.getCreatives(ad.getJSONArray("creative"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
