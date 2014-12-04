package com.fallen.ultra;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.ultra.R;

public class FragmentPlayer extends android.support.v4.app.Fragment implements PlayerControlsListener {
	PlayerFragmentCallback playerFragmentCallback;
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		playerFragmentCallback = (PlayerFragmentCallback) getActivity();
		Button startButton = (Button) getView().findViewById(R.id.startInFragment);
		Button stopButton = (Button) getView().findViewById(R.id.stopInFragment);
		MyButtonClickListener buttonListener = new MyButtonClickListener(this);
		startButton.setOnClickListener(buttonListener);
		stopButton.setOnClickListener(buttonListener);
		super.onActivityCreated(savedInstanceState);
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return inflater.inflate(R.layout.fragment_player, null);
	}
	@Override
	public void onButtonClicked(int button_key_id) {
		// TODO Auto-generated method stub
		Bundle bundle = new Bundle();
		bundle.putInt(Params.KEY_ACTION, button_key_id);
//		switch (button_key_id) {
//		case Params.BUTTON_START_KEY:
//			bundle.
//			break;
//
//		default:
//			break;
//		}
		playerFragmentCallback.buttonClicked(bundle);
	}
}
