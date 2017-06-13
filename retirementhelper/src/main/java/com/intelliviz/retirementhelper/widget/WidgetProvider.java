package com.intelliviz.retirementhelper.widget;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.os.Build;
import android.support.annotation.NonNull;
import android.widget.RemoteViews;

import com.intelliviz.retirementhelper.R;
import com.intelliviz.retirementhelper.db.RetirementContract;
import com.intelliviz.retirementhelper.ui.SummaryActivity;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by edm on 6/12/2017.
 */

public class WidgetProvider extends AppWidgetProvider {
    private StringBuilder mStoredSymbols = new StringBuilder();

    public void updateStockQuotes(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        final int N = appWidgetIds.length;
        for(int i = 0; i < N; i++) {
            int appWidgetId = appWidgetIds[i];

            RemoteViews views = new RemoteViews(context.getPackageName(),
                    R.layout.milestone_collection_item_layout);

            // Bind this widget to a remove view service
            Intent intent = new Intent(context, MilestonesRemoteViewsService.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            views.setRemoteAdapter(appWidgetId, R.id.collection_widget_list_view, intent);
            Intent templateIntent = new Intent(context, SummaryActivity.class);
            templateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            PendingIntent templatePendingIntent = PendingIntent.getActivity(context, 0, templateIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            views.setPendingIntentTemplate(R.id.collection_widget_list_view, templatePendingIntent);

            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    public void updateStockQuotesOld(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Cursor initQueryCursor;
        StringBuilder urlStringBuilder = new StringBuilder();
        try {
            // Base URL for the Yahoo query
            urlStringBuilder.append("https://query.yahooapis.com/v1/public/yql?q=");
            urlStringBuilder.append(URLEncoder.encode("select * from yahoo.finance.quotes where symbol "
                    + "in (", "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        initQueryCursor = context.getContentResolver().query(RetirementContract.IncomeTypeEntry.CONTENT_URI,
                new String[]{"Distinct " + RetirementContract.IncomeTypeEntry.COLUMN_NAME, RetirementContract.IncomeTypeEntry.COLUMN_TYPE}, null,
                null, null);
        if (initQueryCursor != null) {
            DatabaseUtils.dumpCursor(initQueryCursor);
            int count = initQueryCursor.getCount();
            initQueryCursor.moveToFirst();
            for (int i = 0; i < initQueryCursor.getCount(); i++) {
                mStoredSymbols.append("\"" +
                        initQueryCursor.getString(initQueryCursor.getColumnIndex(RetirementContract.IncomeTypeEntry.COLUMN_NAME)) + "\",");
                mStoredSymbols.append("\"" +
                        initQueryCursor.getString(initQueryCursor.getColumnIndex(RetirementContract.IncomeTypeEntry.COLUMN_TYPE)) + "\",");
                initQueryCursor.moveToNext();
            }
            mStoredSymbols.replace(mStoredSymbols.length() - 1, mStoredSymbols.length(), ")");
            try {
                urlStringBuilder.append(URLEncoder.encode(mStoredSymbols.toString(), "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        initQueryCursor.close();

        final int N = appWidgetIds.length;
        for(int i = 0; i < N; i++) {
            int appWidgetId = appWidgetIds[i];
            RemoteViews views = new RemoteViews(context.getPackageName(),
                    R.layout.widget_provider_layout);
            views.setTextViewText(R.id.stock_text, mStoredSymbols.toString());
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        //if(StockSyncAdapter.STOCKS_REFRESHED.equals(intent.getAction())) {
            updateMilestones(context);
        //}
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                         int[] appWidgetIds) {

        updateStockQuotes(context, appWidgetManager, appWidgetIds);
    }


    /**
     * Sets the remote adapter used to fill in the list items
     *
     * @param views RemoteViews to set the RemoteAdapter
     */
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private static void setRemoteAdapter(Context context, @NonNull final RemoteViews views) {
        views.setRemoteAdapter(R.id.widget_list,
                new Intent(context, WidgetService.class));
    }

    public void updateMilestones(Context context) {
        ComponentName thisWidget = new ComponentName(context, WidgetProvider.class);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
        updateStockQuotes(context, appWidgetManager, appWidgetIds);
    }
}
