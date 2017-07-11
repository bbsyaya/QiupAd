package com.guang.client.mode;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class GEventtrack {
	private int event_type;//EventId {SHOW = 1, CLICK = 2, OPEN = 3; DOWNLOAD=4; INSTALL = 5; ACTIVE = 6; }
	
	private List<String> notify_url;

	
	
	public GEventtrack(){}
	public GEventtrack(int event_type, List<String> notify_url) {
		super();
		this.event_type = event_type;
		this.notify_url = notify_url;
	}

	public int getEvent_type() {
		return event_type;
	}

	public void setEvent_type(int event_type) {
		this.event_type = event_type;
	}

	public List<String> getNotify_url() {
		return notify_url;
	}

	public void setNotify_url(List<String> notify_url) {
		this.notify_url = notify_url;
	}
	
	
	public void init(JSONObject eventtrack)
	{
		if(eventtrack == null)
			return;
		try {
			this.event_type= eventtrack.getInt("event_type");
			JSONArray urls =  eventtrack.getJSONArray("notify_url");
			this.notify_url = new ArrayList<String>();
			for(int i=0;i<urls.length();i++)
			{
				this.notify_url.add((String)urls.get(i));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public static GEventtrack getEventtrack(JSONObject eventtrack)
	{
		GEventtrack gEventtrack = new GEventtrack();
		gEventtrack.init(eventtrack);
		return gEventtrack;
	}
	
	public static List<GEventtrack> getEventtracks(JSONArray eventtrack)
	{
		List<GEventtrack> gEventtracks = new ArrayList<GEventtrack>();
		
		if(eventtrack != null)
		{
			for(int i=0;i<eventtrack.length();i++)
			{
				try {
					gEventtracks.add(getEventtrack(eventtrack.getJSONObject(i)));
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
		return gEventtracks;
	}
}
