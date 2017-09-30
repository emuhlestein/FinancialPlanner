package com.intelliviz.retirementhelper.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.intelliviz.retirementhelper.data.IncomeTypeData;
import com.intelliviz.retirementhelper.data.PensionIncomeData;

/**
 * Created by edm on 9/30/2017.
 */

public class PensionDatabase extends BaseDatabase {
    private volatile static PensionDatabase mINSTANCE;

    public static PensionDatabase getInstance(Context context) {
        if(mINSTANCE == null) {
            synchronized (PensionDatabase.class) {
                if(mINSTANCE == null) {

                    mINSTANCE = new PensionDatabase(context);
                }
            }
        }
        return mINSTANCE;
    }

    private PensionDatabase(Context context) {
        super(context.getContentResolver());
    }

    public long insert(IncomeTypeData data) {
        if(!(data instanceof PensionIncomeData)) {
            return 0;
        }
        PensionIncomeData pid = (PensionIncomeData)data;

        String id = addIncomeType(pid);
        if(id == null) {
            return -1;
        }

        long incomeId = Long.parseLong(id);
        ContentValues values = new ContentValues();
        values.put(RetirementContract.PensionIncomeEntry.COLUMN_INCOME_TYPE_ID, incomeId);
        values.put(RetirementContract.PensionIncomeEntry.COLUMN_MONTH_BENEFIT, Double.toString(pid.getFullMonthlyBenefit()));
        values.put(RetirementContract.PensionIncomeEntry.COLUMN_START_AGE, pid.getStartAge());
        Uri uri = getContentResolver().insert(RetirementContract.PensionIncomeEntry.CONTENT_URI, values);
        if(uri == null) {
            return -1;
        } else {
            return Long.parseLong(uri.getLastPathSegment());
        }
    }

    public PensionIncomeData get(long incomeId) {
        IncomeData idata = getData(incomeId);
        if(idata == null) {
            return null;
        }
        Cursor cursor = query(incomeId);
        if(cursor == null || !cursor.moveToFirst()) {
            return null;
        }
        int startAgeIndex = cursor.getColumnIndex(RetirementContract.PensionIncomeEntry.COLUMN_START_AGE);
        int monthlyBenefitIndex = cursor.getColumnIndex(RetirementContract.PensionIncomeEntry.COLUMN_MONTH_BENEFIT);
        String startAge = cursor.getString(startAgeIndex);
        String monthlyBenefit = cursor.getString(monthlyBenefitIndex);
        double amount = Double.parseDouble(monthlyBenefit);
        return new PensionIncomeData(incomeId, idata.name, idata.type, startAge, amount);
    }

    public int update(IncomeTypeData data) {
        if(!(data instanceof PensionIncomeData)) {
            return 0;
        }
        PensionIncomeData pid = (PensionIncomeData)data;

        updateIncomeTypeName(pid);
        ContentValues values = new ContentValues();
        values.put(RetirementContract.PensionIncomeEntry.COLUMN_MONTH_BENEFIT, Double.toString(pid.getFullMonthlyBenefit()));
        values.put(RetirementContract.PensionIncomeEntry.COLUMN_START_AGE, pid.getStartAge());

        String id = String.valueOf(pid.getId());
        Uri uri = RetirementContract.PensionIncomeEntry.CONTENT_URI;
        uri = Uri.withAppendedPath(uri, id);
        return getContentResolver().update(uri, values, null, null);
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
        Uri uri = RetirementContract.PensionIncomeEntry.CONTENT_URI;
        String selection = RetirementContract.PensionIncomeEntry.COLUMN_INCOME_TYPE_ID + " = ?";
        String id = String.valueOf(incomeId);
        String[] selectionArgs = {id};
        return getContentResolver().query(uri, null, selection, selectionArgs, null);
    }
}
