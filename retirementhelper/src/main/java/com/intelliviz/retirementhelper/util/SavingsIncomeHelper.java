package com.intelliviz.retirementhelper.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.intelliviz.retirementhelper.data.SavingsIncomeData;
import com.intelliviz.retirementhelper.db.RetirementContract;

import static com.intelliviz.retirementhelper.util.DataBaseUtils.addIncomeType;
import static com.intelliviz.retirementhelper.util.DataBaseUtils.getIncomeTypeData;

/**
 * Utility class for savings.
 * Created by Ed Muhlestein on 6/10/2017.
 */
public class SavingsIncomeHelper {
    public static long addData(Context context, SavingsIncomeData sid) {
        String id = addIncomeType(context, sid);
        if(id == null) {
            return -1;
        }

        long incomeId = Long.parseLong(id);
        ContentValues values = new ContentValues();
        values.put(RetirementContract.SavingsIncomeEntry.COLUMN_INCOME_TYPE_ID, incomeId);
        values.put(RetirementContract.SavingsIncomeEntry.COLUMN_MONTH_ADD, sid.getMonthlyIncrease());
        values.put(RetirementContract.SavingsIncomeEntry.COLUMN_INTEREST, sid.getInterest());
        values.put(RetirementContract.SavingsIncomeEntry.COLUMN_BALANCE, Double.toString(sid.getBalance()));
        Uri uri = context.getContentResolver().insert(RetirementContract.SavingsIncomeEntry.CONTENT_URI, values);

        if(uri == null) {
            return -1;
        } else {
            return Long.parseLong(uri.getLastPathSegment());
        }
    }

    public static int saveData(Context context, SavingsIncomeData sid) {
        DataBaseUtils.updateIncomeTypeName(context, sid);
        ContentValues values  = new ContentValues();
        values.put(RetirementContract.SavingsIncomeEntry.COLUMN_MONTH_ADD, sid.getMonthlyIncrease());
        values.put(RetirementContract.SavingsIncomeEntry.COLUMN_INTEREST, sid.getInterest());
        values.put(RetirementContract.SavingsIncomeEntry.COLUMN_BALANCE, Double.toString(sid.getBalance()));

        String id = String.valueOf(sid.getId());
        Uri uri = RetirementContract.SavingsIncomeEntry.CONTENT_URI;
        uri = Uri.withAppendedPath(uri, id);
        return context.getContentResolver().update(uri, values, null, null);
    }

    private static Cursor getSavingsIncome(Context context, long incomeId) {
        String id = String.valueOf(incomeId);
        Uri uri = RetirementContract.SavingsIncomeEntry.CONTENT_URI;
        uri = Uri.withAppendedPath(uri, id);
        return context.getContentResolver().query(uri, null, null, null, null);
    }

    public static SavingsIncomeData getData(Context context, long incomeId) {
        DataBaseUtils.IncomeDataHelper idh = getIncomeTypeData(context, incomeId);
        if(idh == null) {
            return null;
        }
        Cursor cursor = getSavingsIncome(context, incomeId);
        if(cursor == null || !cursor.moveToFirst()) {
            return null;
        }
        int interestIndex = cursor.getColumnIndex(RetirementContract.SavingsIncomeEntry.COLUMN_INTEREST);
        int monthAddIndex = cursor.getColumnIndex(RetirementContract.SavingsIncomeEntry.COLUMN_MONTH_ADD);
        int balanceIndex = cursor.getColumnIndex(RetirementContract.SavingsIncomeEntry.COLUMN_BALANCE);
        double interest = Double.parseDouble(cursor.getString(interestIndex));
        double monthAdd = Double.parseDouble(cursor.getString(monthAddIndex));
        double balance = Double.parseDouble(cursor.getString(balanceIndex));
        return null;//new SavingsIncomeData(incomeId, idh.name, idh.type, interest, monthAdd, balance);
    }

    public static int deleteSavingsIncome(Context context, long incomeId) {
        String sid = String.valueOf(incomeId);
        Uri uri = RetirementContract.IncomeTypeEntry.CONTENT_URI;
        uri = Uri.withAppendedPath(uri, sid);
        context.getContentResolver().delete(uri, null, null);
        uri = RetirementContract.SavingsIncomeEntry.CONTENT_URI;
        uri = Uri.withAppendedPath(uri, sid);
        return context.getContentResolver().delete(uri, null, null);
    }

    public static SavingsIncomeData extractData(Cursor cursor) {
        if(cursor == null || !cursor.moveToFirst()) {
            return null;
        }
        int incomeIdIndex = cursor.getColumnIndex(RetirementContract.IncomeTypeEntry._ID);
        int nameIndex = cursor.getColumnIndex(RetirementContract.IncomeTypeEntry.COLUMN_NAME);
        int typeIndex = cursor.getColumnIndex(RetirementContract.IncomeTypeEntry.COLUMN_TYPE);

        int interestIndex = cursor.getColumnIndex(RetirementContract.SavingsIncomeEntry.COLUMN_INTEREST);
        int monthlyAddIndex = cursor.getColumnIndex(RetirementContract.SavingsIncomeEntry.COLUMN_MONTH_ADD);
        int balanceIndex = cursor.getColumnIndex(RetirementContract.TaxDeferredIncomeEntry.COLUMN_BALANCE);

        long incomeId = cursor.getLong(incomeIdIndex);
        String name = cursor.getString(nameIndex);
        int incomeType = cursor.getInt(typeIndex);

        double interest = Double.parseDouble(cursor.getString(interestIndex));
        double monthAdd = Double.parseDouble(cursor.getString(monthlyAddIndex));
        double balance = Double.parseDouble(cursor.getString(balanceIndex));

        return null;//new SavingsIncomeData(incomeId, name, incomeType, interest, monthAdd, balance);
    }
}
