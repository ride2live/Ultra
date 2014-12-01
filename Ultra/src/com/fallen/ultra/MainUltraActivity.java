package com.fallen.ultra;

import com.example.ultra.R;
import com.fallen.ultra.UltraPlayerService.LocalPlayerBinder;

import android.app.ActionBar;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;

public class MainUltraActivity extends Activity implements TabChangeListener {
	android.app.ActionBar actionTabsBar;
	private ServiceConnection servCon;
	private UltraPlayerService playerService;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_ultra);
		actionTabsBar = getActionBar();
		actionTabsBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		
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
				System.out.println ("onServiceDisconnected");
			}

			@Override
			public void onServiceConnected(ComponentName name, IBinder service) {
				// TODO Auto-generated method stub
				playerService = ((LocalPlayerBinder) service).getService();
				 
				System.out.println ("onServiceConnected");
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
		//playerService.unbindService(servCon);
		System.out.println ("Activity onStop");
		super.onStop();
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		System.out.println ("Activity onDestr");
		super.onDestroy();
	}
	@Override
	public void pageChanged(int position) {
		// TODO Auto-generated method stub

	}
	public void start (View V)
	{
		playerService.playStream();
		this.finish();
	}
	public void stop (View V)
	{
		playerService.stopPlayStream();
	}

}

