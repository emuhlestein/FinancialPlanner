package com.intelliviz.retirementhelper.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.intelliviz.retirementhelper.db.RetirementContract;

/**
 * Created by edm on 4/25/2017.
 */

public class DataBaseUtils {
    public static String addIncomeSource(Context context, int incomeSourceType, String incomeSourceName) {
        ContentValues values = new ContentValues();
        values.put(RetirementContract.IncomeSourceEntry.COLUMN_NAME, incomeSourceName);
        values.put(RetirementContract.IncomeSourceEntry.COLUMN_TYPE, incomeSourceType);
        Uri uri = context.getContentResolver().insert(RetirementContract.IncomeSourceEntry.CONTENT_URI, values);
        return uri.getLastPathSegment();
    }

    public static String addSavingsData(Context context, long incomeSourceId, String monthlyIncrease, String annualInterest) {
        ContentValues values = new ContentValues();
        values.put(RetirementContract.SavingsDataEntry.COLUMN_INCOME_SOURCE_ID, incomeSourceId);
        values.put(RetirementContract.SavingsDataEntry.COLUMN_MONTHLY_ADDITION, monthlyIncrease);
        values.put(RetirementContract.SavingsDataEntry.COLUMN_INTEREST, annualInterest);
        Uri uri = context.getContentResolver().insert(RetirementContract.SavingsDataEntry.CONTENT_URI, values);
        return uri.getLastPathSegment();
    }

    public static String addBalanceData(Context context, long incomeSourceId, String amount, String date) {
        ContentValues values = new ContentValues();
        values.put(RetirementContract.BalanceEntry.COLUMN_INCOME_SOURCE_ID, incomeSourceId);
        values.put(RetirementContract.BalanceEntry.COLUMN_AMOUNT, amount);
        values.put(RetirementContract.BalanceEntry.COLUMN_DATE, date);
        Uri uri = context.getContentResolver().insert(RetirementContract.BalanceEntry.CONTENT_URI, values);
        return uri.getLastPathSegment();
    }

    public static String addTaxDeferredData(Context context, long incomeSourceId, String minimumAge, String penaltyAmount, int is401k) {
        ContentValues values = new ContentValues();
        values.put(RetirementContract.TaxDeferredEntry.COLUMN_INCOME_SOURCE_ID, incomeSourceId);
        values.put(RetirementContract.TaxDeferredEntry.COLUMN_PENALTY_AGE, minimumAge);
        values.put(RetirementContract.TaxDeferredEntry.COLUMN_PENALTY_AMOUNT, penaltyAmount);
        values.put(RetirementContract.TaxDeferredEntry.COLUMN_IS_401K, is401k);
        Uri uri = context.getContentResolver().insert(RetirementContract.TaxDeferredEntry.CONTENT_URI, values);
        return uri.getLastPathSegment();
    }

    public static String addGovernmentPensionData(Context context, long incomeSourceId, String monthlyAmount, String minimumAge) {
        ContentValues values = new ContentValues();
        values.put(RetirementContract.GovPensionDataEntry.COLUMN_INCOME_SOURCE_ID, incomeSourceId);
        values.put(RetirementContract.GovPensionDataEntry.COLUMN_MONTHLY_BENEFIT, monthlyAmount);
        values.put(RetirementContract.GovPensionDataEntry.COLUMN_MIN_AGE, minimumAge);
        Uri uri = context.getContentResolver().insert(RetirementContract.GovPensionDataEntry.CONTENT_URI, values);
        return uri.getLastPathSegment();
    }

    public static String addPensionData(Context context, long incomeSourceId, String monthlyAmount, String startAge) {
        ContentValues values = new ContentValues();
        values.put(RetirementContract.PensionDataEntry.COLUMN_INCOME_SOURCE_ID, incomeSourceId);
        values.put(RetirementContract.PensionDataEntry.COLUMN_MONTHLY_BENEFIT, monthlyAmount);
        values.put(RetirementContract.PensionDataEntry.COLUMN_START_AGE, startAge);
        Uri uri = context.getContentResolver().insert(RetirementContract.PensionDataEntry.CONTENT_URI, values);
        return uri.getLastPathSegment();
    }

    public static int saveIncomeSourceData(Context context, long incomeSourceId, String incomeSourceName, int incomeSourceType) {
        ContentValues values  = new ContentValues();
        values.put(RetirementContract.IncomeSourceEntry.COLUMN_NAME, incomeSourceName);
        values.put(RetirementContract.IncomeSourceEntry.COLUMN_TYPE, incomeSourceType);

        String sid = String.valueOf(incomeSourceId);
        String selectionClause = RetirementContract.IncomeSourceEntry._ID + " = ?";
        String[] selectionArgs = new String[]{sid};
        Uri uri = RetirementContract.IncomeSourceEntry.CONTENT_URI;
        uri = Uri.withAppendedPath(uri, sid);
        return context.getContentResolver().update(uri, values, selectionClause, selectionArgs);
    }

    public static int saveSavingsData(Context context, long incomeSourceId, String monthlyIncrease, String annualInterest) {
        ContentValues values  = new ContentValues();
        values.put(RetirementContract.SavingsDataEntry.COLUMN_MONTHLY_ADDITION, monthlyIncrease);
        values.put(RetirementContract.SavingsDataEntry.COLUMN_INTEREST, annualInterest);

        String sid = String.valueOf(incomeSourceId);
        String selectionClause = RetirementContract.SavingsDataEntry.COLUMN_INCOME_SOURCE_ID + " = ?";
        String[] selectionArgs = new String[]{sid};
        Uri uri = RetirementContract.SavingsDataEntry.CONTENT_URI;
        uri = Uri.withAppendedPath(uri, sid);
        return context.getContentResolver().update(uri, values, selectionClause, selectionArgs);
    }

    public static int saveBalanceData(Context context, long incomeSourceId, String amount, String date) {
        ContentValues values  = new ContentValues();
        values.put(RetirementContract.BalanceEntry.COLUMN_AMOUNT, amount);
        values.put(RetirementContract.BalanceEntry.COLUMN_DATE, date);

        String sid = String.valueOf(incomeSourceId);
        String selectionClause = RetirementContract.BalanceEntry.COLUMN_INCOME_SOURCE_ID + " = ?";
        String[] selectionArgs = new String[]{sid};
        Uri uri = RetirementContract.BalanceEntry.CONTENT_URI;
        uri = Uri.withAppendedPath(uri, sid);
        return context.getContentResolver().update(uri, values, selectionClause, selectionArgs);
    }

    public static int saveTaxDeferredData(Context context, long incomeSourceId, String minimumAge, String penaltyAmount, int is401k) {
        /*
        ContentValues values  = new ContentValues();
        values.put(RetirementContract.SavingsDataEntry.COLUMN_MONTHLY_ADDITION, monthlyIncrease);
        values.put(RetirementContract.SavingsDataEntry.COLUMN_INTEREST, annualInterest);

        String sid = String.valueOf(incomeSourceId);
        String selectionClause = RetirementContract.SavingsDataEntry.COLUMN_INCOME_SOURCE_ID + " = ?";
        String[] selectionArgs = new String[]{sid};
        Uri uri = RetirementContract.SavingsDataEntry.CONTENT_URI;
        uri = Uri.withAppendedPath(uri, sid);
        return context.getContentResolver().update(uri, values, selectionClause, selectionArgs);
        */
        return 0;
    }

    public static Cursor getIncomeSource(Context context, long incomeSourceId) {
        Uri uri = RetirementContract.IncomeSourceEntry.CONTENT_URI;
        String[] projection = null; // we want all columns
        String selection = RetirementContract.IncomeSourceEntry._ID + " = ?";
        String id = String.valueOf(incomeSourceId);
        String[] selectionArgs = {id};
        return context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
    }

    public static Cursor getIncomeSource(Context context, String incomeSourceName) {
        Uri uri = RetirementContract.IncomeSourceEntry.CONTENT_URI;
        String[] projection = null; // we want all columns
        String selection = RetirementContract.IncomeSourceEntry.COLUMN_NAME + " = ?";
        String[] selectionArgs = {incomeSourceName};
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

    public static IncomeSourceData getIncomeSourceData(Context context, String incomeSourceName) {
        Cursor cursor = getIncomeSource(context, incomeSourceName);
        if(cursor == null || !cursor.moveToFirst()) {
            return null;
        }
        int nameIndex = cursor.getColumnIndex(RetirementContract.IncomeSourceEntry.COLUMN_NAME);
        int typeIndex = cursor.getColumnIndex(RetirementContract.IncomeSourceEntry.COLUMN_TYPE);
        incomeSourceName = cursor.getString(nameIndex);
        int incomeSourceType = cursor.getInt(typeIndex);
        return new IncomeSourceData(incomeSourceName, incomeSourceType);
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
