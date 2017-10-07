package com.intelliviz.retirementhelper.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.intelliviz.retirementhelper.data.PensionIncomeData;
import com.intelliviz.retirementhelper.db.RetirementContract;

/**
 * Utility class for pension.
 * Created by Ed Muhlestein on 6/10/2017.
 */

public class PensionHelper {

    public static long addData(Context context, PensionIncomeData pid) {
        String id = DataBaseUtils.addIncomeType(context, pid);
        if(id == null) {
            return -1;
        }

        long incomeId = Long.parseLong(id);
        ContentValues values = new ContentValues();
        values.put(RetirementContract.PensionIncomeEntry.COLUMN_INCOME_TYPE_ID, incomeId);
        values.put(RetirementContract.PensionIncomeEntry.COLUMN_MONTH_BENEFIT, Double.toString(pid.getFullMonthlyBenefit()));
        values.put(RetirementContract.PensionIncomeEntry.COLUMN_START_AGE, pid.getStartAge());
        Uri uri = context.getContentResolver().insert(RetirementContract.PensionIncomeEntry.CONTENT_URI, values);
        if(uri == null) {
            return -1;
        } else {
            return Long.parseLong(uri.getLastPathSegment());
        }
    }

    public static int saveData(Context context, PensionIncomeData pid) {
        DataBaseUtils.updateIncomeTypeName(context, pid);
        ContentValues values = new ContentValues();
        values.put(RetirementContract.PensionIncomeEntry.COLUMN_MONTH_BENEFIT, Double.toString(pid.getFullMonthlyBenefit()));
        values.put(RetirementContract.PensionIncomeEntry.COLUMN_START_AGE, pid.getStartAge());

        String id = String.valueOf(pid.getId());
        Uri uri = RetirementContract.PensionIncomeEntry.CONTENT_URI;
        uri = Uri.withAppendedPath(uri, id);
        return context.getContentResolver().update(uri, values, null, null);
    }

    private static Cursor getPensionIncome(Context context, long incomeId) {
        Uri uri = RetirementContract.PensionIncomeEntry.CONTENT_URI;
        String selection = RetirementContract.PensionIncomeEntry.COLUMN_INCOME_TYPE_ID + " = ?";
        String id = String.valueOf(incomeId);
        String[] selectionArgs = {id};
        return context.getContentResolver().query(uri, null, selection, selectionArgs, null);
    }

    public static PensionIncomeData getData(Context context, long incomeId) {
        DataBaseUtils.IncomeDataHelper idh = DataBaseUtils.getIncomeTypeData(context, incomeId);
        if(idh == null) {
            return null;
        }
        Cursor cursor = getPensionIncome(context, incomeId);
        if(cursor == null || !cursor.moveToFirst()) {
            return null;
        }
        int startAgeIndex = cursor.getColumnIndex(RetirementContract.PensionIncomeEntry.COLUMN_START_AGE);
        int monthlyBenefitIndex = cursor.getColumnIndex(RetirementContract.PensionIncomeEntry.COLUMN_MONTH_BENEFIT);
        String startAge = cursor.getString(startAgeIndex);
        String monthlyBenefit = cursor.getString(monthlyBenefitIndex);
        double amount = Double.parseDouble(monthlyBenefit);
        return null;//new PensionIncomeData(incomeId, idh.name, idh.type, startAge, amount);
    }

    public static int deleteData(Context context, long incomeId) {
        String sid = String.valueOf(incomeId);
        Uri uri = RetirementContract.IncomeTypeEntry.CONTENT_URI;
        uri = Uri.withAppendedPath(uri, sid);
        context.getContentResolver().delete(uri, null, null);
        uri = RetirementContract.PensionIncomeEntry.CONTENT_URI;
        uri = Uri.withAppendedPath(uri, sid);
        return context.getContentResolver().delete(uri, null, null);
    }

    public static PensionIncomeData extractData(Cursor cursor) {
        if(cursor == null || !cursor.moveToFirst()) {
            return null;
        }
        int incomeIdIndex = cursor.getColumnIndex(RetirementContract.IncomeTypeEntry._ID);
        int nameIndex = cursor.getColumnIndex(RetirementContract.IncomeTypeEntry.COLUMN_NAME);
        int typeIndex = cursor.getColumnIndex(RetirementContract.IncomeTypeEntry.COLUMN_TYPE);

        int startAgeIndex = cursor.getColumnIndex(RetirementContract.PensionIncomeEntry.COLUMN_START_AGE);
        int monthlyBenefitIndex = cursor.getColumnIndex(RetirementContract.PensionIncomeEntry.COLUMN_MONTH_BENEFIT);

        long incomeId = cursor.getLong(incomeIdIndex);
        String name = cursor.getString(nameIndex);
        int incomeType = cursor.getInt(typeIndex);

        String startAge = cursor.getString(startAgeIndex);
        double monthlyBenefit = Double.parseDouble(cursor.getString(monthlyBenefitIndex));

        return null;//new PensionIncomeData(incomeId, name, incomeType, startAge, monthlyBenefit);
    }
}
