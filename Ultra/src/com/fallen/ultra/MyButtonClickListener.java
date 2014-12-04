package com.fallen.ultra;

import com.example.ultra.R;

import android.view.View;
import android.view.View.OnClickListener;



public  class MyButtonClickListener implements OnClickListener{

	private PlayerControlsListener onButtonClickListener;
	public MyButtonClickListener(PlayerControlsListener onButtonClickListener) {
		// TODO Auto-generated constructor stub
		this.onButtonClickListener = onButtonClickListener;
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int button_key_id = -1;
		switch (v.getId()) {
		case R.id.startInFragment:
			button_key_id  = Params.BUTTON_START_KEY;
			break;
		case R.id.stopInFragment:
			button_key_id  = Params.BUTTON_STOP_KEY;
			break;

		default:
			break;
		}
		onButtonClickListener.onButtonClicked(button_key_id);
	}

}
