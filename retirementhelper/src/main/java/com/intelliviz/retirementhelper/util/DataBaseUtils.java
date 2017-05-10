package com.intelliviz.retirementhelper.util;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.intelliviz.retirementhelper.db.RetirementContract;

/**
 * Created by edm on 4/25/2017.
 */

public class DataBaseUtils {
    public static int saveIncomeSource(long incomeSourceId, int incomeSourceType, String incomeSourceName) {
        String sid = Long.toString(incomeSourceId);

        // save income source data
        /*
        ContentValues values = new ContentValues();
        values.put(RetirementContract.IncomeSourceEntry.COLUMN_NAME, incomeSourceName);
        values.put(RetirementContract.IncomeSourceEntry.COLUMN_TYPE, incomeSourceType);

        String[] projection = {RetirementContract.IncomeSourceEntry.COLUMN_NAME};
        String selectionClause = RetirementContract.IncomeSourceEntry.COLUMN_NAME + " = ?";
        String[] selectionArgs = {incomeSourceName};
        String selectionClause = RetirementContract.IncomeSourceEntry._ID + " = ?";

        selectionArgs = new String[]{sid};
        uri = RetirementContract.IncomeSourceEntry.CONTENT_URI;
        uri = Uri.withAppendedPath(uri, sid);
        int rowsUpdated = getContext().getContentResolver().update(uri, values, selectionClause, selectionArgs);
        if(rowsUpdated != 1) {
            Toast.makeText(getContext(), "Error updating " + incomeSourceName, Toast.LENGTH_LONG).show();
        }
        */
        return 0;
    }

    public static void saveSavingsData() {

    }

    public static void saveBalanceData() {

    }

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

    public static Cursor getTaxDeferred(Context context, long incomeSourceId) {
        Uri uri = RetirementContract.TaxDeferredEntry.CONTENT_URI;
        String[] projection = null; // we want all columns
        String selection = RetirementContract.TaxDeferredEntry.COLUMN_INCOME_SOURCE_ID + " = ?";
        String id = String.valueOf(incomeSourceId);
        String[] selectionArgs = {id};
        return context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
    }

    /**
     * Get the balances for the specified income source id.
     * @param context The context.
     * @param incomeSourceId The income source id.
     * @return The cursor.
     */
    public static Cursor getBalances(Context context, long incomeSourceId) {
        Uri uri = RetirementContract.BalanceEntry.CONTENT_URI;
        String[] projection = null; // we want all columns
        String selection = RetirementContract.BalanceEntry.COLUMN_INCOME_SOURCE_ID + " = ?";
        String id = String.valueOf(incomeSourceId);
        String[] selectionArgs = {id};
        String sortOrder = RetirementContract.BalanceEntry.COLUMN_DATE + " DESC";
        return context.getContentResolver().query(uri, projection, selection, selectionArgs, sortOrder);
    }

    public static IncomeSourceData getIncomeSourceData(Context context, long incomeSourceId) {
        Cursor cursor = getIncomeSource(context, incomeSourceId);
        if(cursor == null || !cursor.moveToFirst()) {
            return null;
        }
        int nameIndex = cursor.getColumnIndex(RetirementContract.IncomeSourceEntry.COLUMN_NAME);
        int typeIndex = cursor.getColumnIndex(RetirementContract.IncomeSourceEntry.COLUMN_TYPE);
        String incomeSourceName = cursor.getString(nameIndex);
        int incomeSourceType = cursor.getInt(typeIndex);
        return new IncomeSourceData(incomeSourceName, incomeSourceType);
    }

    public static SavingsDataData getSavingsDataData(Context context, long incomeSourceId) {
        Cursor cursor = getSavingsData(context, incomeSourceId);
        if(cursor == null || !cursor.moveToFirst()) {
            return null;
        }
        int interestIndex = cursor.getColumnIndex(RetirementContract.SavingsDataEntry.COLUMN_INTEREST);
        int monthlyIncreaseIndex = cursor.getColumnIndex(RetirementContract.SavingsDataEntry.COLUMN_MONTHLY_ADDITION);
        String interest = cursor.getString(interestIndex);
        String monthlyIncrease = cursor.getString(monthlyIncreaseIndex);
        return new SavingsDataData(interest, monthlyIncrease);
    }

    public static TaxDeferredData getTaxDeferredData(Context context, long incomeSourceId) {
        Cursor cursor = getTaxDeferred(context, incomeSourceId);
        if(cursor == null || !cursor.moveToFirst()) {
            return null;
        }
        int amountIndex = cursor.getColumnIndex(RetirementContract.TaxDeferredEntry.COLUMN_PENALTY_AMOUNT);
        int ageIndex = cursor.getColumnIndex(RetirementContract.TaxDeferredEntry.COLUMN_PENALTY_AGE);
        int is401kIndex = cursor.getColumnIndex(RetirementContract.TaxDeferredEntry.COLUMN_IS_401K);
        String amount = cursor.getString(amountIndex);
        String age = cursor.getString(ageIndex);
        int is401k = cursor.getInt(is401kIndex);
        return new TaxDeferredData(amount, age, is401k);
    }

    public static BalanceData[] getBalanceData(Context context, long incomeSourceId) {
        Cursor cursor = getBalances(context, incomeSourceId);
        if(cursor == null || !cursor.moveToFirst()) {
            return null;
        }

        BalanceData bd[] = new BalanceData[cursor.getCount()];
        int index = 0;
        for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            int balanceIndex = cursor.getColumnIndex(RetirementContract.BalanceEntry.COLUMN_AMOUNT);
            int dateIndex = cursor.getColumnIndex(RetirementContract.BalanceEntry.COLUMN_DATE);
            String balance = cursor.getString(balanceIndex);
            String date = cursor.getString(dateIndex);
            bd[index++] = new BalanceData(balance, date);
        }

        return bd;
    }


}
