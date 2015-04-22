package com.fallen.ultra.listeners;

import android.app.Activity;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;

import com.fallen.ultra.R;
import com.fallen.ultra.callbacks.UniversalFragmentButtonListener;
import com.fallen.ultra.utils.Params;


public class MyFragmentButtonClickListener implements OnClickListener {

    private UniversalFragmentButtonListener onButtonClickListener;

    public MyFragmentButtonClickListener(UniversalFragmentButtonListener onButtonClickListener, Activity fragmentActivity) {
        this.onButtonClickListener = onButtonClickListener;
    }

    @Override
    public void onClick(View v) {
        int action = Params.ACTION_WRONG;
        switch (v.getId()) {
            case R.id.startInFragment:
                action = Params.BUTTON_START_KEY; //+ high or low
                break;

            case R.id.sstopInFragment:
                action = Params.BUTTON_STOP_KEY;
                break;
            case R.id.favoritesButtonOff:
                action = Params.BUTTON_FAV_OFF;
                break;
            case R.id.favoritesButtonOn:
                action = Params.BUTTON_FAV_ON;
                break;
            case R.id.listFavButton:
                action = Params.BUTTON_SHOW_LIST_FAV;
                break;
            default:
                break;
        }
        onButtonClickListener.onButtonClicked(action);
    }




}
