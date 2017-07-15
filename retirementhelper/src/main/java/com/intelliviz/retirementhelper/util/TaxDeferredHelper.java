package com.intelliviz.retirementhelper.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.intelliviz.retirementhelper.data.BalanceData;
import com.intelliviz.retirementhelper.data.TaxDeferredIncomeData;
import com.intelliviz.retirementhelper.db.RetirementContract;

import java.util.List;

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
        values.put(RetirementContract.TaxDeferredIncomeEntry.COLUMN_IS_401K, tdid.getIs401k());
        Uri uri = context.getContentResolver().insert(RetirementContract.TaxDeferredIncomeEntry.CONTENT_URI, values);

        List<BalanceData> balanceDataList = tdid.getBalanceData();
        for(BalanceData bd : balanceDataList) {
            DataBaseUtils.addBalanceData(context, incomeId, bd.getBalance(), bd.getDate());
        }

        if(uri == null) {
            return null;
        } else {
            return uri.toString();
        }
    }

    public static int saveTaxDeferredData(Context context, TaxDeferredIncomeData tdid) {
        DataBaseUtils.updateIncomeTypeName(context, tdid);
        DataBaseUtils.saveIncomeType(context, tdid);
        ContentValues values = new ContentValues();
        values.put(RetirementContract.TaxDeferredIncomeEntry.COLUMN_MIN_AGE, tdid.getMinimumAge());
        values.put(RetirementContract.TaxDeferredIncomeEntry.COLUMN_INTEREST, Double.toString(tdid.getInterestRate()));
        values.put(RetirementContract.TaxDeferredIncomeEntry.COLUMN_MONTH_ADD, Double.toString(tdid.getMonthAddition()));
        values.put(RetirementContract.TaxDeferredIncomeEntry.COLUMN_PENALTY, Double.toString(tdid.getPenalty()));
        values.put(RetirementContract.TaxDeferredIncomeEntry.COLUMN_IS_401K, tdid.getIs401k());

        String id = String.valueOf(tdid.getId());
        Uri uri = RetirementContract.TaxDeferredIncomeEntry.CONTENT_URI;
        uri = Uri.withAppendedPath(uri, id);
        context.getContentResolver().update(uri, values, null, null);
        List<BalanceData> balanceDataList = tdid.getBalanceData();
        for(BalanceData bd : balanceDataList) {
            DataBaseUtils.saveBalanceData(context, tdid.getId(), bd.getBalance(), bd.getDate());
        }

        return 0;
    }

    private static Cursor getTaxDeferredIncome(Context context, long incomeId) {
        Uri uri = RetirementContract.TaxDeferredIncomeEntry.CONTENT_URI;
        String selection = RetirementContract.TaxDeferredIncomeEntry.COLUMN_INCOME_TYPE_ID + " = ?";
        String sid = String.valueOf(incomeId);
        String[] selectionArgs = {sid};
        return context.getContentResolver().query(uri, null, selection, selectionArgs, null);
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
        int is401kIndex = cursor.getColumnIndex(RetirementContract.TaxDeferredIncomeEntry.COLUMN_IS_401K);
        String minAge = cursor.getString(minAgeIndex);
        double interest = Double.parseDouble(cursor.getString(interestIndex));
        double monthAdd = Double.parseDouble(cursor.getString(monthAddIndex));
        double penalty = Double.parseDouble(cursor.getString(penaltyIndex));
        int is401k = cursor.getInt(is401kIndex);
        cursor.close();
        TaxDeferredIncomeData tdid = new TaxDeferredIncomeData(incomeId, idh.name, idh.type, minAge, interest, monthAdd, penalty, is401k);

        BalanceData[] bds = DataBaseUtils.getBalanceData(context, incomeId);
        if(bds != null) {
            for (BalanceData bd : bds) {
                tdid.addBalanceData(bd);
            }
        }
        return tdid;
    }
}
