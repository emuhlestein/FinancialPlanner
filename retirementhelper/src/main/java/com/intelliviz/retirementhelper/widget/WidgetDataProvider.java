package com.intelliviz.retirementhelper.widget;

import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by edm on 6/12/2017.
 */

public class WidgetDataProvider implements RemoteViewsService.RemoteViewsFactory {

    List<String> mCollection = new ArrayList<>();
    Context mContext;
    Intent mIntent;

    public WidgetDataProvider(Context context, Intent intent) {
        mContext = context;
        mIntent = intent;
    }
    @Override
    public void onCreate() {
        initData();
    }

    @Override
    public void onDataSetChanged() {
        initData();
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return mCollection.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews remoteViews = new RemoteViews(mContext.getPackageName(),
                android.R.layout.simple_list_item_1);
        remoteViews.setTextViewText(android.R.id.text1, mCollection.get(position));
        return remoteViews;
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
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    private void initData() {
        mCollection.clear();
        for(int i = 0; i <= 10; i++) {
            mCollection.add("ListView item: " + i);
        }
    }
}
