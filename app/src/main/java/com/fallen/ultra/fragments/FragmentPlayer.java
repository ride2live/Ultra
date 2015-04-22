package com.fallen.ultra.fragments;

import android.app.Fragment;
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
import android.widget.TextView;

import com.fallen.ultra.R;
import com.fallen.ultra.activities.MainUltraActivity;
import com.fallen.ultra.callbacks.ActivityToFragmentPlayerListener;
import com.fallen.ultra.callbacks.PlayerFragmentCallback;
import com.fallen.ultra.callbacks.UniversalFragmentButtonListener;
import com.fallen.ultra.com.fallen.ultra.model.StatusObjectOverall;
import com.fallen.ultra.com.fallen.ultra.model.StatusObjectPlayer;
import com.fallen.ultra.listeners.MyFragmentButtonClickListener;
import com.fallen.ultra.utils.Params;
import com.fallen.ultra.utils.UtilsUltra;

import java.io.File;

public class FragmentPlayer extends Fragment implements
        UniversalFragmentButtonListener, ActivityToFragmentPlayerListener {
    PlayerFragmentCallback playerFragmentCallback;
    TextView artistView, trackView, statusView;
    int current_quality_key = Params.QUALITY_DEFAULT_KEY;



    Context context;
    Button startButton, sstopButton, listButton, buttonstatus;
    ImageButton starOnButton, starOffButton;
    ProgressBar progress;
    ImageView artImage;
    private String currentTitle = Params.NO_TITLE;
    private String currentArtist = Params.NO_TITLE;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainUltraActivity mainActivity =(MainUltraActivity) getActivity();
        if (mainActivity!=null)
            mainActivity.setCallback(this);

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
       // buttonstatus = (Button) getView().findViewById(R.id.buttonstatus);

        //listButton= (Button) getView().findViewById(R.id.listFavButton);

        MyFragmentButtonClickListener buttonListener = new MyFragmentButtonClickListener(this,
               getActivity());
        //listButton.setOnClickListener(buttonListener);
        starOnButton.setOnClickListener(buttonListener);
        starOffButton.setOnClickListener(buttonListener);
        startButton.setOnClickListener(buttonListener);
        sstopButton.setOnClickListener(buttonListener);

        playerFragmentCallback.onInit();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_player, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (playerFragmentCallback!=null) {
            setFavoritesButtonState(playerFragmentCallback.checkIsCurrentTrackFav());

                onImageBuffered();
            if (playerFragmentCallback.getOverallStatus()!=null)
                onRebindRestoreStatus(playerFragmentCallback.getOverallStatus());
        }


        System.out.println("resume player fragment");
    }

    private void setDefaultImage() {
        if (artImage !=null)
            artImage.setImageResource(R.drawable.ultra_400400);
    }

    @Override
    public void onPause() {
        super.onPause();
        System.out.println("pause player fragment");
    }

    @Override
    public void onButtonClicked(int actionFromButton) {

        playerFragmentCallback.buttonClicked(actionFromButton);


    }
    @Override
    public void onFavoriteDefine(boolean isAlreadyInFav) {
            setFavoritesButtonState (isAlreadyInFav);


    }

    @Override
    public void onRebindRestoreStatus(StatusObjectOverall currentStatusObjectOverall) {
        onStatusChanged(currentStatusObjectOverall);
        setCurrentTrack(currentStatusObjectOverall.getArtist(),currentStatusObjectOverall.getTrack());

    }

    @Override
    public void onRestartStream() {
        showProgress();
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


    public void setCurrentTrack(String artist, String title) {
        // TODO Auto-generated method stub
        starOnButton.setVisibility(View.GONE);
        starOffButton.setVisibility(View.GONE);
        currentTitle = title;
        currentArtist = artist;
        this.artistView.setText(artist);
        this.trackView.setText(title);
        setFavoritesButtonState(playerFragmentCallback.checkIsCurrentTrackFav());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        playerFragmentCallback.onFragmentCreatedNoInit();
    }





    @Override
    public void onStatusChanged(StatusObjectOverall stausObj) {
        if (stausObj == null)
            return;
        int currentOverallStatus = stausObj.getStatusAsync();
        //buttonstatus.setText(String.valueOf(currentOverallStatus));
        boolean isConsumedByHigherStatus = false;
        UtilsUltra.printLog(currentOverallStatus + " status recieved in player fragment");
            switch (currentOverallStatus) {
                case Params.STATUS_NEW_TITLE:
                    setCurrentTrack(stausObj.getArtist(), stausObj.getTrack());
                    break;
                case Params.STATUS_ERROR:

                    setCurrentStatus(stausObj.getError());
                    break;
                case Params.STATUS_CONNECTING:
                    isConsumedByHigherStatus = true;
                    showProgress();
                    setCurrentStatus(UtilsUltra.getDescriptionByStatus(context,
                            currentOverallStatus));
                    break;
                case Params.STATUS_BUFFERING:
                    isConsumedByHigherStatus = true;
                    showProgress();
                    setCurrentStatus(UtilsUltra.getDescriptionByStatus(context,
                            currentOverallStatus));
                    break;
                case Params.STATUS_STOPPING_IN_PROCESS:
                    isConsumedByHigherStatus = true;
                    showProgress();
                    setCurrentStatus(UtilsUltra.getDescriptionByStatus(context,
                            currentOverallStatus));

                    break;
                case Params.STATUS_STOPED:
                    isConsumedByHigherStatus= false;
                    setCurrentStatus(UtilsUltra.getDescriptionByStatus(context,
                            currentOverallStatus));
                default:
                    break;
            }
        if (!isConsumedByHigherStatus)
            parseMediaStatus(stausObj.getStatusPlayer());


    }

    private void setCurrentStatus(String currentStatus) {
        // TODO Auto-generated method stub
        statusView.setText(currentStatus);
    }

//    @Override
//    public void onQualityChange(int quality) {
//        // TODO Auto-generated method stub
//        playerFragmentCallback.setQuality(quality);
//    }




    private void parseMediaStatus(int mediaStatus) {

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

        sstopButton.setVisibility(View.INVISIBLE);
        startButton.setVisibility(View.INVISIBLE);
        progress.setVisibility(View.VISIBLE);


    }

//    @Override
//    public void updateOnRebind(StatusObjectOverall statusObj, StatusObjectPlayer statusPlayer) {
//        // TODO Auto-generated method stub
//        if (statusObj.isOnRebindKeeper()) {
//            if (statusObj.getError() != null) {
//                showPlayButton();
//                setCurrentStatus(statusObj.getError());
//            } else if (statusPlayer.getPlayerStatus() != Params.STATUS_NONE) {
//                parseMediaStatus(statusPlayer.getPlayerStatus());
//                setCurrentTrack(statusObj.getArtist(), statusObj.getTrack());
//            } else {
//                setCurrentStatus(UtilsUltra.getDescriptionByStatus(context,
//                        statusObj.getStatusAsync()));
//
//            }
//        } else {
//            setCurrentStatus("");
//        }
//    }

    @Override
    public void onImageBuffered() {
        // TODO Auto-generated method stub
        if (getActivity()!=null && !((MainUltraActivity)getActivity()).isArtEnabled()) {
            setDefaultImage();
            return;
        }
        File artFile = new File(getActivity().getFilesDir(),
                Params.TEMP_FILE_NAME);
        if (artFile.exists()) {
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inPreferredConfig = Bitmap.Config.ARGB_8888;
            artImage.setImageBitmap(BitmapFactory.decodeFile(artFile
                    .getAbsolutePath()));
        } else {
            setDefaultImage();
        }
    }

}
