package com.fallen.ultra.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.fallen.ultra.R;
import com.fallen.ultra.callbacks.PlayerFragmentCallback;
import com.fallen.ultra.utils.Params;
import com.fallen.ultra.utils.UtilsUltra;

/**
 * Created by Nolwe on 12.04.2015.
 */
public class DialogStreamsFragment extends DialogFragment implements  RadioGroup.OnCheckedChangeListener {
    PlayerFragmentCallback playerFragmentCallback;
    RadioGroup radioStreams;
//    @Nullable
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//
//        return v;
//    }


    int currentQuality = Params.QUALITY_128;
//    @Override
//    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//        UtilsUltra.printLog( "tag " + buttonView.getTag().toString() + " " + isChecked);
//        if (playerFragmentCallback!=null && isChecked) {
//            //sendQualityFromButton(buttonView.getTag(), playerFragmentCallback);
//            UtilsUltra.printLog(buttonView.getTag().toString());
//        }
//
//
//    }

    private void sendQualityFromButton(int id) {

        UtilsUltra.printLog("id came  " + id + "; 64 id = " + R.id.chooser64 + "; 128 id = " + R.id.chooser128 + "; 192 id = " + R.id.chooser192, "ultra", Log.ERROR );
        switch (id)
        {
            case R.id.chooser64:

                currentQuality = Params.QUALITY_64;

                break;
            case R.id.chooser128:
                currentQuality = Params.QUALITY_128;

                break;
            case R.id.chooser192:
                currentQuality = Params.QUALITY_192;

                break;
            default:

                break;

        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        playerFragmentCallback= (PlayerFragmentCallback) getActivity();
        AlertDialog.Builder builderDialog = new AlertDialog.Builder(getActivity());
        UtilsUltra.printLog("onCreateDialog", "", Log.ERROR);
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View v = inflater.inflate(R.layout.fragment_dialog_streams, null, false);
        builderDialog.setView(v);
        builderDialog.setTitle("Выберите стрим");
        builderDialog.setPositiveButton("Ок", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (playerFragmentCallback!=null) {
                    UtilsUltra.printLog("on Ok quality " + currentQuality, "", Log.ERROR);
                    playerFragmentCallback.onQualityChangedPositiveClicked(currentQuality);
                }
            }
        });
        builderDialog.setNegativeButton("Отмена", null);

        // getDialog().setTitle("Выберите стрим");
        //View v = builderDialog.setView(v);
        radioStreams = (RadioGroup) v.findViewById(R.id.radioGroupStreams);
        radioStreams.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId)
                {
                    case R.id.chooser64:

                        currentQuality = Params.QUALITY_64;

                        break;
                    case R.id.chooser128:
                        currentQuality = Params.QUALITY_128;

                        break;
                    case R.id.chooser192:
                        currentQuality = Params.QUALITY_192;

                        break;
                    default:

                        break;

                }
            }
        });
        RadioButton button192 = (RadioButton) v.findViewById(
                R.id.chooser192);
        RadioButton button128 = (RadioButton) v.findViewById(
                R.id.chooser128);
        RadioButton button64 = (RadioButton) v.findViewById(
                R.id.chooser64);
        int qualityKeyFromPrefs = getActivity().getSharedPreferences(
                Params.KEY_PREFERENCES, Activity.MODE_PRIVATE).getInt(
                Params.KEY_PREFERENCES_QUALITY_FIELD, Params.QUALITY_192);
        if (qualityKeyFromPrefs == Params.QUALITY_64) {
            button64.setChecked(true);
            button128.setChecked(false);
            button192.setChecked(false);
        } else if (qualityKeyFromPrefs == Params.QUALITY_128)
        {
            button64.setChecked(false);
            button128.setChecked(true);
            button192.setChecked(false);
        }
        else if (qualityKeyFromPrefs == Params.QUALITY_192)
        {
            button64.setChecked(false);
            button128.setChecked(false);
            button192.setChecked(true);
        }


//        button64.setOnCheckedChangeListener(this);
//        button128.setOnCheckedChangeListener(this);
//        button192.setOnCheckedChangeListener(this);

        return builderDialog.create();
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        sendQualityFromButton(checkedId);
        //UtilsUltra.printLog( "tag " + buttonView.getTag().toString() + " " + isChecked);
    }
}
