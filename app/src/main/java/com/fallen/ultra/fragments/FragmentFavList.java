package com.fallen.ultra.fragments;


import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.fallen.ultra.R;
import com.fallen.ultra.activities.MainUltraActivity;
import com.fallen.ultra.adapters.FavListAdapter;
import com.fallen.ultra.callbacks.ActivityToFragmentFavListener;
import com.fallen.ultra.callbacks.FavlistFragmentCallback;

/**
 * Created by Nolwe on 25.03.2015.
 */
public class FragmentFavList extends android.app.Fragment implements ActivityToFragmentFavListener{

    FavlistFragmentCallback favCallback;
    ListView favListView;
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        MainUltraActivity activity = (MainUltraActivity) getActivity();
        favCallback = activity ;
        activity.setCallBackFav(this);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fragmentFavView = inflater.inflate(R.layout.fragment_favlist, container, false);
        favListView = (ListView) fragmentFavView.findViewById(R.id.favListView);
        if (favCallback!=null)
        {
            favCallback.setCallBackFav(this);
        }
        return fragmentFavView;
    }

    @Override
    public void setFavAdapter(Context context, Cursor cursor)
    {
        favListView.setAdapter(new FavListAdapter(context,cursor, true, favCallback));

    }


    @Override
    public void artistDeleted(Cursor cursor) {
    if (favListView!=null) {
        FavListAdapter favAdapter = (FavListAdapter) favListView.getAdapter();
        if (favAdapter!=null)
            favAdapter.changeCursor(cursor);
    }
    }
}
