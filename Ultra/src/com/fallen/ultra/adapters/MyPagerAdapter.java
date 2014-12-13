package com.fallen.ultra.adapters;

import com.fallen.ultra.fragments.FragmentPlayer;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.view.View;

public class MyPagerAdapter extends FragmentPagerAdapter {

	public MyPagerAdapter(FragmentManager fm) {
		super(fm);
		// TODO Auto-generated constructor stub
	}



	@Override
	public Fragment getItem(int arg0) {
		// TODO Auto-generated method stub
		
		
		return new FragmentPlayer();
	}



	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return 3;//remove hardcode
	}

}
