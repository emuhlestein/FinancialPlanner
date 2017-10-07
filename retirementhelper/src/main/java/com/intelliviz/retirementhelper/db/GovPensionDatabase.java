package com.intelliviz.retirementhelper.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.intelliviz.retirementhelper.data.GovPensionIncomeData;
import com.intelliviz.retirementhelper.data.IncomeTypeData;

/**
 * Created by edm on 9/27/2017.
 */

public class GovPensionDatabase extends BaseDatabase {
    private volatile static GovPensionDatabase mINSTANCE;

    public static GovPensionDatabase getInstance(Context context) {
        if(mINSTANCE == null) {
            synchronized (GovPensionDatabase.class) {
                if(mINSTANCE == null) {

                    mINSTANCE = new GovPensionDatabase(context);
                }
            }
        }
        return mINSTANCE;
    }

    private GovPensionDatabase(Context context) {
        super(context.getContentResolver());
    }

    public long insert(IncomeTypeData data) {
        if(!(data instanceof GovPensionIncomeData)) {
            return 0;
        }
        GovPensionIncomeData gpid = (GovPensionIncomeData)data;

        String id = addIncomeType(gpid);
        if(id == null) {
            return -1;
        }
        long incomeId = Long.parseLong(id);
        ContentValues values = new ContentValues();
        values.put(RetirementContract.GovPensionIncomeEntry.COLUMN_INCOME_TYPE_ID, incomeId);
        values.put(RetirementContract.GovPensionIncomeEntry.COLUMN_MONTH_BENEFIT, Double.toString(gpid.getFullMonthlyBenefit()));
        values.put(RetirementContract.GovPensionIncomeEntry.COLUMN_MIN_AGE, gpid.getMinAge());
        Uri uri = getContentResolver().insert(RetirementContract.GovPensionIncomeEntry.CONTENT_URI, values);
        if (uri == null) {
            return -1;
        } else
            return Long.parseLong(uri.getLastPathSegment());
    }

    public IncomeTypeData get(long incomeId) {
        IncomeData id = getData(incomeId);
        if(id == null) {
            return null;
        }

        Cursor cursor = query(incomeId);
        if(cursor == null || !cursor.moveToFirst()) {
            return null;
        }
        int minAgeIndex = cursor.getColumnIndex(RetirementContract.GovPensionIncomeEntry.COLUMN_MIN_AGE);
        int monthlyBenefitIndex = cursor.getColumnIndex(RetirementContract.GovPensionIncomeEntry.COLUMN_MONTH_BENEFIT);
        String startAge = cursor.getString(minAgeIndex);
        String monthlyBenefit = cursor.getString(monthlyBenefitIndex);
        double amount = Double.parseDouble(monthlyBenefit);
        cursor.close();
        return new GovPensionIncomeData(incomeId, id.type, id.name, incomeId, startAge, monthlyBenefit);
    }

    public int update(IncomeTypeData data) {
        if(!(data instanceof GovPensionIncomeData)) {
            return 0;
        }
        GovPensionIncomeData gpid = (GovPensionIncomeData)data;

        updateIncomeTypeName(gpid);
        ContentValues values = new ContentValues();
        values.put(RetirementContract.GovPensionIncomeEntry.COLUMN_MONTH_BENEFIT, Double.toString(gpid.getFullMonthlyBenefit()));
        values.put(RetirementContract.GovPensionIncomeEntry.COLUMN_MIN_AGE, gpid.getMinAge());

        String sid = String.valueOf(gpid.getId());
        String selectionClause = RetirementContract.GovPensionIncomeEntry.COLUMN_INCOME_TYPE_ID + " = ?";
        String[] selectionArgs = new String[]{sid};
        Uri uri = RetirementContract.GovPensionIncomeEntry.CONTENT_URI;
        uri = Uri.withAppendedPath(uri, sid);
        return getContentResolver().update(uri, values, selectionClause, selectionArgs);
    }

    public int delete(long incomeId) {
        String sid = String.valueOf(incomeId);
        Uri uri = RetirementContract.IncomeTypeEntry.CONTENT_URI;
        uri = Uri.withAppendedPath(uri, sid);
        getContentResolver().delete(uri, null, null);
        uri = RetirementContract.GovPensionIncomeEntry.CONTENT_URI;
        uri = Uri.withAppendedPath(uri, sid);
        return getContentResolver().delete(uri, null, null);
    }

    private Cursor query(long incomeId) {
        Uri uri = RetirementContract.GovPensionIncomeEntry.CONTENT_URI;
        String selection = RetirementContract.GovPensionIncomeEntry.COLUMN_INCOME_TYPE_ID + " = ?";
        String id = String.valueOf(incomeId);
        String[] selectionArgs = {id};
        return getContentResolver().query(uri, null, selection, selectionArgs, null);
    }
}
