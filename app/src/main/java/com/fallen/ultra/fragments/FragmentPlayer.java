package com.fallen.ultra.fragments;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.fallen.ultra.R;
import com.fallen.ultra.activities.MainUltraActivity;
import com.fallen.ultra.callbacks.ActivityToFragmentListener;
import com.fallen.ultra.callbacks.PlayerFragmentCallback;
import com.fallen.ultra.callbacks.UniversalFragmentButtonListener;
import com.fallen.ultra.creators.StatusObject;
import com.fallen.ultra.listeners.MyFragmentButtonClickListener;
import com.fallen.ultra.utils.Params;
import com.fallen.ultra.utils.UtilsUltra;

import java.io.File;

public class FragmentPlayer extends android.support.v4.app.Fragment implements
        UniversalFragmentButtonListener, ActivityToFragmentListener {
    PlayerFragmentCallback playerFragmentCallback;
    TextView artistView, trackView, statusView;
    int current_quality_key = Params.QUALITY_DEFAULT_KEY;



    Context context;
    Button startButton, sstopButton;
    ImageButton starOnButton, starOffButton;
    ProgressBar progress;
    ImageView artImage;
    private String currentTitle = Params.NO_TITLE;
    private String currentArtist = Params.NO_TITLE;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((MainUltraActivity) getActivity()).setCallback(this);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        context = getActivity();
        starOnButton = (ImageButton) getView().findViewById(R.id.favoritesButtonOn);
        starOffButton = (ImageButton) getView().findViewById(R.id.favoritesButtonOff);
        playerFragmentCallback = (PlayerFragmentCallback) getActivity();
        progress = (ProgressBar) getView().findViewById(R.id.progressFragment);
        artImage = (ImageView) getView().findViewById(R.id.imageViewArt);
        artistView = (TextView) getView().findViewById(R.id.artistTextFragment);
        trackView = (TextView) getView().findViewById(R.id.trackTextFragment);
        statusView = (TextView) getView().findViewById(R.id.statusInfoFragment);
        startButton = (Button) getView().findViewById(R.id.startInFragment);
        sstopButton = (Button) getView().findViewById(R.id.sstopInFragment);
        RadioButton rbutton128 = (RadioButton) getView().findViewById(
                R.id.chooser128);
        RadioButton rbutton64 = (RadioButton) getView().findViewById(
                R.id.chooser64);
        MyFragmentButtonClickListener buttonListener = new MyFragmentButtonClickListener(this,
                getActivity());
        starOnButton.setOnClickListener(buttonListener);
        starOffButton.setOnClickListener(buttonListener);
        startButton.setOnClickListener(buttonListener);
        sstopButton.setOnClickListener(buttonListener);
        int qualityKeyFromPrefs = getActivity().getSharedPreferences(
                Params.KEY_PREFERENCES_QUALITY, Activity.MODE_PRIVATE).getInt(
                Params.KEY_PREFERENCES_QUALITY_FIELD, Params.QUALITY_128);
        current_quality_key = qualityKeyFromPrefs;

        playerFragmentCallback.setQuality(qualityKeyFromPrefs);
        if (qualityKeyFromPrefs == Params.QUALITY_64) {
            rbutton64.setChecked(true);
            rbutton128.setChecked(false);
        } else {
            rbutton64.setChecked(false);
            rbutton128.setChecked(true);
        }
        rbutton64.setOnCheckedChangeListener(buttonListener);
        rbutton128.setOnCheckedChangeListener(buttonListener);


        playerFragmentCallback.onInit();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        return inflater.inflate(R.layout.fragment_player, container, false);
    }

    @Override
    public void onButtonClicked(int actionFromButton) {
//        switch (actionFromButton) {
//            case Params.BUTTON_FAV_OFF:
//                switchFav(false);
//                break;
//            case Params.BUTTON_FAV_ON:
//                switchFav(true);
//                break;
//            default:
//
//                break;
//        }
        playerFragmentCallback.buttonClicked(actionFromButton);


    }
    @Override
    public void onFavoriteDefine(boolean isAlreadyInFav) {
            setFavoritesButtonState (isAlreadyInFav);


    }

    private void setFavoritesButtonState(boolean isAlreadyInFav) {

        if (isAlreadyInFav) {
                starOnButton.setVisibility(View.VISIBLE);
                starOffButton.setVisibility(View.GONE);
            } else {
                starOnButton.setVisibility(View.GONE);
                starOffButton.setVisibility(View.VISIBLE);
            }
    }

    private void switchFav(boolean isFav) {
//        if (!currentArtist.equals(Params.NO_TITLE) && !currentTitle.equals(Params.NO_TITLE)) {
//            if (isFav) {
//                starOnButton.setVisibility(View.GONE);
//                starOffButton.setVisibility(View.VISIBLE);
//            } else {
//                starOnButton.setVisibility(View.VISIBLE);
//                starOffButton.setVisibility(View.GONE);
//            }
//        } else if (context != null) {
//            Toast.makeText(context, "Исполнитель и трек не загружены", Toast.LENGTH_SHORT).show();
//        }
    }

    public void setCurrentTrack(String artist, String title) {
        // TODO Auto-generated method stub
        starOnButton.setVisibility(View.GONE);
        starOffButton.setVisibility(View.GONE);
        currentTitle = title;
        currentArtist = artist;
        this.artistView.setText(artist);
        this.trackView.setText(title);
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        playerFragmentCallback.onFragmentCreatedNoInit();
        super.onDestroy();
    }

    @Override
    public void onTitleChanged(String artist, String title) {
        // TODO Auto-generated method stub
        setCurrentTrack(artist, title);
        starOnButton.setVisibility(View.GONE);
        starOffButton.setVisibility(View.GONE);

    }

    @Override
    public void onStatusChanged(String status) {
        // TODO Auto-generated method stub
        setCurrentStatus(status);

    }

    private void setCurrentStatus(String currentStatus) {
        // TODO Auto-generated method stub
        statusView.setText(currentStatus);
    }

    @Override
    public void onQualityChange(int quality) {
        // TODO Auto-generated method stub
        playerFragmentCallback.setQuality(quality);
    }

    @Override
    public void updateAll(String currentArtist, String currentTrack,
                          String currentStatus) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onStatusChanged(StatusObject stausObj) {
        // TODO Auto-generated method stub
        int asyncStatus = stausObj.getAsyncStatus();
        int mediaStatus = stausObj.getPlayerStatus();
        if (asyncStatus != Params.STATUS_NONE) {
            switch (asyncStatus) {
                case Params.STATUS_NEW_TITLE:
                    setCurrentTrack(stausObj.getArtist(), stausObj.getTrack());
                    break;
                case Params.STATUS_ERROR:

                    setCurrentStatus(stausObj.getError());
                    break;
                case Params.STATUS_CONNECTING:
                    showProgress();
                    setCurrentStatus(UtilsUltra.getDescriptionByStatus(context,
                            asyncStatus));
                    break;
                case Params.STATUS_BUFFERING:
                    showProgress();
                    setCurrentStatus(UtilsUltra.getDescriptionByStatus(context,
                            asyncStatus));
                    break;

                default:
                    break;
            }
        } else {
            parseMediaStatus(mediaStatus);

        }

    }

    private void parseMediaStatus(int mediaStatus) {
        // TODO Auto-generated method stub
        switch (mediaStatus) {
            case Params.STATUS_PLAYING:
                setCurrentStatus(UtilsUltra.getDescriptionByStatus(context,
                        mediaStatus));
                showStopButton();
                break;
            case Params.STATUS_STOPED:
                setCurrentStatus(UtilsUltra.getDescriptionByStatus(context,
                        mediaStatus));
                showPlayButton();
                break;

            default:
                break;
        }

    }

    private void showPlayButton() {
        sstopButton.setVisibility(View.GONE);
        startButton.setVisibility(View.VISIBLE);
        progress.setVisibility(View.GONE);
    }

    private void showStopButton() {
        sstopButton.setVisibility(View.VISIBLE);
        startButton.setVisibility(View.GONE);
        progress.setVisibility(View.GONE);
    }

    private void showProgress() {
        sstopButton.setVisibility(View.GONE);
        startButton.setVisibility(View.GONE);
        progress.setVisibility(View.VISIBLE);
    }

    @Override
    public void updateOnRebind(StatusObject statusObj) {
        // TODO Auto-generated method stub
        if (statusObj.isOnRebindKeeper()) {
            if (statusObj.getError() != null) {
                showPlayButton();
                setCurrentStatus(statusObj.getError());
            } else if (statusObj.getPlayerStatus() != Params.STATUS_NONE) {
                parseMediaStatus(statusObj.getPlayerStatus());
                setCurrentTrack(statusObj.getArtist(), statusObj.getTrack());
            } else {
                setCurrentStatus(UtilsUltra.getDescriptionByStatus(context,
                        statusObj.getAsyncStatus()));

            }
        } else {
            setCurrentStatus("");
        }
    }

    @Override
    public void onImageBuffered() {
        // TODO Auto-generated method stub
        File artFile = new File(getActivity().getFilesDir(),
                Params.TEMP_FILE_NAME);
        if (artFile.exists()) {
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inPreferredConfig = Bitmap.Config.ARGB_8888;
            artImage.setImageBitmap(BitmapFactory.decodeFile(artFile
                    .getAbsolutePath()));
        } else {
            artImage.setImageResource(android.R.color.transparent);
        }
    }

}
