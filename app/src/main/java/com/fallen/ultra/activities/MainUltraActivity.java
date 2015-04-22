package com.fallen.ultra.activities;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.fallen.ultra.R;
import com.fallen.ultra.callbacks.ActivityToFragmentFavListener;
import com.fallen.ultra.callbacks.ActivityToFragmentPlayerListener;
import com.fallen.ultra.callbacks.FavlistFragmentCallback;

import com.fallen.ultra.callbacks.PlayerFragmentCallback;
import com.fallen.ultra.callbacks.ServiceToActivityCallback;
import com.fallen.ultra.com.fallen.ultra.model.StatusObjectOverall;
import com.fallen.ultra.com.fallen.ultra.model.StatusObjectPlayer;
import com.fallen.ultra.database.SQLiteDB;
import com.fallen.ultra.fragments.DialogStreamsFragment;
import com.fallen.ultra.fragments.FragmentFavList;
import com.fallen.ultra.fragments.FragmentPlayer;
import com.fallen.ultra.services.UltraPlayerService;
import com.fallen.ultra.services.UltraPlayerService.LocalPlayerBinder;
import com.fallen.ultra.utils.Params;
import com.fallen.ultra.utils.UtilsUltra;

public class MainUltraActivity extends Activity implements
        PlayerFragmentCallback, ServiceToActivityCallback,
        FragmentManager.OnBackStackChangedListener, FavlistFragmentCallback {
    private ServiceConnection servCon;
    private UltraPlayerService playerService;

    StatusObjectOverall currentStatusObjectOverall;
    //ViewPager pager;
    private boolean isServiceBinded = false;
    private ActivityToFragmentPlayerListener mFragmentPlayerCallback;
    ActivityToFragmentFavListener fragmentFavCallBack;
    private int currentQualityKey;
    //private String currentArtist, currentTrack;
    SQLiteDB myDb;
    FragmentManager manager;
    private boolean isArtEnabled = true;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_ultra);
        currentQualityKey = UtilsUltra.getQualityFromPreferences(getSharedPreferences(Params.KEY_PREFERENCES, 0));
        isArtEnabled = UtilsUltra.getIsArtEnabledFromPreferences(getSharedPreferences(Params.KEY_PREFERENCES,0));
        createFragmentManager();
        if (manager!=null) {
            showPlayerFragment();

        }

    }

    private void showPlayerFragment() {
        FragmentTransaction ft =  manager.beginTransaction();
        ft.add(R.id.fragmentHolder, new FragmentPlayer());
        ft.commit();

    }

    private void createFragmentManager() {
        manager = getFragmentManager();
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences myPreferences = getSharedPreferences(
                Params.KEY_PREFERENCES, MODE_PRIVATE);
        UtilsUltra.getQualityFromPreferences(myPreferences);
        Intent intent = UtilsUltra.createServiceIntentFromActivity(
                getApplicationContext(), Params.FLAG_BIND_ACTIVITY);
        if (servCon == null)
            settingUpServiceConnection();
        bindService(intent, servCon, Context.BIND_AUTO_CREATE);


    }

    private void settingUpServiceConnection() {
        servCon = new ServiceConnection() {

            @Override
            public void onServiceDisconnected(ComponentName name) {
                System.out.println("onServiceDisconnected");
                isServiceBinded = false;
                if (playerService != null)
                    playerService.setCallback(null);
                else
                    UtilsUltra.printLog("player service is null in activity",
                            "Ultra Activity", Log.ERROR);
                servCon = null;

            }

            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                // TODO Auto-generated method stub
                playerService = ((LocalPlayerBinder) service).getService();
                isServiceBinded = true;
                playerService.setCallback(MainUltraActivity.this);
                currentStatusObjectOverall = playerService.getStatusObject();
                mFragmentPlayerCallback.onRebindRestoreStatus(currentStatusObjectOverall);
                System.out.println("onServiceConnected");
            }
        };
    }

    @Override
    protected void onPause() {
        if (isServiceBinded && servCon != null) {

            unbindService(servCon);
            UtilsUltra.printLog("Activity onPause, unbind service",
                    "Ultra Activity", Log.VERBOSE);
        } else
            UtilsUltra.printLog("cant unbind service from activity",
                    "Ultra Activity", Log.ERROR);

        super.onPause();
    }

    @Override
    protected void onStop() {


        System.out.println("Activity onStop");
        System.out.println("put quality " + currentQualityKey);
        UtilsUltra.putQualityToSharedPrefs(
                getSharedPreferences(Params.KEY_PREFERENCES,
                        MODE_PRIVATE), currentQualityKey);
//        if (currentStatusObjectOverall !=null && (currentStatusObjectOverall.getStatusAsync() == Params.STATUS_BUFFERING|| currentStatusObjectOverall.getStatusAsync() == Params.STATUS_CONNECTING))
//            stop();
         super.onStop();
    }

    @Override
    protected void onDestroy() {
        System.out.println("Activity onDestroy");
        super.onDestroy();
    }

//	@Override
//	public void pageChanged(int position) {
//		// TODO Auto-generated method stub
//		
//
//	}

    public void sendStartIntent(int flag) {
        Intent intent = UtilsUltra.createServiceIntentFromActivity(
                getApplicationContext(), flag, currentQualityKey);
        startService(intent);
    }

    public void stop() {
        Intent intent = UtilsUltra.createServiceIntentFromActivity(
                getApplicationContext(), Params.FLAG_STOP);
        startService(intent);
    }

    // Only button click handle
    // if need addition info use another callback
    @Override
    public void buttonClicked(int action) {
        switch (action) {
            case Params.BUTTON_START_KEY:
                sendStartIntent(Params.FLAG_PLAY);
                break;
            case Params.BUTTON_STOP_KEY:
                stop();
                break;
            case Params.BUTTON_SHOW_LIST_FAV:
                showFavList();
                break;

            case Params.BUTTON_FAV_ON:
                sendArtistToDb();
                break;

            case Params.BUTTON_FAV_OFF:
                sendArtistToDb();
                break;

            default:
                Log.e("ultraerr", "MainUltraActivity wrong action button id " + action);
                break;
        }
    }

    private void showFavList() {

        if (manager!=null) {
            FragmentTransaction fragmentTransaction = manager.beginTransaction();
             fragmentTransaction.replace(R.id.fragmentHolder, new FragmentFavList());
             fragmentTransaction.addToBackStack(null).commit();

        }
    }


    private void sendArtistToDb() {
        if (myDb==null)
            myDb = new SQLiteDB(this);
        if (currentStatusObjectOverall !=null && mFragmentPlayerCallback !=null) {
            int resultCode = myDb.actionFavArtist(currentStatusObjectOverall.getArtist(), currentStatusObjectOverall.getTrack());
            UtilsUltra.printLog("result code on Fav to Db" + resultCode);
            switch (resultCode)
            {
                case Params.DB_ADD_SUCCESS:
                    mFragmentPlayerCallback.onFavoriteDefine(true);
                    break;
                case Params.DB_ARTIST_DELETED:
                    mFragmentPlayerCallback.onFavoriteDefine(false);
                    break;
                default:
                    UtilsUltra.printLog("error occurred in DB action code =" + resultCode, Params.DEFAULT_LOG_TYPE, Log.ERROR);
                    break;
            }
        }
        else
        {
            UtilsUltra.printLog("Status object is null");
        }

    }

    @Override
    public void onUnbindService() {


    }


    @Override
    public void onInit() {

        //int playerFragmentPosition = 0;
        //MyPagerAdapter myAdapter = (MyPagerAdapter) pager.getAdapter();
        //SparseArray<Fragment> spa = myAdapter.getCurrentFragment();
        //Fragment f = spa.get(playerFragmentPosition);
        //FragmentPlayer myFragment = (FragmentPlayer) f;
        //mFragmentPlayerCallback = myFragment;
        UtilsUltra.printLog("onFragmentInit");
        if (playerService != null && isServiceBinded
                && mFragmentPlayerCallback != null)// looks
            // like
            // we
            // rebind
            UtilsUltra.printLog("trying to reset status on init player fragment");
            mFragmentPlayerCallback.onStatusChanged(currentStatusObjectOverall);
        // updateAllFramentFields();

    }

    @Override
    public void onFragmentCreatedNoInit() {
        mFragmentPlayerCallback = null;
    }

//	@Override
//	public void onStatusAsyncChanged(int status) {
//		if (mFragmentPlayerCallback != null) {
//			currentStringStatus = UtilsUltra.getStatusDescription(
//					getResources(), status);
//			mFragmentPlayerCallback.onStatusAsyncChanged(currentStringStatus);
//		}
//
//	}


    public void setQuality(int qualityKey) {
        currentQualityKey = qualityKey;
        UtilsUltra.putQualityToSharedPrefs(
                getSharedPreferences(Params.KEY_PREFERENCES,
                        MODE_PRIVATE), currentQualityKey);


//        if (currentStatusObjectOverall !=null && (currentStatusObjectOverall.getStatusAsync() == Params.STATUS_PLAYING || currentStatusObjectOverall.getStatusAsync() == Params.STATUS_BUFFERING || currentStatusObjectOverall.getStatusAsync() ==Params.STATUS_CONNECTING))
//        {
//            //System.out.println ("player status " + currentStatusObjectAsync.getPlayerStatus());
//            stop();
//            isWaitingFroStop = true;
//        }
//        else
//            sendStartIntent();


    }

    @Override
    public boolean checkIsCurrentTrackFav() {
        if (myDb ==null)
            myDb = new SQLiteDB(this);
        if (currentStatusObjectOverall!=null)
        return myDb.checkIfAlreadyAdded(currentStatusObjectOverall.getArtist(), currentStatusObjectOverall.getTrack());
        else
            return true;
    }

    @Override
    public void onQualityChangedPositiveClicked(int qualityFromDialog) {
        UtilsUltra.printLog("current " + currentQualityKey +"; qualityFromDialog" + qualityFromDialog, "", Log.ERROR);
        if (currentQualityKey == qualityFromDialog)
            return; //do nothing, same quality
        else {

            setQuality(qualityFromDialog);
            if (currentStatusObjectOverall.getStatusPlayer() == Params.STATUS_PLAYING) {
                sendStartIntent(Params.FLAG_RESTART);

            }
        }
    }

    @Override
    public StatusObjectOverall getOverallStatus() {
        return currentStatusObjectOverall;
    }


    public boolean isArtEnabled() {
        return isArtEnabled;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.menu_item_favorites:
                   showFavList();
                break;
            case R.id.menu_item_stream_select:
                showDialogStreamSelect();
                break;
            case R.id.menu_load_art:
                item.setChecked(!isArtEnabled);
                UtilsUltra.printLog("setArtLoad " +item.isChecked() );
                boolean isLoadImageEnabled = item.isChecked();
                setLoadImages(isLoadImageEnabled);
                return false;
                    
            default:

                break;

        }
        return true;
    }

    private void setLoadImages(boolean isLoadImageEnabled) {
        isArtEnabled = isLoadImageEnabled;
        UtilsUltra.putImageLoadToSharedPrefs(getSharedPreferences(Params.KEY_PREFERENCES,0), isLoadImageEnabled);
    }

    private void showDialogStreamSelect() {
        if (manager!=null) {
            DialogStreamsFragment streamsDialog = new DialogStreamsFragment();
            streamsDialog.show(manager, "streamsDialog");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_ultra, menu);
       MenuItem checkableMenuItem  = menu.findItem(R.id.menu_load_art);
        checkableMenuItem.setChecked(isArtEnabled);

        return true;
    }
//
//    @Override
//    public void onRebindStatus(StatusObjectOverall statusObjectOverallRebinded) {
//        // TODO Auto-generated method stub
//        this.statusObjectOverallRebinded = statusObjectOverallRebinded;
//        if (playerService != null && isServiceBinded
//                && statusObjectOverallRebinded != null && mFragmentPlayerCallback != null)// looks
//            // like
//            // we
//            // rebind
//            mFragmentPlayerCallback.updateOnRebind(playerService.getStatusObject());
//        UtilsUltra.printLog("onRebindStatus");
//    }

    public void setCallback(FragmentPlayer fragmentPlayer) {
        // TODO Auto-generated method stub
        mFragmentPlayerCallback = fragmentPlayer;
    }

    @Override
    public void onImageBuffered() {
        // TODO Auto-generated method stub
        if (mFragmentPlayerCallback != null)
            mFragmentPlayerCallback.onImageBuffered();
    }

    @Override
    public void onStreamStop() {
//        if (isWaitingFroStop ==true) {
//            isWaitingFroStop = false;
//            sendStartIntent();
//        }
    }

    @Override
    public void onUpdateStatus(StatusObjectOverall statusObjectOverall) {

        if (mFragmentPlayerCallback != null && statusObjectOverall!=null) {
            mFragmentPlayerCallback.onStatusChanged(statusObjectOverall);
            if (statusObjectOverall.getStatusAsync() == Params.STATUS_NEW_TITLE) {
                if (myDb ==null)
                    myDb = new SQLiteDB(this);
                mFragmentPlayerCallback.onFavoriteDefine(myDb.checkIfAlreadyAdded(currentStatusObjectOverall.getArtist(), currentStatusObjectOverall.getTrack()));
            }
        }

        currentStatusObjectOverall = statusObjectOverall;
    }

    @Override
    public void onBackStackChanged() {
    }

    @Override
    public void deleteItemClicked(int itemId) {
        if (myDb!=null) {
            System.out.println("onDeleteFromClick " + myDb.removeArtistFromDB(itemId));
            if (fragmentFavCallBack!=null)
                fragmentFavCallBack.artistDeleted(myDb.getAllFields());
        }
    }

    @Override
    public void setCallBackFav(ActivityToFragmentFavListener fragmentFavCallBack) {
        if (fragmentFavCallBack!=null) {
            this.fragmentFavCallBack = fragmentFavCallBack;
            if (myDb==null) {
                myDb = new SQLiteDB(this);
            }
                fragmentFavCallBack.setFavAdapter(this, myDb.getAllFields());
        }

    }
}
