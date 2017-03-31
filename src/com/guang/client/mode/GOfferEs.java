package com.guang.client.mode;

import java.util.List;

public class GOfferEs {
	private int time;
	private List<String> url;
	
	public GOfferEs(){}
	public GOfferEs(int time,List<String> url){
		this.time = time;
		this.url = url;
	}
	
	public int getTime() {
		return time;
	}
	public void setTime(int time) {
		this.time = time;
	}
	public List<String> getUrl() {
		return url;
	}
	public void setUrl(List<String> url) {
		this.url = url;
	}
}
