package com.fallen.ultra.fragments;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;

import com.fallen.ultra.R;


public class FragmentTeletype extends Fragment {

	Button updateTeletype;
	WebView webTeletype;
	String urlTeletype = "http://www.radioultra.ru/ultra-teletype";
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return LayoutInflater.from(getActivity()).inflate(R.layout.fragment_teletype, null);
	}
	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		// TODO Auto-generated method stub
		if (isVisibleToUser)
		{
			System.out.println("fragment becomes visible");
		}
		super.setUserVisibleHint(isVisibleToUser);
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		View root = getView();
		updateTeletype = (Button) root.findViewById(R.id.updateTeletypeButton);
		updateTeletype.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				webTeletype.loadUrl(urlTeletype);
			}
		});
		webTeletype = (WebView) root.findViewById(R.id.webTeletype);
		webTeletype.setInitialScale(1);
		webTeletype.getSettings().setJavaScriptEnabled(true);
		webTeletype.getSettings().setLoadWithOverviewMode(true);
		webTeletype.getSettings().setUseWideViewPort(true);
		super.onActivityCreated(savedInstanceState);
	}
}
