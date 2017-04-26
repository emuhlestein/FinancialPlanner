package com.intelliviz.retirementhelper.util;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.intelliviz.retirementhelper.db.RetirementContract;

/**
 * Created by edm on 4/25/2017.
 */

public class DataBaseUtils {
    public static Cursor getIncomeSource(Context context, long incomeSourceId) {
        Uri uri = RetirementContract.IncomeSourceEntry.CONTENT_URI;
        String[] projection = null; // we want all columns
        String selection = RetirementContract.IncomeSourceEntry._ID + " = ?";
        String id = String.valueOf(incomeSourceId);
        String[] selectionArgs = {id};
        return context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
    }

    public static Cursor getSavingsData(Context context, long incomeSourceId) {
        Uri uri = RetirementContract.SavingsDataEntry.CONTENT_URI;
        String[] projection = null; // we want all columns
        String selection = RetirementContract.SavingsDataEntry.COLUMN_INCOME_SOURCE_ID + " = ?";
        String id = String.valueOf(incomeSourceId);
        String[] selectionArgs = {id};
        return context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
    }
}
