package com.intelliviz.retirementhelper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import com.intelliviz.retirementhelper.data.GovPensionIncomeData;
import com.intelliviz.retirementhelper.db.RetirementContract;

/**
 * Created by Ed Muhlestein on 9/23/2017.
 */

public class GovPensionRepository implements LoaderManager.LoaderCallbacks<Cursor> {
    public static final int GPID_LOADER = 1;
    public static final String ID_ARGS = "id";
    private volatile static GovPensionRepository sInstance;
    private GovPensionIncomeData mData = null;

    private BroadcastReceiver mMilestoneReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
        }
    };

    private GovPensionRepository() {}

    public static GovPensionRepository getInstance() {
        if(sInstance == null) {
            synchronized (GovPensionRepository.class) {
                if(sInstance == null) {
                    sInstance = new GovPensionRepository();
                }
            }
        }
        return sInstance;
    }

    public GovPensionIncomeData getGovPensionIncomeData() {
        if(mData == null) {

        }
        return mData;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle args) {
        Loader<Cursor> loader = null;
        Uri uri;
        switch (loaderId) {
            case GPID_LOADER:
                String id = args.getString(ID_ARGS);
                uri = RetirementContract.GovPensionIncomeEntry.CONTENT_URI;
                if(uri != null) {
                    uri = Uri.withAppendedPath(uri, id);
                }
/*
                loader = new CursorLoader(getActivity(),
                        uri,
                        null,
                        null,
                        null,
                       null);
*/
                break;
            default:
                loader = null;
        }

        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
