package com.guang.client.mode;

import org.json.JSONException;
import org.json.JSONObject;

public class GAdm {
	private String source;//比如url的图片地址
	private GNative gnative;
	
	public GAdm(){}
	public GAdm(String source, GNative gnative) {
		super();
		this.source = source;
		this.gnative = gnative;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public GNative getGnative() {
		return gnative;
	}
	public void setGnative(GNative gnative) {
		this.gnative = gnative;
	}
	
	public void init(JSONObject adm)
	{
		if(adm == null)
			return;
		try {
			this.source = adm.getString("source");
			this.gnative = GNative.getNative(adm.getJSONObject("native"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public static GAdm getAdm(JSONObject adm)
	{
		GAdm gAdm = new GAdm();
		gAdm.init(adm);
		return gAdm;
	}
}
