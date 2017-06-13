package com.intelliviz.retirementhelper.widget;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.intelliviz.retirementhelper.R;
import com.intelliviz.retirementhelper.db.RetirementContract;

/**
 * Created by edm on 6/12/2017.
 */

public class MilestonesRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    private Context mContext;
    private ContentResolver mCR;
    private Cursor mCursor;

    public MilestonesRemoteViewsFactory(Context context) {
        mContext = context;
        mCR = context.getContentResolver();
    }

    @Override
    public void onCreate() {
        mCursor = executeQuery();
    }

    @Override
    public void onDataSetChanged() {
        mCursor = executeQuery();
    }

    @Override
    public void onDestroy() {
        mCursor.close();
    }

    @Override
    public int getCount() {
        if(mCursor != null) {
            return mCursor.getCount();
        } else {
            return 0;
        }
    }

    @Override
    public RemoteViews getViewAt(int position) {
        mCursor.moveToPosition(position);
        int count = mCursor.getCount();

        int nameIndex = mCursor.getColumnIndex(RetirementContract.IncomeTypeEntry.COLUMN_NAME);
        int typeIndex = mCursor.getColumnIndex(RetirementContract.IncomeTypeEntry.COLUMN_TYPE);

        String name = mCursor.getString(nameIndex);
        int type = mCursor.getInt(typeIndex);

        RemoteViews rv = new RemoteViews(mContext.getPackageName(),
                R.layout.milestone_collection_item_layout);
        rv.setTextViewText(R.id.start_age_text_view, name);
        rv.setTextViewText(R.id.monthly_benefit_text_view, Integer.toString(type));

        Intent fillIntent = new Intent();

        // TODO should not use name here; need to figure something out
        Uri uri = Uri.withAppendedPath(RetirementContract.IncomeTypeEntry.CONTENT_URI, name);
        fillIntent.setData(uri);

        rv.setOnClickFillInIntent(R.id.start_age_text_view, fillIntent);
        rv.setOnClickFillInIntent(R.id.monthly_benefit_text_view, fillIntent);

        return rv;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        if(mCursor != null) {
            return mCursor.getInt(mCursor.getColumnIndex(RetirementContract.IncomeTypeEntry._ID));
        } else {
            return position;
        }
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    private Cursor executeQuery() {
        String[] projection = new String[] {
                RetirementContract.IncomeTypeEntry._ID,
                RetirementContract.IncomeTypeEntry.COLUMN_TYPE,
                RetirementContract.IncomeTypeEntry.COLUMN_NAME
        };

        Cursor cursor = mContext.getContentResolver().query(RetirementContract.IncomeTypeEntry.CONTENT_URI,
                projection, null, null, null);

        return cursor;
    }
}
