package com.intelliviz.retirementhelper.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.intelliviz.retirementhelper.data.GovPensionIncomeData;
import com.intelliviz.retirementhelper.db.RetirementContract;

import static com.intelliviz.retirementhelper.util.DataBaseUtils.getIncomeTypeData;

/**
 * Created by edm on 6/12/2017.
 */

public class GovPensionHelper {

    public static String addGovPensionData(Context context, GovPensionIncomeData gpid) {
        String id = DataBaseUtils.addIncomeType(context, gpid);
        if(id == null) {
            return null;
        }
        long incomeId = Long.parseLong(id);
        ContentValues values = new ContentValues();
        values.put(RetirementContract.GovPensionIncomeEntry.COLUMN_INCOME_TYPE_ID, incomeId);
        values.put(RetirementContract.GovPensionIncomeEntry.COLUMN_MONTH_BENEFIT, Double.toString(gpid.getMonthlyBenefit(0)));
        values.put(RetirementContract.GovPensionIncomeEntry.COLUMN_MIN_AGE, gpid.getStartAge());
        Uri uri = context.getContentResolver().insert(RetirementContract.GovPensionIncomeEntry.CONTENT_URI, values);
        if (uri == null) {
            return null;
        } else
            return uri.getLastPathSegment();
    }

    public static int saveGovPensionData(Context context, GovPensionIncomeData gpid) {
        ContentValues values = new ContentValues();
        values.put(RetirementContract.GovPensionIncomeEntry.COLUMN_MONTH_BENEFIT, Double.toString(gpid.getMonthlyBenefit(0)));
        values.put(RetirementContract.GovPensionIncomeEntry.COLUMN_MIN_AGE, gpid.getStartAge());

        String sid = String.valueOf(gpid.getId());
        String selectionClause = RetirementContract.GovPensionIncomeEntry.COLUMN_INCOME_TYPE_ID + " = ?";
        String[] selectionArgs = new String[]{sid};
        Uri uri = RetirementContract.GovPensionIncomeEntry.CONTENT_URI;
        uri = Uri.withAppendedPath(uri, sid);
        return context.getContentResolver().update(uri, values, selectionClause, selectionArgs);
    }

    private static Cursor getGovPensionIncome(Context context, long incomeId) {
        Uri uri = RetirementContract.GovPensionIncomeEntry.CONTENT_URI;
        String selection = RetirementContract.GovPensionIncomeEntry.COLUMN_INCOME_TYPE_ID + " = ?";
        String id = String.valueOf(incomeId);
        String[] selectionArgs = {id};
        return context.getContentResolver().query(uri, null, selection, selectionArgs, null);
    }

    public static GovPensionIncomeData getGovPensionIncomeData(Context context, long incomeId) {
        DataBaseUtils.IncomeDataHelper idh = getIncomeTypeData(context, incomeId);
        if(idh == null) {
            return null;
        }

        Cursor cursor = getGovPensionIncome(context, incomeId);
        if(cursor == null || !cursor.moveToFirst()) {
            return null;
        }
        int minAgeIndex = cursor.getColumnIndex(RetirementContract.GovPensionIncomeEntry.COLUMN_MIN_AGE);
        int monthlyBenefitIndex = cursor.getColumnIndex(RetirementContract.GovPensionIncomeEntry.COLUMN_MONTH_BENEFIT);
        String startAge = cursor.getString(minAgeIndex);
        String monthlyBenefit = cursor.getString(monthlyBenefitIndex);
        double amount = Double.parseDouble(monthlyBenefit);
        return new GovPensionIncomeData(incomeId, idh.name, idh.type, startAge, amount);
    }
}
