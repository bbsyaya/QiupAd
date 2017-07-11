package com.guang.client.mode;

import org.json.JSONException;
import org.json.JSONObject;

public class GInteraction {

	private String url;//点击后跳转地址，用于BROWSE ; DOWNLOAD
	private String phone;//目的电话号码 用于 DIALING or MESSAGE
	private String mail;
	private String msg;
	private String dplinkurl;//在app内部打开地址
	
	
	public GInteraction(){}
	public GInteraction(String url, String phone, String mail, String msg,
			String dplinkurl) {
		super();
		this.url = url;
		this.phone = phone;
		this.mail = mail;
		this.msg = msg;
		this.dplinkurl = dplinkurl;
	}
	
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getMail() {
		return mail;
	}
	public void setMail(String mail) {
		this.mail = mail;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public String getDplinkurl() {
		return dplinkurl;
	}
	public void setDplinkurl(String dplinkurl) {
		this.dplinkurl = dplinkurl;
	}
	
	public void init(JSONObject interaction)
	{
		try {
			this.url = interaction.getString("url");
			this.phone = interaction.getString("phone");
			this.mail = interaction.getString("mail");
			this.msg = interaction.getString("msg");
			this.dplinkurl = interaction.getString("dplinkurl");
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
	}
	
	public static GInteraction getInteraction(JSONObject interaction)
	{
		GInteraction gInteraction = new GInteraction();
		gInteraction.init(interaction);
		return gInteraction;
	}
}
