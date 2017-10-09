package com.intelliviz.retirementhelper.widget;

import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.intelliviz.retirementhelper.R;
import com.intelliviz.retirementhelper.db.AppDatabase;
import com.intelliviz.retirementhelper.db.entity.SummaryEntity;

import java.util.List;

/**
 * Implementation of remote views factory.
 * Created by Ed Muhlestein on 6/12/2017.
 */

class MilestonesRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    private Context mContext;
    private List<SummaryEntity> mSummaryIncome;

    MilestonesRemoteViewsFactory(Context context) {
        mContext = context;
    }

    @Override
    public void onCreate() {
        mSummaryIncome = executeQuery();
    }

    @Override
    public void onDataSetChanged() {
        mSummaryIncome = executeQuery();
    }

    @Override
    public void onDestroy() {
    }

    @Override
    public int getCount() {
        if(mSummaryIncome != null) {
            return mSummaryIncome.size();
        } else {
            return 0;
        }
    }

    @Override
    public RemoteViews getViewAt(int position) {
        SummaryEntity entity = mSummaryIncome.get(position);

        String age = entity.getAge().toString();
        String monthlyBenefit = entity.getAmount();

        RemoteViews rv = new RemoteViews(mContext.getPackageName(),
                R.layout.milestone_collection_item_layout);
        rv.setTextViewText(R.id.start_age_text_view, age);
        rv.setTextViewText(R.id.monthly_benefit_text_view, monthlyBenefit);

        Intent fillIntent = new Intent();

        //Uri uri = RetirementContract.IncomeTypeEntry.CONTENT_URI;
        //fillIntent.setData(uri);

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
        if(mSummaryIncome != null) {
            return mSummaryIncome.get(position).getId();
        } else {
            return position;
        }
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    private List<SummaryEntity> executeQuery() {
        AppDatabase db = AppDatabase.getInstance(mContext.getApplicationContext());
        return db.summaryDao().get();
    }
}