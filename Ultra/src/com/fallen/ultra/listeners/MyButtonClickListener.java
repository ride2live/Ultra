package com.fallen.ultra.listeners;

import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;

import com.fallen.ultra.R;
import com.fallen.ultra.callbacks.UniversalFragmentButtonListener;
import com.fallen.ultra.utils.Params;



public  class MyButtonClickListener implements OnClickListener,  android.widget.CompoundButton.OnCheckedChangeListener{

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
			action  = Params.BUTTON_START_KEY; //+ high or low
			break;
		case R.id.stopInFragment:
			action  = Params.BUTTON_STOP_KEY;
			break;

		default:
			break;
		}
		onButtonClickListener.onButtonClicked(action);
	}
	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		// TODO Auto-generated method stub
		if (isChecked)
		{
			if (buttonView.getId()  == R.id.chooser64)
				onButtonClickListener.onQualityChange(Params.QUALITY_64);
			else if (buttonView.getId()  == R.id.chooser128)
				onButtonClickListener.onQualityChange(Params.QUALITY_128);
		}
	}


}
