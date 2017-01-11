package com.guang.client.controller;


import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.guang.client.GCommon;
import com.guang.client.mode.GOffer;
import com.guang.client.tools.GLog;
import com.guang.client.tools.GTools;
import com.mobvista.msdk.MobVistaConstans;
import com.mobvista.msdk.MobVistaSDK;
import com.mobvista.msdk.out.Campaign;
import com.mobvista.msdk.out.Frame;
import com.mobvista.msdk.out.MobVistaSDKFactory;
import com.mobvista.msdk.out.MvNativeHandler;
import com.mobvista.msdk.out.PreloadListener;
import com.qinglu.ad.QLAdController;
import com.qinglu.ad.QLBatteryLockActivity;
import com.qinglu.ad.QLInstall;
import com.qinglu.ad.QLSpotActivity;
import com.qinglu.ad.QLUnInstall;

public class GOfferController {

	private static GOfferController _instance;
	private List<GOffer> offers;
	private MvNativeHandler nativeHandle;
	private int adPositionType;
	private int clickAdPositionType;
	private boolean isRequesting = false;
	private GOfferController()
	{
		offers = new ArrayList<GOffer>();
	}
	
	public static GOfferController getInstance()
	{
		if(_instance == null)
			_instance = new GOfferController();
		return _instance;
	}
	
	public void initMobVista()
	{
		isRequesting = false;
		MobVistaSDK sdk = MobVistaSDKFactory.getMobVistaSDK();
		Map<String,String> map = sdk.getMVConfigurationMap("31545","68cb3b7e3dc61650fb9356655827fe44"); 
	    sdk.init(map, QLAdController.getInstance().getContext());
	         
	    preloadNative();
	    
        Map<String, Object> properties = MvNativeHandler.getNativeProperties("4846");
        //设置获取的广告个数，1-10个
        properties.put(MobVistaConstans.PROPERTIES_AD_NUM, 2);
        nativeHandle = new MvNativeHandler(properties, QLAdController.getInstance().getContext());
        
        nativeHandle.setAdListener(new MvNativeHandler.NativeAdListener() {
            @Override
            public void onAdLoaded(List<Campaign> campaigns, int template) {
                if (campaigns != null && campaigns.size() > 0) {                	
                    for(int i=0;i<campaigns.size();i++)
                    {
                        final Campaign campaign = campaigns.get(i);
                        String imageName = campaign.getImageUrl().substring(campaign.getImageUrl().length()/3*2, 
                        		campaign.getImageUrl().length());
                        String iconName = campaign.getIconUrl().substring(campaign.getIconUrl().length()/3*2, 
                        		campaign.getIconUrl().length());
                        GTools.downloadRes(campaign.getImageUrl(), null, null, imageName,false);
                        GTools.downloadRes(campaign.getIconUrl(), null, null, iconName,false);
                       
                        offers.add(new GOffer(campaign.getId(), campaign.getPackageName(), campaign.getAppName(),
                        		campaign.getAppDesc(), campaign.getSize(), 
                        		iconName, imageName, campaign.getType(),campaign));  
                      
//                		GTools.uploadStatistics(GCommon.REQUEST,adPositionType,campaign.getId());	
                    }
                }
                isRequesting = false;              
            }
            @Override
            public void onAdLoadError(String message) {
            	isRequesting = false;
            	GLog.e("************************", "onAdLoadError");
            }
            @Override
            public void onAdClick(Campaign campaign){         
//            	GTools.uploadStatistics(GCommon.CLICK,clickAdPositionType,campaign.getId());
            	GOfferController.getInstance().deleteOfferById(campaign.getId());
            	if(GCommon.CHARGLOCK == clickAdPositionType)
            	{
            		GTools.saveSharedData(GCommon.SHARED_KEY_LOCK_SAVE_TIME, GTools.getCurrTime());
            		QLBatteryLockActivity lock = QLBatteryLockActivity.getInstance();
            		if(lock!=null)
            		{
            			lock.hide();
            		}
            	}
            	else if(GCommon.APP_INSTALL == clickAdPositionType)
            	{
            		GTools.saveSharedData(GCommon.SHARED_KEY_LOCK_SAVE_TIME, GTools.getCurrTime());
            		QLInstall.getInstance().hide();
            	}
            	else if(GCommon.APP_UNINSTALL == clickAdPositionType)
            	{
            		GTools.saveSharedData(GCommon.SHARED_KEY_LOCK_SAVE_TIME, GTools.getCurrTime());
            		QLUnInstall.getInstance().hide();
            	}
            	else if(GCommon.OPENSPOT == clickAdPositionType)
            	{
            		GTools.saveSharedData(GCommon.SHARED_KEY_OPEN_SPOT_TIME, GTools.getCurrTime());
            		QLSpotActivity spotActivity = QLSpotActivity.getInstance();
            		if(spotActivity != null)
            		{
            			spotActivity.hide();
            		}
            	}
            }
            @Override
            public void onAdFramesLoaded(final List<Frame> list) {
            }
        });
        nativeHandle.setTrackingListener(new MvNativeHandler.NativeTrackingListener() {
            @Override
            public void onStartRedirection(Campaign campaign, String url) {}
            @Override
            public void onRedirectionFailed(Campaign campaign, String url) {
            }
            @Override
            public void onFinishRedirection(Campaign campaign, String url) {}
            @Override
            public void onDownloadStart(Campaign campaign) {}
            @Override
            public void onDownloadFinish(Campaign campaign) {}
            @Override
            public void onDownloadProgress(int progress) {}
            @Override
            public boolean onInterceptDefaultLoadingDialog() {
                return false;
            }
            @Override
            public void onShowLoading(Campaign campaign) {}
            @Override
            public void onDismissLoading(Campaign campaign) {}
        });
	}
	
	private void preloadNative()
	{
		MobVistaSDK sdk = MobVistaSDKFactory.getMobVistaSDK();
        Map<String, Object> preloadMap = new HashMap<String, Object>();
        //广告形式 必传
        preloadMap.put(MobVistaConstans.PROPERTIES_LAYOUT_TYPE,
                MobVistaConstans.LAYOUT_NATIVE);
        //MV 广告位 ID 必传
        preloadMap.put(MobVistaConstans.PROPERTIES_UNIT_ID, "4846");
        //是否预加载图片
        preloadMap.put(MobVistaConstans.PREIMAGE, false);
        //请求广告条数
        preloadMap.put(MobVistaConstans.PROPERTIES_AD_NUM, 2);
//        preloadMap.put(MobVistaConstans.PRELOAD_RESULT_LISTENER, new PreloadListener() {
//			@Override
//			public void onPreloadSucceed() {
//				GLog.e("***********************", "onPreloadSucceed");
////				nativeHandle.load();
//			}			
//			@Override
//			public void onPreloadFaild(String arg0) {
//				GLog.e("***********************", "onPreloadFaild");
//			}
//		});
        //调用预加载
        sdk.preload(preloadMap);
	}

	public void getRandOffer(int adPositionType)
	{
		if(isRequesting || offers.size() > 0)
			return;
		this.adPositionType = adPositionType;
		isRequesting = true;
		GLog.e("***********************", "isRequesting="+isRequesting);
		nativeHandle.load();
//		preloadNative();
//		new Thread(){
//			public void run() {
//				try {
//					Thread.sleep(20*1000);
//					nativeHandle.load();
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//			};
//		}.start();
		new Thread(){
			public void run() {
				try {
					Thread.sleep(30*1000);
					isRequesting = false;
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			};
		}.start();
	}
	
	public void registerView(int clickAdPositionType,View var1, List<View> var2, Campaign var3)
	{
		this.clickAdPositionType = clickAdPositionType;
		nativeHandle.registerView(var1, var2, var3);
	}
	
	 public void unregisterView(View var1, List<View> var2, Campaign var3)
	 {
		 nativeHandle.unregisterView(var1, var2, var3);
	 }
	
	public boolean isDownloadResSuccess()
	{
		for(GOffer offer : offers)
		{
			// 判断图片是否存在
			String picRelPath = QLAdController.getInstance().getContext().getFilesDir().getPath() + "/" + offer.getImageUrl();
			File file = new File(picRelPath);
			String picRelPath2 = QLAdController.getInstance().getContext().getFilesDir().getPath() + "/" + offer.getIconUrl();
			File file2 = new File(picRelPath2);
			if (file.exists() && file2.exists()) 
			{
				return true;
			}
		}
		return false;
	}
	
	public boolean isGetRandOffer()
	{
		long time = GTools.getSharedPreferences().getLong(GCommon.SHARED_KEY_OFFER_SAVE_TIME, 0l);
		long now_time = GTools.getCurrTime();
		if(isDownloadResSuccess()  && (time == 0 || now_time-time > 1000*60*60*8))
		{
			GTools.saveSharedData(GCommon.SHARED_KEY_OFFER_SAVE_TIME, now_time);
			return true;
		}
		return false;
	}
	public GOffer getOffer()
	{
		for(GOffer offer : offers)
		{
			// 判断图片是否存在
			String picRelPath = QLAdController.getInstance().getContext().getFilesDir().getPath() + "/" + offer.getImageUrl();
			File file = new File(picRelPath);
			String picRelPath2 = QLAdController.getInstance().getContext().getFilesDir().getPath() + "/" + offer.getIconUrl();
			File file2 = new File(picRelPath2);
			if (file.exists() && file2.exists()) 
			{
				return offer;
			}
		}
		return null;
	}
	public List<GOffer> getOffers()
	{
		return offers;
	}
	//根据id获取offer
	public GOffer getOfferById(String id)
	{
		for(GOffer offer : offers)
		{
			if(offer.getId().equals(id))
			{
				return offer;
			}
		}
		return null;
	}
	//根据id删除offer
	public void deleteOfferById(String id)
	{
		for(GOffer offer : offers)
		{
			if(offer.getId().equals(id))
			{
				offers.remove(offer);
				break;
			}
		}
	}
	//显示插屏
	public void showSpot()
	{
		Context context = QLAdController.getInstance().getContext();
		Intent intent = new Intent(context, QLSpotActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
		context.startActivity(intent);	
	}
}
