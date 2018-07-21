package com.intelliviz.income.util;

import android.app.Application;
import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.intelliviz.income.R;
import com.intelliviz.income.ui.BirthdateDialog;

/**
 * Created by edm on 6/16/2018.
 */

public class uiUtils {

    public static void updateAppWidget(Application application) {
        //AppDatabase db = AppDatabase.getInstance(application);
        //DataBaseUtils.updateSummaryData(db);
        //AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(application);
        //ComponentName appWidget = new ComponentName(application, WidgetProvider.class);
        //int[] appWidgetIds = appWidgetManager.getAppWidgetIds(appWidget);
        //appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.collection_widget_list_view);
    }

    public static void showDialog(FragmentActivity activity, String birthdate, BirthdateDialogAction birthdateDialogAction) {
        FragmentManager fm = activity.getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        BirthdateDialog birthdateDialog = BirthdateDialog.getInstance(birthdate, birthdateDialogAction);
        birthdateDialog.show(fm, "birhtdate");
    }

    public static String getIncomeSourceTypeString(Context context, int incomeSourceType) {
        final String[] incomeTypes = context.getResources().getStringArray(R.array.income_types);
        return incomeTypes[incomeSourceType];
    }
}
