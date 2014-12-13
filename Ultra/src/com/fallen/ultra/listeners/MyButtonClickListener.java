package com.fallen.ultra.listeners;

import com.fallen.ultra.R;
import com.fallen.ultra.utils.Params;

import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;



public  class MyButtonClickListener implements OnClickListener{

	private UniversalFragmentButtonListener onButtonClickListener;
	public MyButtonClickListener(UniversalFragmentButtonListener onButtonClickListener, FragmentActivity fragmentActivity) {
		// TODO Auto-generated constructor stub
		this.onButtonClickListener = onButtonClickListener;
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int action = Params.ACTION_WRONG;
		switch (v.getId()) {
		case R.id.startInFragment:
			action  = Params.BUTTON_START_KEY;
			break;
		case R.id.stopInFragment:
			action  = Params.BUTTON_STOP_KEY;
			break;

		default:
			break;
		}
		onButtonClickListener.onButtonClicked(action);
	}

}
