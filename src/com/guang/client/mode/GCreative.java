package com.guang.client.mode;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GCreative {
	private String adid;//广告唯一id
	
	//InteractionType {ANY=0; NO_INTERACTION = 1; BROWSE = 2; DOWNLOAD = 3;DIALING = 4;MESSAGE = 5; MAIL = 6; ; deeplink = 11;}
	private int interaction_type;
	private GInteraction interaction;
	private int adm_type;//AdmType {PIC = 0; MRAID = 1; HTML = 2; NATIVE = 3; icontext=4}
	private GAdm adm;
	private List<GEventtrack> eventtrack;
	
	
	public GCreative(){}
	public GCreative(String adid, int interaction_type,
			GInteraction interaction, int adm_type, GAdm adm,
			List<GEventtrack> eventtrack) {
		super();
		this.adid = adid;
		this.interaction_type = interaction_type;
		this.interaction = interaction;
		this.adm_type = adm_type;
		this.adm = adm;
		this.eventtrack = eventtrack;
	}
	public String getAdid() {
		return adid;
	}
	public void setAdid(String adid) {
		this.adid = adid;
	}
	public int getInteraction_type() {
		return interaction_type;
	}
	public void setInteraction_type(int interaction_type) {
		this.interaction_type = interaction_type;
	}
	public GInteraction getInteraction() {
		return interaction;
	}
	public void setInteraction(GInteraction interaction) {
		this.interaction = interaction;
	}
	public int getAdm_type() {
		return adm_type;
	}
	public void setAdm_type(int adm_type) {
		this.adm_type = adm_type;
	}
	public GAdm getAdm() {
		return adm;
	}
	public void setAdm(GAdm adm) {
		this.adm = adm;
	}
	public List<GEventtrack> getEventtrack() {
		return eventtrack;
	}
	public void setEventtrack(List<GEventtrack> eventtrack) {
		this.eventtrack = eventtrack;
	}
	
	public void init(JSONObject creative)
	{
		if(creative == null)
			return;
		try {
			this.adid = creative.getString("adid");
			this.interaction_type = creative.getInt("interaction_type");
			this.interaction = GInteraction.getInteraction(creative.getJSONObject("interaction"));
			this.adm_type = creative.getInt("adm_type");
			this.adm = GAdm.getAdm(creative.getJSONObject("adm"));
			this.eventtrack = GEventtrack.getEventtracks(creative.getJSONArray("eventtrack"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public static GCreative getCreative(JSONObject creative)
	{
		GCreative gCreative = new GCreative();
		gCreative.init(creative);
		return gCreative;
	}
	
	public static List<GCreative> getCreatives(JSONArray creatives)
	{
		List<GCreative> gCreatives = new ArrayList<GCreative>();
		if(creatives != null)
		{
			for(int i=0;i<creatives.length();i++)
			{
				try {
					gCreatives.add(getCreative(creatives.getJSONObject(i)));
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
		return gCreatives;
	}
}
