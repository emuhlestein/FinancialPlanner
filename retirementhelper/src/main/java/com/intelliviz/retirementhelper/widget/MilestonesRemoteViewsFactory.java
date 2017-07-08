package com.intelliviz.retirementhelper.widget;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.intelliviz.retirementhelper.R;
import com.intelliviz.retirementhelper.db.RetirementContract;

/**
 * Implementation of remote views factory.
 * Created by Ed Muhlestein on 6/12/2017.
 */

class MilestonesRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    private Context mContext;
    private Cursor mCursor;

    MilestonesRemoteViewsFactory(Context context) {
        mContext = context;
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

        int ageIndex = mCursor.getColumnIndex(RetirementContract.SummaryEntry.COLUMN_AGE);
        int amountIndex = mCursor.getColumnIndex(RetirementContract.SummaryEntry.COLUMN_AMOUNT);

        String age = mCursor.getString(ageIndex);
        String monthlyBenefit = mCursor.getString(amountIndex);

        RemoteViews rv = new RemoteViews(mContext.getPackageName(),
                R.layout.milestone_collection_item_layout);
        rv.setTextViewText(R.id.start_age_text_view, age);
        rv.setTextViewText(R.id.monthly_benefit_text_view, monthlyBenefit);

        Intent fillIntent = new Intent();

        Uri uri = RetirementContract.IncomeTypeEntry.CONTENT_URI;
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
            return mCursor.getInt(mCursor.getColumnIndex(RetirementContract.SummaryEntry._ID));
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
                RetirementContract.SummaryEntry._ID,
                RetirementContract.SummaryEntry.COLUMN_AGE,
                RetirementContract.SummaryEntry.COLUMN_AMOUNT
        };

        return mContext.getContentResolver().query(RetirementContract.SummaryEntry.CONTENT_URI,
                projection, null, null, null);
    }
}