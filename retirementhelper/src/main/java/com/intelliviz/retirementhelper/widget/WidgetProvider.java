package com.intelliviz.retirementhelper.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.intelliviz.retirementhelper.R;
import com.intelliviz.retirementhelper.ui.SummaryActivity;

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
                    R.layout.milestone_collection_widget_layout);

            // Bind this widget to a remote view service
            Intent intent = new Intent(context, MilestonesRemoteViewsService.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            views.setRemoteAdapter(R.id.collection_widget_list_view, intent);

            Intent templateIntent = new Intent(context, SummaryActivity.class);
            templateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            PendingIntent templatePendingIntent = PendingIntent.getActivity(context, 0, templateIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            views.setPendingIntentTemplate(R.id.collection_widget_list_view, templatePendingIntent);


            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    public void updateStockQuotes1(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        final int N = appWidgetIds.length;
        for(int i = 0; i < N; i++) {
            int appWidgetId = appWidgetIds[i];

            RemoteViews views = new RemoteViews(context.getPackageName(),
                    R.layout.milestone_collection_item_layout);

            // Bind this widget to a remote view service
            Intent intent = new Intent(context, MilestonesRemoteViewsService.class);
            //intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            views.setRemoteAdapter(appWidgetId, R.id.collection_widget_list_view, intent);

            //Intent templateIntent = new Intent(context, SummaryActivity.class);
            //templateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            //PendingIntent templatePendingIntent = PendingIntent.getActivity(context, 0, templateIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            //views.setPendingIntentTemplate(R.id.collection_widget_list_view, templatePendingIntent);


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

    public void updateMilestones(Context context) {
        ComponentName thisWidget = new ComponentName(context, WidgetProvider.class);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
        updateStockQuotes(context, appWidgetManager, appWidgetIds);
    }
}
