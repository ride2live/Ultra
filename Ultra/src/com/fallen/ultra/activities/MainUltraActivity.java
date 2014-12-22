package com.fallen.ultra.activities;

import android.app.ActionBar;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.SparseArray;

import com.fallen.ultra.R;
import com.fallen.ultra.adapters.MyPagerAdapter;
import com.fallen.ultra.callbacks.ActivityToFragmentListener;
import com.fallen.ultra.callbacks.Observer;
import com.fallen.ultra.callbacks.PlayerFragmentCallback;
import com.fallen.ultra.callbacks.ServiceToActivityCallback;
import com.fallen.ultra.creators.StatusObject;
import com.fallen.ultra.creators.TabsCreator;
import com.fallen.ultra.fragments.FragmentPlayer;
import com.fallen.ultra.listeners.TabChangeListener;
import com.fallen.ultra.services.UltraPlayerService;
import com.fallen.ultra.services.UltraPlayerService.LocalPlayerBinder;
import com.fallen.ultra.utils.Params;
import com.fallen.ultra.utils.UtilsUltra;

public class MainUltraActivity extends FragmentActivity implements
		TabChangeListener, PlayerFragmentCallback, ServiceToActivityCallback, Observer {
	android.app.ActionBar actionTabsBar;
	private ServiceConnection servCon;
	private UltraPlayerService playerService;
	private String currentArtist;
	private String currentTrack;
	ViewPager pager;
	private boolean isServiceBinded = false;
	private ActivityToFragmentListener mFragmentCallback;
	private String currentStringStatus;
	private int currentQualityKey;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_ultra);
		actionTabsBar = getActionBar();
		actionTabsBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		pager = (ViewPager) findViewById(R.id.pager);
		pager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
		TabsCreator.buildActionBar(actionTabsBar, this);
		SharedPreferences myPreferences = getSharedPreferences(Params.KEY_PREFERENCES_QUALITY, MODE_PRIVATE);
		UtilsUltra.getQualityFromPreferences(myPreferences);
		
		Intent intent = new Intent(getApplicationContext(), SplashActivity.class);
		startActivity(intent);
	
	}

	@Override
	protected void onResume() {
	//	if (servCon == null)
		//	settingUpServiceConnection();
		
		Intent intent = UtilsUltra.createServiceIntentFromActivity(getApplicationContext(), Params.FLAG_BIND_ACTIVITY);
		if (servCon == null)
			settingUpServiceConnection();
		bindService(intent, servCon, Context.BIND_AUTO_CREATE);

		super.onResume();
	}

	private void settingUpServiceConnection() {
		// TODO Auto-generated method stub
		servCon = new ServiceConnection() {

			@Override
			public void onServiceDisconnected(ComponentName name) {
				// TODO Auto-generated method stub
				System.out.println("onServiceDisconnected");
				isServiceBinded = false;
				if (playerService !=null)
					playerService.setCallback(null);
				else
					UtilsUltra.printLog("player service is null in activity", "Ultra Activity", Log.ERROR);
				servCon = null;

			}

			@Override
			public void onServiceConnected(ComponentName name, IBinder service) {
				// TODO Auto-generated method stub
				playerService = ((LocalPlayerBinder) service).getService();
				isServiceBinded = true;
				playerService.setCallback(MainUltraActivity.this);
				
				System.out.println("onServiceConnected");
			}
		};
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		
		if (isServiceBinded && servCon!=null)
		{
			
			unbindService(servCon);
			UtilsUltra.printLog("Activity onPause, unbind service", "Ultra Activity", Log.VERBOSE);
		}
		else
			UtilsUltra.printLog("cant unbind service from activity", "Ultra Activity", Log.ERROR);
		
		
		super.onPause();
	}

	@Override
	protected void onStop() {
		System.out.println("Activity onStop");
		UtilsUltra.putQualityToSharedPrefs(getSharedPreferences(Params.KEY_PREFERENCES_QUALITY, MODE_PRIVATE),currentQualityKey);
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		System.out.println("Activity onDestr");
		super.onDestroy();
	}

	@Override
	public void pageChanged(int position) {
		// TODO Auto-generated method stub

	}

	
	public void start() {
		Intent intent = UtilsUltra.createServiceIntentFromActivity(getApplicationContext(), Params.FLAG_PLAY, currentQualityKey);
		startService(intent);
	}

	public void stop() {
		Intent intent = UtilsUltra.createServiceIntentFromActivity(getApplicationContext(), Params.FLAG_STOP);
		startService(intent);
	}

	//Only button click handle 
	//if need addition info use another callback
	@Override
	public void buttonClicked(int action) {
		// TODO Auto-generated method stub

			sendToService(action);
	}

	private void sendToService(int action) {
		// TODO Auto-generated method stub
		switch (action) {
			case Params.BUTTON_START_KEY:
				start();
				break;
			case Params.BUTTON_STOP_KEY:
				stop();
				break;
			default:
				Log.e("ultraerr", "MainUltraActivity wrong id");
				break;
			}

		//}
	}

	@Override
	public void onUnbindService() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void newTitleRetrieved(String artist, String title) {
		// TODO Auto-generated method stub
		currentArtist = artist;
		currentTrack = title;
		Log.e("MainUltraActivity newTitleRetrieved", "UltraLog");
		updateFragmentWithTitles();
		
	}

	private void updateFragmentWithTitles() {
		// TODO Auto-generated method stub
		if (mFragmentCallback!=null)
			mFragmentCallback.onTitleChanged(currentArtist, currentTrack);
	}

	@Override
	public void onInit() {
		int playerFragmentPosition = 0;
		MyPagerAdapter myAdapter = (MyPagerAdapter)pager.getAdapter();
		SparseArray<Fragment> spa = myAdapter.getCurrentFragment();
		Fragment f = spa.get(playerFragmentPosition);
		FragmentPlayer myFragment = (FragmentPlayer)f; 
		mFragmentCallback = myFragment;
		if (playerService!=null && isServiceBinded)//looks like we rebind
			updateAllFramentFields();
		// TODO Auto-generated method stub
		
	}

	private void updateAllFramentFields() {
		// TODO Auto-generated method stub
		if (mFragmentCallback!=null)
			mFragmentCallback.updateAll (currentArtist, currentTrack, currentStringStatus);
	}

	@Override
	public void onFragmentCreatedNoInit() {
		// TODO Auto-generated method stub
		mFragmentCallback = null;
	}

	@Override
	public void onStatusChanged(int status) {
		// TODO Auto-generated method stub
		if (mFragmentCallback !=null)
		{
			currentStringStatus = UtilsUltra.getStatusDescription(getResources(), status);
			mFragmentCallback.onStatusChanged(currentStringStatus);
		}

		
	}

	@Override
	public void setQuality(int qualityKey) {
		
		currentQualityKey = qualityKey;
/*		switch (qualityKey) {
		case Params.QUALITY_128:
			qualityURL = Params.ULTRA_URL_HIGH;
			break;
		case Params.QUALITY_64:
			qualityURL = Params.ULTRA_URL_LOW;
			break;		
		default:
			qualityURL = Params.ULTRA_URL_HIGH;
			break;
		}*/
		
		
	
	}

	@Override
	public void update(StatusObject sObject) {
		// TODO Auto-generated method stub
		//mFragmentCallback.onStatusChanged();
		if (mFragmentCallback!=null)
			mFragmentCallback.onStatusChanged(sObject);
	}






}
