package com.fallen.ultra;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;

import com.example.ultra.R;
import com.fallen.ultra.UltraPlayerService.LocalPlayerBinder;

public class MainUltraActivity extends FragmentActivity implements
		TabChangeListener, PlayerFragmentCallback, ServiceCallback {
	android.app.ActionBar actionTabsBar;
	private ServiceConnection servCon;
	private UltraPlayerService playerService;
	ViewPager pager;
	private boolean isServiceBinded = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_ultra);
		actionTabsBar = getActionBar();
		actionTabsBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		pager = (ViewPager) findViewById(R.id.pager);
		pager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
		TabsCreator.buildActionBar(actionTabsBar, this);

	}

	@Override
	protected void onResume() {
		if (servCon == null)
			settingUpServiceConnection();

		Intent intent = new Intent(MainUltraActivity.this,
				UltraPlayerService.class);
		bindService(intent, servCon, Context.BIND_AUTO_CREATE);
		startService(intent);

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
				playerService.setCallback(null);

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
		unbindService(servCon);
		super.onPause();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		// playerService.unbindService(servCon);
		System.out.println("Activity onStop");
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

	//better rewrite and use intent
	public void start() {
		
		//playerService.playStream();
		// this.finish();
	}

	public void stop() {
		playerService.stopStream();
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
		if (playerService == null || servCon == null || !isServiceBinded) {
			Log.e("ultraerr", "MainActivity cantfind service");
		} 
		else 
		{
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

		}
	}

	@Override
	public void unbindService() {
		// TODO Auto-generated method stub
		if (isServiceBinded)
			playerService.unbindService(servCon);
	}

}
