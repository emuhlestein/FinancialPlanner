package com.intelliviz.retirementhelper.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.intelliviz.retirementhelper.data.TaxDeferredIncomeData;
import com.intelliviz.retirementhelper.db.RetirementContract;

/**
 * Utility class for tax deferred income source.
 * Created by Ed Muhlestein on 6/9/2017.
 */

public class TaxDeferredHelper {

    public static String addTaxDeferredIncome(Context context, TaxDeferredIncomeData tdid) {
        String id = DataBaseUtils.addIncomeType(context, tdid);
        if(id == null) {
            return null;
        }

        long incomeId = Long.parseLong(id);
        ContentValues values = new ContentValues();
        values.put(RetirementContract.TaxDeferredIncomeEntry.COLUMN_INCOME_TYPE_ID, incomeId);
        values.put(RetirementContract.TaxDeferredIncomeEntry.COLUMN_MIN_AGE, tdid.getMinimumAge());
        values.put(RetirementContract.TaxDeferredIncomeEntry.COLUMN_INTEREST, Double.toString(tdid.getInterestRate()));
        values.put(RetirementContract.TaxDeferredIncomeEntry.COLUMN_MONTH_ADD, Double.toString(tdid.getMonthAddition()));
        values.put(RetirementContract.TaxDeferredIncomeEntry.COLUMN_PENALTY, Double.toString(tdid.getPenalty()));
        values.put(RetirementContract.TaxDeferredIncomeEntry.COLUMN_BALANCE, Double.toString(tdid.getBalance()));
        values.put(RetirementContract.TaxDeferredIncomeEntry.COLUMN_IS_401K, tdid.getIs401k());
        Uri uri = context.getContentResolver().insert(RetirementContract.TaxDeferredIncomeEntry.CONTENT_URI, values);

        if(uri == null) {
            return null;
        } else {
            return uri.toString();
        }
    }

    public static int saveTaxDeferredData(Context context, TaxDeferredIncomeData tdid) {
        DataBaseUtils.updateIncomeTypeName(context, tdid);
        ContentValues values = new ContentValues();
        values.put(RetirementContract.TaxDeferredIncomeEntry.COLUMN_MIN_AGE, tdid.getMinimumAge());
        values.put(RetirementContract.TaxDeferredIncomeEntry.COLUMN_INTEREST, Double.toString(tdid.getInterestRate()));
        values.put(RetirementContract.TaxDeferredIncomeEntry.COLUMN_MONTH_ADD, Double.toString(tdid.getMonthAddition()));
        values.put(RetirementContract.TaxDeferredIncomeEntry.COLUMN_PENALTY, Double.toString(tdid.getPenalty()));
        values.put(RetirementContract.TaxDeferredIncomeEntry.COLUMN_BALANCE, Double.toString(tdid.getBalance()));
        values.put(RetirementContract.TaxDeferredIncomeEntry.COLUMN_IS_401K, tdid.getIs401k());

        String id = String.valueOf(tdid.getId());
        Uri uri = RetirementContract.TaxDeferredIncomeEntry.CONTENT_URI;
        uri = Uri.withAppendedPath(uri, id);
        context.getContentResolver().update(uri, values, null, null);

        return 0;
    }

    private static Cursor getTaxDeferredIncome(Context context, long incomeId) {
        Uri uri = RetirementContract.TaxDeferredIncomeEntry.CONTENT_URI;
        String selection = RetirementContract.TaxDeferredIncomeEntry.COLUMN_INCOME_TYPE_ID + " = ?";
        String sid = String.valueOf(incomeId);
        uri = uri.withAppendedPath(uri, sid);
        String[] selectionArgs = {sid};
        return context.getContentResolver().query(uri, null, null, null, null);
    }

    public static int deleteTaxDeferredIncome(Context context, long incomeId) {
        String sid = String.valueOf(incomeId);
        Uri uri = RetirementContract.IncomeTypeEntry.CONTENT_URI;
        uri = Uri.withAppendedPath(uri, sid);
        context.getContentResolver().delete(uri, null, null);
        uri = RetirementContract.TaxDeferredIncomeEntry.CONTENT_URI;
        uri = Uri.withAppendedPath(uri, sid);
        context.getContentResolver().delete(uri, null, null);
        uri = RetirementContract.BalanceEntry.CONTENT_URI;
        uri = Uri.withAppendedPath(uri, sid);
        return context.getContentResolver().delete(uri, null, null);
    }

    public static TaxDeferredIncomeData getTaxDeferredIncomeData(Context context, long incomeId) {
        DataBaseUtils.IncomeDataHelper idh = DataBaseUtils.getIncomeTypeData(context, incomeId);
        if(idh == null) {
            return null;
        }

        Cursor cursor = getTaxDeferredIncome(context, incomeId);
        if(cursor == null || !cursor.moveToFirst()) {
            return null;
        }
        int minAgeIndex = cursor.getColumnIndex(RetirementContract.TaxDeferredIncomeEntry.COLUMN_MIN_AGE);
        int interestIndex = cursor.getColumnIndex(RetirementContract.TaxDeferredIncomeEntry.COLUMN_INTEREST);
        int monthAddIndex = cursor.getColumnIndex(RetirementContract.TaxDeferredIncomeEntry.COLUMN_MONTH_ADD);
        int penaltyIndex = cursor.getColumnIndex(RetirementContract.TaxDeferredIncomeEntry.COLUMN_PENALTY);
        int balanceIndex = cursor.getColumnIndex(RetirementContract.TaxDeferredIncomeEntry.COLUMN_BALANCE);
        int is401kIndex = cursor.getColumnIndex(RetirementContract.TaxDeferredIncomeEntry.COLUMN_IS_401K);
        String minAge = cursor.getString(minAgeIndex);
        double interest = Double.parseDouble(cursor.getString(interestIndex));
        double monthAdd = Double.parseDouble(cursor.getString(monthAddIndex));
        double penalty = Double.parseDouble(cursor.getString(penaltyIndex));
        double balance = Double.parseDouble(cursor.getString(balanceIndex));
        int is401k = cursor.getInt(is401kIndex);
        cursor.close();
        return new TaxDeferredIncomeData(incomeId, idh.name, idh.type, minAge, interest, monthAdd, penalty, balance, is401k);
    }

    public static TaxDeferredIncomeData extractData(Cursor cursor) {
        if(cursor == null || !cursor.moveToFirst()) {
            return null;
        }
        int incomeIdIndex = cursor.getColumnIndex(RetirementContract.IncomeTypeEntry._ID);
        int nameIndex = cursor.getColumnIndex(RetirementContract.IncomeTypeEntry.COLUMN_NAME);
        int typeIndex = cursor.getColumnIndex(RetirementContract.IncomeTypeEntry.COLUMN_TYPE);

        int minAgeIndex = cursor.getColumnIndex(RetirementContract.TaxDeferredIncomeEntry.COLUMN_MIN_AGE);
        int interestIndex = cursor.getColumnIndex(RetirementContract.TaxDeferredIncomeEntry.COLUMN_INTEREST);
        int monthlyAddIndex = cursor.getColumnIndex(RetirementContract.TaxDeferredIncomeEntry.COLUMN_MONTH_ADD);
        int penaltyIndex = cursor.getColumnIndex(RetirementContract.TaxDeferredIncomeEntry.COLUMN_PENALTY);
        int is401KIndex = cursor.getColumnIndex(RetirementContract.TaxDeferredIncomeEntry.COLUMN_IS_401K);

        int balanceIndex = cursor.getColumnIndex(RetirementContract.TaxDeferredIncomeEntry.COLUMN_BALANCE);

        long incomeId = cursor.getLong(incomeIdIndex);
        String name = cursor.getString(nameIndex);
        int incomeType = cursor.getInt(typeIndex);

        String minAge = cursor.getString(minAgeIndex);
        double interest = Double.parseDouble(cursor.getString(interestIndex));
        double monthAdd = Double.parseDouble(cursor.getString(monthlyAddIndex));
        double penalty = Double.parseDouble(cursor.getString(penaltyIndex));
        int is401k = cursor.getInt(is401KIndex);

        String amount = cursor.getString(balanceIndex);
        double balance = Double.parseDouble(amount);

        TaxDeferredIncomeData tdid = new TaxDeferredIncomeData(incomeId, name, incomeType, minAge, interest, monthAdd, penalty, balance, is401k);
        return tdid;
    }
}
