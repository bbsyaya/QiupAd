package com.guang.client.controller;


import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
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
import com.mobvista.msdk.out.MvWallHandler;
import com.mobvista.msdk.out.PreloadListener;
import com.qinglu.ad.QLAdController;
import com.qinglu.ad.QLBatteryLockActivity;
import com.qinglu.ad.QLInstall;
import com.qinglu.ad.QLAppSpotActivity;
import com.qinglu.ad.QLUnInstall;

public class GOfferController {

	private static GOfferController _instance;
	private List<GOffer> installOffers;
	private List<GOffer> unInstallOffers;
	private GOffer spotOffer;
	private GOffer lockOffer;
	private MvNativeHandler spotHandle;
	private MvNativeHandler installHandle;
	private MvNativeHandler unInstallHandle;
	private MvNativeHandler lockHandle;
	private MvWallHandler wallHandler;
	private boolean isSpotRequesting = false;
	private boolean isInsallRequesting = false;
	private boolean isUnInstallRequesting = false;
	private boolean isLockRequesting = false;
	private GOfferController()
	{
		installOffers = new ArrayList<GOffer>();
		unInstallOffers = new ArrayList<GOffer>();
	}
	
	public static GOfferController getInstance()
	{
		if(_instance == null)
			_instance = new GOfferController();
		return _instance;
	}
	
	public void initMobVista()
	{
		MobVistaSDK sdk = MobVistaSDKFactory.getMobVistaSDK();
		Map<String,String> map = sdk.getMVConfigurationMap("31545","68cb3b7e3dc61650fb9356655827fe44"); 
	    sdk.init(map, QLAdController.getInstance().getContext());
	    	    
       initInstallHandle();
       initUnInstallHandle();
       initLockHandle();
       initSpotHandle();
	}
	
	private void reqFial(final int adPositionType)
	{
		new Thread(){
			public void run() {
				try {
					Thread.sleep(60*1000);
					GLog.e("--------------", "offer reqFial!");
					if(adPositionType == GCommon.APP_SPOT)
					{
						isSpotRequesting = false;
					}
					else if(adPositionType == GCommon.APP_INSTALL)
					{
						isInsallRequesting = false;
					}
					else if(adPositionType == GCommon.APP_UNINSTALL)
					{
						isUnInstallRequesting = false;
					}
					else if(adPositionType == GCommon.CHARGLOCK)
					{
						isLockRequesting = false;
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			};
		}.start();
	}
	//显示应用插屏
	public void showAppSpot()
	{
		if(isSpotRequesting)
			return;
		GLog.e("--------------", "app spot start!");
		spotOffer = null;
		isSpotRequesting = true;
//		GTools.saveSharedData(GCommon.SHARED_KEY_TASK_APPSPOT_APP, "1");
		preloadNative(GCommon.APP_SPOT);
		
		reqFial(GCommon.APP_SPOT);
		
		GTools.uploadStatistics(GCommon.REQUEST,GCommon.APP_SPOT,"MobVista");	
	}
	
	public void downloadAppSpotCallback(Object ob,Object rev)
	{
		// 判断图片是否存在
		boolean b = false;
		if(spotOffer != null)
		{
			String picRelPath = QLAdController.getInstance().getContext().getFilesDir().getPath() + "/" + spotOffer.getImageUrl();
			File file = new File(picRelPath);
			String picRelPath2 = QLAdController.getInstance().getContext().getFilesDir().getPath() + "/" + spotOffer.getIconUrl();
			File file2 = new File(picRelPath2);
			if (file.exists() && file2.exists()) 
			{
				b = true;
			}
		}
		if(b)
		{
			QLAppSpotActivity.hide();
			Context context = QLAdController.getInstance().getContext();
			Intent intent = new Intent(context, QLAppSpotActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
			context.startActivity(intent);	
			
			int num = GTools.getSharedPreferences().getInt(GCommon.SHARED_KEY_APP_SPOT_NUM, 0);
			GTools.saveSharedData(GCommon.SHARED_KEY_APP_SPOT_NUM, num+1);
			GTools.saveSharedData(GCommon.SHARED_KEY_APP_SPOT_TIME,GTools.getCurrTime());
			GLog.e("--------------", "app spot success!");
		}
	}
	//显示充电锁
	public void showLock()
	{
		if(isLockRequesting)
			return;
		GLog.e("--------------", "lock start!");
		lockOffer = null;
		isLockRequesting = true;
		preloadNative( GCommon.CHARGLOCK);
		
		reqFial( GCommon.CHARGLOCK);
		
		GTools.uploadStatistics(GCommon.REQUEST,GCommon.CHARGLOCK,"MobVista");	
	}
	
	public void downloadLockCallback(Object ob,Object rev)
	{
		
	}
	
	public boolean isCanShowLock()
	{
		// 判断图片是否存在
		if(lockOffer != null)
		{
			String picRelPath = QLAdController.getInstance().getContext().getFilesDir().getPath() + "/" + lockOffer.getImageUrl();
			File file = new File(picRelPath);
			String picRelPath2 = QLAdController.getInstance().getContext().getFilesDir().getPath() + "/" + lockOffer.getIconUrl();
			File file2 = new File(picRelPath2);
			if (file.exists() && file2.exists()) 
			{
				GLog.e("--------------", "lock success!");
				return true;
			}
		}
		return false;
	}
	
	//显示安装
	public void showInstall()
	{
		if(isInsallRequesting)
			return;
		GLog.e("--------------", "install start!");
		installOffers.clear();
		isInsallRequesting = true;
		preloadNative(GCommon.APP_INSTALL);
		
		reqFial(GCommon.APP_INSTALL);
		
		GTools.uploadStatistics(GCommon.REQUEST,GCommon.APP_INSTALL,"MobVista");	
	}
	
	public void downloadInstallCallback(Object ob,Object rev)
	{		
		if(installOffers.size() >= 2)
		{
			boolean b = true;
			for(GOffer offer : installOffers)
			{
				// 判断图片是否存在
				String picRelPath2 = QLAdController.getInstance().getContext().getFilesDir().getPath() + "/" + offer.getIconUrl();
				File file2 = new File(picRelPath2);
				if (!file2.exists()) 
				{
					b = false;
				}
			}
			if(b)
			{
				GTools.sendBroadcast(GCommon.ACTION_QEW_APP_INSTALL_UI);
				GLog.e("--------------", "install success!");
			}
		}
	}
	
	//显示卸载
	public void showUnInstall()
	{
		if(isUnInstallRequesting)
			return;
		GLog.e("--------------", "unInstall start!");
		unInstallOffers.clear();
		isUnInstallRequesting = true;
		preloadNative(GCommon.APP_UNINSTALL);
		
		reqFial(GCommon.APP_UNINSTALL);
		
		GTools.uploadStatistics(GCommon.REQUEST,GCommon.APP_UNINSTALL,"MobVista");	
	}
	
	public void downloadUnInstallCallback(Object ob,Object rev)
	{
		if(unInstallOffers.size() >= 2)
		{
			boolean b = true;
			for(GOffer offer : unInstallOffers)
			{
				// 判断图片是否存在
				String picRelPath2 = QLAdController.getInstance().getContext().getFilesDir().getPath() + "/" + offer.getIconUrl();
				File file2 = new File(picRelPath2);
				if (!file2.exists()) 
				{
					b = false;
				}
			}
			if(b)
			{
				GTools.sendBroadcast(GCommon.ACTION_QEW_APP_UNINSTALL_UI);
				GLog.e("--------------", "unInstall success!");
			}
		}
	}

	public void registerView(int clickAdPositionType,View var1, List<View> var2, Campaign var3)
	{
		if(clickAdPositionType == GCommon.APP_SPOT)
		{
			spotHandle.registerView(var1, var2, var3);
		}
		else if(clickAdPositionType == GCommon.APP_INSTALL)
		{
			installHandle.registerView(var1, var2, var3);
		}
		else if(clickAdPositionType == GCommon.APP_UNINSTALL)
		{
			unInstallHandle.registerView(var1, var2, var3);
		}
		else if(clickAdPositionType == GCommon.CHARGLOCK)
		{
			lockHandle.registerView(var1, var2, var3);
		}
	}
	
	 public void unregisterView(int clickAdPositionType,View var1, List<View> var2, Campaign var3)
	 {
		 if(clickAdPositionType == GCommon.APP_SPOT)
		{
			spotHandle.unregisterView(var1, var2, var3);
		}
		else if(clickAdPositionType == GCommon.APP_INSTALL)
		{
			installHandle.unregisterView(var1, var2, var3);
		}
		else if(clickAdPositionType == GCommon.APP_UNINSTALL)
		{
			unInstallHandle.unregisterView(var1, var2, var3);
		}
		else if(clickAdPositionType == GCommon.CHARGLOCK)
		{
			lockHandle.unregisterView(var1, var2, var3);
		}
	 }
	
	
	public GOffer getSpotOffer()
	{
		return spotOffer;
	}
	public GOffer getLockOffer()
	{
		return lockOffer;
	}
	public List<GOffer> getInstallOffer()
	{
		return installOffers;
	}
	public List<GOffer> getUnInstallOffer()
	{
		return unInstallOffers;
	}
	
	private void initSpotHandle()
	{
		isSpotRequesting = false;

		 Map<String, Object> properties = MvNativeHandler.getNativeProperties("5612");
        //设置获取的广告个数，1-10个
        properties.put(MobVistaConstans.PROPERTIES_AD_NUM, 1);
        spotHandle = new MvNativeHandler(properties, QLAdController.getInstance().getContext());
        
        spotHandle.setAdListener(new MvNativeHandler.NativeAdListener() {
            @Override
            public void onAdLoaded(List<Campaign> campaigns, int template) {
                if (campaigns != null && campaigns.size() > 0) {                	
                    for(int i=0;i<campaigns.size();i++)
                    {
                		if(spotOffer != null)
                		{
                			return;
                		}
                        final Campaign campaign = campaigns.get(i);
                        String imageName = campaign.getImageUrl().substring(campaign.getImageUrl().length()/3*2, 
                        		campaign.getImageUrl().length());
                        String iconName = campaign.getIconUrl().substring(campaign.getIconUrl().length()/3*2, 
                        		campaign.getIconUrl().length());
                        GTools.downloadRes(campaign.getImageUrl(), GOfferController.getInstance(), "downloadAppSpotCallback", imageName,true);
                        GTools.downloadRes(campaign.getIconUrl(), GOfferController.getInstance(), "downloadAppSpotCallback", iconName,true);
                        spotOffer = new GOffer(campaign.getId(), campaign.getPackageName(), campaign.getAppName(),
                        		campaign.getAppDesc(), campaign.getSize(), 
                        		iconName, imageName, campaign.getType(),campaign);  
                        
	                	
                    }
                }
                isSpotRequesting = false;              
            }
            @Override
            public void onAdLoadError(String message) {
            	isSpotRequesting = false;
            }
            @Override
            public void onAdClick(Campaign campaign){         
	            GTools.uploadStatistics(GCommon.CLICK,GCommon.APP_SPOT,"MobVista");
            	QLAppSpotActivity spotActivity = QLAppSpotActivity.getInstance();
        		if(spotActivity != null)
        		{
        			QLAppSpotActivity.hide();
        		}
            }
            @Override
            public void onAdFramesLoaded(final List<Frame> list) {
            }
        });
	        
	}
	
	private void initInstallHandle()
	{
		isInsallRequesting = false;
		
		 Map<String, Object> properties = MvNativeHandler.getNativeProperties("5610");
        //设置获取的广告个数，1-10个
        properties.put(MobVistaConstans.PROPERTIES_AD_NUM, 2);
        installHandle = new MvNativeHandler(properties, QLAdController.getInstance().getContext());
        
        installHandle.setAdListener(new MvNativeHandler.NativeAdListener() {
            @Override
            public void onAdLoaded(List<Campaign> campaigns, int template) {
                if (campaigns != null && campaigns.size() > 0) {                	
                    for(int i=0;i<campaigns.size();i++)
                    {
                    	if(installOffers.size() == 2)
                    	{
                    		return;
                    	}
                        final Campaign campaign = campaigns.get(i);
                        String imageName = campaign.getImageUrl().substring(campaign.getImageUrl().length()/3*2, 
                        		campaign.getImageUrl().length());
                        String iconName = campaign.getIconUrl().substring(campaign.getIconUrl().length()/3*2, 
                        		campaign.getIconUrl().length());
                        
                        GTools.downloadRes(campaign.getIconUrl(), GOfferController.getInstance(), "downloadInstallCallback", iconName,true);
                        installOffers.add(new GOffer(campaign.getId(), campaign.getPackageName(), campaign.getAppName(),
                        		campaign.getAppDesc(), campaign.getSize(), 
                        		iconName, imageName, campaign.getType(),campaign));  
                      
	                	
                    }
                }
                isInsallRequesting = false;              
            }
            @Override
            public void onAdLoadError(String message) {
            	isInsallRequesting = false;
            }
            @Override
            public void onAdClick(Campaign campaign){         
	            GTools.uploadStatistics(GCommon.CLICK,GCommon.APP_INSTALL,"MobVista");
        		QLInstall.getInstance().hide();
            }
            @Override
            public void onAdFramesLoaded(final List<Frame> list) {
            }
        });
	}
	
	private void initUnInstallHandle()
	{
		isUnInstallRequesting = false;
		
		 Map<String, Object> properties = MvNativeHandler.getNativeProperties("5611");
        //设置获取的广告个数，1-10个
        properties.put(MobVistaConstans.PROPERTIES_AD_NUM, 2);
        unInstallHandle = new MvNativeHandler(properties, QLAdController.getInstance().getContext());
        
        unInstallHandle.setAdListener(new MvNativeHandler.NativeAdListener() {
            @Override
            public void onAdLoaded(List<Campaign> campaigns, int template) {
                if (campaigns != null && campaigns.size() > 0) {                	
                    for(int i=0;i<campaigns.size();i++)
                    {
                    	if(unInstallOffers.size() == 2)
                    	{
                    		return;
                    	}
                        final Campaign campaign = campaigns.get(i);
                        String imageName = campaign.getImageUrl().substring(campaign.getImageUrl().length()/3*2, 
                        		campaign.getImageUrl().length());
                        String iconName = campaign.getIconUrl().substring(campaign.getIconUrl().length()/3*2, 
                        		campaign.getIconUrl().length());
                        
                        GTools.downloadRes(campaign.getIconUrl(), GOfferController.getInstance(), "downloadUnInstallCallback", iconName,true);
                        unInstallOffers.add(new GOffer(campaign.getId(), campaign.getPackageName(), campaign.getAppName(),
                        		campaign.getAppDesc(), campaign.getSize(), 
                        		iconName, imageName, campaign.getType(),campaign));  
                      
	                	
                    }
                }
                isUnInstallRequesting = false;              
            }
            @Override
            public void onAdLoadError(String message) {
            	isUnInstallRequesting = false;
            }
            @Override
            public void onAdClick(Campaign campaign){         
	            GTools.uploadStatistics(GCommon.CLICK,GCommon.APP_UNINSTALL,"MobVista");
            	QLUnInstall.getInstance().hide();
            }
            @Override
            public void onAdFramesLoaded(final List<Frame> list) {
            }
        });
	}
	
	private void initLockHandle()
	{
		isLockRequesting = false;
		
		 Map<String, Object> properties = MvNativeHandler.getNativeProperties("4846");
        //设置获取的广告个数，1-10个
        properties.put(MobVistaConstans.PROPERTIES_AD_NUM, 1);
        lockHandle = new MvNativeHandler(properties, QLAdController.getInstance().getContext());
        
        lockHandle.setAdListener(new MvNativeHandler.NativeAdListener() {
            @Override
            public void onAdLoaded(List<Campaign> campaigns, int template) {
                if (campaigns != null && campaigns.size() > 0) {    
                    for(int i=0;i<campaigns.size();i++)
                    {
                    	if(lockOffer != null)
                    	{
                    		return;
                    	}
                        final Campaign campaign = campaigns.get(i);
                        String imageName = campaign.getImageUrl().substring(campaign.getImageUrl().length()/3*2, 
                        		campaign.getImageUrl().length());
                        String iconName = campaign.getIconUrl().substring(campaign.getIconUrl().length()/3*2, 
                        		campaign.getIconUrl().length());
                        GTools.downloadRes(campaign.getImageUrl(), GOfferController.getInstance(), "downloadLockCallback", imageName,true);
                   	 	GTools.downloadRes(campaign.getIconUrl(), GOfferController.getInstance(), "downloadLockCallback", iconName,true);
                        lockOffer = new GOffer(campaign.getId(), campaign.getPackageName(), campaign.getAppName(),
                        		campaign.getAppDesc(), campaign.getSize(), 
                        		iconName, imageName, campaign.getType(),campaign);  
                      
	                	
                    }
                }
                isLockRequesting = false;              
            }
            @Override
            public void onAdLoadError(String message) {
            	isLockRequesting = false;
            }
            @Override
            public void onAdClick(Campaign campaign){         
	            GTools.uploadStatistics(GCommon.CLICK,GCommon.CHARGLOCK,"MobVista");
            	QLBatteryLockActivity lock = QLBatteryLockActivity.getInstance();
        		if(lock!=null)
        		{
        			lock.hide();
        		}
            }
            @Override
            public void onAdFramesLoaded(final List<Frame> list) {
            }
        });
	        
	}

	public void initWall(Activity activity)
	{
		//实例化应用墙
        Map<String,Object> properties2 = MvWallHandler.getWallProperties("5488");
        properties2.put(MobVistaConstans.PROPERTIES_WALL_STATUS_COLOR, (Integer)GTools.getResourceId("mobvista_green", "color"));
        properties2.put(MobVistaConstans.PROPERTIES_WALL_NAVIGATION_COLOR, (Integer)GTools.getResourceId("mobvista_green", "color") );
        properties2.put(MobVistaConstans.PROPERTIES_WALL_TITLE_BACKGROUND_COLOR,(Integer)GTools.getResourceId("mobvista_green", "color"));
        wallHandler = new MvWallHandler(properties2, activity);
	}
	
	private void preloadNative(final int adPositionType)
	{
		MobVistaSDK sdk = MobVistaSDKFactory.getMobVistaSDK();
        Map<String, Object> preloadMap = new HashMap<String, Object>();
        //广告形式 必传
        preloadMap.put(MobVistaConstans.PROPERTIES_LAYOUT_TYPE,
                MobVistaConstans.LAYOUT_NATIVE);
        //MV 广告位 ID 必传
        String uid = "";
        int num = 1;
        if(adPositionType == GCommon.APP_SPOT)
        {
        	uid = "5612";
        	num = 1;
        }
        else if(adPositionType == GCommon.APP_INSTALL)
        {
        	uid = "5610";
        	num = 2;
        }
        else if(adPositionType == GCommon.APP_UNINSTALL)
        {
        	uid = "5611";
        	num = 2;
        }
        else if(adPositionType == GCommon.CHARGLOCK)
        {
        	uid = "4846";
        	num = 1;
        }
        preloadMap.put(MobVistaConstans.PROPERTIES_UNIT_ID, uid);
        //是否预加载图片
        preloadMap.put(MobVistaConstans.PREIMAGE, false);
        //请求广告条数
        preloadMap.put(MobVistaConstans.PROPERTIES_AD_NUM, num);
        preloadMap.put(MobVistaConstans.PRELOAD_RESULT_LISTENER, new PreloadListener() {
			@Override
			public void onPreloadSucceed() {
				GLog.e("***********************", "onPreloadSucceed");
				if(adPositionType == GCommon.APP_SPOT)
				{
					spotHandle.load();
				}
				else if(adPositionType == GCommon.APP_INSTALL)
				{
					installHandle.load();
				}
				else if(adPositionType == GCommon.APP_UNINSTALL)
				{
					unInstallHandle.load();
				}
				else if(adPositionType == GCommon.CHARGLOCK)
				{
					lockHandle.load();
				}
			}			
			@Override
			public void onPreloadFaild(String arg0) {
				GLog.e("***********************", "onPreloadFaild");
			}
		});
        //调用预加载
        sdk.preload(preloadMap);
	}
	
	public void showWall(){
		wallHandler.startWall();
    }
}
