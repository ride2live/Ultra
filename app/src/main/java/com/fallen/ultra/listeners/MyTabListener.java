package com.fallen.ultra.listeners;

import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.FragmentTransaction;

public class MyTabListener implements TabListener {
	TabChangeListener tabChangeListener;
	public MyTabListener(TabChangeListener tabChangeListener) {
		// TODO Auto-generated constructor stub
		this.tabChangeListener = tabChangeListener;
	}
	
	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub
		tabChangeListener.pageChanged(tab.getPosition());
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub
		
	}

}
