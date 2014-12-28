package com.fallen.ultra.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;
import android.util.SparseArray;
import android.view.ViewGroup;

import com.fallen.ultra.fragments.FragmentPlayer;
import com.fallen.ultra.fragments.FragmentTeletype;
import com.fallen.ultra.utils.UtilsUltra;

public class MyPagerAdapter extends FragmentPagerAdapter {

	SparseArray<Fragment> mPageReferenceMap;
	public MyPagerAdapter(FragmentManager fm) {
		super(fm);
		// TODO Auto-generated constructor stub
		mPageReferenceMap = new SparseArray<Fragment>(3);
	}



	@Override
	public Fragment getItem(int arg0) {
		// TODO Auto-generated method stub
		Fragment currentFragment;
		switch (arg0) {
		case 0:
			currentFragment = new FragmentPlayer();
			break;

		case 1:
			currentFragment = new FragmentTeletype();
			break;

		default:
			currentFragment = new Fragment();
			break;
		}
	
	
		
		return currentFragment;
	}
	
	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		// TODO Auto-generated method stub
		
		 
		 Fragment fragment = (Fragment) super.instantiateItem(container, position);
		 UtilsUltra.printLog("put fragment to conatainer " +fragment.getId() + " " + position, "ultra", Log.VERBOSE);
		 mPageReferenceMap.put(position, fragment);
		return fragment;
	}
	public  SparseArray<Fragment> getCurrentFragment() {
		return mPageReferenceMap;
	}

	


	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return 3;//remove hardcode
	}
	
	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		// TODO Auto-generated method stub
		super.destroyItem(container, position, object);
		
	}
	

}
